package com.bilibil.controller;

import com.bilibil.constant.AuthRoleConstant;
import com.bilibil.entity.JsonResponse;
import com.bilibil.entity.UserMoment;
import com.bilibil.entity.annotation.ApiLimitedRole;
import com.bilibil.service.UserMomentsService;
import com.bilibil.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Date:  2023/8/23
 * 用户动态的操作
 */
@RestController
public class UserMomentsController {
    @Resource
    private UserMomentsService userMomentsService;
    @Autowired
    private UserSupport userSupport;

    // 控制接口权限
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})
    // 新建用户的动态
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment){
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }
    // 查询用户订阅的消息,查询的是用户关注用户的信
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments() {
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list = userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }
}
