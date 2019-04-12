package com.yhl.orm.componet.util;

import com.yhl.orm.dao.JpaBaseDao;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WhereBuildUtil<T,ID extends Serializable>{

    private Predicate currentPredicate;

    private Predicate currentPredicateNew;

    private CriteriaBuilder  builder ;

    private Root<T> root;

    public  WhereBuildUtil(JpaBaseDao<T,ID> jpaBaseDao){
        this.builder =jpaBaseDao.getBuilder();
        this.root =jpaBaseDao.getRoot();
    }
    public WhereBuildUtil<T,ID> and(){
        Assert.notNull(currentPredicateNew, "请先构建条件再连接");
        currentPredicate =ObjectUtils.isEmpty(currentPredicate)
                          ?currentPredicateNew
                          :builder.and(currentPredicate,currentPredicateNew);
        return this;
    }
    public WhereBuildUtil<T,ID> or(){
        Assert.notNull(currentPredicateNew, "请先构建条件再连接");
        currentPredicate =ObjectUtils.isEmpty(currentPredicate)
                ?currentPredicateNew
                :builder.or(currentPredicate,currentPredicateNew);
        return this;
    }
    public Predicate end(){
        Assert.notNull(currentPredicateNew, "请先构建条件再连接");
        Predicate predicate =currentPredicate;
        currentPredicate =null;
        currentPredicateNew =null;
        return predicate;
    }
    public Predicate build(Predicate predicate1,Predicate predicate2,String type){
        if (ObjectUtils.isEmpty(predicate1)||ObjectUtils.isEmpty(predicate2)){
            Assert.notNull(currentPredicateNew, "请先构建条件再连接");
        }
        if ("and".equalsIgnoreCase(type)&&"or".equalsIgnoreCase(type)){
            Assert.notNull(null, "请明确构建类型 and 或 or");
        }
        Predicate predicate ="and".equalsIgnoreCase(type)
                             ?builder.and(predicate1,predicate2)
                             :builder.or(predicate1,predicate2);
        return predicate;
    }

    public WhereBuildUtil<T,ID> addLike(String key,Object value){
            currentPredicateNew =  builder.like(getPath(key),"%/"+value+"%",'/');
            return this;
    }
    public WhereBuildUtil<T,ID> addNotLike(String key,Object value){
            currentPredicateNew=  builder.notLike(getPath(key),"%/"+value+"%",'/');
         return this;
    }
    public WhereBuildUtil<T,ID> addLt(String key,Object value){
        Path  path =getPath(key);
            currentPredicateNew= path.getJavaType().getSimpleName().contains("Date")
                    ?builder.lessThan(path,getTime(value.toString()))
                    :builder.lt(path,new BigDecimal(value.toString()));
        return this;
    }
    public WhereBuildUtil<T,ID> addLe(String key,Object value){
        Path  path =getPath(key);
            currentPredicateNew= path.getJavaType().getSimpleName().contains("Date")
                    ?builder.lessThanOrEqualTo(path,getTime(value.toString()))
                    :builder.le(path,new BigDecimal(value.toString()));
        return this;
    }
    public WhereBuildUtil<T,ID> addGt(String key,Object value){
        Path  path =getPath(key);
            currentPredicateNew= path.getJavaType().getSimpleName().contains("Date")
                    ?builder.greaterThan(path,getTime(value.toString()))
                    :builder.gt(path,new BigDecimal(value.toString()));

        return this;
    }
    public WhereBuildUtil<T,ID> addGe(String key,Object value){
        Path  path =getPath(key);
            currentPredicateNew= path.getJavaType().getSimpleName().contains("Date")
                    ?builder.greaterThanOrEqualTo(path,getTime(value.toString()))
                    :builder.ge(path,new BigDecimal(value.toString()));
         return this;
    }
    public WhereBuildUtil<T,ID> addEq(String key,Object value){
            Path  path =getPath(key);
            currentPredicateNew = path.getJavaType().getSimpleName().contains("Date")
                    ?builder.equal(path,getTime(value.toString()))
                    :builder.equal(path,value);
        return this;
    }
    public WhereBuildUtil<T,ID> addNotEq(String key,Object value){
        Path  path =getPath(key);
            currentPredicateNew = path.getJavaType().getSimpleName().contains("Date")
                    ?builder.notEqual(path,getTime(value.toString()))
                    :builder.notEqual(path,value);
        return this;
    }

    public WhereBuildUtil<T,ID> addIn(String key,Object[] value){
        Path  path =getPath(key);
        CriteriaBuilder.In in = builder.in(path);
        Object[] objects =(Object[])value;
        for (int i = 0; i <objects.length ; i++) {
            in.value(objects[i]);
        }
            currentPredicateNew =in;
        return this;
    }
    public WhereBuildUtil<T,ID> addNotIn(String key,Object[] value){
        Path  path =getPath(key);
        CriteriaBuilder.In inn = builder.in(path);
        Object[] objects1 =(Object[])value;
        for (int i = 0; i <objects1.length ; i++) {
            inn.value(objects1[i]);
        }
            currentPredicateNew =builder.not(inn);
        return this;
    }
    public WhereBuildUtil<T,ID> addIsNull(String key){
        Path  path =getPath(key);
            currentPredicateNew = builder.isNull(path);
        return this;
    }
    public WhereBuildUtil<T,ID> addIsNotNull(String key){
        Path  path =getPath(key);
            currentPredicateNew = builder.isNotNull(path);
        return this;
    }

    public Path getPath(String key){
        Assert.notNull(key, "key值不能为空");
        Path path =null;
        if (key.contains(".")) {
            String[] keys = StringUtils.split(key, "\\.");
            assert keys != null;
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

}
