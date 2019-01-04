/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author John
 */

public class SubUserAuthoritysBean {

 
    private String username;
    private String authorityitem;
    private String deviceserialno;
    private String ifhave;
    private String remarks;
    private static final String sFileName = "--->>SubUserAuthoritysBean.java";
 
    public SubUserAuthoritysBean() {
    }
    public SubUserAuthoritysBean( String username, String authorityitem,String deviceserialno,String ifhave) {

        this.username = username;
        this.authorityitem = authorityitem;
        this.deviceserialno = deviceserialno;
        this.ifhave = ifhave;
    }
    public SubUserAuthoritysBean( String username, String authorityitem,String deviceserialno,String ifhave,String remarks) {

        this.username = username;
        this.authorityitem = authorityitem;
        this.deviceserialno = deviceserialno;
        this.ifhave = ifhave;
        this.remarks = remarks;
    }

   

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthorityitem() {
        return authorityitem;
    }

    public void setAuthorityitem(String authorityitem) {
        this.authorityitem = authorityitem;
    }

    public String getDeviceserialno() {
        return deviceserialno;
    }

    public void setDeviceserialno(String deviceserialno) {
        this.deviceserialno = deviceserialno;
    }
    
    /**
     * @return the ifhave
     */
    public String getIfhave() {
        return ifhave;
    }

    /**
     * @param ifhave the ifhave to set
     */
    public void setIfhave(String ifhave) {
        this.ifhave = ifhave;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    /**
	 * 函数:      getSubUserAuthoritysList
         * 函数描述:  获取用户附加权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息，其中,'0'表示非新添设备，,'1'表示新添设备，主要是为编程需要
         * @param UserName    用户名
         * @param AuthorityType    权限类型;对应标准用户表中的用户类型
         * @param FileName     调用该函数的文件名
         * @return ArrayList 
    */
    public static ArrayList getSubUserAuthoritysList(String UserName,String AuthorityType,String FileName) {
        if (UserName == null || UserName.equals("")) return null;
        if (AuthorityType == null || AuthorityType.equals("")) return null;
        if (FileName == null || FileName.equals("")) return null;
        String sqlQuery;
        /*因为用户附加权限表中不存在超级管理员用户，所以可以这样写代码。只分为两级：管理员和操作员*/
        if (AuthorityType.equals(jyms.CommonParas.USER_TYPECODE_MANAGER) || AuthorityType.equals("all")){
            sqlQuery = "select SUBUSERAUTHORITYS.USERNAME, AUTHORITYITEM, DEVICESERIALNO,DEVICEPARA.ANOTHERNAME,IFHAVE,SUBUSERAUTHORITYS.REMARKS,'0' from SUBUSERAUTHORITYS,DEVICEPARA  "
                        + "where SUBUSERAUTHORITYS.DEVICESERIALNO = DEVICEPARA.SERIALNO and SUBUSERAUTHORITYS.UserName = '" + UserName 
                        + "'  order by SUBUSERAUTHORITYS.UserName,Authorityitem, DEVICESERIALNO";
        }else {
            sqlQuery = "select SUBUSERAUTHORITYS.USERNAME, AUTHORITYITEM, DEVICESERIALNO,DEVICEPARA.ANOTHERNAME,IFHAVE,SUBUSERAUTHORITYS.REMARKS,'0' from SUBUSERAUTHORITYS,DEVICEPARA  "
                        + "where SUBUSERAUTHORITYS.DEVICESERIALNO = DEVICEPARA.SERIALNO and SUBUSERAUTHORITYS.UserName = '" + UserName  
                        + "' and remarks = '" + AuthorityType + "'  order by SUBUSERAUTHORITYS.UserName,Authorityitem, DEVICESERIALNO";
        }
        
      
        return InitDB.getInitDB(FileName + sFileName).executeQueryList(sqlQuery) ;
    }
    /**
	 * 函数:      getSubUserAuthoritysList
         * 函数描述:  获取用户附加权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息
         * @param UserName    用户名
         * @param FileName     调用该函数的文件名
         * @return ArrayList 
    */
    public static ArrayList getSubUserAuthoritysList(String UserName,String FileName) {
        return getSubUserAuthoritysList(UserName,"all",FileName);
    }    
    
    /**
	 * 函数:      getAuthorityDeviceNums
         * 函数描述:  获取该用户附加权限所对应的设备数（主要用户新添加设备后，重新修改用户的设备权限）
         * @param UserName    用户名
         * @param FileName     调用该函数的文件名
         * @return ArrayList 
    */
    public static int getAuthorityDeviceNums(String UserName,String FileName){
        String QueryCommand  = "SELECT count(distinct deviceserialno) FROM SUBUSERAUTHORITYS where username = '"+UserName +"'";
        return InitDB.getInitDB(FileName + sFileName).getNums(QueryCommand) ;
    }
    
    /**
	 * 函数:      getNewSubUserAuthoritysList
         * 函数描述:  根据新添设备，获取标准权限表中的权限项目、用户类型，生成新的用户附加权限列表
         *            主要包括：用户名、权限项目、设备序列号、设备别名、有否、备注、是否新添设备等信息，其中,是否新添设备等信息'0'表示非新添设备，,'1'表示新添设备，主要是为编程需要设备序列号、设备别名、有否、备注等信息，其中,'0'表示非新添设备，,'1'表示新添设备，主要是为编程需要
         * @param UserName    用户名
         * @param FileName    调用该函数的文件名
         * @return ArrayList 
    */
    public static ArrayList getNewSubUserAuthoritysList(String UserName, String FileName) {
        if (UserName == null || UserName.equals("")) return null;
        if (FileName == null || FileName.equals("")) return null;
        
        String sqlQuery = "select '"+UserName+"', a.AUTHORITYITEM, b.SERIALNO, b.ANOTHERNAME, '0', a.USERTYPE, '1'" 
                        +" from STANDARDAUTHORITYS a, DEVICEPARA b" 
                        +" where a.IFSUBDIVISION = '1' and b.SERIALNO not in  (select distinct(DEVICESERIALNO) from SUBUSERAUTHORITYS where USERNAME = '"+UserName+"')"; 
        
        return InitDB.getInitDB(FileName + sFileName).executeQueryList(sqlQuery) ;
    }
    
    public static String getStringInsertCommand(String UserName, String Authorityitem,String DeviceSerialno,String IfHave,String Remarks){
        String InsertCommand  = "INSERT INTO SUBUSERAUTHORITYS (USERNAME, AUTHORITYITEM, DEVICESERIALNO, IFHAVE, REMARKS) VALUES ('" 
                            + UserName + "', '" + Authorityitem + "', '" + DeviceSerialno + "','" + IfHave + "', '" + Remarks + "')\r\n";
        return InsertCommand;
    }
    
    public static String getStringUpdateCommand(String UserName, String Authorityitem,String DeviceSerialno,String IfHave){
        String UpdateCommand  = "UPDATE SUBUSERAUTHORITYS SET  IFHAVE = '"+ IfHave + "' WHERE USERNAME = '"+ UserName + "' and  AUTHORITYITEM = '"+ Authorityitem + "' and  DEVICESERIALNO = '"+ DeviceSerialno + "'";
        return UpdateCommand;
    }
 
    public static String getStringDeleteCommand(String UserName){
        String UpdateCommand  = "delete  from SUBUSERAUTHORITYS  where UserName ='" + UserName + "'";
        return UpdateCommand;
    }
}
