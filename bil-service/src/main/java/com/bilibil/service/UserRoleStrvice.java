package com.bilibil.service;

import com.bilibil.auth.UserRole;

import java.util.List;

/**
 * Date:  2023/8/24
 */
public interface UserRoleStrvice {
    // 通过用户ID来查询用户的角色
    List<UserRole> getUserRolesByuserId(Long userId);
    // 添加进数据库
    void addUserRole(UserRole userRole);
}
