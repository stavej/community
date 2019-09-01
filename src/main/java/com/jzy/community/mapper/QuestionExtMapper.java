package com.jzy.community.mapper;

import com.jzy.community.dto.QuestionQueryDTO;
import com.jzy.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record);
    int incCommentCount(Question record);
    List<Question> selectRelated(Question question);
    Integer getTotalCount(QuestionQueryDTO questionQueryDTO);

    List<Question> selectAllOrSearchQusetion(QuestionQueryDTO questionQueryDTO);
}
