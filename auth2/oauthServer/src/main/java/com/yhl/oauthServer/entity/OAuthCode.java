package com.yhl.oauthServer.entity;


import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 在项目中,主要操作oauth_code表的对象是JdbcAuthorizationCodeServices.java.
 * 更多的细节请参考该类. 
 * 只有当grant_type为"authorization_code"时,该表中才会有数据产生;
 * 其他的grant_type没有使用该表.
 */
@Getter
@Setter
@Entity
@Table(name = "oauth_code")
public class OAuthCode extends BaseEntity {

    private static final long serialVersionUID = 7086367203392700303L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 存储服务端系统生成的code的值(未加密).
     */
    @Column(name = "code")
    private String code;
    /**
     * 存储将AuthorizationRequestHolder.java对象序列化后的二进制数据.
     */
    @Column(name = "authentication")
    private String authentication;


}
