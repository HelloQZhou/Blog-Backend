package com.qzhou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
@MapperScan("com.qzhou.mapper")
public class QzhouAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(QzhouAdminApplication.class);

    }
}
