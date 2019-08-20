package com.jzy.community.mapper;

import com.jzy.community.model.Question;

public interface QuestionExtMapper {
    int incView(Question record);
    int incCommentCount(Question record);

}
