package com.yhl.orm.componet.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.orm.componet.constant.Expression;
import com.yhl.orm.componet.constant.WhereContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PresentWhereContextUtil {

    /**
     * 获取构建好wherecondition条件的TypedQuery
     * */
    public static <T> TypedQuery<T> getTypedQuery(Class<T> tClass, EntityManager entityManager, WhereContext whereContext){
        CriteriaBuilder  builder=entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(tClass);
        //获取查询条件
        Expression[] expressions = whereContext.getExpressions().toArray(new Expression[whereContext.getExpressions().size()]);

        Root<T> root =query.from(tClass);
        //构建查询条件

        //查询条件为空
        if (expressions.length>0){
            query.where(expressionToPredicate( builder,root,expressions));
        }else {
            query.where();
        }
        // 构建分组
        if (!ObjectUtils.isEmpty(whereContext.getGroupby())){
            groupBy(query,root,whereContext.getGroupby().toArray(new String[whereContext.getGroupby().size()]));
        }
        //排序
        Sort sort = getToSort( whereContext.getSort());
        if (sort != null) {
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
        }
        return  entityManager.createQuery(query);
    }
    /**
     * 获取构建好wherecondition条件的TypedQuery
     * */
    public static <T> CriteriaQuery<T> getCriteriaQueryByPredicate(Class<T> tClass, EntityManager entityManager, Predicate predicate){
        CriteriaBuilder  builder=entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(tClass);
                query.from(tClass);
        if (!ObjectUtils.isEmpty(predicate))
                    query.where(predicate);
        else
            query.where();
        return  query;
    }
    /**
     * 获取构建好wherecondition条件的TypedQuery
     * */
    public static<T>  CriteriaQuery<Long> getContQueryByPredicate(Class<T> tClass, EntityManager entityManager, Predicate predicate,boolean distinct){
        CriteriaBuilder  builder=entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root root =query.from(tClass);
        if (!ObjectUtils.isEmpty(predicate))
            query.where(predicate);
        else
            query.where();
        if (distinct){
            query.distinct(distinct);
        }
        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }
        return  query;
    }
    public static<T> Predicate expressionToPredicate(CriteriaBuilder  builder,Root<T> root,Expression[] expressions){
        Predicate predicate =null;
        if (!ObjectUtils.isEmpty(expressions)) {
            List<Predicate> predicateList = new ArrayList<>();
            for (Expression expression : expressions) {
                predicateList.add(presentExPression(builder, root, expression));
            }
            if (!predicateList.isEmpty())
                predicate = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        }
        return predicate;
    }

    public static<T> Predicate presentExPression(CriteriaBuilder  builder, Root<T> root, Expression expression){
        Predicate predicate =null;
        if (expression.getUnique()==1){
            Path path =expression.getPath(root);
            Object value =expression.getValue();
            switch (expression.getType()){
                case "like":
                    predicate = builder.like(path,"%/"+value+"%",'/');
                    break;
                case "notLike":
                    predicate = builder.notLike(path,"%/"+value+"%",'/');
                    break;
                case "lt":
                    predicate = path.getJavaType().getSimpleName().contains("Date")
                                ?builder.lessThan(path,getTime(value.toString(),path))
                                :builder.lt(path,new BigDecimal(value.toString()));
                    break;
                case "le":
                    predicate = path.getJavaType().getSimpleName().contains("Date")
                            ?builder.lessThanOrEqualTo(path,getTime(value.toString(),path))
                            :builder.le(path,new BigDecimal(value.toString()));
                    break;
                case "gt":
                    predicate = path.getJavaType().getSimpleName().contains("Date")
                            ?builder.greaterThan(path,getTime(value.toString(),path))
                            :builder.gt(path,new BigDecimal(value.toString()));
                    break;
                case "ge":
                    predicate = path.getJavaType().getSimpleName().contains("Date")
                            ?builder.greaterThanOrEqualTo(path,getTime(value.toString(),path))
                            :builder.ge(path,new BigDecimal(value.toString()));
                    break;
                case "eq":
                    predicate = path.getJavaType().getSimpleName().contains("Date")
                            ?builder.equal(path,getTime(value.toString(),path))
                            :builder.equal(path,value);
                    break;
                case "notEq":
                    predicate = path.getJavaType().getSimpleName().contains("Date")
                            ?builder.notEqual(path,getTime(value.toString(),path))
                            :builder.notEqual(path,value);
                    break;
                case "in":
                    CriteriaBuilder.In in = builder.in(path);
                    Object[] objects =(Object[])value;
                    for (int i = 0; i <objects.length ; i++) {
                        in.value(objects[i]);
                    }
                    predicate =in;
                    break;
                case "notIn":
                    CriteriaBuilder.In inn = builder.in(path);
                    Object[] objects1 =(Object[])value;
                    for (int i = 0; i <objects1.length ; i++) {
                        inn.value(objects1[i]);
                    }
                    predicate =builder.not(inn);
                    break;
                case "isNull":
                    predicate= builder.isNull(path);
                    break;
                case "isNotNull":
                    predicate= builder.isNotNull(path);
                    break;
                /*case "isMember":
                    Object[] members =(Object[])value;
                    predicate =builder.isMember(value, Arrays.asList(members))
                    break;
                case "isNotMember":
                    Object[] notMembers =(Object[])value;
                    predicate =builder.isNotMember(value, Arrays.asList(notMembers));
                    break;*/
            }
        }else {
           List<Expression> list = expression.getExpressions();
           List<Predicate> predicateList =new ArrayList<>();
           list.forEach(ele ->{
               predicateList.add(presentExPression( builder,  root,  ele));
           });
            predicate ="and".equalsIgnoreCase(expression.getJoinType())?
                        builder.and(predicateList.toArray(new Predicate[predicateList.size()])):
                        builder.or(predicateList.toArray(new Predicate[predicateList.size()]));
        }
        return predicate;
    }


    public   static  void groupBy(CriteriaQuery  query,Root root,String[] fieldNames){
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
     * @param timeStr 只能时'yyyy-MM-dd'或者'yyyy-MM-dd HH:mm:ss'
     *
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

    /**
     *获取排序
     * */
    public static Sort  getToSort(JSONArray sort){
        List<Sort.Order> list =new ArrayList<>();
        if (!CollectionUtils.isEmpty(sort)){
            for (int i = 0; i < sort.size(); i++) {
                JSONObject jsonObject =sort.getJSONObject(i);
                Sort.Direction direction=  "desc".equalsIgnoreCase(jsonObject.getString("sortType"))
                        ?Sort.Direction.DESC
                        :Sort.Direction.ASC;
                list.add(new Sort.Order(direction,jsonObject.getString("fieldName")));
            }
        }
        return  new Sort(list);
    }

    public static <T> Page<T> readPage(WhereContext whereContext,Class<T> clazz, EntityManager entityManager) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        Expression[] expressions = null;
        if (!ObjectUtils.isEmpty(whereContext.getExpressions())){
            expressions = whereContext.getExpressions().toArray(new Expression[whereContext.getExpressions().size()]);
        }
        Predicate predicate = PresentWhereContextUtil.expressionToPredicate(builder,root,expressions);

        CriteriaQuery<T> criteriaQuery = PresentWhereContextUtil.getCriteriaQueryByPredicate(clazz,entityManager,predicate);
        CriteriaQuery<Long> countQuery =PresentWhereContextUtil.getContQueryByPredicate(clazz,entityManager,predicate,false);

        if (!ObjectUtils.isEmpty(whereContext.getGroupby())) {
            String[] group = whereContext.getGroupby().toArray(new String[whereContext.getGroupby().size()]);
            PresentWhereContextUtil.groupBy(criteriaQuery, root, group);
            PresentWhereContextUtil.groupBy(countQuery, root, group);
        }
        Sort sort = PresentWhereContextUtil.getToSort(whereContext.getSort());
        if (!ObjectUtils.isEmpty(sort)){
            criteriaQuery.orderBy(QueryUtils.toOrders(sort, root, builder));
        }
        TypedQuery<T> typedQuery =entityManager.createQuery(criteriaQuery);
        TypedQuery<Long> typedCountQuery =entityManager.createQuery(countQuery);

        Long total = executeCountQuery(typedCountQuery);
        PageRequest pageable =new PageRequest(whereContext.getPageNum() - 1, whereContext.getPageSize(),sort);
        typedCountQuery.setFirstResult((int) pageable.getOffset());
        typedCountQuery.setMaxResults(pageable.getPageSize());
        return new PageImpl(typedQuery.getResultList(), pageable, total);
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

}
