package com.jzy.community.service;

import com.jzy.community.mapper.UserMapper;
import com.jzy.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jzy
 * @create 2019-08-12-20:28
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void creatOrUpdate(User user) {
        User dbuser = userMapper.findByAccount(user.getAccountId());
        if(dbuser.getId() == null) {
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);

        }
        else{
            dbuser.setGmtModified(System.currentTimeMillis());
            dbuser.setAvatarUrl(user.getAvatarUrl());
            dbuser.setName(user.getName());
            dbuser.setToken(user.getToken());
            userMapper.update(dbuser);

        }

    }
}
