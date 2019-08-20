package com.jzy.community.exception;

/**
 * @author jzy
 * @create 2019-08-14-13:43
 */
public class CustmoizeException extends RuntimeException {
    private String message;
    private Integer code;

    public CustmoizeException(ICustomizeErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMassage();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
