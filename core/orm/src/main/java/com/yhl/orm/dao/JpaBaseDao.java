package com.yhl.orm.dao;


import com.alibaba.fastjson.JSONArray;
import com.yhl.orm.componet.constant.FieldContext;
import com.yhl.orm.componet.constant.PageInfo;
import com.yhl.orm.componet.constant.WhereContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

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

/**
 *继承勒JpaRepository的接口，findN那些所有方法，
 *JpaSpecificationExecutor 的执行器所有方法
 * */
@NoRepositoryBean
 public interface JpaBaseDao<T,ID extends Serializable>  extends JpaRepository<T,ID>, JpaSpecificationExecutor<T> {

     <T> T insertByEntity(T entity);

     <T> int insertByList(List<T> entitys);

     <T> T updateByFieldContext(FieldContext fieldContext);

     <T> int updateByFieldContexts(FieldContext[] fieldContexts, int flushSize);

     <T> int updateByFieldContextAndWhereContext(WhereContext whereContext, int flushSize);

     <T> List<T> findByWhereContext(WhereContext whereContext);

     <T> T updateByEntity(T entity);

     <T> T[] updateByEntitys(T[] entity);

     long findCountByWhereContext(WhereContext whereContext);

     <T> PageInfo<T> findPageByWhereContext(WhereContext whereContext);

     void deleteById(ID id);

     int deleteByWhereContext(WhereContext whereContext) ;

    int deleteByTypeQuery(TypedQuery<T> typedQuery);

    List<T> findbyTypeQuery(TypedQuery<T> typedQuery);


    EntityManager getEntityManager();

     Class getEntityClass();

     Map<String,Field> getFieldMap();

     CriteriaBuilder getBuilder();

     Root<T> getRoot();

     CriteriaQuery<T> getQuery();

    /**
     * JpaRepository的接口
     * List<T> findAll();
     * List<T> findAll(Sort var1);
     * List<T> findAll(Iterable<ID> var1);
     * <S extends T> List<S> save(Iterable<S> var1);
     * void flush();
     * T saveAndFlush(T var1);
     * void deleteInBatch(Iterable<T> var1);
     * void deleteAllInBatch();
     * T getOne(ID var1);
     * */
    /**
     * JpaSpecificationExecutor 的接口
     * T findOne(Specification<T> var1);
     * List<T> findAll(Specification<T> var1);
     * Page<T> findAll(Specification<T> var1, Pageable var2);
     * List<T> findAll(Specification<T> var1, Sort var2);
     * int count(Specification<T> var1);
     * */
}
