package com.bilibil.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bilibil.constant.UserMomentsConstant;
import com.bilibil.entity.UserMoment;
import com.bilibil.exception.ConditionException;
import com.bilibil.mapper.UserMomentsMapper;
import com.bilibil.service.UserMomentsService;
import com.bilibil.util.RocketMQUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Date:  2023/8/23
 */
@Service
public class UserMomentsSerIMPL implements UserMomentsService {
    @Autowired
    private UserMomentsMapper userMomentsMapper;
    // 获取到跟springboot相关的配置和bean ， 需要获取到MQ中的生产者和消费者
    @Autowired
    private ApplicationContext applicationContext;
    // 引入redis
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    // 新建用户的动态
    @Override
    public void addUserMoments(UserMoment userMoment) {
        userMoment.setCreateTime(new Date());
        userMomentsMapper.addUserMoments(userMoment);
        // 获取生产者
        DefaultMQProducer producer = (DefaultMQProducer) applicationContext.getBean("momentsProducer");
        // 生成Massage 主题 发送到MQ中
        Message message = new Message(
                UserMomentsConstant.TOPIC_MOMENTS , JSONObject.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));
        // 同步发送消息
        try {
            RocketMQUtil.syncSendMsg(producer,message);
        } catch (Exception e) {
            throw new ConditionException("发送消息失败！");
        }
    }
    // 查询用户订阅的消息,查询的是用户关注用户的信
    @Override
    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        // 数据存到redis里，所以我们要从redis里取数据
        String key = "subscribed-"+userId;
        // 从redis查出来的是字符串，查出来的是所有关注的up主的用户订阅的信息
        String listStr = redisTemplate.opsForValue().get(key);
        // 转换成列表
        List<UserMoment> list = JSONArray.parseArray(listStr, UserMoment.class);
        return list;
    }
}
