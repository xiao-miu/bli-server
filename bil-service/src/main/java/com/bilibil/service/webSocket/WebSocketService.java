package com.bilibil.service.webSocket;

import com.alibaba.fastjson.JSONObject;
import com.bilibil.constant.UserConstant;
import com.bilibil.constant.UserMomentsConstant;
import com.bilibil.entity.Danmu;
import com.bilibil.service.DanmuService;
import com.bilibil.util.RocketMQUtil;
import com.bilibil.util.TokenUtil;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Date:  2023/9/3
 */
@Component
@ServerEndpoint("/imService")
public class WebSocketService {
    // 日志记录
    private final Logger logger =  LoggerFactory.getLogger(this.getClass());
    // AtomicInteger当前长连接的人数，一开始每人连接所以是0
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);
    // ConcurrentHashMap 确保线程安全的Map类来存储数据
    public static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP
            = new ConcurrentHashMap<>();
    // 通过Session进行客户端通信
    private Session session;
    // 唯一id
    private String sessionId;
    private Long userId;

    private static ApplicationContext APPLICATION_CONTEXT;

    //ApplicationContext是从启动类里来的
    public static void setApplicationContext(ApplicationContext application){
        WebSocketService.APPLICATION_CONTEXT = application;
    }


    // 打开连接 ，@OnOpen 当连接成功去调用这个注解相连接的方法
    @OnOpen
    public void openConnection(Session session , @PathParam("token") String token) {
        // 多例模式bean解决
//        RedisTemplate<String,String> redisTemplate = (RedisTemplate) WebSocketService
//                .APPLICATION_CONTEXT.getBean("redisTemplate");
//        redisTemplate.opsForValue().get("token");
        try {
            // 游客模式没有Token
            this.userId = TokenUtil.verifyToken(token);
        } catch (Exception e) {
        }
        this.session.getId();
        // 把session放到本地session里
        this.session = session;
        // 判断如果WEBSOCKET_MAP里存储过session，如果有去掉，添加新的
        if(WEBSOCKET_MAP.containsKey(sessionId)){
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId, this);
        }else {
            // 客户端第一次连接服务端
            WEBSOCKET_MAP.put(sessionId, this);
            // 当前人数加1
            ONLINE_COUNT.getAndIncrement();
        }
        // 日志
        logger.info("用户连接成功:"+sessionId+"当前连接的人数:"+ONLINE_COUNT.get());
        // 通知前端或客户端连接成功
        try {
            // 返回状态码0 表示成功
            this.sendMessage("0");
        }catch (Exception e){
            logger.error("连接异常");
        }
    }

    // 连接关闭  服务端断了，关闭当前页面
    @OnClose
    public void closeConnection() {
        if(WEBSOCKET_MAP.containsKey(sessionId)){
            WEBSOCKET_MAP.remove(sessionId);
            // 在线人数-1
            ONLINE_COUNT.getAndDecrement();
        }
        // 日志
        logger.info("用户退出:"+sessionId+"当前连接的人数:"+ONLINE_COUNT.get());
    }

    // 正在通信的时候
    @OnMessage
    public void onMessage(String message){
        logger.info("用户信息:"+sessionId+"，报文:"+message);
        if(!StringUtil.isNullOrEmpty(message)){
            try {
                // 实现弹幕 ， 群发消息
                for(Map.Entry<String,WebSocketService> entry : WEBSOCKET_MAP.entrySet()){
                    // 获取每一个长连的服务
                    WebSocketService webSocketService = entry.getValue();
                    // 优化
                    // 弹幕的生产者
                    DefaultMQProducer danmuProducer = (DefaultMQProducer) APPLICATION_CONTEXT.getBean("danmusProducer");
                    // 往弹幕生产者里发送消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message",message);
                    jsonObject.put("sessionId",webSocketService.getSessionId());
                    Message msg = new Message(UserMomentsConstant.TOPIC_DANMUS, jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
                    // 异步发送消息
                    RocketMQUtil.asyncSendMsg(danmuProducer,msg);
                }

                if(this.userId != null){
                    // 弹幕保存导数据库
                    Danmu danmu = JSONObject.parseObject(message, Danmu.class);
                    danmu.setUserId(userId);
                    danmu.setCreateTime(new Date());
                    DanmuService danmuService = (DanmuService) APPLICATION_CONTEXT.getBean("danmuService");
                    // 把保存弹幕先放到队列 ，削峰处理
                    danmuService.asyncAddDanmu(danmu);
//                    danmuService.addDanmu(danmu);
                    //保存弹幕到redis（可以同步，也可以异步）
                    danmuService.addDanmusToRedis(danmu);
                }
            }catch (Exception e){
                logger.error("弹幕接收出现问题");
            }
        }
    }

    // 如果出现问题的时候
    @OnError
    public void onError(Throwable error){

    }

    // 连接成功
    public void sendMessage(String message) throws IOException {
        // 字符串 信息传输
        this.session.getBasicRemote().sendText(message);
    }

    //或直接指定时间间隔，例如：5秒       Scheduled 定时任务
    @Scheduled(fixedRate=5000)
    private void noticeOnlineCount() throws IOException {
        for(Map.Entry<String, WebSocketService> entry : WebSocketService.WEBSOCKET_MAP.entrySet()){
            WebSocketService webSocketService = entry.getValue();
            // 查看会话是否是打开的
            if(webSocketService.session.isOpen()){
                // 给前端的提示语
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount", ONLINE_COUNT.get());
                jsonObject.put("msg", "当前在线人数为" + ONLINE_COUNT.get());
                webSocketService.sendMessage(jsonObject.toJSONString());
            }
        }
    }


    public Session getSession() {
        return session;
    }

    public String getSessionId() {
        return sessionId;
    }
}
