package com.sky.ai.dto;

import lombok.Data;

/**
 * AI 问答入参
 */
@Data
public class AiChatDTO {

    /** 用户问题 */
    private String question;

    /**
     * 会话 id：今天先不用，Day10 做多轮对话时用它把历史串起来
     * 先留着字段，避免以后改接口签名
     */
    private Long sessionId;
}
