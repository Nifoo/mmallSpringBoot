package com.mmall.model.vo;

import com.mmall.model.Product;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductBriefVo {

    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private BigDecimal price;

    private Integer status;

    private String imgHost;

    public ProductBriefVo(Product product){
        //Not set imgHost here
        this.categoryId = product.getCategoryId();
        this.id = product.getId();
        this.mainImage = product.getMainImage();
        this.price = product.getPrice();
        this.name = product.getName();
        this.status = product.getStatus();
        this.subtitle = product.getSubtitle();
    }

}
