package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.UserApproval;
import org.springframework.stereotype.Repository;

@Repository
public interface UserApprovalDao extends BaseDao<UserApproval, Long> {
}
