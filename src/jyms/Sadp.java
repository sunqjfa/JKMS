/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import java.io.File;

/**
 *
 * @author John
 */
//SDK接口说明,Sadp.dll . SADP 在线设备搜索
public interface Sadp extends StdCallLibrary {

    //Sadp INSTANCE = (Sadp) Native.loadLibrary("..\\lib\\Sadp",Sadp.class);//System.getProperty("user.dir")+File.separator+"lib\\PlayCtrl.dll"
    Sadp INSTANCE = (Sadp) Native.loadLibrary(System.getProperty("user.dir")+File.separator+"lib\\Sadp", Sadp.class);
    
    //错误号及说明
    public static final int SADP_NOERROR   = 0 ;// 没有错误 
    public static final int SADP_ALLOC_RESOURCE_ERROR  = 2001 ;// 资源分配错误 
    public static final int SADP_NOT_START_ERROR  = 2002 ;// SADP未启动 
    public static final int SADP_NO_ADAPTER_ERROR  = 2003 ;// 无网卡 
    public static final int SADP_GET_ADAPTER_FAIL_ERROR  = 2004 ;// 获取网卡信息失败 
    public static final int SADP_PARAMETER_ERROR  = 2005 ;// 参数错误 
    public static final int SADP_OPEN_ADAPTER_FAIL_ERROR  = 2006 ;// 打开网卡失败 
    public static final int SADP_SEND_PACKET_FAIL_ERROR  = 2007 ;// 发送数据失败 
    public static final int SADP_SYSTEM_CALL_ERROR  = 2008 ;// 系统接口调用失败 
    public static final int SADP_DENY_OR_TIMEOUT_ERROR  = 2009 ;// 设备拒绝处理 
    public static final int SADP_NPF_INSTALL_FAILED  = 2010 ;// 安装NPF服务失败 
    public static final int SADP_TIMEOUT  = 2011 ;// 超时 
    public static final int SADP_CREATE_SOCKET_ERROR  = 2012 ;// 创建socket失败 
    public static final int SADP_BIND_SOCKET_ERROR  = 2013 ;// 绑定socket失败 
    public static final int SADP_JOIN_MULTI_CAST_ERROR  = 2014 ;// 加入多播组失败 
    public static final int SADP_NETWORK_SEND_ERROR  = 2015 ;// 发送出错 
    public static final int SADP_NETWORK_RECV_ERROR  = 2016 ;// 接收出错 
    public static final int SADP_XML_PARSE_ERROR  = 2017 ;// 多播XML解析出错 
    public static final int SADP_LOCKED  = 2018 ;// 设备锁定 
    public static final int SADP_NOT_ACTIVATED   = 2019 ;// 设备未激活 
    public static final int SADP_RISK_PASSWORD  = 2020 ;// 风险高的密码 
    public static final int SADP_HAS_ACTIVATED   = 2021 ;// 设备已激活 
    
    //获取设备信息
    public static final int GET_DEVICE_CODE = 1;//获取设备信息命令
    public static final int MAX_DEVICE_CODE = 128;//设备返回码最大长度
    
    //信息类型，设备重启、设备下线、设备上线、设备更新等详见表
    public static final int SADP_ADD = 1;	//增加一设备
    public static final int SADP_UPDATE = 2;	//更新设备
    public static final int SADP_DEC = 3;	//设备下线
    public static final int SADP_RESTART = 4;	//设备重新启动
    public static final int SADP_UPDATEFAIL = 5;//设备更新失败
    
    //设备信息结构体
    public static class SADP_DEVICE_INFO extends Structure {
        public byte[] szSeries = new  byte[12];  /*设备系列（保留）*/
        public byte[] szSerialNO = new byte[48];  /*设备序列号*/
        public byte[] szMAC = new byte[20];  /*设备物理地址*/
        public byte[] szIPv4Address = new byte[16];  /* 设备IPv4地址*/
        public byte[] szIPv4SubnetMask = new byte[16];  /* 设备IPv4子网掩码*/
        public int dwDeviceType;  /* 设备类型，具体数值代表的设备型号*/
        public int dwPort;  /* 设备服务端口号*/
        public int wNumberOfEncoders;   /* 设备编码器个数，即设备编码通道个数。对于解码器，其值设为0*/
        public int dwNumberOfHardDisk;  /* 设备硬盘数目*/
        public byte[] szDeviceSoftwareVersion = new byte[48];  /* 设备软件版本号*/
        public byte[] szDSPVersion = new byte[48];   /* 设备DSP版本号*/
        public byte[] szBootTime = new byte[48];  /* 开机时间*/
        public int iResult;  /* 信息类型，设备重启、设备下线、设备上线、设备更新等，详见下表 */
        public byte[] szDevDesc = new byte[24];   /* 设备类型描述，与dwDeviceType对应*/
        public byte[] szOEMinfo = new byte[24];   /* OEM产商信息*/
        public byte[] szIPv4Gateway = new byte[16];   /* 设备IPv4网关*/
        public byte[] szIPv6Address = new byte[46];   /* 设备IPv6地址*/
        public byte[] szIPv6Gateway = new byte[46];   /* 设备IPv6网关*/
        public byte byIPv6MaskLen;   /* IPv6子网前缀长度*/
        public byte bySupport;   /* 按位表示，对应值为1表示支持 */
        public byte byDhcpEnabled;   /* Dhcp状态：0- 不启用，1- 启用*/
        public byte byDeviceAbility;   /* 设备能力集：0- 设备不支持以下功能 1- 设备支持上述功能 设备类型描述、OEM厂商、IPv4网关、IPv6地址、IPv6网 关、IPv6子网前缀、DHCP*/
        public short wHttpPort;   /* http端口*/
        public short wDigitalChannelNum;  /* 数字通道数*/
        public byte[] szCmsIPv4 = new byte[16];  /* CMS服务器IPv4地址*/
        public short wCmsPort;  /* CMS服务器监听端口 */
        public byte byOEMCode;   /* OEM标识：0-基线设备 1-OEM设备*/
        public byte byActivated;   /* 设备是否激活；0-未激活，1-激活*/
        public byte[] szBaseDesc = new byte[24];   /* 基线短型号，不随定制而修改的型号，用于萤石平台进行型号对比*/
        public byte[] byRes = new byte[16]; /* 保留*/

    }
    //获取设备安全码结构体
    public static class SADP_SAFE_CODE extends Structure {
        public int  dwCodeSize; /*设备返回码长度*/
        public byte[] szDeviceCode = new byte[MAX_DEVICE_CODE];/*设备返回码 */
        public byte[] byRes = new byte[128]; /*保留*/
    }
    
    //设备网络参数结构体
    public static class SADP_DEV_NET_PARAM extends Structure {
        public byte[] szIPv4Address = new byte[16]; /* IPv4地址 */
        public byte[] szIPv4SubNetMask = new byte[16];  /* IPv4子网掩码 */
        public byte[] szIPv4Gateway = new byte[16];  /* IPv4网关 */
        public byte[] szIPv6Address = new byte[128];  /* IPv6地址 */
        public byte[] szIPv6Gateway = new byte[128];  /* IPv6网关 */
        public short wPort; /* 设备监听端口 */
        public byte byIPv6MaskLen;  /* IPv6掩码长度 */
        public byte byDhcpEnable;  /* DHCP使能：0- 禁用，1- 启用 */
        public short wHttpPort; /* Http端口 */
        public byte[] byRes = new byte[126]; /* 保留 */
    }
 /**
        *内部类:   SADP_DEV_NET_PARAM_JAVA
        *类描述:   设备网络参数结构体。对应sadp的结构SADP_DEV_NET_PARAM。
        *          是为了保存临时数据用的
    */  
    public static class SADP_DEV_NET_PARAM_JAVA implements Cloneable {
        //下面数据项主要是为了修改设备的网络信息的
        private String szSerialNO;/*设备序列号*/
        private String szMAC;/*设备物理地址*/
        private String szIPv4Address ;/* IPv4地址 */
        private String szIPv4SubNetMask;  /* IPv4子网掩码 */
        private String szIPv4Gateway ; /* IPv4网关 */
        private String szIPv6Address ; /* IPv6地址 */
        private String szIPv6Gateway ; /* IPv6网关 */
        private String sPort;/* 设备监听端口 */
        private String sIPv6MaskLen;   /* IPv6掩码长度 */
        private String sDhcpEnable;   /* DHCP使能：0- 禁用，1- 启用 */
        private String sHttpPort;  /* Http端口 */

        //下面的数据主要是为了更新在线设备的信息的
        private String szDevDesc;  /* 设备类型描述 */
        private String szDeviceSoftwareVersion;  /* 主控版本 */
        private String szActivated;  /* 设备是否激活 0-已激活，1-未激活 */
        private String szBootTime;  /* 设备开机时间 */

        public SADP_DEV_NET_PARAM_JAVA(){}

        public String getSerialNO() {
            return szSerialNO ;
        }

        public void setSerialNO (String SerialNO) {
            this.szSerialNO  = SerialNO;
        }
        
        public String getMAC() {
            return szMAC ;
        }

        public void setMAC (String MAC) {
            this.szMAC  = MAC;
        }
        
        public String getIPv4Address() {
            return szIPv4Address ;
        }

        public void setIPv4Address (String dvrip) {
            this.szIPv4Address  = dvrip;
        }

        public String getIPv4SubNetMask() {
            return szIPv4SubNetMask;
        }

        public void setIPv4SubNetMask(String IPv4SubNetMask) {
            this.szIPv4SubNetMask = IPv4SubNetMask;
        }

        public String getIPv4Gateway() {
            return szIPv4Gateway;
        }

        public void setIPv4Gateway(String IPv4Gateway) {
            this.szIPv4Gateway = IPv4Gateway;
        }
        public String getIPv6Address() {
            return szIPv6Address;
        }

        public void setIPv6Address(String IPv6Address) {
            this.szIPv6Address = IPv6Address;
        }
        public String getIPv6Gateway() {
            return szIPv6Gateway;
        }

        public void setIPv6Gateway(String IPv6Gateway) {
            this.szIPv6Gateway = IPv6Gateway;
        }
        public String getPort() {
            return sPort;
        }

        public void setPort(String Port) {
            this.sPort = Port;
        }
        public String getIPv6MaskLen() {
            return sIPv6MaskLen;
        }

        public void setIPv6MaskLen(String IPv6MaskLen) {
            this.sIPv6MaskLen = IPv6MaskLen;
        }
        public String getDhcpEnabled() {
            return sDhcpEnable;
        }

        public void setDhcpEnabled(String DhcpEnable) {
            this.sDhcpEnable = DhcpEnable;
        }
        public String getHttpPort() {
            return sHttpPort;
        }

        public void setHttpPort(String HttpPort) {
            this.sHttpPort = HttpPort;
        }

        /**
         * @return the szDevDesc
         */
        public String getSzDevDesc() {
            return szDevDesc;
        }

        /**
         * @param szDevDesc the szDevDesc to set
         */
        public void setSzDevDesc(String szDevDesc) {
            this.szDevDesc = szDevDesc;
        }

        /**
         * @return the szDeviceSoftwareVersion
         */
        public String getSzDeviceSoftwareVersion() {
            return szDeviceSoftwareVersion;
        }

        /**
         * @param szDeviceSoftwareVersion the szDeviceSoftwareVersion to set
         */
        public void setSzDeviceSoftwareVersion(String szDeviceSoftwareVersion) {
            this.szDeviceSoftwareVersion = szDeviceSoftwareVersion;
        }

        /**
         * @return the byActivated
         */
        public String getSzActivated() {
            return szActivated;
        }

        /**
         * @param byActivated the byActivated to set
         */
        public void setaSzActivated(String byActivated) {
            this.szActivated = byActivated;
        }

        /**
         * @return the szBootTime
         */
        public String getSzBootTime() {
            return szBootTime;
        }

        /**
         * @param szBootTime the szBootTime to set
         */
        public void setSzBootTime(String szBootTime) {
            this.szBootTime = szBootTime;
        }
        
        @Override
        public SADP_DEV_NET_PARAM_JAVA clone(){
                SADP_DEV_NET_PARAM_JAVA Cloned;
            try {
                Cloned = (SADP_DEV_NET_PARAM_JAVA)super.clone(); //Object 中的clone()识别出你要复制的是哪一个对象。
                return Cloned;
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
            return null;
         }

    }
    // 设备CMS参数结构体
    public static class SADP_CMS_PARAM  extends Structure {
        public byte[] szPUID = new byte[32]; /* 预分配的PUID */
        public byte[] szPassword = new byte[16];  /* 设置的登录密码 */
        public byte[] szCmsIPv4 = new byte[16];  /* CMS服务器IPv4地址 */
        public byte[] szCmsIPv6 = new byte[128];  /* CMS服务器IPv6地址 */
        public short wCmsPort; /* CMS服务器监听端口 */
        public byte[] byRes = new byte[30]; /* 保留 */
    }
    //SADP_Start_V30的回调函数
    public static interface PDEVICE_FIND_CALLBACK extends StdCallLibrary.StdCallCallback {
        public void invoke(SADP_DEVICE_INFO lpDeviceInfo,Pointer pUserData);
    }
    
    /**
	 * 启动SADP在线设备搜索,在该接口设置的回调函数中可以获取得到在线的设备信息
	 * @param  pDeviceFindCallBack 	回调函数，获取在线设备信息 [in]
	 * @param bInstallNPF 是否安装NPF服务, 只针对window系统，默认安装：0- 不安装，非0- 安装（安装需要管理员权限） [in]
	 * @param  pUserData 用户数据指针 [in]
         * @return 返回值int：1表示成功，0表示失败。函数返回失败请调用接口SADP_GetLastError获取错误码。 
    */
    int SADP_Start_V30(PDEVICE_FIND_CALLBACK pDeviceFindCallBack, int bInstallNPF , Pointer pUserData) ;
//    int SADP_Start_V30(PDEVICE_FIND_CALLBACK pDeviceFindCallBack) ;
    int SADP_Stop() ;//停止SADP在线搜索
    //激活设备
    /**
	 * 函数:      SADP_SendInquiry
         * 函数描述:  手动刷新搜索启动SADP在线设备搜索
	 * @param  sDevSerialNO 设备序列号
	 * @param sCommand  设置的密码
         * @return 返回值int：1表示成功，0表示失败。函数返回失败请调用接口SADP_GetLastError获取错误码。 
    */
    int SADP_ActivateDevice(String sDevSerialNO, String sCommand) ;
   
    /**
	 * SADP_GetDeviceConfig获取设备信息
         * @param  sDevSerialNO  	[in]设备序列号
         * @param  dwCommand [in]获取命令 [in]
         * @param  lpInBuffer [in]输入参数，具体内容跟dwCommand有关[in] 
	 * @param  dwinBuffSize [in]输入缓冲区大小 [in]
	 * @param  lpOutBuffer 	输出缓冲区，具体内容跟dwCommand有关 [out]
         * @param  dwOutBuffSize 输出缓冲区大小[in]  
         * @return 返回值int：1表示成功，0表示失败。函数返回失败请调用接口SADP_GetLastError获取错误码。 
         * 说  明： 	不同的获取功能对应不同的结构体和命令号，如下表所示。 
         * dwCommand        dwCommand含义 	lpInBuffer 	dwinBuffSize 	lpOutBuffer             dwOutBuffSize                   dwCommand值 
         * GET_DEVICE_CODE 	获取设备码 	NULL            0               SADP_SAFE_CODE结构体 	SADP_SAFE_CODE结构体长度 	1 
    */
    int SADP_GetDeviceConfig(String sDevSerialNO, int dwCommand,Pointer lpInBuffer, int  dwinBuffSize, Pointer lpOutBuffer, int  dwOutBuffSize);
    

    /**
        *函数:      SADP_SendInquiry
        *函数描述:  手动刷新搜索
        * @return int。1表示成功，0表示失败。
     */
    int SADP_SendInquiry();
   
    /**
	 * 修改设备网络参数，修改设备IP地址、端口、网关、掩码、DHCP等网络参数。
	 * @param sMAC 设备物理地址[in]
	 * @param sPassword 设备admin用户的密码[in] 
	 * @param lpNetParam  需要修改的网络参数，详见：SADP_DEV_NET_PARAM [in]
         * @return 返回值int：1表示成功，0表示失败。函数返回失败请调用接口SADP_GetLastError获取错误码。 
     */
    int SADP_ModifyDeviceNetParam(String sMAC, String sPassword, SADP_DEV_NET_PARAM lpNetParam);
    /**
	 * 设置设备CMS信息，需要设备支持，当前仅推模式设备（如单兵、车载）支持。 
	 * @param sMac 设备物理地址
	 * @param lpCmsParam 需要设置的CMS参数，详见：SADP_CMS_PARAM [in]
         * @return 返回值int：1表示成功，0表示失败。函数返回失败请调用接口SADP_GetLastError获取错误码。 
    */
    int SADP_SetCMSInfo(String sMac, SADP_CMS_PARAM lpCmsParam) ;
    //清理搜索到的设备 
    int SADP_Clearup() ;
    //启用写日志文件
    int SADP_SetLogToFile(int nLogLevel, String strLogDir, int bAutoDel);
    //获取SDK版本信息
    int SADP_GetSadpVersion() ;
    //获取错误号
    int SADP_GetLastError();
}