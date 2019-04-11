package com.yhl.oauthCommon.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.*;

@Getter
@Setter
public class OAuthClientDetailsDto implements ClientDetails {

    private static final long serialVersionUID = 5859645032443922567L;

    private String clientId;

    private Set<String> resourceIds = Collections.emptySet();

    private String clientSecret;

    private Set<String> scopes = Collections.emptySet();

    private Set<String> authorizedGrantTypes = Collections.emptySet();

    private Set<String> registeredRedirectUri;

    private List<GrantedAuthority> authorities = Collections.emptyList();

    private Integer accessTokenValiditySeconds = 60 * 60 * 12;

    private Integer refreshTokenValiditySeconds = 60 * 60 * 24 * 30;

    private Map<String, Object> additionalInformation = new LinkedHashMap<String, Object>();


    private boolean autoApprove = false;

    private Integer archived;

    private Integer trusted;

    private Long companyId;

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    @Override
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        return this.scopes != null && !this.scopes.isEmpty();
    }

    @Override
    public Set<String> getScope() {
        return scopes;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return autoApprove;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return additionalInformation;
    }

}
