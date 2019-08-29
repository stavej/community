package com.jzy.community.dto;

import com.jzy.community.model.User;
import lombok.Data;

/**
 * @author jzy
 * @create 2019-08-28-10:46
 */
@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;//阅读状态
    private String notifier;//发起通知的user name
    private String outerTitle;//发起通知的question或comment的标题或内容
    private String type;//通知类型（回复问题，回复评论等）
    private Long questionId;//链接的问题ID


}
