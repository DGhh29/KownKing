package com.dg.schoolhelp.controller;

import com.dg.schoolhelp.service.IArticlesService;
import com.dg.schoolhelp.utils.RestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  资料接口
 *
 * @author DG
 * @since 2024-11-13
 */
@RestController
@RequestMapping("/api/articles")
public class ArticlesController {

    @Autowired
    IArticlesService articlesService;

    @GetMapping("/list")
    public RestBean list(int page) {
        return RestBean.success(articlesService.selectPageVo(page, 10));
    }
}
