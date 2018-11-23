package com.mmall.service;

import com.mmall.common.ServerResponse;
import java.math.BigDecimal;

public interface IOrderService {

    ServerResponse create(Integer shippingId, Integer userId);

    ServerResponse charge(String stripeToken, String stripeTokenType, String stripeEmail,
            Integer userId, BigDecimal totalPrice, Long orderNum);

    ServerResponse cancel(Long orderNum, Integer userId);
}
