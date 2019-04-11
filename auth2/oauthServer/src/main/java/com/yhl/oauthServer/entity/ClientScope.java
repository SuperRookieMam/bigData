package com.yhl.oauthServer.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "client_scope",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"client_id", "scope"})},
        indexes = {@Index(columnList = "client_id")})
public class ClientScope extends BaseEntity {
    private static final long serialVersionUID = 6264195683179623743L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "scope")
    private String scope;

    @Column(name = "company_id")
    private Long companyId;

}
