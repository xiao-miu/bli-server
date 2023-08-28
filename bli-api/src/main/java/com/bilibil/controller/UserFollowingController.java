package com.bilibil.controller;

import com.bilibil.entity.FollowingGroup;
import com.bilibil.entity.JsonResponse;
import com.bilibil.entity.UserFollowing;
import com.bilibil.service.UserFollowingService;
import com.bilibil.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Date:  2023/8/21
 */
@RestController
public class UserFollowingController {
    @Autowired
    private UserFollowingService userFollowingService;
    // 需要获取到当前用户的信息
    @Autowired
    private UserSupport userSupport;
    // 用户添加关注
    @PostMapping("/user-follow")
    public JsonResponse<String> addUserFollowing(@RequestBody UserFollowing userFollowing){
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.adduserFollowings(userFollowing);
        return JsonResponse.success();
    }
    // 获取关注的用户列表
    @GetMapping("/user-following")
    public JsonResponse<List<FollowingGroup>> getUserFollowing(){
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> userFollowing = userFollowingService.getUserFollowing(userId);
        return new JsonResponse<>(userFollowing);
    }
    // 获取当前用户的粉丝列表
    @GetMapping("/user-UserFans")
    public JsonResponse<List<UserFollowing>> getUserFans(){
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowing> userFans = userFollowingService.getUserFans(userId);
        return new JsonResponse<>(userFans);
    }
    // 新建用户关注分组
    @PostMapping("/user-following-groups")
    public JsonResponse<Long> addUserFollowingGroup(@RequestBody FollowingGroup followingGroup) {
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroups(followingGroup);
        return new JsonResponse<>(groupId);
    }
    // 获取用户关注分组
    @GetMapping("/user-following-groups")
    public JsonResponse<List<FollowingGroup>>
        getUserFollowingGroup(){
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> followingGroupList = userFollowingService.getUserFollowingGroups(userId);

        return new JsonResponse<>(followingGroupList  );
    }

}
