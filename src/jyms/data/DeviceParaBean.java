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

public class DeviceParaBean  implements Cloneable {

    private String sSerialNO;
    private String sDVRIP;
    private String sServerport;
    private String sUsername;
    private String sPassword;
    private String sAnothername;
    private String sDVRType = jyms.CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE;//编码设备/门口机
    private String ifsetupAlarm = "0";
    //数据库长度限制，别名长度不能>20
    public static final int ANOTHERNAME_LENGTH = 20;
    private static final String sFileName = "--->>DeviceparaBean.java";
    

    public DeviceParaBean() {    }

    public DeviceParaBean(String serialNO) {
        this.sSerialNO = serialNO==null?"":serialNO;
    }

    public DeviceParaBean(String serialNO,String dvrip, String serverport, String username, String password,  String anothername) {
        this.sSerialNO =  serialNO==null?"":serialNO;
        this.sDVRIP = dvrip==null?"":dvrip;
        this.sServerport = serverport==null?"":serverport;
        this.sUsername = username==null?"":username;
        this.sPassword = password==null?"":password;
        this.sAnothername = anothername==null?"":anothername;
    }
    
    public DeviceParaBean(String serialNO,String dvrip, String serverport, String username, String password,  String anothername,String DVRType) {
        this.sSerialNO =  serialNO==null?"":serialNO;
        this.sDVRIP = dvrip==null?"":dvrip;
        this.sServerport = serverport==null?"":serverport;
        this.sUsername = username==null?"":username;
        this.sPassword = password==null?"":password;
        this.sAnothername = anothername==null?"":anothername;
        this.sDVRType = DVRType==null?"":DVRType;
    }
    public DeviceParaBean(String serialNO,String dvrip, String serverport, String username, String password,  String anothername,String DVRType,String IfsetupAlarm) {
        this.sSerialNO =  serialNO==null?"":serialNO;
        this.sDVRIP = dvrip==null?"":dvrip;
        this.sServerport = serverport==null?"":serverport;
        this.sUsername = username==null?"":username;
        this.sPassword = password==null?"":password;
        this.sAnothername = anothername==null?"":anothername;
        this.sDVRType = DVRType==null?"":DVRType;
        this.ifsetupAlarm = IfsetupAlarm==null?"":IfsetupAlarm;
    }


    public String getDVRIP() {
        return sDVRIP;
    }

    public void setDVRIP(String dvrip) {
        this.sDVRIP = dvrip;
    }

    public String getServerport() {
        return sServerport;
    }

    public void setServerport(String serverport) {
        this.sServerport = serverport;
    }

    public String getUsername() {
        return sUsername;
    }

    public void setUsername(String username) {
        this.sUsername = username;
    }

    public String getPassword() {
        return sPassword;
    }

    public void setPassword(String password) {
        this.sPassword = password;
    }

    public String getSerialNO() {
        return sSerialNO;
    }

    public void setSerialNO(String serialNO) {
        this.sSerialNO = serialNO;
    }

    public String getAnothername() {
        return sAnothername;
    }

    public void setAnothername(String anothername) {
        this.sAnothername = anothername;
    }
    public String getDVRType() {
        return sDVRType;
    }
    public void setDVRType(String DVRType) {
        this.sDVRType = DVRType;
    }
    
    /**
     * @return the ifsetupAlarm
     */
    public String getIfsetupAlarm() {
        return ifsetupAlarm;
    }

    /**
     * @param ifsetupAlarm the ifsetupAlarm to set
     */
    public void setIfsetupAlarm(String ifsetupAlarm) {
        this.ifsetupAlarm = ifsetupAlarm;
    }
    
    @Override
    public DeviceParaBean clone() throws CloneNotSupportedException
    { 
        DeviceParaBean Cloned=(DeviceParaBean)super.clone();//Object 中的clone()识别出你要复制的是哪一个对象。 
        return Cloned; 
    }
    
    /**
	 * 函数:      getOneDeviceDetailPara
         * 函数描述:  获取设备参数表中的 设备序列号,IP地址,端口号,用户名,密码,别名,设备类型代码，设备类型（代码表中的代码名称）等参数
         * @param SerialNO     设备序列号
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList getOneDeviceDetailPara(String SerialNO, String FileName) {
        String sqlQuery;
        if (SerialNO == null || SerialNO.equals("")) return null;
        if (FileName == null || FileName.equals("")) return null;
        sqlQuery = "select devicepara.*,codes.CODENAME from devicepara,codes where  SerialNO = '" + SerialNO + "' and devicepara.DVRTYPE=codes.code";
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;
        try {
            if(rs.next()) {
                DeviceParaBean deviceparaBean = new DeviceParaBean(rs.getString(1),rs.getString(2),rs.getString(3),
                                                rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8)) ;
                ArrayList NewList = new ArrayList();
                
                NewList.add(deviceparaBean);
                String ss = rs.getString("CODENAME");
                NewList.add(ss==null?"":ss);
                return NewList;
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName, "getDeviceParaList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return InitDB.getInitDB(FileName + sFileName).executeQueryOneColList(sqlQuery) ;
    }
    
    /**
	 * 函数:      getDeviceParaList
         * 函数描述:  获取设备参数表中的 DeviceParaBean-0,设备类型-1（代码表中的代码名称）等参数；
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList getDeviceDetailParaList(String FileName) {
        String sqlQuery;
        if (FileName == null || FileName.equals("")) return null;
        ArrayList listDevicepara = new ArrayList();
        sqlQuery = "select devicepara.*,codes.CODENAME AS CODENAME from devicepara,codes where devicepara.DVRTYPE=codes.code order by SerialNO";
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;
        try {
            while(rs.next()) {
                DeviceParaBean deviceparaBean = new DeviceParaBean(rs.getString(1),rs.getString(2),rs.getString(3),
                                                rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8)) ;
                ArrayList NewList = new ArrayList();
                
                NewList.add(deviceparaBean);
                String ss = rs.getString("CODENAME");
                NewList.add(ss==null?"":ss);
                listDevicepara.add(NewList);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName, "getDeviceParaList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
         return listDevicepara;
    }
    /**
	 * 函数:      getDeviceParaList
         * 函数描述:  获取设备参数表中的 设备序列号,IP地址,端口号,用户名,密码,别名,设备类型代码等参数
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList<DeviceParaBean> getDeviceParaList(String FileName) {
         return getDeviceParaList("",FileName);
    }
    
    /**
	 * 函数:      getDeviceParaList
         * 函数描述:  获取设备参数表中的 设备序列号,IP地址,端口号,用户名,密码,别名,设备类型代码等参数
         * @param DVRTypeCode   设备类型代码
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList<DeviceParaBean> getDeviceParaList(String DVRTypeCode,String FileName) {
        ArrayList<DeviceParaBean> listDeviceparaBean = new ArrayList<>();
        String sqlQuery;
        if (DVRTypeCode == null || DVRTypeCode.equals("") || DVRTypeCode.equals("all")){
            sqlQuery = "select * from DEVICEPARA";
        }else {
            sqlQuery = "select * from DEVICEPARA  where DVRType = '" + DVRTypeCode + "'";
        }
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sqlQuery) ;
        try {
            while(rs.next()) {
                DeviceParaBean deviceparaBean = new DeviceParaBean(rs.getString(1),rs.getString(2),rs.getString(3),
                                                rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7)) ;
                listDeviceparaBean.add(deviceparaBean);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(FileName, "getDeviceParaList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(FileName + sFileName, "getDeviceParaList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
         return listDeviceparaBean;
    }
    /**
	 * 函数:      insertLoginDevice
         * 函数描述:  向设备基本参数表添加数据，向设备资源表添加数据，同时向设备预览参数表添加相关联的数据。同时添加成功则提交commit，否则则回滚rollback。
	 * @param listDeviceGroupBean   设备资源分组类列表
	 * @param deviceparaBean	 设备参数类
         * @param FileName             调用该函数的文件名
         * @return boolean：操作成功放回true；失败，返回false.
    */
    public static boolean insertLoginDevice(ArrayList<DeviceGroupBean> listDeviceGroupBean,DeviceParaBean deviceparaBean,String FileName){
        
        if (listDeviceGroupBean == null || deviceparaBean == null) return false;
        if (listDeviceGroupBean.isEmpty()) return false;
        
        InitDB DB = InitDB.getInitDB(FileName + sFileName);
        
        DB.setMyAutoCommit(false);
        for (int i=0;i<listDeviceGroupBean.size();i++){
            DeviceGroupBean deviceGroupBean = listDeviceGroupBean.get(i);
            String sqlInsert2 = "INSERT INTO DEVICEGROUP  (SERIALNO,NODENAME,GROUPNAME,RESOURCETYPE,SERIALNOJOIN   ) VALUES ('" 
                                    + deviceGroupBean.getSerialNO() + "', '" +  deviceGroupBean.getNodename()+"', '" 
                                    + deviceGroupBean.getGroupName()+ "', '"  + deviceGroupBean.getRescourceType() + "', '" 
                                    +  deviceGroupBean.getsSerialNOJoin() + "')";
            int iCount2 = DB.executeUpdate(sqlInsert2,false);
            if (iCount2 < 1) {
                DB.myRollback();

                DB.setMyAutoCommit(true);
                return false;
            }
            String sqlInsert3 = "INSERT INTO DEVICERESOURCE  (SERIALNO,NODENAME,RESOURCETYPE,SERIALNOJOIN  ) VALUES ('" 
                                    + deviceGroupBean.getSerialNO() + "', '" +  deviceGroupBean.getNodename()+"', '" 
                                    + deviceGroupBean.getRescourceType() + "', '" +  deviceGroupBean.getsSerialNOJoin()+ "')";
            int iCount3 = DB.executeUpdate(sqlInsert3,false);
            if (iCount3 < 1) {
                DB.myRollback();

                DB.setMyAutoCommit(true);
                return false;
            }
        }
        
        String sqlInsert = "INSERT INTO DEVICEPARA (SERIALNO, DVRIP, SERVERPORT, USERNAME, PASSWORD,  ANOTHERNAME, DVRTYPE) " +
"                           VALUES ('"+ deviceparaBean.getSerialNO() + "', '" + deviceparaBean.getDVRIP() + "', '" 
                            + deviceparaBean.getServerport() + "', '" + deviceparaBean.getUsername() + "', '" 
                            + deviceparaBean.getPassword() + "', '"  + deviceparaBean.getAnothername() + "', '"  
                            + deviceparaBean.getDVRType() + "')";
        int iCount = DB.executeUpdate(sqlInsert,false);
        if (iCount > 0){
            DB.myCommit();
            DB.setMyAutoCommit(true);
            return true;
        }else{
            DB.myRollback();
            DB.setMyAutoCommit(true);
            return false;
        }
    }
    /**
        *函数:      UpdateManagedDevice
        *函数描述:  修改设备参数表的设备数据
        * @param deviceparaBean	 DeviceparaBean
        * @param FileName       调用该函数的文件名
        * @return boolean：存在返回true；失败，返回false.
     */
    public static boolean UpdateManagedDevice(DeviceParaBean deviceparaBean, String FileName){
        if (deviceparaBean == null) return false;
        String sUpdateSql = "update DEVICEPARA set DVRIP = '" + deviceparaBean.getDVRIP() + "'," + "SERVERPORT = '" + deviceparaBean.getServerport()
                            + "',USERNAME= '" + deviceparaBean.getUsername()+ "',PASSWORD= '" + deviceparaBean.getPassword() 
                            + "',ANOTHERNAME= '" + deviceparaBean.getAnothername() + "'  where SERIALNO ='" + deviceparaBean.getSerialNO()+ "'";
        if (InitDB.getInitDB(FileName + sFileName).executeUpdate(sUpdateSql) > 0) return true;
        return false;
    } 
    /**
        *函数:      ModifyPassword
        *函数描述:  修改管理设备密码
        * @param deviceparaBean	 DeviceparaBean
        * @param FileName       调用该函数的文件名
        * @return boolean：存在返回true；失败，返回false.
     */
    public static boolean ModifyPassword(String NewPassWord, String SerialNO, String FileName){
        if (NewPassWord == null || NewPassWord.equals("")) return false;
        String sUpdateSql = "update DEVICEPARA set PASSWORD= '" + NewPassWord 
                            + "'  where SERIALNO ='" + SerialNO+ "'";
        if (InitDB.getInitDB(FileName + sFileName).executeUpdate(sUpdateSql) > 0) return true;
        return false;
    }
    /**
        *函数:      DeleteManagedDevice
        *函数描述:  删除设备参数表和设备分组表与该别名有关的设备数据和分组数据
        * @param SerialNO	 设备序列号
        * @param FileName       调用该函数的文件名
        * @return boolean：存在返回true；失败，返回false.
     */
    public static boolean DeleteManagedDevice(String SerialNO, String FileName){
        InitDB DB = InitDB.getInitDB(FileName + sFileName);
        
        DB.setMyAutoCommit(false);
        String sql3 = "delete  from DEVICEGROUP where SerialNO ='" + SerialNO + "'";
        
        int iCount3 = DB.executeUpdate(sql3,false);
        if (iCount3 < 1) {
            DB.myRollback();

            DB.setMyAutoCommit(true);
            return false;
        }
        
        String sql2 = "delete  from DEVICERESOURCE where SerialNO ='" + SerialNO + "'";
        
        int iCount2 = DB.executeUpdate(sql2,false);
        if (iCount2 < 1) {
            DB.myRollback();

            DB.setMyAutoCommit(true);
            return false;
        }
        
        String sql = "delete from DEVICEPARA where SerialNO ='" + SerialNO + "'";
        int iCount1 = DB.executeUpdate(sql,false);
        if (iCount1 > 0){
            DB.myCommit();
            DB.setMyAutoCommit(true);
            return true;
        }else{
            DB.myRollback();
            DB.setMyAutoCommit(true);
            return false;
        }

    }
    /**
        *函数:      getIfExistTheAnothername
        *函数描述:  数据库查询是否存在该设备别名
        * @param sAnothername	 设备别名
        * @param FileName       调用该函数的文件名
        * @return boolean：存在返回true；失败，返回false.
    */
    
    public static boolean getIfExistTheAnothername(String sAnothername,String FileName){
        String sql = "select ANOTHERNAME from DEVICEPARA where ANOTHERNAME ='" + sAnothername + "'";
        ResultSet rs = InitDB.getInitDB(FileName + sFileName).getRs(sql);
        if (rs == null) return false;
        try {
            if (!rs.next()) return false;
            else return true;
        } catch (SQLException e) {
            TxtLogger.append(FileName + sFileName, "getIfExistTheAnothername()","用户在数据查询是否存在该设备别名的过程中捕捉到SQLException" + "\r\n                       SQL：" + sql
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
            return false;
        }
    }
    /**
        *函数:      getNumsOfDVRType
        *函数描述:  数据库查询是否存在该类型设备
        * @param DVRTypeCode	 设备类型代码
        * @param FileName       调用该函数的文件名
        * @return int：存在返回存在的个数；不存在返回0；失败或参数错误或其他错误，返回-1
    */
    public static int getNumsOfDVRType(String DVRTypeCode,String FileName){

        if (DVRTypeCode == null || DVRTypeCode.equals("")) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        //条件：该类和其父类为DVRTypeCode的设备
        String sqlQuery = "select count(*) from DEVICEPARA  where DVRType = '" + DVRTypeCode + "' OR DVRType in (select code from Codes where uppercode='"+DVRTypeCode+"')";
        return InitDB.getInitDB(FileName + sFileName).getNums(sqlQuery);
    }
    
    /**
        *函数:      setupDVRAlarm
        *函数描述:  修改设备参数表的是否设置布防标志
        * @param SerialNO	 设备序列号
        * @param IfSetupAlarm	 boolean：是否已布防
        * @param FileName       调用该函数的文件名
        * @return boolean：存在返回true；失败，返回false.
     */
    public static boolean setupDVRAlarm(String SerialNO,boolean IfSetupAlarm, String FileName){
        if (SerialNO == null || SerialNO.equals("")) return false;
        
        String sUpdateSql = "update DEVICEPARA set IFSETUPALARM = '" + (IfSetupAlarm?1:0) + "'  where SERIALNO ='" + SerialNO + "'";
        return InitDB.getInitDB(FileName + sFileName).executeUpdate(sUpdateSql) > 0;
    }
    
}
