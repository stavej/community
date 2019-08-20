package com.jzy.community.service;

import com.jzy.community.dto.PaginationDTO;
import com.jzy.community.dto.QuestionDTO;
import com.jzy.community.exception.CustmoizeException;
import com.jzy.community.exception.CustomizeErrorCode;
import com.jzy.community.mapper.QuestionExtMapper;
import com.jzy.community.mapper.QuestionMapper;
import com.jzy.community.mapper.UserMapper;
import com.jzy.community.model.Question;
import com.jzy.community.model.QuestionExample;
import com.jzy.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jzy
 * @create 2019-08-10-13:41
 */
@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;


    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalcount = (int)questionMapper.countByExample(new QuestionExample());
        paginationDTO.setPagination(totalcount,page,size);

        if (page <1){
            page=1;
        }
        if (page>paginationDTO.getTotalPage()){
            page=paginationDTO.getTotalPage();
        }

        Integer offset = size*(page-1);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //BeanUtils工具类中的copyProperties方法可以将参数左边对象的属性赋值给右边对象的属性
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }

    public PaginationDTO list(Long userId,Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalcount = (int)questionMapper.countByExample(example);

        paginationDTO.setPagination(totalcount,page,size);

        if (page <1){
            page=1;
        }
        if (page>paginationDTO.getTotalPage()){
            page=paginationDTO.getTotalPage();
        }

        Integer offset = size*(page-1);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //BeanUtils工具类中的copyProperties方法可以将参数左边对象的属性赋值给右边对象的属性
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null){
            throw new CustmoizeException(CustomizeErrorCode.QUESTION_NOT_FOUNG);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        //BeanUtils工具类中的copyProperties方法可以将参数左边对象的属性赋值给右边对象的属性
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }


    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtCreate(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }else {
            //更新
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            questionMapper.updateByExampleSelective(updateQuestion, example);
            int updated = questionMapper.updateByExampleSelective(updateQuestion,example);
            if (updated != 1){
                throw new CustmoizeException(CustomizeErrorCode.QUESTION_NOT_FOUNG);
            }
        }
    }

    public void addViewCount(Long id) {
        Question updateQuestion = new Question();
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null){
            throw new CustmoizeException(CustomizeErrorCode.QUESTION_NOT_FOUNG);
        }
        updateQuestion.setViewCount(question.getViewCount()+1);
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andIdEqualTo(id);
        questionMapper.updateByExampleSelective(updateQuestion, example);
    }

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }
}
