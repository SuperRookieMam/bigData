package com.yhl.resourceServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.resourceServer.entity.OAuthAccessToken;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthAccessTokenDao extends BaseDao<OAuthAccessToken, String> {
}
