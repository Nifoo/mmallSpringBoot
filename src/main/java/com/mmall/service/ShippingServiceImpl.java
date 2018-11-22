package com.mmall.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.model.Shipping;
import com.mmall.model.ShippingMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        if (shipping == null) {
            return ServerResponse.failWithMsg("shipping is empty.");
        }
        shipping.setUserId(userId);
        int rowCnt = shippingMapper.insertSelective(shipping);
        if (rowCnt > 0) {
            Map<String, Integer> mp = new HashMap<>();
            mp.put("shippingId", shipping.getId());
            return ServerResponse.succWithMsgData("insert shipping succeed.", mp);
        }
        return ServerResponse.failWithMsg("insert shipping failed.");
    }

    @Override
    public ServerResponse delete(Integer userId, Integer shippingId) {
        if (shippingId == null) {
            return ServerResponse.failWithMsg("shippingId is empty!");
        }
        int rowCnt = shippingMapper.deleteByUserIdShippingId(userId, shippingId);
        if (rowCnt > 0) {
            return ServerResponse.succWithMsg("delete shipping succeed.");
        }
        return ServerResponse.failWithMsg("delete shipping failed.");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        if (shipping == null) {
            return ServerResponse.failWithMsg("shipping is empty.");
        }
        shipping.setUserId(userId);
        int rowCnt = shippingMapper.updateByUserIdShippingId(shipping);
        if (rowCnt > 0) {
            Map<String, Integer> mp = new HashMap<>();
            mp.put("shippingId", shipping.getId());
            return ServerResponse.succWithMsgData("update shipping succeed.", mp);
        }
        return ServerResponse.failWithMsg("update shipping failed.");
    }

    @Override
    public ServerResponse select(Integer userId, Integer shippingId) {
        if (shippingId == null) {
            return ServerResponse.failWithMsg("shippingId is empty!");
        }
        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId, shippingId);
        if (shipping!=null) {
            return ServerResponse.succWithMsgData("select shipping succeed.", shipping);
        }
        return ServerResponse.failWithMsg("select shipping failed.");
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize){
        //PageHelper
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippings);
        return ServerResponse.succWithMsgData("get shipping list succeed.", pageInfo);
    }
}
