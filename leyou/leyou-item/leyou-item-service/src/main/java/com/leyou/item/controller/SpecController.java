package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.impl.SpecServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lennon
 */
@RestController
@RequestMapping("spec")
public class SpecController {

    @Autowired
    private SpecServiceImpl specService;

    /**
     * 根据分类id查询分组
     *
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = this.specService.queryGroupsByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }
    @GetMapping("groups/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupWithParam(@PathVariable("cid") Long cid){
        List<SpecGroup> specGroupList = this.specService.queryGroupWithParam(cid);
        if (CollectionUtils.isEmpty(specGroupList)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroupList);
    }

    /**
     * 根据条件查找参数
     *
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamByGid(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching)
            {
        List<SpecParam> params = this.specService.queryParamByGid(gid,cid,generic,searching);
        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    /**
     * 修改商品组
     * @param specGroup
     * @return
     */
    @PutMapping("group")
    public ResponseEntity modifySpecGroup(@RequestBody SpecGroup specGroup) {
        this.specService.modifySpecGroup(specGroup);
        return new ResponseEntity(HttpStatus.OK);
    }
    /**
     * 删除商品组
     * @param id
     * @return
     */
    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteSpeGroup(@PathVariable("id") Long id){
        specService.deleteSpeGroup(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    /**
     * 新增商品组
     * @param specGroup
     * @return
     */
    @PostMapping("group")
    public ResponseEntity saveSpeGroup(@RequestBody SpecGroup specGroup){
        specService.saveSpeGroup(specGroup);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 修改商品参数
     * @return
     */
    @PutMapping("param")
    public ResponseEntity modifySpeParam(@RequestBody SpecParam specParam){
        specService.modifySpecParam(specParam);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 删除商品参数
     * @param id
     * @return
     */
    @DeleteMapping("param/{id}")
    public ResponseEntity deleteSpeParam(@PathVariable("id") Long id ){
        specService.deleteSpeParam(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 新增商品参数
     * @param specParam
     * @return
     */
    @PostMapping("param")
    public ResponseEntity saveSpeParam(@RequestBody SpecParam specParam){
        specService.saveSpeParam(specParam);
        return new  ResponseEntity(HttpStatus.OK);
    }
}
