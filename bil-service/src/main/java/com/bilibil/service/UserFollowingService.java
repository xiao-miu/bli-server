package com.bilibil.service;

import com.bilibil.entity.FollowingGroup;
import com.bilibil.entity.UserFollowing;
import com.bilibil.entity.UserInfo;

import java.util.List;

/**
 * Date:  2023/8/21
 * 用户关注类
 */
public interface UserFollowingService {

    // 用户添加关注
    void adduserFollowings(UserFollowing userFollowing);
    // 将关注用户按关注分组进行分类
    List<FollowingGroup> getUserFollowing(Long userId);
    // 第一步:获取当前用户的粉丝列表
    List<UserFollowing> getUserFans(Long userId);
    // 新建用户关注分组
    Long addUserFollowingGroups(FollowingGroup followingGroup);
    // 获取用户关注分组
    List<FollowingGroup> getUserFollowingGroups(Long userId);
    // 检查关注的状态，用户信息的列表，和当前登录的ID
    List<UserInfo> checkFullowingStatus(List<UserInfo> list, Long userId);

}
