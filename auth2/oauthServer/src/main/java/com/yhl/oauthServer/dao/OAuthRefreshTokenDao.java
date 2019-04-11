package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.OAuthRefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthRefreshTokenDao extends BaseDao<OAuthRefreshToken,Long> {
}
