package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author lennon
 */
public interface BrandMapper extends Mapper<Brand> {
    /**
     * 新增品牌  #{} 会替换为？然后调用Preparedstatement预编译处理  ${} 直接值替换
     * 接受多个参数时用@Param 一个默认忽略
     * @param cid
     * @param id
     */
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid}, #{id} )")
    void insertGategoryAndBrand(@Param("cid") Long cid, @Param("id") Long id);

    /**
     * 根据商品分类id查找品牌
     * @param id
     * @return
     */
    @Select("SELECT * FROM tb_brand b INNER JOIN tb_category_brand cb ON b.id=cb.brand_id WHERE cb.category_id=#{cid}")
    List<Brand> queryBrandById(Long id);
}
