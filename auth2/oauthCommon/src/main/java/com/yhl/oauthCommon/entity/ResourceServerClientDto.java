package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResourceServerClientDto implements Serializable {

    private String clientId;


    private Long resourceId;

    private Long companyId;
}
