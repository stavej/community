package com.jzy.community.controller;

import com.jzy.community.dto.CommentCreateDTO;
import com.jzy.community.dto.CommentDTO;
import com.jzy.community.dto.ResultDTO;
import com.jzy.community.enums.CommentTypeEnum;
import com.jzy.community.exception.CustomizeErrorCode;
import com.jzy.community.model.Comment;
import com.jzy.community.model.User;
import com.jzy.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jzy
 * @create 2019-08-19-16:03
 */
@Controller
public class commentController {

    @Autowired
    private CommentService commentService;


    //写入回复
    @ResponseBody
    @RequestMapping(value = "/comment" ,method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDto,
                       HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");

        if (user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if (commentCreateDto == null || StringUtils.isBlank(commentCreateDto.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentCreateDto.getParentId());
        comment.setContent(commentCreateDto.getContent());
        comment.setType(commentCreateDto.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }


    //获取二级评论
    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }
}
