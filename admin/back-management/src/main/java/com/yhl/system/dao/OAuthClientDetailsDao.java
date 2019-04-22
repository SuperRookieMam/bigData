package com.yhl.system.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.system.entity.OAuthClientDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthClientDetailsDao extends BaseDao<OAuthClientDetails,Long> {
}
