package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.ClientScope;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientScopeDao extends BaseDao<ClientScope, Long> {
}
