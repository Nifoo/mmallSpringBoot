package com.mmall.model.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CartVo {
    private Integer userId;
    private List<CartItemVo> cartItemVoList;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked;
    private String imgHost;
}
