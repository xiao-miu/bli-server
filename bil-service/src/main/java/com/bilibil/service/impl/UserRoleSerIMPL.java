package com.bilibil.service.impl;

import com.bilibil.auth.UserRole;
import com.bilibil.mapper.UserRoleMapper;
import com.bilibil.service.UserRoleStrvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Date:  2023/8/24
 */
@Service
public class UserRoleSerIMPL implements UserRoleStrvice {
    @Autowired
    private UserRoleMapper userRoleMapper;
    // 通过用户ID来查询用户的角色
    @Override
    public List<UserRole> getUserRolesByuserId(Long userId) {

        return userRoleMapper.getUserRolesByuserId(userId);
    }
    // 添加进数据库
    @Override
    public void addUserRole(UserRole userRole) {
        userRole.setCreateTime(new Date());
        userRoleMapper.addUserRole(userRole);
    }
}
