package com.yhl.oauthServer.service;

import com.yhl.base.service.BaseService;
import com.yhl.oauthServer.entity.OAuthClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface OAuthClientDetailsService extends ClientDetailsService, BaseService<OAuthClientDetails, Long> {


}
