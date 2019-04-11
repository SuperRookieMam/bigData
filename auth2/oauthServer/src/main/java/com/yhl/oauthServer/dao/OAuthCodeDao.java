package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.OAuthCode;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthCodeDao extends BaseDao<OAuthCode, Long> {
}
