package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @author lennon
 */
@Table(name = "tb_spec_param")
@Data
public class SpecParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    /**
     * 该注解的作用： 1. 区别于MySQL中的关键字  2. 属性与表字段名字不一样时可用
     */
    @Column(name = "`numeric`")
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;

}
