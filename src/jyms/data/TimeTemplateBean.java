/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author John
 */

public class TimeTemplateBean {

    private static final long serialVersionUID = 1L;
    
//    private int ID;//自动增长ID
    private String templatename;//模板名称
    private byte weekday;//周几
    private byte starthour;//起始时
    private byte startminute;//起始分
    private byte stophour;//终止时
    private byte stopminute;//终止分
    private String remark = "";//备注
    //数据库长度限制，模板名称长度不能>20
    public static final int TEMPLATENAME_LENGTH = 20;
    private static final String sFileName = "--->>TimetemplateBean.java";

    public TimeTemplateBean() {
    }


    public TimeTemplateBean(String templatename, byte weekday, byte starthour, byte startminute, byte stophour, byte stopminute) {

        this.templatename = templatename;
        this.weekday = weekday;
        this.starthour = starthour;
        this.startminute = startminute;
        this.stophour = stophour;
        this.stopminute = stopminute;
    }
    public TimeTemplateBean(String templatename, byte weekday, byte starthour, byte startminute, byte stophour, byte stopminute, String remark) {

        this.templatename = templatename;
        this.weekday = weekday;
        this.starthour = starthour;
        this.startminute = startminute;
        this.stophour = stophour;
        this.stopminute = stopminute;
        this.remark = remark;
    }
//    public TimeTemplateBean(int ID,String templatename, byte weekday, byte starthour, byte startminute, byte stophour, byte stopminute,String remark) {
//        this.ID = ID;
//        this.templatename = templatename;
//        this.weekday = weekday;
//        this.starthour = starthour;
//        this.startminute = startminute;
//        this.stophour = stophour;
//        this.stopminute = stopminute;
//        this.remark = remark;
//    }
//    private int getID() {
//        return ID;
//    }
    public String getTemplatename() {
        return templatename;
    }

    public void setTemplatename(String templatename) {
        this.templatename = templatename;
    }

    public byte getWeekday() {
        return weekday;
    }

    public void setWeekday(byte weekday) {
        this.weekday = weekday;
    }

    public byte getStarthour() {
        return starthour;
    }

    public void setStarthour(byte starthour) {
        this.starthour = starthour;
    }

    public byte getStartminute() {
        return startminute;
    }

    public void setStartminute(byte startminute) {
        this.startminute = startminute;
    }

    public byte getStophour() {
        return stophour;
    }

    public void setStophour(byte stophour) {
        this.stophour = stophour;
    }

    public byte getStopminute() {
        return stopminute;
    }

    public void setStopminute(byte stopminute) {
        this.stopminute = stopminute;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    /**
	 * 函数:      getIfExistTheTemplate
         * 函数描述:    检索是否存在该时间模板
	 * @param TimeTemplate	 时间模板名称
         * @param FileName             调用该函数的文件名
         * @return int：存在返回>=1；不存在，返回0；出现其他错误返回-1
    */
    public static int getIfExistTheTemplate(String TimeTemplate,String FileName){
        if (TimeTemplate == null || TimeTemplate.equals("")) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        
        String sqlQuery = "SELECT count(templatename)  FROM TIMETEMPLATE where templatename = '" + TimeTemplate + "'";
        String sCount = InitDB.getInitDB(FileName + sFileName).executeQueryOneCol(sqlQuery);
        
        if (sCount == null ||sCount.equals(""))  return -1;
        try {
            return Integer.parseInt(sCount);
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getIfExistTheTemplate()","系统在检索是否存在该时间模板过程中，出现错误" 
                                      + "\r\n                       Exception:" + e.toString());  
        }
        return -1;
    }
    /**
	 * 函数:      getTemplateNameList
         * 函数描述:  获取时间模板表中的“模板名”列表
         * @param FileName     调用该函数的文件名
         * @return ArrayList     如果出错，返回NULL
    */
    public static ArrayList<String> getTemplateNameList(String FileName) {
        String sqlQuery = "SELECT distinct templatename  FROM TIMETEMPLATE";
        return InitDB.getInitDB(FileName + sFileName).executeQueryOneColList(sqlQuery) ;
    }
    /**
	 * 函数:      getDeviceGroupList
         * 函数描述:  获取时间模板表中的“ID,模板名称、周几、起始时、起始分、终止时、终止分、备注”等参数
         * @param TemplateName    模板名
         * @param FileName     调用该函数的文件名
         * @return ArrayList 
    */
    public static ArrayList<TimeTemplateBean> getTimeTemplateList(String TemplateName,String FileName) {
        ArrayList<TimeTemplateBean> listTimeTemplateBean = new ArrayList<TimeTemplateBean>();
        String sqlQuery;
        if (TemplateName == null || TemplateName.equals("")) return null;
        if (FileName == null || FileName.equals("")) return null;
        
        sqlQuery = "select TEMPLATENAME, WEEKDAY, STARTHOUR, STARTMINUTE, STOPHOUR, STOPMINUTE, REMARK from TimeTemplate  where TEMPLATENAME = '" + TemplateName + "'";
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;
        try {
            while(rs.next()) {
                TimeTemplateBean TimeTemplateBean = new TimeTemplateBean(rs.getString(1),rs.getByte(2),rs.getByte(3),rs.getByte(4),rs.getByte(5),rs.getByte(6),rs.getString(7)) ;
                listTimeTemplateBean.add(TimeTemplateBean);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName + sFileName, "getTimeTemplateList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getTimeTemplateList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getTimeTemplateList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getTimeTemplateList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getTimeTemplateList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getTimeTemplateList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getTimeTemplateList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listTimeTemplateBean;
    }
    
    public static String getStringInsertCommand(TimeTemplateBean TimeTemplate){
        String InsertCommand  = "INSERT INTO TimeTemplate(TEMPLATENAME, WEEKDAY, STARTHOUR, STARTMINUTE, STOPHOUR, STOPMINUTE) VALUES ('" 
                            + TimeTemplate.getTemplatename() + "', " + TimeTemplate.getWeekday() + "," 
                            + TimeTemplate.getStarthour() + "," + TimeTemplate.getStartminute()  + ","
                            + TimeTemplate.getStophour()+ "," + TimeTemplate.getStopminute()+ ")";
        return InsertCommand;
    }
    
    public static String getStringUpdateCommand(TimeTemplateBean TimeTemplate){
        String UpdateCommand  = "UPDATE TIMETEMPLATE SET STARTHOUR = "+TimeTemplate.getStarthour()+", STARTMINUTE = "+TimeTemplate.getStartminute()
                            +", STOPHOUR = "+TimeTemplate.getStophour()+", STOPMINUTE = "+TimeTemplate.getStopminute()
                            +" WHERE templatename = '" + TimeTemplate.getTemplatename() + "' and weekday =" + TimeTemplate.getWeekday();
        return UpdateCommand;
    }
    
    public static String getStringDeleteCommand(String Templatename){
        String UpdateCommand  = "delete  from TimeTemplate  where templatename =" + Templatename;
        return UpdateCommand;
    }
    
    public static int batchInsertUpdate(ArrayList<String> ListInsertStr,String FileName ){
        return InitDB.getInitDB(FileName + sFileName).batchExecuteUpdate(ListInsertStr);
    }
    
}
