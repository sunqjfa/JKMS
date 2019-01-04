/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.data;

import java.io.Serializable;

/**
 *
 * @author John
 */

public class DevTimeTemplateBean {

    private String sSerialNO;//设备序列号
    private String sTemplateClass;//模板用途分类
    private int iSubResourceNO = -1;//代表编码通道（监控点）的序号、报警输入的序号、报警输出的序号。没有的话，则默认为-1
    private String sAlarmTypeName;//报警类型名称
    private String sTemplateName;//时间模板名称
    private String sRemark = "";
    
    private static final String sFileName = "--->>DevTimeTemplateBean.java";

    public DevTimeTemplateBean() {
    }

    public DevTimeTemplateBean(String serialno,String templateClass, int subresourceno,String alarmTypeName,String templatename) {
        this(serialno,templateClass, subresourceno,alarmTypeName,templatename,"");
    }
    
    public DevTimeTemplateBean(String serialno,String templateClass, int subresourceno,String alarmTypeName,String templatename,String remark) {
        this.sSerialNO = serialno;
        this.sTemplateClass = templateClass;
        this.iSubResourceNO = subresourceno;
        if (subresourceno < 0) this.iSubResourceNO = -1;
        this.sAlarmTypeName = alarmTypeName;
        this.sTemplateName = templatename;
        this.sRemark = remark;
    }
    
    public String getTemplatename() {
        return sTemplateName;
    }

    public void setTemplatename(String templatename) {
        this.sTemplateName = templatename;
    }

    public String getRemark() {
        return sRemark;
    }

    public void setRemark(String remark) {
        this.sRemark = remark;
    }

    /**
     * @return the SerialNO
     */
    public String getSerialNO() {
        return sSerialNO;
    }

    /**
     * @param SerialNO the SerialNO to set
     */
    public void setSerialNO(String SerialNO) {
        this.sSerialNO = SerialNO;
    }
  
    
    /**
     * @return the SubResourceNO
     */
    public int getSubResourceNO() {
        return iSubResourceNO;
    }

    /**
     * @param SubResourceNO the SubResourceNO to set
     */
    public void setSubResourceNO(int SubResourceNO) {
        this.iSubResourceNO = SubResourceNO;
    }
    
    /**
     * @return the TemplateClass
     */
    public String getTemplateClass() {
        return sTemplateClass;
    }

    /**
     * @param TemplateClass the TemplateClass to set
     */
    public void setTemplateClass(String TemplateClass) {
        this.sTemplateClass = TemplateClass;
    }
    /**
     * @return the AlarmTypeName
     */
    public String getAlarmTypeName() {
        return sAlarmTypeName;
    }

    /**
     * @param AlarmTypeName the AlarmTypeName to set
     */
    public void setAlarmTypeName(String AlarmTypeName) {
        this.sAlarmTypeName = AlarmTypeName;
    }
    
    /**
	 * 函数:      getTemplateName
         * 函数描述:  获取设备时间模板表中的“模板名”
         * @param SerialNO     调用该函数的文件名
         * @param TemplateClass     调用该函数的文件名
         * @param SubResourceNO     调用该函数的文件名
         * @param FileName     调用该函数的文件名
         * @return ArrayList     如果出错，返回NULL
    */
    public static String getTemplateName(String SerialNO,String TemplateClass,int SubResourceNO,String AlarmTypeName,String FileName) {
        if (SerialNO == null || SerialNO.equals("")) return null;
        if (TemplateClass == null || TemplateClass.equals("")) return null;
        if (FileName == null || FileName.equals("")) return null;
        
        String sqlQuery = "SELECT templatename  FROM DevTimeTemplate where SerialNO = '" +SerialNO
                + "' and TemplateClass = '"  +TemplateClass+ "' and  SubResourceNO = " + SubResourceNO 
                + "  AND ALARMTYPENAME = '" + AlarmTypeName + "'";
        return InitDB.getInitDB(FileName + sFileName).executeQueryOneCol(sqlQuery) ;
    }
    /**
	 * 函数:      insertDevTimeTemplate
         * 函数描述:    向设备布防时间模板表添加一条数据
	 * @param devTimeTemplateBean	 设备布防时间模板类
         * @param FileName             调用该函数的文件名
         * @return int：操作成功放回1；失败，返回0；出现其他错误返回-1
    */
    public static int insertDevTimeTemplate(DevTimeTemplateBean devTimeTemplateBean,String FileName){
        try {
            if (devTimeTemplateBean == null) return -1;
            if (FileName == null || FileName.equals("")) return -1;
            String SqlInsert = "INSERT INTO DEVTIMETEMPLATE (SERIALNO, TemplateClass, SUBRESOURCENO, ALARMTYPENAME,TEMPLATENAME, REMARK) " 
                    +"	VALUES ('"+devTimeTemplateBean.getSerialNO()+"', '"+devTimeTemplateBean.getTemplateClass()
                    +"',"+devTimeTemplateBean.getSubResourceNO()+", '"+devTimeTemplateBean.getAlarmTypeName()
                    +"', '"+devTimeTemplateBean.getTemplatename()+"', '')";
            return InitDB.getInitDB(FileName + sFileName).executeUpdate(SqlInsert);
        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "insertDevTimeTemplate()","用户在填写设备布防时间模板表的过程中，出现错误"
                            +  "\r\n                       Exception:" + e.toString() + ";");  
            return -1;
        }
    }
    /**
	 * 函数:      updateDevTimeTemplate
         * 函数描述:    向设备布防时间模板表修改一条数据
	 * @param devTimeTemplateBean	 设备布防时间模板类
         * @param FileName             调用该函数的文件名
         * @return int：操作成功放回1；失败，返回0；出现其他错误返回-1
    */
    public static int updateDevTimeTemplate(DevTimeTemplateBean devTimeTemplateBean,String FileName){
       try {
            if (devTimeTemplateBean == null) return -1;
            if (FileName == null || FileName.equals("")) return -1;
            String SqlInsert = "UPDATE DEVTIMETEMPLATE SET TEMPLATENAME = '"+devTimeTemplateBean.getTemplatename()
                    + "' WHERE SERIALNO = '"+devTimeTemplateBean.getSerialNO()+"' AND TemplateClass = '"+devTimeTemplateBean.getTemplateClass()
                    +"' AND SUBRESOURCENO = "+devTimeTemplateBean.getSubResourceNO() 
                    + " AND ALARMTYPENAME = '" + devTimeTemplateBean.getAlarmTypeName()+ "'";
            return InitDB.getInitDB(FileName + sFileName).executeUpdate(SqlInsert);
        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "updateDevTimeTemplate()","用户在修改设备布防时间模板表的过程中，出现错误"
                            +  "\r\n                       Exception:" + e.toString() + ";");  
            return -1;
        }
    }
   
    /**
	 * 函数:      saveDevTimeTemplate
         * 函数描述:    向设备布防时间模板表添加或修改一条数据
	 * @param devTimeTemplateBean	 设备布防时间模板类
         * @param FileName             调用该函数的文件名
         * @return int：操作成功放回1；失败，返回0；出现其他错误返回-1
    */
    public static int saveDevTimeTemplate(DevTimeTemplateBean devTimeTemplateBean,String FileName){
       try {
            if (devTimeTemplateBean == null) return -1;
            if (FileName == null || FileName.equals("")) return -1;
            String TemplateName = getTemplateName(devTimeTemplateBean.getSerialNO(),devTimeTemplateBean.getTemplateClass() ,devTimeTemplateBean.getSubResourceNO() , devTimeTemplateBean.getAlarmTypeName(),FileName);
            if (TemplateName == null) return insertDevTimeTemplate( devTimeTemplateBean, FileName);
            else return updateDevTimeTemplate( devTimeTemplateBean, FileName);
            
        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "saveDevTimeTemplate()","用户在修改设备布防时间模板表的过程中，出现错误"
                            +  "\r\n                       Exception:" + e.toString() + ";");  
            return -1;
        }
    }

    
    
}
