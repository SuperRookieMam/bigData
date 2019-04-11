package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OAuthClientTokenDto implements Serializable {

    private static final long serialVersionUID = 8934749460104530535L;
    private String tokenId;


    private String token;


    private String authenticationId;


    private String userName;


    private String clientId;
}
