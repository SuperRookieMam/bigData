package com.yhl.resourceServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.resourceServer.entity.OAuthRefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthRefreshTokenDao extends BaseDao<OAuthRefreshToken, String> {
}
