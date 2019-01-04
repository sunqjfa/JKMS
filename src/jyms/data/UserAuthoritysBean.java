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

public class UserAuthoritysBean {

    private String username;
    private String authorityitem;
    private String ifhave;
    private String remarks="";
    private String authorityitemRemarks="";//不存在数据表中，但是只是为了编程，是权限项目的翻译形式
    private static final String sFileName = "--->>UserAuthoritysBean.java";

    public UserAuthoritysBean() {
    }
    public UserAuthoritysBean( String username, String authorityitem, String ifhave) {
        this.username = username;
        this.authorityitem = authorityitem;
        this.ifhave = ifhave;
    }

    public UserAuthoritysBean( String username, String authorityitem, String ifhave,String remarks) {

        this.username = username;
        this.authorityitem = authorityitem;
        this.ifhave = ifhave;
        this.remarks = remarks;
    }
    
    public UserAuthoritysBean( String username, String authorityitem, String ifhave,String remarks, String authorityitemRemarks) {

        this.username = username;
        this.authorityitem = authorityitem;
        this.ifhave = ifhave;
        this.remarks = remarks;
        this.authorityitemRemarks=authorityitemRemarks;
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

    public String getIfhave() {
        return ifhave;
    }

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
     * @return the authorityitemRemarks
     */
    public String getAuthorityitemRemarks() {
        return authorityitemRemarks;
    }

    /**
     * @param authorityitemRemarks the authorityitemRemarks to set
     */
    public void setAuthorityitemRemarks(String authorityitemRemarks) {
        this.authorityitemRemarks = authorityitemRemarks;
    }
    
    
    
    /**
	 * 函数:      getUserAuthoritysList
         * 函数描述:  获取用户权限表中的用户名、权限项目、有无权限、备注等信息
         * @param UserName    用户名
         * @param AuthorityType    权限类型
         * @param FileName     调用该函数的文件名
         * @return ArrayList 
    */
    public static ArrayList<UserAuthoritysBean> getUserAuthoritysList(String UserName, String AuthorityType, String FileName) {
        if (UserName == null || UserName.equals("")) return null;
        if (AuthorityType == null || AuthorityType.equals("")) return null;
        String sqlQuery;
        ArrayList<UserAuthoritysBean> listUserAuthoritysBean = new ArrayList<>();

        if (AuthorityType.equals(jyms.CommonParas.USER_TYPECODE_MANAGER) || AuthorityType.equals("all")){
            sqlQuery = "select a.USERNAME, a.AUTHORITYITEM, a.IFHAVE,a.REMARKS,b.REMARKS from UserAuthoritys a,StandardAuthoritys b where a.AUTHORITYITEM=b.AUTHORITYITEM and UserName = '" + UserName + "'  order by a.REMARKS,a.Authorityitem";
        }else {
            sqlQuery = "select a.USERNAME, a.AUTHORITYITEM, a.IFHAVE,a.REMARKS,b.REMARKS from UserAuthoritys a,StandardAuthoritys b where a.AUTHORITYITEM=b.AUTHORITYITEM and UserName = '" + UserName + "' and a.remarks = '" + AuthorityType + "'  order by a.REMARKS,a.Authorityitem";
        }
        
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;
        try {
            while(rs.next()) {
                UserAuthoritysBean userAuthoritysBean = new UserAuthoritysBean(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)) ;
                listUserAuthoritysBean.add(userAuthoritysBean);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName + sFileName, "getUserAuthoritysList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getUserAuthoritysList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getUserAuthoritysList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getUserAuthoritysList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getUserAuthoritysList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getUserAuthoritysList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getUserAuthoritysList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listUserAuthoritysBean;
    }
    /**
	 * 函数:      getUserAuthoritysList
         * 函数描述:  获取用户权限表中的用户名、权限项目、有无权限、备注等信息
         * @param UserName    用户名
         * @param FileName     调用该函数的文件名
         * @return ArrayList 
    */
    public static ArrayList<UserAuthoritysBean> getUserAuthoritysList(String UserName,String FileName) {
        return getUserAuthoritysList(UserName,"all",FileName);
    }
    
    public static String getStringInsertCommand(String UserName, String Authorityitem, String IfHave,String Remarks){
        String InsertCommand  = "INSERT INTO USERAUTHORITYS (USERNAME, AUTHORITYITEM, IFHAVE, REMARKS) VALUES ('" 
                            + UserName + "', '" + Authorityitem + "', '" + IfHave + "','" + Remarks + "')";
        return InsertCommand;
    }
    
    public static String getStringUpdateCommand(String UserName, String Authorityitem, String IfHave){
        String UpdateCommand  = "UPDATE USERAUTHORITYS SET IFHAVE = '" + IfHave + "' WHERE USERNAME = '" + UserName + "' and  AUTHORITYITEM = '" + Authorityitem + "'" ;
        return UpdateCommand;
    }
    
    public static String getStringDeleteCommand(String UserName){
        String UpdateCommand  = "delete  from USERAUTHORITYS  where UserName ='" + UserName + "'";
        return UpdateCommand;
    }
    
    public static int batchInsertUpdate(ArrayList<String> ListInsertStr,String FileName ){
        return InitDB.getInitDB(FileName + sFileName).batchExecuteUpdate(ListInsertStr);
    }
    
    public static int batchDeleteAndInsertUpdate(ArrayList<String> ListDeleteAndInsertStr,String FileName ){
        return InitDB.getInitDB(FileName + sFileName).batchExecuteUpdate(ListDeleteAndInsertStr);
    }
    /**
        * 函数:      copyAuthoritysToOtherUsers
        * 函数描述:  复制用户权限给其他用户
        * @param UserName       被复制权限的用户名
        * @param UserType       被复制权限的用户名
        * @param ListOtherUsers 复制权限的用户名列表
        * @param ListAuthorityTypes 复制权限的权限类型列表
        * @param FileName       调用该函数的文件名
        * @return               成功返回1；失败返回0；错误，返回-1 
    */
    public static int copyAuthoritysToOtherUsers(String UserName, String UserType, ArrayList<String> ListOtherUsers, ArrayList<String> ListAuthorityTypes,  String FileName){
        if (UserName == null || UserName.equals("")) return -1;
        if (ListOtherUsers == null || ListOtherUsers.isEmpty()) return -1;
        if (ListAuthorityTypes == null || ListAuthorityTypes.isEmpty()) return -1;
        if (ListAuthorityTypes.size() != ListAuthorityTypes.size()) return -1;
        
        try{
            ArrayList<String> ListInsertIntoSql = new ArrayList<>();
            for (int i=0;i<ListOtherUsers.size();i++){
                String SqlDlelete = "delete from USERAUTHORITYS where username = '" + ListOtherUsers.get(i) + "'";
                String SqlDlelete2 = "delete from SUBUSERAUTHORITYS where username = '" + ListOtherUsers.get(i) + "'";
                String SqlInsertInto, SqlInsertInto2;
                String AuthorityType = ListAuthorityTypes.get(i);
                //超级管理员用户在用户权限表和附加用户权限表中是没有记录的，所以必须单独处理
                if (UserType.equals(jyms.CommonParas.USER_TYPECODE_ADMIN)){//被复制用户如果是超级管理员用户
                    /*
                        因为有些权限被设置为超级管理员权限，管理员权限就不是全部权限了。
                        不能将超级管理员加进去，所以必须加上限制
                    */
                    SqlInsertInto  = "insert into USERAUTHORITYS(USERNAME, AUTHORITYITEM, IFHAVE, REMARKS) "
                                        + " SELECT '" + ListOtherUsers.get(i) + "', AUTHORITYITEM, '1',USERTYPE "
                                        + " FROM STANDARDAUTHORITYS  where USERTYPE >= '" + AuthorityType + "'";
                    SqlInsertInto2 = "insert into SUBUSERAUTHORITYS(USERNAME, AUTHORITYITEM, DEVICESERIALNO, IFHAVE, REMARKS) "
                                        + " SELECT '" + ListOtherUsers.get(i) + "', AUTHORITYITEM, SERIALNO, '1',USERTYPE "
                                        + " FROM STANDARDAUTHORITYS a, DEVICEPARA b where a.IFSUBDIVISION ='1' and USERTYPE >= '"  + AuthorityType + "'";
//                    if (AuthorityType.equals(jyms.CommonParas.USER_TYPECODE_MANAGER)){
//                        SqlInsertInto  = "insert into USERAUTHORITYS(USERNAME, AUTHORITYITEM, IFHAVE, REMARKS) "
//                                        + " SELECT '" + ListOtherUsers.get(i) + "', AUTHORITYITEM, '1',USERTYPE "
//                                        + " FROM STANDARDAUTHORITYS ";
//                        SqlInsertInto2 = "insert into SUBUSERAUTHORITYS(USERNAME, AUTHORITYITEM, DEVICESERIALNO, IFHAVE, REMARKS) "
//                                        + " SELECT '" + ListOtherUsers.get(i) + "', AUTHORITYITEM, SERIALNO, '1',USERTYPE "
//                                        + " FROM STANDARDAUTHORITYS a, DEVICEPARA b where a.IFSUBDIVISION ='1'";
//                    }else {
//                        //insert into USERAUTHORITYS(USERNAME, AUTHORITYITEM, IFHAVE, REMARKS) 
//                        //        select '李童2', authorityitem, ifhave, remarks  from USERAUTHORITYS  where username = '李广' and remarks = '020300';
//                        SqlInsertInto  = "insert into USERAUTHORITYS(USERNAME, AUTHORITYITEM, IFHAVE, REMARKS) "
//                                        + " SELECT '" + ListOtherUsers.get(i) + "', AUTHORITYITEM, '1',USERTYPE "
//                                        + " FROM STANDARDAUTHORITYS where USERTYPE = '" + AuthorityType + "'";
//                        SqlInsertInto2 = "insert into SUBUSERAUTHORITYS(USERNAME, AUTHORITYITEM, DEVICESERIALNO, IFHAVE, REMARKS) "
//                                        + " SELECT '" + ListOtherUsers.get(i) + "', AUTHORITYITEM, SERIALNO, '1',USERTYPE "
//                                        + " FROM STANDARDAUTHORITYS a, DEVICEPARA b where a.IFSUBDIVISION ='1' and USERTYPE = '"  + AuthorityType + "'";
//                    }
                }else {//被复制用户如果不是超级管理员用户
                    /*因为不是超级管理员用户，所以可以这样写代码。只分为两级：管理员和操作员*/
                    if (AuthorityType.equals(jyms.CommonParas.USER_TYPECODE_MANAGER)){
                        SqlInsertInto  = "insert into USERAUTHORITYS(USERNAME, AUTHORITYITEM, IFHAVE, REMARKS) "
                                        + " select '" + ListOtherUsers.get(i) + "', authorityitem, ifhave, remarks  "
                                        + " from USERAUTHORITYS  where username = '"  + UserName + "'";
                        SqlInsertInto2 = "insert into SUBUSERAUTHORITYS(USERNAME, AUTHORITYITEM, DEVICESERIALNO, IFHAVE, REMARKS) "
                                        + " select '" + ListOtherUsers.get(i) + "', AUTHORITYITEM, DEVICESERIALNO, IFHAVE, REMARKS  "
                                        + " from SUBUSERAUTHORITYS  where username = '"+ UserName + "'";
                    }else {
                        //insert into USERAUTHORITYS(USERNAME, AUTHORITYITEM, IFHAVE, REMARKS) 
                        //        select '李童2', authorityitem, ifhave, remarks  from USERAUTHORITYS  where username = '李广' and remarks = '020300';
                        SqlInsertInto  = "insert into USERAUTHORITYS(USERNAME, AUTHORITYITEM, IFHAVE, REMARKS) "
                                        + " select '" + ListOtherUsers.get(i) + "', authorityitem, ifhave, remarks  "
                                        + " from USERAUTHORITYS  where username = '"  + UserName + "' and remarks = '" + AuthorityType + "'";
                        SqlInsertInto2 = "insert into SUBUSERAUTHORITYS(USERNAME, AUTHORITYITEM, DEVICESERIALNO, IFHAVE, REMARKS) "
                                        + " select '" + ListOtherUsers.get(i) + "', AUTHORITYITEM, DEVICESERIALNO, IFHAVE, REMARKS  "
                                        + " from SUBUSERAUTHORITYS  where username = '"+ UserName + "' and remarks = '" + AuthorityType + "'";
                    }
                }
                

                ListInsertIntoSql.add(SqlDlelete);
                ListInsertIntoSql.add(SqlDlelete2);
                ListInsertIntoSql.add(SqlInsertInto);
                ListInsertIntoSql.add(SqlInsertInto2);
            }
            return InitDB.getInitDB(FileName + sFileName).batchExecuteUpdate(ListInsertIntoSql);
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "copyAuthoritysToOtherUsers()","复制用户权限给其他用户过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return -1;

    }

    
}
