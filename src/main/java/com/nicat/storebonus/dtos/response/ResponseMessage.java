package com.nicat.storebonus.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ResponseMessage {
    // Success codes (example, between the 1000-1999)
    SUCCESS_FETCH(1000, "Data fetched successfully"),
    SUCCESS_CREATE(1001, "Data created successfully"),
    SUCCESS_CALCULATED(1002, "Grade successfully calculated"),


    // Error codes (example, between the 2000-3000)
    NOT_FOUND(2000, "Resource not found"),
    ALREADY_EXISTS(2001, "Resource already exists"),
    INTERNAL_ERROR(2002, "An unexpected system error occurred"),
    TARGET_NOT_REACHED(2003, "Market was not reached to target"),

    //Validation error
    VALIDATION_ERROR(6000, "Validation failed");


    private final String message;
    private final int code;

    ResponseMessage(int code, String message) {
        this.message = message;
        this.code = code;
    }
}