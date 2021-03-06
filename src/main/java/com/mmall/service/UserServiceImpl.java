package com.mmall.service;

import com.mmall.common.Cnst;
import com.mmall.common.Cnst.Role;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.model.User;
import com.mmall.model.UserMapper;
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
        //User user in session doesn't have the password.
        if (userMapper.checkUsername(username) == 0)
            return new ServerResponse<>(ResponseCode.ERROR.getCode(), "username not exists", null);
        User user = userMapper.selectLogin(username, MD5Util.getMD5(password));
        if (user == null) return new ServerResponse<>(ResponseCode.ERROR.getCode(), "wrong password", null);
        user.setPassword("");
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), "login succeed", user);
    }

    @Override
    public ServerResponse<String> signup(User user) {
        if (user.getUsername().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty())
            return ServerResponse.failWithMsg("info incomplete!");
        if (userMapper.checkUsername(user.getUsername()) > 0)
            return ServerResponse.failWithMsg("username already exists!");
        if (userMapper.checkEmail(user.getEmail()) > 0) return ServerResponse.failWithMsg("email already exists!");
        user.setRole(Cnst.Role.ROLE_CUSTOMER);
        //MD5 encryption
        user.setPassword(MD5Util.getMD5(user.getPassword()));
        if (userMapper.insertSelective(user) <= 0) return ServerResponse.failWithMsg("register failed!");
        else return ServerResponse.succWithMsg("register succeed!");
    }

    @Override
    public ServerResponse<String> getQuestion(String username) {
        if (userMapper.checkUsername(username) == 0) return ServerResponse.failWithMsg("user doesn't exist!");
        String question = userMapper.selectQuestionByUsername(username);
        if (!question.isEmpty()) return ServerResponse.succWithMsgData("question found", question);
        else return ServerResponse.failWithMsg("question not found");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String answer) {
        //If answer is wrong
        if (userMapper.checkAnswer(username, answer) <= 0) return ServerResponse.failWithMsg("wrong answer");
        //If correct, Generate token and put into local cache
        String forgetToken = UUID.randomUUID().toString();
        TokenCache.setKey("token_" + username, forgetToken);
        return ServerResponse.succWithMsgData("forgetToken generated", forgetToken);
    }

    @Override
    public ServerResponse<String> resetPassword(String username, String newPassword, String token) {
        //Check token
        String tokenCached = TokenCache.getKey("token_" + username);
        if (tokenCached == null || !tokenCached.equals(token)) return ServerResponse.failWithMsg("wrong token!");
        //Token checked
        if (userMapper.updatePasswordByUsername(username, MD5Util.getMD5(newPassword)) > 0)
            return ServerResponse.succWithMsg("update password succeed");
        else return ServerResponse.failWithMsg("update password failed");
    }

    @Override
    public ServerResponse<String> resetPasswordLogin(User user, String oldPassword, String newPassword) {
        if (userMapper.checkUseridPassword(user.getId(), MD5Util.getMD5(oldPassword)) <= 0)
            return ServerResponse.failWithMsg("userid and password not match!");
        user.setPassword(MD5Util.getMD5(newPassword));
        if (userMapper.updateByPrimaryKey(user) <= 0) return ServerResponse.failWithMsg("reset password failed!");
        else return ServerResponse.succWithMsg("reset password succeed!");
    }

    @Override
    //return full-userInfo in DB according to userInfo in Session
    public ServerResponse<User> getUserInfo(User user) {
        User userInDB = userMapper.selectByPrimaryKey(user.getId());
        if (userInDB == null) return ServerResponse.failWithMsg("user not found in DB!");
        else return ServerResponse.succWithMsgData("getUserInfo succeed", userInDB);
    }

    @Override
    public ServerResponse<User> updateUserInfo(User user) {
        //We ONLY update user.email, phone, question, answer according to id. NOT update user.id, name, password.
        //id, name got from session.
        if (userMapper.checkIdEmail(user.getId(), user.getEmail()) > 0)
            return ServerResponse.failWithMsg("duplicate email! update userInfo failed!");
        User updateUser = new User();
        updateUser.setUpdateField(user);
        if (userMapper.updateByPrimaryKeySelective(updateUser) > 0)
            return ServerResponse.succWithMsgData("update userInfo succeed!", updateUser);
        else return ServerResponse.failWithMsg("update userInfo failed!");
    }

    @Override
    public ServerResponse checkAdminRole(User user){
         if (user.getRole()==Role.ROLE_ADMIN) return ServerResponse.succWithMsg("is admin");
         else return ServerResponse.failWithMsg("not admin");
    }
}
