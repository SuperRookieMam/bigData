package com.yhl.base.componet.dto;

import com.yhl.base.componet.enumpackage.ResultEnum;

public class ResultDto {

    private  int code;

    private  String msg;

    private  Object data;

    public static ResultDto success(Object data){
        ResultEnum resultEnum = ResultEnum.valueOf(0);
        ResultDto resultDto =new ResultDto();
        resultDto.setCode(resultEnum.getCode());
        resultDto.setMsg(resultEnum.getMsg());
        resultDto.setData(data);
        return resultDto;
    }

    public static  ResultDto error(Object data){
        ResultEnum resultEnum = ResultEnum.valueOf(1);
        ResultDto resultDto =new ResultDto();
        resultDto.setCode(resultEnum.getCode());
        resultDto.setMsg(resultEnum.getMsg());
        resultDto.setData(data);
        return resultDto;
    }

    public static  ResultDto error(Exception e){
        ResultEnum resultEnum = ResultEnum.valueOf(1);
        ResultDto resultDto =new ResultDto();
        resultDto.setCode(resultEnum.getCode());
        resultDto.setMsg(e.getMessage());
        resultDto.setData(null);
        return resultDto;
    }



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
