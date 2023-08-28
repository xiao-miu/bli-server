package com.bilibil.service;

import com.bilibil.auth.UserAuthorities;

/**
 * Date:  2023/8/24
 */
public interface UserAuthService {
    // 获取用户权限
    UserAuthorities getUserAuthorities(Long userId);
    // 添加默认权限角色
    void addUserDefaultRole(Long id);
}
