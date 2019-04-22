package com.yhl.system.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.system.entity.OAuthRefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthRefreshTokenDao extends BaseDao<OAuthRefreshToken,Long> {
}
