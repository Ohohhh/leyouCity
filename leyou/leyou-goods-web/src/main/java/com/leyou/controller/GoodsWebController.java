package com.leyou.controller;

import com.leyou.service.GoodsHtmlService;
import com.leyou.service.GoodsWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("item")
public class GoodsWebController {

    @Autowired
    private GoodsWebService goodsWebService;
    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @GetMapping("{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model){
        Map<String, Object> map = goodsWebService.loadData(id);
        model.addAllAttributes(map);
        // 页面静态化处理
        goodsHtmlService.creatHtml(id);
        return "item";
    }
}
