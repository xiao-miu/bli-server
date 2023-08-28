package com.bilibil.handler;

import com.bilibil.entity.JsonResponse;
import com.bilibil.exception.ConditionException;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Date:  2023/8/19
 * @author 1
 * 全局异常处理
 */
@ControllerAdvice
// 最高优先级
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonGlobalExceptionHandler {
    // 表示异常处理器
    @ExceptionHandler(value = Exception.class)
    // 返回参数     HttpServletRequest 封装前端获取到的请求
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request , Exception e) {
        //错误信息
        String errorMessage = e.getMessage();
        // 主动抛出异常，如果信息不匹配返回前端通通用状态码
        if(e instanceof ConditionException){
            String errorCode = ((ConditionException) e).getCode();
            return new JsonResponse<>(errorCode,errorMessage);
        }else {
            // 通用异常处理
            return new JsonResponse<>("500",errorMessage);
        }
    }
}
