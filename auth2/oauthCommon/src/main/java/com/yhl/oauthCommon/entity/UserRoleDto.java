package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserRoleDto {

    private String userName;


    private RoleInfoDto roleInfo;


    private Long companyId;


}
