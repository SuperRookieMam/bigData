package com.yhl.base.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createTime", "modifyTime","createUser","modifyUser"})
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1127874074039848204L;

    @CreatedDate
    @Column(name = "create_time",updatable = false)
    private LocalDateTime createTime ;

    @LastModifiedDate
    @Column(name = "modify_time",updatable = false)
    private LocalDateTime modifyTime ;

    @CreatedBy
    @Column(name = "create_user",updatable = false,insertable = false)
    private String createUser;

    @LastModifiedBy
    @Column(name = "create_user" ,updatable = false)
    private String modifyUser;
}
