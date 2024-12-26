package com.dg.schoolhelp.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 *
 * @author DG
 * @since 2024-12-14
 */
@Data
@TableName("ai_message")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    private String creatorId;

    /**
     * 消息类型(用户/助手/系统)
     */
    private String type;

    /**
     * 消息内容
     */
    private String textContent;

    /**
     * 媒体内容如图片链接、语音链接
     */
    private String medias;

    /**
     * 会话id
     */
    private String aiSessionId;

    // 构造函数，在创建对象时自动填充时间
    public AiMessage() {
        this.createdTime = LocalDateTime.now();
    }
}