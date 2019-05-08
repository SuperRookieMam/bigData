package com.yhl.system.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "company",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"company_name"}),
                @UniqueConstraint(columnNames = {"organization_code"})},
        indexes = {@Index(columnList = "company_name")})
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //公司名字
    @Column(name = "company_name")
    private String companyName;

    //公司秘密
    @Column(name = "secret")
    private String secret;

    // 营业执照
    @Column(name = "business_license")
    private String businessLicense;

    //组织机构代码
    @Column(name = "organization_code")
    private String organizationCode;


    //营业执照图片地址
    @Column(name = "business_license_image")
    private String businessLicenseImage;

    //法人代表id
    @Column(name = "legal_person_id")
    private String legalPersonId;
    @Transient
    private LocalDateTime localDateTime = LocalDateTime.now();
}
