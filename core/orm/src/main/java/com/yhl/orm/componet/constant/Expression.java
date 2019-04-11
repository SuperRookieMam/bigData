package com.yhl.orm.componet.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class Expression implements Serializable {
    private static final long serialVersionUID = -3564092744884569360L;
    //是否单列表达式（没有子表达式）0,1
    private int   unique;
    //子表表达式之间的连接符号 and or
    private String joinType;
    // 子表达式
    private ArrayList<Expression> expressions =new ArrayList<>();
    // 字段名字，下面几个字段与上面 javatype 和express时是冲突的，会根据unique拼接条件
    private String key ;
    //field  in ,like
    private String type;
    //field 值
    private Object value;
    //字段名对应的jpa路径
    private Path path;

    public  Path getPath(Root root){
        if (!ObjectUtils.isEmpty(this.path)){
            return this.path;
        }
        Assert.notNull(this.key, "key值不能为空");
        if (this.key.contains(".")) {
            String[] keys = StringUtils.split(this.key, "\\.");
            assert keys != null;
            this.path = root.get(keys[0]);
            Class clazz = this.path.getJavaType();
            if (Set.class.isAssignableFrom(clazz)) {
                SetJoin setJoin = root.joinSet(keys[0]);
                for (int i = 1; i <keys.length ; i++) {
                    this.path = i==1?setJoin.get(keys[i]):path.get(keys[i]);
                }
            } else if (List.class.isAssignableFrom(clazz)) {
                ListJoin listJoin = root.joinList(keys[0]);
                for (int i = 1; i <keys.length ; i++) {
                    this.path = i==1?listJoin.get(keys[i]):this.path.get(keys[i]);
                }
            } else if (Map.class.isAssignableFrom(clazz)) {
                MapJoin mapJoin = root.joinMap(keys[0]);
                for (int i = 1; i <keys.length ; i++) {
                    this. path = i==1?mapJoin.get(keys[i]):path.get(keys[i]);
                }
            } else {
                //是many to one时
                for (int i = 1; i <keys.length ; i++) {
                    this.path = path.get(keys[i]);
                }
            }
        }else {//基本类型
            this.path = root.get(key);
        }
        return  this.path;
    }


}
