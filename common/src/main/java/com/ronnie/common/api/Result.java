package com.ronnie.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    private  T data;
    private  Integer code;
    private  String msg;

    public static <T> Result<T> success(T data) {
        return new Result<>(data, null, null);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(null, code, msg);
    }

    public static boolean isSuccess(Result<?> result) {
        return result != null && result.code == null;
    }
}
