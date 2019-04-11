package com.yhl.oauthServer.service.impl;

import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.oauthServer.entity.OAuthAccessToken;
import com.yhl.oauthServer.service.OAuthAccessTokenService;
import org.springframework.stereotype.Service;

@Service
public class OAuthAccessTokenServiceImpl extends BaseServiceImpl<OAuthAccessToken,Long> implements OAuthAccessTokenService {
}
