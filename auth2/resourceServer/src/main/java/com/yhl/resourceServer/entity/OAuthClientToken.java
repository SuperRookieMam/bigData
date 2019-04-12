package com.yhl.resourceServer.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 该表用于在客户端系统中存储从服务端获取的token数据,
 * 在spring-oauth-server项目中未使用到.
 * 对oauth_client_token表的主要操作在
 * JdbcClientTokenServices.java类中,
 */
@Getter
@Setter
@Entity
@Table(name = "oauth_client_token")
public class OAuthClientToken extends BaseEntity {

    private static final long serialVersionUID = -4320621346795422710L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 从服务器端获取到的access_token的值.
     */
    @Column(name = "token_id")
    private String tokenId;

    /**
     * 这是一个二进制的字段, 存储的数据是OAuth2AccessToken.java对象序列化后的二进制数据.
     */
    @Column(name = "token")
    private String token;

    /**
     * 该字段具有唯一性, 是根据当前的username(如果有),client_id与scope通过MD5加密生成的.
     * 具体实现请参考DefaultClientKeyGenerator.java类.
     */
    @Column(name = "authentication_id")
    private String authenticationId;
    /**
     * 登录时的用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * client_id
     */
    @Column(name = "client_id")
    private String clientId;
}
