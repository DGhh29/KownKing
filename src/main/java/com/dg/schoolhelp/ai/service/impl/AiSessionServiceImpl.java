package com.dg.schoolhelp.ai.service.impl;

import com.dg.schoolhelp.ai.entity.AiSession;
import com.dg.schoolhelp.ai.mapper.AiSessionMapper;
import com.dg.schoolhelp.ai.service.IAiSessionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DG
 * @since 2024-12-16
 */
@Service
public class AiSessionServiceImpl extends ServiceImpl<AiSessionMapper, AiSession> implements IAiSessionService {

    @Autowired
    private AiSessionMapper aiSessionMapper;

    @Override
    public List<AiSession> getSessionsByUserId(int userId) {
        return aiSessionMapper.getSessionsByCreatorId(userId);
    }

    @Override
    public String insertSession(String textContent,String userId) {
        AiSession aiSession = new AiSession();
        aiSession.setCreatorId(userId);
        aiSession.setName(textContent);
        aiSession.setId(UUID.randomUUID().toString());
        aiSession.setCreatedTime(LocalDateTime.now());
        save(aiSession);
        return aiSession.getId();
    }

    @Override
    public String getSessionNameById(String sessionId) {
        return aiSessionMapper.getSessionNameById(sessionId);
    }
}
