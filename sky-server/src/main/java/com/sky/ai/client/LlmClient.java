package com.sky.ai.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sky.ai.config.AiProperties;
import com.sky.ai.exception.AiServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大模型调用客户端（OpenAI 兼容协议）
 *
 * 今天先做"单轮问答"；Day3 加超时/重试/成本日志；Day10 加多轮历史。
 */
@Slf4j
@Component
public class LlmClient {

    @Autowired
    private AiProperties aiProperties;

    private final RestTemplate restTemplate;

    public LlmClient() {
        // ⚠️ 必须设置超时！RestTemplate 默认没有超时，模型慢的时候会一直挂住线程
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);   // 连接超时 10s
        factory.setReadTimeout(60000);      // 读取超时 60s（Day3 可以改成读配置）
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * 单轮问答
     *
     * @param systemPrompt 系统提示词（定义人设/规则）
     * @param userQuestion 用户问题
     * @return 模型回答
     */
    public String chat(String systemPrompt, String userQuestion) {
        long start = System.currentTimeMillis();
        try {
            // ================= TODO 1：拼请求体 =================
            // 目标 JSON：
            // {
            //   "model": "qwen-plus",
            //   "temperature": 0.3,
            //   "max_tokens": 800,
            //   "messages": [ {"role":"system","content":"..."}, {"role":"user","content":"..."} ]
            // }
            Map<String, Object> body = new HashMap<>();
            // ① 放 model / temperature / max_tokens（从 aiProperties 取）、
            body.put("model", aiProperties.getChatModel());
            body.put("temperature", aiProperties.getTemperature());
            body.put("max_tokens", aiProperties.getMaxTokens());
            // ② 构造 List<Map<String,String>> messages：
            List<Map<String,String>> messages = new ArrayList<>();

            if (org.springframework.util.StringUtils.hasText(systemPrompt)) {
                Map<String,String> sys = new HashMap<>();
                sys.put("role", "system");
                sys.put("content", systemPrompt);
                messages.add(sys);
            }



            Map<String,String> usr = new HashMap<>();
            usr.put("role", "user");
            usr.put("content", userQuestion);
            messages.add(usr);
            body.put("messages", messages);

            //    Map<String,String> sys = new HashMap<>(); sys.put("role","system"); sys.put("content", systemPrompt);
            //    Map<String,String> usr = new HashMap<>(); usr.put("role","user");   usr.put("content", userQuestion);
            //    注意：systemPrompt 为空时不要加 system 那条

            // ================= 请求发送（不用改） =================
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiProperties.getApiKey());

            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);
            ResponseEntity<String> resp = restTemplate.postForEntity(
                    aiProperties.getBaseUrl() + "/chat/completions", entity, String.class);

            // ================= TODO 2：解析响应 =================
            // resp.getBody() 形如：
            // {"model":"qwen-plus","choices":[{"message":{"role":"assistant","content":"..."}}],
            //  "usage":{"prompt_tokens":10,"completion_tokens":20}}
            // 要求：
            // ① JSONObject json = JSON.parseObject(resp.getBody());
            JSONObject json = JSON.parseObject(resp.getBody());
            // ② 取 choices 数组，为空要抛 AiServiceException("模型返回为空")
            JSONArray choices = json.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new AiServiceException("模型返回为空");
            }
            // ③ 取 choices[0].message.content
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject messageObj = firstChoice.getJSONObject("message");
            String content = messageObj.getString("content");
            // ④ log.info 打印：耗时、usage.prompt_tokens、usage.completion_tokens
            long costTime = System.currentTimeMillis() - start;
            JSONObject usage = json.getJSONObject("usage");
            Integer promptTokens = usage.getInteger("prompt_tokens");
            Integer completionTokens = usage.getInteger("completion_tokens");
            log.info("AI调用耗时：{}ms，输入token：{}，输出token：{}", costTime, promptTokens, completionTokens);
            // ⑤ return 回答内容
            return content;

        } catch (RestClientException e) {
            log.error("LLM 调用失败: {}", e.getMessage());
            throw new AiServiceException("AI 服务暂时不可用，请稍后再试");
        }
    }
}
