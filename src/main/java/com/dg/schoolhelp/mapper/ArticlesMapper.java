package com.dg.schoolhelp.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dg.schoolhelp.entity.Articles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DG
 * @since 2024-11-13
 */
@Mapper
public interface ArticlesMapper extends BaseMapper<Articles> {
    @Select("SELECT id,title,create_time,description,type FROM articles")
    IPage<Articles> selectPageVo(IPage<Articles> page);
}
