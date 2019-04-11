package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.ResourceServerClient;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceServerClientDao extends BaseDao<ResourceServerClient,Long> {
}
