package com.jzy.community.service;

import com.jzy.community.dto.NotificationDTO;
import com.jzy.community.dto.PaginationDTO;
import com.jzy.community.dto.QuestionDTO;
import com.jzy.community.enums.NotificationStatusEnum;
import com.jzy.community.enums.NotificationTypeEnum;
import com.jzy.community.exception.CustmoizeException;
import com.jzy.community.exception.CustomizeErrorCode;
import com.jzy.community.mapper.CommentMapper;
import com.jzy.community.mapper.NotificationMapper;
import com.jzy.community.mapper.QuestionMapper;
import com.jzy.community.mapper.UserMapper;
import com.jzy.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.NotificationBroadcasterSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author jzy
 * @create 2019-08-29-0:14
 */
@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CommentMapper commentMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(userId);
        Integer totalcount = (int)notificationMapper.countByExample(example);

        paginationDTO.setPagination(totalcount,page,size);

        if (page <1){
            page=1;
        }
        if (page>paginationDTO.getTotalPage()){
            page=paginationDTO.getTotalPage();
        }

        Integer offset = size*(page-1);
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId);
        //设置通知倒序排列
        notificationExample.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, size));

        if (notifications == null){
            return paginationDTO;
        }
        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            User user = userMapper.selectByPrimaryKey(notification.getNotifier());

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setId(notification.getId());
            notificationDTO.setGmtCreate(notification.getGmtCreate());
            notificationDTO.setNotifier(user.getName());
            notificationDTO.setStatus(notification.getStatus());
            //判断是回复的问题还是回复的评论
            if (notification.getType()==NotificationTypeEnum.REPLY_QUESTION.getType()){//回复问题
                //根据outerid拿到问题
                Question question = questionMapper.selectByPrimaryKey(notification.getOuterid());
                notificationDTO.setType(NotificationTypeEnum.REPLY_QUESTION.getName());//设置回复类型
                notificationDTO.setQuestionId(notification.getOuterid());//设置问题页id
                notificationDTO.setOuterTitle(question.getTitle()); //设置显示的标题
            }else if (notification.getType()==NotificationTypeEnum.REPLY_COMMENT.getType()){//回复评论
                Comment comment = commentMapper.selectByPrimaryKey(notification.getOuterid());
                notificationDTO.setOuterTitle(comment.getContent());
                notificationDTO.setType(NotificationTypeEnum.REPLY_COMMENT.getName());
                notificationDTO.setQuestionId(comment.getParentId());
            }

            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);

        return paginationDTO;
    }

    public Long unreadCount(Long id) {
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(id)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        List<Notification> notifications = notificationMapper.selectByExample(example);
        return Long.valueOf(notifications.size());
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null){
            throw new CustmoizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUNG);
        }
        if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustmoizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setGmtCreate(notification.getGmtCreate());
        notificationDTO.setNotifier(user.getName());
        //判断是回复的问题还是回复的评论
        if (notification.getType()==NotificationTypeEnum.REPLY_QUESTION.getType()){//回复问题
            //根据outerid拿到问题
            notificationDTO.setQuestionId(notification.getOuterid());//设置问题页id
        }else if (notification.getType()==NotificationTypeEnum.REPLY_COMMENT.getType()){//回复评论
            Comment comment = commentMapper.selectByPrimaryKey(notification.getOuterid());
            notificationDTO.setQuestionId(comment.getParentId());
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        return notificationDTO;
    }
}
