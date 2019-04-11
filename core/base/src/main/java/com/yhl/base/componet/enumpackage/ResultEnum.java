package com.yhl.base.componet.enumpackage;

public enum ResultEnum {
    SUCCESS(0, "success"),
    FAILURE(1, "failure");
    private int code;
    private String msg;


    ResultEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public static ResultEnum valueOf(int code) {
        ResultEnum[] resultEnums = values();
        for(int i = 0; i < resultEnums.length; ++i) {
            ResultEnum resultEnum = resultEnums[i];
            if(resultEnum.code == code) {
                return resultEnum;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + code + "]");
    }

}
