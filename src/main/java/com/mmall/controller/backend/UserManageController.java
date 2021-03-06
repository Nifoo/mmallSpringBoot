package com.mmall.controller.backend;

import com.mmall.common.Cnst;
import com.mmall.common.ServerResponse;
import com.mmall.model.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = userService.login(username, password);
        if (response.isSucc()) {
            User userLogin = response.getData();
            if (userLogin.getRole() == Cnst.Role.ROLE_ADMIN) {
                session.setAttribute(Cnst.CURRENT_USER, userLogin);
                return response;
            } else {
                return ServerResponse.failWithMsg("identity is not admin, can't login!");
            }
        } else return response;
    }
}
