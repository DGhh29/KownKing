package com.dg.schoolhelp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dg.schoolhelp.entity.Articles;
import com.dg.schoolhelp.mapper.ArticlesMapper;
import com.dg.schoolhelp.service.IArticlesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DG
 * @since 2024-11-13
 */
@Service
public class ArticlesServiceImpl extends ServiceImpl<ArticlesMapper, Articles> implements IArticlesService {

    @Autowired
    ArticlesMapper articlesMapper;

    @Override
    public IPage<Articles> selectPageVo(int page, int size) {
        Page<Articles> pageVo = new Page<>(page, size);
        return articlesMapper.selectPageVo(pageVo);
    }
}
