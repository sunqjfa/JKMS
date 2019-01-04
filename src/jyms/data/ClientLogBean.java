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

public class ClientLogBean {

    private static final long serialVersionUID = 1L;
    /*客户端日志CLIENTLOG
    ID、操作时间TIMESTAMP、用户名VARCHAR(16)、日志类型CHAR(6)、描述信息VARCHAR(60)、设备序列号 VARCHAR(48)、分组名VARCHAR(20)、节点名 VARCHAR(30)、注释CHAR(200)
    */
    /*客户端日志CLIENTLOG
    ID、操作时间TIMESTAMP、用户名VARCHAR(16)、日志类型CHAR(6)、描述信息VARCHAR(60)、设备序列号 VARCHAR(48)、分组名VARCHAR(20)、节点名 VARCHAR(30)、
    接入设备序列号 VARCHAR(48)、接入通道、设备类型char（6）、被操作对象类型CHAR(6)、注释CHAR(200)
    */
    //组名现在版本也不用，只是为以后扩充准备
    //加入只是为以后的查询显示做准备：“接入设备序列号 VARCHAR(48)、接入通道”
    //其中“设备类型char（6）”、“被操作对象类型CHAR(6)”2项现在只是在数据库表中存在，但是还没有处理，为以后的版本准备
    private String OperationTime;
    private String UserName;
    private String LogType;
    private String Description = "";
    private String SerialNO = "";
    
    
    private String GroupName = "";
    private String NodeName  = "";
    //加入只是为以后的查询显示做准备
    private String SerialNOJoin = "";
    private String ChannelJoin = "";
    //2项现在只是在数据库表中存在，但是还没有处理，为以后的版本准备
    private String DVRType = "";
    private String ObjectType = "";

    private String Remarks = "";
    //数据库长度限制，备注长度不能>200
    public static final int REMARKS_LENGTH = 200;
    private static String sFileName = "--->>ClientLogBean.java";

    public ClientLogBean() {}
    //4项：操作时间、用户名、日志类型、描述信息
    public ClientLogBean( String operationtime, String username, String logtype, String description) {

        this.OperationTime = operationtime;
        this.UserName = username;
        this.LogType = logtype;
        this.Description = description;
    }
    //5项：操作时间、用户名、日志类型、描述信息、设备序列号
    public ClientLogBean(String operationtime,String username,String logtype,String description,String serialno) {
        this.OperationTime = operationtime;
        this.UserName = username;
        this.LogType = logtype;
        this.Description = description;
        this.SerialNO = serialno;
        //this.Remarks = remarks;
    }
    //7项：操作时间、用户名、日志类型、描述信息、设备序列号、分组名、节点名
    public ClientLogBean(String operationtime,String username,String logtype,String description,String serialno,String groupname,String nodename) {
        this.OperationTime = operationtime;
        this.UserName = username;
        this.LogType = logtype;
        this.Description = description;
        this.SerialNO = serialno;
        this.GroupName = groupname;
        this.NodeName = nodename;
        //this.Remarks = remarks;
    }
    
    //8项：操作时间、用户名、日志类型、描述信息、设备序列号、节点名、接入设备序列号、注释（专门为日志搜索准备的）
    public ClientLogBean(String operationtime,String username,String logtype,String description,String serialno,String nodename,String serialnojoin,String remarks) {
        this.OperationTime = operationtime;
        this.UserName = username;
        this.LogType = logtype;
        this.Description = description;
        this.SerialNO = serialno;
        this.NodeName = nodename;
        this.SerialNOJoin = serialnojoin;
        this.Remarks = remarks;
    }
    
    //9项：操作时间、用户名、日志类型、描述信息、设备序列号、分组名、节点名 、接入设备序列号、接入通道
    public ClientLogBean(String operationtime,String username,String logtype,String description,String serialno,String groupname,String nodename,String serialnojoin,String channeljoin) {
        this.OperationTime = operationtime;
        this.UserName = username;
        this.LogType = logtype;
        this.Description = description;
        this.SerialNO = serialno;
        this.GroupName = groupname;
        this.NodeName = nodename;
        this.SerialNOJoin = serialnojoin;
        this.ChannelJoin = channeljoin;
        //this.Remarks = remarks;
    }
    //11项：操作时间、用户名、日志类型、描述信息、设备序列号、分组名、节点名 、接入设备序列号、接入通道、设备类型、被操作对象类型
    public ClientLogBean(String operationtime,String username,String logtype,String description,String serialno,String groupname,
                            String nodename,String serialnojoin,String channeljoin,String dvrtype,String objecttype) {
        this.OperationTime = operationtime;
        this.UserName = username;
        this.LogType = logtype;
        this.Description = description;
        this.SerialNO = serialno;
        this.GroupName = groupname;
        this.NodeName = nodename;
        this.SerialNOJoin = serialnojoin;
        this.ChannelJoin = channeljoin;
        this.DVRType = dvrtype;
        this.ObjectType = objecttype;
        //this.Remarks = remarks;
    }
    
    //全部12项：操作时间、用户名、日志类型、描述信息、设备序列号、分组名、节点名 、接入设备序列号、接入通道、设备类型、被操作对象类型、注释（专门为日志搜索准备的）
    public ClientLogBean(String operationtime,String username,String logtype,String description,String serialno,String groupname,
                            String nodename,String serialnojoin,String channeljoin,String dvrtype,String objecttype, String remarks) {
        this.OperationTime = operationtime;
        this.UserName = username;
        this.LogType = logtype;
        this.Description = description;
        this.SerialNO = serialno;
        this.GroupName = groupname;
        this.NodeName = nodename;
        this.SerialNOJoin = serialnojoin;
        this.ChannelJoin = channeljoin;
        this.DVRType = dvrtype;
        this.ObjectType = objecttype;
        this.Remarks = remarks;
    }

    public String getOperationtime() {
        return OperationTime;
    }

    public void setOperationtime(String operationtime) {
        this.OperationTime = operationtime;
    }

    public String getUsername() {
        return UserName;
    }

    public void setUsername(String username) {
        this.UserName = username;
    }

    public String getLogtype() {
        return LogType;
    }

    public void setLogtype(String logtype) {
        this.LogType = logtype;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }
/**
     * @return the serialno
     */
    public String getSerialno() {
        return SerialNO;
    }

    /**
     * @param serialno the serialno to set
     */
    public void setSerialno(String serialno) {
        this.SerialNO = serialno;
    }


    public String getGroupname() {
        return GroupName;
    }

    public void setGroupname(String groupname) {
        this.GroupName = groupname;
    }

/**
     * @return the nodename
     */
    public String getNodename() {
        return NodeName;
    }

    /**
     * @param nodename the nodename to set
     */
    public void setNodename(String nodename) {
        this.NodeName = nodename;
    }
    
    
    /**
     * @return the SerialNOJoin
     */
    public String getSerialNOJoin() {
        return SerialNOJoin;
    }

    /**
     * @param SerialNOJoin the SerialNOJoin to set
     */
    public void setSerialNOJoin(String SerialNOJoin) {
        this.SerialNOJoin = SerialNOJoin;
    }

    /**
     * @return the ChannelJoin
     */
    public String getChannelJoin() {
        return ChannelJoin;
    }

    /**
     * @param ChannelJoin the ChannelJoin to set
     */
    public void setChannelJoin(String ChannelJoin) {
        this.ChannelJoin = ChannelJoin;
    }

    /**
     * @return the DVRType
     */
    public String getDVRType() {
        return DVRType;
    }

    /**
     * @param DVRType the DVRType to set
     */
    public void setDVRType(String DVRType) {
        this.DVRType = DVRType;
    }

    /**
     * @return the ObjectType
     */
    public String getObjectType() {
        return ObjectType;
    }

    /**
     * @param ObjectType the ObjectType to set
     */
    public void setObjectType(String ObjectType) {
        this.ObjectType = ObjectType;
    }
    
    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        this.Remarks = remarks;
    }
    /**
	 * 函数:      insertClientLog
         * 函数描述:    向客户日志表添加一条数据
	 * @param LogBean	 设备参数类
         * @param FileName             调用该函数的文件名
         * @return int：操作成功放回1；失败，返回0；出现其他错误返回-1
    */
    public static int insertClientLog(ClientLogBean LogBean,String FileName){
        try {
            if (LogBean == null) return -1;
            String SqlInsert = "INSERT INTO CLIENTLOG (OPERATIONTIME, USERNAME_oper, LOGTYPE, DESCRIPTION, SERIALNO_oper, GROUPNAME_oper, NODENAME_oper,SERIALNOJOIN, CHANNELJOIN, DVRTYPE_oper,OBJECTTYPE_oper, REMARKS)  VALUES ('" 
                            + LogBean.getOperationtime() + "', '"  + LogBean.getUsername() + "', '" 
                            + LogBean.getLogtype()+ "', '"   + LogBean.getDescription()+ "', '"  + LogBean.getSerialno()+ "', '"  
                            + LogBean.getGroupname()+ "', '"  + LogBean.getNodename()+ "', '" + LogBean.getSerialNOJoin()+ "', '" 
                            + LogBean.getChannelJoin()+ "', '" + LogBean.getDVRType()+ "', '" + LogBean.getObjectType()+ "', '" + LogBean.getRemarks()+  "')";
            return InitDB.getInitDB(FileName + sFileName).executeUpdate(SqlInsert);
        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "insertClientLog()","用户在填写日志表的过程中，出现错误"
                            +  "\r\n                       Exception:" + e.toString() + ";");  
            return -1;
        }
    }
    /**
        * 函数:      setClientLogRemarks
        * 函数描述:    向客户日志表添加一条数据
        * @param operationtime  操作时间的字符串表示
        * @param newremarks 日志记录的注释
        * @param FileName             调用该函数的文件名
        * @return int：操作成功放回1；失败，返回0；出现其他错误返回-1
    */
    public static int setClientLogRemarks(String operationtime,String newremarks,String FileName){
        if (operationtime == null || operationtime.equals("")) return -1;
        if (newremarks == null || newremarks.equals("")) return -1;
        
        String sqlUpdate = "UPDATE CLIENTLOG SET REMARKS = '" + newremarks + "' WHERE OPERATIONTIME = '" + operationtime + "'";
        return InitDB.getInitDB(FileName + sFileName).executeUpdate(sqlUpdate);
    }

    /**
        * 函数:      getClientLogFromSearch
        * 函数描述:  获取ClientLogBean（客户端日志表中的操作时间、用户名、日志类型代码、描述信息、设备序列号、节点名、接入设备序列号、注释）-0
        *            对应的“设备别名-1”、“对应的的接入设备的“设备别名-2”、对应的日志类型-3
        * @param OperationTime1 搜索的起始时间
        * @param OperationTime2 搜索的终止时间
        * @param FileName     调用该函数的文件名
        * @return ArrayList     
        * "select c.OperationTime,c.UserName,c.LogType,c.Description,c.SerialNO,c.NodeName,c.SerialNOJoin,c.Remarks,a.anothername as Aanothername, b.anothername  as Banothername,d.CodeName as LogName 
            from ClientLog c left outer join devicepara a 
            on c.serialno=a.serialno 
            left outer join devicepara b 
            on c.serialnojoin = b.serialno 
            left outer join CODES d
            on c.LogType = d.Code 
            where OperationTime >= '2016-05-30 09:25:46' and OperationTime <= '2016-05-30 10:25:46' order by c.OperationTime"
    */
    public static ArrayList getClientLogFromSearch(String OperationTime1,String OperationTime2,String FileName){
        if (OperationTime1 == null || OperationTime1.equals("")) return null;
        if (OperationTime2 == null || OperationTime2.equals("")) return null;
        
        String sqlQuery = "select c.OperationTime, c.UserName_oper, c.LogType, c.Description, c.SerialNO_oper, c.NodeName_oper, c.SerialNOJoin, c.Remarks,"
			+ "a.anothername as Aanothername, b.anothername  as Banothername,d.CodeName as LogName"
                        + " from ClientLog c left outer join devicepara a"
                        + " on c.serialno_oper = a.serialno"
                        + " left outer join devicepara b"
                        + " on c.serialnojoin = b.serialno "
                        + " left outer join CODES d " 
                        + " on c.LogType = d.Code "
                        + " where OperationTime >= '" + OperationTime1 + "' and OperationTime <= '" + OperationTime2 + "' order by c.OperationTime";
        
        return getClientLogFromSearch(sqlQuery,FileName);
        
    }
    /**
        * 函数:      getClientLogFromSearch
        * 函数描述:  根据起始时间得到日志搜索的SQL查询语句
        * @param sqlQuery
        * @param FileName     调用该函数的文件名
        * @return String[]     
        * "select c.OperationTime,c.UserName,c.LogType,c.Description,c.SerialNO,c.NodeName,c.SerialNOJoin,c.Remarks,a.anothername as Aanothername, b.anothername  as Banothername,d.CodeName as LogName 
            from ClientLog c left outer join devicepara a 
            on c.serialno=a.serialno 
            left outer join devicepara b 
            on c.serialnojoin = b.serialno 
            left outer join CODES d
            on c.LogType = d.Code 
            where OperationTime >= '2016-05-30 09:25:46' and OperationTime <= '2016-05-30 10:25:46' order by c.OperationTime"
    */
    public static ArrayList getClientLogFromSearch(String sqlQuery,String FileName){
        if (sqlQuery == null || sqlQuery.equals("")) return null;
        if (!(sqlQuery.toLowerCase().contains("select"))) return null;
        
        ArrayList listClientLog = new ArrayList();
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;
        try {
            while(rs.next()) {
                ClientLogBean clientLogBean = new ClientLogBean(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),
                                                                rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12)) ;
                String s1 = rs.getString(1);
                String s2 = rs.getString(2);
                String s3 = rs.getString(3);
                String s4 = rs.getString(4);
                String s5 = rs.getString(5);
                String s6 = rs.getString(6);
                String s7 = rs.getString(7);
                String s8 = rs.getString(8);
                String s9 = rs.getString(9);
                String s10 = rs.getString(10);
                String s11 = rs.getString(11);
                String s12 = rs.getString(12);
  
                ArrayList newList = new ArrayList();
                
                newList.add(clientLogBean);
                String ss= rs.getString("Aanothername");
                newList.add(ss==null?"":ss);
                
                ss = rs.getString("Banothername");
                newList.add(ss==null?"":ss);
                
                ss = rs.getString("LogName");
                newList.add(ss==null?"":ss);
                listClientLog.add(newList);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName, "getClientLogFromSearch()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getClientLogFromSearch()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getClientLogFromSearch()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getClientLogFromSearch()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getClientLogFromSearch()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getClientLogFromSearch()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getClientLogFromSearch()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listClientLog;
    }
    /**
        * 函数:      getSqlQuery
        * 函数描述:  根据起始时间得到日志搜索的SQL查询语句
        * @param OperationTime1 搜索的起始时间
        * @param OperationTime2 搜索的终止时间
        * @param FileName     调用该函数的文件名
        * @return String[]     
        * "select c.OperationTime,c.UserName,c.LogType,c.Description,c.SerialNO,c.NodeName,c.SerialNOJoin,c.Remarks,a.anothername as Aanothername, b.anothername  as Banothername,d.CodeName as LogName 
            from ClientLog c left outer join devicepara a 
            on c.serialno=a.serialno 
            left outer join devicepara b 
            on c.serialnojoin = b.serialno 
            left outer join CODES d
            on c.LogType = d.Code 
            where OperationTime >= '2016-05-30 09:25:46' and OperationTime <= '2016-05-30 10:25:46' order by c.OperationTime"
    */
    public static String[] getSqlQuery(String OperationTime1,String OperationTime2,String FileName){
        if (OperationTime1 == null || OperationTime1.equals("")) return null;
        if (OperationTime2 == null || OperationTime2.equals("")) return null;
        String[] sqlQuery = new String[3];
        ////全部12项：操作时间、用户名、日志类型、描述信息、设备序列号、分组名、节点名 、接入设备序列号、接入通道、设备类型、被操作对象类型、注释（专门为日志搜索准备的）
        sqlQuery[0]= "select count(*) from ClientLog where OperationTime >= '" + OperationTime1 + "' and OperationTime <= '" + OperationTime2 + "'";
        sqlQuery[1]= "select c.OperationTime, c.UserName_oper, c.LogType, c.Description, c.SerialNO_oper, c.GROUPNAME_OPER, c.NodeName_oper, "
                        + "c.SerialNOJoin, c.CHANNELJOIN, c.DVRTYPE_OPER, c.OBJECTTYPE_OPER, c.Remarks,"
			+ "a.anothername as Aanothername, b.anothername  as Banothername,d.CodeName as LogName"
                        + " from ClientLog c left outer join devicepara a"
                        + " on c.serialno_oper=a.serialno"
                        + " left outer join devicepara b"
                        + " on c.serialnojoin = b.serialno "
                        + " left outer join CODES d " 
                        + " on c.LogType = d.Code "
                        + " where OperationTime >= '" + OperationTime1 + "' and OperationTime <= '" + OperationTime2 + "'";
        sqlQuery[2]= " order by c.OperationTime desc";   
        return sqlQuery;
    }
    
    public static String getInLimitOfSerialNO(String InLimit,String FileName){
        if (InLimit.equals("")) return "";
        return " SERIALNO_OPER in ("+InLimit + ")";//不加上设备序列号为空的记录
    }
    public static String getInLimitOfLogType(String InLimit,String FileName){
        if (InLimit.equals("")) return "";
        return " LOGTYPE in ("+InLimit + ")";
    }
    public static String getInLimitOfUserName(String InLimit,String FileName){
        if (InLimit.equals("")) return "";
        return " USERNAME_OPER in ("+InLimit + ")";
    }
    public static String getInLimitOfObjectType(String InLimit,String FileName){
        if (InLimit.equals("")) return "";
        return " OBJECTTYPE_OPER  in ("+InLimit + ")";//不加上操作对象为空的记录
        //return " OBJECTTYPE_OPER  in ("+InLimit + ",'')";//加上操作对象为空的记录
    }
    
}
