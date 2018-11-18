package com.mmall.model.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductDetailVo {

    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private String subImages;

    private String detail;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private String createTime;

    private String updateTime;

    private String imgHost;

    private Integer parentCategoryId;
}
