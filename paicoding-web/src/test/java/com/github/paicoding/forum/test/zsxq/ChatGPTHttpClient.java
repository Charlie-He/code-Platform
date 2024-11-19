package com.github.paicoding.forum.test.zsxq;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatGPTHttpClient {

    private static final String API_KEY = "sk-gp3ZovpG6HV6j290mRoYt2qrxsncZ6LLcTOlDmqEV6WN5ji3"; // 替换为你的 OpenAI API 密钥
    private static final String API_URL = "https://api.chatanywhere.tech/v1/chat/completions";

    public static void main(String[] args) {
        ChatGPTHttpClient client = new ChatGPTHttpClient();
        String response = client.askChatGPT("赏析一下静夜思");
        System.out.println("Response: " + response);
    }

    public String askChatGPT(String message) {
        try {
            // 构建 JSON 请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo"); // 指定模型
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", message));
            requestBody.put("messages", messages);
            requestBody.put("stream",true);

            // 创建 HTTP 请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + API_KEY) // 设置 API 密钥
                    .header("Content-Type", "application/json") // 设置请求头
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString())) // 请求体
                    .build();

            // 创建 HTTP 客户端并发送请求
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 处理响应
            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                return choices.getJSONObject(0).getJSONObject("message").getString("content");
            } else {
                System.err.println("Error: " + response.statusCode());
                System.err.println(response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
