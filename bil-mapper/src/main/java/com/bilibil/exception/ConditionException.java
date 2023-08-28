package com.bilibil.exception;


/**
 * Date:  2023/8/19
 * @author 1\
 * 条件异常
 */
public class ConditionException extends RuntimeException{
    // 版本号
    private static final long serialVersionUID = 1L;
    // 响应状态码
    private String code;


    public ConditionException(String name) {
        super(name);
    }

    public ConditionException(String name , String code) {
        super(name);
        this.code = code;
    }

    /**
     * 获取
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

}
