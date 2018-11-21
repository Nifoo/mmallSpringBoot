package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.model.vo.CartVo;
import java.util.List;

public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> delete(Integer userId, Integer productId);

    CartVo getCartVo(Integer userId);

    ServerResponse<CartVo> changeCheckState(Integer userId, List<Integer> productIds);

    ServerResponse<CartVo> setAllCheckState(Integer userId, int checkState);
}
