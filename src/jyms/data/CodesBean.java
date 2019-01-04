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

public class CodesBean {

    private String sCode;
    private String sCodename;
    private String sUppercode;
    private String sRremarks;

    private static final String sFileName = "--->>CodesBean.java";

    public CodesBean() {
    }

    public CodesBean(String code) {
        this.sCode = code;
    }
    public CodesBean(String code,String codename, String uppercode, String remarks) {
        
        this.sCode = code;
        if (code == null) this.sCode = "";
        this.sCodename = codename;
        if (codename == null) this.sCodename = "";
        this.sUppercode = uppercode;
        if (uppercode == null) this.sUppercode = "";
        this.sRremarks = remarks;
        if (remarks == null) this.sRremarks = "";
        
    }
    
    public String getCode() {
        return sCode;
    }

    public void setCode(String code) {
        this.sCode = code;
        if (code == null) this.sCode = "";
    }

    public String getCodename() {
        return sCodename;
    }

    public void setCodename(String codename) {
        this.sCodename = codename;
        if (codename == null) this.sCodename = "";
    }
    
    public String getUpperCode() {
        return sUppercode;
    }

    public void setUpperCode(String uppercode) {
        this.sUppercode = uppercode;
        if (uppercode == null) this.sUppercode = "";
    }
    public String getRemarks() {
        return sRremarks;
    }

    public void setRemarks(String remarks) {
        this.sRremarks = remarks;
        if (remarks == null) this.sRremarks = "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sCode != null ? sCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CodesBean)) {
            return false;
        }
        CodesBean other = (CodesBean) object;
        if ((this.sCode == null && other.sCode != null) || (this.sCode != null && !this.sCode.equals(other.sCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jyms.data.Codes[ code=" + sCode + " ]";
    }
    /**
        *函数:      setRemarks
        *函数描述:  将该设备类型设置为使用或不使用
        * @param Code	 设备类型代码
        * @param Remarks    备注的值（该设备类型是否使用1使用0不使用）
        * @param FileName       调用该函数的文件名
        * @return int：修改成功返回1；未作任何修改返回0；参数错误或者其他错误，返回-1
    */
    public static int setRemarks(String Code,String Remarks,String FileName){
        try {
            if (Code == null || Code.equals("")) return -1;
            if (Remarks == null || Remarks.equals("")) return -1;
            if (FileName == null || FileName.equals("")) return -1;
            String SqlUpdate = "update Codes set REMARKS = '" + Remarks + "' where code = '"  + Code +"'";
            int Count = InitDB.getInitDB(FileName + sFileName).executeUpdate(SqlUpdate);
            return Count;
        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "setRemarks()","用户在将该设备类型设置为使用或不使用的过程中，出现错误"
                            +  "\r\n                       Exception:" + e.toString()
                            + "\r\n                       各个参数值为：" + Code + ";" + Remarks + ";" + FileName + ";");  
            return -1;
        }
    }
    /**
        *函数:      setRemarksList
        *函数描述:  将该设备类型设置为使用或不使用
        * @param CodesList	 设备类型代码列表
        * @param Remarks    备注的值（该设备类型是否使用1使用0不使用）
        * @param FileName       调用该函数的文件名
        * @return int：修改成功返回修改的行数n>0；未作任何修改返回0；参数错误或者其他错误，返回-1
    */
    public static int setRemarksList(ArrayList CodesList,String Remarks,String FileName){
        try {
            if (CodesList == null || CodesList.equals("")) return -1;
            if (Remarks == null || Remarks.equals("")) return -1;
            if (FileName == null || FileName.equals("")) return -1;
            String InCodesList = "('";
            for (int i=0;i<CodesList.size();i++){
                if (i == 0) InCodesList += (String)CodesList.get(i);
                else InCodesList = InCodesList + "','" + (String)CodesList.get(i);
            }
            InCodesList = InCodesList + "')";
            String SqlUpdate = "update Codes set REMARKS = '" + Remarks + "' where code in "  + InCodesList;
            int Count = InitDB.getInitDB(FileName + sFileName).executeUpdate(SqlUpdate);
            return Count;
        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "setRemarks()","用户在将该设备类型设置为使用或不使用的过程中，出现错误"
                            +  "\r\n                       Exception:" + e.toString()
                            + "\r\n                       各个参数值为：" + CodesList.toString() + ";" + Remarks + ";" + FileName + ";");  
            return -1;
        }
    }
    /**
	 * 函数:      getNewMaxCode
         * 函数描述:  产生代码表中的某一类代码的最大代码
         * @param UpperCode    上级代码
         * @param FileName     调用该函数的文件名
         * @return String     某一类代码新的最大代码
    */
    public static String getNewMaxCode(String UpperCode,String FileName){
        String MaxCode = null;
        if (UpperCode == null || UpperCode.equals("")) return null;
        String sqlQuery = "select MAX(CODE) from CODES where UPPERCODE = '" + UpperCode + "'";
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;

        try {
            if(rs.next()) {
                MaxCode = rs.getString(1);
                int iCode = Integer.parseInt("1" + MaxCode) + 1;
                String sTemp = Integer.toString(iCode);
                MaxCode = UpperCode.substring(0, 3) + sTemp.substring(sTemp.length()-3, sTemp.length());
            }
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getNewMaxCode()","用户在产生代码表中的某一类代码的最大代码过程中，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getNewMaxCode()","用户在用户在捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (NumberFormatException e){
            TxtLogger.append(FileName + sFileName, "getNewMaxCode()","用户在产生代码表中的某一类代码的最大代码过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        
        return MaxCode;
    }
    /**
	 * 函数:      getCodesList
         * 函数描述:  获取代码表中的某一类代码的“代码”,“代码名称”,“上级代码”,“备注”等参数
         * @param UpperCode    上级代码
         * @param Remarks      该代码是否被使用。1则代表已使用；0代表没有被使用。
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList<CodesBean> getCodesList(String UpperCode,String Remarks,String FileName) {
        ArrayList<CodesBean> listCodesBean = new ArrayList<CodesBean>();
        String sqlQuery;
        if (UpperCode == null || UpperCode.equals("") || UpperCode.equals("all")){
            sqlQuery = "select * from CODES   order by CODE";
        }else if (Remarks!= null && Remarks.equals("1")){
            sqlQuery = "select * from CODES where UPPERCODE = '" + UpperCode + "' and REMARKS = '1' order by CODE";
        }else {
            sqlQuery = "select * from CODES where UPPERCODE = '" + UpperCode + "'  order by CODE";
        }
            
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;
        
        try {
            while(rs.next()) {
                CodesBean CodesBean = new CodesBean(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)) ;
                listCodesBean.add(CodesBean);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName + sFileName, "getCodesList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getCodesList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getCodesList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getCodesList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getCodesList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getCodesList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getCodesList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
         return listCodesBean;
    }
}
