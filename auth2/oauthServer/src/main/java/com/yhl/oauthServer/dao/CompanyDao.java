package com.yhl.oauthServer.dao;

import com.yhl.base.dao.BaseDao;
import com.yhl.oauthServer.entity.Company;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyDao extends BaseDao<Company, Long> {
}
