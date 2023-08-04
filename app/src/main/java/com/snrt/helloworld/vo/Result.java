package com.snrt.helloworld.vo;

public class Result <T>{
    // 标识代码,2000表示成功，其它数值表示出错
    private Integer status;
    // 返回的数据
    private T data;
    // 提示信息,供报错时使用
    private String error;
    // 返回时间
    private Integer errcode;
    private String timestamp;
    // 有参构造
    public Result(Integer status, Integer errcode, String error, T data) {
        this.status = status;
        this.errcode = errcode;
        this.error = error;
        this.data = data;
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }

    public Result(T data) {
        this.data = data;
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }

    // sucess、error两个方法方便Result的使用
    public static <T> Result<T> success(T data) {
        return new Result( data);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
