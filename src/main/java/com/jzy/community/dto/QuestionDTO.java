package com.jzy.community.dto;

import com.jzy.community.model.User;
import lombok.Data;

/**
 * @author jzy
 * @create 2019-08-10-13:38
 */
@Data
public class QuestionDTO {

    private Long id;
    private  String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private  Long creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private User user;
}
