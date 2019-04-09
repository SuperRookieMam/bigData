package com.yhl.orm.componet.util;

import com.yhl.orm.componet.constant.ConnectCondition;
import com.yhl.orm.componet.constant.WhereContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PresentWhereContextUtil {

    /**
     * 获取构建好wherecondition条件的TypedQuery
     * */
    public static <T> TypedQuery<T> getTypedQuery(Class<T> tClass, CriteriaBuilder  builder, WhereContext whereContext){
        return  null;
    }


    /**
     * 获取映射的路径和对应的条件对象
     * */
    public  static<T> Map<Path,ConnectCondition> getPathMap(Root<T> root,WhereContext whereContext){
        Map<Path,ConnectCondition> map =new HashMap<>();
        Set<String> set =whereContext.keySet();
        set.forEach(key->{
            Path path =  getPath(root,key);
            map.put(path,(ConnectCondition)whereContext.get(key));
        });
        return map;
    }
    /**
     *取得引用对象的值比如说field.field2.field3 的值
     * */
    public  static <T>  Path getPath(Root<T> root, String key){
        Map<Path,Object> map =null;
        key=StringUtils.isEmpty(key)?null:key;
        Assert.notNull(key, "key不能为空字符串");
        Path path=null;
        if (key.contains(".")) {
            String[] keys = StringUtils.split(key, ".");
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
                    path = i==1?mapJoin.get(keys[i]):path.get(keys[i]);
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
        return path;
    }




}
