package com.bilibil.mapper;

import com.alibaba.fastjson.JSONObject;
import com.bilibil.entity.RefreshTokenDetail;
import com.bilibil.entity.User;
import com.bilibil.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * Date:  2023/8/20
 */
@Mapper
public interface UserMapper {
    // 获取关注的用户信息放到集合
     ArrayList<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);

    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);
    // 查询用户信息
    User getUserById(Long userId);
    // 查询用户详细信息
    UserInfo getUserInfoByUserId(Long userId);

    void updateUser(User user);

    void updateUserInfo(UserInfo userInfo);
    // 分页查询用户列表（方便用户关注和取消关注操作） 查询出总数量
    Integer pageCountUserInfos(Map<String,Object> params);
    // 分页查询出用户的数据
    List<UserInfo> pageListUserInfos(JSONObject params);
    // 删除刷新token
    void deleteRefreshToken(@Param("refreshToken") String refreshToken, @Param("userId") Long userId);
    // 新增刷新Token
    void addRefreshToken(@Param("refreshToken") String refreshToken, @Param("userId") Long userId, @Param("createTime") Date createTime);
    //获取到刷新之后的AEStoken
    RefreshTokenDetail getRefreshTokenDetail(String refreshToken);
}
