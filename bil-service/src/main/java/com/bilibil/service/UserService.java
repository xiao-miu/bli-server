package com.bilibil.service;

import com.alibaba.fastjson.JSONObject;
import com.bilibil.entity.PageResult;
import com.bilibil.entity.User;
import com.bilibil.entity.UserInfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Date:  2023/8/20
 * 用户信息类
 */
public interface UserService {
    // 注册用户
    void addUser(User user);
    // 登录
    String login(User user) throws Exception;
    // 获取用户信息
    User getUserInfo(Long userId);

    void updateUsers(User user);
    // 更新用户基本信息
    void updateUserInfo(UserInfo userInfo);
    // 查询用户信息
    User getUserById(Long id);
    // 获取关注的用户信息放到集合
    ArrayList<UserInfo> getUserInfoByUserIds(Set<Long> userId);
    // 分页查询用户列表（方便用户关注和取消关注操作）
    PageResult<UserInfo> pageListUserInfos(JSONObject params);
    //双token登录  AES 双加密，AES接入toke
    Map<String, Object> loginForDts(User user) throws Exception;
    // 退出登录
    void logout(Long userId, String refreshToken);
    // 获取到刷新之后的AEStoken
    String refreshAccessToken(String refreshToken) throws Exception;
}
