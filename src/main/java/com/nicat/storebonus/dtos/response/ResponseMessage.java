package com.nicat.storebonus.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ResponseMessage {
    // Success codes (example, between the 1000-1999)
    SUCCESS_FETCH(1000, "Data fetched successfully"),
    SUCCESS_CREATE(1001, "Data created successfully"),

    // Error codes (example, between the 4000-4999)
    NOT_FOUND(4004, "Resource not found"),
    ALREADY_EXISTS(4009, "Resource already exists"),
    INTERNAL_ERROR(5000, "An unexpected system error occurred");

    private final String message;
    private final int code;

    ResponseMessage(int code, String message) {
        this.message = message;
        this.code = code;
    }
}