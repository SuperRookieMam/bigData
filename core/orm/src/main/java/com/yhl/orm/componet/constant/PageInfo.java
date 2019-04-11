package com.yhl.orm.componet.constant;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@Data
public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = 2587523436890037070L;
    //当前页码
    private int pageNum;
    // 页面大小
    private int pageSize;
    // 开始行
    private int startRow;
    // 结束行
    private int endRow;
    //总行数
    private long total;
    // 总页码数
    private int pages;
    //根据排序字段
    private String orderBy;
    //实体队列
    private List<T> list;
}
