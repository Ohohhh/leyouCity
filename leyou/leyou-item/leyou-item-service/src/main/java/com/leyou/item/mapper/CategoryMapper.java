package com.leyou.item.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;
import com.leyou.item.pojo.Category;

import java.util.List;

/**
 * @author lennon
 */
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category,Long> {
    /**
     * 根据品牌id查找商品分类
     * @param bid
     * @return
     */
    @Select("SELECT * FROM tb_category WHERE id IN (SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> queryByBrandId(Long bid);
}