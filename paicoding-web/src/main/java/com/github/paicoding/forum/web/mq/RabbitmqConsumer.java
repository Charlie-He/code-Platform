package com.github.paicoding.forum.web.mq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paicoding.forum.api.model.context.ReqInfoContext;
import com.github.paicoding.forum.api.model.enums.NotifyStatEnum;
import com.github.paicoding.forum.api.model.enums.NotifyTypeEnum;
import com.github.paicoding.forum.api.model.event.MessageQueueEvent;
import com.github.paicoding.forum.api.model.vo.notify.NotifyMsgEvent;
import com.github.paicoding.forum.core.cache.RedisClient;
import com.github.paicoding.forum.core.common.CommonConstants;
import com.github.paicoding.forum.service.comment.repository.entity.CommentDO;
import com.github.paicoding.forum.service.notify.repository.entity.NotifyMsgDO;
import com.github.paicoding.forum.service.rank.service.UserActivityRankService;
import com.github.paicoding.forum.service.rank.service.model.ActivityScoreBo;
import com.github.paicoding.forum.service.statistics.constants.CountConstants;
import com.github.paicoding.forum.service.user.repository.entity.UserDO;
import com.github.paicoding.forum.service.user.repository.entity.UserFootDO;
import com.github.paicoding.forum.service.user.repository.entity.UserRelationDO;
import com.github.paicoding.forum.web.mq.comsumer.MessageQueueNotifyMsgConsumer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: pai_coding
 * @description: rabbitmq消费者
 * @author: heshaowei
 * @create: 2024-10-30
 */

@Component
@Slf4j
public class RabbitmqConsumer {

    @Autowired
    private MessageQueueNotifyMsgConsumer messageQueueNotifyMsgConsumer;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = CommonConstants.MESSAGE_QUEUE_NAME_NOTIFY_EVENT,
                    durable = "true"
//                        arguments = @Argument(name = "x-queue-mode", value = "lazy")
            ),
            exchange = @Exchange(name = CommonConstants.MESSAGE_QUEUE_EXCHANGE_NAME_DIRECT, type = ExchangeTypes.DIRECT),
            key = {CommonConstants.MESSAGE_QUEUE_KEY_NOTIFY}
    ),
            concurrency = "1-5"
    )
    @SuppressWarnings("unchecked")
    public <T> void listenNotifyEventQueue(MessageQueueEvent<T> msgEvent) {
        log.info("消费者2接收到direct.queue2的消息：【{}】", msgEvent);

        switch (msgEvent.getNotifyType()){
            case COMMENT :
                messageQueueNotifyMsgConsumer.saveCommentNotify(objectMapper.convertValue(msgEvent, new TypeReference<>() {}));
                break;
            case REPLY:
                messageQueueNotifyMsgConsumer.saveReplyNotify(objectMapper.convertValue(msgEvent, new TypeReference<>() {}));
                break;
            case PRAISE:
            case COLLECT:
                messageQueueNotifyMsgConsumer.saveArticleNotify(objectMapper.convertValue(msgEvent, new TypeReference<>() {}));
                break;
            case CANCEL_PRAISE:
            case CANCEL_COLLECT:
                messageQueueNotifyMsgConsumer.removeArticleNotify(objectMapper.convertValue(msgEvent, new TypeReference<>() {}));
                break;
            case FOLLOW:
                messageQueueNotifyMsgConsumer.saveFollowNotify(objectMapper.convertValue(msgEvent, new TypeReference<>() {}));
                break;
            case CANCEL_FOLLOW:
                messageQueueNotifyMsgConsumer.removeFollowNotify(objectMapper.convertValue(msgEvent, new TypeReference<>() {}));
                break;
            case REGISTER:
                messageQueueNotifyMsgConsumer.saveRegisterSystemNotify(objectMapper.convertValue(msgEvent, new TypeReference<>() {}));
                break;
            case LOGIN:
                // TODO: 增加一个登录消息
                break;
            default:
                break;
        }
    }
}
