package com.jzy.community.controller;

import com.jzy.community.dto.NotificationDTO;
import com.jzy.community.model.User;
import com.jzy.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jzy
 * @create 2019-08-30-0:19
 */
@Controller
public class NoticationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notication/{id}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "id") Long id) {
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        NotificationDTO notificationDTO = notificationService.read(id,user);

        return "redirect:/question/"+notificationDTO.getQuestionId();
    }
}
