package com.hotelManagement.system.exception;


public class ConflictException extends BaseAppException {
    public ConflictException(String message) {
        super(ApiCode.ADDFAILS, message);
    }
}
