package com.jzy.community.exception;

/**
 * @author jzy
 * @create 2019-08-20-10:49
 */
public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUNG(2001,"你找的问题不存在，要不要换个试试？"),
    TARGET_PARAM_NOT_FOUNG(2002,"未选中任何问题或评论进行回复"),
    NO_LOGIN(2003,"当前操作需要登录，请登录后重试"),
    SYS_ERROR(2004,"服务冒烟了，要不然你稍后再试试！！！"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"回复的评论不在了，要不要换个试试？"),
    ;


    @Override
    public String getMassage() {
        return massage;
    }

    @Override
    public Integer getCode(){
        return code;
    }

    private Integer code;
    private String massage;


    CustomizeErrorCode(Integer code, String massage) {
        this.code = code;
        this.massage = massage;
    }
}
