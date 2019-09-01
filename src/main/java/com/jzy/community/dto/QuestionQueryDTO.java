package com.jzy.community.dto;

import lombok.Data;

/**
 * @author jzy
 * @create 2019-09-01-1:02
 */
@Data
public class QuestionQueryDTO {
    String search;
    Integer offset;
    Integer size;
}
