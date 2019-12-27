package com.leyou.item.bo;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import java.util.List;

/**
 * @author lennon
 */
@Data
public class SpuBo extends Spu {
    @Transient
    private String cname;
    @Transient
    private String bname;
    @Transient
    private SpuDetail spuDetail;
    @Transient
    private List<Sku> skus;


}
