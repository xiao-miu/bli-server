package com.bilibil.service;

import com.bilibil.entity.FollowingGroup;

import java.util.List;

/**
 * Date:  2023/8/21
 * 用户关注类
 */
public interface FollowingGroupService {
    // 查询用户关注分组
    FollowingGroup getByType(String type);
    // 查询用户关注信息
    FollowingGroup getById(Long id);
    // 进行吧获取到关注的用户进行分组
    List<FollowingGroup> getByUserId(Long userId);
    // 新建用户关注分组
    Long addUserFollowingGroups(FollowingGroup followingGroup);

    List<FollowingGroup> getUserFollowingGroups(Long userId);
}
