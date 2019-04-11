package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.OAuthClientDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthClientDetailsDao extends BaseDao<OAuthClientDetails,Long> {
}
