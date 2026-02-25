package com.nicat.storebonus.dtos.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private int code;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(boolean success, String message, int code, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
        this.timestamp = timestamp;
    }

    public static <T> ApiResponse<T> success(T data, ResponseMessage resp) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(resp.getCode())
                .message(resp.getMessage())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }


    public static <T> ApiResponse<T> error(ResponseMessage resp) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(resp.getCode())
                .message(resp.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}