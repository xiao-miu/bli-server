package com.bilibil.util;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RocketMQUtil {
    // 同步发送消息
    public static void syncSendMsg(DefaultMQProducer producer, Message msg) throws Exception{
        SendResult result = producer.send(msg);
        System.out.println(result);
    }
    // 异步发送消息
    public static void asyncSendMsg(DefaultMQProducer producer, Message msg) throws Exception{
        //发送两次消息
        int count = 2;
        // 倒计时的器
        CountDownLatch2 countDownLatch = new CountDownLatch2(2);
        for(int i = 0; i < count; i++) {
            producer.send(msg, new SendCallback() {
                // 发送成功提醒
                @Override
                public void onSuccess(SendResult sendResult) {
                    countDownLatch.countDown();
                    System.out.println(sendResult.getMsgId());
                }
                // 失败回调
                @Override
                public void onException(Throwable throwable) {
                    countDownLatch.countDown();
                    System.out.println("发送消息的时候异常，发送失败！"+throwable);
                    throwable.printStackTrace();
                }
            });
        }
        // 消息发送结束停留5秒钟
        countDownLatch.await(5, TimeUnit.SECONDS);
//        producer.send(msg, new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//                Logger logger = LoggerFactory.getLogger(RocketMQUtil.class);
//                logger.info("异步发送消息成功，消息id：" + sendResult.getMsgId());
//            }
//            @Override
//            public void onException(Throwable e) {
//                e.printStackTrace();
//            }
//        });
    }
}
