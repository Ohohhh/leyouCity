package com.leyou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 解决跨域问题 Cors
 * @author lennon
 */
@Configuration
public class GateWayConfiguration {

    @Bean
    public CorsFilter corsFilter(){
        // 配置CORS对象 添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域 不要写*  否则cookie无法使用了
        config.addAllowedOrigin("http://manage.leyou.com");
        config.addAllowedOrigin("http://www.leyou.com");
        // 是否发送Cookie信息
        config.setAllowCredentials(true);
        // 配置允许携带的头信息
        config.addAllowedHeader("*");
        // 配置允许的请求方式
        config.addAllowedMethod("*");
        // 配置源对象 添加映射路径
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",config);
        // 返回新的CorsFilter 参数为 配置源对象
        return new CorsFilter(configurationSource);
    }
}
