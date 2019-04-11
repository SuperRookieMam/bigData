package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ClientRegisteredRedirectUriDto implements Serializable {

    private static final long serialVersionUID = -439922298929663795L;

    private String clientId;

    private String redirectUri;

    private Long companyId;
}
