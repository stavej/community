package com.jzy.community.controller;

import com.jzy.community.dto.PaginationDTO;
import com.jzy.community.model.User;
import com.jzy.community.service.NotificationService;
import com.jzy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

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
                        @RequestParam(name = "page",defaultValue = "1")Integer page,
                        @RequestParam(name = "size",defaultValue = "6")Integer size,
                        HttpServletRequest request){

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }
        PaginationDTO pagination = questionService.list(page,size);
        Long unreadCount = notificationService.unreadCount(user.getId());
        model.addAttribute("unreadCount",unreadCount);
        model.addAttribute("pagination",pagination);
        return "index";

    }
}
