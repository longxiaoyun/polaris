package com.unbiased.common.exception;

public class CustomUnauthorizedException extends RuntimeException {
    public CustomUnauthorizedException(String msg){
        super(msg);
    }

    public CustomUnauthorizedException() {
        super();
    }
}
