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

public class StandardAuthoritysBean {

 

    private String sUsertype;
    private String sAuthorityitem;
    private String sIfsubdivision;
    private String sRemarks;
    private static final String FILE_NAME = "--->>StandardAuthoritysBean.java";
    
    public StandardAuthoritysBean() {
    }
    
    public StandardAuthoritysBean( String Uusertype, String Authorityitem, String Ifsubdivision) {

        this.sUsertype = Uusertype;
        this.sAuthorityitem = Authorityitem;
        this.sIfsubdivision = Ifsubdivision;
    }
    public StandardAuthoritysBean( String Uusertype, String Authorityitem, String Ifsubdivision,String Remarks) {

        this.sUsertype = Uusertype;
        this.sAuthorityitem = Authorityitem;
        this.sIfsubdivision = Ifsubdivision;
        this.sRemarks = Remarks; 
    }


    public String getUsertype() {
        return sUsertype;
    }

    public void setUsertype(String usertype) {
        this.sUsertype = usertype;
    }

    public String getAuthorityitem() {
        return sAuthorityitem;
    }

    public void setAuthorityitem(String authorityitem) {
        this.sAuthorityitem = authorityitem;
    }

    public String getIfsubdivision() {
        return sIfsubdivision;
    }

    public void setIfsubdivision(String ifsubdivision) {
        this.sIfsubdivision = ifsubdivision;
    }

    public String getRemarks() {
        return sRemarks;
    }

    public void setRemarks(String remarks) {
        this.sRemarks = remarks;
    }
    /**
	 * 函数:      getStandardAuthoritysList
         * 函数描述:  获取标准权限表中的用户类型代码、权限项目、是否细分、备注等信息
         * @param Usertype    用户类型代码
         * @param FileName     调用该函数的文件名
         * @return ArrayList 
    */
    public static ArrayList<StandardAuthoritysBean> getStandardAuthoritysList(String Usertype,String FileName) {
        if (Usertype == null || Usertype.equals("")) return null;
        ArrayList<StandardAuthoritysBean> listStandardAuthoritysBean = new ArrayList<>();
        String sqlQuery;
//        if (Usertype.equals("all") || Usertype.equals("020200"))//管理员权限
//            sqlQuery = "select Usertype,Authorityitem,Ifsubdivision,Remarks from StandardAuthoritys  order by Usertype, Authorityitem";
//        else
        /*将以上部分注释掉，主要因为现在还加了超级管理员权限，共3级权限 2017.1.17*/
        sqlQuery = "select Usertype,Authorityitem,Ifsubdivision,Remarks from StandardAuthoritys  where Usertype >= '" + Usertype + "'  order by Authorityitem";
        
        ResultSet rs = InitDB.getInitDB(FileName + FILE_NAME).getRs(sqlQuery) ;
        try {
            while(rs.next()) {
                StandardAuthoritysBean StandardAuthoritysBean = new StandardAuthoritysBean(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)) ;
                listStandardAuthoritysBean.add(StandardAuthoritysBean);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName + FILE_NAME, "getStandardAuthoritysList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + FILE_NAME, "getStandardAuthoritysList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + FILE_NAME, "getStandardAuthoritysList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + FILE_NAME, "getStandardAuthoritysList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + FILE_NAME, "getStandardAuthoritysList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + FILE_NAME, "getStandardAuthoritysList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getStandardAuthoritysList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listStandardAuthoritysBean;
    }
    
    public static int batchUpdateUpdate(ArrayList<String> ListDeleteAndInsertStr, String FileName ){
        return InitDB.getInitDB(FileName + FILE_NAME).batchExecuteUpdate(ListDeleteAndInsertStr);
    }
    
    public static String getStringUpdateCommand(String Remarks, String Authorityitem){
        String UpdateCommand  = "UPDATE STANDARDAUTHORITYS SET REMARKS = '" + Remarks + "' WHERE AUTHORITYITEM = '" + Authorityitem + "'" ;
        return UpdateCommand;
    }
    
}
