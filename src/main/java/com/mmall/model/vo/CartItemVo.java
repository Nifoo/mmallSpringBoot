package com.mmall.model.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CartItemVo {
    //a Value Class combined both cart and product info
    private Integer cartId;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private BigDecimal productTotalPrice;
    private Integer productStatus;
    private Integer productStock;
    private Integer productChecked;

    private boolean quantityLimited; // info about whether total quantity excesses the current stock
}
