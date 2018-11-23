package com.mmall.model.vo;

import com.mmall.model.Shipping;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class OrderVo {

    private Integer orderId;

    private Long orderNo;

    private BigDecimal paymentAmount;

    private Integer status;

    private String statusDesc;

    private String paymentTime;

    private String sendTime;

    private String endTime;

    private String closeTime;

    private String createTime;

    private List<OrderItemVo> orderItemVoList;

    private String imageHost;

    private Shipping shipping;
}
