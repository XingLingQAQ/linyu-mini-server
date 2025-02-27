package com.cershy.linyuminiserver.service;

import cn.hutool.core.util.StrUtil;
import com.cershy.linyuminiserver.configs.LinyuConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RagFlowAiService {

    private final Cache<String, AtomicInteger> limitCache;

    @Resource
    private LinyuConfig linyuConfig;
    private RestTemplate restTemplate;
    private HttpHeaders headers;

    RagFlowAiService() {
        this.limitCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();
    }

    @PostConstruct
    public void init() {
        // 初始化 RestTemplate
        restTemplate = new RestTemplate();
        // 设置请求头，包括 API Key
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + linyuConfig.getRagflow().getApiKey());
        headers.set("Content-Type", "application/json");
    }

    public String ask(String userId, String content) {
        AtomicInteger count = limitCache.getIfPresent(userId);
        if (count == null) {
            count = new AtomicInteger(0);
            limitCache.put(userId, count);
        }

        // 检查用户调用次数限制
        if (linyuConfig.getRagflow().getCountLimit() > 0
                && count.incrementAndGet() > linyuConfig.getRagflow().getCountLimit()) {
            return "您已经达到限制了，请24小时后再来吧~";
        }

        // 检查内容是否为空
        if (StrUtil.isBlank(content)) {
            return "内容不能为空~";
        }

        // 检查内容长度限制
        if (linyuConfig.getRagflow().getLengthLimit() > 0
                && content.length() > linyuConfig.getRagflow().getLengthLimit()) {
            return "问一些简单的问题吧~";
        }

        // 调用 DeepSeek API
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("session_id", linyuConfig.getRagflow().getSessionId());
            requestBody.put("stream", false);
            requestBody.put("question", content);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    linyuConfig.getRagflow().getHost() + "/api/v1/chats/"
                            + linyuConfig.getRagflow().getChatId() + "/completions",
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );
            // 解析响应
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> choices = (Map<String, Object>) responseEntity.getBody().get("data");
                return (String) choices.get("answer");
            } else {
                return "林语小助手异常，请稍后再试~";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "林语小助手已离家出走了，请稍后再试~";
        }
    }
}