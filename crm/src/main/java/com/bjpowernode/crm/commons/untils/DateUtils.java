package com.bjpowernode.crm.commons.untils;

import java.text.SimpleDateFormat;
import java.util.Date;

//对Date数据类型进行格式化
//此处封装了三个不同的格式 , 方便后续不同需求使用
public class DateUtils {
//    年-月-日 时:分:秒
    public static String formateDateTime(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);
        return format;
    }
//    年-月-日
    public static String formateDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(date);
        return format;
    }
//    时:分:秒
    public static String formateTime(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String format = simpleDateFormat.format(date);
        return format;
    }
}
