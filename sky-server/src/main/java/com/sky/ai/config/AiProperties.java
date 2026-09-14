package com.sky.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 模块配置：读取 application-dev.yml 里 sky.ai.* 的配置
 *
 * 为什么要单独写配置类？
 * ① 配置集中管理，代码里不出现魔法字符串
 * ② 换模型/换厂商只改 yml，不动 Java 代码
 */
@Data
@Component
@ConfigurationProperties(prefix = "sky.ai")
public class AiProperties {

    /** 百炼 API Key：从环境变量 DASHSCOPE_API_KEY 读取，绝不写死在代码里 */
    private String apiKey;

    /** OpenAI 兼容模式地址，如 https://dashscope.aliyuncs.com/compatible-mode/v1 */
    private String baseUrl;

    /** 聊天模型，如 qwen-plus */
    private String chatModel;

    /** 随机性：客服场景建议 0~0.3（越低越稳定、越少胡说） */
    private Double temperature = 0.3;

    /** 单次回答最大 token 数（控制成本） */
    private Integer maxTokens = 800;

    /** 连接超时（毫秒） */
    private Integer connectTimeout = 10000;

    /** 读取超时（毫秒）：模型可能比较慢，给足 60 秒 */
    private Integer readTimeout = 60000;
}
