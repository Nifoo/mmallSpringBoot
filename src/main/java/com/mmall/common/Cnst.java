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

    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    //Product: 1-Sale 2-NotOnSale 3-Deleted
    public interface SaleStatus{
        int SALE = 1;
        int NOT_SALE = 2;
        int DELETED = 3;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
}
