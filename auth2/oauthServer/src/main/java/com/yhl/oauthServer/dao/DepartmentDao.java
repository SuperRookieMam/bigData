package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.Department;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentDao extends BaseDao<Department, Long> {
}
