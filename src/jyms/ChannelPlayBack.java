/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.ptr.IntByReference;
import java.awt.Panel;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import jyms.data.DeviceParaBean;
import jyms.data.TxtLogger;
import jyms.tools.DateUtil;
import jyms.tools.DateUtil.DateHandler;

/**
 *通道录像回放类
 * @author John
 */
public class ChannelPlayBack{
        
        private String sFileName = this.getClass().getName() + ".java";
        private final double MaxSpeed = 8;// 8x
        private final double MinSpeed = 0.125;// 1/8x
        private int iIndexOflistDevice = -1;//对应的g_listDeviceDetailPara的索引号，从索引号可以得到UseriID,和其他相关设备信息
        //和mDeviceParaBean同时赋值，用处不同。主要用于取得UserID
        private DeviceParaBean mDeviceParaBean = null;//和iIndexOflistDevice同时赋值，用处不同。
        private String sSerialNoJoin = "";//接入设备的序列号
        private String sEncodingDVRChannelNode = "";//编码通道节点名称，比如：IP监控点_01
        private String sWholeDVRChannelNode = "";//完整的编码通道节点名称，比如：录像机_枪机_IP监控点_01
        private Panel panelPlayBack = null;//对应的Panel
        private NativeLong lChannel = new NativeLong(-1);//通道号
        private NativeLong lUserID = new NativeLong(-1);//用户登录句柄
        private NativeLong playBackHandle = new NativeLong(-1);//回放句柄
        
        private Timer Playbacktimer;//回放用定时器
        /**
            * 还需要写入的参数包括：
            * 正在现在的状态：播放1/暂停3
            * 播放速度：正常7/快速5/慢速6
            * 声音打开与否：打开9/关闭10
            * 音量大小：
            * 回放方式：按文件回放/按时间回放
            * 按时间回放的起止时间
            * 按文件回放的文件名
        */
        private int iPlayState = 0;//正在现在的状态：HCNetSDK.NET_DVR_PLAYSTART播放1/HCNetSDK.NET_DVR_PLAYPAUSE暂停3/初始状态0
        private int iPlayDirection = 29;//NET_DVR_PLAY_FORWARD 29 倒放切换为正放/NET_DVR_PLAY_REVERSE 30 正放切换为倒放 
        private double dPlaySpeed = 1;//速度倍数。如果iPlaySpeed是快速，则为1x、2x、4x、8x、16x；如果dPlaySpeed是慢速，则为1/2x、1/2x、1/4x、1/8x、1/16x
        private int iAudioOpen = 9;//声音打开与否：NET_DVR_PLAYSTARTAUDIO打开9/NET_DVR_PLAYSTOPAUDIO关闭10
        private int iAudioSize;//音量大小：
        private String sPlayBackMode = "time";//回放方式：按文件回放File/按时间回放Time
        private String sPlayFileName = "";//回放的录像文件名
        private HCNetSDK.NET_DVR_TIME struStartTime = new HCNetSDK.NET_DVR_TIME();//回放的起始时间
        private HCNetSDK.NET_DVR_TIME struStopTime = new HCNetSDK.NET_DVR_TIME();//回放的终止时间
        private String sRemarks  = "";//日志记录中的备注内容：回放的当前的录像文件名/起止时间
        private String sRemarks2 = "";//日志记录中的附加备注内容：下载文件/抓图文件名称
        private ArrayList<HCNetSDK.NET_DVR_TIME_EX> listRecordLostStartTime = new ArrayList<>();//缺失录像的起始时间列表
        private ArrayList<HCNetSDK.NET_DVR_TIME_EX> listRecordLostStopTime = new ArrayList<>();//缺失录像的终止时间列表
        private boolean bIfRecordChecked = false;//是否已经进行过录像完整性检测。没有检测成功过，不算。
        
        private IntByReference nFileTime = new IntByReference(0);//文件总时间
        private IntByReference nTotalFrames = new IntByReference(0);//文件总帧数
        private int iTotalBytes = 0;//总字节数

        private int nTotalSecond = 0;//总秒数
        private int nTotalMinute = 0;//总分钟数
        private int nTotalHour = 0;//总小时

        private boolean bGetMaxTime;//是否已获得总播放时间,在计时器响函数里,只需要调用一次
        private boolean bIfSaveFile;//是否在保存文件
        private String sSaveFileName;//保存的文件名
        
        public ChannelPlayBack(){}
        
        public void intialParas(){
            iIndexOflistDevice = -1;//对应的g_listDeviceDetailPara的索引号，从索引号可以得到UseriID,和其他相关设备信息
            mDeviceParaBean = null;//和iIndexOflistDevice同时赋值，用处不同。
            sSerialNoJoin = "";//接入设备的序列号
            sEncodingDVRChannelNode = "";//编码通道节点名称
            sWholeDVRChannelNode = "";//完整的编码通道节点名称，比如：录像机_枪机_IP监控点_01
            panelPlayBack = null;//对应的Panel
            lChannel = new NativeLong(-1);//通道号
            lUserID = new NativeLong(-1);//用户登录句柄
            playBackHandle = new NativeLong(-1);//回放句柄
            if (Playbacktimer != null) Playbacktimer.cancel();
            iPlayState = 0;//正在现在的状态：播放1/暂停3/初始状态0
            iPlayDirection = 29;//NET_DVR_PLAY_FORWARD 29 倒放切换为正放/NET_DVR_PLAY_REVERSE 30 正放切换为倒放 
            dPlaySpeed = 1;//倍速为1x
            iAudioOpen = 9;//声音打开与否：打开9/关闭10
            sPlayFileName = "";//回放的录像文件名
            
            struStartTime.clear();//回放的起始时间
            struStopTime.clear();//回放的终止时间
            sRemarks = "";//日志记录中的备注内容：回放的当前的录像文件名/起止时间
            sRemarks2 = "";//日志记录中的附加备注内容：下载文件/抓图文件名称
            listRecordLostStartTime.clear();////缺失录像的起始时间列表
            listRecordLostStopTime.clear();//缺失录像的终止时间列表
            bIfRecordChecked = false;//是否已经进行过录像完整性检测。没有检测成功过，不算。
            
            nFileTime = new IntByReference(0);//文件总时间
            nTotalFrames = new IntByReference(0);//文件总帧数
            iTotalBytes = 0;//总字节数

            nTotalSecond = 0;//总秒数
            nTotalMinute = 0;//总分钟数
            nTotalHour = 0;//总小时
            bGetMaxTime = false;//是否已获得总播放时间,在计时器响函数里,只需要调用一次
            bIfSaveFile = false;//是否在保存文件
            sSaveFileName = "";//保存的文件名
        }
        

        
        /**
         * @return the indexOflistDeviceDetailPara
         */
        public int getIndexOflistDevice() {
            return iIndexOflistDevice;
        }

        /**
            * 函数:      setIndexOflistDevice
            * 函数描述:  设置设备列表索引，同时设置设备BEAN、设备登录ID
            * @param indexOflistDeviceDetailPara  设备列表索引
        */
        public void setIndexOflistDevice(int indexOflistDeviceDetailPara) {
            this.iIndexOflistDevice = indexOflistDeviceDetailPara;
            setDeviceParaBean(CommonParas.getDeviceParaBean(indexOflistDeviceDetailPara, sFileName));
            setUserID(CommonParas.getUserID(iIndexOflistDevice, sFileName));
        }
        
        /**
        * @return the mDeviceParaBean
        */
       public DeviceParaBean getDeviceParaBean() {
           return mDeviceParaBean;
       }

       /**
        * @param DeviceParaBean the mDeviceParaBean to set
        */
       private void setDeviceParaBean(DeviceParaBean DeviceParaBean) {
           this.mDeviceParaBean = DeviceParaBean;
       }
        /**
         * @return the panelPlayBack
         */
        public Panel getPanelPlayBack() {
            return panelPlayBack;
        }

        /**
         * @param panelPlayBack the panelPlayBack to set
         */
        public void setPanelPlayBack(Panel panelPlayBack) {
            this.panelPlayBack = panelPlayBack;
        }

        /**
         * @return the channelNO
         */
        public NativeLong getChannelNO() {
            return lChannel;
        }

        /**
         * @param channelNO the channelNO to set
         */
        public void setChannelNO(NativeLong channelNO) {
            this.lChannel = channelNO;
        }
        /**
            * @return the lUserID
        */
        public NativeLong getUserID() {
            return lUserID;
        }

        /**
         * @param lUserID the lUserID to set
         */
        private void setUserID(NativeLong lUserID) {
            this.lUserID = lUserID;
        }
        
        /**
         * @return the playBackHandle
         */
        public NativeLong getPlayBackHandle() {
            return playBackHandle;
        }

        /**
         * @param playBackHandle the playBackHandle to set
         */
        public void setPlayBackHandle(NativeLong playBackHandle) {
            this.playBackHandle = playBackHandle;
        }

        

        /**
         * @return the Playbacktimer
         */
        public Timer getPlaybacktimer() {
            return Playbacktimer;
        }

        /**
         * @param Playbacktimer the Playbacktimer to set
         */
        public void setPlaybacktimer(Timer Playbacktimer) {
            this.Playbacktimer = Playbacktimer;
        }

        /**
         * @return the iPlayState
         */
        public int getPlayState() {
            return iPlayState;
        }

        /**
         * @param iPlayState the iPlayState to set
         */
        public void setPlayState(int iPlayState) {
            this.iPlayState = iPlayState;
        }

        /**
            * @return the iPlayDirection
        */
        public int getPlayDirection() {
            return iPlayDirection;
        }

        /**
         * @param PlayDirection the iPlayDirection to set
        */
        public void setPlayDirection(int PlayDirection) {
            this.iPlayDirection = PlayDirection;
        }
        
        /**
         * @return the iPlaySpeed
         */
        public double getPlaySpeed() {
            return dPlaySpeed;
        }

        /**
         * @param iPlaySpeed the iPlaySpeed to set
         */
        public void setPlaySpeed(double iPlaySpeed) {
            this.dPlaySpeed = iPlaySpeed;
        }

        /**
         * @return the iAudioOpen
         */
        public int getAudioOpen() {
            return iAudioOpen;
        }

        /**
         * @param iAudioOpen the iAudioOpen to set
         */
        public void setAudioOpen(int iAudioOpen) {
            this.iAudioOpen = iAudioOpen;
        }

        /**
         * @return the iAudioSize
         */
        public int getAudioSize() {
            return iAudioSize;
        }

        /**
         * @param iAudioSize the iAudioSize to set
         */
        public void setAudioSize(int iAudioSize) {
            this.iAudioSize = iAudioSize;
        }

        /**
         * @return the sPlayBackMode
         */
        public String getPlayBackMode() {
            return sPlayBackMode;
        }

        /**
         * @param sPlayBackMode the sPlayBackMode to set
         */
        public void setPlayBackMode(String sPlayBackMode) {
            this.sPlayBackMode = sPlayBackMode;
        }

        /**
         * @return the sFileName
         */
        public String getPlayFileName() {
            return sPlayFileName;
        }

        /**
         * @param sFileName the sFileName to set
         */
        public void setPlayFileName(String sFileName) {
            this.sPlayFileName = sFileName;
        }

        /**
         * @return the struStartTime
         */
        public HCNetSDK.NET_DVR_TIME getStruStartTime() {
            return struStartTime;
        }

        /**
         * @param struStartTime the struStartTime to set
         */
        public void setStruStartTime(HCNetSDK.NET_DVR_TIME struStartTime) {
            this.struStartTime = struStartTime;
        }

        public void setStruStartTime(Date StartTime) {
            DateHandler dateHandler = DateHandler.getDateInstance(StartTime);
            struStartTime.dwYear = dateHandler.getYear();
            struStartTime.dwMonth = dateHandler.getMonth();
            struStartTime.dwDay = dateHandler.getDay();
            struStartTime.dwHour = dateHandler.getHour();
            struStartTime.dwMinute = dateHandler.getMinute();
            struStartTime.dwSecond = dateHandler.getSecond();
        }
        //格式如：2016-05-17 00:00:00
        public void setStruStartTime(String StartTime) {
            DateHandler dateHandler = DateHandler.getTimeStringInstance(StartTime);
            struStartTime.dwYear = dateHandler.getYear();
            struStartTime.dwMonth = dateHandler.getMonth();
            struStartTime.dwDay = dateHandler.getDay();
            struStartTime.dwHour = dateHandler.getHour();
            struStartTime.dwMinute = dateHandler.getMinute();
            struStartTime.dwSecond = dateHandler.getSecond();
        }
        /**
         * @return the struStopTime
         */
        public HCNetSDK.NET_DVR_TIME getStruStopTime() {
            return struStopTime;
        }

        /**
         * @param struStopTime the struStopTime to set
         */
        public void setStruStopTime(HCNetSDK.NET_DVR_TIME struStopTime) {
            this.struStopTime = struStopTime;
        }
        
        public void setStruStopTime(Date StopTime) {
            //this.strStopTime = struStopTime;
            DateHandler dateHandler = DateHandler.getDateInstance(StopTime);
            struStopTime.dwYear = dateHandler.getYear();
            struStopTime.dwMonth = dateHandler.getMonth();
            struStopTime.dwDay = dateHandler.getDay();
            struStopTime.dwHour = dateHandler.getHour();
            struStopTime.dwMinute = dateHandler.getMinute();
            struStopTime.dwSecond = dateHandler.getSecond();
            
        }
        public void setStruStopTime(String StopTime) {
            //this.strStopTime = struStopTime;
            DateHandler dateHandler = DateHandler.getTimeStringInstance(StopTime);
            struStopTime.dwYear = dateHandler.getYear();
            struStopTime.dwMonth = dateHandler.getMonth();
            struStopTime.dwDay = dateHandler.getDay();
            struStopTime.dwHour = dateHandler.getHour();
            struStopTime.dwMinute = dateHandler.getMinute();
            struStopTime.dwSecond = dateHandler.getSecond();
            
        }
        
        /**
            * @return the listRecordLostStartTime
        */
        public ArrayList<HCNetSDK.NET_DVR_TIME_EX> getListRecordLostStartTime() {
            return listRecordLostStartTime;
        }

        /**
         * @return the listRecordLostStopTime
         */
        public ArrayList<HCNetSDK.NET_DVR_TIME_EX> getListRecordLostStopTime() {
            return listRecordLostStopTime;
        }

        /**
         * @return the nFileTime
         */
        public IntByReference getFileTime() {
            return nFileTime;
        }

        /**
         * @param nFileTime the nFileTime to set
         */
        public void setFileTime(IntByReference nFileTime) {
            this.nFileTime = nFileTime;
        }

        /**
         * @return the m_nTotalFrames
         */
        public IntByReference getTotalFrames() {
            return nTotalFrames;
        }

        /**
         * @param m_nTotalFrames the m_nTotalFrames to set
         */
        public void setTotalFrames(IntByReference m_nTotalFrames) {
            this.nTotalFrames = m_nTotalFrames;
        }

        /**
         * @return the m_nTotalSecond
         */
        public int getTotalSecond() {
            return nTotalSecond;
        }

        /**
         * @param m_nTotalSecond the m_nTotalSecond to set
         */
        public void setTotalSecond(int m_nTotalSecond) {
            this.nTotalSecond = m_nTotalSecond;
        }

        /**
         * @return the m_nTotalMinute
         */
        public int getTotalMinute() {
            return nTotalMinute;
        }

        /**
         * @param m_nTotalMinute the m_nTotalMinute to set
         */
        public void setTotalMinute(int m_nTotalMinute) {
            this.nTotalMinute = m_nTotalMinute;
        }

        /**
         * @return the m_nTotalHour
         */
        public int getTotalHour() {
            return nTotalHour;
        }

        /**
         * @param m_nTotalHour the m_nTotalHour to set
         */
        public void setTotalHour(int m_nTotalHour) {
            this.nTotalHour = m_nTotalHour;
        }

        /**
         * @return the m_bGetMaxTime
         */
        public boolean isGetMaxTime() {
            return bGetMaxTime;
        }

        /**
         * @param m_bGetMaxTime the m_bGetMaxTime to set
         */
        public void setGetMaxTime(boolean m_bGetMaxTime) {
            this.bGetMaxTime = m_bGetMaxTime;
        }

        /**
         * @return the SaveFile
         */
        public boolean isIfSaveFile() {
            return bIfSaveFile;
        }

        /**
         * @param SaveFile the SaveFile to set
         */
        public void setIfSaveFile(boolean SaveFile) {
            this.bIfSaveFile = SaveFile;
        }

        /**
         * @return the iTotalBytes
         */
        public int getTotalBytes() {
            return iTotalBytes;
        }

        /**
         * @param iTotalBytes the iTotalBytes to set
         */
        public void setTotalBytes(int iTotalBytes) {
            this.iTotalBytes = iTotalBytes;
        }

        /**
         * @return the SaveFile
         */
        public String getSaveFileName() {
            return sSaveFileName;
        }

        /**
         * @param SaveFile the SaveFile to set
         */
        public void setSaveFileName(String SaveFile) {
            this.sSaveFileName = SaveFile;
        }
        
        /**
            * @return the MultiplesSpeed
        */
        public double getMultiplesSpeed() {
            return dPlaySpeed;
        }

        /**
        * @param MultiplesSpeed the MultiplesSpeed to set
        */
        public void setMultiplesSpeed(double MultiplesSpeed) {
            this.dPlaySpeed = MultiplesSpeed;
        }
        
        /**
        * @return the sSerialNoJoin
        */
        public String getSerialNoJoin() {
           return sSerialNoJoin;
        }

        /**
         * @param SerialNoJoin the sSerialNoJoin to set
         */
        private void setSerialNoJoin(String SerialNoJoin) {
            this.sSerialNoJoin = SerialNoJoin;
        }

        /**
         * @return the sEncodingDVRChannelNode
         */
        public String getEncodingDVRChannelNode() {
            return sEncodingDVRChannelNode;
        }

        /**
         * @param TreeNode the sEncodingDVRChannelNode to set
         */
        public void setEncodingDVRChannelNode(String TreeNode) {
            sWholeDVRChannelNode = TreeNode;
            //设置接入设备的序列号和设备资源节点
            String[] ReturnA = CommonParas.getArraysFromTreeNode(TreeNode);//数组：设备名、接入设备名、设备资源节点名
            this.sEncodingDVRChannelNode = ReturnA[2];

            if(!ReturnA[1].equals("")) setSerialNoJoin(CommonParas.getSerialNO(ReturnA[1], sFileName));//接入设备序列号
        }
        /**
         * @return the sWholeDVRChannelNode
         */
        public String getWholeDVRChannelNode() {
            return sWholeDVRChannelNode;
        }
        /**
         * @return the sRemarks
        */
        public String getRemarks() {
            return sRemarks;
        }

        /**
         * @param Remarks the sRemarks to set
         */
        public void setRemarks(String Remarks) {
            this.sRemarks = Remarks;
        }
        /**
         * @return the sRemarks2
        */
        public String getRemarks2() {
            return sRemarks2;
        }

        /**
         * @param Remarks2 the sRemarks to set
         */
        public void setRemarks2(String Remarks2) {
            this.sRemarks2 = Remarks2;
        }
        
        //private 
        public String getTextOfSpeed(){
            //public static final int NET_DVR_PLAYFAST = 5;//快放
            //public static final int NET_DVR_PLAYSLOW = 6;//慢放
            //public static final int NET_DVR_PLAYNORMAL = 7;//正常速度
            if (dPlaySpeed == 1) return "1x";
            else if (dPlaySpeed == 2) return "2x";
            else if (dPlaySpeed == 4) return "4x";
            else if (dPlaySpeed == 8) return "8x";
            else if (dPlaySpeed == 16) return "16x";
            else if (dPlaySpeed == 0.5) return "1/2x";
            else if (dPlaySpeed == 0.25) return "1/4x";
            else if (dPlaySpeed == 0.125) return "1/8x";
            else if (dPlaySpeed == 0.0625) return "1/16x";
            else return  "1x";
        }
        /**
            * 函数:      playPanel
            * 函数描述:  播放窗口按了播放“play/pause”键
        */ 
        public void playPanel(){

            if (playBackHandle.intValue() == -1) return;
            try{

                //如果为正向，而且正在回放，则只做暂停处理
                if (iPlayDirection== HCNetSDK.NET_DVR_PLAY_FORWARD && iPlayState == HCNetSDK.NET_DVR_PLAYSTART){
                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYPAUSE, 0, null)){
                        setPlayState(HCNetSDK.NET_DVR_PLAYPAUSE);
                    } 
                }else if (iPlayDirection== HCNetSDK.NET_DVR_PLAY_FORWARD && iPlayState == HCNetSDK.NET_DVR_PLAYPAUSE){//如果为正向，而且在暂停，则只做恢复播放处理
                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYRESTART, 0, null)){
                        setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
                    } 
                }else if (iPlayDirection== HCNetSDK.NET_DVR_PLAY_REVERSE && iPlayState == HCNetSDK.NET_DVR_PLAYSTART){//如果为倒向，而且正在回放，则只做倒放切换为正放处理
                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(playBackHandle, HCNetSDK.NET_DVR_PLAY_FORWARD, Pointer.NULL, 0, Pointer.NULL, null)){
                        setPlayDirection(HCNetSDK.NET_DVR_PLAY_FORWARD);
                        setPlaySpeed(1);
                    } 
                }else if (iPlayDirection== HCNetSDK.NET_DVR_PLAY_REVERSE && iPlayState == HCNetSDK.NET_DVR_PLAYPAUSE){//如果为倒向，而且正在暂停，则做2步处理：正放切换为倒放；恢复播放处理
                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(playBackHandle, HCNetSDK.NET_DVR_PLAY_FORWARD, Pointer.NULL, 0, Pointer.NULL, null)){
                        setPlayDirection(HCNetSDK.NET_DVR_PLAY_FORWARD);
                        setPlaySpeed(1);
                        if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYRESTART, 0, null)){
                            setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
                        } 
                    } 
                }

            }catch(Exception e){
                TxtLogger.append(this.sFileName, "playPanel()","某一个播放窗口按了播放“play/pause”键，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
        }
        /**
            * 函数:      reversePanel
            * 函数描述:  播放窗口按了倒放“reverse/pause”键
        */ 
        public void reversePanel(){

            if (playBackHandle.intValue() == -1) return;
            try{
                 //如果为倒向，而且正在回放，则只做暂停处理
                if (iPlayDirection== HCNetSDK.NET_DVR_PLAY_REVERSE && iPlayState == HCNetSDK.NET_DVR_PLAYSTART){
                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYPAUSE, 0, null)){
                        setPlayState(HCNetSDK.NET_DVR_PLAYPAUSE);
                    } 
                }else if (iPlayDirection== HCNetSDK.NET_DVR_PLAY_REVERSE && iPlayState == HCNetSDK.NET_DVR_PLAYPAUSE){//如果为倒向，而且在暂停，则只做恢复播放处理
                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYRESTART, 0, null)){
                        setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
                    } 
                }else if (iPlayDirection== HCNetSDK.NET_DVR_PLAY_FORWARD && iPlayState == HCNetSDK.NET_DVR_PLAYSTART){//如果为正向，而且正在回放，则只做正放切换为倒放处理
                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(playBackHandle, HCNetSDK.NET_DVR_PLAY_REVERSE, Pointer.NULL, 0, Pointer.NULL, null)){
                        setPlayDirection(HCNetSDK.NET_DVR_PLAY_REVERSE);
                        setPlaySpeed(1);
                    } 
                }else if (iPlayDirection== HCNetSDK.NET_DVR_PLAY_FORWARD && iPlayState == HCNetSDK.NET_DVR_PLAYPAUSE){//如果为正向，而且正在暂停，则做2步处理：正放切换为倒放；恢复播放处理
                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(playBackHandle, HCNetSDK.NET_DVR_PLAY_REVERSE, Pointer.NULL, 0, Pointer.NULL, null)){
                        setPlayDirection(HCNetSDK.NET_DVR_PLAY_REVERSE);
                        setPlaySpeed(1);
                        if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYRESTART, 0, null)){
                            setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
                        } 
                    } 
                }

            }catch(Exception e){
                TxtLogger.append(this.sFileName, "reversePanel()","播放窗口按了播放“reverse/pause”键，出现错误"
                                 + "\r\n                       Exception:" + e.toString());   
            }
        }
        
        
        /**
            * 函数:      fastPlayBackSpeed
            * 函数描述:  快速播放
            * @return     成功，返回播放的速度；失败返回0；如果已经到达最高速了，返回-1；出现其他错误，返回-2
        */ 
        public double  fastPlayBackSpeed(){
            if (dPlaySpeed == MaxSpeed) return -1;
            try{
                if(CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYFAST, 0, null)){
                    dPlaySpeed = dPlaySpeed * 2;
                    return dPlaySpeed;
                }else return 0;
            }catch(Exception e){
                TxtLogger.append(this.sFileName, "fastPlayBackSpeed()","快速播放的过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
            return -2;
        }
        /**
            * 函数:      slowPlayBackSpeed
            * 函数描述:  慢速播放
            * @return     成功，返回播放的速度；失败返回0；如果已经到达最高速了，返回-1；出现其他错误，返回-2
        */ 
        public double  slowPlayBackSpeed(){
            if (dPlaySpeed == MinSpeed) return -1;
            try{
                if(CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYSLOW, 0, null)){
                    dPlaySpeed = dPlaySpeed / 2;
                    return dPlaySpeed;
                }else return 0;
            }catch(Exception e){
                TxtLogger.append(this.sFileName, "slowPlayBackSpeed()","慢速播放的过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
            return -2;
        }
        
        /**
            * 函数:      normalPlayBackSpeed
            * 函数描述:  恢复正常回放速度
            * @return     成功，返回播放的速度；失败返回0；出现其他错误，返回-1
        */ 
        public double  normalPlayBackSpeed(){
            
            try{
                if (dPlaySpeed == 1) return 1;
                if(CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYNORMAL, 0, null)){
                    dPlaySpeed = 1;
                    return dPlaySpeed;
                }else return 0;
            }catch(Exception e){
                TxtLogger.append(this.sFileName, "slowPlayBackSpeed()","慢速播放的过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
            return -1;
        }
        
        /**
            * 函数:      normalPlayBackState
            * 函数描述:  恢复正常回放状态
            * @return     成功，1；失败返回0；出现其他错误，返回-1
        */ 
        public int  normalPlayBackState(){
            
            try{
                if (iPlayState  == HCNetSDK.NET_DVR_PLAYSTART) return 1;
                if (iPlayState  == HCNetSDK.NET_DVR_PLAYPAUSE){
                    if(CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYRESTART, 0, null)){
                        iPlayState = HCNetSDK.NET_DVR_PLAYSTART;
                        return 1;
                    }else return 0;
                }
                return 1;
            }catch(Exception e){
                TxtLogger.append(this.sFileName, "slowPlayBackSpeed()","慢速播放的过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
            return -1;
        }
        /**
            * 函数:      setSliderAudio
            * 函数描述:  设置某一个播放窗口的音量大小
            * @param jSliderAudio
        */ 
        public void setSliderAudio(JSlider jSliderAudio){

            if (playBackHandle.intValue() == -1) return;
            try{
                //调节音量，取值范围[0,0xffff] 十进制[0,65535]
                int VolumeSize = jSliderAudio.getValue()*65535/100;
                if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYAUDIOVOLUME, VolumeSize, null)){
                    setAudioSize(VolumeSize);
                }else{
                        //System.out.println(" Set volume Failed!");
                }
            }catch(Exception e){
                TxtLogger.append(this.sFileName, "setSliderAudio()","设置播放窗口的音量大小的过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }

        }
        /**
            * 函数:      setAudioIfMute
            * 函数描述:  设置某一个播放窗口是否关闭音量
        */
        public void setAudioIfMute(){

            if (playBackHandle.intValue() == -1) return;
            try{

                if (getAudioOpen() == HCNetSDK.NET_DVR_PLAYSTARTAUDIO){
                        if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYSTOPAUDIO, 0, null)){
                            setAudioOpen(HCNetSDK.NET_DVR_PLAYSTOPAUDIO);//设置为声音关闭状态
                        }
                }else{
                        if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYSTARTAUDIO, 0, null)){
                            setAudioOpen(HCNetSDK.NET_DVR_PLAYSTARTAUDIO);//设置为声音关闭状态
                        }
                }
            }catch(Exception e){
                TxtLogger.append(this.sFileName, "setAudioIfMute()","设置播放窗口是否关闭音量的过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
        }
        
        /**
            * 函数:      stopPlayBackOnePanel
            * 函数描述:  停止某一个通道的回放
            * @return int   停止回放成功返回1；停止预览失败返回0；出现错误返回-1；原先停止预览则返回-2
        */
        public  int stopPlayBackOnePanel(){
            try{

                if (playBackHandle.intValue() == -1) return -2;
                doBeforeStopPlayBack();
                if (playBackHandle.intValue() >= 0){

                    if (CommonParas.hCNetSDK.NET_DVR_StopPlayBack(playBackHandle))
                    {
                        doAfterStopPlayBack();
                        return 1;
                    }
                    else return 0;
                }
            }catch(Exception e){
                TxtLogger.append(sFileName, "stopPlayBackOnePanel()","某一个播放窗口按了播放“stop”键，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
            return -1;
        }
        /**
            * 函数:      doBeforeStopPlayBack
            * 函数描述:  在结束窗口回放之前需要进行的操作
        */ 
        public void doBeforeStopPlayBack(){
            /*在停止回放之前，要做以下操作：
                1、是否在录像，如果是，则调用停止录像函数
                2、是否开始球机联动，如果是，则停止球机联动
                3、是否打开声音
                4、是否在进行对讲
            */
            if (bIfSaveFile){
                if (!CommonParas.hCNetSDK.NET_DVR_StopPlayBackSave(playBackHandle))            {
                        System.out.println("Stop save failed!");
                }else{
                    setIfSaveFile(false);
                }
            }
        }

        /**
            * 函数:      doAfterStopPlayBack
            * 函数描述:  在结束窗口回放后需要进行的操作
        */ 
        public void doAfterStopPlayBack(){
            panelPlayBack.repaint();

            playBackHandle.setValue(-1) ;
            Playbacktimer.cancel();
            setPlayFileName("");
            setTotalBytes(0);
        }
        
        /**
          * 函数:      saveOnePanelRecord
          * 函数描述:  给某一个播放窗口存储录像
        */ 
        public void saveOnePanelRecord(){

            if (playBackHandle.intValue() == -1) return;
            try{
                //如果没有下载录像的权限，则返回
                if (!ifHaveDownload()) return;

                //如果不是正在存储文件状态
                if (!isIfSaveFile()){
                    //得到存储路径，自动命名文件名
                    String PathName = CommonParas.SysParas.getSysParasFileSaveDirVedio();
                    String SaveFileName = getInitialName(PathName);

                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackSaveData(playBackHandle, SaveFileName + ".mp4")){
                        setIfSaveFile(true);
                        setSaveFileName(SaveFileName);
                    }
                } else{//如果正在存储文件状态
                    boolean ifSaveSuccess = CommonParas.hCNetSDK.NET_DVR_StopPlayBackSave(playBackHandle);

                    if (ifSaveSuccess) {
                        File FileRecord = new File(getSaveFileName() + ".mp4");
                        File NewFileRecord = new File(getReName() + ".mp4");
                        //设置附加备注：存储录像文件
                        setRemarks2(NewFileRecord.toString());

                        if(!NewFileRecord.exists()){//若在该目录下已经有一个文件和新文件名相同，则不允许重命名   
                            //FileRecord.renameTo(NewFileRecord);
                            if (FileRecord.renameTo(NewFileRecord)) {
                                writePlayBackLog(sSaveRecord);//"保存录像"
                                CommonParas.showMessage(sSaveRecord_Succ + NewFileRecord, sFileName);//"成功保存录像："
                                //JOptionPane.showMessageDialog(null, "成功保存录像：" + NewFileRecord);
                            }
                        }   
                        //JOptionPane.showMessageDialog(this, "存储录像文件成功：" + channelPlayBack.getSaveFileName());
                    }else {
                        writePlayBackErrorLog(sSaveRecordFile_Fail);//"存储录像文件失败"
                        CommonParas.showErrorMessage( sSaveRecordFileColon_Fail + getSaveFileName(), getDeviceParaBean().getAnothername(), sFileName);//"存储录像文件失败："
                    }

                    setIfSaveFile(false);
                }
            }catch(Exception e){
                TxtLogger.append(this.sFileName, "saveOnePanelRecord()","给给某一个播放窗口存储录像的过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
        }
        
        /**
          * 函数:      setFastPlayOnePanel
          * 函数描述:  给某一个播放窗口抓图
        */ 
        public void captureOnePanel(){

            if (playBackHandle.intValue() == -1) return;
            try{
                //得到存储路径，自动命名文件名
                String PathName = CommonParas.SysParas.getSysParasFileSaveDirCapture();
                String sPicName;// = "e:/Picture/" + channelPlayBack.getChannelNO().intValue()+ System.currentTimeMillis() + ".bmp";

                if (CommonParas.hCNetSDK.NET_DVR_SetCapturePictureMode(HCNetSDK.CAPTURE_MODE.JPEG_MODE)){
                    sPicName = getInitialName(PathName) + ".jpg";
                }else{
                    sPicName = getInitialName(PathName) + ".bmp";
                    //写错误日志
                    CommonParas.SystemWriteErrorLog( sSetCaptureMode_Fail,  getDeviceParaBean().getAnothername(), sFileName);//"设置抓图模式为JPG模式时失败"
                }
                //设置附加备注：抓图文件
                setRemarks2(sCaptureFile  + sPicName);//"抓图文件："

                if (CommonParas.hCNetSDK.NET_DVR_PlayBackCaptureFile(playBackHandle, sPicName)){
                        writePlayBackLog(sPlaybackCapture );//"录像回放抓图"
                        CommonParas.showMessage(sCaptureFile  + sPicName, sFileName);//"抓图文件："
                }else{
                        writePlayBackErrorLog(sPlaybackCapture_Fail );//"回放抓图失败"
                        CommonParas.showErrorMessage(sPlaybackCapture_Fail , getDeviceParaBean().getAnothername(), sFileName);//"回放抓图失败"
                }
            }catch(Exception e){
                TxtLogger.append(this.sFileName, "captureOnePanel()","给某一个录像回放窗口抓图的过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
        }
        
//        /**利用函数NET_DVR_StartRemoteConfig、NET_DVR_GetNextRemoteConfig进行录像完整性检测，NVR不支持，错误代码：23，所以只能放弃
//            *函数:      checkRecordLostInfo
//            *函数描述:  检测在录像回放时间内的录像缺失信息，现在主要包括缺失录像的起始时间
//            * @return   1,则有缺失；0，无缺失，录像完整；-1表示检测失败
//        */
//        public int checkRecordLostInfo(){
//            try{
//                //如果是文件播放的话，则不需要进行检查，没有缺失
//                if (sPlayBackMode.equals("file")) return 1;
//                //判断是否已经经过检测
//                if(bIfRecordChecked){
//                    if (!listRecordLostStartTime.isEmpty() && !listRecordLostStopTime.isEmpty()) return 1;
//                    else return 0;
//                }
//                if (struStartTime == null || struStopTime == null) return -1;
//
//                HCNetSDK.NET_DVR_RECORD_CHECK_COND StruRecordCheckCond = new HCNetSDK.NET_DVR_RECORD_CHECK_COND();
//                StruRecordCheckCond.byCheckType = 1;//0- 录像是否完整，1- 录像是否完整&缺失录像的起止时间 
//                StruRecordCheckCond.dwSize = StruRecordCheckCond.size();
//
//                StruRecordCheckCond.struBeginTime.wYear = (short)struStartTime.dwYear;
//                StruRecordCheckCond.struBeginTime.byMonth = (byte)struStartTime.dwMonth;
//                StruRecordCheckCond.struBeginTime.byDay = (byte)struStartTime.dwDay;
//                StruRecordCheckCond.struBeginTime.byHour = (byte)struStartTime.dwHour;
//                StruRecordCheckCond.struBeginTime.byMinute = (byte)struStartTime.dwMinute;
//                StruRecordCheckCond.struBeginTime.bySecond = (byte)struStartTime.dwSecond;
//
//                StruRecordCheckCond.struEndTime.wYear = (short)struStopTime.dwYear;
//                StruRecordCheckCond.struEndTime.byMonth = (byte)struStopTime.dwMonth;
//                StruRecordCheckCond.struEndTime.byDay = (byte)struStopTime.dwDay;
//                StruRecordCheckCond.struEndTime.byHour = (byte)struStopTime.dwHour;
//                StruRecordCheckCond.struEndTime.byMinute = (byte)struStopTime.dwMinute;
//                StruRecordCheckCond.struEndTime.bySecond = (byte)struStopTime.dwSecond;
//                
//                StruRecordCheckCond.struStreamInfo.dwChannel = lChannel.intValue();
//                StruRecordCheckCond.struStreamInfo.dwSize = StruRecordCheckCond.struStreamInfo.size();
//
//                
//                StruRecordCheckCond.write();
//                Pointer pUser = null;
//                FRemoteConfigCallback RemoteConfigCallback = null;
//                Pointer lpRecordCheckCond = StruRecordCheckCond.getPointer();
//                //通道号无效，所以设为0
//                NativeLong LHandle = CommonParas.hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_RECORD_CHECK,  lpRecordCheckCond, StruRecordCheckCond.size(), RemoteConfigCallback, pUser);
//                StruRecordCheckCond.read();
//
//                if (LHandle.intValue() != -1){
//                    NativeLong LReturn = new NativeLong(-1);
//                    do{
//                        HCNetSDK.NET_DVR_RECORD_CHECK_RET StruRecordCheckRet = new HCNetSDK.NET_DVR_RECORD_CHECK_RET();
//
//                        StruRecordCheckRet.write();
//                        Pointer lpRecordCheckRet = StruRecordCheckRet.getPointer();
//                        LReturn = CommonParas.hCNetSDK.NET_DVR_GetNextRemoteConfig(LHandle,  lpRecordCheckRet, StruRecordCheckRet.size());
//                        StruRecordCheckRet.read();
//                        if (LReturn.intValue() == -1 || LReturn.intValue()==HCNetSDK.NET_SDK_GET_NEXT_STATUS_FAILED ) {
//                            writePlayBackErrorLog("录像完整性检测");
//                            listRecordLostStartTime.clear();
//                            listRecordLostStopTime.clear();
//                            //出现错误，关闭长连接配置接口所创建的句柄，释放资源。
//                            stopRemoteConfig(LHandle);
//                            return -1;
//                        }else if(LReturn.intValue() == HCNetSDK.NET_SDK_GET_NEXT_STATUS_SUCCESS) {//成功读取到数据，处理完本次数据后需要再次调用NET_DVR_GetNextRemoteConfig获取下一条数据
//                            //录像是否完整：0- 完整，1- 不完整 
//                            if (StruRecordCheckRet.byRecordNotComplete == 1){
//                                listRecordLostStartTime.add(StruRecordCheckRet.struBeginTime);
//                                listRecordLostStopTime.add(StruRecordCheckRet.struBeginTime);
//                            }
//
//                        }else if(LReturn.intValue() == HCNetSDK.NET_SDK_GET_NEXT_STATUS_FINISH){
//                            //检测完毕，关闭长连接
//                            stopRemoteConfig(LHandle);
//                            bIfRecordChecked = true;//表示检测成功了。
//                        }else{}
//
//                        //NET_SDK_GET_NEXT_STATUS_FINISH 1002 数据全部取完，可调用NET_DVR_StopRemoteConfig结束长连接 
//                        //NET_SDK_GET_NEXT_STATUS_FAILED  1003 出现异常，可调用NET_DVR_StopRemoteConfig结束长连接 
//                    }while(LReturn.intValue()!=HCNetSDK.NET_SDK_GET_NEXT_STATUS_FINISH || LReturn.intValue()!=HCNetSDK.NET_SDK_GET_NEXT_STATUS_FAILED );
//                    
//                    if (!listRecordLostStartTime.isEmpty() && !listRecordLostStopTime.isEmpty()) return 1;
//                    else return 0;
//
//                }else{
//                    writePlayBackErrorLog("录像完整性检测");
//                    return -1;
//                }
//            }catch(Exception e){
//                TxtLogger.append(sFileName, "checkRecordLostInfo()","检测在录像回放时间内的录像缺失信息时，出现错误"
//                                 + "\r\n                       Exception:" + e.toString());   
//            }
//            return -1;
//        }

        /**
            *函数:      addRecordLostTime
            *函数描述:  添加在录像回放时间内的录像缺失时间信息
            * @param StartTime  录像缺失开始时间
            * @param StopTime   录像缺失截止时间
        */
        public void addRecordLostTime(String StartTime, String StopTime){
            try{
                HCNetSDK.NET_DVR_TIME_EX RecordLostStartTime = new HCNetSDK.NET_DVR_TIME_EX();
                HCNetSDK.NET_DVR_TIME_EX RecordLostStopTime = new HCNetSDK.NET_DVR_TIME_EX();

                DateHandler dateHandler = DateHandler.getTimeStringInstance(StartTime);
                RecordLostStartTime.wYear = (short)dateHandler.getYear();
                RecordLostStartTime.byMonth = (byte)dateHandler.getMonth();
                RecordLostStartTime.byDay = (byte)dateHandler.getDay();
                RecordLostStartTime.byHour = (byte)dateHandler.getHour();
                RecordLostStartTime.byMinute = (byte)dateHandler.getMinute();
                RecordLostStartTime.bySecond = (byte)dateHandler.getSecond();
                listRecordLostStartTime.add(RecordLostStartTime);//缺失录像的起始时间列表

                DateHandler dateHandler2 = DateHandler.getTimeStringInstance(StopTime);
                RecordLostStopTime.wYear = (short)dateHandler2.getYear();
                RecordLostStopTime.byMonth = (byte)dateHandler2.getMonth();
                RecordLostStopTime.byDay = (byte)dateHandler2.getDay();
                RecordLostStopTime.byHour = (byte)dateHandler2.getHour();
                RecordLostStopTime.byMinute = (byte)dateHandler2.getMinute();
                RecordLostStopTime.bySecond = (byte)dateHandler2.getSecond();
                listRecordLostStopTime.add(RecordLostStopTime);//缺失录像的终止时间列表
            }catch(Exception e){
                TxtLogger.append(sFileName, "addRecordLostTime()","添加在录像回放时间内的录像缺失时间信息时，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
        }
        /**
            *函数:      addRecordLostTime
            *函数描述:  添加在录像回放时间内的录像缺失时间信息
            * @param StruStartTime  录像缺失开始时间
            * @param StruStopTime   录像缺失截止时间
        */
        public void addRecordLostTime(HCNetSDK.NET_DVR_TIME StruStartTime, HCNetSDK.NET_DVR_TIME StruStopTime){
            try{
                HCNetSDK.NET_DVR_TIME_EX RecordLostStartTime = new HCNetSDK.NET_DVR_TIME_EX();
                HCNetSDK.NET_DVR_TIME_EX RecordLostStopTime = new HCNetSDK.NET_DVR_TIME_EX();

                RecordLostStartTime.wYear = (short)StruStartTime.dwYear;
                RecordLostStartTime.byMonth = (byte)StruStartTime.dwMonth;
                RecordLostStartTime.byDay = (byte)StruStartTime.dwDay;
                RecordLostStartTime.byHour = (byte)StruStartTime.dwHour;
                RecordLostStartTime.byMinute = (byte)StruStartTime.dwMinute;
                RecordLostStartTime.bySecond = (byte)StruStartTime.dwSecond;
                listRecordLostStartTime.add(RecordLostStartTime);//缺失录像的起始时间列表

                RecordLostStopTime.wYear = (short)StruStopTime.dwYear;
                RecordLostStopTime.byMonth = (byte)StruStopTime.dwMonth;
                RecordLostStopTime.byDay = (byte)StruStopTime.dwDay;
                RecordLostStopTime.byHour = (byte)StruStopTime.dwHour;
                RecordLostStopTime.byMinute = (byte)StruStopTime.dwMinute;
                RecordLostStopTime.bySecond = (byte)StruStopTime.dwSecond;
                listRecordLostStopTime.add(RecordLostStopTime);//缺失录像的终止时间列表
            }catch(Exception e){
                TxtLogger.append(sFileName, "addRecordLostTime()","添加在录像回放时间内的录像缺失时间信息时，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }
        }
//        /**
//            *函数:      stopRemoteConfig
//            *函数描述:  关闭长连接配置接口所创建的句柄，释放资源
//        */
//        private void stopRemoteConfig(NativeLong LHandle){
//            //出现错误，关闭长连接配置接口所创建的句柄，释放资源。
//            boolean bClose = CommonParas.hCNetSDK.NET_DVR_StopRemoteConfig(LHandle);
//            if (!bClose) {
//                writePlayBackErrorLog("关闭长连接配置接口所创建的句柄，释放资源");
//            }
//        }
        
    /**
        *函数:      playBackByTime
        *函数描述:  根据时间间隔段播放回放视频
    */
    public void playBackByTime(){

        try{
            if (panelPlayBack == null) return;
            if (iIndexOflistDevice < 0) return;
            if (lChannel.intValue() < 0) return;
            //NativeLong playBackHandle = getplayBackHandle();
    //         //如果已经在回放
    //         if(playBackHandle.intValue() != -1)
    //        {
    //            CommonParas.hCNetSDK.NET_DVR_StopPlayBack(channelPlayBack.getplayBackHandle());
    //            channelPlayBack.intialParas();//各参数恢复初始值
    ////                    m_lplayBackHandle.setValue(-1);
    //        }

            if (playBackHandle.intValue() == -1){

    //            m_iChanShowNum = Integer.parseInt(jTextFieldChanNum.getText());

                //获取窗口句柄
                W32API.HWND hwnd = new W32API.HWND();

                hwnd.setPointer(Native.getComponentPointer(panelPlayBack));//获取窗口的指针
                NativeLong UserID = CommonParas.getUserID(iIndexOflistDevice, sFileName);

                playBackHandle = CommonParas.hCNetSDK.NET_DVR_PlayBackByTime(UserID, lChannel, struStartTime, struStopTime, hwnd);

                if (playBackHandle.intValue() == -1)
                {
                    System.out.println("playBackByTime failed!");//JOptionPane.showMessageDialog(null, "按时间回放失败");
                    return;
                }
                else{
                    //还要调用该接口才能开始回放
                    CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
                    setPlayBackHandle(playBackHandle);
                    //channelPlayBack.setChannelNO(new NativeLong(iChannel));//通道号
                    setPlayBackMode("time");
                    setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
                    //jPanelPlayWindow.jButtonPlay.setText("Pause");

                    System.out.println("Start playBackByTime");
                }

                if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYSTARTAUDIO, 0, null)){

                    setAudioOpen(HCNetSDK.NET_DVR_PLAYSTARTAUDIO);////声音打开与否：打开9/关闭10
                    CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYAUDIOVOLUME, (0xffff) / 2, null);
                    System.out.println("Start volice");
                    //jPanelPlayWindow.jSliderAudio.setValue(50);
                    //jPanelPlayWindow.jButtonAudio.setText("Audio");

                } else{

                    setAudioOpen(HCNetSDK.NET_DVR_PLAYSTOPAUDIO);////声音打开与否：打开9/关闭10
                    //jPanelPlayWindow.jButtonAudio.setText("Mute");

                }

                //开始计时器
                //现在计时器都放在类内部，即每一个回放窗口都有一个计时器
                setPlaybacktimer(new Timer());//新建定时器
                getPlaybacktimer().schedule(new PlaybackTask(), 0, 1000);//0秒后开始响应函数
            }
        }catch(Exception e){
            TxtLogger.append(sFileName, "playBackByTime()","根据时间间隔段播放回放视频时，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
    
    

    /**
        * 函数:      writePlayBackLog
        * 函数描述:  设备预览窗口的写日志函数
        * @param Description
    */
    public void writePlayBackLog(String Description){
        //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、备注、调用的文件名
        CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, Description, getDeviceParaBean().getSerialNO(), "", getEncodingDVRChannelNode(), 
                                getSerialNoJoin(),"",CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE, sRemarks2.equals("")?sRemarks:sRemarks+sSeparator+sRemarks2, sFileName);//sSeparator"；"
        setRemarks2("");//将附加备注恢复为""
    }

    /**
        * 函数:      writePlayBackErrorLog
        * 函数描述:  设备录像回放窗口的写错误日志函数
        * @param Description
    */
    public void writePlayBackErrorLog(String Description){
        //写错误日志
        //操作时间、设备别名、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
        CommonParas.SystemWriteErrorLog("", getDeviceParaBean().getAnothername(), Description, getDeviceParaBean().getSerialNO(), "",getEncodingDVRChannelNode(), 
                                    getSerialNoJoin(),"",CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE,sFileName);
        setRemarks2("");//将附加备注恢复为""
    }

    /**
          * 函数:      getInitialName
          * 函数描述:  给出刚开始存储录像的初始文件名（不带后缀）
          *             命名规则：设备名_通道号_开始时间_终止时间.mp4/jpg
     * @param PathName
     * @return 
    */ 
    public String getInitialName(String PathName){
        
        //if (playBackHandle.intValue() == -1) return "";
        int Channel = lChannel.intValue();
        String AnotherName = mDeviceParaBean.getAnothername();
        String StartTime = "";
        
        HCNetSDK.NET_DVR_TIME StruPlayBackTime = new HCNetSDK.NET_DVR_TIME();
        if (CommonParas.hCNetSDK.NET_DVR_GetPlayBackOsdTime(playBackHandle, StruPlayBackTime)){
            StartTime = StruPlayBackTime.toStringTitle();
        }else StartTime = DateUtil.convertDateToString("yyyyMMddHHmmss", new Date());
        
        String FileName = PathName + "\\PlayBack_" + AnotherName + "_" + Channel +  "_" + StartTime;
        if (Channel > HCNetSDK.MAX_ANALOG_CHANNUM) {
            int Channel2 = Channel - HCNetSDK.MAX_ANALOG_CHANNUM;
            int IndexOflistDevice = CommonParas.getIndexOfDeviceList(AnotherName, FileName);
            DeviceParaBean DeviceParaBeanJoin =  CommonParas.getDeviceParaBeanJoin(IndexOflistDevice, Channel, sFileName);
            if (DeviceParaBeanJoin != null) FileName = PathName + "\\PlayBack_" + AnotherName + "_"+ DeviceParaBeanJoin.getAnothername()
                                                    + "_" + Channel2 +  "_" + StartTime;
        }
        
        return FileName;
    }
    /**
          * 函数:      getReName
          * 函数描述:  给出存储录像的最终文件名（不带后缀）、或者抓图文件名（不带后缀）
     * @return 
    */ 
    public String getReName(){
        //if (playBackHandle.intValue() == -1) return "";
        String StartTime = "";
        
        HCNetSDK.NET_DVR_TIME StruPlayBackTime = new HCNetSDK.NET_DVR_TIME();
        if (CommonParas.hCNetSDK.NET_DVR_GetPlayBackOsdTime(playBackHandle, StruPlayBackTime)){
            StartTime = StruPlayBackTime.toStringTitle();
        }else StartTime = DateUtil.convertDateToString("yyyyMMddHHmmss", new Date());

        String FileName = this.sSaveFileName + "_" + StartTime;
        return FileName;
    }
    
    /**
        * 函数:      getRecordDownloadName
        * 函数描述:  给出刚开始存储录像的初始文件名（不带后缀）
        *             命名规则：PlayBack_设备名_接入设备名_通道号_开始时间_终止时间.mp4/jpg。如：PlayBack_录像机_鱼眼_4_20160726104122_20160726104124
        * @param PathName   存储路径
        * @param StartTime  开始时间
        * @param StopTime   结束时间
        * @return 录像的文件名（带路径，不带后缀）
    */ 
    public String getRecordDownloadName(String PathName,String StartTime,String StopTime){
        int iChannel = lChannel.intValue();
        String FileName = PathName + "\\PlayBack_" + mDeviceParaBean.getAnothername() + "_" + iChannel +  "_" + StartTime +  "_" + StopTime;
        if (iChannel > HCNetSDK.MAX_ANALOG_CHANNUM) {
            int Channel = iChannel - HCNetSDK.MAX_ANALOG_CHANNUM;
            DeviceParaBean DeviceParaBeanJoin =  CommonParas.getDeviceParaBeanJoin(iIndexOflistDevice, iChannel, sFileName);
            if (DeviceParaBeanJoin != null) FileName = PathName + "\\PlayBack_" + mDeviceParaBean.getAnothername() + "_"+ DeviceParaBeanJoin.getAnothername()
                                                    + "_" + Channel +  "_" +  StartTime +  "_" + StopTime;
        }
        
        return FileName;
    }

    /**
        * 函数:      ifHaveDownload
        * 函数描述:  判断是否拥有下载录像的权限
    */
    private boolean ifHaveDownload(){
        try{
            if (sWholeDVRChannelNode.equals("")) return false;
            
            //完整的编码通道节点名称，比如：录像机_枪机_IP监控点_01
            String[] Splits = sWholeDVRChannelNode.split("_");//Splits[1]为接入设备的别名
            
            if (sWholeDVRChannelNode.lastIndexOf("IP") > -1){
                //判断当前是否拥有该接入设备录像回放的权限
                return CommonParas.showNoAuthorityMessage(CommonParas.AuthorityItems.AUTHORITY_PLAYBACK_DOWNLOAD,  Splits[1], sFileName);
            }else {
                //判断当前是否拥有该设备录像回放的权限
                return CommonParas.showNoAuthorityMessage(CommonParas.AuthorityItems.AUTHORITY_PLAYBACK_DOWNLOAD,  Splits[0], sFileName);
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "delNoAuthorityDVR()","系统在删除权限之外的设备节点过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
 
    }
    /**
        * 函数:      synOtherObject
        * 函数描述:  和别的回放对象同步回放
        * @param OtherChannelPlayBack
    */
    public HCNetSDK.NET_DVR_TIME synOtherObject(ChannelPlayBack OtherChannelPlayBack){
        if (OtherChannelPlayBack == null || OtherChannelPlayBack.getPlayBackHandle().intValue() < 0)  return null;
        if (this.playBackHandle.intValue() == -1) return null;
        /*同步包括回放状态、方向、速度、回放时间等因素的同步，
            但是ivms4200里面，同步状态不允许倒放，同步暂停时，先进行恢复回放，再进行同步
            所以回放状态和方向不被考虑。
        */
        try{
            //速度
            double PlaySpeed = OtherChannelPlayBack.getPlaySpeed();
            if (dPlaySpeed == 1) {}
            else if (dPlaySpeed == 2) {fastPlayBackSpeed();}
            else if (dPlaySpeed == 4) {fastPlayBackSpeed();fastPlayBackSpeed();}
            else if (dPlaySpeed == 8) {fastPlayBackSpeed();fastPlayBackSpeed();fastPlayBackSpeed();}
            else if (dPlaySpeed == 16) { fastPlayBackSpeed();  fastPlayBackSpeed();  fastPlayBackSpeed(); fastPlayBackSpeed(); }
            else if (dPlaySpeed == 0.5) { slowPlayBackSpeed();}
            else if (dPlaySpeed == 0.25) { slowPlayBackSpeed();slowPlayBackSpeed();}
            else if (dPlaySpeed == 0.125) { slowPlayBackSpeed();slowPlayBackSpeed();slowPlayBackSpeed();}
            else if (dPlaySpeed == 0.0625) { slowPlayBackSpeed();slowPlayBackSpeed();slowPlayBackSpeed();slowPlayBackSpeed();}


            //回放时间定位
            HCNetSDK.NET_DVR_TIME StruPlayBackTime = new HCNetSDK.NET_DVR_TIME();//存储当前回放时间
            if (CommonParas.hCNetSDK.NET_DVR_GetPlayBackOsdTime(OtherChannelPlayBack.getPlayBackHandle(), StruPlayBackTime)){
                setPlayTime( StruPlayBackTime);
                return StruPlayBackTime;
            }
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "synOtherObject()","在和别的回放对象同步回放过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return null;
    }
    
    /**
        * 函数:      synOtherObject
        * 函数描述:  和别的回放对象同步回放
        * @param OtherChannelPlayBack
    */
    public HCNetSDK.NET_DVR_TIME synOtherObjectNormalSpeed(ChannelPlayBack OtherChannelPlayBack){
        if (OtherChannelPlayBack == null || OtherChannelPlayBack.getPlayBackHandle().intValue() < 0)  return null;
        if (this.playBackHandle.intValue() == -1) return null;
        /*同步包括回放状态、方向、速度、回放时间等因素的同步，
            但是ivms4200里面，同步状态不允许倒放，同步暂停时，先进行恢复回放，再进行同步
            所以回放状态和方向不被考虑。
        */
        try{
            //速度
            normalPlayBackSpeed();

            //回放时间定位
            HCNetSDK.NET_DVR_TIME StruPlayBackTime = new HCNetSDK.NET_DVR_TIME();//存储当前回放时间
            if (CommonParas.hCNetSDK.NET_DVR_GetPlayBackOsdTime(OtherChannelPlayBack.getPlayBackHandle(), StruPlayBackTime)){
                setPlayTime( StruPlayBackTime);
                return StruPlayBackTime;
            }
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "synOtherObject()","在和别的回放对象同步回放过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return null;
    }
 
    /**
        * 函数:      setPlayTime
        * 函数描述:  将录像回放进行按绝对时间定位
     * @param StruPlayBackTime
    */
    public void setPlayTime(HCNetSDK.NET_DVR_TIME StruPlayBackTime){

        if (this.playBackHandle.intValue() > -1){
            
//            if (channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYPAUSE){
//                JPanelPlayWindow PlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
//                PlayWindow.setPlayOnePanel();
//            }
            
            IntByReference lpOutLen = new IntByReference(0);

            StruPlayBackTime.write();
            Pointer lpAbsoluteTime = StruPlayBackTime.getPointer();
            Pointer lpOut = null;

            boolean bSetTime = CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(playBackHandle, HCNetSDK.NET_DVR_PLAYSETTIME, lpAbsoluteTime, StruPlayBackTime.size(), Pointer.NULL, lpOutLen);
            StruPlayBackTime.read();
            if (!bSetTime){
                writePlayBackErrorLog(sPlackbackAbsolute_Fail );//"回放录像按绝对时间定位失败"
                CommonParas.showErrorMessage(sPlackbackAbsolute_Fail , getDeviceParaBean().getAnothername(), sFileName);//"回放录像按绝对时间定位失败"
            }
        }
    }

    /*************************************************
    类:      PlaybackTask
    类描述:  回放定时器响应函数
     *************************************************/
    class PlaybackTask extends java.util.TimerTask
    {
        
        //定时器函数 
        @Override
        public void run()
        {
            try{
            	IntByReference nCurrentTime = new IntByReference(0);
                IntByReference nCurrentFrame = new IntByReference(0);
                IntByReference nPos = new IntByReference(0);
                int nHour, nMinute, nSecond;
                
                IntByReference FileTime = new IntByReference(0);//文件总时间
                IntByReference TotalFrames = new IntByReference(0);//文件总帧数

//                int TotalSecond = channelPlayBack.getTotalSecond();//总秒数
//                int TotalMinute = channelPlayBack.getTotalMinute();//总分钟数
//                int TotalHour = channelPlayBack.getTotalHour();//总小时
                
//                if (!m_bGetMaxTime)
            	if (!bGetMaxTime)
		{
			CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_GETTOTALTIME, 0, FileTime);
			if (FileTime.getValue() == 0)
			{
				return;
			}
                        setFileTime(FileTime);
			if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_GETTOTALFRAMES, 0, TotalFrames))
			{
				if (TotalFrames.getValue() == 0)
				{
					return;
				}
                                setTotalFrames(TotalFrames);
			}
			else
			{
				System.out.println("Failed to get the total number of frames");//"获取总帧数失败"
			}

                        setTotalHour(FileTime.getValue()/3600);
                        setTotalMinute((FileTime.getValue()%3600)/60);
                        setTotalSecond(FileTime.getValue()%60);
                        setGetMaxTime(true);

		}

		CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYGETTIME, 0, nCurrentTime);
		if (nCurrentTime.getValue() >= FileTime.getValue())
		{
			nCurrentTime.setValue(FileTime.getValue());
		}
		nHour = (nCurrentTime.getValue()/3600)%24;
		nMinute =(nCurrentTime.getValue()%3600)/60;
		nSecond = nCurrentTime.getValue()%60;
		CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYGETFRAME, 0, nCurrentFrame);
		if (nCurrentFrame.getValue() > TotalFrames.getValue())
		{
			nCurrentFrame.setValue(TotalFrames.getValue());
		}

                 String sPlayTime;//播放时间
                 sPlayTime = String.format("%02d:%02d:%02d/%02d:%02d:%02d ",nHour,nMinute,nSecond,nTotalHour,nTotalMinute,nTotalSecond);
                 //listJPanelPlayWindow.get(IndexOfCurrentJPanel).setPlayTime(sPlayTime);
//                jTextFieldPlayTime.setText(sPlayTime);

		CommonParas.hCNetSDK.NET_DVR_PlayBackControl(playBackHandle, HCNetSDK.NET_DVR_PLAYGETPOS, 0, nPos);
		if (nPos.getValue() > 100)//200 indicates network exception
		{
			stopPlayBackOnePanel();
                        JOptionPane.showMessageDialog(null, sPlackbackExpectionMsg );//"由于网络原因或DVR忙,回放异常终止!"
		}
		else
		{
//			jSliderPlayback.setValue(nPos.getValue());
			if (nPos.getValue() == 100)
			{
				stopPlayBackOnePanel();
			}
		}
            }catch(Exception e){
                TxtLogger.append(sFileName, "PlaybackTask()","回放定时器响应函数，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
            }
        }
    }
    
    
    class FRemoteConfigCallback implements HCNetSDK.FRemoteConfigCallback{
        public void invoke(int dwType, Pointer lpBuffer,int dwBufLen,Pointer pUserData){}
    }
    
    /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    public static void modifyLocales(){
        
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作
        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        sSaveRecord                 = Locales.getString("ClassStrings", "ChannelPlayBack.sSaveRecord");  //音量
        sSaveRecord_Succ            = Locales.getString("ClassStrings", "ChannelPlayBack.sSaveRecord_Succ");  //成功保存录像：
        sSaveRecordFile_Fail        = Locales.getString("ClassStrings", "ChannelPlayBack.sSaveRecordFile_Fail");  //存储录像文件失败
        sSaveRecordFileColon_Fail   = Locales.getString("ClassStrings", "ChannelPlayBack.sSaveRecordFileColon_Fail");  //存储录像文件失败：
        sPlackbackExpectionMsg      = Locales.getString("ClassStrings", "ChannelPlayBack.sPlackbackExpectionMsg");  //由于网络原因或DVR忙,回放异常终止！
        sPlackbackAbsolute_Fail     = Locales.getString("ClassStrings", "ChannelPlayBack.sPlackbackAbsolute_Fail");  //回放录像按绝对时间定位失败
        sSetCaptureMode_Fail        = Locales.getString("ClassStrings", "ChannelPlayBack.sSetCaptureMode_Fail");  //设置抓图模式为JPG模式时失败
        sCaptureFile                = Locales.getString("ClassStrings", "ChannelPlayBack.sCaptureFile");  //抓图文件： 
        sPlaybackCapture            = Locales.getString("ClassStrings", "ChannelPlayBack.sPlaybackCapture");  //录像回放抓图
        sPlaybackCapture_Fail       = Locales.getString("ClassStrings", "ChannelPlayBack.sPlaybackCapture_Fail");  //回放抓图失败
        sSeparator                  = Locales.getString("ClassStrings", "ChannelPlayBack.sSeparator");  //"；";//分隔符

    }
    
    private static String sSaveRecord = "保存录像";
    private static String sSaveRecord_Succ="成功保存录像：";
    private static String sSaveRecordFile_Fail="存储录像文件失败";
    private static String sSaveRecordFileColon_Fail="存储录像文件失败：";

    private static String sPlackbackExpectionMsg = "由于网络原因或DVR忙,回放异常终止！";
    private static String sPlackbackAbsolute_Fail = "回放录像按绝对时间定位失败";//Failed to replay in absolute positioning
    private static String sSetCaptureMode_Fail = "设置抓图模式为JPG模式时失败";//Failed while setting the capture mode for JPG mode

    private static String sCaptureFile ="抓图文件：" ;
    private static String sPlaybackCapture = "录像回放抓图";
    private static String sPlaybackCapture_Fail = "回放抓图失败";
    private static String sSeparator = "；";//分隔符
    
}
