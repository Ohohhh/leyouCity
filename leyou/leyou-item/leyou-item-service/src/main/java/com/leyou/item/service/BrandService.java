package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;

/**
 * @author lennon
 */
public interface BrandService {
    /**
     * 根据查询条件分页并排序查询信息
     *
     * @param key    搜索栏关键字
     * @param page   当前页页
     * @param rows   当前页行数
     * @param sortBy 排序条件
     * @param desc   是否为降序
     * @return
     */
    PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);
    /**
     *
     * 新增品牌  如果不设置 rollbackFor = Exception.class，则当方法抛出检查型异常时，数据库操作不会回滚。
     * @param brand
     * @param cids
     */
    void saveBrand(Brand brand, List<Long> cids);

    /**
     * 查找品牌分类
     * @param id
     * @return
     */
    List<Brand> queryBrandById(Long id);

    /**
     * 根据id查找品牌名字
     * @param id
     * @return
     */
    Brand queryBrandNameById(Long id);
}
