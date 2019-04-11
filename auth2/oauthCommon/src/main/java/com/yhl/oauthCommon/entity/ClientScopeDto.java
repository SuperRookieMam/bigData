package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ClientScopeDto implements Serializable {

    private static final long serialVersionUID = 23843852324957319L;

    private String clientId;

    private String scope;

    private Long companyId;

}
