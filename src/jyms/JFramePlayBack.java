
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Hashtable;
import java.util.Timer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import jyms.data.TxtLogger;
import jyms.tools.DateChooserJButtonE;
import jyms.tools.DateUtil;
import jyms.tools.DateUtil.DateHandler;
import jyms.tools.GlassPaneToolTip;
import jyms.tools.GlassPaneToolTipAera;
import jyms.tools.ImageIconBufferPool;
import jyms.ui.SliderUI_Customize;
import jyms.ui.ToolBarUI;




/**
 *
 * @author John
 */
public class JFramePlayBack extends javax.swing.JFrame{
    
    private final String sFileName = this.getClass().getName() + ".java";

    private DefaultTableModel fileListModel;
    private DefaultMutableTreeNode m_DeviceRootResource;//通道树根节点

    private String sChannelNode = "";//存储当前的通道节点名
    private int indexOflistDevice = -1;//对应的g_listDeviceDetailPara的索引号，从索引号可以得到UseriID,和其他相关设备信息
    private String sAnotherName = "";//存储当前的设备别名
    private String sSerialNo = "";//存储当前的设备序列号
    private int iChannel = -1;//存储当前的通道号
    private String sSaveFileName;//存储当前的录像文件名（主要用于下载录像文件，而不是回放时录像）
    private String sTime1 = "";//存储上一次查询的起始时间，用来比对是否发生更改事件
    private String sTime2= "";//存储上一次查询的终止时间，用来比对是否发生更改事件
    private int indexComboBoxRecodType;//存储上一次查询的事件类型：下拉列表框的索引，用来比对是否发生更改事件
    
    private int iAllAlarmRecordType = -100;//全部报警录像类型，自定义，检索用的


    private JPanel currentJPanel;//当前的JPanel
    private Panel currentPanel;//当前的Panel
    private ArrayList<ChannelPlayBack> listChannelPlayBack = new ArrayList<>();//ChannelPlayBack
    private ArrayList<JPanelPlayWindow> listJPanelPlayWindow = new ArrayList<>();//
    private ArrayList<ArrayList> listRecordFiles = new ArrayList<>();//存储各个录像回放窗口的录像文件列表
    private boolean bZoomIn = false;
    private final int iPanelBoderThickness = 1;//回放窗口的Panel、JPanel的Boder粗度

    private boolean bFirstSelect = true;//是否窗口初始化过程中选择JComboBox，因为编码选择也是触发了ItemStateChanged事件

    private DateChooserJButtonE buttonTime1 = new DateChooserJButtonE(DateUtil.getDateFromNow(Calendar.DAY_OF_YEAR, -1));
    private DateChooserJButtonE buttonTime2 = new DateChooserJButtonE();
    
    private NativeLong m_lDownloadHandle;//下载句柄
    private NativeLong m_lUserID;//用户ID
    private Timer Downloadtimer;//下载用定时器
    
    private static final int WINDOWSNUM = 4;
    private GlassPaneToolTip myGlassPane = new GlassPaneToolTip();
    private GlassPaneToolTipAera myGlassPaneAera = new GlassPaneToolTipAera();
    //假设jSlider和标尺之间的距离为16(这个数值正好，当然在jSlider在现在的尺寸时是正确的)
    int iSpace = 16;
    private SliderUI_Customize mySliderUI;// = new SliderUI_Customize(jSliderTimeLine);
    private Container contentPane = this.getContentPane();
    
    private HCNetSDK.NET_DVR_TIME struDownloadStartTime = new HCNetSDK.NET_DVR_TIME();//录像剪辑（也就是下载）的起始时间
    private HCNetSDK.NET_DVR_TIME struDownloadStopTime = new HCNetSDK.NET_DVR_TIME();//录像剪辑（也就是下载）的的终止时间
    private int downloadFlag = 0;//录像剪辑（也就是下载）标志。0表示未开始；1表示剪辑了一下，起始时间；2表示剪辑了终止时间，正在下载。
    
    //回放同步/异步
    private boolean ifSyn = false;//是否进行同步回放
    private int iPlayState = 0;//正在现在的状态：HCNetSDK.NET_DVR_PLAYSTART播放1/HCNetSDK.NET_DVR_PLAYPAUSE暂停3/初始状态0
    private int iPlayDirection = 29;//NET_DVR_PLAY_FORWARD 29 倒放切换为正放/NET_DVR_PLAY_REVERSE 30 正放切换为倒放 
    private double dPlaySpeed = 1;//速度倍数。如果iPlaySpeed是快速，则为1x、2x、4x、8x、16x；如果dPlaySpeed是慢速，则为1/2x、1/2x、1/4x、1/8x、1/16x
    /**
     * Creates new form JFrameRecordSchedule
     */
    public JFramePlayBack() {
        //可能你界面上有重量级组件，把轻量级combox挡住了，用这个试试 JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        //我的问题就是通过在主函数的frame = new JFrame("匹配软件") 这行代码前边加的，就OK了
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        initComponents();
        modifyLocales();
        initialDialog();
    }

    /**
        * 函数:      initialDialog
        * 函数描述:  初始化设备树和搜索信息
    */
    private void initialDialog()
    {
        try{
            jPanelTime.setLayout(new BorderLayout(1, 1));
            jPanelTime.add(buttonTime1, BorderLayout.WEST);
            jPanelTime.add(buttonTime2, BorderLayout.CENTER);
            
            
            //全部, 定时录像, 移动侦测, 报警触发, 报警|动测, 报警&动测, 命令触发, 手动录像7-智能录像13-移动侦测、PIR、无线、呼救等所有报警类型的"或"，
            //不带卡号查找时类型：0xff-全部，0-定时录像，1-移动侦测，2-报警触发，3-报警触发或移动侦测，4-报警触发和移动侦测，5-命令触发，6-手动录像，7-智能录像，10-PIR报警，11-无线报警，12-呼救报警，13-移动侦测、PIR、无线、呼救等所有报警类型的"或"
//            iRecordFileTypes = new int[6];
//            choiceRecodType.add(sChoiceRecodType[0]);//"全部录像"    
//            iRecordFileTypes[0] = 0xff;
//            choiceRecodType.add(sChoiceRecodType[1]);//"定时录像");    
//            iRecordFileTypes[1] = 0;
//            choiceRecodType.add(sChoiceRecodType[2]);//"手动录像");    
//            iRecordFileTypes[2] = 6;
//            choiceRecodType.add(sChoiceRecodType[3]);//"命令触发");    
//            iRecordFileTypes[3] = 5;
//            choiceRecodType.add(sChoiceRecodType[4]);//"智能侦测");    
//            iRecordFileTypes[4] = 19;
//            choiceRecodType.add(sChoiceRecodType[5]);//"报警录像");    
//            iRecordFileTypes[5] = iAllAlarmRecordType;
            for (int i=0;i<sChoiceRecodType.length;i++){
                jComboBoxRecodType.addItem(sChoiceRecodType[i]);
            }

           
            
            
            //0-定时录像6-手动录像5-命令触发19-智能侦测、全部报警录像iAllAlarmRecordType
            
            m_lDownloadHandle = new NativeLong(-1);

            

            for (int i=0;i<WINDOWSNUM;i++){
                ChannelPlayBack channelPlayBack = new ChannelPlayBack();
                listChannelPlayBack.add(channelPlayBack);
            }
            
            jPanelPlayBackContainer.setLayout(new GridLayout(2, 2,iPanelBoderThickness,iPanelBoderThickness));
            jPanelPlayBackContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, iPanelBoderThickness));
            for (int i=0;i<WINDOWSNUM;i++)   {
                JPanelPlayWindow jPanelPlayWindowTmp = new JPanelPlayWindow(i);
                jPanelPlayWindowTmp.setChannelPlayBack(listChannelPlayBack.get(i));
                //jPanelPlayWindowTmp.setPreferredSize(new Dimension(jPanelPlayBackContainer.getWidth()/2-2, jPanelPlayBackContainer.getHeight()/2-2));
                jPanelPlayWindowTmp.setBorder(BorderFactory.createLineBorder(Color.BLACK, iPanelBoderThickness));
                jPanelPlayBackContainer.add(jPanelPlayWindowTmp);  
                listJPanelPlayWindow.add(jPanelPlayWindowTmp);
            }
            for (int i=0;i<WINDOWSNUM;i++){
                ArrayList NewList = new ArrayList();
                listRecordFiles.add(NewList);
            }

            jProgressBar.setVisible(false);
//            //起止时间
//            buttonTime1.addMouseListener(new java.awt.event.MouseAdapter() {
//                @Override
//                public void mouseReleased(java.awt.event.MouseEvent evt) {
//                    String ss = buttonTime1.getText();
//                    if (!ss.equals(sTime1)) refreshFileList();
//                }
//            });
//            buttonTime1.addMouseListener(new java.awt.event.MouseAdapter() {
//                @Override
//                public void mouseReleased(java.awt.event.MouseEvent evt) {
//                    String ss = buttonTime1.getText();
//                    if (!ss.equals(sTime1)) refreshFileList();
//                }
//            });

            sTime1 =  buttonTime1.getDefaultDateText();//.getText();//存储上一次查询的起始时间，用来比对是否发生更改事件
            sTime2=  buttonTime2.getDefaultDateText();//.getText();//存储上一次查询的终止时间，用来比对是否发生更改事件
            indexComboBoxRecodType = jComboBoxRecodType.getSelectedIndex();//choiceRecodType.getSelectedIndex();

            jSplitPanePlayBack.setDividerLocation(300);
            fileListModel= this.initialTableModel();
            jTableFileList.setModel(fileListModel);
            jTableFileList.setRowHeight(30);
            jTableFileList.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N

            

            setTableColWidth();
    //        jTreeResource.setSelectionInterval(1, 1);//选中第二个节点（第一个节点的第一个子节点）
            bFirstSelect = false;
            //根据权限，将权限之外的设备节点删除
            delNoAuthorityDVR();
            
            jSliderTimeLine.setMajorTickSpacing(3600);
            jSliderTimeLine.setMaximum(86400);
            setJSiliderModel(jSliderTimeLine);
            
            //设置自定义UI

//            CommonParas.setJButtonUnDecorated(jButtonFast);
//            CommonParas.setJButtonUnDecorated(jButtonPlay);
//            CommonParas.setJButtonUnDecorated(jButtonReverse);
//            CommonParas.setJButtonUnDecorated(jButtonSlow);
//            CommonParas.setJButtonUnDecorated(jButtonStop);

            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "initialDialog()","系统在初始化设备树和搜索信息过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      delNoAuthorityDVR
        * 函数描述:  将权限之外的设备节点删除
    */
    private void delNoAuthorityDVR(){
        try{
            //待删除的设备节点。对于当前用户无权限和不具备雨刷能力的设备，其对应树节点节点将从设备树中删除。
            ArrayList<DefaultMutableTreeNode> ListTreeNodeDelete = new ArrayList<>();//设备节点
            ArrayList<DefaultMutableTreeNode> ListTreeNodeDelete2 = new ArrayList<>();//设备子节点（针对IP设备，如NVR)
            for (int i=0;i<m_DeviceRootResource.getChildCount();i++){
                DefaultMutableTreeNode Node2 = (DefaultMutableTreeNode)m_DeviceRootResource.getChildAt(i);
                String AnotherName = Node2.toString();

                //判断当前是否拥有该设备录像回放的权限
                boolean ifHaveSubAuthority = CommonParas.ifHaveSubAuthorityAnotherName(CommonParas.AuthorityItems.AUTHORITY_PLAYBACK, AnotherName, sFileName);
                if (!ifHaveSubAuthority) {ListTreeNodeDelete.add(Node2); continue;}//
                
                //第一个子节点的名称
                String SubNodeName0 = ((DefaultMutableTreeNode)Node2.getChildAt(0)).toString();
                if (SubNodeName0.lastIndexOf("IP") < 0) continue;//如果不包含IP，说明不是IP设备
                
                for (int k=0;k<Node2.getChildCount();k++){
                    DefaultMutableTreeNode Node22 = (DefaultMutableTreeNode)Node2.getChildAt(k);
                    String NodeName22 = Node22.toString();
                    String[] Splits = NodeName22.split("_");//Splits[1]为接入设备的别名
                    //判断当前是否拥有该设备录像回放的权限
                    boolean ifHaveSubAuthority2 = CommonParas.ifHaveSubAuthorityAnotherName(CommonParas.AuthorityItems.AUTHORITY_PLAYBACK, Splits[1], sFileName);
                    if (!ifHaveSubAuthority2) ListTreeNodeDelete2.add(Node22); 
                }
                
            }
            //删除权限之外的设备节点
            for (int i=0;i<ListTreeNodeDelete.size();i++){
                ListTreeNodeDelete.get(i).removeFromParent();
            }
            //删除权限之外的设备子节点
            for (int i=0;i<ListTreeNodeDelete2.size();i++){
                ListTreeNodeDelete2.get(i).removeFromParent();
            }
            DefaultTreeModel TreeModel = ((DefaultTreeModel) jTreeResource.getModel());//获取树模型
            TreeModel.reload();//将添加的节点显示到界面
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "delNoAuthorityDVR()","系统在删除权限之外的设备节点过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      ifHaveDownload
        * 函数描述:  判断是否拥有下载录像的权限
    */
    private boolean ifHaveDownload(){
        try{
            if (sAnotherName.equals("")) return false;

            if (sChannelNode.lastIndexOf("IP") > -1){
                String[] Splits = sChannelNode.split("_");//Splits[1]为接入设备的别名
                //判断当前是否拥有该接入设备录像回放的权限
                return CommonParas.showNoAuthorityMessage(rootPane, CommonParas.AuthorityItems.AUTHORITY_PLAYBACK_DOWNLOAD,  Splits[1], sFileName);
            }else {
                //判断当前是否拥有该设备录像回放的权限
                return CommonParas.showNoAuthorityMessage(rootPane, CommonParas.AuthorityItems.AUTHORITY_PLAYBACK_DOWNLOAD,  sAnotherName, sFileName);
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "delNoAuthorityDVR()","系统在删除权限之外的设备节点过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
 
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialogVolumeCtrl = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jSlider2 = new javax.swing.JSlider();
        jSplitPanePlayBack = new javax.swing.JSplitPane();
        jPanelTree = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabelChannel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeResource = new javax.swing.JTree();
        jPanelRight = new javax.swing.JPanel();
        jPanelSearch = new javax.swing.JPanel();
        jToggleButtonExit = new javax.swing.JToggleButton();
        jLabelTimeStartStop = new javax.swing.JLabel();
        jPanelTime = new javax.swing.JPanel();
        jLabelRecordType = new javax.swing.JLabel();
        jComboBoxRecodType = new javax.swing.JComboBox<>();
        jScrollPaneFileList = new javax.swing.JScrollPane();
        jTableFileList = new javax.swing.JTable();
        jPanelPlayBackContainer = new javax.swing.JPanel();
        jPanelSysTray = new javax.swing.JPanel();
        panelCtrTools1 = new java.awt.Panel();
        jToolBarPlayBack = new javax.swing.JToolBar();
        jPanel10 = new javax.swing.JPanel();
        jButtonsyn = new javax.swing.JButton();
        jButtonaysn = new javax.swing.JButton();
        jButtonAllPlay = new javax.swing.JButton();
        jButtonAllStop = new javax.swing.JButton();
        jButtonAllSlow = new javax.swing.JButton();
        jTextFieldAllSpeed = new javax.swing.JTextField();
        jButtonAllFast = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButtonDownloadByFile = new javax.swing.JButton();
        jButtonRecordCut = new javax.swing.JButton();
        jButtonDownloadByTime = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jSliderTimeLine = new javax.swing.JSlider();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabelDVRNodeName2 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jProgressBar = new javax.swing.JProgressBar();
        jPanel6 = new javax.swing.JPanel();
        jButtonPreviousDay = new javax.swing.JButton();
        jLabelPlayBackDay = new javax.swing.JLabel();
        jButtonNextDay = new javax.swing.JButton();

        jDialogVolumeCtrl.setUndecorated(true);
        jDialogVolumeCtrl.setResizable(false);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/broadcast.jpg"))); // NOI18N
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);

        jSlider2.setOrientation(javax.swing.JSlider.VERTICAL);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
            .addComponent(jSlider2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
        );

        jDialogVolumeCtrl.getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("远程回放");
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jSplitPanePlayBack.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSplitPanePlayBackPropertyChange(evt);
            }
        });

        jPanelTree.setBackground(new java.awt.Color(55, 54, 59));
        jPanelTree.setPreferredSize(new java.awt.Dimension(399, 670));
        jPanelTree.setLayout(new java.awt.BorderLayout());

        jPanel8.setBackground(new java.awt.Color(55, 54, 59));
        jPanel8.setForeground(new java.awt.Color(255, 255, 255));

        jLabelChannel.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelChannel.setForeground(new java.awt.Color(255, 255, 255));
        jLabelChannel.setText("   监控点");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabelChannel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelChannel, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanelTree.add(jPanel8, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setBackground(new java.awt.Color(64, 64, 64));
        jScrollPane1.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(390, 322));

        jTreeResource.setBackground(new java.awt.Color(64, 64, 64));
        jTreeResource.setForeground(new java.awt.Color(255, 255, 255));
        jTreeResource.setModel(this.initialResourceTreeModel());
        jTreeResource.setMaximumSize(new java.awt.Dimension(400, 48));
        jTreeResource.setMinimumSize(new java.awt.Dimension(300, 0));
        jTreeResource.setPreferredSize(new java.awt.Dimension(399, 48));
        jTreeResource.setRootVisible(false);
        jTreeResource.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeResourceMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTreeResource);

        jPanelTree.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jSplitPanePlayBack.setLeftComponent(jPanelTree);

        jPanelRight.setLayout(new java.awt.BorderLayout());

        jPanelSearch.setBackground(new java.awt.Color(55, 54, 59));
        jPanelSearch.setForeground(new java.awt.Color(255, 255, 255));

        jToggleButtonExit.setBackground(new java.awt.Color(64, 64, 64));
        jToggleButtonExit.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jToggleButtonExit.setForeground(new java.awt.Color(255, 255, 255));
        jToggleButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/exit.png"))); // NOI18N
        jToggleButtonExit.setText("退出");
        jToggleButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonExitActionPerformed(evt);
            }
        });

        jLabelTimeStartStop.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelTimeStartStop.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTimeStartStop.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTimeStartStop.setText("起止时间：");

        jPanelTime.setBackground(new java.awt.Color(151, 151, 151));
        jPanelTime.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanelTimeLayout = new javax.swing.GroupLayout(jPanelTime);
        jPanelTime.setLayout(jPanelTimeLayout);
        jPanelTimeLayout.setHorizontalGroup(
            jPanelTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 125, Short.MAX_VALUE)
        );
        jPanelTimeLayout.setVerticalGroup(
            jPanelTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabelRecordType.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelRecordType.setForeground(new java.awt.Color(255, 255, 255));
        jLabelRecordType.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelRecordType.setText("录像类型：");

        jComboBoxRecodType.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jComboBoxRecodType.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
                jComboBoxRecodTypePopupMenuCanceled(evt);
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                jComboBoxRecodTypePopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        jComboBoxRecodType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxRecodTypeItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelSearchLayout = new javax.swing.GroupLayout(jPanelSearch);
        jPanelSearch.setLayout(jPanelSearchLayout);
        jPanelSearchLayout.setHorizontalGroup(
            jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTimeStartStop, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelRecordType, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxRecodType, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 444, Short.MAX_VALUE)
                .addComponent(jToggleButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelSearchLayout.setVerticalGroup(
            jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTimeStartStop, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelSearchLayout.createSequentialGroup()
                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelRecordType, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxRecodType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jToggleButtonExit))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanelRight.add(jPanelSearch, java.awt.BorderLayout.NORTH);

        jScrollPaneFileList.setPreferredSize(new java.awt.Dimension(202, 402));

        jTableFileList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableFileList.setToolTipText("");
        jTableFileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableFileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableFileListMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTableFileListMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTableFileListMouseExited(evt);
            }
        });
        jTableFileList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jTableFileListMouseMoved(evt);
            }
        });
        jScrollPaneFileList.setViewportView(jTableFileList);

        jPanelRight.add(jScrollPaneFileList, java.awt.BorderLayout.EAST);
        jPanelRight.add(jPanelPlayBackContainer, java.awt.BorderLayout.CENTER);

        jPanelSysTray.setLayout(new java.awt.BorderLayout());

        panelCtrTools1.setLayout(new java.awt.BorderLayout());

        jToolBarPlayBack.setBackground(new java.awt.Color(151, 151, 151));
        jToolBarPlayBack.setForeground(new java.awt.Color(255, 255, 255));
        jToolBarPlayBack.setRollover(true);

        jPanel10.setBackground(new java.awt.Color(151, 151, 151));
        jPanel10.setForeground(new java.awt.Color(255, 255, 255));
        jPanel10.setLayout(new java.awt.GridLayout(1, 0));

        jButtonsyn.setBackground(new java.awt.Color(64, 64, 64));
        jButtonsyn.setForeground(new java.awt.Color(255, 255, 255));
        jButtonsyn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/syn.png"))); // NOI18N
        jButtonsyn.setToolTipText("同步回放");
        jButtonsyn.setBorderPainted(false);
        jButtonsyn.setContentAreaFilled(false);
        jButtonsyn.setFocusable(false);
        jButtonsyn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonsyn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonsyn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonsynActionPerformed(evt);
            }
        });
        jPanel10.add(jButtonsyn);

        jButtonaysn.setBackground(new java.awt.Color(64, 64, 64));
        jButtonaysn.setForeground(new java.awt.Color(255, 255, 255));
        jButtonaysn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/asyn.png"))); // NOI18N
        jButtonaysn.setToolTipText("异步回放");
        jButtonaysn.setBorderPainted(false);
        jButtonaysn.setFocusable(false);
        jButtonaysn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonaysn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonaysn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonaysnActionPerformed(evt);
            }
        });
        jPanel10.add(jButtonaysn);

        jToolBarPlayBack.add(jPanel10);

        jButtonAllPlay.setBackground(new java.awt.Color(151, 151, 151));
        jButtonAllPlay.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jButtonAllPlay.setForeground(new java.awt.Color(255, 255, 255));
        jButtonAllPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/pause.png"))); // NOI18N
        jButtonAllPlay.setToolTipText("暂停");
        jButtonAllPlay.setBorderPainted(false);
        jButtonAllPlay.setEnabled(false);
        jButtonAllPlay.setFocusable(false);
        jButtonAllPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAllPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAllPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAllPlayActionPerformed(evt);
            }
        });
        jToolBarPlayBack.add(jButtonAllPlay);

        jButtonAllStop.setBackground(new java.awt.Color(151, 151, 151));
        jButtonAllStop.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jButtonAllStop.setForeground(new java.awt.Color(255, 255, 255));
        jButtonAllStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/stop.png"))); // NOI18N
        jButtonAllStop.setToolTipText("全部停止");
        jButtonAllStop.setBorderPainted(false);
        jButtonAllStop.setFocusable(false);
        jButtonAllStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAllStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAllStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAllStopActionPerformed(evt);
            }
        });
        jToolBarPlayBack.add(jButtonAllStop);

        jButtonAllSlow.setBackground(new java.awt.Color(151, 151, 151));
        jButtonAllSlow.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jButtonAllSlow.setForeground(new java.awt.Color(255, 255, 255));
        jButtonAllSlow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/slow.png"))); // NOI18N
        jButtonAllSlow.setToolTipText("慢放");
        jButtonAllSlow.setBorderPainted(false);
        jButtonAllSlow.setEnabled(false);
        jButtonAllSlow.setFocusable(false);
        jButtonAllSlow.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAllSlow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAllSlow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAllSlowActionPerformed(evt);
            }
        });
        jToolBarPlayBack.add(jButtonAllSlow);

        jTextFieldAllSpeed.setEditable(false);
        jTextFieldAllSpeed.setBackground(new java.awt.Color(151, 151, 151));
        jTextFieldAllSpeed.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jTextFieldAllSpeed.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldAllSpeed.setText("  1x  ");
        jTextFieldAllSpeed.setBorder(null);
        jTextFieldAllSpeed.setOpaque(false);
        jToolBarPlayBack.add(jTextFieldAllSpeed);

        jButtonAllFast.setBackground(new java.awt.Color(151, 151, 151));
        jButtonAllFast.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jButtonAllFast.setForeground(new java.awt.Color(255, 255, 255));
        jButtonAllFast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/fast.png"))); // NOI18N
        jButtonAllFast.setToolTipText("快放");
        jButtonAllFast.setBorderPainted(false);
        jButtonAllFast.setEnabled(false);
        jButtonAllFast.setFocusable(false);
        jButtonAllFast.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAllFast.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAllFast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAllFastActionPerformed(evt);
            }
        });
        jToolBarPlayBack.add(jButtonAllFast);

        panelCtrTools1.add(jToolBarPlayBack, java.awt.BorderLayout.CENTER);

        jPanelSysTray.add(panelCtrTools1, java.awt.BorderLayout.WEST);

        jPanel1.setBackground(new java.awt.Color(151, 151, 151));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jButtonDownloadByFile.setBackground(new java.awt.Color(64, 64, 64));
        jButtonDownloadByFile.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonDownloadByFile.setForeground(new java.awt.Color(255, 255, 255));
        jButtonDownloadByFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/downloadbyfile.png"))); // NOI18N
        jButtonDownloadByFile.setText("下载文件");
        jButtonDownloadByFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadByFileActionPerformed(evt);
            }
        });

        jButtonRecordCut.setBackground(new java.awt.Color(64, 64, 64));
        jButtonRecordCut.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonRecordCut.setForeground(new java.awt.Color(255, 255, 255));
        jButtonRecordCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/videoclip.png"))); // NOI18N
        jButtonRecordCut.setText("录像剪辑");
        jButtonRecordCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecordCutActionPerformed(evt);
            }
        });

        jButtonDownloadByTime.setBackground(new java.awt.Color(64, 64, 64));
        jButtonDownloadByTime.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonDownloadByTime.setForeground(new java.awt.Color(255, 255, 255));
        jButtonDownloadByTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/downloadbytime.png"))); // NOI18N
        jButtonDownloadByTime.setText("按时间下载");
        jButtonDownloadByTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadByTimeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jButtonDownloadByTime)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 374, Short.MAX_VALUE)
                .addComponent(jButtonRecordCut)
                .addGap(46, 46, 46)
                .addComponent(jButtonDownloadByFile)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 8, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonDownloadByTime, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonRecordCut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonDownloadByFile, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanelSysTray.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(64, 64, 64));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jSliderTimeLine.setBackground(new java.awt.Color(64, 64, 64));
        jSliderTimeLine.setForeground(new java.awt.Color(255, 255, 255));
        jSliderTimeLine.setMajorTickSpacing(60);
        jSliderTimeLine.setMaximum(86400);
        jSliderTimeLine.setMinorTickSpacing(1);
        jSliderTimeLine.setPaintLabels(true);
        jSliderTimeLine.setSnapToTicks(true);
        jSliderTimeLine.setToolTipText("");
        jSliderTimeLine.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jSliderTimeLine.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jSliderTimeLineMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jSliderTimeLineMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jSliderTimeLineMouseExited(evt);
            }
        });
        jSliderTimeLine.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jSliderTimeLineMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSliderTimeLine, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1166, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSliderTimeLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel5.setBackground(new java.awt.Color(151, 151, 151));
        jPanel5.setForeground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel7.setBackground(new java.awt.Color(151, 151, 151));
        jPanel7.setForeground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new java.awt.BorderLayout());

        jLabelDVRNodeName2.setBackground(new java.awt.Color(151, 151, 151));
        jLabelDVRNodeName2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelDVRNodeName2.setForeground(new java.awt.Color(255, 255, 255));
        jLabelDVRNodeName2.setText("                                                ");
        jPanel7.add(jLabelDVRNodeName2, java.awt.BorderLayout.CENTER);

        jPanel5.add(jPanel7, java.awt.BorderLayout.WEST);

        jPanel9.setBackground(new java.awt.Color(151, 151, 151));
        jPanel9.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jProgressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jProgressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel9, java.awt.BorderLayout.CENTER);

        jPanel6.setBackground(new java.awt.Color(151, 151, 151));
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));

        jButtonPreviousDay.setBackground(new java.awt.Color(64, 64, 64));
        jButtonPreviousDay.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonPreviousDay.setForeground(new java.awt.Color(255, 255, 255));
        jButtonPreviousDay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/pagefirst.png"))); // NOI18N
        jButtonPreviousDay.setText("上一日");
        jButtonPreviousDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousDayActionPerformed(evt);
            }
        });

        jLabelPlayBackDay.setBackground(new java.awt.Color(151, 151, 151));
        jLabelPlayBackDay.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jLabelPlayBackDay.setForeground(new java.awt.Color(255, 255, 255));
        jLabelPlayBackDay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPlayBackDay.setText("2016-08-29");

        jButtonNextDay.setBackground(new java.awt.Color(64, 64, 64));
        jButtonNextDay.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonNextDay.setForeground(new java.awt.Color(255, 255, 255));
        jButtonNextDay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/pagelast.png"))); // NOI18N
        jButtonNextDay.setText("下一日");
        jButtonNextDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextDayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonPreviousDay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPlayBackDay, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNextDay)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelPlayBackDay, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonNextDay)
                        .addComponent(jButtonPreviousDay)))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel6, java.awt.BorderLayout.EAST);

        jPanel2.add(jPanel5, java.awt.BorderLayout.PAGE_END);

        jPanelSysTray.add(jPanel2, java.awt.BorderLayout.PAGE_END);

        jPanelRight.add(jPanelSysTray, java.awt.BorderLayout.PAGE_END);

        jSplitPanePlayBack.setRightComponent(jPanelRight);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPanePlayBack, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1250, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPanePlayBack, javax.swing.GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDownloadByFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadByFileActionPerformed
        //此操作只是下载文件，主要考虑在比如单独设置报警录像时有用。比如有的单个录像文件就是一个报警录像，此时下载比较方便。
        try{
            ChannelPlayBack channelPlayBack = getCurrentChannelPlayBack();
            if (channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0)  return;
            //如果没有下载录像的权限，则返回
            if (!ifHaveDownload()) return;
            
            //如果不在下载,开始下载
            if (m_lDownloadHandle.intValue() == -1){
                //未选择文件时提示选择要下载的文件
                int Row = jTableFileList.getSelectedRow();

                if (Row == -1){
                    JOptionPane.showMessageDialog(this, sSelectDownloadFile);//"请选择要下载的文件");
                    return;
                }

                //获取文件名
                DefaultTableModel FileTableModel = ((DefaultTableModel) jTableFileList.getModel());
                String FileName = FileTableModel.getValueAt(Row, 0).toString();
                //得到存储路径，自动命名文件名
                String PathName = CommonParas.SysParas.getSysParasFileSaveDirVedio();
                //命名规则：设备名_通道号_开始时间_终止时间.mp4/jpg
//                String NewFileName = PathName + "\\" + sAnotherName + "_" + iChannel +  "_" + (String)FileTableModel.getValueAt(Row,4) 
//                                    + "_" + (String)FileTableModel.getValueAt(Row,5) + ".mp4";
                String NewFileName = channelPlayBack.getRecordDownloadName( PathName, (String)FileTableModel.getValueAt(Row,4), (String)FileTableModel.getValueAt(Row,5)) + ".mp4";
                
                String Remarks = FileName + "；" + NewFileName;
                //设置附加备注：下载录像文件
                channelPlayBack.setRemarks2(Remarks);
                
                m_lDownloadHandle = CommonParas.hCNetSDK.NET_DVR_GetFileByName(m_lUserID, FileName, NewFileName);
                if (m_lDownloadHandle.intValue() >= 0){
                    CommonParas.hCNetSDK.NET_DVR_PlayBackControl(m_lDownloadHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
                    jButtonDownloadByFile.setText(sStopDownload);//"停止下载");
                    jButtonDownloadByFile.setIcon(ImageIconBufferPool.getInstance().getImageIcon("stoprun.png"));
                    jProgressBar.setValue(0);
                    jProgressBar.setVisible(true);

                    //开始计时器
                    Downloadtimer = new Timer();//新建定时器
                    Downloadtimer.schedule(new DownloadTask(sDownloadFile, "downloadbyfile.png", jButtonDownloadByFile, channelPlayBack), 0, 1000);//"下载文件"0秒后开始响应函数
                } else{
                    channelPlayBack.writePlayBackErrorLog(sRecordFileDownload_Fail);//"录像文件下载失败");
                    CommonParas.showErrorMessage(sRecordFileDownload_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"录像文件下载失败"
                    //JOptionPane.showMessageDialog(this, "下载文件失败");
                }
            }else{//如果在下载,停止下载
                CommonParas.hCNetSDK.NET_DVR_StopGetFile(m_lDownloadHandle);
                m_lDownloadHandle.setValue(-1);
                jButtonDownloadByFile.setText(sDownloadFile);//"下载文件");
                jButtonDownloadByFile.setIcon(ImageIconBufferPool.getInstance().getImageIcon("downloadbyfile.png"));
                jProgressBar.setValue(0);
                jProgressBar.setVisible(false);
                Downloadtimer.cancel();
                CommonParas.showMessage(sStopDownloadColon + channelPlayBack.getRemarks2(), sFileName);//"停止下载："
                channelPlayBack.writePlayBackLog(sRecordFileDownload);//"录像文件下载");
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jButtonDownloadByFileActionPerformed()","系统在下载录像文件过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }//GEN-LAST:event_jButtonDownloadByFileActionPerformed

    private void jToggleButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonExitActionPerformed
        if (getIfExistPlayBack()){
                if ((JOptionPane.showConfirmDialog(this, sMessageBoxExitPlayback,//"正在录像回放，退出就要停止回放。是否要继续？",  
                        sRemind,JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)) return;//"提醒"
                stopAllPanels();
        }
        dispose();
        for (JFrame ArrayFrame1 : CommonParas.g_ListJFrame) {
            if (ArrayFrame1 instanceof  JFramePlayBack){
                CommonParas.g_ListJFrame.remove(ArrayFrame1);
                return;
            }
        }
    }//GEN-LAST:event_jToggleButtonExitActionPerformed

    private void jTableFileListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFileListMouseClicked
        // TODO add your handling code here:
        try{
            if (evt.getClickCount() == 2){//双击
                //playBackByFile();//回放视频文件
                ChannelPlayBack channelPlayBack = getCurrentChannelPlayBack();
                int IndexOfCurrentJPanel = getIndexOfCurrentJPanel();
                if (channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0)  return;
                //获取文件名
                int Row = jTableFileList.getSelectedRow();
                if (Row < 0) return;
                DefaultTableModel FileTableModel = ((DefaultTableModel) jTableFileList.getModel());
                String StartTime = FileTableModel.getValueAt(Row, 2).toString();
                DateHandler newDate = DateHandler.getTimeStringInstance(StartTime);

                HCNetSDK.NET_DVR_TIME StruAbsoluteTime = new HCNetSDK.NET_DVR_TIME();
                StruAbsoluteTime.dwYear = newDate.getYear();
                StruAbsoluteTime.dwMonth = newDate.getMonth();
                StruAbsoluteTime.dwDay = newDate.getDay();
                StruAbsoluteTime.dwHour = newDate.getHour();
                StruAbsoluteTime.dwMinute = newDate.getMinute();
                StruAbsoluteTime.dwSecond = newDate.getSecond();

                String SDate = StartTime.substring(0, 10);//日期字符串
                if (!jLabelPlayBackDay.getText().trim().equals(SDate)){
                    refreshRecordInfo(channelPlayBack, new MyDate(StruAbsoluteTime.dwYear, StruAbsoluteTime.dwMonth, StruAbsoluteTime.dwDay));
                }
                
                if (ifSyn) synTimeAllPlayWindows(StruAbsoluteTime);
                else 
                    listJPanelPlayWindow.get(IndexOfCurrentJPanel).setTimeOnePanel(StruAbsoluteTime);
                    //setTimeOnePlayWindow(IndexOfCurrentJPanel, StruAbsoluteTime);//将该回放窗口进行按绝对时间定位
//                //开始按绝对时间定位（录像回放）
//                IntByReference lpOutLen = new IntByReference(0);
//
//                StruAbsoluteTime.write();
//                Pointer lpAbsoluteTime = StruAbsoluteTime.getPointer();
//                Pointer lpOut = null;
//
//                boolean bSetTime = CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(channelPlayBack.getPlayBackHandle(), HCNetSDK.NET_DVR_PLAYSETTIME, lpAbsoluteTime, StruAbsoluteTime.size(), Pointer.NULL, lpOutLen);
//                StruAbsoluteTime.read();
//                if (!bSetTime){
//                    channelPlayBack.writePlayBackErrorLog("录像回放按绝对时间定位失败");
//                    CommonParas.showErrorMessage("录像回放按绝对时间定位失败", channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);
//                }
                //jLabel2.setText("长度："+jSlider1.getWidth()+"；位置："+ evt.getX());
                int Value2 = getValueFromTime(StruAbsoluteTime.dwHour, StruAbsoluteTime.dwMinute, StruAbsoluteTime.dwSecond);
                jSliderTimeLine.setValue(Value2);
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jTableFileListMouseClicked()","系统在录像回放文件过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        
    }//GEN-LAST:event_jTableFileListMouseClicked

    private void jButtonAllPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAllPlayActionPerformed
        // TODO add your handling code here:
        if (!ifSyn) return;
        if(!getIfExistPlayBack()) return;
        
        playAllPanels();
        synOCXState();
//        /*
//            1、正向，无回放
//            2、正向、正在回放
//            3、正向，暂停
//            4、反向、正在回放
//            5、反向、暂停
//            6、反向、无回放
//        */
//        //private int iPlayState = 0;//正在现在的状态：HCNetSDK.NET_DVR_PLAYSTART播放1/HCNetSDK.NET_DVR_PLAYPAUSE暂停3/初始状态0
//        if (iPlayDirection == HCNetSDK.NET_DVR_PLAY_FORWARD && iPlayState == 0){//如果为正向，而且无回放
//            iPlayState = HCNetSDK.NET_DVR_PLAYPAUSE;
//        }else if (iPlayDirection == HCNetSDK.NET_DVR_PLAY_FORWARD && iPlayState == HCNetSDK.NET_DVR_PLAYSTART){//如果为正向，而且正在回放，则只做暂停处理
//            iPlayState = HCNetSDK.NET_DVR_PLAYPAUSE;
//        }else if (iPlayDirection == HCNetSDK.NET_DVR_PLAY_FORWARD && iPlayState == HCNetSDK.NET_DVR_PLAYPAUSE){//如果为正向，而且在暂停，则只做恢复播放处理
//            iPlayState = HCNetSDK.NET_DVR_PLAYSTART;
//        }else if (iPlayDirection == HCNetSDK.NET_DVR_PLAY_REVERSE && iPlayState == HCNetSDK.NET_DVR_PLAYSTART){//如果为倒向，而且正在回放，则只做倒放切换为正放处理，回放速度变为1
//            iPlayDirection = HCNetSDK.NET_DVR_PLAY_FORWARD;
//        }else if (iPlayDirection == HCNetSDK.NET_DVR_PLAY_REVERSE && iPlayState == HCNetSDK.NET_DVR_PLAYPAUSE){//如果为倒向，而且正在暂停，则做2步处理：正放切换为倒放；恢复播放处理，回放速度变为1
//            iPlayDirection = HCNetSDK.NET_DVR_PLAY_FORWARD;
//            iPlayState = HCNetSDK.NET_DVR_PLAYSTART;
//        }else if (iPlayDirection == HCNetSDK.NET_DVR_PLAY_REVERSE && iPlayState == 0){//如果为倒向，而且无回放
//            iPlayState = HCNetSDK.NET_DVR_PLAYPAUSE;
//        }
//        
        if (iPlayDirection == HCNetSDK.NET_DVR_PLAY_FORWARD && iPlayState == HCNetSDK.NET_DVR_PLAYSTART){
            jButtonAllPlay.setIcon(ImageIconBufferPool.getInstance().getImageIcon("pause.png"));
            jButtonAllPlay.setToolTipText(sPause);//"暂停");

        }else {
            jButtonAllPlay.setIcon(ImageIconBufferPool.getInstance().getImageIcon("play.png"));
            jButtonAllPlay.setToolTipText(sForwardPlayback);//"正向回放");
        }
//        setEnable();
        
    }//GEN-LAST:event_jButtonAllPlayActionPerformed

    private void jButtonAllStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAllStopActionPerformed
        // TODO add your handling code here:
        stopAllPanels();
    }//GEN-LAST:event_jButtonAllStopActionPerformed

    private void jButtonAllSlowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAllSlowActionPerformed
        // TODO add your handling code here:
        setSlowPlayAllPanels();
    }//GEN-LAST:event_jButtonAllSlowActionPerformed

    private void jButtonAllFastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAllFastActionPerformed
        // TODO add your handling code here:
        setFastPlayAllPanels();
    }//GEN-LAST:event_jButtonAllFastActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        if (getIfExistPlayBack()){
//            if (JOptionPane.showConfirmDialog(this, "正在录像回放，退出就要停止回放。是否要继续？",  
//                        "提醒",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)  return;
            stopAllPanels();
        }
        this.dispose();
        for (JFrame ArrayFrame1 : CommonParas.g_ListJFrame) {
            if (ArrayFrame1 instanceof  JFramePlayBack){
                CommonParas.g_ListJFrame.remove(ArrayFrame1);
                return;
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void jTableFileListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFileListMouseMoved
        // TODO add your handling code here:
//        TableColumnModel columnModel = jTableFileList.getColumnModel();
//        int column = columnModel.getColumnIndexAtX(evt.getX());
        if(fileListModel.getRowCount()<1) return;
        
        final int focusRow = evt.getY() / jTableFileList.getRowHeight();
        if (focusRow >=fileListModel.getRowCount() || focusRow <0 ) return;
        int FileType = Integer.parseInt(fileListModel.getValueAt(focusRow, 6).toString());
        String StartTime = fileListModel.getValueAt(focusRow,2).toString();
        String StopTime = fileListModel.getValueAt(focusRow,3).toString();
        jTableFileList.getColumn(tableTitle[2]).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell=super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row == table.getSelectedRow()) cell.setBackground(new Color(200, 200, 200));
                else if (row == focusRow) cell.setBackground(new Color(100, 100, 100));
                else cell.setBackground(table.getBackground());
                
                return cell;
            }
        });
//        jTableFileList.setSelectionBackground(new Color(200, 200, 200));
//                jTableFileList.setSelectionForeground(new Color(255, 255, 255));
//        jTableFileList.setRowSelectionInterval(focusRow,focusRow);
//        //labelFileDetail.setVisible(true);
////        labelFileDetail.setText("行号："+ row + "；列号：" + column);
//        //labelFileDetail.setBounds(evt.getX()+jTableFileList.getX(),evt.getY()+jTableFileList.getY(),200,20);
        myGlassPaneAera.setPoint(SwingUtilities.convertPoint(jTableFileList,jTableFileList.getX(),evt.getY()+jTableFileList.getRowHeight(), myGlassPaneAera));
        //String TimeString = getTimeString((evt.getX()-iSpace) * jSliderTimeLine.getMaximum()/(jSliderTimeLine.getWidth()-iSpace*2));
        
        myGlassPaneAera.setToolTipMessage(MessageFormat.format(sRecordFileInfo, StartTime, StopTime) + recordFileTypeNames[FileType]);//"开始："+StartTime+"<br>结束："+StopTime+"<br>类型："
        myGlassPaneAera.repaint();
    }//GEN-LAST:event_jTableFileListMouseMoved

    private void jButtonDownloadByTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadByTimeActionPerformed
        // TODO add your handling code here:m_lDownloadHandle
        try{
            ChannelPlayBack channelPlayBack = getCurrentChannelPlayBack();
            if (channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0)  return;
            
            //如果没有下载录像的权限，则返回
            if (!ifHaveDownload()) return;
            
            if (m_lDownloadHandle.intValue() == -1) {
                if (sAnotherName.equals("") || iChannel < 0) return;
                HCNetSDK.NET_DVR_TIME struStartTime = channelPlayBack.getStruStartTime();
                HCNetSDK.NET_DVR_TIME struStopTime = channelPlayBack.getStruStopTime();

//                struStartTime = new HCNetSDK.NET_DVR_TIME();
//                struStopTime = new HCNetSDK.NET_DVR_TIME();
//                String StrTime1 = buttonTime1.getText();
//                String StrTime2 = buttonTime2.getText();
//                struStartTime.dwYear = Integer.parseInt(StrTime1.substring(0, 4));//开始时间
//                struStartTime.dwMonth = Integer.parseInt(StrTime1.substring(5, 7));
//                struStartTime.dwDay = Integer.parseInt(StrTime1.substring(8, 10));
//                struStartTime.dwHour = Integer.parseInt(StrTime1.substring(11, 13));
//                struStartTime.dwMinute = Integer.parseInt(StrTime1.substring(14, 16));
//                struStartTime.dwSecond = Integer.parseInt(StrTime1.substring(17, 19));
//
//                struStopTime.dwYear = Integer.parseInt(StrTime2.substring(0, 4));////结束时间
//                struStopTime.dwMonth = Integer.parseInt(StrTime2.substring(5, 7));
//                struStopTime.dwDay = Integer.parseInt(StrTime2.substring(8, 10));
//                struStopTime.dwHour = Integer.parseInt(StrTime2.substring(11, 13));
//                struStopTime.dwMinute = Integer.parseInt(StrTime2.substring(14, 16));
//                struStopTime.dwSecond = Integer.parseInt(StrTime2.substring(17, 19));


                //得到存储路径，自动命名文件名
                String PathName = CommonParas.SysParas.getSysParasFileSaveDirVedio();
                //命名规则：设备名_通道号_开始时间_终止时间.mp4/jpg
//                String NewFileName = PathName + "\\" + sAnotherName + "_" + iChannel +  "_" + struStartTime.toStringTitle()
//                                    + "_" +  struStopTime.toStringTitle() + ".mp4";
                String NewFileName = channelPlayBack.getRecordDownloadName( PathName, struStartTime.toStringTitle(), struStopTime.toStringTitle()) + ".mp4";
                //sRemarks = NewFileName;
                //设置附加备注：下载录像文件
                channelPlayBack.setRemarks2(NewFileName);

                m_lDownloadHandle = CommonParas.hCNetSDK.NET_DVR_GetFileByTime(m_lUserID, new NativeLong(iChannel), struStartTime, struStopTime, NewFileName);
                if (m_lDownloadHandle.intValue() >= 0) {
                    CommonParas.hCNetSDK.NET_DVR_PlayBackControl(m_lDownloadHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
                    jButtonDownloadByTime.setText(sStopDownload);//"停止下载");
                    jButtonDownloadByTime.setIcon(ImageIconBufferPool.getInstance().getImageIcon("stoprun.png"));
                    jProgressBar.setValue(0);
                    jProgressBar.setVisible(true);
                    Downloadtimer = new Timer();//新建定时器
                    Downloadtimer.schedule(new DownloadTask(sDownloadByTime, "downloadbytime.png", jButtonDownloadByTime, channelPlayBack), 0, 5000);//"按时间下载"0秒后开始响应函数
                } else {
                    channelPlayBack.writePlayBackErrorLog(sDownloadRecordByTime_Fail);//"按时间下载录像失败");
                    CommonParas.showErrorMessage(sDownloadRecordByTime_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"按时间下载录像失败"
                    //JOptionPane.showMessageDialog(this, "按时间下载失败");
                    //System.out.println("laste error " + CommonParas.hCNetSDK.NET_DVR_GetLastError());
                }
            }else {
                CommonParas.hCNetSDK.NET_DVR_StopGetFile(m_lDownloadHandle);
                jButtonDownloadByTime.setText(sDownloadByTime);//"按时间下载");
                jButtonDownloadByTime.setIcon(ImageIconBufferPool.getInstance().getImageIcon("downloadbytime.png"));
                Downloadtimer.cancel();
                m_lDownloadHandle.setValue(-1);
                jProgressBar.setValue(0);
                jProgressBar.setVisible(false);
                CommonParas.showMessage(sStopDownloadColon + channelPlayBack.getRemarks2(), sFileName);//"停止下载："
                channelPlayBack.writePlayBackLog(sStopDownloadColon);//"按时间下载录像");
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jButtonDownloadByTimeActionPerformed()","系统在根据起止时间段下载录像文件过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }//GEN-LAST:event_jButtonDownloadByTimeActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        CommonParas.g_RootPane = rootPane;
    }//GEN-LAST:event_formWindowActivated

    private void jSliderTimeLineMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderTimeLineMouseClicked
        // TODO add your handling code here:
        try{
            ChannelPlayBack channelPlayBack = getCurrentChannelPlayBack();
            int IndexOfCurrentJPanel = getIndexOfCurrentJPanel();
            if(channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0) return;

            int Value2 = (evt.getX()-iSpace) * jSliderTimeLine.getMaximum()/(jSliderTimeLine.getWidth()-iSpace*2);
            String TimeString = jLabelPlayBackDay.getText() + " "+ getTimeString(Value2);
            DateHandler newDate = DateHandler.getTimeStringInstance(TimeString);
            HCNetSDK.NET_DVR_TIME StruAbsoluteTime = new HCNetSDK.NET_DVR_TIME();
            StruAbsoluteTime.dwYear = newDate.getYear();
            StruAbsoluteTime.dwMonth = newDate.getMonth();
            StruAbsoluteTime.dwDay = newDate.getDay();
            StruAbsoluteTime.dwHour = newDate.getHour();
            StruAbsoluteTime.dwMinute = newDate.getMinute();
            StruAbsoluteTime.dwSecond = newDate.getSecond();
            //必须排除在起止时间之外的点击；
            if (StruAbsoluteTime.compareTo(channelPlayBack.getStruStartTime()) < 0 || StruAbsoluteTime.compareTo(channelPlayBack.getStruStopTime()) > 0 ) return;
            //必须排除录像缺失的时间。
            ArrayList<HCNetSDK.NET_DVR_TIME_EX> listRecordLostStartTime = channelPlayBack.getListRecordLostStartTime();//缺失录像的起始时间列表
            ArrayList<HCNetSDK.NET_DVR_TIME_EX> listRecordLostStopTime = channelPlayBack.getListRecordLostStopTime();//缺失录像的终止时间列表
            
            if (listRecordLostStartTime.size() != listRecordLostStopTime.size()) return;
            for (int i=0;i<listRecordLostStartTime.size();i++){
                HCNetSDK.NET_DVR_TIME_EX StartTime2 = listRecordLostStartTime.get(i);
                HCNetSDK.NET_DVR_TIME_EX StopTime2  = listRecordLostStopTime.get(i);
                if (StruAbsoluteTime.compareTo(StartTime2) > 0 && StruAbsoluteTime.compareTo(StopTime2) < 0 ) return;
            }
            if (ifSyn) synTimeAllPlayWindows(StruAbsoluteTime);
            else 
                //setTimeOnePlayWindow(IndexOfCurrentJPanel, StruAbsoluteTime);//将该回放窗口进行按绝对时间定位
                listJPanelPlayWindow.get(IndexOfCurrentJPanel).setTimeOnePanel(StruAbsoluteTime);
//            //开始按绝对时间定位（回放录像）
//            IntByReference lpOutLen = new IntByReference(0);
//
//            StruAbsoluteTime.write();
//            Pointer lpAbsoluteTime = StruAbsoluteTime.getPointer();
//            Pointer lpOut = null;
//
//            boolean bSetTime = CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(channelPlayBack.getPlayBackHandle(), HCNetSDK.NET_DVR_PLAYSETTIME, lpAbsoluteTime, StruAbsoluteTime.size(), Pointer.NULL, lpOutLen);
//            StruAbsoluteTime.read();
//            if (!bSetTime){
//                channelPlayBack.writePlayBackErrorLog("回放录像按绝对时间定位失败");
//            }
            //jLabel2.setText("长度："+jSlider1.getWidth()+"；位置："+ evt.getX());
            jSliderTimeLine.setValue(Value2);
            //jLabel3.setText("position:"+evt.getX() * jSlider1.getMaximum()/jSlider1.getWidth());
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jSliderTimeLineMouseClicked()","用户在点击回放时间轴的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }//GEN-LAST:event_jSliderTimeLineMouseClicked

    private void jSliderTimeLineMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderTimeLineMouseMoved
        // TODO add your handling code here:
        //jLabel2.setText(""+evt.getX());
        myGlassPane.setPoint(SwingUtilities.convertPoint(jSliderTimeLine,evt.getX(),jSliderTimeLine.getHeight()/2-10, myGlassPane));
        String TimeString = getTimeString((evt.getX()-iSpace) * jSliderTimeLine.getMaximum()/(jSliderTimeLine.getWidth()-iSpace*2));
        myGlassPane.setToolTipMessage(TimeString);
        myGlassPane.repaint();
        //jLabel2.setLocation( evt.getLocationOnScreen());
    }//GEN-LAST:event_jSliderTimeLineMouseMoved

    private void jSliderTimeLineMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderTimeLineMouseEntered
        // TODO add your handling code here:
        this.setGlassPane(myGlassPane);
        Point contentPanePoint = SwingUtilities.convertPoint(jSliderTimeLine,evt.getPoint(), myGlassPane);
        myGlassPane.setPoint(contentPanePoint);
        myGlassPane.repaint();
        myGlassPane.setVisible(true);
    }//GEN-LAST:event_jSliderTimeLineMouseEntered

    private void jSliderTimeLineMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderTimeLineMouseExited
        // TODO add your handling code here:
        myGlassPane.setVisible(false);
    }//GEN-LAST:event_jSliderTimeLineMouseExited

    private void jButtonNextDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextDayActionPerformed
        // TODO add your handling code here:
        //显示录像完整性检测，如果还没有回放，JSlier也应该恢复默认的滑道显示
        DateUtil.DateHandler dd = DateUtil.DateHandler.getDateStringInstance(jLabelPlayBackDay.getText().trim());
        dd.nextDay();
        refreshRecordInfo(getCurrentChannelPlayBack(),new MyDate(dd.getYear(),dd.getMonth(),dd.getDay()));
    }//GEN-LAST:event_jButtonNextDayActionPerformed

    private void jButtonPreviousDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousDayActionPerformed
        // TODO add your handling code here:
        //显示录像完整性检测，如果还没有回放，JSlier也应该恢复默认的滑道显示
        DateUtil.DateHandler dd = DateUtil.DateHandler.getDateStringInstance(jLabelPlayBackDay.getText().trim());
        dd.previousDay();
        refreshRecordInfo(getCurrentChannelPlayBack(),new MyDate(dd.getYear(),dd.getMonth(),dd.getDay()));
    }//GEN-LAST:event_jButtonPreviousDayActionPerformed

    private void jTreeResourceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeResourceMouseClicked
        // TODO add your handling code here:
        //        DefaultMutableTreeNode  NewNode =(DefaultMutableTreeNode)jTreeResource.getSelectionPath().getLastPathComponent();
//        String GroupName = NewNode.toString();
//        int Level = NewNode.getLevel();//如果是在组名上则为1；在节点上则为2
        try {
            if (evt.getClickCount() == 2){
                
                DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) jTreeResource.getLastSelectedPathComponent();
                if (selectionNode == null) return;
                String nodeName = selectionNode.toString();
                
                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                // 判断是否为树叶节点，若是则读取设备通道录像参数，若不是则不做任何事。
                if (selectionNode.isLeaf()) {
                    sChannelNode = nodeName;
                    //设备名称
                    sAnotherName = sChannelNode.substring(0, sChannelNode.indexOf("_"));
                    indexOflistDevice = CommonParas.getIndexOfDeviceList(sAnotherName, sFileName);
                    sSerialNo = CommonParas.getSerialNO(sAnotherName, sFileName);

                    //通道号
                    iChannel = getChannelNumber(sChannelNode);
                    //读取设备通道录像参数
                    m_lUserID = CommonParas.getUserID(indexOflistDevice, sFileName);

                    //refreshFileList();//刷新要查询的录像文件列表
                    int IndexOfCurrentJPanel = getIndexOfNeedPlayBack();
                    if (searchFilesList(IndexOfCurrentJPanel)){
                        //refreshFileTableList(IndexOfCurrentJPanel);//playBackByTime函数中包含有这个函数
                        playBackByTime(IndexOfCurrentJPanel);//按时间回放录像文件
                        setEnable();
                    }
                }
            }

        }catch (Exception e){
            TxtLogger.append(sFileName, "jTreeResourceMouseClicked()","系统在进行录像回放过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jTreeResourceMouseClicked

    private void jButtonRecordCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecordCutActionPerformed
        // TODO add your handling code here:
        //downloadFlag录像剪辑（也就是下载）标志。0表示未开始；1表示剪辑了一下，起始时间；2表示剪辑了终止时间。
        ChannelPlayBack channelPlayBack = getCurrentChannelPlayBack();
        if (channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0) return;
        //如果没有下载录像的权限，则返回
        if (!ifHaveDownload()) return;
        
        if (downloadFlag == 0){
            struDownloadStartTime = getNetDVRTimeFromValue(jSliderTimeLine.getValue());
            downloadFlag = 1;
            jButtonRecordCut.setText(sVideoClipAgain);//"再次剪辑");
            jButtonRecordCut.setIcon(ImageIconBufferPool.getInstance().getImageIcon("videoclipagain.png"));
            return;
        }
        if (downloadFlag == 1){
            struDownloadStopTime = getNetDVRTimeFromValue(jSliderTimeLine.getValue());
            downloadFlag = 2;
            downloadSelectedTimes(channelPlayBack);
            return;
        }
        if (downloadFlag == 2 && m_lDownloadHandle.intValue() != -1){
            CommonParas.hCNetSDK.NET_DVR_StopGetFile(m_lDownloadHandle);
            jButtonRecordCut.setText(sVideoClip);//"录像剪辑");
            jButtonRecordCut.setIcon(ImageIconBufferPool.getInstance().getImageIcon("videoclip.png"));
            Downloadtimer.cancel();
            m_lDownloadHandle.setValue(-1);
            jProgressBar.setValue(0);
            jProgressBar.setVisible(false);
            CommonParas.showMessage(sStopDownloadColon + channelPlayBack.getRemarks2(), sFileName);//"停止下载："
            channelPlayBack.writePlayBackLog(sDownloadRecordByTime);//"按时间下载录像");//Remarks2在写完日志之后就变为""
            downloadFlag = 0;
        }
    }//GEN-LAST:event_jButtonRecordCutActionPerformed

    private void jTableFileListMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFileListMouseEntered
        // TODO add your handling code here:
        this.setGlassPane(myGlassPaneAera);
        Point contentPanePoint = SwingUtilities.convertPoint(jTableFileList,evt.getPoint(), myGlassPaneAera);
        myGlassPaneAera.setPoint(contentPanePoint);
        myGlassPaneAera.repaint();
        myGlassPaneAera.setVisible(true);
    }//GEN-LAST:event_jTableFileListMouseEntered

    private void jTableFileListMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFileListMouseExited
        // TODO add your handling code here:
        myGlassPaneAera.setVisible(false);
        jTableFileList.getColumn(tableTitle[2]).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell=super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row == table.getSelectedRow()) cell.setBackground(new Color(200, 200, 200));
                else cell.setBackground(table.getBackground());
                
                return cell;
            }
        });
    }//GEN-LAST:event_jTableFileListMouseExited

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        //设置自定义UI
            jyms.tools.TreeUtil.modifyTreeCellRenderer(jTreeResource);
            //CommonParas.CreateDeviceResourceTree(jTreeResource, m_DeviceRootResource, "", CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE, sFileName);//监控点
            CommonParas.CreateOneTypeDeviceResourceTree(jTreeResource, m_DeviceRootResource, "", CommonParas.DVRType.DVRTYPE_ENCODINGDVR_NVR_CODE, 
                    CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE, sFileName);//监控点
            
            jToolBarPlayBack.setUI(new ToolBarUI());
            
            mySliderUI = new SliderUI_Customize(jSliderTimeLine);
            jSliderTimeLine.setUI(mySliderUI);
    }//GEN-LAST:event_formWindowOpened

    private void jSplitPanePlayBackPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSplitPanePlayBackPropertyChange
        // TODO add your handling code here:
        /*监听这两个属性的改变都能获得同样的效果：
            1、DIVIDER_LOCATION_PROPERTY 绑定 dividerLocation 属性
            2、LAST_DIVIDER_LOCATION_PROPERTY 绑定 lastLocation 属性
        */
//        if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {     
        if (evt.getPropertyName().equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {     
            if (bZoomIn){
                //zoomInPreviewOneChannel();zoomInPreviewOneChannel();
                currentJPanel.setPreferredSize(new Dimension(jPanelPlayBackContainer.getWidth() - 4*iPanelBoderThickness, jPanelPlayBackContainer.getHeight()-4*iPanelBoderThickness));
            }
        } 
    }//GEN-LAST:event_jSplitPanePlayBackPropertyChange

    private void jButtonsynActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonsynActionPerformed
        // TODO add your handling code here:
        //如果点击之前就是“同步”状态，则不做任何操作
        if (!ifSyn) {
            ifSyn = true;//同步

            jButtonsyn.setContentAreaFilled(true);
            jButtonaysn.setContentAreaFilled(false);

            setEnable();
            synOhterPlayWindows();
        }
        
    }//GEN-LAST:event_jButtonsynActionPerformed

    private void jButtonaysnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonaysnActionPerformed
        // TODO add your handling code here:
        if (ifSyn) {
            ifSyn = false;//异步
           
            jButtonsyn.setContentAreaFilled(false);
            jButtonaysn.setContentAreaFilled(true);
            
            setEnable();
        }
    }//GEN-LAST:event_jButtonaysnActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
        if (getExtendedState() == Frame.NORMAL) setExtendedState(JFrame.MAXIMIZED_BOTH); 
    }//GEN-LAST:event_formComponentResized

    private void jComboBoxRecodTypePopupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jComboBoxRecodTypePopupMenuCanceled
        // TODO add your handling code here:
        jComboBoxRecodType.updateUI();
    }//GEN-LAST:event_jComboBoxRecodTypePopupMenuCanceled

    private void jComboBoxRecodTypePopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jComboBoxRecodTypePopupMenuWillBecomeInvisible
        // TODO add your handling code here:
        jComboBoxRecodType.updateUI();
    }//GEN-LAST:event_jComboBoxRecodTypePopupMenuWillBecomeInvisible

    private void jComboBoxRecodTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxRecodTypeItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
            if (bFirstSelect)  return;
            refreshFileTableList(getIndexOfCurrentJPanel());
        }
    }//GEN-LAST:event_jComboBoxRecodTypeItemStateChanged
    
    private void downloadSelectedTimes(ChannelPlayBack channelPlayBack){
        try{
            
            if (channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0) return;
            
            if (m_lDownloadHandle.intValue() == -1) {

                HCNetSDK.NET_DVR_TIME struStartTime;
                HCNetSDK.NET_DVR_TIME struStopTime;
                
                if (struDownloadStartTime.compareTo(struDownloadStopTime) < 0){
                    struStartTime = struDownloadStartTime;
                    struStopTime = struDownloadStopTime;
                }else {
                    struStartTime = struDownloadStopTime;
                    struStopTime = struDownloadStartTime;
                }
 
                //得到存储路径，自动命名文件名
                String PathName = CommonParas.SysParas.getSysParasFileSaveDirVedio();
                String NewFileName = channelPlayBack.getRecordDownloadName( PathName, struStartTime.toStringTitle(), struStopTime.toStringTitle()) + ".mp4";
                //sRemarks = NewFileName;
                //设置附加备注：下载录像文件
                channelPlayBack.setRemarks2(NewFileName);

                m_lDownloadHandle = CommonParas.hCNetSDK.NET_DVR_GetFileByTime(channelPlayBack.getUserID(), channelPlayBack.getChannelNO(), struStartTime, struStopTime, NewFileName);
                if (m_lDownloadHandle.intValue() >= 0) {
                    CommonParas.hCNetSDK.NET_DVR_PlayBackControl(m_lDownloadHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
                    jButtonRecordCut.setText(sStopVideoClip);//"停止剪辑");
                    jButtonRecordCut.setIcon(ImageIconBufferPool.getInstance().getImageIcon("stoprun.png"));
                    jProgressBar.setValue(0);
                    jProgressBar.setVisible(true);
                    Downloadtimer = new Timer();//新建定时器
                    Downloadtimer.schedule(new DownloadTask(sVideoClip, "videoclip.png", jButtonRecordCut, channelPlayBack), 0, 5000);//"录像剪辑"0秒后开始响应函数
                } else {
                    channelPlayBack.writePlayBackErrorLog(sDownloadRecordByTime_Fail);//"按时间下载录像失败"
                    CommonParas.showErrorMessage(sDownloadRecordByTime_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"按时间下载录像失败"
                    //JOptionPane.showMessageDialog(this, "按时间下载失败");
                    //System.out.println("laste error " + CommonParas.hCNetSDK.NET_DVR_GetLastError());
                }
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "downloadSelectedTimes()","系统在根据起止时间段下载录像文件过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        *函数:      playBackByFile
        *函数描述:  回放视频文件
     */
//    private void playBackByFile(){
//        try{
//                int Row = jTableFileList.getSelectedRow();
//                
//                if(Row == -1){
//                    JOptionPane.showMessageDialog(this, sSelectPlayFile);//"请选择要播放的文件"
//                    return;
//                }
//
//                //当前JPanel的索引号
//                int IndexOfCurrentJPanel = getIndexOfCurrentJPanel();
//                if (IndexOfCurrentJPanel == -1) {
//                    IndexOfCurrentJPanel = getIndexOfNotPlayBack();
//                    if (IndexOfCurrentJPanel == -1) IndexOfCurrentJPanel = 0;
//                    listJPanelPlayWindow.get(IndexOfCurrentJPanel).setDefaultCurrentJPanel();
//                }
//
//                //播放窗口
//                JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
//                //回放对象
//                ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
//                //如果已经在回放
//                if(channelPlayBack.getPlayBackHandle().intValue() != -1){
//                    CommonParas.hCNetSDK.NET_DVR_StopPlayBack(channelPlayBack.getPlayBackHandle());
//                }
//                channelPlayBack.intialParas();//各参数恢复初始值
//                
//
//                //获取文件名
//                DefaultTableModel FileTableModel = ((DefaultTableModel) jTableFileList.getModel());
//                String FileNamePlayBack = FileTableModel.getValueAt(Row, 0).toString();
//                String Remarks = "按文件回放："+ FileNamePlayBack;
//                String sSize = FileTableModel.getValueAt(Row,1).toString();
//                
//                //设置ChannelPlayBack对象的参数信息
//                channelPlayBack.setIndexOflistDevice(indexOflistDevice);//设置设备列表索引，同时设置设备BEAN、设备登录ID
//                channelPlayBack.setEncodingDVRChannelNode(sChannelNode);
//                channelPlayBack.setRemarks(Remarks);
//                channelPlayBack.setStruStartTime(FileTableModel.getValueAt(Row,2).toString());//2
//                channelPlayBack.setStruStopTime(FileTableModel.getValueAt(Row,3).toString());//3
//                
//                //获取窗口句柄
//                W32API.HWND hwnd = new W32API.HWND();
//                channelPlayBack.setPanelPlayBack(currentPanel);
//                hwnd.setPointer(Native.getComponentPointer(currentPanel));//获取窗口的指针
//                
//                
//                //调用接口开始回放
//                NativeLong PlayBackHandle = CommonParas.hCNetSDK.NET_DVR_PlayBackByName(m_lUserID, FileNamePlayBack, hwnd);
//                if (PlayBackHandle.longValue() > -1){
//                    //写日志
//                    channelPlayBack.writePlayBackLog("录像回放");
//                    //System.out.println("回放成功");
//                }else{
//                    //写错误日志
//                    channelPlayBack.writePlayBackErrorLog("录像回放失败");
//                    CommonParas.showErrorMessage("录像回放失败", channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);
//                    return;
//                }
//
//                //调用playControl才会开始播放
//                if (!CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null)){
//                    channelPlayBack.writePlayBackErrorLog("录像回放失败");
//                    CommonParas.showErrorMessage("录像回放失败", channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);
//
//                }else{
//                    
//                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYSTARTAUDIO, 0, null)){
//                        channelPlayBack.setAudioOpen(HCNetSDK.NET_DVR_PLAYSTARTAUDIO);////声音打开与否：打开9/关闭10
//                        CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYAUDIOVOLUME, (0xffff) / 2, null);
//                        //System.out.println("开始声音");
//                        jPanelPlayWindow.jSliderAudio.setValue(50);
//                        //jPanelPlayWindow.jButtonAudio.setText("Audio");
//                    } else{
//
//                        channelPlayBack.setAudioOpen(HCNetSDK.NET_DVR_PLAYSTOPAUDIO);////声音打开与否：打开9/关闭10
//                        //jPanelPlayWindow.jButtonAudio.setText("Mute");
//
//                    }
//                    
//                    
//                    channelPlayBack.setPlayBackHandle(PlayBackHandle);//回放句柄
//                    channelPlayBack.setChannelNO(new NativeLong(iChannel));//通道号
//                    channelPlayBack.setPlayBackMode("file");
//                    channelPlayBack.setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
//                    //jPanelPlayWindow.jButtonPlay.setText("Pause");
//                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYSTARTAUDIO, 0, null)){
//                        channelPlayBack.setAudioOpen(HCNetSDK.NET_DVR_PLAYSTARTAUDIO);////声音打开与否：打开9/关闭10
//                        CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYAUDIOVOLUME, (0xffff) / 2, null);
//
//                        jPanelPlayWindow.jSliderAudio.setValue(50);
//                        //jPanelPlayWindow.jButtonAudio.setText("Audio");
//                    } else{
//                        channelPlayBack.setAudioOpen(HCNetSDK.NET_DVR_PLAYSTOPAUDIO);////声音打开与否：打开9/关闭10
//                        //jPanelPlayWindow.jButtonAudio.setText("Mute");
//                    }
//                     
//                    //开始计时器
//                    //现在计时器都放在类内部，即每一个回放窗口都有一个计时器
//                    channelPlayBack.setPlaybacktimer(new Timer());//新建定时器
//                    channelPlayBack.getPlaybacktimer().schedule(new PlaybackTimeTask(IndexOfCurrentJPanel), 0, 1000);//0秒后开始响应函数
//                    
//                    jPanelPlayWindow.setDVRNodeName(this.sChannelNode);
//                    
//                    //显示录像完整性检测，如果还没有回放，JSlier也应该恢复默认的滑道显示
//                    //因为刚开始播放，用NET_DVR_GetPlayBackOsdTime函数会出问题。但是用debug一步步执行却没有问题。
//                    DateUtil.DateHandler dd = DateUtil.DateHandler.getTimeStringInstance(FileTableModel.getValueAt(Row,2).toString());
//                    refreshRecordInfo(channelPlayBack,new MyDate(dd.getYear(),dd.getMonth(),dd.getDay()));
//                    refreshPlayBackInfo(channelPlayBack);
//                }
//
//        }catch (Exception e){
//            TxtLogger.append(this.sFileName, "playBackByName()","系统在回放视频文件过程中，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
//    }
    /**
        *函数:      playBackByTime
        *函数描述:  根据时间间隔段播放回放视频
        * @return DefaultTreeModel
     */
    private void playBackByTime(int IndexOfCurrentJPanel){
        try{
            HCNetSDK.NET_DVR_TIME struStartTime;
            HCNetSDK.NET_DVR_TIME struStopTime;


            //播放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
            //回放对象
            ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            //如果已经在回放
            if(PlayBackHandle.intValue() != -1){
                CommonParas.hCNetSDK.NET_DVR_StopPlayBack(PlayBackHandle);
            }
            channelPlayBack.intialParas();//各参数恢复初始值



            struStartTime = new HCNetSDK.NET_DVR_TIME();
            struStopTime = new HCNetSDK.NET_DVR_TIME();

            String StrTime1 = buttonTime1.getDefaultDateText();//.getText();
            String StrTime2 = buttonTime2.getDefaultDateText();//.getText();
            
            String Remarks = sPlaybackByTimeColon + StrTime1 + "--" + StrTime2;//"按时间回放："
            struStartTime.dwYear = Integer.parseInt(StrTime1.substring(0, 4));//开始时间
            struStartTime.dwMonth = Integer.parseInt(StrTime1.substring(5, 7));
            struStartTime.dwDay = Integer.parseInt(StrTime1.substring(8, 10));
            struStartTime.dwHour = Integer.parseInt(StrTime1.substring(11, 13));
            struStartTime.dwMinute = Integer.parseInt(StrTime1.substring(14, 16));
            struStartTime.dwSecond = Integer.parseInt(StrTime1.substring(17, 19));

            struStopTime.dwYear = Integer.parseInt(StrTime2.substring(0, 4));////结束时间
            struStopTime.dwMonth = Integer.parseInt(StrTime2.substring(5, 7));
            struStopTime.dwDay = Integer.parseInt(StrTime2.substring(8, 10));
            struStopTime.dwHour = Integer.parseInt(StrTime2.substring(11, 13));
            struStopTime.dwMinute = Integer.parseInt(StrTime2.substring(14, 16));
            struStopTime.dwSecond = Integer.parseInt(StrTime2.substring(17, 19));

            //设置ChannelPlayBack对象的参数信息
            channelPlayBack.setIndexOflistDevice(indexOflistDevice);//设置设备列表索引，同时设置设备BEAN、设备登录ID
            channelPlayBack.setEncodingDVRChannelNode(sChannelNode);
            channelPlayBack.setRemarks(Remarks);
            channelPlayBack.setStruStartTime(struStartTime);
            channelPlayBack.setStruStopTime(struStopTime);

            //获取窗口句柄
            W32API.HWND hwnd = new W32API.HWND();
            channelPlayBack.setPanelPlayBack(currentPanel);
            hwnd.setPointer(Native.getComponentPointer(currentPanel));//获取窗口的指针


            PlayBackHandle = CommonParas.hCNetSDK.NET_DVR_PlayBackByTime(m_lUserID, new NativeLong(iChannel), struStartTime, struStopTime, hwnd);

            if (PlayBackHandle.intValue() == -1)
            {
                channelPlayBack.writePlayBackErrorLog(sPlackbackRecord_Fail);//"录像回放失败"
                CommonParas.showErrorMessage(sPlackbackRecord_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"录像回放失败"
                //JOptionPane.showMessageDialog(this, "按时间回放失败");
                return;
            }else{
                channelPlayBack.writePlayBackLog(sPlackbackRecord);//"录像回放"
                //还要调用该接口才能开始回放
                CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
                channelPlayBack.setPlayBackHandle(PlayBackHandle);
                channelPlayBack.setChannelNO(new NativeLong(iChannel));//通道号
                channelPlayBack.setPlayBackMode("time");
                channelPlayBack.setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
                //检测在录像回放时间内的录像缺失信息
                checkRecordLostInfo(IndexOfCurrentJPanel);
                //System.out.println("开始回放");
            }

            if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYSTARTAUDIO, 0, null)){
                channelPlayBack.setAudioOpen(HCNetSDK.NET_DVR_PLAYSTARTAUDIO);////声音打开与否：打开9/关闭10
                CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYAUDIOVOLUME, (0xffff) / 2, null);
                jPanelPlayWindow.jSliderAudio.setValue(50);
            } else{
                channelPlayBack.setAudioOpen(HCNetSDK.NET_DVR_PLAYSTOPAUDIO);////声音打开与否：打开9/关闭10
            }

            //开始计时器
            //现在计时器都放在类内部，即每一个回放窗口都有一个计时器
            channelPlayBack.setPlaybacktimer(new Timer());//新建定时器
            channelPlayBack.getPlaybacktimer().schedule(new PlaybackTimeTask(IndexOfCurrentJPanel), 0, 1000);//0秒后开始响应函数
            
            jPanelPlayWindow.setDVRNodeName(this.sChannelNode);
            

            //同步状态下，回放某一通道录像时需要进行的将该回放窗口根据其他窗口的回放时间同步
            if (ifSyn) {
                HCNetSDK.NET_DVR_TIME StruPlayBackTime = synOnePlayWindow(IndexOfCurrentJPanel);
                if (StruPlayBackTime == null){
                    DateUtil.DateHandler dd = DateUtil.DateHandler.getTimeStringInstance(buttonTime1.getDefaultDateText().trim());//.getText()
                    refreshRecordInfo(channelPlayBack,new MyDate(dd.getYear(),dd.getMonth(),dd.getDay()));
                }else{
                    refreshRecordInfo(channelPlayBack,new MyDate(StruPlayBackTime.dwYear,StruPlayBackTime.dwMonth,StruPlayBackTime.dwDay));
                }
            }else {
                //因为刚开始播放，用NET_DVR_GetPlayBackOsdTime函数会出问题。但是用debug一步步执行却没有问题。
                DateUtil.DateHandler dd = DateUtil.DateHandler.getTimeStringInstance(buttonTime1.getDefaultDateText().trim());//.getText()
                refreshRecordInfo(channelPlayBack,new MyDate(dd.getYear(),dd.getMonth(),dd.getDay()));
            }
            
            //刷新该录像回放窗口的录像文件列表；显示录像完整性检测，如果还没有回放，JSlier也应该恢复默认的滑道显示
            refreshPlayBackInfo(channelPlayBack);
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "playBackByTime()","系统在根据时间间隔段播放回放视频过程中，出现错误"
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
        m_DeviceRootResource = new DefaultMutableTreeNode(sChannel);//"监控点"
        DefaultTreeModel myDefaultTreeModel = new DefaultTreeModel(m_DeviceRootResource);//使用根节点创建模型
        return myDefaultTreeModel;
    }
    

    /**
	 * 函数:      getChannelNumber
         * 函数描述:  获得该设备当前的通道号
         * @return int   设备当前的通道号
    */
    private int getChannelNumber(String ChannelNode){
        int Channel = Integer.parseInt(ChannelNode.substring(ChannelNode.lastIndexOf("_")+1,ChannelNode.length()));
        if (ChannelNode.lastIndexOf("IP") > -1) Channel = Channel + HCNetSDK.MAX_ANALOG_CHANNUM;//32;//IP通道号要加32
        return Channel;
    }
    /**
	 * 函数:      setTableColWidth
         * 函数描述:  设置表格特殊列的宽度
    */
    private void setTableColWidth(){
        TableColumnModel tcmSubAuthoritys = jTableFileList.getColumnModel();
        tcmSubAuthoritys.getColumn(0).setMinWidth(0);
//        tcmSubAuthoritys.getColumn(0).setMaxWidth(60);
//        tcmSubAuthoritys.getColumn(0).setPreferredWidth(60);
        
        tcmSubAuthoritys.getColumn(0).setMinWidth(0);
        tcmSubAuthoritys.getColumn(0).setMaxWidth(0);
        tcmSubAuthoritys.getColumn(0).setWidth(0);
        tcmSubAuthoritys.getColumn(1).setMinWidth(0);
        tcmSubAuthoritys.getColumn(1).setMaxWidth(0);
        tcmSubAuthoritys.getColumn(1).setWidth(0);
        tcmSubAuthoritys.getColumn(3).setMinWidth(0);
        tcmSubAuthoritys.getColumn(3).setMaxWidth(0);
        tcmSubAuthoritys.getColumn(3).setWidth(0);
        tcmSubAuthoritys.getColumn(4).setMinWidth(0);
        tcmSubAuthoritys.getColumn(4).setMaxWidth(0);
        tcmSubAuthoritys.getColumn(4).setWidth(0);
        tcmSubAuthoritys.getColumn(5).setMinWidth(0);
        tcmSubAuthoritys.getColumn(5).setMaxWidth(0);
        tcmSubAuthoritys.getColumn(5).setWidth(0);
        tcmSubAuthoritys.getColumn(6).setMinWidth(0);
        tcmSubAuthoritys.getColumn(6).setMaxWidth(0);
        tcmSubAuthoritys.getColumn(6).setWidth(0);
        tcmSubAuthoritys.getColumn(7).setMinWidth(0);
        tcmSubAuthoritys.getColumn(7).setMaxWidth(0);
        tcmSubAuthoritys.getColumn(7).setWidth(0);
//        jTableFileList.updateUI();
    }
    
    /*-------****************************************
    函数:      initialTableModel
    函数描述:  初始化文件列表
     ****************************************-------*/
    private DefaultTableModel initialTableModel()
    {
        
        class MyTableModel extends DefaultTableModel{ 
            
            public MyTableModel(Object[] columnNames, int rowCount){
                super(columnNames,rowCount);
            }
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            } 
        }
//        DefaultTableModel fileTableModel = new DefaultTableModel(tabeTile, 0);

        MyTableModel fileTableModel = new MyTableModel(tableTitle, 0);
        return fileTableModel;
    }

    


   
    /**
          * 函数:      getIfExistPlayBack
          * 函数描述:  检查是否还有窗口在视频回放？主要是在退出窗口时才调用该函数
    */ 
    private boolean getIfExistPlayBack(){
        for (int i=0;i<WINDOWSNUM;i++){
            ChannelPlayBack channelPlayBack= listChannelPlayBack.get(i);
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            if (PlayBackHandle.intValue() > -1) return true;
        }
        return false;
    }
    
    /**
          * 函数:      StopAllPanels
          * 函数描述:  停止所有窗口的视频回放
    */ 
    private void stopAllPanels(){
        for (int i=0;i<WINDOWSNUM;i++){
            ChannelPlayBack channelPlayBack= listChannelPlayBack.get(i);
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            if (PlayBackHandle.intValue() > -1) stopOnePanel(i);
        }
    }
    /**
          * 函数:      stopOnePanel
          * 函数描述:  停止一个窗口的视频回放，同时保存未保存的视频
          * @param IndexOfCurrentJPanel 窗口索引
    */ 
    private  void stopOnePanel(int IndexOfCurrentJPanel){
        try{

            if (IndexOfCurrentJPanel == -1) return;
            ChannelPlayBack channelPlayBack= listChannelPlayBack.get(IndexOfCurrentJPanel);
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);

            if (PlayBackHandle.intValue() == -1) return;
            if (PlayBackHandle.intValue() >= 0){
    //            if (SaveFile)
                if (channelPlayBack.isIfSaveFile())
                {
                    if (!CommonParas.hCNetSDK.NET_DVR_StopPlayBackSave(PlayBackHandle)){
                            //System.out.println("停止保存失败");
                    }else{
                        channelPlayBack.setIfSaveFile(false);
    //                    SaveFile = false;
                    }

                }
                if (!CommonParas.hCNetSDK.NET_DVR_StopPlayBack(PlayBackHandle)){
                    //写错误日志
                    channelPlayBack.writePlayBackErrorLog(sStopPlackbackRecord_Fail);//"结束录像回放失败"
                    CommonParas.showErrorMessage(sStopPlackbackRecord_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"结束录像回放失败"
                    //System.out.println("NET_DVR_StopPlayBack failed");
                }else {
                    
                    jPanelPlayWindow.panelPlayBack.repaint();
                    
                    channelPlayBack.getPlayBackHandle().setValue(-1) ;
                    channelPlayBack.getPlaybacktimer().cancel();
                    channelPlayBack.setPlayFileName("");
                    channelPlayBack.setTotalBytes(0);
                    if (bZoomIn) zoomInPlayBackOneChannel();
                    
                    jPanelPlayWindow.intialComponentParas();
                    
                    //清除该录像回放窗口对应的录像文件列表
                    ((ArrayList)listRecordFiles.get(IndexOfCurrentJPanel)).clear();
                    //显示录像完整性检测，如果还没有回放，JSlier也应该恢复默认的滑道显示
                    refreshRecordInfo(channelPlayBack,null);
                    //刷新该录像回放窗口的录像文件列表显示；
                    refreshPlayBackInfo(channelPlayBack);
                    //写日志
                    channelPlayBack.writePlayBackLog(sStopPlackbackRecord);//"结束录像回放"

                }
            }
        }catch(Exception e){
            TxtLogger.append(this.sFileName, "stopOnePanel()","某一个播放窗口按了播放“stop”键，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }

    /**
          * 函数:      playAllPanels
          * 函数描述:  所有播放窗口播放“play/pause”键
     */ 
    private void playAllPanels(){
        
        for (int i=0;i<WINDOWSNUM;i++){
            //playOnePanel(i);
            listJPanelPlayWindow.get(i).setPlayOnePanel();
        }
    }
    /**
        * 函数:      playPanel
        * 函数描述:  某一个播放窗口按了播放“play/pause”键
     */ 
    private void playOnePanel(int IndexOfCurrentJPanel){

        if (IndexOfCurrentJPanel == -1) return;
        try{
            //播放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
            //回放对象
            ChannelPlayBack channelPlayBack = jPanelPlayWindow.getChannelPlayBack();//listChannelPlayBack.get(IndexOfCurrentJPanel);
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            
            //如果为正向，而且正在回放，则只做暂停处理
            if (channelPlayBack.getPlayDirection()== HCNetSDK.NET_DVR_PLAY_FORWARD && channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYSTART){
                if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYPAUSE, 0, null)){
                    channelPlayBack.setPlayState(HCNetSDK.NET_DVR_PLAYPAUSE);
                } 
            }else if (channelPlayBack.getPlayDirection()== HCNetSDK.NET_DVR_PLAY_FORWARD && channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYPAUSE){//如果为正向，而且在暂停，则只做恢复播放处理
                if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYRESTART, 0, null)){
                    channelPlayBack.setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
                } 
            }else if (channelPlayBack.getPlayDirection()== HCNetSDK.NET_DVR_PLAY_REVERSE && channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYSTART){//如果为倒向，而且正在回放，则只做倒放切换为正放处理
                if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(PlayBackHandle, HCNetSDK.NET_DVR_PLAY_FORWARD, Pointer.NULL, 0, Pointer.NULL, null)){
                    channelPlayBack.setPlayDirection(HCNetSDK.NET_DVR_PLAY_FORWARD);
                } 
            }else if (channelPlayBack.getPlayDirection()== HCNetSDK.NET_DVR_PLAY_REVERSE && channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYPAUSE){//如果为倒向，而且正在暂停，则做2步处理：正放切换为倒放；恢复播放处理
                if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(PlayBackHandle, HCNetSDK.NET_DVR_PLAY_FORWARD, Pointer.NULL, 0, Pointer.NULL, null)){
                    channelPlayBack.setPlayDirection(HCNetSDK.NET_DVR_PLAY_FORWARD);
                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYRESTART, 0, null)){
                        channelPlayBack.setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
                    } 
                } 
            }

        }catch(Exception e){
            TxtLogger.append(this.sFileName, "playOnePanel()","某一个播放窗口按了播放“play/pause”键，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
    
    /**
        * 函数:      reversePanel
        * 函数描述:  某一个播放窗口按了倒放“reverse/pause”键
     */ 
    private void reverseOnePanel(int IndexOfCurrentJPanel){

        if (IndexOfCurrentJPanel == -1) return;
        try{
            //播放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
            //回放对象
            ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            //如果为倒向，而且正在回放，则只做暂停处理
            if (channelPlayBack.getPlayDirection()== HCNetSDK.NET_DVR_PLAY_REVERSE && channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYSTART){
                if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYPAUSE, 0, null)){
                    channelPlayBack.setPlayState(HCNetSDK.NET_DVR_PLAYPAUSE);
                } 
            }else if (channelPlayBack.getPlayDirection()== HCNetSDK.NET_DVR_PLAY_REVERSE && channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYPAUSE){//如果为倒向，而且在暂停，则只做恢复播放处理
                if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYRESTART, 0, null)){
                    channelPlayBack.setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
                } 
            }else if (channelPlayBack.getPlayDirection()== HCNetSDK.NET_DVR_PLAY_FORWARD && channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYSTART){//如果为正向，而且正在回放，则只做正放切换为倒放处理
                if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(PlayBackHandle, HCNetSDK.NET_DVR_PLAY_REVERSE, Pointer.NULL, 0, Pointer.NULL, null)){
                    channelPlayBack.setPlayDirection(HCNetSDK.NET_DVR_PLAY_REVERSE);
                    channelPlayBack.setPlaySpeed(1);
                } 
            }else if (channelPlayBack.getPlayDirection()== HCNetSDK.NET_DVR_PLAY_FORWARD && channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYPAUSE){//如果为正向，而且正在暂停，则做2步处理：正放切换为倒放；恢复播放处理
                if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl_V40(PlayBackHandle, HCNetSDK.NET_DVR_PLAY_REVERSE, Pointer.NULL, 0, Pointer.NULL, null)){
                    channelPlayBack.setPlayDirection(HCNetSDK.NET_DVR_PLAY_REVERSE);
                    channelPlayBack.setPlaySpeed(1);
                    if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYRESTART, 0, null)){
                        channelPlayBack.setPlayState(HCNetSDK.NET_DVR_PLAYSTART);
                    } 
                } 
            }
            
        }catch(Exception e){
            TxtLogger.append(this.sFileName, "reverseOnePanel()","某一个播放窗口按了播放“reverse/pause”键，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
    /**
          * 函数:      setOneSliderPlayback
          * 函数描述:  设置某一个播放窗口的播放进度
    */ 
    private void setOneSliderPlayback(int IndexOfCurrentJPanel){

        if (IndexOfCurrentJPanel == -1) return;
        try{
            //播放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
            //回放对象
            ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            int iPos = jPanelPlayWindow.jSliderPlayback.getValue();
             if (PlayBackHandle.intValue() >= 0)
            {
                    if ((iPos >=0) && (iPos <=100))
                    {
                            if (iPos == 100)
                            {
                                    stopOnePanel(IndexOfCurrentJPanel);
                                    iPos = 99;
                            }
                            else
                            {
                                    if(CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYSETPOS, iPos, null))
                                    {
                                            //System.out.println("设置播放进度成功");
                                    }
                                    else
                                    {
                                            //System.out.println("设置播放进度失败");
                                    }
                            }
                    }
            }
        }catch(Exception e){
            TxtLogger.append(this.sFileName, "setOneSliderPlayback()","设置某一个播放窗口的播放进度的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    
    }
    /**
          * 函数:      setOneSliderAudio
          * 函数描述:  设置某一个播放窗口的音量大小
    */ 
    private void setOneSliderAudio(int IndexOfCurrentJPanel){

        if (IndexOfCurrentJPanel == -1) return;
        try{
            //播放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
            //回放对象
            ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            if (PlayBackHandle.intValue() >= 0)
		{
			if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYAUDIOVOLUME, jPanelPlayWindow.jSliderAudio.getValue(), null))
			{
				//System.out.println(" Set volume Succeed!");
			}
			else
			{
				//System.out.println(" Set volume Failed!");
			}
		}
        }catch(Exception e){
            TxtLogger.append(this.sFileName, "setOneSliderAudio()","设置某一个播放窗口的音量大小的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    
    }
    /**
          * 函数:      setAudioIfMute
          * 函数描述:  设置某一个播放窗口是否关闭音量
    */
    private void setAudioIfMute(int IndexOfCurrentJPanel){
        
        if (IndexOfCurrentJPanel == -1) return;
        try{
            //播放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
            //回放对象
            ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            if (PlayBackHandle.intValue() >= 0){
                    if (channelPlayBack.getAudioOpen() == HCNetSDK.NET_DVR_PLAYSTARTAUDIO){
                            if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYSTOPAUDIO, 0, null)){
                                channelPlayBack.setAudioOpen(HCNetSDK.NET_DVR_PLAYSTOPAUDIO);//设置为声音关闭状态
                            }
                    }else{
                            if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYSTARTAUDIO, 0, null)){
                                channelPlayBack.setAudioOpen(HCNetSDK.NET_DVR_PLAYSTARTAUDIO);//设置为声音关闭状态
                            }
                    }
            }
        }catch(Exception e){
            TxtLogger.append(this.sFileName, "setAudioIfMute()","设置某一个播放窗口的音量大小的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
    /**
          * 函数:      setSlowPlayAllPanels
          * 函数描述:  设置所有播放窗口减速播放
    */ 
    private void setSlowPlayAllPanels(){
        for (int i=0;i<WINDOWSNUM;i++){
            if (listChannelPlayBack.get(i).getPlayBackHandle().intValue() == -1) continue;
            listJPanelPlayWindow.get(i).setSlowPlayOnePanel();
            jTextFieldAllSpeed.setText(listJPanelPlayWindow.get(i).getTextFieldSpeed());
        }
    }

    /**
          * 函数:      setFastPlayAllPanels
          * 函数描述:  设置所有播放窗口快速播放
    */ 
    private void setFastPlayAllPanels(){
        //系统暂时设置同步回放最大速度为2倍速。
        if (jTextFieldAllSpeed.getText().trim().equals("2x")) return;
        for (int i=0;i<WINDOWSNUM;i++){
            if (listChannelPlayBack.get(i).getPlayBackHandle().intValue() == -1) continue;
            listJPanelPlayWindow.get(i).setFastPlayOnePanel();
            jTextFieldAllSpeed.setText(listJPanelPlayWindow.get(i).getTextFieldSpeed());
        }
    }

    /**
          * 函数:      saveAllPanelsRecord
          * 函数描述:  给所有播放窗口存储录像
    */ 
    private void saveAllPanelsRecord(){
        for (int i=0;i<WINDOWSNUM;i++){
            saveOnePanelRecord(i);
        }
    }
    /**
          * 函数:      saveOnePanelRecord
          * 函数描述:  给某一个播放窗口存储录像
    */ 
    private void saveOnePanelRecord(int IndexOfCurrentJPanel){

        if (IndexOfCurrentJPanel == -1) return;
        try{
            
            //回放对象
            ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
            if (channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0) return;
            
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            if (PlayBackHandle.intValue() == -1)return;
            
            channelPlayBack.saveOnePanelRecord();
//            //如果没有下载录像的权限，则返回
//            if (!ifHaveDownload()) return;
//            
//            
//            //播放窗口
//            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
//            
//            //如果不是正在存储文件状态
//            if (!channelPlayBack.isIfSaveFile()){
//                //得到存储路径，自动命名文件名
//                String PathName = CommonParas.SysParas.getSysParasFileSaveDirVedio();
//                String SaveFileName = channelPlayBack.getInitialName(PathName);
//
//                if (CommonParas.hCNetSDK.NET_DVR_PlayBackSaveData(PlayBackHandle, SaveFileName + ".mp4")){
//                    channelPlayBack.setIfSaveFile(true);
//                    channelPlayBack.setSaveFileName(SaveFileName);
//                }
//            } else{//如果正在存储文件状态
//                boolean ifSaveSuccess = CommonParas.hCNetSDK.NET_DVR_StopPlayBackSave(PlayBackHandle);
// 
//                if (ifSaveSuccess) {
//                    File FileRecord = new File(channelPlayBack.getSaveFileName() + ".mp4");
//                    File NewFileRecord = new File(channelPlayBack.getReName() + ".mp4");
//                    //设置附加备注：存储录像文件
//                    channelPlayBack.setRemarks2(NewFileRecord.toString());
//                    
//                    if(!NewFileRecord.exists()){//若在该目录下已经有一个文件和新文件名相同，则不允许重命名   
//                        //FileRecord.renameTo(NewFileRecord);
//                        if (FileRecord.renameTo(NewFileRecord)) {
//                            channelPlayBack.writePlayBackLog(sSaveRecord);//"保存录像"
//                            CommonParas.showMessage(sSaveRecord_Succ + NewFileRecord, sFileName);//"成功保存录像："
//                            //JOptionPane.showMessageDialog(null, "成功保存录像：" + NewFileRecord);
//                        }
//                    }   
//                    //JOptionPane.showMessageDialog(this, "存储录像文件成功：" + channelPlayBack.getSaveFileName());
//                }else {
//                    channelPlayBack.writePlayBackErrorLog(sSaveRecordFile_Fail);//"存储录像文件失败"
//                    CommonParas.showErrorMessage(sSaveRecordFileColon_Fail + channelPlayBack.getSaveFileName(), channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"存储录像文件失败："
//                }
//                
//                channelPlayBack.setIfSaveFile(false);
//            }
        }catch(Exception e){
            TxtLogger.append(this.sFileName, "saveOnePanelRecord()","给给某一个播放窗口存储录像的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
    /**
	 * 函数:      refreshFileList
         * 函数描述:  刷新要查询的录像文件列表
    */
    private void refreshFileList(){
        try{
            ChannelPlayBack channelPlayBack = this.getCurrentChannelPlayBack();
            boolean IfBefore = buttonTime1.getDate().before(buttonTime2.getDate());
            if (!IfBefore) {
                JOptionPane.showMessageDialog(rootPane, sSearchDateRemind);//"起始日期不能在终止日期之后或相同！"The start date can not be after the end date or the same!
                return ;
            }
            //单击搜索文件
            //((DefaultTableModel) jTableFileList.getModel()).getDataVector().removeAllElements();//搜索前先清空列表
            fileListModel.getDataVector().removeAllElements();//搜索前先清空列表
            fileListModel.fireTableDataChanged();
            //把改变显示到列表控件
    //        ((DefaultTableModel) jTableFileList.getModel()).fireTableStructureChanged();

            HCNetSDK.NET_DVR_FILECOND m_strFilecond = new HCNetSDK.NET_DVR_FILECOND();
            m_strFilecond.struStartTime = new HCNetSDK.NET_DVR_TIME();
            m_strFilecond.struStopTime = new HCNetSDK.NET_DVR_TIME();
            String StrTime1 = buttonTime1.getDefaultDateText();//.getText();
            String StrTime2 = buttonTime2.getDefaultDateText();//.getText();
            sTime1 = StrTime1;//存储上一次查询的起始时间，用来比对是否发生更改事件
            sTime2= StrTime2;//存储上一次查询的终止时间，用来比对是否发生更改事件

            m_strFilecond.struStartTime.dwYear = Integer.parseInt(StrTime1.substring(0, 4));//开始时间
            m_strFilecond.struStartTime.dwMonth = Integer.parseInt(StrTime1.substring(5, 7));
            m_strFilecond.struStartTime.dwDay = Integer.parseInt(StrTime1.substring(8, 10));
            m_strFilecond.struStartTime.dwHour = Integer.parseInt(StrTime1.substring(11, 13));
            m_strFilecond.struStartTime.dwMinute = Integer.parseInt(StrTime1.substring(14, 16));
            m_strFilecond.struStartTime.dwSecond = Integer.parseInt(StrTime1.substring(17, 19));

            m_strFilecond.struStopTime.dwYear = Integer.parseInt(StrTime2.substring(0, 4));////结束时间
            m_strFilecond.struStopTime.dwMonth = Integer.parseInt(StrTime2.substring(5, 7));
            m_strFilecond.struStopTime.dwDay = Integer.parseInt(StrTime2.substring(8, 10));
            m_strFilecond.struStopTime.dwHour = Integer.parseInt(StrTime2.substring(11, 13));
            m_strFilecond.struStopTime.dwMinute = Integer.parseInt(StrTime2.substring(14, 16));
            m_strFilecond.struStopTime.dwSecond = Integer.parseInt(StrTime2.substring(17, 19));
            m_strFilecond.lChannel = new NativeLong(iChannel);//通道号
            int Index = jComboBoxRecodType.getSelectedIndex();//choiceRecodType.getSelectedIndex();//recordFileTypes
            if (Index > 0) m_strFilecond.dwFileType = iRecordFileTypes[Index];
            else m_strFilecond.dwFileType = iRecordFileTypes[0];
//            if (Index > 0) m_strFilecond.dwFileType = Index - 1;
//            else m_strFilecond.dwFileType = 0xff;

    //        m_strFilecond.dwFileType = jComboBoxRecodType.getSelectedIndex();//文件类型0xff;//
    //        System.out.println("文件类型" +  jComboBoxRecodType.getSelectedIndex());
            m_strFilecond.dwIsLocked = 0xff;
            m_strFilecond.dwUseCardNo = 0;//jRadioButtonByCardNumber.isSelected() ? 1 : 0;  //是否使用卡号
    //            if (m_strFilecond.dwUseCardNo == 1)
    //            {
    //                m_strFilecond.sCardNumber = new Byte("0");//(byte)0;//jTextFieldCardNumber.getText().getBytes();//卡号
    //                System.out.printf("卡号%s", m_strFilecond.sCardNumber);
    //            }

            NativeLong lFindFile = CommonParas.hCNetSDK.NET_DVR_FindFile_V30(m_lUserID, m_strFilecond);
            HCNetSDK.NET_DVR_FINDDATA_V30 strFile = new HCNetSDK.NET_DVR_FINDDATA_V30();

            if (lFindFile.intValue() == -1){//如果失败（根据文件类型、时间查找设备录像文件。）
                channelPlayBack.writePlayBackErrorLog(sSearchRecord_Fail);//"查找设备录像文件失败"
                CommonParas.showErrorMessage(sSearchRecord_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"查找设备录像文件失败"
                
                return;
            }
            NativeLong lnext;

            while (true){
                lnext = CommonParas.hCNetSDK.NET_DVR_FindNextFile_V30(lFindFile, strFile);
                if (lnext.longValue() == HCNetSDK.NET_DVR_FILE_SUCCESS){
                    //搜索成功
                    //DefaultTableModel FileTableModel = ((DefaultTableModel) jTableFileList.getModel());//获取表格模型
                    Vector<String> newRow = new Vector<String>();

                    //添加文件名信息
                    String[] s = new String[2];
                    s = new String(strFile.sFileName).split("\0", 2);
                    newRow.add(new String(s[0]));

                    int iTemp;
                    String MyString;
                    if (strFile.dwFileSize < 1024 * 1024)
                    {
                        iTemp = (strFile.dwFileSize) / (1024);
                        MyString = iTemp + "K";
                    }
                    else
                    {
                        iTemp = (strFile.dwFileSize) / (1024 * 1024);
                        MyString = iTemp + "M   ";
                        iTemp = ((strFile.dwFileSize) % (1024 * 1024)) / (1204);
                        MyString = MyString + iTemp + "K";
                    }
                    newRow.add(MyString);                            //添加文件大小信息
                    newRow.add(strFile.struStartTime.toStringTime());//添加开始时间信息
                    newRow.add(strFile.struStopTime.toStringTime()); //添加结束时间信息
                    newRow.add(strFile.struStartTime.toStringTitle());//添加开始时间信息2
                    newRow.add(strFile.struStopTime.toStringTitle()); //添加结束时间信息2

                    fileListModel.getDataVector().add(0, newRow);
                    fileListModel.fireTableDataChanged();
                }else if (lnext.longValue() == HCNetSDK.NET_DVR_ISFINDING){//搜索中
                }else if (lnext.longValue() == HCNetSDK.NET_DVR_FILE_NOFIND){//未查找到文件
                    CommonParas.showMessage(sSearchRecord_Null, sFileName);//"没有搜到文件"
                    break;
                }else{//NET_DVR_NOMOREFILE  1003 没有更多的文件，查找结束；NET_DVR_FILE_EXCEPTION  1004 查找文件时异常 
                        boolean flag = CommonParas.hCNetSDK.NET_DVR_FindClose_V30(lFindFile);
                        if (flag == false){
                            channelPlayBack.writePlayBackErrorLog(sCloseSearchRecord_Fail);//"关闭文件查找，释放资源失败"
                            CommonParas.showErrorMessage(sCloseSearchRecord_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"关闭文件查找，释放资源失败"
                        }
                        break;
                }
            }
        }catch (NumberFormatException | HeadlessException e){
            TxtLogger.append(sFileName, "refreshFileList()","系统在刷新要查询的录像文件列表过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        
    }
    
    /**
        * 函数:      refreshFileTableList
        * 函数描述:  刷新要查询的录像文件列表:"文件名称","大小","开始时间","结束时间","开始时间2","结束时间2","文件类型","是否被锁"
    */
    private void refreshFileTableList(int IndexOfCurrentJPanel){
        if (IndexOfCurrentJPanel == -1) return;
        fileListModel.getDataVector().removeAllElements();//搜索前先清空列表
        fileListModel.fireTableDataChanged();
        
        int FileTypeCombox = iRecordFileTypes[jComboBoxRecodType.getSelectedIndex()];//choiceRecodType.getSelectedIndex()
        ArrayList NewFilesList = listRecordFiles.get(IndexOfCurrentJPanel);
        for (int i=0;i<NewFilesList.size();i++){
            Vector NewRow = (Vector)NewFilesList.get(i);
            int FileType = (Byte)NewRow.get(6);
            if (ifFileTypeNeeded( FileType, FileTypeCombox))
                fileListModel.addRow(NewRow);
        }
        if (NewFilesList.size() > 0) fileListModel.fireTableDataChanged();
    }
    /**
        * 函数:      ifFileTypeNeeded
        * 函数描述:  判断该文件类型是否符合需要要求（录像类型下拉框中的选择）
    */
    private boolean ifFileTypeNeeded(int FileType, int FileTypeCombox){
        if (FileTypeCombox == 0xff) return true;//全部录像
        if (FileTypeCombox == iAllAlarmRecordType) { //所有的报警类型类型
            return FileType!=0 && FileType!=5 && FileType!=6;
        }
        return FileType==FileTypeCombox;
        
    }
    /**
	 * 函数:      searchFilesList
         * 函数描述:  搜索对应设备对应通道的录像文件列表（发生在双击树节点，也就是设备通道节点之后）
    */
    private boolean searchFilesList(int IndexOfCurrentJPanel){
        try{
            if (IndexOfCurrentJPanel < 0) return false;
            ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
            if (channelPlayBack == null)  return false;//此处channelPlayBack还没有开始回放

            HCNetSDK.NET_DVR_FILECOND m_strFilecond = new HCNetSDK.NET_DVR_FILECOND();
            m_strFilecond.struStartTime = new HCNetSDK.NET_DVR_TIME();
            m_strFilecond.struStopTime = new HCNetSDK.NET_DVR_TIME();
            String StrTime1 = buttonTime1.getDefaultDateText();//.getText();
            String StrTime2 = buttonTime2.getDefaultDateText();//.getText();
            sTime1 = StrTime1;//存储上一次查询的起始时间，用来比对是否发生更改事件
            sTime2= StrTime2;//存储上一次查询的终止时间，用来比对是否发生更改事件
            
            boolean IfBefore = buttonTime1.getDate().before(buttonTime2.getDate());
            if (!IfBefore) {
                JOptionPane.showMessageDialog(rootPane, sSearchDateRemind);//"起始日期不能在终止日期之后或相同！"The start date can not be after the end date or the same!
                return false;
            }

            m_strFilecond.struStartTime.dwYear = Integer.parseInt(StrTime1.substring(0, 4));//开始时间
            m_strFilecond.struStartTime.dwMonth = Integer.parseInt(StrTime1.substring(5, 7));
            m_strFilecond.struStartTime.dwDay = Integer.parseInt(StrTime1.substring(8, 10));
            m_strFilecond.struStartTime.dwHour = Integer.parseInt(StrTime1.substring(11, 13));
            m_strFilecond.struStartTime.dwMinute = Integer.parseInt(StrTime1.substring(14, 16));
            m_strFilecond.struStartTime.dwSecond = Integer.parseInt(StrTime1.substring(17, 19));

            m_strFilecond.struStopTime.dwYear = Integer.parseInt(StrTime2.substring(0, 4));////结束时间
            m_strFilecond.struStopTime.dwMonth = Integer.parseInt(StrTime2.substring(5, 7));
            m_strFilecond.struStopTime.dwDay = Integer.parseInt(StrTime2.substring(8, 10));
            m_strFilecond.struStopTime.dwHour = Integer.parseInt(StrTime2.substring(11, 13));
            m_strFilecond.struStopTime.dwMinute = Integer.parseInt(StrTime2.substring(14, 16));
            m_strFilecond.struStopTime.dwSecond = Integer.parseInt(StrTime2.substring(17, 19));
            m_strFilecond.lChannel = new NativeLong(iChannel);//通道号
            
            m_strFilecond.dwFileType = 0xff;//全部录像

            m_strFilecond.dwIsLocked = 0xff;//是否锁定：0-未锁定文件，1-锁定文件，0xff表示所有文件（包括锁定和未锁定） 
            m_strFilecond.dwUseCardNo = 0;//是否带卡号查找 按卡号查找录像文件需要设备支持，比如ATM机

            NativeLong lFindFile = CommonParas.hCNetSDK.NET_DVR_FindFile_V30(m_lUserID, m_strFilecond);

            if (lFindFile.intValue() == -1){//如果失败（根据文件类型、时间查找设备录像文件。）
                channelPlayBack.writePlayBackErrorLog(sSearchRecord_Fail);//"查找设备录像文件失败"
                CommonParas.showErrorMessage(sSearchRecord_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"查找设备录像文件失败"
                
                return false;
            }
            
            ArrayList NewFilesList = listRecordFiles.get(IndexOfCurrentJPanel);
            NewFilesList.clear();//必须将上次的录像文件列表清除！！。
            
            NativeLong lnext;
            HCNetSDK.NET_DVR_FINDDATA_V30 struFile = new HCNetSDK.NET_DVR_FINDDATA_V30();

            while (true){
                lnext = CommonParas.hCNetSDK.NET_DVR_FindNextFile_V30(lFindFile, struFile);
                if (lnext.intValue() == HCNetSDK.NET_DVR_FILE_SUCCESS){
                    //搜索成功
                    //DefaultTableModel FileTableModel = ((DefaultTableModel) jTableFileList.getModel());//获取表格模型
                    Vector newRow = new Vector<String>();

                    //添加文件名信息
                    String[] s = new String(struFile.sFileName).split("\0", 2);
                    newRow.add(s[0]);

                    int iTemp;
                    String MyString;
                    if (struFile.dwFileSize < 1024 * 1024){
                        iTemp = (struFile.dwFileSize) / (1024);
                        MyString = iTemp + "K";
                    }else{
                        iTemp = (struFile.dwFileSize) / (1024 * 1024);
                        MyString = iTemp + "M   ";
                        iTemp = ((struFile.dwFileSize) % (1024 * 1024)) / (1204);
                        MyString = MyString + iTemp + "K";
                    }
                    newRow.add(MyString);                            //添加文件大小信息
                    newRow.add(struFile.struStartTime.toStringTime());//添加开始时间信息
                    newRow.add(struFile.struStopTime.toStringTime()); //添加结束时间信息
                    newRow.add(struFile.struStartTime.toStringTitle());//添加开始时间信息2
                    newRow.add(struFile.struStopTime.toStringTitle()); //添加结束时间信息2
                    newRow.add(struFile.byFileType); //文件类型
                    newRow.add(struFile.byLocked); //文件是否被锁定

                    NewFilesList.add(newRow);
                }else if (lnext.longValue() == HCNetSDK.NET_DVR_ISFINDING){//搜索中
                }else if (lnext.longValue() == HCNetSDK.NET_DVR_FILE_NOFIND){//未查找到文件
                    CommonParas.showMessage(sSearchRecord_Null, sFileName);//"没有搜到文件"
                    break;
                }else{//NET_DVR_NOMOREFILE  1003 没有更多的文件，查找结束；NET_DVR_FILE_EXCEPTION  1004 查找文件时异常 
                        boolean flag = CommonParas.hCNetSDK.NET_DVR_FindClose_V30(lFindFile);
                        if (flag == false){
                            channelPlayBack.writePlayBackErrorLog(sCloseSearchRecord_Fail);//"关闭文件查找，释放资源失败"
                            CommonParas.showErrorMessage(sCloseSearchRecord_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"关闭文件查找，释放资源失败"
                        }
                        break;
                }

            }
            return true;
        }catch (NumberFormatException | HeadlessException e){
            TxtLogger.append(sFileName, "searchFilesList()","系统在搜索对应设备对应通道的录像文件列表过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
        
    }

    /**
        * 函数:      getIndexOfCurrentJPanel
        * 函数描述:  获得当前JPanel的索引
        * @return int 成功当前JPanel的索引；失败-1
    */
    private int getIndexOfCurrentJPanel(){
        if (currentJPanel == null) return -1;
        String NameJPandel = currentJPanel.getName();
        return Integer.parseInt(NameJPandel.substring(NameJPandel.length()-1, NameJPandel.length())) ;
    }
    /**
        * 函数:      getIndexOfNotPlayBack
        * 函数描述:  检查是否还有窗口未视频回放？如果有，返回其索引
    */ 
    private int getIndexOfNotPlayBack(){
        for (int i=0;i<WINDOWSNUM;i++){
            ChannelPlayBack channelPlayBack= listChannelPlayBack.get(i);
            NativeLong PlayBackHandle = channelPlayBack.getPlayBackHandle();
            if (PlayBackHandle.intValue() == -1) return i;
        }
        return -1;
    }
    /**
        * 函数:      getIndexOfNeedPlayBack
        * 函数描述:  获得要进行录像回放时需要的JPanel的索引
        * @return int 成功当前JPanel的索引；失败-1
    */
    private int getIndexOfNeedPlayBack(){
        int IndexOfCurrentJPanel = getIndexOfCurrentJPanel();
        if (IndexOfCurrentJPanel == -1) {
            IndexOfCurrentJPanel = getIndexOfNotPlayBack();
            if (IndexOfCurrentJPanel == -1) IndexOfCurrentJPanel = 0;
            listJPanelPlayWindow.get(IndexOfCurrentJPanel).setDefaultCurrentJPanel();
        }
        return IndexOfCurrentJPanel;
    }
    /**
        * 函数:      getIndexOfAlreadyPlayBack
        * 函数描述:  获得已经进行录像回放时需要的JPanel的索引
        * @return int 成功当前JPanel的索引；失败-1
    */
    private int getIndexOfAlreadyPlayBack(){
        int IndexOfCurrentJPanel = getIndexOfCurrentJPanel();
        if (IndexOfCurrentJPanel == -1) {
            for (int i=0;i<listChannelPlayBack.size();i++){
                ChannelPlayBack channelPlayBack2 =listChannelPlayBack.get(i);
                if (channelPlayBack2.getPlayBackHandle().intValue() > -1) return i;
            }
        }else {
            ChannelPlayBack channelPlayBack =listChannelPlayBack.get(IndexOfCurrentJPanel);
            if (channelPlayBack.getPlayBackHandle().intValue() > -1) return IndexOfCurrentJPanel;
            else{
                for (int i=0;i<listChannelPlayBack.size();i++){
                    ChannelPlayBack channelPlayBack2 =listChannelPlayBack.get(i);
                    if (channelPlayBack2.getPlayBackHandle().intValue() > -1) return i;
                }
            }
        }
        return -1;
    }
    /**
	 * 函数:      getCurrentChannelRealPlayer
         * 函数描述:   获得当前窗口的预览对象
         * @return ChannelRealPlayer   预览对象ChannelRealPlayer
    */
    private ChannelPlayBack getCurrentChannelPlayBack(){
        int Index  = getIndexOfCurrentJPanel();
        if (Index == -1) return null;
        return listChannelPlayBack.get(Index);
    }
    /**
	 * 函数:      getIfPanelPlayBack
         * 函数描述:  判断当前窗口是否已经在回放
         * @param IndexCurrentJPanel 窗口的索引号
         * @return boolean true在回放；
    */
    private boolean getIfPanelPlayBack(int IndexCurrentJPanel){
        
        ChannelPlayBack channelPlayBack =listChannelPlayBack.get(IndexCurrentJPanel);
        if (channelPlayBack.getPlayBackHandle().intValue() > -1) return true;
        else return false;
    }
    /**
	 * 函数:      remindBeforExit
         * 函数描述:  关闭当前窗口之前提醒是否还在回放。如果有的话，则停止所有回放
    */
    private void remindBeforExit(){
        if (getIfExistPlayBack()){
                if ((JOptionPane.showConfirmDialog(this, sMessageBoxExitPlayback,//"正在录像回放，退出就要停止回放。是否要继续？",  
                        sRemind,JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)) return;//"提醒"
                stopAllPanels();
        }
    }
    /**
	 * 函数:      zoomInPlayBackOneChannel
         * 函数描述:  将某一个通道的远程回放放大显示
         * @para ZoomIn  true放大预览；false缩小预览
    */
    private void zoomInPlayBackOneChannel(){
        try{
                //                zoomIn2();
                //jPanel2.setLayout(null);
                if (bZoomIn){

                    jPanelPlayBackContainer.setLayout(new GridLayout(2, 2,1,1));
                    jPanelPlayBackContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    for (int i=0;i<listJPanelPlayWindow.size();i++){
                        JPanel temJPanel = (JPanel)listJPanelPlayWindow.get(i);
                        temJPanel.setVisible(true);
                    }
                    bZoomIn = false;
                }else {

                    jPanelPlayBackContainer.setLayout(new java.awt.FlowLayout(FlowLayout.CENTER,1,1));
                    for (int i=0;i<listJPanelPlayWindow.size();i++){
                        JPanel temJPanel = (JPanel)listJPanelPlayWindow.get(i);
                        if (!currentJPanel.equals(temJPanel)) temJPanel.setVisible(false);
                    }
                    currentJPanel.setPreferredSize(new Dimension(jPanelPlayBackContainer.getWidth() - 4, jPanelPlayBackContainer.getHeight()-4));
                    bZoomIn = true;
                }

        }catch (Exception e){
            TxtLogger.append(sFileName, "zoomInPlayBackOneChannel()","系统在将某一个通道的远程回放放大显示过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    

//    /**
//          * 函数:      getRecordDownloadName
//          * 函数描述:  给出刚开始存储录像的初始文件名（不带后缀）
//          *             命名规则：PlayBack_设备名_接入设备名_通道号_开始时间_终止时间.mp4/jpg。如：PlayBack_录像机_鱼眼_4_20160726104122_20160726104124
//    */ 
//    private String getRecordDownloadName(String PathName,String StartTime,String StopTime){
//
//        String FileName = PathName + "\\PlayBack_" + sAnotherName + "_" + iChannel +  "_" + StartTime +  "_" + StopTime;
//        if (iChannel > HCNetSDK.MAX_ANALOG_CHANNUM) {
//            int Channel = iChannel - HCNetSDK.MAX_ANALOG_CHANNUM;
//            DeviceParaBean DeviceParaBeanJoin =  CommonParas.getDeviceParaBeanJoin(indexOflistDevice, iChannel, sFileName);
//            if (DeviceParaBeanJoin != null) FileName = PathName + "\\PlayBack_" + sAnotherName + "_"+ DeviceParaBeanJoin.getAnothername()
//                                                    + "_" + Channel +  "_" +  StartTime +  "_" + StopTime;
//        }
//        
//        return FileName;
//    }
//    /**
//	 * 函数:      writePlayBackLog
//         * 函数描述:  设备录像回放窗口的写日志函数。此函数不针对ChannelPlayBack对象
//    */
//    private void writePlayBackLog(String Description){
//        //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、备注、调用的文件名
//        //设置接入设备的序列号和设备资源节点
//        String[] ReturnA = CommonParas.getArraysFromTreeNode(sChannelNode);//数组：设备名、接入设备名、设备资源节点名
//        String SerialNoJoin = "";
//        if (!ReturnA[1].equals("")) SerialNoJoin = CommonParas.getSerialNO(ReturnA[1], sFileName);
//        CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, Description, sSerialNo, "", ReturnA[2], SerialNoJoin,"",
//                                CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,CommonParas.DVRResourceType.ENCODINGDVR_CHANNEL_CODE,sRemarks, sFileName);
//    }
//    /**
//	 * 函数:      writePlayBackErrorLog
//         * 函数描述:  设备录像回放窗口的写错误日志函数。此函数不针对ChannelPlayBack对象
//    */
//    private void writePlayBackErrorLog(String Description){
//        //写错误日志
//        //操作时间、设备别名、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
//        String[] ReturnA = CommonParas.getArraysFromTreeNode(sChannelNode);//数组：设备名、接入设备名、设备资源节点名
//        String SerialNoJoin = "";
//        if (!ReturnA[1].equals("")) SerialNoJoin = CommonParas.getSerialNO(ReturnA[1], sFileName);
//        CommonParas.SystemWriteErrorLog("", sAnotherName, Description, sSerialNo, "",ReturnA[2], SerialNoJoin,"",
//                                    CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,CommonParas.DVRResourceType.ENCODINGDVR_CHANNEL_CODE,sFileName);
//    }
    
    /**
        * 函数:      PanelMouseReleased
        * 函数描述:  在回放窗口松开鼠标按键时所进行的操作
        * @para evt  鼠标事件
    */
    private void PanelMouseReleased(java.awt.event.MouseEvent evt){
        
        try{
            jPanelPlayBackContainer.validate();
            
            ChannelPlayBack channelPlayBack = getCurrentChannelPlayBack();
            
            //显示录像完整性检测，如果还没有回放，JSlier也应该恢复默认的滑道显示
            refreshRecordInfo(channelPlayBack,null);
            //刷新当前回放窗口的信息，包括录像文件列表信息、相关控件信息
            refreshPlayBackInfo(channelPlayBack);
            
//            if (channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0) {
//                channelPlayBack.getPanelPlayBack().repaint();
//                jPanelPlayBackContainer.validate();
//            }
            //双击
            if (evt.getClickCount() == 2){  zoomInPlayBackOneChannel();  }
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PanelMouseReleased()","当前回放窗口在鼠标释放时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        
    }
    
    /**
	 * 函数:      setJSiliderModel
         * 函数描述:  重新设定JSlider控件的属性
         * @para Slider   JSlider控件
    */
    private void setJSiliderModel(JSlider Slider){
        Hashtable table = new Hashtable();

        table.put(0, new JLabel("00:00"));
        //table.put(3600, new JLabel("01:00"));
        table.put(7200, new JLabel("02:00"));
        //table.put(10800,new JLabel("03:00"));
        table.put(14400, new JLabel("04:00"));
        //table.put(18000, new JLabel("05:00"));
        table.put(21600, new JLabel("06:00"));
        //table.put(25200, new JLabel("07:00"));
        table.put(28800, new JLabel("08:00"));
        //table.put(32400, new JLabel("09:00"));
        table.put(36000,new JLabel("10:00"));
        //table.put(39600, new JLabel("11:00"));
        table.put(43200, new JLabel("12:00"));
        //table.put(46800, new JLabel("13:00"));
        table.put(50400, new JLabel("14:00"));

        //table.put(54000, new JLabel("15:00"));
        table.put(57600, new JLabel("16:00"));
        //table.put(61200, new JLabel("17:00"));
        table.put(64800, new JLabel("18:00"));
        //table.put(68400, new JLabel("19:00"));
        table.put(72000, new JLabel("20:00"));
        //table.put(75600, new JLabel("21:00"));
        table.put(79200, new JLabel("22:00"));
        //table.put(82800, new JLabel("23:00"));
        table.put(86400, new JLabel("00:00"));

        Slider.setLabelTable(table);
    }
    /**
        * 函数:      getTimeString
        * 函数描述:  获得时间字符串
    */
    private String getTimeString(int Value){
        try{
            int Hour,Minute,Second;
            Hour = Value/3600;
            Minute = Value%3600/60;
            Second = Value%3600%60;
            return CommonParas.getNumberToTwoBitsStr(Hour) + ":" + CommonParas.getNumberToTwoBitsStr(Minute) + ":" + CommonParas.getNumberToTwoBitsStr(Second);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getTimeString()","系统在获得时间字符串时，出现错误"
                             + "\r\n                       Exception:" + e.toString()); 
        }
        return "00:00:00";
    }
    /**
        * 函数:      getNetDVRTimeFromValue
        * 函数描述:  根据jSliderTimeLine的Value值，获得对应的NET_DVR_TIME对象
        * @param Value  jSliderTimeLine的Value值
    */
    private HCNetSDK.NET_DVR_TIME getNetDVRTimeFromValue(int Value){
        String TimeString = jLabelPlayBackDay.getText() + " "+ getTimeString(Value);
        DateHandler newDate = DateHandler.getTimeStringInstance(TimeString);
        HCNetSDK.NET_DVR_TIME StruAbsoluteTime = new HCNetSDK.NET_DVR_TIME();
        StruAbsoluteTime.dwYear = newDate.getYear();
        StruAbsoluteTime.dwMonth = newDate.getMonth();
        StruAbsoluteTime.dwDay = newDate.getDay();
        StruAbsoluteTime.dwHour = newDate.getHour();
        StruAbsoluteTime.dwMinute = newDate.getMinute();
        StruAbsoluteTime.dwSecond = newDate.getSecond();
        return StruAbsoluteTime;
    }
    /**
        * 函数:      getValueFromTime
        * 函数描述:  获得jSliderTimeLine时间轴上的时间对应的Value值
    */
    private int getValueFromTime(int Hour, int Minute, int Second){
        return Hour * 3600 + Minute * 60 + Second;
    }
    /**
        * 函数:      getValueFromSliderClick
        * 函数描述:  根据在控件jSliderTimeLine上的鼠标点击，获得点击处的jSliderTimeLine的Value值
    */
    private int getValueFromSliderClick(java.awt.event.MouseEvent evt){
        return (evt.getX()-iSpace) * jSliderTimeLine.getMaximum()/(jSliderTimeLine.getWidth()-iSpace*2);
    }
    /**
        * 函数:      setPanelPlayControlVisible
        * 函数描述:  设置回放窗口的控制条的显示/隐藏
    */
    private void setPanelPlayControlVisible(int Index){

        for (int i=0;i<WINDOWSNUM;i++){
            if (i == Index && listChannelPlayBack.get(Index).getPlayBackHandle().intValue() > -1) listJPanelPlayWindow.get(i).setPlayControlVisible(true);
            else listJPanelPlayWindow.get(i).setPlayControlVisible(false);
        }
        
    }
    /**
        * 函数:      refreshPlayBackInfo
        * 函数描述:   刷新当前回放窗口的信息，包括录像文件列表信息，控件显示信息
        * @param channelPlayBack ChannelPlayBack对象实例
    */
    private void refreshPlayBackInfo(ChannelPlayBack channelPlayBack){
        try{
            
            //录像剪辑的起止时间归零
            struDownloadStartTime.clear();
            struDownloadStopTime.clear();
            //downloadFlag=2表示已经开始下载，不能归零
            if (downloadFlag == 1) downloadFlag = 0;//录像剪辑（也就是下载）标志。0表示未开始；1表示剪辑了一下，起始时间；2表示剪辑了终止时间。
            //录像文件列表信息
            int IndexOfCurrentJPanel  = getIndexOfCurrentJPanel();
            refreshFileTableList(IndexOfCurrentJPanel);
            
            //左下角正在进行的录像回放的设备节点名称显示
            if (channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0) {
                    jLabelDVRNodeName2.setText("                                ");
            } else  jLabelDVRNodeName2.setText(" "+ channelPlayBack.getWholeDVRChannelNode());                                
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshPlayBackInfo()","显示在录像回放时间内的录像是否缺失信息时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      refreshRecordInfo
        * 函数描述:  在时间轴上显示在录像回放时间内的录像是否缺失信息，现在主要包括缺失录像的起始时间
        * @param channelPlayBack ChannelPlayBack对象实例
    */
    private void refreshRecordInfo(ChannelPlayBack channelPlayBack, MyDate NewDate){
        try{
            //将当日的录像是否缺失信息初始化
            mySliderUI.initialTrackParas();

            if(channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0){
                //显示初始化信息
                jSliderTimeLine.setUI(mySliderUI);
                jLabelPlayBackDay.setText("");
                jButtonPreviousDay.setEnabled(false);
                jButtonNextDay.setEnabled(false);
                return;
            }
            if (NewDate == null){
                //设置在当前回放日期24小时内的录像是否缺失信息（JSlider显示）
                HCNetSDK.NET_DVR_TIME StruPlayBackTime = new HCNetSDK.NET_DVR_TIME();//存储当前回放时间
                if (CommonParas.hCNetSDK.NET_DVR_GetPlayBackOsdTime(channelPlayBack.getPlayBackHandle(), StruPlayBackTime)){
                    NewDate = new MyDate(StruPlayBackTime.dwYear,  StruPlayBackTime.dwMonth,  StruPlayBackTime.dwDay);
                }else return;
            }
            //设置当前显示日期
            jLabelPlayBackDay.setText(NewDate.getDateString());

            /*设置正常录像时间段*/
            Point NormalPoint = new Point();
            HCNetSDK.NET_DVR_TIME struStartTime = channelPlayBack.getStruStartTime();
            HCNetSDK.NET_DVR_TIME struStopTime = channelPlayBack.getStruStopTime();
            int StartCompare = compareToPlayBackDay(struStartTime.dwYear, struStartTime.dwMonth, struStartTime.dwDay, NewDate.myYear,  NewDate.myMonth,  NewDate.myDay);
            if (StartCompare > 0){}//起始日期在当前显示日期之后，这种情况不可能
            else if (StartCompare == 0){
                NormalPoint.x = getValueFromTime(struStartTime.dwHour, struStartTime.dwMinute, struStartTime.dwSecond);
                jButtonPreviousDay.setEnabled(false);
            }
            else if (StartCompare < 0){//起始日期在显示回放日期之前
                NormalPoint.x = getValueFromTime(0, 0, 0);
                jButtonPreviousDay.setEnabled(true);
            }

            int StopCompare = compareToPlayBackDay(struStopTime.dwYear,   struStopTime.dwMonth,  struStopTime.dwDay,  NewDate.myYear,  NewDate.myMonth,  NewDate.myDay);
            if (StopCompare > 0){//截止日期在当前显示日期之后
                NormalPoint.y = getValueFromTime(24, 0, 0);
                jButtonNextDay.setEnabled(true);
            }
            else if (StopCompare == 0){
                NormalPoint.y = getValueFromTime(struStopTime.dwHour, struStopTime.dwMinute, struStopTime.dwSecond);
                jButtonNextDay.setEnabled(false);
            }
            else if (StopCompare < 0){}//截止日期在当前显示日期之前，这种情况不可能

            mySliderUI.setNormalTrackSection(NormalPoint);


            /*设置录像缺失时间段*/
            ArrayList<HCNetSDK.NET_DVR_TIME_EX> listRecordLostStartTime = channelPlayBack.getListRecordLostStartTime();//缺失录像的起始时间列表
            ArrayList<HCNetSDK.NET_DVR_TIME_EX> listRecordLostStopTime = channelPlayBack.getListRecordLostStopTime();//缺失录像的终止时间列表

            if (listRecordLostStartTime.size() != listRecordLostStopTime.size()) return;
            for (int i=0;i<listRecordLostStartTime.size();i++){
                Point RecordLostPoint = new Point();
                HCNetSDK.NET_DVR_TIME_EX StartTime2 = listRecordLostStartTime.get(i);
                HCNetSDK.NET_DVR_TIME_EX StopTime2  = listRecordLostStopTime.get(i);
                int StartCompare2 = compareToPlayBackDay(StartTime2.wYear, StartTime2.byMonth, StartTime2.byDay, NewDate.myYear,  NewDate.myMonth,  NewDate.myDay);
                int StopCompare2  = compareToPlayBackDay(StopTime2.wYear,  StopTime2.byMonth,  StopTime2.byDay,  NewDate.myYear,  NewDate.myMonth,  NewDate.myDay);
                //只分4种情况，整个缺失时间段：1、在当天时间内；2、整个时间段横跨当天00：00：00；3、整个时间段横跨当天24：00：00；4、整个时间段横跨当天24小时
                if (StartCompare2 ==0 && StopCompare2 == 0){
                    RecordLostPoint.x = getValueFromTime(StartTime2.byHour, StartTime2.byMinute, StartTime2.bySecond);
                    RecordLostPoint.y = getValueFromTime(StopTime2.byHour,  StopTime2.byMinute,  StopTime2.bySecond);
                }else if (StartCompare2  < 0 && StopCompare2 == 0){
                    RecordLostPoint.x = getValueFromTime(0, 0, 0);
                    RecordLostPoint.y = getValueFromTime(StopTime2.byHour,  StopTime2.byMinute,  StopTime2.bySecond);
                }else if (StartCompare2  == 0 && StopCompare2 > 0){
                    RecordLostPoint.x = getValueFromTime(StartTime2.byHour, StartTime2.byMinute, StartTime2.bySecond);
                    RecordLostPoint.y = getValueFromTime(24,  0,  0);
                }else if (StartCompare2  < 0 && StopCompare2 > 0){
                    RecordLostPoint.x = getValueFromTime(0, 0, 0);
                    RecordLostPoint.y = getValueFromTime(24,  0,  0);
                }else {
                    continue;
                }

                mySliderUI.addSpecialTrackSection(RecordLostPoint);
            }

            jSliderTimeLine.setUI(mySliderUI);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshRecordInfo()","显示在录像回放时间内的录像信息时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      compareToPlayBackDay
        * 函数描述:  判断日期与具体时间比较，是在之前，还是之后，还是同一天
        * @param int 在具体比较时间之后，返回1；同一天，返回0，之前，返回-1。
    */
    private int compareToPlayBackDay(int Year, int Month, int Day, HCNetSDK.NET_DVR_TIME StruPlayBackTime){
        return compareToPlayBackDay( Year,  Month,  Day,  StruPlayBackTime.dwYear,  StruPlayBackTime.dwMonth,  StruPlayBackTime.dwDay);
    }
    /**
        * 函数:      compareToPlayBackDay
        * 函数描述:  判断日期与具体时间比较，是在之前，还是之后，还是同一天
        * @param int 在具体比较时间之后，返回1；同一天，返回0，之前，返回-1。
    */
    private int compareToPlayBackDay(int Year, int Month, int Day, int Year2, int Month2, int Day2){
        if (Year > Year2) return 1;
        else if (Year < Year2) return -1;
        else if (Month > Month2) return 1;
        else if (Month < Month2) return -1;
        else if (Day > Day2) return 1;
        else if (Day < Day2) return -1;
        else return 0;
    }
//    /**
//        * 函数:      checkRecordLostInfo
//        * 函数描述:  检测在录像回放时间内的录像缺失信息，现在主要包括缺失录像的起始时间
//        * @param channelPlayBack ChannelPlayBack对象实例
//    */
//    private void checkRecordLostInfo(ChannelPlayBack channelPlayBack){
//        try{
//            DefaultTableModel FileTableModel = ((DefaultTableModel) jTableFileList.getModel());
//            int Count = FileTableModel.getRowCount();
//            //取出倒数第一行数据
//            String LastStartTime = FileTableModel.getValueAt(Count-1,2).toString();
//            String LastStopTime = FileTableModel.getValueAt(Count-1,3).toString();
//            for (int i=Count-2;i>-1;i--){
//                String StartTime = FileTableModel.getValueAt(i,2).toString();
//                String StopTime = FileTableModel.getValueAt(i,3).toString();
//                if (!StartTime.equals(LastStopTime)){
//                    channelPlayBack.addRecordLostTime(LastStopTime, StartTime);
//                }
//                LastStartTime = StartTime;
//                LastStopTime = StopTime;
//            }
//        }catch (Exception e){
//            TxtLogger.append(this.sFileName, "checkRecordLostInfo()","检测在录像回放时间内的录像缺失信息时，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
//    }
    
    /**
        * 函数:      checkRecordLostInfo
        * 函数描述:  检测在录像回放时间内的录像缺失信息，现在主要包括缺失录像的起始时间
        * @param channelPlayBack ChannelPlayBack对象实例
    */
    private void checkRecordLostInfo(int IndexOfCurrentJPanel){
        try{
            //回放对象
            ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
            if(channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0) return;
            
            HCNetSDK.NET_DVR_TIME struStartTime = channelPlayBack.getStruStartTime();
            HCNetSDK.NET_DVR_TIME struStopTime = channelPlayBack.getStruStopTime();
            ArrayList NewFilesList = listRecordFiles.get(IndexOfCurrentJPanel);
            //DefaultTableModel FileTableModel = ((DefaultTableModel) jTableFileList.getModel());
            
            int Count = NewFilesList.size();
            if (Count <0) return;
            if (Count == 0){//没有录像文件，则说明没有录像
                channelPlayBack.addRecordLostTime(struStartTime, struStopTime);
                return;
            }
            //取出第一行数据
            Vector FirstRow = (Vector)NewFilesList.get(0);
            String LastStartTime = FirstRow.get(2).toString();//FileTableModel.getValueAt(Count-1,2).toString();
            String LastStopTime = FirstRow.get(3).toString();//FileTableModel.getValueAt(Count-1,3).toString();
            
            //判断第一个文件的起始时间和回放对象的起始时间相比
            DateHandler dateHandler = DateHandler.getTimeStringInstance(LastStartTime);
            HCNetSDK.NET_DVR_TIME struLastStartTime = new HCNetSDK.NET_DVR_TIME(dateHandler.getYear(),dateHandler.getMonth(),dateHandler.getDay(),dateHandler.getHour(),dateHandler.getMinute(),dateHandler.getSecond());
            if (struLastStartTime.compareTo(struStartTime) > 0) 
                channelPlayBack.addRecordLostTime(struStartTime, struLastStartTime);//从回放对象起始时间到文件的起始时间
            
            for (int i=1;i<Count;i++){
                Vector NextRow = (Vector)NewFilesList.get(i);
                String StartTime = NextRow.get(2).toString();//FileTableModel.getValueAt(i,2).toString();
                String StopTime = NextRow.get(3).toString();//FileTableModel.getValueAt(i,3).toString();
                if (!StartTime.equals(LastStopTime)){
                    channelPlayBack.addRecordLostTime(LastStopTime, StartTime);
                }
                LastStartTime = StartTime;
                LastStopTime = StopTime;
            }
            //判断最后一个文件的结束时间和回放对象的终止时间相比
            dateHandler = DateHandler.getTimeStringInstance(LastStopTime);
            HCNetSDK.NET_DVR_TIME struLastStopTime = new HCNetSDK.NET_DVR_TIME(dateHandler.getYear(),dateHandler.getMonth(),dateHandler.getDay(),dateHandler.getHour(),dateHandler.getMinute(),dateHandler.getSecond());
            if (struLastStopTime.compareTo(struStopTime) < 0) 
                channelPlayBack.addRecordLostTime(struLastStopTime, struStopTime);//从最后一个文件的起始时间到回放对象终止时间
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "checkRecordLostInfo()","检测在录像回放时间内的录像缺失信息时，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      synTimeOhterPlayWindows
        * 函数描述:  将其余回放窗口按当前窗口进行时间同步操作
    */
//    private void synTimeOhterPlayWindows(){
//        //得到当前回放对象ChannelPlayBack，获得当前的回放时间
//        int IndexOfCurrentJPanel = getIndexOfCurrentJPanel();
//        ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
//        HCNetSDK.NET_DVR_TIME StruPlayBackTime = new HCNetSDK.NET_DVR_TIME();//存储当前回放时间
//        if (CommonParas.hCNetSDK.NET_DVR_GetPlayBackOsdTime(channelPlayBack.getPlayBackHandle(), StruPlayBackTime)){
//            
//            for (int i=0;i<listChannelPlayBack.size();i++){
//                if (i == IndexOfCurrentJPanel) continue;
//                ChannelPlayBack channelPlayBack2 = listChannelPlayBack.get(i);
//                setTimeOnePlayWindow(i, StruPlayBackTime);//将该回放窗口进行按绝对时间定位
//            }
//        }
//        
//    }
    
    /**
        * 函数:      synOhterPlayWindows
        * 函数描述:  将其余回放窗口按当前窗口进行同步操作
    */
    private void synOhterPlayWindows(){
        //得到当前回放对象ChannelPlayBack，获得当前的回放时间
        int IndexOfCurrentJPanel = getIndexOfAlreadyPlayBack();//getIndexOfCurrentJPanel();
        if (IndexOfCurrentJPanel == -1) return;
        ChannelPlayBack channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);

        if (channelPlayBack == null || channelPlayBack.getPlayBackHandle().intValue() < 0) return;
        //首先将暂停的变为回放,所有的回放速度变为正常速度
        for (int i=0;i<listJPanelPlayWindow.size();i++){
            //回放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(i);
            if (jPanelPlayWindow.getChannelPlayBack().getPlayBackHandle().intValue() == -1) continue;
            
            jPanelPlayWindow.setNormalPlayOnePanel();
        }
        
        //将所有其他窗口的回放状态与当前窗口同步
        for (int i=0;i<listJPanelPlayWindow.size();i++){
            //回放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(i);
            if (listChannelPlayBack.get(i).getPlayBackHandle().intValue() == -1) continue;
            
            if (i != IndexOfCurrentJPanel) 
                jPanelPlayWindow.setSynOnePanelNormalSpeed(channelPlayBack);
        }
        //最后将同步的按钮状态按照回放窗口一致处理
        synOCXState();

    }
    
    /**
        * 函数:      synOnePlayWindow
        * 函数描述:  同步状态下，回放某一通道录像时需要进行的将该回放窗口根据其他窗口的回放时间同步
    */
    private HCNetSDK.NET_DVR_TIME synOnePlayWindow(int IndexOfCurrentJPanel){
        if (IndexOfCurrentJPanel < 0) return null;
        for (int i=0;i<listChannelPlayBack.size();i++){

            if (i == IndexOfCurrentJPanel) continue;
            ChannelPlayBack channelPlayBack2 = listChannelPlayBack.get(i);
            if (channelPlayBack2 == null || channelPlayBack2.getPlayBackHandle().intValue() < 0) continue;
            //首先将所有的回放速度变为正常速度
            //播放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
            return jPanelPlayWindow.setSynOnePanel(channelPlayBack2);
            //最后将同步的按钮状态一致处理
            

        }
        return null;
    }
    
    /**
        * 函数:      synTimeAllPlayWindows
        * 函数描述:  同步状态下将所有的回放窗口进行时间同步操作(在回放时间轴上点击或者在录像文件上双击产生的所有窗口进行时间定位)
    */
    private void synTimeAllPlayWindows(HCNetSDK.NET_DVR_TIME StruPlayBackTime){

        for (int i=0;i<listChannelPlayBack.size();i++){
            //播放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(i);
            jPanelPlayWindow.setTimeOnePanel(StruPlayBackTime);
            //setTimeOnePlayWindow(i, StruPlayBackTime);//将该回放窗口进行按绝对时间定位
        }
    }
    /**
        * 函数:      synOCXState
        * 函数描述:  回放同步时，同步所有的回放按钮状态
    */ 
    private void synOCXState(){
        for (int i=0;i<listJPanelPlayWindow.size();i++){
            //回放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(i);
            ChannelPlayBack channelPlayBack = jPanelPlayWindow.getChannelPlayBack();
            if (channelPlayBack.getPlayBackHandle().intValue() != -1) {
                //主要包括回放按钮的状态，回放速度
                iPlayDirection = channelPlayBack.getPlayDirection();
                iPlayState = channelPlayBack.getPlayState();
                jTextFieldAllSpeed.setText(jPanelPlayWindow.getTextFieldSpeed());
                setEnable();
                break;
            }
            
        }
    }

    /**
        * 函数:      setEnable
        * 函数描述:  设置回放窗口按钮的Enable属性
    */ 
    private void setEnable(){
        if (ifSyn){
            if(!getIfExistPlayBack()) {
                jButtonAllPlay.setEnabled(false);
                //jButtonReverse.setEnabled(false);
                jButtonAllSlow.setEnabled(false);
                jButtonAllFast.setEnabled(false);
            }else{
                jButtonAllPlay.setEnabled(true);
                //jButtonReverse.setEnabled(false);
                jButtonAllSlow.setEnabled(true);
                jButtonAllFast.setEnabled(true);
            }
            
            //如果处于回放状态，则可以加速减速；如果处于暂停状态，则不能进行加速减速
            if (iPlayDirection == HCNetSDK.NET_DVR_PLAY_FORWARD && iPlayState == HCNetSDK.NET_DVR_PLAYPAUSE){
                jButtonAllSlow.setEnabled(false);
                jButtonAllFast.setEnabled(false);
            }else {
                jButtonAllSlow.setEnabled(true);
                jButtonAllFast.setEnabled(true);
            }
        }else{
            jButtonAllPlay.setEnabled(false);
            //jButtonReverse.setEnabled(false);
            jButtonAllSlow.setEnabled(false);
            jButtonAllFast.setEnabled(false);
        }
        
        setSynEnable();
    }
    
    private void setSynEnable(){
        for (int i=0;i<listJPanelPlayWindow.size();i++){
            //播放窗口
            JPanelPlayWindow jPanelPlayWindow = listJPanelPlayWindow.get(i);
            jPanelPlayWindow.setSynEnable(ifSyn);
        }
    }
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
            java.util.logging.Logger.getLogger(JFramePlayBack.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFramePlayBack.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFramePlayBack.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFramePlayBack.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFramePlayBack().setVisible(true);
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
        tableTitle[0] = Locales.getString("JFramePlayBack", "JFramePlayBack.tableTitle0");  //文件名称
        tableTitle[1] = Locales.getString("JFramePlayBack", "JFramePlayBack.tableTitle1");  //大小
        tableTitle[2] = Locales.getString("JFramePlayBack", "JFramePlayBack.tableTitle2");  //开始时间
        tableTitle[3] = Locales.getString("JFramePlayBack", "JFramePlayBack.tableTitle3");  //结束时间
        tableTitle[4] = Locales.getString("JFramePlayBack", "JFramePlayBack.tableTitle4");  //开始时间2
        tableTitle[5] = Locales.getString("JFramePlayBack", "JFramePlayBack.tableTitle5");  //结束时间2
        tableTitle[6] = Locales.getString("JFramePlayBack", "JFramePlayBack.tableTitle6");  //文件类型
        tableTitle[7] = Locales.getString("JFramePlayBack", "JFramePlayBack.tableTitle7");  //是否被锁

        recordFileTypeNames[0] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames0");  //定时录像
        recordFileTypeNames[1] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames1");  //移动侦测
        recordFileTypeNames[2] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames2");  //报警触发
        recordFileTypeNames[3] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames3");  //报警|移动侦测
        recordFileTypeNames[4] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames4");  //报警&移动侦测
        recordFileTypeNames[5] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames5");  //命令触发
        recordFileTypeNames[6] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames6");  //手动录像
        recordFileTypeNames[7] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames7");  //震动报警
        recordFileTypeNames[8] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames8");  //环境报警
        recordFileTypeNames[9] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames9");  //智能报警
        recordFileTypeNames[10] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames10");  //PIR报警
        recordFileTypeNames[11] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames11");  //无线报警
        recordFileTypeNames[12] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames12");  //呼救报警
        recordFileTypeNames[13] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames13");  //移动侦测、PIR、无线、呼救等所有报警类型的“或”
        recordFileTypeNames[14] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames14");  //智能交通事件
        recordFileTypeNames[15] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames15");  //越界侦测
        recordFileTypeNames[16] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames16");  //区域入侵侦测
        recordFileTypeNames[17] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames17");  //音频异常侦测
        recordFileTypeNames[18] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames18");  //场景变更侦测
        recordFileTypeNames[19] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames19");  //智能侦测
        recordFileTypeNames[20] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames20");  //人脸侦测
        recordFileTypeNames[21] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames21");  //信号量
        recordFileTypeNames[22] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames22");  //回传
        recordFileTypeNames[23] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames23");  //回迁录像
        recordFileTypeNames[24] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames24");  //遮挡
        recordFileTypeNames[25] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames25");  //进入区域侦测
        recordFileTypeNames[26] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames26");  //离开区域侦测
        recordFileTypeNames[27] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames27");  //徘徊侦测
        recordFileTypeNames[28] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames28");  // 人员聚集侦测
        recordFileTypeNames[29] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames29");  //快速运动侦测
        recordFileTypeNames[30] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames30");  //停车侦测
        recordFileTypeNames[31] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames31");  //物品遗留侦测
        recordFileTypeNames[32] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames32");  //物品拿取侦测
        recordFileTypeNames[33] = Locales.getString("JFramePlayBack", "JFramePlayBack.recordFileTypeNames33");  //火点检测
        
        sChoiceRecodType[0] = Locales.getString("JFramePlayBack", "JFramePlayBack.sChoiceRecodType0");  //全部录像
        sChoiceRecodType[1] = Locales.getString("JFramePlayBack", "JFramePlayBack.sChoiceRecodType1");  //定时录像
        sChoiceRecodType[2] = Locales.getString("JFramePlayBack", "JFramePlayBack.sChoiceRecodType2");  //手动录像
        sChoiceRecodType[3] = Locales.getString("JFramePlayBack", "JFramePlayBack.sChoiceRecodType3");  //命令触发
        sChoiceRecodType[4] = Locales.getString("JFramePlayBack", "JFramePlayBack.sChoiceRecodType4");  //智能侦测
        sChoiceRecodType[5] = Locales.getString("JFramePlayBack", "JFramePlayBack.sChoiceRecodType5");  //报警录像

        sSelectDownloadFile =           Locales.getString("JFramePlayBack", "JFramePlayBack.sSelectDownloadFile");  //请选择要下载的文件
        sDownloadFile =                 Locales.getString("JFramePlayBack", "JFramePlayBack.sDownloadFile");  //下载文件
        sRecordFileDownload =           Locales.getString("JFramePlayBack", "JFramePlayBack.sRecordFileDownload");  //录像文件下载
        sRecordFileDownload_Fail =      Locales.getString("JFramePlayBack", "JFramePlayBack.sRecordFileDownload_Fail");  //录像文件下载失败
        sRecordDownload =               Locales.getString("JFramePlayBack", "JFramePlayBack.sRecordDownload");  //录像下载
        sRecordDownload_Fail =          Locales.getString("JFramePlayBack", "JFramePlayBack.sRecordDownload_Fail");  //录像下载失败
        sDownloadByTime =               Locales.getString("JFramePlayBack", "JFramePlayBack.sDownloadByTime");  //按时间下载
        sDownloadRecordByTime_Fail =    Locales.getString("JFramePlayBack", "JFramePlayBack.sDownloadRecordByTime_Fail");  //按时间下载录像失败
        sDownloadRecordByTime =         Locales.getString("JFramePlayBack", "JFramePlayBack.sDownloadRecordByTime");  //按时间下载录像
        sStopDownload =                 Locales.getString("JFramePlayBack", "JFramePlayBack.sStopDownload");  //停止下载
        sStopDownloadColon =            Locales.getString("JFramePlayBack", "JFramePlayBack.sStopDownloadColon");  //停止下载：
        sDownloadOver =                 Locales.getString("JFramePlayBack", "JFramePlayBack.sDownloadOver");  //下载完毕
        sPause =                        Locales.getString("JFramePlayBack", "JFramePlayBack.sPause");  //暂停
        sAllPause =                     Locales.getString("JFramePlayBack", "JFramePlayBack.sAllPause");  //全部暂停
        sForwardPlayback =              Locales.getString("JFramePlayBack", "JFramePlayBack.sForwardPlayback");  //正向回放
//        sPlackbackExpectionMsg =        Locales.getString("JFramePlayBack", "JFramePlayBack.sPlackbackExpectionMsg");  //由于网络原因或DVR忙,回放异常终止！
        sRecordFileInfo =               Locales.getString("JFramePlayBack", "JFramePlayBack.sRecordFileInfo");  //开始：{0}<br>结束：{1}<br>类型：
        sPlackbackRecord =              Locales.getString("JFramePlayBack", "JFramePlayBack.sPlackbackRecord");  //录像回放
        sPlaybackByTimeColon =          Locales.getString("JFramePlayBack", "JFramePlayBack.sPlaybackByTimeColon");  //按时间回放
        sPlackbackRecord_Fail =         Locales.getString("JFramePlayBack", "JFramePlayBack.sPlackbackRecord_Fail");  //录像回放失败
        sStopPlackbackRecord =          Locales.getString("JFramePlayBack", "JFramePlayBack.sStopPlackbackRecord");  //结束录像回放
        sStopPlackbackRecord_Fail =     Locales.getString("JFramePlayBack", "JFramePlayBack.sStopPlackbackRecord_Fail");  //结束录像回放失败
        sVideoClip =                    Locales.getString("JFramePlayBack", "JFramePlayBack.sVideoClip");  //录像剪辑
        sVideoClipAgain =               Locales.getString("JFramePlayBack", "JFramePlayBack.sVideoClipAgain");  //再次剪辑
        sStopVideoClip =                Locales.getString("JFramePlayBack", "JFramePlayBack.sStopVideoClip");  //停止剪辑
//        sSaveRecord =                   Locales.getString("JFramePlayBack", "JFramePlayBack.sSaveRecord");  //保存录像
//        sSaveRecord_Succ =              Locales.getString("JFramePlayBack", "JFramePlayBack.sSaveRecord_Succ");  //成功保存录像：
//        sSaveRecordFile_Fail =          Locales.getString("JFramePlayBack", "JFramePlayBack.sSaveRecordFile_Fail");  //存储录像文件失败
        sSelectPlayFile =               Locales.getString("JFramePlayBack", "JFramePlayBack.sSelectPlayFile");  //请选择要播放的文件
        sChannel =                      Locales.getString("JFramePlayBack", "JFramePlayBack.sChannel");  //监控点
        sSearchRecord_Fail =            Locales.getString("JFramePlayBack", "JFramePlayBack.sSearchRecord_Fail");  //查找设备录像文件失败
        sSearchRecord_Null =            Locales.getString("JFramePlayBack", "JFramePlayBack.sSearchRecord_Null");  //没有搜到文件
        sCloseSearchRecord_Fail =       Locales.getString("JFramePlayBack", "JFramePlayBack.sCloseSearchRecord_Fail");  //关闭文件查找，释放资源失败
        sMessageBoxExitPlayback =       Locales.getString("JFramePlayBack", "JFramePlayBack.sMessageBoxExitPlayback");  //正在录像回放，退出就要停止回放。是否要继续？
        sRemind =                       Locales.getString("JFramePlayBack", "JFramePlayBack.sRemind");  //提醒
        sTitle =                        Locales.getString("JFramePlayBack", "JFramePlayBack.sTitle");  //远程回放
        sAudio =    Locales.getString("JFramePlayBack", "JFramePlayBack.sAudio");  //音量
        sCapture =  Locales.getString("JFramePlayBack", "JFramePlayBack.sCapture");  //抓图
        sFast =     Locales.getString("JFramePlayBack", "JFramePlayBack.sFast");  //快放
        sReverse =  Locales.getString("JFramePlayBack", "JFramePlayBack.sReverse");  //倒放
        sSave =     Locales.getString("JFramePlayBack", "JFramePlayBack.sSave");  //开始录像
        sSlow =     Locales.getString("JFramePlayBack", "JFramePlayBack.sSlow");  //慢放
        sStop =     Locales.getString("JFramePlayBack", "JFramePlayBack.sStop");  //停止
        sSearchDateRemind       = Locales.getString("JFramePlayBack", "JFramePlayBack.sSearchDateRemind");  //"起始日期不能在终止日期之后或相同！"
        
        //标签和按钮显示
        jLabelChannel.setText(" "+  Locales.getString("JFramePlayBack", "JFramePlayBack.sChannel"));  //监控点
        jLabelRecordType.setText(   Locales.getString("JFramePlayBack", "JFramePlayBack.jLabelRecordType"));  //起止时间：
        jLabelTimeStartStop.setText(Locales.getString("JFramePlayBack", "JFramePlayBack.jLabelTimeStartStop"));  //录像类型：
        jToggleButtonExit.setText(  Locales.getString("JFramePlayBack", "JFramePlayBack.jToggleButtonExit"));  //退出
        jButtonNextDay.setText(     Locales.getString("JFramePlayBack", "JFramePlayBack.jButtonNextDay"));  //下一日
        jButtonPreviousDay.setText( Locales.getString("JFramePlayBack", "JFramePlayBack.jButtonPreviousDay"));  //上一日
        jButtonDownloadByFile.setText( Locales.getString("JFramePlayBack", "JFramePlayBack.sDownloadFile"));  //下载文件
        jButtonDownloadByTime.setText( Locales.getString("JFramePlayBack", "JFramePlayBack.sDownloadByTime"));  //按时间下载
        jButtonRecordCut.setText( Locales.getString("JFramePlayBack", "JFramePlayBack.sVideoClip"));  //录像剪辑
        
        jButtonAllPlay.setToolTipText(  Locales.getString("JFramePlayBack", "JFramePlayBack.sAllPause"));  //全部暂停
        jButtonAllFast.setToolTipText(  Locales.getString("JFramePlayBack", "JFramePlayBack.jButtonAllFast"));  //全部快放
        jButtonaysn.setToolTipText(     Locales.getString("JFramePlayBack", "JFramePlayBack.jButtonaysn"));  //异步回放
        jButtonsyn.setToolTipText(      Locales.getString("JFramePlayBack", "JFramePlayBack.jButtonsyn"));  //同步回放
        jButtonAllSlow.setToolTipText(  Locales.getString("JFramePlayBack", "JFramePlayBack.jButtonAllSlow"));  //全部慢放
        jButtonAllStop.setToolTipText(  Locales.getString("JFramePlayBack", "JFramePlayBack.jButtonAllStop"));  //全部停止
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonAllFast;
    private javax.swing.JButton jButtonAllPlay;
    private javax.swing.JButton jButtonAllSlow;
    private javax.swing.JButton jButtonAllStop;
    private javax.swing.JButton jButtonDownloadByFile;
    private javax.swing.JButton jButtonDownloadByTime;
    private javax.swing.JButton jButtonNextDay;
    private javax.swing.JButton jButtonPreviousDay;
    private javax.swing.JButton jButtonRecordCut;
    private javax.swing.JButton jButtonaysn;
    private javax.swing.JButton jButtonsyn;
    private javax.swing.JComboBox<String> jComboBoxRecodType;
    private javax.swing.JDialog jDialogVolumeCtrl;
    private javax.swing.JLabel jLabelChannel;
    private javax.swing.JLabel jLabelDVRNodeName2;
    private javax.swing.JLabel jLabelPlayBackDay;
    private javax.swing.JLabel jLabelRecordType;
    private javax.swing.JLabel jLabelTimeStartStop;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelPlayBackContainer;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSearch;
    private javax.swing.JPanel jPanelSysTray;
    private javax.swing.JPanel jPanelTime;
    private javax.swing.JPanel jPanelTree;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneFileList;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSliderTimeLine;
    private javax.swing.JSplitPane jSplitPanePlayBack;
    private javax.swing.JTable jTableFileList;
    private javax.swing.JTextField jTextFieldAllSpeed;
    private javax.swing.JToggleButton jToggleButtonExit;
    private javax.swing.JToolBar jToolBarPlayBack;
    private javax.swing.JTree jTreeResource;
    private java.awt.Panel panelCtrTools1;
    // End of variables declaration//GEN-END:variables

    private String[] tableTitle = new String[]{"文件名称","大小","开始时间","结束时间","开始时间2","结束时间2","文件类型","是否被锁"};
    //录像文件类型
    private String[] recordFileTypeNames = new String[]{"定时录像","移动侦测","报警触发","报警|移动侦测","报警&移动侦测","命令触发","手动录像","震动报警","环境报警","智能报警",
                                                    "PIR报警","无线报警","呼救报警","移动侦测、PIR、无线、呼救等所有报警类型的\"或\"","智能交通事件","越界侦测","区域入侵侦测",
                                                    "音频异常侦测","场景变更侦测","智能侦测","人脸侦测 ","信号量","回传","回迁录像","遮挡","进入区域侦测","离开区域侦测",
                                                    "徘徊侦测", "人员聚集侦测","快速运动侦测","停车侦测","物品遗留侦测","物品拿取侦测","火点检测"};
    private String[] sChoiceRecodType = new String[]{"全部录像","定时录像","手动录像","命令触发","智能侦测","报警录像"};
    private int[] iRecordFileTypes = new int[]{0xff, 0, 6, 5, 19, -100};//文件类型，检索用的
    

    private String sSelectDownloadFile = "请选择要下载的文件";
    private String sDownloadFile = "下载文件";
    private String sRecordFileDownload = "录像文件下载";
    private String sRecordFileDownload_Fail = "录像文件下载失败";
    private String sRecordDownload = "录像下载";
    private String sRecordDownload_Fail = "录像下载失败";
    private String sDownloadByTime = "按时间下载";
    private String sDownloadRecordByTime_Fail = "按时间下载录像失败";
    private String sDownloadRecordByTime = "按时间下载录像";
    private String sStopDownload = "停止下载";
    private String sStopDownloadColon = "停止下载：";
    private String sDownloadOver = "下载完毕";
    private String sPause = "暂停";
    private String sAllPause = "全部暂停";
    private String sForwardPlayback = "正向回放";
//    private String sPlackbackExpectionMsg = "由于网络原因或DVR忙,回放异常终止！";
    private String sRecordFileInfo = "开始：{0}<br>结束：{1}<br>类型：";
    private String sPlackbackRecord = "录像回放";
    private String sPlaybackByTimeColon = "按时间回放：";
    private String sPlackbackRecord_Fail = "录像回放失败";
    private String sStopPlackbackRecord = "结束录像回放";
    private String sStopPlackbackRecord_Fail = "结束录像回放失败";
    private String sVideoClip = "录像剪辑";
    private String sVideoClipAgain = "再次剪辑";
    private String sStopVideoClip = "停止剪辑";
//    private String sSaveRecord = "保存录像";
//    private String sSaveRecord_Succ = "成功保存录像：";
//    private String sSaveRecordFile_Fail = "存储录像文件失败";
//    private String sSaveRecordFileColon_Fail = "存储录像文件失败：";
    private String sSelectPlayFile = "请选择要播放的文件";
    private String sChannel = "监控点";
    private String sSearchRecord_Fail = "查找设备录像文件失败";
    private String sSearchRecord_Null = "没有搜到文件";
    private String sCloseSearchRecord_Fail = "关闭文件查找，释放资源失败";
    private String sMessageBoxExitPlayback = "正在录像回放，退出就要停止回放。是否要继续？";
    private String sRemind = "提醒";
    private String sTitle = "远程回放";
    
    private String sAudio = "音量";
    private String sCapture = "抓图";
    private String sFast = "快放";
    private String sReverse = "倒放";
    private String sSave = "开始录像";
    private String sSlow = "慢放";
    private String sStop = "停止";
    
    private String sSearchDateRemind = "起始日期不能在终止日期之后或相同！";
    
    /**
        * 内部类:    JPanelPlayWindow
        * 类描述:  录像回放、控制窗口
    */
    class JPanelPlayWindow extends JPanel{
        private int iIndexOfPanel;
        //0、Header显示，包括:回放设备节点名、当前时间
        JPanel jPanelHeader = new JPanel();
        private JLabel jLabelDVRNodeName = new JLabel();
        //暂时不用这个时间显示了，没有什么意思，录像画面上都有。还浪费时间
        //private JLabel jLabelPlayBackCurrentTime = new JLabel();
        //1、回放播放窗口
        Panel  panelPlayBack = new Panel();
        //2、回放控制窗口
        JPanel jPanelPlayControl = new JPanel();
        //2.1回放控制的播放进度控制和声音控制
        //JPanel jPanelPlayControlSlider = new JPanel();
        JSlider jSliderPlayback = new JSlider();
        //2.1.2回放控制的声音控制
        JPanel jPanelPlayControlAudioSlider = new JPanel();
        JButton jButtonAudio = new JButton();
        JSlider jSliderAudio = new JSlider();
        //2.2回放控制的播放、暂停、快进、慢放、抓图、录像等
        JToolBar jToolBarPlayBack = new JToolBar();
        JButton jButtonReverse = new JButton();
        JButton jButtonPlay = new JButton();
        JButton jButtonStop = new JButton();
        JLabel jLabelSpace1 = new JLabel();
        JButton jButtonSlow = new JButton();
        JLabel jLabelSpeed = new JLabel();
        JTextField jTextFieldSpeed = new JTextField();
        JButton jButtonFast = new JButton();
        JLabel jLabelSpace2 = new JLabel();
        JButton jButtonCapture = new JButton();
        JButton jButtonSave = new JButton();
        JLabel jLabelSpace3 = new JLabel();
        //JLabel jTextFieldPlayTime = new JLabel();
        
        Point Point1 = new Point();
        Point Point2 = new Point();
        private ChannelPlayBack channelPlayBack;
        //GlassPaneMouseEvent myGlass = new GlassPaneMouseEvent(Point1,Point2);
        
        public JPanelPlayWindow(int IndexOfPanel){
            
            this.iIndexOfPanel = IndexOfPanel;
            intialJPanel();
        }
        //JPanel初始化功能
        private void intialJPanel(){

            this.setName("jPanelPlayWindow" + getIndexOfPanel()); // NOI18N
                    this.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseReleased(java.awt.event.MouseEvent evt) {
            //                jPanelPlayWindowMouseReleased(evt);
                            if (currentJPanel != null) currentJPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, iPanelBoderThickness));
                            currentJPanel = (JPanel)evt.getSource();
                            currentJPanel.setBorder(BorderFactory.createLineBorder(Color.RED, iPanelBoderThickness));
                            currentPanel = panelPlayBack;
                            setPanelPlayControlVisible(iIndexOfPanel);
                            //jPanelPlayControl.setVisible(true);
                        }
//                        @Override
//                        public void mouseEntered(java.awt.event.MouseEvent evt) {
//                            jPanelPlayControl.setVisible(true);
//                        }
//                        @Override
//                        public void mouseExited(java.awt.event.MouseEvent evt) {
//                            jPanelPlayControl.setVisible(false);
//                        }
                    });
                    
            this.setLayout(new java.awt.BorderLayout());
            
            /*----------0、回放Header窗口 开始---------*/
            jPanelHeader.setName("jPanelHeader" + getIndexOfPanel());
                jPanelHeader.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseReleased(java.awt.event.MouseEvent evt) {

                            if (currentJPanel != null) currentJPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, iPanelBoderThickness));
                            currentJPanel = (JPanel)panelPlayBack.getParent();//evt.getSource();
                            currentJPanel.setBorder(BorderFactory.createLineBorder(Color.RED, iPanelBoderThickness));
                            currentPanel = panelPlayBack;
                            setPanelPlayControlVisible(iIndexOfPanel);

                            PanelMouseReleased(evt);//包括：窗口放大、鼠标右键弹出菜单等

                }});
                jLabelDVRNodeName.setBackground(new java.awt.Color(151,151,151));
                jLabelDVRNodeName.setForeground(new java.awt.Color(255, 255, 255));//
                jLabelDVRNodeName.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
                jLabelDVRNodeName.setText("                             ");
                
//                jLabelPlayBackCurrentTime.setBackground(new java.awt.Color(151,151,151));
//                jLabelPlayBackCurrentTime.setForeground(new java.awt.Color(255, 255, 255));//
//                jLabelPlayBackCurrentTime.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
//                jLabelPlayBackCurrentTime.setText("                             ");
                
            jPanelHeader.setBackground(new java.awt.Color(151,151,151));
            jPanelHeader.setLayout(new java.awt.BorderLayout());
            jPanelHeader.add(jLabelDVRNodeName, java.awt.BorderLayout.CENTER);
//            jPanelHeader.add(jLabelPlayBackCurrentTime, java.awt.BorderLayout.EAST);
            this.add(jPanelHeader, java.awt.BorderLayout.NORTH);
            /*----------0、回放Header窗口 结束---------*/
            
            
            /*----------1、回放播放窗口 开始---------*/
            panelPlayBack.setName("panelPlayBack" + getIndexOfPanel());
            panelPlayBack.setBackground(new java.awt.Color(64, 64, 64));
            panelPlayBack.setForeground(new java.awt.Color(153, 255, 255));
                    panelPlayBack.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseReleased(java.awt.event.MouseEvent evt) {

                            if (currentJPanel != null) currentJPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, iPanelBoderThickness));
                            currentJPanel = (JPanel)panelPlayBack.getParent();//evt.getSource();
                            currentJPanel.setBorder(BorderFactory.createLineBorder(Color.RED, iPanelBoderThickness));
                            currentPanel = panelPlayBack;
                            setPanelPlayControlVisible(iIndexOfPanel);

                            PanelMouseReleased(evt);//包括：窗口放大、鼠标右键弹出菜单等

                        }
//                        @Override
//                        public void mouseEntered(java.awt.event.MouseEvent evt) {
//                            rootPane.setGlassPane(myGlass);
//                            Point1 = listJPanelPlayWindow.get(iIndexOfPanel).getLocationOnScreen();
//
//                            Point2 = new Point(Point1.x+listJPanelPlayWindow.get(iIndexOfPanel).getWidth(),Point1.y+listJPanelPlayWindow.get(iIndexOfPanel).getHeight());
//                            //Point contentPanePoint = SwingUtilities.convertPoint(contentPane,Point1, myGlass);
//                            Point contentPanePoint = SwingUtilities.convertPoint(contentPane,Point1, myGlass);
//                            myGlass.setPoint1(Point1);//SwingUtilities.convertPoint(contentPane,Point1, myGlass));//SwingUtilities.convertPoint(contentPane,Point1, myGlass));
//                            myGlass.setPoint2(Point2);//(SwingUtilities.convertPoint(contentPane,Point2, myGlass));//(SwingUtilities.convertPoint(contentPane,Point2, myGlass));
////                            Panel jPanel = listChannelPlayBack.get(iIndexOfPanel).getPanelPlayBack();
////                            Point1 = new Point(0,0);//jPanel.getLocation();
////                            Point2 = new Point(jPanel.getWidth(),jPanel.getHeight());//Point(Point1.x+jPanel.getWidth(),Point1.y+jPanel.getHeight());
////                            //Point contentPanePoint = SwingUtilities.convertPoint(contentPane,Point1, myGlass);
////                            //Point contentPanePoint = SwingUtilities.convertPoint(jPanel,Point1, myGlass);
////                            myGlass.setPoint1(SwingUtilities.convertPoint(jPanel,Point1, myGlass));//SwingUtilities.convertPoint(contentPane,Point1, myGlass));
////                            myGlass.setPoint2(SwingUtilities.convertPoint(jPanel,Point2, myGlass));//(SwingUtilities.convertPoint(contentPane,Point2, myGlass));
//                            
//                            myGlass.setVisible(true);
//
//                        }
//                        @Override
//                        public void mouseExited(java.awt.event.MouseEvent evt) {
//                            jPanelPlayControl.setVisible(false);
//                        }
                    });
            panelPlayBack.setLayout(new java.awt.BorderLayout());
            this.add(panelPlayBack, java.awt.BorderLayout.CENTER);
            /*-------1、回放播放窗口 结束-------*/
            

            /*-------2、回放控制窗口：设置播放控制JPanel 开始-------*/;
            jPanelPlayControl.setName("jPanelPlayControl" + getIndexOfPanel());
            jPanelPlayControl.setLayout(new GridLayout(iPanelBoderThickness, iPanelBoderThickness));
            
//                /*-------2.1回放控制的播放进度控制和声音控制   开始-------*/
//                jPanelPlayControlSlider.setName("jPanelPlayControlSlider" + getIndexOfPanel());
//                jPanelPlayControlSlider.setLayout(new java.awt.BorderLayout(10,0));
////                jPanelPlayControlSlider.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
//                        /*-------2.1.1回放控制的播放进度控制   开始-------*/
//                        //2.1.1回设置播放进度条
//                        jSliderPlayback.setValue(0);
//                        jSliderPlayback.addMouseListener(new java.awt.event.MouseAdapter() {
//                            @Override
//                            public void mouseReleased(java.awt.event.MouseEvent evt) {
//                                setDefaultCurrentJPanel();
//                                setOneSliderPlayback(getIndexOfPanel());
//                            }
//                        });
//                        jSliderPlayback.setSize(400, 20);//.setBounds(0, 330, 400, 20);
//                        /*-------2.1.1回放控制的播放进度控制   结束-------*/
//                jPanelPlayControlSlider.add(jSliderPlayback, java.awt.BorderLayout.CENTER);
//                
//
//                        /*-------*2.1.2回放控制的声音控制   开始-------**/
//                        jPanelPlayControlAudioSlider.setName("jPanelPlayControlAudioSlider" + getIndexOfPanel());
//                        jPanelPlayControlAudioSlider.setLayout(new java.awt.BorderLayout());
////                        jPanelPlayControlAudioSlider.setSize(110, 20);
//                                
//                                /*-------2.1.2.1设置声音设置按钮   开始-------**/
//                                jButtonAudio.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
//                                jButtonAudio.setForeground(new java.awt.Color(51, 51, 255));
//                                jButtonAudio.setText("Mute");
//                                jButtonAudio.setFocusable(false);
//                                jButtonAudio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
//                                jButtonAudio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
//                                jButtonAudio.setBorder(null);
//                                jButtonAudio.setRolloverEnabled(true);
//                                jButtonAudio.setBorderPainted(false);
//                                jButtonAudio.setContentAreaFilled(false); 
//                                jButtonAudio.addActionListener(new java.awt.event.ActionListener() {
//                                    @Override
//                                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                                        setDefaultCurrentJPanel();
//                                        setAudioIfMute(getIndexOfPanel());
//                                    }
//                                });
//                                /*-------2.1.2.2设置声音控制进度条   开始-------**/
//                        jPanelPlayControlAudioSlider.add(jButtonAudio,java.awt.BorderLayout.CENTER);//, 
//                                
//                                jSliderAudio.setValue(50);
//                                jSliderAudio.setPreferredSize(new java.awt.Dimension(80,20));
////                                jSliderAudio.setSize(80, 20);//.setBounds(200, 330, 100, 20);
//                                jSliderAudio.addMouseListener(new java.awt.event.MouseAdapter() {
//                                    @Override
//                                    public void mouseReleased(java.awt.event.MouseEvent evt) {
//                                        setDefaultCurrentJPanel();
//                                        setOneSliderAudio(getIndexOfPanel());
//                                    }
//                                });
//                                //jSliderAudio.setBounds(0, 330, 400, 20);
//                                /*-------2.1.2.2设置声音控制进度条   结束-------*/
//                        jPanelPlayControlAudioSlider.add(jSliderAudio, java.awt.BorderLayout.EAST);//
//                        /*-------2.1.2回放控制的声音控制   结束-------*/
//                jPanelPlayControlSlider.add(jPanelPlayControlAudioSlider, java.awt.BorderLayout.EAST);
//                /********2.1回放控制的播放进度控制和声音控制   结束-------*/
//                
//            jPanelPlayControl.add(jPanelPlayControlSlider);
            
                /*-------2.2回放控制的播放、暂停、快进、慢放、抓图、录像等   开始-------*/
                //设置此工具栏的 rollover 状态。如果 rollover 状态为 true，则仅当鼠标指针悬停在工具栏按钮上时，才绘制该工具栏按钮的边框。此属性的默认值为 false。
                jToolBarPlayBack.setBackground(new java.awt.Color(64, 64, 64));
                jToolBarPlayBack.setRollover(true);
                jToolBarPlayBack.setFloatable(false);
//                        jToolBarPlayBack.addMouseListener(new java.awt.event.MouseAdapter() {
//                            @Override
//                            public void mouseEntered(java.awt.event.MouseEvent evt) {
//                                jPanelPlayControl.setVisible(true);
//                            }
//                        });

                        /*2.2.0设置倒放按钮 开始*/
                        jButtonReverse.setBackground(new java.awt.Color(204, 204, 255));
                        jButtonReverse.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
                        jButtonReverse.setForeground(new java.awt.Color(51, 51, 255));
                        jButtonReverse.setToolTipText(sReverse);//"倒放"
                        jButtonReverse.setIcon(ImageIconBufferPool.getInstance().getImageIcon("reverse2.png"));
                        jButtonReverse.setFocusable(false);
                        jButtonReverse.setBorderPainted(false);
                        jButtonReverse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        jButtonReverse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        jButtonReverse.addActionListener(new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                setDefaultCurrentJPanel();
                                setReverseOnePanel();
                                setEnable();
                            }
                        });
                        /*2.2.0设置倒放按钮 结束*/
                jToolBarPlayBack.add(jButtonReverse);
                
                        /*2.2.1设置播放按钮 开始*/
                        jButtonPlay.setBackground(new java.awt.Color(204, 204, 255));
                        jButtonPlay.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
                        jButtonPlay.setForeground(new java.awt.Color(51, 51, 255));
                        jButtonPlay.setToolTipText(sPause);//"暂停");
                        jButtonPlay.setIcon(ImageIconBufferPool.getInstance().getImageIcon("pause2.png"));
                        jButtonPlay.setFocusable(false);
                        jButtonPlay.setBorderPainted(false);
                        jButtonPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        jButtonPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        jButtonPlay.addActionListener(new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                setDefaultCurrentJPanel();
                                setPlayOnePanel();
                                setEnable();
                            }
                        });
                        /*2.2.1设置播放按钮 结束*/
                jToolBarPlayBack.add(jButtonPlay);

                        /*2.2.2设置停止播放按钮 开始*/
                        jButtonStop.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
                        jButtonStop.setForeground(new java.awt.Color(51, 51, 255));
                        jButtonStop.setIcon(ImageIconBufferPool.getInstance().getImageIcon("stop2.png"));
                        jButtonStop.setToolTipText(sStop);//"停止"
                        jButtonStop.setFocusable(false);
                        jButtonStop.setBorderPainted(false);
                        jButtonStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        jButtonStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                setDefaultCurrentJPanel();
                                stopOnePanel(getIndexOfPanel());
                            }
                        });
                        /*2.2.2设置停止播放按钮 结束*/
                jToolBarPlayBack.add(jButtonStop);


                        /*2.2.3间隔条 开始*/
                        jLabelSpace1.setText("   ");
                        /*2.2.3间隔条 结束*/
                jToolBarPlayBack.add(jLabelSpace1);

                        /*2.2.4设置慢速播放按钮 开始*/
                        jButtonSlow.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
                        jButtonSlow.setForeground(new java.awt.Color(51, 51, 255));
                        jButtonSlow.setIcon(ImageIconBufferPool.getInstance().getImageIcon("slow2.png"));
                        jButtonSlow.setToolTipText(sSlow);//"慢放"
                        jButtonSlow.setFocusable(false);
                        jButtonSlow.setBorderPainted(false);
                        jButtonSlow.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        jButtonSlow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        jButtonSlow.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                setDefaultCurrentJPanel();
                                //setSlowPlayOnePanel(getIndexOfPanel());
                                setSlowPlayOnePanel();
                            }
                        });
                        /*2.2.4设置慢速播放按钮 结束*/

                jToolBarPlayBack.add(jButtonSlow);
                        /*2.2.5设置播放速度显示文本框 开始*/                
                        jTextFieldSpeed.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
                        jTextFieldSpeed.setForeground(new java.awt.Color(51, 51, 255));
                        jTextFieldSpeed.setBackground(new java.awt.Color(240, 240, 240));
                        jTextFieldSpeed.setHorizontalAlignment(javax.swing.JTextField.CENTER);
                        jTextFieldSpeed.setPreferredSize(new Dimension(40,25));
                        jTextFieldSpeed.setEditable(false);
                        jTextFieldSpeed.setOpaque(false);
                        jTextFieldSpeed.setText("1x");
                        /*2.2.5设置播放速度显示文本框 结束*/      
                jToolBarPlayBack.add(jTextFieldSpeed);
                        /*2.2.6设置快速播放按钮 开始*/
                        jButtonFast.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
                        jButtonFast.setForeground(new java.awt.Color(51, 51, 255));
                        jButtonFast.setIcon(ImageIconBufferPool.getInstance().getImageIcon("fast2.png"));
                        jButtonFast.setToolTipText(sFast);//"快放"
                        jButtonFast.setFocusable(false);
                        jButtonFast.setBorderPainted(false);
                        jButtonFast.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        jButtonFast.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        jButtonFast.addActionListener(new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                setDefaultCurrentJPanel();
                                //setFastPlayOnePanel(getIndexOfPanel());
                                setFastPlayOnePanel();
                            }
                        });
                        /*2.2.6设置快速播放按钮 结束*/
                jToolBarPlayBack.add(jButtonFast);

                        /*2.2.7间隔条 开始*/
                        jLabelSpace2.setText("  ");
                        /*2.2.7间隔条 结束*/
                jToolBarPlayBack.add(jLabelSpace2);

                        /*2.2.8设置抓图按钮 开始*/
                        jButtonCapture.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
                        jButtonCapture.setForeground(new java.awt.Color(51, 51, 255));
                        jButtonCapture.setIcon(ImageIconBufferPool.getInstance().getImageIcon("capture2.png"));
                        jButtonCapture.setToolTipText(sCapture);//"抓图"
                        jButtonCapture.setFocusable(false);
                        jButtonCapture.setBorderPainted(false);
                        jButtonCapture.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        jButtonCapture.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        jButtonCapture.addActionListener(new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                setDefaultCurrentJPanel();
                                channelPlayBack.captureOnePanel();
                                //captureOnePanel(getIndexOfPanel());
                            }
                        });
                        /*2.2.8设置抓图按钮 结束*/
                jToolBarPlayBack.add(jButtonCapture);

                        /*2.2.9设置保存录像回放的按钮 开始*/
                        jButtonSave.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
                        jButtonSave.setForeground(new java.awt.Color(51, 51, 255));
                        jButtonSave.setIcon(ImageIconBufferPool.getInstance().getImageIcon("record2.png"));
                        jButtonSave.setToolTipText(sSave);//"开始录像"
                        jButtonSave.setFocusable(false);
                        jButtonSave.setBorderPainted(false);
                        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                setDefaultCurrentJPanel();
                                setSaveOnePanelRecord();
//                                saveOnePanelRecord(getIndexOfPanel());
//                                if (channelPlayBack.isIfSaveFile()) jButtonSave.setIcon(ImageIconBufferPool.getInstance().getImageIcon("stoprecord2.png"));
//                                else jButtonSave.setIcon(ImageIconBufferPool.getInstance().getImageIcon("record2.png"));
                            }
                        });
                        /*2.2.9设置保存录像回放的按钮 开始*/
                jToolBarPlayBack.add(jButtonSave);

                        /*2.2.10间隔条 开始*/
                        jLabelSpace3.setText("  ");
                        /*2.2.10间隔条 结束*/
                jToolBarPlayBack.add(jLabelSpace3);
                
                        /*2.2.11设置声音设置按钮 开始*/
                        jButtonAudio.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
                        jButtonAudio.setForeground(new java.awt.Color(51, 51, 255));
                        jButtonAudio.setIcon(ImageIconBufferPool.getInstance().getImageIcon("audio2.png"));
                        jButtonAudio.setToolTipText(sAudio);//"音量"
                        jButtonAudio.setFocusable(false);
                        jButtonAudio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        jButtonAudio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        jButtonAudio.setBorder(null);
                        jButtonAudio.setRolloverEnabled(true);
                        jButtonAudio.setBorderPainted(false);
                        jButtonAudio.setContentAreaFilled(false); 
                        jButtonAudio.addActionListener(new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                setDefaultCurrentJPanel();
                                channelPlayBack.setAudioIfMute();
                                //setAudioIfMute(getIndexOfPanel());
                                if (channelPlayBack.getAudioOpen()== HCNetSDK.NET_DVR_PLAYSTARTAUDIO)
                                    jButtonAudio.setIcon(ImageIconBufferPool.getInstance().getImageIcon("audio2.png"));
                                else jButtonAudio.setIcon(ImageIconBufferPool.getInstance().getImageIcon("mute2.png"));
                            }
                        });
                        /*2.2.11设置声音设置按钮 结束*/
                jToolBarPlayBack.add(jButtonAudio);
                        /*2.2.12设置声音jSlider 开始*/
                        jSliderAudio.setValue(50);
                        jSliderAudio.setPreferredSize(new java.awt.Dimension(80,20));
        //                                jSliderAudio.setSize(80, 20);//.setBounds(200, 330, 100, 20);
                        jSliderAudio.addMouseListener(new java.awt.event.MouseAdapter() {
                            @Override
                            public void mouseReleased(java.awt.event.MouseEvent evt) {
                                setDefaultCurrentJPanel();
                                channelPlayBack.setSliderAudio(jSliderAudio);
                                //setOneSliderAudio(getIndexOfPanel());
                            }
                        });
                        /*2.2.12设置声音jSlider 结束*/
                jToolBarPlayBack.add(jSliderAudio);
                jToolBarPlayBack.setUI(new ToolBarUI());
                
//                    /*2.2.11播放时间 开始*/
//                    jTextFieldPlayTime.setForeground(new java.awt.Color(51, 51, 255));
//                    jTextFieldPlayTime.setText("                             ");
//                    /*2.2.11播放时间 结束*/
//                jToolBarPlayBack.add(jTextFieldPlayTime);
                /*-------2.2回放控制的播放、暂停、快进、慢放、抓图、录像等   结束-------*/
            jPanelPlayControl.add(jToolBarPlayBack);
            jPanelPlayControl.setVisible(false);
            /*-------2、回放控制窗口：设置播放控制JPanel 结束-------*/;
            
            this.add(jPanelPlayControl, java.awt.BorderLayout.SOUTH);
//            this.add(jToolBarPlayBack, java.awt.BorderLayout.SOUTH);
        }
        
        public void intialComponentParas(){
            
            jButtonPlay.setIcon(ImageIconBufferPool.getInstance().getImageIcon("play2.png"));jButtonPlay.setToolTipText(sForwardPlayback);//"正向回放");
            jButtonSave.setIcon(ImageIconBufferPool.getInstance().getImageIcon("record2.png"));jButtonSave.setToolTipText("开始录像");
            jButtonAudio.setIcon(ImageIconBufferPool.getInstance().getImageIcon("audio2.png"));
            jLabelDVRNodeName.setText("                             ");
//            jLabelPlayBackCurrentTime.setText("                             ");
            //jTextFieldPlayTime.setText("                             ");
//            jSliderPlayback.setValue(0);
            jSliderPlayback.setValue(-1);
            jSliderAudio.setValue(50);
            jTextFieldSpeed.setText("1x");
        }
        public void setDefaultCurrentJPanel(){
            if (currentJPanel != null) currentJPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            currentJPanel = this;
            currentJPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            currentPanel = panelPlayBack;
        }
        /**
            * 函数:      setEnable
            * 函数描述:  设置回放窗口按钮的Enable属性
        */ 
        private void setEnable(){
            if (channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYPAUSE){
                jButtonSlow.setEnabled(false);
                jButtonFast.setEnabled(false);
                jButtonSave.setEnabled(false);
                jButtonCapture.setEnabled(false);
            }else{
                jButtonSlow.setEnabled(true);
                jButtonFast.setEnabled(true);
                jButtonSave.setEnabled(true);
                jButtonCapture.setEnabled(true);
            }
        }
        /**
            * 函数:      setSynEnable
            * 函数描述:  设置回放窗口在处于和其他窗口同步状态时，按钮的Enable属性
        */ 
        public void setSynEnable(boolean IfSyn){
            if (IfSyn){//如果同步状态
                jButtonReverse.setEnabled(false);
                jButtonPlay.setEnabled(false);
                jButtonSlow.setEnabled(false);
                jButtonFast.setEnabled(false);

            }else{//异步状态，恢复窗口本身按钮的本来状态
                jButtonReverse.setEnabled(true);
                jButtonPlay.setEnabled(true);
                setEnable();
            }
        }
        
        public void setReverseOnePanel(){
            if (channelPlayBack.getPlayBackHandle().intValue() == -1) return;
            channelPlayBack.reversePanel();
            setTextFieldSpeed(channelPlayBack.getTextOfSpeed());
            //reverseOnePanel(getIndexOfPanel());
            //NET_DVR_PLAY_FORWARD 29 倒放切换为正放/NET_DVR_PLAY_REVERSE 30 正放切换为倒放 
            //如果回放状态为正在回放，而且回放方向为倒放，则为暂停图标，否则为倒放图标
            if (channelPlayBack.getPlayDirection()== HCNetSDK.NET_DVR_PLAY_REVERSE && channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYSTART){
                //jButtonPlay.setText("Pause");
                jButtonReverse.setIcon(ImageIconBufferPool.getInstance().getImageIcon("pause2.png"));jButtonReverse.setToolTipText("暂停");
                jButtonPlay.setIcon(ImageIconBufferPool.getInstance().getImageIcon("play2.png"));jButtonPlay.setToolTipText("正向回放");
            }else {
                jButtonReverse.setIcon(ImageIconBufferPool.getInstance().getImageIcon("reverse2.png"));jButtonReverse.setToolTipText("倒放");
            }

        }
        public void setPlayOnePanel(){
            if (channelPlayBack.getPlayBackHandle().intValue() == -1) return;
            channelPlayBack.playPanel();
            setTextFieldSpeed(channelPlayBack.getTextOfSpeed());
            //如果回放状态为正在回放，而且回放方向为正向，则为暂停图标，否则为正放图标
            if (channelPlayBack.getPlayDirection()== HCNetSDK.NET_DVR_PLAY_FORWARD && channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYSTART){
                jButtonPlay.setIcon(ImageIconBufferPool.getInstance().getImageIcon("pause2.png"));jButtonPlay.setToolTipText("暂停");
                jButtonReverse.setIcon(ImageIconBufferPool.getInstance().getImageIcon("reverse2.png"));jButtonReverse.setToolTipText("倒放");
            }else {
                jButtonPlay.setIcon(ImageIconBufferPool.getInstance().getImageIcon("play2.png"));jButtonPlay.setToolTipText("正向回放");
            }
        }

        public void setNormalPlayOnePanel(){
            if (channelPlayBack.getPlayBackHandle().intValue() == -1) return;
            //如果刚才处于暂停状态，则按钮要恢复到回放暂停状态；现在都恢复到回放状态
            jButtonPlay.setIcon(ImageIconBufferPool.getInstance().getImageIcon("pause2.png"));jButtonPlay.setToolTipText("暂停");
            channelPlayBack.normalPlayBackState();
            channelPlayBack.normalPlayBackSpeed();
            
            
            setTextFieldSpeed(channelPlayBack.getTextOfSpeed());
        }
        
        public void setSlowPlayOnePanel(){
            if (channelPlayBack.getPlayBackHandle().intValue() == -1) return;
            channelPlayBack.slowPlayBackSpeed();
            setTextFieldSpeed(channelPlayBack.getTextOfSpeed());
        }
        
        public void setFastPlayOnePanel(){
            try{
                if (channelPlayBack.getPlayBackHandle().intValue() == -1) return;
                channelPlayBack.fastPlayBackSpeed();
                setTextFieldSpeed(channelPlayBack.getTextOfSpeed());
            }catch(Exception e){
                TxtLogger.append(this.getClass().getName(), "setFastPlayOnePanel()","快速播放的过程中，出现错误"
                                 + "\r\n                       Exception:" + e.toString());   
            }
        }
        
        public void setSaveOnePanelRecord(){
            if (channelPlayBack.getPlayBackHandle().intValue() == -1) return;
            channelPlayBack.saveOnePanelRecord();
            if (channelPlayBack.isIfSaveFile()) {
                jButtonSave.setIcon(ImageIconBufferPool.getInstance().getImageIcon("stoprecord2.png"));jButtonSave.setToolTipText("停止录像");
            }
            else {
                jButtonSave.setIcon(ImageIconBufferPool.getInstance().getImageIcon("record2.png"));jButtonSave.setToolTipText("开始录像");
            }
        }
        
        public HCNetSDK.NET_DVR_TIME setSynOnePanel(ChannelPlayBack OtherChannelPlayBack){
            if (channelPlayBack.getPlayBackHandle().intValue() == -1) return null;
            HCNetSDK.NET_DVR_TIME StruPlayBackTime = channelPlayBack.synOtherObject(OtherChannelPlayBack);
            setTextFieldSpeed(channelPlayBack.getTextOfSpeed());
            setSynEnable(ifSyn);
            return StruPlayBackTime;
        }
        
        public HCNetSDK.NET_DVR_TIME setSynOnePanelNormalSpeed(ChannelPlayBack OtherChannelPlayBack){
            if (channelPlayBack.getPlayBackHandle().intValue() == -1) return null;
            HCNetSDK.NET_DVR_TIME StruPlayBackTime = channelPlayBack.synOtherObjectNormalSpeed(OtherChannelPlayBack);
            setTextFieldSpeed(channelPlayBack.getTextOfSpeed());
            setSynEnable(ifSyn);
            return StruPlayBackTime;
        }
        /**
            * 函数:      setTimeOnePanel
            * 函数描述:  回放窗口进行按绝对时间定位
        */
        private void setTimeOnePanel(HCNetSDK.NET_DVR_TIME StruPlayBackTime){
            if (channelPlayBack.getPlayBackHandle().intValue() == -1) return;
            if (channelPlayBack.getPlayBackHandle().intValue() > -1){
                if (channelPlayBack.getPlayState() == HCNetSDK.NET_DVR_PLAYPAUSE){
                    setPlayOnePanel();
                }
                channelPlayBack.setPlayTime(StruPlayBackTime);
            }
        }
        public void setTextFieldSpeed(String sSpeed){
            jTextFieldSpeed.setText(" " + sSpeed + " ");
        }
        public String getTextFieldSpeed(){
            return jTextFieldSpeed.getText();
        }
        public void setPlayControlVisible(boolean ifShow){
            jPanelPlayControl.setVisible(ifShow);
        }
        /**
         * @return the iIndexOfPanel
         */
        public int getIndexOfPanel() {
            return iIndexOfPanel;
        }

        /**
         * @return the jLabelDVRNodeName
         */
        public String getDVRNodeName() {
            return jLabelDVRNodeName.getText().trim();
        }

        /**
         * @param jLabelDVRNodeName the jLabelDVRNodeName to set
         */
        public void setDVRNodeName(String DVRNodeName) {
            this.jLabelDVRNodeName.setText(" "+DVRNodeName);// = jLabelDVRNodeName;
        }

//        /**
//         * @return the jLabelPlayBackCurrentTime
//         */
//        public String getPlayBackCurrentTime() {
//            return jLabelPlayBackCurrentTime.getText().trim();
//        }
//
//        /**
//         * @param jLabelPlayBackCurrentTime the jLabelPlayBackCurrentTime to set
//         */
//        public void setPlayBackCurrentTime(String PlayBackCurrentTime) {
//            this.jLabelPlayBackCurrentTime.setText(PlayBackCurrentTime + " ");// = jLabelPlayBackCurrentTime;
//        }

        /**
         * @param channelPlayBack the channelPlayBack to set
         */
        public void setChannelPlayBack(ChannelPlayBack channelPlayBack) {
            this.channelPlayBack = channelPlayBack;
        }

        /**
         * @return the channelPlayBack
         */
        public ChannelPlayBack getChannelPlayBack() {
            return channelPlayBack;
        }
        /**
         * 内部私有类 玻璃窗板
         */
//        class GlassPaneMouseEvent  extends JComponent{
//            private Point point1;
//            private Point point2;
//            private boolean ifInAera = false;
//
//            GlassPaneMouseEvent(Point Point1,Point Point2) {
//                    // Should forward the input events to check box.
//                    this.point1 = Point1;
//                    this.point2 = Point2;
//                    addMouseMotionListener(new MouseMotionListener() {
//                        @Override
//                        public void mouseDragged(MouseEvent e) {
//                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                        }
//
//                        @Override
//                        public void mouseMoved(MouseEvent e) {
//                            Point glassPanePoint = e.getLocationOnScreen();
//                            if (glassPanePoint.x > point1.x && glassPanePoint.x<point2.x && glassPanePoint.y>point1.y && glassPanePoint.y<point2.y) {
//                                jPanelPlayControl.setVisible(true);
//                                setIfInAera(true);
//                            }
//                            else {
//                                jPanelPlayControl.setVisible(false);
//                                setIfInAera(false);
//                                
//                            }
//                        }
//                    });
//            }
//
//
//            /**
//             * @return the ifInAera
//             */
//            public boolean isIfInAera() {
//                return ifInAera;
//            }
//            /**
//                * @param ifInAera the ifInAera to set
//            */
//            public void setIfInAera(boolean ifInAera) {
//                this.ifInAera = ifInAera;
//                if (!ifInAera)  this.setVisible(false);
//            }
//            /**
//             * @param point1 the point1 to set
//             */
//            public void setPoint1(Point point1) {
//                this.point1 = point1;
//            }
//
//            /**
//             * @param point2 the point2 to set
//             */
//            public void setPoint2(Point point2) {
//                this.point2 = point2;
//            }
//        }

    }
    
    
    /**
        * 内部类:    DownloadTask
        * 类描述:  下载定时器响应函数
    */
    class DownloadTask extends java.util.TimerTask
    {
        private String sTextOfButton = "";
        private String sTextOfImageIcon = "";
        private JButton jButtonDownload;
        private ChannelPlayBack channelPlayBack;
        
        public DownloadTask(String TextOfButton, String TextOfImageIcon, JButton ButtonDownload, ChannelPlayBack channelPlayBack){
            this.sTextOfButton = TextOfButton;
            this.sTextOfImageIcon = TextOfImageIcon;
            this.jButtonDownload = ButtonDownload;
            this.channelPlayBack = channelPlayBack;
        }
        //定时器函数 
        @Override
        public void run()
        {
            try{
        	int  iPos = CommonParas.hCNetSDK.NET_DVR_GetDownloadPos(m_lDownloadHandle);
//                System.out.println(iPos);
                if (iPos < 0)		               //failed
                {
                        CommonParas.hCNetSDK.NET_DVR_StopGetFile(m_lDownloadHandle);
                        jProgressBar.setVisible(false);
                        jButtonDownload.setText(sTextOfButton);
                        jButtonDownload.setIcon(ImageIconBufferPool.getInstance().getImageIcon(sTextOfImageIcon));
                        m_lDownloadHandle.setValue(-1);
                        downloadFlag = 0;
                        channelPlayBack.writePlayBackErrorLog(sRecordDownload_Fail);//"录像下载失败"
                        CommonParas.showErrorMessage(sRecordDownload_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"录像下载失败"

                        Downloadtimer.cancel();
                }else if (iPos == 100) {		//end download
                        CommonParas.hCNetSDK.NET_DVR_StopGetFile(m_lDownloadHandle);
                        jProgressBar.setVisible(false);
                        jButtonDownload.setText(sTextOfButton);
                        jButtonDownload.setIcon(ImageIconBufferPool.getInstance().getImageIcon(sTextOfImageIcon));
                        m_lDownloadHandle.setValue(-1);
                        downloadFlag = 0;
                        channelPlayBack.writePlayBackLog(sRecordDownload);//"录像下载"
                        CommonParas.showMessage(sStopDownloadColon, sFileName);//"下载完毕"

                        Downloadtimer.cancel();
                }else if (iPos > 100) {	        //download exception for network problems or DVR hasten
                        CommonParas.hCNetSDK.NET_DVR_StopGetFile(m_lDownloadHandle);
                        jProgressBar.setVisible(false);
                        jButtonDownload.setText(sTextOfButton);
                        jButtonDownload.setIcon(ImageIconBufferPool.getInstance().getImageIcon(sTextOfImageIcon));
                        m_lDownloadHandle.setValue(-1);
                        downloadFlag = 0;
                        channelPlayBack.writePlayBackErrorLog(sRecordDownload_Fail);//"录像下载失败"
                        CommonParas.showErrorMessage(sRecordDownload_Fail, channelPlayBack.getDeviceParaBean().getAnothername(), sFileName);//"录像下载失败"

                        Downloadtimer.cancel();
                }else{
                        jProgressBar.setValue(iPos);
                }
            }catch (Exception e){
                TxtLogger.append(sFileName, "DownloadTask.run()","系统在下载定时器响应函数运行过程中，出现错误"
                                 + "\r\n                       Exception:" + e.toString());
            }
        }
    }
    
    /**
        * 内部类:    PlaybackTimeTask
        * 函数描述:  按时间回放定时器响应函数
    */
    class PlaybackTimeTask extends java.util.TimerTask
    {
        private NativeLong PlayBackHandle;
        private int IndexOfCurrentJPanel;
        //回放对象
        private ChannelPlayBack channelPlayBack;
        private JPanelPlayWindow jPanelPlayWindow;
        
        public PlaybackTimeTask(int IndexOfCurrentJPanel){
            this.IndexOfCurrentJPanel = IndexOfCurrentJPanel;
            this.channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
            this.PlayBackHandle = channelPlayBack.getPlayBackHandle();
            this.jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
        }
        //定时器函数 
        @Override
        public void run(){
            try{
                
                //设置在当前回放日期24小时内的录像是否缺失信息（JSlider显示）
                HCNetSDK.NET_DVR_TIME StruPlayBackTime = new HCNetSDK.NET_DVR_TIME();//存储当前回放时间
                if (CommonParas.hCNetSDK.NET_DVR_GetPlayBackOsdTime(channelPlayBack.getPlayBackHandle(), StruPlayBackTime)){
                    
                    //NewDate = new MyDate(StruPlayBackTime.dwYear,  StruPlayBackTime.dwMonth,  StruPlayBackTime.dwDay);
                    int SliderValue = getValueFromTime(StruPlayBackTime.dwHour, StruPlayBackTime.dwMinute, StruPlayBackTime.dwSecond);
                    if (getIndexOfCurrentJPanel() == IndexOfCurrentJPanel) jSliderTimeLine.setValue(SliderValue);
                    
                    //现在不显示该时间了，因为录像上都有。
                    //jPanelPlayWindow.setPlayBackCurrentTime(StruPlayBackTime.toStringTime());
                }
                
            }catch (Exception e){
                TxtLogger.append(sFileName, "PlaybackTimeTask.run()","系统在回放定时器响应函数运行过程中，出现错误"
                                 + "\r\n                       Exception:" + e.toString());
            }
        }
    }
    
//    /**
//        * 内部类:    PlaybackFileTask
//        * 函数描述:  按文件回放定时器响应函数
//    */
//    class PlaybackFileTask extends java.util.TimerTask
//    {
//        NativeLong PlayBackHandle;
//        int IndexOfCurrentJPanel;
//        IntByReference FileTime = new IntByReference(0);//文件总时间
//        
//        //回放对象
//        ChannelPlayBack channelPlayBack;
//        JPanelPlayWindow jPanelPlayWindow;
//        public PlaybackFileTask(int IndexOfCurrentJPanel){
//            this.IndexOfCurrentJPanel = IndexOfCurrentJPanel;
//            channelPlayBack = listChannelPlayBack.get(IndexOfCurrentJPanel);
//            this.PlayBackHandle = channelPlayBack.getPlayBackHandle();
//            jPanelPlayWindow = listJPanelPlayWindow.get(IndexOfCurrentJPanel);
//        }
//        //定时器函数 
//        @Override
//        public void run()
//        {
//            try{
//            	IntByReference nCurrentTime = new IntByReference(0);
////                IntByReference nCurrentFrame = new IntByReference(0);
//                IntByReference nPos = new IntByReference(0);
//                int nHour, nMinute, nSecond;
//                
//                
////                IntByReference TotalFrames = new IntByReference(0);//文件总帧数
//
//                int TotalSecond = channelPlayBack.getTotalSecond();//总秒数
//                int TotalMinute = channelPlayBack.getTotalMinute();//总分钟数
//                int TotalHour = channelPlayBack.getTotalHour();//总小时
//                
////                if (!m_bGetMaxTime)
//            	if (!channelPlayBack.isGetMaxTime())
//		{
//			CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_GETTOTALTIME, 0, FileTime);
//			if (FileTime.getValue() == 0)return;
//
//                        channelPlayBack.setFileTime(FileTime);
//                        channelPlayBack.setTotalHour(FileTime.getValue()/3600);
//                        channelPlayBack.setTotalMinute((FileTime.getValue()%3600)/60);
//                        channelPlayBack.setTotalSecond(FileTime.getValue()%60);
//                        channelPlayBack.setGetMaxTime(true);
//                        
////			if (CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_GETTOTALFRAMES, 0, TotalFrames)){
////				if (TotalFrames.getValue() == 0)return;
////                                channelPlayBack.setTotalFrames(TotalFrames);
////			}
//
//		}
//
//		CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYGETTIME, 0, nCurrentTime);
//		if (nCurrentTime.getValue() >= FileTime.getValue())
//		{
//			nCurrentTime.setValue(FileTime.getValue());
//		}
//		nHour = (nCurrentTime.getValue()/3600)%24;
//		nMinute =(nCurrentTime.getValue()%3600)/60;
//		nSecond = nCurrentTime.getValue()%60;
////		CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYGETFRAME, 0, nCurrentFrame);
////		if (nCurrentFrame.getValue() > TotalFrames.getValue())
////		{
////			nCurrentFrame.setValue(TotalFrames.getValue());
////		}
//
////                 String sPlayTime;//播放时间
////                 sPlayTime = String.format("%02d:%02d:%02d/%02d:%02d:%02d ",nHour,nMinute,nSecond,TotalHour,TotalMinute,TotalSecond);
//                 
//
//		CommonParas.hCNetSDK.NET_DVR_PlayBackControl(PlayBackHandle, HCNetSDK.NET_DVR_PLAYGETPOS, 0, nPos);
//		if (nPos.getValue() > 100){//200 indicates network exception
//		
//			stopOnePanel(IndexOfCurrentJPanel);
//                        JOptionPane.showMessageDialog(null, sPlackbackExpectionMsg);//"由于网络原因或DVR忙,回放异常终止!"
//		}else{
//			//jPanelPlayWindow.jSliderPlayback.setValue(nPos.getValue());
//			if (nPos.getValue() == 100){
//				stopOnePanel(IndexOfCurrentJPanel);
//			}
//		}
//            }catch (Exception e){
//                TxtLogger.append(sFileName, "PlaybackFileTask.run()","系统在回放定时器响应函数运行过程中，出现错误"
//                                 + "\r\n                       Exception:" + e.toString());
//            }
//        }
//    }

    
    private class MyDate{
        private int myYear,myMonth,myDay;
        public MyDate(int Year,int Month,int Day){
            this.myYear  = Year;
            this.myMonth = Month;
            this.myDay   = Day;
        }
        public MyDate(String sDate){
            try{
                this.myYear  = Integer.parseInt(sDate.substring(0,4));
                this.myMonth = Integer.parseInt(sDate.substring(5,7));
                this.myDay   = Integer.parseInt(sDate.substring(8,10));
            }catch (Exception e){
                TxtLogger.append(sFileName, "MyDate","MyDate构造函数中，出现错误"
                                 + "\r\n                       Exception:" + e.toString());
                this.myYear  = 2016;
                this.myMonth = 1;
                this.myDay   = 1;
            }
        }
        public String getDateString(){
            String S1  = Integer.toString(myYear);
            String S2  = myMonth > 9 ? Integer.toString(myMonth):("0"+Integer.toString(myMonth));
            String S3  = myDay > 9 ? Integer.toString(myDay):("0"+Integer.toString(myDay));
            return S1+"-"+S2+"-"+S3;
        }
    }
    
    

}
