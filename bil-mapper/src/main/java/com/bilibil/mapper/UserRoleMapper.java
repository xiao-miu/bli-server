package com.bilibil.mapper;

import com.bilibil.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Date:  2023/8/24
 */
@Mapper
public interface UserRoleMapper {
    // 通过用户ID来查询用户的角色
    List<UserRole> getUserRolesByuserId(Long userId);
    // 添加进数据库
    void addUserRole(UserRole userRole);
}
