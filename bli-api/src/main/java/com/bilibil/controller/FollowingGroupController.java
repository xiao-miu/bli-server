package com.bilibil.controller;

import com.bilibil.entity.FollowingGroup;
import com.bilibil.entity.JsonResponse;
import com.bilibil.service.FollowingGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date:  2023/8/21
 */
@RestController
public class FollowingGroupController {
    @Autowired
    private FollowingGroupService followingGroupService;

    // 查询用户关注分组
//    @GetMapping("/")
//    public JsonResponse<String> getByType(String type){
//
//        return JsonResponse.success();
//    }
}
