package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.OAuthAccessToken;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthAccessTokenDao extends BaseDao<OAuthAccessToken, Long> {
}
