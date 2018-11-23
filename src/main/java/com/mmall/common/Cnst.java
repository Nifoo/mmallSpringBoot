package com.mmall.common;

import com.google.common.collect.Sets;
import java.util.Set;

public class Cnst {

    public static final String CURRENT_USER = "CURRENT_USER";
/*    public enum Role{
        ROLE_CUSTOMER(0, "CUSTOMER"), ROLE_ADMIN(1, "ADMIN");
        private final int code;
        private final String desc;
        Role(int code, String desc){
            this.code = code;
            this.desc = desc;
        }
        public int getCode(){ return code;}
        public String getDesc(){ return desc;}
    }*/

    public interface Role {

        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    //Product: 1-Sale 2-NotOnSale 3-Deleted
    public interface SaleStatus {

        int SALE = 1;
        int NOT_SALE = 2;
        int DELETED = 3;
    }

    public interface ProductListOrderBy {

        Set<String> ProductOrder = Sets.newHashSet("price_desc", "price_asc");
    }

    public interface Cart {

        int CHECKED = 1;
        int UNCHECKED = 2;
    }

    public static int changeCheckState(int curState) {
        if (curState == Cart.CHECKED) {
            return Cart.UNCHECKED;
        } else {
            return Cart.CHECKED;
        }
    }

    public interface paymentPlatform {

        int STRIPE = 1;
    }

    public interface paymentType {

        int CREDIT_CARD = 1;
    }

    public enum OrderStatusEnum {
        CANCELED(0, "Canceled"),
        NOT_PAID(10, "NotPaid"),
        PAID(20, "Paid"),
        SHIPPED(40, "Shipped"),
        ORDER_COMPLETED(50, "Completed"),
        ORDER_CLOSED(60, "Closed");

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static String getValue(int code){
            for(OrderStatusEnum ele: OrderStatusEnum.values()){
                if(ele.getCode() == code) return ele.getValue();
            }
            return null;
        }
    }

    public final static String dateStrFmt = "yyyy-MM-dd";
}
