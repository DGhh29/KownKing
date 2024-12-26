package com.dg.schoolhelp.ai.service;

import com.dg.schoolhelp.ai.entity.AiMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *  服务类
 *
 * @author DG
 * @since 2024-12-14
 */
public interface IAiMessageService extends IService<AiMessage> {

    List<AiMessage> findBySessionId(String sessionId);

    void saveMessage(AiMessage message);
}
