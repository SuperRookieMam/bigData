package com.yhl.base.service;


import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.dao.BaseDao;
import com.yhl.base.entity.BaseEntity;
import com.yhl.orm.componet.constant.FieldContext;
import com.yhl.orm.componet.constant.WhereContext;
import com.yhl.orm.componet.util.WhereBuilder;

import javax.persistence.TypedQuery;
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

    ResultDto deleteByTypeQuery(TypedQuery<T> typedQuery);

    ResultDto findbyTypeQuery(TypedQuery<T> typedQuery);

    BaseDao<T,ID> getBaseDao();

    WhereBuilder<T,ID> getWhereBuilder();
}
