package com.bilibil.service;

import com.bilibil.auth.AuthRole;
import com.bilibil.auth.AuthRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * Date:  2023/8/24
 */
public interface AuthRoleMenuService {
    // 角色页面菜单关联表
    List<AuthRoleMenu> getauthRoleMenus(Set<Long> roleIdSet);

}
