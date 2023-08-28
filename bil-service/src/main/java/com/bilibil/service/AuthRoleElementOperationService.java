package com.bilibil.service;

import com.bilibil.auth.AuthRoleElementOperation;

import java.util.List;
import java.util.Set;

/**
 * Date:  2023/8/24
 * 页面元素操作表
 */
public interface AuthRoleElementOperationService {
    // 获取用户页面元素的操作权限
    List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdList);
}
