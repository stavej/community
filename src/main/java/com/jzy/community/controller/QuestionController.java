package com.jzy.community.controller;

import com.jzy.community.dto.CommentDTO;
import com.jzy.community.dto.QuestionDTO;
import com.jzy.community.enums.CommentTypeEnum;
import com.jzy.community.model.Question;
import com.jzy.community.service.CommentService;
import com.jzy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author jzy
 * @create 2019-08-12-17:02
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id, Model model){
        //累加阅读数
        questionService.incView(id);
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        QuestionDTO questionDTO = questionService.getById(id);
        List<QuestionDTO> relateQuestions = questionService.selectRelated(questionDTO);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        model.addAttribute("relateQuestions",relateQuestions);

        return "question";
    }
    

}
