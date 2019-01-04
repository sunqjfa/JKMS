/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.NativeLong;
import java.util.Arrays;
import jyms.data.TxtLogger;

/**
 *
 * @author John
 */
public class HCNetSDKExpand {
    
    private static String sFileName = "--->>HCNetSDKExpand.java";
    
    /**
	 * 函数:      isIPD
         * 函数描述:  判断是否是球机
         * @param wDevType 设备型号
         * @return boolean   是true。否false
    */
    public static boolean isIPD(short wDevType){
        return (wDevType == HCNetSDK.IPDOME || wDevType == HCNetSDK.IPDOME_MEGA200  || wDevType == HCNetSDK.IPDOME_MEGA130);
    }
    /**
	 * 函数:      isFishEye
         * 函数描述:  判断设备是否是鱼眼
         * @param wDevType 设备型号
         * @return boolean   是true。否false
    */
    public static boolean isFishEye(short wDevType){
        return (wDevType == HCNetSDK.IPCAM_FISHEYE);
    }
    
    /**
        * 函数:      isFishEye
        * 函数描述:  判断设备是否是鱼眼
        * @param StrDevInfo HCNetSDK.NET_DVR_DEVICEINFO_V30设备参数结构体。
        * @param m_strIpparaCfg HCNetSDK.NET_DVR_IPPARACFGIP设备资源及IP通道资源配置结构体
        * @param Channel    通道号
        * @param FileName   调用该函数的文件名
        * @return boolean   是true。否false
    */
    public static boolean isFishEye(HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo, HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg, int Channel, String FileName){
        try{
            if (StrDevInfo == null) return false;
            if (isFishEye(StrDevInfo.wDevType)) return true;
            //前面判断非接入设备是否是鱼眼
            //下面判断是否是接入设备，根据HCNetSDK.NET_DVR_IPPARACFG，得出接入设备参数，然后判断
            if (m_strIpparaCfg == null) return false;
            int iIPChanel = Channel - (HCNetSDK.MAX_ANALOG_CHANNUM + StrDevInfo.byStartChan);
            if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return false;//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）

            byte iDevID = m_strIpparaCfg.struIPChanInfo[iIPChanel].byIPID;
            String IP4 = new String (m_strIpparaCfg.struIPDevInfo[iDevID - 1].struIP.sIpV4).trim();
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfoIP = CommonParas.getStrDeviceInfo(CommonParas.getIndexOfDeviceList(IP4, "", sFileName), sFileName);
            if (StrDevInfoIP == null) return false;
            if (isFishEye(StrDevInfoIP.wDevType)) return true;

        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "isFishEye()","系统在判断设备是否是鱼眼过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    
    /**
        * 函数:      isFishEyeFirstChannel
        * 函数描述:  判断设备是否是鱼眼第一通道
        * @param StrDevInfo HCNetSDK.NET_DVR_DEVICEINFO_V30设备参数结构体。
        * @param Channel    通道号
        * @param FileName   调用该函数的文件名
        * @return boolean   是true。否false
    */
    public static boolean isFishEyeFirstChannel(HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo,  int Channel, String FileName){
        try{
            if (StrDevInfo == null) return false;
            if (isFishEye(StrDevInfo.wDevType) && Channel == 1) return true;//如果是鱼眼第一通道，返回true

        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "isFishEye()","系统在判断设备是否是鱼眼过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    
    /**
        * 函数:      isFishEyeFirstChannel
        * 函数描述:  判断设备是否是鱼眼第一通道
        * @param StrDevInfo HCNetSDK.NET_DVR_DEVICEINFO_V30设备参数结构体。
        * @param m_strIpparaCfg HCNetSDK.NET_DVR_IPPARACFGIP设备资源及IP通道资源配置结构体
        * @param Channel    通道号
        * @param FileName   调用该函数的文件名
        * @return boolean   是true。否false
    */
    public static boolean isFishEyeFirstChannel(HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo, HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg, int Channel, String FileName){
        try{
            if (StrDevInfo == null) return false;
            if (isFishEye(StrDevInfo.wDevType) && Channel == 1) return true;//如果是鱼眼第一通道，返回true
            //前面判断非接入设备是否是鱼眼
            //下面判断是否是接入设备，根据HCNetSDK.NET_DVR_IPPARACFG，得出接入设备参数，然后判断
            if (m_strIpparaCfg == null) return false;
            int iIPChanel = Channel - (HCNetSDK.MAX_ANALOG_CHANNUM + StrDevInfo.byStartChan);
            if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return false;//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）

            byte iDevID = m_strIpparaCfg.struIPChanInfo[iIPChanel].byIPID;
            String IP4 = new String (m_strIpparaCfg.struIPDevInfo[iDevID - 1].struIP.sIpV4).trim();
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfoIP = CommonParas.getStrDeviceInfo(CommonParas.getIndexOfDeviceList(IP4, "", sFileName), sFileName);
            if (StrDevInfoIP == null) return false;
            int ChannelJoin = m_strIpparaCfg.struIPChanInfo[iIPChanel].byChannel;
            if (isFishEye(StrDevInfoIP.wDevType) && ChannelJoin == 1) return true;//如果该通道为接入鱼眼的第一通道，返回true

        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "isFishEye()","系统在判断设备是否是鱼眼过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /**
	 * 函数:      isHavePTZAbility
         * 函数描述:  判断设备是否具有云台
         *            现在暂时用isIPD（是否是球机）判断，具有云台的只有球机
         * @param wDevType 设备型号
         * @return boolean   是true。否false
    */
    public static boolean isHavePTZAbility(short wDevType){
        return isIPD(wDevType);
    }

    /**
        * 函数:      isHavePTZAbility
        * 函数描述:  判断设备是否具有云台
        *            现在暂时用isIPD（是否是球机）判断，具有云台的只有球机
        * @param StrDevInfo 设备登陆后得到的设备参数结构体
        * @param m_strIpparaCfg IP设备资源及IP通道资源配置结构体
        * @param Channel    预览以及参数配置要使用的音视频通道号
        * @param FileName   调用的文件名
        * @return boolean   是true。否false
    */
    public static boolean isHavePTZAbility(HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo, HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg, int Channel, String FileName){
 
        try{
            if (StrDevInfo == null) return false;
            if (isHavePTZAbility(StrDevInfo.wDevType)) return true;
            //前面判断非接入设备是否具有云台
            //下面判断是否是接入设备，根据HCNetSDK.NET_DVR_IPPARACFG，得出接入设备参数，然后判断
            if (m_strIpparaCfg == null) return false;
            int iIPChanel = Channel - (HCNetSDK.MAX_ANALOG_CHANNUM + StrDevInfo.byStartChan);
            if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return false;//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）

            byte iDevID = m_strIpparaCfg.struIPChanInfo[iIPChanel].byIPID;
            String IP4 = new String (m_strIpparaCfg.struIPDevInfo[iDevID - 1].struIP.sIpV4).trim();
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfoIP = CommonParas.getStrDeviceInfo(CommonParas.getIndexOfDeviceList(IP4, "", sFileName), sFileName);
            if (StrDevInfoIP == null) return false;
            
            if (isHavePTZAbility(StrDevInfoIP.wDevType)) return true;

        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "isHavePTZAbility()","系统在判断设备是否具有云台过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    
   
    /**
	 * 函数:      isNVR
         * 函数描述:  判断该设备是否是NVR 
         * @param StrDevInfo HCNetSDK.NET_DVR_DEVICEINFO_V30
         * @return boolean   是true。否false
    */
    public static boolean isNVR(HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo){
        int ChanNum = StrDevInfo.byChanNum;//设备模拟通道个数，数字（IP）通道最大个数为byIPChanNum + byHighDChanNum*256 
                    //设备模拟通道个数，数字（IP）通道最大个数为byIPChanNum + byHighDChanNum*256 
        int IPChanNum = StrDevInfo.byIPChanNum + StrDevInfo.byHighDChanNum*256;
        
        //模拟通道个数大于0、IP通道 个数为0，表示DVR或者IPC；
        //模拟通道个数为0、IP通道个数大于0，表示NVR或者CVR；
        //模拟通道个数大于0、IP通道个数也大于0，表示混合型DVR
        //模拟通道个数为0、IP通道个数大于0
        return ChanNum == 0 && IPChanNum > 0;
    }
    

    
    /**
        * 函数:      GetDeviceAbility
        * 函数描述:  获取设备能力集可以判断设备是否支持相关功能
        * @param AnotherName    设备别名
        * @param Channel    通道号（预览或者设置用的，包括IP通道号）
        * @param Command    能力集命令，例如：鱼眼能力集HCNetSDK.FISHEYE_ABILITY
        * @param InBufLeft  XML输入描述的左半部分。例如："《FishEyeIPCAbility version=\"2.0\"》《channelNO》"（用《》代替<>）
        * @param InBufRight  XML输入描述的右半部分。例如： "《/channelNO》《/FishEyeIPCAbility》"（用《》代替<>）
        * @param pOutBuf    输出缓冲区 例如：byte[32*1024]
        * @return 成功 返回1，失败返回0；程序错误或者参数错误返回-1
    */
    public static int GetDeviceAbility(String AnotherName, int Channel, int Command, String InBufLeft, String InBufRight, byte[] pOutBuf){
        try{
            if (AnotherName.equals("") ) return -1;

            int Index  = CommonParas.getIndexOfDeviceList(AnotherName, sFileName);
            if (Index == -1) return -1;
            
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo = CommonParas.getStrDeviceInfo(Index, sFileName);
            HCNetSDK.NET_DVR_IPPARACFG StrIpparaCfg = CommonParas.getStrIpparacfg(Index, sFileName);
            NativeLong UserID = CommonParas.getUserID(Index, sFileName);
            String pInBuf = InBufLeft + Channel + InBufRight;
            boolean bRet = false;
            if (StrIpparaCfg != null){
                    String IPJoin = CommonParas.getDVRIPJoin(StrDevInfo, StrIpparaCfg, Channel, sFileName);

                    NativeLong UserID2 = CommonParas.getUserID(CommonParas.getIndexOfDeviceList(IPJoin, "", sFileName), sFileName);
                    int iIPChanel = Channel - (HCNetSDK.MAX_ANALOG_CHANNUM + StrDevInfo.byStartChan);
                    if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return -1;//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）
                    int ChannelJoin = StrIpparaCfg.struIPChanInfo[iIPChanel].byChannel;
                    pInBuf = InBufLeft + ChannelJoin + InBufRight;//"<AlarmAbility><channelID>" + ChannelJoin + "</channelID></AlarmAbility>";

                    bRet = CommonParas.hCNetSDK.NET_DVR_GetDeviceAbility(UserID2, Command, pInBuf, pInBuf.length(), pOutBuf, pOutBuf.length);
            }else   bRet = CommonParas.hCNetSDK.NET_DVR_GetDeviceAbility( UserID, Command, pInBuf, pInBuf.length(), pOutBuf, pOutBuf.length);
            //写错误日志
            if (!bRet) CommonParas.SystemWriteErrorLog(sGetDeviceCapabilitySetsFail,  AnotherName, sFileName);//"获取设备能力集失败"
            return bRet?1:0;
        }catch (Exception e){
            TxtLogger.append(sFileName, "GetDeviceAbility()","系统在获取设备报警能力集可以判断设备是否支持相关功能过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }
    /**
        * 函数:      NET_DVR_Login_V40
        * 函数描述:  为了和NET_DVR_Login_V30兼容，用和NET_DVR_Login_V30几乎相同的参数进行登录
        * @param DVRIP 设备IP地址
        * @param DVRPort 设备端口号
        * @param UserName 设备用户名
        * @param Password 设备密码
        * @param lpDeviceInfo   HCNetSDK.NET_DVR_DEVICEINFO_V40，NET_DVR_Login_V40()参数结构。设备参数结构体。
        * @param FileName   调用的文件
        * @return 设备登录ID
    */
    public static NativeLong  NET_DVR_Login_V40(String DVRIP, short DVRPort, String UserName, String Password, HCNetSDK.NET_DVR_DEVICEINFO_V40  lpDeviceInfo, String FileName){
        try{
            HCNetSDK.NET_DVR_USER_LOGIN_INFO StruUserLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();
            StruUserLoginInfo.sUserName = Arrays.copyOf(UserName.getBytes(), HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN);
            StruUserLoginInfo.sPassword = Arrays.copyOf(Password.getBytes(), HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN);
            StruUserLoginInfo.sDeviceAddress = Arrays.copyOf(DVRIP.getBytes(), HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN);
            StruUserLoginInfo.wPort = DVRPort;
            StruUserLoginInfo.bUseAsynLogin = 0;//是否异步登录：0- 否，1- 是 
            StruUserLoginInfo.cbLoginResult = null;//登录状态回调函数，bUseAsynLogin 为1时有效 
            return CommonParas.hCNetSDK.NET_DVR_Login_V40(StruUserLoginInfo, lpDeviceInfo);
        }catch (Exception e){
            TxtLogger.append(FileName + sFileName, "NET_DVR_Login_V40()","系统在设备登录过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return new NativeLong(-1);
    }
    /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    public static void modifyLocales(){
        
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作
        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        sGetDeviceCapabilitySetsFail =        Locales.getString("ClassStrings", "HCNetSDKExpand.sGetDeviceCapabilitySetsFail");  //获取设备能力集失败
    }
    private static String sGetDeviceCapabilitySetsFail = "获取设备能力集失败";
    
}
