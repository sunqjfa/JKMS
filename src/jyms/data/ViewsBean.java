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
import java.util.LinkedHashMap;

/**
 *
 * @author John
 */

public class ViewsBean implements Serializable {

    private static final String FILE_NAME = "--->>ViewsBean.java";
    private String viewname;
    private short windowno;
    private short gridx;
    private short gridy;
    private short gridwidth;
    private short gridheight;
    private String remarks;
    //数据库长度限制，视图名称长度不能>30
    public static final int VIEWNAME_LENGTH = 30;
    
    public static final String VIEW_6 = "VIEW_6";
    public static final String VIEW_8 = "VIEW_8";

    public ViewsBean() {
    }


    public ViewsBean(String viewname, short windowno, short positionx, short positiony, short width, short height) {
        this.viewname = viewname;
        this.windowno = windowno;
        this.gridx = positionx;
        this.gridy = positiony;
        this.gridwidth = width;
        this.gridheight = height;
    }
    
    public ViewsBean(String viewname, short windowno, short positionx, short positiony, short width, short height, String remarks) {
        this.viewname = viewname;
        this.windowno = windowno;
        this.gridx = positionx;
        this.gridy = positiony;
        this.gridwidth = width;
        this.gridheight = height;
        this.remarks = remarks==null?"":remarks;
    }
    
    public String getViewname() {
        return viewname;
    }

    public void setViewname(String viewname) {
        this.viewname = viewname;
    }

    public short getWindowno() {
        return windowno;
    }

    public void setWindowno(short windowno) {
        this.windowno = windowno;
    }
    public short getGridx() {
        return gridx;
    }

    public void setGridx(short positionx) {
        this.gridx = positionx;
    }

    public short getGridy() {
        return gridy;
    }

    public void setGridy(short positiony) {
        this.gridy = positiony;
    }

    public short getGridWidth() {
        return gridwidth;
    }

    public void setGridWidth(short width) {
        this.gridwidth = width;
    }

    public short getGridHeight() {
        return gridheight;
    }

    public void setGridHeight(short height) {
        this.gridheight = height;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    
    /**
	 * 函数:      getDeviceGroupList
         * 函数描述:  获取设备资源表中的“设备序列号”,“节点名”,“设备资源分类”等参数
         * @param FileName     调用该函数的文件名
         * @return HashMap 结果集
    */
    public static LinkedHashMap<String, ArrayList<ViewsBean>> getAllViewsHashMap(String FileName) {
        

        String sqlQuery = "select ID, VIEWNAME, WINDOWNO, GRIDX, GRIDY, GRIDWIDTH, GRIDHEIGHT, REMARKS from VIEWS ORDER BY ID";
        
        LinkedHashMap<String, ArrayList<ViewsBean>> ViewsHP = new LinkedHashMap<>();
        
        ArrayList<ViewsBean> listOneView =  new ArrayList<>();
        ResultSet rs = InitDB.getInitDB(FileName + FILE_NAME).getRs(sqlQuery) ;
        
        String NewViewName = "";
        try {
            while(rs.next()) {
                String TmpViewName = rs.getString(2);
                if (!TmpViewName.equals(NewViewName)){
                    if (!listOneView.isEmpty()) ViewsHP.put(NewViewName, listOneView);
                    NewViewName = TmpViewName;
                    listOneView =  new ArrayList<>();
                }
                ViewsBean TmpViewsBean = new ViewsBean(rs.getString(2),rs.getShort(3),rs.getShort(4),rs.getShort(5),rs.getShort(6),rs.getShort(7),rs.getString(8)) ;
                listOneView.add(TmpViewsBean);
            }
            if (!listOneView.isEmpty()) ViewsHP.put(NewViewName, listOneView);
            
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName + FILE_NAME, "getAllViewsHashMap()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + FILE_NAME, "getAllViewsHashMap()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + FILE_NAME, "getAllViewsHashMap()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + FILE_NAME, "getAllViewsHashMap()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + FILE_NAME, "getAllViewsHashMap()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + FILE_NAME, "getAllViewsHashMap()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getAllViewsHashMap()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return ViewsHP;
    }
    
    /**
        *函数:      getStringInsertCommand
        *函数描述:  生成向表中插入一行数据的SQL语句
        * @param ViewsBean	 视图数据
        * @param FileName       调用该函数的文件名
        * @return String：成功返回insert SQL语句；出错返回"".
     */
    private static String getStringInsertCommand(ViewsBean ViewsBean1){
        if (ViewsBean1 == null) return "";
        String InsertCommand  = "INSERT INTO VIEWS (VIEWNAME, WINDOWNO, GRIDX, GRIDY, GRIDWIDTH, GRIDHEIGHT) " 
                              + " VALUES ('" + ViewsBean1.getViewname() + "',"+ ViewsBean1.getWindowno() + "," + ViewsBean1.gridx + "," 
                              + ViewsBean1.getGridy() + "," + ViewsBean1.getGridWidth() + "," + ViewsBean1.getGridHeight()+ ")";
        return InsertCommand;
    }
    
//    public static String getStringUpdateCommand(String UserName, String Authorityitem, String IfHave){
//        String UpdateCommand  = "UPDATE USERAUTHORITYS SET IFHAVE = '" + IfHave + "' WHERE USERNAME = '" + UserName + "' and  AUTHORITYITEM = '" + Authorityitem + "'" ;
//        return UpdateCommand;
//    }
//    
//    public static String getStringDeleteCommand(String UserName){
//        String UpdateCommand  = "delete  from USERAUTHORITYS  where UserName ='" + UserName + "'";
//        return UpdateCommand;
//    }
    /**
        *函数:      batchInsert
        *函数描述:  向表中插入视图数据
        * @param ListViewsBean	 视图数据
        * @param FileName       调用该函数的文件名
        * @return int：成功返回删除的行数n>0；失败，返回0；出错返回-1.
     */
    public static int batchInsert(ArrayList<ViewsBean> ListViewsBean,String FileName ){
        if (ListViewsBean == null || ListViewsBean.isEmpty()) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        ArrayList<String> ListInsertStr = new ArrayList();
        for (ViewsBean ViewsBean1 : ListViewsBean) {
            if (ViewsBean1 != null) ListInsertStr.add(getStringInsertCommand(ViewsBean1));
        }
        return InitDB.getInitDB(FileName + FILE_NAME).batchExecuteUpdate(ListInsertStr);
    }
    /**
        *函数:      batchUpdate
        *函数描述:  修改视图数据
        * @param Viewname	 视图名
        * @param ListViewsBean  视图数据
        * @param FileName       调用该函数的文件名
        * @return int：成功返回删除的行数n>0；失败，返回0；出错返回-1.
     */
    public static int batchUpdate(String Viewname, ArrayList<ViewsBean> ListViewsBean,String FileName ){
        if (ListViewsBean == null || ListViewsBean.isEmpty()) return -1;
        if (Viewname == null || Viewname.equals("")) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        
        ArrayList<String> ListInsertStr = new ArrayList();
        ListInsertStr.add("DELETE FROM VIEWS WHERE VIEWNAME = '" + Viewname + "'");
        for (ViewsBean ViewsBean1 : ListViewsBean) {
            if (ViewsBean1 != null) ListInsertStr.add(getStringInsertCommand(ViewsBean1));
        }
        return InitDB.getInitDB(FileName + FILE_NAME).batchExecuteUpdate(ListInsertStr);
    }
    
    /**
        *函数:      DeleteOneView
        *函数描述:  删除视图数据
        * @param Viewname	 视图名
        * @param FileName       调用该函数的文件名
        * @return int：成功返回删除的行数n>0；失败，返回0；出错返回-1；如果是VIEW_6、VIEW_8，不能删除，返回-2
     */
    public static int DeleteOneView(String Viewname, String FileName){
        if (Viewname == null || Viewname.equals("")) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        if (Viewname.equals(VIEW_6) || Viewname.equals(VIEW_8)) return -2;
        
        String sql3 = "DELETE FROM VIEWS WHERE VIEWNAME ='" + Viewname + "'";
        return InitDB.getInitDB(FileName + FILE_NAME).executeUpdate(sql3);
    }

}
