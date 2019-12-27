package com.leyou.item.controller;

import com.leyou.item.pojo.Brand;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.service.impl.BrandServiceImpl;
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
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandServiceImpl brandService;

    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
                @RequestParam(value = "key",required = false)String key,
                @RequestParam(value = "page",defaultValue = "1")Integer page,
                @RequestParam(value = "rows",defaultValue = "5")Integer rows,
                @RequestParam(value = "sortBy",required = false)String sortBy,
                @RequestParam(value = "desc",required = false)Boolean desc
    ){
            PageResult<Brand> result = this.brandService.queryBrandByPage(key,page,rows,sortBy,desc);
            if (CollectionUtils.isEmpty(result.getItems())){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> saveBrand(@RequestBody Brand brand, @RequestParam("cids") List<Long> cids){
        this.brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据id查找品牌分类
     * @param id
     * @return
     */
    @GetMapping("cid/{id}")
    public ResponseEntity<List<Brand>> queryBrandById(@PathVariable("id") Long id){
        List<Brand> list =  brandService.queryBrandById(id);
        if (CollectionUtils.isEmpty(list)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandNameById(@PathVariable("id") Long id){
        Brand brand = brandService.queryBrandNameById(id);
        if (brand == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }
}
