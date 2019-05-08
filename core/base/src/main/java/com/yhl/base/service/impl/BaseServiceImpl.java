package com.yhl.base.service.impl;


import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.dao.BaseDao;
import com.yhl.base.entity.BaseEntity;
import com.yhl.base.service.BaseService;
import com.yhl.orm.componet.constant.FieldContext;
import com.yhl.orm.componet.constant.PageInfo;
import com.yhl.orm.componet.constant.WhereContext;
import com.yhl.orm.componet.util.WhereBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class BaseServiceImpl<T extends BaseEntity,ID extends Serializable> implements BaseService<T, ID> {

    @Autowired
    BaseDao<T,ID> baseDao;

    @Override
    @Transactional(value ="jpaTransactionManager")
    public <T> ResultDto insertByEntity(T entity) {
        entity=(T)baseDao.insertByEntity(entity);
        return ResultDto.success(entity);
    }

    @Override
    @Transactional(value ="jpaTransactionManager")
    public <T> ResultDto insertByList(List<T>  entitys) {
        return ResultDto.success(baseDao.insertByList(entitys));
    }

    @Override
    @Transactional(value ="jpaTransactionManager")
    public <T> ResultDto updateByEntity(T entity) {
        entity = baseDao.updateByEntity(entity);
        return ResultDto.success(entity) ;
    }

    @Override
    @Transactional(value ="jpaTransactionManager")
    public <T> ResultDto updateByEntitys(T[] entitys) {
        baseDao.updateByEntity(entitys);
        return ResultDto.success(entitys) ;
    }

    @Override
    @Transactional(value ="jpaTransactionManager")
    public <T> ResultDto updateByFieldContext(FieldContext fieldContext) {
        T entity = (T) baseDao.updateByFieldContext(fieldContext);
        return ResultDto.success(entity) ;
    }

    @Override
    @Transactional(value ="jpaTransactionManager")
    public <T> ResultDto updateByFieldContexts(FieldContext[] fieldContexts,int flusSize) {
        int number =  baseDao.updateByFieldContexts(fieldContexts,flusSize);
        return ResultDto.success(number) ;
    }
    @Override
    @Transactional(value ="jpaTransactionManager")
    public <T> ResultDto updateByFieldContextAndWhereContext(WhereContext whereContext, int flushSize){
        int number =  baseDao.updateByFieldContextAndWhereContext(whereContext, flushSize);
        return ResultDto.success(number) ;
    }


    @Override
    public <T> ResultDto findById(ID id) {
        T entity= (T) baseDao.findById(id).get();
        return ResultDto.success(entity);
    }

    @Override
    public <T> ResultDto  findByWhereContext(WhereContext whereContext) {
       List<T> list = baseDao.findByWhereContext(whereContext);
        return ResultDto.success(list);
    }

    @Override
    public ResultDto findCountByWhereContext(WhereContext whereContext) {
        return ResultDto.success(baseDao.findCountByWhereContext(whereContext));
    }

    @Override
    public <T> ResultDto findPageByWhereContext(WhereContext whereContext) {
        PageInfo pageInfo=baseDao.findPageByWhereContext(whereContext);
        return ResultDto.success(pageInfo);
    }

    @Override
    @Transactional(value ="jpaTransactionManager")
    public ResultDto deleteById(ID id) {
        baseDao.deleteById(id);
        return ResultDto.success(null);
    }

    @Override
    @Transactional(value ="jpaTransactionManager")
    public ResultDto deleteByWhereContext(WhereContext whereContext) {
        return ResultDto.success(baseDao.deleteByWhereContext(whereContext));
    }

    @Override
    public ResultDto deleteByTypeQuery(TypedQuery<T> typedQuery) {
        return ResultDto.success(baseDao.deleteByTypeQuery(typedQuery));
    }
    @Override
    public ResultDto findbyTypeQuery(TypedQuery<T> typedQuery){
        return  ResultDto.success(baseDao.findbyTypeQuery(typedQuery));
    }
    @Override
    public BaseDao<T,ID> getBaseDao(){
        return this.baseDao;
    }

    @Override
    public WhereBuilder<T,ID> getWhereBuilder(){
        return new WhereBuilder<>(this.baseDao);
    }
}
