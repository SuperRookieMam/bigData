package com.yhl.system.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "role_info",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"role_name", "company_Id"})}

)
public class RoleInfo extends BaseEntity {
    private static final long serialVersionUID = -6133439565976081362L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 角色名称
    @Column(name = "role_name")
    private String rolName;

    //公司Id
    @Column(name = "company_id")
    private Long companyId;
    //公司Id
    @Column(name = "company_name")
    private String companyName;

    //部门//预留
    @Column(name = "department_id")
    private Long departmentId;
    //部门Id//预留
    @Column(name = "department_name")
    private String departmentName;

}
