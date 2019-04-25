package com.yhl.resourceServer.entity;

import com.yhl.base.entity.BaseEntity;
import com.yhl.oauthCommon.entity.RoleInfoDto;
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

    public static RoleInfoDto toRoleInfoDto(RoleInfo roleInfo){
        RoleInfoDto roleInfoDto = new RoleInfoDto();
        roleInfoDto.setRolName(roleInfo.getRolName());
        roleInfoDto.setCompanyName(roleInfo.getCompanyName());
        roleInfoDto.setCompanyId(roleInfo.getCompanyId());
        roleInfoDto.setDepartmentName(roleInfo.getDepartmentName());
        roleInfoDto.setDepartmentId(roleInfo.getDepartmentId());
        roleInfoDto.setId(roleInfo.getId());
        roleInfoDto.setCreateTime(roleInfo.getCreateUser());
        return roleInfoDto;
    }
    public static RoleInfo copyPropertis(RoleInfoDto roleInfoDto){
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRolName(roleInfoDto.getRolName());
        roleInfo.setCompanyName(roleInfoDto.getCompanyName());
        roleInfo.setCompanyId(roleInfoDto.getCompanyId());
        roleInfo.setDepartmentName(roleInfoDto.getDepartmentName());
        roleInfo.setDepartmentId(roleInfoDto.getDepartmentId());
        roleInfo.setId(roleInfoDto.getId());
        return roleInfo;
    }
}
