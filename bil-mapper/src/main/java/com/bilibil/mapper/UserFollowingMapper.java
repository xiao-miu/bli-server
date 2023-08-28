package com.bilibil.mapper;

import com.bilibil.entity.FollowingGroup;
import com.bilibil.entity.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Date:  2023/8/21
 * 用户关注分组表
 */
@Mapper
public interface UserFollowingMapper {


    // 删除用户的关联关系
    // @Param根据这个注解识别到指定的字段名称
    Integer deleteUserFollowing(@Param("userId") Long userId,@Param("followingId") Long followingId);
    //添加关注的用户
    void adduserFollowings(UserFollowing userFollowing);
    // 获取用户信息
    List<UserFollowing> getUserFollowings(Long userId);
    // 获取当前用户的粉丝列表
    List<UserFollowing> getUserFans(Long userId);

}
