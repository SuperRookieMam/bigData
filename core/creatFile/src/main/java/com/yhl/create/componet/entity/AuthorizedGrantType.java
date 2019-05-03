package com.yhl.create.componet.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "authorized_grant_type",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"client_id", "grant_type"})},
        indexes = {@Index(columnList = "client_id")})
public class AuthorizedGrantType extends BaseEntity {

    private static final long serialVersionUID = 4388749422719856987L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "client_id", nullable = false, length = 50)
    private String clientId;

    //    其中用Grant Type代表当前授权的类型。 Grant Type包括：
//    authorization_code：传统的授权码模式
//    implicit：隐式授权模式
//    password：资源所有者（即用户）密码模式
//    client_credentials：客户端凭据（客户端ID以及Key）模式
//    refresh_token：获取access token时附带的用于刷新新的token模式
    @Column(name = "grant_type", nullable = false, length = 50)
    private String grantType;

    @Column(name = "company_id")
    private Long companyId;
}
