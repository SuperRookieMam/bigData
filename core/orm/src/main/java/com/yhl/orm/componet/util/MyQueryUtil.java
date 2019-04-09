package com.yhl.orm.componet.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.orm.componet.constant.ConnectCondition;
import com.yhl.orm.componet.constant.WhereCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MyQueryUtil {
    /**
    * 获取构建好wherecondition条件的TypedQuery
    * */
    public static <T> TypedQuery<T> getTypedQuery(Class<T> tClass, EntityManager entityManager, WhereCondition whereCondition){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(tClass);
        Root<T> root =applySpecificationToCriteria(whereCondition,tClass,query,entityManager);
        //分组
        JSONArray jsonArray =whereCondition.getGroupby();
         if (jsonArray!=null){
             String[] fieldNames =jsonArray.toArray(new String[jsonArray.size()]);
             groupBy(query, root, fieldNames);
         }
        //排序
        Sort sort = getToSort( whereCondition);
        if (sort != null) {
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
        }
        return  entityManager.createQuery(query);
    }
    private  static  CriteriaQuery groupBy(CriteriaQuery  query,Root root,String[] fieldNames){
         if (fieldNames!=null){
             Path[] paths =new  Path[fieldNames.length];
             for (int i = 0; i < fieldNames.length ; i++) {
                String fieldName =fieldNames[i];
                String[] panthNames= fieldName.split("\\.");
                 Path path=null;
                 for (int j = 0; j < panthNames.length; j++) {
                    if (j==0){
                        path =root.get(panthNames[j]);
                    }else {
                        path =path.get(panthNames[j]);
                    }
                 }
                 paths[i] = path;
             }
             return  query.groupBy(paths);
         }
        return query;
    }
    private static <T>  Root<T> applySpecificationToCriteria(WhereCondition whereCondition, Class<T> domainClass, CriteriaQuery query,EntityManager entityManager) {
            Assert.notNull(domainClass, "实体必须不为空!");
            Assert.notNull(query, "CriteriaQuery 必须不为空!");
            Root<T> root = query.from(domainClass);
            if (whereCondition == null) {
                return root;
            } else {
                CriteriaBuilder builder = entityManager.getCriteriaBuilder();
                Predicate predicate = toPredicate(root,builder,whereCondition);
                if (predicate != null) {
                    query.where(predicate);
                }
                return root;
            }
    }
    /**
     *获取wherecondition条件构建的 Predicate
     * */
    public static <T> Predicate toPredicate(Root<T> root, CriteriaBuilder criteriaBuilder,WhereCondition whereCondition) {
        List<Predicate> predicates = getPredicates(root,criteriaBuilder,whereCondition);
        int m =predicates.size();
        if (m>0){
            return criteriaBuilder.and(predicates.toArray(new  Predicate[predicates.size()]));
        }
        return criteriaBuilder.conjunction();
    }
    /**
     * 获得过滤条件数组
     * */
    private static<T>  List<Predicate> getPredicates(Root<T> root, CriteriaBuilder criteriaBuilder,WhereCondition whereCondition){
        List<Predicate> predicates = new ArrayList();
        ConnectCondition and =whereCondition.getAnd();
        ConnectCondition or =whereCondition.getOr();
        if (!ObjectUtils.isEmpty(and)){
            Predicate[] predicates1 = getPredicateArray(root,and,criteriaBuilder);
            if (predicates1!=null){
                Predicate predicate=  criteriaBuilder.and(predicates1);
                predicates.add(predicate);
            }
        }
        if (!ObjectUtils.isEmpty(or)){
            Predicate[] predicates1 = getPredicateArray(root,or,criteriaBuilder);
            if (predicates1!=null){
                Predicate predicate=  criteriaBuilder.or(predicates1);
                predicates.add(predicate);
            }
        }
        return  predicates;
    }

    private static <T> Predicate[] getPredicateArray(Root<T> root, ConnectCondition connectCondition , CriteriaBuilder criteriaBuilder) {
        List<Predicate> list = new ArrayList<>();
        JSONObject json = connectCondition.getLike();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                Predicate predicate= criteriaBuilder.like(entry.getKey(),"%/"+entry.getValue()+"%",'/');
                list.add(predicate);
            }
        }
        json = connectCondition.getNotLike();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                Predicate predicate= criteriaBuilder.notLike(entry.getKey(),"%/"+entry.getValue()+"%",'/');
                list.add(predicate);
            }
        }
        json = connectCondition.getLt();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                //判断时不时时间格式.只要匹配的倒时间格式则用时间比较
                Predicate predicate=null;
                String value=  entry.getValue().toString();
                //比大小只支持时间和数字
                if (value.matches("\\d{4}-\\d{2}-\\{d}{2}")
                        ||value.matches("\\d{4}-\\d{2}-\\{d}{2}\\s+\\d{2}:\\d{2}:\\d{2}")){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(entry.getValue().toString(), formatter);
                    predicate = criteriaBuilder.lessThan(entry.getKey(),dateTime);
                }else {
                    predicate= criteriaBuilder.lt(entry.getKey(),new BigDecimal(entry.getValue().toString()));
                }
                list.add(predicate);
            }
        }

        json = connectCondition.getLe();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                //判断时不时时间格式.只要匹配的倒时间格式则用时间比较
                Predicate predicate=null;
                String value=  entry.getValue().toString();
                //比大小只支持时间和数字
                if (value.matches("\\d{4}-\\d{2}-\\{d}{2}")
                        ||value.matches("\\d{4}-\\d{2}-\\{d}{2}\\s+\\d{2}:\\d{2}:\\d{2}")){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(entry.getValue().toString(), formatter);
                    predicate = criteriaBuilder.lessThanOrEqualTo(entry.getKey(),dateTime);
                }else {
                    predicate= criteriaBuilder.le(entry.getKey(),new BigDecimal(entry.getValue().toString()));
                }
                list.add(predicate);
            }
        }

        json = connectCondition.getGt();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                //判断时不时时间格式.只要匹配的倒时间格式则用时间比较
                Predicate predicate=null;
                String value=  entry.getValue().toString();
                //比大小只支持时间和数字
                if (value.matches("\\d{4}-\\d{2}-\\{d}{2}")
                        ||value.matches("\\d{4}-\\d{2}-\\{d}{2}\\s+\\d{2}:\\d{2}:\\d{2}")){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(entry.getValue().toString(), formatter);
                    predicate = criteriaBuilder.greaterThan(entry.getKey(),dateTime);
                }else {
                    predicate= criteriaBuilder.gt(entry.getKey(),new BigDecimal(entry.getValue().toString()));
                }
                list.add(predicate);
            }
        }
        json = connectCondition.getGe();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                //判断时不时时间格式.只要匹配的倒时间格式则用时间比较
                Predicate predicate=null;
                String value=  entry.getValue().toString();
                //比大小只支持时间和数字
                if (value.matches("\\d{4}-\\d{2}-\\{d}{2}")
                        ||value.matches("\\d{4}-\\d{2}-\\{d}{2}\\s+\\d{2}:\\d{2}:\\d{2}")){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(entry.getValue().toString(), formatter);
                    predicate = criteriaBuilder.greaterThanOrEqualTo(entry.getKey(),dateTime);
                }else {
                    predicate= criteriaBuilder.ge(entry.getKey(),new BigDecimal(entry.getValue().toString()));
                }
                list.add(predicate);
            }
        }
        json = connectCondition.getEq();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                //判断时不时时间格式.只要匹配的倒时间格式则用时间比较
                Predicate predicate=null;
                String value=  entry.getValue().toString();
                //比大小只支持时间和数字
                if (value.matches("\\d{4}-\\d{2}-\\{d}{2}")
                        ||value.matches("\\d{4}-\\d{2}-\\{d}{2}\\s+\\d{2}:\\d{2}:\\d{2}")){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(entry.getValue().toString(), formatter);
                    predicate = criteriaBuilder.equal(entry.getKey(),dateTime);
                }else {
                    predicate= criteriaBuilder.equal(entry.getKey(),entry.getValue());
                }
                list.add(predicate);
            }
        }

        json = connectCondition.getNotEq();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                //判断时不时时间格式.只要匹配的倒时间格式则用时间比较
                Predicate predicate=null;
                String value=  entry.getValue().toString();
                //比大小只支持时间和数字
                if (value.matches("\\d{4}-\\d{2}-\\{d}{2}")
                        ||value.matches("\\d{4}-\\d{2}-\\{d}{2}\\s+\\d{2}:\\d{2}:\\d{2}")){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(entry.getValue().toString(), formatter);
                    predicate = criteriaBuilder.notEqual(entry.getKey(),dateTime);
                }else {
                    predicate= criteriaBuilder.notEqual(entry.getKey(),entry.getValue());
                }
                list.add(predicate);
            }
        }

        json = connectCondition.getIn();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                CriteriaBuilder.In predicate= criteriaBuilder.in(entry.getKey());
                Object[] objects =(Object[]) entry.getValue();
                for (int i = 0; i <objects.length; i++) {
                    predicate.value(objects[i]);
                }
                list.add(predicate);
            }
        }
        json = connectCondition.getNotIn();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                CriteriaBuilder.In predicate= criteriaBuilder.in(entry.getKey());
                Object[] objects =(Object[]) entry.getValue();
                for (int i = 0; i <objects.length ; i++) {
                    predicate.value(objects[i]);
                }
                list.add(criteriaBuilder.not(predicate));
            }
        }

        json = connectCondition.getIsNull();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                Predicate predicate= criteriaBuilder.isNull(entry.getKey());
                list.add(predicate);
            }
        }

        json = connectCondition.getIsNotNull();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                Predicate predicate= criteriaBuilder.isNotNull(entry.getKey());
                list.add(predicate);
            }
        }

        json = connectCondition.getIsMember();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                //注意这个时值哈,且注意Path 的参数，is
                Object[] objects =(Object[])entry.getValue();
                for (int i = 0; i < objects.length; i++) {
                    Predicate predicate= criteriaBuilder.isMember(objects[i],entry.getKey());
                    list.add(predicate);
                }

            }
        }
        json = connectCondition.getIsNotMember();
        if (!ObjectUtils.isEmpty(json)){
            Map<Path,Object> map  = getPath(root,json);
            Iterator<Map.Entry<Path,Object>> iterator1 =  map.entrySet().iterator();
            while (iterator1.hasNext()){
                Map.Entry<Path,Object> entry = iterator1.next();
                //注意这个时值哈,且注意Path 的参数，is
                Object[] objects =(Object[])entry.getValue();
                for (int i = 0; i < objects.length; i++) {
                    Predicate predicate= criteriaBuilder.isNotMember(objects[i],entry.getKey());
                    list.add(predicate);
                }

            }
        }
        return list.size()==0?null:list.toArray(new Predicate[list.size()]);
    }

    /**
     *取得引用对象的值比如说field.field2.field3 的值
     * */
    private static <T> Map<Path,Object> getPath(Root<T> root, JSONObject jsonObject){
        Map<Path,Object> map =null;
        if(!ObjectUtils.isEmpty(jsonObject)){
            Iterator<String> iterator = jsonObject.keySet().iterator();
            map =new HashMap<>();
            Path path=null;
            while (iterator.hasNext()){
                String  key =iterator.next();
                //此处是表关联数据，注意仅限一层关联，如user.address，
                //查询user的address集合中，address的name为某个值
                if (key.contains(".")) {
                    String[] keys = StringUtils.split(key, ".");
                    //获取该属性的类型，Set？List？Map？
                    path = root.get(keys[0]);
                    Class clazz = path.getJavaType();
                    if (clazz.equals(Set.class)) {
                        SetJoin setJoin = root.joinSet(keys[0]);
                        for (int i = 1; i <keys.length ; i++) {
                            path = i==1?setJoin.get(keys[i]):path.get(keys[i]);
                        }
                    } else if (clazz.equals(List.class)) {
                        ListJoin listJoin = root.joinList(keys[0]);
                        for (int i = 1; i <keys.length ; i++) {
                            path = i==1?listJoin.get(keys[i]):path.get(keys[i]);
                        }
                    } else if (clazz.equals(Map.class)) {
                        MapJoin mapJoin = root.joinMap(keys[0]);
                        for (int i = 1; i <keys.length ; i++) {
                            path = i==1?mapJoin.get(keys[i]):path.get(keys[i]);
                        }
                    } else {
                        //是many to one时
                        for (int i = 1; i <keys.length ; i++) {
                            path = path.get(keys[i]);
                        }
                    }
                } else {
                    //单表查询
                    path = root.get(key);
                }
                map.put(path,jsonObject.get(key));
            }
        }
        return map;
    }
    /**
     *取构建好wherecondition条件的查询条数的TypedQuery
     * */
    public static <T> TypedQuery<Long> getCountQuery(Class<T> tClass, EntityManager entityManager,WhereCondition whereCondition) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = applySpecificationToCriteria(whereCondition,tClass,query,entityManager);
        //分组
        JSONArray jsonArray =whereCondition.getGroupby();
        if (jsonArray!=null){
            String[] fieldNames =jsonArray.toArray(new String[jsonArray.size()]);
            groupBy(query, root, fieldNames);
        }
        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }
        query.orderBy(Collections.emptyList());
        return entityManager.createQuery(query);
    }
    /**
     *分页查询
     * */
    public static <T> Page<T> readPage(WhereCondition whereCondition, Class<T> tClass, EntityManager entityManager) {
        TypedQuery<T> query=getTypedQuery(tClass,entityManager,whereCondition);
        Sort sort = getToSort(whereCondition);
        PageRequest pageable =new PageRequest(whereCondition.getPageNum() - 1, whereCondition.getPageSize(),sort);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        long l=executeCountQuery(getCountQuery(tClass, entityManager, whereCondition));
        return new PageImpl(query.getResultList(), pageable, l);
    }


    /**
     * 执行查询条数
     * */
    public static Long executeCountQuery(TypedQuery<Long> query) {
        Assert.notNull(query, "TypedQuery must not be null!");
        List<Long> totals = query.getResultList();
        Long total = 0L;
        Long element;
        for(Iterator var3 = totals.iterator(); var3.hasNext(); total = total + (element == null ? 0L : element)) {
            element = (Long)var3.next();
        }
        return total;
    }


    /**
     *获取排序
     * */
    public static Sort getToSort(WhereCondition whereCondition){
        JSONArray sort =whereCondition.getSort();
        if (sort==null||sort.isEmpty()){
            return null;
        }else {
            List<Sort.Order> list =new ArrayList<>();
            for (int i = 0; i < sort.size(); i++) {
                JSONObject jsonObject =sort.getJSONObject(i);
                Sort.Direction direction=  "desc".equalsIgnoreCase(jsonObject.getString("sortType"))
                        ?Sort.Direction.DESC
                        :Sort.Direction.ASC;
                list.add(new Sort.Order(direction,jsonObject.getString("fieldName")));
            }
            return   new Sort(list);
        }
    }

    /**
     *根据实体
     * */
    public static <T> CriteriaQuery<T> getCriteriaQuery(Class<T> tClass, EntityManager entityManager){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        return builder.createQuery(tClass);
    }
    /**
     * 获取实体根路路径
     * */
    public static <T> Root<T>  getRoot(Class<T> tClass,  CriteriaQuery<T> query){
        return query.from(tClass);
    }
    /**
     * 获取实体CriteriaBuilder
     * */
    public static  CriteriaBuilder  getCriteriaQuery(EntityManager entityManager){
        return entityManager.getCriteriaBuilder();
    }
    /**
     * 根据你自定义的query 添加好条件后构建TypedQuery
     * */
    public static <T> TypedQuery<T> getTypedQuery(EntityManager entityManager,CriteriaQuery<T> query){
          return  entityManager.createQuery(query);
    }

}
