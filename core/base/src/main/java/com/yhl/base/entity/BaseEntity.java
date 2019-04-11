package com.yhl.base.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1127874074039848204L;

    @Column(name = "create_time",updatable = false)
    private String createTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    @Column(name = "modify_time")
    private String modifyTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


    @Column(name = "create_user",updatable = false)
    private String createUser;
}
