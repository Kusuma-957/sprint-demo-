package com.hotelManagement.system.exception;


public class BadRequestException extends BaseAppException {
    public BadRequestException(String message) {
        super(ApiCode.BADREQUEST, message);
    }
}
