package com.github.paicoding.forum.web.controller.chat.rest;

import com.github.paicoding.forum.api.model.context.ReqInfoContext;
import com.github.paicoding.forum.api.model.enums.ai.AISourceEnum;
import com.github.paicoding.forum.api.model.vo.chat.ChatItemVo;
import com.github.paicoding.forum.api.model.vo.chat.ChatRecordsVo;
import com.github.paicoding.forum.service.chatai.service.processMessage;
import com.github.paicoding.forum.web.controller.chat.helper.WsAnswerHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

/**
 * STOMP协议的ChatGpt聊天通讯实现方式
 *
 * @author heshaowei
 * @date 2024/07/11
 */
@Slf4j
@RestController
public class ChatRestController {
    @Autowired
    private WsAnswerHelper answerHelper;
    @Resource
    private processMessage processMessage;

    /**
     * 接收用户发送的消息
     *
     * @param msg
     * @param session
     * @param attrs
     * @return
     * @MessageMapping（"/chat/{session}"）注解的方法将用来接收"/app/chat/xxx路径发送来的消息，<br/> 如果有 @SendTo，则表示将返回结果，转发到其对应的路径上 （这个sendTo的路径，就是前端订阅的路径）
     * @DestinationVariable： 实现路径上的参数解析
     * @Headers 实现请求头格式的参数解析, @Header("headName") 表示获取某个请求头的内容
     */
    @MessageMapping("/chat/{session}")
//    @SendTo("/chat/rsp")
    public void chat(String msg, @DestinationVariable("session") String session, @Header("simpSessionAttributes") Map<String, Object> attrs) {
        processMessage.processMessage(msg);
    }
}
