package com.yhl.orm.dao.Impl;


import com.alibaba.fastjson.JSONArray;
import com.yhl.orm.componet.constant.*;
import com.yhl.orm.componet.util.MyClassUtil;
import com.yhl.orm.componet.util.MyQueryUtil;
import com.yhl.orm.componet.util.PresentWhereContextUtil;
import com.yhl.orm.dao.JpaBaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

//@NoRepositoryBean ：启动时不初始化该实体类。是spring date jpa的一种注解
public class JpaBaseDaoImpl<T,ID extends Serializable> extends SimpleJpaRepository<T,ID > implements JpaBaseDao<T,ID> {

    private final EntityManager entityManager;
    private Class clazz;
    private CriteriaBuilder builder ;
    private CriteriaQuery<T> query;
    private Root<T> root;

    //父类没有不带参数的构造方法，这里手动构造父类
    public JpaBaseDaoImpl(Class<T> modelClass, EntityManager entityManager) {
        super(modelClass, entityManager);
        this.entityManager = entityManager;
        clazz =modelClass;
        this.builder = this.entityManager.getCriteriaBuilder();
        this.query =this.builder.createQuery(clazz);
        this.root =this.query.from(clazz);
    }
 /*   *//**
     * 根据id查询
     * *//*
    @Override
    public <T> T findById(ID id) {
        return (T) entityManager.find(clazz,id);
    }*/
    /**
     * 单个插入
     * */
    @Override
    public <T> T insertByEntity(T entity) {
        entityManager.persist(entity);
        return entity;
    }
    /**
     * 批量插入
     * */
    @Override
    public <T> int insertByList(List<T> entitys) {
        //分批保存相对于速度要块很多
        int batchSize = entitys.size()>1?entitys.size():1;
        for (int i = 0; i < entitys.size(); i++) {
            entityManager.persist(entitys.get(i));
            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        return entitys.size();
    }
    /**
     * 单个跟新
     * */
    @Override
    public <T> T updateByUpdateFields(UpdateFields updateFields) {
      T entity =(T)  entityManager.find(clazz,updateFields.get("id"));
        entity = updateFields.copyPropertis(entity,updateFields,entityManager);
        return entityManager.merge(entity);
    }

    /**
     * 注意这entity 必须时游览状态的要不肯能跟新不聊
     * 因为好向以前试过
     * */
    @Override
    public <T> T updateByEntity(T entity) {
        return entityManager.merge(entity);
    }

    @Override
    public <T> T[] updateByEntitys(T[] entitys) {
        for (int i = 0; i <entitys.length; i++) {
            entityManager.merge(entitys[i]);
        }
        return entitys;
    }

    /**
     * 批量跟新
     * */
    @Override
    public <T> int updateByUpdateFields(UpdateFields[] updateFieldss, int flushSize) {
        Map<String, Field> map = MyClassUtil.getAllFields(clazz);
        for (int i = 0; i < updateFieldss.length; i++) {
            T entity =(T)  entityManager.find(clazz,updateFieldss[i].get("id"));
            entity = UpdateFields.copyPropertis(entity,updateFieldss[i],entityManager,map);
            entityManager.merge(entity);
            if (i%flushSize==0){
                entityManager.flush();
                entityManager.clear();
            }
        }
        return updateFieldss.length;
    }
    /**
     * 根据条件跟新某个字段，但不是联表跟新，
     * */
    @Override
    public <T> int updateByWhereCondition (UpdateFields updateFields, WhereCondition whereCondition, int flushSize) {
        Map<String, Field> map = MyClassUtil.getAllFields(clazz);
        List<T> list =findByParams(whereCondition);
        for (int i = 0; i < list.size(); i++) {
            T entity = list.get(i);
            entity = updateFields.copyPropertis(entity,updateFields,entityManager,map);
            entityManager.merge(entity);
            if (i%flushSize==0){
                entityManager.flush();
                entityManager.clear();
            }
        }
        return list.size();
    }

    @Override
    public <T> List<T> findByParams(WhereCondition whereCondition) {
        if (whereCondition==null){
            return (List<T>)super.findAll();
        }
        List<T> list = MyQueryUtil.getTypedQuery(clazz,entityManager,whereCondition).getResultList();
        return list;
    }

    @Override
    public long findCountByWhereCondition(WhereCondition whereCondition) {
        if (whereCondition==null){
            return super.count();
        }
        TypedQuery query =MyQueryUtil.getCountQuery(clazz,entityManager,whereCondition);
        return MyQueryUtil.executeCountQuery(query);
    }

    @Override
    public <T> PageInfo<T> findPageByParams(WhereContext whereContext) {
        Page page = PresentWhereContextUtil.readPage(whereContext,clazz,entityManager);
        PageInfo<T> pageInfo=new PageInfo<>();
        pageInfo.setPageNum(page.getNumber());
        pageInfo.setPageSize(page.getSize());
        pageInfo.setStartRow((whereContext.getPageNum()-1)*whereContext.getPageSize());
        pageInfo.setEndRow((whereContext.getPageNum()-1)*whereContext.getPageSize()+whereContext.getPageSize());
        pageInfo.setPages(page.getTotalPages());
        pageInfo.setList(page.getContent());
        pageInfo.setTotal(page.getTotalElements());
        pageInfo.setOrderBy(page.getSort().toString());
        return pageInfo;
    }
    /**
     * 类似于 hibernate 的 save 方法. 使对象由临时状态变为持久化状态.
     *和 hibernate 的 save 方法的不同之处: 若对象有 id,
     * 则不能执行 insert 操作, 而会抛出异常
     *
     *总的来说: 类似于 hibernate Session 的 saveOrUpdate 方法.
     * 对象没有id，插入操作
     * 对象有id，且和数据库中有对应的id，修改操作
     * 对象有id，但数据库中找不到对应的id，则抛弃id
     * 进行插入操作entityManager.merge(customer);
     * */
    @Override
    public void deleteById(ID id) {
        T entity =getOne(id);
         if (!ObjectUtils.isEmpty(entity)){
             entityManager.detach(entity);
         }
    }
    @Override
    public int deleteByWhereCondition(WhereCondition whereCondition) {
        List<T> list = this.findByParams(whereCondition);
        super.deleteAll(list);
        return list.size();
    }

    public EntityManager getEntityManager(){
        return  this.entityManager;
    }
    public Class getEntityClass(){
        return  this.clazz;
    }
}
