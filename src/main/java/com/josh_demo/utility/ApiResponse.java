package com.josh_demo.utility;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private T data;

    public ApiResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
    public ApiResponse(ApiHttpStatus status, T data) {
        this.statusCode = status.getCode();
        this.message = status.getMessage();
        this.data = data;
    }
    public static <T> ApiResponse<T> success(ApiHttpStatus status, T data) {
        return new ApiResponse<>(status.getCode(), status.getMessage(), data);
    }
    public static <T> ApiResponse<T> error(ApiHttpStatus status, String message) {
        return new ApiResponse<>(status.getCode(), message, null);
    }
}

