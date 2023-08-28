package com.bilibil.controller;

import com.bilibil.auth.UserAuthorities;
import com.bilibil.entity.JsonResponse;
import com.bilibil.service.UserAuthService;
import com.bilibil.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date:  2023/8/24
 * 用户权限，这是用户权限的切入点
 */
@RestController
public class UserAuthController {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserAuthService authService;
    // 获取用户权限
    @GetMapping("/user-auths")
    public JsonResponse<UserAuthorities> getUserAuthorities(){
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = authService.getUserAuthorities(userId);
        return new JsonResponse<>(userAuthorities);
    }
}
