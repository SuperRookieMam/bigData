package com.yhl.orm.dao.Impl;


import com.alibaba.fastjson.JSONArray;
import com.yhl.orm.componet.constant.Expression;
import com.yhl.orm.componet.constant.FieldContext;
import com.yhl.orm.componet.constant.PageInfo;
import com.yhl.orm.componet.constant.WhereContext;
import com.yhl.orm.componet.util.CopyFieldUtil;
import com.yhl.orm.componet.util.MyClassUtil;
import com.yhl.orm.componet.util.PresentWhereContextUtil;
import com.yhl.orm.dao.JpaBaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

//@NoRepositoryBean ：启动时不初始化该实体类。是spring date jpa的一种注解
public class JpaBaseDaoImpl<T,ID extends Serializable> extends SimpleJpaRepository<T,ID > implements JpaBaseDao<T,ID> {

    private final EntityManager entityManager;

    private Class<T> clazz;

    private CriteriaBuilder builder ;

    private Root<T> root;

    private Map<String,Field> fieldMap;

    private String ID ="id";

    //父类没有不带参数的构造方法，这里手动构造父类
    public JpaBaseDaoImpl(Class<T> modelClass, EntityManager entityManager) {
        super(modelClass, entityManager);
        this.entityManager = entityManager;
        clazz =modelClass;
        this.builder = this.entityManager.getCriteriaBuilder();
        this.root =this.builder.createQuery(clazz).from(clazz);
    }
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
     * 单个跟新
     * */
    @Override
    public <T> T updateByFieldContext(FieldContext fieldContext) {
        String idName = (String)fieldContext.get(ID);
        Object object = fieldContext.get(idName);
        fieldContext.remove(ID);
        fieldContext.remove(idName);
        CriteriaUpdate criteriaUpdate =this.builder.createCriteriaUpdate(clazz);
        criteriaUpdate.from(clazz);
        CopyFieldUtil.setFiled(fieldContext,criteriaUpdate,entityManager);
        criteriaUpdate.where(builder.equal(root.get(idName),object));
        entityManager.createQuery(criteriaUpdate).executeUpdate();
        return (T)entityManager.find(clazz,object);
    }

    /**
     * 批量跟新
     * */
    @Override
    public <T> int updateByFieldContexts(FieldContext[] fieldContexts, int flushSize) {
        Map<String, Field> map =getFieldMap();
        for (int i = 0; i < fieldContexts.length; i++) {
            updateByFieldContext(fieldContexts[i]);
            if (i%flushSize==0){
                entityManager.flush();
                entityManager.clear();
            }
        }
        return fieldContexts.length;
    }
    /**
     * 根据条件跟新某个字段，但不是联表跟新，
     * */
    @Override
    public <T> int updateByFieldContextAndWhereContext (WhereContext whereContext, int flushSize) {
        FieldContext fieldContext =whereContext.getFieldContext();
        CriteriaUpdate<T> criteriaUpdate = (CriteriaUpdate<T>) this.builder.createCriteriaUpdate(clazz);
        criteriaUpdate.from((Class<T>) clazz);
        CopyFieldUtil.setFiled(fieldContext,criteriaUpdate,entityManager);
        Predicate predicate =   PresentWhereContextUtil.expressionToPredicate(builder,root,new Expression[whereContext.getExpressions().size()]);
        criteriaUpdate.where(predicate);
        return  entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public <T> List<T> findByWhereContext(WhereContext whereContext) {
       if (whereContext==null){
            return (List<T>)super.findAll();
        }
        List<T> list = (List<T>) PresentWhereContextUtil.getTypedQuery(clazz,entityManager,whereContext).getResultList();
        return list;
    }

    @Override
    public long findCountByWhereContext(WhereContext whereContext) {
        if (whereContext==null){
            return super.count();
        }
        TypedQuery query =PresentWhereContextUtil.getTypedQuery(Long.class,entityManager,whereContext);
        return PresentWhereContextUtil.executeCountQuery(query);
    }

    @Override
    public <T> PageInfo<T> findPageByWhereContext(WhereContext whereContext) {
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
    public int deleteByWhereContext(WhereContext whereContext) {
        List<T> list = this.findByWhereContext(whereContext);
        super.deleteAll(list);
        return list.size();
    }
    @Override
    public int deleteByTypeQuery(TypedQuery<T> typedQuery) {
        List<T> list =  this.findbyTypeQuery(typedQuery);
        super.deleteAll(list);
        return list.size();
    }
    @Override
   public List<T> findbyTypeQuery(TypedQuery<T> typedQuery){
       return  typedQuery.getResultList();
    }

    public EntityManager getEntityManager(){
        return  this.entityManager;
    }

    public<T> Class<T> getEntityClass(){
        return (Class<T>) this.clazz;
    }

    public Map<String,Field> getFieldMap(){
        if (ObjectUtils.isEmpty(this.fieldMap)){
            this.fieldMap = MyClassUtil.getAllFields(clazz);
        }
        return this.fieldMap;
    }
    public CriteriaBuilder getBuilder(){
        return this.builder;
    }

    public Root<T> getRoot(){
        return this.root;
    }

    public  CriteriaQuery<T> getQuery(){
        return this.builder.createQuery(clazz);
    }
}
