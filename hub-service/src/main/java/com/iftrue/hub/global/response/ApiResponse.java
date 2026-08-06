package com.iftrue.hub.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Getter
public class ApiResponse<T> {

    private final OffsetDateTime timestamp;
    private final int status;
    private final String code;
    private final T data;

    public ApiResponse(HttpStatus status, T data) {
        this.timestamp = OffsetDateTime.now();
        this.status = status.value();
        this.code = "success";
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(HttpStatus.CREATED, data);
    }
}
