package com.bilibil.service.impl;

import com.bilibil.auth.*;
import com.bilibil.constant.AuthRoleConstant;
import com.bilibil.mapper.UserAuthoritiesMapper;
import com.bilibil.service.AuthRoleService;
import com.bilibil.service.UserAuthService;
import com.bilibil.service.UserRoleStrvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date:  2023/8/24、
 * 用户权限 
 */
@Service
public class UserAuthSerINPL implements UserAuthService {
    // 用户权限表
    @Autowired
    private UserAuthoritiesMapper userAuthoritiesMapper;
    // 用户角色关联表
    @Autowired
    private UserRoleStrvice userRoleStrvice;
    // 权限控制--角色表
    @Autowired
    private AuthRoleService authRoleService;
    // 获取用户权限
    @Override
    public UserAuthorities getUserAuthorities(Long userId) {
        // 一个用户多个角色, 通过用户ID来查询用户的角色
        List<UserRole> userRolesList = userRoleStrvice.getUserRolesByuserId(userId);
        // 把角色的ID查出来
        Set<Long> roleIdSet = userRolesList.stream().map(UserRole :: getRoleId)
                .collect(Collectors.toSet());
        // 根据角色ID查询权限
        // 获取用户页面元素的操作权限
        List<AuthRoleElementOperation> roleElementOperationList =
                authRoleService.getRoleElementOperationsByRoleIds(roleIdSet);
        // 角色页面菜单关联表
        List<AuthRoleMenu> authRoleMenuList = authRoleService.getauthRoleMenus(roleIdSet);
        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleMenuList(authRoleMenuList);
        userAuthorities.setRoleElementOperationList(roleElementOperationList);
        return userAuthorities;
    }
    // 添加默认权限角色
    @Override
    public void addUserDefaultRole(Long id) {
        // 添加用户和角色的关联
        UserRole userRole = new UserRole();
        // 角色选取，通过权限角色的唯一编码获取到对应的角色
        AuthRole authRole = authRoleService.getRoleByCode(AuthRoleConstant.ROLE_LV0);
        userRole.setUserId(id);
        userRole.setRoleId(authRole.getId());
        // 添加进数据库
        userRoleStrvice.addUserRole(userRole);
    }
}
