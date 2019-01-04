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

public class DeviceResourceBean {

    private String sSerialno;
    private String sNodename;
    private String sResourcetype;
    private String sSerialnoJoin = "";
    private String ifRecordSchedule = "0"; 
    private static final String sFileName = "--->>DeviceResourceBean.java";

    public DeviceResourceBean() {
    }

    public DeviceResourceBean(String Serialno, String Nodename, String Resourcetype) {
        this.sSerialno = Serialno==null?"":Serialno;
        this.sNodename = Nodename==null?"":Nodename;
        this.sResourcetype = Resourcetype==null?"":Resourcetype;
    }
    public DeviceResourceBean(String Serialno, String Nodename, String Resourcetype, String SerialnoJoin) {
        this.sSerialno = Serialno==null?"":Serialno;
        this.sNodename = Nodename==null?"":Nodename;
        this.sResourcetype = Resourcetype==null?"":Resourcetype;
        this.sSerialnoJoin = SerialnoJoin==null?"":SerialnoJoin;
    }
    public DeviceResourceBean(String Serialno, String Nodename, String Resourcetype, String SerialnoJoin, String IfRecordSchedule) {
        this.sSerialno = Serialno==null?"":Serialno;
        this.sNodename = Nodename==null?"":Nodename;
        this.sResourcetype = Resourcetype==null?"":Resourcetype;
        this.sSerialnoJoin = SerialnoJoin==null?"":SerialnoJoin;
        this.ifRecordSchedule = IfRecordSchedule==null?"0":IfRecordSchedule;
    }
    

    public String getResourcetype() {
        return sResourcetype;
    }

    public void setResourcetype(String Resourcetype) {
        this.sResourcetype = Resourcetype;
    }

    /**
     * @return the serialno
     */
    public String getSerialno() {
        return sSerialno;
    }

    /**
     * @param Serialno the serialno to set
     */
    public void setSerialno(String Serialno) {
        this.sSerialno = Serialno;
    }

    /**
     * @return the nodename
     */
    public String getNodename() {
        return sNodename;
    }

    /**
     * @param Nodename the nodename to set
     */
    public void setNodename(String Nodename) {
        this.sNodename = Nodename;
    }
    
    
    /**
     * @return the sSerialnoJoin
     */
    public String getsSerialnoJoin() {
        return sSerialnoJoin;
    }

    /**
     * @param sSerialnoJoin the sSerialnoJoin to set
     */
    public void setsSerialnoJoin(String sSerialnoJoin) {
        this.sSerialnoJoin = sSerialnoJoin;
    }
    
    /**
     * @return the ifRecordSchedule
     */
    private String getIfRecordSchedule() {
        return ifRecordSchedule;
    }

    /**
     * @param ifRecordSchedule the ifRecordSchedule to set
     */
    private void setIfRecordSchedule(String ifRecordSchedule) {
        this.ifRecordSchedule = ifRecordSchedule;
    }
    
    /**
	 * 函数:      getAllDeviceResourceTypeList
         * 函数描述:  获取设备资源表中的“设备序列号-0”、“设备资源分类-1”、对应的“设备别名-2”（设备参数表中的设备别名）、设备资源分类名称-3（代码表中代码名称）
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList getAllDeviceResourceTypeList(String FileName){
        String sqlQuery = "SELECT distinct(a.serialno),a.resourcetype,b.anothername,c.codename FROM DEVICERESOURCE a,DEVICEPARA b,CODES c where a.serialno = b.serialno and a.resourcetype = c.code order by a.serialno,a.resourcetype";
        return InitDB.getInitDB(FileName + sFileName).executeQueryList(sqlQuery) ;
    }
    /**
	 * 函数:      getAllDeviceResourceParaList
         * 函数描述:  获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
         *            对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）
         * @param ResourceType  //设备资源分类
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList getAllDeviceResourceParaList(String ResourceType, String FileName) {
        String sqlQuery;
        if (ResourceType == null || ResourceType.equals("") || ResourceType.equals("all")){
            sqlQuery = "select c.serialno,nodename,resourcetype,c.serialnojoin,c.ifRecordSchedule,a.anothername as Aanothername, a.dvrip  as Advrip,b.anothername  as Banothername, d.CODENAME as ResourceName"
                        + " from DEVICERESOURCE c left outer join devicepara a"
                        + " on c.serialno = a.serialno"
                        + " left outer join devicepara b"
                        + " on c.serialnojoin = b.serialno "
                        + " left outer join CODES d" 
                        + " on c.resourcetype = d.CODE"
                        + " order by c.serialno, c.resourcetype, c.nodename";
        }else {
            sqlQuery = "select c.serialno,nodename,resourcetype,c.serialnojoin,c.ifRecordSchedule,a.anothername as Aanothername, a.dvrip  as Advrip,b.anothername  as Banothername, d.CODENAME as ResourceName "
                        + " from DEVICERESOURCE c left outer join devicepara a"
                        + " on c.serialno=a.serialno"
                        + " left outer join devicepara b"
                        + " on c.serialnojoin = b.serialno"
                        + " left outer join CODES d"
                        + " on c.resourcetype=d.CODE"
                        +" where c.resourcetype = '" + ResourceType + "' order by c.serialno,c.nodename";
        }
        return getAllDeviceResourceParaList2(sqlQuery,FileName);
    }
    
    
    /**
	 * 函数:      getDeviceResourceParaList
         * 函数描述:  获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
         *            对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）
         * @param DVRType    二级设备类型
         * @param ResourceType  //设备资源分类
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList getDeviceResourceParaList(String DVRType, String ResourceType, String FileName) {
        String sqlQuery;
        sqlQuery = "select c.serialno,nodename,resourcetype,c.serialnojoin,c.ifRecordSchedule,a.anothername as Aanothername, "
                + "a.dvrip  as Advrip,b.anothername  as Banothername, d.CODENAME as ResourceName"
                + " from DEVICERESOURCE c left outer join devicepara a"
                + " on c.serialno = a.serialno"
                + " left outer join devicepara b"
                + " on c.serialnojoin = b.serialno "
                + " left outer join CODES d"
                + " on c.resourcetype = d.CODE";
        String AffixSql = "";
        if (DVRType == null || DVRType.equals("") || DVRType.equals("all")){
            if (ResourceType == null || ResourceType.equals("") || ResourceType.equals("all")){
                AffixSql = " order by c.serialno, c.resourcetype, c.nodename";
            }else {
                AffixSql = " where c.resourcetype = '" + ResourceType + "' order by c.serialno,c.nodename";
            }
        }else{
            if (ResourceType == null || ResourceType.equals("") || ResourceType.equals("all")){
                AffixSql = " where a.DVRTYPE = '" + DVRType + "' order by c.serialno, c.resourcetype, c.nodename";
            }else {
                AffixSql = " where a.DVRTYPE = '" + DVRType + "' and c.resourcetype = '" + ResourceType + "' order by c.nodename";
            }
        }
        
        sqlQuery = sqlQuery + AffixSql;
       
        return getAllDeviceResourceParaList2(sqlQuery,FileName);
    }
    /**
        * 函数:      getAllDeviceResourceParaList
        * 函数描述:  获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
        *            对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）
        * @param SerialNO   设备序列号
        * @param ResourceType  //设备资源分类
        * @param FileName     调用该函数的文件名
        * @return ArrayList     
    */
    public static ArrayList getAllDeviceResourceParaList(String SerialNO, String ResourceType, String FileName) {
        String sqlQuery;
        //if (SerialNO == null || SerialNO.equals("") || SerialNO.equals("all")) return getAllDeviceResourceParaList( ResourceType,  FileName);
        boolean bResourceType = (ResourceType == null || ResourceType.equals("") || ResourceType.equals("all"));
        boolean bSerialNO = (SerialNO == null || SerialNO.equals("") || SerialNO.equals("all")); 
        String SqlQueryWhere1 = "",SqlQueryWhere2 = "";
        if (bResourceType) SqlQueryWhere1 =  "c.resourcetype = '" + ResourceType + "'";
        if (bSerialNO) SqlQueryWhere2 =  "c.serialno = '" + SerialNO + "'";
        
        if (bResourceType && bSerialNO){//代表设备序列号和设备资源类型都为空的情况下
            sqlQuery = "select c.serialno,nodename,resourcetype,c.serialnojoin,c.ifRecordSchedule,a.anothername as Aanothername, a.dvrip  as Advrip,b.anothername  as Banothername, d.CODENAME as ResourceName"
                        + " from DEVICERESOURCE c left outer join devicepara a"
                        + " on c.serialno = a.serialno"
                        + " left outer join devicepara b"
                        + " on c.serialnojoin = b.serialno "
                        + " left outer join CODES d" 
                        + " on c.resourcetype = d.CODE"
                        + " order by c.serialno, c.resourcetype, c.nodename";
        }else if (!bResourceType && !bSerialNO){//代表设备序列号和设备资源类型都不为空的情况下
            sqlQuery = "select c.serialno,nodename,resourcetype,c.serialnojoin,c.ifRecordSchedule,a.anothername as Aanothername, a.dvrip  as Advrip,b.anothername  as Banothername, d.CODENAME as ResourceName "
                        + " from DEVICERESOURCE c left outer join devicepara a"
                        + " on c.serialno=a.serialno"
                        + " left outer join devicepara b"
                        + " on c.serialnojoin = b.serialno"
                        + " left outer join CODES d"
                        + " on c.resourcetype=d.CODE"
                        +" where c.serialno = '"+ SerialNO + "' and c.resourcetype = '" + ResourceType + "' order by c.serialno,c.nodename";
        }else if  (!bResourceType && bSerialNO){//代表设备序列号和设备资源类型中，备资源类型不为空的情况下
            sqlQuery = "select c.serialno,nodename,resourcetype,c.serialnojoin,c.ifRecordSchedule,a.anothername as Aanothername, a.dvrip  as Advrip,b.anothername  as Banothername, d.CODENAME as ResourceName "
                        + " from DEVICERESOURCE c left outer join devicepara a"
                        + " on c.serialno=a.serialno"
                        + " left outer join devicepara b"
                        + " on c.serialnojoin = b.serialno"
                        + " left outer join CODES d"
                        + " on c.resourcetype=d.CODE"
                        +" where c.resourcetype = '" + ResourceType + "' order by c.serialno, c.resourcetype, c.nodename";
        }else {//if  (bResourceType && bSerialNO){代表设备序列号和设备资源类型中，设备序列号不为空的情况下
            sqlQuery = "select c.serialno,nodename,resourcetype,c.serialnojoin,c.ifRecordSchedule,a.anothername as Aanothername, a.dvrip  as Advrip,b.anothername  as Banothername, d.CODENAME as ResourceName "
                        + " from DEVICERESOURCE c left outer join devicepara a"
                        + " on c.serialno=a.serialno"
                        + " left outer join devicepara b"
                        + " on c.serialnojoin = b.serialno"
                        + " left outer join CODES d"
                        + " on c.resourcetype=d.CODE"
                        +" where c.serialno = '" + SerialNO + "' order by c.serialno, c.resourcetype, c.nodename";
        }
        return getAllDeviceResourceParaList2(sqlQuery,FileName);
        
    }
    
    private static ArrayList getAllDeviceResourceParaList2(String SqlQuery, String FileName) {
        if (SqlQuery == null || SqlQuery.equals("")) return null;
        ArrayList listDeviceResource = new ArrayList();
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(SqlQuery) ;
        try {
            while(rs.next()) {
                DeviceResourceBean deviceResourceBean = new DeviceResourceBean(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)) ;
                ArrayList newList = new ArrayList();
                
                newList.add(deviceResourceBean);
                String ss= rs.getString("Aanothername");
                newList.add(ss==null?"":ss);
                
                ss = rs.getString("Advrip");
                newList.add(ss==null?"":ss);
                
                ss = rs.getString("Banothername");
                newList.add(ss==null?"":ss);
                
                ss = rs.getString("ResourceName");
                newList.add(ss==null?"":ss);
                
                listDeviceResource.add(newList);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName, "getAllDeviceResourceParaList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getAllDeviceResourceParaList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getAllDeviceResourceParaList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getAllDeviceResourceParaList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getAllDeviceResourceParaList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getAllDeviceResourceParaList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + SqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getAllDeviceResourceParaList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listDeviceResource;
    }
    /**
	 * 函数:      getDeviceGroupList
         * 函数描述:  获取设备资源表中的“设备序列号”,“节点名”,“设备资源分类”等参数
         * @param SerialNO    设备序列号
         * @param ResourceType    设备资源分类
         * @param FileName     调用该函数的文件名
         * @return ArrayList 
    */
    public static ArrayList<DeviceResourceBean> getDeviceResourceList(String SerialNO,String ResourceType,String FileName) {
        
        if (ResourceType == null || ResourceType.equals("")) return null;
        String sqlQuery;
        if (SerialNO == null || SerialNO.equals("")) 
            sqlQuery = "select serialno, nodename, resourcetype, serialnojoin, ifRecordSchedule from DeviceResource where resourcetype = '" + ResourceType + "'";
        else
            sqlQuery = "select serialno, nodename, resourcetype, serialnojoin, ifRecordSchedule from DeviceResource where SerialNO = '" + SerialNO + "' and resourcetype = '" + ResourceType + "'";
        
        ArrayList<DeviceResourceBean> listDeviceResourceBean = new ArrayList<>();
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;
        
        try {
            while(rs.next()) {
                DeviceResourceBean deviceResourceBean = new DeviceResourceBean(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)) ;
                listDeviceResourceBean.add(deviceResourceBean);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName + sFileName, "getDeviceResourceList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getDeviceResourceList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getDeviceResourceList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getDeviceResourceList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getDeviceResourceList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getDeviceResourceList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getDeviceResourceList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listDeviceResourceBean;
    }
    
    /**
        *函数:      copyToGroup
        *函数描述:  将设备资源表中的数据复制到设备分组表中
        * @param SerialnoList	设备序列号列表
        * @param GroupName    组名
        * @param FileName       调用该函数的文件名
        * @return int：赋值成功返回赋值的行数n>0；未作任何修改返回0；参数错误或者其他错误，返回-1
    */
    public static int copyToGroup(ArrayList<String> SerialnoList,String GroupName,String FileName){
        try {
            if (SerialnoList == null || SerialnoList.size() < 1) return -1;
            if (GroupName == null || GroupName.equals("")) return -1;
            if (FileName == null || FileName.equals("")) return -1;
            //例子：insert into DEVICEGROUP(SERIALNO,NODENAME, GROUPNAME, RESOURCETYPE) select SERIALNO,NODENAME, 'aaaaa', RESOURCETYPE from DEVICERESOURCE where SERIALNO in ('dd','fff');
//            String InSerialnoList = "('";
//            for (int i=0;i<SerialnoList.size();i++){
//                if (i == 0) InSerialnoList += SerialnoList.get(i);
//                else InSerialnoList = InSerialnoList + "','" + SerialnoList.get(i);
//            }
//            InSerialnoList = InSerialnoList + "')";
            
            String InSerialnoList= getListToString(SerialnoList);
            
            String SqlUpdate = "insert into DEVICEGROUP(SERIALNO,NODENAME, GROUPNAME, RESOURCETYPE,SERIALNOJOIN) select SERIALNO,NODENAME, '" +GroupName + "', RESOURCETYPE,SERIALNOJOIN from DEVICERESOURCE where SERIALNO in " + InSerialnoList;
            int Count = InitDB.getInitDB(FileName + sFileName).executeUpdate(SqlUpdate);
            return Count;
        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "copyToGroup()","用户在将设备资源表中的数据复制到设备分组表中的过程中，出现错误"
                            +  "\r\n                       Exception:" + e.toString()
                            + "\r\n                       各个参数值为：" + SerialnoList.toString() + ";" + GroupName + ";" + FileName + ";");  
            return -1;
        }
    }
    
    /**
        *函数:      CopySelectedToGroup
        *函数描述:  将设备资源表中的数据复制到设备分组表中
        * @param ListSerialNoNome	设备序列号连接节点名列表
        * @param GroupName    组名
        * @param FileName       调用该函数的文件名
        * @return int：赋值成功返回赋值的行数n>0；未作任何修改返回0；参数错误或者其他错误，返回-1
    */
    public static int CopySelectedToGroup(ArrayList<String> ListSerialNoNome, String GroupName, String FileName){
        try{
            if (ListSerialNoNome == null || ListSerialNoNome.size() < 1) return -1;
            if (GroupName == null || GroupName.equals("")) return -1;
            if (FileName == null || FileName.equals("")) return -1;

            String SerialNoJoinNomeList= getListToString(ListSerialNoNome);

            String InsertCommand  ="insert into DEVICEGROUP(SERIALNO,NODENAME, GROUPNAME, RESOURCETYPE,SERIALNOJOIN) "
                                    + "select SERIALNO,NODENAME, '" + GroupName + "', RESOURCETYPE,SERIALNOJOIN from DEVICERESOURCE "
                                    + "where (SERIALNO || NODENAME) in  " + SerialNoJoinNomeList;
            return InitDB.getInitDB(FileName + sFileName).executeUpdate(InsertCommand);
        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "CopySelectedToGroup()","用户在将设备资源表中的数据复制到设备分组表中的过程中，出现错误"
                            +  "\r\n                       Exception:" + e.toString()
                            + "\r\n                       各个参数值为：" + ListSerialNoNome.toString() + ";" + GroupName + ";" + FileName + ";");  
            return -1;
        }
    }
    
    private static String getListToString(ArrayList<String> ListString){
        String SerialNoJoinNomeList = "('";
        for (int i=0;i<ListString.size();i++){
            if (i == 0) SerialNoJoinNomeList += ListString.get(i);
            else SerialNoJoinNomeList = SerialNoJoinNomeList + "','" + ListString.get(i);
        }
        SerialNoJoinNomeList = SerialNoJoinNomeList + "')";
        return SerialNoJoinNomeList;
    }

    
    /**
        *函数:      setupRecordSchedule
        *函数描述:  修改设备资源表的是否已实现存储计划标志ifRecordSchedule
        * @param SerialNO	 设备序列号
        * @param NodeName       设备节点名
        * @param IfRecordSchedule	 boolean：是否已实现存储计划标志
        * @param FileName       调用该函数的文件名
        * @return boolean：存在返回true；失败，返回false.
     */
    public static boolean setupRecordSchedule(String SerialNO, String NodeName, boolean IfRecordSchedule, String FileName){
        if (SerialNO == null || SerialNO.equals("")) return false;
        if (NodeName == null || NodeName.equals("")) return false;
        
        String sUpdateSql = "update DEVICERESOURCE  set IFRECORDSCHEDULE = '" + (IfRecordSchedule?1:0) + "'  where SERIALNO ='" + SerialNO + "' and NODENAME ='" + NodeName + "'";
        return InitDB.getInitDB(FileName + sFileName).executeUpdate(sUpdateSql) > 0;
    }

}
