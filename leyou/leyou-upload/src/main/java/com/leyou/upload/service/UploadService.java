package com.leyou.upload.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author lennon
 */
@Service
public class UploadService {
    /**
     * 定义上传文件类型白名单
     */
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif");
    /**
     * 定义日志工具
     */
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    public String uploadImage(MultipartFile file) {
        String filename = file.getOriginalFilename();
        // 校验文件上传类型 不符合直接返回null
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)) {
            logger.info("文件类型不符合：{}", filename);
            return null;
        }
        try {
            // 校验文件内容是否为空
            BufferedImage inputStream = ImageIO.read(file.getInputStream());
            if (inputStream == null) {
                logger.info("文件内容不合法：{}", filename);
                return null;
            }
            // 保存到服务器
            file.transferTo(new File("D:\\study\\java\\projects\\leyou-image\\" + filename));
            // 回显url
            return "http://image.leyou.com/" + filename;
        } catch (IOException e) {
            logger.info("服务器内部错误：{}", filename);
            e.printStackTrace();
        }
        return null;
    }
}
