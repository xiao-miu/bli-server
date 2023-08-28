package com.bilibil.mapper;

import com.bilibil.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * Date:  2023/8/26
 * // 角色页面菜单关联表
 */
@Mapper
public interface AuthRoleMenuMapper {


    List<AuthRoleMenu> getauthRoleMenus(Set<Long> roleIdSet);
}
