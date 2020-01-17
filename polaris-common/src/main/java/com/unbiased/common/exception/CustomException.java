package com.unbiased.common.exception;

/**
 * @author longjiang
 */
public class CustomException extends RuntimeException {
    public CustomException(String msg){
        super(msg);
    }

    public CustomException() {
        super();
    }
}
