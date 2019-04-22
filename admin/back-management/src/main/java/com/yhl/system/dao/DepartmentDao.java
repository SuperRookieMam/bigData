package com.yhl.system.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.system.entity.Department;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentDao extends BaseDao<Department, Long> {
}
