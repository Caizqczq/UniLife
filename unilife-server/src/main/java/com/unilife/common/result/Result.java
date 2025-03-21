package com.unilife.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T>{
    /***
     * 状态码
     */
    private Integer code;
    /***
     * 消息
     */
    private String message;
    /***
     *数据
     */
    private T data;

    /***
     *
     * @param data 数据
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T>Result<T>success(T data){
        return new Result<T>(200, "success", data);
    }

    /**
     * 成功返回结果
     * @return 结果对象
     */
    public static Result<Void> success() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(200, message, null);
    }

    /**
     * 失败返回结果
     * @param code 状态码
     * @param message 消息
     * @return 结果对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(T data,String message){
        return new Result<>(200,message,null);
    }
}
