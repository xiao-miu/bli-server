package com.bilibil.service.impl;

import com.bilibil.auth.AuthRoleMenu;
import com.bilibil.mapper.AuthRoleMenuMapper;
import com.bilibil.service.AuthRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Date:  2023/8/24
 */
@Service
public class AuthRoleMenuSerIMPL implements AuthRoleMenuService {
    @Autowired
    private AuthRoleMenuMapper authRoleMenuMapper;
    // 角色页面菜单关联表
    @Override
    public List<AuthRoleMenu> getauthRoleMenus(Set<Long> roleIdSet) {
        return authRoleMenuMapper.getauthRoleMenus(roleIdSet);
    }
}
