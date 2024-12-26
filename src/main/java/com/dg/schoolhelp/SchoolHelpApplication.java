package com.dg.schoolhelp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dg.schoolhelp.ai.mapper")
public class SchoolHelpApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolHelpApplication.class, args);
    }
}
