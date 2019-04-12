package com.yhl.resourceServer.entity;

import com.yhl.base.entity.BaseEntity;
import com.yhl.oauthCommon.entity.OAuthRefreshTokenDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 在项目中,主要操作oauth_refresh_token表的对象是JdbcTokenStore.java. \
 * (与操作oauth_access_token表的对象一样);更多的细节请参考该类. 
 * 如果客户端的grant_type不支持refresh_token,则不会使用该表.
 */
@Getter
@Setter
@Entity
@Table(name = "oauth_refresh_token",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"token_id"})},
        indexes = {@Index(columnList = "token_id")})
public class OAuthRefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 该字段的值是将refresh_token的值通过MD5加密后存储的.
     */
    @Column(name = "token_id")
    private String tokenId;

    /**
     * 存储将OAuth2Authentication.java对象序列化后的二进制数据.
     */
    @Column(name = "authentication")
    private String authentication;
    public static OAuthRefreshTokenDto toOAuthRefreshTokenDto(OAuthRefreshToken  oAuthRefreshToken) {
        OAuthRefreshTokenDto oAuthRefreshTokenDto = new OAuthRefreshTokenDto();
        oAuthRefreshTokenDto.setAuthentication(oAuthRefreshToken.getAuthentication());
        oAuthRefreshTokenDto.setTokenId(oAuthRefreshToken.getTokenId());
        return oAuthRefreshTokenDto;
    }
    public static OAuthRefreshToken  copyPropertis(OAuthRefreshTokenDto  oAuthRefreshTokenDto) {
        OAuthRefreshToken oAuthRefreshToken = new OAuthRefreshToken();
        oAuthRefreshToken.setAuthentication(oAuthRefreshTokenDto.getAuthentication());
        oAuthRefreshToken.setTokenId(oAuthRefreshTokenDto.getTokenId());
        return oAuthRefreshToken;
    }
}
