package com.bilibil.entity.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Date:  2023/8/26
 */
// 在运行阶段
@Retention(RetentionPolicy.RUNTIME)
// 目标，    目标都是放在方法上的
@Target({ElementType.METHOD})
@Documented
@Component
public @interface ApiLimitedRole {
    // 要限制的角色
    String[] limitedRoleCodeList() default {};
}
