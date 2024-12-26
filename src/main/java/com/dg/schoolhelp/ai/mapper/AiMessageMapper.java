package com.dg.schoolhelp.ai.mapper;

import com.dg.schoolhelp.ai.entity.AiMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *  AiMessageMapper 接口
 *
 * @author DG
 * @since 2024-12-14
 */

@Mapper
public interface AiMessageMapper extends BaseMapper<AiMessage> {

    @Select("SELECT created_time,type,text_content FROM ai_message WHERE ai_session_id = #{sessionId} ORDER BY created_time ASC")
    List<AiMessage> findBySessionId(String sessionId);

    @Select("SELECT type,text_content FROM ai_message WHERE ai_session_id = #{sessionId} ORDER BY created_time ASC LIMIT #{lastN}")
    List<AiMessage> findTenTextBySessionId(String sessionId, int lastN);
}
