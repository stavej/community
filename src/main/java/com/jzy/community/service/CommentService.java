package com.jzy.community.service;

import com.jzy.community.enums.CommentTypeEnum;
import com.jzy.community.exception.CustmoizeException;
import com.jzy.community.exception.CustomizeErrorCode;
import com.jzy.community.mapper.CommentMapper;
import com.jzy.community.mapper.QuestionExtMapper;
import com.jzy.community.mapper.QuestionMapper;
import com.jzy.community.model.Comment;
import com.jzy.community.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jzy
 * @create 2019-08-20-9:41
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    //开启事务

    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustmoizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUNG);
        }

        if (comment.getType()==null|| !CommentTypeEnum.isExist(comment.getType())){
            throw new CustmoizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null){
                throw new CustmoizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        }else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null){
                throw new CustmoizeException(CustomizeErrorCode.QUESTION_NOT_FOUNG);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }
    }
}
