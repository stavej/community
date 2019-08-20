package com.jzy.community.controller;

import com.jzy.community.dto.CommentDto;
import com.jzy.community.dto.ResultDTO;
import com.jzy.community.exception.CustomizeErrorCode;
import com.jzy.community.mapper.CommentMapper;
import com.jzy.community.model.Comment;
import com.jzy.community.model.User;
import com.jzy.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jzy
 * @create 2019-08-19-16:03
 */
@Controller
public class commentController {

    @Autowired
    private CommentService commentService;


    @ResponseBody
    @RequestMapping(value = "/comment" ,method = RequestMethod.POST)
    public Object post(@RequestBody CommentDto commentDto,
                       HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        Comment comment = new Comment();
        comment.setParentId(commentDto.getParentId());
        comment.setContent(commentDto.getContent());
        comment.setType(commentDto.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }
}
