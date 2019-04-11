package com.yhl.oauthCommon.entity;

import com.yhl.oauthCommon.utils.SerializationUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

@Getter
@Setter
public class OAuthRefreshTokenDto implements OAuth2RefreshToken {


    private String tokenId;



    private String authentication;

    @Override
    public String getValue() {
        return SerializationUtils.serialize(this);
    }
}
