package com.mmall.service;

import com.mmall.common.Cnst;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.model.UserMapper;
import com.mmall.model.User;
import com.mmall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        if(userMapper.checkUsername(username)==0) return new ServerResponse<>(ResponseCode.ERROR.getCode(), "username not exists", null);
        User user = userMapper.selectLogin(username, MD5Util.getMD5(password));
        if(user==null) return new ServerResponse<>(ResponseCode.ERROR.getCode(), "wrong password", null);
        user.setPassword("");
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), "login success", user);
    }

    @Override
    public ServerResponse<String> signup(User user){
        if(userMapper.checkUsername(user.getUsername())>0) return ServerResponse.failWithMsg("username already exists!");
        if(userMapper.checkEmail(user.getEmail())>0) return ServerResponse.failWithMsg("email already exists!");
        user.setRole(Cnst.Role.ROLE_CUSTOMER);
        //MD5 encryption
        user.setPassword(MD5Util.getMD5(user.getPassword()));
        if(userMapper.insertSelective(user)<=0) return ServerResponse.failWithMsg("register failed!");
        else return ServerResponse.succWithMsg("register success!");
    }

    @Override
    public ServerResponse<String> getQuestion(String username){
        if(userMapper.checkUsername(username)==0) return ServerResponse.failWithMsg("user doesn't exist!");
        String question = userMapper.selectQuestionByUsername(username);
        if(!question.isEmpty()) return ServerResponse.succWithMsgData("question found", question);
        else return ServerResponse.failWithMsg("question not found");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String answer){
        //If answer is rong
        if(userMapper.checkAnswer(username, answer)<=0) return ServerResponse.failWithMsg("wrong answer");
        //If correct, Generate token and put into local cache
        String forgetToken = UUID.randomUUID().toString();
        TokenCache.setKey("token_" + username, forgetToken);
        return ServerResponse.succWithMsgData("forgetToken generated", forgetToken);
    }

    @Override
    public ServerResponse<String> updatePassword(String username, String newPassword, String token){
        //Check token
        String tokenCached = TokenCache.getKey("token_" + username);
        if(tokenCached == null || !tokenCached.equals(token)) return ServerResponse.failWithMsg("wrong token!");
        //Token checked
        if(userMapper.updatePasswordByUsername(username, MD5Util.getMD5(newPassword))>0) return ServerResponse.failWithMsg("update password success");
        else return ServerResponse.failWithMsg("update password failed");
    }
}
