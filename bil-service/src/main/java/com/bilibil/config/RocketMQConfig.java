package com.bilibil.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bilibil.constant.UserMomentsConstant;
import com.bilibil.entity.UserFollowing;
import com.bilibil.entity.UserMoment;
import com.bilibil.service.UserFollowingService;
import com.bilibil.service.webSocket.WebSocketService;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Date:  2023/8/23
 * 用户动态提醒
 */
@Configuration
public class RocketMQConfig {
    // 配置
    // 注入
    @Value("${rocketmq.name-server.address}")
    private String nameServerAddr;

    // redis 工具类
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    // 粉丝列表
    @Autowired
    private UserFollowingService userFollowingService;

    // 生产者
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(
                UserMomentsConstant.GROUP_MOMENTS);
        // 设置地址
        producer.setNamesrvAddr(nameServerAddr);
        // 启动
        producer.start();
        return producer;
    }

    // 消费者
    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws MQClientException {
        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        // 设置地址
        pushConsumer.setNamesrvAddr(nameServerAddr);
        // 订阅生产者，跟这个主题所有相关的分类的内容都要订阅
        pushConsumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*");
        // 添加注册监听器，用户动态提醒
        pushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                    List<MessageExt> masgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                // 对消息进行打印
                // List<MessageExt> 因为我们默认只发送一条数据，所以masgs里只有一条数据
                MessageExt messageExt = masgs.get(0);
                if(messageExt == null){
                    // 消费者消费成功的状态
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                // 取数据
                String body = new String(messageExt.getBody());
                // 第一个参数是传入的参数，第二个参数是要转换成什么类型，在创建用户动态的时候新生成的userMoment实体类
                UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(body), UserMoment.class);
                // 取ID
                Long userId = userMoment.getUserId();
                // 查询订阅了这个UserId的用户，通过用户粉丝来查询
                List<UserFollowing> userFans = userFollowingService.getUserFans(userId);
                for (UserFollowing userFollowing:
                     userFans) {
                    // 给粉丝，发送新动态
                    // 把发送的内容先给redis，在通过redis查询到每个粉丝里,那个用户订阅了这个内容
                    String key = "subscribed-"+userFollowing.getUserId();
                    // 先看一下有没有对应的value , 粉丝肯定订阅了很多up主，所以肯定订阅肯定是个list
                    String subscribedListStr = redisTemplate.opsForValue().get(key);
                    // 声明一个订阅的列表
                    List<UserMoment> subscribedList;
                    // 判断列表是否存在
                    if(StringUtil.isNullOrEmpty(subscribedListStr)){
                        subscribedList = new ArrayList<UserMoment>();
                    }else {
                        // 不为空进行类型转换
                        subscribedList = JSONArray.parseArray(subscribedListStr , UserMoment.class);
                    }
                    subscribedList.add(userMoment);
                    // 添加进去
                    redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
                }
//                for (MessageExt messageExt : masgs ){
//                    System.out.println(messageExt);
//                }
                // 消费者消费成功的状态
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        pushConsumer.start();
        return pushConsumer;
    }


    // 弹幕生产者
    @Bean("danmusProducer")
    public DefaultMQProducer danmusProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(
                UserMomentsConstant.GROUP_DANMUS);
        // 设置地址
        producer.setNamesrvAddr(nameServerAddr);
        // 启动
        producer.start();
        return producer;
    }

    // 弹幕消费者
    @Bean("danmusConsumer")
    public DefaultMQPushConsumer danmusConsumer() throws MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_DANMUS);
        // 设置NameServer的地址
        consumer.setNamesrvAddr(nameServerAddr);
        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe(UserMomentsConstant.TOPIC_DANMUS, "*");
        // 注册回调实现类来处理从broker拉取回来的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt msg = msgs.get(0);
                byte[] msgByte = msg.getBody();
                String bodyStr = new String(msgByte);
                JSONObject jsonObject = JSONObject.parseObject(bodyStr);
                // 存储客户和服务端会话的ID
                String sessionId = jsonObject.getString("sessionId");
                // 前端传过来的弹幕
                String message = jsonObject.getString("message");
                WebSocketService webSocketService = WebSocketService.WEBSOCKET_MAP.get(sessionId);
                if(webSocketService.getSession().isOpen()){
                    try {
                        webSocketService.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // 标记该消息已经被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者实例
        consumer.start();
        return consumer;
    }
}
