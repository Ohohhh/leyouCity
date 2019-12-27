package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @author lennon
 */
@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryService;

    /**
     * 根据父节点id查找字节点
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam(value = "pid", defaultValue = "0") Long pid){
            // 400 参数不合法
            if (pid == null || pid < 0){
                // return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                return ResponseEntity.badRequest().build();
            }
            List<Category> categories= this.categoryService.queryCategoryByPid(pid);
            // 404 资源服务器未找到
            if (CollectionUtils.isEmpty(categories)){
                // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                return ResponseEntity.notFound().build();
            }
            // 200  成功
            return ResponseEntity.ok(categories);
        }
     @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid){
        List<Category> list =  this.categoryService.queryCategoryByBid(bid);
        if (list == null || list.size()<1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
     }
     @GetMapping("name")
     public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids") List<Long> ids){
         List<String> names = this.categoryService.queryCategoryByIds(ids);
         if (CollectionUtils.isEmpty(names)){
             return ResponseEntity.notFound().build();
         }
         return ResponseEntity.ok(names);
     }
    }
