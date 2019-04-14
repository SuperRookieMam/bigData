package com.yhl.oauthServer.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "oath_granted_authority_map",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "role_info", "oath_granted_authority"})
        },
        indexes = {
                @Index(columnList = "client_id")
        })
public class OAthGrantedAuthorityMap extends BaseEntity {

    private static final long serialVersionUID = 4607647844190816999L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //客户端id
    @Column(name = "client_id", length = 50)
    private String clientId;


    @ManyToOne
    @JoinColumn(name = "role_info")
    private RoleInfo roleInfo;


    @ManyToOne
    @JoinColumn(name = "oath_granted_authority")
    private OAthGrantedAuthority oAthGrantedAuthority;


}
