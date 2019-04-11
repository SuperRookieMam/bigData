package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.AuthorizedGrantType;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizedGrantTypeDao extends BaseDao<AuthorizedGrantType, Long> {
}
