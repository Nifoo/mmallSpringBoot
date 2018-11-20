package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.model.Product;
import com.mmall.model.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(int productId, int status);

    ServerResponse<ProductDetailVo> getProductDetail(int productId);

    ServerResponse<ProductDetailVo> getProductDetailUser(int productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(Integer productId, String productName, int pageNum,
            int pageSize);

    ServerResponse<PageInfo> searchProductUser(String keyword, Integer categoryId,
            int pageNum, int pageSize, String orderBy);

}
