package com.yhl.system.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.system.entity.OAuthAccessToken;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthAccessTokenDao extends BaseDao<OAuthAccessToken, Long> {
}
