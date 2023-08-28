package com.bilibil.mapper;

import com.bilibil.entity.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Date:  2023/8/21
 * 用户关注表
 */
@Mapper
public interface FollowingGroupMapper {
    // 查询用户的分组
    FollowingGroup getByType(String type);
    // 根据ID查询用户关注的信息
    FollowingGroup getById(Long id);
    // 进行吧获取到关注的用户进行分组
    List<FollowingGroup> getByUserId(Long userId);
    // 新建用户关注分组
    Integer adaddFollowingGroup(FollowingGroup followingGroup);
    // 获取用户关注分组
    List<FollowingGroup> getUserFollowingGroups(Long userId);
}
