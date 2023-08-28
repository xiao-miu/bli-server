package com.bilibil.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bilibil.constant.UserConstant;
import com.bilibil.entity.PageResult;
import com.bilibil.entity.RefreshTokenDetail;
import com.bilibil.entity.User;
import com.bilibil.entity.UserInfo;
import com.bilibil.exception.ConditionException;
import com.bilibil.mapper.UserMapper;
import com.bilibil.service.UserAuthService;
import com.bilibil.service.UserService;
import com.bilibil.util.MD5Util;
import com.bilibil.util.RSAUtil;
import com.bilibil.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Date:  2023/8/20
 */
@Service
public class UserSerIMPL implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAuthService userAuthService;
    @Override
    // user是前端获取到的
    public void addUser(User user) {
        // 先获取手机号
        String phone = user.getPhone();
        // 判断是空还是null
        if(StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空");
        }
        // 这是从数据库查出的手机号
        User userPhone = this.getUserPhone(phone);
        if(userPhone != null){
            throw new ConditionException("手机号已注册");
        }
        // 先获取当前系统时间
        Date time = new Date();
        // 生成盐值
        String salt = String.valueOf(time.getTime());
        // 获取前端传过来的密码(是被前端加密后传过来的)
        String password = user.getPassword();
        // 进行解密
        String rawPassword;
        try {
            // 解密后的密码
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        // 把密码加密MD5,存入数据库
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(time);
        userMapper.addUser(user);
        // 添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        // 设置初始昵称
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_FEMALE);
        userInfo.setCreateTime(time);
        // 添加进数据库
        userMapper.addUserInfo(userInfo);
        // 添加默认权限角色
        userAuthService.addUserDefaultRole(user.getId());
    }

    @Override
    public String login(User user) throws Exception {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空!");
        }
        // 根据手机号获取用户信息
        User dbUser = this.getUserPhone(phone);
        if(dbUser == null){
            throw new ConditionException("当前用户不存在!");
        }
        String password = user.getPassword();
        // 解密密码
        String rawPassword;
        try {
            // 解密后的密码
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败!");
        }
        // 把密码加密MD5
        // 获取用户盐值
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        // 比对是否一致
        if(!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误!");
        }
        // 生成用户令牌返给前端

        return TokenUtil.generateToken(dbUser.getId());
    }
    // 获取用户信息
    @Override
    public User getUserInfo(Long userId) {
         // 先查一下有没有跟这个id绑定的用户
        User user = userMapper.getUserById(userId);
        UserInfo userInfo = userMapper.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }
    // 更新用户信息
    @Override
    public void updateUsers(User user) {
         user.setUpdateTime(new Date());
         userMapper.updateUser(user);
    }
    // 更新用户基本信息
    @Override
    public void updateUserInfo(UserInfo userInfo) {
        userInfo.setCreateTime(new Date());
        userMapper.updateUserInfo(userInfo);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }
    // 获取关注的用户信息放到集合
    @Override
    public ArrayList<UserInfo> getUserInfoByUserIds(Set<Long> userId) {
        return userMapper.getUserInfoByUserIds(userId);
    }
    // 分页查询用户列表（方便用户关注和取消关注操作）
    @Override
    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        // 当前分页页码
        Integer no = params.getInteger("no");
        // 这一页展示多少条数据
        Integer size = params.getInteger("size");
        //   start开始  起始页码
        params.put("start",(no-1)*size);
        // 每次查询多少条数据
        params.put("limit",size);
        // total 总页数 根据模糊姓名查总数
        Integer totol = userMapper.pageCountUserInfos(params);
        List<UserInfo> list = new ArrayList<>();
        if(totol > 0){
            // 分页查询
            list = userMapper.pageListUserInfos(params);
        }
        return new PageResult<>(totol, list);
    }
    //双token登录  AES 双加密，AES接入toke
    @Override
    public Map<String, Object> loginForDts(User user) throws Exception {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空!");
        }
        // 根据手机号获取用户信息
        User dbUser = this.getUserPhone(phone);
        if(dbUser == null){
            throw new ConditionException("当前用户不存在!");
        }
        String password = user.getPassword();
        // 解密密码
        String rawPassword;
        try {
            // 解密后的密码
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败!");
        }
        // 把密码加密MD5
        // 获取用户盐值
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        // 比对是否一致
        if(!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误!");
        }
        Long userId = dbUser.getId();
        // 生成用户令牌返给前端
        // 接入的token
        String accessToken = TokenUtil.generateToken(userId);
        // 刷新token
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        // 将refreshToken和UserID保存到数据库里 ， 方便后续延迟刷新
        // 先删除再新建
        userMapper.deleteRefreshToken(refreshToken,userId);
        userMapper.addRefreshToken(refreshToken,userId,new Date());
        Map<String,Object> rssult = new HashMap<>();
        rssult.put("accessToken",accessToken);
        rssult.put("refreshToken",refreshToken);
        return rssult;
    }
    // 退出登录
    @Override
    public void logout(Long userId, String refreshToken) {
        userMapper.deleteRefreshToken(refreshToken, userId);
    }
    // 获取到刷新之后的AEStoken
    @Override
    public String refreshAccessToken(String refreshToken) throws Exception {
        RefreshTokenDetail refreshTokenDetail = userMapper.getRefreshTokenDetail(refreshToken);
        if(refreshTokenDetail == null) {
            throw new ConditionException("555","token过期！");
        }
        Long userId = refreshTokenDetail.getUserId();
        // 获取userid 是为了获取 rat - token
        return TokenUtil.generateToken(userId);

    }

    // 判断手机号有没有已经注册了
    public User getUserPhone(String phone){
        return userMapper.getUserByPhone(phone);
    }
}