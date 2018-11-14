package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.model.Category;
import java.util.List;

public interface ICategoryService {

    ServerResponse addCategory(String name, int parentId);

    ServerResponse updateCategory(Category category);

    ServerResponse<List<Category>> findSonCategory(int categoryId);

}
