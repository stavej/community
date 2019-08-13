package com.jzy.community.model;

import lombok.Data;

/**
 * @author jzy
 * @create 2019-08-07-18:06
 */
@Data
public class User {

    private Integer id;
    private String name;
    private String accountId;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;
    private String avatarUrl;


}
