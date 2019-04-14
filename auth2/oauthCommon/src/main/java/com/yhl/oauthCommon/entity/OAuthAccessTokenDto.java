package com.yhl.oauthCommon.entity;

import com.alibaba.fastjson.JSONArray;
import com.yhl.oauthCommon.utils.SerializationUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.util.*;

@Getter
@Setter
public class OAuthAccessTokenDto implements OAuth2AccessToken {

    private String tokenId;

    private String authenticationId;

    private String authentication;

    private String clientId;

    private String userName;

    private String refreshToken;

    private RoleInfoDto roleInfo;

   /* private Set<OAthGrantedAuthorityDto> oAthGrantedAuthorities;*/

    private String tokenType;

    private  Date  expiration =new Date();

    private String scope;

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return new LinkedHashMap<>();
    }

    @Override
    public Set<String> getScope() {
        JSONArray jsonArray =JSONArray.parseArray(this.scope);
        Set<String> set = new  HashSet();
        for (int i = 0; i < jsonArray.size(); i++) {
            set.add(jsonArray.getString(i));
        }
        return set;
    }
    private void setScope(String scope) {
        this.scope = scope;
    }
    public void setScope(Set<String> scope) {
        JSONArray jsonArray =new JSONArray();
        jsonArray.addAll(scope);
        setScope(jsonArray.toString()) ;
    }

    @Override
    public OAuth2RefreshToken getRefreshToken() {
        return SerializationUtils.deserialize(refreshToken);
    }
    public String getRefreshTokenStr() {
        return  refreshToken;
    }
    @Override
    public String getTokenType() {
        return tokenType;
    }

    @Override
    public boolean isExpired() {
        return expiration.getTime()-System.currentTimeMillis()<=0;
    }

    @Override
    public Date getExpiration() {
        return expiration;
    }

    @Override
    public int getExpiresIn() {
        return getExpiration().getTime()-System.currentTimeMillis()>0
                ?(int)((getExpiration().getTime()-System.currentTimeMillis())/1000)
                :0;
    }

    @Override
    public String getValue() {
        return tokenId;
    }
}
