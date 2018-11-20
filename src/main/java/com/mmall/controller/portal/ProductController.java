package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.model.vo.ProductDetailVo;
import com.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService productService;

    //Get product detail info for common user. If the product not on sale or deleted, won't be shown.
    @RequestMapping("detail.do")
    public ServerResponse<ProductDetailVo> getDetail(int productId) {
        return productService.getProductDetailUser(productId);
    }

    @RequestMapping("list.do")
    public ServerResponse<PageInfo> searchProduct(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return productService.searchProductUser(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
