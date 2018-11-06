package com.mmall.service;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.model.UserMapper;
import com.mmall.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resCount = userMapper.checkUsername(username);
        if(resCount==0) return new ServerResponse<>(ResponseCode.ERROR.getCode(), "username not exists", null);

        User user = userMapper.selectLogin(username, password);
        if(user==null) return new ServerResponse<>(ResponseCode.ERROR.getCode(), "wrong password", null);

        user.setPassword("");
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), "login success", user);
    }
}
