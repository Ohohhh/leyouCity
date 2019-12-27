package com.leyou.item.API;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpeApi {
    /**
     * 根据条件查找参数
     *
     * @param gid
     * @return
     */
    @GetMapping("params")
    List<SpecParam> queryParamByGid(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching);
    /**
     * 根据cid查找分类参数组
     * @param cid
     * @return
     */
    @GetMapping("groups/param/{cid}")
    List<SpecGroup> queryGroupWithParam( @PathVariable Long cid);
    /**
     * 根据分类id查询分组
     *
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    List<SpecGroup> queryGroupsByCid(@PathVariable("cid") Long cid);
}
