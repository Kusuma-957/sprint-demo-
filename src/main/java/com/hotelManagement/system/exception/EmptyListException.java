package com.hotelManagement.system.exception;


public class EmptyListException extends BaseAppException {
    public EmptyListException(String message) {
        super(ApiCode.GETFAILS, message);
    }
}
