package com.bilibil.service.impl;

import com.bilibil.constant.UserConstant;
import com.bilibil.entity.FollowingGroup;
import com.bilibil.mapper.FollowingGroupMapper;
import com.bilibil.service.FollowingGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Date:  2023/8/21
 * 用户关注类
 */
@Service
public class FollowingGroupSerIMPL implements FollowingGroupService {
    @Autowired
    private FollowingGroupMapper followingGroupMapper;
//    先查询用户的分组    关注分组类型：0特别关注  1悄悄关注 2默认分组  3用户自定义分组
    @Override
    public FollowingGroup getByType(String type){
        return followingGroupMapper.getByType(type);
    }
    // 根据ID查询用户关注的信息
    @Override
    public FollowingGroup getById(Long id){
        return followingGroupMapper.getById(id);
    }
    // 进行吧获取到关注的用户进行分组
    @Override
    public List<FollowingGroup> getByUserId(Long userId) {
        return followingGroupMapper.getByUserId(userId);
    }

    @Override
    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroupMapper.adaddFollowingGroup(followingGroup);
        return followingGroup.getId();
    }
    // 获取用户关注分组
    @Override
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupMapper.getUserFollowingGroups(userId);
    }

}
