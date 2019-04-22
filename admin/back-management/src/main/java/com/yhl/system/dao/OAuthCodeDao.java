package com.yhl.system.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.system.entity.OAuthCode;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthCodeDao extends BaseDao<OAuthCode, Long> {
}
