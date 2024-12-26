package com.dg.schoolhelp.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author DG
 * @since 2024-12-16
 */
@Data
@TableName("ai_session")
public class AiSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    private String creatorId;

    /**
     * 会话名称
     */
    private String name;
}
