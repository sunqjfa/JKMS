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
public class DeviceGroupBean {

    private String sNodeName;//节点名
    private String sGroupName;//组名
    private String sRescourceType = jyms.CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE;//设备资源分类//编码通道
    private String sSerialNO = "";
    private String sSerialNOJoin = "";
    //数据库长度限制，组名长度不能>20
    public static final int GROUPNAME_LENGTH = 20;
    private static final String sFileName = "--->>DeviceGroupBean.java";
    
//    private String sFileName = "";

    public DeviceGroupBean() {
    }

    public DeviceGroupBean(String SerialNO,String NodeName,String GroupName,String RescourceType) {
        this.sNodeName = NodeName==null?"":NodeName;
        this.sGroupName = GroupName==null?"":GroupName;
        this.sRescourceType = RescourceType==null?"":RescourceType;
        this.sSerialNO = SerialNO==null?"":SerialNO;
    }

    public DeviceGroupBean(String SerialNO,String NodeName,String GroupName,String RescourceType,String SerialNOJoin) {
        this.sNodeName = NodeName==null?"":NodeName;
        this.sGroupName = GroupName==null?"":GroupName;
        this.sRescourceType = RescourceType==null?"":RescourceType;
        this.sSerialNO = SerialNO==null?"":SerialNO;
        this.sSerialNOJoin = SerialNOJoin==null?"":SerialNOJoin;
    }
    public String getNodename() {
        return sNodeName;
    }

    public void setNodename(String NodeName) {
        this.sNodeName = NodeName;
    }
    
    public String getGroupName() {
        return sGroupName;
    }

    public void setGroupName(String GroupName) {
        this.sGroupName = GroupName;
    }
    public String getRescourceType() {
        return sRescourceType;
    }

    public void setRescourceType(String RescourceType) {
        this.sRescourceType = RescourceType;
    }
    
    public String getSerialNO() {
        return sSerialNO;
    }

    public void setSerialNO(String SerialNO) {
        this.sSerialNO = SerialNO;
    }
    /**
     * @return the sSerialNOJoin
     */
    public String getsSerialNOJoin() {
        return sSerialNOJoin;
    }

    /**
     * @param sSerialNOJoin the sSerialNOJoin to set
     */
    public void setsSerialNOJoin(String sSerialNOJoin) {
        this.sSerialNOJoin = sSerialNOJoin;
    }
    /**
	 * 函数:      getDeviceGroupNameList
         * 函数描述:  获取设备分组表中的““组名”列表
         * @param FileName     调用该函数的文件名
         * @return ArrayList     如果出错，返回NULL
    */
    public static ArrayList getDeviceGroupNameList(String FileName) {
        String sqlQuery = "SELECT distinct GROUPNAME  FROM DEVICEGROUP";
        return InitDB.getInitDB(FileName + sFileName).executeQueryOneColList(sqlQuery) ;
    }
    
    /**
	 * 函数:      getAllDeviceGroupParaList
         * 函数描述:  获取设备分组表中的“节点名”,“别名”,“组名”,“设备资源分类”
         *              和对应的设备基本参数表中的“设备序列号”,“IP地址”等参数
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList getAllDeviceGroupParaList(String FileName) {
        return getAllDeviceGroupParaList("",FileName);
    }

    
    /**
	 * 函数:      getAllDeviceGroupParaList
         * 函数描述:  获取DeviceGroupBean（设备分组表中的“设备序列号”、“节点名”、“组名”、“设备资源分类”、“接入设备的序列号”）-0
         *            对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）
         * @param ResourceType  //设备资源分类
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList getAllDeviceGroupParaList(String ResourceType,String FileName) {
        String sqlQuery;
        if (ResourceType == null || ResourceType.equals("") || ResourceType.equals("all")){
            sqlQuery = "select c.serialno,nodename,groupname,resourcetype,c.serialnojoin,"
                        + "a.anothername as Aanothername, a.dvrip  as Advrip,b.anothername  as Banothername , d.CODENAME as ResourceName"
                        + " from devicegroup c left outer join devicepara a"
                        + " on c.serialno=a.serialno"
                        + " left outer join devicepara b"
                        + " on c.serialnojoin = b.serialno"
                        + " left outer join CODES d " 
                        + " on c.resourcetype = d.CODE"
                        + " order by c.groupname, c.resourcetype, c.serialno, c.nodename";
        }else {
            sqlQuery = "select c.serialno,nodename,groupname,resourcetype,c.serialnojoin,"
                        + "a.anothername as Aanothername, a.dvrip  as Advrip,b.anothername  as Banothername , d.CODENAME as ResourceName"
                        + " from devicegroup c left outer join devicepara a"
                        + " on c.serialno=a.serialno"
                        + " left outer join devicepara b"
                        + " on c.serialnojoin = b.serialno"
                        + " left outer join CODES d " 
                        + " on c.resourcetype = d.CODE"
                        +" where resourcetype = '" + ResourceType + "' order by c.groupname, c.resourcetype, c.serialno, c.nodename";
        }
        
        ArrayList listDeviceGroupBean = new ArrayList();
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;
        try {
            while(rs.next()) {
                DeviceGroupBean deviceGroupBean = new DeviceGroupBean(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)) ;
                ArrayList newList = new ArrayList();
                
                newList.add(deviceGroupBean);
                String ss= rs.getString("Aanothername");
                newList.add(ss==null?"":ss);
                
                ss = rs.getString("Advrip");
                newList.add(ss==null?"":ss);
                
                ss = rs.getString("Banothername");
                newList.add(ss==null?"":ss);
                
                 ss = rs.getString("ResourceName");
                newList.add(ss==null?"":ss);
                listDeviceGroupBean.add(newList);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName, "getAllDeviceGroupParaList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getAllDeviceGroupParaList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getAllDeviceGroupParaList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getAllDeviceGroupParaList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getAllDeviceGroupParaList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getAllDeviceGroupParaList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getAllDeviceGroupParaList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listDeviceGroupBean;
    }
    /**
	 * 函数:      getDeviceGroupList
         * 函数描述:  获取设备分组表中的“设备序列号”,“节点名”,“组名”,“设备资源分类”等参数
         * @param GroupName    设备分组名
         * @param FileName     调用该函数的文件名
         * @return ArrayList 
    */
    public static ArrayList<DeviceGroupBean> getDeviceGroupList(String GroupName,String FileName) {
        ArrayList<DeviceGroupBean> listDeviceGroupBean = new ArrayList<DeviceGroupBean>();
        String sqlQuery;
        if (GroupName == null || GroupName.equals("") || GroupName.equals("all")){
            sqlQuery = "select * from DEVICEGROUP    order by NODENAME";
        }else {
            sqlQuery = "select * from DEVICEGROUP  where GroupName = '" + GroupName + "'  order by SERIALNO,NODENAME";
        }
        return getDeviceGroupListQuery(sqlQuery,FileName);
    }
    
    /**
	 * 函数:      getDeviceGroupListQuery
         * 函数描述:  获取设备分组表中的“节点名”,“别名”,“组名”,“设备资源分类”、“接入设备的序列号”等参数
         * @param sqlQuery    查询语句
         * @param FileName     调用该函数的文件名
         * @return ArrayList 
    */
    private static ArrayList<DeviceGroupBean> getDeviceGroupListQuery(String sqlQuery,String FileName) {
        if (sqlQuery == null || sqlQuery.equals("")) return null;
        if (!sqlQuery.toLowerCase().contains("select")) return null;
        ArrayList<DeviceGroupBean> listDeviceGroupBean = new ArrayList<DeviceGroupBean>();
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;
        try {
            while(rs.next()) {
                DeviceGroupBean deviceGroupBean = new DeviceGroupBean(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)) ;
                listDeviceGroupBean.add(deviceGroupBean);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName + sFileName, "getDeviceGroupListQuery()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getDeviceGroupListQuery()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getDeviceGroupListQuery()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getDeviceGroupListQuery()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getDeviceGroupListQuery()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getDeviceGroupListQuery()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getDeviceGroupListQuery()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listDeviceGroupBean;
    }
    /**
        *函数:      getNumsOfGroup
        *函数描述:  数据库查询是否存在该分组的数据
        * @param GroupName	 分组名
        * @param FileName       调用该函数的文件名
        * @return int：存在返回存在的个数；不存在返回0；失败或参数错误或其他错误，返回-1
    */
    public static int getNumsOfGroup(String GroupName,String FileName){
        
        if (GroupName == null || GroupName.equals("")) return -1;
        if (FileName == null || FileName.equals("")) return -1;

        String sqlQuery = "select count(*) from DEVICEGROUP  where GROUPNAME  = '" + GroupName + "'";
        return InitDB.getInitDB(FileName + sFileName).getNums(sqlQuery);
    }
    /**
        *函数:      DeleteDeviceGroup
        *函数描述:  删除设备分组表中该分组的数据
        * @param GroupName	 分组名
        * @param FileName       调用该函数的文件名
        * @return int：成功返回删除的行数n>0；失败，返回0；出错返回-1.
     */
    public static int DeleteDeviceGroup(String GroupName, String FileName){
        if (GroupName == null || GroupName.equals("")) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        
        String sql3 = "delete  from DEVICEGROUP where GROUPNAME ='" + GroupName + "'";
        return InitDB.getInitDB(FileName + sFileName).executeUpdate(sql3);
        
    }
    
    /**
        *函数:      DeleteGroupResourceType
        *函数描述:  删除设备分组表中该分组的某一设备资源分类的数据
        * @param GroupName	 分组名
        * @param RescourceType   设备资源分类
        * @param FileName       调用该函数的文件名
        * @return int：成功返回删除的行数>0；失败，返回0；出错返回-1.
    */
    public static int DeleteGroupResourceType(String GroupName, String RescourceType, String FileName){
        if (GroupName == null || GroupName.equals("")) return -1;
        if (RescourceType == null || RescourceType.equals("")) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        
        String sqlDelOne = "delete  from DEVICEGROUP where GROUPNAME ='" + GroupName + "' and ResourceType = '" + RescourceType + "'";
        return InitDB.getInitDB(FileName + sFileName).executeUpdate(sqlDelOne);
        
    }
    
    /**
        *函数:      DeleteGroupResource
        *函数描述:  删除设备分组表中一条记录
        * @param GroupName	 分组名
        * @param SerialNO   设备序列号
        * @param NodeName   资源节点名
        * @param FileName       调用该函数的文件名
        * @return int：成功返回删除的行数1；失败，返回0；出错返回-1.
     */
    public static int DeleteGroupResource(String GroupName, String SerialNO, String NodeName, String FileName){
        if (GroupName == null || GroupName.equals("")) return -1;
        if (SerialNO == null || SerialNO.equals("")) return -1;
        if (NodeName == null || NodeName.equals("")) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        
        String sqlDelOne = "delete  from DEVICEGROUP where GROUPNAME ='" + GroupName + "' and serialno = '" + SerialNO + "' and nodename = '" + NodeName + "'";
        return InitDB.getInitDB(FileName + sFileName).executeUpdate(sqlDelOne);
        
    }

    /**
        *函数:      modifyGroupName
        *函数描述:  修改组名
        * @param OldGroupName	 分组名
        * @param NewGroupName   新组名
        * @param FileName       调用该函数的文件名
        * @return int：成功返回删除的行数n>0；失败，返回0；出错返回-1.
     */
    public static int modifyGroupName(String OldGroupName,String NewGroupName, String FileName){
        if (OldGroupName == null || OldGroupName.equals("")) return -1;
        if (NewGroupName == null || NewGroupName.equals("")) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        
        String modifySql = "UPDATE DEVICEGROUP SET GROUPNAME = '" + NewGroupName + "' WHERE GroupName = '" + OldGroupName + "'";
        
        return InitDB.getInitDB(FileName + sFileName).executeUpdate(modifySql);

    }

    
}
