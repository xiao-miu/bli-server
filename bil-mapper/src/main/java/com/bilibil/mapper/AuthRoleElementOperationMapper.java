package com.bilibil.mapper;

import com.bilibil.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * Date:  2023/8/26
 */
@Mapper
public interface AuthRoleElementOperationMapper {
    // 获取用户页面元素的操作权限
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdList);
}
