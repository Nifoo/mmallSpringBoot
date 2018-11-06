package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.model.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
}
