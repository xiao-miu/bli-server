package com.bilibil.service;

import com.bilibil.entity.UserMoment;

import java.util.List;

/**
 * Date:  2023/8/23
 */
public interface UserMomentsService {
    // 新建用户的动态
    void addUserMoments(UserMoment userMoment);
    // 查询用户订阅的消息,查询的是用户关注用户的信
    List<UserMoment>  getUserSubscribedMoments(Long  userId);
}
