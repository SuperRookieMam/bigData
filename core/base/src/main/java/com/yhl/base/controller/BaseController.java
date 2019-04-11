package com.yhl.base.controller;


import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.entity.BaseEntity;
import com.yhl.base.service.BaseService;
import com.yhl.orm.componet.constant.FieldContext;
import com.yhl.orm.componet.constant.WhereContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

public class BaseController<T extends BaseEntity,ID extends Serializable>{

    @Autowired
    BaseService<T,ID> baseService;

    @GetMapping("{id}")
    @ResponseBody
    @ApiOperation(value="根据Id查询实体", notes="findById")
    public ResultDto findById(@PathVariable("id") ID id){
        return baseService.findById(id);
    }
    /**
     * 根据参数自定义查询
     * */
    @GetMapping(params = {"list"})
    @ResponseBody
    @ApiOperation(value="根据条件查询列表", notes="findByWhereContext")
    public ResultDto findByWhereContext(@RequestBody WhereContext whereContext){
        return  baseService.findByWhereContext(whereContext);
    }

    /**
     * 分页查询
     * */
    @GetMapping(params ={"page"})
    @ResponseBody
    @ApiOperation(value="根据条件分页查询", notes="findPageByWhereContext")
    public ResultDto findPageByWhereContext(@RequestBody WhereContext whereContext){
        return  baseService.findPageByWhereContext(whereContext);
    }


    @PostMapping
    @ResponseBody
    @ApiOperation(value="根据实体插入", notes="insertByEntity")
    public<T> ResultDto insertByEntity(@RequestBody T entity){
        return  baseService.insertByEntity(entity);
    }

   /**
     * 批量插入
     * */
    @PostMapping("list")
    @ResponseBody
    @ApiOperation(value="根据list插入", notes="insertByList")
    public <T> ResultDto insertByList(@RequestBody List<T> entitys){
        return  baseService.insertByList(entitys);
    }


    /**
     * 根据实体跟新
     * */
    @PutMapping
    @ResponseBody
    @ApiOperation(value="完全根据传入实体字段跟新", notes="updateByEntity")
    public<T> ResultDto updateByEntity(@RequestBody T entity){
        return  baseService.updateByEntity(entity);
    }

    /**
     * 根据实体跟新
     * */
    @PutMapping("list")
    @ResponseBody
    @ApiOperation(value="根据传入的实体数组跟新", notes="updateByEntitys")
    public<T> ResultDto updateByEntitys(@RequestBody T[] entitys){
        return  baseService.updateByEntitys(entitys);
    }
    /**
     * 根据实体跟新
     * */
    @PutMapping("field")
    @ResponseBody
    @ApiOperation(value="根据传入的跟新字段条件，跟新单个实体", notes="updateByFieldContext")
    public<T> ResultDto updateByFieldContext(@RequestBody WhereContext whereContext){
        return  baseService.updateByFieldContext(whereContext.getFieldContext());
    }

    /**
     * 根据实体跟新
     * */
    @PutMapping("fields")
    @ResponseBody
    @ApiOperation(value="根据传入的跟新字段条件,数组跟新", notes="updateByFieldContexts")
    public<T> ResultDto updateByFieldContexts(@RequestBody FieldContext[] fieldContexts){
        return  baseService.updateByFieldContexts(fieldContexts,1000);
    }
    /**
     * 根据实体跟新
     * */
    @PutMapping("free")
    @ResponseBody
    @ApiOperation(value="根据传入的field条件,和where条件批量跟新", notes="updateByFree")
    public<T> ResultDto updateByFieldContextAndWhereContext(@RequestBody WhereContext whereContext){
        return  baseService.updateByFieldContextAndWhereContext(whereContext,1000);
    }

    @DeleteMapping("{id}")
    @ResponseBody
    @ApiOperation(value="根据Id删除实体", notes="deleteById")
    public<T> ResultDto deleteById(@PathVariable  ID id){
      return   baseService.deleteById(id);
    }
    @PostMapping("where")
    @ResponseBody
    @ApiOperation(value="根据Where条件批量删除实体", notes="deleteByWhereContext")
    public ResultDto deleteByWhereContext(@RequestParam WhereContext whereContext){
        return  baseService.deleteByWhereContext(whereContext);
    }

}
