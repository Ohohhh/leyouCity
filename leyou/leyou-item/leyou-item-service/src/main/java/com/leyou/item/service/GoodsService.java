package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author lennon
 */
public interface GoodsService {
    /**
     * 查找分页显示
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows);

    /**
     * 新增商品
     * @param spuBo
     */
    void saveGoods(SpuBo spuBo);

    /**
     * 根据spuId 查询spuDetail
     * @param spuId
     * @return
     */
    SpuDetail querySpuDetailSpuId(Long spuId);

    /**
     * 根据spuId 查询Sku
     * @param spuId
     * @return
     */
    List<Sku> querySkuBySpuId(Long spuId);

    /**
     * 更新SpuBo表
     * @param spuBo
     */
    void updateGoods(SpuBo spuBo);
    /**
     * 根据spuId 查找spu
     * @param id
     * @return
     */
    @GetMapping("{id}")
    Spu querySpuById(@PathVariable("id") Long id);

    /**
     * 根据skuId  查找sku
     * @param skuId
     * @return
     */
    Sku querySkuBySkuId(Long skuId);

}
