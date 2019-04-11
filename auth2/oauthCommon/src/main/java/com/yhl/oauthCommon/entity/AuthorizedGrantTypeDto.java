package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AuthorizedGrantTypeDto implements Serializable {
    private static final long serialVersionUID = -1609159160942038394L;

    private String clientId;

    private String grantType;

    private Long companyId;
}
