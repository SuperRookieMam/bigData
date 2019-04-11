package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleInfoDto {

    private Long id;

    private String rolName;

    private Long companyId;

    private String companyName;


    private Long departmentId;

    private String departmentName;

    private String createTime;

    private String modifyTime;

    private String createUser;

}
