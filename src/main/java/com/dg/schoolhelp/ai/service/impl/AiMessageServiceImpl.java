package com.dg.schoolhelp.ai.service.impl;

import com.dg.schoolhelp.ai.entity.AiMessage;
import com.dg.schoolhelp.ai.mapper.AiMessageMapper;
import com.dg.schoolhelp.ai.service.IAiMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 *  服务实现类
 *
 * @author DG
 * @since 2024-12-14
 */
@Service
public class AiMessageServiceImpl extends ServiceImpl<AiMessageMapper, AiMessage> implements IAiMessageService {

    @Autowired
    private AiMessageMapper aiMessageMapper;

    @Override
    public List<AiMessage> findBySessionId(String sessionId) {
        return aiMessageMapper.findBySessionId(sessionId);
    }

    @Async
    @Override
    public void saveMessage(AiMessage message) {
        AiMessage aiMessage = new AiMessage();
        aiMessage = message;
        save(aiMessage);
    }
}
