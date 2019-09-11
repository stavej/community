package com.jzy.community.controller;

import com.jzy.community.dto.PaginationDTO;
import com.jzy.community.service.NotificationService;
import com.jzy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
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



    @Autowired
    private QuestionService questionService;

    @Autowired
    NotificationService notificationService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                        @RequestParam(name = "search",required = false) String search
                        ){

        PaginationDTO pagination = questionService.list(search,page,size);
        model.addAttribute("pagination",pagination);
        return "index";

    }
}
