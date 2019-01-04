/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;

import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import jyms.CommonParas;
import jyms.data.TxtLogger;

/**
 *
 * @author John
 */
public class DateUtil {
    
    private static String datePattern = "yyyy-MM-dd";
    private static String timePattern = datePattern + " HH:mm:ss";
            /**
                * 获取日期
                * @param timeType 时间类型，譬如：Calendar.DAY_OF_YEAR
                * @param timenum  时间数字，譬如：-1 昨天，0 今天，1 明天
                * @return 日期
             */
            public static final Date getDateFromNow(int timeType, int timenum){
                    Calendar cld = Calendar.getInstance();
                    //cld.set(timeType, cld.get(timeType) + timenum);
                    cld.add(timeType, timenum);
                    return cld.getTime();
            }
            public static final Date getDateFromNow(Date dd,int timeType, int timenum){
                    Calendar cld = Calendar.getInstance();
                    cld.setTime(dd);
                    //cld.set(timeType, cld.get(timeType) + timenum);
                    cld.add(timeType, timenum);
                    return cld.getTime();
            }
            
            /**
            * This method generates a string representation of a date/time
            * in the format you specify on input
            *
            * @param aMask the date pattern the string is in
            * @param strDate a string representation of a date
            * @return a converted Date object
            * @see java.text.SimpleDateFormat
            * @throws ParseException
            */
            public static final Date convertStringToDate(String aMask, String strDate)
                           throws ParseException {
                    DateFormat df = null;
                    Date date = null;
                    df = new SimpleDateFormat(aMask);

                    try {
                            df.setLenient(false);
                            date = df.parse(strDate);
                    } catch (ParseException pe) {
                            return null;
                    }
                    return (date);
            }
            public static final Date convertDateStringToDate(String strDate) throws ParseException {
                    return convertStringToDate( datePattern,  strDate); 
            }
            public static final Date convertTimeStringToDate(String strDate) throws ParseException {
                    return convertStringToDate( timePattern,  strDate); 
//                DateFormat df = new SimpleDateFormat(timePattern);
//                    Date date = null;
//                    //df = new SimpleDateFormat(timePattern);
//
//                    try {
//                            date = df.parse(strDate);
//                    } catch (ParseException pe) {
//                            return null;
//                    }
//
//                    return (date);
            }
        
            /**
                *得到格式化后的指定日期
                *@param strScheme 格式模式字符串
                *@param date 指定的日期值
                *@return 格式化后的指定日期，如果有异常产生，返回空串""
                *@see java.util.SimpleDateFormat
            */
            public static String convertDateToString( String strScheme,Date date) {
                    String strReturn = null;
                    try {
                            SimpleDateFormat sdf = new SimpleDateFormat(strScheme);
                            strReturn = sdf.format(date);
                    } catch (Exception e) {
                            strReturn = "2016-05-17 00:00:00";
                    }

                    return strReturn;
            }
             /**
                *得到格式化后的指定日期
                *@param strScheme 格式模式字符串
                *@param date 指定的日期值
                *@return 格式化后的指定日期，如果有异常产生，返回空串""
                *@see java.util.SimpleDateFormat
            */
            public static String getDateString(Date date) {
                    String strReturn = null;
                    try {
                            SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                            strReturn = sdf.format(date);
                    } catch (Exception e) {
                            strReturn = "2016-05-17";
                    }

                    return strReturn;
            }
            /**
                *得到格式化后的指定日期
                *@param date 指定的日期值
                *@return 格式化后的指定日期，如果有异常产生，返回空串""
                *@see java.util.SimpleDateFormat
            */
            public static String getDateTimeString(Date date) {
                    String strReturn = null;
                    try {
                            SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
                            strReturn = sdf.format(date);
                    } catch (Exception e) {
                            strReturn = "2016-05-17 00:00:00";
                    }

                    return strReturn;
            }
            /**
                * This method returns the current date in the format: MM/dd/yyyy
                *
                * @param aDate
                * @return the current date
                * @throws ParseException
            */
            public static Calendar getCalendar(Date aDate) throws ParseException {
                    Calendar cal = new GregorianCalendar();
                    cal.setTime(aDate);

                    return cal;
            }
            
            /**
                * This method returns the current date in the format: MM/dd/yyyy
                *
                * @param sDate
                * @return the current date
                * @throws ParseException
            */
            public static Calendar getCalendar(String sDate) throws ParseException {

                    Calendar cal = new GregorianCalendar();
                    cal.setTime(convertTimeStringToDate(sDate));

                    return cal;
            }
            
            /**
                * 获得某个月最大天数
                *
                * @param year 年份
                * @param month 月份 (1-12)
                * @return 某个月最大天数
            */
            public static int getMaxDayByYearMonth(int year, int month) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                return calendar.getActualMaximum(Calendar.DAY_OF_MONTH );
            }
            
            /**
            * 判断是否润年
            * @param Year 年份
            * @return
        */

	public static boolean isLeapYear(int Year) {

		/**
		 * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
		 * 3.能被4整除同时能被100整除则不是闰年
		 */

//		Date d = strToDate(ddate);
//
//		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
//
//		gc.setTime(d);
//
//		int year = gc.get(Calendar.YEAR);

		if ((Year % 400) == 0) return true;
		else if ((Year % 4) == 0) {
			if ((Year % 100) == 0) return false;
                        else return true;
		} else return false;

	}
        
        
        
        public static class DateHandler {
            private final String sFileName = "DateHandler.java";
            private static DateHandler dateInstance = null ;
            private Calendar cal = new GregorianCalendar();
            
            private DateHandler(Date aDate) {
                if (aDate == null) aDate = new Date();
                cal.setTime(aDate);
            }
            private DateHandler(String sDate) {
                try {
                    cal.setTime(convertTimeStringToDate(sDate));
                } catch (ParseException ex) {
                    TxtLogger.append(this.sFileName, "DateHandler()","日期类构造函数，出现错误" + 
                            "\r\n                       Exception:" + ex.toString());   
                }
            }
            private DateHandler(String sDate, String aMask) {//增加aMask这个参数，只是为了增加一种构造函数而已，这个参数本身没有任何价值
                try {
                    cal.setTime(convertDateStringToDate(sDate));
                } catch (ParseException ex) {
                    TxtLogger.append(this.sFileName, "DateHandler()","日期类构造函数，出现错误" + 
                            "\r\n                       Exception:" + ex.toString());   
                }
            }
            public static DateHandler getTimeStringInstance(String sDate){
                if (dateInstance == null) dateInstance = new DateHandler(sDate);
                else {
                    dateInstance.setDateTime(sDate);
                } 
                return dateInstance;
            }
            public static DateHandler getDateStringInstance(String sDate){
                if (dateInstance == null) dateInstance = new DateHandler(sDate,null);
                else {
                    dateInstance.setDate(sDate);
                } 
                return dateInstance;
            }
            
            public static DateHandler getDateInstance(Date aDate){
                if (dateInstance == null) dateInstance = new DateHandler(aDate);
                else {
                    if (aDate == null) aDate = new Date();
                    dateInstance.setDate(aDate);
                } 
                return dateInstance;
            }
            
            private void setDateTime(String sDate) {
                try {
                    cal.setTime(convertTimeStringToDate(sDate));
                } catch (Exception ex) {
                    TxtLogger.append(this.sFileName, "setDateTime()","设置日期，出现错误" + 
                            "\r\n                       Exception:" + ex.toString());  
                }
            }
            
            private void setDate(String sDate) {
                try {
                    cal.setTime(convertDateStringToDate(sDate));
                } catch (Exception ex) {
                    TxtLogger.append(this.sFileName, "setDate(String sDate)","设置日期，出现错误" + 
                            "\r\n                       Exception:" + ex.toString());  
                }
            }
            private void setDate(Date aDate) {
                cal.setTime(aDate);
            }
            
            public void nextDay(){
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            public void previousDay(){
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
            public int getYear(){
                return cal.get(Calendar.YEAR);//Integer.parseInt(sDate.substring(0,4));
            }
            
            public int getMonth(){
                //返回的值在 0 和 11 之间，值 0 表示 1 月
                return cal.get(Calendar.MONTH) + 1;//Integer.parseInt(sDate.substring(5,7));
            }
            public int getDay(){
                return cal.get(Calendar.DAY_OF_MONTH);//Integer.parseInt(sDate.substring(8,10));
            }
            
            public int getHour(){
                return cal.get(Calendar.HOUR_OF_DAY);//Integer.parseInt(sDate.substring(11,13));
            }
            
            public int getMinute(){
                return cal.get(Calendar.MINUTE);//Integer.parseInt(sDate.substring(14,16));
            }
            
            public int getSecond(){
                return cal.get(Calendar.SECOND);//Integer.parseInt(sDate.substring(17,19));
            }
            //返回此日期表示的周中的某一天。返回值 (0 = Sunday, 1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday)
            public int getWeekDay(){
                return cal.get(Calendar.DAY_OF_WEEK) - 1;//Integer.parseInt(sDate.substring(17,19));
            }
            public String getWeekDayStr(){
                int WeekDay = getWeekDay();
                switch (WeekDay){
                    case 0:
                        return CommonParas.sWeek7;//"星期日"
                    case 1:
                        return CommonParas.sWeek1;//"星期一"
                    case 2:
                        return CommonParas.sWeek2;//"星期二"
                    case 3:
                        return CommonParas.sWeek3;//"星期三"
                    case 4:
                        return CommonParas.sWeek4;//"星期四"
                    case 5:
                        return CommonParas.sWeek5;//"星期五"
                    case 6:
                        return CommonParas.sWeek6;//"星期六"
                    default:
                        return "";
                }
            }
            //返回日期格式："2016-05-17 00:00:00"
            public String getDateString(){
                return getDateTimeString(cal.getTime());
            }
        }
}
