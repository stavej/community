package com.jzy.community.dto;

import lombok.Data;

/**
 * @author jzy
 * @create 2019-08-06-15:49
 */
@Data
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;


}
