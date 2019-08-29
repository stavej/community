package com.jzy.community.enums;

/**
 * @author jzy
 * @create 2019-08-28-9:50
 */
public enum NotificationStatusEnum {
    UNREAD(0),READ(1);
    private int status;

    public int getStatus() {
        return status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}
