package com.mmall.controller.portal;

import com.mmall.common.Cnst;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.model.User;
import com.mmall.service.IOrderService;
import java.math.BigDecimal;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {
        User loginUser = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (loginUser == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        } else {
            return orderService.create(shippingId, loginUser.getId());
        }
    }

    //Charge the Cart after obtain costumer's card info and get the stripeToken.
    @RequestMapping("charge.do")
    @ResponseBody
    public ServerResponse charge(String stripeToken, String stripeTokenType, String stripeEmail,
            BigDecimal totalPrice, Long orderNum, HttpSession session) {
        User loginUser = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (loginUser == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        } else {
            return orderService
                    .charge(stripeToken, stripeTokenType, stripeEmail, loginUser.getId(), totalPrice, orderNum);
        }
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNum){
        User loginUser = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (loginUser == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        } else {
            return orderService.cancel(orderNum, loginUser.getId());
        }
    }
}
