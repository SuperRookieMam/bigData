package com.yhl.oauthServer.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "oath_user_detailes",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_name"})},
        indexes = {@Index(columnList = "user_name")})
public class OAthUserDetailes extends BaseEntity {

    private static final long serialVersionUID = 9056596580975978130L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "pass_word")
    private String passWord;

    @Column(name = "head_image")
    private String headImage;

    // 用户的权限
    @Transient// 不建表，动态获取
    private List<OAthGrantedAuthority> authorities = Collections.emptyList();

    @Column(name = "is_expired")
    private boolean isExpired;

    @Column(name = "is_lock")
    private boolean isLock;

    @Column(name = "credentials")
    private String credentials;

    @Column(name = "is_enabled")
    private boolean isEnabled;

}
