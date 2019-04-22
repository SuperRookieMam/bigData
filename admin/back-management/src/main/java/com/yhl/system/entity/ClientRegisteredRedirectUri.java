package com.yhl.system.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "client_registered_redirect_uri",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"client_id", "redirect_uri"})},
        indexes = {@Index(columnList = "client_id")})
public class ClientRegisteredRedirectUri extends BaseEntity {

    private static final long serialVersionUID = 5659395845747258201L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "client_id", nullable = false, length = 50)
    private String clientId;

    @Column(name = "redirect_uri", nullable = false, length = 50)
    private String redirectUri;

    @Column(name = "company_id")
    private Long companyId;
}
