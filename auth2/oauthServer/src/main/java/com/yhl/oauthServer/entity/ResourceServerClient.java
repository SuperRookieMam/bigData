package com.yhl.oauthServer.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "resource_server_client",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "resource_id"})
        },
        indexes = {
                @Index(columnList = "client_id")
        })
public class ResourceServerClient extends BaseEntity {
    private static final long serialVersionUID = 8181194360392195940L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 客户端Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "company_id")
    private Long companyId;


}
