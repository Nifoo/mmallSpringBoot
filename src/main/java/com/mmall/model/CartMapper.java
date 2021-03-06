package com.mmall.model;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CartMapper {

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_cart
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_cart
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int insert(Cart record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_cart
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int insertSelective(Cart record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_cart
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    Cart selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_cart
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int updateByPrimaryKeySelective(Cart record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mmall_cart
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdProductId(@Param("userId") Integer userId,
            @Param("productId") Integer productId);

    List<Cart> selectByUserId(Integer userId);

    int updateAllCheckByUserId(@Param("userId") Integer userId,
            @Param("checkState") Integer checkState);
}