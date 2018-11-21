package com.mmall.controller.portal;

import com.mmall.common.Cnst;
import com.mmall.common.Cnst.Cart;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.model.User;
import com.mmall.model.vo.CartVo;
import com.mmall.service.ICartService;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService cartService;

    //Add count of items (productId) to cart
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId) {
        User loginUser = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (loginUser == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        } else {
            return cartService.add(loginUser.getId(), productId, count);
        }
    }

    //Update count of items (productId) to cart
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId) {
        User loginUser = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (loginUser == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        } else {
            return cartService.update(loginUser.getId(), productId, count);
        }
    }

    //delete item (productId) from cart
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse<CartVo> delete(HttpSession session, Integer productId) {
        User loginUser = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (loginUser == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        } else {
            return cartService.delete(loginUser.getId(), productId);
        }
    }

    @RequestMapping("change_check_state.do")
    @ResponseBody
    public ServerResponse<CartVo> changeCheckState(HttpSession session, List<Integer> productIds) {
        User loginUser = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (loginUser == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        } else {
            return cartService.changeCheckState(loginUser.getId(), productIds);
        }
    }

    @RequestMapping("all_check.do")
    @ResponseBody
    public ServerResponse<CartVo> allCheck(HttpSession session) {
        User loginUser = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (loginUser == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "user not login!");
        } else {
            return cartService.setAllCheckState(loginUser.getId(), Cnst.Cart.CHECKED);
        }
    }
}
