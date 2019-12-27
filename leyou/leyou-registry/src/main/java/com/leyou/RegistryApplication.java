package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 启动类
 * @author lennon
 */
@SpringBootApplication
@EnableEurekaServer  //加上此注解表示将此工程启动后为注册中心
public class RegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistryApplication.class,args);
    }
}
