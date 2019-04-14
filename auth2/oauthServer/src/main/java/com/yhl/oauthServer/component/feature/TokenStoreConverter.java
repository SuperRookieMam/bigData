package com.yhl.oauthServer.component.feature;

import com.yhl.oauthCommon.entity.OAuthAccessTokenDto;
import com.yhl.oauthCommon.entity.OAuthRefreshTokenDto;
import com.yhl.oauthCommon.utils.SerializationUtils;
import com.yhl.oauthServer.entity.OAuthAccessToken;
import com.yhl.oauthServer.entity.OAuthRefreshToken;
import com.yhl.oauthServer.service.OAuthAccessTokenService;
import com.yhl.oauthServer.service.OAuthRefreshTokenService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/***
 * token 的创建、持久化、获取
 * */
@Setter
public class TokenStoreConverter implements TokenStore {

    @Autowired
    private OAuthAccessTokenService oAuthAccessTokenService;
    @Autowired
    private OAuthRefreshTokenService oAuthRefreshTokenService;


    private final String AUTHENTICATIONID = "authenticationId";
    private final String TOKENID = "tokenId";
    private final String REFRESHTOKEN = "refreshToken";
    private final String CLIENTID = "clientId";
    private final String USERNAME = "userName";
    //使用默认的MD5解密
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication oAuth2Authentication = null;
        TypedQuery typedQuery = oAuthAccessTokenService.getWhereBuildUtil().beginAnSeclect().beginAnWhere().addEq(TOKENID, token).and().end().buildTypedQuery();
        List<OAuthAccessToken> oAuthAccessTokens =(List<OAuthAccessToken> )oAuthAccessTokenService.findbyTypeQuery(typedQuery).getData();
        if (!oAuthAccessTokens.isEmpty()) {
            String authenticationStr = oAuthAccessTokens.get(0).getAuthentication();
            oAuth2Authentication = deserializeAuthentication(authenticationStr);
        }
        return oAuth2Authentication;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public void storeAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication) {
         OAuthAccessTokenDto token =(OAuthAccessTokenDto)oAuth2AccessToken;
        // 保持token的唯一性，如果有先删除
        if (readAccessToken(token.getValue()) != null) {
            removeAccessToken(token.getValue());
        }
        OAuthAccessToken oAuthAccessToken = OAuthAccessToken.copyProperty(token);
        oAuthAccessTokenService.insertByEntity(oAuthAccessToken);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken oAuth2AccessToken = null;
        TypedQuery typedQuery =  oAuthAccessTokenService.getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(TOKENID, tokenValue).and().end().buildTypedQuery();
        List<OAuthAccessToken> oAuthAccessTokens = (List<OAuthAccessToken>)oAuthAccessTokenService.findbyTypeQuery(typedQuery).getData();
        if (!oAuthAccessTokens.isEmpty()) {
            OAuthAccessToken oAuthAccessToken = oAuthAccessTokens.get(0);
            oAuth2AccessToken = OAuthAccessToken.toOAuthAccessTokenDto(oAuthAccessToken);

        }
        return oAuth2AccessToken;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public void removeAccessToken(OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public void removeAccessToken(String tokenId) {
        TypedQuery typedQuery =oAuthAccessTokenService.getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(TOKENID, tokenId).and().end().buildTypedQuery();
        oAuthAccessTokenService.deleteByTypeQuery(typedQuery);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        OAuthRefreshTokenDto oAuthRefreshTokenDto = (OAuthRefreshTokenDto)refreshToken;
        OAuthRefreshToken oAuthRefreshToken =OAuthRefreshToken.copyPropertis(oAuthRefreshTokenDto);
        oAuthRefreshTokenService.insertByEntity(oAuthRefreshToken);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        OAuth2RefreshToken refreshToken = null;
        TypedQuery typedQuery =oAuthRefreshTokenService.getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(TOKENID,  tokenValue).and().end().buildTypedQuery();
        List<OAuthRefreshToken> oAuthRefreshTokens =(List<OAuthRefreshToken>)oAuthRefreshTokenService.findbyTypeQuery(typedQuery).getData();
        if (!oAuthRefreshTokens.isEmpty()) {
            refreshToken = OAuthRefreshToken.toOAuthRefreshTokenDto(oAuthRefreshTokens.get(0));
        }
        return refreshToken;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public OAuth2Authentication readAuthenticationForRefreshToken(String tokenValue) {
        OAuth2Authentication authentication = null;
        TypedQuery typedQuery =oAuthRefreshTokenService.getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(TOKENID,  tokenValue).and().end().buildTypedQuery();
        List<OAuthRefreshToken> oAuthRefreshTokens =(List<OAuthRefreshToken>)oAuthRefreshTokenService.findbyTypeQuery(typedQuery).getData();
        if (!oAuthRefreshTokens.isEmpty()) {
            String authenticationStr = oAuthRefreshTokens.get(0).getAuthentication();
            authentication = deserializeAuthentication(authenticationStr);
        }
        return authentication;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public void removeRefreshToken(OAuth2RefreshToken token) {
        removeRefreshToken(token.getValue());
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public void removeRefreshToken(String tokenValue) {
        TypedQuery typedQuery =oAuthRefreshTokenService.getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(TOKENID,tokenValue).and().end().buildTypedQuery();
        oAuthRefreshTokenService.deleteByTypeQuery(typedQuery);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        OAuthRefreshTokenDto oAuthRefreshTokenDto =(OAuthRefreshTokenDto)refreshToken;
        removeAccessTokenUsingRefreshToken(SerializationUtils.serialize(oAuthRefreshTokenDto));
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public void removeAccessTokenUsingRefreshToken(String refreshToken) {
        TypedQuery typedQuery =oAuthAccessTokenService.getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(REFRESHTOKEN,  refreshToken).and().end().buildTypedQuery();
        oAuthAccessTokenService.deleteByTypeQuery(typedQuery);
    }

    /**
     * 检索根据提供的身份验证密钥存储的访问令牌
     */
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken auth2AccessToken = null;
        //提取authentication请求的参数string作为QAuthAccessToken 的唯一Id
        //{username:xxxxx,client_id:xxxx,scope:xxxxx}
        String key = authenticationKeyGenerator.extractKey(authentication);
        TypedQuery typedQuery =oAuthAccessTokenService.getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(AUTHENTICATIONID, key).and().end().buildTypedQuery();
        List<OAuthAccessToken> oAuthAccessTokens =(List<OAuthAccessToken>)oAuthAccessTokenService.findbyTypeQuery(typedQuery).getData();
        if (oAuthAccessTokens.size() > 0) {
            auth2AccessToken  = OAuthAccessToken.toOAuthAccessTokenDto(oAuthAccessTokens.get(0));
        }
        return auth2AccessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
        TypedQuery typedQuery =oAuthAccessTokenService.getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(CLIENTID, clientId)
                                                       .and()
                                                       .addEq(USERNAME, userName)
                                                       .and()
                                                       .end().buildTypedQuery();
        List<OAuthAccessToken> oAuthAccessTokens =(List<OAuthAccessToken>)oAuthAccessTokenService.findbyTypeQuery(typedQuery).getData();
        for (int i = 0; i < oAuthAccessTokens.size(); i++) {
            accessTokens.add(OAuthAccessToken.toOAuthAccessTokenDto(oAuthAccessTokens.get(i)) );
        }
        return accessTokens;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
        TypedQuery typedQuery =oAuthAccessTokenService.getWhereBuildUtil().beginAnSeclect().beginAnSeclect().beginAnWhere().addEq(CLIENTID, clientId).and().end().buildTypedQuery();
        List<OAuthAccessToken> oAuthAccessTokens =(List<OAuthAccessToken>)oAuthAccessTokenService.findbyTypeQuery(typedQuery).getData();
        for (int i = 0; i < oAuthAccessTokens.size(); i++) {
            accessTokens.add(OAuthAccessToken.toOAuthAccessTokenDto(oAuthAccessTokens.get(i)));
        }
        return accessTokens;
    }


    /**
     * 反序列化得到OAuth2Authentication
     */
    private OAuth2Authentication deserializeAuthentication(String authenticationStr) {
        if (StringUtils.isEmpty(authenticationStr)) {
            return null;
        }
        return SerializationUtils.deserialize(authenticationStr);
    }


}
