package com.bilibil.entity;

/**
 * Date:  2023/8/19
 */
public class JsonResponse<T> {
    // 返回的状态码
    private String code;
    // 返回的提示语
    private String msg;
    // 返回的类型
    private T data;

    public JsonResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public JsonResponse(T data) {
        this.data = data;
        // 设置固定的初始化赋值
        msg = "成功";
        code = "0";
    }

    // 请求成功
    public static JsonResponse<String> success(){
        //不需要返回前端但是请求成功的场景
        return new JsonResponse<>(null);
    }
    public static JsonResponse<String> success(String data){
        //不需要返回前端但是请求成功的场景
        return new JsonResponse<>(data);
    }
    //请求失败
    public static JsonResponse<String> fail(){
        //不需要返回前端但是请求成功的场景
        return new JsonResponse<>("1","失败");
    }
    public static JsonResponse<String> fail(String code,String msg){
        return new JsonResponse<>(code,msg);
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

    /**
     * 获取
     * @return msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取
     * @return data
     */
    public T getData() {
        return data;
    }

    /**
     * 设置
     * @param data
     */
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JsonResponse{code = " + code + ", msg = " + msg + ", data = " + data + "}";
    }
}
