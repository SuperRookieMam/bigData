package com.yhl.system.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "menu_function")
public class MenuFunction extends BaseEntity {
    private static final long serialVersionUID = -53761947833746331L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cname_")
    private String cname;

    @Column(name = "ename_")
    private String ename;

    @Column(name = "url_")
    private String url;

    @Column(name = "sort_")
    private Integer sort;

    @Column(name = "function_number")
    private Integer functionNumber;

    @Column(name = "pid_")
    private Integer pid;

    @Column(name = "is_menu_")
    private Integer isMenu;

    @Column(name = "is_show")
    private Integer isShow;

    @Column(name = "is_flow")
    private Integer isFlow;

    @Column(name = "company_id")
    private Long companyId;

}

