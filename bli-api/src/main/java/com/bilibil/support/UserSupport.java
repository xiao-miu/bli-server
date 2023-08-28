package com.bilibil.support;

import com.bilibil.exception.ConditionException;
import com.bilibil.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * Date:  2023/8/20
 * 全局支持的功能包
 */
//依赖注入
@Component
public class UserSupport {
    // 获取用户id
    public Long getCurrentUserId() {
        // 对前端获取的请求进行抓取
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 获取用户的令牌
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader("token");
        Long userId = TokenUtil.verifyToken(token);
        // 判断是否有非法用户
        if  (userId < 0){
            throw new ConditionException("非法用户!");
        }
        return userId;
    }
}
