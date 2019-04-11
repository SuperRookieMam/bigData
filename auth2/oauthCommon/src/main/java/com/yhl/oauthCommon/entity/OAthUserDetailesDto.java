package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class OAthUserDetailesDto implements UserDetails {

    private static final long serialVersionUID = 9056596580975978130L;

    private String userName;

    private String passWord;

    private String headImage;

    private List<OAthGrantedAuthorityDto> authorities = Collections.emptyList();

    private boolean isExpired;

    private boolean isLock;

    private String credentials;

    private boolean isEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passWord;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLock;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !StringUtils.isEmpty(credentials);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
