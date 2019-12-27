package com.leyou.item.service;

import com.leyou.item.pojo.Category;

import java.util.List;

/**
 * @author lennon
 */
public interface CategoryService {
    /**
     * 根据父节点查找子节点
     * @param pid
     * @return
     */
    List<Category> queryCategoryByPid(Long pid);
    /**
     * 根据品牌id查找商品分类
     * @param bid
     * @return
     */
    List<Category> queryCategoryByBid(Long bid);

    /**
     * 通过ids 查找分类
     * @param ids
     * @return
     */
    List<String> queryCategoryByIds(List<Long> ids);
}
