package com.jzy.community.exception;

/**
 * @author jzy
 * @create 2019-08-14-13:43
 */
public class CustmoizeException extends RuntimeException {
    private String message;

    public CustmoizeException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
