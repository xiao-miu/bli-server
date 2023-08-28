package com.bilibil.service.impl;

import com.bilibil.constant.UserConstant;
import com.bilibil.entity.FollowingGroup;
import com.bilibil.entity.User;
import com.bilibil.entity.UserFollowing;
import com.bilibil.entity.UserInfo;
import com.bilibil.exception.ConditionException;
import com.bilibil.mapper.UserFollowingMapper;
import com.bilibil.service.FollowingGroupService;
import com.bilibil.service.UserFollowingService;
import com.bilibil.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date:  2023/8/21
 */
@Service
public class UserFollowingSerIMPL implements UserFollowingService {
    @Autowired
    private UserFollowingMapper userFollowingMapper;
    @Autowired
    private FollowingGroupService followingGroupService;
    // 查询用户
    @Autowired
    private UserService userService;
    // 添加事务预防删除成功添加失败，可以回滚数据
    @Override
    @Transactional
    // 用户添加关注
    public void adduserFollowings(UserFollowing userFollowing) {
        // 先分组(有默认分组)
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {
            // 不存在的情况下
            FollowingGroup byType = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            // 添加默认分组
            userFollowing.setGroupId(byType.getId());
        }else {
            // 添加默认分组
            FollowingGroup byId = followingGroupService.getById(groupId);
            // 判断分组是否存在
            if(byId == null){
                throw new ConditionException("关注分组不存在");
            }
        }
        // 判断用户关注的人存不存在
        Long followingId = userFollowing.getFollowingId();
        // 获取用户信息
        User userById = userService.getUserById(followingId);
        if(userById == null){
            throw new ConditionException("关注用户不存在!");
        }
        // 关联关系添加
        // 先删除关联关系
        userFollowingMapper.deleteUserFollowing(userFollowing.getUserId(),followingId);
        //添加关注的用户
        userFollowing.setCreateTime(new Date());
        userFollowingMapper.adduserFollowings(userFollowing);
    }
    //第一步:获取关注的用户列表
    //第二步:根据关注用户的id查询关注用户的基本信息
    //第三步:将关注用户按关注分组进行分类
    @Override
    public List<FollowingGroup> getUserFollowing(Long userId){
        // 获取用户信息
        //第一步:获取关注的用户列表
        List<UserFollowing> list = userFollowingMapper.getUserFollowings(userId);
        Set<Long> followingSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        //把获取到的关注用户的信息放到list里
        ArrayList<UserInfo> userInfoList = new ArrayList<>();
        // 拿到用户信息
        if(followingSet.size() > 0){
            userInfoList = userService.getUserInfoByUserIds(followingSet);
        }
        for (UserFollowing userFollowing : list) {
            for (UserInfo userInfo : userInfoList) {
                // 找出匹配的信息
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    // 专注的用户放到列表里
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        // 进行吧获取到关注的用户进行分组
        List<FollowingGroup> followingGroupList = followingGroupService.getByUserId(userId);
        // 进行用户分组，前端展现时候的分组直接拿出来拼成就行
        FollowingGroup followingGroup = new FollowingGroup();
        followingGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        //全部分组的构建
        followingGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(followingGroup);
        for (FollowingGroup group:followingGroupList) {
            ArrayList<UserInfo> userInfos = new ArrayList<>();
            // 存放用户关注的列表
            for (UserFollowing userFollowingList:list) {
                // 判断
                if(group.getId().equals(userFollowingList.getGroupId())){
                    userInfos.add(userFollowingList.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(userInfos);
            result.add(group);
        }
        return result;
    }

    // 第一步:获取当前用户的粉丝列表
    // 第二步:根据粉丝的用户id查询基本信息
    // 策三步:查询当前用户是否已经关注该粉丝
    @Override
    public List<UserFollowing> getUserFans(Long userId) {
        // 第一步:获取当前用户的粉丝列表
        List<UserFollowing> list = userFollowingMapper.getUserFans(userId);
        Set<Long> fanIdSet = list.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        //把获取到的关注用户的信息放到list里
        ArrayList<UserInfo> userInfoList = new ArrayList<>();
        // 第二步:根据粉丝的用户id查询基本信息
        if (fanIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        // 策三步:查询当前用户是否已经关注该粉丝
        List<UserFollowing> userFollowings = userFollowingMapper.getUserFollowings(userId);
        // 遍历粉丝列表
       for (UserFollowing userFollowing: list){
           // 对粉丝列表进行赋值
           for(UserInfo userInfo: userInfoList){
                // 是否被关注的状态赋值
                if(userFollowing.getUserId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(false);
                    userFollowing.setUserInfo(userInfo);
                }
           }
           // 用户关注的列表
            for(UserFollowing userFoll: userFollowings){
                // 是否互粉
                if(userFoll.getUserId().equals(userFollowing.getUserId())){
                    userFollowing.getUserInfo().setFollowed(true);
                }
            }
        }
       return list;
    }

    // 新建用户关注分组
    @Override
    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        Long userid = followingGroupService.addUserFollowingGroups(followingGroup);
        return userid;
    }
    // 获取用户关注分组
    @Override
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }
    // 检查关注的状态，用户信息的列表，和当前登录的ID
    @Override
    public List<UserInfo> checkFullowingStatus(List<UserInfo> list, Long userId) {
        // 查询当前登录用户，关注了那些用户
        List<UserFollowing> userFollowingList = userFollowingMapper.getUserFollowings(userId);
        for (UserInfo userInfo : list) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : userFollowingList) {
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(true);
                }
            }
        }
        return list;
    }

}
