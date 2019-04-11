package com.yhl.base.service;


import com.alibaba.fastjson.JSONArray;
import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.dao.BaseDao;
import com.yhl.base.entity.BaseEntity;
import com.yhl.orm.componet.constant.FieldContext;
import com.yhl.orm.componet.constant.WhereContext;
import com.yhl.orm.componet.util.WhereBuildUtil;

import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.List;

public interface BaseService<T extends BaseEntity,ID extends Serializable> {



    <T> ResultDto insertByEntity(T entity);

    <T> ResultDto insertByList(List<T> entitys);

    <T> ResultDto updateByEntity(T entity);

    <T> ResultDto updateByEntitys(T[] entity);

    <T> ResultDto updateByFieldContext(FieldContext fieldContext);

    <T> ResultDto updateByFieldContexts(FieldContext[] fieldContexts, int flusSize);

    <T> ResultDto updateByFieldContextAndWhereContext(WhereContext whereContext, int flushSize);

    <T> ResultDto findById(ID id);

    <T> ResultDto findByWhereContext(WhereContext whereContext);

    ResultDto findCountByWhereContext(WhereContext whereContext);

    <T> ResultDto findPageByWhereContext(WhereContext whereContext);

    ResultDto deleteById(ID id);

    ResultDto deleteByWhereContext(WhereContext whereContext);

    ResultDto findbyPredicate(Predicate predicate);

    ResultDto findGroupbyByPredicate(Predicate predicate, String[] groupbys);

    ResultDto findOrderByPredicate(Predicate predicate, JSONArray sorts);

    ResultDto findOrderAndGroupByPredicate(Predicate predicate, String[] groupbys, JSONArray sorts);

    BaseDao<T,ID> getBaseDao();

    WhereBuildUtil<T,ID> getWhereBuildUtil();
}
