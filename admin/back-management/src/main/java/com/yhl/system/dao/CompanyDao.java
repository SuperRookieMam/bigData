package com.yhl.system.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.system.entity.Company;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyDao extends BaseDao<Company, Long> {
}
