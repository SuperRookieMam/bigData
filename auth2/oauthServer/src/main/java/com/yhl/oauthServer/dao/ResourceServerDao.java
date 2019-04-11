package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.ResourceServer;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceServerDao extends BaseDao<ResourceServer, Long> {
}
