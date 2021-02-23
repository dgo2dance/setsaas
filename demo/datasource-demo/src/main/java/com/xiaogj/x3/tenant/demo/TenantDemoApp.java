package com.xiaogj.x3.tenant.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @ClassName: TenantDemoApp
 * @Description:
 * @author: xiaolinlin
 * @date: 2020/9/9 21:46
 **/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = "com.xiaogj.x3.tenant.demo.mapper")
public class TenantDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(TenantDemoApp.class, args);
    }
}
