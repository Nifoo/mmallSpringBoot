package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Cnst;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.model.Product;
import com.mmall.model.User;
import com.mmall.model.vo.ProductDetailVo;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IProductService productService;
    @Autowired
    private IFileService fileService;

    @Value("${ftp.server.http.prefix}")
    private String ftpPrefix;

    //Verified
    //insert or update product in Product Table.
    //If product.id is given and already in DB, then do Update; else do Insert.
    @RequestMapping("save_or_update.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Pleas login as admin!");
        }
        if (userService.checkAdminRole(user).isSucc()) {
            return productService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.failWithMsg("Only admin can save product!");
        }
    }

    //Verified
    //Update product status: 1-Sale 2-NotOnSale 3-Deleted
    @RequestMapping("sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Pleas login as admin!");

        }
        if (userService.checkAdminRole(user).isSucc()) {
            return productService.setSaleStatus(productId, status);
        } else {
            return ServerResponse.failWithMsg("Only admin can set sale status!");
        }
    }


    //Verified
    //Get product detail VO( product(date converted to Str for better frontend view) + parentCategoryId ) by productId
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpSession session, int productId) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Pleas login as admin!");
        }
        if (userService.checkAdminRole(user).isSucc()) {
            return productService.getProductDetail(productId);
        } else {
            return ServerResponse.failWithMsg("Only admin can save product!");
        }
    }

    //Verified
    //Get complete product List processed by PageHelper
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getProductList(HttpSession session,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Pleas login as admin!");
        }
        if (userService.checkAdminRole(user).isSucc()) {
            return productService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.failWithMsg("Only admin can get the product list!");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchProduct(HttpSession session, Integer productId,
            String productName,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Pleas login as admin!");
        }
        if (userService.checkAdminRole(user).isSucc()) {
            return productService.searchProduct(productId, productName, pageNum, pageSize);
        } else {
            return ServerResponse.failWithMsg("Only admin can search the product!");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(@RequestParam(name = "upload_file") MultipartFile multipartFile,
            HttpServletRequest request,
            HttpSession session) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Pleas login as admin!");
        }
        if (userService.checkAdminRole(user).isSucc()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = fileService.upload(multipartFile, path);
            if (targetFileName == null) {
                return ServerResponse.failWithMsg("upload failed!");
            } else {
                String url = ftpPrefix + targetFileName;
                Map<String, String> fileMap = new HashMap<>();
                fileMap.put("uri", targetFileName);
                fileMap.put("url", url);
                return ServerResponse.succWithMsgData("upload succeed.", fileMap);
            }
        } else {
            return ServerResponse.failWithMsg("Only admin can upload the product!");
        }
    }
}
