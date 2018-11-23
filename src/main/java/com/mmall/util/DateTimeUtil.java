package com.mmall.util;

import com.mmall.common.Cnst;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    public static String dateToStr(Date date, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
    public static String dateToStr(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Cnst.dateStrFmt);
        return simpleDateFormat.format(date);
    }
    public static Date strToDate(String str, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(dateToStr(new Date(), "yyyy-MM-dd"));
        System.out.println(strToDate(dateToStr(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd"));
    }
}

