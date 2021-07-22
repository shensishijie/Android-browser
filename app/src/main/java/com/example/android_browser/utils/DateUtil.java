package com.example.android_browser.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getDate() {
        //返回String类型的日期
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        return ft.format(date);
    }
}
