package com.jzy.community.service;

import com.jzy.community.dto.PaginationDTO;
import com.jzy.community.dto.QuestionDTO;
import com.jzy.community.dto.QuestionQueryDTO;
import com.jzy.community.exception.CustmoizeException;
import com.jzy.community.exception.CustomizeErrorCode;
import com.jzy.community.mapper.QuestionExtMapper;
import com.jzy.community.mapper.QuestionMapper;
import com.jzy.community.mapper.UserMapper;
import com.jzy.community.model.Question;
import com.jzy.community.model.QuestionExample;
import com.jzy.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    //访问首页或搜索调用
    public PaginationDTO list(String search, Integer page, Integer size) {
        //判断参数search的值是否存在，存在则把search拼接成mysql能够正则匹配的字符串
        if (StringUtils.isNotBlank(search)) {
            String[] searchs = StringUtils.split(search, " ");
            search = Arrays.stream(searchs).collect(Collectors.joining("|"));
        }else {
            search =null;
        }


        PaginationDTO paginationDTO = new PaginationDTO();//分页对象
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();//查询问题对象

        questionQueryDTO.setSearch(search);


        //查询问题总数，如果是带search的，可以根据seacrh的值匹配相关问题的总数
        Integer totalcount = questionExtMapper.getTotalCount(questionQueryDTO);
        //根据问题总数，offset（当前页），size（页面显示的问题数）得到分页相关的属性
        paginationDTO.setPagination(totalcount, page, size);

        if (page < 1) {
            page = 1;
        }
        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        //拿到问题的list
        Integer offset = size * (page - 1);
        questionQueryDTO.setOffset(offset);
        questionQueryDTO.setSize(size);
        List<Question> questions = questionExtMapper.selectAllOrSearchQusetion(questionQueryDTO);

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //BeanUtils工具类中的copyProperties方法可以将参数左边对象的属性赋值给右边对象的属性
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setSearch(search);
        paginationDTO.setData(questionDTOList);

        return paginationDTO;
    }


    //访问我的问题调用
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);

        Integer totalcount = (int) questionMapper.countByExample(example);

        paginationDTO.setPagination(totalcount, page, size);

        if (page < 1) {
            page = 1;
        }
        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page - 1);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        questionExample.setOrderByClause("gmt_create desc");//倒序
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //BeanUtils工具类中的copyProperties方法可以将参数左边对象的属性赋值给右边对象的属性
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return paginationDTO;
    }


    //问题id拿到questiondto对象（包含user）
    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustmoizeException(CustomizeErrorCode.QUESTION_NOT_FOUNG);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        //BeanUtils工具类中的copyProperties方法可以将参数左边对象的属性赋值给右边对象的属性
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }

    //发布新问题还是修改问题
    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtCreate(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        } else {
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
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated != 1) {
                throw new CustmoizeException(CustomizeErrorCode.QUESTION_NOT_FOUNG);
            }
        }
    }


    //增加阅读数
    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    //根据问题的标签获取它的相关问题（需要修改）
    public List<QuestionDTO> selectRelated(QuestionDTO queryQuestionDTO) {
        if (StringUtils.isBlank(queryQuestionDTO.getTag())) {
            return new ArrayList<>();
        }
        String regexpTag = StringUtils.replace(queryQuestionDTO.getTag(), ",", "|");
        Question question = new Question();
        question.setId(queryQuestionDTO.getId());
        question.setTag(regexpTag);
        List<Question> questions = questionExtMapper.selectRelated(question);

        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;

    }

    public void delete(Long id, User user) {
        User questionCreator = userMapper.selectByPrimaryKey(questionMapper.selectByPrimaryKey(id).getCreator());
        if (!questionCreator.getAccountId().equals(user.getAccountId())){
            throw new CustmoizeException(CustomizeErrorCode.DEL_QUESTION_FAIL);
        }
        questionMapper.deleteByPrimaryKey(id);
    }
}
