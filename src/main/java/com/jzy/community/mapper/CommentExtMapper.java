package com.jzy.community.mapper;

import com.jzy.community.model.Comment;
import com.jzy.community.model.CommentExample;
import com.jzy.community.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}