package com.yhl.resourceServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.resourceServer.entity.OAuthClientDetails;
import org.springframework.stereotype.Repository;


@Repository
public interface OAuthClientDetailsDao extends BaseDao<OAuthClientDetails, String> {
}
