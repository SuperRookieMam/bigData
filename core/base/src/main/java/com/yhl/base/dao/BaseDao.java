package com.yhl.base.dao;

import com.yhl.base.entity.BaseEntity;
import com.yhl.orm.dao.JpaBaseDao;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseDao  <T extends BaseEntity,ID extends Serializable>  extends JpaBaseDao<T ,ID> {


}
