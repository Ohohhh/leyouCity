package com.leyou.item.service.impl;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lennon
 */
@Service
public class SpecServiceImpl implements SpecService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;


    @Override
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specGroupMapper.select(specGroup);
    }

    @Override
    public List<SpecParam> queryParamByGid(Long gid,Long cid,Boolean generic,Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return this.specParamMapper.select(specParam);
    }


    @Override
    public void modifySpecGroup(SpecGroup specGroup) {
        System.out.println(specGroup.getName());
        /**
         * updateByPrimaryKeySelective更新新的model中不为空的字段。
         *         如果你只想更新某一字段，可以用这个方法。
         *
         *         updateByPrimaryKey对你注入的字段全部更新，
         *         如果为字段不更新，数据库的值就为null。
         */
        specGroupMapper.updateByPrimaryKeySelective(specGroup);
        specGroupMapper.updateByPrimaryKey(specGroup);
    }


    @Override
    public void deleteSpeGroup(Long id) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setId(id);
        this.specGroupMapper.delete(specGroup);
        specGroupMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void saveSpeGroup(SpecGroup specGroup) {
        specGroupMapper.insertSelective(specGroup);
    }

    @Override
    public void modifySpecParam(SpecParam specParam) {
        specParamMapper.updateByPrimaryKeySelective(specParam);
    }

    @Override
    public void deleteSpeParam(Long id) {
        SpecParam specParam = new SpecParam();
        specParam.setId(id);
        specParamMapper.delete(specParam);
        specParamMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void saveSpeParam(SpecParam specParam) {
        specParamMapper.insertSelective(specParam);
    }

    @Override
    public List<SpecGroup> queryGroupWithParam(Long cid) {
        // 先查询组
        List<SpecGroup> groups = this.queryGroupsByCid(cid);
        // 再根据组id查找参数
        groups.forEach(group -> {
            List<SpecParam> params = this.queryParamByGid(group.getId(), null, null, null);
            group.setParams(params);
        });
        return groups;
    }
}
