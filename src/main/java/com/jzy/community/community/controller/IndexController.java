package com.jzy.community.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author jzy
 * @create 2019-08-06-9:35
 */
@Controller
public class IndexController {
    @GetMapping("/")
    public String index(){

       return "index";
    }
}
