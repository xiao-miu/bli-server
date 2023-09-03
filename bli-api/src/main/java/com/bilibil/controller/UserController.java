package com.bilibil.controller;

import com.alibaba.fastjson.JSONObject;
import com.bilibil.entity.JsonResponse;
import com.bilibil.entity.PageResult;
import com.bilibil.entity.User;
import com.bilibil.entity.UserInfo;
import com.bilibil.service.UserFollowingService;
import com.bilibil.service.UserService;
import com.bilibil.support.UserSupport;
import com.bilibil.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Date:  2023/8/20
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserSupport userSupport;
    // 用户关注的列表
    @Autowired
    private UserFollowingService userFollowingService;
    // 获取用户信息
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo(){
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return new JsonResponse<>(user);
    }

    // 获取rsa公钥
    @GetMapping("/rsa-pas")
    public JsonResponse<String> getResPulicKey() {
        String publicKeyStr = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(publicKeyStr);
    }
    @PostMapping("/users")
    // 注册用户              @RequestBody 在前端传值的时候进行封装成JSON类型
    public JsonResponse<String> addUser(@RequestBody User user){
        userService.addUser(user);
        return JsonResponse.success();
    }

    // 用户登录，先获取请求用户凭证
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return JsonResponse.success(token);
    }
    //双token登录  AES 双加密，AES接入token  ， RSA刷新token  通过RSA刷新token
    @PostMapping("/user-dts")
    public JsonResponse<Map<String,Object>> loginForDts(@RequestBody User user) throws Exception {

        Map<String,Object> map = userService.loginForDts(user);
        return new JsonResponse<>(map);
    }

    // 退出登录
    // HttpServletRequest 可以获取到请求头的信息以及其他有关信息（登录的信息）
    @DeleteMapping("/refresh-token")
    public JsonResponse<String> logout(HttpServletRequest request){
        // 根据名称获取请求头的值
        String refreshToken = request.getHeader("refreshToken");
        Long userId = userSupport.getCurrentUserId();
        userService.logout(userId, refreshToken);
        return JsonResponse.success("退出登录!");
    }
    // 刷新REStoken
    @PostMapping("access-token")
    public JsonResponse<String> accessToken(HttpServletRequest request) throws Exception {
        // 根据名称获取请求头的值
        String refreshToken = request.getHeader("refreshToken");
        //获取到刷新之后的AEStoken
        String accessToken = userService.refreshAccessToken(refreshToken);
        return JsonResponse.success(accessToken);
    }

    // 更新用户信息
    @PutMapping("/users")
    public JsonResponse<String> updateUsers(@RequestBody User user) {
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        userService.updateUsers(user);
        return JsonResponse.success();
    }
    // 更新用户基本信息
    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfo(@RequestBody UserInfo userInfo) {
        Long userInfoId = userSupport.getCurrentUserId();
        userInfo.setId(userInfoId);
        userService.updateUserInfo(userInfo);
        return JsonResponse.success();
    }
    // 分页查询用户列表（方便用户关注和取消关注操作）  no 页码  ，size 当前这一页展示多少条数据 ，nick昵称可以通过昵称进行模糊查询
    // @RequestParam需要相关的参数的
    @GetMapping("/get-infos")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(
            @RequestParam Integer no , @RequestParam Integer size , String nick){
        Long userId = userSupport.getCurrentUserId();
        JSONObject params = new JSONObject();
        params.put("no",no);
        params.put("size",size);
        params.put("nick",nick);
        params.put("userId",userId);
        // 分页查询用户列表（方便用户关注和取消关注操作)
        PageResult<UserInfo> result = userService.pageListUserInfos(params);
        // 根据关注的用户列表进行判断，判断当前的用户有没有被关注过，如果没关注过才可以关注
        if(result.getTotal() > 0){
            // 检查关注的状态，用户信息的列表，和当前登录的ID
            List<UserInfo> list = userFollowingService.checkFullowingStatus(result.getList(),userId);
            result.setList(list);
        }
        return new JsonResponse<>(result);
    }


}
