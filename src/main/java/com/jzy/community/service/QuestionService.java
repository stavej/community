package com.jzy.community.service;

import com.jzy.community.dto.PaginationDTO;
import com.jzy.community.dto.QuestionDTO;
import com.jzy.community.mapper.QuestionMapper;
import com.jzy.community.mapper.UserMapper;
import com.jzy.community.model.Question;
import com.jzy.community.model.User;
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


    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalcount = questionMapper.count();
        paginationDTO.setPagination(totalcount,page,size);

        if (page <1){
            page=1;
        }
        if (page>paginationDTO.getTotalPage()){
            page=paginationDTO.getTotalPage();
        }

        Integer offset = size*(page-1);
        List<Question> questions = questionMapper.list(offset,size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //BeanUtils工具类中的copyProperties方法可以将参数左边对象的属性赋值给右边对象的属性
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }

    public PaginationDTO list(Integer userId,Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalcount = questionMapper.countByCreator(userId);
        paginationDTO.setPagination(totalcount,page,size);

        if (page <1){
            page=1;
        }
        if (page>paginationDTO.getTotalPage()){
            page=paginationDTO.getTotalPage();
        }

        Integer offset = size*(page-1);
        List<Question> questions = questionMapper.listByUserId(userId,offset,size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //BeanUtils工具类中的copyProperties方法可以将参数左边对象的属性赋值给右边对象的属性
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.getById(id);
        User user = userMapper.findById(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        //BeanUtils工具类中的copyProperties方法可以将参数左边对象的属性赋值给右边对象的属性
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }


    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtCreate(question.getGmtCreate());
            questionMapper.create(question);
        }else {
            question.setGmtCreate(System.currentTimeMillis());
            questionMapper.update(question);
        }
    }
}
