package com.mmall.service;


import com.mmall.common.ServerResponse;
import com.mmall.model.Category;
import com.mmall.model.CategoryMapper;
import com.mysql.cj.util.StringUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
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

    //find all the child-nodes Id (recursive) of given parentId (default 0) and itself
    @Override
    public ServerResponse<List<Integer>> findChildCategory(int categoryId){
        HashSet<Integer> categoryIdHashSet = new HashSet<>();
        Deque<Integer> categoryIdDeque = new ArrayDeque<>();
        categoryIdDeque.add(categoryId);
        if(categoryId!=0) categoryIdHashSet.add(categoryId);
        while(!categoryIdDeque.isEmpty()){
            int sze = categoryIdDeque.size();
            for(int i=0; i<sze; i++) {
                int curCategoryId = categoryIdDeque.poll();
                List<Category> sonCategoryList = findSonCategory(curCategoryId).getData();
                if(sonCategoryList.size()==0) continue;
                for(Category sonCategory: sonCategoryList){
                    if(categoryIdHashSet.add(sonCategory.getId())){
                        categoryIdDeque.add(sonCategory.getId());
                    }
                }
            }
        }
        return ServerResponse.succWithMsgData("", new ArrayList<>(categoryIdHashSet));
    }
}
