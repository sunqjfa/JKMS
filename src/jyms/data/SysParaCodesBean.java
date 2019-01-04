/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author John
 */

public class SysParaCodesBean {

    private static final long serialVersionUID = 1L;

    private String sysparaclasscode;
    private String sysparacode;
    private String sysparaname;
    private String sysparavalue;
    private String remarks = "";
    
    private static final String FILE_NAME = "--->>SysParaCodesBean.java";

    public SysParaCodesBean() {
    }

    public SysParaCodesBean(String Sysparacode) {
        this.sysparacode = Sysparacode;
    }

    public SysParaCodesBean(String Sysparaclasscode, String Sysparacode, String Sysparaname, String Sysparavalue) {
        this.sysparacode = Sysparacode;
        this.sysparaclasscode = Sysparaclasscode;
        this.sysparaname = Sysparaname;
        this.sysparavalue = Sysparavalue;
    }
    
    public SysParaCodesBean(String Sysparaclasscode, String Sysparacode, String Sysparaname, String Sysparavalue,String Remarks) {
        this.sysparacode = Sysparacode;
        this.sysparaclasscode = Sysparaclasscode;
        this.sysparaname = Sysparaname;
        this.sysparavalue = Sysparavalue;
        this.remarks = Remarks==null?"":Remarks;
    }

    public String getSysparaclasscode() {
        return sysparaclasscode;
    }

    public void setSysparaclasscode(String sysparaclasscode) {
        this.sysparaclasscode = sysparaclasscode;
    }

    public String getSysparacode() {
        return sysparacode;
    }

    public void setSysparacode(String sysparacode) {
        this.sysparacode = sysparacode;
    }

    public String getSysparaname() {
        return sysparaname;
    }

    public void setSysparaname(String sysparaname) {
        this.sysparaname = sysparaname;
    }

    public String getSysparavalue() {
        return sysparavalue;
    }

    public void setSysparavalue(String sysparavalue) {
        this.sysparavalue = sysparavalue;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sysparacode != null ? sysparacode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SysParaCodesBean)) {
            return false;
        }
        SysParaCodesBean other = (SysParaCodesBean) object;
        if ((this.sysparacode == null && other.sysparacode != null) || (this.sysparacode != null && !this.sysparacode.equals(other.sysparacode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jyms.data.SysParaCodesBean[ sysparacode=" + sysparacode + " ]";
    }
    
    public static ArrayList getSysParaList(String SysParaClsssCode,String FileName){
        String sqlQuery;
        if (SysParaClsssCode == null || SysParaClsssCode.equals("") || SysParaClsssCode.equals("all")){
            sqlQuery = "SELECT a.*, b.CODENAME as sysparaclassname  FROM SYSPARACODES a,CODES b  where a.sysparaclasscode = b.code order by a.sysparaclasscode, a.sysparacode";
        }else {
            sqlQuery = "select a.*, b.CODENAME as sysparaclassname  FROM SYSPARACODES a,CODES b  where a.sysparaclasscode = b.code and sysparaclasscode = '" + SysParaClsssCode + "'  order by a.sysparaclasscode, a.sysparacode";
        }
        
        ArrayList listSysPara = new ArrayList();
        ResultSet rs = InitDB.getInitDB(FileName + FILE_NAME).getRs(sqlQuery) ;
        try {
            while(rs.next()) {
                SysParaCodesBean deviceGroupBean = new SysParaCodesBean(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)) ;
                ArrayList newList = new ArrayList();
                
                newList.add(deviceGroupBean);
                String ss= rs.getString("sysparaclassname");
                newList.add(ss==null?"":ss);

                listSysPara.add(newList);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName, "getSysParaList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + FILE_NAME, "getSysParaList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + FILE_NAME, "getSysParaList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + FILE_NAME, "getSysParaList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + FILE_NAME, "getSysParaList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + FILE_NAME, "getSysParaList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getSysParaList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listSysPara;
    }
    
    
    public static String getStringUpdateCommand(String SysParaCode, String SysParaValue){
        String UpdateCommand  = "UPDATE SYSPARACODES SET SYSPARAVALUE = '" + SysParaValue + "' WHERE SYSPARACODE = '" + SysParaCode + "'" ;
        return UpdateCommand;
    }

    public static int batchInsertUpdate(ArrayList<String> ListUpdateStr,String FileName ){
        return InitDB.getInitDB(FileName + FILE_NAME).batchExecuteUpdate(ListUpdateStr);
    }
    
    /**
	 * 函数:      getParaValue
         * 函数描述:  获取某一个系统参数的值
         * @param SysParaCode   系统参数代码
         * @param FileName             调用该函数的文件名
         * @return String：操作成功返回系统参数值；失败，返回""
    */
    public static String getParaValue(String SysParaCode, String FileName){
        if (FileName == null || FileName.equals("")) return "";
        if (SysParaCode == null || SysParaCode.equals("")) return "";
        String Sql = "select sysparavalue  FROM SYSPARACODES where sysparacode = '" + SysParaCode + "'";
        return InitDB.getInitDB(FileName + FILE_NAME).executeQueryOneCol(Sql);
    }
    /**
	 * 函数:      updateSysParaValue
         * 函数描述:  修改某一个系统参数的值
         * @param SysParaCode   系统参数代码
         * @param SysParaValue  系统参数值
         * @param FileName             调用该函数的文件名
         * @return int：操作成功放回1；失败，返回0；出现其他错误返回-1
    */
    public static int updateSysParaValue(String SysParaCode, String SysParaValue, String FileName){
       try {
            if (SysParaCode == null || SysParaCode.equals("")) return -1;
            if (SysParaValue == null || SysParaValue.equals("")) return -1;
            if (FileName == null || FileName.equals("")) return -1;
            
            String SqlUpdate = "UPDATE SYSPARACODES SET SYSPARAVALUE = '" + SysParaValue + "' WHERE SYSPARACODE = '" + SysParaCode + "'";
            return InitDB.getInitDB(FileName + FILE_NAME).executeUpdate(SqlUpdate);
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "updateSysParaValue()","用户在修改设备布防时间模板表的过程中，出现错误"
                            +  "\r\n                       Exception:" + e.toString() + ";");  
            return -1;
        }
    }
}
