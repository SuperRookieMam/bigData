package com.yhl.system.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "department",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"company_name", "department_name"})},
        indexes = {@Index(columnList = "department_name"), @Index(columnList = "company_name")})
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //公司名字
    @Column(name = "department_name")
    private String departmentName;
    // 营业执照
    @Column(name = "company_name")
    private String companyName;
    // 父级Id
    @Column(name = "pid")
    private String pid;

    // 部门负责人id
    @Column(name = "principal_man")
    private String principalMan;

    @Column(name = "company_id")
    private Long companyId;
}
