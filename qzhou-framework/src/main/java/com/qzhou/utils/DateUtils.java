package com.qzhou.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date getCurrentTime() {
        //HH 24小时制
        //hh 12小时制
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 创建日期格式化对象
        String formattedDate = dateFormat.format(new Date()); // 将当前时间格式化为 "yyyy-MM-dd hh:mm:ss" 格式的字符串
        Date currentTime = null;
        try {
            currentTime = dateFormat.parse(formattedDate); // 将格式化后的字符串解析为 Date 类型
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentTime;
    }

    public static void main(String[] args) {
        Date currentTime = getCurrentTime();
        System.out.println(currentTime);
    }
}


