package com.dg.schoolhelp;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;

public class Generator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/schoolhelp?userUnicode=true&characterEncoding=utf-8", "root", "123456")
                .globalConfig(builder -> builder
                        .author("DG")
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                        .commentDate("yyyy-MM-dd")

                )
                .packageConfig(builder -> builder
                        .parent("com.dg.schoolhelp.ai")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.Impl")
                )
                .strategyConfig(builder -> builder
                        .addInclude("ai_session")
                        .entityBuilder()
                        .enableLombok()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
