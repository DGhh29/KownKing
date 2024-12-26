package com.dg.schoolhelp.ai.service;

import com.dg.schoolhelp.ai.entity.AiSession;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DG
 * @since 2024-12-16
 */
public interface IAiSessionService extends IService<AiSession> {

    List<AiSession> getSessionsByUserId(int userId);

    String insertSession(String textContent,String userId);

    String getSessionNameById(String sessionId);
}
