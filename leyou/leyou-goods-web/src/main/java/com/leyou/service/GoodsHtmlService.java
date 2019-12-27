package com.leyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 页面静态化  提高响应速度
 */
@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine engine;

    @Autowired
    private GoodsWebService goodsWebService;

    /**
     * 新增静态页面
     * @param spuId
     */
    public void creatHtml(Long spuId){
        PrintWriter writer = null;
        try {
            // 获取页面数据
            Map<String, Object> map = goodsWebService.loadData(spuId);
            // 初始化上下文
            Context context = new Context();
            // 把数据放入上下文
            context.setVariables(map);
            // 初始化静态文件输出流
            File file = new File("D:\\ProgramFiles\\nginx-1.12.2\\html\\item\\"+ spuId+".html");
             writer = new PrintWriter(file);
            // 执行解析
            engine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null){
                writer.close();
            }
        }
    }

    /**
     * 删除静态页面
     * @param id
     */
    public void deleteHtml(Long id) {
        File file = new File("D:\\ProgramFiles\\nginx-1.12.2\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
