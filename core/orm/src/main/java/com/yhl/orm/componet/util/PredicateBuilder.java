package com.yhl.orm.componet.util;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PredicateBuilder <T,ID extends Serializable> {

    private CriteriaBuilder builder ;

    private Root<T> root;

    private List<Predicate> list;

    private List<String> types;

    public PredicateBuilder (CriteriaBuilder builder ,Root<T> root){
      this.builder =builder;
      this.root =root;
      this.list =new ArrayList<>();
      this.types =new ArrayList<>();
    }
    /**
     * 当产生一个Predicate与下一个即将产生Predicate的的连接条件 为and
     * */
    public PredicateBuilder<T,ID> and (){
        types.add("and");
        return this;
    }

    /**
     * 当产生一个Predicate与下一个即将产生Predicate的的连接条件 为and
     * */
    public PredicateBuilder<T,ID> or (){
        types.add("or");
        return this;
    }
    /**
     * 当一系列统一层级的Predicate定义完成后，此方法合并成为一个Predicate
     * 注意and 与 or 的关联，请与sql and 与or 相同，否则可能与你意想的结果可能不一样
     * */
    public Predicate  end (){
        System.out.println(list.size());
        System.out.println(types.size());
        Assert.isTrue(list.size()-1==types.size(),"请条件表达式 和链接表达式不明显");
        Predicate predicate =null;
        for (int i = 0; i <list.size() ; i++) {
            if (i==0){
                predicate =list.get(0) ;
            }else {
                predicate ="and".equals(types.get(i-1))
                            ?builder.and(predicate,list.get(i))
                            :builder.or(predicate,list.get(i));
            }
        }
        types.clear();
        list.clear();
        return predicate;
    }

    public PredicateBuilder<T,ID> addLike(String key,Object value){
        list.add(builder.like(getPath(key),"%/"+value+"%",'/'));
        return this;
    }
    public PredicateBuilder<T,ID> addNotLike(String key,Object value){
        list.add(builder.notLike(getPath(key),"%/"+value+"%",'/'));
        return this;
    }
    public PredicateBuilder<T,ID> addLt(String key,Object value){
        Path path =getPath(key);
        list.add(path.getJavaType().getSimpleName().contains("Date")
                ?builder.lessThan(path,getTime(value.toString(),path))
                :builder.lt(path,new BigDecimal(value.toString())));
        return this;
    }
    public PredicateBuilder<T,ID> addLe(String key,Object value){
        Path  path =getPath(key);
        list.add( path.getJavaType().getSimpleName().contains("Date")
                ?builder.lessThanOrEqualTo(path,getTime(value.toString(),path))
                :builder.le(path,new BigDecimal(value.toString())));
        return this;
    }
    public PredicateBuilder<T,ID> addGt(String key,Object value){
        Path  path =getPath(key);
        list.add( path.getJavaType().getSimpleName().contains("Date")
                ?builder.greaterThan(path,getTime(value.toString(),path))
                :builder.gt(path,new BigDecimal(value.toString())));
        return this;
    }
    public PredicateBuilder<T,ID> addGe(String key,Object value){
        Path  path =getPath(key);
        list.add( path.getJavaType().getSimpleName().contains("Date")
                ?builder.greaterThanOrEqualTo(path,getTime(value.toString(),path))
                :builder.ge(path,new BigDecimal(value.toString())));
        return this;
    }
    public PredicateBuilder<T,ID> addEq(String key,Object value){
        Path  path =getPath(key);
        list.add(path.getJavaType().getSimpleName().contains("Date")
                ?builder.equal(path,getTime(value.toString(),path))
                :builder.equal(path,value));
        return this;
    }
    public PredicateBuilder<T,ID> addNotEq(String key,Object value){
        Path  path =getPath(key);
        list.add(path.getJavaType().getSimpleName().contains("Date")
                ?builder.notEqual(path,getTime(value.toString(),path))
                :builder.notEqual(path,value));
        return this;
    }
    public PredicateBuilder<T,ID> addIn(String key,Object[] value){
        Path  path =getPath(key);
        CriteriaBuilder.In in = builder.in(path);
        Object[] objects =(Object[])value;
        for (int i = 0; i <objects.length ; i++) {
            in.value(objects[i]);
        }
        list.add(in);
        return this;
    }
    public PredicateBuilder<T,ID> addNotIn(String key,Object[] value){
        Path  path =getPath(key);
        CriteriaBuilder.In inn = builder.in(path);
        Object[] objects1 =(Object[])value;
        for (int i = 0; i <objects1.length ; i++) {
            inn.value(objects1[i]);
        }
        list.add(builder.not(inn));
        return this;
    }
    public PredicateBuilder<T,ID> addIsNull(String key){
        Path  path =getPath(key);
        list.add(builder.isNull(path));
        return this;
    }
    public PredicateBuilder<T,ID> addIsNotNull(String key){
        Path  path =getPath(key);
        list.add(builder.isNotNull(path));
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

    /**
     * 时间统一字符串
     * */
    public  static Comparable  getTime(String timeStr,Path path){
        Class timeclass =path.getJavaType();
        Comparable comparable=null;
        String pattern= timeStr.matches("\\d{4}-\\d{2}-\\{d}{2}\\s+\\d{2}:\\d{2}:\\d{2}")?
                "yyyy-MM-dd HH:mm:ss":"yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            if (LocalDateTime.class.isAssignableFrom(timeclass)){
                comparable=  LocalDateTime.parse(timeStr,formatter);
             }else if (LocalDate.class.isAssignableFrom(timeclass)){
                comparable =LocalDate.parse(timeStr,formatter);
            }else if(Date.class.isAssignableFrom(timeclass)){
             comparable =new SimpleDateFormat(pattern).parse(timeStr);
            }else if (ZonedDateTime.class.isAssignableFrom(timeclass)){
                LocalDateTime localDateTime =   LocalDateTime.parse(timeStr,formatter);
                ZoneId zone = ZoneId.systemDefault();
                comparable = localDateTime.atZone(zone);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  comparable;

    }
}
