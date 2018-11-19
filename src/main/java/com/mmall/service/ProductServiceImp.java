package com.mmall.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.model.Category;
import com.mmall.model.CategoryMapper;
import com.mmall.model.Product;
import com.mmall.model.ProductMapper;
import com.mmall.model.vo.ProductDetailVo;
import com.mmall.util.DateTimeUtil;
import com.mysql.cj.util.StringUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImp implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Value("${ftp.server.http.prefix}")
    private String ftpServerHttpPrefix;

    //insert or update product. set MainImage as the first of SubImages.
    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product == null) {
            return ServerResponse.failWithMsg("product info in Empty!");
        }
        //Set subImg[0] as the mainImg
        if (!StringUtils.isNullOrEmpty(product.getSubImages())) {
            product.setMainImage(product.getSubImages().split(",")[0]);
        }
        if (product.getId() == null || productMapper.selectByPrimaryKey(product.getId()) == null) {
            //Insert new product
            int rowCnt = productMapper.insert(product);
            if (rowCnt > 0) {
                return ServerResponse.succWithMsg("Insert product succeed.");
            } else {
                return ServerResponse.failWithMsg("Insert product failed!");
            }
        } else {
            //Update product
            int rowCnt = productMapper.updateByPrimaryKeySelective(product);
            if (rowCnt > 0) {
                return ServerResponse.succWithMsg("Update product succeed.");
            } else {
                return ServerResponse.failWithMsg("Update product failed!");
            }
        }

    }

    @Override
    public ServerResponse setSaleStatus(int productId, int status) {
        Product product = new Product();
        product.setStatus(status);
        product.setId(productId);
        if (productMapper.selectByPrimaryKey(productId) == null) {
            return ServerResponse.failWithMsg("Wrong ProductId!");
        }
        productMapper.updateByPrimaryKeySelective(product);
        return ServerResponse.succWithMsg("setSaleStatus succeed");
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(int productId) {

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.failWithMsg("Wrong ProductId!");
        } else {
            ProductDetailVo productDetailVo = asbProductDetailVo(product);
            return ServerResponse.succWithMsgData("getProductDetail succeed", productDetailVo);
        }
    }

    private ProductDetailVo asbProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //imgHost, parentId
        productDetailVo.setImgHost(ftpServerHttpPrefix);

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime(), "yyyy-MM-dd"));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime(), "yyyy-MM-dd"));
        return productDetailVo;
    }

    //return product list with PageHelper
    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize){
        /*
        PageHepler is based on AOP. The steps to use PageHelper:
        1. StartPage
        2. Complete customized sql
        3. Process with PageHelper (PageHelper would add extra sql like "OFFSET ... LIMIT ...")
         */

        //1.
        PageHelper.startPage(pageNum, pageSize);
        //2.
        List<Product> products = productMapper.selectList();
        //3.
        PageInfo pageOfProducts = new PageInfo(products);
        return ServerResponse.succWithMsgData("getProductList succeed.", pageOfProducts);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(Integer productId, String productName, int pageNum, int pageSize){
        //1.
        PageHelper.startPage(pageNum, pageSize);
        //2.
        if(productId==null && StringUtils.isNullOrEmpty(productName)) return ServerResponse.failWithMsg("id and name are empty!");
        if(StringUtils.isNullOrEmpty(productName)) productName = null;
        List<Product> products = productMapper.searchProduct(productId, productName);
        //3.
        PageInfo pageOfProducts = new PageInfo(products);
        return ServerResponse.succWithMsgData("getProductList succeed.", pageOfProducts);
    }
}
