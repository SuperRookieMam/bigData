package com.yhl.resourceServer.entity;

import com.alibaba.fastjson.JSONArray;
import com.yhl.base.entity.BaseEntity;
import com.yhl.oauthCommon.entity.OAuthAccessTokenDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 在项目中,主要操作oauth_access_token表的对象是JdbcTokenStore.java. 更多的细节请参考该类.
 */
@Getter
@Setter
@Entity
@Table(name = "oauth_access_token",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"authentication_id"})},
        indexes = {@Index(columnList = "authentication_id")})
public class OAuthAccessToken extends BaseEntity {

    private static final long serialVersionUID = -5123030928910884773L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 该字段的值是将access_token的值通过MD5加密后存储的.
     */
    @Column(name = "token_id")
    private String tokenId;

    /**
     * 存储将OAuth2AccessToken.java对象序列化后的二进制数据, 是真实的AccessToken的数据值.
     */

    /**
     * 该字段具有唯一性, 其值是根据当前的username(如果有),
     * client_id与scope通过MD5加密生成的.
     * 具体实现请参考DefaultAuthenticationKeyGenerator.java类.
     */
    @Column(name = "authentication_id")
    private String authenticationId;

    /**
     * 存储将OAuth2Authentication.java对象序列化后的二进制数据.
     */
    @Column(name = "authentication")
    @Lob
    private String authentication;
    /**
     *
     * */
    @Column(name = "client_id")
    private String clientId;

    /**
     * 登录时的用户名, 若客户端没有用户名(如grant_type="client_credentials"),
     * 则该值等于client_id
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 该字段的值是将refresh_token的值通过MD5加密后存储的.
     */
    @Column(name = "refresh_token")
    private String refreshToken;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "role_info")
    private RoleInfo roleInfo;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "oath_granted_authority")
    @JoinTable(name = "oath_granted_authority_map",
            joinColumns = {@JoinColumn(name = "client_id", referencedColumnName = "client_id"),
                    @JoinColumn(name = "role_info", referencedColumnName = "role_info")})
    private Set<OAthGrantedAuthority> oAthGrantedAuthorities = Collections.emptySet();

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "scope")
    private String scope;

    @Column(name = "expiration")
    private Date expiration;

    public Set<String> getScope() {
        JSONArray jsonArray =JSONArray.parseArray(this.scope);
        Set<String> set = new HashSet();
        for (int i = 0; i < jsonArray.size(); i++) {
            set.add(jsonArray.getString(i));
        }
        return set;
    }
    private void setScope(String scope) {
        this.scope = scope;
    }
    public void setScope(Set<String> scope) {
        JSONArray jsonArray =new JSONArray();
        jsonArray.addAll(scope);

        setScope(jsonArray.toString()) ;
    }

    public Set<OAthGrantedAuthority> getOAthGrantedAuthorities() {
        return oAthGrantedAuthorities;
    }


    public static OAuthAccessTokenDto toOAuthAccessTokenDto(OAuthAccessToken oAuthAccessToken) {
        OAuthAccessTokenDto oAuthAccessTokenDto = new OAuthAccessTokenDto();
        oAuthAccessTokenDto.setTokenId(oAuthAccessToken.getTokenId());
        oAuthAccessTokenDto.setAuthenticationId(oAuthAccessToken.getAuthenticationId());
        oAuthAccessTokenDto.setAuthentication(oAuthAccessToken.getAuthentication());
        oAuthAccessTokenDto.setRefreshToken(oAuthAccessToken.getRefreshToken());
        oAuthAccessTokenDto.setClientId(oAuthAccessToken.getClientId());
        oAuthAccessTokenDto.setUserName(oAuthAccessToken.getUserName());
        oAuthAccessTokenDto.setScope(oAuthAccessToken.getScope());
        oAuthAccessTokenDto.setRoleInfo(RoleInfo.toRoleInfoDto(oAuthAccessToken.getRoleInfo()));
        oAuthAccessTokenDto.setTokenType(oAuthAccessToken.getTokenType());
        oAuthAccessTokenDto.setExpiration(oAuthAccessToken.getExpiration());
        oAuthAccessTokenDto.setOAthGrantedAuthorities(OAthGrantedAuthority.tooAthGrantedAuthorityDto(oAuthAccessToken.getOAthGrantedAuthorities()));
        return oAuthAccessTokenDto;
    }

    public static OAuthAccessToken copyProperty(OAuthAccessTokenDto oAuthAccessTokenDto) {
        OAuthAccessToken oAuthAccessToken = new OAuthAccessToken();
        oAuthAccessToken.setTokenId(oAuthAccessTokenDto.getTokenId());
        oAuthAccessToken.setAuthenticationId(oAuthAccessTokenDto.getAuthenticationId());
        oAuthAccessToken.setAuthentication(oAuthAccessTokenDto.getAuthentication());
        oAuthAccessToken.setRefreshToken( oAuthAccessTokenDto.getRefreshTokenStr());
        oAuthAccessToken.setClientId(oAuthAccessTokenDto.getClientId());
        oAuthAccessToken.setUserName(oAuthAccessTokenDto.getUserName());
        oAuthAccessToken.setScope(oAuthAccessTokenDto.getScope());
        oAuthAccessToken.setRoleInfo(RoleInfo.copyPropertis(oAuthAccessTokenDto.getRoleInfo()));
        oAuthAccessToken.setTokenType(oAuthAccessTokenDto.getTokenType());
        oAuthAccessToken.setExpiration(oAuthAccessTokenDto.getExpiration());
        oAuthAccessToken.setRoleInfo(RoleInfo.copyPropertis(oAuthAccessTokenDto.getRoleInfo()));
        return oAuthAccessToken;
    }
}
