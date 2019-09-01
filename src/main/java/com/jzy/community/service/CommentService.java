package com.jzy.community.service;

import com.jzy.community.dto.CommentDTO;
import com.jzy.community.enums.CommentTypeEnum;
import com.jzy.community.enums.NotificationStatusEnum;
import com.jzy.community.enums.NotificationTypeEnum;
import com.jzy.community.exception.CustmoizeException;
import com.jzy.community.exception.CustomizeErrorCode;
import com.jzy.community.mapper.*;
import com.jzy.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    //开启事务
    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustmoizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUNG);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustmoizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustmoizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);

            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);
            //创建通知,判断评论人是否和父评论人一致，如一致则不发起通知
            if (comment.getCommentator() != dbComment.getCommentator()) {
                creatNotify(comment, dbComment.getCommentator(), NotificationTypeEnum.REPLY_COMMENT);
            }


        } else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustmoizeException(CustomizeErrorCode.QUESTION_NOT_FOUNG);
            }
            comment.setCommentCount(0);
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
            //创建通知,判断评论人是否和问题发起人一致，如一致则不发起通知
            if (comment.getCommentator() != question.getCreator()) {
                creatNotify(comment, question.getCreator(), NotificationTypeEnum.REPLY_QUESTION);
            }

        }
    }

    //发起通知(参数：comment回复，questionid通知的问题id，receiverUserId接收通知的user的id)
    private void creatNotify(Comment comment, Long receiverUserId, NotificationTypeEnum notificationType) {

        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        //问题id或者父评论id

        notification.setOuterid(comment.getParentId());

        //发起通知人
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        //通知接收人
        notification.setReceiver(receiverUserId);
        notificationMapper.insert(notification);
    }


    //获取评论
    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        //评论倒序（是问题的评论就倒序，评论的回复就正序）
        if (type.getType() == 1){
            commentExample.setOrderByClause("gmt_create desc");
        }

        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if (comments.size() == 0) {
            return new ArrayList<>();
        }

        //获取去重的评论人ID
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        //根据评论人ID获取user集合，转换成Map(Long（user的id）,User)形式
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换 comment 为 commentDTO(根据comment的Commentator值在userMap中的到user)
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
