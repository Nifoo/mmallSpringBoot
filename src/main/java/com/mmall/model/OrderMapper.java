package com.mmall.model;

public interface OrderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_order
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_order
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int insert(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_order
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int insertSelective(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_order
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    Order selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_order
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_order
     *
     * @mbg.generated Tue Nov 06 01:18:41 PST 2018
     */
    int updateByPrimaryKey(Order record);
}