package com.yhl.orm.componet.util;

import com.yhl.orm.dao.JpaBaseDao;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WhereBuildUtil<T,ID extends Serializable>{

    private Predicate currentPredicate;

    private Predicate currentPredicateNew;

    private Map<String,Predicate> map =new HashMap<>();

    private CriteriaBuilder  builder ;

    CriteriaQuery<T> query ;

    private Root<T> root;

    private EntityManager entityManager;

    private Class<T> clazz;

    private int keyflag =0;

    public  WhereBuildUtil(JpaBaseDao<T,ID> jpaBaseDao){
        this.entityManager = jpaBaseDao.getEntityManager();
        this.clazz =jpaBaseDao.getEntityClass();
    }
    /**
     * 构建初始化 查询组建
     * */
    public  WhereBuildUtil<T,ID>  beginAnSeclect(){
        this.builder =  this.entityManager.getCriteriaBuilder();
        this.query = this.builder.createQuery(clazz);
        this.root =this.query.from(clazz);
        return this;
    }
    /**
     * 构建初始化条件
     * */
    public  WhereBuildUtil<T,ID>  beginAnWhere(){
        this.map.clear();
        this.currentPredicate =null;
        this.currentPredicateNew =null;
        return this;
    }
    public WhereBuildUtil<T,ID> and(){
        if (!ObjectUtils.isEmpty(this.currentPredicateNew)){
            this.currentPredicate = this.builder.and(this.currentPredicate,this.currentPredicateNew);
        }
        return this;
    }
    public WhereBuildUtil<T,ID> or(){
        if (!ObjectUtils.isEmpty(this.currentPredicateNew)){
            this.currentPredicate = this.builder.or(currentPredicate,currentPredicateNew);
        }
        return this;
    }
    public  WhereBuildUtil<T,ID> end(String key){
        Assert.notNull(currentPredicate,"请先构建条件");
        map.put(key,currentPredicate);
        currentPredicate =null;
        currentPredicateNew =null;
        return this;
    }
    public  WhereBuildUtil<T,ID> end(){
        Assert.notNull(currentPredicate,"请先构建条件");
        keyflag++;
        String key = keyflag+"";
        map.put(key,currentPredicate);
        currentPredicate =null;
        currentPredicateNew =null;
        return this;
    }

    /**
     * 多个map复杂的条件再组合成为一个条件
     * */
    public Predicate buildPredicate(String type,String ...keys) {
        Predicate[] predicates =new Predicate[keys.length];
        for (int i = 0; i <keys.length ; i++) {
             predicates[i] =map.get(keys[i]);
        }
        Predicate predicate ="and".equalsIgnoreCase(type)
                            ? this.builder.and(predicates)
                            :this.builder.or(predicates);
        return predicate;
    }
    /**
     * 多个 buildPredicate创建的
     * predicate复杂的条件再组合成为一个条件
     * */
    public Predicate buildPredicate(String type,Predicate ...predicates) {
        Predicate predicate ="and".equalsIgnoreCase(type)
                ? this.builder.and(predicates)
                :this.builder.or(predicates);
        return predicate;
    }

    /**
     *如果没有制定key 则map 全部按照and 组合上一步构建的条件，并返回构建返回的typeQuery
     * */
    public TypedQuery<T> buildTypedQuery(){
        Predicate predicate =  buildPredicate("and",map.keySet().toArray(new  String[map.keySet().size()]));
        this.query.where(predicate);
        TypedQuery<T> tTypedQuery= this.entityManager.createQuery(this.query);
        return tTypedQuery;
    }

    public TypedQuery<T> buildTypedQueryGroup(String ...gorupby){
        Predicate predicate =  buildPredicate("and",map.keySet().toArray(new  String[map.keySet().size()]));
        this.query.where(predicate);
        groupBy(gorupby);
        TypedQuery<T> tTypedQuery= this.entityManager.createQuery(this.query);
        return tTypedQuery;
    }
    public TypedQuery<T> buildTypedQuerySort(LinkedHashMap<String,String> sortby){
        Predicate predicate =  buildPredicate("and",map.keySet().toArray(new  String[map.keySet().size()]));
        this.query.where(predicate);
        Sort sort = getToSort(sortby);
        if (sort != null) {
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
        }
        TypedQuery<T> tTypedQuery= this.entityManager.createQuery(this.query);
        return tTypedQuery;
    }

    public TypedQuery<T> buildTypedQueryGroupAndSort(LinkedHashMap<String,String> sortby,String ...gorupby){
        Predicate predicate =  buildPredicate("and",map.keySet().toArray(new  String[map.keySet().size()]));
        this.query.where(predicate);
        groupBy(gorupby);
        Sort sort = getToSort(sortby);
        if (sort != null) {
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
        }
        TypedQuery<T> tTypedQuery= this.entityManager.createQuery(this.query);
        return tTypedQuery;
    }

    public TypedQuery<T> buildTypedQueryByPredicate(String type,Predicate ...predicates){
        Predicate predicate =  buildPredicate(type,predicates);
        this.query.where(predicate);
        TypedQuery<T> tTypedQuery= this.entityManager.createQuery(this.query);
        return tTypedQuery;
    }
    public TypedQuery<T> buildTypedQueryGroupByPredicate(String type,String[] gorupby,Predicate ...predicates){
        Predicate predicate =  buildPredicate(type,predicates);
        this.query.where(predicate);
        groupBy(gorupby);
        TypedQuery<T> tTypedQuery= this.entityManager.createQuery(this.query);
        return tTypedQuery;
    }
    public TypedQuery<T> buildTypedQuerySortByPredicate(String type,LinkedHashMap<String,String> sortby,Predicate ...predicates){
        Predicate predicate =  buildPredicate(type,predicates);
        this.query.where(predicate);
        Sort sort = getToSort(sortby);
        if (sort != null) {
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
        }
        TypedQuery<T> tTypedQuery= this.entityManager.createQuery(this.query);
        return tTypedQuery;
    }

    public TypedQuery<T> buildTypedQueryGroupAndSortByPredicate(String type,String[] gorupby ,LinkedHashMap<String,String> sortby,Predicate ...predicates){
        Predicate predicate =  buildPredicate(type,predicates);
        this.query.where(predicate);
        groupBy(gorupby);
        Sort sort = getToSort(sortby);
        if (sort != null) {
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
        }
        TypedQuery<T> tTypedQuery= this.entityManager.createQuery(this.query);
        return tTypedQuery;
    }



    public WhereBuildUtil<T,ID> addLike(String key,Object value){
            if (ObjectUtils.isEmpty(currentPredicate)){
                currentPredicate = builder.like(getPath(key),"%/"+value+"%",'/');
            }else {
                currentPredicateNew =  builder.like(getPath(key),"%/"+value+"%",'/');
            }
            return this;
    }
    public WhereBuildUtil<T,ID> addNotLike(String key,Object value){
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate = builder.notLike(getPath(key),"%/"+value+"%",'/');
        }else {
            currentPredicateNew=  builder.notLike(getPath(key),"%/"+value+"%",'/');
        }
         return this;
    }
    public WhereBuildUtil<T,ID> addLt(String key,Object value){
        Path  path =getPath(key);
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate = path.getJavaType().getSimpleName().contains("Date")
                    ?builder.lessThan(path,getTime(value.toString()))
                    :builder.lt(path,new BigDecimal(value.toString()));
        }else {
            currentPredicateNew=  path.getJavaType().getSimpleName().contains("Date")
                    ?builder.lessThan(path,getTime(value.toString()))
                    :builder.lt(path,new BigDecimal(value.toString()));
        }
        return this;
    }
    public WhereBuildUtil<T,ID> addLe(String key,Object value){
        Path  path =getPath(key);
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate =path.getJavaType().getSimpleName().contains("Date")
                    ?builder.lessThanOrEqualTo(path,getTime(value.toString()))
                    :builder.le(path,new BigDecimal(value.toString()));
        }else {
            currentPredicateNew=  path.getJavaType().getSimpleName().contains("Date")
                    ?builder.lessThanOrEqualTo(path,getTime(value.toString()))
                    :builder.le(path,new BigDecimal(value.toString()));
        }
        return this;
    }
    public WhereBuildUtil<T,ID> addGt(String key,Object value){
        Path  path =getPath(key);
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate =path.getJavaType().getSimpleName().contains("Date")
                    ?builder.greaterThan(path,getTime(value.toString()))
                    :builder.gt(path,new BigDecimal(value.toString()));
        }else {
            currentPredicateNew= path.getJavaType().getSimpleName().contains("Date")
                    ?builder.greaterThan(path,getTime(value.toString()))
                    :builder.gt(path,new BigDecimal(value.toString()));
        }
        return this;
    }
    public WhereBuildUtil<T,ID> addGe(String key,Object value){
        Path  path =getPath(key);
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate =path.getJavaType().getSimpleName().contains("Date")
                    ?builder.greaterThanOrEqualTo(path,getTime(value.toString()))
                    :builder.ge(path,new BigDecimal(value.toString()));
        }else {
            currentPredicateNew=path.getJavaType().getSimpleName().contains("Date")
                    ?builder.greaterThanOrEqualTo(path,getTime(value.toString()))
                    :builder.ge(path,new BigDecimal(value.toString()));
        }
         return this;
    }
    public WhereBuildUtil<T,ID> addEq(String key,Object value){
            Path  path =getPath(key);
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate =path.getJavaType().getSimpleName().contains("Date")
                    ?builder.equal(path,getTime(value.toString()))
                    :builder.equal(path,value);
        }else {
            currentPredicateNew=  path.getJavaType().getSimpleName().contains("Date")
                    ?builder.equal(path,getTime(value.toString()))
                    :builder.equal(path,value);
        }
        return this;
    }
    public WhereBuildUtil<T,ID> addNotEq(String key,Object value){
        Path  path =getPath(key);
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate =path.getJavaType().getSimpleName().contains("Date")
                    ?builder.notEqual(path,getTime(value.toString()))
                    :builder.notEqual(path,value);
        }else {
            currentPredicateNew= path.getJavaType().getSimpleName().contains("Date")
                    ?builder.notEqual(path,getTime(value.toString()))
                    :builder.notEqual(path,value);
        }
        return this;
    }

    public WhereBuildUtil<T,ID> addIn(String key,Object[] value){
        Path  path =getPath(key);
        CriteriaBuilder.In in = builder.in(path);
        Object[] objects =(Object[])value;
        for (int i = 0; i <objects.length ; i++) {
            in.value(objects[i]);
        }
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate =in;
        }else {
            currentPredicateNew =in;
        }
        return this;
    }
    public WhereBuildUtil<T,ID> addNotIn(String key,Object[] value){
        Path  path =getPath(key);
        CriteriaBuilder.In inn = builder.in(path);
        Object[] objects1 =(Object[])value;
        for (int i = 0; i <objects1.length ; i++) {
            inn.value(objects1[i]);
        }
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate =builder.not(inn);
        }else {
            currentPredicateNew =builder.not(inn);
        }
        return this;
    }
    public WhereBuildUtil<T,ID> addIsNull(String key){
        Path  path =getPath(key);
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate = builder.isNull(path);
        }else {
            currentPredicateNew = builder.isNull(path);
        }
        return this;
    }
    public WhereBuildUtil<T,ID> addIsNotNull(String key){
        Path  path =getPath(key);
        if (ObjectUtils.isEmpty(currentPredicate)){
            currentPredicate =  builder.isNotNull(path);
        }else {
            currentPredicateNew =  builder.isNotNull(path);
        }
        return this;
    }

    public Path getPath(String key){
        Assert.notNull(key, "key值不能为空");
        Path path =null;
        if (key.contains(".")) {
            String[] keys = StringUtils.split(key, ".");
            path = root.get(keys[0]);
            Class clazz = path.getJavaType();
            if (Set.class.isAssignableFrom(clazz)) {
                SetJoin setJoin = root.joinSet(keys[0]);
                for (int i = 1; i <keys.length ; i++) {
                    path = i==1?setJoin.get(keys[i]):path.get(keys[i]);
                }
            } else if (List.class.isAssignableFrom(clazz)) {
                ListJoin listJoin = root.joinList(keys[0]);
                for (int i = 1; i <keys.length ; i++) {
                    path = i==1?listJoin.get(keys[i]):path.get(keys[i]);
                }
            } else if (Map.class.isAssignableFrom(clazz)) {
                MapJoin mapJoin = root.joinMap(keys[0]);
                for (int i = 1; i <keys.length ; i++) {
                    path= i==1?mapJoin.get(keys[i]):path.get(keys[i]);
                }
            } else {
                //是many to one时
                for (int i = 1; i <keys.length ; i++) {
                    path = path.get(keys[i]);
                }
            }
        }else {//基本类型
            path = root.get(key);
        }
        return  path;
    }

    public  static LocalDateTime getTime(String timeStr){
        String pattern= timeStr.matches("\\d{4}-\\d{2}-\\{d}{2}\\s+\\d{2}:\\d{2}:\\d{2}")?
                "yyyy-MM-dd HH:mm:ss":"yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime =  LocalDateTime.parse(timeStr,formatter);
        return  localDateTime;

    }

    public  void groupBy(String... fieldNames){
        if (fieldNames!=null){
            Path[] paths =new  Path [fieldNames.length];
            for (int i = 0; i < fieldNames.length ; i++) {
                String[] panthNames= fieldNames[i].split("\\.");
                Path path=null;
                for (int j = 0; j < panthNames.length; j++) {
                    path =j==0?root.get(panthNames[j]):path.get(panthNames[j]);
                }
                paths[i] = path;
            }
            query.groupBy(paths);
        }
    }

    /**
     *获取排序
     * */
    public Sort getToSort(LinkedHashMap sort){
        List<Sort.Order> list =new ArrayList<>();
        if (!CollectionUtils.isEmpty(sort)){
               Set<Map.Entry<String,String>>  entries =sort.entrySet();
            Iterator<Map.Entry<String,String>>  entryIterator =  entries.iterator();
            while (entryIterator.hasNext()){
                Map.Entry<String,String> entry =entryIterator.next();
                Sort.Direction direction=  "desc".equalsIgnoreCase(entry.getValue())
                        ?Sort.Direction.DESC
                        :Sort.Direction.ASC;
                list.add(new Sort.Order(direction,entry.getKey()));
            }
        }
        return  new Sort(list);
    }
}
