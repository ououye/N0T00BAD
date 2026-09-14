package com.sky.ai.exception;

import com.sky.exception.BaseException;

/**
 * AI 模块业务异常：模型调用失败、参数不合法等
 * 继承项目统一的 BaseException，由 GlobalExceptionHandler 自动转成 Result.error(msg)
 */
public class AiServiceException extends BaseException {

    public AiServiceException(String message) {
        super(message);
    }
}