package com.jzy.community.dto;

import lombok.Data;

import java.util.List;

/**
 * @author jzy
 * @create 2019-08-27-22:33
 */
@Data
public class TagDTO {
    private String categoryName;
    private List<String> tags;
}
