package com.yhl.orm.dao;


import com.yhl.orm.componet.constant.PageInfo;
import com.yhl.orm.componet.constant.UpdateFields;
import com.yhl.orm.componet.constant.WhereCondition;
import com.yhl.orm.componet.constant.WhereContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 *继承勒JpaRepository的接口，findN那些所有方法，
 *JpaSpecificationExecutor 的执行器所有方法
 * */
@NoRepositoryBean
public interface JpaBaseDao<T,ID extends Serializable>  extends JpaRepository<T,ID>, JpaSpecificationExecutor<T> {

//    /**
//     * 自定义接口
//     * */
//    public <T> T findById(ID id);

    public <T> int updateByWhereCondition(UpdateFields updateFields, WhereCondition whereCondition, int flushSize);

    /**
     * 根据参数自定义查询
     * */
    public <T> List<T> findByParams(WhereCondition whereCondition);

    /**
     * 根据一个实体插入
     * */
    public <T> T insertByEntity(T entity);
    /**
     * 批量插入
     * */
    public <T> int insertByList(List<T> entitys);
    /**
     * 根据实体跟新
     * */
    public <T> T updateByUpdateFields(UpdateFields updateFields);


    <T> T updateByEntity(T entity);

    <T> T[] updateByEntitys(T[] entity);
    /**
     * 根据实体跟新
     * @param  updateFieldss 要跟新的字段值
     * @param  flushSize 多少条刷新一次
     * */
    public <T> int updateByUpdateFields(UpdateFields[] updateFieldss, int flushSize);
    /**
     * 根据条件查询条数
     * */
    public long findCountByWhereCondition(WhereCondition whereCondition);


    public <T> PageInfo<T> findPageByParams(WhereContext whereContext);


    public void deleteById(ID id);

    public int deleteByWhereCondition(WhereCondition whereCondition) ;
    public EntityManager getEntityManager();
    public Class getEntityClass();
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
