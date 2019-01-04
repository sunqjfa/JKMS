/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.GDI32.RECT;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.User32.POINT;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.ptr.IntByReference;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import jyms.data.DevTimeTemplateBean;
import jyms.data.DeviceResourceBean;
import jyms.data.TimeTemplateBean;
import jyms.data.TxtLogger;
import jyms.tools.DomXML;
/**
 *
 * @author John
 */
public class JInFrameAlarmConfig extends javax.swing.JInternalFrame {

    private final String sFileName = this.getClass().getName() + ".java";
    static GDI32 gDi = GDI32.INSTANCE;
    static USER32 uSer = USER32.INSTANCE;
    
    private HCNetSDK.NET_DVR_PICCFG_V30 struPicCfg;//通道图像结构体
    private String[] sAlarmTypes = new String[]{"移动侦测", "视频丢失", "遮挡报警", "越界侦测", "区域入侵侦测"};
    private ArrayList<String> listAlarmTypes = new ArrayList();//存储报警类型列表
    private String sAlarmType = "";//当前的设备报警类型
    private byte[] pOutBufAlarm = new byte[64*1024];//64k应该够用了
    private byte[] pOutBufVideoPic = new byte[64*1024];//64k应该够用了
    private int iDetectIndex = 0;			//motion detect zone index移动检测区域索引
    private final int iPrecision = 16;// the precision of the 22*11 detect unit under D1(704*576) resolution is 32*32, in the demothepicture is displayed in cif(352*288), and precision/2
    private final int iMultiple = 2;//倍数。和iPrecision相对，32/16 = 2
    private final int MAX_MOTION_NUM = 4;
    private RECT[] rectSet = new RECT[MAX_MOTION_NUM];	//motion detect zone display rectanglemotion 移动侦测功能区显示矩形
    private RECT[] rectMouse = new RECT[MAX_MOTION_NUM];//mouse drawing line鼠标绘图线
    private FDrawFunMotionDetectGet MotionDetectGetCallBack = new FDrawFunMotionDetectGet(); //显示移动侦测区域回调函数
    private FDrawFunMotionDetectSet MotionDetectSetCallBack = new FDrawFunMotionDetectSet();//设置移动侦测区域回调函数
    
    private boolean bHideGet = false;//是否已经显示遮挡报警区域
    private FDrawFunHideGet HideGetCallBack = new FDrawFunHideGet();//显示遮挡报警区域回调函数
    private FDrawFunHideSet HideSetCallBack = new FDrawFunHideSet();//设置遮挡报警区域回调函数
    private FDrawFunGrids GridsCallBack = new FDrawFunGrids();//
    private HCNetSDK.byte96[] byGridsScope = new HCNetSDK.byte96[64];/*遮挡报警区域内部用类似移动侦测区域的方式表示*/
    
    
    private HCNetSDK.NET_DVR_CHANNEL_GROUP strChannelGroup;//通道号和组号信息结构体。批量获取/设置设备配置信息（带发送数据）用。
    private HCNetSDK.STATUSLIST strStatusList;////错误信息列表；NET_DVR_GetDeviceConfig使用到
    private HCNetSDK.NET_VCA_TRAVERSE_PLANE_DETECTION strTreversePlane;//越界报警结构体
    private FDrawFunTraversePlane TraversePlaneCallBack = new FDrawFunTraversePlane();//越界侦测回调函数

    private HCNetSDK.NET_VCA_FIELDDETECION strFieldDetecion;//区域入侵报警结构体
    private FDrawFunFieldDetecion FieldDetecionCallBack = new FDrawFunFieldDetecion();//越界侦测回调函数
    
    private HCNetSDK.NET_DVR_EXCEPTION_V30 struExceptionInfo;//异常信息结构体
    private ArrayList<String> listExceptionTypes = new ArrayList();//存储异常类型列表
    private String[] sEexceptionTypes = new String[]{"硬盘满","硬盘出错","网线断","IP 地址冲突","非法访问","输入/输出视频制式不匹配","视频信号异常","录像异常",
                                                    "阵列异常","前端/录像分辨率不匹配异常","行车超速（车载专用）","热备异常（N+1使用）","温度异常","子系统异常",
                                                    "风扇异常","POE供电异常"};
    private final String[] sExceptionTypeCodes = new String[]{"diskfull","diskerror","nicbroken","ipconflict","illaccess","videomismatch","badvideo","recordingfailure",
                                                    "raid","resolutionmismatch","","spareexception","temperatureexception","subsystemexception",
                                                    "fanexception","poepoweexception"};
    private String sExceptionType = "";//当前的设备异常报警类型
    private HCNetSDK.NET_DVR_ALARMINCFG_V30 struAlarmInCfg;//报警输入结构体
    private HCNetSDK.NET_DVR_ALARMOUTCFG_V30 struAlarmOutCfg;//报警输出结构体
    
    private DefaultMutableTreeNode m_DeviceRootResource1;//通道树根节点
    private DefaultMutableTreeNode m_DeviceRootResource2;//通道树根节点
    private DefaultMutableTreeNode m_DeviceRootResource3;//通道树根节点
    private DefaultMutableTreeNode m_DeviceRootResource4;//通道树根节点
    private ChannelRealPlayer channelRP;//通道预览信息类
    
    private int    iIndexOfTreeNodeClicked = 0;//用来判断是否鼠标是否点击在合适的位置上，比如必须点击在叶子节点上。如果点在枝节点上则会设置为0。点击在叶子节点上，分别为1、2、3、4
    
    private int indexOfTab = 0;//jTabbedPaneAlarm序号，0监控点报警，1异常报警，2报警输入，3报警输出
    private String sChannelNode = "";//存储当前的通道节点名
    private int iIndexListDevicePara = -1;// glistDeviceDetailPara设备参数表中的索引
    private String sAnotherName = "";//存储当前的设备别名
    private String sAnotherNameJoin = "";//存储当前的接入设备别名
    private String sSerialNo = "";//存储当前的设备别序列号
    private String sNameOfTree = "";//当前的Tree名字
    private int iChannel = 0;//存储当前的通道号
    private NativeLong lUserID = new NativeLong(-1);//用户ID
    private HCNetSDK.NET_DVR_DEVICEINFO_V30 strDeviceInfo = null;//NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30
    private HCNetSDK.NET_DVR_IPPARACFG struIpparaCfg = null;
    
    private final ArrayList<TabParas> listTabParas = new ArrayList();//存储各个Tab的值，因为在互相切换期间，经常出现错误，比如当前的sChannelNode等值。
    
    private String sTemplateClass = CommonParas.TimeTemplateClass.MONITOR_SETUPALARMCHAN;//模板用途分类:
    private int indexComboBoxAlarmType;//存储上一次查询的事件类型：下拉列表框的索引，用来比对是否发生更改事件
    private boolean bIntialSelect = true;//是否刷新右侧界面过程中选择JComboBox，因为编码选择也是触发了ItemStateChanged事件
    private boolean bFirstSelect = true;//是否窗口初始化过程中选择JComboBox，因为编码选择也是触发了ItemStateChanged事件
    
    
//    private int iAlarmStrIntial = 0;//报警结构体初始化索引。即刚才读取哪个结构体了。1为通道图像结构体2、越界报警结构体3、区域入侵报警结构体
    //把通道图像结构体、越界报警结构体、区域入侵报警结构体如果未读出，则设为false；读出，则设为true
//    private ArrayList<Boolean> listIfRefrenshStru = new ArrayList<Boolean>();//现在是三种大结构体；1代表已经从刷新该设备该通道的信息。0代表没有
//    private static final int STRUNUMS = 3;//暂时3种：1为通道图像结构体2、越界报警结构体3、区域入侵报警结构体
    private HCNetSDK.NET_DVR_SCHEDTIMEWEEK[] strSchedTimeWeek = new HCNetSDK.NET_DVR_SCHEDTIMEWEEK[HCNetSDK.MAX_DAYS];//每天一个时段，共7天

    private ArrayList<String> listTimeTeplateName = new ArrayList<>();//存放最初的时间模板名称，用来比对是否发生修改
    private ArrayList<CheckListItem> listTraggerAlarmOut = new ArrayList<>();//报警输出通道checkbox对应值
    
    private ArrayList<CheckListItem> listTraggerRecord = new ArrayList<>();//触发录像通道checkbox对应值
    private DefaultListModel listModelTraggerAlarmOut;
    private DefaultListModel listModelTraggerRecord;
    private CheckListItemRenderer checkListItemRenderer;
    private String[] sHandleTypes = new String[]{"无响应","监视器上警告","声音警告","上传中心","触发报警输出","Jpeg抓图并上传EMail","无线声光报警器联动",
            "联动电子地图(目前仅PCNVR支持)","抓图并上传ftp","虚焦侦测联动聚焦","PTZ联动跟踪(球机跟踪目标)"};
    private String[] sHandleTypeCodes = new String[]{"","monitor","audio","center","alarmout","picture","wirelesslight","","uploadftp","","ptztrack"};
    private String sHandleTypeCode = "";//存储当前的异常处理方式列表（包括监控点报警、异常报警、报警输入等的异常处理方式）
    /**
     * Creates new form JInFrameAlarmConfig
     */
    public JInFrameAlarmConfig() {
//        this.sHandleTypeCodes = new String[]{"","monitor","audio","center","alarmout","picture","wirelesslight","","uploadftp","","ptztrack"};
//        this.sHandleTypes = new String[]{"无响应","监视器上警告","声音警告","上传中心","触发报警输出","Jpeg抓图并上传EMail","无线声光报警器联动",
//            "联动电子地图(目前仅PCNVR支持)","抓图并上传ftp","虚焦侦测联动聚焦","PTZ联动跟踪(球机跟踪目标)"};
        for (int i=0;i<4;i++){
            listTabParas.add(new TabParas());
        }
        initComponents();
        modifyLocales();
        initialWindowParas();
    }

    /**
        *函数:      initialWindowParas
        *函数描述:  初始化窗口参数
     */
    private void initialWindowParas(){
        try{
            jTreeResource1.setModel(this.initialResourceTreeModel1());
            jTreeResource2.setModel(this.initialResourceTreeModel2());
            jTreeResource3.setModel(this.initialResourceTreeModel3());
            jTreeResource4.setModel(this.initialResourceTreeModel4());
            //设置自定义UI
            jyms.tools.TreeUtil.modifyTreeCellRenderer(jTreeResource1);
            jyms.tools.TreeUtil.modifyTreeCellRenderer(jTreeResource2);
            jyms.tools.TreeUtil.modifyTreeCellRenderer(jTreeResource3);
            jyms.tools.TreeUtil.modifyTreeCellRenderer(jTreeResource4);
            CommonParas.CreateDeviceResourceTree(jTreeResource1, m_DeviceRootResource1, "", CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE, sFileName);//监控点
            //CommonParas.CreateGroupResourceTree(jTreeResource2, m_DeviceRootResource2,"", sFileName);//监控设备
            CommonParas.CreateDeviceTree(jTreeResource2, m_DeviceRootResource2, "", sFileName);//
            CommonParas.CreateDeviceResourceTree(jTreeResource3, m_DeviceRootResource3, "", CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_CODE, sFileName);//报警输入
            CommonParas.CreateDeviceResourceTree(jTreeResource4, m_DeviceRootResource4, "", CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE , sFileName);//报警输出

            jSplitPaneMonitorAlarm.setDividerLocation(360);
            jSplitPaneException.setDividerLocation(360);
            jSplitPaneAlarmIn.setDividerLocation(360);
            jSplitPaneAlarmOut.setDividerLocation(360);

            for (int i = 0; i < MAX_MOTION_NUM; i++)
            {
                rectSet[i] = new RECT();
                rectMouse[i] = new RECT();
            }
            for (int i = 0; i < 64; i++)
            {
                byGridsScope[i] = new HCNetSDK.byte96();  
            }
            channelRP = new ChannelRealPlayer() ;
            checkListItemRenderer = new CheckListItemRenderer();
            jListTraggerRecord.setCellRenderer(checkListItemRenderer);
            jListTraggerAlarmOut.setCellRenderer(checkListItemRenderer);
            jListTraggerAlarmOut2.setCellRenderer(checkListItemRenderer);
            jListTraggerRecord3.setCellRenderer(checkListItemRenderer);
            jListTraggerAlarmOut3.setCellRenderer(checkListItemRenderer);

            jListTraggerRecord.addMouseListener(new CheckListMouseListener());
            jListTraggerAlarmOut.addMouseListener(new CheckListMouseListener());
            jListTraggerAlarmOut2.addMouseListener(new CheckListMouseListener());
            jListTraggerRecord3.addMouseListener(new CheckListMouseListener());
            jListTraggerAlarmOut3.addMouseListener(new CheckListMouseListener());


            setJSpinnerState(jSpinnerDuration);
            setJSpinnerState(jSpinnerRate);

            initialJComboboxTemplates();//初始化时刷新下拉列表框选择的模板名称
            for (int i=0;i<4;i++){
                listTimeTeplateName.add("");
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "initialWindowParas()","系统在初始化窗口参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        *函数:      initialResourceTreeModel1
        *函数描述:  初始化设备资源树
        * @return DefaultTreeModel
     */
    private DefaultTreeModel initialResourceTreeModel1()
    {
        m_DeviceRootResource1 = new DefaultMutableTreeNode(sChannel);// "监控点"
        DefaultTreeModel myDefaultTreeModel = new DefaultTreeModel(m_DeviceRootResource1);//使用根节点创建模型
        return myDefaultTreeModel;
    }
    /**
        *函数:      initialResourceTreeModel2
        *函数描述:  初始化设备资源树
        * @return DefaultTreeModel
     */
    private DefaultTreeModel initialResourceTreeModel2()
    {
        m_DeviceRootResource2 = new DefaultMutableTreeNode(sDevice);// "设备"
        DefaultTreeModel myDefaultTreeModel = new DefaultTreeModel(m_DeviceRootResource2);//使用根节点创建模型
        return myDefaultTreeModel;
    }
    /**
        *函数:      initialResourceTreeModel3
        *函数描述:  初始化设备资源树
        * @return DefaultTreeModel
     */
    private DefaultTreeModel initialResourceTreeModel3()
    {
        m_DeviceRootResource3 = new DefaultMutableTreeNode(sAlarmIn);// "报警输入"
        DefaultTreeModel myDefaultTreeModel = new DefaultTreeModel(m_DeviceRootResource3);//使用根节点创建模型
        return myDefaultTreeModel;
    }
    /**
        *函数:      initialResourceTreeModel4
        *函数描述:  初始化设备资源树
        * @return DefaultTreeModel
     */
    private DefaultTreeModel initialResourceTreeModel4()
    {
        m_DeviceRootResource4 = new DefaultMutableTreeNode(sAlarmOut);// "报警输出"
        DefaultTreeModel myDefaultTreeModel = new DefaultTreeModel(m_DeviceRootResource4);//使用根节点创建模型
        return myDefaultTreeModel;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPaneAlarm = new javax.swing.JTabbedPane();
        jSplitPaneMonitorAlarm = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeResource1 = new javax.swing.JTree();
        jPanelRight1 = new javax.swing.JPanel();
        jPanelHeader = new javax.swing.JPanel();
        jLabelAlarmType = new javax.swing.JLabel();
        jComboBoxAlarmType = new javax.swing.JComboBox<>();
        jCheckBoxEnable = new javax.swing.JCheckBox();
        jLabelArmingTime = new javax.swing.JLabel();
        jComboBoxSetAlarmTime = new javax.swing.JComboBox<>();
        jButtonTemplate = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jPanelSetupAera = new javax.swing.JPanel();
        jPanelSetupAeraDetail = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabelArmingPara = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabelSensitive = new javax.swing.JLabel();
        jSliderSensitive = new javax.swing.JSlider();
        jTextFieldSensitive = new javax.swing.JTextField();
        jPanelSetupParas = new javax.swing.JPanel();
        jLabelID = new javax.swing.JLabel();
        jComboBoxID = new javax.swing.JComboBox<>();
        jButtonPTZControl = new javax.swing.JButton();
        jLabelLineDirection = new javax.swing.JLabel();
        jComboBoxCrossDirection = new javax.swing.JComboBox<>();
        jLabelPointNum = new javax.swing.JLabel();
        jComboBoxPointNum = new javax.swing.JComboBox<>();
        jLabelDuration = new javax.swing.JLabel();
        jSpinnerDuration = new javax.swing.JSpinner();
        jLabelDurationUnit = new javax.swing.JLabel();
        jLabelRate = new javax.swing.JLabel();
        jSpinnerRate = new javax.swing.JSpinner();
        jLabelRateUnit = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabelAlarmProcessMode = new javax.swing.JLabel();
        jPanelAlarmPrecessMode = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanelLinkageClient = new javax.swing.JPanel();
        jCheckBoxMonitorAlarm = new javax.swing.JCheckBox();
        jCheckBoxAudioAlarm = new javax.swing.JCheckBox();
        jCheckBoxCenterAlarm = new javax.swing.JCheckBox();
        jCheckBoxJpegEMailAlarm = new javax.swing.JCheckBox();
        jCheckBoxPTZTrack = new javax.swing.JCheckBox();
        jLabelLinkClient = new javax.swing.JLabel();
        jPanelRecordChannel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabelTriggerRecord = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jListTraggerRecord = new javax.swing.JList();
        jPanelAlarmOut = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jCheckBoxAlarmout = new javax.swing.JCheckBox();
        jScrollPane5 = new javax.swing.JScrollPane();
        jListTraggerAlarmOut = new javax.swing.JList();
        jPanelPlay = new javax.swing.JPanel();
        panelPlay = new java.awt.Panel();
        jButtonFullScreen = new javax.swing.JButton();
        jButtonDel = new javax.swing.JButton();
        jPanelFoot1 = new javax.swing.JPanel();
        jButtonSave = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jSplitPaneException = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTreeResource2 = new javax.swing.JTree();
        jPanelRight2 = new javax.swing.JPanel();
        jPanelHeader2 = new javax.swing.JPanel();
        jLabelExceptionType = new javax.swing.JLabel();
        jComboBoxExceptionType = new javax.swing.JComboBox<>();
        jCheckBoxEnable2 = new javax.swing.JCheckBox();
        jScrollPane10 = new javax.swing.JScrollPane();
        jPanelAlarmPrecessMode2 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelAlarmProcessMode2 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanelLinkageClient2 = new javax.swing.JPanel();
        jCheckBoxMonitorAlarm2 = new javax.swing.JCheckBox();
        jCheckBoxAudioAlarm2 = new javax.swing.JCheckBox();
        jCheckBoxCenterAlarm2 = new javax.swing.JCheckBox();
        jCheckBoxJpegEMailAlarm2 = new javax.swing.JCheckBox();
        jLabelLinkClient2 = new javax.swing.JLabel();
        jPanelAlarmOut2 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jListTraggerAlarmOut2 = new javax.swing.JList();
        jCheckBoxAlarmout2 = new javax.swing.JCheckBox();
        jPanelFoot2 = new javax.swing.JPanel();
        jButtonSave2 = new javax.swing.JButton();
        jButtonExit2 = new javax.swing.JButton();
        jSplitPaneAlarmIn = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTreeResource3 = new javax.swing.JTree();
        jPanelRight3 = new javax.swing.JPanel();
        jPanelHeader3 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jCheckBoxEnable3 = new javax.swing.JCheckBox();
        jScrollPane9 = new javax.swing.JScrollPane();
        jPanelAlarmPrecessMode3 = new javax.swing.JPanel();
        jPanelAlarmInInfo = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabelAlarmInInfo = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jTextFieldAlarmInName = new javax.swing.JTextField();
        jLabelAlarmerType = new javax.swing.JLabel();
        jLabelAlarmInName = new javax.swing.JLabel();
        jComboBoxAlerterType = new javax.swing.JComboBox();
        jLabelArmingTime3 = new javax.swing.JLabel();
        jComboBoxSetAlarmTime3 = new javax.swing.JComboBox<>();
        jButtonTemplate3 = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabelAlarmProcessMode3 = new javax.swing.JLabel();
        jPanelAlarmPrecessDetail3 = new javax.swing.JPanel();
        jPanelLinkageClient3 = new javax.swing.JPanel();
        jCheckBoxMonitorAlarm3 = new javax.swing.JCheckBox();
        jCheckBoxAudioAlarm3 = new javax.swing.JCheckBox();
        jCheckBoxCenterAlarm3 = new javax.swing.JCheckBox();
        jCheckBoxJpegEMailAlarm3 = new javax.swing.JCheckBox();
        jLabelLinkClient3 = new javax.swing.JLabel();
        jPanelRecordChannel3 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabelTriggerRecord3 = new javax.swing.JLabel();
        jScrollPaneTraggerRecord3 = new javax.swing.JScrollPane();
        jListTraggerRecord3 = new javax.swing.JList();
        jPanelAlarmOut3 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jCheckBoxAlarmout3 = new javax.swing.JCheckBox();
        jScrollPaneTraggerAlarmOut3 = new javax.swing.JScrollPane();
        jListTraggerAlarmOut3 = new javax.swing.JList();
        jPanelPTZ = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabelPTZLink = new javax.swing.JLabel();
        jPanelPTZDdetail = new javax.swing.JPanel();
        jRadioButtonCruise = new javax.swing.JRadioButton();
        jComboBoxCruise = new javax.swing.JComboBox();
        jRadioButtonPreset = new javax.swing.JRadioButton();
        jComboBoxPreset = new javax.swing.JComboBox();
        jRadioButtonTrack = new javax.swing.JRadioButton();
        jComboBoxTrack = new javax.swing.JComboBox();
        jCheckBoxPTZEnable = new javax.swing.JCheckBox();
        jPanelFoot3 = new javax.swing.JPanel();
        jButtonSave3 = new javax.swing.JButton();
        jButtonExit3 = new javax.swing.JButton();
        jSplitPaneAlarmOut = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTreeResource4 = new javax.swing.JTree();
        jPanelRight4 = new javax.swing.JPanel();
        jPanelAlarmOutInfo = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabelAlarmOutInfo = new javax.swing.JLabel();
        jPanelAlarmOutInfoDetail = new javax.swing.JPanel();
        jLabelAlarmOutName = new javax.swing.JLabel();
        jTextFieldAlarmOutName = new javax.swing.JTextField();
        jLabelAlarmDuration = new javax.swing.JLabel();
        jComboBoxAlarmOutDelay = new javax.swing.JComboBox<>();
        jLabelArmingTime4 = new javax.swing.JLabel();
        jComboBoxSetAlarmTime4 = new javax.swing.JComboBox<>();
        jButtonTemplate4 = new javax.swing.JButton();
        jPanelFoot4 = new javax.swing.JPanel();
        jButtonSave4 = new javax.swing.JButton();
        jButtonExit4 = new javax.swing.JButton();

        setBorder(null);
        setTitle("报警设置");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jTabbedPaneAlarm.setOpaque(true);
        jTabbedPaneAlarm.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneAlarmStateChanged(evt);
            }
        });

        jScrollPane1.setPreferredSize(new java.awt.Dimension(260, 322));

        jTreeResource1.setBackground(new java.awt.Color(64, 64, 64));
        jTreeResource1.setForeground(new java.awt.Color(255, 255, 255));
        jTreeResource1.setToolTipText("");
        jTreeResource1.setName("jTreeResource1"); // NOI18N
        jTreeResource1.setRootVisible(false);
        jTreeResource1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeResource1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTreeResource1);

        jSplitPaneMonitorAlarm.setLeftComponent(jScrollPane1);

        jPanelRight1.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRight1.setForeground(new java.awt.Color(255, 255, 255));
        jPanelRight1.setLayout(new java.awt.BorderLayout());

        jPanelHeader.setBackground(new java.awt.Color(64, 64, 64));
        jPanelHeader.setForeground(new java.awt.Color(255, 255, 255));

        jLabelAlarmType.setBackground(new java.awt.Color(64, 64, 64));
        jLabelAlarmType.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelAlarmType.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAlarmType.setText("  报警类型：");

        jComboBoxAlarmType.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxAlarmType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxAlarmTypeItemStateChanged(evt);
            }
        });

        jCheckBoxEnable.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxEnable.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxEnable.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxEnable.setText("启用");
        jCheckBoxEnable.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxEnableItemStateChanged(evt);
            }
        });

        jLabelArmingTime.setBackground(new java.awt.Color(64, 64, 64));
        jLabelArmingTime.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelArmingTime.setForeground(new java.awt.Color(255, 255, 255));
        jLabelArmingTime.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelArmingTime.setText("布防时间：");

        jComboBoxSetAlarmTime.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxSetAlarmTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "全天模板", "工作日模板", "添加模板" }));
        jComboBoxSetAlarmTime.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxSetAlarmTimeItemStateChanged(evt);
            }
        });

        jButtonTemplate.setBackground(new java.awt.Color(64, 64, 64));
        jButtonTemplate.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonTemplate.setForeground(new java.awt.Color(255, 255, 255));
        jButtonTemplate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/timetemplate.png"))); // NOI18N
        jButtonTemplate.setText("编辑模板");
        jButtonTemplate.setEnabled(false);
        jButtonTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTemplateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelHeaderLayout = new javax.swing.GroupLayout(jPanelHeader);
        jPanelHeader.setLayout(jPanelHeaderLayout);
        jPanelHeaderLayout.setHorizontalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelAlarmType, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxAlarmType, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxEnable, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelArmingTime, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxSetAlarmTime, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonTemplate, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelHeaderLayout.setVerticalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHeaderLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonTemplate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxSetAlarmTime, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelArmingTime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxEnable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxAlarmType, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelAlarmType, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanelRight1.add(jPanelHeader, java.awt.BorderLayout.PAGE_START);

        jPanelSetupAera.setBackground(new java.awt.Color(64, 64, 64));
        jPanelSetupAera.setForeground(new java.awt.Color(255, 255, 255));
        jPanelSetupAera.setLayout(new java.awt.BorderLayout());

        jPanelSetupAeraDetail.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel6.setBackground(new java.awt.Color(151, 151, 151));
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));

        jLabelArmingPara.setBackground(new java.awt.Color(151, 151, 151));
        jLabelArmingPara.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelArmingPara.setForeground(new java.awt.Color(255, 255, 255));
        jLabelArmingPara.setText("  布防参数");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelArmingPara, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(571, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabelArmingPara, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.add(jPanel6, java.awt.BorderLayout.PAGE_START);

        jPanel9.setBackground(new java.awt.Color(64, 64, 64));
        jPanel9.setForeground(new java.awt.Color(255, 255, 255));

        jLabelSensitive.setBackground(new java.awt.Color(64, 64, 64));
        jLabelSensitive.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelSensitive.setForeground(new java.awt.Color(255, 255, 255));
        jLabelSensitive.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelSensitive.setText("灵敏度：");

        jSliderSensitive.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderSensitiveStateChanged(evt);
            }
        });

        jTextFieldSensitive.setText("2");
        jTextFieldSensitive.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldSensitiveKeyTyped(evt);
            }
        });

        jPanelSetupParas.setBackground(new java.awt.Color(64, 64, 64));
        jPanelSetupParas.setForeground(new java.awt.Color(255, 255, 255));

        jLabelID.setBackground(new java.awt.Color(64, 64, 64));
        jLabelID.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelID.setForeground(new java.awt.Color(255, 255, 255));
        jLabelID.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelID.setText("警戒线ID:");

        jComboBoxID.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxID.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
        jComboBoxID.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxIDItemStateChanged(evt);
            }
        });

        jButtonPTZControl.setBackground(new java.awt.Color(64, 64, 64));
        jButtonPTZControl.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonPTZControl.setForeground(new java.awt.Color(255, 255, 255));
        jButtonPTZControl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptz.png"))); // NOI18N
        jButtonPTZControl.setText("云台控制");
        jButtonPTZControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPTZControlActionPerformed(evt);
            }
        });

        jLabelLineDirection.setBackground(new java.awt.Color(64, 64, 64));
        jLabelLineDirection.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelLineDirection.setForeground(new java.awt.Color(255, 255, 255));
        jLabelLineDirection.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLineDirection.setText("警戒线方向：");

        jComboBoxCrossDirection.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxCrossDirection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "双向（A <-> B）", "由左至右（A -> B）", "由右至左（B -> A）" }));

        jLabelPointNum.setBackground(new java.awt.Color(64, 64, 64));
        jLabelPointNum.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelPointNum.setForeground(new java.awt.Color(255, 255, 255));
        jLabelPointNum.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPointNum.setText("有效点数：");

        jComboBoxPointNum.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxPointNum.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "4" }));

        jLabelDuration.setBackground(new java.awt.Color(64, 64, 64));
        jLabelDuration.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelDuration.setForeground(new java.awt.Color(255, 255, 255));
        jLabelDuration.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDuration.setText("触发时间阈值：");

        jSpinnerDuration.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jSpinnerDuration.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        jSpinnerDuration.setValue(5);

        jLabelDurationUnit.setBackground(new java.awt.Color(64, 64, 64));
        jLabelDurationUnit.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelDurationUnit.setForeground(new java.awt.Color(255, 255, 255));
        jLabelDurationUnit.setText("秒");

        jLabelRate.setBackground(new java.awt.Color(64, 64, 64));
        jLabelRate.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelRate.setForeground(new java.awt.Color(255, 255, 255));
        jLabelRate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRate.setText("占 比：");

        jSpinnerRate.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jSpinnerRate.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        jSpinnerRate.setValue(5);

        jLabelRateUnit.setBackground(new java.awt.Color(64, 64, 64));
        jLabelRateUnit.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelRateUnit.setForeground(new java.awt.Color(255, 255, 255));
        jLabelRateUnit.setText("%");

        javax.swing.GroupLayout jPanelSetupParasLayout = new javax.swing.GroupLayout(jPanelSetupParas);
        jPanelSetupParas.setLayout(jPanelSetupParasLayout);
        jPanelSetupParasLayout.setHorizontalGroup(
            jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSetupParasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelPointNum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelLineDirection, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelSetupParasLayout.createSequentialGroup()
                        .addComponent(jComboBoxID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonPTZControl, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                    .addComponent(jComboBoxCrossDirection, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxPointNum, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(52, 52, 52)
                .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelDuration, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(jLabelRate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinnerRate, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinnerDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelDurationUnit, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                    .addComponent(jLabelRateUnit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelSetupParasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jSpinnerDuration, jSpinnerRate});

        jPanelSetupParasLayout.setVerticalGroup(
            jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSetupParasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSetupParasLayout.createSequentialGroup()
                        .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jSpinnerRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelRateUnit)
                            .addComponent(jLabelRate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinnerDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelDurationUnit))
                        .addContainerGap())
                    .addGroup(jPanelSetupParasLayout.createSequentialGroup()
                        .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelID)
                            .addComponent(jComboBoxID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonPTZControl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelPointNum)
                            .addComponent(jComboBoxPointNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelSetupParasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelLineDirection)
                            .addComponent(jComboBoxCrossDirection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelSensitive, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addComponent(jSliderSensitive, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldSensitive, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanelSetupParas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanelSetupParas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSliderSensitive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelSensitive, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldSensitive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel9, java.awt.BorderLayout.CENTER);

        jPanel7.setBackground(new java.awt.Color(151, 151, 151));
        jPanel7.setForeground(new java.awt.Color(255, 255, 255));

        jLabelAlarmProcessMode.setBackground(new java.awt.Color(151, 151, 151));
        jLabelAlarmProcessMode.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelAlarmProcessMode.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAlarmProcessMode.setText("  报警处理方式");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelAlarmProcessMode, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(573, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabelAlarmProcessMode, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.add(jPanel7, java.awt.BorderLayout.PAGE_END);

        jPanelSetupAeraDetail.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanelAlarmPrecessMode.setBackground(new java.awt.Color(64, 64, 64));
        jPanelAlarmPrecessMode.setForeground(new java.awt.Color(255, 255, 255));
        jPanelAlarmPrecessMode.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(64, 64, 64));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jPanelLinkageClient.setBackground(new java.awt.Color(64, 64, 64));
        jPanelLinkageClient.setForeground(new java.awt.Color(255, 255, 255));

        jCheckBoxMonitorAlarm.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxMonitorAlarm.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxMonitorAlarm.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxMonitorAlarm.setText("监视器上警告");

        jCheckBoxAudioAlarm.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxAudioAlarm.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxAudioAlarm.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxAudioAlarm.setText("声音警告");

        jCheckBoxCenterAlarm.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxCenterAlarm.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxCenterAlarm.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxCenterAlarm.setText("上传中心");

        jCheckBoxJpegEMailAlarm.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxJpegEMailAlarm.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxJpegEMailAlarm.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxJpegEMailAlarm.setText("抓图发送邮件");

        jCheckBoxPTZTrack.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxPTZTrack.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxPTZTrack.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxPTZTrack.setText("PTZ联动跟踪");

        jLabelLinkClient.setBackground(new java.awt.Color(64, 64, 64));
        jLabelLinkClient.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelLinkClient.setForeground(new java.awt.Color(255, 255, 255));
        jLabelLinkClient.setText("联动客户端：");

        javax.swing.GroupLayout jPanelLinkageClientLayout = new javax.swing.GroupLayout(jPanelLinkageClient);
        jPanelLinkageClient.setLayout(jPanelLinkageClientLayout);
        jPanelLinkageClientLayout.setHorizontalGroup(
            jPanelLinkageClientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinkageClientLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLinkageClientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxMonitorAlarm)
                    .addComponent(jCheckBoxAudioAlarm)
                    .addComponent(jCheckBoxCenterAlarm)
                    .addComponent(jCheckBoxJpegEMailAlarm)
                    .addComponent(jCheckBoxPTZTrack))
                .addContainerGap(52, Short.MAX_VALUE))
            .addGroup(jPanelLinkageClientLayout.createSequentialGroup()
                .addComponent(jLabelLinkClient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelLinkageClientLayout.setVerticalGroup(
            jPanelLinkageClientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinkageClientLayout.createSequentialGroup()
                .addComponent(jLabelLinkClient, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxMonitorAlarm)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxAudioAlarm)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxCenterAlarm)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxJpegEMailAlarm)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxPTZTrack)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelRecordChannel.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRecordChannel.setForeground(new java.awt.Color(255, 255, 255));
        jPanelRecordChannel.setLayout(new java.awt.BorderLayout());

        jPanel8.setBackground(new java.awt.Color(64, 64, 64));
        jPanel8.setForeground(new java.awt.Color(255, 255, 255));

        jLabelTriggerRecord.setBackground(new java.awt.Color(64, 64, 64));
        jLabelTriggerRecord.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelTriggerRecord.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTriggerRecord.setText("触发录像通道：");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTriggerRecord, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTriggerRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanelRecordChannel.add(jPanel8, java.awt.BorderLayout.PAGE_START);

        jListTraggerRecord.setBackground(new java.awt.Color(64, 64, 64));
        jListTraggerRecord.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jListTraggerRecord.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPane6.setViewportView(jListTraggerRecord);

        jPanelRecordChannel.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        jPanelAlarmOut.setBackground(new java.awt.Color(64, 64, 64));
        jPanelAlarmOut.setForeground(new java.awt.Color(255, 255, 255));
        jPanelAlarmOut.setPreferredSize(new java.awt.Dimension(309, 402));
        jPanelAlarmOut.setLayout(new java.awt.BorderLayout());

        jPanel10.setBackground(new java.awt.Color(64, 64, 64));
        jPanel10.setForeground(new java.awt.Color(255, 255, 255));

        jCheckBoxAlarmout.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxAlarmout.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxAlarmout.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxAlarmout.setText("触发报警输出");
        jCheckBoxAlarmout.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxAlarmoutItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jCheckBoxAlarmout, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jCheckBoxAlarmout, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanelAlarmOut.add(jPanel10, java.awt.BorderLayout.PAGE_START);

        jListTraggerAlarmOut.setBackground(new java.awt.Color(64, 64, 64));
        jListTraggerAlarmOut.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jListTraggerAlarmOut.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPane5.setViewportView(jListTraggerAlarmOut);

        jPanelAlarmOut.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanelRecordChannel, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelAlarmOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jPanelLinkageClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelRecordChannel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelAlarmOut, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                    .addComponent(jPanelLinkageClient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanelAlarmOut, jPanelRecordChannel});

        jPanelAlarmPrecessMode.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanelSetupAeraDetail.add(jPanelAlarmPrecessMode, java.awt.BorderLayout.CENTER);

        jPanelSetupAera.add(jPanelSetupAeraDetail, java.awt.BorderLayout.CENTER);

        jPanelPlay.setBackground(new java.awt.Color(64, 64, 64));
        jPanelPlay.setForeground(new java.awt.Color(255, 255, 255));

        panelPlay.setBackground(new java.awt.Color(102, 102, 102));
        panelPlay.setPreferredSize(new java.awt.Dimension(352, 288));
        panelPlay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelPlayMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panelPlayMouseReleased(evt);
            }
        });
        panelPlay.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelPlayMouseDragged(evt);
            }
        });

        javax.swing.GroupLayout panelPlayLayout = new javax.swing.GroupLayout(panelPlay);
        panelPlay.setLayout(panelPlayLayout);
        panelPlayLayout.setHorizontalGroup(
            panelPlayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 352, Short.MAX_VALUE)
        );
        panelPlayLayout.setVerticalGroup(
            panelPlayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 288, Short.MAX_VALUE)
        );

        jButtonFullScreen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/fullscreen.png"))); // NOI18N
        jButtonFullScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFullScreenActionPerformed(evt);
            }
        });

        jButtonDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/clear.png"))); // NOI18N
        jButtonDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPlayLayout = new javax.swing.GroupLayout(jPanelPlay);
        jPanelPlay.setLayout(jPanelPlayLayout);
        jPanelPlayLayout.setHorizontalGroup(
            jPanelPlayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPlayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPlay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPlayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonFullScreen, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelPlayLayout.setVerticalGroup(
            jPanelPlayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPlayLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPlayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPlayLayout.createSequentialGroup()
                        .addComponent(jButtonFullScreen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDel))
                    .addComponent(panelPlay, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanelSetupAera.add(jPanelPlay, java.awt.BorderLayout.EAST);

        jScrollPane7.setViewportView(jPanelSetupAera);

        jPanelRight1.add(jScrollPane7, java.awt.BorderLayout.CENTER);

        jPanelFoot1.setBackground(new java.awt.Color(151, 151, 151));
        jPanelFoot1.setForeground(new java.awt.Color(255, 255, 255));

        jButtonSave.setBackground(new java.awt.Color(64, 64, 64));
        jButtonSave.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSave.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok.png"))); // NOI18N
        jButtonSave.setText("保存");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonExit.setBackground(new java.awt.Color(64, 64, 64));
        jButtonExit.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonExit.setForeground(new java.awt.Color(255, 255, 255));
        jButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cancel.png"))); // NOI18N
        jButtonExit.setText("退出");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFoot1Layout = new javax.swing.GroupLayout(jPanelFoot1);
        jPanelFoot1.setLayout(jPanelFoot1Layout);
        jPanelFoot1Layout.setHorizontalGroup(
            jPanelFoot1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFoot1Layout.createSequentialGroup()
                .addContainerGap(1302, Short.MAX_VALUE)
                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanelFoot1Layout.setVerticalGroup(
            jPanelFoot1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFoot1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelFoot1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSave)
                    .addComponent(jButtonExit))
                .addContainerGap())
        );

        jPanelRight1.add(jPanelFoot1, java.awt.BorderLayout.PAGE_END);

        jSplitPaneMonitorAlarm.setRightComponent(jPanelRight1);

        jTabbedPaneAlarm.addTab(" 监控点报警  ", new javax.swing.ImageIcon(getClass().getResource("/jyms/image/alarm2.png")), jSplitPaneMonitorAlarm); // NOI18N

        jScrollPane2.setPreferredSize(new java.awt.Dimension(140, 322));

        jTreeResource2.setBackground(new java.awt.Color(64, 64, 64));
        jTreeResource2.setForeground(new java.awt.Color(255, 255, 255));
        jTreeResource2.setName("jTreeResource2"); // NOI18N
        jTreeResource2.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeResource2ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jTreeResource2);

        jSplitPaneException.setLeftComponent(jScrollPane2);

        jPanelRight2.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRight2.setForeground(new java.awt.Color(255, 255, 255));
        jPanelRight2.setLayout(new java.awt.BorderLayout());

        jPanelHeader2.setBackground(new java.awt.Color(64, 64, 64));
        jPanelHeader2.setForeground(new java.awt.Color(255, 255, 255));

        jLabelExceptionType.setBackground(new java.awt.Color(64, 64, 64));
        jLabelExceptionType.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelExceptionType.setForeground(new java.awt.Color(255, 255, 255));
        jLabelExceptionType.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelExceptionType.setText("  异常类型：");

        jComboBoxExceptionType.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxExceptionType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxExceptionTypeItemStateChanged(evt);
            }
        });

        jCheckBoxEnable2.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxEnable2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxEnable2.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxEnable2.setText("启用");
        jCheckBoxEnable2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxEnable2ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelHeader2Layout = new javax.swing.GroupLayout(jPanelHeader2);
        jPanelHeader2.setLayout(jPanelHeader2Layout);
        jPanelHeader2Layout.setHorizontalGroup(
            jPanelHeader2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHeader2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelExceptionType, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxExceptionType, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jCheckBoxEnable2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelHeader2Layout.setVerticalGroup(
            jPanelHeader2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHeader2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanelHeader2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jCheckBoxEnable2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelExceptionType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxExceptionType))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelRight2.add(jPanelHeader2, java.awt.BorderLayout.PAGE_START);

        jPanelAlarmPrecessMode2.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(151, 151, 151));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jLabelAlarmProcessMode2.setBackground(new java.awt.Color(64, 64, 64));
        jLabelAlarmProcessMode2.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelAlarmProcessMode2.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAlarmProcessMode2.setText("  报警处理方式");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelAlarmProcessMode2, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(339, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelAlarmProcessMode2, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
        );

        jPanelAlarmPrecessMode2.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel11.setBackground(new java.awt.Color(64, 64, 64));
        jPanel11.setForeground(new java.awt.Color(255, 255, 255));

        jPanelLinkageClient2.setBackground(new java.awt.Color(64, 64, 64));
        jPanelLinkageClient2.setForeground(new java.awt.Color(255, 255, 255));

        jCheckBoxMonitorAlarm2.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxMonitorAlarm2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxMonitorAlarm2.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxMonitorAlarm2.setText("监视器上警告");

        jCheckBoxAudioAlarm2.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxAudioAlarm2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxAudioAlarm2.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxAudioAlarm2.setText("声音警告");

        jCheckBoxCenterAlarm2.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxCenterAlarm2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxCenterAlarm2.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxCenterAlarm2.setText("上传中心");

        jCheckBoxJpegEMailAlarm2.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxJpegEMailAlarm2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxJpegEMailAlarm2.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxJpegEMailAlarm2.setText("抓图发送邮件");

        jLabelLinkClient2.setBackground(new java.awt.Color(64, 64, 64));
        jLabelLinkClient2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelLinkClient2.setForeground(new java.awt.Color(255, 255, 255));
        jLabelLinkClient2.setText("联动客户端：");

        javax.swing.GroupLayout jPanelLinkageClient2Layout = new javax.swing.GroupLayout(jPanelLinkageClient2);
        jPanelLinkageClient2.setLayout(jPanelLinkageClient2Layout);
        jPanelLinkageClient2Layout.setHorizontalGroup(
            jPanelLinkageClient2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinkageClient2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLinkageClient2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxMonitorAlarm2)
                    .addComponent(jCheckBoxAudioAlarm2)
                    .addComponent(jCheckBoxCenterAlarm2)
                    .addComponent(jCheckBoxJpegEMailAlarm2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelLinkageClient2Layout.createSequentialGroup()
                .addComponent(jLabelLinkClient2, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 82, Short.MAX_VALUE))
        );
        jPanelLinkageClient2Layout.setVerticalGroup(
            jPanelLinkageClient2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinkageClient2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabelLinkClient2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxMonitorAlarm2)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxAudioAlarm2)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxCenterAlarm2)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxJpegEMailAlarm2)
                .addContainerGap(147, Short.MAX_VALUE))
        );

        jPanelAlarmOut2.setBackground(new java.awt.Color(64, 64, 64));
        jPanelAlarmOut2.setForeground(new java.awt.Color(255, 255, 255));

        jListTraggerAlarmOut2.setBackground(new java.awt.Color(64, 64, 64));
        jListTraggerAlarmOut2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jListTraggerAlarmOut2.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPane8.setViewportView(jListTraggerAlarmOut2);

        jCheckBoxAlarmout2.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxAlarmout2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxAlarmout2.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxAlarmout2.setText("触发报警输出");
        jCheckBoxAlarmout2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxAlarmout2ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelAlarmOut2Layout = new javax.swing.GroupLayout(jPanelAlarmOut2);
        jPanelAlarmOut2.setLayout(jPanelAlarmOut2Layout);
        jPanelAlarmOut2Layout.setHorizontalGroup(
            jPanelAlarmOut2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
            .addComponent(jCheckBoxAlarmout2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelAlarmOut2Layout.setVerticalGroup(
            jPanelAlarmOut2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAlarmOut2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxAlarmout2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jPanelAlarmOut2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jPanelLinkageClient2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelAlarmOut2, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelLinkageClient2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel11Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanelAlarmOut2, jPanelLinkageClient2});

        jPanelAlarmPrecessMode2.add(jPanel11, java.awt.BorderLayout.CENTER);

        jScrollPane10.setViewportView(jPanelAlarmPrecessMode2);

        jPanelRight2.add(jScrollPane10, java.awt.BorderLayout.CENTER);

        jPanelFoot2.setBackground(new java.awt.Color(151, 151, 151));
        jPanelFoot2.setForeground(new java.awt.Color(255, 255, 255));

        jButtonSave2.setBackground(new java.awt.Color(64, 64, 64));
        jButtonSave2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSave2.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSave2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok.png"))); // NOI18N
        jButtonSave2.setText("保存");
        jButtonSave2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSave2ActionPerformed(evt);
            }
        });

        jButtonExit2.setBackground(new java.awt.Color(64, 64, 64));
        jButtonExit2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonExit2.setForeground(new java.awt.Color(255, 255, 255));
        jButtonExit2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cancel.png"))); // NOI18N
        jButtonExit2.setText("退出");
        jButtonExit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExit2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFoot2Layout = new javax.swing.GroupLayout(jPanelFoot2);
        jPanelFoot2.setLayout(jPanelFoot2Layout);
        jPanelFoot2Layout.setHorizontalGroup(
            jPanelFoot2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFoot2Layout.createSequentialGroup()
                .addContainerGap(315, Short.MAX_VALUE)
                .addComponent(jButtonSave2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonExit2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78))
        );
        jPanelFoot2Layout.setVerticalGroup(
            jPanelFoot2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFoot2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelFoot2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonExit2)
                    .addComponent(jButtonSave2))
                .addContainerGap())
        );

        jPanelRight2.add(jPanelFoot2, java.awt.BorderLayout.PAGE_END);

        jSplitPaneException.setRightComponent(jPanelRight2);

        jTabbedPaneAlarm.addTab(" 异常报警  ", new javax.swing.ImageIcon(getClass().getResource("/jyms/image/alarmexception.png")), jSplitPaneException); // NOI18N

        jScrollPane3.setPreferredSize(new java.awt.Dimension(260, 322));

        jTreeResource3.setBackground(new java.awt.Color(64, 64, 64));
        jTreeResource3.setForeground(new java.awt.Color(255, 255, 255));
        jTreeResource3.setName("jTreeResource3"); // NOI18N
        jTreeResource3.setRootVisible(false);
        jTreeResource3.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeResource3ValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jTreeResource3);

        jSplitPaneAlarmIn.setLeftComponent(jScrollPane3);

        jPanelRight3.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRight3.setForeground(new java.awt.Color(255, 255, 255));
        jPanelRight3.setLayout(new java.awt.BorderLayout());

        jPanelHeader3.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(64, 64, 64));
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));

        jCheckBoxEnable3.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxEnable3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxEnable3.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxEnable3.setText("启用");
        jCheckBoxEnable3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxEnable3ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxEnable3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(807, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jCheckBoxEnable3)
                .addContainerGap())
        );

        jPanelHeader3.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanelRight3.add(jPanelHeader3, java.awt.BorderLayout.PAGE_START);

        jPanelAlarmPrecessMode3.setBackground(new java.awt.Color(64, 64, 64));
        jPanelAlarmPrecessMode3.setForeground(new java.awt.Color(255, 255, 255));
        jPanelAlarmPrecessMode3.setLayout(new java.awt.BorderLayout());

        jPanelAlarmInInfo.setLayout(new java.awt.BorderLayout());

        jPanel12.setBackground(new java.awt.Color(151, 151, 151));
        jPanel12.setForeground(new java.awt.Color(255, 255, 255));

        jLabelAlarmInInfo.setBackground(new java.awt.Color(151, 151, 151));
        jLabelAlarmInInfo.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelAlarmInInfo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAlarmInInfo.setText("  报警输入信息");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelAlarmInInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(864, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelAlarmInInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
        );

        jPanelAlarmInInfo.add(jPanel12, java.awt.BorderLayout.PAGE_START);

        jPanel13.setBackground(new java.awt.Color(64, 64, 64));
        jPanel13.setForeground(new java.awt.Color(255, 255, 255));

        jTextFieldAlarmInName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jTextFieldAlarmInName.setText("           ");

        jLabelAlarmerType.setBackground(new java.awt.Color(64, 64, 64));
        jLabelAlarmerType.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelAlarmerType.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAlarmerType.setText("报警器状态：");

        jLabelAlarmInName.setBackground(new java.awt.Color(64, 64, 64));
        jLabelAlarmInName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelAlarmInName.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAlarmInName.setText("报警输入名称：");

        jComboBoxAlerterType.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxAlerterType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "常开", "常闭" }));

        jLabelArmingTime3.setBackground(new java.awt.Color(64, 64, 64));
        jLabelArmingTime3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelArmingTime3.setForeground(new java.awt.Color(255, 255, 255));
        jLabelArmingTime3.setText("布防时间：");

        jComboBoxSetAlarmTime3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxSetAlarmTime3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "全天模板", "工作日模板", "添加模板" }));
        jComboBoxSetAlarmTime3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxSetAlarmTime3ItemStateChanged(evt);
            }
        });

        jButtonTemplate3.setBackground(new java.awt.Color(64, 64, 64));
        jButtonTemplate3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonTemplate3.setForeground(new java.awt.Color(255, 255, 255));
        jButtonTemplate3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/timetemplate.png"))); // NOI18N
        jButtonTemplate3.setText("编辑模板");
        jButtonTemplate3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTemplate3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelAlarmInName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelAlarmerType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBoxAlerterType, 0, 214, Short.MAX_VALUE)
                    .addComponent(jTextFieldAlarmInName))
                .addGap(25, 25, 25)
                .addComponent(jLabelArmingTime3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxSetAlarmTime3, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonTemplate3, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(299, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelArmingTime3)
                        .addComponent(jComboBoxSetAlarmTime3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonTemplate3))
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelAlarmInName)
                        .addComponent(jTextFieldAlarmInName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelAlarmerType)
                    .addComponent(jComboBoxAlerterType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelAlarmInInfo.add(jPanel13, java.awt.BorderLayout.CENTER);

        jPanelAlarmPrecessMode3.add(jPanelAlarmInInfo, java.awt.BorderLayout.PAGE_START);

        jPanel18.setLayout(new java.awt.BorderLayout());

        jPanel14.setBackground(new java.awt.Color(151, 151, 151));
        jPanel14.setForeground(new java.awt.Color(255, 255, 255));

        jLabelAlarmProcessMode3.setBackground(new java.awt.Color(151, 151, 151));
        jLabelAlarmProcessMode3.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelAlarmProcessMode3.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAlarmProcessMode3.setText("  报警处理方式");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelAlarmProcessMode3, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(871, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelAlarmProcessMode3, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
        );

        jPanel18.add(jPanel14, java.awt.BorderLayout.PAGE_START);

        jPanelAlarmPrecessDetail3.setBackground(new java.awt.Color(64, 64, 64));
        jPanelAlarmPrecessDetail3.setForeground(new java.awt.Color(255, 255, 255));
        jPanelAlarmPrecessDetail3.setPreferredSize(new java.awt.Dimension(983, 303));

        jPanelLinkageClient3.setBackground(new java.awt.Color(64, 64, 64));
        jPanelLinkageClient3.setForeground(new java.awt.Color(255, 255, 255));

        jCheckBoxMonitorAlarm3.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxMonitorAlarm3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxMonitorAlarm3.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxMonitorAlarm3.setText("监视器上警告");

        jCheckBoxAudioAlarm3.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxAudioAlarm3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxAudioAlarm3.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxAudioAlarm3.setText("声音警告");

        jCheckBoxCenterAlarm3.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxCenterAlarm3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxCenterAlarm3.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxCenterAlarm3.setText("上传中心");

        jCheckBoxJpegEMailAlarm3.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxJpegEMailAlarm3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxJpegEMailAlarm3.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxJpegEMailAlarm3.setText("抓图发送邮件");

        jLabelLinkClient3.setBackground(new java.awt.Color(64, 64, 64));
        jLabelLinkClient3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelLinkClient3.setForeground(new java.awt.Color(255, 255, 255));
        jLabelLinkClient3.setText("   联动客户端");

        javax.swing.GroupLayout jPanelLinkageClient3Layout = new javax.swing.GroupLayout(jPanelLinkageClient3);
        jPanelLinkageClient3.setLayout(jPanelLinkageClient3Layout);
        jPanelLinkageClient3Layout.setHorizontalGroup(
            jPanelLinkageClient3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinkageClient3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLinkageClient3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxMonitorAlarm3)
                    .addComponent(jCheckBoxAudioAlarm3)
                    .addComponent(jCheckBoxCenterAlarm3)
                    .addComponent(jCheckBoxJpegEMailAlarm3))
                .addContainerGap(125, Short.MAX_VALUE))
            .addGroup(jPanelLinkageClient3Layout.createSequentialGroup()
                .addComponent(jLabelLinkClient3, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelLinkageClient3Layout.setVerticalGroup(
            jPanelLinkageClient3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinkageClient3Layout.createSequentialGroup()
                .addComponent(jLabelLinkClient3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxMonitorAlarm3)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxAudioAlarm3)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxCenterAlarm3)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxJpegEMailAlarm3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelRecordChannel3.setLayout(new java.awt.BorderLayout());

        jPanel17.setBackground(new java.awt.Color(64, 64, 64));
        jPanel17.setForeground(new java.awt.Color(255, 255, 255));

        jLabelTriggerRecord3.setBackground(new java.awt.Color(64, 64, 64));
        jLabelTriggerRecord3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelTriggerRecord3.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTriggerRecord3.setText("触发录像通道：");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTriggerRecord3, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTriggerRecord3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanelRecordChannel3.add(jPanel17, java.awt.BorderLayout.PAGE_START);

        jListTraggerRecord3.setBackground(new java.awt.Color(64, 64, 64));
        jListTraggerRecord3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jListTraggerRecord3.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPaneTraggerRecord3.setViewportView(jListTraggerRecord3);

        jPanelRecordChannel3.add(jScrollPaneTraggerRecord3, java.awt.BorderLayout.CENTER);

        jPanelAlarmOut3.setLayout(new java.awt.BorderLayout());

        jPanel16.setBackground(new java.awt.Color(64, 64, 64));
        jPanel16.setForeground(new java.awt.Color(255, 255, 255));

        jCheckBoxAlarmout3.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxAlarmout3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxAlarmout3.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxAlarmout3.setText("触发报警输出");
        jCheckBoxAlarmout3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxAlarmout3ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jCheckBoxAlarmout3, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jCheckBoxAlarmout3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelAlarmOut3.add(jPanel16, java.awt.BorderLayout.PAGE_START);

        jListTraggerAlarmOut3.setBackground(new java.awt.Color(64, 64, 64));
        jListTraggerAlarmOut3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jListTraggerAlarmOut3.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPaneTraggerAlarmOut3.setViewportView(jListTraggerAlarmOut3);

        jPanelAlarmOut3.add(jScrollPaneTraggerAlarmOut3, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanelAlarmPrecessDetail3Layout = new javax.swing.GroupLayout(jPanelAlarmPrecessDetail3);
        jPanelAlarmPrecessDetail3.setLayout(jPanelAlarmPrecessDetail3Layout);
        jPanelAlarmPrecessDetail3Layout.setHorizontalGroup(
            jPanelAlarmPrecessDetail3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAlarmPrecessDetail3Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jPanelRecordChannel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelAlarmOut3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jPanelLinkageClient3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(189, Short.MAX_VALUE))
        );
        jPanelAlarmPrecessDetail3Layout.setVerticalGroup(
            jPanelAlarmPrecessDetail3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAlarmPrecessDetail3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAlarmPrecessDetail3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelAlarmOut3, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelLinkageClient3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelRecordChannel3, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel18.add(jPanelAlarmPrecessDetail3, java.awt.BorderLayout.CENTER);

        jPanelPTZ.setLayout(new java.awt.BorderLayout());

        jPanel15.setBackground(new java.awt.Color(151, 151, 151));
        jPanel15.setForeground(new java.awt.Color(255, 255, 255));

        jLabelPTZLink.setBackground(new java.awt.Color(151, 151, 151));
        jLabelPTZLink.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelPTZLink.setForeground(new java.awt.Color(255, 255, 255));
        jLabelPTZLink.setText("  PTZ联动：");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPTZLink, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(953, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelPTZLink, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanelPTZ.add(jPanel15, java.awt.BorderLayout.PAGE_START);

        jPanelPTZDdetail.setBackground(new java.awt.Color(64, 64, 64));
        jPanelPTZDdetail.setForeground(new java.awt.Color(255, 255, 255));

        jRadioButtonCruise.setBackground(new java.awt.Color(64, 64, 64));
        buttonGroup1.add(jRadioButtonCruise);
        jRadioButtonCruise.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonCruise.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButtonCruise.setText("巡航");
        jRadioButtonCruise.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jComboBoxCruise.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jRadioButtonPreset.setBackground(new java.awt.Color(64, 64, 64));
        buttonGroup1.add(jRadioButtonPreset);
        jRadioButtonPreset.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonPreset.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButtonPreset.setText("预置点");
        jRadioButtonPreset.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jComboBoxPreset.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jRadioButtonTrack.setBackground(new java.awt.Color(64, 64, 64));
        buttonGroup1.add(jRadioButtonTrack);
        jRadioButtonTrack.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonTrack.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButtonTrack.setText("轨迹");
        jRadioButtonTrack.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jComboBoxTrack.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxTrack.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4" }));

        jCheckBoxPTZEnable.setBackground(new java.awt.Color(64, 64, 64));
        jCheckBoxPTZEnable.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jCheckBoxPTZEnable.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxPTZEnable.setText("启用云台");
        jCheckBoxPTZEnable.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxPTZEnableItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelPTZDdetailLayout = new javax.swing.GroupLayout(jPanelPTZDdetail);
        jPanelPTZDdetail.setLayout(jPanelPTZDdetailLayout);
        jPanelPTZDdetailLayout.setHorizontalGroup(
            jPanelPTZDdetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPTZDdetailLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxPTZEnable, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonCruise, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxCruise, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPreset, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxPreset, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jRadioButtonTrack, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxTrack, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(389, Short.MAX_VALUE))
        );
        jPanelPTZDdetailLayout.setVerticalGroup(
            jPanelPTZDdetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPTZDdetailLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(jPanelPTZDdetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jCheckBoxPTZEnable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonCruise, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonPreset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonTrack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxCruise)
                    .addComponent(jComboBoxPreset)
                    .addComponent(jComboBoxTrack))
                .addContainerGap())
        );

        jPanelPTZ.add(jPanelPTZDdetail, java.awt.BorderLayout.CENTER);

        jPanel18.add(jPanelPTZ, java.awt.BorderLayout.PAGE_END);

        jPanelAlarmPrecessMode3.add(jPanel18, java.awt.BorderLayout.CENTER);

        jScrollPane9.setViewportView(jPanelAlarmPrecessMode3);

        jPanelRight3.add(jScrollPane9, java.awt.BorderLayout.CENTER);

        jPanelFoot3.setBackground(new java.awt.Color(151, 151, 151));
        jPanelFoot3.setForeground(new java.awt.Color(255, 255, 255));

        jButtonSave3.setBackground(new java.awt.Color(64, 64, 64));
        jButtonSave3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSave3.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSave3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok.png"))); // NOI18N
        jButtonSave3.setText("保存");
        jButtonSave3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSave3ActionPerformed(evt);
            }
        });

        jButtonExit3.setBackground(new java.awt.Color(64, 64, 64));
        jButtonExit3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonExit3.setForeground(new java.awt.Color(255, 255, 255));
        jButtonExit3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cancel.png"))); // NOI18N
        jButtonExit3.setText("退出");
        jButtonExit3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExit3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFoot3Layout = new javax.swing.GroupLayout(jPanelFoot3);
        jPanelFoot3.setLayout(jPanelFoot3Layout);
        jPanelFoot3Layout.setHorizontalGroup(
            jPanelFoot3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFoot3Layout.createSequentialGroup()
                .addContainerGap(641, Short.MAX_VALUE)
                .addComponent(jButtonSave3, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExit3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64))
        );
        jPanelFoot3Layout.setVerticalGroup(
            jPanelFoot3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFoot3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFoot3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonExit3)
                    .addComponent(jButtonSave3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelRight3.add(jPanelFoot3, java.awt.BorderLayout.PAGE_END);

        jSplitPaneAlarmIn.setRightComponent(jPanelRight3);

        jTabbedPaneAlarm.addTab(" 报警输入  ", new javax.swing.ImageIcon(getClass().getResource("/jyms/image/alarmin.png")), jSplitPaneAlarmIn); // NOI18N

        jScrollPane4.setPreferredSize(new java.awt.Dimension(140, 322));

        jTreeResource4.setBackground(new java.awt.Color(64, 64, 64));
        jTreeResource4.setForeground(new java.awt.Color(255, 255, 255));
        jTreeResource4.setName("jTreeResource4"); // NOI18N
        jTreeResource4.setRootVisible(false);
        jTreeResource4.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeResource4ValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jTreeResource4);

        jSplitPaneAlarmOut.setLeftComponent(jScrollPane4);

        jPanelRight4.setLayout(new java.awt.BorderLayout());

        jPanelAlarmOutInfo.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(151, 151, 151));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jLabelAlarmOutInfo.setBackground(new java.awt.Color(151, 151, 151));
        jLabelAlarmOutInfo.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelAlarmOutInfo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAlarmOutInfo.setText("  报警输出信息");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelAlarmOutInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(861, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelAlarmOutInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanelAlarmOutInfo.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanelAlarmOutInfoDetail.setBackground(new java.awt.Color(64, 64, 64));
        jPanelAlarmOutInfoDetail.setForeground(new java.awt.Color(255, 255, 255));

        jLabelAlarmOutName.setBackground(new java.awt.Color(64, 64, 64));
        jLabelAlarmOutName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelAlarmOutName.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAlarmOutName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelAlarmOutName.setText("报警输出名称：");

        jTextFieldAlarmOutName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelAlarmDuration.setBackground(new java.awt.Color(64, 64, 64));
        jLabelAlarmDuration.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelAlarmDuration.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAlarmDuration.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelAlarmDuration.setText("报警维持时间：");

        jComboBoxAlarmOutDelay.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxAlarmOutDelay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5秒", "10秒", "30秒", "1分钟", "2分钟", "5分钟", "10分钟", "手动（需手动关闭）" }));
        jComboBoxAlarmOutDelay.setSelectedIndex(5);

        jLabelArmingTime4.setBackground(new java.awt.Color(64, 64, 64));
        jLabelArmingTime4.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelArmingTime4.setForeground(new java.awt.Color(255, 255, 255));
        jLabelArmingTime4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelArmingTime4.setText("布防时间：");

        jComboBoxSetAlarmTime4.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxSetAlarmTime4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "全天模板", "工作日模板", "添加模板" }));
        jComboBoxSetAlarmTime4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxSetAlarmTime4ItemStateChanged(evt);
            }
        });

        jButtonTemplate4.setBackground(new java.awt.Color(64, 64, 64));
        jButtonTemplate4.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonTemplate4.setForeground(new java.awt.Color(255, 255, 255));
        jButtonTemplate4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/timetemplate.png"))); // NOI18N
        jButtonTemplate4.setText("编辑模板");
        jButtonTemplate4.setEnabled(false);
        jButtonTemplate4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTemplate4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelAlarmOutInfoDetailLayout = new javax.swing.GroupLayout(jPanelAlarmOutInfoDetail);
        jPanelAlarmOutInfoDetail.setLayout(jPanelAlarmOutInfoDetailLayout);
        jPanelAlarmOutInfoDetailLayout.setHorizontalGroup(
            jPanelAlarmOutInfoDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAlarmOutInfoDetailLayout.createSequentialGroup()
                .addGap(220, 220, 220)
                .addGroup(jPanelAlarmOutInfoDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldAlarmOutName)
                    .addComponent(jComboBoxAlarmOutDelay, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxSetAlarmTime4, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButtonTemplate4, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(501, Short.MAX_VALUE))
            .addGroup(jPanelAlarmOutInfoDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelAlarmOutInfoDetailLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanelAlarmOutInfoDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabelArmingTime4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelAlarmDuration, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                        .addComponent(jLabelAlarmOutName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap(942, Short.MAX_VALUE)))
        );
        jPanelAlarmOutInfoDetailLayout.setVerticalGroup(
            jPanelAlarmOutInfoDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAlarmOutInfoDetailLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldAlarmOutName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxAlarmOutDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelAlarmOutInfoDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxSetAlarmTime4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTemplate4))
                .addContainerGap(699, Short.MAX_VALUE))
            .addGroup(jPanelAlarmOutInfoDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelAlarmOutInfoDetailLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabelAlarmOutName)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabelAlarmDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabelArmingTime4)
                    .addContainerGap(702, Short.MAX_VALUE)))
        );

        jPanelAlarmOutInfoDetailLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabelAlarmOutName, jTextFieldAlarmOutName});

        jPanelAlarmOutInfoDetailLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBoxAlarmOutDelay, jLabelAlarmDuration});

        jPanelAlarmOutInfoDetailLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBoxSetAlarmTime4, jLabelArmingTime4});

        jPanelAlarmOutInfo.add(jPanelAlarmOutInfoDetail, java.awt.BorderLayout.CENTER);

        jPanelRight4.add(jPanelAlarmOutInfo, java.awt.BorderLayout.CENTER);

        jPanelFoot4.setBackground(new java.awt.Color(151, 151, 151));
        jPanelFoot4.setForeground(new java.awt.Color(255, 255, 255));

        jButtonSave4.setBackground(new java.awt.Color(64, 64, 64));
        jButtonSave4.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSave4.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSave4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok.png"))); // NOI18N
        jButtonSave4.setText("保存");
        jButtonSave4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSave4ActionPerformed(evt);
            }
        });

        jButtonExit4.setBackground(new java.awt.Color(64, 64, 64));
        jButtonExit4.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonExit4.setForeground(new java.awt.Color(255, 255, 255));
        jButtonExit4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cancel.png"))); // NOI18N
        jButtonExit4.setText("退出");
        jButtonExit4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExit4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFoot4Layout = new javax.swing.GroupLayout(jPanelFoot4);
        jPanelFoot4.setLayout(jPanelFoot4Layout);
        jPanelFoot4Layout.setHorizontalGroup(
            jPanelFoot4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFoot4Layout.createSequentialGroup()
                .addContainerGap(877, Short.MAX_VALUE)
                .addComponent(jButtonSave4, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExit4, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
        );
        jPanelFoot4Layout.setVerticalGroup(
            jPanelFoot4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFoot4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFoot4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSave4)
                    .addComponent(jButtonExit4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelRight4.add(jPanelFoot4, java.awt.BorderLayout.PAGE_END);

        jSplitPaneAlarmOut.setRightComponent(jPanelRight4);

        jTabbedPaneAlarm.addTab(" 报警输出  ", new javax.swing.ImageIcon(getClass().getResource("/jyms/image/alarmout.png")), jSplitPaneAlarmOut); // NOI18N

        getContentPane().add(jTabbedPaneAlarm, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTreeResource1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeResource1ValueChanged
        // TODO add your handling code here:
        treeNodeValueChanged(jTreeResource1,evt);
    }//GEN-LAST:event_jTreeResource1ValueChanged

    private void jComboBoxAlarmTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxAlarmTypeItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED)
        {
            if (bFirstSelect)  return;
            refreshMonitorPointAlarm();
        }
    }//GEN-LAST:event_jComboBoxAlarmTypeItemStateChanged

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        // TODO add your handling code here:
        if (sAnotherName.equals("")) return;
        //        if (!jCheckBoxEnable.isSelected()) return;

        if (checkSensitive() < 0) return;//灵敏度检查

        String AlarmType = (String)jComboBoxAlarmType.getSelectedItem();//报警器类型
        if (AlarmType.equals(sAlarmTypes[0])) setMotionDetectAlarm();//"移动侦测"
        else if(AlarmType.equals(sAlarmTypes[1])) setVideoInLostAlarm();//"视频丢失"
        else if(AlarmType.equals(sAlarmTypes[2])) setHideAlarm();//"遮挡报警"
        else if(AlarmType.equals(sAlarmTypes[3])) setStrTreversePlane();//"越界侦测"
        else if(AlarmType.equals(sAlarmTypes[4])) setStruFieldDetecion();//"区域入侵侦测"
        else {}
//        switch (AlarmType){
//            case sAlarmTypes[0]://"移动侦测"
//                setMotionDetectAlarm();
//                break;
//            case "视频丢失"://
//                setVideoInLostAlarm();
//                break;
//            case "遮挡报警"://
//                setHideAlarm();
//                break;
//            case "越界侦测"://
//                setStrTreversePlane();
//                break;
//            case "区域入侵侦测"://
//                setStruFieldDetecion();
//                break;
//        }
        setSelfTimeTemplate(jComboBoxSetAlarmTime,0,sAnotherName,sTemplateClass,AlarmType,iChannel);
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jCheckBoxEnableItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxEnableItemStateChanged
        // TODO add your handling code here:
        setAlarmEnable();
//        if (jComboBoxAlarmType.getItemCount() > 0){
//            if (sAlarmType.equals(sAlarmTypes[4])) refreshStruFieldDetecionAbility();//根据区域入侵能力集，设置控件的显示和隐藏
//        }
    }//GEN-LAST:event_jCheckBoxEnableItemStateChanged

    private void jCheckBoxAlarmoutItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxAlarmoutItemStateChanged
        // TODO add your handling code here:
        if (jCheckBoxAlarmout.isSelected()) jListTraggerAlarmOut.setEnabled(true);
        else jListTraggerAlarmOut.setEnabled(false);
        //        jListTraggerAlarmOut
    }//GEN-LAST:event_jCheckBoxAlarmoutItemStateChanged

    private void jButtonTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTemplateActionPerformed
        // TODO add your handling code here:
        editTimeTemplate(jComboBoxSetAlarmTime, jButtonTemplate);
    }//GEN-LAST:event_jButtonTemplateActionPerformed

    private void jComboBoxSetAlarmTimeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxSetAlarmTimeItemStateChanged
        // TODO add your handling code here:
        ComboBoxAlarmTimeItemChanged(jComboBoxSetAlarmTime,jButtonTemplate,evt);
    }//GEN-LAST:event_jComboBoxSetAlarmTimeItemStateChanged

    private void jComboBoxIDItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxIDItemStateChanged
        // TODO add your handling code here:bIntialSelect
        if(evt.getStateChange() == ItemEvent.SELECTED)
        {
            if (bIntialSelect) return;
            sAlarmType = (String)jComboBoxAlarmType.getSelectedItem();//报警类型

            if(sAlarmType.equals(sAlarmTypes[3])) getTreversePlaneParas();//"越界侦测"
            else if(sAlarmType.equals(sAlarmTypes[4])) getFieldDetecionParas();//"区域入侵侦测"
            else {}
//            switch (sAlarmType){
//                case "越界侦测":
//                    getTreversePlaneParas();
//                    break;
//                case "区域入侵侦测":
//                    getFieldDetecionParas();
//                    break;
//            }
        }
    }//GEN-LAST:event_jComboBoxIDItemStateChanged

    private void jButtonPTZControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPTZControlActionPerformed
        // TODO add your handling code here:
        JDialogPTZControl dialog = new JDialogPTZControl(null, true,channelRP);
        CommonParas.setAppropriateLocation(dialog, jButtonPTZControl);
        dialog.setVisible(true);
    }//GEN-LAST:event_jButtonPTZControlActionPerformed

    private void panelPlayMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelPlayMousePressed
        // TODO add your handling code here:
        //        if (!((JToggleButton.ToggleButtonModel)jToggleButtonAdd.getModel()).isSelected()) return ;
        String AlarmType = jComboBoxAlarmType.getSelectedItem().toString();
        POINT point = new POINT(evt.getX(), evt.getY());
        
        if (AlarmType.equals(sAlarmTypes[0])) {//"移动侦测"
                if (iDetectIndex >= MAX_MOTION_NUM){
                    initialMotionScopeRect(struPicCfg.struMotion.byMotionScope,MAX_MOTION_NUM);//初始化移动侦测区域和矩形区域参数
                }

                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), MotionDetectSetCallBack, 0);

                rectMouse[iDetectIndex].left = point.x/iPrecision*iPrecision;
                rectMouse[iDetectIndex].top = point.y/iPrecision*iPrecision;
                rectMouse[iDetectIndex].right = rectMouse[iDetectIndex].left;
                rectMouse[iDetectIndex].bottom = rectMouse[iDetectIndex].top;

                rectSet[iDetectIndex].left = point.x/iPrecision*iPrecision;
                rectSet[iDetectIndex].top = point.y/iPrecision*iPrecision;

                rectSet[iDetectIndex].right = point.x/iPrecision*iPrecision+1;
                rectSet[iDetectIndex].bottom = point.y/iPrecision*iPrecision+1;
                iDetectIndex ++;
            
        }else if(AlarmType.equals(sAlarmTypes[1])) {//"视频丢失"
        }else if(AlarmType.equals(sAlarmTypes[2])) {//"遮挡报警"
                //                if (bHideGet) return;//如果已经设置了，则返回

                if (iDetectIndex >= 1)
                {
                    initialMotionScopeRect(byGridsScope, MAX_MOTION_NUM);
                }

                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), HideSetCallBack, 0);

                rectMouse[0].left = point.x/iPrecision*iPrecision;
                rectMouse[0].top = point.y/iPrecision*iPrecision;
                rectMouse[0].right = rectMouse[0].left;
                rectMouse[0].bottom = rectMouse[0].top;

                rectSet[0].left = point.x/iPrecision*iPrecision;
                rectSet[0].top = point.y/iPrecision*iPrecision;

                rectSet[0].right = point.x/iPrecision*iPrecision+1;
                rectSet[0].bottom = point.y/iPrecision*iPrecision+1;
                iDetectIndex ++;
        }else if(AlarmType.equals(sAlarmTypes[3])) {//"越界侦测"
                //int IndexOfID = jComboBoxID.getSelectedIndex() + 1;
                //TraversePlaneCallBack.setIndex(IndexOfID);
                TraversePlaneCallBack.setArrowDirection(jComboBoxCrossDirection.getSelectedIndex());
                TraversePlaneCallBack.setPosition1(new POINT(evt.getX(), evt.getY()));
                TraversePlaneCallBack.setPosition2(new POINT(evt.getX()+1, evt.getY()+1));
        }else if(AlarmType.equals(sAlarmTypes[4])) {//"区域入侵侦测"
                //FieldDetecionCallBack
                FieldDetecionCallBack.setPointsNum(Integer.parseInt(jComboBoxPointNum.getSelectedItem().toString()));
                FieldDetecionCallBack.addPoint(point);
        }else {}
        
//        switch (AlarmType){
//            case "移动侦测":
//                if (iDetectIndex >= MAX_MOTION_NUM)
//                {
//                    initialMotionScopeRect(struPicCfg.struMotion.byMotionScope,MAX_MOTION_NUM);//初始化移动侦测区域和矩形区域参数
//                }
//
//                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), MotionDetectSetCallBack, 0);
//
//                rectMouse[iDetectIndex].left = point.x/iPrecision*iPrecision;
//                rectMouse[iDetectIndex].top = point.y/iPrecision*iPrecision;
//                rectMouse[iDetectIndex].right = rectMouse[iDetectIndex].left;
//                rectMouse[iDetectIndex].bottom = rectMouse[iDetectIndex].top;
//
//                rectSet[iDetectIndex].left = point.x/iPrecision*iPrecision;
//                rectSet[iDetectIndex].top = point.y/iPrecision*iPrecision;
//
//                rectSet[iDetectIndex].right = point.x/iPrecision*iPrecision+1;
//                rectSet[iDetectIndex].bottom = point.y/iPrecision*iPrecision+1;
//                iDetectIndex ++;
//                break;
//            case "视频丢失":
//
//                break;
//            case "遮挡报警":
//                //                if (bHideGet) return;//如果已经设置了，则返回
//
//                if (iDetectIndex >= 1)
//                {
//                    initialMotionScopeRect(byGridsScope, MAX_MOTION_NUM);
//                }
//
//                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), HideSetCallBack, 0);
//
//                rectMouse[0].left = point.x/iPrecision*iPrecision;
//                rectMouse[0].top = point.y/iPrecision*iPrecision;
//                rectMouse[0].right = rectMouse[0].left;
//                rectMouse[0].bottom = rectMouse[0].top;
//
//                rectSet[0].left = point.x/iPrecision*iPrecision;
//                rectSet[0].top = point.y/iPrecision*iPrecision;
//
//                rectSet[0].right = point.x/iPrecision*iPrecision+1;
//                rectSet[0].bottom = point.y/iPrecision*iPrecision+1;
//                iDetectIndex ++;
//                break;
//            case "越界侦测":
//                //int IndexOfID = jComboBoxID.getSelectedIndex() + 1;
//                //TraversePlaneCallBack.setIndex(IndexOfID);
//                TraversePlaneCallBack.setArrowDirection(jComboBoxCrossDirection.getSelectedIndex());
//                TraversePlaneCallBack.setPosition1(new POINT(evt.getX(), evt.getY()));
//                TraversePlaneCallBack.setPosition2(new POINT(evt.getX()+1, evt.getY()+1));
//                break;
//            case "区域入侵侦测":
//                //FieldDetecionCallBack
//                FieldDetecionCallBack.setPointsNum(Integer.parseInt(jComboBoxPointNum.getSelectedItem().toString()));
//                FieldDetecionCallBack.addPoint(point);
//                break;
//        }
    }//GEN-LAST:event_panelPlayMousePressed

    private void panelPlayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelPlayMouseReleased
        // TODO add your handling code here:
        //        if (!((JToggleButton.ToggleButtonModel)jToggleButtonAdd.getModel()).isSelected()) return ;

        String AlarmType = jComboBoxAlarmType.getSelectedItem().toString();
        
        if (AlarmType.equals(sAlarmTypes[0])) {//"移动侦测"
                setMotionScope(struPicCfg.struMotion.byMotionScope);
                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), MotionDetectGetCallBack, 0);
        }else if(AlarmType.equals(sAlarmTypes[1])) {//"视频丢失"
        }else if(AlarmType.equals(sAlarmTypes[2])) {//"遮挡报警"
                setMotionScope(byGridsScope);
                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), GridsCallBack, 0);
        }else if(AlarmType.equals(sAlarmTypes[3])) {//"越界侦测"
        }else if(AlarmType.equals(sAlarmTypes[4])) {//"区域入侵侦测"
        }else {}
        
//        switch (AlarmType){
//            case "移动侦测":
//                //                if (iDetectIndex >= MAX_MOTION_NUM)
//                //                {
//                    //                    return;
//                    //                }
//                setMotionScope(struPicCfg.struMotion.byMotionScope);
//                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), MotionDetectGetCallBack, 0);
//
//                break;
//            case "视频丢失":
//                break;
//            case "遮挡报警":
//                //                if (bHideGet) return;//如果已经设置了，则返回
//                //                bHideGet = true;
//                setMotionScope(byGridsScope);
//                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), GridsCallBack, 0);
//                break;
//            case "越界侦测":
//            case "区域入侵侦测":
//
//                break;
//        }
    }//GEN-LAST:event_panelPlayMouseReleased

    private void panelPlayMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelPlayMouseDragged
        // TODO add your handling code here:
        //        if (!((JToggleButton.ToggleButtonModel)jToggleButtonAdd.getModel()).isSelected()) return ;
        String AlarmType = jComboBoxAlarmType.getSelectedItem().toString();
        POINT point = new POINT(evt.getX(), evt.getY());
        if (point.x > panelPlay.getWidth()){
            point.x = panelPlay.getWidth();
        }
        if (point.y > panelPlay.getHeight()){
            point.y = panelPlay.getHeight();
        }
        
        if (AlarmType.equals(sAlarmTypes[0])) {//"移动侦测"
                rectMouse[iDetectIndex-1].right = point.x / iPrecision * iPrecision;
                rectMouse[iDetectIndex-1].bottom = point.y / iPrecision * iPrecision;

                rectSet[iDetectIndex-1].right = point.x / iPrecision * iPrecision;
                rectSet[iDetectIndex-1].bottom = point.y / iPrecision * iPrecision;
        }else if(AlarmType.equals(sAlarmTypes[1])) {//"视频丢失"
        }else if(AlarmType.equals(sAlarmTypes[2])) {//"遮挡报警"
                //if (bHideGet) return;//如果已经设置了，则返回
                rectMouse[0].right = point.x / iPrecision * iPrecision;

                rectMouse[0].bottom = point.y / iPrecision * iPrecision;

                rectSet[0].right = point.x / iPrecision * iPrecision;
                rectSet[0].bottom = point.y / iPrecision * iPrecision;
        }else if(AlarmType.equals(sAlarmTypes[3])) {//"越界侦测"
                TraversePlaneCallBack.setPosition2(new POINT(evt.getX(), evt.getY()));
        }else if(AlarmType.equals(sAlarmTypes[4])) {//"区域入侵侦测"
        }else {}
//        switch (AlarmType){
//            case "移动侦测":
//                //                if (iDetectIndex >= MAX_MOTION_NUM)
//                //                {
//                    //                    return;
//                    //                }
//
//                rectMouse[iDetectIndex-1].right = point.x / iPrecision * iPrecision;
//                rectMouse[iDetectIndex-1].bottom = point.y / iPrecision * iPrecision;
//
//                rectSet[iDetectIndex-1].right = point.x / iPrecision * iPrecision;
//                rectSet[iDetectIndex-1].bottom = point.y / iPrecision * iPrecision;
//                break;
//            case "视频丢失":
//                break;
//            case "遮挡报警":
//                //if (bHideGet) return;//如果已经设置了，则返回
//                rectMouse[0].right = point.x / iPrecision * iPrecision;
//
//                rectMouse[0].bottom = point.y / iPrecision * iPrecision;
//
//                rectSet[0].right = point.x / iPrecision * iPrecision;
//                rectSet[0].bottom = point.y / iPrecision * iPrecision;
//                break;
//            case "越界侦测":
//                TraversePlaneCallBack.setPosition2(new POINT(evt.getX(), evt.getY()));
//                break;
//            case "区域入侵侦测":
//
//                break;
//        }
    }//GEN-LAST:event_panelPlayMouseDragged

    private void jButtonDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelActionPerformed
        // TODO add your handling code here:
        //        if (((JToggleButton.ToggleButtonModel)jToggleButtonAdd.getModel()).isSelected()) jToggleButtonAdd.setSelected(false);
        String AlarmType = jComboBoxAlarmType.getSelectedItem().toString();
        if (AlarmType.equals(sAlarmTypes[0])) {//"移动侦测"
                //clear
                //                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), null, 0);
                initialMotionScopeRect(struPicCfg.struMotion.byMotionScope, MAX_MOTION_NUM);//初始化移动侦测区域和矩形区域参数
        }else if(AlarmType.equals(sAlarmTypes[1])) {//"视频丢失"
        }else if(AlarmType.equals(sAlarmTypes[2])) {//"遮挡报警"
                initialMotionScopeRect(byGridsScope, MAX_MOTION_NUM);
        }else if(AlarmType.equals(sAlarmTypes[3])) {//"越界侦测"
                TraversePlaneCallBack.initialParas();
        }else if(AlarmType.equals(sAlarmTypes[4])) {//"区域入侵侦测"
                FieldDetecionCallBack.initialParas(Integer.parseInt((String) jComboBoxPointNum.getSelectedItem()));
        }else {}
//        switch (AlarmType){
//            case "移动侦测":
//                //clear
//                //                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), null, 0);
//                initialMotionScopeRect(struPicCfg.struMotion.byMotionScope, MAX_MOTION_NUM);//初始化移动侦测区域和矩形区域参数
//                break;
//            case "视频丢失":
//                break;
//            case "遮挡报警":
//                initialMotionScopeRect(byGridsScope, MAX_MOTION_NUM);
//
//                break;
//            case "越界侦测":
//                TraversePlaneCallBack.initialParas();
//                break;
//            case "区域入侵侦测":
//                FieldDetecionCallBack.initialParas(Integer.parseInt((String) jComboBoxPointNum.getSelectedItem()));
//                break;
//        }
    }//GEN-LAST:event_jButtonDelActionPerformed

    private void jButtonFullScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFullScreenActionPerformed
        // TODO add your handling code here:
        String AlarmType = jComboBoxAlarmType.getSelectedItem().toString();
        POINT point;
        
        if (AlarmType.equals(sAlarmTypes[0])) {//"移动侦测"
                for (int i = 0; i < 64; i++)
                {
                    for (int j = 0; j < 96; j++)
                    {
                        struPicCfg.struMotion.byMotionScope[i].byMotionScope[j] = 1;
                    }
                }

                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), MotionDetectGetCallBack, 0);
        }else if(AlarmType.equals(sAlarmTypes[1])) {//"视频丢失"
        }else if(AlarmType.equals(sAlarmTypes[2])) {//"遮挡报警"
                for (int i = 0; i < 64; i++)
                {
                    for (int j = 0; j < 96; j++)
                    {
                        byGridsScope[i].byMotionScope[j] = 1;
                    }
                }

                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), GridsCallBack, 0);
                
        }else if(AlarmType.equals(sAlarmTypes[3])) {//"越界侦测"
                TraversePlaneCallBack.initialParas();
        }else if(AlarmType.equals(sAlarmTypes[4])) {//"区域入侵侦测"
                FieldDetecionCallBack.initialParas(Integer.parseInt((String) jComboBoxPointNum.getSelectedItem()));
        }else {}
//        switch (AlarmType){
//            case "移动侦测":
//                for (int i = 0; i < 64; i++)
//                {
//                    for (int j = 0; j < 96; j++)
//                    {
//                        struPicCfg.struMotion.byMotionScope[i].byMotionScope[j] = 1;
//                    }
//                }
//
//                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), MotionDetectGetCallBack, 0);
//                break;
//            case "遮挡报警":
//                for (int i = 0; i < 64; i++)
//                {
//                    for (int j = 0; j < 96; j++)
//                    {
//                        byGridsScope[i].byMotionScope[j] = 1;
//                    }
//                }
//
//                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), GridsCallBack, 0);
//                break;
//            case "越界侦测":
//            case "区域入侵侦测":
//            case "视频丢失":
//                break;
//        }
    }//GEN-LAST:event_jButtonFullScreenActionPerformed

    private void jTextFieldSensitiveKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSensitiveKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (c == KeyEvent.VK_DELETE || c == KeyEvent.VK_BACK_SPACE) return;
        if (!(c >= '0' && c <= '9')) {
            evt.setKeyChar('\0');
            CommonParas.showMessage(this.getRootPane(), sIncorrectInput, sFileName);// "输入有误。</br>灵敏度值必须为数字，其它字符非法！"
            //JOptionPane.showMessageDialog(rootPane, "输入有误。输入必须数字，其它字符非法！");
        }else{
            int iSensitive = Integer.parseInt(jTextFieldSensitive.getText().trim());
            if (iSensitive > jSliderSensitive.getMaximum() || iSensitive <  jSliderSensitive.getMinimum()) {
                evt.setKeyChar('\0');
                CommonParas.showMessage(this.getRootPane(), MessageFormat.format(sOutOfRange , jSliderSensitive.getMinimum(), jSliderSensitive.getMaximum()), sFileName);//"灵敏度的输入超出了</br>["+jSliderSensitive.getMinimum() + ", " + jSliderSensitive.getMaximum() + "]"
            }
        }
    }//GEN-LAST:event_jTextFieldSensitiveKeyTyped

    private void jSliderSensitiveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderSensitiveStateChanged
        // TODO add your handling code here:
        jTextFieldSensitive.setText(Integer.toString(jSliderSensitive.getValue()));
    }//GEN-LAST:event_jSliderSensitiveStateChanged

    private void jTreeResource2ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeResource2ValueChanged
        // TODO add your handling code here:
        treeNodeValueChanged(jTreeResource2,evt);
    }//GEN-LAST:event_jTreeResource2ValueChanged

    private void jComboBoxExceptionTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxExceptionTypeItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED)
        {
            if (bFirstSelect)  return;
            refreshExceptionInfo();//刷新异常信息
        }
    }//GEN-LAST:event_jComboBoxExceptionTypeItemStateChanged

    private void jCheckBoxAlarmout2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxAlarmout2ItemStateChanged
        // TODO add your handling code here:
        if (jCheckBoxAlarmout2.isSelected()) jListTraggerAlarmOut2.setEnabled(true);
        else jListTraggerAlarmOut2.setEnabled(false);
    }//GEN-LAST:event_jCheckBoxAlarmout2ItemStateChanged

    private void jButtonSave2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSave2ActionPerformed
        // TODO add your handling code here:
        setExceptionInfo();
    }//GEN-LAST:event_jButtonSave2ActionPerformed

    private void jTreeResource3ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeResource3ValueChanged
        // TODO add your handling code here:
        treeNodeValueChanged(jTreeResource3,evt);
    }//GEN-LAST:event_jTreeResource3ValueChanged

    private void jCheckBoxEnable3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxEnable3ItemStateChanged
        // TODO add your handling code here:
        setAlarmInEnable();
    }//GEN-LAST:event_jCheckBoxEnable3ItemStateChanged

    private void jButtonTemplate3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTemplate3ActionPerformed
        // TODO add your handling code here:
        editTimeTemplate(jComboBoxSetAlarmTime3, jButtonTemplate3);
    }//GEN-LAST:event_jButtonTemplate3ActionPerformed

    private void jButtonSave3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSave3ActionPerformed
        // TODO add your handling code here:
        setAlarmInCfg();
    }//GEN-LAST:event_jButtonSave3ActionPerformed

    private void jComboBoxSetAlarmTime3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxSetAlarmTime3ItemStateChanged
        // TODO add your handling code here:
        ComboBoxAlarmTimeItemChanged(jComboBoxSetAlarmTime3,jButtonTemplate3,evt);
    }//GEN-LAST:event_jComboBoxSetAlarmTime3ItemStateChanged

    private void jCheckBoxPTZEnableItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxPTZEnableItemStateChanged
        // TODO add your handling code here:
        if (jCheckBoxPTZEnable.isSelected()){
            jRadioButtonCruise.setEnabled(true);
            jComboBoxCruise.setEnabled(true);
            jRadioButtonPreset.setEnabled(true);
            jComboBoxPreset.setEnabled(true);
            jRadioButtonTrack.setEnabled(true);
            jComboBoxTrack.setEnabled(true);
            //jButtonCfg.setEnabled(true);
        }
        else {
            jRadioButtonCruise.setEnabled(false);
            jComboBoxCruise.setEnabled(false);
            jRadioButtonPreset.setEnabled(false);
            jComboBoxPreset.setEnabled(false);
            jRadioButtonTrack.setEnabled(false);
            jComboBoxTrack.setEnabled(false);
            //jButtonCfg.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxPTZEnableItemStateChanged

    private void jTreeResource4ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeResource4ValueChanged
        //         TODO add your handling code here:
        treeNodeValueChanged(jTreeResource4,evt);
    }//GEN-LAST:event_jTreeResource4ValueChanged

    private void jButtonTemplate4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTemplate4ActionPerformed
        // TODO add your handling code here:
        editTimeTemplate(jComboBoxSetAlarmTime4, jButtonTemplate4);
    }//GEN-LAST:event_jButtonTemplate4ActionPerformed

    private void jComboBoxSetAlarmTime4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxSetAlarmTime4ItemStateChanged
        // TODO add your handling code here:
        ComboBoxAlarmTimeItemChanged(jComboBoxSetAlarmTime4,jButtonTemplate4,evt);
    }//GEN-LAST:event_jComboBoxSetAlarmTime4ItemStateChanged

    private void jButtonSave4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSave4ActionPerformed
        // TODO add your handling code here:
        setAlarmOutCfg();
    }//GEN-LAST:event_jButtonSave4ActionPerformed

    private void jTabbedPaneAlarmStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneAlarmStateChanged
        // TODO add your handling code here:
        //JOptionPane.showMessageDialog(rootPane, "序号"+jTabbedPaneAlarm.getSelectedIndex());
        //先记录原先的Tab参数
        listTabParas.get(indexOfTab).setTabParas();
        
        indexOfTab = jTabbedPaneAlarm.getSelectedIndex();
        //重新赋值新的Tab参数
        listTabParas.get(indexOfTab).getTabParas();
        
        if (indexOfTab == 0) panelPlay.setVisible(true);
        sTemplateClass = CommonParas.TimeTemplateClass.createTimeTemplateClas(indexOfTab + 1);////模板用途分类:
        initialListRecordAlarmOut();
    }//GEN-LAST:event_jTabbedPaneAlarmStateChanged

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        bFirstSelect = false;
//        jCheckBoxEnable.setEnabled(false);
        setAlarmEnable();
        setExceptionEnable();
        setAlarmInEnable();
        setAlarmOutEnable(false);
    }//GEN-LAST:event_formInternalFrameOpened

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        // TODO add your handling code here:
        doDefaultCloseAction();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonExit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExit2ActionPerformed
        // TODO add your handling code here:
        doDefaultCloseAction();
    }//GEN-LAST:event_jButtonExit2ActionPerformed

    private void jButtonExit3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExit3ActionPerformed
        // TODO add your handling code here:
        doDefaultCloseAction();
    }//GEN-LAST:event_jButtonExit3ActionPerformed

    private void jButtonExit4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExit4ActionPerformed
        // TODO add your handling code here:
        doDefaultCloseAction();
    }//GEN-LAST:event_jButtonExit4ActionPerformed

    private void jCheckBoxEnable2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxEnable2ItemStateChanged
        // TODO add your handling code here:
        setExceptionEnable();
    }//GEN-LAST:event_jCheckBoxEnable2ItemStateChanged

    private void formInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameDeactivated
        // TODO add your handling code here:
        panelPlay.setVisible(false);
    }//GEN-LAST:event_formInternalFrameDeactivated

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        // TODO add your handling code here:
        //jPanelPlay.setVisible(true);
        int IndexOfTab = jTabbedPaneAlarm.getSelectedIndex();
        if (IndexOfTab == 0) panelPlay.setVisible(true);
    }//GEN-LAST:event_formInternalFrameActivated

    private void jCheckBoxAlarmout3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxAlarmout3ItemStateChanged
        // TODO add your handling code here:
        jListTraggerAlarmOut3.setEnabled(jCheckBoxAlarmout3.isSelected());
    }//GEN-LAST:event_jCheckBoxAlarmout3ItemStateChanged

                               
    
    /**********************报警设置信息*************************/
    /**
	 * 函数:      refreshAlarmStruInfo
         * 函数描述:  读取报警信息。主要在1、点击树之后运行；（点击jComboBox之后直接运行各自的函数刷新）
         *            原先两个函数，现在合并为一个函数了。
    */
    private void refreshAlarmStruInfo(){
        switch (sTemplateClass){
            case CommonParas.TimeTemplateClass.MONITOR_SETUPALARMCHAN:
                //设置设备、通道/报警输入/报警输出的序列号、报警类型对应的时间模板
                //initialSelfTemplate(jComboBoxSetAlarmTime,0,sAnotherName,(String)jComboBoxAlarmType.getSelectedItem(),iChannel);
                refreshMonitorPointAlarmAbility();
                refreshMonitorPointAlarm();
                break;
            case CommonParas.TimeTemplateClass.EXCEPTION_SETUPALARMCHAN:
                refreshExceptionAbility();
                refreshExceptionInfo();
                break;
            case CommonParas.TimeTemplateClass.ALARMIN_SETUPALARMCHAN:
                //设置设备、通道/报警输入/报警输出的序列号、报警类型对应的时间模板
                //initialSelfTemplate(jComboBoxSetAlarmTime3,2,sAnotherName,"",iChannel);
                refreshAlarmInAbility();
                refreshAlarmInCfg();
                break;
            case CommonParas.TimeTemplateClass.ALARMOUT_SETUPALARMCHAN:
                //设置设备、通道/报警输入/报警输出的序列号、报警类型对应的时间模板
                //(jComboBoxSetAlarmTime4,3,sAnotherName,"",iChannel);
                refreshAlarmOutCfg();
                break;

        }
        
    }
    /**
	 * 函数:      refreshMonitorPointAlarm
         * 函数描述:  读取监控点报警信息。
    */
    private void refreshMonitorPointAlarm(){
        //refreshMonitorPointAlarmAbility();
        if(sAnotherName.equals("")) return;
        
        if (jComboBoxAlarmType.getItemCount() == 0){
            setAlarmEnable();
            return;
        }
        
        initialSelfTemplate(jComboBoxSetAlarmTime,0,sAnotherName,(String)jComboBoxAlarmType.getSelectedItem(),iChannel);
        previewChannelRP();//预览
        
        sAlarmType = (String)jComboBoxAlarmType.getSelectedItem();//报警器类型
        if (sAlarmType.equals(sAlarmTypes[0])) {//"移动侦测"
                refreshStruPicCfg();//刷新通道图像结构体
                //做相关的操作
                refreshMotionDetectAlarm();
        }else if(sAlarmType.equals(sAlarmTypes[1])) {//"视频丢失"
                refreshStruPicCfg();//刷新通道图像结构体
                //做相关的操作
                refreshVideoInLostAlarm();
        }else if(sAlarmType.equals(sAlarmTypes[2])) {//"遮挡报警"
                refreshStruPicCfg();//刷新通道图像结构体
                //做相关的操作
                refreshHideAlarm();
        }else if(sAlarmType.equals(sAlarmTypes[3])) {//"越界侦测"
                refreshStruTreversePlane();//越界报警结构体
        }else if(sAlarmType.equals(sAlarmTypes[4])) {//"区域入侵侦测"
                refreshStruFieldDetecion();//区域入侵报警结构体
        }else {}
        
//        switch (sAlarmType){
//            case "移动侦测":
////                if (!listIfRefrenshStru.get(0)) 
//                refreshStruPicCfg();//刷新通道图像结构体
//                //做相关的操作
//                refreshMotionDetectAlarm();
//                break;
//            case "视频丢失":
////                if (!listIfRefrenshStru.get(0)) 
//                    refreshStruPicCfg();//刷新通道图像结构体
//                //做相关的操作
//                refreshVideoInLostAlarm();
//                break;
//            case "遮挡报警":
////                if (!listIfRefrenshStru.get(0)) 
//                    refreshStruPicCfg();//刷新通道图像结构体
//                //做相关的操作
//                refreshHideAlarm();
//                break;
//            case "越界侦测":
////                if (!listIfRefrenshStru.get(1)) 
//                    refreshStruTreversePlane();//越界报警结构体
//                //做相关的操作
//                break;
//            case "区域入侵侦测":
////                if (!listIfRefrenshStru.get(2)) 
//                    refreshStruFieldDetecion();//区域入侵报警结构体
//                //做相关的操作
//                break;
//        }
        //设置可用性
        setAlarmEnable();
        
    }
    /**
	 * 函数:      refreshMonitorPointAlarmAbility
         * 函数描述:  获取设备报警能力集可以判断设备是否支持相关功能
    */
    private void refreshMonitorPointAlarmAbility(){
        try{
            //private String[] sAlarmTypes = new String[]{"移动侦测", "视频丢失", "遮挡报警", "越界侦测", "区域入侵侦测"};
            listAlarmTypes.clear();
            
            //设备图像参数能力<VideoPicAbility version="2.0"><channelNO>1</channelNO></VideoPicAbility>         DEVICE_VIDEOPIC_ABILITY
            //主要针对<MotionDetection><VILostDetection><HideDetection>
            if (getDeviceAbility(HCNetSDK.DEVICE_VIDEOPIC_ABILITY, "<VideoPicAbility version=\"2.0\"><channelNO>", "</channelNO></VideoPicAbility>", pOutBufVideoPic)){
                String XMLOut2 = new String(pOutBufVideoPic).trim();
                DomXML domXML2 = new DomXML(XMLOut2,sFileName);
                
                //MotionDetection
                if (domXML2.isExistMajorNode("MotionDetection")){
                    listAlarmTypes.add(sAlarmTypes[0]);
                }
                //VILostDetection
                if (domXML2.isExistMajorNode("VILostDetection")){
                    listAlarmTypes.add(sAlarmTypes[1]);
                }
                //HideDetection
                if (domXML2.isExistMajorNode("HideDetection")){
                    listAlarmTypes.add(sAlarmTypes[2]);
                }
            }
            
            //获取设备报警能力集//<FieldDetection><TraversingVirtualPlane>
            if (getDeviceAbility(HCNetSDK.DEVICE_ABILITY_INFO, "<EventAbility version=\"2.0\"><channelNO>", "</channelNO></EventAbility>", pOutBufAlarm)){
                String XMLOut = new String(pOutBufAlarm).trim();
                DomXML domXML = new DomXML(XMLOut,sFileName);
                
                //FieldDetection
                if (domXML.isExistMajorNode("FieldDetection")){
                    listAlarmTypes.add(sAlarmTypes[3]);
                }
                //TraversingVirtualPlane
                if (domXML.isExistMajorNode("TraversingVirtualPlane")){
                    listAlarmTypes.add(sAlarmTypes[4]);
                }
            }
            Object[] Items = listAlarmTypes.toArray();
            jComboBoxAlarmType.setModel(new DefaultComboBoxModel(Items) );
            bFirstSelect = true;
            jComboBoxAlarmType.setSelectedItem(sAlarmType);
            bFirstSelect = false;
            //根据设备该通道支持的报警能力，设备相关控件的是否可用
            //最后还是放在setAlarmEnable函数中
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshMonitorPointAlarmAbility()","系统在获取设备报警能力集可以判断设备是否支持相关功能过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    /**
	 * 函数:      GetDeviceAbility
         * 函数描述:  获取设备能力集可以判断设备是否支持相关功能
    */
    private boolean getDeviceAbility(int Command, String InBufLeft, String InBufRight, byte[] pOutBuf){
        return getDeviceAbility( Command,  InBufLeft, iChannel,  InBufRight, pOutBuf);
    }
    /**
	 * 函数:      GetDeviceAbility
         * 函数描述:  获取设备能力集可以判断设备是否支持相关功能
    */
    private boolean getDeviceAbility(int Command, String InBufLeft,int Channel, String InBufRight, byte[] pOutBuf){
        try{
            String pInBuf = InBufLeft + Channel + InBufRight;

//            if (strIpparaCfg != null && iChannel > 0){//iChannel = 0则只有设置设备异常的时候才为0。因为异常只针对设备进行操作
//                    String IPJoin = CommonParas.getDVRIPJoin(strDeviceInfo, strIpparaCfg, iChannel, sFileName);
//
//                    NativeLong UserID = CommonParas.getUserID(CommonParas.getIndexOfDeviceList(IPJoin, "", sFileName), sFileName);
//                    int iIPChanel = iChannel - (HCNetSDK.MAX_ANALOG_CHANNUM + strDeviceInfo.byStartChan);
//                    if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return false;//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）
//                    int ChannelJoin = strIpparaCfg.struIPChanInfo[iIPChanel].byChannel;
//                    pInBuf = InBufLeft + ChannelJoin + InBufRight;//"<AlarmAbility><channelID>" + ChannelJoin + "</channelID></AlarmAbility>";
//
//                    bRet = CommonParas.hCNetSDK.NET_DVR_GetDeviceAbility( UserID, Command, pInBuf, pInBuf.length(), pOutBuf, pOutBuf.length);
//            }else   
             boolean   bRet = CommonParas.hCNetSDK.NET_DVR_GetDeviceAbility( lUserID, Command, pInBuf, pInBuf.length(), pOutBuf, pOutBuf.length);
            //写错误日志
            if (!bRet) {
                CommonParas.SystemWriteErrorLog( sGetDeviceCapabilitySetsFail,  sAnotherName, sFileName);// "获取设备能力集失败"
                CommonParas.showErrorMessage(this.getRootPane(), sGetDeviceCapabilitySetsFail, sAnotherName, sFileName);// "获取设备能力集失败"
            }
            return bRet;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "GetDeviceAbility()","系统在获取设备报警能力集可以判断设备是否支持相关功能过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /**********************移动侦测开始*************************/
    /**
	 * 函数:      refreshMotionDetectAlarm
         * 函数描述:  读取移动侦测结构参数，并预览、显示出来
    */
    private void refreshMotionDetectAlarm(){
        
        //获取报警事件处理能力集可以判断设备是否支持相关功能
        refreshMotionDetectAbility();
        /**
            * 1、是否设置侦测区域？是，预览、则画出侦测区域
            * 2、灵敏度
            * 3、触发录像的通道
            * 4、触发报警输出
            * 5、报警处理方式：监视器警告/声音报警/上传中心/Email/等的处理
            * 6、布防时间
        */
        try{
//            jComboBoxSensitive.removeAllItems();
////        关闭, 0--最低, 1, 2, 3, 4, 5--最高
                jSliderSensitive.setMaximum(5);
                jSliderSensitive.setMinimum(0);
            //2、灵敏度
            if (struPicCfg.struMotion.byMotionSensitive == -1)
            {
//                    jComboBoxSensitive.setSelectedIndex(0);
                    jTextFieldSensitive.setText("0");
            }
            else
            {
//                    jComboBoxSensitive.setSelectedIndex(struPicCfg.struMotion.byMotionSensitive + 1);
                    jTextFieldSensitive.setText(Integer.toString(struPicCfg.struMotion.byMotionSensitive));
                    jSliderSensitive.setValue(struPicCfg.struMotion.byMotionSensitive);
            }
            //5、报警处理方式：监视器警告/声音报警/上传中心/Email/等的处理
            jCheckBoxMonitorAlarm.setSelected((struPicCfg.struMotion.struMotionHandleType.dwHandleType & 0x01) == 1);
            jCheckBoxAudioAlarm.setSelected(((struPicCfg.struMotion.struMotionHandleType.dwHandleType >> 1) & 0x01) == 1);
            jCheckBoxCenterAlarm.setSelected(((struPicCfg.struMotion.struMotionHandleType.dwHandleType >> 2) & 0x01) == 1);
            jCheckBoxAlarmout.setSelected(((struPicCfg.struMotion.struMotionHandleType.dwHandleType >> 3) & 0x01) == 1);
            jCheckBoxJpegEMailAlarm.setSelected(((struPicCfg.struMotion.struMotionHandleType.dwHandleType >> 4) & 0x01) == 1);//Email上传抓图

            //6、布防时间
            strSchedTimeWeek = struPicCfg.struMotion.struAlarmTime;//[i].struAlarmTime[0];//只取一个时间段

            //3、触发录像的通道  4、触发报警输出
            //触发报警输出通道list
            listTraggerAlarmOut.clear();
            listModelTraggerAlarmOut.clear();
//            for (int i = 0; i < strDeviceInfo.byAlarmOutPortNum; i++)
//            {
//                CheckListItem checkListItem = new CheckListItem(struPicCfg.struMotion.struMotionHandleType.byRelAlarmOut[i] == 1,
//                                 sAnotherName + "_" + CommonParas.getNodeName(i+1, false, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT));
//                listTraggerAlarmOut.add(checkListItem);
//                listModelTraggerAlarmOut.addElement(checkListItem);    // 为触发报警输出List增加报警输出
//            }
            /*获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
                对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）*/
            ArrayList ListDeviceSourceAllParas = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE , sFileName);
            for (int i=0;i<ListDeviceSourceAllParas.size();i++){
                //IP通道从33开始（前MAX_ANALOG_CHANNUM个是模拟通道）
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                if (!AnotherNameJoin.equals("")){
                    String NodeName2 = NewList.get(1) + "_" + NewList.get(3) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struPicCfg.struMotion.struMotionHandleType.byRelAlarmOut[Channel + HCNetSDK.MAX_ANALOG_ALARMOUT - 1] == 1, NodeName2);
                    listTraggerAlarmOut.add(checkListItem);
                    listModelTraggerAlarmOut.addElement(checkListItem);   
                }else{
                    String NodeName2 = NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struPicCfg.struMotion.struMotionHandleType.byRelAlarmOut[Channel - 1] == 1, NodeName2);
                    listTraggerAlarmOut.add(checkListItem);
                    listModelTraggerAlarmOut.addElement(checkListItem); 
                }
            }
            
            

            //触发录像通道list
            listTraggerRecord.clear();
            listModelTraggerRecord.clear();
          
            /*获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
                对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）*/
            ArrayList ListDeviceSourceAllParas2 = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE , sFileName);
            for (int i=0;i<ListDeviceSourceAllParas2.size();i++){
                //IP通道从33开始（前MAX_ANALOG_CHANNUM个是模拟通道）
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas2.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                if (!AnotherNameJoin.equals("")){
                    String NodeName2 = NewList.get(1) + "_" + NewList.get(3) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struPicCfg.struMotion.byRelRecordChan[Channel + HCNetSDK.MAX_ANALOG_CHANNUM - 1] == 1, NodeName2);
                    listTraggerRecord.add(checkListItem);
                    listModelTraggerRecord.addElement(checkListItem);   
                }else{
                    String NodeName2 = NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struPicCfg.struMotion.byRelRecordChan[Channel - 1] == 1, NodeName2);
                    listTraggerRecord.add(checkListItem);
                    listModelTraggerRecord.addElement(checkListItem); 
                }
            }
            //1、是否设置侦测区域？
            jCheckBoxEnable.setSelected((struPicCfg.struMotion.byEnableHandleMotion > 0));
            //重新给矩形赋值
            for (int i = 0; i < MAX_MOTION_NUM; i++)
            {
                rectSet[i] = new RECT();
                rectMouse[i] = new RECT();
            }
            CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), MotionDetectGetCallBack, 0);
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshMotionDetectAlarm()","系统在读取移动侦测结构参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      refreshMotionDetectAbility
         * 函数描述:  获取报警事件处理能力集可以判断设备是否支持相关功能
    */
    private void refreshMotionDetectAbility(){
        refreshVideoPicAlarmHanleAbility("MotionDetection");
    }
    /**
	 * 函数:      refreshVideoPicAbility
         * 函数描述:  获取报警事件处理能力集可以判断设备是否支持相关功能
         * @param MajorNodeName 报警节点
    */
    private void refreshVideoPicAlarmHanleAbility(String MajorNodeName){
        try{
            sHandleTypeCode = "";
            String XMLOut = new String(pOutBufVideoPic).trim();
            DomXML domXML = new DomXML(XMLOut,sFileName);
            //获得异常处理方式，各种异常处理方式的列表
            //主要包括："","monitor","audio","center","alarmout","picture","wirelesslight","","uploadftp","","ptztrack"
            String ReturnV = domXML.readSecondLevelAttributeValue(MajorNodeName, "alarmHandleType", "opt");//optional中文解释	"可选择的"
            if (!(ReturnV.equals(""))) {
                sHandleTypeCode = ReturnV.toLowerCase();//将异常的处理方式保存
            }
            refreshOCXByHandleTypeCodes(sHandleTypeCode);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshVideoPicAlarmHanleAbility()","系统在获取报警事件处理能力集可以判断设备是否支持相关功能过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      setMotionDetectAlarm
         * 函数描述:  设置移动侦测结构参数
    */
    private void setMotionDetectAlarm(){
        try{
            //if (sAnotherName.equals("")) return;
            struPicCfg.struMotion.byEnableHandleMotion = (byte) ((jCheckBoxEnable.isSelected() == true) ? 1 : 0);
 
            //save zone settings on the device
            setMotionScope(struPicCfg.struMotion.byMotionScope);

            
//            if (jComboBoxSensitive.getSelectedIndex() == 0)
//            {
//                     struPicCfg.struMotion.byMotionSensitive = -1;
//            }
//            else
//            {
//                    struPicCfg.struMotion.byMotionSensitive = (byte)(jComboBoxSensitive.getSelectedIndex() - 1);
//            }
            struPicCfg.struMotion.byMotionSensitive = Byte.parseByte(jTextFieldSensitive.getText().trim());

            struPicCfg.struMotion.struMotionHandleType.dwHandleType = 0;
            struPicCfg.struMotion.struMotionHandleType.dwHandleType |= ((jCheckBoxMonitorAlarm.isSelected()?1:0));
            struPicCfg.struMotion.struMotionHandleType.dwHandleType |= ((jCheckBoxAudioAlarm.isSelected()?1:0) << 1);
            struPicCfg.struMotion.struMotionHandleType.dwHandleType |= ((jCheckBoxCenterAlarm.isSelected()?1:0) << 2);
            struPicCfg.struMotion.struMotionHandleType.dwHandleType |= ((jCheckBoxAlarmout.isSelected()?1:0) << 3);
            struPicCfg.struMotion.struMotionHandleType.dwHandleType |= ((jCheckBoxJpegEMailAlarm.isSelected()?1:0) << 4);

//            for (int i = 0; i < listTraggerAlarmOut.size(); i++)
//            {
////                    struPicCfg.struMotion.struMotionHandleType.byRelAlarmOut[i] = (byte)(m_traggerAlarmOut[i].getCheck()?1:0);
////                struPicCfg.struMotion.struMotionHandleType.byRelAlarmOut[i] = (byte)(((CheckListItem)listModelTraggerAlarmOut.getElementAt(i)).getCheck()?1:0);
//                    struPicCfg.struMotion.struMotionHandleType.byRelAlarmOut[i] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
//            }
            //设置报警输出
            for (int i = 0; i < listTraggerAlarmOut.size(); i++)
            {
                String NodeName2 = listTraggerAlarmOut.get(i).getText();
                int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                if (NodeName2.lastIndexOf("IP") > -1){
                    struPicCfg.struMotion.struMotionHandleType.byRelAlarmOut[Channel + HCNetSDK.MAX_ANALOG_ALARMOUT -1] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
                }else{
                    struPicCfg.struMotion.struMotionHandleType.byRelAlarmOut[Channel - 1] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
                }
                    
            }
            //设置录像触发
            for (int i = 0; i< listTraggerRecord.size(); i++)
            {
                String NodeName2 = listTraggerRecord.get(i).getText();
                int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                if (NodeName2.lastIndexOf("IP") > -1){
                    struPicCfg.struMotion.byRelRecordChan[Channel + HCNetSDK.MAX_ANALOG_CHANNUM -1] = (byte)(listTraggerRecord.get(i).getCheck()?1:0);
                    
                }else {
                    struPicCfg.struMotion.byRelRecordChan[Channel - 1] = (byte)(listTraggerRecord.get(i).getCheck()?1:0);
                }
                    
            }

//            for (int i = 0; i< listTraggerRecord.size(); i++)
//            {
////                    struPicCfg.struMotion.byRelRecordChan[i] = (byte)(m_traggerRecord[i].getCheck()?1:0);
//                    if (listTraggerRecord.get(i).getText().substring(0,2).equals("IP"))
//                        struPicCfg.struMotion.byRelRecordChan[i + HCNetSDK.MAX_ANALOG_CHANNUM] = (byte)(listTraggerRecord.get(i).getCheck()?1:0);
//                    else 
//                        struPicCfg.struMotion.byRelRecordChan[i] = (byte)(listTraggerRecord.get(i).getCheck()?1:0);
//            }
            
            //设置通道图像结构体参数。包含移动侦测、视频信号丢失、遮挡报警参数结构体。
            //设置这三个结构体都要通过设置通道图像结构体参数结构体实现
            if (setStruPicCfg()){
                writeAlarmConfigLog( sAlarmTypes[0], CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);
            }else{
                writeAlarmConfigErrorLog( sAlarmTypes[0], CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);
            }
            //时间模板在存储按钮操作中都已经统一进行处理了。
//            setSelfTimeTemplate(jComboBoxSetAlarmTime,0,sAnotherName,sTemplateClass,"移动侦测",iChannel);
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setMotionDetectAlarm()","系统在保存移动侦测结构参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      setMotionScope
         * 函数描述:  设置移动侦测区域
    */
    private void setMotionScope(HCNetSDK.byte96[] byMotionScope){
        //save zone settings on the device
        for (int k = 0; k < iDetectIndex; k++)
        {
                if (rectMouse[k].top <= rectMouse[k].bottom)
                {
                        if (rectMouse[k].left <= rectMouse[k].right)
                        {//draw from top-left to bottom-right
                                for (int i = rectMouse[k].top/iPrecision;i<64 && i<rectMouse[k].bottom/iPrecision; i++)
                                {
                                        for (int j = rectMouse[k].left/iPrecision; j < rectMouse[k].right/iPrecision; j++)
                                        {
                                                byMotionScope[i].byMotionScope[j] = 1;
                                        }
                                }
                        }
                        else
                        {//draw from top-right to bottom-left
                                for (int i = rectMouse[k].top/iPrecision; i<64 && i < rectMouse[k].bottom/iPrecision; i++)
                                {
                                        for (int j = rectMouse[k].right/iPrecision; j < rectMouse[k].left/iPrecision; j++)
                                        {
                                                byMotionScope[i].byMotionScope[j] = 1;
                                        }
                                }
                        }
                }
                else
                {
                        if (rectMouse[k].left <= rectMouse[k].right)
                        {//draw from bottom-left to top-right
                                for (int i = rectMouse[k].bottom/iPrecision; i < rectMouse[k].top/iPrecision; i++)
                                {
                                        for (int j = rectMouse[k].left/iPrecision; j < rectMouse[k].right/iPrecision; j++)
                                        {
                                                  byMotionScope[i].byMotionScope[j] = 1;
                                        }
                                }
                        }
                        else
                        {//draw from bottom-right to top-left
                                for (int i = rectMouse[k].bottom/iPrecision; i < rectMouse[k].top/iPrecision; i++)
                                {
                                        for (int j = rectMouse[k].right/iPrecision; j < rectMouse[k].left/iPrecision; j++)
                                        {
                                                  byMotionScope[i].byMotionScope[j] = 1;
                                        }
                                }
                        }
                }
        }
    }
    /**
	 * 函数:      initialMotionScopeRect
         * 函数描述:  初始化移动侦测区域和矩形区域参数
    */
    private void initialMotionScopeRect(HCNetSDK.byte96[] byMotionScope,int RectNums){
        for (int i = 0; i < 64; i++)
        {
                for (int j = 0; j < 96; j++)
                {
                        byMotionScope[i].byMotionScope[j] = 0;
                }
        }
        for (int i = 0; i < RectNums; i++)
        {
            rectSet[i] = new RECT();
            rectMouse[i] = new RECT();
        }
        iDetectIndex = 0;
    }
    /**********************移动侦测结束*************************/
    /**********************视频信号丢失开始*************************/
    /**
	 * 函数:      refreshVideoInLostAlarm
         * 函数描述:  读取视频信号丢失报警结构参数，并显示出来
    */
    private void refreshVideoInLostAlarm(){
        
        try{
            if (sAnotherName.equals("")) return;
            //获取报警事件处理能力集可以判断设备是否支持相关功能
            refreshVideoInLostAbility();
            //5、报警处理方式：监视器警告/声音报警/上传中心/Email/等的处理
            jCheckBoxMonitorAlarm.setSelected((struPicCfg.struVILost.strVILostHandleType.dwHandleType & 0x01) == 1);
            jCheckBoxAudioAlarm.setSelected(((struPicCfg.struVILost.strVILostHandleType.dwHandleType >> 1) & 0x01) == 1);
            jCheckBoxCenterAlarm.setSelected(((struPicCfg.struVILost.strVILostHandleType.dwHandleType >> 2) & 0x01) == 1);
            jCheckBoxAlarmout.setSelected(((struPicCfg.struVILost.strVILostHandleType.dwHandleType >> 3) & 0x01) == 1);
            jCheckBoxJpegEMailAlarm.setSelected(((struPicCfg.struVILost.strVILostHandleType.dwHandleType >> 4) & 0x01) == 1);
            
            //清除录像通道list
            listTraggerRecord.clear();
            listModelTraggerRecord.clear();for (int i = 0; i < strDeviceInfo.byAlarmOutPortNum; i++)
            {
                CheckListItem checkListItem = new CheckListItem(struPicCfg.struVILost.strVILostHandleType.byRelAlarmOut[i] == 1,
                                                                "AlarmOut" + (i + 1));
                listTraggerAlarmOut.add(checkListItem);
                listModelTraggerAlarmOut.addElement(checkListItem);    // 为触发报警输出List增加报警输出
            }
            //3、触发录像的通道  4、触发报警输出
            //触发报警输出通道list
            listTraggerAlarmOut.clear();
            listModelTraggerAlarmOut.clear();
//            for (int i = 0; i < strDeviceInfo.byAlarmOutPortNum; i++)
//            {
//                CheckListItem checkListItem = new CheckListItem(struPicCfg.struVILost.strVILostHandleType.byRelAlarmOut[i] == 1,
//                                                                "AlarmOut" + (i + 1));
//                listTraggerAlarmOut.add(checkListItem);
//                listModelTraggerAlarmOut.addElement(checkListItem);    // 为触发报警输出List增加报警输出
//            }
            /*获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
                对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）*/
            ArrayList ListDeviceSourceAllParas = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE , sFileName);
            for (int i=0;i<ListDeviceSourceAllParas.size();i++){
                //IP通道从33开始（前MAX_ANALOG_CHANNUM个是模拟通道）
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                if (!AnotherNameJoin.equals("")){
                    String NodeName2 = NewList.get(1) + "_" + NewList.get(3) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struPicCfg.struVILost.strVILostHandleType.byRelAlarmOut[Channel + HCNetSDK.MAX_ANALOG_ALARMOUT - 1] == 1, NodeName2);
                    listTraggerAlarmOut.add(checkListItem);
                    listModelTraggerAlarmOut.addElement(checkListItem);   
                }else{
                    String NodeName2 = NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struPicCfg.struVILost.strVILostHandleType.byRelAlarmOut[Channel - 1] == 1, NodeName2);
                    listTraggerAlarmOut.add(checkListItem);
                    listModelTraggerAlarmOut.addElement(checkListItem); 
                }
            }
            //6、布防时间
            strSchedTimeWeek = struPicCfg.struVILost.struAlarmTime;//[i].struAlarmTime[0];//只取一个时间段
            //1、是否处理信号丢失报警？
            jCheckBoxEnable.setSelected((struPicCfg.struVILost.byEnableHandleVILost  > 0));
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshMotionDetectAlarm()","系统在读取移动侦测结构参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      refreshVideoInLostAbility
         * 函数描述:  获取报警事件处理能力集可以判断设备是否支持相关功能
    */
    private void refreshVideoInLostAbility(){
        refreshVideoPicAlarmHanleAbility("VILostDetection");
    }
    /**
	 * 函数:      setVideoInLostAlarm
         * 函数描述:  设置视频信号丢失报警结构参数
    */
    private void setVideoInLostAlarm(){
        try{
            
            struPicCfg.struVILost.strVILostHandleType.dwHandleType = 0;
            struPicCfg.struVILost.strVILostHandleType.dwHandleType |= ((jCheckBoxMonitorAlarm.isSelected()?1:0));
            struPicCfg.struVILost.strVILostHandleType.dwHandleType |= ((jCheckBoxAudioAlarm.isSelected()?1:0) << 1);
            struPicCfg.struVILost.strVILostHandleType.dwHandleType |= ((jCheckBoxCenterAlarm.isSelected()?1:0) << 2);
            struPicCfg.struVILost.strVILostHandleType.dwHandleType |= ((jCheckBoxAlarmout.isSelected()?1:0) << 3);
            struPicCfg.struVILost.strVILostHandleType.dwHandleType |= ((jCheckBoxJpegEMailAlarm.isSelected()?1:0) << 4);

//            for (int i = 0; i <  strDeviceInfo.byAlarmOutPortNum; i++)
//            {
//                    struPicCfg.struVILost.strVILostHandleType.byRelAlarmOut[i] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
//            }
            //设置报警输出
            for (int i = 0; i < listTraggerAlarmOut.size(); i++)
            {
                String NodeName2 = listTraggerAlarmOut.get(i).getText();
                int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                if (NodeName2.lastIndexOf("IP") > -1){
                    struPicCfg.struVILost.strVILostHandleType.byRelAlarmOut[Channel + HCNetSDK.MAX_ANALOG_ALARMOUT -1] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
                }else{
                    struPicCfg.struVILost.strVILostHandleType.byRelAlarmOut[Channel - 1] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
                }
                    
            }
            
            struPicCfg.struVILost.byEnableHandleVILost = (byte) ((jCheckBoxEnable.isSelected() == true) ? 1 : 0);
            //设置通道图像结构体参数。包含移动侦测、视频信号丢失、遮挡报警参数结构体。
            //设置这三个结构体都要通过设置通道图像结构体参数结构体实现
            if (setStruPicCfg()){
                writeAlarmConfigLog( sAlarmTypes[1], CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);//"视频丢失"
            }else{
                writeAlarmConfigErrorLog( sAlarmTypes[1], CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);//"视频丢失"
            }
            //时间模板在存储按钮操作中都已经统一进行处理了。
//            setSelfTimeTemplate(jComboBoxSetAlarmTime,0,sAnotherName,sTemplateClass,"视频丢失",iChannel);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setVideoInLostAlarm()","系统在设置视频信号丢失报警结构参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**********************视频信号丢失结束*************************/
    /**********************遮挡报警开始*************************/
    /**
	 * 函数:      refreshHideAlarm
         * 函数描述:  读取遮挡报警结构参数，并显示出来
    */
    private void refreshHideAlarm(){
        try{
            //获取报警事件处理能力集可以判断设备是否支持相关功能
            refreshHideAlarmAbility();
    ////        1-低灵敏度，2-中灵敏度，3-高灵敏度 
            jSliderSensitive.setMaximum(3);
            jSliderSensitive.setMinimum(1);
            //1、是否设置侦测区域？
            jCheckBoxEnable.setSelected((struPicCfg.struHideAlarm.dwEnableHideAlarm > 0) ? true : false);
            if (struPicCfg.struHideAlarm.dwEnableHideAlarm > 0)
            {
    //            jComboBoxSensitive.setSelectedIndex(struPicCfg.struHideAlarm.dwEnableHideAlarm - 1);
                jTextFieldSensitive.setText(Integer.toString(struPicCfg.struHideAlarm.dwEnableHideAlarm));
                jSliderSensitive.setValue(struPicCfg.struHideAlarm.dwEnableHideAlarm);
            } else
            {
    //            jComboBoxSensitive.setSelectedIndex(0);
                jTextFieldSensitive.setText("1");
            }

            //清除录像通道list
            listTraggerRecord.clear();
            listModelTraggerRecord.clear();
            //3、触发录像的通道  4、触发报警输出
            //触发报警输出通道list
            listTraggerAlarmOut.clear();
            listModelTraggerAlarmOut.clear();
    //         for (int i = 0; i < strDeviceInfo.byAlarmOutPortNum; i++)
    //        {
    //            CheckListItem checkListItem = new CheckListItem(struPicCfg.struHideAlarm.strHideAlarmHandleType.byRelAlarmOut[i] == 1,
    //                                                                "AlarmOut" + (i + 1));
    //            listTraggerAlarmOut.add(checkListItem);
    //            listModelTraggerAlarmOut.addElement(checkListItem);    // 为触发报警输出List增加报警输出
    //        }

            /*获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
                   对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）*/
            ArrayList ListDeviceSourceAllParas = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE , sFileName);
            for (int i=0;i<ListDeviceSourceAllParas.size();i++){
                //IP通道从33开始（前MAX_ANALOG_CHANNUM个是模拟通道）
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                if (!AnotherNameJoin.equals("")){
                    String NodeName2 = NewList.get(1) + "_" + NewList.get(3) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struPicCfg.struHideAlarm.strHideAlarmHandleType.byRelAlarmOut[Channel + HCNetSDK.MAX_ANALOG_ALARMOUT - 1] == 1, NodeName2);
                    listTraggerAlarmOut.add(checkListItem);
                    listModelTraggerAlarmOut.addElement(checkListItem);   
                }else{
                    String NodeName2 = NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struPicCfg.struHideAlarm.strHideAlarmHandleType.byRelAlarmOut[Channel - 1] == 1, NodeName2);
                    listTraggerAlarmOut.add(checkListItem);
                    listModelTraggerAlarmOut.addElement(checkListItem); 
                }
            }

            jCheckBoxMonitorAlarm.setSelected((struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType & 0x01) == 1);
            jCheckBoxAudioAlarm.setSelected(((struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType >> 1) & 0x01) == 1);
            jCheckBoxCenterAlarm.setSelected(((struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType >> 2) & 0x01) == 1);
            jCheckBoxAlarmout.setSelected(((struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType >> 3) & 0x01) == 1);
            jCheckBoxJpegEMailAlarm.setSelected(((struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType >> 4) & 0x01) == 1);

            //6、布防时间

            strSchedTimeWeek = struPicCfg.struMotion.struAlarmTime;//[i].struAlarmTime[0];//只取一个时间段

            //重新给矩形赋值
            initialMotionScopeRect(byGridsScope, MAX_MOTION_NUM);
//            rectSet[0] = new RECT();
//            rectMouse[0] = new RECT();

            rectMouse[0].left = struPicCfg.struHideAlarm.wHideAlarmAreaTopLeftX / iMultiple;//g_rectHideAlarmSetArea.left;
            rectMouse[0].top = struPicCfg.struHideAlarm.wHideAlarmAreaTopLeftY / iMultiple;
            rectMouse[0].right = (struPicCfg.struHideAlarm.wHideAlarmAreaTopLeftX + struPicCfg.struHideAlarm.wHideAlarmAreaWidth) / iMultiple;
            rectMouse[0].bottom = (struPicCfg.struHideAlarm.wHideAlarmAreaTopLeftY + struPicCfg.struHideAlarm.wHideAlarmAreaHeight) / iMultiple;


    //        if (!(struPicCfg.struHideAlarm.wHideAlarmAreaTopLeftX == 0 && struPicCfg.struHideAlarm.wHideAlarmAreaTopLeftY == 0 
    //                && struPicCfg.struHideAlarm.wHideAlarmAreaWidth == 0 && struPicCfg.struHideAlarm.wHideAlarmAreaHeight == 0)) bHideGet = true;
    //        CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), HideGetCallBack, 0);
            iDetectIndex = 1;
            setMotionScope(byGridsScope);
            CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), GridsCallBack, 0);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshHideAlarm()","系统在读取遮挡报警结构参数，并进行显示过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    /**
	 * 函数:      refreshHideAlarmAbility
         * 函数描述:  获取报警事件处理能力集可以判断设备是否支持相关功能
    */
    private void refreshHideAlarmAbility(){
        refreshVideoPicAlarmHanleAbility("HideDetection");
    }
    /**
	 * 函数:      setHideAlarm
         * 函数描述:  设置遮挡报警结构参数
    */
    private void setHideAlarm(){
        try{
            //if (sAnotherName.equals("")) return;
            struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType = 0;
            struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType |= ((jCheckBoxMonitorAlarm.isSelected() ? 1 : 0) << 0);
            struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType |= ((jCheckBoxAudioAlarm.isSelected() ? 1 : 0) << 1);
            struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType |= ((jCheckBoxCenterAlarm.isSelected() ? 1 : 0) << 2);
            struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType |= ((jCheckBoxAlarmout.isSelected() ? 1 : 0) << 3);
            struPicCfg.struHideAlarm.strHideAlarmHandleType.dwHandleType |= ((jCheckBoxJpegEMailAlarm.isSelected() ? 1 : 0) << 4);

//            for (int i = 0; i < strDeviceInfo.byAlarmOutPortNum; i++)
//            {
//                struPicCfg.struHideAlarm.strHideAlarmHandleType.byRelAlarmOut[i] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
//            }
            //设置报警输出
            for (int i = 0; i < listTraggerAlarmOut.size(); i++)
            {
                String NodeName2 = listTraggerAlarmOut.get(i).getText();
                int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                if (NodeName2.lastIndexOf("IP") > -1){
                    struPicCfg.struHideAlarm.strHideAlarmHandleType.byRelAlarmOut[Channel + HCNetSDK.MAX_ANALOG_ALARMOUT -1] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
                }else{
                    struPicCfg.struHideAlarm.strHideAlarmHandleType.byRelAlarmOut[Channel - 1] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
                }
                    
            }
            
            if (jCheckBoxEnable.isSelected()) struPicCfg.struHideAlarm.dwEnableHideAlarm = Integer.parseInt(jTextFieldSensitive.getText().trim());
            else struPicCfg.struHideAlarm.dwEnableHideAlarm = 0;
            

            struPicCfg.struHideAlarm.wHideAlarmAreaTopLeftX = (short)(rectMouse[0].left * iMultiple);//g_rectHideAlarmSetArea.left;
            struPicCfg.struHideAlarm.wHideAlarmAreaTopLeftY = (short)(rectMouse[0].top * iMultiple);
            struPicCfg.struHideAlarm.wHideAlarmAreaWidth = (short)((rectMouse[0].right - rectMouse[0].left) * iMultiple);
            struPicCfg.struHideAlarm.wHideAlarmAreaHeight = (short)((rectMouse[0].bottom - rectMouse[0].top) * iMultiple);
            
            
            //灵敏度
            if (jCheckBoxEnable.isSelected()) struPicCfg.struHideAlarm.dwEnableHideAlarm = Integer.parseInt(jTextFieldSensitive.getText().trim());
            //设置通道图像结构体参数。包含移动侦测、视频信号丢失、遮挡报警参数结构体。
            //设置这三个结构体都要通过设置通道图像结构体参数结构体实现
            if (setStruPicCfg()){
                writeAlarmConfigLog( sAlarmTypes[2], CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);//"遮挡报警"
            }else{
                writeAlarmConfigErrorLog( sAlarmTypes[2], CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);//"遮挡报警"
            }
            //时间模板在存储按钮操作中都已经统一进行处理了。
//            setSelfTimeTemplate(jComboBoxSetAlarmTime,0,sAnotherName,sTemplateClass,"遮挡报警",iChannel);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setMotionDetectAlarm()","系统在保存移动侦测结构参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**********************遮挡报警结束*************************/
    /**********************通道图像结构体参数开始*************************/
    /**
	 * 函数:      refreshStruPicCfg
         * 函数描述:  读取通道图像结构体参数，这样移动侦测、视频信号丢失、遮挡报警参数就可以读出来并显示在组件上
    */
    private void refreshStruPicCfg(){
        if (sAnotherName.equals("")) return;
        
        IntByReference ibrBytesReturned = new IntByReference(0);//获取图片参数
        struPicCfg = new HCNetSDK.NET_DVR_PICCFG_V30();
        struPicCfg.write();
        Pointer lpPicConfig = struPicCfg.getPointer();
        boolean getDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_PICCFG_V30,
                new NativeLong(iChannel), lpPicConfig, struPicCfg.size(), ibrBytesReturned);
        struPicCfg.read();
        if (!getDVRConfigSuc){
            CommonParas.showErrorMessage(this.getRootPane(), sGetChannelImageParaFail, sAnotherName, sFileName);// "获取通道图像参数失败"
            //JOptionPane.showMessageDialog(this, "获取通道图像结构体失败。错误代码：" + CommonParas.hCNetSDK.NET_DVR_GetLastError());
            return;
        }
//        listIfRefrenshStru.set(0, true);
    }
    /**
	 * 函数:      setStruPicCfg
         * 函数描述:  设置通道图像结构体参数。包含移动侦测、视频信号丢失、遮挡报警参数结构体。
         *              设置这三个结构体都要通过设置通道图像结构体参数结构体实现
    */
    private boolean setStruPicCfg(){
        try{    
            struPicCfg.write();
            Pointer lpPicConfig = struPicCfg.getPointer();
            boolean setDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_PICCFG_V30,
                    new NativeLong(iChannel), lpPicConfig, struPicCfg.size());
            struPicCfg.read();
            
//            if (setDVRConfigSuc){
//                JOptionPane.showMessageDialog(rootPane, "保存参数成功");
//                return true;
//            }else {
//                CommonParas.showErrorMessage(this.getRootPane(), "设置图片参数失败！", sAnotherName, sFileName);
//                //JOptionPane.showMessageDialog(this, "设置图片参数失败。错误码：" + CommonParas.hCNetSDK.NET_DVR_GetLastError());
//                return false;
//            }
            return setDVRConfigSuc;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setStruPicCfg()","系统在设置通道图像结构体参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }

    /**********************通道图像结构体参数结束*************************/
    
    /**********************越界报警结构体参数开始*************************/
    /**
	 * 函数:      refreshStruTreversePlane
         * 函数描述:  刷新越界报警结构体参数，并预览、显示出来
    */
    private void refreshStruTreversePlane(){
        if (sAnotherName.equals("")) return;
            //获取事件能力集可以判断设备是否支持相关功能
            refreshStruTreversePlaneAbility();
            //获得当前区域ID的越界报警结构体参数，并预览、显示出来
            getTreversePlaneParas();
    }
    /**
	 * 函数:      getTreversePlaneParas
         * 函数描述:  读取越界报警结构体参数，并预览、显示出来
    */
    private void getTreversePlaneParas(){
        try{

            //读取结构体参数
            strChannelGroup = new HCNetSDK.NET_DVR_CHANNEL_GROUP();
            strStatusList = new HCNetSDK.STATUSLIST();
            strTreversePlane = new HCNetSDK.NET_VCA_TRAVERSE_PLANE_DETECTION();
            strChannelGroup.dwSize = strChannelGroup.size();
            strChannelGroup.dwChannel = iChannel;
            strChannelGroup.dwGroup = 0;
            int IndexOfID = jComboBoxID.getSelectedIndex() + 1;
            strChannelGroup.byID = (byte)IndexOfID;
            //strChannelGroup.byID = 1;
            strChannelGroup.dwPositionNo = 1;//场景位置索引号，IPC为0，IPD从1开始 

            strChannelGroup.write();
            strStatusList.write();
            strTreversePlane.write();

            Pointer lpChannelGroup = strChannelGroup.getPointer();
            Pointer lpStatusList = strStatusList.getPointer();
            Pointer lpTreversePlane = strTreversePlane.getPointer();

            boolean getDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_GetDeviceConfig(lUserID, HCNetSDK.NET_DVR_GET_TRAVERSE_PLANE_DETECTION,1,
                                            lpChannelGroup,strChannelGroup.size(),lpStatusList,lpTreversePlane,strTreversePlane.size());

            strChannelGroup.read();
            strStatusList.read();
            strTreversePlane.read();
            if (!getDVRConfigSuc){
                CommonParas.showErrorMessage(this.getRootPane(), MessageFormat.format(sGetParaFailed, sAlarmTypes[3]), sAnotherName, sFileName);//"获取越界侦测参数失败！"
                //JOptionPane.showMessageDialog(this, "获取越界侦测报警结构体参数失败。错误代码：" + CommonParas.hCNetSDK.NET_DVR_GetLastError());
                return;
            }

            //读取参数，并显示设置桌面组件
            //5、报警处理方式：监视器警告/声音报警/上传中心/Email/等的处理
            jCheckBoxMonitorAlarm.setSelected((strTreversePlane.struHandleException.dwHandleType & 0x01) == 1);
            jCheckBoxAudioAlarm.setSelected(((strTreversePlane.struHandleException.dwHandleType >> 1) & 0x01) == 1);
            jCheckBoxCenterAlarm.setSelected(((strTreversePlane.struHandleException.dwHandleType >> 2) & 0x01) == 1);
            jCheckBoxAlarmout.setSelected(((strTreversePlane.struHandleException.dwHandleType >> 3) & 0x01) == 1);
            jCheckBoxJpegEMailAlarm.setSelected(((strTreversePlane.struHandleException.dwHandleType >> 4) & 0x01) == 1);//Email上传抓图
            
            jCheckBoxPTZTrack.setSelected(((strTreversePlane.struHandleException.dwHandleType >> 11) & 0x01) == 1);//0x800: PTZ联动跟踪(球机跟踪目标)
            //6、布防时间
            strSchedTimeWeek = strTreversePlane.struAlarmSched;//[i].struAlarmTime[0];//只取一个时间段
            //3、触发录像的通道  4、触发报警输出
            //触发报警输出通道list
            listTraggerAlarmOut.clear();
            listModelTraggerAlarmOut.clear();
//            if (strDeviceInfo.byAlarmOutPortNum == strTreversePlane.struHandleException.dwMaxRelAlarmOutChanNum) JOptionPane.showMessageDialog(rootPane, "两者相等");
//            else JOptionPane.showMessageDialog(rootPane, "两者不相等");
            ArrayList ListDeviceSourceAllParas = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE , sFileName);
            //IP通道时，最大值为32，而实际则没有那么多，所以要取实际值；有时鱼眼可能只允许通道1进行录像，所以要选择dwMaxRelRecordChanNum；所以哪个最小取哪个
            int MinOfTwo = 0;//strTreversePlane.dwMaxRelRecordChanNum和ListDeviceSourceBean.size()中的最小值
            if (strTreversePlane.struHandleException.dwMaxRelAlarmOutChanNum < ListDeviceSourceAllParas.size()) MinOfTwo = strTreversePlane.struHandleException.dwMaxRelAlarmOutChanNum;
            else MinOfTwo = ListDeviceSourceAllParas.size();
            //dwMaxRelAlarmOutChanNum表示设备支持的报警输出数量
            for (int i = 0; i < MinOfTwo; i++)
            {
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                String NodeName2 = (String)NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                if (!AnotherNameJoin.equals(""))
                    NodeName2 = (String)NewList.get(1) + "_" + AnotherNameJoin + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                CheckListItem checkListItem = new CheckListItem(false,  NodeName2);
                listTraggerAlarmOut.add(checkListItem);
                listModelTraggerAlarmOut.addElement(checkListItem);    // 为触发报警输出List增加报警输出
            }
            //dwRelAlarmOutChanNum表示实际设置的报警输出数量
            for (int j = 0;j<strTreversePlane.struHandleException.dwRelAlarmOutChanNum;j++){
                //dwRelAlarmOut[j]其值表示报警输出通道号(从1开始),所以要 -1
                int Channel = strTreversePlane.struHandleException.dwRelAlarmOut[j];//从1开始
                for (int i=0;i<listTraggerAlarmOut.size();i++){
                    String NodeName2 = listTraggerAlarmOut.get(i).getText();
                    int Channel2 = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    if (NodeName2.lastIndexOf("IP") > -1){
                        if ((Channel - HCNetSDK.MAX_ANALOG_ALARMOUT) == Channel2) {
                            listTraggerAlarmOut.get(i).setCheck(true);
                            continue;
                        }
                    }else{
                        if (Channel == Channel2) {
                            listTraggerAlarmOut.get(i).setCheck(true);
                            continue;
                        }
                    }
                }
             }

            //触发录像通道list
            listTraggerRecord.clear();
            listModelTraggerRecord.clear();
//            if (strDeviceInfo.byChanNum == strTreversePlane.dwMaxRelRecordChanNum) JOptionPane.showMessageDialog(rootPane, "两者相等");
//            else JOptionPane.showMessageDialog(rootPane, "两者不相等");
            
            /*获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
                    对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）*/
            ArrayList ListDeviceSourceAllParas2 = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE , sFileName);
            //IP通道时，最大值为32，而实际则没有那么多，所以要取实际值；有时鱼眼可能只允许通道1进行录像，所以要选择dwMaxRelRecordChanNum；所以哪个最小取哪个
            MinOfTwo = 0;//strTreversePlane.dwMaxRelRecordChanNum和ListDeviceSourceBean.size()中的最小值
            if (strTreversePlane.dwMaxRelRecordChanNum < ListDeviceSourceAllParas2.size()) MinOfTwo = strTreversePlane.dwMaxRelRecordChanNum;
            else MinOfTwo = ListDeviceSourceAllParas2.size();
            
            for (int i = 0; i < MinOfTwo; i++)
            {
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas2.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                String NodeName2 = (String)NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                if (!AnotherNameJoin.equals(""))
                    NodeName2 = (String)NewList.get(1) + "_" + AnotherNameJoin + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                //CheckListItem checkListItem = new CheckListItem(false, ListDeviceSourceBean.get(i).getNodename());
                CheckListItem checkListItem = new CheckListItem(false, NodeName2);
                listTraggerRecord.add(checkListItem);
                listModelTraggerRecord.addElement(checkListItem);    // 为触发录像List增加报警输出
            }

            for (int j = 0;j<strTreversePlane.dwRelRecordChanNum;j++){
                //strTreversePlane.byRelRecordChan[j]其值表示录像通道号，从1开始，所以要 -1
                //IP通道从33开始（前MAX_ANALOG_CHANNUM个是模拟通道）
                int Channel = strTreversePlane.byRelRecordChan[j];//从1开始
                for (int i=0;i<listTraggerRecord.size();i++){
                    String NodeName2 = listTraggerRecord.get(i).getText();
                    int Channel2 = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    if (NodeName2.lastIndexOf("IP") > -1){
                        if ((Channel - HCNetSDK.MAX_ANALOG_CHANNUM) == Channel2) {
                            listTraggerRecord.get(i).setCheck(true);
                            continue;
                        }
                    }else{
                        if (Channel == Channel2) {
                            listTraggerRecord.get(i).setCheck(true);
                            continue;
                        }
                    }
                }
//                if (strDeviceInfo.byChanNum == 0) listTraggerRecord.get(strTreversePlane.byRelRecordChan[j] - HCNetSDK.MAX_ANALOG_CHANNUM - 1 ).setCheck(true);
//                else listTraggerRecord.get(strTreversePlane.byRelRecordChan[j] - 1).setCheck(true);
            }
            
            //1、是否设置越界侦测？
            jCheckBoxEnable.setSelected((strTreversePlane.byEnable > 0) ? true : false);
            //2、灵敏度
            jTextFieldSensitive.setText(Byte.toString(strTreversePlane.struAlertParam[IndexOfID - 1].bySensitivity));
            jSliderSensitive.setValue(strTreversePlane.struAlertParam[IndexOfID - 1].bySensitivity);
            
            //如果有的话，输出显示第一条警戒线及方向
            HCNetSDK.NET_VCA_LINE strPlaneLine = strTreversePlane.struAlertParam[IndexOfID - 1].struPlaneBottom;

            TraversePlaneCallBack.initialParas();//初始化越界侦测回调函数的参数
            TraversePlaneCallBack.setIndex(IndexOfID);
            int Px = (int)(strPlaneLine.struStart.fX * panelPlay.getWidth());
            int Py = (int)(strPlaneLine.struStart.fY * panelPlay.getHeight());
            TraversePlaneCallBack.setPosition1(new POINT(Px,Py));
            Px = (int)(strPlaneLine.struEnd.fX * panelPlay.getWidth());
            Py = (int)(strPlaneLine.struEnd.fY * panelPlay.getHeight());
            TraversePlaneCallBack.setPosition2(new POINT(Px,Py));
            TraversePlaneCallBack.setArrowDirection(strTreversePlane.struAlertParam[IndexOfID - 1].dwCrossDirection);

            CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), TraversePlaneCallBack, 0);
//            listIfRefrenshStru.set(1, true);
            //设置警戒线对话框
            jComboBoxCrossDirection.setSelectedIndex(strTreversePlane.struAlertParam[IndexOfID - 1].dwCrossDirection);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getTreversePlaneParas()","系统在读取越界报警结构体参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    
    }
    /**
	 * 函数:      refreshTreversePlaneAbility
         * 函数描述:  获取越界侦测报警事件处理能力集可以判断设备是否支持相关功能
    */
    private void refreshStruTreversePlaneAbility(){
        try{
            
            String XMLOut = new String(pOutBufAlarm).trim();
            DomXML domXML = new DomXML(XMLOut,sFileName);
             //获取越界侦测支持的最大警戒线个数、警戒面支持的跨越方向: 0-双向，1-从左到右2-从右到左、灵敏度

            //获取最大警戒线个数
            String ReturnV = domXML.readSecondLevelElementValue("TraversingVirtualPlane", "alertlineNum");

            if (!(ReturnV.equals(""))) {
                int alertlineNum = Integer.parseInt(ReturnV);
                if (alertlineNum > 0){
                    String[] Items = new String[alertlineNum];
                    for (int i=1;i<=alertlineNum;i++){
                        Items[i-1] = Integer.toString(i);
                    }
                    jComboBoxID.setModel(new DefaultComboBoxModel(Items) );
                }
            }
            
            //警戒面支持的跨越方向: 0-双向，1-从左到右2-从右到左。例如：“0,1,2”、“bothway,leftToRight,rightToLeft”
            ReturnV = domXML.readThirdLevelAttributeValue("TraversingVirtualPlane", "AlertLine", "crossDirection", "opt");
            if (!(ReturnV.equals(""))) {
                String[] Driection = ReturnV.split(",");
                String[] Items = new String[Driection.length];
                for (int i=0;i<Driection.length;i++) {
                    //双向 A <-> B, 由左至右 A -> B, 由右至左 B -> A
                    switch (Driection[i]){
                        case "bothway":
                            Items[i] = sDirections[0];//"双向（A <-> B）"
                            break;
                        case "leftToRight":
                            Items[i] = sDirections[1];//"由左至右（A -> B）"
                            break;
                        case "rightToLeft":
                            Items[i] = sDirections[2];//"由右至左（B -> A）"
                            break;
                    }
                            
                }
                jComboBoxCrossDirection.setModel(new DefaultComboBoxModel(Items) );
            }

            //灵敏度sensitivityLevel 
            ReturnV = domXML.readThirdLevelAttributeValue("TraversingVirtualPlane", "AlertLine", "sensitivityLevel", "max");
            if (!(ReturnV.equals(""))) {
                int SensitivityLevel = Integer.parseInt(ReturnV);
                jSliderSensitive.setMaximum(SensitivityLevel);
                jSliderSensitive.setMinimum(1);
            }
            
            sHandleTypeCode = "";
            //获得异常处理方式，各种异常处理方式的列表
            //主要包括："","monitor","audio","center","alarmout","picture","wirelesslight","","uploadftp","","ptztrack"
            String ReturnV2 = domXML.readSecondLevelAttributeValue("TraversingVirtualPlane", "alarmHandleType", "opt");//optional中文解释	"可选择的"
            if (!(ReturnV2.equals(""))) {
                sHandleTypeCode = ReturnV2.toLowerCase();//将异常的处理方式保存
            }
            refreshOCXByHandleTypeCodes(sHandleTypeCode);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshTreversePlaneAbility()","系统在获取报警事件处理能力集可以判断设备是否支持相关功能过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    /**
	 * 函数:      setStrTreversePlane
         * 函数描述:  设置越界报警结构体参数.
    */
    private void setStrTreversePlane(){
        try{  
            //if (sAnotherName.equals("")) return;
            //1、设置越界侦测
            strTreversePlane.byEnable = (byte) ((jCheckBoxEnable.isSelected() == true) ? 1 : 0);
            
            //4、触发报警输出
            int CountAlarmOut = 0;
            for (int i = 0; i < listTraggerAlarmOut.size(); i++)
            {
                    if (listTraggerAlarmOut.get(i).getCheck()) {
                        String NodeName2 = listTraggerAlarmOut.get(i).getText();
                        int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                        if (NodeName2.lastIndexOf("IP") > -1)
                            strTreversePlane.struHandleException.dwRelAlarmOut[CountAlarmOut] = Channel + HCNetSDK.MAX_ANALOG_ALARMOUT;
                        else
                            strTreversePlane.struHandleException.dwRelAlarmOut[CountAlarmOut] = Channel;
                        CountAlarmOut++;
                    }
            }
            strTreversePlane.struHandleException.dwRelAlarmOutChanNum = CountAlarmOut;

            //3、触发录像的通道
            int CountRecordChan = 0;
            for (int i = 0; i< listTraggerRecord.size(); i++)
            {
                if (listTraggerRecord.get(i).getCheck()){
                    String NodeName2 = listTraggerRecord.get(i).getText();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    if (NodeName2.lastIndexOf("IP") > -1)
                        strTreversePlane.byRelRecordChan[CountRecordChan] = Channel + HCNetSDK.MAX_ANALOG_CHANNUM;
                    else 
                        strTreversePlane.byRelRecordChan[CountRecordChan] = Channel;
                    CountRecordChan++;
                }
            }
            strTreversePlane.dwRelRecordChanNum = CountRecordChan;

            //5、报警处理方式：监视器警告/声音报警/上传中心/Email/等的处理
            strTreversePlane.struHandleException.dwHandleType = 0;
            strTreversePlane.struHandleException.dwHandleType |= ((jCheckBoxMonitorAlarm.isSelected()?1:0) << 0);
            strTreversePlane.struHandleException.dwHandleType |= ((jCheckBoxAudioAlarm.isSelected()?1:0) << 1);
            strTreversePlane.struHandleException.dwHandleType |= ((jCheckBoxCenterAlarm.isSelected()?1:0) << 2);
            strTreversePlane.struHandleException.dwHandleType |= ((jCheckBoxAlarmout.isSelected()?1:0) << 3);
            strTreversePlane.struHandleException.dwHandleType |= ((jCheckBoxJpegEMailAlarm.isSelected()?1:0) << 4);
            //是否联动球机目标（只有鱼眼具有此功能），需要判断
            //if (HCNetSDKExpand.isFishEye(strDeviceInfo,strIpparaCfg,iChannel,sFileName))
            strTreversePlane.struHandleException.dwHandleType |= ((jCheckBoxPTZTrack.isSelected()?1:0) << 11);//0x800: PTZ联动跟踪(球机跟踪目标)

            //6、布防时间，已经在模板编辑中处理过了，已经赋过值了
            //7、警戒线赋值。包括：警戒面底边、穿越方向、灵敏度、检测目标
            //2、灵敏度在setStrPlaneLine()函数中统一设置了，
            setStrPlaneLine();//当前的警戒线存储。之前的警戒线随时就存储了。
            
            //8、存储越界侦测结构体参数
           
            strChannelGroup = new HCNetSDK.NET_DVR_CHANNEL_GROUP();
            strStatusList = new HCNetSDK.STATUSLIST();
            
            strChannelGroup.dwSize = strChannelGroup.size();
            strChannelGroup.dwChannel = iChannel;
            strChannelGroup.dwGroup = 0;
            //strChannelGroup.byID = 1;//以后待变，以实际为准
            int IndexOfID = jComboBoxID.getSelectedIndex() + 1;
            strChannelGroup.byID = (byte)IndexOfID;
            strChannelGroup.dwPositionNo = 0;//场景位置索引号，IPC为0，IPD从1开始 
            
            strChannelGroup.write();
            strStatusList.write();
            strTreversePlane.write();

            Pointer lpChannelGroup = strChannelGroup.getPointer();
            Pointer lpStatusList = strStatusList.getPointer();
            Pointer lpTreversePlane = strTreversePlane.getPointer();

            boolean setDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_SetDeviceConfig(lUserID, HCNetSDK.NET_DVR_SET_TRAVERSE_PLANE_DETECTION,1,
                lpChannelGroup,strChannelGroup.size(),lpStatusList,lpTreversePlane,strTreversePlane.size());
            strChannelGroup.read();
            strStatusList.read();
            strTreversePlane.read();
            
            if (setDVRConfigSuc){
                writeAlarmConfigLog( sAlarmTypes[3], CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);//"越界侦测"
            }else{
                writeAlarmConfigErrorLog( sAlarmTypes[3], CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);//"越界侦测"
            }
            
            //时间模板在存储按钮操作中都已经统一进行处理了。
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setStrTreversePlane()","系统在设置越界报警结构体参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      setStrPlaneLine
         * 函数描述:  设置当前的越界报警的警戒线参数 
    */
    private void setStrPlaneLine(){
        try{
            int Index = TraversePlaneCallBack.getIndex();
            if(Index < 1) return;
            float PanelWidth = (float)panelPlay.getWidth();
            float PanelHight = (float)panelPlay.getHeight();
            //警戒面底边
            strTreversePlane.struAlertParam[Index-1].struPlaneBottom.struStart.fX = ((float)TraversePlaneCallBack.getPosition1().x)/PanelWidth;
            strTreversePlane.struAlertParam[Index-1].struPlaneBottom.struStart.fY = ((float)TraversePlaneCallBack.getPosition1().y)/PanelHight;

            strTreversePlane.struAlertParam[Index-1].struPlaneBottom.struEnd.fX = ((float)TraversePlaneCallBack.getPosition2().x)/PanelWidth;
            strTreversePlane.struAlertParam[Index-1].struPlaneBottom.struEnd.fY = ((float)TraversePlaneCallBack.getPosition2().y)/PanelHight;
            //穿越方向
            strTreversePlane.struAlertParam[Index-1].dwCrossDirection = jComboBoxCrossDirection.getSelectedIndex();
            //灵敏度
            strTreversePlane.struAlertParam[Index-1].bySensitivity = Byte.parseByte(jTextFieldSensitive.getText().trim());
            //检测目标
            strTreversePlane.struAlertParam[Index-1].byDetectionTarget = 0;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setStrPlaneLine()","系统在设置设置当前的越界报警的警戒线参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**********************越界报警结构体参数结束*************************/
    /**********************区域入侵报警结构体参数开始*************************/
    /**
	 * 函数:      refreshStruIntrusion
         * 函数描述:  刷新区域入侵报警结构体参数，并预览、显示出来
    */
    private void refreshStruFieldDetecion(){
            if (sAnotherName.equals("")) return;
            //获取事件能力集可以判断设备是否支持相关功能
            refreshStruFieldDetecionAbility();
            //读取区域入侵结构体参数，并预览、显示出来
            getFieldDetecionParas();
    }
    /**
	 * 函数:      getFieldDetecionParas
         * 函数描述:  读取区域入侵结构体参数，并预览、显示出来
    */
    private void getFieldDetecionParas(){
        try{
            
            //刷新操作
            //读取结构体参数
            strChannelGroup = new HCNetSDK.NET_DVR_CHANNEL_GROUP();
            strStatusList = new HCNetSDK.STATUSLIST();
            strFieldDetecion = new HCNetSDK.NET_VCA_FIELDDETECION ();

            strChannelGroup.dwSize = strChannelGroup.size();
            strChannelGroup.dwChannel = iChannel;
            strChannelGroup.dwGroup = 0;
            int IndexOfID = jComboBoxID.getSelectedIndex() + 1;
            strChannelGroup.byID = (byte)IndexOfID;
            //strChannelGroup.byID = 1;
            strChannelGroup.dwPositionNo = 1;//场景位置索引号，IPC为0，IPD从1开始 

            strChannelGroup.write();
            strStatusList.write();
            strFieldDetecion.write();

            Pointer lpChannelGroup = strChannelGroup.getPointer();
            Pointer lpStatusList = strStatusList.getPointer();
            Pointer lpFieldDetecion = strFieldDetecion.getPointer();

            boolean getDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_GetDeviceConfig(lUserID, HCNetSDK.NET_DVR_GET_FIELD_DETECTION,1,
                    lpChannelGroup,strChannelGroup.size(),lpStatusList,lpFieldDetecion,strFieldDetecion.size());

            strChannelGroup.read();
            strStatusList.read();
            strFieldDetecion.read();
            if (!getDVRConfigSuc){
                CommonParas.showErrorMessage(this.getRootPane(), MessageFormat.format(sGetParaFailed, sAlarmTypes[4]), sAnotherName, sFileName);//"获取区域入侵报警参数失败。"
                return;
            }

            //读取参数，并显示设置桌面组件
            //5、报警处理方式：监视器警告/声音报警/上传中心/Email/等的处理
            jCheckBoxMonitorAlarm.setSelected((strFieldDetecion.struHandleException.dwHandleType & 0x01) == 1);
            jCheckBoxAudioAlarm.setSelected(((strFieldDetecion.struHandleException.dwHandleType >> 1) & 0x01) == 1);
            jCheckBoxCenterAlarm.setSelected(((strFieldDetecion.struHandleException.dwHandleType >> 2) & 0x01) == 1);
            jCheckBoxAlarmout.setSelected(((strFieldDetecion.struHandleException.dwHandleType >> 3) & 0x01) == 1);
            jCheckBoxJpegEMailAlarm.setSelected(((strFieldDetecion.struHandleException.dwHandleType >> 4) & 0x01) == 1);//Email上传抓图
            jCheckBoxPTZTrack.setSelected(((strFieldDetecion.struHandleException.dwHandleType >> 11) & 0x01) == 1);//0x800: PTZ联动跟踪(球机跟踪目标)
            //6、布防时间
            strSchedTimeWeek = strFieldDetecion.struAlarmSched;//[i].struAlarmTime[0];//只取一个时间段
            //3、触发录像的通道  4、触发报警输出
            //触发报警输出通道list
            listTraggerAlarmOut.clear();
            listModelTraggerAlarmOut.clear();
//            if (strDeviceInfo.byAlarmOutPortNum == strFieldDetecion.struHandleException.dwMaxRelAlarmOutChanNum) JOptionPane.showMessageDialog(rootPane, "两者相等");
//            else JOptionPane.showMessageDialog(rootPane, "两者不相等");
//            for (int i = 0; i < strFieldDetecion.struHandleException.dwMaxRelAlarmOutChanNum; i++)
//            {
//                CheckListItem checkListItem = new CheckListItem(false,  "AlarmOut" + (i + 1));
//                listTraggerAlarmOut.add(checkListItem);
//                listModelTraggerAlarmOut.addElement(checkListItem);    // 为触发报警输出List增加报警输出
//            }
//            for (int j = 0;j<strFieldDetecion.struHandleException.dwRelAlarmOutChanNum;j++){
//                //dwRelAlarmOut[j]其值表示报警输出通道号(从1开始),所以要 -1
//                listTraggerAlarmOut.get(strFieldDetecion.struHandleException.dwRelAlarmOut[j] - 1).setCheck(true);
//            }
            ArrayList ListDeviceSourceAllParas = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE , sFileName);
            //IP通道时，最大值为32，而实际则没有那么多，所以要取实际值；有时鱼眼可能只允许通道1进行录像，所以要选择dwMaxRelRecordChanNum；所以哪个最小取哪个
            int MinOfTwo = 0;//strTreversePlane.dwMaxRelRecordChanNum和ListDeviceSourceBean.size()中的最小值
            if (strFieldDetecion.struHandleException.dwMaxRelAlarmOutChanNum < ListDeviceSourceAllParas.size()) MinOfTwo = strFieldDetecion.struHandleException.dwMaxRelAlarmOutChanNum;
            else MinOfTwo = ListDeviceSourceAllParas.size();
            //dwMaxRelAlarmOutChanNum表示设备支持的报警输出数量
            for (int i = 0; i < MinOfTwo; i++)
            {
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                String NodeName2 = (String)NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                if (!AnotherNameJoin.equals(""))
                    NodeName2 = (String)NewList.get(1) + "_" + AnotherNameJoin + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                CheckListItem checkListItem = new CheckListItem(false,  NodeName2);
                listTraggerAlarmOut.add(checkListItem);
                listModelTraggerAlarmOut.addElement(checkListItem);    // 为触发报警输出List增加报警输出
            }
            //dwRelAlarmOutChanNum表示实际设置的报警输出数量
            for (int j = 0;j<strFieldDetecion.struHandleException.dwRelAlarmOutChanNum;j++){
                //dwRelAlarmOut[j]其值表示报警输出通道号(从1开始),所以要 -1
                int Channel = strFieldDetecion.struHandleException.dwRelAlarmOut[j];//从1开始
                for (int i=0;i<listTraggerAlarmOut.size();i++){
                    String NodeName2 = listTraggerAlarmOut.get(i).getText();
                    int Channel2 = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    if (NodeName2.lastIndexOf("IP") > -1){
                        if ((Channel - HCNetSDK.MAX_ANALOG_ALARMOUT) == Channel2) {
                            listTraggerAlarmOut.get(i).setCheck(true);
                            continue;
                        }
                    }else{
                        if (Channel == Channel2) {
                            listTraggerAlarmOut.get(i).setCheck(true);
                            continue;
                        }
                    }
                }
             }

            //触发录像通道list
            listTraggerRecord.clear();
            listModelTraggerRecord.clear();
//            if (strDeviceInfo.byChanNum == strFieldDetecion.dwMaxRelRecordChanNum) JOptionPane.showMessageDialog(rootPane, "两者相等");
//            else JOptionPane.showMessageDialog(rootPane, "两者不相等");

            /*获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
                    对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）*/
            ArrayList ListDeviceSourceAllParas2 = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE , sFileName);
            //IP通道时，最大值为32，而实际则没有那么多，所以要取实际值；有时鱼眼可能只允许通道1进行录像，所以要选择dwMaxRelRecordChanNum；所以哪个最小取哪个
            MinOfTwo = 0;//dwMaxRelRecordChanNum和ListDeviceSourceBean.size()中的最小值
            if (strFieldDetecion.dwMaxRelRecordChanNum < ListDeviceSourceAllParas2.size()) MinOfTwo = strFieldDetecion.dwMaxRelRecordChanNum;
            else MinOfTwo = ListDeviceSourceAllParas2.size();
            
            for (int i = 0; i < MinOfTwo; i++)
            {
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas2.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                String NodeName2 = (String)NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                if (!AnotherNameJoin.equals(""))
                    NodeName2 = (String)NewList.get(1) + "_" + AnotherNameJoin + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                CheckListItem checkListItem = new CheckListItem(false, NodeName2);
                listTraggerRecord.add(checkListItem);
                listModelTraggerRecord.addElement(checkListItem);    // 为触发录像List增加报警输出
            }
//            for (int j = 0;j<strFieldDetecion.dwRelRecordChanNum;j++){
//                //strTreversePlane.byRelRecordChan[j]其值表示录像通道号，从1开始，所以要 -1
//                //IP通道从33开始（前MAX_ANALOG_CHANNUM个是模拟通道）
//                
//                if (strDeviceInfo.byChanNum == 0) listTraggerRecord.get(strFieldDetecion.byRelRecordChan[j] - HCNetSDK.MAX_ANALOG_CHANNUM - 1 ).setCheck(true);
//                else listTraggerRecord.get(strFieldDetecion.byRelRecordChan[j] - 1).setCheck(true);
//            }
            for (int j = 0;j<strFieldDetecion.dwRelRecordChanNum;j++){
                //strTreversePlane.byRelRecordChan[j]其值表示录像通道号，从1开始，所以要 -1
                //IP通道从33开始（前MAX_ANALOG_CHANNUM个是模拟通道）
                int Channel = strFieldDetecion.byRelRecordChan[j];//从1开始
                for (int i=0;i<listTraggerRecord.size();i++){
                    String NodeName2 = listTraggerRecord.get(i).getText();
                    int Channel2 = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    if (NodeName2.lastIndexOf("IP") > -1){
                        if ((Channel - HCNetSDK.MAX_ANALOG_CHANNUM) == Channel2) {
                            listTraggerRecord.get(i).setCheck(true);
                            continue;
                        }
                    }else{
                        if (Channel == Channel2) {
                            listTraggerRecord.get(i).setCheck(true);
                            continue;
                        }
                    }
                }
//                if (strDeviceInfo.byChanNum == 0) listTraggerRecord.get(strTreversePlane.byRelRecordChan[j] - HCNetSDK.MAX_ANALOG_CHANNUM - 1 ).setCheck(true);
//                else listTraggerRecord.get(strTreversePlane.byRelRecordChan[j] - 1).setCheck(true);
            }
            
            //1、是否设置越界侦测？
            jCheckBoxEnable.setSelected((strFieldDetecion.byEnable > 0) ? true : false);
            //触发时间阈值、占比、灵敏度开始做
            //2、灵敏度
            //区域入侵报警的灵敏度范围在refreshFieldDetecionAbility（）中已经设置过了
            if (strFieldDetecion.struIntrusion[IndexOfID - 1].bySensitivity == 0) strFieldDetecion.struIntrusion[IndexOfID - 1].bySensitivity =1;
            jTextFieldSensitive.setText(Byte.toString(strFieldDetecion.struIntrusion[IndexOfID - 1].bySensitivity));
            jSliderSensitive.setValue(strFieldDetecion.struIntrusion[IndexOfID - 1].bySensitivity);
            //占比
            jSpinnerDuration.setValue(strFieldDetecion.struIntrusion[IndexOfID - 1].wDuration);
            //触发时间阈值
            jSpinnerRate.setValue(strFieldDetecion.struIntrusion[IndexOfID - 1].byRate);
            //如果有的话，输出显示第一个警戒区域

            //回调函数的初始化操作
            FieldDetecionCallBack.setIndex(IndexOfID);
            int PointNum = strFieldDetecion.struIntrusion[IndexOfID - 1].struRegion.dwPointNum;
            if (PointNum > 0) {
                FieldDetecionCallBack.initialParas(PointNum);
                //FieldDetecionCallBack.setPointsNum(strFieldDetecion.struIntrusion[IndexOfID - 1].struRegion.dwPointNum);
                jComboBoxPointNum.setSelectedItem(Integer.toString(PointNum));
                for (int i=0;i<PointNum;i++){
                    int Px = (int)(strFieldDetecion.struIntrusion[IndexOfID - 1].struRegion.struPos[i].fX* panelPlay.getWidth());
                    int Py = (int)(strFieldDetecion.struIntrusion[IndexOfID - 1].struRegion.struPos[i].fY * panelPlay.getHeight());
                    FieldDetecionCallBack.addPoint(new POINT(Px,Py));
                }
            }else {
                FieldDetecionCallBack.initialParas(Integer.parseInt(jComboBoxPointNum.getSelectedItem().toString()));
            }
            

            //调用回调函数
            CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), FieldDetecionCallBack, 0);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getFieldDetecionParas()","系统在读取区域入侵报警结构体参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      refreshFieldDetecionAbility
         * 函数描述:  获取报警事件处理能力集可以判断设备是否支持相关功能
    */
    private void refreshStruFieldDetecionAbility(){
        try{
            
            String XMLOut = new String(pOutBufAlarm).trim();
            DomXML domXML = new DomXML(XMLOut,sFileName);
            //获取最大警戒线个数、每个区域有效点数、触发时间阈值、灵敏度、占比

            //获取最大警戒线个数
            String ReturnV = domXML.readSecondLevelElementValue("FieldDetection", "intrusiongionNum");

            if (!(ReturnV.equals(""))) {
                int intrusiongionNum = Integer.parseInt(ReturnV);
                if (intrusiongionNum > 0){
                    String[] Items = new String[intrusiongionNum];
                    for (int i=1;i<=intrusiongionNum;i++){
                        Items[i-1] = Integer.toString(i);
                    }
                    jComboBoxID.setModel(new DefaultComboBoxModel(Items) );
                }
            }
            //每个区域有效点数 
            ReturnV = domXML.readThirdLevelAttributeValue("FieldDetection", "Intrusiongion", "regionNum", "max");
            //因为现在所有设备的最小点数都为4，所以最小点数就为4，不再读取
            if (!(ReturnV.equals(""))) {
                int regionNum = Integer.parseInt(ReturnV);
                if (regionNum >= 4){
                    String[] Items = new String[regionNum-4+1];
                    for (int i=4;i<=regionNum;i++){
                        Items[i-4] = Integer.toString(i);
                    }
                    jComboBoxPointNum.setModel(new DefaultComboBoxModel(Items) );
                }
            }
            
            //触发时间阈值
            ReturnV = domXML.readThirdLevelAttributeValue("FieldDetection", "Intrusiongion", "duration", "max");
            if (!(ReturnV.equals(""))) {
                int DurationTime = Integer.parseInt(ReturnV);
                jSpinnerDuration.setModel(new SpinnerNumberModel(5, 1, DurationTime, 1));
                jLabelDuration.setVisible(true);
                jSpinnerDuration.setVisible(true);
                jLabelDurationUnit.setVisible(true);
            }else{
                jLabelDuration.setVisible(false);
                jSpinnerDuration.setVisible(false);
                jLabelDurationUnit.setVisible(false);
            }
            

            //占比rate;注意：球机没有占比这一项
            ReturnV = domXML.readThirdLevelAttributeValue("FieldDetection", "Intrusiongion", "rate", "max");
            if (!(ReturnV.equals(""))) {
                int Rate = Integer.parseInt(ReturnV);
                jSpinnerRate.setModel(new SpinnerNumberModel(12, 1, Rate, 1));
                jLabelRate.setVisible(true);
                jSpinnerRate.setVisible(true);
                jLabelRateUnit.setVisible(true);
            }else{
                jLabelRate.setVisible(false);
                jSpinnerRate.setVisible(false);
                jLabelRateUnit.setVisible(false);
            }

            //灵敏度sensitivityLevel 
            ReturnV = domXML.readThirdLevelAttributeValue("FieldDetection", "Intrusiongion", "sensitivityLevel", "max");
            if (!(ReturnV.equals(""))) {
                int SensitivityLevel = Integer.parseInt(ReturnV);
                jSliderSensitive.setMaximum(SensitivityLevel);
                jSliderSensitive.setMinimum(1);
            }
            sHandleTypeCode = "";
            //获得异常处理方式，各种异常处理方式的列表
            //主要包括："","monitor","audio","center","alarmout","picture","wirelesslight","","uploadftp","","ptztrack"
            String ReturnV2 = domXML.readSecondLevelAttributeValue("FieldDetection", "alarmHandleType", "opt");//optional中文解释	"可选择的"
            if (!(ReturnV2.equals(""))) {
                sHandleTypeCode = ReturnV2.toLowerCase();//将异常的处理方式保存
            }
            refreshOCXByHandleTypeCodes(sHandleTypeCode);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshFieldDetecionAbility()","系统在获取报警事件处理能力集可以判断设备是否支持相关功能过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    /**
	 * 函数:      setStruFieldDetecion
         * 函数描述:  设置区域入侵报警结构体参数
    */
    private void setStruFieldDetecion(){
        try{         
            //1、设置区域入侵
            strFieldDetecion.byEnable = (byte) ((jCheckBoxEnable.isSelected() == true) ? 1 : 0);
            //2、灵敏度
            
            //4、触发报警输出
            int CountAlarmOut = 0;
//            for (int i = 0; i < listTraggerAlarmOut.size(); i++)
//            {
//                    if (listTraggerAlarmOut.get(i).getCheck()) {
//                        strFieldDetecion.struHandleException.dwRelAlarmOut[CountAlarmOut] = i + 1;
//                        CountAlarmOut++;
//                    }
//            }
            for (int i = 0; i < listTraggerAlarmOut.size(); i++)
            {
                    if (listTraggerAlarmOut.get(i).getCheck()) {
                        String NodeName2 = listTraggerAlarmOut.get(i).getText();
                        int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                        if (NodeName2.lastIndexOf("IP") > -1)
                            strFieldDetecion.struHandleException.dwRelAlarmOut[CountAlarmOut] = Channel + HCNetSDK.MAX_ANALOG_ALARMOUT;
                        else
                            strFieldDetecion.struHandleException.dwRelAlarmOut[CountAlarmOut] = Channel;
                        CountAlarmOut++;
                    }
            }
            strFieldDetecion.struHandleException.dwRelAlarmOutChanNum = CountAlarmOut;
            //3、触发录像的通道
            int CountRecordChan = 0;
//            for (int i = 0; i< listTraggerRecord.size(); i++)
//            {
//                if (listTraggerRecord.get(i).getCheck()){
//                    if (strDeviceInfo.byChanNum == 0) strFieldDetecion.byRelRecordChan[CountRecordChan] = i + HCNetSDK.MAX_ANALOG_CHANNUM + 1;
//                    else strFieldDetecion.byRelRecordChan[CountRecordChan] = i + 1;
//                    CountRecordChan++;
//                }
//            }
            for (int i = 0; i< listTraggerRecord.size(); i++)
            {
                if (listTraggerRecord.get(i).getCheck()){
                    String NodeName2 = listTraggerRecord.get(i).getText();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    if (NodeName2.lastIndexOf("IP") > -1)
                        strFieldDetecion.byRelRecordChan[CountRecordChan] = Channel + HCNetSDK.MAX_ANALOG_CHANNUM;
                    else 
                        strFieldDetecion.byRelRecordChan[CountRecordChan] = Channel;
                    CountRecordChan++;
                }
            }
            strFieldDetecion.dwRelRecordChanNum = CountRecordChan;

            //5、报警处理方式：监视器警告/声音报警/上传中心/Email/等的处理
            strFieldDetecion.struHandleException.dwHandleType = 0;
            strFieldDetecion.struHandleException.dwHandleType |= ((jCheckBoxMonitorAlarm.isSelected()?1:0) << 0);
            strFieldDetecion.struHandleException.dwHandleType |= ((jCheckBoxAudioAlarm.isSelected()?1:0) << 1);
            strFieldDetecion.struHandleException.dwHandleType |= ((jCheckBoxCenterAlarm.isSelected()?1:0) << 2);
            strFieldDetecion.struHandleException.dwHandleType |= ((jCheckBoxAlarmout.isSelected()?1:0) << 3);
            strFieldDetecion.struHandleException.dwHandleType |= ((jCheckBoxJpegEMailAlarm.isSelected()?1:0) << 4);
            //是否联动球机目标（只有鱼眼具有此功能），需要判断
            //if (HCNetSDKExpand.isFishEye(strDeviceInfo,strIpparaCfg,iChannel,sFileName))
            strFieldDetecion.struHandleException.dwHandleType |= ((jCheckBoxPTZTrack.isSelected()?1:0) << 11);//0x800: PTZ联动跟踪(球机跟踪目标)

            //6、布防时间，已经在模板编辑中处理过了，已经赋过值了
            //7、警戒区域各点赋值
            setStruRegion();//当前的警戒区域各点存储。之前的警戒区域各点随时就存储了。
            
            //8、存储区域入侵结构体参数
           
            strChannelGroup = new HCNetSDK.NET_DVR_CHANNEL_GROUP();
            strStatusList = new HCNetSDK.STATUSLIST();
            
            strChannelGroup.dwSize = strChannelGroup.size();
            strChannelGroup.dwChannel = iChannel;
            strChannelGroup.dwGroup = 0;
            int IndexOfID = jComboBoxID.getSelectedIndex() + 1;
            strChannelGroup.byID = (byte)IndexOfID;
            //strChannelGroup.byID = 1;//以后待变，以实际为准
            strChannelGroup.dwPositionNo = 1;//场景位置索引号，IPC为0，IPD从1开始 
            
            strChannelGroup.write();
            strStatusList.write();
            strFieldDetecion.write();

            Pointer lpChannelGroup = strChannelGroup.getPointer();
            Pointer lpStatusList = strStatusList.getPointer();
            Pointer lpFieldDetecion = strFieldDetecion.getPointer();

            boolean setDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_SetDeviceConfig(lUserID, HCNetSDK.NET_DVR_SET_FIELD_DETECTION,1,
                lpChannelGroup,strChannelGroup.size(),lpStatusList,lpFieldDetecion,strFieldDetecion.size());
            strChannelGroup.read();
            strStatusList.read();
            strFieldDetecion.read();
            
            if (setDVRConfigSuc){
                writeAlarmConfigLog( sAlarmTypes[4], CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);//"区域入侵侦测"
            }else{
                writeAlarmConfigErrorLog( sAlarmTypes[4], CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);//"区域入侵侦测"
            }
            
            //时间模板在存储按钮操作中都已经统一进行处理了。
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setStruPicCfg()","系统在设置区域入侵结构体参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      setStruRegion
         * 函数描述:  设置当前的警戒区域各点参数 
    */
    private void setStruRegion(){
        try{
            //if (FieldDetecionCallBack.getListSize() < 3) return;
            int PointNum = FieldDetecionCallBack.getListSize();
            int Index = FieldDetecionCallBack.getIndex();
            if(Index < 1) return;
            float PanelWidth = (float)panelPlay.getWidth();
            float PanelHight = (float)panelPlay.getHeight();

            strFieldDetecion.struIntrusion[Index - 1].struRegion.dwPointNum = PointNum;
            for (int i=0;i<PointNum;i++){
                POINT Point = FieldDetecionCallBack.getPoint(i);
                strFieldDetecion.struIntrusion[Index - 1].struRegion.struPos[i].fX = (float)Point.x/PanelWidth;
                strFieldDetecion.struIntrusion[Index - 1].struRegion.struPos[i].fY = (float)Point.y/PanelHight;
            }
            //行为事件触发时间阈值，判断有效报警的时间
            strFieldDetecion.struIntrusion[Index - 1].wDuration = Short.parseShort(jSpinnerDuration.getValue().toString());
            //灵敏度
            String sSensitive = jTextFieldSensitive.getText().trim();
            if (sSensitive.equals("")) sSensitive = "50";
            try{
                strFieldDetecion.struIntrusion[Index - 1].bySensitivity = Byte.parseByte(sSensitive);
            }
            catch(Exception e){
                strFieldDetecion.struIntrusion[Index - 1].bySensitivity = 50;
            }
            //占比：区域内所有未报警目标尺寸目标占区域面积的比重，归一化为1~100 
            strFieldDetecion.struIntrusion[Index - 1].byRate = Byte.parseByte(jSpinnerRate.getValue().toString());
            //检测目标：0- 所有目标，1- 人，2- 车 
            strFieldDetecion.struIntrusion[Index - 1].byDetectionTarget  = 0 ;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setStruRegion()","系统在设置当前的警戒区域各点参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    /**********************区域入侵报警结构体参数结束*************************/
    /**
	 * 函数:      refreshListIfRefrenshStru
         * 函数描述:  初始化ListIfRefrenshStru。
         *            把通道图像结构体、越界报警结构体、区域入侵报警结构体都标志为未读出状态，即设为false
    */
//    private void refreshListIfRefrenshStru(){
//        listIfRefrenshStru.clear();
//        for (int i=0;i<STRUNUMS;i++){
//            listIfRefrenshStru.add(new Boolean(false));
//        }
//    }
    
    /**********************异常设置信息开始*************************/
    /**
	 * 函数:      refreshExceptionInfo
         * 函数描述:  获取异常信息
    */
    private void refreshExceptionInfo(){
        try{    
            if(sAnotherName.equals("")) return;
            sExceptionType = (String)jComboBoxExceptionType.getSelectedItem();//当前的设备异常报警类型
            
            IntByReference ibrBytesReturned = new IntByReference(0);
            boolean getDVRConfigSuc = false;
            struExceptionInfo = new HCNetSDK.NET_DVR_EXCEPTION_V30();
            struExceptionInfo.write();
            Pointer lpConfig = struExceptionInfo.getPointer();
            getDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_EXCEPTIONCFG_V30,
                    new NativeLong(0), lpConfig, struExceptionInfo.size(), ibrBytesReturned);
            struExceptionInfo.read();
            if (getDVRConfigSuc != true){
                //JOptionPane.showMessageDialog(this, "获取异常处理参数失败。错误代码：" + CommonParas.hCNetSDK.NET_DVR_GetLastError());
                CommonParas.showErrorMessage(this.getRootPane(), MessageFormat.format(sGetParaFailed , sExceptionAlarm ), sAnotherName, sFileName);//"获取异常报警参数失败"
                return;
            }
//            else JOptionPane.showMessageDialog(this, "获取异常处理参数成功！");
            
//            jComboBoxExceptionType.setSelectedIndex(0);
            int iType = getIndexOfExceptionTypes(sExceptionType);//jComboBoxExceptionType.getSelectedIndex();
            if (iType == -1) return;
            //5、报警处理方式：监视器警告/声音报警/上传中心/Email/等的处理
            jCheckBoxMonitorAlarm2.setSelected((struExceptionInfo.struExceptionHandleType[iType].dwHandleType & 0x01) == 1);
            jCheckBoxAudioAlarm2.setSelected(((struExceptionInfo.struExceptionHandleType[iType].dwHandleType >> 1) & 0x01) == 1);
            jCheckBoxCenterAlarm2.setSelected(((struExceptionInfo.struExceptionHandleType[iType].dwHandleType >> 2) & 0x01) == 1);
            jCheckBoxAlarmout2.setSelected(((struExceptionInfo.struExceptionHandleType[iType].dwHandleType >> 3) & 0x01) == 1);
            jCheckBoxJpegEMailAlarm2.setSelected(((struExceptionInfo.struExceptionHandleType[iType].dwHandleType >> 4) & 0x01) == 1);//Email上传抓图
            
//            jCheckBoxEnable2.setSelected(struExceptionInfo.struExceptionHandleType[iType].dwHandleType == 0x00);
            //4、触发报警输出
            //触发报警输出通道list
            listTraggerAlarmOut.clear();
            listModelTraggerAlarmOut.clear();
//            int selectAlarmOut = 0;//是否选择了报警输出
//            for (int i = 0; i < strDeviceInfo.byAlarmOutPortNum; i++)
//            {
//                if (struExceptionInfo.struExceptionHandleType[iType].byRelAlarmOut[i] == 1) selectAlarmOut ++;
//                CheckListItem checkListItem = new CheckListItem(struExceptionInfo.struExceptionHandleType[iType].byRelAlarmOut[i] == 1,
//                                                                "AlarmOut" + (i + 1));
//                listTraggerAlarmOut.add(checkListItem);
//                listModelTraggerAlarmOut.addElement(checkListItem);    // 为触发报警输出List增加报警输出
//            }
            /*获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
                对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）*/
            ArrayList ListDeviceSourceAllParas = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE , sFileName);
            for (int i=0;i<ListDeviceSourceAllParas.size();i++){
                //IP通道从33开始（前MAX_ANALOG_CHANNUM个是模拟通道）
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                if (!AnotherNameJoin.equals("")){
                    String NodeName2 = NewList.get(1) + "_" + NewList.get(3) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struExceptionInfo.struExceptionHandleType[iType].byRelAlarmOut[Channel + HCNetSDK.MAX_ANALOG_ALARMOUT - 1] == 1, NodeName2);
                    listTraggerAlarmOut.add(checkListItem);
                    listModelTraggerAlarmOut.addElement(checkListItem);   
                }else {
                    String NodeName2 = NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                     CheckListItem checkListItem = new CheckListItem(struExceptionInfo.struExceptionHandleType[iType].byRelAlarmOut[Channel - 1] == 1, NodeName2);
                    listTraggerAlarmOut.add(checkListItem);
                    listModelTraggerAlarmOut.addElement(checkListItem);  
                }
                
            }
            //其实结构并没有是否启用这一项，但是为了操作方便，加了这一项。
            int CheckedNum = 0;
            for (int i=0;i<listTraggerAlarmOut.size();i++){
                CheckListItem checkListItem = listTraggerAlarmOut.get(i);
                if (checkListItem.getCheck()) CheckedNum++;
            }
            if (struExceptionInfo.struExceptionHandleType[iType].dwHandleType == 0 && CheckedNum == 0){
                jCheckBoxEnable2.setSelected(false);
            }else if (struExceptionInfo.struExceptionHandleType[iType].dwHandleType > 0 || CheckedNum > 0){
                jCheckBoxEnable2.setSelected(true);
            }
            //jCheckBoxEnable2.setSelected(selectAlarmOut > 0 && struExceptionInfo.struExceptionHandleType[iType].dwHandleType == 0x00);
            setExceptionEnable();
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshExceptionInfo()","系统在获取异常信息过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      getIndexOfExceptionTypes
         * 函数描述:  获取设备异常报警类型数组的索引号
    */
    private int getIndexOfExceptionTypes(String ExceptionType){
        for (int i=0;i<sEexceptionTypes.length;i++){
            if (sEexceptionTypes[i].equals(ExceptionType)) return i;
        }
        return -1;
    }
    /**
	 * 函数:      refreshMonitorPointAlarmAbility
         * 函数描述:  获取设备报警能力集可以判断设备是否支持相关功能
    */
    private void refreshExceptionAbility(){
        try{

            listExceptionTypes.clear();
            sHandleTypeCode = "";
            //获取设备报警能力集//<FieldDetection><TraversingVirtualPlane>
            if (getDeviceAbility(HCNetSDK.DEVICE_ABILITY_INFO, "<EventAbility version=\"2.0\"><channelNO>", "</channelNO></EventAbility>", pOutBufAlarm)){
                String XMLOut = new String(pOutBufAlarm).trim();
                DomXML domXML = new DomXML(XMLOut,sFileName);
                
                //获取该设备支持的异常列表
                String ReturnV = domXML.readSecondLevelAttributeValue("ExceptionAlarm", "exceptionType", "opt");//optional中文解释	"可选择的"
                //主要包括：diskFull,diskError,nicBroken,ipConflict,illAccess,videoMismatch, badVideo,recordingFailure,raid,resolutionMismatch,spareException,POEPoweException
                if (!(ReturnV.equals(""))) {
                    String[] ExceptionTypeCodes2 = ReturnV.split(",");
                    String[] ExceptionTypes2 = new String[ExceptionTypeCodes2.length];

                    for (int i=0;i<ExceptionTypeCodes2.length;i++) {
                        ExceptionTypeCodes2[i] = ExceptionTypeCodes2[i].trim().toLowerCase();
                        for (int j=0;j< sExceptionTypeCodes.length;j++) {
                            if (sExceptionTypeCodes[j].equals(ExceptionTypeCodes2[i])){
                                ExceptionTypes2[i] = sEexceptionTypes[j];
                            }
                        }
                    }
                    jComboBoxExceptionType.setModel(new DefaultComboBoxModel(ExceptionTypes2) );
                }
                //获得异常处理方式，各种异常处理方式的列表
                //主要包括："","monitor","audio","center","alarmout","picture","wirelesslight","","uploadftp","","ptztrack"
                String ReturnV2 = domXML.readSecondLevelAttributeValue("ExceptionAlarm", "alarmHandleType", "opt");//optional中文解释	"可选择的"
                if (!(ReturnV2.equals(""))) {
                    sHandleTypeCode = ReturnV2.toLowerCase();//将异常的处理方式保存
                }
                
            }

            bFirstSelect = true;
            jComboBoxExceptionType.setSelectedItem(sExceptionType);//设置当前的设备异常报警类型
            bFirstSelect = false;
            //根据设备该通道支持的报警能力，设备相关控件的是否可用
            //最后还是放在setAlarmEnable函数中
            refreshOCXByHandleTypeCodes2(sHandleTypeCode);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshMonitorPointAlarmAbility()","系统在获取设备报警能力集可以判断设备是否支持相关功能过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    /**
	 * 函数:      setExceptionInfo
         * 函数描述:  设置异常信息
    */
    private void setExceptionInfo(){
        try{
            if (sAnotherName.equals("")) return;
            //int iExceptionType = jComboBoxExceptionType.getSelectedIndex();
            int iExceptionType = getIndexOfExceptionTypes(sExceptionType);//jComboBoxExceptionType.getSelectedIndex();
            if (iExceptionType == -1) return;

            struExceptionInfo.struExceptionHandleType[iExceptionType].dwHandleType = 0;
            struExceptionInfo.struExceptionHandleType[iExceptionType].dwHandleType |= ((jCheckBoxMonitorAlarm2.isSelected() == true ? 1 : 0) << 0);
            struExceptionInfo.struExceptionHandleType[iExceptionType].dwHandleType |= ((jCheckBoxAudioAlarm2.isSelected() == true ? 1 : 0) << 1);
            struExceptionInfo.struExceptionHandleType[iExceptionType].dwHandleType |= ((jCheckBoxCenterAlarm2.isSelected() == true ? 1 : 0) << 2);
            struExceptionInfo.struExceptionHandleType[iExceptionType].dwHandleType |= ((jCheckBoxAlarmout2.isSelected() == true ? 1 : 0) << 3);
            struExceptionInfo.struExceptionHandleType[iExceptionType].dwHandleType |= ((jCheckBoxJpegEMailAlarm2.isSelected() == true ? 1 : 0) << 4);

//            for (int i = 0; i < strDeviceInfo.byAlarmOutPortNum; i++)
//            {
//                struExceptionInfo.struExceptionHandleType[iExceptionType].byRelAlarmOut[i] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
//            }
            
            //设置报警输出
            for (int i = 0; i < listTraggerAlarmOut.size(); i++)
            {
                String NodeName2 = listTraggerAlarmOut.get(i).getText();
                int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                if (NodeName2.lastIndexOf("IP") > -1){
                    struExceptionInfo.struExceptionHandleType[iExceptionType].byRelAlarmOut[Channel + HCNetSDK.MAX_ANALOG_ALARMOUT -1] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
                }else{
                    struExceptionInfo.struExceptionHandleType[iExceptionType].byRelAlarmOut[Channel - 1] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
                }
                    
            }

            struExceptionInfo.write();
            Pointer lpConfig = struExceptionInfo.getPointer();
            boolean setDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_EXCEPTIONCFG_V30,
                    new NativeLong(0), lpConfig, struExceptionInfo.size());
            struExceptionInfo.read();
            
            if (setDVRConfigSuc){
                writeAlarmConfigLog(sExceptionAlarm, "");//"异常报警"
            }else{
                writeAlarmConfigErrorLog(sExceptionAlarm, "");//"异常报警"
            }
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setExceptionInfo()","系统在设置异常信息过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /*---------------------------------异常设置信息结束-----------------------------*/
    /*---------------------------------报警输入设置信息开始---------------------------------*/
    /**
	 * 函数:      refreshAlarmInCfg
         * 函数描述:  获取报警输入信息
    */
    private void refreshAlarmInCfg(){
        try{
            if(sAnotherName.equals("")) return;
            initialSelfTemplate(jComboBoxSetAlarmTime3,2,sAnotherName,"",iChannel);
            IntByReference ibrBytesReturned = new IntByReference(0);
            struAlarmInCfg = new HCNetSDK.NET_DVR_ALARMINCFG_V30();
            struAlarmInCfg.write();
            Pointer lpConfig = struAlarmInCfg.getPointer();
            //此时iChannel表示报警输入/输出口号，从0开始（通道号从1开始）
            //对于配置命令NET_DVR_GET_ALARMINCFG_V30和NET_DVR_GET_ALARMOUTCFG_V30，lChannel从0开始，表示报警输入/输出口号
            boolean getDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_ALARMINCFG_V30,
                    new NativeLong(iChannel - 1), lpConfig, struAlarmInCfg.size(), ibrBytesReturned);
            struAlarmInCfg.read();
            if (getDVRConfigSuc != true)
            {
                CommonParas.showErrorMessage(this.getRootPane(), MessageFormat.format(sGetParaFailed , sAlarmIn ), sAnotherName, sFileName);//"获取报警输入参数失败"
                return;
            }
//            else JOptionPane.showMessageDialog(this, "获取报警输入参数成功！");

            jCheckBoxEnable3.setSelected(struAlarmInCfg.byAlarmInHandle == 1);//是否启用报警输入处理

//            String[] sName = new String[2];
//            sName = new String(struAlarmInCfg.sAlarmInName).split("\0", 2);
            //this.jTextFieldAlarmInName.setText(sName[0]);
            //获取设备编码类型
            String CharEncodeType =CommonParas.getCharEncodeType(iIndexListDevicePara, sFileName);
            //只要将获取的字节数组，用设备的字符编码类型生成字符串就可以正确显示
            this.jTextFieldAlarmInName.setText(new String(struAlarmInCfg.sAlarmInName, CharEncodeType).trim());//报警输入名称

            jComboBoxAlerterType.setSelectedIndex(struAlarmInCfg.byAlarmType);

            //触发报警输出参数,触发类型
            jCheckBoxMonitorAlarm3.setSelected((struAlarmInCfg.struAlarmHandleType.dwHandleType & 0x01) == 1);
            jCheckBoxAudioAlarm3.setSelected(((struAlarmInCfg.struAlarmHandleType.dwHandleType >> 1) & 0x01) == 1);
            jCheckBoxCenterAlarm3.setSelected(((struAlarmInCfg.struAlarmHandleType.dwHandleType >> 2) & 0x01) == 1);
            jCheckBoxAlarmout3.setSelected(((struAlarmInCfg.struAlarmHandleType.dwHandleType >> 3) & 0x01) == 1);
            jCheckBoxJpegEMailAlarm.setSelected(((struAlarmInCfg.struAlarmHandleType.dwHandleType >> 4) & 0x01) == 1);

            //6、布防时间
            strSchedTimeWeek = struAlarmInCfg.struAlarmTime;//[i].struAlarmTime[0];//只取一个时间段

            //3、触发录像的通道  4、触发报警输出
            //触发报警输出通道list
            listTraggerAlarmOut.clear();
            listModelTraggerAlarmOut.clear();
            //加入模拟报警输出通道
//            for (int i = 0; i < strDeviceInfo.byAlarmOutPortNum; i++)
//            {
//                CheckListItem checkListItem = new CheckListItem(struAlarmInCfg.struAlarmHandleType.byRelAlarmOut[i] == 1,
//                                                                "AlarmOut" + (i + 1));
//                listTraggerAlarmOut.add(checkListItem);
//                listModelTraggerAlarmOut.addElement(checkListItem);    // 为触发报警输出List增加报警输出
//            }
            //加入IP报警输出通道
            /*获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
                对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）*/
            ArrayList ListDeviceSourceAllParas = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE , sFileName);
            for (int i=0;i<ListDeviceSourceAllParas.size();i++){
                //IP通道从33开始（前MAX_ANALOG_CHANNUM个是模拟通道）
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                if (!AnotherNameJoin.equals("")){
                    String NodeName2 = NewList.get(1) + "_" + NewList.get(3) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struAlarmInCfg.struAlarmHandleType.byRelAlarmOut[Channel + HCNetSDK.MAX_ANALOG_ALARMOUT - 1] == 1, NodeName2);
                    listTraggerAlarmOut.add(checkListItem);
                    listModelTraggerAlarmOut.addElement(checkListItem);   
                }else {
                    String NodeName2 = NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                     CheckListItem checkListItem = new CheckListItem(struAlarmInCfg.struAlarmHandleType.byRelAlarmOut[Channel - 1] == 1, NodeName2);
                    listTraggerAlarmOut.add(checkListItem);
                    listModelTraggerAlarmOut.addElement(checkListItem);  
                }
                
            }
            
            
            
            //触发录像通道list
            listTraggerRecord.clear();
            listModelTraggerRecord.clear();
            //加入模拟通道
//            for (int i = 0; i < strDeviceInfo.byChanNum; i++)
//            {
//                //m_traggerRecord[i] = new CheckListItem(false, "Camara" + (i + 1));
//                CheckListItem checkListItem = new CheckListItem(struAlarmInCfg.byRelRecordChan[i] == 1, "Camara" + (i + 1));
//                listTraggerRecord.add(checkListItem);
//                listModelTraggerRecord.addElement(checkListItem);    // 为触发录像List增加报警输出
//            }
            //加入IP通道
            //如果是IP通道，直接用原来已经登记的IP通道参数即可。
            //if (strDeviceInfo.byChanNum == 0){
            /*获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
                对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）*/
            ArrayList ListDeviceSourceAllParas2 = DeviceResourceBean.getAllDeviceResourceParaList(sSerialNo, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE , sFileName);
            for (int i=0;i<ListDeviceSourceAllParas2.size();i++){
                //IP通道从33开始（前MAX_ANALOG_CHANNUM个是模拟通道）
                ArrayList NewList = (ArrayList)ListDeviceSourceAllParas2.get(i);
                String AnotherNameJoin = (String)NewList.get(3);
                if (!AnotherNameJoin.equals("")){
                    String NodeName2 = NewList.get(1) + "_" + NewList.get(3) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struAlarmInCfg.byRelRecordChan[Channel + HCNetSDK.MAX_ANALOG_CHANNUM - 1] == 1, NodeName2);
                    listTraggerRecord.add(checkListItem);
                    listModelTraggerRecord.addElement(checkListItem);   
                }else{
                    String NodeName2 = NewList.get(1) + "_" +  ((DeviceResourceBean)NewList.get(0)).getNodename();
                    int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                    CheckListItem checkListItem = new CheckListItem(struAlarmInCfg.byRelRecordChan[Channel - 1] == 1, NodeName2);
                    listTraggerRecord.add(checkListItem);
                    listModelTraggerRecord.addElement(checkListItem); 
                }
            }
            //}
            refreshPTZStrCfgAbility();//如果是球机，获取云台巡航、轨迹、预置点信息
            setAlarmInEnable();
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshAlarmInCfg()","系统在获取报警输入信息过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      refreshAlarmInAbility
         * 函数描述:  获取设备报警能力集可以判断设备是否支持相关功能
    */
    private void refreshAlarmInAbility(){
        try{
            //private String[] sAlarmTypes = new String[]{"移动侦测", "视频丢失", "遮挡报警", "越界侦测", "区域入侵侦测"};
            sHandleTypeCode = "";
            //获取设备报警能力集//<FieldDetection><TraversingVirtualPlane>
            if (getDeviceAbility(HCNetSDK.DEVICE_ABILITY_INFO, "<EventAbility version=\"2.0\"><channelNO>", 1 ,"</channelNO></EventAbility>", pOutBufAlarm)){
                String XMLOut = new String(pOutBufAlarm).trim();
                DomXML domXML = new DomXML(XMLOut,sFileName);
                
                //获得异常处理方式，各种异常处理方式的列表
                //主要包括："","monitor","audio","center","alarmout","picture","wirelesslight","","uploadftp","","ptztrack"
                String ReturnV2 = domXML.readSecondLevelAttributeValue("AlarmIn", "alarmHandleType", "opt");//optional中文解释	"可选择的"
                if (!(ReturnV2.equals(""))) {
                    sHandleTypeCode = ReturnV2.toLowerCase();//将异常的处理方式保存
                }
                
            }

            //根据设备该通道支持的报警能力，设备相关控件的是否可用
            //最后还是放在setAlarmEnable函数中
            refreshOCXByHandleTypeCodes3(sHandleTypeCode);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshMonitorPointAlarmAbility()","系统在获取设备报警能力集可以判断设备是否支持相关功能过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    /**
	 * 函数:      refreshPTZStrCfg
         * 函数描述:  刷新云台巡航、轨迹、预置点信息
    */
    private void refreshPTZStrCfgAbility(){
        try{
            if (!isHavePTZAbilityByAlarmIn()) {
                jCheckBoxPTZEnable.setEnabled(false);
                return;//如果无云台，则不做任何操作
            }else jCheckBoxPTZEnable.setEnabled(true);
            
            NativeLong UserID = new NativeLong(-1);
            if(struIpparaCfg != null) {
                UserID  = CommonParas.getUserID(sAnotherNameJoin, sFileName);
            }else UserID.setValue(lUserID.longValue());
            //sAnotherNameJoin = CommonParas.getAnotherNameJoinFromNode(sChannelNode);

            IntByReference ibrBytesReturned = new IntByReference(0);//获取设备的配置信息。

            boolean bRet = false;
            HCNetSDK.NET_DVR_PRESET_NAME320 strPreSetName = new HCNetSDK.NET_DVR_PRESET_NAME320();
            strPreSetName.write();
            Pointer lpStrPreSetName = strPreSetName.getPointer();
            //此时iChannel表示报警输入/输出口号，从0开始（而通道号从1开始。球机只有一个通道）
            bRet = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(UserID, HCNetSDK.NET_DVR_GET_PRESET_NAME, new NativeLong(1), lpStrPreSetName, strPreSetName.size(), ibrBytesReturned);
            strPreSetName.read();

            if (!bRet)
            {
                //JOptionPane.showMessageDialog(this, CommonParas.createErrorRemarks(sAnotherName,"获取预置点信息失败"));
                CommonParas.showErrorMessage(this.getRootPane(), sGetPresetInfoFail, sAnotherName, sFileName);// "获取预置点信息失败。"
                //写错误日志
                CommonParas.SystemWriteErrorLog( sGetPresetInfoFail,  sAnotherName, sFileName);// "获取预置点信息失败"
                return;
            }
;
            for (int i=0;i<strPreSetName.strPreSetName320.length;i++){
                if (strPreSetName.strPreSetName320[i].dwSize > 0){
                    //添加预置点信息
                    jComboBoxPreset.addItem("" + strPreSetName.strPreSetName320[i].wPresetNum + new String(strPreSetName.strPreSetName320[i].byName).trim());
                }
            }
            
            //获得巡航路径最大值和云台轨迹最大值
            String pInBuf = "<PTZAbility><channelNO>1</channelNO></PTZAbility>";
            byte[] pOutBuf = new byte[64*1024];//128k应该够用了
            bRet = CommonParas.hCNetSDK.NET_DVR_GetDeviceAbility( UserID, HCNetSDK.DEVICE_ABILITY_INFO, pInBuf, pInBuf.length(), pOutBuf, 64*1024);
            
            if (!bRet)
            {
                //JOptionPane.showMessageDialog(this, CommonParas.createErrorRemarks(sAnotherName,"获取设备能力集失败"));
                CommonParas.showErrorMessage(this.getRootPane(), sGetDeviceCapabilitySetsFail, sAnotherName, sFileName);//"获取设备能力集失败。"
                //写错误日志
                CommonParas.SystemWriteErrorLog( sGetDeviceCapabilitySetsFail,  sAnotherName, sFileName);// "获取设备能力集失败"
                return;
            }
//            else JOptionPane.showMessageDialog(this, "获取巡航路径最大值成功");
            String XMLOut = new String(pOutBuf).trim();
            DomXML domXML = new DomXML(XMLOut,sFileName);
            //Document doc = DomXML.readXMLString(XMLOut);
            
            //获得预置点最大值
            //if (strPreSetName.strPreSetName400[i].dwSize > 0)如果没有预置点信息，dwSize=0
            
            
            //获得巡航路径最大值
            String ReturnV = domXML.readSecondLevelAttributeValue("Patrol", "patrolNum", "max");

            if (!(ReturnV.equals(""))) {
                int MaxPatrolNum = Integer.parseInt(ReturnV);
                jComboBoxCruise.removeAllItems();
                for (int i= 1;i <= MaxPatrolNum;i++){
                    jComboBoxCruise.addItem(i);
                }
            }
            //获得云台轨迹最大值
            ReturnV = domXML.readSecondLevelAttributeValue( "Pattern", "patternID", "max");
            if (!(ReturnV.equals(""))) {
                int MaxPatternID = Integer.parseInt(ReturnV);
                jComboBoxTrack.removeAllItems();
                for (int i= 1;i <= MaxPatternID;i++){
                    jComboBoxTrack.addItem(i);
                }
            }
            //先恢复原来的值
            jRadioButtonCruise.setSelected(false);
            jRadioButtonPreset.setSelected(false);
            jRadioButtonTrack.setSelected(false);
            
            jComboBoxCruise.setSelectedIndex(0);
            jComboBoxPreset.setSelectedIndex(0);
            jComboBoxTrack.setSelectedIndex(0);
            
            int BallChannel = 0;
            if (struIpparaCfg != null)  {
                String DVRIP = CommonParas.getDeviceParaBean(sAnotherNameJoin, sFileName).getDVRIP();
                BallChannel = CommonParas.getDVRChannelJoin(strDeviceInfo, struIpparaCfg, DVRIP, sFileName) - 1;
            }
//            现在带云台的只有球机，暂时就按1个通道处理
            jRadioButtonCruise.setSelected(struAlarmInCfg.byEnableCruise[BallChannel] == 1);
            jRadioButtonPreset.setSelected(struAlarmInCfg.byEnablePreset[BallChannel] == 1);
            jRadioButtonTrack.setSelected(struAlarmInCfg.byEnablePtzTrack[BallChannel] == 1);
            if (struAlarmInCfg.byEnableCruise[BallChannel] == 1 || struAlarmInCfg.byEnablePreset[BallChannel] == 1 || struAlarmInCfg.byEnablePtzTrack[BallChannel] == 1)
                jCheckBoxPTZEnable.setSelected(true);
            else jCheckBoxPTZEnable.setSelected(false);
            
            jComboBoxCruise.setSelectedIndex(struAlarmInCfg.byCruiseNo[BallChannel]);
            jComboBoxPreset.setSelectedIndex(struAlarmInCfg.byPresetNo[BallChannel]);
            jComboBoxTrack.setSelectedIndex(struAlarmInCfg.byPTZTrack[BallChannel]);
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshPTZStrCfg()","系统在刷新云台巡航、轨迹、预置点信息过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }

    }
    /**
	 * 函数:      isHavePTZAbilityByAlarmIn
         * 函数描述:  根据报警输入节点，判断设备是否具有云台能力。
    */
    private boolean isHavePTZAbilityByAlarmIn(){
        
        //如果是报警输入的话，设备通道统一为球机通道1。需要判断是否是IP设备
        if(struIpparaCfg == null) {
            return HCNetSDKExpand.isHavePTZAbility(strDeviceInfo.wDevType);
        }
        else{
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo2 = CommonParas.getStrDeviceInfo(sAnotherNameJoin, sFileName);
            if (StrDevInfo2 != null) return HCNetSDKExpand.isHavePTZAbility(StrDevInfo2.wDevType);
            else return false;
        }
        
        //下面代码是想做一个统一的判断是否具有云台能力的函数，但是现在没用上。
//        //首先判断一下是监控点报警还是报警输入
//        switch (sTemplateClass){
//            case CommonParas.TimeTemplateClass.MONITOR_SETUPALARMCHAN:
//                //如果是监控点报警，还是按照原先的处理
//                return HCNetSDKExpand.isHavePTZAbility(strDeviceInfo, strIpparaCfg, iChannel, sFileName);
//            case CommonParas.TimeTemplateClass.ALARMIN_SETUPALARMCHAN:
//                //如果是报警输入的话，设备通道统一为球机通道1。需要判断是否是IP设备
//                if(strIpparaCfg == null) {
//                    return HCNetSDKExpand.isHavePTZAbility(strDeviceInfo.wDevType);
//                }
//                else{
//                    HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo2 = CommonParas.getStrDeviceInfo(sAnotherNameJoin, sFileName);
//                    if (StrDevInfo2 != null) return HCNetSDKExpand.isHavePTZAbility(StrDevInfo2.wDevType);
//                }
//                break;
//        }
//        return false;
    }
    /**
	 * 函数:      refreshPTZStrCfg
         * 函数描述:  设置报警输入中的云台巡航、轨迹、预置点信息
    */
    private void setPTZStrCfg(){
        try{
            if (!isHavePTZAbilityByAlarmIn()) return;//如果无云台，则不做任何操作
            //现在带云台的只有球机，暂时就按1个通道处理
            int BallChannel = 0;
            if (struIpparaCfg != null)  {
                String DVRIP = CommonParas.getDeviceParaBean(sAnotherNameJoin, sFileName).getDVRIP();
                BallChannel = CommonParas.getDVRChannelJoin(strDeviceInfo, struIpparaCfg, DVRIP, sFileName) - 1;
            }
            struAlarmInCfg.byEnableCruise[BallChannel] = (byte) ((jRadioButtonCruise.isSelected() == true) ? 1 : 0);
            struAlarmInCfg.byEnablePreset[BallChannel] = (byte) ((jRadioButtonPreset.isSelected() == true) ? 1 : 0);
            struAlarmInCfg.byEnablePtzTrack[BallChannel] = (byte) ((jRadioButtonTrack.isSelected() == true) ? 1 : 0);
            struAlarmInCfg.byCruiseNo[BallChannel] = (byte) jComboBoxCruise.getSelectedIndex();
            struAlarmInCfg.byPresetNo[BallChannel] = (byte) jComboBoxPreset.getSelectedIndex();
            struAlarmInCfg.byPTZTrack[BallChannel] = (byte) jComboBoxTrack.getSelectedIndex();
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setPTZStrCfg()","系统在设置报警输入中的云台巡航、轨迹、预置点信息过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }

    }
    /**
	 * 函数:      setAlarmInCfg
         * 函数描述:  设置报警输入信息
    */
    private void setAlarmInCfg(){
        
        try{
            if (sAnotherName.equals("")) return;
            //获取设备编码类型
            String CharEncodeType =CommonParas.getCharEncodeType(iIndexListDevicePara, sFileName);
            
            struAlarmInCfg.sAlarmInName = Arrays.copyOf(jTextFieldAlarmInName.getText().getBytes(CharEncodeType), HCNetSDK.NAME_LEN);//(jTextFieldAlarmInName.getText() + "\0").getBytes(); //报警输入名称
            struAlarmInCfg.byAlarmInHandle = (byte) ((jCheckBoxEnable3.isSelected() == true) ? 1 : 0);
            struAlarmInCfg.byAlarmType = (byte) jComboBoxAlerterType.getSelectedIndex();

            //触发报警输出参数,触发类型
            struAlarmInCfg.struAlarmHandleType.dwHandleType = 0;
            struAlarmInCfg.struAlarmHandleType.dwHandleType |= ((jCheckBoxMonitorAlarm3.isSelected()?1:0) << 0);
            struAlarmInCfg.struAlarmHandleType.dwHandleType |= ((jCheckBoxAudioAlarm3.isSelected()?1:0) << 1);
            struAlarmInCfg.struAlarmHandleType.dwHandleType |= ((jCheckBoxCenterAlarm3.isSelected()?1:0) << 2);
            struAlarmInCfg.struAlarmHandleType.dwHandleType |= ((jCheckBoxAlarmout3.isSelected()?1:0) << 3);
            struAlarmInCfg.struAlarmHandleType.dwHandleType |= ((jCheckBoxJpegEMailAlarm.isSelected()?1:0) << 4);
            //设置报警输出
            for (int i = 0; i < listTraggerAlarmOut.size(); i++)
            {
                String NodeName2 = listTraggerAlarmOut.get(i).getText();
                int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                if (NodeName2.lastIndexOf("IP") > -1){
                    struAlarmInCfg.struAlarmHandleType.byRelAlarmOut[Channel + HCNetSDK.MAX_ANALOG_ALARMOUT -1] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
                }else{
                    struAlarmInCfg.struAlarmHandleType.byRelAlarmOut[Channel - 1] = (byte)(listTraggerAlarmOut.get(i).getCheck()?1:0);
                }
                    
            }
            //设置录像触发
            for (int i = 0; i< listTraggerRecord.size(); i++)
            {
                String NodeName2 = listTraggerRecord.get(i).getText();
                int Channel = Integer.parseInt(NodeName2.substring(NodeName2.lastIndexOf("_") + 1 , NodeName2.length()));
                if (NodeName2.lastIndexOf("IP") > -1){
                    struAlarmInCfg.byRelRecordChan[Channel + HCNetSDK.MAX_ANALOG_CHANNUM -1] = (byte)(listTraggerRecord.get(i).getCheck()?1:0);
                    
                }else {
                    struAlarmInCfg.byRelRecordChan[Channel - 1] = (byte)(listTraggerRecord.get(i).getCheck()?1:0);
                }
                    
            }


            //6、布防时间，已经在模板编辑中处理过了，已经赋过值了
            //设置报警输入中的云台巡航、轨迹、预置点信息
            setPTZStrCfg();

            struAlarmInCfg.write();
            Pointer lpConfig = struAlarmInCfg.getPointer();
            //对于配置命令NET_DVR_GET_ALARMINCFG_V30和NET_DVR_GET_ALARMOUTCFG_V30，lChannel从0开始，表示报警输入/输出口号
            boolean setDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_ALARMINCFG_V30,
                    new NativeLong(iChannel - 1), lpConfig, struAlarmInCfg.size());
            struAlarmInCfg.read();
            
            if (setDVRConfigSuc){
                writeAlarmConfigLog( sAlarmIn, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_CODE);// "报警输入"
                //存储时保存该设备、该通道（报警输入、输出）序号、模板用途分类对应的时间模板
                setSelfTimeTemplate(jComboBoxSetAlarmTime3,2,sAnotherName,sTemplateClass,"",iChannel);
            }else{
                writeAlarmConfigErrorLog( sAlarmIn, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_CODE);// "报警输入"
            }
            
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setAlarmInCfg()","系统在设置置报警输入信息过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }
    }
    /**********************报警输入设置信息结束*************************/
    /**********************报警输出设置信息开始*************************/
    /**
	 * 函数:      refreshAlarmOutCfg
         * 函数描述:  获取报警输出信息
    */
    private void refreshAlarmOutCfg(){
        try{
            if(sAnotherName.equals("")) return;
            initialSelfTemplate(jComboBoxSetAlarmTime4,3,sAnotherName,"",iChannel);
            IntByReference ibrBytesReturned = new IntByReference(0);
            struAlarmOutCfg = new HCNetSDK.NET_DVR_ALARMOUTCFG_V30();
            struAlarmOutCfg.write();
            Pointer lpConfig = struAlarmOutCfg.getPointer();
            //此时iChannel表示报警输入/输出口号，从0开始（通道号从1开始）
            //对于配置命令NET_DVR_GET_ALARMINCFG_V30和NET_DVR_GET_ALARMOUTCFG_V30，lChannel从0开始，表示报警输入/输出口号
            boolean getDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_ALARMOUTCFG_V30,
                    new NativeLong(iChannel - 1), lpConfig, struAlarmOutCfg.size(), ibrBytesReturned);
            struAlarmOutCfg.read();
            if (getDVRConfigSuc != true){
                CommonParas.showErrorMessage(this.getRootPane(), MessageFormat.format(sGetParaFailed , sAlarmOut), sAnotherName, sFileName);//"获取报警输出参数失败。"
                //JOptionPane.showMessageDialog(this, "获取报警输出参数失败。错误代码：" + CommonParas.hCNetSDK.NET_DVR_GetLastError());
                //jCheckBoxEnable4.setSelected(false);
                setAlarmOutEnable(false);//设置组件的可用属性
                return;
            }
            String[] sName = new String[2];//报警输出名称
            //获取设备编码类型
            String CharEncodeType =CommonParas.getCharEncodeType(iIndexListDevicePara, sFileName);
            //只要将获取的字节数组，用设备的字符编码类型生成字符串就可以正确显示
            sName = new String(struAlarmOutCfg.sAlarmOutName, CharEncodeType).split("\0", 2);
            this.jTextFieldAlarmOutName.setText(sName[0]);
            jComboBoxAlarmOutDelay.setSelectedIndex(struAlarmOutCfg.dwAlarmOutDelay);
            //6、布防时间
            strSchedTimeWeek = struAlarmOutCfg.struAlarmOutTime;//[i].struAlarmTime[0];//只取一个时间段
            
            //读取该报警输出的状态 是否启用（有无输出）
//            HCNetSDK.NET_DVR_ALARMOUTSTATUS_V30 strAlarmOutStatus30 = new HCNetSDK.NET_DVR_ALARMOUTSTATUS_V30();
//            boolean bGet = CommonParas.hCNetSDK.NET_DVR_GetAlarmOut_V30(lUserID,  strAlarmOutStatus30);
//            if (bGet) {
//                jCheckBoxEnable4.setSelected(strAlarmOutStatus30.Output[iChannel - 1] == 1);
//            }

            setAlarmOutEnable(true);//设置组件的可用属性
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshAlarmOutCfg()","系统在获取报警输出信息过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }
    }
    
    /**
	 * 函数:      setAlarmOutCfg
         * 函数描述:  设置报警输出信息
    */
    private void setAlarmOutCfg(){
        try{
            if (sAnotherName.equals("")) return;
            //获取设备编码类型
            String CharEncodeType =CommonParas.getCharEncodeType(iIndexListDevicePara, sFileName);
            struAlarmOutCfg.sAlarmOutName = Arrays.copyOf(jTextFieldAlarmOutName.getText().getBytes(CharEncodeType), HCNetSDK.NAME_LEN);//(jTextFieldAlarmOutName.getText() + "\0").getBytes(); //通道名称
            struAlarmOutCfg.dwAlarmOutDelay = jComboBoxAlarmOutDelay.getSelectedIndex();
            struAlarmOutCfg.write();
            Pointer lpConfig = struAlarmOutCfg.getPointer();
            boolean setDVRConfigSuc = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_ALARMOUTCFG_V30,
                    new NativeLong(iChannel - 1), lpConfig, struAlarmOutCfg.size());
            struAlarmOutCfg.read();

            if (setDVRConfigSuc){
                writeAlarmConfigLog( sAlarmOut, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE);// "报警输出"
                //存储时保存该设备、该通道（报警输入、输出）序号、模板用途分类对应的时间模板
                setSelfTimeTemplate(jComboBoxSetAlarmTime4,3,sAnotherName,sTemplateClass,"",iChannel);
//                //设置该报警输出的状态 是否启用（有无输出）
//                NativeLong AlarmOutStatic = new NativeLong(jCheckBoxEnable4.isSelected()?1:0);
//                boolean bSet = CommonParas.hCNetSDK.NET_DVR_SetAlarmOut(lUserID, new NativeLong(iChannel - 1), AlarmOutStatic);
//                if (bSet){
//                    writeAlarmConfigLog( "设置报警输出信息", CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE);
//                    //存储时保存该设备、该通道（报警输入、输出）序号、模板用途分类对应的时间模板
//                    setSelfTimeTemplate(jComboBoxSetAlarmTime4,3,sAnotherName,sTemplateClass,"",iChannel);
//                }else{
//                    writeAlarmConfigErrorLog( "设置报警输出信息失败", CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE);
//                }
                
            }else{
                writeAlarmConfigErrorLog( sAlarmOut, CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE);// "报警输出"
            }

            
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setAlarmOutCfg()","系统在设置报警输出信息过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }
        
    }
    /**********************报警输出设置信息结束*************************/
    /**********************共用函数开始*************************/
    
    private void treeNodeValueChanged(JTree jTreeResource,javax.swing.event.TreeSelectionEvent evt) {                                            
        // TODO add your handling code here:
        try {
            iIndexOfTreeNodeClicked = 0;
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode)jTreeResource.getLastSelectedPathComponent();
            if (selectionNode == null) return;
            String nodeName = selectionNode.toString();
            
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            // 判断是否为树叶节点，若是则读取设备通道录像参数，若不是则不做任何事。
            if (selectionNode.isLeaf()) {
//                JOptionPane.showMessageDialog(rootPane, "我是叶子节点");
                
                bIntialSelect = true;//是否刷新右侧界面过程中选择JComboBox。保证在下面的编码选择不触动JComboBox的Item改变事件。

                //设备名称
                String AnotherName = nodeName;//sChannelNode.substring(0, sChannelNode.indexOf("_"));
                //jTreeResource名字
                String NameOfTree = jTreeResource.getName();
                //通道号
                int Channel = 0;
                //异常报警只是针对设备进行操作，与通道号或者报警输入输出号无关
                if (!(sTemplateClass.equals(CommonParas.TimeTemplateClass.EXCEPTION_SETUPALARMCHAN)))  {
                    int Level = selectionNode.getLevel();//如果是在设备上则为1；在设备通道/报警输入/输出节点上则为2
                    if (Level == 1) {
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        return;//如果该设备没有报警输入/输出，则不做任何操作。
                    }
//                    JOptionPane.showMessageDialog(rootPane, "我是二级叶子节点");
                    Channel = CommonParas.getChannel(nodeName);
                    AnotherName = nodeName.substring(0, nodeName.indexOf("_"));
                }
                
                iIndexOfTreeNodeClicked = jTabbedPaneAlarm.getSelectedIndex() + 1;
                //如果设备名和通道号和上次点击一样，则不做任何操作。
                if (AnotherName.equals(sAnotherName) && nodeName.equals(sChannelNode) && Channel == iChannel && NameOfTree.equals(sNameOfTree)){
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
                //刷新当前设备信息
                sChannelNode = nodeName;
                sAnotherName = AnotherName;
                iIndexListDevicePara = CommonParas.getIndexOfDeviceList(AnotherName,sFileName);
                sSerialNo = CommonParas.getSerialNO(iIndexListDevicePara, sFileName);
                iChannel = Channel;
                sNameOfTree = NameOfTree;
                lUserID = CommonParas.getUserID(iIndexListDevicePara, sFileName);
                if(lUserID.intValue() == -1) {
                    strDeviceInfo = null;
                    struIpparaCfg = null;
                }else{
                    strDeviceInfo = CommonParas.getStrDeviceInfo(AnotherName, sFileName);
                    struIpparaCfg = CommonParas.getStrIpparacfg(iIndexListDevicePara, sFileName);
                }
                
                
                if(struIpparaCfg != null) sAnotherNameJoin = CommonParas.getAnotherNameJoinFromNode(sChannelNode);
                else sAnotherNameJoin = "";
                
                //每次设备别名和通道都变化的时候，都需要刷新其初始值。对应设备和通道号对应的结构体是否已经读取。
//                refreshListIfRefrenshStru();
                
                refreshAlarmStruInfo();//刷新报警结构信息

//                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                bIntialSelect = false;//刷新右侧界面过后选择JComboBox。则会触动JComboBox的Item改变事件。
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "treeNodeValueChanged()","系统在读取设备报警参数过程中，出现错误"
                            + "\r\n                       JTree:" + jTreeResource.getName()
                             + "\r\n                       Exception:" + e.toString());
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }     
    /**
	 * 函数:      ComboBoxAlarmTimeItemChanged
         * 函数描述:  时间模板下拉框值改变所触发的操作
         * @param ComboxBoxAlarmTime  时间模板下拉框
         * @param ButtonTemplate   模板编辑按钮
         * @param java.awt.event.ItemEvent   下拉框事件
    */
    private void ComboBoxAlarmTimeItemChanged(JComboBox ComboxBoxAlarmTime,JButton ButtonTemplate, java.awt.event.ItemEvent evt) {
        if(evt.getStateChange() == ItemEvent.SELECTED)
        {
            if (bIntialSelect) return;
            String TimeTemplate = ComboxBoxAlarmTime.getSelectedItem().toString();
            if (TimeTemplate.equals(sTemplates[0])) {//"全天模板"
                    setStrSchedTimeWeekAllWork(TimeTemplate);
                    ButtonTemplate.setEnabled(false);
            }else if(TimeTemplate.equals(sTemplates[1])) {//"工作日模板"
                    setStrSchedTimeWeekAllWork(TimeTemplate);
                    ButtonTemplate.setEnabled(false);
            }else if(TimeTemplate.equals(sTemplates[2])) {//"添加模板"
                    ButtonTemplate.setEnabled(true);
            }else {//其他模板，以前定义过的模板
                    setStrSchedTimeWeek(TimeTemplate);
                    ButtonTemplate.setEnabled(true);
            }
            
//            switch (TimeTemplate){
//                case "全天模板":
//                case "工作日模板":
//                    setStrSchedTimeWeekAllWork(TimeTemplate);
//                    ButtonTemplate.setEnabled(false);
//                    break;
//                case "添加模板":
//                    ButtonTemplate.setEnabled(true);
//                    break;
//                default://其他模板，以前定义过的模板
//                    setStrSchedTimeWeek(TimeTemplate);
//                    ButtonTemplate.setEnabled(true);
//                    break;
//            }
        }
    }
    /**
	 * 函数:      editTimeTemplate
         * 函数描述:  编辑时间模板
         * @param ComboxBoxAlarmTime  时间模板下拉框
    */
    private void editTimeTemplate(JComboBox ComboxBoxAlarmTime, JButton jButtonTemplateEdit) {                                                
        // TODO add your handling code here:
        String TimeTemplate = ComboxBoxAlarmTime.getSelectedItem().toString();
        //"全天模板","工作日模板":
        if (TimeTemplate.equals(sTemplates[0]) || TimeTemplate.equals(sTemplates[1])) return;
//        switch (TimeTemplate){
//            case "全天模板":
//            case "工作日模板":
//                return;
//            default:
//                break;
//        }
        JDialogTimeTemplate DialogTimeTemplate = new JDialogTimeTemplate(null, true, TimeTemplate, strSchedTimeWeek);
        //设置窗口显示位置
        CommonParas.setAppropriateLocation(DialogTimeTemplate, jButtonTemplateEdit);
        DialogTimeTemplate.setVisible(true);

        int State = DialogTimeTemplate.getReturnStatus();
        if (State < 0) return;//取消操作

        TimeTemplate = DialogTimeTemplate.getTimeTemplateName();
        if (TimeTemplate.equals(sTemplates[0]) || TimeTemplate.equals(sTemplates[1]))
                ComboxBoxAlarmTime.setSelectedItem(TimeTemplate);
        else{
                ComboxBoxAlarmTime.addItem(TimeTemplate);
                ComboxBoxAlarmTime.setSelectedItem(TimeTemplate);
        }
//        switch (TimeTemplate){
//            case "全天模板":
//            case "工作日模板":
//                ComboxBoxAlarmTime.setSelectedItem(TimeTemplate);
//                break;
//            default:
//                ComboxBoxAlarmTime.addItem(TimeTemplate);
//                ComboxBoxAlarmTime.setSelectedItem(TimeTemplate);
//                //                //添加设备的关联模板
//                break;
//        }

    } 
    
    /**
	 * 函数:      checkSensitive
         * 函数描述:  根据JSlider控件的属性，判断灵敏度的设定
         * @return int 完全符合条件，返回1；输入不合法，返回-1；超出范围返回-2
    */
    private int checkSensitive(){
        String sSensitive = jTextFieldSensitive.getText().trim();
        int iSensitive;
        try {
            iSensitive = Integer.parseInt(sSensitive);
        }catch (Exception e){
            //JOptionPane.showMessageDialog(this, "灵敏度的输入不合法，必须是数字！");
            CommonParas.showMessage(this.getRootPane(), sIncorrectInput, sFileName);// "输入有误。</br>灵敏度值必须为数字，其它字符非法！"
            return -1;
        }
        
        if (iSensitive<=jSliderSensitive.getMaximum() && iSensitive>= jSliderSensitive.getMinimum()) {
            return 1;
        }else {
            CommonParas.showMessage(this.getRootPane(), MessageFormat.format(sOutOfRange , jSliderSensitive.getMinimum(), jSliderSensitive.getMaximum()), sFileName);//"灵敏度的输入超出了范围["+jSliderSensitive.getMinimum() + ", " + jSliderSensitive.getMaximum() + "]"
            //JOptionPane.showMessageDialog(this, "灵敏度的输入超出了范围["+jSliderSensitive.getMinimum() + ", " + jSliderSensitive.getMaximum() + "]");
            return -2;
        }
        
    }
    /**
	 * 函数:      setAlarmEnable
         * 函数描述:  根据各种条件判断报警组件的可用属性
    */
    private void setAlarmEnable(){
        //1、首先根据该设备是否支持报警功能，进行设置某些控件的是否可用
        if (jComboBoxAlarmType.getItemCount() == 0){
            jCheckBoxEnable.setSelected(false);
            jCheckBoxEnable.setEnabled(false);
            jButtonSave.setEnabled(false);
            jComboBoxAlarmType.setEnabled(false);
        }else{
            jCheckBoxEnable.setEnabled(true);
            jButtonSave.setEnabled(true);
            jComboBoxAlarmType.setEnabled(true);
        }
        //2、根据报警类型，设置某些控件的是否可见
        String AlarmType = "";
        if (jComboBoxAlarmType.getItemCount() > 0)   AlarmType = jComboBoxAlarmType.getSelectedItem().toString();
        int IndexOfAlarmTypes = this.getIndexOfArray(sAlarmTypes, AlarmType);
        //设置某些项目不可见/可见属性
        jButtonFullScreen.setVisible(false);
        switch (IndexOfAlarmTypes){
            case 0://----------"移动侦测"-------------
            case 1://----------"视频丢失"-------------
                jPanelSetupParas.setVisible(false);    
            case 2://-------------"遮挡报警"-------------
                jButtonFullScreen.setVisible(true);
                break;
            case 3://-------------"越界侦测"-------------
                jLabelLineDirection.setVisible(true);
                jComboBoxCrossDirection.setVisible(true);
                jLabelID.setText(sAlertLineID);// "警戒线ID："
                jLabelPointNum.setVisible(false);
                jComboBoxPointNum.setVisible(false);
                jLabelDuration.setVisible(false);
                jSpinnerDuration.setVisible(false);
                jLabelDurationUnit.setVisible(false);
                jLabelRate.setVisible(false);
                jSpinnerRate.setVisible(false);
                jLabelRateUnit.setVisible(false);
                jPanelSetupParas.setVisible(true);
                break;
            case 4://-------------"区域入侵侦测"-------------
                jLabelLineDirection.setVisible(false);
                jComboBoxCrossDirection.setVisible(false);
                jLabelID.setText(sAreaID);// "区域 ID："
                jLabelPointNum.setVisible(true);
                jComboBoxPointNum.setVisible(true);
                jLabelDuration.setVisible(true);
                jSpinnerDuration.setVisible(true);
                jLabelDurationUnit.setVisible(true);
                //比如球机是没有占比这一项的，所以不用在这里判断
//                jLabelRate.setVisible(true);
//                jSpinnerRate.setVisible(true);
//                jLabelRateUnit.setVisible(true);
                jPanelSetupParas.setVisible(true);
                break;
        }
        //3、根据设备类型判断某些控件的可见性
        //现在根据refreshVideoPicAlarmHanleAbility进行判断
//        if (HCNetSDKExpand.isHavePTZAbility(strDeviceInfo,strIpparaCfg,iChannel,sFileName) && (AlarmType.equals("区域入侵侦测") || AlarmType.equals("越界侦测"))) jCheckBoxPTZTrack.setVisible(true);
//        if (HCNetSDKExpand.isFishEye(strDeviceInfo,strIpparaCfg,iChannel,sFileName) && (AlarmType.equals("区域入侵侦测") || AlarmType.equals("越界侦测"))) jCheckBoxPTZTrack.setVisible(true);
//        else  jCheckBoxPTZTrack.setVisible(false);
        //4、根据报警类型、项目的启用等条件设置控件的可用性
        //设置某些项目Enable启用/禁用属性
        if (iIndexOfTreeNodeClicked == 1 && jCheckBoxEnable.isSelected()){
            jComboBoxSetAlarmTime.setEnabled(true);
            
//            panelPlay.setEnabled(true);
//            jComboBoxSensitive.setEnabled(true);
//            jToggleButtonAdd.setEnabled(true);
            jButtonDel.setEnabled(true);
            //setJPanelAllOCXEnable(jPanelRecordChannel,true);
            jListTraggerRecord.setEnabled(true);
//            setJPanelAllOCXEnable(jPanelAlarmOut,true);
            
            jCheckBoxAlarmout.setEnabled(true);
            jListTraggerAlarmOut.setEnabled(jCheckBoxAlarmout.isSelected());
            

            setJPanelAllOCXEnable(jPanelLinkageClient,true);
//            setJPanelAllOCXEnable(jPanelSetAlarmArea,true);
            jListTraggerRecord.setEnabled(true);
            
            String TimeTemplate = jComboBoxSetAlarmTime.getSelectedItem().toString();
            int IndexOfTimeTemplates = this.getIndexOfArray(sTemplates, TimeTemplate);
            switch (IndexOfTimeTemplates){
                case 0://----------"全天模板"----------
                case 1://----------"工作日模板"---------
                    jButtonTemplate.setEnabled(false);
                    break;
                default:
                    jButtonTemplate.setEnabled(true);
                    break;
            }
            
            //String AlarmType = jComboBoxAlarmType.getSelectedItem().toString();
            switch (IndexOfAlarmTypes){
                case 0://"移动侦测"
                    setJPanelAllOCXEnable(jPanelSetupParas,false);//布防区域中的区域ID和警戒线方向
//                    jComboBoxSensitive.setEnabled(true);//灵敏度
                    jListTraggerRecord.setEnabled(true);//触发录像通道
                    panelPlay.setEnabled(true);//预览窗口
//                    jToggleButtonAdd.setEnabled(true);//预览窗口的添加按钮
                    jButtonDel.setEnabled(true);//预览窗口的删除按钮
                    jButtonFullScreen.setEnabled(true);
                    jSliderSensitive.setEnabled(true);
                    jTextFieldSensitive.setEnabled(true);
                    break;
                case 1://--------"视频丢失"--------------
                    setJPanelAllOCXEnable(jPanelSetupParas,false);//布防区域中的区域ID和警戒线方向
                    //setJPanelAllOCXEnable(jPanelRecordChannel,false);//触发录像通道
                    jListTraggerRecord.setEnabled(false);//触发录像通道
//                    jComboBoxSensitive.setEnabled(false);//灵敏度

                    panelPlay.setEnabled(false);//预览窗口
//                    jToggleButtonAdd.setEnabled(false);//预览窗口的添加按钮
                    jButtonDel.setEnabled(false);//预览窗口的删除按钮
                    break;
                case 2://------------"遮挡报警"---------------
                    setJPanelAllOCXEnable(jPanelSetupParas,false);//布防区域中的区域ID和警戒线方向
//                    jComboBoxSensitive.setEnabled(false);//灵敏度
                    //setJPanelAllOCXEnable(jPanelRecordChannel,false);//触发录像通道
                    jListTraggerRecord.setEnabled(false);//触发录像通道
                    panelPlay.setEnabled(true);//预览窗口
//                    jToggleButtonAdd.setEnabled(true);//预览窗口的添加按钮
                    jButtonDel.setEnabled(true);//预览窗口的删除按钮
                    jButtonFullScreen.setEnabled(true);
                    jSliderSensitive.setEnabled(true);
                    jTextFieldSensitive.setEnabled(true);
                    break;
                case 3://-------------"越界侦测"--------------
                    setJPanelAllOCXEnable(jPanelSetupParas,true);//布防区域中的区域ID和警戒线方向
//                    jComboBoxSensitive.setEnabled(true);//灵敏度
                    jListTraggerRecord.setEnabled(true);//触发录像通道
                    panelPlay.setEnabled(true);//预览窗口
//                    jToggleButtonAdd.setEnabled(true);//预览窗口的添加按钮
                    jButtonDel.setEnabled(true);//预览窗口的删除按钮
                    jButtonFullScreen.setEnabled(true);
                    jSliderSensitive.setEnabled(true);
                    jTextFieldSensitive.setEnabled(true);
                case 4://------------"区域入侵侦测"-------------------
                    setJPanelAllOCXEnable(jPanelSetupParas,true);//布防区域中的区域ID和警戒线方向
//                    jComboBoxSensitive.setEnabled(true);//灵敏度
                    jListTraggerRecord.setEnabled(true);//触发录像通道
                    panelPlay.setEnabled(true);//预览窗口
//                    jToggleButtonAdd.setEnabled(true);//预览窗口的添加按钮
                    jButtonDel.setEnabled(true);//预览窗口的删除按钮
                    jButtonFullScreen.setEnabled(true);
                    jSliderSensitive.setEnabled(true);
                    jTextFieldSensitive.setEnabled(true);    
                    break;
            }
            //如果无云台，则不能进行云台操作
            jButtonPTZControl.setEnabled(HCNetSDKExpand.isHavePTZAbility(strDeviceInfo,struIpparaCfg,iChannel,sFileName));
        }else{
            jComboBoxSetAlarmTime.setEnabled(false);
            jButtonTemplate.setEnabled(false);

            panelPlay.setEnabled(false);
//            jComboBoxSensitive.setEnabled(false);
//            jToggleButtonAdd.setEnabled(false);
            jButtonDel.setEnabled(false);
            jButtonFullScreen.setEnabled(false);
//            setJPanelAllOCXEnable(jPanelRecordChannel,false);
//            setJPanelAllOCXEnable(jPanelAlarmOut,false);
            
            setJPanelAllOCXEnable(jPanelLinkageClient,false);
            setJPanelAllOCXEnable(jPanelSetupParas,false);
            if (AlarmType.equals(sAlarmTypes[3]) || AlarmType.equals(sAlarmTypes[4])) jComboBoxID.setEnabled(true);
            jCheckBoxAlarmout.setEnabled(false);
            jListTraggerRecord.setEnabled(false);
            jListTraggerAlarmOut.setEnabled(false);
            jButtonPTZControl.setEnabled(false);
            jSliderSensitive.setEnabled(false);
            jTextFieldSensitive.setEnabled(false);
        }
        
    }
    /**
	 * 函数:      setExceptionEnable
         * 函数描述:  根据各种条件判断异常件的可用属性
    */
    private void setExceptionEnable(){
        //1、首先根据该设备是否支持异常，进行设置某些控件的是否可用
        if (jComboBoxExceptionType.getItemCount() == 0){
            jCheckBoxEnable2.setSelected(false);
            jCheckBoxEnable2.setEnabled(false);
            jButtonSave2.setEnabled(false);
            jComboBoxExceptionType.setEnabled(false);
        }else{
            jCheckBoxEnable2.setEnabled(true);
            jButtonSave2.setEnabled(true);
            jComboBoxExceptionType.setEnabled(true);
        }
        
        //设置某些项目Enable启用/禁用属性
        if (iIndexOfTreeNodeClicked == 2 && jCheckBoxEnable2.isSelected()){
            setJPanelAllOCXEnable(jPanelLinkageClient2,true);
            //setJPanelAllOCXEnable(jPanelAlarmOut2,true);
            jCheckBoxAlarmout2.setEnabled(true);
            jListTraggerAlarmOut2.setEnabled(jCheckBoxAlarmout2.isSelected());
        }else{
            setJPanelAllOCXEnable(jPanelLinkageClient2,false);
            //setJPanelAllOCXEnable(jPanelAlarmOut2,false);
            jCheckBoxAlarmout2.setEnabled(false);
            jListTraggerAlarmOut2.setEnabled(false);
        }
    }
    /**
	 * 函数:      setAlarmInEnable
         * 函数描述:  根据各种条件判断报警输入组件的可用属性
    */
    private void setAlarmInEnable(){
        if (sAnotherName.equals("") || iIndexOfTreeNodeClicked != 3 || sAnotherName.equals(sChannelNode)){
            jCheckBoxEnable3.setEnabled(false);
            jCheckBoxEnable3.setSelected(false);
            jButtonSave3.setEnabled(false);
        }else {
            jCheckBoxEnable3.setEnabled(true);
            jButtonSave3.setEnabled(true);
        }
        
        //设置某些项目Enable启用/禁用属性
        if (iIndexOfTreeNodeClicked == 3 && jCheckBoxEnable3.isSelected()){
            jTextFieldAlarmInName.setEnabled(true);
            jComboBoxAlerterType.setEnabled(true);
            jComboBoxSetAlarmTime3.setEnabled(true);

            String TimeTemplate = jComboBoxSetAlarmTime3.getSelectedItem().toString();
            int IndexOfTimeTemplates = this.getIndexOfArray(sTemplates, TimeTemplate);
            switch (IndexOfTimeTemplates){
                case 0://"全天模板"
                case 1://"工作日模板"
                    jButtonTemplate3.setEnabled(false);
                    break;
                default:
                    jButtonTemplate3.setEnabled(true);
                    break;
            }
            
            //setJPanelAllOCXEnable(jPanelAlarmPrecessMode3,true);
            //setJPanelAllOCXEnable(jPanelAlarmOut3,false);
            jCheckBoxAlarmout3.setEnabled(true);
            jListTraggerAlarmOut3.setEnabled(jCheckBoxAlarmout3.isSelected());
            setJPanelAllOCXEnable(jPanelLinkageClient3,true);          
            jListTraggerRecord3.setEnabled(true);
            
            if (isHavePTZAbilityByAlarmIn()) {
                jCheckBoxPTZEnable.setEnabled(true);
                if (jCheckBoxPTZEnable.isSelected()){
                    jRadioButtonCruise.setEnabled(true);
                    jComboBoxCruise.setEnabled(true);
                    jRadioButtonPreset.setEnabled(true);
                    jComboBoxPreset.setEnabled(true);
                    jRadioButtonTrack.setEnabled(true);
                    jComboBoxTrack.setEnabled(true);
                    //jButtonCfg.setEnabled(true);
                }
                else {
                    jRadioButtonCruise.setEnabled(false);
                    jComboBoxCruise.setEnabled(false);
                    jRadioButtonPreset.setEnabled(false);
                    jComboBoxPreset.setEnabled(false);
                    jRadioButtonTrack.setEnabled(false);
                    jComboBoxTrack.setEnabled(false);
                    //jButtonCfg.setEnabled(false);
                }
            }else{
                setJPanelAllOCXEnable(jPanelPTZDdetail,false);
//                jCheckBoxPTZEnable.setEnabled(false);
//                jRadioButtonCruise.setEnabled(false);
//                jComboBoxCruise.setEnabled(false);
//                jRadioButtonPreset.setEnabled(false);
//                jComboBoxPreset.setEnabled(false);
//                jRadioButtonTrack.setEnabled(false);
//                jComboBoxTrack.setEnabled(false);
                //jButtonCfg.setEnabled(false);
            }
    
           
        }else{
            jTextFieldAlarmInName.setEnabled(false);
            jComboBoxAlerterType.setEnabled(false);
            jComboBoxSetAlarmTime3.setEnabled(false);
            jButtonTemplate3.setEnabled(false);

            //setJPanelAllOCXEnable(jPanelAlarmPrecessMode3,false);
            //setJPanelAllOCXEnable(jPanelAlarmOut3,false);
            jCheckBoxAlarmout3.setEnabled(false);
            setJPanelAllOCXEnable(jPanelLinkageClient3,false);   
            jListTraggerRecord3.setEnabled(false);
            jListTraggerAlarmOut3.setEnabled(false);

            setJPanelAllOCXEnable(jPanelPTZDdetail,false);
        }
    }
//    /**
//	 * 函数:      setAlarmOutEnable
//         * 函数描述:  根据各种条件判断报警输出组件的可用属性
//    */
//    private void setAlarmOutEnable(){
//        if (sAnotherName.equals("") || iIndexOfTreeNodeClicked != 4 || sAnotherName.equals(sChannelNode)){
//            jCheckBoxEnable4.setEnabled(false);
//            jCheckBoxEnable4.setSelected(false);
//            jButtonSave4.setEnabled(false);
//        }else {
//            jCheckBoxEnable4.setEnabled(true);
//            jButtonSave4.setEnabled(true);
//        }
//        
//        //设置某些项目Enable启用/禁用属性
//        if (iIndexOfTreeNodeClicked == 4 && jCheckBoxEnable4.isSelected()){
//            jTextFieldAlarmOutName.setEnabled(true);
//            jComboBoxAlarmOutDelay.setEnabled(true);
//            jComboBoxSetAlarmTime4.setEnabled(true);
//            String TimeTemplate = jComboBoxSetAlarmTime4.getSelectedItem().toString();
//            switch (TimeTemplate){
//                case "全天模板":
//                case "工作日模板":
//                    jButtonTemplate4.setEnabled(false);
//                    break;
//                default:
//                    jButtonTemplate4.setEnabled(true);
//                    break;
//            }
//        }else{
//            jTextFieldAlarmOutName.setEnabled(false);
//            jComboBoxAlarmOutDelay.setEnabled(false);
//            jComboBoxSetAlarmTime4.setEnabled(false);
//            jButtonTemplate4.setEnabled(false);
//        }
//    }
    /**
	 * 函数:      setAlarmOutEnable
         * 函数描述:  根据各种条件判断报警输出组件的可用属性
    */
    private void setAlarmOutEnable(boolean IfReadSucc){
        if (IfReadSucc){
            jTextFieldAlarmOutName.setEnabled(true);
            jComboBoxAlarmOutDelay.setEnabled(true);
            jComboBoxSetAlarmTime4.setEnabled(true);
            jButtonSave4.setEnabled(true);
            String TimeTemplate = jComboBoxSetAlarmTime4.getSelectedItem().toString();
            int IndexOfTimeTemplates = this.getIndexOfArray(sTemplates, TimeTemplate);
            switch (IndexOfTimeTemplates){
                case 0://"全天模板"
                case 1://"工作日模板"
                    jButtonTemplate4.setEnabled(false);
                    break;
                default:
                    jButtonTemplate4.setEnabled(true);
                    break;
            }
        }else{
            jTextFieldAlarmOutName.setEnabled(false);
            jComboBoxAlarmOutDelay.setEnabled(false);
            jComboBoxSetAlarmTime4.setEnabled(false);
            jButtonTemplate4.setEnabled(false);
            jButtonSave4.setEnabled(false);
        
        }
        
        
    }
    /**
	 * 函数:      refreshExceptionOCXByHandleTypeCodes
         * 函数描述:  根据设备支持的监控点报警的报警/异常处理方式列表处理控件的显示/隐藏、可用性
         * @para HandleTypeCodes  设备支持的处理方式列表
    */
    private void refreshOCXByHandleTypeCodes(String HandleTypeCodes){
        /*handleTypeCodes = new String[]{"","monitor","audio","center","alarmout","picture","wirelesslight","","uploadftp","","ptztrack"};*/
        if (HandleTypeCodes.lastIndexOf("monitor") > -1) 
            jCheckBoxMonitorAlarm.setVisible(true);
        else jCheckBoxMonitorAlarm.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("audio") > -1)
            jCheckBoxAudioAlarm.setVisible(true);
        else jCheckBoxAudioAlarm.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("center") > -1) 
            jCheckBoxCenterAlarm.setVisible(true);
        else jCheckBoxCenterAlarm.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("picture") > -1) 
            jCheckBoxJpegEMailAlarm.setVisible(true);
        else jCheckBoxJpegEMailAlarm.setVisible(false);
        if (HandleTypeCodes.lastIndexOf("ptztrack") > -1) 
            jCheckBoxPTZTrack.setVisible(true);
        else jCheckBoxPTZTrack.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("alarmout") > -1) 
            jPanelAlarmOut.setVisible(true);
        else jPanelAlarmOut.setVisible(false);
 
    }
    /**
	 * 函数:      refreshOCXByHandleTypeCodes2
         * 函数描述:  根据设备支持的报警输入报警/异常处理方式列表处理控件的显示/隐藏、可用性
         * @para HandleTypeCodes  设备支持的异常处理方式列表
    */
    private void refreshOCXByHandleTypeCodes2(String HandleTypeCodes){
        /*handleTypeCodes = new String[]{"","monitor","audio","center","alarmout","picture","wirelesslight","","uploadftp","","ptztrack"};*/
        if (HandleTypeCodes.lastIndexOf("monitor") > -1) jCheckBoxMonitorAlarm2.setVisible(true);
        else jCheckBoxMonitorAlarm2.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("audio") > -1) jCheckBoxAudioAlarm2.setVisible(true);
        else jCheckBoxAudioAlarm2.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("center") > -1) jCheckBoxCenterAlarm2.setVisible(true);
        else jCheckBoxCenterAlarm2.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("picture") > -1) jCheckBoxJpegEMailAlarm2.setVisible(true);
        else jCheckBoxJpegEMailAlarm2.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("alarmout") > -1) jPanelAlarmOut2.setVisible(true);
        else jPanelAlarmOut2.setVisible(false);

    }
    
    /**
	 * 函数:      refreshOCXByHandleTypeCodes3
         * 函数描述:  根据设备支持的异常报警报警/异常处理方式列表处理控件的显示/隐藏、可用性
         * @para HandleTypeCodes  设备支持的处理方式列表
    */
    private void refreshOCXByHandleTypeCodes3(String HandleTypeCodes){
        /*handleTypeCodes = new String[]{"","monitor","audio","center","alarmout","picture","wirelesslight","","uploadftp","","ptztrack"};*/
        if (HandleTypeCodes.lastIndexOf("monitor") > -1) jCheckBoxMonitorAlarm3.setVisible(true);
        else jCheckBoxMonitorAlarm3.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("audio") > -1) jCheckBoxAudioAlarm3.setVisible(true);
        else jCheckBoxAudioAlarm3.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("center") > -1) jCheckBoxCenterAlarm3.setVisible(true);
        else jCheckBoxCenterAlarm3.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("picture") > -1) jCheckBoxJpegEMailAlarm3.setVisible(true);
        else jCheckBoxJpegEMailAlarm3.setVisible(false);
        
        if (HandleTypeCodes.lastIndexOf("alarmout") > -1) jPanelAlarmOut3.setVisible(true);
        else jPanelAlarmOut3.setVisible(false);

    }
    /**
	 * 函数:      setJPanelAllOCXEnable
         * 函数描述:  设置JPanel中所有的控件是否可用
         * @para jPanel  JPanel
         * @para enabled   是否可用
    */
    private void setJPanelAllOCXEnable(JPanel jPanel,boolean enabled){
        jPanel.setEnabled(enabled);
        Component[] com =  jPanel.getComponents();
        for (Component com1 : com) {
            com1.setEnabled(enabled);
        }
    }
    /**
	 * 函数:      setStrSchedTimeWeek
         * 函数描述:  根据下拉列表框选择的模板设置布防时间段
    */
    private void setStrSchedTimeWeek(String TimeTemplate){
        
        ArrayList<TimeTemplateBean> listTimeTemplateBean = TimeTemplateBean.getTimeTemplateList(TimeTemplate, sFileName);
        
        for (int i=0;i<listTimeTemplateBean.size();i++){
            TimeTemplateBean timeTemplateBean = listTimeTemplateBean.get(i);
            strSchedTimeWeek[i].struAlarmTime[0].byStartHour = timeTemplateBean.getStarthour();
            strSchedTimeWeek[i].struAlarmTime[0].byStartMin = timeTemplateBean.getStartminute();
            strSchedTimeWeek[i].struAlarmTime[0].byStopHour = timeTemplateBean.getStophour();
            strSchedTimeWeek[i].struAlarmTime[0].byStopMin = timeTemplateBean.getStopminute();
        }
        //MAX_DAYS
    }
    /**
	 * 函数:      setStrSchedTimeWeekAllWork
         * 函数描述:  设置布防时间段，针对全天模板和工作日模板
    */
    private void setStrSchedTimeWeekAllWork(String Type){
        byte byStartHour=0,byStartMin=0,byStopHour=24,byStopMin=0;
        int IndexOfTimeTemplates = this.getIndexOfArray(sTemplates, Type);

        switch (IndexOfTimeTemplates){
            case 0://"全天模板"
                break;
            case 1://"工作日模板"
                byStartHour=8;
                byStopHour=20;
                break;
        }
        for (int i=0;i<HCNetSDK.MAX_DAYS;i++){
            strSchedTimeWeek[i].struAlarmTime[0].byStartHour = byStartHour;
            strSchedTimeWeek[i].struAlarmTime[0].byStartMin = byStartMin;
            strSchedTimeWeek[i].struAlarmTime[0].byStopHour = byStopHour;
            strSchedTimeWeek[i].struAlarmTime[0].byStopMin = byStopMin;
        }
    }
    /**
	 * 函数:      setSelfTimeTemplate
         * 函数描述:  存储时保存该设备、该通道（报警输入、输出）序号、模板用途分类对应的时间模板
         * @param jComboBoxAlarmTime   
         * @param AnotherName   设备别名
         * @param sTemplateClass    模板用途分类
         * @param SubResourceNO 该通道（报警输入、输出）序号
    */
    private void setSelfTimeTemplate(JComboBox jComboBoxAlarmTime, int Index, String AnotherName, String sTemplateClass, String AlarmTypeName, int SubResourceNO){
        try{
            String SerialNO = CommonParas.getSerialNO(AnotherName, sFileName);
            String TemplateName = (String)jComboBoxAlarmTime.getSelectedItem();
            String TemplateName2 = listTimeTeplateName.get(Index);
            if (!TemplateName.equals(TemplateName2)) {
                DevTimeTemplateBean devTimeTemplateBean = new DevTimeTemplateBean(SerialNO, sTemplateClass, SubResourceNO, AlarmTypeName, TemplateName);
                int SaveRet = DevTimeTemplateBean.saveDevTimeTemplate(devTimeTemplateBean, sFileName);
                if (SaveRet < 1) TxtLogger.append(this.sFileName, "setSelfTimeTemplate()","系统在存储设备的布防时间模板过程中，出现错误"
                            + "\r\n                       AnotherName:" + AnotherName
                            + "\r\n                       sTemplateClass:" + sTemplateClass
                            + "\r\n                       SubResourceNO:" + SubResourceNO);
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setSelfTimeTemplate()","系统在存储设备的布防时间模板过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      initialListRecordAlarmOut
         * 函数描述:  初始化时刷新下拉列表框选择的模板名称
    */
    private void initialListRecordAlarmOut(){
        
        switch (sTemplateClass){
            case CommonParas.TimeTemplateClass.MONITOR_SETUPALARMCHAN:
                listModelTraggerRecord = new DefaultListModel();
                jListTraggerRecord.setModel(listModelTraggerRecord);

                listModelTraggerAlarmOut = new DefaultListModel();
                jListTraggerAlarmOut.setModel(listModelTraggerAlarmOut);
                break;
            case CommonParas.TimeTemplateClass.EXCEPTION_SETUPALARMCHAN:
                listModelTraggerAlarmOut = new DefaultListModel();
                jListTraggerAlarmOut2.setModel(listModelTraggerAlarmOut);
                break;
            case CommonParas.TimeTemplateClass.ALARMIN_SETUPALARMCHAN:
                listModelTraggerRecord = new DefaultListModel();
                jListTraggerRecord3.setModel(listModelTraggerRecord);

                listModelTraggerAlarmOut = new DefaultListModel();
                jListTraggerAlarmOut3.setModel(listModelTraggerAlarmOut);
                break;
            case CommonParas.TimeTemplateClass.ALARMOUT_SETUPALARMCHAN:
//                refreshExceptionInfo();
                break;
        }




    }
    /**
	 * 函数:      initialJComboboxTemplates
         * 函数描述:  初始化时刷新下拉列表框选择的模板名称
    */
    private void initialJComboboxTemplates(){
        
        ArrayList<String> listTimeTemplateName = TimeTemplateBean.getTemplateNameList(sFileName);
        for (int i=0;i<listTimeTemplateName.size();i++){
            String TemplateName = listTimeTemplateName.get(i);
            jComboBoxSetAlarmTime.addItem(TemplateName);
            jComboBoxSetAlarmTime3.addItem(TemplateName);
            jComboBoxSetAlarmTime4.addItem(TemplateName);
        }
    }
    
    /**
	 * 函数:      initialSelfTemplate
         * 函数描述:  JTree被点击后，设置设备通道0/报警输入2/报警输出3的序列号对应的时间模板
    */
    private void initialSelfTemplate(JComboBox jComboBoxTemplate,int Index,String AnotherName,String AlarmTypeName,int SubResourceNO){
        try{
            String SerialNO = CommonParas.getSerialNO(AnotherName, sFileName);
            String TemplateName = DevTimeTemplateBean.getTemplateName(SerialNO, sTemplateClass, SubResourceNO, AlarmTypeName,sFileName);
            if (!(TemplateName == null || TemplateName.equals(""))){
                jComboBoxTemplate.setSelectedItem(TemplateName);
                listTimeTeplateName.set(Index, TemplateName);//初始化最初的时间模板名称
            }else{
                jComboBoxTemplate.setSelectedItem(sTemplates[2]);//"添加模板"
                listTimeTeplateName.set(Index, sTemplates[2]);//初始化最初的时间模板名称"添加模板"
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "initialSelfTemplate()","JTree被点击后，设置设备通道0/报警输入2/报警输出3的序列号对应的时间模板过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString()
                            + "\r\n                       AnotherName:" + AnotherName
                            + "\r\n                       jComboBoxAlarmTime:" + jComboBoxTemplate.toString()
                            + "\r\n                       Index:" + Index
                            + "\r\n                       SubResourceNO:" + SubResourceNO);
        }
    }
    
    /**
        * 函数:      previewChannelRP
        * 函数描述:  设备通道预览
    */
    private void previewChannelRP(){
        try{
            //判断如果上次还在预览的话，结束预览
                int RetrunCode;
                if (channelRP.getPreviewHandle().intValue() >-1) {
                    CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRP.getPreviewHandle(), null, 0);
                    RetrunCode = CommonParas.stopPreviewOneChannel(channelRP, sFileName);
                    if (RetrunCode < 1) {
                        TxtLogger.append(this.sFileName, "previewChannelRP()","系统结束预览失败"
                                 + "\r\n                       设备:" + sAnotherName);
                    }
                }
                //如果是视频丢失，则不必进行预览
                String AlarmType = (String)jComboBoxAlarmType.getSelectedItem();
                if (AlarmType.equals(sAlarmTypes[1])) return;//"视频丢失"
                
                //预览
                //通道预览信息类设置信息
                channelRP.intialParas();
                channelRP.setPanelRealplay(panelPlay);
                channelRP.setChannel(new NativeLong(iChannel));
                CommonParas.setBasicParasOfChannelRealPlayer(channelRP, null, sAnotherName, sFileName);
                RetrunCode = CommonParas.previewOneChannel(channelRP, sFileName);
                if (RetrunCode < 1) {
                    TxtLogger.append(this.sFileName, "previewChannelRP()","系统预览失败"
                             + "\r\n                       设备:" + sAnotherName);
                }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "previewChannelRP()","设备通道预览时出现错误："
                                + "\r\n                       Exception:" + e.toString());
        }
    }
    
    /**
	 * 函数:      setJSpinnerModel
         * 函数描述:  重新设定JSpinner控件的属性
         * @para Spinner   JSpinner控件
    */
    private void setJSpinnerState(JSpinner Spinner){
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(Spinner, "0");
        Spinner.setEditor(editor);
        JFormattedTextField textField = ((JSpinner.NumberEditor) Spinner.getEditor()).getTextField();
        textField.setEditable(true);
        DefaultFormatterFactory factory = (DefaultFormatterFactory) textField.getFormatterFactory();
        NumberFormatter formatter = (NumberFormatter) factory.getDefaultFormatter();
        formatter.setAllowsInvalid(false);
    }
    
    public void writeAlarmConfigLog(String Description, String DVRResourceType){
        String DVRNodeName = CommonParas.getDVRNodeFromTreeNode(sChannelNode);
        String SerialNoJoin = CommonParas.getSerialNO(sAnotherNameJoin, sFileName);
        //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
        CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, MessageFormat.format(sSetItem , Description), sSerialNo, "", DVRNodeName, SerialNoJoin,"",CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,
                 DVRResourceType,sFileName);//"设置"+ Description
        CommonParas.showMessage( MessageFormat.format(sSetItemSucc, Description), sFileName);//"设置"+ Description + "成功！"
    }
  
    public void writeAlarmConfigErrorLog(String Description, String DVRResourceType){
        String DVRNodeName = CommonParas.getDVRNodeFromTreeNode(sChannelNode);
        String SerialNoJoin = CommonParas.getSerialNO(sAnotherNameJoin, sFileName);
        //写错误日志
        //操作时间、设备别名、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
        CommonParas.SystemWriteErrorLog("", sAnotherName, MessageFormat.format(sSetItemFail, Description), sSerialNo, "", DVRNodeName, 
                                    SerialNoJoin,"",CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,DVRResourceType,sFileName);//"设置"+ Description + "失败"
        CommonParas.showErrorMessage( MessageFormat.format(sSetItemFail, Description), sAnotherName, sFileName);//"设置"+ Description + "失败"
    }
    
    private int getIndexOfArray(String[] arr,String value2){  
        if (arr==null || arr.length==0 || value2.isEmpty()) return -1;
        for(int i=0;i<arr.length;i++){  
            if(arr[i].equals(value2)){  
                return i;  
            }
        }  
        return -1;
    }
    
    /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    private void modifyLocales(){
        
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作

        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        //信息显示
        sAlarmTypes     = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sAlarmTypes").split(",");  //移动侦测, 视频丢失, 遮挡报警, 越界侦测, 区域入侵侦测
        sEexceptionTypes= Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sEexceptionTypes").split(",");  //硬盘满,硬盘出错,网线断,IP 地址冲突,非法访问,输入/输出视频制式不匹配,视频信号异常,录像异常,阵列异常,前端/录像分辨率不匹配异常,行车超速（车载专用）,热备异常（N+1使用）,温度异常,子系统异常,风扇异常,POE供电异常
        sHandleTypes    = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sHandleTypes").split(",");  //无响应,监视器上警告,声音警告,上传中心,触发报警输出,Jpeg抓图并上传EMail,无线声光报警器联动,联动电子地图(目前仅PCNVR支持),抓图并上传ftp,虚焦侦测联动聚焦,PTZ联动跟踪(球机跟踪目标)
        sDirections     = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sDirections").split(",");  //双向（A <-> B）,由左至右（A -> B）,由右至左（B -> A）
        sTemplates      = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sTemplates").split(",");  //全天模板, 工作日模板, 添加模板
        
        sSwitchingStates= Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sSwitchingStates");  //常开,常闭
        sAlarmDurations = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sAlarmDurations");  //5秒,10秒,30秒,1分钟,2分钟,5分钟,10分钟,手动（需手动关闭）
        sAlarmSettings  = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sAlarmSettings");  //报警设置
        sChannel        = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sChannel");  //监控点
        sDevice         = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sDevice");  //设备
        sMonitorAlarms  = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sMonitorAlarms");  //监控点报警
        sExceptionAlarm= Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sExceptionAlarms");  //异常报警
        sAlarmIn        = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sAlarmIn");  //报警输入
        sAlarmOut       = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sAlarmOut");  //报警输出
        sGetDeviceCapabilitySetsFail= Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sGetDeviceCapabilitySetsFail");  //获取设备能力集失败
        sGetPresetInfoFail          = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sGetPresetInfoFail");  //获取预置点信息失败
        sGetChannelImageParaFail    = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sGetChannelImageParaFail");  //获取通道图像参数失败
        sGetParaFailed              = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sGetParaFailed");  //获{0}取参数失败
        sSetItem                    = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sSetItem");  //设置{0}
        sSetItemSucc                = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sSetItemSucc");  //设置{0}成功！
        sSetItemFail                = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sSetItemFail");  //设置{0}失败
        sIncorrectInput             = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sIncorrectInput");  //输入有误。</br>灵敏度值必须为数字，其它字符非法！
        sOutOfRange                 = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sOutOfRange");  //灵敏度的输入超出了范围[{0}, {1}]
        sAlertLineID                = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sAlertLineID");  //警戒线ID：
        sAreaID                     = Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.sAreaID");  //区域 ID：
        
        //标签和按钮显示
        jLabelAlarmType.setText(        Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelAlarmType"));  //报警类型：
        jCheckBoxEnable.setText(        Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jCheckBoxEnable"));  //启用
        jLabelArmingTime.setText(       Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelArmingTime"));  //布防时间：
        jButtonTemplate.setText(        Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jButtonTemplate"));  //编辑模板
        jLabelArmingPara.setText(       Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelArmingPara"));  //布防参数
        jLabelSensitive.setText(        Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelSensitive"));  //灵敏度：
        jButtonPTZControl.setText(      Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jButtonPTZControl"));  //云台控制
        jLabelLineDirection.setText(    Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelLineDirection"));  //警戒线方向：
        jLabelPointNum.setText(         Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelPointNum"));  //有效点数：
        jLabelDuration.setText(         Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelDuration"));  //触发时间阈值：
        jLabelDurationUnit.setText(     Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelDurationUnit"));  //秒
        jLabelRate.setText(             Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelRate"));  //占 比：
        jLabelAlarmProcessMode.setText( Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelAlarmProcessMode"));  //报警处理方式
        jCheckBoxMonitorAlarm.setText(  Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jCheckBoxMonitorAlarm"));  //监视器上警告
        jCheckBoxAudioAlarm.setText(    Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jCheckBoxAudioAlarm"));  //声音警告
        jCheckBoxCenterAlarm.setText(   Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jCheckBoxCenterAlarm"));  //上传中心
        jCheckBoxJpegEMailAlarm.setText(Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jCheckBoxJpegEMailAlarm"));  //抓图发送邮件
        jCheckBoxPTZTrack.setText(  Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jCheckBoxPTZTrack"));  //PTZ联动跟踪
        jLabelLinkClient.setText(   Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelLinkClient"));  //联动客户端：
        jLabelTriggerRecord.setText(Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelTriggerRecord"));  //触发录像通道：
        jCheckBoxAlarmout.setText(  Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jCheckBoxAlarmout"));  //触发报警输出
        jButtonSave.setText(        Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jButtonSave"));  //保存
        jButtonExit.setText(        Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jButtonExit"));  //退出
        jLabelExceptionType.setText(Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelExceptionType"));  //异常类型：
        jLabelAlarmInInfo.setText(  Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelAlarmInInfo"));  //报警输入信息
        jLabelAlarmerType.setText(  Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelAlarmerType"));  //报警器状态：
        jLabelAlarmInName.setText(  Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelAlarmInName"));  //报警输入名称：
        jLabelPTZLink.setText(      Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelPTZLink"));  //PTZ联动：
        jRadioButtonCruise.setText( Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jRadioButtonCruise"));  //巡航
        jRadioButtonPreset.setText( Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jRadioButtonPreset"));  //预置点
        jRadioButtonTrack.setText(  Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jRadioButtonTrack"));  //轨迹
        jCheckBoxPTZEnable.setText( Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jCheckBoxPTZEnable"));  //启用云台
        jLabelAlarmOutInfo.setText( Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelAlarmOutInfo"));  //报警输出信息
        jLabelAlarmOutName.setText( Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelAlarmOutName"));  //报警输出名称：
        jLabelAlarmDuration.setText(Locales.getString("JInFrameAlarmConfig", "JInFrameAlarmConfig.jLabelAlarmDuration"));  //报警维持时间：

        //二次赋值
        this.setTitle(sAlarmSettings);
        jLabelID.setText(sAlertLineID);
        jCheckBoxEnable2.setText(           jCheckBoxEnable.getText()); //启用
        jLabelAlarmProcessMode2.setText(    jLabelAlarmProcessMode.getText()); //  报警处理方式
        jCheckBoxMonitorAlarm2.setText(     jCheckBoxMonitorAlarm.getText()); //监视器上警告
        jCheckBoxAudioAlarm2.setText(       jCheckBoxAudioAlarm.getText()); //声音警告
        jCheckBoxCenterAlarm2.setText(      jCheckBoxCenterAlarm.getText()); //上传中心
        jCheckBoxJpegEMailAlarm2.setText(   jCheckBoxJpegEMailAlarm.getText()); //抓图发送邮件
        jLabelLinkClient2.setText(          jLabelLinkClient.getText()); //联动客户端：
        jCheckBoxAlarmout2.setText(         jCheckBoxAlarmout.getText()); //触发报警输出
        jButtonSave2.setText(               jButtonSave.getText()); //保存
        jButtonExit2.setText(               jButtonExit.getText()); //退出
        jCheckBoxEnable3.setText(           jCheckBoxEnable.getText()); //启用
        jLabelArmingTime3.setText(          jLabelArmingTime.getText()); //布防时间：
        jButtonTemplate3.setText(           jButtonTemplate.getText()); //编辑模板     
        jLabelAlarmProcessMode3.setText(    jLabelAlarmProcessMode.getText()); //  报警处理方式：
        jCheckBoxMonitorAlarm3.setText(     jCheckBoxMonitorAlarm.getText()); //监视器上警告
        jCheckBoxAudioAlarm3.setText(       jCheckBoxAudioAlarm.getText()); //声音警告
        jCheckBoxCenterAlarm3.setText(      jCheckBoxCenterAlarm.getText()); //上传中心
        jCheckBoxJpegEMailAlarm3.setText(   jCheckBoxJpegEMailAlarm.getText()); //抓图发送邮件
        jLabelLinkClient3.setText(          jLabelLinkClient.getText()); //   联动客户端
        jLabelTriggerRecord3.setText(       jLabelTriggerRecord.getText()); //触发录像通道：
        jCheckBoxAlarmout3.setText(         jCheckBoxAlarmout.getText()); //触发报警输出
        jButtonSave3.setText(               jButtonSave.getText()); //保存
        jButtonExit3.setText(               jButtonExit.getText()); //退出
        jLabelArmingTime4.setText(          jLabelArmingTime.getText()); //布防时间：
        jButtonTemplate4.setText(           jButtonTemplate.getText()); //编辑模板
        jButtonSave4.setText(               jButtonSave.getText()); //保存
        jButtonExit4.setText(               jButtonExit.getText()); //退出
        
        jTabbedPaneAlarm.setTitleAt(0, " "+sMonitorAlarms);  //监控点报警
        jTabbedPaneAlarm.setTitleAt(1, " "+sExceptionAlarm);  //异常报警
        jTabbedPaneAlarm.setTitleAt(2, " "+sAlarmIn);  //报警输入
        jTabbedPaneAlarm.setTitleAt(3, " "+sAlarmOut);  //报警输出
        
        jComboBoxSetAlarmTime.setModel(new DefaultComboBoxModel<>(sTemplates));
        jComboBoxCrossDirection.setModel(new DefaultComboBoxModel<>(sDirections));
        jComboBoxAlerterType.setModel(new DefaultComboBoxModel<>(sSwitchingStates.split(",")));
        jComboBoxSetAlarmTime3.setModel(new DefaultComboBoxModel<>(sTemplates));
        jComboBoxAlarmOutDelay.setModel(new DefaultComboBoxModel<>(sAlarmDurations.split(",")));
        jComboBoxSetAlarmTime4.setModel(new DefaultComboBoxModel<>(sTemplates));

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonDel;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonExit2;
    private javax.swing.JButton jButtonExit3;
    private javax.swing.JButton jButtonExit4;
    private javax.swing.JButton jButtonFullScreen;
    private javax.swing.JButton jButtonPTZControl;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSave2;
    private javax.swing.JButton jButtonSave3;
    private javax.swing.JButton jButtonSave4;
    private javax.swing.JButton jButtonTemplate;
    private javax.swing.JButton jButtonTemplate3;
    private javax.swing.JButton jButtonTemplate4;
    private javax.swing.JCheckBox jCheckBoxAlarmout;
    private javax.swing.JCheckBox jCheckBoxAlarmout2;
    private javax.swing.JCheckBox jCheckBoxAlarmout3;
    private javax.swing.JCheckBox jCheckBoxAudioAlarm;
    private javax.swing.JCheckBox jCheckBoxAudioAlarm2;
    private javax.swing.JCheckBox jCheckBoxAudioAlarm3;
    private javax.swing.JCheckBox jCheckBoxCenterAlarm;
    private javax.swing.JCheckBox jCheckBoxCenterAlarm2;
    private javax.swing.JCheckBox jCheckBoxCenterAlarm3;
    private javax.swing.JCheckBox jCheckBoxEnable;
    private javax.swing.JCheckBox jCheckBoxEnable2;
    private javax.swing.JCheckBox jCheckBoxEnable3;
    private javax.swing.JCheckBox jCheckBoxJpegEMailAlarm;
    private javax.swing.JCheckBox jCheckBoxJpegEMailAlarm2;
    private javax.swing.JCheckBox jCheckBoxJpegEMailAlarm3;
    private javax.swing.JCheckBox jCheckBoxMonitorAlarm;
    private javax.swing.JCheckBox jCheckBoxMonitorAlarm2;
    private javax.swing.JCheckBox jCheckBoxMonitorAlarm3;
    private javax.swing.JCheckBox jCheckBoxPTZEnable;
    private javax.swing.JCheckBox jCheckBoxPTZTrack;
    private javax.swing.JComboBox<String> jComboBoxAlarmOutDelay;
    private javax.swing.JComboBox<String> jComboBoxAlarmType;
    private javax.swing.JComboBox jComboBoxAlerterType;
    private javax.swing.JComboBox<String> jComboBoxCrossDirection;
    private javax.swing.JComboBox jComboBoxCruise;
    private javax.swing.JComboBox<String> jComboBoxExceptionType;
    private javax.swing.JComboBox<String> jComboBoxID;
    private javax.swing.JComboBox<String> jComboBoxPointNum;
    private javax.swing.JComboBox jComboBoxPreset;
    private javax.swing.JComboBox<String> jComboBoxSetAlarmTime;
    private javax.swing.JComboBox<String> jComboBoxSetAlarmTime3;
    private javax.swing.JComboBox<String> jComboBoxSetAlarmTime4;
    private javax.swing.JComboBox jComboBoxTrack;
    private javax.swing.JLabel jLabelAlarmDuration;
    private javax.swing.JLabel jLabelAlarmInInfo;
    private javax.swing.JLabel jLabelAlarmInName;
    private javax.swing.JLabel jLabelAlarmOutInfo;
    private javax.swing.JLabel jLabelAlarmOutName;
    private javax.swing.JLabel jLabelAlarmProcessMode;
    private javax.swing.JLabel jLabelAlarmProcessMode2;
    private javax.swing.JLabel jLabelAlarmProcessMode3;
    private javax.swing.JLabel jLabelAlarmType;
    private javax.swing.JLabel jLabelAlarmerType;
    private javax.swing.JLabel jLabelArmingPara;
    private javax.swing.JLabel jLabelArmingTime;
    private javax.swing.JLabel jLabelArmingTime3;
    private javax.swing.JLabel jLabelArmingTime4;
    private javax.swing.JLabel jLabelDuration;
    private javax.swing.JLabel jLabelDurationUnit;
    private javax.swing.JLabel jLabelExceptionType;
    private javax.swing.JLabel jLabelID;
    private javax.swing.JLabel jLabelLineDirection;
    private javax.swing.JLabel jLabelLinkClient;
    private javax.swing.JLabel jLabelLinkClient2;
    private javax.swing.JLabel jLabelLinkClient3;
    private javax.swing.JLabel jLabelPTZLink;
    private javax.swing.JLabel jLabelPointNum;
    private javax.swing.JLabel jLabelRate;
    private javax.swing.JLabel jLabelRateUnit;
    private javax.swing.JLabel jLabelSensitive;
    private javax.swing.JLabel jLabelTriggerRecord;
    private javax.swing.JLabel jLabelTriggerRecord3;
    private javax.swing.JList jListTraggerAlarmOut;
    private javax.swing.JList jListTraggerAlarmOut2;
    private javax.swing.JList jListTraggerAlarmOut3;
    private javax.swing.JList jListTraggerRecord;
    private javax.swing.JList jListTraggerRecord3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelAlarmInInfo;
    private javax.swing.JPanel jPanelAlarmOut;
    private javax.swing.JPanel jPanelAlarmOut2;
    private javax.swing.JPanel jPanelAlarmOut3;
    private javax.swing.JPanel jPanelAlarmOutInfo;
    private javax.swing.JPanel jPanelAlarmOutInfoDetail;
    private javax.swing.JPanel jPanelAlarmPrecessDetail3;
    private javax.swing.JPanel jPanelAlarmPrecessMode;
    private javax.swing.JPanel jPanelAlarmPrecessMode2;
    private javax.swing.JPanel jPanelAlarmPrecessMode3;
    private javax.swing.JPanel jPanelFoot1;
    private javax.swing.JPanel jPanelFoot2;
    private javax.swing.JPanel jPanelFoot3;
    private javax.swing.JPanel jPanelFoot4;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelHeader2;
    private javax.swing.JPanel jPanelHeader3;
    private javax.swing.JPanel jPanelLinkageClient;
    private javax.swing.JPanel jPanelLinkageClient2;
    private javax.swing.JPanel jPanelLinkageClient3;
    private javax.swing.JPanel jPanelPTZ;
    private javax.swing.JPanel jPanelPTZDdetail;
    private javax.swing.JPanel jPanelPlay;
    private javax.swing.JPanel jPanelRecordChannel;
    private javax.swing.JPanel jPanelRecordChannel3;
    private javax.swing.JPanel jPanelRight1;
    private javax.swing.JPanel jPanelRight2;
    private javax.swing.JPanel jPanelRight3;
    private javax.swing.JPanel jPanelRight4;
    private javax.swing.JPanel jPanelSetupAera;
    private javax.swing.JPanel jPanelSetupAeraDetail;
    private javax.swing.JPanel jPanelSetupParas;
    private javax.swing.JRadioButton jRadioButtonCruise;
    private javax.swing.JRadioButton jRadioButtonPreset;
    private javax.swing.JRadioButton jRadioButtonTrack;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JScrollPane jScrollPaneTraggerAlarmOut3;
    private javax.swing.JScrollPane jScrollPaneTraggerRecord3;
    private javax.swing.JSlider jSliderSensitive;
    private javax.swing.JSpinner jSpinnerDuration;
    private javax.swing.JSpinner jSpinnerRate;
    private javax.swing.JSplitPane jSplitPaneAlarmIn;
    private javax.swing.JSplitPane jSplitPaneAlarmOut;
    private javax.swing.JSplitPane jSplitPaneException;
    private javax.swing.JSplitPane jSplitPaneMonitorAlarm;
    private javax.swing.JTabbedPane jTabbedPaneAlarm;
    private javax.swing.JTextField jTextFieldAlarmInName;
    private javax.swing.JTextField jTextFieldAlarmOutName;
    private javax.swing.JTextField jTextFieldSensitive;
    private javax.swing.JTree jTreeResource1;
    private javax.swing.JTree jTreeResource2;
    private javax.swing.JTree jTreeResource3;
    private javax.swing.JTree jTreeResource4;
    private java.awt.Panel panelPlay;
    // End of variables declaration//GEN-END:variables
    
    private String[] sDirections = new String[]{"双向 A <-> B","由左至右 A -> B","由右至左 B -> A"};
    private String[] sTemplates = new String[]{"全天模板","工作日模板","添加模板"};
    private String sSwitchingStates = "常开,常闭";
    private String sAlarmDurations = "5秒,10秒,30秒,1分钟,2分钟,5分钟,10分钟,手动（需手动关闭）";
    private String sAlarmSettings = "报警设置";
    private String sChannel = "监控点";
    private String sDevice = "设备";
    private String sMonitorAlarms = "监控点报警";
    private String sExceptionAlarm = "异常报警";
    private String sAlarmIn = "报警输入";
    private String sAlarmOut = "报警输出";
    private String sGetDeviceCapabilitySetsFail = "获取设备能力集失败";
    private String sGetPresetInfoFail = "获取预置点信息失败";
    private String sGetChannelImageParaFail = "获取通道图像参数失败";
    private String sGetParaFailed = "获取{0}参数失败";
    private String sSetItem = "设置{0}";
    private String sSetItemSucc = "设置{0}成功！";
    private String sSetItemFail = "设置{0}失败";
    private String sIncorrectInput = "输入有误。</br>灵敏度值必须为数字，其它字符非法！";
    private String sOutOfRange = "灵敏度的输入超出了范围[{0}, {1}]";
    private String sAlertLineID = "警戒线ID：";
    private String sAreaID = "区域 ID：";
/*************************************************
    类:        FDrawFunSet
    函数描述:   设置移动侦测区域回调函数
     *************************************************/
    class FDrawFunMotionDetectSet implements HCNetSDK.FDrawFun
    {
        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser)
        {
            int i = 0;
            for (i=0; i < iDetectIndex; i++)
            {
                    uSer.DrawEdge(hDc, rectSet[i], USER32.BDR_SUNKENOUTER, USER32.BF_RECT);
            }
            gDi.SetBkMode(hDc,GDI32.TRANSPARENT);
    //        System.out.println("设备输出字符成功");
        }
    }

    /*************************************************
    类:        FDrawFunGet
    函数描述:   显示移动侦测区域回调函数
     *************************************************/
        class FDrawFunMotionDetectGet implements HCNetSDK.FDrawFun
    {
        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser)
        {
            RECT rect = new RECT();
            int i = 0, j = 0;
            User32.POINT point = new User32.POINT();
//            gDi.SetTextColor(hDc, 0x0000ff);
            for (i = 0; i < 64; i++)
            {
                for (j = 0; j < 96; j++)
                {
                    if (struPicCfg.struMotion.byMotionScope[i].byMotionScope[j] == 1)
                    {
                        point.x = j * iPrecision;
                        point.y = i * iPrecision;
                        rect.left = point.x;
                        rect.top = point.y;
                        rect.right = point.x + iPrecision;
                        rect.bottom = point.y + iPrecision;
                        uSer.DrawEdge(hDc, rect, USER32.BDR_SUNKENOUTER, USER32.BF_RECT);
                    }
                }
            }

            gDi.SetBkMode(hDc, GDI32.TRANSPARENT);
        }
    }

      /*************************************************
    类:        FDrawFunHideSet
    函数描述:   设置遮挡报警区域回调函数
     *************************************************/
    class FDrawFunHideSet implements HCNetSDK.FDrawFun
    {

        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser)
        {
            uSer.DrawEdge(hDc, rectSet[0], USER32.BDR_SUNKENOUTER, USER32.BF_RECT);
            gDi.SetBkMode(hDc, GDI32.TRANSPARENT);
        }
    }

     /*************************************************
    类:        FDrawFunHideGet
    函数描述:   显示遮挡报警区域回调函数
     *************************************************/
    class FDrawFunHideGet implements HCNetSDK.FDrawFun
    {
        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser)
        {

            RECT rect = new RECT();
            rect.left = rectMouse[0].left;
            rect.top = rectMouse[0].top;
            rect.right = rectMouse[0].right;
            rect.bottom = rectMouse[0].bottom;
            uSer.DrawEdge(hDc, rect, USER32.BDR_SUNKENOUTER, USER32.BF_RECT);
            gDi.SetBkMode(hDc, GDI32.TRANSPARENT);
            
        }
    }
    /*************************************************
    类:        FDrawFunGrids
    函数描述:  显示区域的网格
     *************************************************/
    class FDrawFunGrids implements HCNetSDK.FDrawFun
    {
        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser)
        {
            RECT rect = new RECT();
            int i = 0, j = 0;
            User32.POINT point = new User32.POINT();
//            gDi.SetTextColor(hDc, 0x0000ff);
            for (i = 0; i < 64; i++)
            {
                for (j = 0; j < 96; j++)
                {
                    if (byGridsScope[i].byMotionScope[j] == 1)
                    {
                        point.x = j * iPrecision;
                        point.y = i * iPrecision;
                        rect.left = point.x;
                        rect.top = point.y;
                        rect.right = point.x + iPrecision;
                        rect.bottom = point.y + iPrecision;
                        uSer.DrawEdge(hDc, rect, USER32.BDR_SUNKENOUTER, USER32.BF_RECT);
                    }
                }
            }

            gDi.SetBkMode(hDc, GDI32.TRANSPARENT);
        }
    }
    
    /**
	 * 函数:      FDrawFunTraverseLineDetect
         * 函数描述:  画出越界侦测配置线及方向
    */
    class FDrawFunTraversePlane implements HCNetSDK.FDrawFun
    {
        private int Index;//第几条越界侦测线
        private POINT Position1,Position2;//界线的初始和终止坐标
        private POINT PositionA,PositionB;//字符A和B的对应坐标
        private POINT PositionArrow1,PositionArrow2;//字符A和B之间的箭头的起始坐标
        private int iArrowDirection;//箭头方向
        private static final double LENGTHAB = 35;//A和B之间距离的一半，即距离界线的长度
        private static final double LENGTHARROW = 20;//箭头的长度的一半
//        private static final double ARROWLINE = 10;//箭头叉线的长度
        
        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser) {
            
            if (isInitial()) return;
            
            gDi.SetTextColor(hDc, 0x0000ff);
            String Text = Integer.toString(getIndex()) + "#";
            boolean DrawText = gDi.TextOut(hDc,Position1.x,Position1.y, Text,Text.length());
            //设置AB两点坐标和箭头起始坐标
            setPointABArrow();
            //设置当前设备的画笔.
            HANDLE hPen =  gDi.CreatePen(GDI32.PS_SOLID, 3, 0x00ff00);
            HANDLE hOldPen = gDi.SelectObject(hDc,hPen);//旧画笔句柄存储在hOldPen中
            
            gDi.MoveToEx(hDc, Position1.x, Position1.y, null);//画直线
            gDi.LineTo(hDc, Position2.x, Position2.y);
            textOutAB(hDc);//在预览屏幕上输出字符A和B

            drawLineHaveArrow(hDc,PositionArrow1,PositionArrow2,30,15,iArrowDirection);//画箭头
            
            gDi.SetBkMode(hDc,GDI32.TRANSPARENT);  
            //恢复原来的画笔.
            gDi.SelectObject(hDc,hOldPen);

            //删除创建的画笔.
            gDi.DeleteObject(hPen);
        }
    
        public FDrawFunTraversePlane(){
            this.Index = 1;
            this.Position1 = new POINT(0,0);
            this.Position2 = new POINT(0,0);
            this.PositionA = new POINT(0,0);
            this.PositionB = new POINT(0,0);
            this.iArrowDirection = HCNetSDK.VCA_BOTH_DIRECTION;
        }
        
        public void initialParas(){
            this.Position1 = new POINT(0,0);
            this.Position2 = new POINT(0,0);
            this.PositionA = new POINT(0,0);
            this.PositionB = new POINT(0,0);
        }
        private boolean isInitial(){
            if (Position1.x == 0 && Position1.y==0 && Position2.x==0 && Position2.y==0) return true;
            return false;
        }
        /**
            * 函数:      textOutAB
            * 函数描述:  在预览屏幕上输出字符A和B
        */
        private void textOutAB(W32API.HDC hDc){
            GDI32.Size size = new GDI32.Size();
            size.write();
            Pointer pSize = size.getPointer();
            
            int iRet = gDi.GetTextExtentPoint32(hDc, "A", 1, pSize);
            size.read();
            
            if (iRet != 0){
                gDi.TextOut(hDc,PositionA.x - 5,PositionA.y - size.Height/2, "A",1);
                gDi.TextOut(hDc,PositionB.x - 5,PositionB.y - size.Height/2, "B",1);
            }else {
                gDi.TextOut(hDc,PositionA.x - 5,PositionA.y - 6, "A",1);
                gDi.TextOut(hDc,PositionB.x - 5,PositionB.y - 6, "B",1);
            }
        
        }
        /**
            * 函数:      setPointABArrow
            * 函数描述:  设置PositionA、PositionB、PositionArrow1、PositionArrow2的坐标
        */
        private void setPointABArrow(){
            double a0 = Position2.x - Position1.x;
            double b0 = Position2.y - Position1.y;
            double a = Math.abs(a0);
            double b = Math.abs(b0);
            double c = Math.sqrt(a*a + b*b);
            double aa = Math.asin(a/c);
            double x0 = (Position2.x + Position1.x)/2;
            double y0 = (Position2.y + Position1.y)/2;
            double ay = LENGTHAB*Math.sin(aa);
            double ax = LENGTHAB*Math.cos(aa);
            double ay2 = LENGTHARROW*Math.sin(aa);
            double ax2 = LENGTHARROW*Math.cos(aa);
            
            if (a0 * b0 > 0) {
                PositionA = new  POINT((int)(x0 -ax),(int)(y0+ay));
                PositionB = new  POINT((int)(x0 +ax),(int)(y0-ay));
                PositionArrow1 = new  POINT((int)(x0 -ax2),(int)(y0+ay2));
                PositionArrow2 = new  POINT((int)(x0 +ax2),(int)(y0-ay2));
            }else {
                PositionA = new  POINT((int)(x0 -ax),(int)(y0-ay));
                PositionB = new  POINT((int)(x0 +ax),(int)(y0+ay));
                PositionArrow1 = new  POINT((int)(x0 -ax2),(int)(y0-ay2));
                PositionArrow2 = new  POINT((int)(x0 +ax2),(int)(y0+ay2));
            }

        }
        /**
            * 函数:      drawLineHaveArrow
            * 函数描述:  画PositionA和PositionB之间的带箭头的直线
            * 参数：      @param hDc W32API.HDC
            *              @param startPoint 起始点A
            *              @param endPoint 终止点B
            *              @param theta 箭头两边线与主线的角度,<90
            *              @param lengthOfArrowLine 箭头边线的长度
            *              @param AToB int方向。0双向；1由左至右 ；2由右至左 
        */
        private void drawLineHaveArrow(W32API.HDC hDc, POINT startPoint, POINT endPoint, double theta, int lengthOfArrowLine, int AToB){
            gDi.MoveToEx(hDc, startPoint.x, startPoint.y, null);
            gDi.LineTo(hDc, endPoint.x, endPoint.y);
            switch (AToB){
                case HCNetSDK.VCA_BOTH_DIRECTION:
                    drawArrow(hDc,startPoint, endPoint,theta,lengthOfArrowLine);
                    drawArrow(hDc,endPoint,startPoint ,theta,lengthOfArrowLine);
                    break;
                case HCNetSDK.VCA_LEFT_GO_RIGHT:
                    drawArrow(hDc,startPoint, endPoint,theta,lengthOfArrowLine);
                    break;
                case HCNetSDK.VCA_RIGHT_GO_LEFT:
                    drawArrow(hDc,endPoint,startPoint ,theta,lengthOfArrowLine);
                    break;
            }
            
        }
        /**
            * 函数:      drawLineHaveArrow
            * 函数描述:  画PositionA和PositionB之间的带箭头的直线
            * 参数：      @param hDc W32API.HDC
            *              @param startPoint 起始点A
            *              @param endPoint 终止点B
            *              @param theta 箭头两边线与主线的角度,<90
            *              @param lengthOfArrowLine 箭头边线的长度
        */
        private void drawArrow(W32API.HDC hDc,POINT startPoint,POINT endPoint,double theta,int lengthOfArrowLine){
            double aa =3.1415926*theta/180;//转换为弧度

            double a,b,P1x,P1y,P2x,P2y;
            //以P2为原点得到向量P2P1（P）
            a=startPoint.x-endPoint.x;
            b=startPoint.y-endPoint.y;
            //向量P旋转aa角得到向量P1
            P1x=a*Math.cos(aa)-b*Math.sin(aa);
            P1y=a*Math.sin(aa)+b*Math.cos(aa);
            //向量P旋转-aa角得到向量P2
            P2x=a*Math.cos(-aa)-b*Math.sin(-aa);
            P2y=a*Math.sin(-aa)+b*Math.cos(-aa);
            //伸缩向量至制定长度
            double c1,c2;//向量的长度
            c1=Math.sqrt(P1x*P1x+P1y*P1y);
            P1x=P1x*lengthOfArrowLine/c1;
            P1y=P1y*lengthOfArrowLine/c1;
            c2=Math.sqrt(P2x*P2x+P2y*P2y);
            P2x=P2x*lengthOfArrowLine/c2;
            P2y=P2y*lengthOfArrowLine/c2;
            //平移变量到直线的末端
            P1x=P1x+endPoint.x;
            P1y=P1y+endPoint.y;
            P2x=P2x+endPoint.x;
            P2y=P2y+endPoint.y;
 
            gDi.MoveToEx(hDc, endPoint.x, endPoint.y, null);
            gDi.LineTo(hDc, (int)P1x,  (int)P1y);

            gDi.MoveToEx(hDc, endPoint.x, endPoint.y, null);
            gDi.LineTo(hDc, (int)P2x,  (int)P2y);

        }
        
        /**
         * @return the Position1
         */
        public POINT getPosition1() {
            return Position1;
        }
        /**
         * @param Position1 the Position1 to set
         */
        public void setPosition1(POINT Position) {
            this.Position1 = Position;
            this.Position2 = new POINT(Position.x + 1, Position.y + 1);
        }
        
        /**
         * @return the Position2
         */
        public POINT getPosition2() {
            return Position2;
        }

        /**
         * @param Position2 the Position2 to set
         */
        public void setPosition2(POINT Position2) {
            this.Position2 = Position2;
        }

        /**
         * @return the Index
         */
        public int getIndex() {
            return Index;
        }

        /**
         * @param Index the Index to set
         */
        public void setIndex(int Index) {
            this.Index = Index;
        }

        /**
         * @return the ArrowDirection
         */
        public int getArrowDirection() {
            return iArrowDirection;
        }

        /**
         * @param ArrowDirection the ArrowDirection to set
         */
        public void setArrowDirection(int ArrowDirection) {
            this.iArrowDirection = ArrowDirection;
        }
  
    }
    
    /**
	 * 函数:      FDrawFunIntrusion
         * 函数描述:  画出区域入侵多边形
    */
    class FDrawFunFieldDetecion implements HCNetSDK.FDrawFun
    {
        private int Index;//第几个警戒区域
        private ArrayList<POINT> listPoints = new ArrayList<POINT>();//多边形的各个点
        private int PointsNum = 8;//HCNetSDK.VCA_MAX_POLYGON_POINT_NUM;//多边形的点数
        
        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser) {
            
            if (isInitial()) return;
            gDi.SetTextColor(hDc, 0x0000ff);
            //设置当前设备的画笔.
            HANDLE hPen =  gDi.CreatePen(GDI32.PS_SOLID, 3, 0x00ff00);
            HANDLE hOldPen = gDi.SelectObject(hDc,hPen);//旧画笔句柄存储在hOldPen中
            //if (listPoints.size() < 1) return;
            
            POINT Point0 = listPoints.get(0);//第一个点
            
            String Text = Integer.toString(getIndex()) + "#";
            boolean DrawText = gDi.TextOut(hDc,Point0.x,Point0.y, Text,Text.length());
            gDi.MoveToEx(hDc, Point0.x, Point0.y, null);//画直线
            
            for (int i=1;i<listPoints.size();i++){
                POINT Point = listPoints.get(i);
                gDi.LineTo(hDc, Point.x, Point.y);
            }
            if (listPoints.size() == PointsNum) gDi.LineTo(hDc, Point0.x, Point0.y);
            gDi.SetBkMode(hDc,GDI32.TRANSPARENT);  
            //恢复原来的画笔.
            gDi.SelectObject(hDc,hOldPen);

            //删除创建的画笔.
            gDi.DeleteObject(hPen);
        }
    
        public FDrawFunFieldDetecion(){
            this.Index = 1;
            
        }
        
        public void initialParas(int Num){
            listPoints.clear();
            //this.Index = 1;
            PointsNum = Num;
        }
        private boolean isInitial(){
            if (PointsNum == 0 || listPoints.size() == 0) return true;
            return false;
        }
        public void addPoint(POINT Point){
            if (Point == null) return;
            if (listPoints.size() >= getPointsNum()) {
                listPoints.clear();
            }
            listPoints.add(Point);
        }
        public POINT getPoint(int index){
            if (listPoints.size() <= index) return null;
            return listPoints.get(index);
        }
        /**
         * @return the Index
         */
        public int getIndex() {
            return Index;
        }

        /**
         * @param Index the Index to set
         */
        public boolean setIndex(int Index) {
            //if (Index > getPointsNum()) return false;
            listPoints.clear();
            this.Index = Index;
            return true;
        }
        public int getListSize(){
            return listPoints.size();
        }
        /**
         * @return the PointsNum
         */
        public int getPointsNum() {
            return PointsNum;
        }

        /**
         * @param PointsNum the PointsNum to set
         */
        public void setPointsNum(int PointsNum) {
            this.PointsNum = PointsNum;
        }
    }
    /******************************************************************************
     *类:   CheckListItemRenderer
     *JCheckBox   ListCellRenderer
     ******************************************************************************/
    public class CheckListItemRenderer extends JCheckBox implements ListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus)
        {
            CheckListItem item = (CheckListItem) value;
            this.setSelected(item.getCheck());
            this.setText(item.getText());
            this.setFont(list.getFont());
            this.setForeground(list.getForeground());
            this.setBackground(list.getBackground());
            this.setEnabled(list.isEnabled());
            item.setEnable(list.isEnabled());
            return this;
        }
    }

    /******************************************************************************
     *类:   CheckListItem
     *
     ******************************************************************************/
    public class CheckListItem
    {
        boolean check;
        String text;
        private boolean enable;
        
        public CheckListItem(boolean check, String text)
        {
            this.check = check;
            this.text = text;
        }

        public boolean getCheck()
        {
            return check;
        }

        public void setCheck(boolean _check)
        {
            check = _check;
        }
        public void setSelfCheck(boolean _check)
        {
            if (enable) 
            check = _check;
        }

        public String getText()
        {
            return text;
        }

        public void setText(String _text)
        {
            text = _text;
        }

        /**
         * @return the enable
         */
        public boolean isEnable() {
            return enable;
        }

        /**
         * @param enable the enable to set
         */
        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }

    /******************************************************************************
     *类:   CheckListMouseListener
     *
     ******************************************************************************/
    class CheckListMouseListener extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent e)
        {
            try{
                JList list = (JList) e.getSource();
                int index = list.locationToIndex(e.getPoint());
                CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
                item.setSelfCheck(!item.getCheck());
                Rectangle rect = list.getCellBounds(index, index);
                list.repaint(rect);
            }catch (Exception ex){
                TxtLogger.append(sFileName, "CheckListMouseListener.mousePressed()","系统在点击列表过程中，出现错误"
                                                 + "\r\n                       Exception:" + ex.toString());
            }
        }
    }
    
    
    private class TabParas{
        private String channelNode = "";//存储当前的通道节点名
        private String anotherName = "";//存储当前的设备别名
        private String anotherNameJoin = "";//存储当前的接入设备别名
        private String serialNo = "";//存储当前的设备别序列号
        private String nameOfTree = "";//当前的Tree名字
        private int channel = 0;//存储当前的通道号
        private NativeLong userID=new NativeLong(-1);//用户ID
        private HCNetSDK.NET_DVR_DEVICEINFO_V30 strDeviceInfo = null;//NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30
        private HCNetSDK.NET_DVR_IPPARACFG struIpparaCfg = null;
        TabParas(){}
        //String ChannelNode, String AnotherName, String AnotherNameJoin, String SerialNo, String NameOfTree, 
//                int Channel, NativeLong UserID, HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDeviceInfo, HCNetSDK.NET_DVR_IPPARACFG StruIpparaCfg
        public void setTabParas() {
//            this.channelNode = ChannelNode;
//            this.anotherName = AnotherName;
//            this.anotherNameJoin = AnotherNameJoin;
//            this.serialNo = SerialNo;
//            this.nameOfTree = NameOfTree;
//            this.channel = Channel;
//            this.userID = UserID;
//            this.strDeviceInfo = StrDeviceInfo;
//            this.struIpparaCfg = StruIpparaCfg;
            
            this.channelNode = sChannelNode;
            this.anotherName = sAnotherName;
            this.anotherNameJoin = sAnotherNameJoin;
            this.serialNo = sSerialNo;
            this.nameOfTree = sNameOfTree;
            this.channel = iChannel;
            this.userID = lUserID;
            this.strDeviceInfo = strDeviceInfo;
            this.struIpparaCfg = struIpparaCfg;
        }
        
        public void getTabParas() {
            sChannelNode = this.channelNode;
            sAnotherName = this.anotherName;
            sAnotherNameJoin = this.anotherNameJoin;
            sSerialNo = this.serialNo;
            sNameOfTree = this.nameOfTree;
            iChannel = this.channel;
            lUserID = this.userID;
            strDeviceInfo = this.strDeviceInfo;
            struIpparaCfg = this.struIpparaCfg;
        }
    }

}
