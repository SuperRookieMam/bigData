package com.yhl.oauthServer.service.impl;

import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.oauthServer.entity.OAuthRefreshToken;
import com.yhl.oauthServer.service.OAuthRefreshTokenService;
import org.springframework.stereotype.Service;

@Service
public class OAuthRefreshTokenServiceImpl extends BaseServiceImpl<OAuthRefreshToken,Long> implements OAuthRefreshTokenService {
}
