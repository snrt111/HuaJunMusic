package com.snrt.helloworld.vo;

/**
 * 通用API响应包装类
 * 统一处理API返回的数据结构
 *
 * @param <T> 响应数据类型
 * @author HuaJun Music Team
 * @version 1.0
 */
public class ApiResponse<T> {

    // 状态码
    private Integer code;

    // 响应消息
    private String message;

    // 响应数据
    private T data;

    // 是否成功
    private Boolean success;

    public ApiResponse() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * 判断请求是否成功
     *
     * @return true表示成功
     */
    public boolean isSuccess() {
        return success != null && success && code != null && code == 200;
    }

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setSuccess(true);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    /**
     * 创建失败响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(500);
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    /**
     * 创建失败响应（带状态码）
     *
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse对象
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
