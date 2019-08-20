package com.jzy.community.dto;

import lombok.Data;

/**
 * @author jzy
 * @create 2019-08-20-8:44
 */
@Data
public class CommentDto {
    private Long parentId;
    private String content;
    private Integer type;
}
