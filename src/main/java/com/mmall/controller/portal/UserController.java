package com.mmall.controller.portal;

import com.mmall.common.Cnst;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.model.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService userService;

    //Verified
    @RequestMapping(value = "login.do") //, method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse serverResponse = userService.login(username, password);
        if (serverResponse.isSucc()) session.setAttribute(Cnst.CURRENT_USER, serverResponse.getData());
        return serverResponse;
    }

    //Verified
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Cnst.CURRENT_USER);
        return ServerResponse.succWithMsg("logout succeed");
    }

    //verified
    @RequestMapping(value = "signup.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> signup(User user) {
        return userService.signup(user);
    }

    //Verified
    @RequestMapping(value = "forget_password_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetPasswordGetQuestion(String username) {
        return userService.getQuestion(username);
    }

    //Verified
    @RequestMapping(value = "forget_password_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetPasswordCheckAnswer(String username, String answer) {
        //return ServerResponse<null> for wrong, ...<token> for correct.
        //forgetToken is a certification for the agent who has answered the question correctly.
        return userService.checkAnswer(username, answer);
    }

    //Verified
    @RequestMapping(value = "forget_password_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetPasswordResetPassword(String username, String newPassword, String token) {
        return userService.resetPassword(username, newPassword, token);
    }

    //Verified
    //reset password while login. use HttpSession & oldPassword instead of Question-Answer-Token verification.
    @RequestMapping(value = "reset_password_login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPasswordLogin(HttpSession session, String oldPassword, String newPassword) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) return ServerResponse.succWithMsg("user not login!");
        else return userService.resetPasswordLogin(user, oldPassword, newPassword);
    }

    //Verified
    @RequestMapping(value = "get_userinfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User loginUser = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (loginUser == null)
            return ServerResponse.failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        else {
            User loginUserFull = userService.getUserInfo(loginUser).getData();
            loginUserFull.setPassword("");
            return ServerResponse.succWithMsgData("getUserInfo succeed", loginUserFull);
        }
    }

    //Verified
    @RequestMapping(value = "update_userinfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> updateUserInfo(HttpSession session, User user) {
        User loginUser = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (loginUser == null) return ServerResponse.failWithMsg("user not login!");
        else {
            //user is user-typeIn. loginUser is userInfo in Session.
            //set id, name according to loginUser.
            user.setId(loginUser.getId());
            user.setUsername(loginUser.getUsername());
            ServerResponse<User> response = userService.updateUserInfo(user);
            if (response.isSucc()) {
                //Update userInfo succeed, update session
                session.setAttribute(Cnst.CURRENT_USER, response.getData());
                return ServerResponse.succWithMsg("updateUserInfo succeed");
            } else {
                return ServerResponse.failWithMsg("updateUserInfo failed");
            }
        }
    }
}
