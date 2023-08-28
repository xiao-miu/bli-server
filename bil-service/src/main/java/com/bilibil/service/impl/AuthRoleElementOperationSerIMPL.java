package com.bilibil.service.impl;

import com.bilibil.auth.AuthRoleElementOperation;
import com.bilibil.mapper.AuthRoleElementOperationMapper;
import com.bilibil.service.AuthRoleElementOperationService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Date:  2023/8/24
 *
 */
@Service
public class AuthRoleElementOperationSerIMPL implements AuthRoleElementOperationService {
    @Autowired
    private AuthRoleElementOperationMapper authRoleElementOperationMapper;
    // 因为是个列表所以指定一下参数的名称 @Param("roleIdList")
    // 获取用户页面元素的操作权限
    @Override
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(@Param("roleIdList") Set<Long> roleIdList) {
        return authRoleElementOperationMapper.getRoleElementOperationsByRoleIds(roleIdList);
    }
}
