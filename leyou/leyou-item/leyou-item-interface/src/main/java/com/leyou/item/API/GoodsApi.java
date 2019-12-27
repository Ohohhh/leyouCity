package com.leyou.item.API;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
public interface GoodsApi {
    /**
     * 根据spuId查询spu详情页
     */
    @GetMapping("spu/detail/{spuId}")
    SpuDetail querySpuDetailBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 分页查询商品
     */
    @GetMapping("spu/page")
    PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows);
    /**
     * 根据spu的id查询sku
     */
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("spuId") Long spuId);

    /**
     * 根据spuId 查找spu
     * @param id
     * @return
     */
    @GetMapping("{id}")
     Spu querySpuById(@PathVariable("id") Long id);
    @GetMapping("id/{skuId}")
    Sku querySkuBySkuId(@PathVariable("skuId") Long skuId);
}
