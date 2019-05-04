package com.yhl.create.componet.annotation;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Description {

    //列表头名字
    String label() default "";

    //update跟新的时列对应的类型. select checkbox  time input
    String prop() default "";
    // 是否参与表头搜素
    boolean search() default  false;

    boolean isColumn() default  false;
    String searchType() default  "";
}
