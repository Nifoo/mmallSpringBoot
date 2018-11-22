package com.mmall.controller.portal;

import com.mmall.common.Cnst;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.model.Shipping;
import com.mmall.model.User;
import com.mmall.service.IShippingService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService shippingService;

    //Response.data: {"shippingId": shippingId}
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        }
        return shippingService.add(user.getId(), shipping);
    }

    //Response.data: null
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        }
        return shippingService.delete(user.getId(), shippingId);
    }


    //Response.data: {"shippingId": shippingId}
    //Request param "shipping" MUST contain shippingId.
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        }
        return shippingService.update(user.getId(), shipping);
    }

    //Response.data: Shipping shipping
    //find the shipping record with shippingId
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        }
        return shippingService.select(user.getId(), shippingId);
    }

    //Response.data: PageInfo
    //find all the shipping records with userId, in PageInfo way.
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session,
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        }
        return shippingService.list(user.getId(), pageNum, pageSize);
    }
}
