package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

/**
 * @author lennon
 */
public interface SpecService {
    /**
     * 根据cid查找参数组
     * @param cid
     * @return
     */
    List<SpecGroup> queryGroupsByCid(Long cid);
    /**
     * 根据条件查找规格参数组
     * @param gid
     * @return
     */
    List<SpecParam> queryParamByGid(Long gid,Long cid,Boolean generic,Boolean searching);
    /**
     * 修改商品组
     * @param specGroup
     */
    void modifySpecGroup(SpecGroup specGroup);
    /**
     * 根据id删除商品组
     * @param id
     */
    void deleteSpeGroup(Long id);

    /**
     * 保存商品组
     * @param specGroup
     */
    void saveSpeGroup(SpecGroup specGroup);
    /**
     *
     * 修改商品参数
     * @return
     */
    void modifySpecParam(SpecParam specParam);
    /**
     * 删除商品参数
     * @param id
     * @return
     */
    void deleteSpeParam(Long id);
    /**
     * 新增商品参数
     * @param specParam
     * @return
     */
    void saveSpeParam(SpecParam specParam);

    /**
     * 根据cid查找分类参数组
     * @param cid
     * @return
     */
    List<SpecGroup> queryGroupWithParam(Long cid);
}
