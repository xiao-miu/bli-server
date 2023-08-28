package com.bilibil.mapper;

import com.bilibil.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * Date:  2023/8/27
 */
@Mapper
public interface AuthRoleMapper {
    // 角色选取，通过权限角色的唯一编码获取到对应的角色
    AuthRole getRoleByCode(String code);
}
