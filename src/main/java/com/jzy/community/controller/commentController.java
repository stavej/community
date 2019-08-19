package com.jzy.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author jzy
 * @create 2019-08-19-16:03
 */
@Controller
public class commentController {

    @RequestMapping(value = "/comment" ,method = RequestMethod.POST)
    public Object post(){
        return "index";
    }
}
