package com.bilibil.service.impl;

import com.bilibil.auth.AuthRole;
import com.bilibil.auth.AuthRoleElementOperation;
import com.bilibil.auth.AuthRoleMenu;
import com.bilibil.mapper.AuthRoleMapper;
import com.bilibil.service.AuthRoleElementOperationService;
import com.bilibil.service.AuthRoleMenuService;
import com.bilibil.service.AuthRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Date:  2023/8/24
 */
@Service
public class AuthRoleSerIMPL implements AuthRoleService {
    // 权限控制--页面元素操作
    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;
    // 页面菜单管理权限
    @Autowired
    private AuthRoleMenuService authRoleMenuService;
    // 角色表
    @Autowired
    private AuthRoleMapper authRoleMapper;


    // 获取用户页面元素的操作权限
    @Override
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdList) {

        return  authRoleElementOperationService.getRoleElementOperationsByRoleIds(roleIdList);
    }
    // 角色页面菜单关联表
    @Override
    public List<AuthRoleMenu> getauthRoleMenus(Set<Long> roleIdSet) {
        return authRoleMenuService.getauthRoleMenus(roleIdSet);
    }
    // 角色选取，通过权限角色的唯一编码获取到对应的角色
    @Override
    public AuthRole getRoleByCode(String code) {
        return authRoleMapper.getRoleByCode(code);
    }
}
