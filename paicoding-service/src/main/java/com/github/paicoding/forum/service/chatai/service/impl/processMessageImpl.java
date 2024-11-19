package com.github.paicoding.forum.service.chatai.service.impl;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paicoding.forum.api.model.vo.chat.ChatItemVo;
import com.github.paicoding.forum.api.model.vo.chat.ChatRecordsVo;
import com.github.paicoding.forum.service.chatai.service.processMessage;
import jakarta.annotation.Resource;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class processMessageImpl implements processMessage{

    private final String OPENAI_API_KEY = "sk-gp3ZovpG6HV6j290mRoYt2qrxsncZ6LLcTOlDmqEV6WN5ji3";
    private final String OPENAI_API_URL = "https://api.chatanywhere.tech/v1/chat/completions";

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    public void processMessage(String prompt) {
        CompletableFuture.runAsync(() -> {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // 构造 HTTP 请求
                HttpPost httpPost = new HttpPost(OPENAI_API_URL);
                httpPost.setHeader("Authorization", "Bearer " + OPENAI_API_KEY);
                httpPost.setHeader("Content-Type", "application/json");

                String requestBody = """
                        {
                          "model": "gpt-3.5-turbo",
                          "messages": [{"role": "user", "content": "%s"}],
                          "stream": true
                        }
                        """.formatted(prompt);
                httpPost.setEntity(new StringEntity(requestBody,StandardCharsets.UTF_8));
                // 执行请求并处理响应流
                try (CloseableHttpResponse response = httpClient.execute(httpPost);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.isBlank() && line.startsWith("data:")) {
                            String content = line.substring(5).trim();// 去掉 "data:" 前缀
                            JsonNode jsonNode = new ObjectMapper().readTree(content);
                            String message = jsonNode.get("choices").get(0).get("delta").get("content").asText();
                            if(StrUtil.isNotBlank(message)){
                                if ("[DONE]".equals(content)) {
                                    break; // 流式响应结束
                                }
                                //推送数据到前端
                                messagingTemplate.convertAndSend("/chat/rsp",message);
                            }
                            }
                    }
                }
            } catch (Exception e) {
                // 异常信息推送到前端
//                messagingTemplate.convertAndSend("/chat/rsp", "Error: " + e.getMessage());
            }
        });
    }
}
