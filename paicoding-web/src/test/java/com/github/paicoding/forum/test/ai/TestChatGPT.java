package com.github.paicoding.forum.test.ai;

import com.esotericsoftware.minlog.Log;
import com.github.paicoding.forum.api.model.vo.chat.ChatItemVo;
import com.github.paicoding.forum.api.model.vo.chat.ChatRecordsVo;
import com.github.paicoding.forum.service.chatai.service.processMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class TestChatGPT{

    // 使用环境变量或占位符替代真实API密钥
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY") != null ? 
                                         System.getenv("OPENAI_API_KEY") : 
                                         "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String OPENAI_API_URL = "https://api.chatanywhere.tech/v1/chat/completions";

    public static void main(String[] args) {
        System.out.println("hhhh");
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String prompt="赏析一下静夜思";
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
                httpPost.setEntity(new StringEntity(requestBody));

                // 执行请求并处理响应流
                try (CloseableHttpResponse response = httpClient.execute(httpPost);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.isBlank() && line.startsWith("data:")) {
                            String content = line.substring(5).trim(); // 去掉 "data:" 前缀
                            if ("[DONE]".equals(content)) {
                                break; // 流式响应结束
                            }
                            System.out.println("gpt输出的数据"+content);
                            // 推送数据到前端
                        }
                    }
                }
            } catch (Exception e) {
                // 异常信息推送到前端
                System.out.println("error");
            }
    }
}