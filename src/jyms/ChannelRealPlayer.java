/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import static com.sun.jna.examples.win32.User32.IMAGE_BITMAP;
import static com.sun.jna.examples.win32.User32.LR_CREATEDIBSECTION;
import static com.sun.jna.examples.win32.User32.LR_LOADFROMFILE;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import java.awt.Panel;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import jyms.data.DeviceParaBean;
import jyms.data.TxtLogger;
import jyms.tools.DateUtil;


/**
 * 通道预览信息类
 * @author John
 */

public class ChannelRealPlayer implements Cloneable {

    private final String sFileName = this.getClass().getName() + ".java";
    private NativeLong lChannel = new NativeLong(-1);//通道号，包括IP通道，>32
    private String sGroupName = "";//编码通道节点分组名
    private String sSerialNoJoin = "";//接入设备的序列号
    private String sEncodingDVRChannelNode = "";//编码通道节点名称
    private Panel panelRealplay = null;//预览的控件Panel
    private boolean bRealPlay = false;//是否在预览.
    //索引：设备登录所产生的用户ID列表listDeviceLoginUserID，包括设备列表listDeviceparaBean索引，该设备登录所产生的用户ID、设备型号
    //和mDeviceParaBean同时赋值，用处不同。主要用于取得UserID
    private int iIndexOflistDevice = -1;
    private DeviceParaBean mDeviceParaBean = null;//和iIndexOflistDevice同时赋值，用处不同。
    
    private NativeLong lUserID = new NativeLong(-1);//用户登录句柄
    private NativeLong lPreviewHandle = new NativeLong(-1);/*预览句柄*/
    private NativeLongByReference lPort = new NativeLongByReference(new NativeLong(-1));//回调预览时播放库端口指针
    
    private boolean ifRecorded = false;//    1、是否开始录像
    private String sFileNameRecord = "";// 录像或者抓图的文件名（不带后缀）
    private boolean ifPTZCtrl = false;//2、是否启用窗口云台控制PTZCtrl

    //3、是否启用电子放大
    private boolean ifTalkBack = false;//4、是否开始对讲
    private boolean ifOpenVoice = false;//5、是否打开声音
    private boolean ifFishBallTrack = false;//6、是否开始球机联动
    
    //视频参数
    private int iBrightness = 6;    //亮度
    private int iContrast = 6;      //对比度
    private int iSaturation = 6;    //饱和度
    private int iHue = 6;           //色度
    private int iVolume = 50;       //语音对讲客户端的音量，百分比
    private NativeLong lVoiceComHandle = new NativeLong(-1);/*语音对讲的句柄*/

    
    private FDrawFunSetPreviewStateIcon SetPreviewStateIconCallBack;//设置预览状态图标，比如录像、对讲、声音控制等
    private final int iResolution =16;//分辨率是16*16
    private final String sImageRecord;//正在录像显示图标

    //以下参数，现在用不到，以后可能会删除
    private NativeLong lAlarmHandle = new NativeLong(-1);//报警布防句柄
    private NativeLong lListenHandle = new NativeLong(-1);//报警监听句柄
    
    public ChannelRealPlayer(){
        this.sImageRecord = CommonParas.SysPath + "image\\record.bmp";
        this.SetPreviewStateIconCallBack = new FDrawFunSetPreviewStateIcon();
    }
    public ChannelRealPlayer(DeviceParaBean deviceParaBean){
        this();
        this.mDeviceParaBean = deviceParaBean;
    }
    /**
        * 函数:      intialParas
        * 函数描述:  初始化参数
    */
    public void intialParas(){

        lChannel = new NativeLong(-1);//通道号
        sEncodingDVRChannelNode = "";//编码通道节点名称
        sGroupName = "";//编码通道节点分组名
        sSerialNoJoin = "";//接入设备的序列号
        panelRealplay = null;//预览的控件Panel
        bRealPlay = false;//是否在预览.
        
        iIndexOflistDevice = -1;//索引：设备登录所产生的用户ID列表listDeviceLoginUserID，包括设备列表listDeviceparaBean索引，该设备登录所产生的用户ID、设备型号
        mDeviceParaBean = null;
        
        lUserID = new NativeLong(-1);//用户句柄
        lPreviewHandle = new NativeLong(-1);/*预览句柄*/
        lPort = new NativeLongByReference(new NativeLong(-1));//回调预览时播放库端口指针
        
        ifRecorded = false;//    1、是否开始录像
        sFileNameRecord = "";// 录像或者抓图的文件名（不带后缀）
        ifPTZCtrl = false;//2、是否启用窗口云台控制PTZCtrl

    //    //3、是否启用电子放大
        ifTalkBack = false;//4、是否开始对讲
        ifOpenVoice = false;//5、是否打开声音
        ifFishBallTrack = false;//6、是否开始球机联动
  
        iBrightness = 6;    //亮度
        iContrast = 6;      //对比度
        iSaturation = 6;    //饱和度
        iHue = 6;           //色度
        iVolume = 50;       //语音对讲客户端的音量，百分比
        lVoiceComHandle = new NativeLong(-1);/*语音对讲的句柄*/
    
        lAlarmHandle = new NativeLong(-1);//报警布防句柄
        lListenHandle = new NativeLong(-1);//报警监听句柄
    }
    
    /**
        * 函数:      setBasicParas
        * 函数描述:  设置基本设备参数
        * @param ParaBean              DeviceParaBean
        * @param IndexOflistDevice           iIndexOflistDevice
        * @param UserID
    */
    public void setBasicParas(DeviceParaBean  ParaBean, int IndexOflistDevice, NativeLong UserID){
        setDeviceparaBean(ParaBean);
        setIndexOflistDevice(IndexOflistDevice);
        setUserID(UserID);
    }
    /**
     * @return the panelRealplay
     */
    public Panel getPanelRealplay() {
        return panelRealplay;
    }

    /**
     * @param PanelRealplay the panelRealplay to set
     */
    public void setPanelRealplay(Panel PanelRealplay) {
        this.panelRealplay = PanelRealplay;
    }

    /**
     * @return the bRealPlay
     */
    public boolean isbRealPlay() {
        return bRealPlay;
    }

    /**
     * @param RealPlay the bRealPlay to set
     */
    public void setRealPlay(boolean RealPlay) {
        this.bRealPlay = RealPlay;
    }

    /**
     * @return the deviceparaBean
     */
    public DeviceParaBean getDeviceparaBean() {
        return mDeviceParaBean;
    }

    /**
     * @param DeviceParaBean the deviceparaBean to set
     */
    public void setDeviceparaBean(DeviceParaBean  DeviceParaBean) {
        this.mDeviceParaBean = DeviceParaBean;
    }

    /**
     * @return the lUserID
     */
    public NativeLong getUserID() {
        return lUserID;
    }

    /**
     * @param UserID the lUserID to set
     */
    public void setUserID(NativeLong UserID) {
        this.lUserID = UserID;
    }

    /**
     * @return the lPreviewHandle
     */
    public NativeLong getPreviewHandle() {
        return lPreviewHandle;
    }

    /**
     * @param PreviewHandle the lPreviewHandle to set
     */
    public void setPreviewHandle(NativeLong PreviewHandle) {
        this.lPreviewHandle = PreviewHandle;
    }

    /**
     * @return the m_lPort
     */
    public NativeLongByReference getPort() {
        return lPort;
    }

    /**
     * @param Port the m_lPort to set
     */
    public void setPort(NativeLongByReference Port) {
        this.lPort = Port;
    }

    /**
     * @return the lAlarmHandle
     */
    public NativeLong getAlarmHandle() {
        return lAlarmHandle;
    }

    /**
     * @param AlarmHandle the lAlarmHandle to set
     */
    public void setAlarmHandle(NativeLong AlarmHandle) {
        this.lAlarmHandle = AlarmHandle;
    }

    /**
     * @return the lListenHandle
     */
    public NativeLong getListenHandle() {
        return lListenHandle;
    }

    /**
     * @param ListenHandle the lListenHandle to set
     */
    public void setListenHandle(NativeLong ListenHandle) {
        this.lListenHandle = ListenHandle;
    }

    /**
     * @return the iIndexOflistDevice
     */
    public int getIndexOflistDevice() {
        return iIndexOflistDevice;
    }

    /**
     * @param IndexOflistDevice the iIndexOfDevicePreviewList to set
     */
    public void setIndexOflistDevice(int IndexOflistDevice) {
        this.iIndexOflistDevice = IndexOflistDevice;
    }

    /**
     * @return the iChannel
     */
    public NativeLong getChannel() {
        return lChannel;
    }

    /**
     * @param Channel the iChannel to set
     */
    public void setChannel(NativeLong Channel) {
        this.lChannel = Channel;
    }

//    /**
//     * @return the SelectedNode
//     */
//    public DefaultMutableTreeNode getSelectedNode() {
//        return dmtnSelectedNode;
//    }
//
//    /**
//     * @param SelectedNode the SelectedNode to set
//     */
//    public void setSelectedNode(DefaultMutableTreeNode SelectedNode) {
//        this.dmtnSelectedNode = SelectedNode;
//    }
    
   

    /**
     * @return the ifRecorded
     */
    public boolean isIfRecorded() {
        return ifRecorded;
    }

    /**
     * @param ifRecorded the ifRecorded to set
     */
    public void setIfRecorded(boolean ifRecorded) {
        this.ifRecorded = ifRecorded;
    }

    /**
     * @return the ifPTZCtrl
     */
    public boolean isIfPTZCtrl() {
        return ifPTZCtrl;
    }

    /**
     * @param ifPTZCtrl the ifPTZCtrl to set
     */
    public void setIfPTZCtrl(boolean ifPTZCtrl) {
        this.ifPTZCtrl = ifPTZCtrl;
        //写日志
        if (ifPTZCtrl) writePreviewLog(sOpenPTZCtrlWindow );//"启用窗口云台控制"
        else writePreviewLog(sClosePTZCtrlWindow );//"关闭窗口云台控制"
    }

    /**
     * @return the ifTalkBack
     */
    public boolean isIfTalkBack() {
        return ifTalkBack;
    }

    /**
     * @param ifTalkBack the ifTalkBack to set
     */
    public void setIfTalkBack(boolean ifTalkBack) {
        this.ifTalkBack = ifTalkBack;
    }

    /**
     * @return the ifOpenVoice
     */
    public boolean isIfOpenVoice() {
        return ifOpenVoice;
    }

    /**
     * @param ifOpenVoice the ifOpenVoice to set
     */
    public void setIfOpenVoice(boolean ifOpenVoice) {
        this.ifOpenVoice = ifOpenVoice;
    }

    /**
     * @return the ifFishBallTrack
     */
    public boolean isIfFishBallTrack() {
        return ifFishBallTrack;
    }

    /**
     * @param ifFishBallTrack the ifFishBallTrack to set
     */
    public void setIfFishBallTrack(boolean ifFishBallTrack) {
        this.ifFishBallTrack = ifFishBallTrack;
        //写日志
        if (ifFishBallTrack) writePreviewLog(sStartFishBallTrack);// "开始球机联动"
        else writePreviewLog(sStopFishBallTrack);// "停止球机联动"
    }
    /**
     * @return the sEncodingDVRChannelNode
     */
    public String getEncodingDVRChannelNode() {
        return sEncodingDVRChannelNode;
    }

    /**
     * @param EncodingDVRChannelNode the sEncodingDVRChannelNode to set
     */
    public void setEncodingDVRChannelNode(String EncodingDVRChannelNode) {

        this.sEncodingDVRChannelNode = EncodingDVRChannelNode;
    }
    /**
     * @return the sGroupName
     */
    public String getGroupName() {
        return sGroupName;
    }

    /**
     * @param sGroupName the sGroupName to set
     */
    public void setGroupName(String sGroupName) {
        this.sGroupName = sGroupName;
    }
    /**
     * @return the sSerialNoJoin
     */
    public String getSerialNoJoin() {
        return sSerialNoJoin;
    }

    /**
     * @param sSerialNoJoin the sSerialNoJoin to set
     */
    public void setSerialNoJoin(String sSerialNoJoin) {
        this.sSerialNoJoin = sSerialNoJoin;
    }
    @Override
    public ChannelRealPlayer clone() throws CloneNotSupportedException
    { 
        
        ChannelRealPlayer Cloned=(ChannelRealPlayer)super.clone();//Object 中的clone()识别出你要复制的是哪一个对象。 
            Cloned.mDeviceParaBean = (DeviceParaBean)mDeviceParaBean.clone();
            Cloned.panelRealplay = null;//预览的控件Panel
            Cloned.bRealPlay = false;//是否在预览.
            Cloned.lPreviewHandle = new NativeLong(-1);/*预览句柄*/

            Cloned.ifRecorded = false;//    1、是否开始录像
            Cloned.sFileNameRecord = "";// 录像或者抓图的文件名（不带后缀）
            Cloned.ifPTZCtrl = false;//2、是否启用窗口云台控制PTZCtrl

            Cloned.ifTalkBack = false;//4、是否开始对讲
            Cloned.ifOpenVoice = false;//5、是否打开声音
            Cloned.ifFishBallTrack = false;//6、是否开始球机联动
            
            Cloned.iBrightness = 6;    //亮度
            Cloned.iContrast = 6;      //对比度
            Cloned.iSaturation = 6;    //饱和度
            Cloned.iHue = 6;           //色度
            Cloned.iVolume = 50;       //语音对讲客户端的音量，百分比
            Cloned.lVoiceComHandle = new NativeLong(-1);/*语音对讲的句柄*/

            Cloned.lAlarmHandle = new NativeLong(-1);//报警布防句柄
            Cloned.lListenHandle = new NativeLong(-1);//报警监听句柄
        return Cloned; 
    }  
    /**
        * 函数:      previewOneChannel
        * 函数描述:  预览某一个通道的摄像
        * @return int   预览成功返回1；预览失败返回0；出现错误返回-1；原先已经在预览则返回-2
    */
    public int previewThisChannel(){
        try{
            if(panelRealplay == null) return -1;//预览Panel错误
            if(lChannel.intValue() < 0) return -1;//通道错误
            if(lUserID.intValue() < 0) return -1;//登录ID错误
            if(lPreviewHandle.intValue() > -1) return -2;//已经预览了
            
            //开始在该CurrenPanel预览该设备Channel通道录像
            W32API.HWND hwnd = new W32API.HWND(Native.getComponentPointer(panelRealplay));//获取窗口句柄
            HCNetSDK.NET_DVR_CLIENTINFO ClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();//软解码预览参数
            ClientInfo.lChannel = lChannel;//设置软解码预览参数的通道号
            ClientInfo.hPlayWnd = hwnd;//设置软解码预览参数的窗口句柄
            //预览
            lPreviewHandle = CommonParas.hCNetSDK.NET_DVR_RealPlay_V30(lUserID, ClientInfo, null, null, true);

            //预览失败时:
            if (lPreviewHandle.intValue() == -1){
                TxtLogger.append(sFileName, "previewThisChannel()","预览失败" + CommonParas.hCNetSDK.NET_DVR_GetLastError()
                        +"\r\nUserID"+lUserID.toString()+"\r\n"+ClientInfo.toString());
                return 0;
            } else{
                bRealPlay = true;//设置类状态为预览状态
                HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo = CommonParas.getStrDeviceInfo(iIndexOflistDevice, sFileName);
                //HCNetSDK.NET_DVR_IPPARACFG StrIpparaCfg = CommonParas.getStrIpparacfg(iIndexOflistDevice, sFileName);
                //this.ifFishBallTrack = HCNetSDKExpand.isFishEyeFirstChannel( StrDevInfo, StrIpparaCfg, lChannel.intValue(), sFileName);
                //暂时先不考虑数字通道、接入设备的问题。
                this.ifFishBallTrack = HCNetSDKExpand.isFishEyeFirstChannel( StrDevInfo, lChannel.intValue(), sFileName);
                
                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(lPreviewHandle, SetPreviewStateIconCallBack, 0);//设置预览状态图标，比如录像、对讲、声音控制等
                ////日志不在此填写
                return 1;
            }
        }catch (Exception e){
            TxtLogger.append(sFileName, "previewOneChannel()","系统预览某一个通道的视频摄像过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }
    
    /**
        * 函数:      stopPreviewThisChannel
        * 函数描述:  停止预览某一个通道的摄像
        * @return int   停止预览成功返回1；停止预览失败返回0；出现错误返回-1；原先停止预览则返回-2
    */
    public int stopPreviewThisChannel(){
        try{
            if(lPreviewHandle.intValue() < 0) return -2;//已经停止预览了
            doBeforeStopPreview();
            if (CommonParas.hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandle)) {//设置停止预览
                doAfterStopPreview();
                //日志不在此填写
                return 1;
            }
            else return 0;
        }catch (Exception e){
            TxtLogger.append(sFileName, "stopPreviewThisChannel()","系统预览某一个通道的视频摄像过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }
    /**
          * 函数:      doBeforeStopPreview
          * 函数描述:  在结束窗口预览之前需要进行的操作
    */ 
    public void doBeforeStopPreview(){
        /*在停止预览之前，要做以下操作：
            1、是否在录像，如果是，则调用停止录像函数
            2、是否开始球机联动，如果是，则停止球机联动
            3、是否打开声音
            4、是否在进行对讲
        */
        if (isIfFishBallTrack()){ ifFishBallTrack = false;}//设置停止鱼球联动。不用setIfFishBallTrack(false)，因为该函数会写日志
        if (isIfRecorded()){saveOnePanelRecord();}//设置停止录像
        if (isIfOpenVoice()){setPreviewVoice();}//设置关闭声音
        if (isIfTalkBack()){setTalkBack();}//设置停止对讲
        CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(lPreviewHandle, null, 0);//设置预览状态图标，比如录像、对讲、声音控制等
    }
    /**
          * 函数:      doAfterStopPreview
          * 函数描述:  在结束窗口预览之后需要进行的操作
    */ 
    public void doAfterStopPreview(){
        //考虑到停止预览之后，可能还需要再次预览，所以不做初始化参数参数操作：intialParas()
        lPreviewHandle = new NativeLong(-1);/*预览句柄*/
        panelRealplay.repaint();
    }
    /**
          * 函数:      saveOnePanelRecord
          * 函数描述:  给预览窗口存储录像
          * @return 开始录像，返回1；结束录像，返回2；操作失败，返回0；其他错误，返回-1
    */ 
    public int saveOnePanelRecord(){
        
        try{
           
            if (lPreviewHandle.intValue() == -1)
            {
                JOptionPane.showMessageDialog(null, sPreviewFirst);// "请先预览！"Please preview first!
                return -1;
            }
            //如果不是正在存储文件状态
            if (!ifRecorded){
                //得到存储路径，自动命名文件名
                String PathName = CommonParas.SysParas.getSysParasFileSaveDirVedio();
                
                this.sFileNameRecord = this.getInitialName(PathName);
                String SaveFileName = sFileNameRecord + ".mp4";
                if (CommonParas.hCNetSDK.NET_DVR_SaveRealData(lPreviewHandle, SaveFileName))
                {
                    //预览界面上加上录象标志图片
//                    String ImageName = getClass().getResource(sImageRecord).toString();
//                    ImageName = ImageName.substring(6);
//                    ImageName = ImageName.replaceAll("/", "\\\\");
//                    ImageName = CommonParas.SysPath + "record.bmp";
                    SetPreviewStateIconCallBack.addIconAmageFile(sImageRecord);
                    
                    ifRecorded = true;
                    //写日志
                    writePreviewLog(sPreviewStartRecord);// "预览开始录像"
                    return 1;
                }else {
                    //写错误日志
                    writePreviewErrorLog( sPreviewStartRecord_Fail);// "预览开始录像时失败"
                    return 0;
                }
                
            } else{//如果正在存储文件状态
                boolean ifSaveSuccess = CommonParas.hCNetSDK.NET_DVR_StopSaveRealData(lPreviewHandle);

                if (ifSaveSuccess) {
                    //JOptionPane.showMessageDialog(this, "存储录像文件成功：" + channelPlayBack.getSaveFileName());
                    File FileRecord = new File(sFileNameRecord + ".mp4");
                    File NewFileRecord = new File(getReName() + ".mp4");
                    if(!NewFileRecord.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名   
                    {   
                        //FileRecord.renameTo(NewFileRecord);
                        if (FileRecord.renameTo(NewFileRecord)) CommonParas.showMessage(sSaveVideo_Succ + NewFileRecord, sFileName);//"成功保存录像："
                    }   
                    //预览界面上删除录象标志图片
//                    String ImageName = getClass().getResource(sImageRecord).toString();
//                    ImageName = ImageName.substring(6);
                    SetPreviewStateIconCallBack.delIconAmageFile(sImageRecord);
                    
                    this.sFileNameRecord = "";
                    ifRecorded = false;
                    //写日志
                    writePreviewLog(sPreviewStopRecord );//"预览停止录像"
                    return 2;
                }else {
                    //写错误日志
                    writePreviewErrorLog(sPreviewStopRecord_Fail);//"预览停止录像时失败"
                    return 0;
                }
            }
        }catch(Exception e){
            TxtLogger.append(this.sFileName, "saveOnePanelRecord()","给某一个预览窗口录像的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
        return -1;
    }
    /**
          * 函数:      captureOnePanelPicture
          * 函数描述:  给预览窗口抓图
          * @return    成功抓图，返回1；操作失败，返回0；其他错误，返回-1
    */ 
    public int captureOnePanelPicture(){
        try{    
            if (lPreviewHandle.intValue() == -1)
            {
                JOptionPane.showMessageDialog(null, sPreviewFirst);// "请先预览"
                return -1;
            }
            //得到存储路径，自动命名文件名
            String PathName = CommonParas.SysParas.getSysParasFileSaveDirCapture();
            String SaveFileName;
            if (CommonParas.hCNetSDK.NET_DVR_SetCapturePictureMode(HCNetSDK.CAPTURE_MODE.JPEG_MODE)){
                SaveFileName = this.getInitialName(PathName) + ".jpg";
            }
            else{
                SaveFileName = this.getInitialName(PathName) + ".bmp";
                //写错误日志
                CommonParas.SystemWriteErrorLog( sSetCaptureMode_Fail,  mDeviceParaBean.getAnothername(), sFileName);// "设置抓图模式为JPG模式时失败"
            }
            
            if (!CommonParas.hCNetSDK.NET_DVR_CapturePicture(lPreviewHandle,SaveFileName)){
                //写错误日志
                writePreviewErrorLog(sPreviewCapture_Fail);// "预览抓图时失败"
                
                return 0;
                
            }else {
                //写日志
                writePreviewLog(sPreviewCapture);// "预览抓图"
                
                CommonParas.showMessage(sSaveCapture_Succ + SaveFileName, sFileName);// "成功保存抓图文件："
                return 1;
            }
        }catch(Exception e){
            TxtLogger.append(this.sFileName, "captureOnePanelPicture()","给预览窗口抓图的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
        return -1;
    }
    /**
          * 函数:      setPreviewVoice
          * 函数描述:  给预览窗口打开/关闭声音
          * @return    关闭声音，返回2；打开声音，返回1；操作失败，返回0；其他错误，返回-1
    */ 
    public int setPreviewVoice(){
        try{
            if (lPreviewHandle.intValue() == -1)
            {
                JOptionPane.showMessageDialog(null, sPreviewFirst);// "请先预览"
                return -1;
            }

            if (!ifOpenVoice){
                if (CommonParas.g_bShareSound)
                {
                        if (!CommonParas.hCNetSDK.NET_DVR_OpenSoundShare(lPreviewHandle))
                        {
                            //写错误日志
                            writePreviewErrorLog( sPreviewOpenSound_Fail);// "预览中打开声音时失败"
                            
                            return 0;
                        }else {
                            ifOpenVoice = true;
                            //写日志
                            writePreviewLog(sPreviewOpenSound);// "预览中打开声音"
                            return 1;
                        }
                }
                else
                {
                        if (!CommonParas.hCNetSDK.NET_DVR_OpenSound(lPreviewHandle))
                        {
                            //写错误日志
                            writePreviewErrorLog(sPreviewOpenSound_Fail);//"预览中打开声音时失败"
                            
                            return 0;
                        }else {
                            ifOpenVoice = true;
                            //写日志
                            writePreviewLog(sPreviewOpenSound);//"预览中打开声音"
                            return 1;
                        }
                }
            }else {
                if (!CommonParas.g_bShareSound){
                        if (!CommonParas.hCNetSDK.NET_DVR_CloseSound())
                        {
                            //写错误日志
                            writePreviewErrorLog(sPreviewOffSound_Fail);// "预览中关闭声音时失败"
                            return 0;
                        }else {
                            ifOpenVoice = false;
                            //写日志
                            writePreviewLog(sPreviewOffSound);// "预览中关闭声音"
                            return 2;
                        }
                }else{
                        if (!CommonParas.hCNetSDK.NET_DVR_CloseSoundShare(lPreviewHandle))
                        {
                            //写错误日志
                            writePreviewErrorLog(sPreviewOffSound_Fail);//"预览中关闭声音时失败"
                            
                            return 0;
                        }else {
                            ifOpenVoice = false;
                            //写日志
                            writePreviewLog(sPreviewOffSound);//"预览中关闭声音"
                            
                            return 2;
                        }
                }

            }
        }catch(Exception e){
            TxtLogger.append(this.sFileName, "captureOnePanelPicture()","给预览窗口抓图的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
        return -1;
    }
    /**
          * 函数:      setTalkBack
          * 函数描述:  设置对讲
    */ 
    public void  setTalkBack(){}
    /**
        * 函数:      saveOneDevCheckTime
        * 函数描述:  设备校时
    */
    public void saveOneDevCheckTime(){
        try{
            if (lPreviewHandle.intValue() == -1)
            {
                JOptionPane.showMessageDialog(null, sPreviewFirst);// "请先预览"
                return;
            }

            HCNetSDK.NET_DVR_TIME strCurTime = new HCNetSDK.NET_DVR_TIME();
            
            Calendar c = Calendar.getInstance(); 
            c.setTime(new Date());
            
            strCurTime.dwYear = c.get(Calendar.YEAR);  
            strCurTime.dwMonth = c.get(Calendar.MONTH) + 1;  
            strCurTime.dwDay = c.get(Calendar.DAY_OF_MONTH); 
            strCurTime.dwHour = c.get(Calendar.HOUR_OF_DAY);  
            strCurTime.dwMinute = c.get(Calendar.MINUTE);  
            strCurTime.dwSecond = c.get(Calendar.SECOND); 
            
            strCurTime.write();
            Pointer lpPicConfig = strCurTime.getPointer();
            boolean setDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_TIMECFG,
                    new NativeLong(0), lpPicConfig, strCurTime.size());
            strCurTime.read();
            if (setDVRConfigSuc != true)
            {
                //写错误日志
                writePreviewErrorLog(sTimeSyn_Fail);// "设备校时失败"
                
            } else
            {
                CommonParas.showMessage(MessageFormat.format(sTimeSynSpecific_Succ, mDeviceParaBean.getAnothername()), sFileName);//"设备 "+mDeviceParaBean.getAnothername()+" 校时成功"
                //写日志
                writePreviewLog(sTimeSyn);// "设备校时"
            }
        }catch (Exception e){
            TxtLogger.append(sFileName, "saveOneDevCheckTime()","系统在设备校时过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
          * 函数:      getInitialName
          * 函数描述:  给出刚开始存储录像的初始文件名（不带后缀）
          *             命名规则：设备名_通道号_开始时间_终止时间.mp4/jpg
    */ 
    private String getInitialName(String PathName){
        
        //String AnotherNameJoin = 
        
        Date NewDate = new Date();
        int Channel = lChannel.intValue();
        
        String FileName = PathName + "\\Preview_" + mDeviceParaBean.getAnothername()+ "_" + Channel +  "_" + DateUtil.convertDateToString("yyyyMMddHHmmss", NewDate);
        if (Channel > HCNetSDK.MAX_ANALOG_CHANNUM) {
            Channel = Channel - HCNetSDK.MAX_ANALOG_CHANNUM;
            DeviceParaBean DeviceParaBeanJoin =  CommonParas.getDeviceParaBeanJoin(iIndexOflistDevice, lChannel.intValue(), sFileName);
            if (DeviceParaBeanJoin != null) FileName = PathName + "\\Preview_" + mDeviceParaBean.getAnothername()+ "_"+ DeviceParaBeanJoin.getAnothername()
                                                    + "_" + Channel +  "_" + DateUtil.convertDateToString("yyyyMMddHHmmss", NewDate);
        }
        
        return FileName;
    }
    /**
          * 函数:      getReName
          * 函数描述:  给出存储录像的最终文件名（不带后缀）、或者抓图文件名（不带后缀）
    */ 
    private String getReName(){
        Date NewDate = new Date();
        String FileName = this.sFileNameRecord + "_" + DateUtil.convertDateToString("yyyyMMddHHmmss", NewDate);
        return FileName;
    }
    
    public void writePreviewLog(String Description){
        //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
        CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, Description, mDeviceParaBean.getSerialNO(), sGroupName, 
                                sEncodingDVRChannelNode, sSerialNoJoin,"",CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE,sFileName);
    }
    public void writePreviewLog(String Description, String remarks){
        //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
        CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, Description, mDeviceParaBean.getSerialNO(), sGroupName, 
                                sEncodingDVRChannelNode, sSerialNoJoin,"",CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE, remarks, sFileName);
    }
  
    public void writePreviewErrorLog(String Description){
        //写错误日志
        //操作时间、设备别名、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
        CommonParas.SystemWriteErrorLog("", mDeviceParaBean.getAnothername(), Description, mDeviceParaBean.getSerialNO(), sGroupName,sEncodingDVRChannelNode, 
                                    sSerialNoJoin,"",CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE,sFileName);
    }

    
    /**
     * @return the Brightness
     */
    public int getBrightness() {
        return iBrightness;
    }

    /**
     * @param Brightness the Brightness to set
     */
    public void setBrightness(int Brightness, boolean SetVideoEffect) {
        this.iBrightness = Brightness;
        if (SetVideoEffect) setVideoEffect(sBrightnessAdjustment);// "亮度调节"
    }

    /**
     * @return the Contrast
     */
    public int getContrast() {
        return iContrast;
    }

    /**
     * @param Contrast the Contrast to set
     */
    public void setContrast(int Contrast, boolean SetVideoEffect) {
        this.iContrast = Contrast;
        if (SetVideoEffect) setVideoEffect(sContrastAdjustment);// "对比度调节"
    }

    /**
     * @return the Saturation
     */
    public int getSaturation() {
        return iSaturation;
    }

    /**
     * @param Saturation the Saturation to set
     */
    public void setSaturation(int Saturation, boolean SetVideoEffect) {
        this.iSaturation = Saturation;
        if (SetVideoEffect) setVideoEffect(sSaturationAdjustment);// "饱和度调节"
    }

    /**
     * @return the Hue
     */
    public int getHue() {
        return iHue;
    }

    /**
     * @param Hue the Hue to set
     * @param SetVideoEffect
     */
    public void setHue(int Hue, boolean SetVideoEffect) {
        this.iHue = Hue;
        if (SetVideoEffect) setVideoEffect(sChromaAdjustment);// "色度调节"
    }

    /**
     * @return the Volume
     */
    public int getVolume() {
        return iVolume;
    }

    /**
     * @param Volume the Volume to set
     */
    public void setVolume(int Volume) {
        if (lVoiceComHandle.intValue() > -1){
            CommonParas.hCNetSDK.NET_DVR_SetVoiceComClientVolume(lVoiceComHandle, (short) (Volume * ((0xffff) / 100)));
            //暂时先不给"设置语音对讲客户端的音量"写日志。要写的话，只要注释掉下面的语句即可。
            //writePreviewLog(sSetVoiceIntercomVolume);// "设置语音对讲客户端的音量"
        }
        this.iVolume = Volume;
    }
    
    public boolean setVideoEffect(String Remarks)
    {
        if(!CommonParas.hCNetSDK.NET_DVR_ClientSetVideoEffect(lPreviewHandle, iBrightness, iContrast, iSaturation, iHue))
        {
            //JOptionPane.showMessageDialog(this, "设置预览视频显示参数失败");
            CommonParas.showErrorMessage(sSetVideoDisplayParaFail, mDeviceParaBean.getAnothername(), sFileName);// "设置预览视频显示参数失败"
            return false;
        }else{
            //暂时先不给"设置预览视频显示参数"写日志。要写的话，只要注释掉下面的语句即可。
            //if (!Remarks.equals("")) writePreviewLog(sSetVideoDisplayPara, Remarks);// "设置预览视频显示参数"
            return true;
        }
    }
    
    public boolean getVideoEffect(String Remarks)
    {
        //NET_DVR_ClientGetVideoEffect(NativeLong lRealHandle,IntByReference pBrightValue,IntByReference pContrastValue, IntByReference pSaturationValue,IntByReference pHueValue);
        IntByReference pBrightValue = new IntByReference(0);//亮度
        IntByReference pContrastValue = new IntByReference(0);//对比度
        IntByReference pSaturationValue = new IntByReference(0);//饱和度
        IntByReference pHueValue = new IntByReference(0);//色度
        if(!CommonParas.hCNetSDK.NET_DVR_ClientGetVideoEffect(lPreviewHandle, pBrightValue, pContrastValue,  pSaturationValue, pHueValue))
        {
            CommonParas.showErrorMessage(sGetVideoDisplayParaFail, mDeviceParaBean.getAnothername(), sFileName);// "获取预览视频显示参数失败"
            return false;
        }else{
            //暂时先不给"获取预览视频显示参数"写日志。要写的话，只要注释掉下面的语句即可。
            //if (!Remarks.equals("")) writePreviewLog(sGetVideoDisplayPara, Remarks);// "获取预览视频显示参数"
            iBrightness = pBrightValue.getValue();
            iContrast = pContrastValue.getValue(); 
            iSaturation = pSaturationValue.getValue(); 
            iHue = pHueValue.getValue();
            return true;
        }
    }

    /**
     * @return the lVoiceComHandle
     */
    public NativeLong getVoiceComHandle() {
        return lVoiceComHandle;
    }

    /**
     * @param VoiceComHandle the lVoiceComHandle to set
     */
    public void setVoiceComHandle(NativeLong VoiceComHandle) {
        this.lVoiceComHandle = VoiceComHandle;
    }

    
    
    
    /**
        * 函数:      FDrawFunSetPreviewStateIcon
        * 函数描述:  设置预览状态图标，比如录像、对讲、声音控制等
    */
    class FDrawFunSetPreviewStateIcon implements HCNetSDK.FDrawFun{
        //POINT CenterPosition;
        ArrayList<String> listIconAmageFileNames = new ArrayList<>(); 
        private final GDI32 gDi = GDI32.INSTANCE;
        private final USER32 uSer = USER32.INSTANCE;
        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser) {
            for (int i=0;i<listIconAmageFileNames.size();i++){
                //只有bmp文件可以正确LoadImage，其他的如png、gif、jpg等类型的图像都不能正常加载。但是转换成bmp文件就可以正确加载
                W32API.HANDLE hBitmap = uSer.LoadImage(null, listIconAmageFileNames.get(i), IMAGE_BITMAP, iResolution, iResolution, LR_LOADFROMFILE|LR_CREATEDIBSECTION);//LR_DEFAULTCOLOR);//LR_LOADFROMFILE|LR_CREATEDIBSECTION|LR_LOADREALSIZE
                W32API.HANDLE hBrush = gDi.CreatePatternBrush(hBitmap);
                //HANDLE hOldPen = gDi.SelectObject(hDc,hPen);//旧画笔句柄存储在hOldPen中
                W32API.HANDLE hOldBrush = gDi.SelectObject(hDc, hBrush);
                //从画面右边开始显示图标
//                int Width = panelRealplay.getWidth();
//                int Remainder = Width % iResolution;
//                gDi.Rectangle(hDc, Width - (i+1)*iResolution - Remainder, 0, Width -  - i*iResolution - Remainder, iResolution);
                //从画面左边开始显示图标
                gDi.Rectangle(hDc, i*iResolution, 0, (i+1)*iResolution, iResolution);

                gDi.SelectObject(hDc, hOldBrush);
                gDi.DeleteObject(hOldBrush);
                gDi.DeleteObject(hBitmap);
            }
        }
        
        public void addIconAmageFile(String IconAmageFileName){
            if (IconAmageFileName == null || IconAmageFileName.equals("")) return;
            listIconAmageFileNames.add(IconAmageFileName);
        }
        
        public void delIconAmageFile(String IconAmageFileName){
            if (IconAmageFileName == null || IconAmageFileName.equals("")) return;
            listIconAmageFileNames.remove(IconAmageFileName);
        }
    }
    
    /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    public static void modifyLocales(){
        
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作
        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        sOpenPTZCtrlWindow =        Locales.getString("ClassStrings", "ChannelRealPlayer.sOpenPTZCtrlWindow");  //启用窗口云台控制
        sOpenPTZCtrlWindow =        Locales.getString("ClassStrings", "ChannelRealPlayer.sOpenPTZCtrlWindow");  //关闭窗口云台控制
        sStartFishBallTrack =       Locales.getString("ClassStrings", "ChannelRealPlayer.sStartFishBallTrack");  //开始球机联动
        sStopFishBallTrack =        Locales.getString("ClassStrings", "ChannelRealPlayer.sStopFishBallTrack");  //停止球机联动
        sPreviewFirst =             Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewFirst");  //请先预览！
        sPreviewStartRecord =       Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewStartRecord");  //预览开始录像
        sPreviewStartRecord_Fail =  Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewStartRecord_Fail");  //预览开始录像时失败
        sSaveVideo_Succ =           Locales.getString("ClassStrings", "ChannelRealPlayer.sSaveVideo_Succ");  //成功保存录像：
        sPreviewStopRecord =        Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewStopRecord");  //预览停止录像
        sPreviewStopRecord_Fail =   Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewStopRecord_Fail");  //预览停止录像时失败
        sSetCaptureMode_Fail =      Locales.getString("ClassStrings", "ChannelRealPlayer.sSetCaptureMode_Fail");  //设置抓图模式为JPG模式时失败
        sPreviewCapture_Fail =      Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewCapture_Fail");  //预览抓图时失败
        sPreviewCapture =           Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewCapture");  //预览抓图
        sSaveCapture_Succ =         Locales.getString("ClassStrings", "ChannelRealPlayer.sSaveCapture_Succ");  //成功保存抓图文件：
        sPreviewOpenSound_Fail =    Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewOpenSound_Fail");  //预览中打开声音时失败
        sPreviewOpenSound =         Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewOpenSound");  //预览中打开声音
        sPreviewOffSound_Fail =     Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewOffSound_Fail");  //预览中关闭声音时失败
        sPreviewOffSound =          Locales.getString("ClassStrings", "ChannelRealPlayer.sPreviewOffSound");  //预览中关闭声音
        sTimeSyn_Fail =             Locales.getString("ClassStrings", "ChannelRealPlayer.sTimeSyn_Fail");  //设备校时失败
        sTimeSynSpecific_Succ =     Locales.getString("ClassStrings", "ChannelRealPlayer.sTimeSynSpecific_Succ");  //设备“{0}”校时成功
        sTimeSyn =                  Locales.getString("ClassStrings", "ChannelRealPlayer.sTimeSyn");  //设备校时
        sChromaAdjustment =         Locales.getString("ClassStrings", "ChannelRealPlayer.sChromaAdjustment");  //色度调节
        sSaturationAdjustment =     Locales.getString("ClassStrings", "ChannelRealPlayer.sSaturationAdjustment");  //饱和度调节
        sContrastAdjustment =       Locales.getString("ClassStrings", "ChannelRealPlayer.sContrastAdjustment");  //对比度调节
        sBrightnessAdjustment =     Locales.getString("ClassStrings", "ChannelRealPlayer.sBrightnessAdjustment");  //亮度调节
        sSetVideoDisplayPara =      Locales.getString("ClassStrings", "ChannelRealPlayer.sSetVideoDisplayPara");  //设置预览视频显示参数
        sSetVideoDisplayParaFail =  Locales.getString("ClassStrings", "ChannelRealPlayer.sSetVideoDisplayParaFail");  //设置预览视频显示参数失败
        sGetVideoDisplayPara =      Locales.getString("ClassStrings", "ChannelRealPlayer.sGetVideoDisplayPara");  //获取预览视频显示参数
        sGetVideoDisplayParaFail =  Locales.getString("ClassStrings", "ChannelRealPlayer.sGetVideoDisplayParaFail");  //获取预览视频显示参数失败
        sSetVoiceIntercomVolume =   Locales.getString("ClassStrings", "ChannelRealPlayer.sSetVoiceIntercomVolume");  //设置语音对讲客户端的音量

    }
    
    private static String sOpenPTZCtrlWindow = "启用窗口云台控制";
    private static String sClosePTZCtrlWindow = "关闭窗口云台控制";
    private static String sStartFishBallTrack = "开始球机联动";
    private static String sStopFishBallTrack = "停止球机联动";
    private static String sPreviewFirst = "请先预览！";
    private static String sPreviewStartRecord = "预览开始录像";
    private static String sPreviewStartRecord_Fail = "预览开始录像时失败";
    private static String sSaveVideo_Succ = "成功保存录像：";
    private static String sPreviewStopRecord = "预览停止录像";
    private static String sPreviewStopRecord_Fail = "预览停止录像时失败";
    private static String sSetCaptureMode_Fail = "设置抓图模式为JPG模式时失败";
    private static String sPreviewCapture_Fail = "预览抓图时失败";
    private static String sPreviewCapture = "预览抓图";
    private static String sSaveCapture_Succ = "成功保存抓图文件：";
    private static String sPreviewOpenSound_Fail = "预览中打开声音时失败";
    private static String sPreviewOpenSound = "预览中打开声音";
    private static String sPreviewOffSound_Fail = "预览中关闭声音时失败";
    private static String sPreviewOffSound = "预览中关闭声音";
    private static String sTimeSyn_Fail = "设备校时失败";
    private static String sTimeSynSpecific_Succ = "设备“{0}”校时成功";
    private static String sTimeSyn = "设备校时";
    private static String sChromaAdjustment = "色度调节";
    private static String sSaturationAdjustment = "饱和度调节";
    private static String sContrastAdjustment = "对比度调节";
    private static String sBrightnessAdjustment = "亮度调节";
    private static String sSetVideoDisplayPara = "设置预览视频显示参数";
    private static String sSetVideoDisplayParaFail = "设置预览视频显示参数失败";
    private static String sGetVideoDisplayPara = "获取预览视频显示参数";
    private static String sGetVideoDisplayParaFail = "获取预览视频显示参数失败";
    private static String sSetVoiceIntercomVolume = "设置语音对讲客户端的音量";


}
    
