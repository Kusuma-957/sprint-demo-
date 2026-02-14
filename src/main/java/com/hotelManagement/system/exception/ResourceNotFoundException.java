package com.hotelManagement.system.exception;

public class ResourceNotFoundException extends BaseAppException {
    public ResourceNotFoundException(String message) {
        super(ApiCode.GETFAILS, message); // will override per use where needed
    }
}
