package com.jzy.community.controller;

import com.jzy.community.dto.QuestionDTO;
import com.jzy.community.exception.CustmoizeException;
import com.jzy.community.exception.CustomizeErrorCode;
import com.jzy.community.mapper.QuestionMapper;
import com.jzy.community.mapper.UserMapper;
import com.jzy.community.model.Question;
import com.jzy.community.model.User;
import com.jzy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jzy
 * @create 2019-08-08-15:53
 */
@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    //修改问题时把数据库存的信息回显到发布页
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       HttpServletRequest request,
                       Model model){
        //判断当前用户与发布问题的用户是否一致，如果不同抛出异常
        Question dbquestion = questionMapper.selectByPrimaryKey(id);
        User quesUser = userMapper.selectByPrimaryKey(dbquestion.getCreator());
        User currentUser = (User) request.getSession().getAttribute("user");
        if (quesUser.getAccountId() != currentUser.getAccountId()){
            throw new CustmoizeException(CustomizeErrorCode.USER_NOT_QUESUSER);
        }
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());

        return "publish";
    }

    //发布新问题
    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }

    //发布问题或修改问题（前端发布按钮绑定post请求/publish路径），拿到所有的参数并验证，验证成功就发布或修改
    //某个参数验证失败就把错误返回给前端，并把已经前端传的参数回显给页面
    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "description" ,required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            @RequestParam(value = "id") Long id,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        //model.addAttribute("tags", TagCache.get());
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        if (title == null || title==""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if (description == null || description==""){
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }
        if (tag == null || tag==""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }
}