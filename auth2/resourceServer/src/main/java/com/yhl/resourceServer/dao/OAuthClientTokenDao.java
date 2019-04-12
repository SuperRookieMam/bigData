package com.yhl.resourceServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.resourceServer.entity.OAuthClientToken;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthClientTokenDao extends BaseDao<OAuthClientToken, String> {
}
