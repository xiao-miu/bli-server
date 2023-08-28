package com.bilibil.service;

import com.bilibil.auth.AuthRole;
import com.bilibil.auth.AuthRoleElementOperation;
import com.bilibil.auth.AuthRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * Date:  2023/8/24
 */
public interface AuthRoleService {
    // 获取用户页面元素的操作权限
    List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdList);
    // 角色页面菜单关联表
    List<AuthRoleMenu> getauthRoleMenus(Set<Long> roleIdSet);
    // 角色选取，通过权限角色的唯一编码获取到对应的角色
    AuthRole getRoleByCode(String roleLv0);
}
