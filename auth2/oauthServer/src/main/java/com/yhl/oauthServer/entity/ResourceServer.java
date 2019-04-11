package com.yhl.oauthServer.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "resource_server")
public class ResourceServer extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 资源服务器名字
     */
    @Column(name = "name")
    private String name;
    /**
     * 注册地址
     */
    @Column(name = "register_url")
    private String registerUrl;
    /**
     * 说明用来存储的是资源服务器的凭证
     */
    @Column(name = "remark")
    private String remark;
    /**
     * 是否使用
     */
    @Column(name = "is_use")
    private Integer isUse = 1;

    @Column(name = "company_id")
    private Long companyId;
}
