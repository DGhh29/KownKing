package com.dg.schoolhelp.ai.mapper;

import com.dg.schoolhelp.ai.entity.AiSession;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author DG
 * @since 2024-12-16
 */

@Mapper
public interface AiSessionMapper extends BaseMapper<AiSession> {

    @Select("SELECT * FROM ai_session WHERE creator_id = #{id} ORDER BY created_time DESC")
    List<AiSession> getSessionsByCreatorId(int id);

    @Select("SELECT name FROM ai_session WHERE id = #{sessionId}")
    String getSessionNameById(String sessionId);
}
