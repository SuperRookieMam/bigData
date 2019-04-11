package com.yhl.oauthServer.component.feature;


import com.yhl.oauthCommon.entity.OAuthClientDetailsDto;
import com.yhl.oauthServer.service.OAthGrantedAuthorityService;
import com.yhl.oauthServer.service.UserRoleService;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Set;


@Setter
public class AuthenticationManagerConverter implements AuthenticationManager {

    private ResourceServerTokenServices tokenServices;

    private ClientDetailsService clientInfoService;

    private String resourceId;

    private OAthGrantedAuthorityService oAthGrantedAuthorityService;

    private UserRoleService userRoleService;

    private final String USERNAME = "userName";

    private final String COMPANYID = "companyId";

    private final String ROLEINFO = "roleInfo.Id";

    private final String CLIENTID = "clientId";

    public void afterPropertiesSet() {
        Assert.state(tokenServices != null, "TokenServices are required");
        Assert.state(clientInfoService != null, "clientInfoService are required");
        Assert.state(userRoleService != null, "userRoleService are required");
        Assert.state(oAthGrantedAuthorityService != null, "oAthGrantedAuthorityService are required");

    }

    /**
     * .
     * 期望传入的身份验证请求具有访问令牌值的主体值(例如，来自授权头)。
     * 从{@link ResourceServerTokenServices}加载身份验证，
     * 并检查 资源id包含在{link AuthorizationRequest}中(如果指定了一个)。
     * 也认证副本 从输入到输出的详细信息(例如，通常使访问令牌值和请求详细信息可以)稍后报告)。
     *
     * @param authentication an authentication request containing an access token value as the principal
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication == null) {
            throw new InvalidTokenException("Invalid token (token not found)");
        }
        String token = (String) authentication.getPrincipal();
        OAuth2Authentication auth = tokenServices.loadAuthentication(token);
        if (auth == null) {
            throw new InvalidTokenException("Invalid token: " + token);
        }

        Collection<String> resourceIds = auth.getOAuth2Request().getResourceIds();
        if (resourceId != null && resourceIds != null && !resourceIds.isEmpty() && !resourceIds.contains(resourceId)) {
            throw new OAuth2AccessDeniedException("Invalid token does not contain resource id (" + resourceId + ")");
        }
        checkClientDetails(auth);
        if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
            // Guard against a cached copy of the same details
            if (!details.equals(auth.getDetails())) {
                // Preserve the authentication details from the one loaded by token services
                details.setDecodedDetails(auth.getDetails());
            }
        }
        auth.setDetails(authentication.getDetails());
        auth.setAuthenticated(true);
        return auth;
    }

    private void checkClientDetails(OAuth2Authentication auth) {
        if (clientInfoService != null) {

            OAuthClientDetailsDto client;
            try {
                client = (OAuthClientDetailsDto) clientInfoService.loadClientByClientId(auth.getOAuth2Request().getClientId());
            } catch (ClientRegistrationException e) {
                throw new OAuth2AccessDeniedException("Invalid token contains invalid client id");
            }
            // 这个是特殊权限也正，也就是用户的特殊权限,暂时按照这个来处理吧
            Set<String> allowed = client.getScope();
            for (String scope : auth.getOAuth2Request().getScope()) {
                if (!allowed.contains(scope)) {
                    throw new OAuth2AccessDeniedException(
                            "Invalid token contains disallowed scope (" + scope + ") for this client");
                }
            }
        }
    }

}
