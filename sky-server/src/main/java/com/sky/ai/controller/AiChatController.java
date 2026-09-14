package com.sky.ai.controller;

import com.sky.ai.client.LlmClient;
import com.sky.ai.dto.AiChatDTO;
import com.sky.ai.exception.AiServiceException;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C 端 AI 智能客服接口
 * 注意：/user/** 已被 JWT 拦截器保护，调用时必须带 authentication 请求头（用户 token）
 */
@RestController
@RequestMapping("/user/ai")
@Api(tags = "C端-AI智能客服接口")
@Slf4j
public class AiChatController {

    @Autowired
    private LlmClient llmClient;

    @PostMapping("/chat")
    @ApiOperation("AI 智能客服问答")
    public Result<String> chat(@RequestBody AiChatDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getQuestion())) {
            throw new AiServiceException("问题不能为空");
        }
        // 日志里不要打印完整问题内容（可能含隐私），只打长度/会话id
        log.info("AI 问答请求：sessionId={}, 问题长度={}", dto.getSessionId(), dto.getQuestion().length());

        String systemPrompt = "你是外卖平台的智能客服，回答简洁、友好。"
                + "只回答你知道的信息，不确定的不要编造，可以引导用户联系门店。";

        String answer = llmClient.chat(systemPrompt, dto.getQuestion());
        return Result.success(answer);
    }
}
