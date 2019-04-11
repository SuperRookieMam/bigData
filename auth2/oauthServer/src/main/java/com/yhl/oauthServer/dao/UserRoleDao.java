package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.UserRole;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleDao extends BaseDao<UserRole, Long> {
}
