package com.jzy.community.dto;

import com.jzy.community.model.User;
import lombok.Data;

/**
 * @author jzy
 * @create 2019-08-26-8:15
 */
@Data
public class CommentDTO {

    private Long id;

    private Long parentId;

    private Integer type;

    private Long commentator;

    private Long gmtCreate;

    private Long gmtModified;

    private Long likeCount;

    private String content;

    private User user;

    private Integer commentCount;
}
