package com.yhl.oauthServer.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;


@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_role",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_name", "company_id"})},
        indexes = {@Index(columnList = "user_name")})
public class UserRole extends BaseEntity {

    private static final long serialVersionUID = -526335157679328935L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @ManyToOne
    @JoinColumn(name = "role_info")
    private RoleInfo roleInfo;


    @Column(name = "company_id")
    private Long companyId;


}
