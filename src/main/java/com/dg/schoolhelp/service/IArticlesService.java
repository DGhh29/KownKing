package com.dg.schoolhelp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dg.schoolhelp.entity.Articles;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DG
 * @since 2024-11-13
 */
public interface IArticlesService extends IService<Articles> {
    IPage<Articles> selectPageVo(int page, int size);
}
