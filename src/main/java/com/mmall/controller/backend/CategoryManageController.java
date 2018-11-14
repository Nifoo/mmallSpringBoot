package com.mmall.controller.backend;

import com.mmall.common.Cnst;
import com.mmall.common.Cnst.Role;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.model.Category;
import com.mmall.model.User;
import com.mmall.service.ICategoryService;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private ICategoryService categoryService;

    @RequestMapping("/add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName,
            @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(),
                            "need login to add category!");
        }
        if (user.getRole() != Role.ROLE_ADMIN) {
            return ServerResponse.failWithMsg("only admin can add category!");
        }
        return categoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping("/update_category.do")
    @ResponseBody
    public ServerResponse updateCategoryById(HttpSession session, Category category) {
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(),
                            "need login to update category!");
        }
        if (user.getRole() != Role.ROLE_ADMIN) {
            return ServerResponse.failWithMsg("only admin can update category!");
        }
        return categoryService.updateCategory(category);
    }

    //find all the son-nodes of given parentId (default 0)
    @RequestMapping("/find_son_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> findSonCategory(HttpSession session, int categoryId){
        User user = (User) session.getAttribute(Cnst.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .failWithCodeMsg(ResponseCode.NEED_LOGIN.getCode(),
                            "need login to update category!");
        }
        if (user.getRole() != Role.ROLE_ADMIN) {
            return ServerResponse.failWithMsg("only admin can update category!");
        }
        return categoryService.findSonCategory(categoryId);
    }
}