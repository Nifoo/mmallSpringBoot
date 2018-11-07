package com.mmall.controller.portal;

import com.mmall.common.Cnst;
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

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse serverResponse = userService.login(username, password);
        if(serverResponse.isSucc()) session.setAttribute(Cnst.CURRENT_USER, serverResponse.getData());
        return serverResponse;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Cnst.CURRENT_USER);
        return ServerResponse.succWithMsg("logout success");
    }

    //create_time null problem
    @RequestMapping(value = "signup.do")
    @ResponseBody
    public ServerResponse<String> signup(User user){
        return userService.signup(user);
    }

    //Verified
    @RequestMapping(value = "forget_password_get_question.do")
    @ResponseBody
    public ServerResponse<String> forgetPasswordGetQuestion(String username){
        return userService.getQuestion(username);
    }

    //Verified
    @RequestMapping(value =  "forget_password_check_answer.do")
    @ResponseBody
    public ServerResponse<String> forgetPasswordCheckAnswer(String username, String answer){
        //return ServerResponse<null> for wrong, ...<token> for correct.
        //forgetToken is a certification for the agent who has answered the question correctly.
        return userService.checkAnswer(username, answer);
    }

    @RequestMapping(value = "forget_password_update_password.do")
    @ResponseBody
    public ServerResponse<String> updatePassword(String username, String newPassword, String token){
        return userService.updatePassword(username, newPassword, token);
    }
}
