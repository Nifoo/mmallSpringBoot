package com.mmall.service;


import com.mmall.common.ServerResponse;
import com.mmall.model.Category;
import com.mmall.model.CategoryMapper;
import com.mysql.cj.util.StringUtils;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Override
    public ServerResponse addCategory(String name, int parentId) {
        if (StringUtils.isNullOrEmpty(name)) {
            return ServerResponse.failWithMsg("category name is empty!");
        }
        Category category = new Category();
        category.setName(name);
        category.setParentId(parentId);
        category.setStatus(true);
        int effectedRow = categoryMapper.insertSelective(category);
        if (effectedRow == 0) {
            return ServerResponse.failWithMsg("add category failed!");
        } else {
            return ServerResponse.succWithMsg("add category succeed!");
        }
    }

    @Override
    public ServerResponse updateCategory(Category category) {
        int effectedRow = categoryMapper.updateByPrimaryKeySelective(category);
        if (effectedRow == 0) {
            return ServerResponse.failWithMsg("update category failed!");
        } else {
            return ServerResponse.succWithMsg("update category succeed!");
        }
    }

    @Override
    public ServerResponse<List<Category>> findSonCategory(int categoryId) {
        List<Category> categoryList = categoryMapper.findSonCategory(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) {
            logger.info("son categories under id = " + categoryId + " not found!");
        }
        return ServerResponse.succWithMsgData("findSonCategory succeed!", categoryList);
    }
}
