package com.mmall.model.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemVo {

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

}
