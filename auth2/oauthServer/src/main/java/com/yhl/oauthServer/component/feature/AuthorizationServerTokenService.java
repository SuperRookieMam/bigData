package com.yhl.oauthServer.component.feature;

import com.yhl.oauthCommon.entity.OAuthAccessTokenDto;
import com.yhl.oauthCommon.entity.OAuthRefreshTokenDto;
import com.yhl.oauthCommon.utils.SerializationUtils;
import com.yhl.oauthServer.entity.OAuthAccessToken;
import com.yhl.oauthServer.entity.OAuthClientDetails;
import com.yhl.oauthServer.entity.UserRole;
import com.yhl.oauthServer.service.OAuthClientDetailsService;
import com.yhl.oauthServer.service.UserRoleService;
import com.yhl.orm.componet.util.PredicateBuilder;
import com.yhl.orm.componet.util.WhereBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 自定义对token的处理
 */
public class AuthorizationServerTokenService implements AuthorizationServerTokenServices, ResourceServerTokenServices,
        ConsumerTokenServices, InitializingBean {

    private int refreshTokenValiditySeconds = 60 * 60 * 24 * 30; // default 30 days.

    private int accessTokenValiditySeconds = 60 * 60 * 12; // default 12 hours.

    private boolean supportRefreshToken = false;

    private boolean reuseRefreshToken = true;

    private TokenStore tokenStore;

    private ClientDetailsService clientDetailsService;

    private TokenEnhancer accessTokenEnhancer;

    private AuthenticationManager authenticationManager;

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;
    @Autowired
    private UserRoleService userRoleService;


    private final String CLIENTID = "clientId";
    private final String USERNAME = "userName";
    private final String COMPANYID = "companyId";
    private  final String TOKENTYPE = "bearer";

    /**
     * Initialize these token services. If no random generator is set, one will be created.
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(tokenStore, "tokenStore must be set");
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        // 查询对应的ACCesstoken
        OAuth2AccessToken existingAccessToken = tokenStore.getAccessToken(authentication);
        OAuth2RefreshToken refreshToken = null;
        if (existingAccessToken != null) {
            //确保每次登录都刷新token,屏蔽掉过期蔡刷新
            if (/*existingAccessToken.isExpired()*/true) {
                if (existingAccessToken.getRefreshToken() != null) {
                    // refreshToken = existingAccessToken.getRefreshToken();
                    // The token store could remove the refresh token when the
                    // access token is removed, but we want to
                    // be sure...
                    tokenStore.removeRefreshToken(existingAccessToken.getRefreshToken());
                }
                tokenStore.removeAccessToken(existingAccessToken);
            } else {
                // Re-store the access token in case the authentication has changed
                tokenStore.storeAccessToken(existingAccessToken, authentication);
                return existingAccessToken;
            }
        }

        // Only create a new refresh token if there wasn't an existing one
        // associated with an expired access token.
        // Clients might be holding existing refresh tokens, so we re-use it in
        // the case that the old access token
        // expired.
        if (refreshToken == null) {
            refreshToken = createRefreshToken(authentication);
        }
        // But the refresh token itself might need to be re-issued if it has
        // expired.
        else if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiring = (ExpiringOAuth2RefreshToken) refreshToken;
            if (System.currentTimeMillis() > expiring.getExpiration().getTime()) {
                refreshToken = createRefreshToken(authentication);
            }
        }

        OAuth2AccessToken accessToken = createAccessToken(authentication, refreshToken);
        tokenStore.storeAccessToken(accessToken, authentication);
        // In case it was modified
        refreshToken = accessToken.getRefreshToken();
        if (refreshToken != null) {
            tokenStore.storeRefreshToken(refreshToken, authentication);
        }
        return accessToken;

    }
    // 暂时是关闭刷新refreshAccessToken的

    @Transactional(value = "jpaTransactionManager", noRollbackFor = {InvalidTokenException.class, InvalidGrantException.class})
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest)
            throws AuthenticationException {

        if (!supportRefreshToken) {
            throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
        }

        OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refreshTokenValue);
        if (refreshToken == null) {
            throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
        }

        OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);
        // 验证如验证
        if (this.authenticationManager != null && !authentication.isClientOnly()) {
            // The client has already been authenticated,
            // but the user authentication might be old now, so give it a
            // chance to re-authenticate.
            Authentication user = new PreAuthenticatedAuthenticationToken(authentication.getUserAuthentication(), "", authentication.getAuthorities());
            user = authenticationManager.authenticate(user);
            Object details = authentication.getDetails();
            authentication = new OAuth2Authentication(authentication.getOAuth2Request(), user);
            authentication.setDetails(details);
        }
        String clientId = authentication.getOAuth2Request().getClientId();
        if (clientId == null || !clientId.equals(tokenRequest.getClientId())) {
            throw new InvalidGrantException("Wrong client for this refresh token: " + refreshTokenValue);
        }

        // clear out any access tokens already associated with the refresh
        // token.
        tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);

        if (isExpired(refreshToken)) {
            tokenStore.removeRefreshToken(refreshToken);
            throw new InvalidTokenException("Invalid refresh token (expired): " + refreshToken);
        }

        authentication = createRefreshedAuthentication(authentication, tokenRequest);

        if (!reuseRefreshToken) {
            tokenStore.removeRefreshToken(refreshToken);
            refreshToken = createRefreshToken(authentication);
        }

        OAuth2AccessToken accessToken = createAccessToken(authentication, refreshToken);
        tokenStore.storeAccessToken(accessToken, authentication);
        if (!reuseRefreshToken) {
            tokenStore.storeRefreshToken(accessToken.getRefreshToken(), authentication);
        }
        return accessToken;
    }


    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return tokenStore.getAccessToken(authentication);
    }

    /**
     * Create a refreshed authentication.
     */
    private OAuth2Authentication createRefreshedAuthentication(OAuth2Authentication authentication, TokenRequest request) {
        OAuth2Authentication narrowed = authentication;
        Set<String> scope = request.getScope();
        OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(request);
        if (scope != null && !scope.isEmpty()) {
            Set<String> originalScope = clientAuth.getScope();
            if (originalScope == null || !originalScope.containsAll(scope)) {
                throw new InvalidScopeException("Unable to narrow the scope of the client authentication to " + scope
                        + ".", originalScope);
            } else {
                clientAuth = clientAuth.narrowScope(scope);
            }
        }
        narrowed = new OAuth2Authentication(clientAuth, authentication.getUserAuthentication());
        return narrowed;
    }

    protected boolean isExpired(OAuth2RefreshToken refreshToken) {
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
            return expiringToken.getExpiration() == null
                    || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
        }
        return false;
    }

    public OAuth2AccessToken readAccessToken(String accessToken) {
        return tokenStore.readAccessToken(accessToken);
    }

    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException,
            InvalidTokenException {
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        } else if (accessToken.isExpired()) {
            tokenStore.removeAccessToken(accessToken);
            throw new InvalidTokenException("Access token expired: " + accessTokenValue);
        }

        OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
        if (result == null) {
            // in case of race condition
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        }
        if (clientDetailsService != null) {
            String clientId = result.getOAuth2Request().getClientId();
            try {
                clientDetailsService.loadClientByClientId(clientId);
            } catch (ClientRegistrationException e) {
                throw new InvalidTokenException("Client not valid: " + clientId, e);
            }
        }
        return result;
    }

    public String getClientId(String tokenValue) {
        OAuth2Authentication authentication = tokenStore.readAuthentication(tokenValue);
        if (authentication == null) {
            throw new InvalidTokenException("Invalid access token: " + tokenValue);
        }
        OAuth2Request clientAuth = authentication.getOAuth2Request();
        if (clientAuth == null) {
            throw new InvalidTokenException("Invalid access token (no client id): " + tokenValue);
        }
        return clientAuth.getClientId();
    }

    public boolean revokeToken(String tokenValue) {
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
        if (accessToken == null) {
            return false;
        }
        if (accessToken.getRefreshToken() != null) {
            tokenStore.removeRefreshToken(accessToken.getRefreshToken());
        }
        tokenStore.removeAccessToken(accessToken);
        return true;
    }

    private OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
        if (!isSupportRefreshToken(authentication.getOAuth2Request())) {
            return null;
        }
        OAuthRefreshTokenDto oAuthRefreshTokenDto =new OAuthRefreshTokenDto();
        oAuthRefreshTokenDto.setAuthentication(SerializationUtils.serialize(authentication));
        oAuthRefreshTokenDto.setTokenId(authenticationKeyGenerator.extractKey(authentication));
        return  oAuthRefreshTokenDto;
    }
    //创建一个游离状态的token
    private OAuth2AccessToken createAccessToken(OAuth2Authentication authentication, OAuth2RefreshToken refreshToken) {
        OAuthRefreshTokenDto oAuthRefreshTokenDto = (OAuthRefreshTokenDto)refreshToken;
        OAuthAccessToken token = new OAuthAccessToken();
        int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
        if (validitySeconds > 0) {
            token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
        }
        token.setRefreshToken(SerializationUtils.serialize(oAuthRefreshTokenDto));
        token.setScope(authentication.getOAuth2Request().getScope());
        token.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
        token.setAuthentication(serializeAuthentication(authentication));
        token.setClientId(authentication.getOAuth2Request().getClientId());
        token.setUserName(authentication.isClientOnly() ? null : authentication.getName());
        token.setTokenId(UUID.randomUUID().toString());
        token.setTokenType(TOKENTYPE);

        //获取当前client的所属公司Id
        WhereBuilder whereBuilder =oAuthClientDetailsService.getWhereBuilder();
        PredicateBuilder predicateBuilder =whereBuilder.getPredicateBuilder();
        TypedQuery  typedQuery =whereBuilder.where(
                             predicateBuilder.addEq(CLIENTID,authentication.getOAuth2Request().getClientId()).end()
                          ).buildTypeQuery();
        List<OAuthClientDetails> oAuthClientDetails =(List<OAuthClientDetails> ) oAuthClientDetailsService.findbyTypeQuery(typedQuery).getData();


       //查询出当前用户在对应公司的角色,并设置此token对应的角色
        WhereBuilder whereBuilder1 =userRoleService.getWhereBuilder();
        PredicateBuilder predicateBuilder1 =whereBuilder1.getPredicateBuilder();
        TypedQuery  typedQuery1 =whereBuilder1.where(
                predicateBuilder1.addEq(COMPANYID,oAuthClientDetails.isEmpty()?0:oAuthClientDetails.get(0).getCompanyId()).end()
                ).buildTypeQuery();
        List<UserRole> list =( List<UserRole> ) userRoleService.findbyTypeQuery(typedQuery1).getData();
        token.setRoleInfo(list.size() > 0 ? list.get(0).getRoleInfo() : null);
        OAuthAccessTokenDto dto = OAuthAccessToken.toOAuthAccessTokenDto(token);
        return accessTokenEnhancer != null ? accessTokenEnhancer.enhance(dto, authentication) : dto;
    }

    /**
     * The access token validity period in seconds
     *
     * @param clientAuth the current authorization request
     * @return the access token validity period in seconds
     */
    protected int getAccessTokenValiditySeconds(OAuth2Request clientAuth) {
        if (clientDetailsService != null) {
            ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            Integer validity = client.getAccessTokenValiditySeconds();
            if (validity != null) {
                return validity;
            }
        }
        return accessTokenValiditySeconds;
    }

    /**
     * The refresh token validity period in seconds
     *
     * @param clientAuth the current authorization request
     * @return the refresh token validity period in seconds
     */
    protected int getRefreshTokenValiditySeconds(OAuth2Request clientAuth) {
        if (clientDetailsService != null) {
            ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            Integer validity = client.getRefreshTokenValiditySeconds();
            if (validity != null) {
                return validity;
            }
        }
        return refreshTokenValiditySeconds;
    }

    /**
     * Is a refresh token supported for this client (or the global setting if
     * {@link #setClientDetailsService(ClientDetailsService) clientDetailsService} is not set.
     *
     * @param clientAuth the current authorization request
     * @return boolean to indicate if refresh token is supported
     */
    protected boolean isSupportRefreshToken(OAuth2Request clientAuth) {
        if (clientDetailsService != null) {
            ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            return client.getAuthorizedGrantTypes().contains("refresh_token");
        }
        return this.supportRefreshToken;
    }

    /**
     * An access token enhancer that will be applied to a new token before it is saved in the token store.
     */
    public void setTokenEnhancer(TokenEnhancer accessTokenEnhancer) {
        this.accessTokenEnhancer = accessTokenEnhancer;
    }


    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public void setSupportRefreshToken(boolean supportRefreshToken) {
        this.supportRefreshToken = supportRefreshToken;
    }

    /**
     * Whether to reuse refresh tokens (until expired).
     */
    public void setReuseRefreshToken(boolean reuseRefreshToken) {
        this.reuseRefreshToken = reuseRefreshToken;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = (AuthenticationManagerConverter) authenticationManager;
    }

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    protected String serializeAuthentication(OAuth2Authentication authentication) {
        String value = new String(SerializationUtils.serialize(authentication));
        return value;
    }

}
