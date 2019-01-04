/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.GDI32.RECT;
import com.sun.jna.examples.win32.User32.POINT;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.ptr.IntByReference;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import jyms.data.TxtLogger;
import jyms.data.ViewsBean;
import jyms.tools.DateUtil.DateHandler;
import jyms.tools.DomXML;
import jyms.tools.GridBagTable.GridBagModel;
import jyms.tools.GridBagTable.GridBagTable;
import jyms.tools.ImageIconBufferPool;
import jyms.ui.PanelUI_LineBorder;
import jyms.ui.ScrollBarUI_White;
import jyms.ui.ScrollPaneUI_White;
import jyms.ui.SliderUI_White;
import jyms.ui.SplitPaneUI_White;
import jyms.ui.TabbedPaneUI_Small;
import jyms.ui.TableHeaderUI_White;
import jyms.ui.TableUI_White;

/**
 *
 * @author John
 */
public class JFrameTotalPreview extends javax.swing.JFrame {

    private final String sFileName = this.getClass().getName() + ".java";
    //从“原先的正在预览的通道预览信息列表”修改为“每一个JPanel对应一个预览对象，可以为null”
    private ArrayList<ChannelRealPlayer> listChannelRealPlayer  = new ArrayList<>();//通道预览信息列表。其和listJPanels的size（）永远都是相等的。对应JPanel列表中每个JPanel的预览信息对象。如果无预览，则该元素为null。
    private ArrayList<JPanel> listJPanels  = new ArrayList<>();//窗口所罗列出的所有JPanel列表（JPanel矩阵）
    private ArrayList<Integer> listIfPreview = new ArrayList<>();//判断JPanel列表中每个JPanel是否已经预览成功。>=0代表已预览成功（对应listChannelRealPlayer中的对象索引）；-1代表未预览。其和listJPanels的size（）永远都是相等的。
    private ChannelRealPlayer currentChannelRealPlayer;//因需要用到当前ChannelRealPlayer对象的地方越来越多，只好创建了此对象
    
    private LinkedHashMap<String, ArrayList<ViewsBean>> hpViews = new LinkedHashMap<>();
    private String modeOfViewShow = "GridLayout";//预览窗口显示模式，默认是标准的“GridLaout”格式；其次是“GridBagLayout”
    private String nameOfViewCustom = "";//系统刚用过的自定义视图名称
    private boolean bAutoSelect = false;//是否是程序控制选择true，还是用户鼠标选择false。是否刷新右侧界面过程中选择JComboBox。保证在下面的编码选择不触动JComboBox的Item改变事件。
    private boolean bAutoTreeValueChanged = false;//是否是完全删除节点的情况下，触发TreeValueChanged事件。
    private GridBagTable jTableView = new GridBagTable(new DefaultTableModel(8, 8)); 
    private int iRowOfView = 3;//自定义视图的行数
    private int iColOfView = 3;//自定义视图的列数
    private DefaultMutableTreeNode m_RootViewNames;//通道树根节点
    private DefaultTableModel selectViewTableModel;//预置点信息
    
    private int iWindowNums = 0;
    private final int iPanelBoderThickness = 1;//预览窗口的Panel、JPanel的Boder粗度
    private final int iPrecision = 16;// the precision of the 22*11 detect unit under D1(704*576) resolution is 32*32, in the demothepicture is displayed in cif(352*288), and precision/2
    
    private DefaultMutableTreeNode m_DeviceRootResource = new DefaultMutableTreeNode("监控点");//通道树根节点
    
//    private ArrayList<DeviceParaBean> listDeviceparaBean;//已管理设备参数表的Bean列表
    //设备登录所产生的用户ID列表，包括设备列表listDeviceparaBean索引，该设备登录所产生的用户ID
    //其数量和listDeviceparaBean是一致的。而且一一对应。
//    private ArrayList listDeviceLoginUserID = new ArrayList();
    private JPanel currentJPanel;//当前的JPanel
    private String sGroupName = "";//存储当前的分组名
//    private HCNetSDK.NET_DVR_DEVICEINFO_V30 strDeviceInfo;//NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30
//    private HCNetSDK.NET_DVR_IPPARACFG struIpparaCfg;
    
    private final GDI32 gDi = GDI32.INSTANCE;
    private final USER32 uSer = USER32.INSTANCE;
    private RECT rectSelectArea = new RECT();
    private FDrawFunSelecctAera fDrawFunSelecctAera= new FDrawFunSelecctAera();//
    //private FDrawFunSetPreviewStateIcon SetPreviewStateIconCallBack = new FDrawFunSetPreviewStateIcon();//设置预览状态图标，比如录像、对讲、声音控制等
    private Point PanelMousePressedPosition;//鼠标按下时的坐标
    private Point PanelMouseReleasedPosition;//鼠标松开时的坐标，因为鼠标有可能拖动
    private boolean bMouseWheelMoving = false;
    
    private JDialogPTZControl  DialogPTZControl;
    private int iZoomPos = 1;
    private int iCommand = -1;
    private boolean bPTZControl = false;
    
    private boolean bZoomIn = false;//是否处于放大状态
    private boolean bFullScreen = false;//是否处于全屏放大状态
    private Dimension   originalScreenSize;//窗口放大前记录原先窗口尺寸
    private boolean bTreeShowOriginal = true;//窗口放大前记录原先设备树是否显示，默认为显示
    //private boolean bOpenPTZOriginal = false;//窗口放大前记录原先是否打开云台控制
    private Dimension dsSizeBeforeZoom = new Dimension();
    private PTZZoomWorker zoomWorker;
        
    private boolean bTreeShow = true;//设备树是否显示，默认为显示
    private int iDividerLocation = 250;//记录隐藏前的左侧宽度
    private int iDividerSize = 8;//记录从LookAndFeel中得到的分割条宽度
    
    private Cursor upleftcursor,uprightcursor,upcursor,downleftcursor,downrightcursor,downcursor,rightcursor,leftcursor;
                
    private Timer currentTimer;//下载用定时器
    
    
    /*-----------------------以下是云台操作专用变量-----------------------*/
    private boolean bOpenPTZ = false;//是否打开云台控制
    private NativeLong m_lRealHandle = new NativeLong(-1);//预览句柄
    private boolean m_bAutoOn = false;//自动左右云台
    private MyBoolean m_bLightOn = new MyBoolean(false);//开启灯光
    private MyBoolean m_bWiperOn = new MyBoolean(false);//开启雨刷
    private MyBoolean m_bFanOn = new MyBoolean(false);//开启风扇
    private MyBoolean m_bHeaterOn = new MyBoolean(false);//开启加热
    private MyBoolean m_bAuxOn1 = new MyBoolean(false);//开启辅助1
    private MyBoolean m_bAuxOn2 = new MyBoolean(false);//开启辅助2
    private boolean m_bIsOnCruise = false;//是否在巡航：RUN_SEQ
    private boolean m_bMemCruise = false;//是否在记录轨迹：STA_MEM_CRUISE
    private boolean m_bRunCruise = false;//是否在运行轨迹：RUN_CRUISE
    
    private ChannelRealPlayer channelRPBall = new ChannelRealPlayer();
    private DefaultTableModel presetTableModel;//预置点信息
    private String[] specialNoOfPreset;//此列表里的预置点只能用于调用，不能有其他操作，比如修改、删除
    private int maxLengthOfPresetName = 20;//预置点名称的最长长度
    private String oldPresetName = "";//存储修改之前的预置点名称
    private final int colOfPresetName = 1;//存储预置点名称的列序号
    
    private DefaultTableModel cruisePointTableModel;//jTableCruise巡航点信息 
    private int iCruisePointNumMax = 32;//单条巡航扫描的最大预置点个数
    private int iDwellTimeMax = 255;//巡航点最大停顿时间
    private int iDwellTimeDefault = 10;//巡航点默认停顿时间
    private int iCruiseSpeedMax = 255;//最大巡航速度
    private int iAddOrUpdate = 1;//1增加巡航点；2为修改巡航点
    private boolean bAutoComboBoxSelect = false;//是否系统读取云台参数过程中自动选择JComboBox，因为编码选择也是触发了ItemStateChanged事件
    
    //为日志填写做准备
    //int[] iCommands = new int[]{2,3,4,5,6,7,8,9,11,12,13,14,15,16,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,3313};
    String[] sCommandMeanings = new String[]{"灯光电源","雨刷开关","风扇开关","加热器开关","辅助设备开关","辅助设备开关2","设置预置点",
                    "清除预置点","焦距变大(倍率变大)","焦距变小(倍率变小)","焦点前调","焦点后调","光圈扩大","光圈缩小","云台上仰","云台下俯","云台左转","云台右转",
                    "云台上仰和左转","云台上仰和右转","云台下俯和左转","云台下俯和右转","云台左右自动扫描","将预置点加入巡航序列","设置巡航点停顿时间","设置巡航速度",
                    "将预置点从巡航序列中删除","开始记录轨迹","停止记录轨迹","开始轨迹","开始巡航","停止巡航","转到预置点","云台下俯和焦距变大(倍率变大)",
                    "云台下俯和焦距变小(倍率变小)","云台左转和焦距变大(倍率变大)","云台左转和焦距变小(倍率变小)","云台右转和焦距变大(倍率变大)",
                    "云台右转和焦距变小(倍率变小)","云台上仰和左转和焦距变大(倍率变大)","云台上仰和左转和焦距变小(倍率变小)","云台上仰和右转和焦距变大(倍率变大)",
                    "云台上仰和右转和焦距变小(倍率变小)","云台下俯和左转和焦距变大(倍率变大)","云台下俯和左转和焦距变小(倍率变小)","云台下俯和右转和焦距变大(倍率变大)",
                    "云台下俯和右转和焦距变小(倍率变小)","云台上仰和焦距变大(倍率变大)","云台上仰和焦距变小(倍率变小)","快球云台花样扫描"};
    HashMap<Integer, String> hmCommandMeanings = new HashMap();
    
    /*-----------------------以上是云台操作专用变量-----------------------*/
    
    /**
     * Creates new form JFrameTotalPreview
     */
    public JFrameTotalPreview() {
        initComponents();
        modifyLocales();
        intialCommonMeaning();
        initialFrameParas();
    }
    
    /**
	 * 函数:      initialFrameParas
         * 函数描述:  初始化窗口参数
    */
    private void initialFrameParas(){
        jSplitPanePreview.setDividerLocation(300);
        PanelMousePressedPosition = new Point();
        PanelMouseReleasedPosition = new Point();
        iDividerSize = jSplitPanePreview.getDividerSize();
        
        //修改窗口中的用户信息
        modifyUserInfo();
        
        upleftcursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/jyms/image/upleftcursor.png")),  new Point(16, 16),  "upleftcursor");
        uprightcursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/jyms/image/uprightcursor.png")),  new Point(16, 16),  "uprightcursor");
        upcursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/jyms/image/upcursor.png")),  new Point(16, 16),  "upcursor");
        downleftcursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/jyms/image/downleftcursor.png")),  new Point(16, 16),  "downleftcursor");
        downrightcursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/jyms/image/downrightcursor.png")),  new Point(0, 0),  "downrightcursor");
        downcursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/jyms/image/downcursor.png")),  new Point(0, 0),  "downcursor");
        rightcursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/jyms/image/rightcursor.png")),  new Point(0, 0),  "rightcursor");
        leftcursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/jyms/image/leftcursor.png")),  new Point(0, 0),  "leftcursor");
        
        //云台隐藏
        jPanelEast.setVisible(false);
        
        //系统时钟
        currentTimer = new Timer();
        currentTimer.schedule(new CurrentTimeTask(), 0, 1000);//0秒后开始响应函数，间隔周期为1秒
        
    
        /*-----------------------以下是云台操作初始化-----------------------*/
        
        presetTableModel = CommonParas.initialNormalOneEditTableModel(sPresetTabeTile);//new String[]{"ID", "预置点"});
        jTablePreset.setModel(presetTableModel);
        jTablePreset.setRowHeight(30);
        
        presetTableModel.addTableModelListener(new TableModelListener(){
                @Override
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    int type = e.getType();
                    if (type != TableModelEvent.UPDATE) return;
                    if(column != colOfPresetName) return;
                    String NewPresetName = (String)jTablePreset.getValueAt(row, column);
                    if (!NewPresetName.equals(oldPresetName)){
                        //判断预置点名称的长度。不能大于maxLengthOfPresetName
                        if (NewPresetName.length() > maxLengthOfPresetName) {
                            NewPresetName = NewPresetName.substring(0, maxLengthOfPresetName);
                            jTablePreset.setValueAt(NewPresetName, row, column);
                        }
                        //修改预置点名称
                        modifyPresetName(row, column, oldPresetName, NewPresetName);
                    }
                }
        });
        cruisePointTableModel = CommonParas.initialNormalNoEditTableModel(sCruisePointTabeTile);//new String[]{"ID", "巡航点","时速"});
        jTableCruise.setModel(cruisePointTableModel);
        jTableCruise.setRowHeight(30);
        
        setTableColWidth();
        /*-----------------------以上是云台操作初始化-----------------------*/
        
        jTreeViewNames.setModel(this.initialViewNamesTreeModel());
        jTreeResource.setModel(this.initialResourceTreeModel());
        //添加自定义GridBagTable
        jPanelViewCustom.add(jTableView, java.awt.BorderLayout.CENTER);
        
        //UI操作
        setSelfUI();
        
        //自定义视图
        selectViewTableModel = CommonParas.initialNormalNoEditTableModel(sViewTabeTile);//new String[]{"","自定义视图列表"});
        jTableSelectView.setModel(selectViewTableModel);
        jTableSelectView.setRowHeight(40);
        setViewTableColWidth();
        //自定义视图树
        CreateViewsTree();

        //设备分组资源树
        createMyGroupTree();
        //弹出菜单项的可用性（根据用户权限）
        refreshMenuStateByAuthority();
        //窗口数目初始化
        jButtonView4.doClick();

    }
    
    /**
        * 函数:      modifyUserInfo
        * 函数描述:  修改窗口中的用户信息
    */
    private void modifyUserInfo(){
        jLabelUserName.setText(CommonParas.UserState.UserName);
        jLabelUserType.setText(CommonParas.UserState.UserType);
        jLabelLogTime.setText(CommonParas.UserState.LogonTime);//.substring(0, 19)
    }
    /**
        * 函数:      createMyGroupTree
        * 函数描述:  创建窗口中的设备分组资源树
    */
    public void createMyGroupTree(){
        //设备分组资源树
        CommonParas.CreateGroupResourceTree(jTreeResource, m_DeviceRootResource,CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE, sFileName);
    }
    
    /**
	 * 函数:      doAfterSwitchUser
         * 函数描述:  切换用户之后重新处理当前窗口中的信息
    */
    public void doAfterSwitchUser(){
        //修改窗口中的用户信息
        modifyUserInfo();
        //弹出菜单项的可用性（根据用户权限）
        refreshMenuStateByAuthority();
    }
    
    /**
        * 函数:      CreateViewsTree
        * 函数描述:  建立自定义视图树
    */
    private void CreateViewsTree(){
        try{
            //自定义视图
            hpViews = ViewsBean.getAllViewsHashMap(sFileName);
            Iterator<String> HSViewNames =  hpViews.keySet().iterator();
            
            //一次只能选一个节点
            jTreeViewNames.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
            DefaultTreeModel TreeModel = ((DefaultTreeModel) jTreeViewNames.getModel());//获取树模型

            bAutoTreeValueChanged = true;//防止在完全删除节点的情况下，触发TreeValueChanged事件。

            //if (HSViewNames)
            m_RootViewNames.removeAllChildren();//根节点删除所有的子节点
            TreeModel.reload();//将添加的节点显示到界面
            
            //选择视图窗口的JTable内容也在这里更新
            Vector v = selectViewTableModel.getDataVector();
            if (v != null) v.clear();
            
            DefaultMutableTreeNode ViewNode;//设备节点
            int ViewNum = 0;
            while  (HSViewNames.hasNext()){  
                String   TmpViewName  =  HSViewNames.next(); 
                ViewNode = new DefaultMutableTreeNode(TmpViewName);
                TreeModel.insertNodeInto(ViewNode, m_RootViewNames, ViewNum++);//在根节点添加设备节点
                //选择视图窗口的JTable内容也在这里更新
                Vector newRow = new Vector();
                newRow.add(ViewNum);
                newRow.add(TmpViewName);
                selectViewTableModel.addRow(newRow);
            }  

            TreeModel.reload();//将添加的节点显示到界面
            //选择视图窗口的JTable内容也在这里更新
            selectViewTableModel.fireTableDataChanged();
            bAutoTreeValueChanged = false;//防止在完全删除节点的情况下，触发TreeValueChanged事件。

//            if (ViewNum > 0 ) {
//                TreePath path = jTreeViewNames.getNextMatch(ViewName, 0, Position.Bias.Forward);
//                jTreeViewNames.setSelectionPath(path);
//            }

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "CreateViewsTree()","系统在建立自定义视图树的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        bAutoTreeValueChanged = false;//防止在完全删除节点的情况下，触发TreeValueChanged事件。
    }
    /**
        * 函数:      setSelfUI
        * 函数描述:  UI操作
    */
    private void setSelfUI(){
        //UI操作
        CommonParas.setJButtonUnDecorated(jButtonTreeShow);
        CommonParas.setJButtonUnDecorated(jButtonPTZShow);
        
        //上下左右自动
        CommonParas.setJButtonUnDecorated(jButtonLeft);
        CommonParas.setJButtonUnDecorated(jButtonLeftDown);
        CommonParas.setJButtonUnDecorated(jButtonLeftUp);
        
        CommonParas.setJButtonUnDecorated(jButtonRight);
        CommonParas.setJButtonUnDecorated(jButtonRightDown);
        CommonParas.setJButtonUnDecorated(jButtonRightUp);
        CommonParas.setJButtonUnDecorated(jButtonUp);
        CommonParas.setJButtonUnDecorated(jButtondown);
        CommonParas.setJButtonUnDecorated(jButtonAuto);
        
        //放大缩小
        CommonParas.setJButtonUnDecorated(jButtonFocusFar);
        CommonParas.setJButtonUnDecorated(jButtonFocusNear);        
        CommonParas.setJButtonUnDecorated(jButtonIrisClose);
        CommonParas.setJButtonUnDecorated(jButtonIrisOpen);
        CommonParas.setJButtonUnDecorated(jButtonZoomOut);
        CommonParas.setJButtonUnDecorated(jButtonZoomIn);
        
        //灯光、风扇、雨刷、加热、辅助
//        CommonParas.setJButtonUnDecorated(jButtonLight);
//        CommonParas.setJButtonUnDecorated(jButtonFanPwron);
//        CommonParas.setJButtonUnDecorated(jButtonHeater);
//        CommonParas.setJButtonUnDecorated(jButtonWiperPwron);
//        CommonParas.setJButtonUnDecorated(jButtonAux1);
//        CommonParas.setJButtonUnDecorated(jButtonAux2);
        
        //预置点
        CommonParas.setJButtonUnDecorated(jButtonDeletePreset);
        CommonParas.setJButtonUnDecorated(jButtonGotoPreset);
        CommonParas.setJButtonUnDecorated(jButtonSetPreset);
        CommonParas.setJButtonUnDecorated(jButtonPTZPark);

        //巡航路径
        CommonParas.setJButtonUnDecorated(jButtonRunSeq);
        CommonParas.setJButtonUnDecorated(jButtonAddPoint);
        CommonParas.setJButtonUnDecorated(jButtonDelPoint);
        
        //云台轨迹
        CommonParas.setJButtonUnDecorated(jButtonRunCruise);
        CommonParas.setJButtonUnDecorated(jButtonMemCruise);
        CommonParas.setJButtonUnDecorated(jButtonDeleteAllCruise);
        CommonParas.setJButtonUnDecorated(jButtonDeleteCruise);
 
        jSliderSpeed.setUI(new SliderUI_White(jSliderSpeed));
        jTabbedPanePTZ.setUI(new TabbedPaneUI_Small());

        jTablePreset.getTableHeader().setUI(new TableHeaderUI_White());
        jTablePreset.setUI(new TableUI_White());
        jScrollPanePreset.setUI(new ScrollPaneUI_White());//jScrollPaneAlarmOut
        jScrollPanePreset.getVerticalScrollBar().setUI(new ScrollBarUI_White());
        jScrollPanePreset.getHorizontalScrollBar().setUI(new ScrollBarUI_White());
        
        jTableCruise.getTableHeader().setUI(new TableHeaderUI_White());
        jTableCruise.setUI(new TableUI_White());
        jScrollPaneCruise.setUI(new ScrollPaneUI_White());//jScrollPaneAlarmOut
        jScrollPaneCruise.getVerticalScrollBar().setUI(new ScrollBarUI_White());
        jScrollPaneCruise.getHorizontalScrollBar().setUI(new ScrollBarUI_White());
        
        CommonParas.setJButtonUnDecorated(jButtonExit2);
        
        //自定义视图表
        jSplitPaneCustomizeView.setUI(new SplitPaneUI_White());
        jPanelViewsNames.setUI(new PanelUI_LineBorder());
        jPanelViewCustom.setUI(new PanelUI_LineBorder());
        jScrollPaneViewNames.setUI(new ScrollPaneUI_White());
        jScrollPaneViewNames.getVerticalScrollBar().setUI(new ScrollBarUI_White());
        jScrollPaneViewNames.getHorizontalScrollBar().setUI(new ScrollBarUI_White());
        CommonParas.setJButtonUnDecorated(jButtonExit3);
        jTableSelectView.getTableHeader().setUI(new TableHeaderUI_White());
        jTableSelectView.setUI(new TableUI_White());
        jScrollPaneSelectView.setUI(new ScrollPaneUI_White());//jScrollPaneAlarmOut
        jScrollPaneSelectView.getVerticalScrollBar().setUI(new ScrollBarUI_White());
        jScrollPaneSelectView.getHorizontalScrollBar().setUI(new ScrollBarUI_White());
        //jPanelSelectView.setUI(new PanelUI_LineBorder());
        CommonParas.setJButtonUnDecorated(jButtonExit4);
        

        /*原先总是出现没有原先效果的问题，最后才查找到CreateGroupResourceTree函数中用到了UpdateUI函数造成的*/
        //UI界面操作
//        jTreeResource.setCellRenderer(new TreeUtil.CustomNodeTreeCellRenderer());
//        jyms.tools.TreeUtil.modifyTreeCellRenderer(jTreeResource);

        jTreeViewNames.setCellRenderer(null);

    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuPreview = new javax.swing.JPopupMenu();
        jMenuItemPreview = new javax.swing.JMenuItem();
        jMenuItemStopAllView = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemRecord = new javax.swing.JMenuItem();
        jMenuItemCapture = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemPTZCtrl = new javax.swing.JMenuItem();
        jMenuItemPTZWindow = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemCorrectTime = new javax.swing.JMenuItem();
        jMenuItemTalkback = new javax.swing.JMenuItem();
        jMenuItemOpenVoice = new javax.swing.JMenuItem();
        jMenuItemVideoEffect = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFishBallTrack = new javax.swing.JMenuItem();
        jMenuItemSetupFishBallTrack = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemLockScreen = new javax.swing.JMenuItem();
        jMenuItemFullScreen = new javax.swing.JMenuItem();
        wnd = new javax.swing.JDialog();
        jDialogCruisePoint = new javax.swing.JDialog();
        jPanelFirst = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jButtonExit2 = new javax.swing.JButton();
        jPanelCenter = new javax.swing.JPanel();
        jLabelCruisePoint = new javax.swing.JLabel();
        jComboBoxCruisePoint = new javax.swing.JComboBox<>();
        jLabelPresetPoint = new javax.swing.JLabel();
        jComboBoxPreset = new javax.swing.JComboBox<>();
        jLabelCruiseTime = new javax.swing.JLabel();
        jComboBoxDwell = new javax.swing.JComboBox<>();
        jLabelCruiseSpeed = new javax.swing.JLabel();
        jComboBoxCruiseSpeed = new javax.swing.JComboBox<>();
        jPanelLast = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jDialogCustomizeView = new javax.swing.JDialog();
        jPanelFirst1 = new javax.swing.JPanel();
        jLabelTitle1 = new javax.swing.JLabel();
        jButtonExit3 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jSplitPaneCustomizeView = new javax.swing.JSplitPane();
        jPanelViewsNames = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jButtonAddView = new javax.swing.JButton();
        jButtonDelView = new javax.swing.JButton();
        jScrollPaneViewNames = new javax.swing.JScrollPane();
        jTreeViewNames = new javax.swing.JTree();
        jPanelViewCustom = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jLabelRow = new javax.swing.JLabel();
        jComboBoxViewRow = new javax.swing.JComboBox<>();
        jLabelCol = new javax.swing.JLabel();
        jComboBoxViewCol = new javax.swing.JComboBox<>();
        jButtonMerge = new javax.swing.JButton();
        jButtonMergeCancel = new javax.swing.JButton();
        jButtonMergeClear = new javax.swing.JButton();
        jPanel24 = new javax.swing.JPanel();
        jButtonOK2 = new javax.swing.JButton();
        jButtonCance2 = new javax.swing.JButton();
        jDialogSelectView = new javax.swing.JDialog();
        jPanelSelectView = new javax.swing.JPanel();
        jPanelFirst2 = new javax.swing.JPanel();
        jLabelTitle2 = new javax.swing.JLabel();
        jButtonExit4 = new javax.swing.JButton();
        jScrollPaneSelectView = new javax.swing.JScrollPane();
        jTableSelectView = new javax.swing.JTable();
        jPanelFoot2 = new javax.swing.JPanel();
        jButtonViewCompile = new javax.swing.JButton();
        jSplitPanePreview = new javax.swing.JSplitPane();
        jPanelLeft = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabelUserNameTag = new javax.swing.JLabel();
        jLabelUserTypeTag = new javax.swing.JLabel();
        jLabelLogTimeTag = new javax.swing.JLabel();
        jLabelCurrentTimeTag = new javax.swing.JLabel();
        jLabelUserType = new javax.swing.JLabel();
        jLabelUserName = new javax.swing.JLabel();
        jLabelLogTime = new javax.swing.JLabel();
        jLabelCurrentTime = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabelChannel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeResource = new javax.swing.JTree();
        jPanel7 = new javax.swing.JPanel();
        jButtonPlayBack = new javax.swing.JButton();
        jButtonReturnMain = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jButtonLogOut = new javax.swing.JButton();
        jPanelRight = new javax.swing.JPanel();
        jPanelPreviewContainer = new javax.swing.JPanel();
        jPanelFoot = new javax.swing.JPanel();
        jButtonView1 = new javax.swing.JButton();
        jButtonView4 = new javax.swing.JButton();
        jButtonView9 = new javax.swing.JButton();
        jButtonView16 = new javax.swing.JButton();
        jButtonView25 = new javax.swing.JButton();
        jButtonView36 = new javax.swing.JButton();
        jButtonView49 = new javax.swing.JButton();
        jButtonPTZShow = new javax.swing.JButton();
        jButtonTreeShow = new javax.swing.JButton();
        jButtonView64 = new javax.swing.JButton();
        jButtonView6 = new javax.swing.JButton();
        jButtonView8 = new javax.swing.JButton();
        jButtonViewCustom = new javax.swing.JButton();
        jPanelEast = new javax.swing.JPanel();
        jPanelPTZ = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new JPanel (){

            public void paintComponent(Graphics g)
            {
                int x=0,y=0;
                ImageIcon icon=new ImageIcon(getClass().getResource("/jyms/image/ptzbackground.png"));

                g.drawImage(icon.getImage(),x,y,getSize().width,getSize().height,this);

                while(true){
                    if(x>getSize().width && y>getSize().height)break;
                    //这段代码是为了保证在窗口大于图片时，图片仍能覆盖整个窗口
                    if(x>getSize().width){
                        x=0;
                        y+=icon.getIconHeight();
                    }else
                    x+=icon.getIconWidth();
                }
            }
        };
        jPanel10 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jButtonLeftUp = new javax.swing.JButton();
        jButtonUp = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jButtonRightUp = new javax.swing.JButton();
        jButtonLeft = new javax.swing.JButton();
        jButtonAuto = new javax.swing.JButton();
        jButtonRight = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jButtonLeftDown = new javax.swing.JButton();
        jButtondown = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jButtonRightDown = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButtonZoomIn = new javax.swing.JButton();
        jLabelMagnification = new javax.swing.JLabel();
        jButtonZoomOut = new javax.swing.JButton();
        jButtonFocusFar = new javax.swing.JButton();
        jLabelFocus = new javax.swing.JLabel();
        jButtonFocusNear = new javax.swing.JButton();
        jButtonIrisOpen = new javax.swing.JButton();
        jLabelAperture = new javax.swing.JLabel();
        jButtonIrisClose = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jSliderSpeed = new javax.swing.JSlider();
        jLabel11 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButtonHeater = new javax.swing.JButton();
        jButtonLight = new javax.swing.JButton();
        jButtonWiperPwron = new javax.swing.JButton();
        jButtonAux1 = new javax.swing.JButton();
        jButtonAux2 = new javax.swing.JButton();
        jButtonFanPwron = new javax.swing.JButton();
        jTabbedPanePTZ = new javax.swing.JTabbedPane();
        jPanelPreset = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jButtonGotoPreset = new javax.swing.JButton();
        jButtonSetPreset = new javax.swing.JButton();
        jButtonDeletePreset = new javax.swing.JButton();
        jButtonPTZPark = new javax.swing.JButton();
        jScrollPanePreset = new javax.swing.JScrollPane();
        jTablePreset = new javax.swing.JTable();
        jPanelCruise = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jComboBoxCruise = new javax.swing.JComboBox();
        jPanel17 = new javax.swing.JPanel();
        jButtonRunSeq = new javax.swing.JButton();
        jButtonAddPoint = new javax.swing.JButton();
        jButtonDelPoint = new javax.swing.JButton();
        jScrollPaneCruise = new javax.swing.JScrollPane();
        jTableCruise = new javax.swing.JTable();
        jPanelTrack = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jComboBoxTrack = new javax.swing.JComboBox();
        jPanel19 = new javax.swing.JPanel();
        jButtonMemCruise = new javax.swing.JButton();
        jButtonRunCruise = new javax.swing.JButton();
        jButtonDeleteCruise = new javax.swing.JButton();
        jButtonDeleteAllCruise = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        jLabelPTZCtrl = new javax.swing.JLabel();

        jPopupMenuPreview.setToolTipText("");
        jPopupMenuPreview.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenuPreviewPopupMenuCanceled(evt);
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenuPreviewPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        jMenuItemPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_stopview.png"))); // NOI18N
        jMenuItemPreview.setText("开始预览");
        jMenuItemPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPreviewActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemPreview);

        jMenuItemStopAllView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_stopallview.png"))); // NOI18N
        jMenuItemStopAllView.setText("停止所有预览");
        jMenuItemStopAllView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStopAllViewActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemStopAllView);
        jPopupMenuPreview.add(jSeparator5);

        jMenuItemRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_startrecord.png"))); // NOI18N
        jMenuItemRecord.setText("开始录像");
        jMenuItemRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRecordActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemRecord);

        jMenuItemCapture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_capture.png"))); // NOI18N
        jMenuItemCapture.setText("抓图");
        jMenuItemCapture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCaptureActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemCapture);
        jPopupMenuPreview.add(jSeparator1);

        jMenuItemPTZCtrl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_startptzctrl.png"))); // NOI18N
        jMenuItemPTZCtrl.setText("开启窗口云台控制");
        jMenuItemPTZCtrl.setToolTipText("");
        jMenuItemPTZCtrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPTZCtrlActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemPTZCtrl);

        jMenuItemPTZWindow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_openptzwindow.png"))); // NOI18N
        jMenuItemPTZWindow.setText("打开云台控制窗口");
        jMenuItemPTZWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPTZWindowActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemPTZWindow);
        jPopupMenuPreview.add(jSeparator2);

        jMenuItemCorrectTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_checktime.png"))); // NOI18N
        jMenuItemCorrectTime.setText("校时");
        jMenuItemCorrectTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCorrectTimeActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemCorrectTime);

        jMenuItemTalkback.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_startspeak.png"))); // NOI18N
        jMenuItemTalkback.setText("开始对讲");
        jMenuItemTalkback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTalkbackActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemTalkback);

        jMenuItemOpenVoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_closevoice.png"))); // NOI18N
        jMenuItemOpenVoice.setText("打开声音");
        jMenuItemOpenVoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenVoiceActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemOpenVoice);

        jMenuItemVideoEffect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_videoparaset.png"))); // NOI18N
        jMenuItemVideoEffect.setText("视频参数设置");
        jMenuItemVideoEffect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemVideoEffectActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemVideoEffect);
        jPopupMenuPreview.add(jSeparator3);

        jMenuItemFishBallTrack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_startfishball.png"))); // NOI18N
        jMenuItemFishBallTrack.setText("开始鱼球联动");
        jMenuItemFishBallTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFishBallTrackActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemFishBallTrack);

        jMenuItemSetupFishBallTrack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_setfishball.png"))); // NOI18N
        jMenuItemSetupFishBallTrack.setText("设置鱼球联动");
        jMenuItemSetupFishBallTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSetupFishBallTrackActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemSetupFishBallTrack);
        jPopupMenuPreview.add(jSeparator4);

        jMenuItemLockScreen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_lockscreen.png"))); // NOI18N
        jMenuItemLockScreen.setText("锁屏");
        jMenuItemLockScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLockScreenActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemLockScreen);

        jMenuItemFullScreen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_fullscreen.png"))); // NOI18N
        jMenuItemFullScreen.setText("全屏");
        jMenuItemFullScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFullScreenActionPerformed(evt);
            }
        });
        jPopupMenuPreview.add(jMenuItemFullScreen);

        javax.swing.GroupLayout wndLayout = new javax.swing.GroupLayout(wnd.getContentPane());
        wnd.getContentPane().setLayout(wndLayout);
        wndLayout.setHorizontalGroup(
            wndLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        wndLayout.setVerticalGroup(
            wndLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jDialogCruisePoint.setTitle("添加巡航点");
        jDialogCruisePoint.setMinimumSize(new java.awt.Dimension(397, 314));
        jDialogCruisePoint.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        jDialogCruisePoint.setUndecorated(true);

        jPanelFirst.setBackground(new java.awt.Color(204, 204, 204));

        jLabelTitle.setFont(new java.awt.Font("微软雅黑", 1, 18)); // NOI18N
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("添加巡航点");

        jButtonExit2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/close.png"))); // NOI18N
        jButtonExit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExit2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFirstLayout = new javax.swing.GroupLayout(jPanelFirst);
        jPanelFirst.setLayout(jPanelFirstLayout);
        jPanelFirstLayout.setHorizontalGroup(
            jPanelFirstLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFirstLayout.createSequentialGroup()
                .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExit2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelFirstLayout.setVerticalGroup(
            jPanelFirstLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFirstLayout.createSequentialGroup()
                .addComponent(jButtonExit2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
            .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jDialogCruisePoint.getContentPane().add(jPanelFirst, java.awt.BorderLayout.PAGE_START);

        jLabelCruisePoint.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelCruisePoint.setText("巡航点：");

        jComboBoxCruisePoint.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelPresetPoint.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelPresetPoint.setText("预置点：");

        jComboBoxPreset.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelCruiseTime.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelCruiseTime.setText("巡航时间：");

        jComboBoxDwell.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelCruiseSpeed.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelCruiseSpeed.setText("巡航速度：");

        jComboBoxCruiseSpeed.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        javax.swing.GroupLayout jPanelCenterLayout = new javax.swing.GroupLayout(jPanelCenter);
        jPanelCenter.setLayout(jPanelCenterLayout);
        jPanelCenterLayout.setHorizontalGroup(
            jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCenterLayout.createSequentialGroup()
                .addContainerGap(68, Short.MAX_VALUE)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCenterLayout.createSequentialGroup()
                        .addComponent(jLabelCruiseSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxCruiseSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCenterLayout.createSequentialGroup()
                        .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabelCruiseTime, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .addComponent(jLabelPresetPoint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxPreset, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxDwell, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCenterLayout.createSequentialGroup()
                        .addComponent(jLabelCruisePoint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxCruisePoint, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanelCenterLayout.setVerticalGroup(
            jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCenterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCruisePoint)
                    .addComponent(jComboBoxCruisePoint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPresetPoint)
                    .addComponent(jComboBoxPreset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCruiseTime)
                    .addComponent(jComboBoxDwell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCruiseSpeed)
                    .addComponent(jComboBoxCruiseSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jDialogCruisePoint.getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jButtonOK.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok2.png"))); // NOI18N
        jButtonOK.setText("确定");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonCancel.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cancel2.png"))); // NOI18N
        jButtonCancel.setText("取消");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLastLayout = new javax.swing.GroupLayout(jPanelLast);
        jPanelLast.setLayout(jPanelLastLayout);
        jPanelLastLayout.setHorizontalGroup(
            jPanelLastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLastLayout.createSequentialGroup()
                .addContainerGap(115, Short.MAX_VALUE)
                .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61))
        );

        jPanelLastLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonCancel, jButtonOK});

        jPanelLastLayout.setVerticalGroup(
            jPanelLastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLastLayout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(jPanelLastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK)
                    .addComponent(jButtonCancel))
                .addGap(28, 28, 28))
        );

        jDialogCruisePoint.getContentPane().add(jPanelLast, java.awt.BorderLayout.PAGE_END);

        jDialogCustomizeView.setMinimumSize(new java.awt.Dimension(878, 671));
        jDialogCustomizeView.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        jDialogCustomizeView.setUndecorated(true);
        jDialogCustomizeView.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                jDialogCustomizeViewWindowOpened(evt);
            }
        });

        jLabelTitle1.setFont(new java.awt.Font("微软雅黑", 1, 18)); // NOI18N
        jLabelTitle1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle1.setText("编辑自定义视图");

        jButtonExit3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/close.png"))); // NOI18N
        jButtonExit3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExit3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFirst1Layout = new javax.swing.GroupLayout(jPanelFirst1);
        jPanelFirst1.setLayout(jPanelFirst1Layout);
        jPanelFirst1Layout.setHorizontalGroup(
            jPanelFirst1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFirst1Layout.createSequentialGroup()
                .addComponent(jLabelTitle1, javax.swing.GroupLayout.DEFAULT_SIZE, 895, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExit3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelFirst1Layout.setVerticalGroup(
            jPanelFirst1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFirst1Layout.createSequentialGroup()
                .addComponent(jButtonExit3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
            .addComponent(jLabelTitle1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jDialogCustomizeView.getContentPane().add(jPanelFirst1, java.awt.BorderLayout.PAGE_START);

        jPanelViewsNames.setLayout(new java.awt.BorderLayout());

        jButtonAddView.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonAddView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/add2.png"))); // NOI18N
        jButtonAddView.setText("添加");
        jButtonAddView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddViewActionPerformed(evt);
            }
        });

        jButtonDelView.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonDelView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/delete2.png"))); // NOI18N
        jButtonDelView.setText("删除");
        jButtonDelView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonAddView)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonDelView))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAddView)
                    .addComponent(jButtonDelView))
                .addContainerGap())
        );

        jPanelViewsNames.add(jPanel27, java.awt.BorderLayout.PAGE_START);

        jTreeViewNames.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jTreeViewNames.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeViewNamesValueChanged(evt);
            }
        });
        jScrollPaneViewNames.setViewportView(jTreeViewNames);

        jPanelViewsNames.add(jScrollPaneViewNames, java.awt.BorderLayout.CENTER);

        jSplitPaneCustomizeView.setLeftComponent(jPanelViewsNames);

        jPanelViewCustom.setLayout(new java.awt.BorderLayout());

        jLabelRow.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelRow.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelRow.setText("行：");

        jComboBoxViewRow.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxViewRow.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "3", "4", "5", "6", "7", "8" }));
        jComboBoxViewRow.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxViewRowItemStateChanged(evt);
            }
        });

        jLabelCol.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelCol.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCol.setText("列：");

        jComboBoxViewCol.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxViewCol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "3", "4", "5", "6", "7", "8" }));
        jComboBoxViewCol.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxViewColItemStateChanged(evt);
            }
        });

        jButtonMerge.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonMerge.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/viewmerge.png"))); // NOI18N
        jButtonMerge.setText("合并");
        jButtonMerge.setToolTipText("");
        jButtonMerge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMergeActionPerformed(evt);
            }
        });

        jButtonMergeCancel.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonMergeCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/viewmergecancel.png"))); // NOI18N
        jButtonMergeCancel.setText("取消合并");
        jButtonMergeCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMergeCancelActionPerformed(evt);
            }
        });

        jButtonMergeClear.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonMergeClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/viewmergecancelall.png"))); // NOI18N
        jButtonMergeClear.setText("全部取消");
        jButtonMergeClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMergeClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel28Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabelRow, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxViewRow, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelCol, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxViewCol, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
                .addComponent(jButtonMerge, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonMergeCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonMergeClear, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        jPanel28Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonMergeCancel, jButtonMergeClear});

        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxViewRow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCol)
                    .addComponent(jComboBoxViewCol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonMergeCancel)
                        .addComponent(jButtonMerge)
                        .addComponent(jButtonMergeClear))
                    .addComponent(jLabelRow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel28Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonMergeCancel, jButtonMergeClear});

        jPanel28Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBoxViewCol, jComboBoxViewRow, jLabelCol, jLabelRow});

        jPanelViewCustom.add(jPanel28, java.awt.BorderLayout.PAGE_START);

        jSplitPaneCustomizeView.setRightComponent(jPanelViewCustom);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPaneCustomizeView)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPaneCustomizeView, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
        );

        jDialogCustomizeView.getContentPane().add(jPanel9, java.awt.BorderLayout.CENTER);

        jButtonOK2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonOK2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok2.png"))); // NOI18N
        jButtonOK2.setText("保存");
        jButtonOK2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOK2ActionPerformed(evt);
            }
        });

        jButtonCance2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonCance2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cancel2.png"))); // NOI18N
        jButtonCance2.setText("取消");
        jButtonCance2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCance2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap(654, Short.MAX_VALUE)
                .addComponent(jButtonOK2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCance2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK2)
                    .addComponent(jButtonCance2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jDialogCustomizeView.getContentPane().add(jPanel24, java.awt.BorderLayout.PAGE_END);

        jDialogSelectView.setMinimumSize(new java.awt.Dimension(225, 480));
        jDialogSelectView.setUndecorated(true);
        jDialogSelectView.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                jDialogSelectViewWindowLostFocus(evt);
            }
        });

        jPanelSelectView.setLayout(new java.awt.BorderLayout());

        jLabelTitle2.setFont(new java.awt.Font("微软雅黑", 1, 18)); // NOI18N
        jLabelTitle2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle2.setText("选择自定义视图");

        jButtonExit4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/close.png"))); // NOI18N
        jButtonExit4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExit4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFirst2Layout = new javax.swing.GroupLayout(jPanelFirst2);
        jPanelFirst2.setLayout(jPanelFirst2Layout);
        jPanelFirst2Layout.setHorizontalGroup(
            jPanelFirst2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFirst2Layout.createSequentialGroup()
                .addComponent(jLabelTitle2, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExit4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelFirst2Layout.setVerticalGroup(
            jPanelFirst2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFirst2Layout.createSequentialGroup()
                .addComponent(jButtonExit4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
            .addComponent(jLabelTitle2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanelSelectView.add(jPanelFirst2, java.awt.BorderLayout.PAGE_START);

        jTableSelectView.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableSelectView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableSelectViewMouseClicked(evt);
            }
        });
        jScrollPaneSelectView.setViewportView(jTableSelectView);

        jPanelSelectView.add(jScrollPaneSelectView, java.awt.BorderLayout.CENTER);

        jButtonViewCompile.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonViewCompile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/viewedit.png"))); // NOI18N
        jButtonViewCompile.setText("编辑视图");
        jButtonViewCompile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonViewCompileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFoot2Layout = new javax.swing.GroupLayout(jPanelFoot2);
        jPanelFoot2.setLayout(jPanelFoot2Layout);
        jPanelFoot2Layout.setHorizontalGroup(
            jPanelFoot2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFoot2Layout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addComponent(jButtonViewCompile)
                .addGap(48, 48, 48))
        );
        jPanelFoot2Layout.setVerticalGroup(
            jPanelFoot2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFoot2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonViewCompile)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelSelectView.add(jPanelFoot2, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jDialogSelectViewLayout = new javax.swing.GroupLayout(jDialogSelectView.getContentPane());
        jDialogSelectView.getContentPane().setLayout(jDialogSelectViewLayout);
        jDialogSelectViewLayout.setHorizontalGroup(
            jDialogSelectViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogSelectViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelSelectView, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialogSelectViewLayout.setVerticalGroup(
            jDialogSelectViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogSelectViewLayout.createSequentialGroup()
                .addComponent(jPanelSelectView, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 13, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("设备预览");
        setName("设备预览"); // NOI18N
        setUndecorated(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jSplitPanePreview.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSplitPanePreviewPropertyChange(evt);
            }
        });

        jPanelLeft.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel6.setBackground(new java.awt.Color(64, 64, 64));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 100, 100)));
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));

        jLabelUserNameTag.setBackground(new java.awt.Color(64, 64, 64));
        jLabelUserNameTag.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelUserNameTag.setForeground(new java.awt.Color(255, 255, 255));
        jLabelUserNameTag.setText("用 户 名：");
        jLabelUserNameTag.setToolTipText("");

        jLabelUserTypeTag.setBackground(new java.awt.Color(64, 64, 64));
        jLabelUserTypeTag.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelUserTypeTag.setForeground(new java.awt.Color(255, 255, 255));
        jLabelUserTypeTag.setText("用户等级：");

        jLabelLogTimeTag.setBackground(new java.awt.Color(64, 64, 64));
        jLabelLogTimeTag.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelLogTimeTag.setForeground(new java.awt.Color(255, 255, 255));
        jLabelLogTimeTag.setText("登录时间：");

        jLabelCurrentTimeTag.setBackground(new java.awt.Color(64, 64, 64));
        jLabelCurrentTimeTag.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelCurrentTimeTag.setForeground(new java.awt.Color(255, 255, 255));
        jLabelCurrentTimeTag.setText("当前时间：");

        jLabelUserType.setBackground(new java.awt.Color(64, 64, 64));
        jLabelUserType.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabelUserType.setForeground(new java.awt.Color(255, 255, 255));

        jLabelUserName.setBackground(new java.awt.Color(64, 64, 64));
        jLabelUserName.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabelUserName.setForeground(new java.awt.Color(255, 255, 255));

        jLabelLogTime.setBackground(new java.awt.Color(64, 64, 64));
        jLabelLogTime.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabelLogTime.setForeground(new java.awt.Color(255, 255, 255));

        jLabelCurrentTime.setBackground(new java.awt.Color(64, 64, 64));
        jLabelCurrentTime.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabelCurrentTime.setForeground(new java.awt.Color(255, 255, 255));
        jLabelCurrentTime.setToolTipText("");
        jLabelCurrentTime.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelUserNameTag)
                    .addComponent(jLabelUserTypeTag)
                    .addComponent(jLabelCurrentTimeTag)
                    .addComponent(jLabelLogTimeTag))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelLogTime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelCurrentTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelUserType, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelUserNameTag, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelUserTypeTag, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelUserType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelLogTimeTag, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelLogTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelCurrentTimeTag, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCurrentTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabelCurrentTimeTag, jLabelLogTimeTag, jLabelUserNameTag, jLabelUserTypeTag});

        jPanel8.add(jPanel6, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBackground(new java.awt.Color(151, 151, 151));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabelChannel.setBackground(new java.awt.Color(151, 151, 151));
        jLabelChannel.setFont(new java.awt.Font("微软雅黑", 1, 17)); // NOI18N
        jLabelChannel.setForeground(new java.awt.Color(255, 255, 255));
        jLabelChannel.setText("   监控点");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabelChannel, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelChannel, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
        );

        jPanel8.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanelLeft.add(jPanel8, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setBackground(new java.awt.Color(64, 64, 64));
        jScrollPane1.setForeground(new java.awt.Color(255, 255, 255));

        jTreeResource.setBackground(new java.awt.Color(64, 64, 64));
        jTreeResource.setForeground(new java.awt.Color(255, 255, 255));
        jTreeResource.setRootVisible(false);
        jTreeResource.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeResourceMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTreeResource);

        jPanelLeft.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel7.setBackground(new java.awt.Color(64, 64, 64));
        jPanel7.setForeground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new java.awt.GridLayout(4, 1));

        jButtonPlayBack.setBackground(new java.awt.Color(64, 64, 64));
        jButtonPlayBack.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonPlayBack.setForeground(new java.awt.Color(255, 255, 255));
        jButtonPlayBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/playback24.png"))); // NOI18N
        jButtonPlayBack.setText("    录像回放       ");
        jButtonPlayBack.setToolTipText("");
        jButtonPlayBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPlayBackActionPerformed(evt);
            }
        });
        jPanel7.add(jButtonPlayBack);

        jButtonReturnMain.setBackground(new java.awt.Color(64, 64, 64));
        jButtonReturnMain.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonReturnMain.setForeground(new java.awt.Color(255, 255, 255));
        jButtonReturnMain.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/returnmain24.png"))); // NOI18N
        jButtonReturnMain.setText("    返回主界面    ");
        jButtonReturnMain.setToolTipText("");
        jButtonReturnMain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnMainActionPerformed(evt);
            }
        });
        jPanel7.add(jButtonReturnMain);

        jButtonExit.setBackground(new java.awt.Color(64, 64, 64));
        jButtonExit.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonExit.setForeground(new java.awt.Color(255, 255, 255));
        jButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/previewexit24.png"))); // NOI18N
        jButtonExit.setText("    退出实时监控");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });
        jPanel7.add(jButtonExit);

        jButtonLogOut.setBackground(new java.awt.Color(64, 64, 64));
        jButtonLogOut.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonLogOut.setForeground(new java.awt.Color(255, 255, 255));
        jButtonLogOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/logout24.png"))); // NOI18N
        jButtonLogOut.setText("    注销             ");
        jButtonLogOut.setToolTipText("");
        jButtonLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLogOutActionPerformed(evt);
            }
        });
        jPanel7.add(jButtonLogOut);

        jPanelLeft.add(jPanel7, java.awt.BorderLayout.PAGE_END);

        jSplitPanePreview.setLeftComponent(jPanelLeft);

        jPanelRight.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanelPreviewContainerLayout = new javax.swing.GroupLayout(jPanelPreviewContainer);
        jPanelPreviewContainer.setLayout(jPanelPreviewContainerLayout);
        jPanelPreviewContainerLayout.setHorizontalGroup(
            jPanelPreviewContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 712, Short.MAX_VALUE)
        );
        jPanelPreviewContainerLayout.setVerticalGroup(
            jPanelPreviewContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelRight.add(jPanelPreviewContainer, java.awt.BorderLayout.CENTER);

        jPanelFoot.setBackground(new java.awt.Color(64, 64, 64));
        jPanelFoot.setForeground(new java.awt.Color(255, 255, 255));

        jButtonView1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/view1.png"))); // NOI18N
        jButtonView1.setToolTipText("1*1");
        jButtonView1.setBorder(null);
        jButtonView1.setBorderPainted(false);
        jButtonView1.setContentAreaFilled(false);
        jButtonView1.setFocusable(false);
        jButtonView1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonView1ActionPerformed(evt);
            }
        });

        jButtonView4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/view4.png"))); // NOI18N
        jButtonView4.setToolTipText("2*2");
        jButtonView4.setBorder(null);
        jButtonView4.setBorderPainted(false);
        jButtonView4.setContentAreaFilled(false);
        jButtonView4.setFocusable(false);
        jButtonView4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonView4ActionPerformed(evt);
            }
        });

        jButtonView9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/view9.png"))); // NOI18N
        jButtonView9.setToolTipText("3*3");
        jButtonView9.setBorder(null);
        jButtonView9.setBorderPainted(false);
        jButtonView9.setContentAreaFilled(false);
        jButtonView9.setFocusable(false);
        jButtonView9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonView9ActionPerformed(evt);
            }
        });

        jButtonView16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/view16.png"))); // NOI18N
        jButtonView16.setToolTipText("4*4");
        jButtonView16.setBorder(null);
        jButtonView16.setBorderPainted(false);
        jButtonView16.setContentAreaFilled(false);
        jButtonView16.setFocusable(false);
        jButtonView16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonView16ActionPerformed(evt);
            }
        });

        jButtonView25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/view25.png"))); // NOI18N
        jButtonView25.setToolTipText("5*5");
        jButtonView25.setBorder(null);
        jButtonView25.setBorderPainted(false);
        jButtonView25.setContentAreaFilled(false);
        jButtonView25.setFocusable(false);
        jButtonView25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonView25ActionPerformed(evt);
            }
        });

        jButtonView36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/view36.png"))); // NOI18N
        jButtonView36.setToolTipText("6*6");
        jButtonView36.setBorder(null);
        jButtonView36.setBorderPainted(false);
        jButtonView36.setContentAreaFilled(false);
        jButtonView36.setFocusable(false);
        jButtonView36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonView36ActionPerformed(evt);
            }
        });

        jButtonView49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/view49.png"))); // NOI18N
        jButtonView49.setToolTipText("7*7");
        jButtonView49.setBorder(null);
        jButtonView49.setBorderPainted(false);
        jButtonView49.setContentAreaFilled(false);
        jButtonView49.setFocusable(false);
        jButtonView49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonView49ActionPerformed(evt);
            }
        });

        jButtonPTZShow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/left.png"))); // NOI18N
        jButtonPTZShow.setBorder(null);
        jButtonPTZShow.setBorderPainted(false);
        jButtonPTZShow.setContentAreaFilled(false);
        jButtonPTZShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPTZShowActionPerformed(evt);
            }
        });

        jButtonTreeShow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/left.png"))); // NOI18N
        jButtonTreeShow.setBorder(null);
        jButtonTreeShow.setBorderPainted(false);
        jButtonTreeShow.setContentAreaFilled(false);
        jButtonTreeShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTreeShowActionPerformed(evt);
            }
        });

        jButtonView64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/view64.png"))); // NOI18N
        jButtonView64.setToolTipText("8*8");
        jButtonView64.setBorder(null);
        jButtonView64.setBorderPainted(false);
        jButtonView64.setContentAreaFilled(false);
        jButtonView64.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonView64ActionPerformed(evt);
            }
        });

        jButtonView6.setBackground(new java.awt.Color(0, 0, 0));
        jButtonView6.setForeground(new java.awt.Color(255, 255, 255));
        jButtonView6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/view6.png"))); // NOI18N
        jButtonView6.setToolTipText("自定义6视图");
        jButtonView6.setBorder(null);
        jButtonView6.setBorderPainted(false);
        jButtonView6.setContentAreaFilled(false);
        jButtonView6.setFocusable(false);
        jButtonView6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonView6ActionPerformed(evt);
            }
        });

        jButtonView8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/view8.png"))); // NOI18N
        jButtonView8.setToolTipText("自定义8视图");
        jButtonView8.setBorder(null);
        jButtonView8.setBorderPainted(false);
        jButtonView8.setContentAreaFilled(false);
        jButtonView8.setFocusable(false);
        jButtonView8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonView8ActionPerformed(evt);
            }
        });

        jButtonViewCustom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/viewcustom.png"))); // NOI18N
        jButtonViewCustom.setToolTipText("自定义视图");
        jButtonViewCustom.setBorder(null);
        jButtonViewCustom.setBorderPainted(false);
        jButtonViewCustom.setContentAreaFilled(false);
        jButtonViewCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonViewCustomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFootLayout = new javax.swing.GroupLayout(jPanelFoot);
        jPanelFoot.setLayout(jPanelFootLayout);
        jPanelFootLayout.setHorizontalGroup(
            jPanelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFootLayout.createSequentialGroup()
                .addComponent(jButtonTreeShow, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jButtonView1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonView4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonView6, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonView8, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonView9, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonView16, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonView25, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonView36, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonView49, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonView64)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonViewCustom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                .addComponent(jButtonPTZShow, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelFootLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonView49, jButtonView64, jButtonViewCustom});

        jPanelFootLayout.setVerticalGroup(
            jPanelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButtonPTZShow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFootLayout.createSequentialGroup()
                .addGroup(jPanelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonTreeShow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonView1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFootLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonView49, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonView4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonView36, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFootLayout.createSequentialGroup()
                        .addComponent(jButtonView16, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addComponent(jButtonView25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonView64, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonView6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonView8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonView9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonViewCustom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanelFootLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonView16, jButtonView25, jButtonView36, jButtonView49, jButtonView64});

        jPanelRight.add(jPanelFoot, java.awt.BorderLayout.PAGE_END);

        jSplitPanePreview.setRightComponent(jPanelRight);

        getContentPane().add(jSplitPanePreview, java.awt.BorderLayout.CENTER);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel3.setForeground(new java.awt.Color(240, 240, 240));

        jPanel10.setForeground(new java.awt.Color(240, 240, 240));
        jPanel10.setOpaque(false);
        jPanel10.setRequestFocusEnabled(false);
        jPanel10.setLayout(new java.awt.GridLayout(3, 3, 1, 1));

        jPanel12.setForeground(new java.awt.Color(240, 240, 240));
        jPanel12.setOpaque(false);

        jButtonLeftUp.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonLeftUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzleftup.png"))); // NOI18N
        jButtonLeftUp.setToolTipText("云台上仰和左转");
        jButtonLeftUp.setBorder(null);
        jButtonLeftUp.setBorderPainted(false);
        jButtonLeftUp.setContentAreaFilled(false);
        jButtonLeftUp.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButtonLeftUp.setRequestFocusEnabled(false);
        jButtonLeftUp.setRolloverEnabled(false);
        jButtonLeftUp.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButtonLeftUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonLeftUpMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonLeftUpMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addComponent(jButtonLeftUp))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(0, 9, Short.MAX_VALUE)
                .addComponent(jButtonLeftUp))
        );

        jPanel10.add(jPanel12);

        jButtonUp.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzup.png"))); // NOI18N
        jButtonUp.setToolTipText("云台上仰");
        jButtonUp.setBorder(null);
        jButtonUp.setBorderPainted(false);
        jButtonUp.setContentAreaFilled(false);
        jButtonUp.setRequestFocusEnabled(false);
        jButtonUp.setRolloverEnabled(false);
        jButtonUp.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButtonUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonUpMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonUpMouseReleased(evt);
            }
        });
        jPanel10.add(jButtonUp);

        jPanel14.setForeground(new java.awt.Color(240, 240, 240));
        jPanel14.setOpaque(false);

        jButtonRightUp.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonRightUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzrightup.png"))); // NOI18N
        jButtonRightUp.setToolTipText("云台上仰和右转");
        jButtonRightUp.setBorder(null);
        jButtonRightUp.setBorderPainted(false);
        jButtonRightUp.setContentAreaFilled(false);
        jButtonRightUp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonRightUp.setRequestFocusEnabled(false);
        jButtonRightUp.setRolloverEnabled(false);
        jButtonRightUp.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButtonRightUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonRightUpMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonRightUpMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jButtonRightUp)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addGap(0, 9, Short.MAX_VALUE)
                .addComponent(jButtonRightUp))
        );

        jPanel10.add(jPanel14);

        jButtonLeft.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzleft.png"))); // NOI18N
        jButtonLeft.setToolTipText("云台左转 ");
        jButtonLeft.setBorder(null);
        jButtonLeft.setBorderPainted(false);
        jButtonLeft.setContentAreaFilled(false);
        jButtonLeft.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonLeft.setRequestFocusEnabled(false);
        jButtonLeft.setRolloverEnabled(false);
        jButtonLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonLeftMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonLeftMouseReleased(evt);
            }
        });
        jPanel10.add(jButtonLeft);

        jButtonAuto.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzauto.png"))); // NOI18N
        jButtonAuto.setToolTipText("云台左右自动扫描");
        jButtonAuto.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAutoActionPerformed(evt);
            }
        });
        jPanel10.add(jButtonAuto);

        jButtonRight.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzright.png"))); // NOI18N
        jButtonRight.setToolTipText("云台右转");
        jButtonRight.setBorder(null);
        jButtonRight.setBorderPainted(false);
        jButtonRight.setContentAreaFilled(false);
        jButtonRight.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButtonRight.setRequestFocusEnabled(false);
        jButtonRight.setRolloverEnabled(false);
        jButtonRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonRightMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonRightMouseReleased(evt);
            }
        });
        jPanel10.add(jButtonRight);

        jPanel15.setForeground(new java.awt.Color(240, 240, 240));
        jPanel15.setOpaque(false);

        jButtonLeftDown.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonLeftDown.setForeground(new java.awt.Color(240, 240, 240));
        jButtonLeftDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzleftdown.png"))); // NOI18N
        jButtonLeftDown.setToolTipText("云台下俯和左转");
        jButtonLeftDown.setBorder(null);
        jButtonLeftDown.setBorderPainted(false);
        jButtonLeftDown.setContentAreaFilled(false);
        jButtonLeftDown.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButtonLeftDown.setRequestFocusEnabled(false);
        jButtonLeftDown.setRolloverEnabled(false);
        jButtonLeftDown.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButtonLeftDown.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonLeftDownMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonLeftDownMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addComponent(jButtonLeftDown))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jButtonLeftDown)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jPanel10.add(jPanel15);

        jButtondown.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtondown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzdown.png"))); // NOI18N
        jButtondown.setToolTipText("云台下俯 ");
        jButtondown.setBorder(null);
        jButtondown.setBorderPainted(false);
        jButtondown.setContentAreaFilled(false);
        jButtondown.setRequestFocusEnabled(false);
        jButtondown.setRolloverEnabled(false);
        jButtondown.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButtondown.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtondownMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtondownMouseReleased(evt);
            }
        });
        jPanel10.add(jButtondown);

        jPanel16.setForeground(new java.awt.Color(240, 240, 240));
        jPanel16.setOpaque(false);

        jButtonRightDown.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonRightDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzrightdown.png"))); // NOI18N
        jButtonRightDown.setToolTipText("云台下俯和右转");
        jButtonRightDown.setBorder(null);
        jButtonRightDown.setBorderPainted(false);
        jButtonRightDown.setContentAreaFilled(false);
        jButtonRightDown.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonRightDown.setRequestFocusEnabled(false);
        jButtonRightDown.setRolloverEnabled(false);
        jButtonRightDown.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButtonRightDown.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonRightDownMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonRightDownMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jButtonRightDown)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jButtonRightDown)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jPanel10.add(jPanel16);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButtonZoomIn.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzzoomin.png"))); // NOI18N
        jButtonZoomIn.setToolTipText("焦距变大(倍率变大)");
        jButtonZoomIn.setBorder(null);
        jButtonZoomIn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonZoomInMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonZoomInMouseReleased(evt);
            }
        });

        jLabelMagnification.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabelMagnification.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMagnification.setText("倍率");

        jButtonZoomOut.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzzoomout.png"))); // NOI18N
        jButtonZoomOut.setToolTipText("焦距变小(倍率变小)");
        jButtonZoomOut.setBorder(null);
        jButtonZoomOut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonZoomOutMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonZoomOutMouseReleased(evt);
            }
        });

        jButtonFocusFar.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonFocusFar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzfocusfar.png"))); // NOI18N
        jButtonFocusFar.setToolTipText("焦点后调");
        jButtonFocusFar.setBorder(null);
        jButtonFocusFar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonFocusFarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonFocusFarMouseReleased(evt);
            }
        });

        jLabelFocus.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabelFocus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFocus.setText("聚焦");

        jButtonFocusNear.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonFocusNear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzfocusnear.png"))); // NOI18N
        jButtonFocusNear.setToolTipText("焦点前调");
        jButtonFocusNear.setBorder(null);
        jButtonFocusNear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonFocusNearMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonFocusNearMouseReleased(evt);
            }
        });

        jButtonIrisOpen.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonIrisOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzirisopen.png"))); // NOI18N
        jButtonIrisOpen.setToolTipText("光圈扩大");
        jButtonIrisOpen.setBorder(null);
        jButtonIrisOpen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonIrisOpenMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonIrisOpenMouseReleased(evt);
            }
        });

        jLabelAperture.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabelAperture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAperture.setText("光圈");

        jButtonIrisClose.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonIrisClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzirisclose.png"))); // NOI18N
        jButtonIrisClose.setToolTipText("光圈缩小");
        jButtonIrisClose.setBorder(null);
        jButtonIrisClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonIrisCloseMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonIrisCloseMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonIrisOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelAperture, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonFocusNear, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelFocus, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelMagnification, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addComponent(jButtonZoomOut))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addGap(2, 2, 2)
                            .addComponent(jButtonFocusFar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jButtonIrisClose, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMagnification, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonZoomOut, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonFocusNear, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFocus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonFocusFar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonIrisOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelAperture, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonIrisClose, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/minus2.png"))); // NOI18N

        jSliderSpeed.setMajorTickSpacing(1);
        jSliderSpeed.setMaximum(7);
        jSliderSpeed.setMinimum(1);
        jSliderSpeed.setToolTipText("云台控制的速度");
        jSliderSpeed.setValue(4);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/plus2.png"))); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSliderSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSliderSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jButtonHeater.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonHeater.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/heateroff.png"))); // NOI18N
        jButtonHeater.setToolTipText("加热");
        jButtonHeater.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHeaterActionPerformed(evt);
            }
        });

        jButtonLight.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonLight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/lightoff.png"))); // NOI18N
        jButtonLight.setToolTipText("开启灯光");
        jButtonLight.setBorder(null);
        jButtonLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLightActionPerformed(evt);
            }
        });

        jButtonWiperPwron.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonWiperPwron.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/wiperoff.png"))); // NOI18N
        jButtonWiperPwron.setToolTipText("开启雨刷");
        jButtonWiperPwron.setBorder(null);
        jButtonWiperPwron.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonWiperPwronActionPerformed(evt);
            }
        });

        jButtonAux1.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonAux1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/auxiliary.png"))); // NOI18N
        jButtonAux1.setToolTipText("开启辅助设备开关");
        jButtonAux1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAux1ActionPerformed(evt);
            }
        });

        jButtonAux2.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonAux2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/auxiliary2.png"))); // NOI18N
        jButtonAux2.setToolTipText("开启辅助设备2开关");
        jButtonAux2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAux2ActionPerformed(evt);
            }
        });

        jButtonFanPwron.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonFanPwron.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/fanoff.png"))); // NOI18N
        jButtonFanPwron.setToolTipText("开启风扇");
        jButtonFanPwron.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFanPwronActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonLight, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonHeater, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButtonAux1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonAux2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButtonWiperPwron)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonFanPwron, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAux1, jButtonAux2, jButtonFanPwron, jButtonHeater, jButtonLight, jButtonWiperPwron});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButtonLight, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonWiperPwron, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonFanPwron, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAux1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonHeater, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAux2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAux1, jButtonAux2, jButtonFanPwron, jButtonHeater, jButtonLight, jButtonWiperPwron});

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12)))
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPanePTZ.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPanePTZ.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jPanelPreset.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelPreset.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 15)); // NOI18N
        jPanelPreset.setLayout(new java.awt.BorderLayout());

        jPanel18.setMinimumSize(new java.awt.Dimension(107, 30));
        jPanel18.setPreferredSize(new java.awt.Dimension(107, 30));
        jPanel18.setLayout(new java.awt.GridLayout(1, 0));

        jButtonGotoPreset.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonGotoPreset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzcall.png"))); // NOI18N
        jButtonGotoPreset.setToolTipText("调用预置点");
        jButtonGotoPreset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGotoPresetActionPerformed(evt);
            }
        });
        jPanel18.add(jButtonGotoPreset);

        jButtonSetPreset.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonSetPreset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/setup.png"))); // NOI18N
        jButtonSetPreset.setToolTipText("设置预置点");
        jButtonSetPreset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetPresetActionPerformed(evt);
            }
        });
        jPanel18.add(jButtonSetPreset);

        jButtonDeletePreset.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonDeletePreset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/delete.png"))); // NOI18N
        jButtonDeletePreset.setToolTipText("删除预置点设置");
        jButtonDeletePreset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeletePresetActionPerformed(evt);
            }
        });
        jPanel18.add(jButtonDeletePreset);

        jButtonPTZPark.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzpark.png"))); // NOI18N
        jButtonPTZPark.setToolTipText("设置默认守望位置");
        jButtonPTZPark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPTZParkActionPerformed(evt);
            }
        });
        jPanel18.add(jButtonPTZPark);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addGap(0, 80, Short.MAX_VALUE)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanelPreset.add(jPanel20, java.awt.BorderLayout.PAGE_START);

        jTablePreset.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14)); // NOI18N
        jTablePreset.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTablePreset.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTablePresetMousePressed(evt);
            }
        });
        jScrollPanePreset.setViewportView(jTablePreset);

        jPanelPreset.add(jScrollPanePreset, java.awt.BorderLayout.CENTER);

        jTabbedPanePTZ.addTab("预置点", jPanelPreset);

        jPanelCruise.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelCruise.setLayout(new java.awt.BorderLayout());

        jPanel13.setMinimumSize(new java.awt.Dimension(110, 30));
        jPanel13.setPreferredSize(new java.awt.Dimension(202, 30));
        jPanel13.setLayout(new java.awt.BorderLayout());

        jComboBoxCruise.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jComboBoxCruise.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCruiseItemStateChanged(evt);
            }
        });
        jPanel13.add(jComboBoxCruise, java.awt.BorderLayout.CENTER);

        jPanel17.setMinimumSize(new java.awt.Dimension(87, 25));
        jPanel17.setPreferredSize(new java.awt.Dimension(87, 75));
        jPanel17.setLayout(new java.awt.GridLayout(1, 0));

        jButtonRunSeq.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonRunSeq.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzcall.png"))); // NOI18N
        jButtonRunSeq.setToolTipText("调用巡航路径");
        jButtonRunSeq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunSeqActionPerformed(evt);
            }
        });
        jPanel17.add(jButtonRunSeq);

        jButtonAddPoint.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonAddPoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/add.png"))); // NOI18N
        jButtonAddPoint.setToolTipText("添加巡航点");
        jButtonAddPoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddPointActionPerformed(evt);
            }
        });
        jPanel17.add(jButtonAddPoint);

        jButtonDelPoint.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonDelPoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/delete.png"))); // NOI18N
        jButtonDelPoint.setToolTipText("删除巡航点");
        jButtonDelPoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelPointActionPerformed(evt);
            }
        });
        jPanel17.add(jButtonDelPoint);

        jPanel13.add(jPanel17, java.awt.BorderLayout.EAST);

        jPanelCruise.add(jPanel13, java.awt.BorderLayout.PAGE_START);

        jTableCruise.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14)); // NOI18N
        jTableCruise.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableCruise.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCruiseMouseClicked(evt);
            }
        });
        jScrollPaneCruise.setViewportView(jTableCruise);

        jPanelCruise.add(jScrollPaneCruise, java.awt.BorderLayout.CENTER);

        jTabbedPanePTZ.addTab("巡航", jPanelCruise);

        jPanelTrack.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelTrack.setLayout(new java.awt.BorderLayout());

        jComboBoxTrack.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jComboBoxTrack.setToolTipText("");

        jPanel19.setLayout(new java.awt.GridLayout(1, 0));

        jButtonMemCruise.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonMemCruise.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cruiserecord.png"))); // NOI18N
        jButtonMemCruise.setToolTipText("开始记录云台轨迹");
        jButtonMemCruise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMemCruiseActionPerformed(evt);
            }
        });
        jPanel19.add(jButtonMemCruise);

        jButtonRunCruise.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonRunCruise.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptzcall.png"))); // NOI18N
        jButtonRunCruise.setToolTipText("开始运行云台轨迹");
        jButtonRunCruise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunCruiseActionPerformed(evt);
            }
        });
        jPanel19.add(jButtonRunCruise);

        jButtonDeleteCruise.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonDeleteCruise.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/delete.png"))); // NOI18N
        jButtonDeleteCruise.setToolTipText("删除单条云台轨迹");
        jButtonDeleteCruise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteCruiseActionPerformed(evt);
            }
        });
        jPanel19.add(jButtonDeleteCruise);

        jButtonDeleteAllCruise.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonDeleteAllCruise.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/clear.png"))); // NOI18N
        jButtonDeleteAllCruise.setToolTipText("删除所有云台轨迹");
        jButtonDeleteAllCruise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteAllCruiseActionPerformed(evt);
            }
        });
        jPanel19.add(jButtonDeleteAllCruise);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxTrack, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(45, 45, 45))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jComboBoxTrack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelTrack.add(jPanel21, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 187, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        jPanelTrack.add(jPanel22, java.awt.BorderLayout.CENTER);

        jTabbedPanePTZ.addTab("轨迹", jPanelTrack);

        jLabelPTZCtrl.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelPTZCtrl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPTZCtrl.setText("云台控制");

        javax.swing.GroupLayout jPanelPTZLayout = new javax.swing.GroupLayout(jPanelPTZ);
        jPanelPTZ.setLayout(jPanelPTZLayout);
        jPanelPTZLayout.setHorizontalGroup(
            jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPTZLayout.createSequentialGroup()
                .addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTabbedPanePTZ, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabelPTZCtrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelPTZLayout.setVerticalGroup(
            jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPTZLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPTZCtrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTabbedPanePTZ, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanelEastLayout = new javax.swing.GroupLayout(jPanelEast);
        jPanelEast.setLayout(jPanelEastLayout);
        jPanelEastLayout.setHorizontalGroup(
            jPanelEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanelEastLayout.createSequentialGroup()
                .addComponent(jPanelPTZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelEastLayout.setVerticalGroup(
            jPanelEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEastLayout.createSequentialGroup()
                .addComponent(jPanelPTZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 310, Short.MAX_VALUE))
        );

        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    //设备资源树双击操作，选中组名，则该组资源全部预览；选中监控点，则该监控点预览
    private void jTreeResourceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeResourceMouseClicked
        // TODO add your handling code here:
        try{
            if (evt.getClickCount() == 2){//˫��
                
                //下面两个函数不能用，因为没有展开的节点不计算行数
                // int row = jTreeResource.getMinSelectionRow();
                //int row2 = jTreeResource.getLeadSelectionRow();
                TreePath tp = jTreeResource.getSelectionPath();//获取选中节点的路径
                
                if (tp == null) return;
                
                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                DefaultMutableTreeNode SelectedNode = (DefaultMutableTreeNode)tp.getLastPathComponent();

                DefaultMutableTreeNode SelectedNode2 = (DefaultMutableTreeNode)tp.getParentPath().getLastPathComponent();

                DefaultMutableTreeNode SelectedNode3 = null;
                
                try{ 
                    SelectedNode3 = (DefaultMutableTreeNode)tp.getParentPath().getParentPath().getLastPathComponent();
                    //如果此时没有抛出NullPointerException，则说明是3级节点，即监控点。说明双击在监控点上
                    //refreshOneChannelRealPlayer刷新单个要预览的通道预览信息列表。返回ChannelRealPlayer单个要预览的通道预览信息
                    //previewOneChannel预览“单个要预览的通道预览信息”
                    String currentName = currentJPanel.getName();
                    int iCurrentJPanel = Integer.parseInt(currentName.substring(6));
//                    if (listIfPreview.get(iCurrentJPanel) > -1) {
//                        //JOptionPane.showMessageDialog(this, "此窗口已经被使用，请重新选择一个窗口进行预览！");
//                        CommonParas.showMessage(this.getRootPane(), "此窗口已经被使用，请重新选择一个窗口进行预览！", sFileName);
//                        return;
//                    }

                    //判断有无权限
                    String NodeName = SelectedNode.toString();
                    sGroupName = SelectedNode2.toString();
                    String AnotherName = NodeName.substring(0, NodeName.indexOf("_"));

                    
                    //判断权限
                    if (!CommonParas.showNoAuthorityMessage(rootPane, CommonParas.AuthorityItems.AUTHORITY_PREVIEW,  AnotherName, sFileName)){
                        return;
                    }
                    //结束当前窗口预览，返回值为-1，表示当前窗口不存在预览对象
                    int StopSucc = iCurrentJPanel;//表示成功
                    if (listIfPreview.get(iCurrentJPanel) > -1){
                        StopSucc = stopPreviewOneChannel(iCurrentJPanel);
                        if (StopSucc < 1) return;//如果停止预览失败，返回
                    }
                    
                    ChannelRealPlayer ChannelRealPlayer2 = refreshOneChannelRealPlayer(SelectedNode,iCurrentJPanel);
                    if (ChannelRealPlayer2 == null) return;
                    int ReturnCode = CommonParas.previewOneChannel(ChannelRealPlayer2, sFileName);
                    if (ReturnCode > 0){
                        //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
                        writePreviewLog(ChannelRealPlayer2,sStartPreview);//"开始预览");
                        listIfPreview.set(iCurrentJPanel, iCurrentJPanel);//设置窗口矩阵第i号窗口已经被预览
//                        if (StopIndexoflistChannelRealPlayer > -1)//本窗口原先存在预览
//                            listIfPreview.set(iCurrentJPanel, StopIndexoflistChannelRealPlayer);//设置窗口矩阵第i号窗口已经被预览
//                        else//本窗口原先不存在预览
//                            listIfPreview.set(iCurrentJPanel, listChannelRealPlayer.size()-1);//设置窗口矩阵第i号窗口已经被预览
                        
                        currentChannelRealPlayer = getCurrentChannelRealPlayer();
                    }
                    
                }catch (NullPointerException e)//如果此时抛出NullPointerException，则说明是2级节点，即组名节点，则说明双击在组名上
                {
                    sGroupName = SelectedNode.toString();
                    
                    stopPreviewAllChannels();
                    refreshChannelRealPlayerList(SelectedNode);//刷新要预览的通道预览信息列表
                    previewAllChannels();
                    showPTZWindow(false);//隐藏云台控制窗口
                }

            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jTreeResourceMouseClicked()","系统在设备资源树被双击操作过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jTreeResourceMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        if (getIfExistPreview()){
            stopPreviewAllChannels();
        }
        currentTimer.cancel();
        this.dispose();
        for (JFrame ArrayFrame1 : CommonParas.g_ListJFrame) {
            if (ArrayFrame1 instanceof  JFrameTotalPreview){
                CommonParas.g_ListJFrame.remove(ArrayFrame1);
                return;
            }
        }
        
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPreviewActionPerformed
        // TODO add your handling code here:
        String currentName = currentJPanel.getName();
        int iCurrentJPanel = Integer.parseInt(currentName.substring(6));
        stopPreviewOneChannel(iCurrentJPanel);

    }//GEN-LAST:event_jMenuItemPreviewActionPerformed

    private void jMenuItemCaptureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCaptureActionPerformed
        // TODO add your handling code here:
        if (currentChannelRealPlayer == null) return;
       currentChannelRealPlayer.captureOnePanelPicture();

    }//GEN-LAST:event_jMenuItemCaptureActionPerformed

    private void jMenuItemRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecordActionPerformed
        // TODO add your handling code here:
        if (currentChannelRealPlayer == null) return;
        currentChannelRealPlayer.saveOnePanelRecord();
 
    }//GEN-LAST:event_jMenuItemRecordActionPerformed

    private void jMenuItemPTZCtrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPTZCtrlActionPerformed
        // TODO add your handling code here:
        if (currentChannelRealPlayer == null) return;
        int Index = currentChannelRealPlayer.getIndexOflistDevice();
        if (!HCNetSDKExpand.isHavePTZAbility(CommonParas.getStrDeviceInfo(Index, sFileName),
                CommonParas.getStrIpparacfg(Index, sFileName),currentChannelRealPlayer.getChannel().intValue(),sFileName)) return;//如果无云台，则不做任何操作
        //判断有无权限
        if (!CommonParas.showNoAuthorityMessage(rootPane, CommonParas.AuthorityItems.AUTHORITY_PTZCTRL,  
                currentChannelRealPlayer.getDeviceparaBean().getAnothername(), sFileName)) {
            return;
        } 
        if (currentChannelRealPlayer.isIfPTZCtrl()){
            currentChannelRealPlayer.setIfPTZCtrl(false);
        }
        else{
            currentChannelRealPlayer.setIfPTZCtrl(true);
        }
        
    }//GEN-LAST:event_jMenuItemPTZCtrlActionPerformed

    private void jMenuItemSetupFishBallTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSetupFishBallTrackActionPerformed
        // TODO add your handling code here:
        try{

            if (currentChannelRealPlayer == null) return;
            //判断是否有鱼球联动的权限
            
            int IndexOflistDeviceLoginUserID = currentChannelRealPlayer.getIndexOflistDevice();
//            ArrayList AList = (ArrayList)listDeviceLoginUserID.get(IndexOflistDeviceLoginUserID);
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo = CommonParas.getStrDeviceInfo(IndexOflistDeviceLoginUserID, sFileName);
            HCNetSDK.NET_DVR_IPPARACFG strIpparaCfg = CommonParas.getStrIpparacfg(IndexOflistDeviceLoginUserID, sFileName);
//            int DVRModelCode = (Integer)AList.get(2);
////            int DVRModelCode = (Integer)((ArrayList)listDeviceLoginUserID.get(IndexOflistDeviceLoginUserID)).get(2);
//            if (DVRModelCode != HCNetSDK.IPCAM_FISHEYE) return;//�
            ChannelRealPlayer FishEyeRealPlayer;
            if (HCNetSDKExpand.isFishEye(StrDevInfo.wDevType)){
                FishEyeRealPlayer = currentChannelRealPlayer.clone();
            }else {
                FishEyeRealPlayer = getFishEyeChannelRealPlayer(StrDevInfo, strIpparaCfg, currentChannelRealPlayer.getChannel().intValue());
            }
            if (FishEyeRealPlayer == null) return;
            
//            if (!HCNetSDKExpand.isFishEye(StrDevInfo,strIpparaCfg,currentChannelRealPlayer.getChannel().intValue(),sFileName)) return;
//            ChannelRealPlayer FishEyeRealPlayer = currentChannelRealPlayer.clone();
            //ChannelRealPlayer BallRealPlayer = getBallChannelRealPlayer();
            
            JFrameFishEyeTrackSetup FishEyeTrackSetup = new JFrameFishEyeTrackSetup(FishEyeRealPlayer);
            FishEyeTrackSetup.setVisible(true);
            //写日志
            writePreviewLog(currentChannelRealPlayer,sStartFishEyeTrackSetup);//"开始设置鱼球联动");
         
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jMenuItemSetupFishBallTrackActionPerformed()","系统在联动球机过程中出现错误："
                             + "\r\n                       Exception:" + e.toString());
        }
    }//GEN-LAST:event_jMenuItemSetupFishBallTrackActionPerformed

    private void jMenuItemFishBallTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFishBallTrackActionPerformed
        // TODO add your handling code here:
        //得到当前窗口是否存在手工设置鱼球联动的情况

        if (currentChannelRealPlayer == null) return;

        if (currentChannelRealPlayer.isIfFishBallTrack()){
            currentChannelRealPlayer.setIfFishBallTrack(false);//设置停止鱼球联动
        }else{
            currentChannelRealPlayer.setIfFishBallTrack(true);//设置开始启动鱼球联动
        }
        
    }//GEN-LAST:event_jMenuItemFishBallTrackActionPerformed

    private void jMenuItemOpenVoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenVoiceActionPerformed
        // TODO add your handling code here:
        if (currentChannelRealPlayer == null) return;
        currentChannelRealPlayer.setPreviewVoice();

    }//GEN-LAST:event_jMenuItemOpenVoiceActionPerformed

    private void jMenuItemCorrectTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCorrectTimeActionPerformed
        // TODO add your handling code here:
        if (currentChannelRealPlayer == null) return;
        //判断有无权限
        if (!CommonParas.showNoAuthorityMessage(rootPane, CommonParas.AuthorityItems.AUTHORITY_CHECKTIME,  
                currentChannelRealPlayer.getDeviceparaBean().getAnothername(), sFileName)) {
            return;
        } 
        currentChannelRealPlayer.saveOneDevCheckTime();
 
    }//GEN-LAST:event_jMenuItemCorrectTimeActionPerformed

    private void jMenuItemFullScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFullScreenActionPerformed
        // TODO add your handling code here:
        fullScreenWindow();
    }//GEN-LAST:event_jMenuItemFullScreenActionPerformed

    private void jMenuItemLockScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLockScreenActionPerformed
        // TODO add your handling code here:
        //evt.getSource()
        //java.awt.Window CurrentWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();//获取当前窗口
        JDialogLockScreen lock = new JDialogLockScreen(null, SystemLockScreen, true);//"系统锁屏"
        lock.setVisible(true);
       
    }//GEN-LAST:event_jMenuItemLockScreenActionPerformed

    private void jMenuItemPTZWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPTZWindowActionPerformed
        // TODO add your handling code here:
        
        try{
            if (currentChannelRealPlayer == null) return;
            //判断有无权限
            if (!CommonParas.showNoAuthorityMessage(rootPane, CommonParas.AuthorityItems.AUTHORITY_PTZCTRL,  
                    currentChannelRealPlayer.getDeviceparaBean().getAnothername(), sFileName)) {
                return;
            } 
            //写日志
            writePreviewLog(currentChannelRealPlayer, sShowPTZWindow);//"打开云台控制窗口");
            showPTZWindow(true);

//            DialogPTZControl = new JDialogPTZControl(this, false, currentChannelRealPlayer);
//            DialogPTZControl.setVisible(true);
            
            
        }catch (Exception | Error e){
            TxtLogger.append(this.sFileName, "jMenuItemPTZWindowActionPerformed()","系统在在打开云台控制窗口过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }//GEN-LAST:event_jMenuItemPTZWindowActionPerformed

    private void jMenuItemTalkbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTalkbackActionPerformed
        // TODO add your handling code here:
        if (currentChannelRealPlayer == null) return;
        //判断有无权限
        if (!CommonParas.showNoAuthorityMessage(rootPane, CommonParas.AuthorityItems.AUTHORITY_SPEAK,  
                currentChannelRealPlayer.getDeviceparaBean().getAnothername(), sFileName)) {
        } 

    }//GEN-LAST:event_jMenuItemTalkbackActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        CommonParas.g_RootPane = rootPane;
    }//GEN-LAST:event_formWindowActivated

    private void jButtonDeletePresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeletePresetActionPerformed
        //int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
        int iPreset = jTablePreset.getSelectedRow()+ 1;
        String PresetName = (String)presetTableModel.getValueAt(iPreset - 1, 1);

        if (CommonParas.hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.CLE_PRESET, iPreset))
        {
            writePTZControlLog(sDelPreset+PresetName);//"删除预置点："
            CommonParas.showMessage(sDelPresetSucc+PresetName, sFileName);//"成功删除预置点："
            //CommonParas.showMessage( "删除预置点"+PresetName+"成功", sFileName);

        }else {
            writePTZControlErrorLog(sDelPreset+PresetName);//"删除预置点："
        }
    }//GEN-LAST:event_jButtonDeletePresetActionPerformed

    private void jButtonSetPresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetPresetActionPerformed
        //int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
        int iPreset = jTablePreset.getSelectedRow()+ 1;
        String PresetName = (String)presetTableModel.getValueAt(iPreset - 1, 1);
        if (CommonParas.hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.SET_PRESET, iPreset))
        {
            writePTZControlLog(sSetPreset+PresetName);//"设置预置点："
            CommonParas.showMessage(sSetPresetSucc+PresetName, sFileName);//"成功设置预置点："
            //CommonParas.showMessage(  "设置预置点"+PresetName+"成功", sFileName);
        }else {
            writePTZControlErrorLog(sSetPreset+PresetName);//"设置预置点："
        }
    }//GEN-LAST:event_jButtonSetPresetActionPerformed

    private void jTablePresetMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablePresetMousePressed
        // TODO add your handling code here:

        if (jTablePreset.getSelectedColumn() == colOfPresetName){
            int Row = jTablePreset.getSelectedRow();
            oldPresetName = (String)jTablePreset.getValueAt(Row, colOfPresetName);
            jTablePreset.editCellAt(Row, colOfPresetName);
        }
    }//GEN-LAST:event_jTablePresetMousePressed

    private void jButtonGotoPresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGotoPresetActionPerformed
        // TODO add your handling code here:
        //int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
        int iPreset = jTablePreset.getSelectedRow()+ 1;
        String PresetName = (String)presetTableModel.getValueAt(iPreset - 1, 1);
        if (CommonParas.hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.GOTO_PRESET, iPreset)){
            writePTZControlLog(sGotoPreset+PresetName);//"调用预置点："
        }else{
            writePTZControlErrorLog(sGotoPreset+PresetName);//"调用预置点："
        }
    }//GEN-LAST:event_jButtonGotoPresetActionPerformed

    private void jComboBoxCruiseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCruiseItemStateChanged
        // TODO add your handling code here:

        if(evt.getStateChange() == ItemEvent.SELECTED){
            if (bAutoComboBoxSelect) return;//是否系统读取云台参数过程中自动选择JComboBox，因为编码选择也是触发了ItemStateChanged事件
            int CruiseRoute = jComboBoxCruise.getSelectedIndex() + 1;
            refreshCruiseTableData(CruiseRoute);
        }

    }//GEN-LAST:event_jComboBoxCruiseItemStateChanged

    private void jButtonRunSeqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunSeqActionPerformed
        // TODO add your handling code here:
        byte iSeq = (byte) (jComboBoxCruise.getSelectedIndex() + 1);
        if (iSeq < 1) return;
        if (!m_bIsOnCruise)
        {
            if (CommonParas.hCNetSDK.NET_DVR_PTZCruise(m_lRealHandle, HCNetSDK.RUN_SEQ, iSeq, (byte) 0, (short) 0)){

                m_bIsOnCruise = true;
                jButtonRunSeq.setToolTipText(sStopRunCruise);//"停止调用巡航路径"
                jButtonRunSeq.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ptzstop.png"));

                modifyCruiseJButtonState(jButtonRunSeq);
            }else{
                writePTZControlErrorLog(MessageFormat.format(sRunCruiseSpecific, iSeq));//"调用第["+iSeq+"]号巡航路径");
                //JOptionPane.showMessageDialog(this, "调用巡航失败");
            }
        }else{
            if (CommonParas.hCNetSDK.NET_DVR_PTZCruise(m_lRealHandle, HCNetSDK.STOP_SEQ, iSeq, (byte) 0, (short) 0)) {
                writePTZControlLog(MessageFormat.format(sStopRunCruiseSpecific, iSeq));//"停止调用第["+iSeq+"]号巡航路径");
                CommonParas.showMessage(MessageFormat.format(sStopRunCruiseSpecific, iSeq), sFileName);//"停止调用第["+iSeq+"]号巡航路径"
                m_bIsOnCruise = false;
                jButtonRunSeq.setToolTipText(sRunCruise);//"调用巡航路径"
                jButtonRunSeq.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ptzcall.png"));

                modifyCruiseJButtonState(null);
            }else{
                writePTZControlErrorLog(MessageFormat.format(sStopRunCruiseSpecific, iSeq));//"停止调用第["+iSeq+"]号巡航路径");
                //JOptionPane.showMessageDialog(this, "停止巡航失败");
            }
        }
    }//GEN-LAST:event_jButtonRunSeqActionPerformed

    private void jButtonAddPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddPointActionPerformed
        // TODO add your handling code here:
        iAddOrUpdate = 1;//增加
        refreshCruisePointData();
        //jDialogCruisePoint.setTitle("添加巡航点");
        jLabelTitle.setText(sAddCruisePoint);//"添加巡航点"
        //jLabelCruisePoint.setText("添加巡航点");
        //设置窗口显示位置
        //CommonParas.setAppropriateLocation3(jDialogCruisePoint, jButtonAddPoint);
        CommonParas.centerWindow(jDialogCruisePoint);
        jDialogCruisePoint.setVisible(true);
    }//GEN-LAST:event_jButtonAddPointActionPerformed

    private void jButtonDelPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelPointActionPerformed
        // TODO add your handling code here:
        int RowOfCruisePointTable = jTableCruise.getSelectedRow();
        int CruisePointNO = (int)cruisePointTableModel.getValueAt(RowOfCruisePointTable, 0);//巡航点号//该巡航路径下的巡航点号（待修改）
        String CruisePointName = (String)cruisePointTableModel.getValueAt(RowOfCruisePointTable, 1);//巡航点号//该巡航路径下的巡航点号（待修改）
        int CruiseRoute = jComboBoxCruise.getSelectedIndex() + 1;//巡航路径号
        boolean DelSuss = delCruisePoint( CruiseRoute, CruisePointNO);
        if (DelSuss) {
            writePTZControlLog(MessageFormat.format(sDelCruisePoint, CruisePointNO) + CruisePointName);//"删除第["+CruisePointNO+"]号巡航点："
            CommonParas.showMessage(MessageFormat.format(sDelCruisePointSucc, CruisePointNO) + CruisePointName, sFileName);//"成功删除第["+CruisePointNO+"]号巡航点："
            refreshCruiseTableData(CruiseRoute);
        }else{
            writePTZControlErrorLog(MessageFormat.format(sDelCruisePoint, CruisePointNO) + CruisePointName);//"删除第["+CruisePointNO+"]号巡航点："
        }
    }//GEN-LAST:event_jButtonDelPointActionPerformed

    private void jTableCruiseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCruiseMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2){//双击˫
            iAddOrUpdate = 2;//修改巡航点
            refreshCruisePointData();
            int Row = jTableCruise.getSelectedRow();
            //int PresetNO = getPresetNum((String)cruisePointTableModel.getValueAt(Row - 1, 1));//根据预置点名称得到对应的预置点号
            int CruisePointNO = (int)cruisePointTableModel.getValueAt(Row, 0);//巡航点号//该巡航路径下的巡航点号（待修改）
            if (CruisePointNO > 0){
                String PresetName = (String)cruisePointTableModel.getValueAt(Row, 1);
                String Value2 = (String)cruisePointTableModel.getValueAt(Row, 2);
                String[] DwellSpeed = Value2.split("s  ");//[0]巡航时间[1]巡航速度
                //                jComboBoxCruisePoint.removeAllItems();//删除所有的巡航点
                //                jComboBoxCruisePoint.setEnabled(false);
                jComboBoxPreset.setSelectedItem(PresetName);
                jComboBoxDwell.setSelectedIndex(Integer.parseInt(DwellSpeed[0]) - 1);
                jComboBoxCruiseSpeed.setSelectedIndex(Integer.parseInt(DwellSpeed[1]) - 1);
                //jDialogCruisePoint.setTitle("编辑巡航点");
                jLabelTitle.setText(sEditCruisePoint);//"编辑巡航点");
                //设置窗口显示位置
                //CommonParas.setAppropriateLocation3(jDialogCruisePoint, evt);
                CommonParas.centerWindow(jDialogCruisePoint);
                jDialogCruisePoint.setVisible(true);
            }
        }
    }//GEN-LAST:event_jTableCruiseMouseClicked

    private void jButtonMemCruiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMemCruiseActionPerformed
        int PatternID = jComboBoxTrack.getSelectedIndex() + 1;
        if (PatternID < 1 ) return;

        if (!m_bMemCruise){
            int Control = PTZPatternControl( HCNetSDK.STA_MEM_CRUISE);
            String Msg = MessageFormat.format(sStartMemTrackSpecific, PatternID);//"开始记录第["+PatternID+"]条云台轨迹";
            if (Control > 0){
                writePTZControlLog(Msg);
                CommonParas.showMessage(Msg, sFileName);
                //jButtonMemCruise.setText("停止记录");
                jButtonMemCruise.setToolTipText(sStopMemTrack);//"停止记录云台轨迹"
                jButtonMemCruise.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ptzstop.png"));
                m_bMemCruise = true;
                modifyTrackJButtonState(jButtonMemCruise);
            }else{
                writePTZControlErrorLog(Msg);
            }
        }else{
            int Control = PTZPatternControl( HCNetSDK.STO_MEM_CRUISE);
            String Msg = MessageFormat.format(stopMemTrackSpecific, PatternID);//"停止记录第["+PatternID+"]条云台轨迹";
            if (Control > 0){
                writePTZControlLog(Msg);
                CommonParas.showMessage(Msg, sFileName);
                //jButtonMemCruise.setText("开始记录");
                jButtonMemCruise.setToolTipText(sStopMemTrack);//"开始记录云台轨迹"
                jButtonMemCruise.setIcon(ImageIconBufferPool.getInstance().getImageIcon("cruiserecord.png"));
                m_bMemCruise = false;
                modifyTrackJButtonState(null);
            }else{
                writePTZControlErrorLog(Msg);
            }
        }

    }//GEN-LAST:event_jButtonMemCruiseActionPerformed

    private void jButtonRunCruiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunCruiseActionPerformed
        int PatternID = jComboBoxTrack.getSelectedIndex() + 1;
        if (PatternID < 1 ) return;

        if (!m_bRunCruise){
            int Control = PTZPatternControl( HCNetSDK.RUN_CRUISE);
            String Msg = MessageFormat.format(sStartRunTrackSpecific, PatternID);//"开始运行第["+PatternID+"]条云台轨迹";
            if (Control > 0){
                writePTZControlLog(Msg);
                CommonParas.showMessage(Msg, sFileName);
                //jButtonRunCruise.setText("停止运行");
                jButtonRunCruise.setToolTipText(sStopRunTrack);//"停止运行云台轨迹"
                jButtonRunCruise.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ptzstop.png"));
                m_bRunCruise = true;
                modifyTrackJButtonState(jButtonRunCruise);
            }else{
                writePTZControlErrorLog(Msg);
            }
        }else{
            int Control = PTZPatternControl( HCNetSDK.STOP_CRUISE);
            String Msg = MessageFormat.format(sStopRunTrackSpecific, PatternID);//"停止运行第["+PatternID+"]条云台轨迹";
            if (Control > 0){
                writePTZControlLog(Msg);
                CommonParas.showMessage(Msg, sFileName);
                //jButtonRunCruise.setText("开始运行");
                jButtonRunCruise.setToolTipText(sStartRunTrack);//"开始运行云台轨迹"
                jButtonRunCruise.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ptzcall.png"));
                m_bRunCruise = false;
                modifyTrackJButtonState(null);
            }else{
                writePTZControlErrorLog(Msg);
            }
        }

    }//GEN-LAST:event_jButtonRunCruiseActionPerformed

    private void jButtonDeleteCruiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteCruiseActionPerformed
        // TODO add your handling code here:
        int PatternID = jComboBoxTrack.getSelectedIndex() + 1;
        if (PatternID < 1 ) return;

        int Control = PTZPatternControl( HCNetSDK.DELETE_CRUISE);
        String Msg = MessageFormat.format(sDelTrackSpecific, PatternID);//"删除第["+PatternID+"]条云台轨迹";
        if (Control > 0){
            writePTZControlLog(Msg);
            CommonParas.showMessage(Msg, sFileName);
        }else{
            writePTZControlErrorLog(Msg);
        }

    }//GEN-LAST:event_jButtonDeleteCruiseActionPerformed

    private void jButtonDeleteAllCruiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteAllCruiseActionPerformed
        // TODO add your handling code here:
        if (jComboBoxTrack.getItemCount() < 1) return;
        int Control = PTZPatternControl( HCNetSDK.DELETE_ALL_CRUISE);
        String Msg = sDelTrackAll;//"删除所有云台轨迹";
        if (Control > 0){
            writePTZControlLog(Msg);
            CommonParas.showMessage(Msg, sFileName);
        }else{
            writePTZControlErrorLog(Msg);
        }

    }//GEN-LAST:event_jButtonDeleteAllCruiseActionPerformed

    private void jButtonLeftUpMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLeftUpMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.UP_LEFT, 0);
    }//GEN-LAST:event_jButtonLeftUpMousePressed

    private void jButtonLeftUpMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLeftUpMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.UP_LEFT, 1);
    }//GEN-LAST:event_jButtonLeftUpMouseReleased

    private void jButtonUpMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonUpMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.TILT_UP, 0);
    }//GEN-LAST:event_jButtonUpMousePressed

    private void jButtonUpMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonUpMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.TILT_UP, 1);
    }//GEN-LAST:event_jButtonUpMouseReleased

    private void jButtonRightUpMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRightUpMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.UP_RIGHT, 0);
    }//GEN-LAST:event_jButtonRightUpMousePressed

    private void jButtonRightUpMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRightUpMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.UP_RIGHT, 1);
    }//GEN-LAST:event_jButtonRightUpMouseReleased

    private void jButtonLeftMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLeftMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.PAN_LEFT, 0);
    }//GEN-LAST:event_jButtonLeftMousePressed

    private void jButtonLeftMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLeftMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.PAN_LEFT, 1);
    }//GEN-LAST:event_jButtonLeftMouseReleased

    private void jButtonAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAutoActionPerformed
        //int iSpeed = jComboBoxSpeed.getSelectedIndex();
        int iSpeed = jSliderSpeed.getValue();
        if (!m_bAutoOn){
            if (iSpeed >= 1)
            {
                CommonParas.hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_AUTO, 0, iSpeed);
            } else
            {
                CommonParas.hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.PAN_AUTO, 0);
            }
            //jButtonAuto.setText("停止");
            jButtonAuto.setContentAreaFilled(true);
            m_bAutoOn = true;
        } else{
            if (iSpeed >= 1)
            {
                CommonParas.hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_AUTO, 1, iSpeed);
            } else
            {
                CommonParas.hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.PAN_AUTO, 1);
            }
            //jButtonAuto.setText("自动");
            jButtonAuto.setContentAreaFilled(false);
            m_bAutoOn = false;
        }
    }//GEN-LAST:event_jButtonAutoActionPerformed

    private void jButtonRightMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRightMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.PAN_RIGHT, 0);
    }//GEN-LAST:event_jButtonRightMousePressed

    private void jButtonRightMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRightMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.PAN_RIGHT, 1);
    }//GEN-LAST:event_jButtonRightMouseReleased

    private void jButtonLeftDownMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLeftDownMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.DOWN_LEFT, 0);
    }//GEN-LAST:event_jButtonLeftDownMousePressed

    private void jButtonLeftDownMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLeftDownMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.DOWN_LEFT, 1);
    }//GEN-LAST:event_jButtonLeftDownMouseReleased

    private void jButtondownMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtondownMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.TILT_DOWN, 0);
    }//GEN-LAST:event_jButtondownMousePressed

    private void jButtondownMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtondownMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.TILT_DOWN, 1);
    }//GEN-LAST:event_jButtondownMouseReleased

    private void jButtonRightDownMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRightDownMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.DOWN_RIGHT, 0);
    }//GEN-LAST:event_jButtonRightDownMousePressed

    private void jButtonRightDownMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRightDownMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.DOWN_RIGHT, 1);
    }//GEN-LAST:event_jButtonRightDownMouseReleased

    private void jButtonZoomInMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonZoomInMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.ZOOM_IN, 0);
    }//GEN-LAST:event_jButtonZoomInMousePressed

    private void jButtonZoomInMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonZoomInMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.ZOOM_IN, 1);
    }//GEN-LAST:event_jButtonZoomInMouseReleased

    private void jButtonZoomOutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonZoomOutMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.ZOOM_OUT, 0);
    }//GEN-LAST:event_jButtonZoomOutMousePressed

    private void jButtonZoomOutMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonZoomOutMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.ZOOM_OUT, 1);
    }//GEN-LAST:event_jButtonZoomOutMouseReleased

    private void jButtonFocusNearMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFocusNearMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.FOCUS_NEAR, 0);
    }//GEN-LAST:event_jButtonFocusNearMousePressed

    private void jButtonFocusNearMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFocusNearMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.FOCUS_NEAR, 1);
    }//GEN-LAST:event_jButtonFocusNearMouseReleased

    private void jButtonFocusFarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFocusFarMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.FOCUS_FAR, 0);
    }//GEN-LAST:event_jButtonFocusFarMousePressed

    private void jButtonFocusFarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFocusFarMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.FOCUS_FAR, 1);
    }//GEN-LAST:event_jButtonFocusFarMouseReleased

    private void jButtonIrisOpenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonIrisOpenMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.IRIS_OPEN, 0);
    }//GEN-LAST:event_jButtonIrisOpenMousePressed

    private void jButtonIrisOpenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonIrisOpenMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.IRIS_OPEN, 1);
    }//GEN-LAST:event_jButtonIrisOpenMouseReleased

    private void jButtonIrisCloseMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonIrisCloseMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.IRIS_CLOSE, 0);
    }//GEN-LAST:event_jButtonIrisCloseMousePressed

    private void jButtonIrisCloseMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonIrisCloseMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.IRIS_CLOSE, 1);
    }//GEN-LAST:event_jButtonIrisCloseMouseReleased

    private void jButtonLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLightActionPerformed
        changeSwitchState(m_bLightOn,  HCNetSDK.LIGHT_PWRON,  jButtonLight,  "lighton.png",  "lightoff.png", sCloseLight , sOpenLight);//"关闭灯光""开启灯光"
    }//GEN-LAST:event_jButtonLightActionPerformed

    private void jButtonWiperPwronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWiperPwronActionPerformed
        changeSwitchState(m_bWiperOn,  HCNetSDK.WIPER_PWRON,  jButtonWiperPwron,  "wiperon.png",  "wiperoff.png", sCloseWiper , sOpenWiper);//"关闭雨刷""开启雨刷"
    }//GEN-LAST:event_jButtonWiperPwronActionPerformed

    private void jButtonAux1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAux1ActionPerformed
        changeSwitchState(m_bAuxOn1,  HCNetSDK.AUX_PWRON1,  jButtonAux1,  "auxiliarystop.png",  "auxiliary.png", sCloseAux , sOpeAux);//"关闭辅助设备开关""开启辅助设备开关"
    }//GEN-LAST:event_jButtonAux1ActionPerformed

    private void jButtonFanPwronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFanPwronActionPerformed

        changeSwitchState(m_bFanOn,  HCNetSDK.FAN_PWRON,  jButtonFanPwron,  "fanon.png",  "fanoff.png", sCloseFan , sOpenFan);//"关闭风扇""开启风扇"
    }//GEN-LAST:event_jButtonFanPwronActionPerformed

    private void jButtonHeaterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHeaterActionPerformed
        changeSwitchState(m_bHeaterOn,  HCNetSDK.HEATER_PWRON,  jButtonHeater,  "heateron.png",  "heateroff.png", sStopHeater , sHeater);//"停止加热""加热"
    }//GEN-LAST:event_jButtonHeaterActionPerformed

    private void jButtonAux2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAux2ActionPerformed
        changeSwitchState(m_bAuxOn2,  HCNetSDK.AUX_PWRON2,  jButtonAux2,  "auxiliarystop2.png",  "auxiliary2.png", sCloseAux2 , sOpenAux2);//"关闭辅助设备2开关""开启辅助设备2开关"
    }//GEN-LAST:event_jButtonAux2ActionPerformed

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        // TODO add your handling code here:
        boolean DelRet = true;
        boolean bRet = false;
        int CruisePointNO = -1;//cruisePointTableModel.getRowCount() + 1;//该巡航路径下的巡航点号（待添加）

        int CruiseRoute = jComboBoxCruise.getSelectedIndex() + 1;//巡航路径号
        int PresetNum = jComboBoxPreset.getSelectedIndex() + 1;//预置点号
        String PresetName = jComboBoxPreset.getSelectedItem().toString();
        int SeqDwell = jComboBoxDwell.getSelectedIndex() + 1;//巡航时间
        int SeqSpeed = jComboBoxCruiseSpeed.getSelectedIndex() + 1;//巡航速度
        if (iAddOrUpdate == 2){
            int RowOfCruisePointTable = jTableCruise.getSelectedRow();
            CruisePointNO = (int)cruisePointTableModel.getValueAt(RowOfCruisePointTable, 0);//巡航点号//该巡航路径下的巡航点号（待修改）
            DelRet = delCruisePoint( CruiseRoute, CruisePointNO);
        }else CruisePointNO = Integer.parseInt(jComboBoxCruisePoint.getSelectedItem().toString());

        if (DelRet)  bRet = addCruisePoint(CruiseRoute,  CruisePointNO,  PresetNum,  SeqDwell,  SeqSpeed);

        if (bRet){
            refreshCruiseTableData(CruiseRoute);
            if (iAddOrUpdate == 1){//添加预置点
                writePTZControlLog(MessageFormat.format(sAddCruisePointSpecific, CruisePointNO, PresetNum, PresetName));//"添加第["+CruisePointNO+"]号巡航点：" + "第["+PresetNum+"]号预置点["+ PresetName+"]");
                CommonParas.showMessage(MessageFormat.format(sAddCruisePointSpecific, CruisePointNO, PresetNum, PresetName), sFileName);//"成功添加第["+CruisePointNO+"]号巡航点：" + "第["+PresetNum+"]号预置点["+ PresetName+"]"
            }else{//修改预置点
                writePTZControlLog(MessageFormat.format(sModifyCruisePoint, CruisePointNO, PresetNum, PresetName));//"修改第["+CruisePointNO+"]号巡航点为：" + "第["+PresetNum+"]号预置点["+PresetName+"]");
                CommonParas.showMessage(MessageFormat.format(sModifyCruisePoint, CruisePointNO, PresetNum, PresetName), sFileName);//"修改第["+CruisePointNO+"]号巡航点为：" + "第["+PresetNum+"]号预置点["+PresetName+"]"
            }
        }
        jDialogCruisePoint.setVisible(false);
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        // TODO add your handling code here:
        jDialogCruisePoint.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        // TODO add your handling code here:
        if (getIfExistPreview()){
            if ((JOptionPane.showConfirmDialog(this, sMessageBoxExitPreview,
                    sMessageBoxRemind, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)) return;//"系统正在使用预览，退出就要停止预览。是否要继续？",  "提醒"
            stopPreviewAllChannels();
        }
        currentTimer.cancel();
        this.dispose();
        for (JFrame ArrayFrame1 : CommonParas.g_ListJFrame) {
            if (ArrayFrame1 instanceof  JFrameTotalPreview){
                CommonParas.g_ListJFrame.remove(ArrayFrame1);
                return;
            }
        }
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonReturnMainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnMainActionPerformed
        // TODO add your handling code here:
        CommonParas.jkms.setVisible(true);
        CommonParas.jkms.setExtendedState(JFrame.MAXIMIZED_BOTH); 
    }//GEN-LAST:event_jButtonReturnMainActionPerformed

    private void jButtonLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogOutActionPerformed
        // TODO add your handling code here:
        CommonParas.jkms.jMenuItemExit.doClick();
    }//GEN-LAST:event_jButtonLogOutActionPerformed

    private void jButtonPlayBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPlayBackActionPerformed
        // TODO add your handling code here:
        CommonParas.jkms.jMenuItemPlayBack.doClick();
    }//GEN-LAST:event_jButtonPlayBackActionPerformed

    private void jSplitPanePreviewPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSplitPanePreviewPropertyChange
        // TODO add your handling code here:LAST_DIVIDER_LOCATION_PROPERTY
        /*监听这两个属性的改变都能获得同样的效果：
            1、DIVIDER_LOCATION_PROPERTY 绑定 dividerLocation 属性
            2、LAST_DIVIDER_LOCATION_PROPERTY 绑定 lastLocation 属性
        */
//        if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {   //后来实践中发现  DIVIDER_LOCATION_PROPERTY还是会出现问题？
        if (evt.getPropertyName().equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {    
            //防止在全屏状态时出现该问题
            if (bZoomIn){
                //zoomInPreviewOneChannel();zoomInPreviewOneChannel();
                setCurrentJPanelMaxSize();//currentJPanel.setPreferredSize(new Dimension(jPanelPreviewContainer.getWidth() - 4*iPanelBoderThickness, jPanelPreviewContainer.getHeight()-4*iPanelBoderThickness));
            }
        } 
    }//GEN-LAST:event_jSplitPanePreviewPropertyChange

    private void jButtonView1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonView1ActionPerformed
        // TODO add your handling code here:
        refreshPrivewWindows(1,1);
    }//GEN-LAST:event_jButtonView1ActionPerformed

    private void jButtonView4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonView4ActionPerformed
        // TODO add your handling code here:
        refreshPrivewWindows(2,2);
    }//GEN-LAST:event_jButtonView4ActionPerformed

    private void jButtonView9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonView9ActionPerformed
        // TODO add your handling code here:
        refreshPrivewWindows(3,3);
    }//GEN-LAST:event_jButtonView9ActionPerformed

    private void jButtonView16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonView16ActionPerformed
        // TODO add your handling code here:
        refreshPrivewWindows(4,4);
    }//GEN-LAST:event_jButtonView16ActionPerformed

    private void jButtonView25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonView25ActionPerformed
        // TODO add your handling code here:
        refreshPrivewWindows(5, 5);
    }//GEN-LAST:event_jButtonView25ActionPerformed

    private void jButtonView36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonView36ActionPerformed
        // TODO add your handling code here:
        refreshPrivewWindows(6, 6);
    }//GEN-LAST:event_jButtonView36ActionPerformed

    private void jButtonView49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonView49ActionPerformed
        // TODO add your handling code here:
        refreshPrivewWindows(7, 7);
    }//GEN-LAST:event_jButtonView49ActionPerformed

    private void jButtonPTZShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPTZShowActionPerformed
        // TODO add your handling code here:
        showPTZWindow(!bOpenPTZ);
    }//GEN-LAST:event_jButtonPTZShowActionPerformed

    private void jButtonTreeShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTreeShowActionPerformed
        // TODO add your handling code here:
        showDVRResourceTree(!bTreeShow);

    }//GEN-LAST:event_jButtonTreeShowActionPerformed

    private void jButtonView64ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonView64ActionPerformed
        // TODO add your handling code here:
        refreshPrivewWindows(8, 8);
    }//GEN-LAST:event_jButtonView64ActionPerformed

    private void jPopupMenuPreviewPopupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenuPreviewPopupMenuCanceled
        // TODO add your handling code here:
        jPopupMenuPreview.updateUI();
    }//GEN-LAST:event_jPopupMenuPreviewPopupMenuCanceled

    private void jPopupMenuPreviewPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenuPreviewPopupMenuWillBecomeInvisible
        // TODO add your handling code here:
        jPopupMenuPreview.updateUI();
    }//GEN-LAST:event_jPopupMenuPreviewPopupMenuWillBecomeInvisible

    private void jButtonExit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExit2ActionPerformed
        // TODO add your handling code here:
        jButtonCancel.doClick();
    }//GEN-LAST:event_jButtonExit2ActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
        if (getExtendedState() == Frame.NORMAL) setExtendedState(JFrame.MAXIMIZED_BOTH); 
    }//GEN-LAST:event_formComponentResized

    private void jButtonView6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonView6ActionPerformed
        // TODO add your handling code here:
        refreshPrivewGridBagWindows(ViewsBean.VIEW_6);
    }//GEN-LAST:event_jButtonView6ActionPerformed

    private void jButtonView8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonView8ActionPerformed
        // TODO add your handling code here:
        refreshPrivewGridBagWindows(ViewsBean.VIEW_8);
    }//GEN-LAST:event_jButtonView8ActionPerformed

    private void jButtonExit3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExit3ActionPerformed
        // TODO add your handling code here:
        jButtonCance2.doClick();
    }//GEN-LAST:event_jButtonExit3ActionPerformed

    private void jMenuItemStopAllViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStopAllViewActionPerformed
        // TODO add your handling code here:
        stopPreviewAllChannels();
    }//GEN-LAST:event_jMenuItemStopAllViewActionPerformed

    private void jButtonViewCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewCustomActionPerformed
        // TODO add your handling code here:
        
        jDialogSelectView.setVisible(true);
        CommonParas.setAppropriateLocation(jDialogSelectView, jButtonViewCustom);
    }//GEN-LAST:event_jButtonViewCustomActionPerformed

    private void jButtonCance2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCance2ActionPerformed
        // TODO add your handling code here:
        jDialogCustomizeView.setVisible(false);
    }//GEN-LAST:event_jButtonCance2ActionPerformed

    private void jComboBoxViewRowItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxViewRowItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
            if (bAutoSelect)  return;
            iRowOfView = Integer.parseInt(jComboBoxViewRow.getSelectedItem().toString());
            refreshViewNumsOfTable(iRowOfView, iColOfView);

        }
    }//GEN-LAST:event_jComboBoxViewRowItemStateChanged

    private void jComboBoxViewColItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxViewColItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
            if (bAutoSelect)  return;
            iColOfView = Integer.parseInt(jComboBoxViewCol.getSelectedItem().toString());
            refreshViewNumsOfTable(iRowOfView, iColOfView);
        }
    }//GEN-LAST:event_jComboBoxViewColItemStateChanged

    private void jTreeViewNamesValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeViewNamesValueChanged
        // TODO add your handling code here:
        /*保存按钮可以触发该事件，removeAllchildren函数会触发该事件*/
        if(bAutoTreeValueChanged) return;//是否是完全删除节点的情况下，触发TreeValueChanged事件。
        try{
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode)jTreeViewNames.getLastSelectedPathComponent();
            if (selectionNode == null) return;
            // 判断是否为树叶节点，若是则读取设备通道录像参数，若不是则不做任何事。
            if (selectionNode.isLeaf()) {

                String ViewName = selectionNode.toString();
                bAutoSelect = true;//是否刷新右侧界面过程中选择JComboBox。保证在下面的编码选择不触动JComboBox的Item改变事件。

                refreshGridBagViewTable(ViewName);

                bAutoSelect = false;//是否刷新右侧界面过程中选择JComboBox。保证在下面的编码选择不触动JComboBox的Item改变事件。
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jTreeViewNamesValueChanged()","系统在选择自定义视图的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }//GEN-LAST:event_jTreeViewNamesValueChanged

    private void jButtonMergeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMergeActionPerformed
        // TODO add your handling code here:
            
            int[] SelectedRows = jTableView.getSelectedRows();
            int[] SelectedCols = jTableView.getSelectedColumns();
            if (jTableView.canMergeCells(SelectedRows, SelectedCols))
                jTableView.mergeCells(SelectedRows, SelectedCols); 
            
    }//GEN-LAST:event_jButtonMergeActionPerformed

    private void jButtonMergeCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMergeCancelActionPerformed
        // TODO add your handling code here:
        jTableView.spliteCellAt(jTableView.getSelectedRow(), jTableView.getSelectedColumn()); 
    }//GEN-LAST:event_jButtonMergeCancelActionPerformed

    private void jButtonMergeClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMergeClearActionPerformed
        // TODO add your handling code here:
        jTableView.clearMergence();
    }//GEN-LAST:event_jButtonMergeClearActionPerformed

    private void jButtonAddViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddViewActionPerformed
        // TODO add your handling code here:
        String NewViewName = JOptionPane.showInputDialog(sInputDialog);//"请输入视图名称："
        if (NewViewName == null || NewViewName.equals("")) return;
        try{
            if (NewViewName.length() > ViewsBean.VIEWNAME_LENGTH){
                JOptionPane.showMessageDialog(null, MessageFormat.format(CommonParas.sLengthCannotExceed, sViewName, ViewsBean.VIEWNAME_LENGTH));// "自定义视图长度不能超过30！"
                return;
            }
            if(ifExistTheView(NewViewName)) {
                CommonParas.showMessage(MessageFormat.format(sViewMessage, NewViewName), sFileName);//"视图“"+NewViewName + "”已存在！"
                return;
            }
            
            DefaultTreeModel TreeModel = ((DefaultTreeModel) jTreeViewNames.getModel());//获取树模型
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(NewViewName);
            TreeModel.insertNodeInto(newNode, m_RootViewNames, m_RootViewNames.getChildCount());
            TreeModel.reload(newNode);
            hpViews.put(NewViewName,null);
            //没有触发jTreeViewNamesValueChanged事件

            TreePath path = jTreeViewNames.getNextMatch(NewViewName, 0, Position.Bias.Forward);
            jTreeViewNames.setSelectionPath(path);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jButtonAddViewActionPerformed()","系统在添加自定义视图的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }//GEN-LAST:event_jButtonAddViewActionPerformed

    private void jButtonOK2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOK2ActionPerformed
        // TODO add your handling code here:
        try{
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode)jTreeViewNames.getLastSelectedPathComponent();
            if (selectionNode == null) return;
            // 判断是否为树叶节点，若是则读取设备通道录像参数，若不是则不做任何事。
            if (selectionNode.isLeaf()) {

                String ViewName = selectionNode.toString();
                ArrayList<ViewsBean> ListViewData = hpViews.get(ViewName);
                ArrayList<ViewsBean> ListViewData2 = new ArrayList();
                short Row = Short.parseShort(jComboBoxViewRow.getSelectedItem().toString());
                short Col = Short.parseShort(jComboBoxViewCol.getSelectedItem().toString());
                short WindowNo = 0;
                ViewsBean TmpViewsBean;

                for (short i=0;i<Row;i++){
                    for (short j=0;j<Col;j++){
                        int CellState = jTableView.getCellState(i, j);
                        switch (CellState) {
                            case GridBagModel.DEFAULT://格子处于正常状态
                                TmpViewsBean = new ViewsBean(ViewName,++WindowNo, j, i, (short)1, (short)1);
                                ListViewData2.add(TmpViewsBean);
                                break;
                            case GridBagModel.MERGE://格子合并了其他的格子
                                //“行和列”和GridBagConstraints中的gridx、gridy正好互换
                                TmpViewsBean = new ViewsBean(ViewName,++WindowNo, j, i, (short)jTableView.getColumnGrid(i, j), (short)jTableView.getRowGrid(i, j));
                                ListViewData2.add(TmpViewsBean);
                                break;
                            default:
                                break; //格子被其他格子合并
                        }

                    }
                }
                
                int RetrunCode = -1;//创建/保存视图操作的返回值
                if (ListViewData == null) {//新建视图
                    RetrunCode = ViewsBean.batchInsert(ListViewData2, sFileName);
                }else{//修改视图
                    RetrunCode = ViewsBean.batchUpdate(ViewName, ListViewData2, sFileName);
                }
                if (RetrunCode > 0) {
                    
                    CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, sSaveView, MessageFormat.format(sSaveViewSpecificSucc, ViewName), sFileName);//"保存视图""保存视图“"+ViewName + "”成功！"
                    CommonParas.showMessage(MessageFormat.format(sSaveViewSpecificSucc, ViewName), sFileName);//"保存视图“"+ViewName + "”成功！"
                    
                    CreateViewsTree();
                    //因为重新生成视图树，所以再次选中该视图
                    TreePath path = jTreeViewNames.getNextMatch(ViewName, 0, Position.Bias.Forward);
                    jTreeViewNames.setSelectionPath(path);
                }else{
                    CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, sSaveView, MessageFormat.format(sSaveViewSpecificFail, ViewName), sFileName);//"保存视图", "保存视图“"+ViewName + "”失败！"
                    CommonParas.showMessage(MessageFormat.format(sSaveViewSpecificFail, ViewName), sFileName);//"保存视图“"+ViewName + "”失败！"
                }
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jButtonOK2ActionPerformed()","系统在保存自定义视图的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }//GEN-LAST:event_jButtonOK2ActionPerformed

    private void jButtonDelViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelViewActionPerformed
        // TODO add your handling code here:
        DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode)jTreeViewNames.getLastSelectedPathComponent();
        if (selectionNode == null) return;
        // 判断是否为树叶节点，若是则读取设备通道录像参数，若不是则不做任何事。
        if (selectionNode.isLeaf()) {

            String ViewName = selectionNode.toString();
            if ((JOptionPane.showConfirmDialog(jDialogCustomizeView, MessageFormat.format(sMessageBoxDelView, ViewName),
                sMessageBoxRemind, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)){//"真的要删除视图“"+ViewName+"”？""提醒"
                return;
            }
            ArrayList<ViewsBean> listViewsBean = hpViews.get(ViewName);
            if (listViewsBean == null) {
                DefaultTreeModel TreeModel = ((DefaultTreeModel) jTreeViewNames.getModel());//获取树模型
                bAutoTreeValueChanged = true;//防止在完全删除节点的情况下，触发TreeValueChanged事件。
                TreeModel.removeNodeFromParent(selectionNode);
                TreeModel.reload(selectionNode);
                bAutoTreeValueChanged = false;//防止在完全删除节点的情况下，触发TreeValueChanged事件。
                jTreeViewNames.setSelectionRow(jTreeViewNames.getRowCount()-1);
                return;
            }

            int RetrunCode = ViewsBean.DeleteOneView(ViewName, sFileName);

            if (RetrunCode > 0) {

                CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, sDelView, MessageFormat.format(sDelViewSpecificSucc, ViewName), sFileName);//"删除视图", "删除视图“"+ViewName + "”成功！"
                CreateViewsTree();
                jTreeViewNames.setSelectionRow(jTreeViewNames.getRowCount()-1);

            }else if (RetrunCode == -2){
                CommonParas.showMessage(MessageFormat.format(sCanNotDelViewSpecific, ViewName), sFileName);//"视图“"+ViewName + "”不能被删除！"
            }else {
                CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, sDelView, MessageFormat.format(sDelViewSpecificFail, ViewName), sFileName); //"删除视图", "删除视图“"+ViewName + "”失败！"
            }
        }
    }//GEN-LAST:event_jButtonDelViewActionPerformed

    private void jButtonExit4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExit4ActionPerformed
        // TODO add your handling code here:
        jDialogSelectView.setVisible(false);
    }//GEN-LAST:event_jButtonExit4ActionPerformed

    private void jButtonViewCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewCompileActionPerformed
        // TODO add your handling code here:
        
        int Row = jTableSelectView.getSelectedRow();
        String ViewName;
        if (Row > -1){
            ViewName = (String)selectViewTableModel.getValueAt(Row, 1);//视图名称
        }else ViewName = ViewsBean.VIEW_6;
        
        TreePath path = jTreeViewNames.getNextMatch(ViewName, 0, Position.Bias.Forward);
        jTreeViewNames.setSelectionPath(path);
        
        CommonParas.centerWindow(jDialogCustomizeView);
        jDialogCustomizeView.setVisible(true);
        
        

    }//GEN-LAST:event_jButtonViewCompileActionPerformed

    private void jDialogSelectViewWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialogSelectViewWindowLostFocus
        // TODO add your handling code here:
        jDialogSelectView.setVisible(false);
    }//GEN-LAST:event_jDialogSelectViewWindowLostFocus

    private void jTableSelectViewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSelectViewMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2){//双击˫
            int Row = jTableSelectView.getSelectedRow();
            if(Row < 0) return;
            
            String ViewName = (String)selectViewTableModel.getValueAt(Row, 1);//视图名称
            refreshPrivewGridBagWindows(ViewName);
        }

    }//GEN-LAST:event_jTableSelectViewMouseClicked

    private void jButtonPTZParkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPTZParkActionPerformed
        // TODO add your handling code here:
        int Row = jTablePreset.getSelectedRow();
        if (Row <0) return;
        int iPreset = Row + 1;
        String PresetName = (String)presetTableModel.getValueAt(Row, 1);

        HCNetSDK.NET_DVR_PTZ_PARKACTION_CFG Stru_PTZPark = new HCNetSDK.NET_DVR_PTZ_PARKACTION_CFG();
        Stru_PTZPark.dwParkTime = 60;
        Stru_PTZPark.byEnable = 1;
        Stru_PTZPark.wActionType = 5;//预置点。0-自动扫描，1-帧扫描，2-随机扫描，3-巡航扫描，4-花样扫描，5-预置点，6-全景扫描，7-垂直扫描 
        Stru_PTZPark.wID = (short) iPreset;
        Stru_PTZPark.dwSize = Stru_PTZPark.size();
        
        boolean bRet = false;
        NativeLong UserID = channelRPBall.getUserID();
        Stru_PTZPark.write();
        Pointer LpStruPTZPark = Stru_PTZPark.getPointer();
        Stru_PTZPark.read();
        bRet = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(UserID, HCNetSDK.NET_DVR_SET_PTZ_PARKACTION_CFG, channelRPBall.getChannel(), LpStruPTZPark, Stru_PTZPark.size());
        
        if (bRet){
            writePTZControlLog(sPTZPark + PresetName);//"设置云台守望位置："
            CommonParas.showMessage(sPTZParkSucc + PresetName, sFileName);//"成功设置云台守望位置："
        }else{
            writePTZControlErrorLog(sPTZPark + PresetName);//"设置云台守望位置："
        }

    }//GEN-LAST:event_jButtonPTZParkActionPerformed

    private void jDialogCustomizeViewWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialogCustomizeViewWindowOpened
        // TODO add your handling code here:
        int Row = jTableView.getRowCount();
        
        if (Row > 0){
            int RowHeight = jTableView.getHeight() / Row;
            if (RowHeight > 0) jTableView.setRowHeight(RowHeight);
        }
    }//GEN-LAST:event_jDialogCustomizeViewWindowOpened

    private void jMenuItemVideoEffectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVideoEffectActionPerformed
        // TODO add your handling code here:
        if (currentChannelRealPlayer == null) return;
        //写日志
        writePreviewLog(currentChannelRealPlayer, sVideoEffectPara);//"视频声音参数设置"
        JDialogVideoEffectSet DialogVideoEffectSet = new JDialogVideoEffectSet(null, true ,currentChannelRealPlayer);
        CommonParas.setAppropriateLocation(DialogVideoEffectSet, MouseInfo.getPointerInfo().getLocation());
        DialogVideoEffectSet.setVisible(true);
        
    }//GEN-LAST:event_jMenuItemVideoEffectActionPerformed
    
    /**
        *函数:      refreshViewNumsOfTable
        *函数描述:  刷新自定义视图的原始小窗口数量显示
        * @param Row    行数
        * @param Col    列数
     */
    private void refreshViewNumsOfTable(int Row, int Col){
        try{
            jTableView.setModel(new DefaultTableModel(Row, Col));
            if (Row > 0){
                int RowHeight = jTableView.getHeight() / Row;
                if (RowHeight > 0) jTableView.setRowHeight(RowHeight);
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshViewNumsOfTable()","系统在刷新自定义视图的原始小窗口数量显示的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        *函数:      initialResourceTreeModel
        *函数描述:  初始化设备资源树
        * @return DefaultTreeModel
     */
    private DefaultTreeModel initialResourceTreeModel()
    {
        DefaultTreeModel myDefaultTreeModel = new DefaultTreeModel(m_DeviceRootResource);//使用根节点创建模型
        return myDefaultTreeModel;
    }
    
    /**
        *函数:      initialViewNamesTreeModel
        *函数描述:  初始化自定义视图树
        * @return DefaultTreeModel
     */
    private DefaultTreeModel initialViewNamesTreeModel()
    {
        m_RootViewNames = new DefaultMutableTreeNode(sViewTreeRoot);//"自定义视图"
        DefaultTreeModel myDefaultTreeModel = new DefaultTreeModel(m_RootViewNames);//使用根节点创建模型
        return myDefaultTreeModel;
    }
    

    /**
        *函数:      refreshGridBagViewTable
        *函数描述:  读取自定义视图的设置，并显示出来
        * @param  ViewName 自定义视图的名称
     */
    private void refreshGridBagViewTable(String ViewName){
    
        try{
            
            ArrayList<ViewsBean> listViewsBean = hpViews.get(ViewName);
            if (listViewsBean == null) {
                jComboBoxViewRow.setSelectedIndex(0);
                jComboBoxViewCol.setSelectedIndex(0);
                refreshViewNumsOfTable(3,3);
                //取消所有合并
                jTableView.clearMergence();
                return;
            }

            Point PointNums = getOriginalViewsNums(listViewsBean);
            int RowOfView = PointNums.x;
            int ColOfView = PointNums.y;
            
            if (RowOfView == 0 || ColOfView == 0) return;
            
            jComboBoxViewRow.setSelectedItem(Integer.toString(RowOfView));
            jComboBoxViewCol.setSelectedItem(Integer.toString(ColOfView));
            //刷新Table中view个数，取消所有合并
            refreshViewNumsOfTable(RowOfView, ColOfView);
            jTableView.clearMergence();
            //设置当前的参数值
            iRowOfView = RowOfView;
            iColOfView = ColOfView;

            //设置合并的View
            for (int i=0;i< listViewsBean.size();i++){
                    ViewsBean TmpViewsBean = listViewsBean.get(i);

                    int Gridx = TmpViewsBean.getGridx();
                    int Gridy = TmpViewsBean.getGridy();
                    int GridWidth = TmpViewsBean.getGridWidth();
                    int GridHeight = TmpViewsBean.getGridHeight();

                    if (GridWidth > 1 || GridHeight > 1){
                        jTableView.mergeCells(Gridy, Gridy + GridHeight - 1, Gridx, Gridx + GridWidth - 1);
                    }
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshGridBagViewTable()","系统在读取自定义视图的设置，并显示出来的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    
    }
    
    /**
	 * 函数:      getOriginalViewsNums
         * 函数描述:  获得该视图原始子窗口的行数和列数
         * @para Point  point.x代表行数，point.y代表列数
    */
    private Point getOriginalViewsNums(ArrayList<ViewsBean> listViewsBean){
        //获取最后一个view的参数，以便设置Table中原始view的个数
        int RowOfView = 0;
        int ColOfView = 0;
        try{

            for (ViewsBean ViewsBean1 : listViewsBean) {
                int TmpRows = ViewsBean1.getGridy() + ViewsBean1.getGridHeight();
                int TmpCols = ViewsBean1.getGridx() + ViewsBean1.getGridWidth();
                if (TmpRows > RowOfView) RowOfView = TmpRows;
                if (TmpCols > ColOfView) ColOfView = TmpCols;
            }

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getOriginalViewsNums()","系统在获得该视图原始子窗口的行数和列数的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return new Point(RowOfView, ColOfView);
    }
    /**
	 * 函数:      ifExistTheView
         * 函数描述:  检测是否已经存在该视图名称
         * @para boolean   存在，返回true；不存在，返回false；
    */
    private boolean ifExistTheView(String ViewName){
        try{
            Iterator<String> HSViewNames =  hpViews.keySet().iterator();
            while  (HSViewNames.hasNext()){  
                String   TmpViewName  =  HSViewNames.next(); 
                if (TmpViewName.equals(ViewName)) return true;
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "ifExistTheView()","系统在检测是否已经存在该视图名称过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /**
	 * 函数:      refreshPrivewGridBagWindows
         * 函数描述:  重新刷新规定数目的预览窗口，同时刷新listJPanelRealPlayer列表中的Panel数据
         *            从GridBagLayout格式转换为GridLayout，只需要转换一下setLayout即可。
         *            从GridLayout格式转换为GridBagLayout，则不简单，则每一个JPanel都需要重新设置
         * @para ViewName   自定义视图的名称
    */
    private void refreshPrivewGridBagWindows(String ViewName){
        try{
            ArrayList<ViewsBean> listViewsBean = hpViews.get(ViewName);
            if (listViewsBean == null) return;
//            if (NewNums == iWindowNums && modeOfViewShow.equals("GridBagLayout")) return;

            int NewNums = listViewsBean.size();
            if (NewNums < getNumsOfPreview()) return;
            if (bZoomIn){   
                zoomInPreviewOneChannel();
            }
 
            jPanelPreviewContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, iPanelBoderThickness));
            
            GridBagLayout gridBagLayout = new GridBagLayout();
                gridBagLayout.columnWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
                gridBagLayout.rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
            jPanelPreviewContainer.setLayout(gridBagLayout);

            GridBagConstraints gridBagConstraints;

            //如果重新刷新的预览窗口数目数目减小，则删除没有预览的几个窗口，从前面开始删除
            if (NewNums < iWindowNums){

                int NewIndexOfListIfPreview = 0;//新索引
                while(true){
                    if (listIfPreview.get(NewIndexOfListIfPreview) == -1){
                        JPanel p1 = listJPanels.get(NewIndexOfListIfPreview);
                        jPanelPreviewContainer.remove(p1);
                        listJPanels.remove(NewIndexOfListIfPreview);
                        listIfPreview.remove(NewIndexOfListIfPreview);
                        listChannelRealPlayer.remove(NewIndexOfListIfPreview);
                        //因为listChannelRealPlayer删除了被结束预览的ChannelRealPlayer对象，所以listIfPreview对应的索引都会发生变化。就是IndexofCurrentJPanel以后的元素都会减1
                        for (int i=NewIndexOfListIfPreview;i<listIfPreview.size();i++){
                            if (listIfPreview.get(i) > -1) listIfPreview.set(i, listIfPreview.get(i) - 1);
                        }
                    }else NewIndexOfListIfPreview++;

                    if (NewNums == listJPanels.size()) break;//如果listJPanels中元素数量等于NewNums时，停止循环
                }
            }
            //如果重新刷新的预览窗口数目增大了，则在最后增加窗口
            if (NewNums > iWindowNums){

                for (int i=iWindowNums;i< NewNums;i++){
                    final JPanel TmpJPanel = new JPanel();

                    //处理预览窗口事件触发问题
                    processJPanelEvent(TmpJPanel);

                    jPanelPreviewContainer.add(TmpJPanel);
                    listChannelRealPlayer.add(null);
                    listJPanels.add(TmpJPanel);
                    listIfPreview.add(-1);//未预览
                }
            }
            
            //从GridBagLayout格式转换为GridLayout，只需要转换一下setLayout即可。从GridLayout格式转换为GridBagLayout，则不简单，则每一个JPanel都需要重新设置
            for (int i=0;i<NewNums;i++){
                ViewsBean TmpViewsBean = listViewsBean.get(i);
                gridBagConstraints= new GridBagConstraints();//定义一个GridBagConstraints， 
                gridBagConstraints.gridx = TmpViewsBean.getGridx();
                gridBagConstraints.gridy = TmpViewsBean.getGridy();
                gridBagConstraints.gridwidth = TmpViewsBean.getGridWidth();
                gridBagConstraints.gridheight = TmpViewsBean.getGridHeight();
                gridBagConstraints.fill = GridBagConstraints.BOTH;

                gridBagLayout.setConstraints(listJPanels.get(i), gridBagConstraints);//设置组件 
            }

            //统一命名各个JPanel的名字
            for (int i=0;i<listJPanels.size();i++){
                listJPanels.get(i).setName("JPanel"+i);
            }
            iWindowNums = NewNums;
            jPanelPreviewContainer.validate();

            modeOfViewShow = "GridBagLayout";//预览窗口显示模式，默认是标准的“GridLaout”格式；其次是“GridBagLayout”自定义模式
//            nameOfViewCustom = "";//系统刚用过的自定义视图名称
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshPrivewGridBagWindows()","系统在重新刷新规定数目的预览窗口，同时刷新listJPanelRealPlayer列表中的Panel数据过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
            
    }
    
    /**
	 * 函数:      refreshPrivewGridBagWindows2
         * 函数描述:  重新刷新规定数目的预览窗口，同时刷新listJPanelRealPlayer列表中的Panel数据
         * @para ViewName   自定义视图的名称
    */
    private void refreshPrivewGridBagWindows2(String ViewName){
        try{
            ArrayList<ViewsBean> listViewsBean = hpViews.get(ViewName);
            if (listViewsBean == null) return;
//            if (NewNums == iWindowNums && modeOfViewShow.equals("GridBagLayout")) return;

            int NewNums = listViewsBean.size();
            if (NewNums < getNumsOfPreview()) return;
            if (bZoomIn){   
                zoomInPreviewOneChannel();
            }
            
            //从GridLayout格式转换为GridBagLayout，则不简单，则每一个JPanel都需要重新设置

            jPanelPreviewContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, iPanelBoderThickness));
            GridBagLayout gridBagLayout = new GridBagLayout();
                gridBagLayout.columnWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
                gridBagLayout.rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
            jPanelPreviewContainer.setLayout(gridBagLayout);

            GridBagConstraints gridBagConstraints;

            //如果重新刷新的预览窗口数目数目减小，则删除没有预览的几个窗口，从前面开始删除
            if (NewNums < iWindowNums){

                int NewIndexOfListIfPreview = 0;//新索引
                while(true){
                    if (listIfPreview.get(NewIndexOfListIfPreview) == -1){
                        JPanel p1 = listJPanels.get(NewIndexOfListIfPreview);
                        jPanelPreviewContainer.remove(p1);
                        listJPanels.remove(NewIndexOfListIfPreview);
                        listIfPreview.remove(NewIndexOfListIfPreview);
                        listChannelRealPlayer.remove(NewIndexOfListIfPreview);
                        //因为listChannelRealPlayer删除了被结束预览的ChannelRealPlayer对象，所以listIfPreview对应的索引都会发生变化。就是IndexofCurrentJPanel以后的元素都会减1
                        for (int i=NewIndexOfListIfPreview;i<listIfPreview.size();i++){
                            if (listIfPreview.get(i) > -1) listIfPreview.set(i, listIfPreview.get(i) - 1);
                        }
                    }else NewIndexOfListIfPreview++;

                    if (NewNums == listJPanels.size()) break;//如果listJPanels中元素数量等于NewNums时，停止循环
                }

                for (int i=0;i<NewNums;i++){
                    ViewsBean TmpViewsBean = listViewsBean.get(i);
                    gridBagConstraints= new GridBagConstraints();//定义一个GridBagConstraints， 
                    gridBagConstraints.gridx = TmpViewsBean.getGridx();
                    gridBagConstraints.gridy = TmpViewsBean.getGridy();
                    gridBagConstraints.gridwidth = TmpViewsBean.getGridWidth();
                    gridBagConstraints.gridheight = TmpViewsBean.getGridHeight();
                    gridBagConstraints.fill = GridBagConstraints.BOTH;

                    gridBagLayout.setConstraints(listJPanels.get(i), gridBagConstraints);//设置组件 
                }

            }
            //如果重新刷新的预览窗口数目增大了，则在最后增加窗口
            if (NewNums > iWindowNums){
                for (int i=0;i<iWindowNums;i++){
                    ViewsBean TmpViewsBean = listViewsBean.get(i);
                    gridBagConstraints= new GridBagConstraints();//定义一个GridBagConstraints， 
                    gridBagConstraints.gridx = TmpViewsBean.getGridx();
                    gridBagConstraints.gridy = TmpViewsBean.getGridy();
                    gridBagConstraints.gridwidth = TmpViewsBean.getGridWidth();
                    gridBagConstraints.gridheight = TmpViewsBean.getGridHeight();
                    gridBagConstraints.fill = GridBagConstraints.BOTH;

                    gridBagLayout.setConstraints(listJPanels.get(i), gridBagConstraints);//设置组件 
                }
                for (int i=iWindowNums;i< NewNums;i++){
                    ViewsBean TmpViewsBean = listViewsBean.get(i);
                    final JPanel TmpJPanel = new JPanel();

                    //处理预览窗口事件触发问题
                    processJPanelEvent(TmpJPanel);

                    gridBagConstraints= new GridBagConstraints();//定义一个GridBagConstraints， 
                    gridBagConstraints.gridx = TmpViewsBean.getGridx();
                    gridBagConstraints.gridy = TmpViewsBean.getGridy();
                    gridBagConstraints.gridwidth = TmpViewsBean.getGridWidth();
                    gridBagConstraints.gridheight = TmpViewsBean.getGridHeight();
                    gridBagConstraints.fill = GridBagConstraints.BOTH;

                    jPanelPreviewContainer.add(TmpJPanel, gridBagConstraints);
                    listChannelRealPlayer.add(null);
                    listJPanels.add(TmpJPanel);
                    listIfPreview.add(-1);//未预览
                }
            }

            //统一命名各个JPanel的名字
            for (int i=0;i<listJPanels.size();i++){
                listJPanels.get(i).setName("JPanel"+i);
            }
            iWindowNums = NewNums;
            jPanelPreviewContainer.validate();

            modeOfViewShow = "GridBagLayout";//预览窗口显示模式，默认是标准的“GridLaout”格式；其次是“GridBagLayout”自定义模式
//            nameOfViewCustom = "";//系统刚用过的自定义视图名称
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshPrivewGridBagWindows2()","系统在重新刷新规定数目的预览窗口，同时刷新listJPanelRealPlayer列表中的Panel数据过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
            
    }
    /**
	 * 函数:      refreshPrivewWindows
         * 函数描述:  重新刷新规定数目的预览窗口，同时刷新listJPanelRealPlayer列表中的Panel数据
         *            从GridBagLayout格式转换为GridLayout，只需要转换一下setLayout即可。
         *            从GridLayout格式转换为GridBagLayout，则不简单，则每一个JPanel都需要重新设置
         * @para RowNums   窗口的行数
         * @para ColNums   窗口的列数
    */
    private void refreshPrivewWindows(int RowNums,int ColNums){
        try{
            int NewNums = RowNums * ColNums;//重新刷新的预览窗口数目
            if (NewNums == iWindowNums && modeOfViewShow.equals("GridLayout")) return;
            if (NewNums < getNumsOfPreview()) return;
            if (bZoomIn){   
                zoomInPreviewOneChannel();
            }
            
            //从GridBagLayout格式转换为GridLayout，只需要转换一下setLayout即可。
            jPanelPreviewContainer.setLayout(new GridLayout(RowNums, ColNums, 0, 0));
            jPanelPreviewContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, iPanelBoderThickness));

            //如果重新刷新的预览窗口数目增大了，则在最后增加窗口
            if (NewNums > iWindowNums){
                for (int i=iWindowNums;i< NewNums;i++){
                    final JPanel TmpJPanel = new JPanel();
                    
                    //处理预览窗口事件触发问题
                    processJPanelEvent(TmpJPanel);
                    
                    listChannelRealPlayer.add(null);
                    listJPanels.add(TmpJPanel);
                    listIfPreview.add(-1);//未预览
                    jPanelPreviewContainer.add(TmpJPanel);
                }
            }
            //如果重新刷新的预览窗口数目数目减小，则删除没有预览的几个窗口，从前面开始删除
            if (NewNums < iWindowNums){

                int NewIndexOfListIfPreview = 0;//新索引
                while(true){
                    if (listIfPreview.get(NewIndexOfListIfPreview) == -1){
                        JPanel p1 = listJPanels.get(NewIndexOfListIfPreview);
                        jPanelPreviewContainer.remove(p1);
                        listJPanels.remove(NewIndexOfListIfPreview);
                        listIfPreview.remove(NewIndexOfListIfPreview);
                        listChannelRealPlayer.remove(NewIndexOfListIfPreview);
                        //因为listChannelRealPlayer删除了被结束预览的ChannelRealPlayer对象，所以listIfPreview对应的索引都会发生变化。就是IndexofCurrentJPanel以后的元素都会减1
                        for (int i=NewIndexOfListIfPreview;i<listIfPreview.size();i++){
                            if (listIfPreview.get(i) > -1) listIfPreview.set(i, listIfPreview.get(i) - 1);
                        }
                    }else NewIndexOfListIfPreview++;

                    if (NewNums == listJPanels.size()) break;//如果listJPanels中元素数量等于NewNums时，停止循环
                }

            }
            
            //统一命名各个JPanel的名字
            for (int i=0;i<listJPanels.size();i++){
                listJPanels.get(i).setName("JPanel"+i);
            }
            iWindowNums = NewNums;
            jPanelPreviewContainer.validate();
            modeOfViewShow = "GridLayout";//预览窗口显示模式，默认是标准的“GridLaout”格式；其次是“GridBagLayout”
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshPrivewWindows()","系统在重新刷新规定数目的预览窗口，同时刷新listJPanelRealPlayer列表中的Panel数据过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      processJPanelEvent
         * 函数描述:  处理预览窗口事件
         * @para TmpJPanel   JPanel控件
    */
    private void processJPanelEvent(final JPanel TmpJPanel){
        final Panel TmpPanel = new Panel();
        
        final Color LineColor = Color.WHITE;//new Color(200,200,200);
        
        TmpJPanel.setLayout(new GridLayout(1 , 1));
        TmpJPanel.setBorder(BorderFactory.createLineBorder(LineColor, iPanelBoderThickness));
        TmpJPanel.add(TmpPanel);
        //JPanel事件：添加鼠标单击事件，获得焦点，边框变红，失去焦点的变黑
        TmpJPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (currentJPanel != null) currentJPanel.setBorder(BorderFactory.createLineBorder(LineColor, iPanelBoderThickness));
                TmpJPanel.setBorder(BorderFactory.createLineBorder(Color.RED, iPanelBoderThickness));
                currentJPanel = TmpJPanel;
            }
        });
        //以下是Panel事件
        TmpPanel.setBackground(new java.awt.Color(64, 64, 64));
        TmpPanel.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (currentJPanel != null) currentJPanel.setBorder(BorderFactory.createLineBorder(LineColor, iPanelBoderThickness));
                TmpJPanel.setBorder(BorderFactory.createLineBorder(Color.RED, iPanelBoderThickness));
                currentJPanel = TmpJPanel;
                PanelMousePressed(evt);//响应鱼球联动手动操作
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                PanelMouseReleased(evt);//包括：窗口放大、鼠标右键弹出菜单、响应鱼球联动手动操作等
            }

        });

        TmpPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                PanelMouseDragged(evt);//响应鱼球联动手动操作
            }

            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                PanelMouseMoved(evt);//响应球机云台操作
            }
        });
        TmpPanel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            @Override
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                PanelMouseWheelMoved(evt);//响应云台放大缩小操作
            }
        });
    }
    /**
	 * 函数:      refreshChannelRealPlayerList
         * 函数描述:  刷新要预览的通道预览信息列表
         * @para SelectedNode  选中的节点名
         * @para Grade   节点的级别。比如2级节点、3级节点
    */
    private void  refreshChannelRealPlayerList(DefaultMutableTreeNode SelectedNode){
       
        try{
            //listChannelRealPlayer.clear();

            Enumeration<?> enumeration = SelectedNode.children();//遍历该节点的所有子节点.
            int ChildCount = SelectedNode.getChildCount();
            
            double SqrtOfChildCount = Math.sqrt(ChildCount);
            int RowOfWindows = (int)SqrtOfChildCount;
            
            if (RowOfWindows == SqrtOfChildCount) refreshPrivewWindows(RowOfWindows, RowOfWindows);
            else refreshPrivewWindows(RowOfWindows+1,RowOfWindows+1);

            int IndexOflistDeviceLoginUserID = -1;//listDeviceLoginUserID������
            int IndexListDeviceparaBean = -1;//listDeviceparaBean������
            int NumsOfListChannelRealPlayer = 0;//listChannelRealPlayer�ĸ���
            String AnotherName = "";//设备别名

            while(enumeration.hasMoreElements()){ //遍历枚举对象.
                if (NumsOfListChannelRealPlayer >= listJPanels.size()) break;
                //先定义一个节点变量.
                DefaultMutableTreeNode Node = (DefaultMutableTreeNode) enumeration.nextElement();//将节点名称给node.
                String NodeName = Node.toString();
                String AnotherName2 = NodeName.substring(0, NodeName.indexOf("_"));
                if (!(AnotherName2.equals(AnotherName))){

                    //IndexListDeviceparaBean = getIndexOfDeviceList(AnotherName2);
                    IndexListDeviceparaBean = CommonParas.getIndexOfDeviceList(AnotherName2, sFileName);
                    if (IndexListDeviceparaBean == -1) continue;

                    AnotherName = AnotherName2;
                    IndexOflistDeviceLoginUserID = IndexListDeviceparaBean;
                }
                //通道号
                int iChannel = Integer.parseInt(NodeName.substring(NodeName.lastIndexOf("_")+1,NodeName.length()));
                if (NodeName.lastIndexOf("IP") > -1) iChannel = iChannel + 32;//IPͨ����Ҫ��32
                NativeLong lChannel = new NativeLong(iChannel);

                //判断有无权限
                if (!CommonParas.showNoAuthorityMessage(rootPane, CommonParas.AuthorityItems.AUTHORITY_PREVIEW,  AnotherName, sFileName)) {
                    continue;
                } 

                ChannelRealPlayer channelRealPlayer = new ChannelRealPlayer();
                    channelRealPlayer.setIndexOflistDevice(IndexOflistDeviceLoginUserID);
                    channelRealPlayer.setChannel(lChannel);
                    channelRealPlayer.setPanelRealplay((Panel)listJPanels.get(NumsOfListChannelRealPlayer).getComponent(0));
                    channelRealPlayer.setDeviceparaBean(CommonParas.getDeviceParaBean(IndexListDeviceparaBean, sFileName));
                    String[] ReturnA = CommonParas.getArraysFromTreeNode(NodeName);//数组：设备名、接入设备名、设备资源节点名
                    channelRealPlayer.setEncodingDVRChannelNode(ReturnA[2]);//设备资源节点名
                    if(!ReturnA[1].equals("")) channelRealPlayer.setSerialNoJoin(CommonParas.getSerialNO(ReturnA[1], sFileName));//接入设备序列号

                    channelRealPlayer.setGroupName(sGroupName);
                //listChannelRealPlayer.add(channelRealPlayer);
                listChannelRealPlayer.set(NumsOfListChannelRealPlayer, channelRealPlayer);

                NumsOfListChannelRealPlayer ++;

            }


        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshChannelRealPlayerList()","系统刷新要预览的通道预览信息列表过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    
    /**
	 * 函数:      refreshOneChannelRealPlayer
         * 函数描述:  刷新单个要预览的通道预览信息
         * @para SelectedNode  选中的节点。针对监控点而不是组名
         * @return ChannelRealPlayer单个要预览的通道预览信息
    */
    private ChannelRealPlayer  refreshOneChannelRealPlayer(DefaultMutableTreeNode SelectedNode, int CurrentJPanel){
        try {
            if (SelectedNode == null) return null;

            String NodeName = SelectedNode.toString();
            String AnotherName = NodeName.substring(0, NodeName.indexOf("_"));
            //ͨ����
            int iChannel = Integer.parseInt(NodeName.substring(NodeName.lastIndexOf("_")+1,NodeName.length()));
            
            if (NodeName.lastIndexOf("IP") > -1) iChannel = iChannel + 32;//IP通道要加上32，从33开始
//            int IndexListDeviceparaBean = getIndexOfDeviceList(AnotherName);
            int IndexListDeviceparaBean = CommonParas.getIndexOfDeviceList(AnotherName,sFileName);
            if (IndexListDeviceparaBean == -1) return null;

            int IndexOflistDeviceLoginUserID = IndexListDeviceparaBean;//listDeviceLoginUserID.get(Grade)
            ChannelRealPlayer channelRealPlayer = new ChannelRealPlayer();
                    //channelRealPlayer.setIndexOflistDevice(IndexOflistDeviceLoginUserID);
                    channelRealPlayer.setChannel(new NativeLong(iChannel));
                    channelRealPlayer.setPanelRealplay((Panel)currentJPanel.getComponent(0));
                    CommonParas.setBasicParasOfChannelRealPlayer(channelRealPlayer, IndexListDeviceparaBean, sFileName);
                    String[] ReturnA = CommonParas.getArraysFromTreeNode(NodeName);//数组：设备名、接入设备名、设备资源节点名
                    channelRealPlayer.setEncodingDVRChannelNode(ReturnA[2]);//设备资源节点名
                    if(!ReturnA[1].equals("")) channelRealPlayer.setSerialNoJoin(CommonParas.getSerialNO(ReturnA[1], sFileName));//接入设备序列号
                    channelRealPlayer.setGroupName(sGroupName);

            listChannelRealPlayer.set(CurrentJPanel, channelRealPlayer);

            return channelRealPlayer;
        }catch (NumberFormatException | HeadlessException e){
            TxtLogger.append(this.sFileName, "refreshOneChannelRealPlayer()","系统刷新单个要预览的通道预览信息过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            return null;
        }
    }
    /**
	 * 函数:      previewAllChannels
         * 函数描述:  预览相关所有通道的视频摄像
    */
    private void previewAllChannels(){
        try{
            
            for (int i=0;i<listChannelRealPlayer.size();i++){
                
                if (i >= listJPanels.size()) break;
                ChannelRealPlayer channelRealPlayer = listChannelRealPlayer.get(i);
                
                if (channelRealPlayer == null) continue;
                
                int ReturnCode = CommonParas.previewOneChannel(channelRealPlayer, sFileName);
                
                if (ReturnCode > 0){
                    //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
                    writePreviewLog(channelRealPlayer,sStartPreview);//"开始预览"
                    listIfPreview.set(i, i);//设置窗口矩阵第i号窗口已经被预览成功
                    
                }else{
                    channelRealPlayer = null;
                    
                }
                
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "previewAllChannels()","系统预览相关所有通道的视频摄像过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      fullScreenWindow
         * 函数描述:  在预览窗口进行全屏显示/退出全屏显示操作
    */
    private void fullScreenWindow(){
        try{
            GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment() .getDefaultScreenDevice();   
            if(bFullScreen){
                bTreeShow = bTreeShowOriginal;//窗口放大前记录原先设备树是否显示，默认为显示
                //bOpenPTZ = bOpenPTZOriginal;//窗口放大前记录原先是否打开云台控制
                //必须进行判断，否则如果全屏之前就已经隐藏了，退出全屏的时候会显示不出来，因为之前记录的设备树宽度为0
                if (bTreeShow) showDVRResourceTree(bTreeShow);
                //showPTZWindow(bOpenPTZ);//退出全屏之后不必要再打开云台控制窗口
                jPanelFoot.setVisible(true);
                bFullScreen = false;
                if (bZoomIn){   //如果先前处于最大化时，会出现全屏显示尺寸不够的问题。
                    zoomInPreviewOneChannel();zoomInPreviewOneChannel();
                }
            }else {
                
                bTreeShowOriginal = bTreeShow;//窗口放大前记录原先设备树是否显示，默认为显示
                //bOpenPTZOriginal = bOpenPTZ;//窗口放大前记录原先是否打开云台控制
                //必须进行判断，否则如果全屏之前就已经隐藏了，退出全屏的时候会显示不出来，因为之前记录的设备树宽度为0
                if (bTreeShow) showDVRResourceTree(false);//隐藏设备树
                if (bOpenPTZ) showPTZWindow(false);//隐藏云台控制窗口
                jPanelFoot.setVisible(false);

                if (modeOfViewShow.equals("GridBagLayout")){//GridBagLayout模式时，经常会出现最大窗口很大，而其他窗口很小的情况
                    zoomInPreviewOneChannel();zoomInPreviewOneChannel();
                }else if (bZoomIn){   //如果先前处于最大化时，会出现全屏显示尺寸不够的问题。
                    zoomInPreviewOneChannel();zoomInPreviewOneChannel();
                }
                bFullScreen = true;
            }

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "fullScreenWindow()","系统在预览窗口进行全屏显示/退出全屏显示操作过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    
    /**
	 * 函数:      fullScreenWindow2
         * 函数描述:  在预览窗口进行全屏显示/退出全屏显示操作
    */
    private void fullScreenWindow2(){
        try{
            GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment() .getDefaultScreenDevice();   
            if(bFullScreen){
                    if (myDevice.isFullScreenSupported()){
                        //this.setUndecorated(false);  
                        //不显示任务栏  
                        myDevice.setFullScreenWindow(null);  
                    }
                    else{
                        //this.setUndecorated(false);
                        this.setSize(originalScreenSize);
                    }

                    bFullScreen = false;
            }else {

                    if (myDevice.isFullScreenSupported()){
                        this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
                        //不显示任务栏  
                        myDevice.setFullScreenWindow(this);  
                        
                    }
                    else{
                        //originalScreenSize = this.getSize();
                        //this.setUndecorated(true);
                        //获取屏幕尺寸
                        Dimension   screenSize   =   Toolkit.getDefaultToolkit().getScreenSize();

                        this.setSize(screenSize);
                        
                    }

                    bFullScreen = true;
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "fullScreenWindow2()","系统在预览窗口进行全屏显示/退出全屏显示操作过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    
    private void zoomIn2(){
        if (bZoomIn){
            int Num = (int)Math.sqrt(listJPanels.size());
            jPanelPreviewContainer.setLayout(new GridLayout(Num, Num,1,1));
            jPanelPreviewContainer.removeAll();
            jPanelPreviewContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            for (int i=0;i<listJPanels.size();i++){
                JPanel temJPanel = listJPanels.get(i);
                jPanelPreviewContainer.add(temJPanel);
                ((Panel)temJPanel.getComponent(0)).validate();
                temJPanel.updateUI();
            } 
            jPanelPreviewContainer.validate();
            bZoomIn = false;

            setJPanelAllOCXEnable(jPanelFoot, true);
            jTreeResource.setEnabled(true);
        }
        else {
            JPanel pp = currentJPanel;
            for (int i=0;i<listJPanels.size();i++){
                JPanel temPanel = listJPanels.get(i);
                if (!temPanel.equals(currentJPanel)) jPanelPreviewContainer.remove(temPanel);
            }
    //        jPanelPreviewContainer.setLayout(null);
            jPanelPreviewContainer.setLayout(new GridLayout(1, 1,1,1));
            jPanelPreviewContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
//            jPanelPreviewContainer.add(pp);
            jPanelPreviewContainer.validate();
            bZoomIn = true;
            setJPanelAllOCXEnable(jPanelFoot, false);
            jTreeResource.setEnabled(false);
        }
        
    }
    /**
	 * 函数:      zoomInPreviewOneChannel2
         * 函数描述:  将某一个通道的摄像预览放大显示
         * @para ZoomIn  true放大预览；false缩小预览
    */
    private void zoomInPreviewOneChannel(){
            //                zoomIn2();
            //jPanel2.setLayout(null);
            if (bZoomIn){
                //新添加的
                currentJPanel.setPreferredSize(dsSizeBeforeZoom);//恢复原来大小，在GridBagLayout方式时，很管用
                if (modeOfViewShow.equals("GridLayout")){
                    int RowOfWindows = (int)Math.sqrt(listJPanels.size());
                    jPanelPreviewContainer.setLayout(new GridLayout(RowOfWindows, RowOfWindows,0,0));
                    jPanelPreviewContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                }
                for (int i=0;i<listJPanels.size();i++){
                    JPanel temJPanel = (JPanel)listJPanels.get(i);
                    if (!currentJPanel.equals(temJPanel))  temJPanel.setVisible(true);
                    //temJPanel.updateUI();
                }

                this.validate();//验证此容器及其所有子组件。使用 validate 方法会使容器再次布置其子组件。已经显示容器后，在修改此容器的子组件的时候（在容器中添加或移除组件，或者更改与布局相关的信息），应该调用上述方法。 
                bZoomIn = false;
            }else {
                dsSizeBeforeZoom = currentJPanel.getSize();//记录原来大小//新添加的
                
                if (modeOfViewShow.equals("GridLayout")) jPanelPreviewContainer.setLayout(new java.awt.FlowLayout(FlowLayout.CENTER,1,1));
                
                for (int i=0;i<listJPanels.size();i++){
                    JPanel temJPanel = (JPanel)listJPanels.get(i);
                    if (!currentJPanel.equals(temJPanel)) temJPanel.setVisible(false);
                }

                setCurrentJPanelMaxSize();//currentJPanel.setPreferredSize(new Dimension(jPanelPreviewContainer.getWidth() - 2, jPanelPreviewContainer.getHeight()-2));
                bZoomIn = true;
            }
            

    }
    /**
	 * 函数:      setCurrentJPanelMaxSize
         * 函数描述:  将currentJPanel尺寸最大化
    */
    private void setCurrentJPanelMaxSize(){
        currentJPanel.setPreferredSize(new Dimension(jPanelPreviewContainer.getWidth() - 2*iPanelBoderThickness, jPanelPreviewContainer.getHeight()-2*iPanelBoderThickness));
    }
    /**
	 * 函数:      zoomInPreviewOneChannel
         * 函数描述:  将某一个通道的摄像预览放大显示
         * @para ZoomIn  true放大预览；false缩小预览
    */
    private void zoomInPreviewOneChannel2(){
        //首先将jPanelZoomIn移动到jPanelPreviewContainer同样的位置，设置一样的大小，并且并将设置在最前层、为可见。
        //获得是应该对listChannelRealPlayer中的哪一个预览对象进行预览，即当前预览对象。从而产生一个新的channelRealPlayer，并将该channelRealPlayer在jPanelZoomIn中的panelPreviewZoomIn进行预览
        try {

            if (currentChannelRealPlayer == null) return;
            
            NativeLong Channel = currentChannelRealPlayer.getChannel();//通道号为空，不做任何操作
            if (Channel.intValue() < 1) return;
            
            final ChannelRealPlayer currentChannelRealPlayerOld = currentChannelRealPlayer;
            final JPanel currentJPanelOld = currentJPanel;
            //新建JWindow 全屏预览
            //final DialogFullScreen wnd = new DialogFullScreen(this,true);
//            final  JWindow wnd = new JWindow();
            //获取屏幕尺寸
            Dimension   screenSize   =   Toolkit.getDefaultToolkit().getScreenSize();
//            Rectangle   PreviewContainerSize   =   jPanelPreviewContainer.getBounds();
//            wnd.setBounds(PreviewContainerSize);
            wnd.setSize(screenSize);
            wnd.setUndecorated(true);
            wnd.setVisible(true);

            wnd.setLayout(new GridLayout(1, 1,0,0));
            final JPanel TmpJPanel = new JPanel();
            currentJPanel = TmpJPanel;
            wnd.add(TmpJPanel);
            wnd.validate();
            TmpJPanel.setLayout(new GridLayout(1, 1,0,0));
            Panel TmpPanel = new Panel();
            
            
            TmpJPanel.add(TmpPanel);
            TmpJPanel.validate();
            
            final ChannelRealPlayer channelRealPlayer = currentChannelRealPlayer.clone();
            channelRealPlayer.setPanelRealplay(TmpPanel);
            
            
            listJPanels.add(TmpJPanel);
            TmpJPanel.setName("JPanel" + (listJPanels.size()-1));
            listIfPreview.add(-1);//未预览
            listChannelRealPlayer.add(channelRealPlayer);
            
            TmpPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                        
                @Override
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    if (currentJPanel != null) currentJPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    TmpJPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                    currentJPanel = TmpJPanel;
                    PanelMousePressed(evt);//响应鱼球联动手动操作
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    try{

                            //如果当前的ChannelRealPlayer对象为空，则不弹出菜单和进行鱼球联动操作
                            if(currentChannelRealPlayer == null) return;
                            //如果已经开始启动云台
                            if (currentChannelRealPlayer.isIfPTZCtrl()) {
                                PTZControlAll(currentChannelRealPlayer.getPreviewHandle(), iCommand, 1);
                            }
                            if (evt.getClickCount() == 2){//双击˫��
                                //停止预览
                                CommonParas.stopPreviewOneChannel(channelRealPlayer, sFileName);
                                listJPanels.remove(TmpJPanel);
                                listIfPreview.remove(listIfPreview.size()-1);//未预览
                                listChannelRealPlayer.remove(channelRealPlayer);
                                currentChannelRealPlayer = currentChannelRealPlayerOld;
                                jMenuItemPreview.setEnabled(true);
                                jMenuItemFullScreen.setEnabled(true);
                                jMenuItemPTZWindow.setEnabled(true);
                                jMenuItemFishBallTrack.setEnabled(true);
                                jMenuItemSetupFishBallTrack.setEnabled(true);
                                currentJPanel = currentJPanelOld;
                                CommonParas.previewOneChannel(currentChannelRealPlayer, sFileName);
                                wnd.remove(TmpJPanel);
                                wnd.dispose();
                                //this.setVisible(true);
                                return;
                            }
                            //鼠标右键弹出菜单
                            showPopupMenu(evt);
                            

                    }catch (Exception e){
                        TxtLogger.append(sFileName, "zoomInPreviewOneChannel2（）-mouseReleased()","当前预览窗口在鼠标释放时，出现错误"
                                         + "\r\n                       Exception:" + e.toString());
                    }
                }

            });
                    
            TmpPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

                @Override
                public void mouseMoved(java.awt.event.MouseEvent evt) {
                    PanelMouseMoved(evt);//响应球机云台操作
                }
            });
            TmpPanel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
                @Override
                public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                    PanelMouseWheelMoved(evt);//响应云台放大缩小操作
                }
            });
            
            jMenuItemPreview.setEnabled(false);
            jMenuItemFullScreen.setEnabled(false);
            jMenuItemPTZWindow.setEnabled(false);
            jMenuItemFishBallTrack.setEnabled(false);
            jMenuItemSetupFishBallTrack.setEnabled(false);
            CommonParas.stopPreviewOneChannel(currentChannelRealPlayerOld, sFileName);
            CommonParas.previewOneChannel(channelRealPlayer, sFileName);
            listIfPreview.set(listIfPreview.size()-1,listChannelRealPlayer.size()-1);//已预览

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "zoomInPreviewOneChannel2()","将某一个通道的摄像预览放大显示"
                             + "\r\n                       Exception:" + e.toString());
        }

    }
    /**
        * 函数:      stopPreviewAllChannels
        * 函数描述:  结束相关所有通道的视频摄像预览
    */
    private void stopPreviewAllChannels(){
        try{
            for (int i=0;i<listChannelRealPlayer.size();i++){

                ChannelRealPlayer channelRealPlayer = listChannelRealPlayer.get(i);
                if (channelRealPlayer != null) {
                    int ReturnCode = CommonParas.stopPreviewOneChannel(channelRealPlayer,sFileName);
                    if (ReturnCode > 0){
                        //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
                        writePreviewLog(channelRealPlayer, sStopPreview);//"结束预览"
                    }
                    channelRealPlayer = null;
                    listChannelRealPlayer.set(i, null);
                }
            }
            //listIfPreview初始化
            for (int i=0;i<listIfPreview.size();i++){
                listIfPreview.set(i, -1);
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "stopPreviewAllChannels()","系统结束相关所有通道的视频摄像预览过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    
    /**
        * 函数:      stopPreviewAllChannels
        * 函数描述:  结束相关所有通道的视频摄像预览
        * @return int 成功，返回1；失败返回0；出错返回-1
    */
    private int stopPreviewOneChannel(int IndexofCurrentJPanel){
        try{
            
//            int IndexlistChannelRealPlayer = listIfPreview.get(IndexofCurrentJPanel);
//            if (IndexlistChannelRealPlayer > -1) {
                //currentChannelRealPlayer = getCurrentChannelRealPlayer();
                if (CommonParas.stopPreviewOneChannel(currentChannelRealPlayer, sFileName) > 0){
                    //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
                    writePreviewLog(currentChannelRealPlayer, sStopPreview);//"结束预览"
                    
                    currentChannelRealPlayer = null;
                    listChannelRealPlayer.set(IndexofCurrentJPanel, null);
                    listIfPreview.set(IndexofCurrentJPanel, -1);
//                    //因为listChannelRealPlayer删除了被结束预览的ChannelRealPlayer对象，所以listIfPreview对应的索引都会发生变化。就是IndexofCurrentJPanel以后的元素都会减1
//                    for (int i=IndexofCurrentJPanel+1;i<listIfPreview.size();i++){
//                        if (listIfPreview.get(i) > -1) listIfPreview.set(i, listIfPreview.get(i) - 1);
//                    }
                    if (bZoomIn) zoomInPreviewOneChannel();
                    return 1;
                }else return 0;
//            }
            
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jMenuItemPreviewActionPerformed()","结束当前窗口预览过程中出现错误："
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }
    /**
	 * 函数:      getFishEyeChannelRealPlayer
         * 函数描述:   获得鱼眼的预览对象
         * @return ChannelRealPlayer   球机预览对象ChannelRealPlayer
    */
    private ChannelRealPlayer getFishEyeChannelRealPlayer(HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo, HCNetSDK.NET_DVR_IPPARACFG StrIpparaCfg, int Channel){
        try{
            if (StrDevInfo == null) return null;
            if (HCNetSDKExpand.isFishEye(StrDevInfo.wDevType)) return currentChannelRealPlayer;
            //前面判断非接入设备是否是鱼眼
            //下面判断是否是接入设备，根据HCNetSDK.NET_DVR_IPPARACFG，得出接入设备参数，然后判断
            if (StrIpparaCfg == null) return null;
            int iIPChanel = Channel - (HCNetSDK.MAX_ANALOG_CHANNUM + StrDevInfo.byStartChan);
            if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return null;//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）

            byte iDevID = StrIpparaCfg.struIPChanInfo[iIPChanel].byIPID;
            String IP4 = new String (StrIpparaCfg.struIPDevInfo[iDevID - 1].struIP.sIpV4).trim();
            int Index = CommonParas.getIndexOfDeviceList(IP4, "", sFileName);
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfoIP = CommonParas.getStrDeviceInfo(Index, sFileName);
            if (StrDevInfoIP == null) return null;
            if (HCNetSDKExpand.isFishEye(StrDevInfoIP.wDevType)) {
                ChannelRealPlayer channelRealPlayer = new ChannelRealPlayer();
                    //channelRealPlayer.setIndexOflistDevice(Index);
                    channelRealPlayer.setChannel(new NativeLong(Channel));
                    //channelRealPlayer.setPanelRealplay((Panel)listJPanels.get(NumsOfListChannelRealPlayer).getComponent(0));
                    CommonParas.setBasicParasOfChannelRealPlayer(channelRealPlayer, Index, sFileName);
                    //channelRealPlayer.setDeviceparaBean(CommonParas.getDeviceParaBean(Index, sFileName));
                return channelRealPlayer;
            }

        }catch (Exception e){
            TxtLogger.append(sFileName, "getFishEyeChannelRealPlayer()","系统在判断设备是否是鱼眼过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return null;
    }
    
    /**
	 * 函数:      getBallChannelRealPlayer
         * 函数描述:   获得球机的预览对象
         * @return ChannelRealPlayer   球机预览对象ChannelRealPlayer
    */
    private ChannelRealPlayer getBallChannelRealPlayer(){
        int IndexBall = -1;
        try {
            for (int i=0;i<CommonParas.g_listDeviceDetailPara.size();i++){
                HCNetSDK.NET_DVR_DEVICEINFO_V30 strDeviceInfo = CommonParas.getStrDeviceInfo(i, sFileName);
                if (HCNetSDKExpand.isIPD(strDeviceInfo.wDevType)){
                    IndexBall = i;
                    break;
                }
            }
            if (IndexBall > -1) {
                ChannelRealPlayer channelRealPlayer = new ChannelRealPlayer();
                    //channelRealPlayer.setIndexOflistDevice(Index);
                    channelRealPlayer.setChannel(new NativeLong(1));
                    //channelRealPlayer.setPanelRealplay((Panel)listJPanels.get(NumsOfListChannelRealPlayer).getComponent(0));
                    CommonParas.setBasicParasOfChannelRealPlayer(channelRealPlayer, IndexBall, sFileName);
                    //channelRealPlayer.setDeviceparaBean(CommonParas.getDeviceParaBean(Index, sFileName));
                return channelRealPlayer;
//                for (int i=0;i<listChannelRealPlayer.size();i++){
//                    ChannelRealPlayer channelRealPlayer = listChannelRealPlayer.get(i);
//                    if (channelRealPlayer.getIndexOflistDevice() == IndexBall) return channelRealPlayer.clone();
//                }
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getBallChannelRealPlayer()","获得球机的预览对象"
                             + "\r\n                       Exception:" + e.toString());
        }
        return null;
    }
    
    /**
	 * 函数:      getCurrentChannelRealPlayer
         * 函数描述:   获得当前窗口的预览对象
         * @return ChannelRealPlayer   预览对象ChannelRealPlayer
    */
    private ChannelRealPlayer getCurrentChannelRealPlayer(){
        
        try{
            String currentName = currentJPanel.getName();
            int iCurrentJPanel = Integer.parseInt(currentName.substring(6));
            int IndexlistChannelRealPlayer = listIfPreview.get(iCurrentJPanel);
            if (listIfPreview.get(iCurrentJPanel) == -1)  return null;//

            ChannelRealPlayer channelRealPlayer = listChannelRealPlayer.get(IndexlistChannelRealPlayer);
            return channelRealPlayer;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getCurrentChannelRealPlayer()","获得当前窗口的预览对象"
                             + "\r\n                       Exception:" + e.toString());
            return null;
        }
    } 
    /**
        * 函数:      getIfExistPreview
        * 函数描述:   搜索是否还存在有设备在进行预览
        * @return boolean   有，返回true;没有，返回false；
    */
    private boolean getIfExistPreview(){
        try{
            if (listIfPreview == null) return false;
            for (int i=0;i<listIfPreview.size();i++){
                if (listIfPreview.get(i) > -1) return true;
            }
            return false;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getIfExistPreview()","搜索是否还存在有设备在进行预览，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            return false;
        }
    }
    
    /**
        * 函数:      getNumsOfPreview
        * 函数描述:   搜索正在进行预览的窗口数量
        * @return int   
    */
    private int getNumsOfPreview(){
        try{
            if (listIfPreview == null) return 0;
            int Nums = 0;
            for (int i=0;i<listIfPreview.size();i++){
                if (listIfPreview.get(i) > -1) Nums++;
            }
            return Nums;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getNumsOfPreview()","搜索正在进行预览的窗口数量时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }

    /**
        * 函数:      PanelMousePressed
        * 函数描述:  在预览窗口鼠标按键后进行的操作
        * @para evt  鼠标事件
    */
    private void PanelMousePressed(java.awt.event.MouseEvent evt){
        try{
            
            ChannelRealPlayer channelRealPlayer2 = getCurrentChannelRealPlayer();
            if (channelRealPlayer2 == null || !channelRealPlayer2.equals(currentChannelRealPlayer)) showPTZWindow(false);//隐藏云台控制窗口
            
            //每次Panel鼠标按下的时候得到当前的ChannelRealPlayer对象
            currentChannelRealPlayer = getCurrentChannelRealPlayer();//如果当前的ChannelRealPlayer对象为空，则不弹出菜单和进行鱼球联动操作
            if(currentChannelRealPlayer == null) return;//如果当前的ChannelRealPlayer对象为空，则不做任何操作
            
            //如果已经开始鱼球联动
            if (currentChannelRealPlayer.isIfFishBallTrack() && evt.getButton() == MouseEvent.BUTTON1) {
                PanelMousePressedPosition.setLocation(evt.getX(),evt.getY());
                //开始画框
                POINT point = new POINT(evt.getX(), evt.getY());
                CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(currentChannelRealPlayer.getPreviewHandle(), fDrawFunSelecctAera, 0);
                if (point.x < 0)
                {
                    point.x = 0;
                }
                rectSelectArea.left = point.x / iPrecision * iPrecision;
                if (point.y < 0)
                {
                    point.y = 0;
                }
                rectSelectArea.top = point.y / iPrecision * iPrecision;
                rectSelectArea.right = rectSelectArea.left/ iPrecision * iPrecision +1;
                rectSelectArea.bottom = rectSelectArea.top/ iPrecision * iPrecision+1;
            }
            //如果已经开始启动云台
            if (currentChannelRealPlayer.isIfPTZCtrl()) {
                //if (zoomWorker != null &&  !(zoomWorker.isCancelled() || zoomWorker.isTimeOut())) zoomWorker.cancelAllWorker();
                PTZControlAll(currentChannelRealPlayer.getPreviewHandle(), iCommand, 0);
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PanelMousePressed()","当前预览窗口在鼠标按下时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        
    }
    /**
        * 函数:      PanelMouseDragged
        * 函数描述:  在预览窗口鼠标按键拖动的操作
        * @para evt  鼠标事件
    */
    private void PanelMouseDragged(java.awt.event.MouseEvent evt){
        try{
            if(currentChannelRealPlayer == null) return;//如果当前的ChannelRealPlayer对象为空，则不做任何操作
            //如果已经开始鱼球联动
            if (currentChannelRealPlayer.isIfFishBallTrack()) {
                //画框
                Panel rpPanel = currentChannelRealPlayer.getPanelRealplay();
                POINT point = new POINT(evt.getX(), evt.getY());
                if (point.x > rpPanel.getWidth())
                {
                    point.x = rpPanel.getWidth();
                }
                rectSelectArea.right = point.x / iPrecision * iPrecision;
                if (point.y > rpPanel.getHeight())
                {
                    point.y = rpPanel.getHeight();
                }
                rectSelectArea.bottom = point.y / iPrecision * iPrecision;
            }
            //如果已经开始启动云台
//            if (currentChannelRealPlayer.isIfPTZCtrl()) {
////                Point PanelMouseDraggedPosition = new Point(evt.getX(), evt.getY());
////                Image imageCursor = Toolkit.getDefaultToolkit().getImage("/jyms/image/upleft.png"); 
////                currentChannelRealPlayer.getPanelRealplay().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
////                   imageCursor,  PanelMouseDraggedPosition, "cursor"));
//            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PanelMouseDragged()","当前预览窗口在鼠标拖动时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      PanelMouseMoved
        * 函数描述:  在预览窗口移动鼠标按键时改变鼠标形状
        * @para evt  鼠标事件
    */
    private void PanelMouseMoved(java.awt.event.MouseEvent evt){
        try{
            if(currentChannelRealPlayer == null) return;//如果当前的ChannelRealPlayer对象为空，则不做任何操作
            Panel panel = currentChannelRealPlayer.getPanelRealplay();
            //如果已经开始启动云台
            if (currentChannelRealPlayer.isIfPTZCtrl()) {
                Point PanelMouseMovePosition = new Point(evt.getX(), evt.getY());
                
                int PanelWidth3 = panel.getWidth()/3;
                int PanelHeight3 = panel.getHeight()/3;
                //panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                int X = evt.getX();
                int Y = evt.getY();
                if (X <=PanelWidth3 && Y <=PanelHeight3) { panel.setCursor(upleftcursor);iCommand = HCNetSDK.UP_LEFT;}
                else if(X >=PanelWidth3 && X<=PanelWidth3*2 && Y <=PanelHeight3) { panel.setCursor(upcursor);iCommand = HCNetSDK.TILT_UP;}
                else if(X >= PanelWidth3*2 && Y <=PanelHeight3) { panel.setCursor(uprightcursor);iCommand = HCNetSDK.UP_RIGHT;}
                else if(X>=PanelWidth3*2 && Y >=PanelHeight3 && Y <= PanelHeight3 *2) { panel.setCursor(rightcursor);iCommand = HCNetSDK.PAN_RIGHT;}
                else if(X>=PanelWidth3*2 && Y >= PanelHeight3 *2) { panel.setCursor(downrightcursor);iCommand = HCNetSDK.DOWN_RIGHT;}
                else if(X >=PanelWidth3 && X<=PanelWidth3*2  && Y >= PanelHeight3 *2) { panel.setCursor(downcursor);iCommand = HCNetSDK.TILT_DOWN;}
                else if(X <=PanelWidth3 && Y >= PanelHeight3 *2) { panel.setCursor(downleftcursor);iCommand = HCNetSDK.DOWN_LEFT;}
                else if(X <=PanelWidth3 && Y >=PanelHeight3 && Y <= PanelHeight3 *2) { panel.setCursor(leftcursor);iCommand = HCNetSDK.PAN_LEFT;}
                else { panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));iCommand = -1;}
                
            }else {panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));iCommand = -1;}
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PanelMouseMoved()","当前预览窗口在鼠标移动时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      PanelMouseWheelMoved
        * 函数描述:  在预览窗口滚动鼠标滚轮时所进行的操作
        * @para evt  鼠标事件
    */
    private void PanelMouseWheelMoved(java.awt.event.MouseWheelEvent evt){
        try{
            if(currentChannelRealPlayer == null) return;//如果当前的ChannelRealPlayer对象为空，则不做任何操作
        //如果已经开始启动云台
            if (currentChannelRealPlayer.isIfPTZCtrl()) {
                if(evt.getWheelRotation()<0){//如果鼠标滚轮的“咔哒声”小于零则表示是向上滚动
                    /**以System.out.println("up"+(++iMouseWheelNum))函数测试过，每一次滚轮，根据滚轮时间长短，可以打印1-n次。可能每滚一次“咔”则打印一次
                     * 所以只要把握每一次放大/缩小的时间就够了。经过试验100ms是比较合适的。200ms就已经多了
                     */
//                    if (!bMouseWheelMoving){
//                        PTZControlZoomMaxSpeed(currentChannelRealPlayer.getPreviewHandle(), HCNetSDK.ZOOM_IN, 0);
//                        bMouseWheelMoving = true;
//                        Thread.sleep(200);//50毫秒之后停止放大窗口
//                        PTZControlZoomMaxSpeed(currentChannelRealPlayer.getPreviewHandle(), HCNetSDK.ZOOM_IN, 1);
//                        bMouseWheelMoving = false;
//                    }
                    /*连续滚动鼠标，则会多次触发MouseWheelMoved事件，平均间隔10几毫秒*/
//System.out.println(new Date().toString()+ "  " + System.currentTimeMillis());
                    zoomOrder(currentChannelRealPlayer.getPreviewHandle(), 300, HCNetSDK.ZOOM_IN);
//                    HCNetSDK.NET_DVR_PTZPOS strPTZPos2 = getPTZPos();
//                    if (Math.abs(strPTZPos2.wZoomPos - strPTZPos1.wZoomPos) >= 16)
                    
                }else{//否则表示向下滚动
                    //int Z1 = iZoomPos;
//                    if (!bMouseWheelMoving){
//                        PTZControlZoomMaxSpeed(currentChannelRealPlayer.getPreviewHandle(), HCNetSDK.ZOOM_OUT, 0);
//                        bMouseWheelMoving = true;
//                        Thread.sleep(200);//50毫秒之后停止放大窗口
//                        PTZControlZoomMaxSpeed(currentChannelRealPlayer.getPreviewHandle(), HCNetSDK.ZOOM_OUT, 1);
//                        bMouseWheelMoving = false;
//                    }
//System.out.println(new Date().toString()+ "  " + System.currentTimeMillis());
                    zoomOrder(currentChannelRealPlayer.getPreviewHandle(), 300, HCNetSDK.ZOOM_OUT);

                }
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PanelMouseWheelMoved()","当前预览窗口在鼠标滚轮时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      runZoomOrder
        * 函数描述:  在预览窗口滚动鼠标滚轮时所进行的操作
    */
    private void zoomOrder(NativeLong RealHandle, int Delay, int Zoom_Order){
        //如果第一次运行zoom或者zoom worker已经取消
        /*因为doInBackground运行完之后就是运行了done方法（意思就是如果任务已完成），所以不能用isDone()方法，虽然对象还没有取消*/
        if (zoomWorker == null || zoomWorker.isCancelled()) {
            zoomWorker = new PTZZoomWorker( RealHandle, Delay, Zoom_Order);
            zoomWorker.execute();//.runZoomOrder(RealHandle, Delay, Zoom_Order);
        }else if (!zoomWorker.ifSameRealHandle(RealHandle)){//如果不是同一个预览窗口，但是这种情况几乎是不可能的
            zoomWorker.cancelAllWorker();
            zoomWorker = new PTZZoomWorker( RealHandle, Delay, Zoom_Order);
            zoomWorker.execute();//.runZoomOrder(RealHandle, Delay, Zoom_Order);
        }else if(zoomWorker.ifSameRealHandle(RealHandle) && (!zoomWorker.ifSameZoomOrder(Zoom_Order))){//同一个预览窗口，而且zoom命令不同，则取消任务
            zoomWorker.cancelAllWorker();
            zoomWorker = new PTZZoomWorker( RealHandle, Delay, Zoom_Order);
            zoomWorker.execute();//.runZoomOrder(RealHandle, Delay, Zoom_Order);
        }else if(zoomWorker.ifSameRealHandle(RealHandle) && zoomWorker.ifSameZoomOrder(Zoom_Order) && (!zoomWorker.isTimeOut())){//同一个预览窗口，zoom命令相同
            zoomWorker.runNextZoomOrder();
        }else if(zoomWorker.ifSameRealHandle(RealHandle) && zoomWorker.ifSameZoomOrder(Zoom_Order) && zoomWorker.isTimeOut()){//同一个预览窗口，zoom命令相同
            
            zoomWorker.cancelAllWorker();
            zoomWorker = new PTZZoomWorker( RealHandle, Delay, Zoom_Order);
            zoomWorker.execute();//.runZoomOrder(RealHandle, Delay, Zoom_Order);
        }
        
    }
    /**
        * 函数:      PanelMouseReleased
        * 函数描述:  在预览窗口松开鼠标按键时所进行的操作
        * @para evt  鼠标事件
    */
    private void PanelMouseReleased(java.awt.event.MouseEvent evt){
        
        try{
            
            //如果当前的ChannelRealPlayer对象为空，则不弹出菜单和进行鱼球联动操作 == MouseEvent.BUTTON1
            if(currentChannelRealPlayer == null) return;
            switch(evt.getButton()){
                case MouseEvent.BUTTON1:
                    //如果已经开始启动云台
                    if (currentChannelRealPlayer.isIfPTZCtrl()) {
                        PTZControlAll(currentChannelRealPlayer.getPreviewHandle(), iCommand, 1);
                    }
                    if (evt.getClickCount() == 2){//双击˫��
                        zoomInPreviewOneChannel();//窗口放大
                    }
                    
                    //手动鱼球联动
                    runFishBallTrack(evt);
                    //如果已经开始鱼球联动
                    if (currentChannelRealPlayer.isIfFishBallTrack() && evt.getButton() == MouseEvent.BUTTON1) {
                        CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(currentChannelRealPlayer.getPreviewHandle(), null, 0);
                    }
                    
                    break;
                case MouseEvent.BUTTON3:  
                    //鼠标右键弹出菜单
                    showPopupMenu(evt);
                    
                    break;
            }

            //加上这句validate()，在弹出对话窗口后，经常引起屏幕闪烁，显示不正常、甚至死机的情况，因此把它注释掉。
            //jPanelPreviewContainer.validate();

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PanelMouseReleased()","当前预览窗口在鼠标释放时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        
    }

    /**
	 * 函数:      showPopupMenu
         * 函数描述:  设备预览通道的鼠标右键弹出菜单
         * @para evt  鼠标事件
    */
    private void showPopupMenu(java.awt.event.MouseEvent evt){
        //鼠标右键弹出菜单
        if (evt.isPopupTrigger()) {
            if (currentChannelRealPlayer.isbRealPlay()){
                jMenuItemPreview.setText( sStopPreview);//"结束预览"
            }
            //设置鱼球联动菜单
            if (currentChannelRealPlayer.isIfFishBallTrack()){
                jMenuItemFishBallTrack.setText(sStopFishBallTrack);//"停止鱼球联动"
            }else{
                jMenuItemFishBallTrack.setText(sStartFishBallTrack);//"开始鱼球联动"
            }

            //jMenuItemRecord
            if(currentChannelRealPlayer.isIfRecorded()){
                jMenuItemRecord.setText(sStopRecord);//"停止录像"
                jMenuItemRecord.setIcon(ImageIconBufferPool.getInstance().getImageIcon("menu_stoprecord.png"));
            }else{
                jMenuItemRecord.setText(sStartRecord);//"开始录像"
                jMenuItemRecord.setIcon(ImageIconBufferPool.getInstance().getImageIcon("menu_startrecord.png"));
            }

            //云台控制
            if(currentChannelRealPlayer.isIfPTZCtrl()){
                jMenuItemPTZCtrl.setText(sClosePTZCtrlWindow);//"关闭窗口云台控制"
            }else{
                jMenuItemPTZCtrl.setText(sOpenPTZCtrlWindow);//"启用窗口云台控制"
            }
            
            if(currentChannelRealPlayer.isIfOpenVoice()){
                jMenuItemOpenVoice.setText(sOpenVoice);//"打开声音"
            }else{
                jMenuItemOpenVoice.setText(sCloseVoice);//"关闭声音"
            }
            
            if (bFullScreen){
                jMenuItemFullScreen.setText(sExitFullScreen);//"退出全屏"
            }else {
                jMenuItemFullScreen.setText(sFullScreen);//"全屏"
            }
            
            jPopupMenuPreview.setPopupSize(240, 420);
            jPopupMenuPreview.show(evt.getComponent(),evt.getX(),evt.getY()); 
            jPopupMenuPreview.updateUI();//updateUI()可以让菜单不被panel挡住
        }
    }
    /**
        * 函数:      runFishBallTrack
        * 函数描述:  鼠标松开按键时实现手动鱼球联动
        * @para evt  鼠标事件
    */
    private void runFishBallTrack(java.awt.event.MouseEvent evt){
        //手动鱼球联动
        try{
            if (currentChannelRealPlayer.isIfFishBallTrack()){
                PanelMouseReleasedPosition.setLocation(evt.getX(), evt.getY());
                HCNetSDK.NET_DVR_PTZ_MANUALTRACE strPTZManualTrace = new HCNetSDK.NET_DVR_PTZ_MANUALTRACE();
                strPTZManualTrace.dwChannel = currentChannelRealPlayer.getChannel().intValue();
                strPTZManualTrace.byTrackType = 0;//跟踪类型：0- 普通跟踪，1- 高速道路跟踪，2- 城市道路跟踪 
                strPTZManualTrace.byLinkageType = 1;//byLinkageType 联动动作: 0- 手动跟踪，1- 联动不跟踪 


                strPTZManualTrace.struPoint = new HCNetSDK.NET_VCA_POINT();
                strPTZManualTrace.struPointEnd = new HCNetSDK.NET_VCA_POINT();

                NativeLong UserID = currentChannelRealPlayer.getUserID();
                if (UserID.intValue() < 0) return;

                Panel FishPanel = currentChannelRealPlayer.getPanelRealplay();
                //标定点，鱼眼，其值归一化到0-1 
                float FishWidth = (float)FishPanel.getWidth();
                float FishHight = (float)FishPanel.getHeight();
                float fX = ((float)PanelMousePressedPosition.getX()) / FishWidth;
                float fY = ((float)PanelMousePressedPosition.getY()) / FishHight;//getY()返回double精度
                strPTZManualTrace.struPoint.fX = fX;
                strPTZManualTrace.struPoint.fY = fY;

                if (PanelMouseReleasedPosition.x == PanelMousePressedPosition.x && PanelMouseReleasedPosition.y == PanelMousePressedPosition.y){
                    //点击选择

                }else{//框选
                    fX = ((float)PanelMouseReleasedPosition.getX()) / FishWidth;
                    fY = ((float)PanelMouseReleasedPosition.getY()) / FishHight;//getY()返回double精度
                    strPTZManualTrace.struPointEnd.fX = fX;
                    strPTZManualTrace.struPointEnd.fY = fY;
                }
                strPTZManualTrace.dwSize = strPTZManualTrace.size();//没想到这一步竟然很关键，不设置的话，会报参数错误。
                strPTZManualTrace.write();
                Pointer lpPTZManualTrace = strPTZManualTrace.getPointer();
                boolean SetTrackSuccess = CommonParas.hCNetSDK.NET_DVR_RemoteControl(UserID, HCNetSDK.NET_DVR_CONTROL_PTZ_MANUALTRACE,
                                                                                    lpPTZManualTrace, strPTZManualTrace.size());
                strPTZManualTrace.read();
                if (!SetTrackSuccess) {
                    TxtLogger.append(this.sFileName, "runFishBallTrack()","鱼球联动手动跟踪定位时失败："
                                 + "\r\n                       错误码:" + CommonParas.hCNetSDK.NET_DVR_GetLastError());
                }
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "runFishBallTrack()","鼠标松开按键时实现手动鱼球联动时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    
    /**
        * 函数:      PTZControlAll
        * 函数描述:  云台控制函数
        * @param   lRealHandle: 预览句柄
        * @param   iPTZCommand: PTZ控制命令
        * @param   iStop: 开始或是停止操作
        * @param   iSpeed云台控制的速度
    */
    private void PTZControlAll(NativeLong lRealHandle, int iPTZCommand, int iStop)
    {
        //int iSpeed = jSliderSpeed.getValue();//jComboBoxSpeed.getSelectedIndex();
        if (iPTZCommand < 0) return;
//        if (bPTZControl && iStop == 0) return;//bPTZControl=true说明云台正在进行其他操作，iStop = 0说明再来新的操作，上个操作没有进行完毕时，本次操作取消
        if (lRealHandle.intValue() >= 0){
//            boolean ret = CommonParas.hCNetSDK.NET_DVR_PTZControlWithSpeed(lRealHandle, iPTZCommand, iStop,1);
            boolean ret = CommonParas.hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
//            if (iStop == 0) bPTZControl = true;
//            else bPTZControl = false;
            if (!ret)
            {
                //JOptionPane.showMessageDialog(this, "云台控制失败");
                CommonParas.showMessage(this.getRootPane(), sPTZCtrlFail, sFileName); //"云台控制失败！"
            }
            //getPTZPos();
        }
    }
    /**
        * 函数:      PTZControlZoomMaxSpeed
        * 函数描述:  云台控制函数
        * @param   lRealHandle: 预览句柄
        * @param   iPTZCommand: PTZ控制命令
        * @param   iStop: 开始或是停止操作
        * @param   iSpeed云台控制的速度
    */
    private void PTZControlZoomMaxSpeed(NativeLong lRealHandle, int iPTZCommand, int iStop)
    {
        //int iSpeed = jSliderSpeed.getValue();//jComboBoxSpeed.getSelectedIndex();
        if (iPTZCommand < 0) return;
//        if (bPTZControl && iStop == 0) return;//bPTZControl=true说明云台正在进行其他操作，iStop = 0说明再来新的操作，上个操作没有进行完毕时，本次操作取消
        if (lRealHandle.intValue() >= 0){
//            boolean ret = CommonParas.hCNetSDK.NET_DVR_PTZControlWithSpeed(lRealHandle, iPTZCommand, iStop,1);
            boolean ret = CommonParas.hCNetSDK.NET_DVR_PTZControlWithSpeed(lRealHandle, iPTZCommand, iStop, 7);
//            if (iStop == 0) bPTZControl = true;
//            else bPTZControl = false;
            if (!ret)
            {
                //JOptionPane.showMessageDialog(this, "云台控制失败");
                CommonParas.showMessage(this.getRootPane(), sPTZCtrlFail, sFileName); //"云台控制失败！"
            }
            //getPTZPos();
        }
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

    private HCNetSDK.NET_DVR_PTZPOS getPTZPos(){
        try{
            if (currentChannelRealPlayer == null) return null;
            NativeLong UserID = currentChannelRealPlayer.getUserID();
            if (UserID == null || UserID.intValue() < 0) return null;
            IntByReference ibrBytesReturned = new IntByReference(0);//获取设备的配置信息。

            boolean bRet = false;
            HCNetSDK.NET_DVR_PTZPOS strPTZPos = new HCNetSDK.NET_DVR_PTZPOS();
            strPTZPos.write();
            Pointer lpStrPTZPos = strPTZPos.getPointer();
            bRet = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(UserID,HCNetSDK.NET_DVR_GET_PTZPOS,currentChannelRealPlayer.getChannel(),lpStrPTZPos,strPTZPos.size(),ibrBytesReturned);
            strPTZPos.read();
            //iZoomPos = strPTZPos.wZoomPos;
            return strPTZPos;

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getPTZPos()","系统在获得球机当前位置PTZ参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return null;
   
    }

    private void setPTZPos(HCNetSDK.NET_DVR_PTZPOS strPTZPos){
        try{
            if (currentChannelRealPlayer == null) return;
            NativeLong UserID = currentChannelRealPlayer.getUserID();
            if (UserID == null || UserID.intValue() < 0) return;
            IntByReference ibrBytesReturned = new IntByReference(0);//获取设备的配置信息。

            boolean bRet = false;
            //HCNetSDK.NET_DVR_PTZPOS strPTZPos = new HCNetSDK.NET_DVR_PTZPOS();
            strPTZPos.write();
            Pointer lpStrPTZPos = strPTZPos.getPointer();
            bRet = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(UserID,HCNetSDK.NET_DVR_SET_PTZPOS,currentChannelRealPlayer.getChannel(),lpStrPTZPos,strPTZPos.size());
            strPTZPos.read();

            //iZoomPos = strPTZPos.wZoomPos;

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setPTZPos()","系统在获得球机当前位置PTZ参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());

        }
   
    }
    //,Point PanelMouseDraggedPosition
//    private void dragPTZPos(java.awt.event.MouseEvent evt){
//        try{
//            if (!currentChannelRealPlayer.isIfPTZCtrl()) return;
//            PanelMouseReleasedPosition.setLocation(evt.getX(), evt.getY());
//            double basisTime = 150;//基准是100毫秒
//            double basisDistance = 100;//基准是100像素
//            double ax = PanelMouseReleasedPosition.x - PanelMousePressedPosition.x;
//            double ay = PanelMouseReleasedPosition.y - PanelMousePressedPosition.y;
//            double axy = Math.abs(ax - ay);
//            double a2 = Math.sqrt(2);
//            double aa =3.1415926*15/180;//转换为弧度
//            double ayy = ay - ax*Math.tan(aa);
//            NativeLong PreviewHandle = currentChannelRealPlayer.getPreviewHandle();
////            System.out.println(PanelMousePressedPosition.toString());
////            System.out.println(PanelMouseReleasedPosition.toString());
//            if (ax >=0 && ay >= 0){//针对第1种情况，鼠标往右下方拖动，而云台应该向左上方运动，才能使画面往右下方运动
//                
//                PTZControlAll(PreviewHandle, HCNetSDK.UP_LEFT, 0);
//                Thread.sleep((long) (a2*ay/basisDistance*basisTime*0.35));
////                System.out.println(a2*ay/basisDistance*basisTime*0.35);
//                PTZControlAll(PreviewHandle, HCNetSDK.UP_LEFT, 1);
//
//            }
//            else if (ax <=0 && ay <= 0){//针对第2种情况
//            }
//            else if (ax > 0 && ay < 0){//针对第3种情况
//            }
//            else if (ax < 0 && ay > 0){//针对第4种情况
//            }
//        }catch (Exception e){
//            TxtLogger.append(this.sFileName, "dragPTZPos()","当前预览窗口在鼠标滚轮时，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
//    }
    /**
	 * 函数:      refreshMenuStateByAuthority
         * 函数描述:  根据用户的权限设置菜单的状态
    */
    private void refreshMenuStateByAuthority(){
        //AUTHORITY_FISHBALL_TRACKSETUP = "鱼球联动设置";
        CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_FISHBALL_TRACKSETUP, jMenuItemSetupFishBallTrack);
        
        //鱼球联动
        CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_FISHBALL_TRACK, jMenuItemFishBallTrack);
        
        //AUTHORITY_PTZCTRL = "云台控制";
        if (!CommonParas.ifHaveUserAuthority(CommonParas.AuthorityItems.AUTHORITY_PTZCTRL, sFileName)){
            jMenuItemPTZCtrl.setEnabled(false);
            jMenuItemPTZWindow.setEnabled(false);
        }
        else{
            jMenuItemPTZCtrl.setEnabled(true);
            jMenuItemPTZWindow.setEnabled(true);
        }

        //AUTHORITY_SPEAK = "语音对讲";jMenuItemTalkback;
        CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_SPEAK, jMenuItemTalkback);
        //暂时先不用开始对讲功能
        jMenuItemTalkback.setVisible(false);
        
        //AUTHORITY_CHECKTIME = "监控点校时";jMenuItemCorrectTime;
        CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_CHECKTIME, jMenuItemCorrectTime);
        
        //将超级管理员的权限部分变为不可视
        setInvisibleItemForNonAdmin();

    }
    
    /**
        * 函数:      setInvisibleItemForNonAdmin
        * 函数描述:  将超级管理员的权限部分变为不可视
    */
    private void setInvisibleItemForNonAdmin(){
        
        boolean IfAdminVisible = CommonParas.ifAdmin();
        
        jMenuItemSetupFishBallTrack.setVisible(IfAdminVisible);
        
    }
    /**
	 * 函数:      showDVRResourceTree
         * 函数描述:  控制设备资源树的显示和隐藏
    */
    private void showDVRResourceTree(boolean IfShow){
        if (IfShow){
            jPanelLeft.setVisible(true);
            jSplitPanePreview.setDividerLocation(iDividerLocation);
            jSplitPanePreview.setDividerSize(iDividerSize);
            this.validate();//解决视频半边缺失的问题
            jButtonTreeShow.setIcon(ImageIconBufferPool.getInstance().getImageIcon("left.png"));
            bTreeShow = true;
        }else {
            //必须同时加上setDividerLocation(0)和setVisible(false)，否则隐藏或显示云台控制的时候，设备树会再次显示出来。
            iDividerLocation = jSplitPanePreview.getDividerLocation();
            jSplitPanePreview.setDividerLocation(0);
            jSplitPanePreview.setDividerSize(0);
            jPanelLeft.setVisible(false);
            this.validate();//验证此容器及其所有子组件。使用 validate 方法会使容器再次布置其子组件。已经显示容器后，在修改此容器的子组件的时候（在容器中添加或移除组件，或者更改与布局相关的信息），应该调用上述方法。 
            jButtonTreeShow.setIcon(ImageIconBufferPool.getInstance().getImageIcon("right.png"));
            bTreeShow = false;
        }
        if (bZoomIn){   
            zoomInPreviewOneChannel();zoomInPreviewOneChannel();
            //以下的语句有时候currentJPanel中的Panel尺寸也会出现问题，也需要设置，所以干脆就用上面的语句，一劳永逸。
            /*jPanelPreviewContainerComponentResized事件在拖动分割线的时候也会出现不停地zoomInPreviewOneChannel，很不方便，不好看。
                所以就不用jPanelPreviewContainerComponentResized事件。
                把其分成两部分，一部分监听分割线拖动，不停的改变其大小；一部分是按钮事件隐藏左半部分，上述代码，代替发生的Resize事件
            */
            //currentJPanel.setPreferredSize(new Dimension(jPanelPreviewContainer.getWidth() - 4*iPanelBoderThickness, jPanelPreviewContainer.getHeight()-4*iPanelBoderThickness));
        }
    }

    /**
	 * 函数:      writePreviewLog
         * 函数描述:  设备预览窗口的写日志函数
    */
    private void writePreviewLog(ChannelRealPlayer ChannelRealPlayer2, String Description){
        //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
        CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, Description, ChannelRealPlayer2.getDeviceparaBean().getSerialNO(), ChannelRealPlayer2.getGroupName(), 
                                ChannelRealPlayer2.getEncodingDVRChannelNode(), ChannelRealPlayer2.getSerialNoJoin(),"",CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE,sFileName);
    }
    
    
    
/*--------------------------云台操作专用函数 开始-----------------------*/
    
    /**
	 * 函数:      showPTZWindow
         * 函数描述:  控制PTZ控制Panel的显示和隐藏
    */
    private void showPTZWindow(boolean IfShow){
        if (IfShow){
           
            channelRPBall = getCurrentChannelRealPlayer();
            if (channelRPBall == null) return;
            
            m_lRealHandle = channelRPBall.getPreviewHandle();
            if (m_lRealHandle.intValue() == -1) return;
            
            presetTableModel.getDataVector().clear();//预置点信息
            presetTableModel.fireTableDataChanged();
            
            bAutoComboBoxSelect = true;//是否系统读取云台参数过程中自动选择JComboBox，因为编码选择也是触发了ItemStateChanged事件
            jComboBoxCruise.removeAllItems();//删除所有的巡航路径
            bAutoComboBoxSelect = false;
            cruisePointTableModel.getDataVector().clear();//jTableCruise巡航点信息 
            cruisePointTableModel.fireTableDataChanged();

            jComboBoxTrack.removeAllItems();//删除所有的云台轨迹
            
            refreshPTZStrCfgAbility();//刷新所有的预置点、巡航路径、云台轨迹信息
            if (jComboBoxCruise.getItemCount() > 0) 
                refreshCruiseTableData(1);

            
            jPanelEast.setVisible(true);
            jButtonPTZShow.setIcon(ImageIconBufferPool.getInstance().getImageIcon("right.png"));
            bOpenPTZ = true;
        }else {
            jPanelEast.setVisible(false);
            jButtonPTZShow.setIcon(ImageIconBufferPool.getInstance().getImageIcon("left.png"));
            bOpenPTZ = false;
        }
        if (bZoomIn){   
            zoomInPreviewOneChannel();zoomInPreviewOneChannel();
            //以下的语句有时候currentJPanel中的Panel尺寸也会出现问题，也需要设置，所以干脆就用上面的语句，一劳永逸。
            /*jPanelPreviewContainerComponentResized事件在拖动分割线的时候也会出现不停地zoomInPreviewOneChannel，很不方便，不好看。
                所以就不用jPanelPreviewContainerComponentResized事件。
                把其分成两部分，一部分监听分割线拖动，不停的改变其大小；一部分是按钮事件隐藏左半部分，上述代码，代替发生的Resize事件
            */
            //currentJPanel.setPreferredSize(new Dimension(jPanelPreviewContainer.getWidth() - 4*iPanelBoderThickness, jPanelPreviewContainer.getHeight()-4*iPanelBoderThickness));
        }
    }
    
    /**
	 * 函数:      writePTZControlLog
         * 函数描述:  PTZ控制窗口的写日志函数
    */
    private void writePTZControlLog( String Remarks){
        //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、备注、调用的文件名
        CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, sPTZCtrl, channelRPBall.getDeviceparaBean().getSerialNO(), channelRPBall.getGroupName(), 
                                channelRPBall.getEncodingDVRChannelNode(), channelRPBall.getSerialNoJoin(), "", CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,
                                CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE, Remarks, sFileName);//"云台控制"
    }
    
    /**
        * 函数:      writePTZControlErrorLog
        * 函数描述:  PTZ控制窗口的写错误日志函数
        * @param Remarks
    */
    public void writePTZControlErrorLog(String Remarks){
        //写错误日志
        //操作时间、设备别名、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、错误细节描述、调用的文件名
        CommonParas.SystemWriteErrorLog("", channelRPBall.getDeviceparaBean().getAnothername(), sPTZCtrlFail, channelRPBall.getDeviceparaBean().getSerialNO(),  channelRPBall.getGroupName(),
                                channelRPBall.getEncodingDVRChannelNode(), channelRPBall.getSerialNoJoin(), "", CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,
                                CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE, Remarks, sFileName);//"云台控制失败"
        CommonParas.showErrorMessage(Remarks, channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
    }

    /**
	 * 函数:      setTableColWidth
         * 函数描述:  设置表格特殊列的宽度
    */
    private void setTableColWidth(){
        //"ID0", "报警时间1"
        TableColumnModel tcmPreset = jTablePreset.getColumnModel();
        tcmPreset.getColumn(0).setMinWidth(40);
        tcmPreset.getColumn(0).setMaxWidth(40);
        tcmPreset.getColumn(0).setPreferredWidth(40);
        
        TableColumnModel tcmCruise = jTableCruise.getColumnModel();
        tcmCruise.getColumn(0).setMinWidth(40);
        tcmCruise.getColumn(0).setMaxWidth(40);
        tcmCruise.getColumn(0).setPreferredWidth(40);
        
        tcmCruise.getColumn(2).setMinWidth(70);
        tcmCruise.getColumn(2).setMaxWidth(70);
        tcmCruise.getColumn(2).setPreferredWidth(70);
//        tcmCruise.getColumn(3).setMinWidth(40);
//        tcmCruise.getColumn(3).setMaxWidth(40);
//        tcmCruise.getColumn(3).setPreferredWidth(40);
    }
    
    /**
	 * 函数:      setViewTableColWidth
         * 函数描述:  设置表格特殊列的宽度
    */
    private void setViewTableColWidth(){
        //"ID0", "报警时间1"
        TableColumnModel tcmPreset = jTableSelectView.getColumnModel();
        tcmPreset.getColumn(0).setMinWidth(40);
        tcmPreset.getColumn(0).setMaxWidth(40);
        tcmPreset.getColumn(0).setPreferredWidth(40);
      }
    /**
	 * 函数:      modifyPresetName
         * 函数描述:  修改预置点名称
    */
    private void modifyPresetName(int Row, int Col, String OldPresetName, String NewPresetName){
        try{
            NativeLong UserID = channelRPBall.getUserID();
            int Channel = 1;
            if (UserID.intValue() < 0) return;
            
            //获取设备编码类型
            String CharEncodeType =CommonParas.getCharEncodeType(channelRPBall.getIndexOflistDevice(), sFileName);

            HCNetSDK.NET_DVR_PRESET_NAME StruPresetName = new HCNetSDK.NET_DVR_PRESET_NAME();
            
            
            StruPresetName.wPresetNum = (short) (Row + 1);
            //复制指定的数组，截取或用 0 填充（如有必要），以使副本具有指定的长度。系统会自动将长度减少到HCNetSDK.NAME_LEN
            StruPresetName.byName = Arrays.copyOf(NewPresetName.getBytes(CharEncodeType), HCNetSDK.NAME_LEN);//NewPresetName.getBytes();
            StruPresetName.dwSize = StruPresetName.size();
            //String sOperateTime = (String)presetTableModel.getValueAt(Row, 1);//取得预置点名称

            StruPresetName.write();
            Pointer lpPresetName = StruPresetName.getPointer();
            boolean setPresetName = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(UserID, HCNetSDK.NET_DVR_SET_PRESET_NAME,
                    new NativeLong(Channel), lpPresetName, StruPresetName.size());
            StruPresetName.read();
            if (setPresetName){
                writePTZControlLog(MessageFormat.format(sModifyPresetName, OldPresetName, NewPresetName));//"修改预置点["+OldPresetName+"]名称为：["+NewPresetName+"]"
                CommonParas.showMessage(MessageFormat.format(sModifyPresetName, OldPresetName, NewPresetName), sFileName);//"修改预置点["+OldPresetName+"]名称为：["+NewPresetName+"]"
            }else{
                CommonParas.showErrorMessage( sModifyPresetFail, channelRPBall.getDeviceparaBean().getAnothername(), sFileName);//"修改预置点名称失败！"
            }
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "modifyPresetName()","系统在修改预置点名称过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      refreshPTZStrCfgAbility
         * 函数描述:  刷新云台巡航、轨迹、预置点信息
    */
        private void refreshPTZStrCfgAbility(){
        try{

            if (channelRPBall == null) return;
            int Index = channelRPBall.getIndexOflistDevice();
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo = CommonParas.getStrDeviceInfo(Index, sFileName);
            HCNetSDK.NET_DVR_IPPARACFG StrIpparaCfg = CommonParas.getStrIpparacfg(Index, sFileName);
            if (!HCNetSDKExpand.isHavePTZAbility(StrDevInfo, StrIpparaCfg, channelRPBall.getChannel().intValue(), sFileName)) {
                //jTabbedPanePTZ.setEnabled(false);
                CommonParas.setJPanelAllOCXEnable(jPanelPreset, false);
                CommonParas.setJPanelAllOCXEnable(jPanelCruise, false);
                CommonParas.setJPanelAllOCXEnable(jPanelTrack, false);
                jTablePreset.setEnabled(false);
                jTableCruise.setEnabled(false);
               
                return;//如果无云台，则不做任何操作
            }
        
            NativeLong UserID = new NativeLong(-1);
            if(StrIpparaCfg != null) {
                int IndexJoin = CommonParas.getIndexJoinOfDeviceList(StrDevInfo, StrIpparaCfg, channelRPBall.getChannel().intValue(), sFileName);
                UserID  = CommonParas.getUserID(IndexJoin, sFileName);
            }else UserID = channelRPBall.getUserID();
            //sAnotherNameJoin = CommonParas.getAnotherNameJoinFromNode(sChannelNode);

            IntByReference ibrBytesReturned = new IntByReference(0);//获取设备的配置信息。
            int Channel = 1;//通道号
            boolean bRet = false;
            HCNetSDK.NET_DVR_PRESET_NAME320 strPreSetName = new HCNetSDK.NET_DVR_PRESET_NAME320();
            strPreSetName.write();
            Pointer lpStrPreSetName = strPreSetName.getPointer();
            //此时iChannel表示报警输入/输出口号，从0开始（而通道号从1开始。球机只有一个通道）
            bRet = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(UserID, HCNetSDK.NET_DVR_GET_PRESET_NAME, new NativeLong(Channel), lpStrPreSetName, strPreSetName.size(), ibrBytesReturned);
            strPreSetName.read();

            if (!bRet){
                //CommonParas.showErrorMessage(this.getRootPane(), "获取预置点信息失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                CommonParas.showErrorMessage(sGetPresetFail, channelRPBall.getDeviceparaBean().getAnothername(), sFileName);//"获取预置点信息失败!"
                //写错误日志
                CommonParas.SystemWriteErrorLog( sGetPresetFail,  channelRPBall.getDeviceparaBean().getAnothername(), sFileName);//"获取预置点信息失败"
                return;
            }
            //else JOptionPane.showMessageDialog(this, "获取云台巡航、轨迹、预置点信息成功！");
            
            //获取设备编码类型
            String CharEncodeType =CommonParas.getCharEncodeType(channelRPBall.getIndexOflistDevice(), sFileName);
            
            
            for (int i=0;i<strPreSetName.strPreSetName320.length;i++){
                if (strPreSetName.strPreSetName320[i].dwSize > 0){
                    //添加预置点信息
                    //jComboBoxPreset.addItem("" + strPreSetName.strPreSetName320[i].wPresetNum + new String(strPreSetName.strPreSetName320[i].byName).trim());
                    Vector NewRow = new Vector();
                    NewRow.add(strPreSetName.strPreSetName320[i].wPresetNum);
                    //System.out.println(CommonParas.getEncoding((new String(strPreSetName.strPreSetName320[i].byName)).trim()));
                    
                    //只要将获取的字节数组，用设备的字符编码类型生成字符串就可以正确显示
                    String NewPresetName = new String(strPreSetName.strPreSetName320[i].byName, CharEncodeType).trim();

                    NewRow.add(NewPresetName);
                    //NewRow.add((new String(strPreSetName.strPreSetName320[i].byName)).trim());//出现乱码
                    presetTableModel.addRow(NewRow);
                }
            }
            presetTableModel.fireTableDataChanged();
            
            
            //如果有预置点信息，将预置点列表Tab设置为可用
            if (presetTableModel.getRowCount() > 0){
                CommonParas.setJPanelAllOCXEnable(jPanelPreset, true);
                jTablePreset.setEnabled(true);
            }
            
            //获得巡航路径最大值和云台轨迹最大值
            String pInBuf = "<PTZAbility><channelNO>1</channelNO></PTZAbility>";
            byte[] pOutBuf = new byte[64*1024];//128k应该够用了
            bRet = CommonParas.hCNetSDK.NET_DVR_GetDeviceAbility( UserID, HCNetSDK.DEVICE_ABILITY_INFO, pInBuf, pInBuf.length(), pOutBuf, 64*1024);
            
            if (!bRet)
            {
                //CommonParas.showErrorMessage(this.getRootPane(), "获取设备能力集失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                CommonParas.showErrorMessage(sGetPTZStrCfgAbilityFail, channelRPBall.getDeviceparaBean().getAnothername(), sFileName);//"获取设备能力集失败。"
                //写错误日志
                CommonParas.SystemWriteErrorLog( sGetPTZStrCfgAbilityFail,  channelRPBall.getDeviceparaBean().getAnothername(), sFileName);//"获取设备能力集失败"
                return;
            }

            String XMLOut = new String(pOutBuf).trim();
            DomXML domXML = new DomXML(XMLOut,sFileName);
            //Document doc = DomXML.readXMLString(XMLOut);
            //获得巡航路径最大值
            String ReturnV = domXML.readSecondLevelAttributeValue("Patrol", "patrolNum", "max");

            if (!(ReturnV.equals(""))) {
                int MaxPatrolNum = Integer.parseInt(ReturnV);
                jComboBoxCruise.removeAllItems();
                String[] CruisePoints = new String[MaxPatrolNum];
                for (int i= 1;i <= MaxPatrolNum;i++){
                    CruisePoints[i - 1] = sPreCruise + i;//"路径"
                    //jComboBoxCruise.addItem(CommonParas.makeObj("路径" + i));
                }
                jComboBoxCruise.setModel(new DefaultComboBoxModel(CruisePoints));
            }
            
            //单条巡航扫描的最大预置点个数、巡航点最大停顿时间、巡航点默认停顿时间、最大巡航速度
            //单条巡航扫描的最大预置点个数
            ReturnV = domXML.readSecondLevelAttributeValue("Patrol", "presetNum", "max");
            if (!(ReturnV.equals(""))) {
                iCruisePointNumMax = Integer.parseInt(ReturnV);
            }
            //巡航点最大停顿时间
            ReturnV = domXML.readSecondLevelAttributeValue("Patrol", "dwellTime", "max");
            if (!(ReturnV.equals(""))) {
                iDwellTimeMax = Integer.parseInt(ReturnV);
            }
            //巡航点默认停顿时间
            ReturnV = domXML.readSecondLevelAttributeValue("Patrol", "dwellTime", "default");
            if (!(ReturnV.equals(""))) {
                iDwellTimeDefault = Integer.parseInt(ReturnV);
            }
            //最大巡航速度
            ReturnV = domXML.readSecondLevelAttributeValue("Patrol", "speed", "max");
            if (!(ReturnV.equals(""))) {
                iCruiseSpeedMax = Integer.parseInt(ReturnV);
            }
            
            
            //获得云台轨迹最大值
            ReturnV = domXML.readSecondLevelAttributeValue( "Pattern", "patternID", "max");
            if (!(ReturnV.equals(""))) {
                int MaxPatternID = Integer.parseInt(ReturnV);
                jComboBoxTrack.removeAllItems();
                for (int i= 1;i <= MaxPatternID;i++){
                    
                    jComboBoxTrack.addItem(sPreTrack + i);//"轨迹"
                }
            }
            //获得预置点名称长度和预置点中的特殊号
            ReturnV = domXML.readSecondLevelAttributeValue( "Preset", "nameLen", "max");
            if (!(ReturnV.equals(""))) {
                maxLengthOfPresetName = Integer.parseInt(ReturnV);
            }
            ReturnV = domXML.readSecondLevelAttributeValue( "Preset", "specialNo", "opt");
            if (!(ReturnV.equals(""))) {
                specialNoOfPreset = ReturnV.split(",");
            }
            //如果有巡航路径信息，将巡航路径面板设置为可用
            if (jComboBoxCruise.getItemCount() > 0) CommonParas.setJPanelAllOCXEnable(jPanelCruise, true);
            //如果有云台轨迹信息，将云台轨迹面板设置为可用
            if (jComboBoxTrack.getItemCount() > 0)  CommonParas.setJPanelAllOCXEnable(jPanelTrack, true);
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshPTZStrCfgAbility()","系统在刷新云台巡航、轨迹、预置点信息过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }

    }
    
    private boolean ifSpecialNo(int Row){
        String sRow = Integer.toString(Row);
        for (String specialNoOfPreset1 : specialNoOfPreset) {
            if (specialNoOfPreset1.equals(sRow)) return true;
        }
        return false;
    }
    /**
	 * 函数:      refreshCruiseTableData
         * 函数描述:  刷新巡航窗口某一条巡航路径的巡航点信息
    */
    private void refreshCruiseTableData(int CruiseRoute){
        try{
                //NativeLong CruiseRoute = new NativeLong(jComboBoxCruise.getSelectedIndex() + 1);
                if (CruiseRoute >= jComboBoxCruise.getItemCount() || CruiseRoute < 0) return;
                if (jComboBoxCruise.getSelectedIndex() < 0) return;
                HCNetSDK.NET_DVR_CRUISE_RET StrCruiseRet = new HCNetSDK.NET_DVR_CRUISE_RET();
                boolean GetPTZCruise = CommonParas.hCNetSDK.NET_DVR_GetPTZCruise(channelRPBall.getUserID(), channelRPBall.getChannel(), new NativeLong(CruiseRoute), StrCruiseRet);
                if (!GetPTZCruise) {
                    //CommonParas.showErrorMessage( "调用巡航路径 '"+CruiseRoute+"' 失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                    return;
                }
                cruisePointTableModel.getDataVector().clear();
                for (int i=0;i<StrCruiseRet.struCruisePoint.length;i++){
                    //"ID", "巡航点","巡航时间", "巡航速度"
                    int PresetNum = StrCruiseRet.struCruisePoint[i].PresetNum;
                    if (PresetNum > 0) {
                        Vector NewRow = new Vector();
                        NewRow.add(i + 1);
                        NewRow.add(presetTableModel.getValueAt(PresetNum - 1, 1));
                        NewRow.add(""+StrCruiseRet.struCruisePoint[i].Dwell +"s  " + StrCruiseRet.struCruisePoint[i].Speed);
                        //NewRow.add(StrCruiseRet.struCruisePoint[i].Speed);
                        cruisePointTableModel.addRow(NewRow);
                    }
                }
                cruisePointTableModel.fireTableDataChanged();
                //如果有云台巡航点信息，将巡航点列表b设置为可用
                if (cruisePointTableModel.getRowCount() > 0) jTableCruise.setEnabled(true);

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshCruiseTableData()","系统在调用巡航路径的过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }
    }
    
    /**
	 * 函数:      refreshCruisePointData
         * 函数描述:  刷新巡航点窗口参数（添加和编辑巡航点）
    */
    private void refreshCruisePointData(){
        jComboBoxCruisePoint.removeAllItems();//删除所有的巡航点
        
        if (iAddOrUpdate == 1){//添加巡航点
            //添加巡航点信息到下拉列表框jComboBoxCruisePoint中
            for (int i=1;i<=HCNetSDK.CRUISE_MAX_PRESET_NUMS;i++){
                jComboBoxCruisePoint.addItem(""+i);
            }
            for (int i=0;i<cruisePointTableModel.getRowCount();i++){
                String CruisePointNum = ""+ cruisePointTableModel.getValueAt(i, 0);
                jComboBoxCruisePoint.removeItem(CruisePointNum);
            }
            jComboBoxCruisePoint.setEnabled(true);
        }else jComboBoxCruisePoint.setEnabled(false);
        
        
        int Count  = presetTableModel.getRowCount();
        if (Count < 1 || iDwellTimeMax < 1 || iCruiseSpeedMax < 1) return;
        
        //String[] CruisePoints = new String[Count];
        for (int i=0;i<Count;i++){
            //CruisePoints[i] = (String)presetTableModel.getValueAt(i, 1);
            jComboBoxPreset.addItem((String)presetTableModel.getValueAt(i, 1));
        }
        //jComboBoxPreset.setModel(new DefaultComboBoxModel(CruisePoints));
        
        for (int i=0;i<iDwellTimeMax;i++){
            jComboBoxDwell.addItem(""+ (i+1));
        }
        if (iDwellTimeDefault > 0) jComboBoxDwell.setSelectedIndex(iDwellTimeDefault - 1);
        
        for (int i=0;i<iCruiseSpeedMax;i++){
            jComboBoxCruiseSpeed.addItem(""+(i+1));
        }
        
        
    }
    
    /**
        * 函数:      getCBPoint
        * 函数描述:  获取鱼球联动标定点
        * @param CruiseRoute    巡航路径号
        * @param CruisePoint    巡航点号
        * @param PresetNum  预置点号
        * @param SeqDwell   巡航时间
        * @param SeqSpeed   巡航速度
        * @return boolean 操作成功返回true，否则false
    */
    private boolean addCruisePoint(int CruiseRoute, int CruisePoint, int PresetNum, int SeqDwell, int SeqSpeed){
        try{
            //添加预置点
            boolean bRet = CommonParas.hCNetSDK.NET_DVR_PTZCruise(m_lRealHandle, HCNetSDK.FILL_PRE_SEQ, (byte) CruiseRoute, (byte) CruisePoint, (short) PresetNum);
            if (bRet)
            {
                bRet = CommonParas.hCNetSDK.NET_DVR_PTZCruise(m_lRealHandle, HCNetSDK.SET_SEQ_DWELL, (byte) CruiseRoute, (byte) CruisePoint, (short) SeqDwell);
                if (bRet)
                { 
                    bRet = CommonParas.hCNetSDK.NET_DVR_PTZCruise(m_lRealHandle, HCNetSDK.SET_SEQ_SPEED, (byte) CruiseRoute, (byte) CruisePoint, (short) SeqSpeed);
                }
            }
            return bRet;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "addCruisePoint()","系统在添加巡航点的过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    
    /**
	 * 函数:      delCruisePoint
         * 函数描述:  删除巡航路径中的巡航点
    */
    private boolean delCruisePoint(int CruiseRoute,int CruisePointNO){
        int RowOfCruisePointTable = jTableCruise.getSelectedRow();
//        
//        int CruisePointNum = (int)cruisePointTableModel.getValueAt(RowOfCruisePointTable, 0);//巡航点号
//        int CruiseRoute = jComboBoxCruise.getSelectedIndex() + 1;//巡航路径号
        boolean bRet = false;
        if (CruisePointNO > 0 && RowOfCruisePointTable >= 0){
            int PresetNum = getPresetNum((String)cruisePointTableModel.getValueAt(RowOfCruisePointTable - 1, 1));//获得对应的预置点号
            bRet = CommonParas.hCNetSDK.NET_DVR_PTZCruise(m_lRealHandle, HCNetSDK.CLE_PRE_SEQ, (byte) CruiseRoute, (byte) CruisePointNO, (short) PresetNum);
//            if (bRet) {
////                cruisePointTableModel.removeRow(CruisePointNum-1);
////                cruisePointTableModel.fireTableDataChanged();
//            }else{
//                CommonParas.showErrorMessage(this.getRootPane(), "删除巡航点失败！", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
//            }
        }
        return  bRet;
    }
    
    /**
        * 函数:      getPresetNum
        * 函数描述:  根据预置点名称获得对应的预置点号
        * @param PresetName 预置点名称
        * @return int 预置点号(索引)
    */
    private int getPresetNum(String PresetName){
        for (int i=0;i<presetTableModel.getRowCount();i++){
            String Value1 = (String)presetTableModel.getValueAt(i, 1);
            if (Value1.equals(PresetName)) return i + 1;
        }
        return 0;
    }
    
    /**
	 * 函数:      PTZWindowControlAll
         * 函数描述:  云台控制函数
         * @param lRealHandle: 预览句柄
         * @param iPTZCommand: PTZ控制命令
         * @param iStop: 开始或是停止操作
    */
    private void PTZWindowControlAll(NativeLong lRealHandle, int iPTZCommand, int iStop)
    {
        try{
            int iSpeed = jSliderSpeed.getValue();//int iSpeed = jComboBoxSpeed.getSelectedIndex();
            if (lRealHandle.intValue() >= 0){

                boolean ret;
                if (iSpeed >= 1){//有速度的ptz
                
                    ret = CommonParas.hCNetSDK.NET_DVR_PTZControlWithSpeed(lRealHandle, iPTZCommand, iStop, iSpeed);

                    if (!ret){
                        //JOptionPane.showMessageDialog(this, "云台控制失败");
                        writePTZControlErrorLog(getCommonMeaning(iPTZCommand));
                        //return;
                    }else{
                        //只有在鼠标释放时，也就是操作结束时才进行写日志操作
                        if(iStop == 1) 
                            writePTZControlLog(getCommonMeaning(iPTZCommand));
                    }


                } else{//速度为默认时
                
                    ret = CommonParas.hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
                    if (!ret){
                        //JOptionPane.showMessageDialog(this, "云台控制失败");
                        writePTZControlErrorLog(getCommonMeaning(iPTZCommand));
                        //return;
                    }else{
                        //只有在鼠标释放时，也就是操作结束时才进行写日志操作
                        if(iStop == 1) writePTZControlLog(getCommonMeaning(iPTZCommand));
                    }

                }
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PTZWindowControlAll()","系统在云台控制过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    
    /**
	 * 函数:      PTZPatternControl
         * 函数描述:  云台轨迹操作（花样扫描）
         * @return int 成功，返回1；失败返回0；其他问题，返回-1
    */
    private int PTZPatternControl(int ControlCommand){
        try{
            int PatternID = jComboBoxTrack.getSelectedIndex() + 1;
            if (PatternID < 1 ) return -1;

            HCNetSDK.NET_DVR_PTZ_PATTERN StruPTZPattern = new HCNetSDK.NET_DVR_PTZ_PATTERN();

            StruPTZPattern.dwSize = StruPTZPattern.size();//没想到这一步竟然很关键，不设置的话，会报参数错误。
            StruPTZPattern.dwChannel = channelRPBall.getChannel().intValue();//通道号
            StruPTZPattern.dwPatternID = PatternID;                         //云台轨迹ID
            StruPTZPattern.dwPatternCmd = ControlCommand;//开始记录轨迹
            StruPTZPattern.write();
            Pointer lpPTZPattern = StruPTZPattern.getPointer();
            boolean ControlSucc = CommonParas.hCNetSDK.NET_DVR_RemoteControl(channelRPBall.getUserID(), HCNetSDK.NET_DVR_CONTROL_PTZ_PATTERN,
                                                                                lpPTZPattern, StruPTZPattern.size());
            StruPTZPattern.read();
            return ControlSucc?1:0;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PTZPatternControl()","系统在设置球机当前位置表示的三个标签的值过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }
    
//    /**
//	 * 函数:      PTZControlAllSwitch
//         * 函数描述:  云台控制函数，控制云台的开关操作
//         * @param lRealHandle: 预览句柄
//         * @param iPTZCommand: PTZ控制命令
//         * @param iStop: 开始或是停止操作
//    */
//    private boolean PTZControlAllSwitch(NativeLong lRealHandle, int iPTZCommand, int iStop){
//        try{
//            String OnOff = iStop==0?"开启":"关闭";
//            boolean ret = CommonParas.hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
//            if (!ret){
//                writePTZControlErrorLog(OnOff + getCommonMeaning(iPTZCommand));
//            }else{
//                writePTZControlLog(OnOff + getCommonMeaning(iPTZCommand));
//            }
//            return ret;
//        }catch (Exception e){
//            TxtLogger.append(this.sFileName, "PTZControlAllSwitch()","系统在云台控制过程中，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
//        return false;
//    }
    
    /**
	 * 函数:      changeSwitchState
         * 函数描述:  控制云台的开关操作之后，改变按钮及相关参数状态
         * @param Switch: MyBoolean对象，因为boolean参数是值传递，创建简单对象，达到引用传递。
         * @param Command: PTZ控制命令
         * @param SwitchButton: 按钮
         * @param ONIcon: 按钮“开关”打开，显示按钮图标
         * @param OFFIcon: 按钮“开关”关闭，显示按钮图标
    */
    private void changeSwitchState(MyBoolean Switch, int Command, JButton SwitchButton, String ONIcon, String OFFIcon, String ONText, String OFFText){
        if (!Switch.isIfON()){
            if (CommonParas.hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, Command, 0)){//0－开始
                //改用图标方式，不用文字方式了
                writePTZControlLog(OFFText);
                SwitchButton.setToolTipText(ONText);
                SwitchButton.setIcon(ImageIconBufferPool.getInstance().getImageIcon(ONIcon));
                Switch.setIfON(true); 
            }else{
                writePTZControlErrorLog(OFFText);
            }
        } else{
            if (CommonParas.hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, Command, 1)){//1－停止 
                writePTZControlLog(ONText);
                SwitchButton.setToolTipText(OFFText);
                SwitchButton.setIcon(ImageIconBufferPool.getInstance().getImageIcon(OFFIcon));
                Switch.setIfON(false); 
            }else{
                writePTZControlErrorLog(ONText);
            }
        }
    }
    /**
	 * 函数:      getCommonMeaning
         * 函数描述:  获得PTZ命令的含义
    */
    private String getCommonMeaning(int Command){
        String ss = hmCommandMeanings.get(Command);
        return ss==null?"":ss;
//        int Index = -1;
//        try{
//            for (int i=0;i<iCommands.length;i++){
//                if (iCommands[i] == Command) {Index = i;break;}
//            }
//            if (Index == -1 || Index>=sCommandMeanings.length) return "";
//            return sCommandMeanings[Index];
//        }catch (Exception e){
//            TxtLogger.append(this.sFileName, "getCommonMeaning()","系统在获得PTZ控制命令含义的过程中，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
//        return "";
    }
    
    private void intialCommonMeaning(){
        hmCommandMeanings.put(2,sCommandMeanings[0]);//"灯光电源"
        hmCommandMeanings.put(3,sCommandMeanings[1]);//"雨刷开关"
        hmCommandMeanings.put(4,sCommandMeanings[2]);//"风扇开关"
        hmCommandMeanings.put(5,sCommandMeanings[3]);//"加热器开关"
        hmCommandMeanings.put(6,sCommandMeanings[4]);//"辅助设备开关"
        hmCommandMeanings.put(7,sCommandMeanings[5]);//"辅助设备开关"
        hmCommandMeanings.put(8,sCommandMeanings[6]);//"设置预置点"
        hmCommandMeanings.put(9,sCommandMeanings[7]);//"清除预置点"
        hmCommandMeanings.put(11,sCommandMeanings[8]);//"焦距变大(倍率变大)"
        hmCommandMeanings.put(12,sCommandMeanings[9]);//"焦距变小(倍率变小)"
        hmCommandMeanings.put(13,sCommandMeanings[10]);//"焦点前调"
        hmCommandMeanings.put(14,sCommandMeanings[11]);//"焦点后调"
        hmCommandMeanings.put(15,sCommandMeanings[12]);//"光圈扩大"
        hmCommandMeanings.put(16,sCommandMeanings[13]);//"光圈缩小"
        hmCommandMeanings.put(21,sCommandMeanings[14]);//"云台上仰"
        hmCommandMeanings.put(22,sCommandMeanings[15]);//"云台下俯"
        hmCommandMeanings.put(23,sCommandMeanings[16]);//"云台左转"
        hmCommandMeanings.put(24,sCommandMeanings[17]);//"云台右转"
        hmCommandMeanings.put(25,sCommandMeanings[18]);//"云台上仰和左转"
        hmCommandMeanings.put(26,sCommandMeanings[19]);//"云台上仰和右转"
        hmCommandMeanings.put(27,sCommandMeanings[20]);//"云台下俯和左转"
        hmCommandMeanings.put(28,sCommandMeanings[21]);//"云台下俯和右转"
        hmCommandMeanings.put(29,sCommandMeanings[22]);//"云台左右自动扫描"
        hmCommandMeanings.put(30,sCommandMeanings[23]);//"将预置点加入巡航序列"
        hmCommandMeanings.put(31,sCommandMeanings[24]);//"设置巡航点停顿时间"
        hmCommandMeanings.put(32,sCommandMeanings[25]);//"设置巡航速度"
        hmCommandMeanings.put(33,sCommandMeanings[26]);// "将预置点从巡航序列中删除"
        hmCommandMeanings.put(34,sCommandMeanings[27]);//"开始记录轨迹"
        hmCommandMeanings.put(35,sCommandMeanings[28]);//"停止记录轨迹"
        hmCommandMeanings.put(36,sCommandMeanings[29]);//"开始轨迹"
        hmCommandMeanings.put(37,sCommandMeanings[30]);//"开始巡航"
        hmCommandMeanings.put(38,sCommandMeanings[31]);//"停止巡航"
        hmCommandMeanings.put(39,sCommandMeanings[32]);//"转到预置点"
        hmCommandMeanings.put(58,sCommandMeanings[33]);//"云台下俯和焦距变大(倍率变大)"
        hmCommandMeanings.put(59,sCommandMeanings[34]);// "云台下俯和焦距变小(倍率变小)"
        hmCommandMeanings.put(60,sCommandMeanings[35]);//"云台左转和焦距变大(倍率变大)"
        hmCommandMeanings.put(61,sCommandMeanings[36]);//"云台左转和焦距变小(倍率变小)"
        hmCommandMeanings.put(62,sCommandMeanings[37]);//"云台右转和焦距变大(倍率变大)"
        hmCommandMeanings.put(63,sCommandMeanings[38]);// "云台右转和焦距变小(倍率变小)"
        hmCommandMeanings.put(64,sCommandMeanings[39]);//"云台上仰和左转和焦距变大(倍率变大)"
        hmCommandMeanings.put(65,sCommandMeanings[40]);//"云台上仰和左转和焦距变小(倍率变小)"
        hmCommandMeanings.put(66,sCommandMeanings[41]);//"云台上仰和右转和焦距变大(倍率变大)"
        hmCommandMeanings.put(67,sCommandMeanings[42]);//"云台上仰和右转和焦距变小(倍率变小)"
        hmCommandMeanings.put(68,sCommandMeanings[43]);//"云台下俯和左转和焦距变大(倍率变大)"
        hmCommandMeanings.put(69,sCommandMeanings[44]);//"云台下俯和左转和焦距变小(倍率变小)"
        hmCommandMeanings.put(70,sCommandMeanings[45]);//"云台下俯和右转和焦距变大(倍率变大)"
        hmCommandMeanings.put(71,sCommandMeanings[46]);//"云台下俯和右转和焦距变小(倍率变小)"
        hmCommandMeanings.put(72,sCommandMeanings[47]);//"云台上仰和焦距变大(倍率变大)"
        hmCommandMeanings.put(73,sCommandMeanings[48]);//"云台上仰和焦距变小(倍率变小)"
        hmCommandMeanings.put(3313,sCommandMeanings[49]);//"快球云台花样扫描"

    }
    
    private void modifyTrackJButtonState(JButton TrackJButton){
        if (TrackJButton == null){
            jComboBoxTrack.setEnabled(true);
            jButtonMemCruise.setEnabled(true);
            jButtonRunCruise.setEnabled(true);
            jButtonDeleteCruise.setEnabled(true);
            jButtonDeleteAllCruise.setEnabled(true);
        }
        else if (TrackJButton.equals(jButtonMemCruise)){
            jComboBoxTrack.setEnabled(false);
            jButtonMemCruise.setEnabled(true);
            jButtonRunCruise.setEnabled(false);
            jButtonDeleteCruise.setEnabled(false);
            jButtonDeleteAllCruise.setEnabled(false);
        }else if (TrackJButton.equals(jButtonRunCruise)){
            jComboBoxTrack.setEnabled(false);
            jButtonMemCruise.setEnabled(false);
            jButtonRunCruise.setEnabled(true);
            jButtonDeleteCruise.setEnabled(false);
            jButtonDeleteAllCruise.setEnabled(false);
        }else {
            jComboBoxTrack.setEnabled(true);
            jButtonMemCruise.setEnabled(true);
            jButtonRunCruise.setEnabled(true);
            jButtonDeleteCruise.setEnabled(true);
            jButtonDeleteAllCruise.setEnabled(true);
        }
        
    }
    
    private void modifyCruiseJButtonState(JButton TrackJButton){
        if (TrackJButton == null){
            jButtonRunSeq.setEnabled(true);
            jButtonAddPoint.setEnabled(true);
            jButtonDelPoint.setEnabled(true);
            jComboBoxCruise.setEnabled(true);
            jTableCruise.setEnabled(true);
        }else if(TrackJButton.equals(jButtonRunSeq)){
            jButtonRunSeq.setEnabled(true);
            jButtonAddPoint.setEnabled(false);
            jButtonDelPoint.setEnabled(false);
            jComboBoxCruise.setEnabled(false);
            jTableCruise.setEnabled(false);
        }else{
            jButtonRunSeq.setEnabled(true);
            jButtonAddPoint.setEnabled(true);
            jButtonDelPoint.setEnabled(true);
            jComboBoxCruise.setEnabled(true);
            jTableCruise.setEnabled(true);
        }
        
    }
    
    /*--------------------------云台操作专用函数 结束-----------------------*/
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrameTotalPreview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrameTotalPreview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrameTotalPreview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrameTotalPreview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrameTotalPreview().setVisible(true);
            }
        });
    }
    
    /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    private void modifyLocales(){
        
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作
        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        //信息显示
//        sCommandMeanings[0] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings0");  //灯光电源
//        sCommandMeanings[1] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings1");  //雨刷开关
//        sCommandMeanings[2] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings2");  //风扇开关
//        sCommandMeanings[3] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings3");  //加热器开关
//        sCommandMeanings[4] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings4");  //辅助设备开关
//        sCommandMeanings[5] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings5");  //辅助设备开关2
//        sCommandMeanings[6] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings6");  //设置预置点
//        sCommandMeanings[7] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings7");  //清除预置点
//        sCommandMeanings[8] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings8");  //焦距变大(倍率变大)
//        sCommandMeanings[9] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings9");  //焦距变小(倍率变小)
//        sCommandMeanings[10] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings10");  //焦点前调
//        sCommandMeanings[11] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings11");  //焦点后调
//        sCommandMeanings[12] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings12");  //光圈扩大
//        sCommandMeanings[13] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings13");  //光圈缩小
//        sCommandMeanings[14] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings14");  //云台上仰
//        sCommandMeanings[15] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings15");  //云台下俯
//        sCommandMeanings[16] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings16");  //云台左转
//        sCommandMeanings[17] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings17");  //云台右转
//        sCommandMeanings[18] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings18");  //云台上仰和左转
//        sCommandMeanings[19] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings19");  //云台上仰和右转
//        sCommandMeanings[20] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings20");  //云台下俯和左转
//        sCommandMeanings[21] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings21");  //云台下俯和右转
//        sCommandMeanings[22] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings22");  //云台左右自动扫描
//        sCommandMeanings[23] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings23");  //将预置点加入巡航序列
//        sCommandMeanings[24] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings24");  //设置巡航点停顿时间
//        sCommandMeanings[25] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings25");  //设置巡航速度
//        sCommandMeanings[26] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings26");  // 将预置点从巡航序列中删除
//        sCommandMeanings[27] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings27");  //开始记录轨迹
//        sCommandMeanings[28] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings28");  //停止记录轨迹
//        sCommandMeanings[29] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings29");  //开始轨迹
//        sCommandMeanings[30] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings30");  //开始巡航
//        sCommandMeanings[31] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings31");  //停止巡航
//        sCommandMeanings[32] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings32");  //转到预置点
//        sCommandMeanings[33] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings33");  //云台下俯和焦距变大(倍率变大)
//        sCommandMeanings[34] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings34");  // 云台下俯和焦距变小(倍率变小)
//        sCommandMeanings[35] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings35");  //云台左转和焦距变大(倍率变大)
//        sCommandMeanings[36] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings36");  //云台左转和焦距变小(倍率变小)
//        sCommandMeanings[37] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings37");  //云台右转和焦距变大(倍率变大)
//        sCommandMeanings[38] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings38");  // 云台右转和焦距变小(倍率变小)
//        sCommandMeanings[39] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings39");  //云台上仰和左转和焦距变大(倍率变大)
//        sCommandMeanings[40] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings40");  //云台上仰和左转和焦距变小(倍率变小)
//        sCommandMeanings[41] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings41");  //云台上仰和右转和焦距变大(倍率变大)
//        sCommandMeanings[42] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings42");  //云台上仰和右转和焦距变小(倍率变小)
//        sCommandMeanings[43] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings43");  //云台下俯和左转和焦距变大(倍率变大)
//        sCommandMeanings[44] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings44");  //云台下俯和左转和焦距变小(倍率变小)
//        sCommandMeanings[45] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings45");  //云台下俯和右转和焦距变大(倍率变大)
//        sCommandMeanings[46] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings46");  //云台下俯和右转和焦距变小(倍率变小)
//        sCommandMeanings[47] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings47");  //云台上仰和焦距变大(倍率变大)
//        sCommandMeanings[48] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings48");  //云台上仰和焦距变小(倍率变小)
//        sCommandMeanings[49] = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings49");  //快球云台花样扫描

        sCommandMeanings = Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCommandMeanings").split(",");  //
        
        sPresetTabeTile[0] =        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPresetTabeTile0");  //ID
        sPresetTabeTile[1] =        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPresetTabeTile1");  //预置点

        sCruisePointTabeTile[0] =   Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCruisePointTabeTile0");  //ID
        sCruisePointTabeTile[1] =   Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCruisePointTabeTile1");  //巡航点
        sCruisePointTabeTile[2] =   Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCruisePointTabeTile2");  //时速

        sViewTabeTile[1] =          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sViewTabeTile1");  //自定义视图列表
        
        sStartPreview =             Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStartPreview");  //开始预览
        sStopPreview =              Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStopPreview");  //结束预览
        sFishBallTrack =            Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sFishBallTrack");  //鱼球联动
        sStartFishBallTrack =       Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStartFishBallTrack");  //开始鱼球联动
        sStopFishBallTrack =        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStopFishBallTrack");  //停止鱼球联动
        sStartRecord =              Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStartRecord");  //开始录像
        sStopRecord =               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStopRecord");  //停止录像
        sOpenPTZCtrlWindow =        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sOpenPTZCtrlWindow");  //启用窗口云台控制
        sClosePTZCtrlWindow =       Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sClosePTZCtrlWindow");  //关闭窗口云台控制
        sOpenVoice =                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sOpenVoice");  //打开声音
        sCloseVoice =               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCloseVoice");  //关闭声音
        sFullScreen =               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sFullScreen");  //全屏
        sExitFullScreen =           Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sExitFullScreen");  //退出全屏
        sStartFishEyeTrackSetup =   Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStartFishEyeTrackSetup");  //开始设置鱼球联动
        sPTZCtrl =                  Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPTZCtrl");  //云台控制
        sPTZCtrlFail =              Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPTZCtrlFail");  //云台控制失败
        SystemLockScreen =          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.SystemLockScreen");  //系统锁屏
        sShowPTZWindow =            Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sShowPTZWindow");  //打开云台控制窗口
        sVideoEffectPara =          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sVideoEffectPara");  //视频声音参数设置

        sCloseLight =               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCloseLight");  //关闭灯光
        sOpenLight =                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sOpenLight");  //开启灯光
        sCloseWiper =               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCloseWiper");  //关闭雨刷
        sOpenWiper =                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sOpenWiper");  //开启雨刷
        sCloseFan =                 Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCloseFan");  //关闭风扇
        sOpenFan =                  Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sOpenFan");  //开启风扇
        sStopHeater =               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStopHeater");  //停止加热
        sHeater =                   Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sHeater");  //加热
        sCloseAux =                 Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCloseAux");  //关闭辅助设备开关
        sOpeAux =                   Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sOpeAux");  //开启辅助设备开关
        sCloseAux2 =                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCloseAux2");  //关闭辅助设备开关2
        sOpenAux2 =                 Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sOpenAux2");  //开启辅助设备开关2

        sGetPresetFail =            Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sGetPresetFail");  //获取预置点信息失败
        sGotoPreset =               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sGotoPreset");  //调用预置点：
        sDelPreset =                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sDelPreset");  //删除预置点：
        sDelPresetSucc =            Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sDelPresetSucc");  //成功删除预置点：
        sSetPreset =                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sSetPreset");  //设置预置点：
        sSetPresetSucc =            Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sSetPresetSucc");  //成功设置预置点：
        sModifyPresetFail =         Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sModifyPresetFail");  //修改预置点名称失败
        sModifyPresetName =         Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sModifyPresetName");  //修改预置点{0}]名称为：[{1}]

        sPTZPark =                      Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPTZPark");  //设置云台守望位置：
        sPTZParkSucc =                  Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPTZParkSucc");  //成功设置云台守望位置：

        sPreCruise =                    Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPreCruise");  //路径
        sRunCruise =                    Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sRunCruise");  //调用巡航路径
        sStopRunCruise =                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStopRunCruise");  //停止调用巡航路径
        sRunCruiseSpecific =            Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sRunCruiseSpecific");  //调用第["+iSeq+"]号巡航路径
        sStopRunCruiseSpecific =        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStopRunCruiseSpecific");  //停止调用第["+iSeq+"]号巡航路径
        sAddCruisePoint =               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sAddCruisePoint");  //添加巡航点
        sAddCruisePointSpecific =       Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sAddCruisePointSpecific");  //添加第[{0}]号巡航点：第[{1}]号预置点[{2}]
        sAddCruisePointSpecificSucc =   Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sAddCruisePointSpecificSucc");  //成功添加第[{0}]号巡航点：第[{1}]号预置点[{2}]
        sDelCruisePoint =               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sDelCruisePoint");  //删除第[{0}]号巡航点：
        sDelCruisePointSucc =           Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sDelCruisePointSucc");  //成功删除第[{0}]号巡航点：
        sEditCruisePoint =              Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sEditCruisePoint");  //编辑巡航点
        sModifyCruisePoint =            Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sModifyCruisePoint");  //修改第[{0}]号巡航点为：第[{1}]号预置点[{2}]

        sPreTrack =                     Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPreTrack");  //轨迹
        sStartMemTrack =                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStartMemTrack");  //开始记录云台轨迹
        sStartMemTrackSpecific =        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStartMemTrackSpecific");  //开始记录第[{0}]条云台轨迹
        sStopMemTrack =                 Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStopMemTrack");  //停止记录云台轨迹
        stopMemTrackSpecific =          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.stopMemTrackSpecific");  //停止记录第[{0}]条云台轨迹
        sStartRunTrack =                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStartRunTrack");  //开始运行云台轨迹
        sStartRunTrackSpecific =        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStartRunTrackSpecific");  //开始运行第[{0}]条云台轨迹
        sStopRunTrack =                 Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStopRunTrack");  //停止运行云台轨迹
        sStopRunTrackSpecific =         Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sStopRunTrackSpecific");  //停止运行第[{0}]条云台轨迹
        sDelTrackSpecific =             Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sDelTrackSpecific");  //删除第[{0}]条云台轨迹
        sDelTrackAll =                  Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sDelTrackAll");  //删除所有云台轨迹

        sGetPTZStrCfgAbilityFail =      Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sGetPTZStrCfgAbilityFail");  //获取设备能力集失败

        sViewTreeRoot =                 Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sViewTreeRoot");  //自定义视图
        sInputDialog =                  Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sInputDialog");  //请输入视图名称：
        sViewName =                     Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sViewName");  //视图名称
        sViewMessage =                  Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sViewMessage");  //视图“{0}”已存在！
        sSaveView =                     Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sSaveView");  //保存视图
        sSaveViewSpecificSucc =         Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sSaveViewSpecificSucc");  //保存视图“{0}”成功！
        sSaveViewSpecificFail =         Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sSaveViewSpecificFail");  //保存视图“{0}”失败
        sDelView =                      Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sDelView");  //删除视图
        sMessageBoxDelView =            Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sMessageBoxDelView");  //真的要删除视图“{0}”？
        sCanNotDelViewSpecific =        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sCanNotDelViewSpecific");  //视图“{0}”不能被删除！
        sDelViewSpecificSucc =          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sDelViewSpecificSucc");  //删除视图“{0}”成功！
        sDelViewSpecificFail =          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sDelViewSpecificFail");  //删除视图“{0}”失败

        sMessageBoxExitPreview =        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sMessageBoxExitPreview");  //系统正在使用预览，退出就要停止预览。是否要继续？
        sMessageBoxRemind =             Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sMessageBoxRemind");  //提醒



        //标签和按钮显示
//        jLabelTitle.setText(    Locales.getString("ClassStrings", "JDialogAlarmFilterSet.jLabelTitle"));//报警信息过滤
//        jCheckBoxAll.setText(   Locales.getString("ClassStrings", "JDialogAlarmFilterSet.jCheckBoxAll"));//全选
//        jButtonSet.setText(     Locales.getString("ClassStrings", "JDialogAlarmFilterSet.jButtonSet"));//设置
        
        jMenuItemStopAllView.setText(       Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jMenuItemStopAllView"));  //停止所有预览
        jMenuItemCapture.setText(           Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jMenuItemCapture"));  //抓图
        jMenuItemCorrectTime.setText(       Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jMenuItemCorrectTime"));  //校时
        jMenuItemTalkback.setText(          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jMenuItemTalkback"));  //开始对讲
        jMenuItemSetupFishBallTrack.setText(Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jMenuItemSetupFishBallTrack"));  //设置鱼球联动
        jMenuItemLockScreen.setText(        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jMenuItemLockScreen"));  //锁屏
        jLabelCruisePoint.setText(          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelCruisePoint"));  //巡航点：
        jLabelPresetPoint.setText(          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelPresetPoint"));  //预置点：
        jLabelCruiseTime.setText(           Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelCruiseTime"));  //巡航时间：
        jLabelCruiseSpeed.setText(          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelCruiseSpeed"));  //巡航速度：
        jButtonOK.setText(                  Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonOK"));  //确定
        jButtonCancel.setText(              Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonCancel"));  //取消
        jLabelTitle1.setText(               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelTitle1"));  //编辑自定义视图
        jButtonAddView.setText(             Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonAddView"));  //添加
        jButtonDelView.setText(             Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonDelView"));  //删除
        jLabelRow.setText(                  Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelRow"));  //行：
        jLabelCol.setText(                  Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelCol"));  //列：
        jButtonMerge.setText(               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonMerge"));  //合并
        jButtonMergeCancel.setText(         Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonMergeCancel"));  //取消合并
        jButtonMergeClear.setText(          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonMergeClear"));  //全部取消
        jButtonOK2.setText(                 Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonOK2"));  //保存
        jButtonCance2.setText(              Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonCance2"));  //取消
        jLabelTitle2.setText(               Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelTitle2"));  //选择自定义视图
        jButtonViewCompile.setText(         Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonViewCompile"));  //编辑视图
        jLabelUserNameTag.setText(          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelUserNameTag"));  //用 户 名：
        jLabelUserTypeTag.setText(          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelUserTypeTag"));  //用户等级：
        jLabelLogTimeTag.setText(           Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelLogTimeTag"));  //登录时间：
        jLabelCurrentTimeTag.setText(       Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelCurrentTimeTag"));  //当前时间：
        jLabelChannel.setText(              Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelChannel"));  //监控点
        jButtonPlayBack.setText(            Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonPlayBack"));  //录像回放       
        jButtonExit.setText(                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonExit"));  //退出实时监控
        jButtonReturnMain.setText(          Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonReturnMain"));  //返回主界面    
        jButtonLogOut.setText(              Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonLogOut"));  //注销     
        jLabelMagnification.setText(        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelMagnification"));  //倍率
        jLabelFocus.setText(                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelFocus"));  //聚焦
        jLabelAperture.setText(             Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelAperture"));  //光圈
        jTabbedPanePTZ.setTitleAt(0,        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jPanelPreset"));  //预置点
        jTabbedPanePTZ.setTitleAt(1,        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jPanelCruise"));  //巡航
        jTabbedPanePTZ.setTitleAt(2,        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jPanelTrack"));  //轨迹
        jButtonView6.setToolTipText(        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonView6"));  //自定义6视图
        jButtonView8.setToolTipText(        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonView8"));  //自定义8视图
        jButtonDeletePreset.setToolTipText( Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonDeletePreset"));  //删除预置点设置
        jButtonDelPoint.setToolTipText(     Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonDelPoint"));  //删除巡航点
        jButtonDeleteCruise.setToolTipText( Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jButtonDeleteCruise"));  //删除单条云台轨迹
        jButtonPTZPark.setToolTipText(      Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPTZParkToolTip"));//设置云台守望位置
        jSliderSpeed.setToolTipText(        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jSliderSpeed"));//云台控制的速度


        jMenuItemPreview.setText(sStartPreview);
        jMenuItemRecord.setText(sStartRecord);
        jMenuItemPTZCtrl.setText(sOpenPTZCtrlWindow);

        jMenuItemPTZWindow.setText(sShowPTZWindow);
        jMenuItemOpenVoice.setText(sOpenVoice);

        jMenuItemVideoEffect.setText(sVideoEffectPara);

        jMenuItemFishBallTrack.setText(sStartFishBallTrack);
        jMenuItemFullScreen.setText(sFullScreen);

        jDialogCruisePoint.setTitle(sAddCruisePoint);

        jLabelTitle.setText(sAddCruisePoint);
        jLabelPTZCtrl.setText(sPTZCtrl);
        jButtonViewCustom.setToolTipText(sViewTreeRoot);

        jButtonLeftUp.setToolTipText(sCommandMeanings[18]);//"云台上仰和左转");

        jButtonUp.setToolTipText(sCommandMeanings[14]);//"云台上仰");

        jButtonRightUp.setToolTipText(sCommandMeanings[19]);//"云台上仰和右转");

        jButtonLeft.setToolTipText(sCommandMeanings[16]);//"云台左转 ");

        jButtonAuto.setToolTipText(sCommandMeanings[22]);//"云台左右自动扫描");
 
        jButtonRight.setToolTipText(sCommandMeanings[16]);//"云台右转");
 
        jButtonLeftDown.setToolTipText(sCommandMeanings[20]);//"云台下俯和左转");

        jButtondown.setToolTipText(sCommandMeanings[15]);//"云台下俯 ");

        jButtonRightDown.setToolTipText(sCommandMeanings[21]);//"云台下俯和右转");

        jButtonZoomIn.setToolTipText(sCommandMeanings[8]);//"焦距变大(倍率变大)");

        

        jButtonZoomOut.setToolTipText(sCommandMeanings[9]);//"焦距变小(倍率变小)");
        jButtonFocusNear.setToolTipText(sCommandMeanings[10]);//"焦点后调");
        jButtonFocusFar.setToolTipText(sCommandMeanings[11]);//"焦点后调");
        jButtonIrisOpen.setToolTipText(sCommandMeanings[12]);//"光圈扩大");
        jButtonIrisClose.setToolTipText(sCommandMeanings[13]);//"光圈缩小");
        jButtonHeater.setToolTipText(sHeater);
        jButtonLight.setToolTipText(sOpenLight);
        jButtonWiperPwron.setToolTipText(sOpenWiper);
        jButtonAux1.setToolTipText(sOpeAux);
        jButtonAux2.setToolTipText(sOpenAux2);
        jButtonFanPwron.setToolTipText(sOpenFan);

        jButtonGotoPreset.setToolTipText(sGotoPreset);
 
        jButtonSetPreset.setToolTipText(sSetPreset);

        jButtonRunSeq.setToolTipText(sRunCruise);

        jButtonAddPoint.setToolTipText(sAddCruisePoint);

        jButtonMemCruise.setToolTipText(sStartMemTrack);
        
        jButtonRunCruise.setToolTipText(sStartRunTrack);//"开始运行云台轨迹");

        jButtonDeleteAllCruise.setToolTipText(sDelTrackAll);//"删除所有云台轨迹");
 
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddPoint;
    private javax.swing.JButton jButtonAddView;
    private javax.swing.JButton jButtonAuto;
    private javax.swing.JButton jButtonAux1;
    private javax.swing.JButton jButtonAux2;
    private javax.swing.JButton jButtonCance2;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDelPoint;
    private javax.swing.JButton jButtonDelView;
    private javax.swing.JButton jButtonDeleteAllCruise;
    private javax.swing.JButton jButtonDeleteCruise;
    private javax.swing.JButton jButtonDeletePreset;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonExit2;
    private javax.swing.JButton jButtonExit3;
    private javax.swing.JButton jButtonExit4;
    private javax.swing.JButton jButtonFanPwron;
    private javax.swing.JButton jButtonFocusFar;
    private javax.swing.JButton jButtonFocusNear;
    private javax.swing.JButton jButtonGotoPreset;
    private javax.swing.JButton jButtonHeater;
    private javax.swing.JButton jButtonIrisClose;
    private javax.swing.JButton jButtonIrisOpen;
    private javax.swing.JButton jButtonLeft;
    private javax.swing.JButton jButtonLeftDown;
    private javax.swing.JButton jButtonLeftUp;
    private javax.swing.JButton jButtonLight;
    private javax.swing.JButton jButtonLogOut;
    private javax.swing.JButton jButtonMemCruise;
    private javax.swing.JButton jButtonMerge;
    private javax.swing.JButton jButtonMergeCancel;
    private javax.swing.JButton jButtonMergeClear;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonOK2;
    private javax.swing.JButton jButtonPTZPark;
    private javax.swing.JButton jButtonPTZShow;
    private javax.swing.JButton jButtonPlayBack;
    private javax.swing.JButton jButtonReturnMain;
    private javax.swing.JButton jButtonRight;
    private javax.swing.JButton jButtonRightDown;
    private javax.swing.JButton jButtonRightUp;
    private javax.swing.JButton jButtonRunCruise;
    private javax.swing.JButton jButtonRunSeq;
    private javax.swing.JButton jButtonSetPreset;
    private javax.swing.JButton jButtonTreeShow;
    private javax.swing.JButton jButtonUp;
    private javax.swing.JButton jButtonView1;
    private javax.swing.JButton jButtonView16;
    private javax.swing.JButton jButtonView25;
    private javax.swing.JButton jButtonView36;
    private javax.swing.JButton jButtonView4;
    private javax.swing.JButton jButtonView49;
    private javax.swing.JButton jButtonView6;
    private javax.swing.JButton jButtonView64;
    private javax.swing.JButton jButtonView8;
    private javax.swing.JButton jButtonView9;
    private javax.swing.JButton jButtonViewCompile;
    private javax.swing.JButton jButtonViewCustom;
    private javax.swing.JButton jButtonWiperPwron;
    private javax.swing.JButton jButtonZoomIn;
    private javax.swing.JButton jButtonZoomOut;
    private javax.swing.JButton jButtondown;
    private javax.swing.JComboBox jComboBoxCruise;
    private javax.swing.JComboBox<String> jComboBoxCruisePoint;
    private javax.swing.JComboBox<String> jComboBoxCruiseSpeed;
    private javax.swing.JComboBox<String> jComboBoxDwell;
    private javax.swing.JComboBox<String> jComboBoxPreset;
    private javax.swing.JComboBox jComboBoxTrack;
    private javax.swing.JComboBox<String> jComboBoxViewCol;
    private javax.swing.JComboBox<String> jComboBoxViewRow;
    private javax.swing.JDialog jDialogCruisePoint;
    private javax.swing.JDialog jDialogCustomizeView;
    private javax.swing.JDialog jDialogSelectView;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabelAperture;
    private javax.swing.JLabel jLabelChannel;
    private javax.swing.JLabel jLabelCol;
    private javax.swing.JLabel jLabelCruisePoint;
    private javax.swing.JLabel jLabelCruiseSpeed;
    private javax.swing.JLabel jLabelCruiseTime;
    private javax.swing.JLabel jLabelCurrentTime;
    private javax.swing.JLabel jLabelCurrentTimeTag;
    private javax.swing.JLabel jLabelFocus;
    private javax.swing.JLabel jLabelLogTime;
    private javax.swing.JLabel jLabelLogTimeTag;
    private javax.swing.JLabel jLabelMagnification;
    private javax.swing.JLabel jLabelPTZCtrl;
    private javax.swing.JLabel jLabelPresetPoint;
    private javax.swing.JLabel jLabelRow;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelTitle1;
    private javax.swing.JLabel jLabelTitle2;
    private javax.swing.JLabel jLabelUserName;
    private javax.swing.JLabel jLabelUserNameTag;
    private javax.swing.JLabel jLabelUserType;
    private javax.swing.JLabel jLabelUserTypeTag;
    private javax.swing.JMenuItem jMenuItemCapture;
    private javax.swing.JMenuItem jMenuItemCorrectTime;
    private javax.swing.JMenuItem jMenuItemFishBallTrack;
    private javax.swing.JMenuItem jMenuItemFullScreen;
    private javax.swing.JMenuItem jMenuItemLockScreen;
    private javax.swing.JMenuItem jMenuItemOpenVoice;
    private javax.swing.JMenuItem jMenuItemPTZCtrl;
    private javax.swing.JMenuItem jMenuItemPTZWindow;
    private javax.swing.JMenuItem jMenuItemPreview;
    private javax.swing.JMenuItem jMenuItemRecord;
    private javax.swing.JMenuItem jMenuItemSetupFishBallTrack;
    private javax.swing.JMenuItem jMenuItemStopAllView;
    private javax.swing.JMenuItem jMenuItemTalkback;
    private javax.swing.JMenuItem jMenuItemVideoEffect;
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
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCruise;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelFirst;
    private javax.swing.JPanel jPanelFirst1;
    private javax.swing.JPanel jPanelFirst2;
    private javax.swing.JPanel jPanelFoot;
    private javax.swing.JPanel jPanelFoot2;
    private javax.swing.JPanel jPanelLast;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelPTZ;
    private javax.swing.JPanel jPanelPreset;
    private javax.swing.JPanel jPanelPreviewContainer;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSelectView;
    private javax.swing.JPanel jPanelTrack;
    private javax.swing.JPanel jPanelViewCustom;
    private javax.swing.JPanel jPanelViewsNames;
    private javax.swing.JPopupMenu jPopupMenuPreview;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneCruise;
    private javax.swing.JScrollPane jScrollPanePreset;
    private javax.swing.JScrollPane jScrollPaneSelectView;
    private javax.swing.JScrollPane jScrollPaneViewNames;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSlider jSliderSpeed;
    private javax.swing.JSplitPane jSplitPaneCustomizeView;
    private javax.swing.JSplitPane jSplitPanePreview;
    private javax.swing.JTabbedPane jTabbedPanePTZ;
    private javax.swing.JTable jTableCruise;
    private javax.swing.JTable jTablePreset;
    private javax.swing.JTable jTableSelectView;
    private javax.swing.JTree jTreeResource;
    private javax.swing.JTree jTreeViewNames;
    private javax.swing.JDialog wnd;
    // End of variables declaration//GEN-END:variables

    private String[] sPresetTabeTile = new String[] {"ID", "预置点"};
    private String[] sCruisePointTabeTile = new String[] {"ID", "巡航点","时速"};
    private String[] sViewTabeTile = new String[] {"","自定义视图列表"};
    private String sStartPreview = "开始预览";
    private String sStopPreview = "结束预览";
    private String sFishBallTrack = "鱼球联动";
    private String sStartFishBallTrack = "开始鱼球联动";
    private String sStopFishBallTrack = "停止鱼球联动";
    private String sStartRecord = "开始录像";
    private String sStopRecord = "停止录像";
    private String sOpenPTZCtrlWindow = "启用窗口云台控制";
    private String sClosePTZCtrlWindow = "关闭窗口云台控制";
    private String sOpenVoice = "打开声音";
    private String sCloseVoice = "关闭声音";
    private String sFullScreen = "全屏";
    private String sExitFullScreen = "退出全屏";
    private String sStartFishEyeTrackSetup = "开始设置鱼球联动";
    private String sPTZCtrl = "云台控制";
    private String sPTZCtrlFail = "云台控制失败";
    private String SystemLockScreen = "系统锁屏";
    private String sShowPTZWindow = "打开云台控制窗口";
    private String sVideoEffectPara = "视频声音参数设置";

    private String sCloseLight = "关闭灯光";
    private String sOpenLight = "开启灯光";
    private String sCloseWiper = "关闭雨刷";
    private String sOpenWiper = "开启雨刷";
    private String sCloseFan = "关闭风扇";
    private String sOpenFan = "开启风扇";
    private String sStopHeater = "停止加热";
    private String sHeater = "加热";
    private String sCloseAux = "关闭辅助设备开关";
    private String sOpeAux = "开启辅助设备开关";
    private String sCloseAux2 = "关闭辅助设备2开关";
    private String sOpenAux2 = "开启辅助设备2开关";

    private String sGetPresetFail = "获取预置点信息失败";
    private String sGotoPreset = "调用预置点：";
    private String sDelPreset = "删除预置点：";
    private String sDelPresetSucc = "成功删除预置点：";
    private String sSetPreset = "设置预置点：";
    private String sSetPresetSucc = "成功设置预置点：";
    private String sModifyPresetFail = "修改预置点名称失败";
    private String sModifyPresetName = "修改预置点[{0}]名称为：[{1}]";

    private String sPTZPark = "设置云台守望位置：";
    private String sPTZParkSucc = "成功设置云台守望位置：";

    private String sPreCruise = "路径";
    private String sRunCruise = "调用巡航路径";
    private String sStopRunCruise = "停止调用巡航路径";
    private String sRunCruiseSpecific = "调用第[{0}]号巡航路径";
    private String sStopRunCruiseSpecific = "停止调用第[{0}]号巡航路径";
    private String sAddCruisePoint = "添加巡航点";
    private String sAddCruisePointSpecific = "添加第[{0}]号巡航点：第[{1}]号预置点[{2}]";
    private String sAddCruisePointSpecificSucc = "成功添加第[{0}]号巡航点：第[{1}]号预置点[{2}]";
    private String sDelCruisePoint = "删除第[{0}]号巡航点：";
    private String sDelCruisePointSucc = "成功删除第[{0}]号巡航点：";
    private String sEditCruisePoint = "编辑巡航点";
    private String sModifyCruisePoint = "修改第[{0}]号巡航点为：第[{1}]号预置点[{2}]";

    private String sPreTrack = "轨迹";
    private String sStartMemTrack = "开始记录云台轨迹";
    private String sStartMemTrackSpecific = "开始记录第[{0}]条云台轨迹";
    private String sStopMemTrack = "停止记录云台轨迹";
    private String stopMemTrackSpecific = "停止记录第[{0}]条云台轨迹";
    private String sStartRunTrack = "开始运行云台轨迹";
    private String sStartRunTrackSpecific = "开始运行第[{0}]条云台轨迹";
    private String sStopRunTrack = "停止运行云台轨迹";
    private String sStopRunTrackSpecific = "停止运行第[{0}]条云台轨迹";
    private String sDelTrackSpecific = "删除第[{0}]条云台轨迹";
    private String sDelTrackAll = "删除所有云台轨迹";

    private String sGetPTZStrCfgAbilityFail = "获取设备能力集失败";

    private String sViewTreeRoot = "自定义视图";
    private String sInputDialog = "请输入视图名称：";
    private String sViewName = "视图名称";
    private String sViewMessage = "视图“{0}”已存在！";
    private String sSaveView = "保存视图";
    private String sSaveViewSpecificSucc = "保存视图“{0}”成功！";
    private String sSaveViewSpecificFail = "保存视图“{0}”失败";
    private String sDelView = "删除视图";
    private String sMessageBoxDelView = "真的要删除视图“{0}”？";
    private String sCanNotDelViewSpecific = "视图“{0}”不能被删除！";
    private String sDelViewSpecificSucc = "删除视图“{0}”成功！";
    private String sDelViewSpecificFail = "删除视图“{0}”失败";

    private String sMessageBoxExitPreview = "系统正在使用预览，退出就要停止预览。是否要继续？";
    private String sMessageBoxRemind = "提醒";


    /*************************************************
    类:        FDrawFunSelecctAera 
    函数描述:   设置鱼眼画选择框的回调函数
     *************************************************/
    class FDrawFunSelecctAera implements HCNetSDK.FDrawFun
    {
        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser) {

            uSer.DrawEdge(hDc, rectSelectArea, USER32.BDR_SUNKENOUTER, USER32.BF_RECT);
            gDi.SetBkMode(hDc, GDI32.TRANSPARENT);
        }
    }
    
//    /**
//        * 函数:      FDrawFunSetPreviewStateIcon
//        * 函数描述:  设置预览状态图标，比如录像、对讲、声音控制等
//    */
//    class FDrawFunSetPreviewStateIcon implements HCNetSDK.FDrawFun{
//        //POINT CenterPosition;
//        ArrayList<String> listIconAmageFileNames = new ArrayList<>(); 
//        
//        @Override
//        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser) {
//            //只有bmp文件可以正确LoadImage，其他的如png、gif、jpg等类型的图像都不能正常加载。但是转换成bmp文件就可以正确加载
//            String ppp = getClass().getResource("/jyms/image/record2.bmp").toString();
//
//            ppp = ppp.substring(6);
//            
//            HANDLE hBitmap = uSer.LoadImage(null, ppp, IMAGE_BITMAP, 22, 22, LR_LOADFROMFILE|LR_CREATEDIBSECTION);//LR_DEFAULTCOLOR);//LR_LOADFROMFILE|LR_CREATEDIBSECTION|LR_LOADREALSIZE
//            HANDLE hBrush = gDi.CreatePatternBrush(hBitmap);
//            //HANDLE hOldPen = gDi.SelectObject(hDc,hPen);//旧画笔句柄存储在hOldPen中
//            HANDLE hOldBrush = gDi.SelectObject(hDc, hBrush);
//            
//            gDi.Rectangle(hDc, 0, 0, 22, 22);
//
//            gDi.SelectObject(hDc, hOldBrush);
//            gDi.DeleteObject(hOldBrush);
//            gDi.DeleteObject(hBitmap);
//
//        }
//        
//        public void addIconAmageFile(String IconAmageFileName){
//            if (IconAmageFileName == null || IconAmageFileName.equals("")) return;
//            listIconAmageFileNames.add(IconAmageFileName);
//        }
//        
//        public void delIconAmageFile(String IconAmageFileName){
//            if (IconAmageFileName == null || IconAmageFileName.equals("")) return;
//            listIconAmageFileNames.remove(IconAmageFileName);
//        }
////        //构造器函数
////        public FDrawFunSetPreviewStateIcon(POINT CenterPosition){
////            //this.CenterPosition = CenterPosition;
////        }
//    }
    
    private class MyBoolean{
        private boolean ifON = false;
        MyBoolean(boolean IfOn){
            ifON = IfOn;
        }

        /**
         * @return the ifON
         */
        public boolean isIfON() {
            return ifON;
        }

        /**
         * @param ifON the ifON to set
         */
        public void setIfON(boolean ifON) {
            this.ifON = ifON;
        }
    
    }
    
    /**
        * 内部类:    CurrentTimeTask
        * 函数描述:  显示当前时间的定时器响应函数
    */
    private class CurrentTimeTask extends java.util.TimerTask{
        
        @Override
        public void run() {
            try{
                if (CommonParas.SysParas.ifChinese) {
                    DateHandler newDate = DateHandler.getDateInstance(new Date());
                    //String ss = newDate.getWeekDayStr();
                    jLabelCurrentTime.setText("<html>" +newDate.getDateString()+"<br>" + newDate.getWeekDayStr()+"</html>");
                }else{
                    jLabelCurrentTime.setText("<html><p>" +CommonParas.SysParas.sysLocales.getOperationTime(new Date())+"</p></html>");
                }
                
            }catch (Exception e){
                TxtLogger.append(sFileName, "CurrentTimeTask.run()","系统在回放定时器响应函数运行过程中，出现错误"
                                 + "\r\n                       Exception:" + e.toString());
            }
        }
    }
    
    private class PTZZoomWorker extends SwingWorker {
        
        private long lEnd;
        private int iDelay;
        private boolean bTimeOut = false;
        private final Timer timer = new Timer();
        private int iZoom_Order;
        private NativeLong lRealHandle;
        //private boolean bNextOrder = false;
        private ArrayList<String> listNextOrder = new ArrayList();
        private int iNextOrder = 0;
        
        public PTZZoomWorker(NativeLong RealHandle, int Delay, int Zoom_Order){
            iDelay = Delay;
            lEnd = Delay + System.currentTimeMillis();
            iZoom_Order = Zoom_Order;
            lRealHandle = RealHandle;
        }
        
        private void runZoomOrder(){

            bTimeOut = false;
//            System.out.println("Start!");
            PTZControlZoomMaxSpeed(lRealHandle, iZoom_Order, 0);
            
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
//                    System.out.println("Stop!");
                    PTZControlZoomMaxSpeed(lRealHandle, iZoom_Order, 1);
                    bTimeOut = true;
                }

            }, new Date(lEnd));
           
        }
        
//        public void runNextZoomOrder(){
//            
//            try {
//                bNextOrder = true;
//                Thread.sleep(iDelay);//50毫秒之后停止放大窗口
//                bNextOrder = false;
//            } catch (InterruptedException ex) {
//                Logger.getLogger(JFrameTotalPreview.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            
//        }
        public void runNextZoomOrder(){

            //bNextOrder = true;
            final int NextOrder = ++iNextOrder;
            listNextOrder.add("NextOrder" + NextOrder);
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    bTimeOut = false;
                    PTZControlZoomMaxSpeed(lRealHandle, iZoom_Order, 0);
                }

            }, new Date(lEnd + 1));
            
            lEnd = lEnd + iDelay + 1;
            
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    PTZControlZoomMaxSpeed(lRealHandle, iZoom_Order, 1);
                    listNextOrder.remove("NextOrder" + NextOrder);
                    bTimeOut = true;
                    //bNextOrder = false;
                }

            }, new Date(lEnd));

            
        }
   
        public boolean isTimeOut(){
            return bTimeOut && listNextOrder.isEmpty();
        }

        public boolean ifSameZoomOrder(int Zoom_Order) {
            return iZoom_Order == Zoom_Order;
        }
        
        public boolean ifSameRealHandle(NativeLong RealHandle){
            return lRealHandle == RealHandle;
        }
        
        //在timer任务没有完成的情况下，如果需要程序命令将其停止，则可以调用该函数
        public void cancelAllWorker(){
            
            if (!isTimeOut()){
                PTZControlZoomMaxSpeed(lRealHandle, iZoom_Order, 1);
            }
            timer.cancel();
            this.cancel(true);

        }

        @Override
        protected Object doInBackground() throws Exception {
            runZoomOrder();
            return null;
        }

        

    }
   
}
