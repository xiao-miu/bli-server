package com.bilibil.aspect;

import com.bilibil.auth.UserRole;
import com.bilibil.entity.annotation.ApiLimitedRole;
import com.bilibil.exception.ConditionException;
import com.bilibil.service.UserRoleStrvice;
import com.bilibil.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date:  2023/8/26
 * 切面
 */
// 优先级
@Order(1)
// 标识是一个切面
@Aspect
@Component
public class AaiLimitedRoleAspect {
    // 查询用户身份   ，  所以需要用户ID
    @Autowired
    private UserSupport userSupport;
    // 获取用户当前关联的角色
    @Autowired
    private UserRoleStrvice userRoleStrvice;

    // 相关的切点，位置
    @Pointcut("@annotation(com.bilibil.entity.annotation.ApiLimitedRole)")
    public void check(){
    }
    // 切入切点之后的处理
    // 获取到限制的角色
    // 切面中的节点     apiLimitedRole 这个名称随意指定
    @Before("check() && @annotation(apiLimitedRole)")
    // JoinPoint 切入的时候相关的参数    ，    以及注解
    public void doBefore(JoinPoint joinPoint , ApiLimitedRole apiLimitedRole) {
        // 角色比对
        Long userId = userSupport.getCurrentUserId();
        // 获取当前用户角色列表
        List<UserRole> userRoleList = userRoleStrvice.getUserRolesByuserId(userId);
        // 拿到要限制的角色
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        // 进行比对
        // set 没有相同的元素
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList)
                .collect(Collectors.toSet());
        // 把角色抽取出来
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        // 取两个Set的交集
        roleCodeSet.retainAll(limitedRoleCodeSet);
        // 若果roleCodeSet  就没有权限调用
        if(roleCodeSet.size() > 0){
            throw new ConditionException("权限不足");
        }
    }

}
