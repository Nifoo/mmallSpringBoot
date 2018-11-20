package com.mmall.model;

import com.mysql.cj.util.StringUtils;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper {

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_product
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_product
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int insert(Product record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_product
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int insertSelective(Product record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_product
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    Product selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_product
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int updateByPrimaryKeySelective(Product record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_product
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> searchProduct(@Param("id") Integer productId, @Param("name") String productName);

    List<Product> selectByNameAndCategoryIds(@Param("keyword") String keyword,
            @Param("categoryid_list") List<Integer> categoryIdList);
}