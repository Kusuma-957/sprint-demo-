package com.hotelManagement.system.exception;

import lombok.Getter;

@Getter
public abstract class BaseAppException extends RuntimeException {
    private final ApiCode code;

    protected BaseAppException(ApiCode code, String message) {
        super(message);
        this.code = code;
    }
}
