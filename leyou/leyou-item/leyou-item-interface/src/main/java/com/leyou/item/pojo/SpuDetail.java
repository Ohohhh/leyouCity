package com.leyou.item.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author lennon
 */
@Table(name="tb_spu_detail")
@Data
public class SpuDetail {
    @Id
    private Long spuId;// 对应的SPU的id
    private String description;// 商品描述
    private String generic_spec;// 商品的通用规格属性
    private String special_spec;// 商品特殊规格的名称及可选值模板 json
    private String packing_list;// 包装清单
    private String after_service;// 售后服务
}
