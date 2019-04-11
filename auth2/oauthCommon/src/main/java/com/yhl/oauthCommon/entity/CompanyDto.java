package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDto {

    private String companyName;

    private String secret;

    private String businessLicense;

    private String organizationCode;

    private String businessLicenseImage;

    private String legalPersonId;
}
