package com.yhl.orm.componet.util;

import com.yhl.orm.dao.JpaBaseDao;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WhereBuilder<T,ID extends Serializable> {

    private EntityManager entityManager;

    private Class<T> clazz;

    private PredicateBuilder<T,ID> predicateBuilder;

    private CriteriaBuilder builder ;

    private CriteriaQuery<T> query ;

    private Root<T> root;

    private  Predicate currentpredicate;

    public WhereBuilder(JpaBaseDao<T,ID> jpaBaseDao){
        this.entityManager =jpaBaseDao.getEntityManager();
        this.clazz =jpaBaseDao.getEntityClass();
        this.builder =this.entityManager.getCriteriaBuilder();
        this.query = this.builder.createQuery(clazz);
        this.root =this.query.from(clazz);
    }

    public WhereBuilder<T,ID> and(Predicate...predicates){
        currentpredicate = ObjectUtils.isEmpty(currentpredicate)
                ?builder.and(predicates)
                :builder.and(currentpredicate,builder.and(predicates));
        return this;
    }
    public WhereBuilder<T,ID> or(Predicate...predicates){
        currentpredicate = ObjectUtils.isEmpty(currentpredicate)
                ?builder.or(predicates)
                :builder.or(currentpredicate,builder.and(predicates));
        return this;
    }
    public Predicate end(){
        Predicate predicate = currentpredicate;
        currentpredicate =null;
        return predicate;
    }

    public PredicateBuilder<T,ID> getPredicateBuilder(){
        if (ObjectUtils.isEmpty(predicateBuilder)){
            predicateBuilder =new PredicateBuilder(builder,root);
        }
        return predicateBuilder;
    }

    public WhereBuilder<T,ID> where(Predicate ...predicates){
        this.query.where(predicates);
        return this;
    }

    public WhereBuilder<T,ID>  groupBy (String ...gorupbys){
        if (gorupbys!=null){
            Path[] paths =new  Path [gorupbys.length];
            for (int i = 0; i < gorupbys.length ; i++) {
                String[] panthNames= gorupbys[i].split("\\.");
                Path path=null;
                for (int j = 0; j < panthNames.length; j++) {
                    path =j==0?root.get(panthNames[j]):path.get(panthNames[j]);
                }
                paths[i] = path;
            }
            this.query.groupBy(paths);
        }
        return this;
    }


    /**
     *获取排序
     * sort 的格式为 如果有多个排序字段为" fieldName:desc"," fieldName:asc"
     * */
    public WhereBuilder<T,ID> orderby(String... sorts){
        if (sorts!=null){
            List<Sort.Order> list =new ArrayList<>();
            for (int i = 0; i <sorts.length ; i++) {
                String[] sort =sorts[i].split(":");
                Sort.Direction direction=  "desc".equalsIgnoreCase(sort[1]) ?Sort.Direction.DESC:Sort.Direction.ASC;
                list.add(new Sort.Order(direction,sort[0]));
            }
            query.orderBy(QueryUtils.toOrders(new Sort(list), root, builder));
        }
        return this;
    }
    public TypedQuery<T> buildTypeQuery(){
        return   this.entityManager.createQuery(query);
    }



}
