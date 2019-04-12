package com.yhl.resourceServer.service.impl;


import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.resourceServer.entity.OAuthAccessToken;
import com.yhl.resourceServer.service.OAuthAccessTokenService;
import org.springframework.stereotype.Service;

@Service
public class OAuthAccessTokenServiceImpl extends BaseServiceImpl<OAuthAccessToken,String> implements OAuthAccessTokenService {
}
