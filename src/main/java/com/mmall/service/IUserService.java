package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.model.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> signup(User user);

    ServerResponse<String> getQuestion(String username);

    ServerResponse<String> checkAnswer(String user, String answer);

    ServerResponse<String> updatePassword(String username, String newPassword, String token);
}
