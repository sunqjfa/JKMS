/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import jyms.data.InitDB;
import jyms.data.TxtLogger;

import jyms.ui.JymsLookAndFeel;
import jyms.ui.MenuBarUI;
import jyms.ui.ToolBarUI;


/**
 *
 * @author John
 */
public class Jkms extends javax.swing.JFrame {
    
    private final String sFileName = this.getClass().getName() + ".java";
    private JInFrameAlarmMSG InFrameAlarmMSG;
    private JInFrameNavigation InFrameNavigation;

    private final String sSysPathURL = getClass().getResource("").toString();
    private String sTitleOfAlarmMSGToolBar = "报警窗口";//报警模块的名字
    private String sTitleOfNavigationToolBar = "导航窗口";//导航模块的名字
    //private String sTitleOfFrameAlarmMSG;
    private JInternalFrame lastInJFrame;//设置一个上一次刚打开的窗口，关闭一个窗口的时候，最近打开的窗口显示出来。
    private ArrayList<JButton> listJButton = new ArrayList<>();//JToolBar上的JButton集合
    //private JPanel LogoPanel = new JPanel();

    private ArrayList<JFrame> listJFrame = CommonParas.g_ListJFrame;//JFrame集合。因为JInternalFrame集合可以读出来
           
    private String[] sInFrameTitle = {"报警设置", "设备管理", "用户管理", "录像计划", "日志管理"};
    private String[] sMessageBox   = {"确定要退出？", "退出"};
    private String sInitializationFailed = "SDK初始化失败!";

    /**
     * Creates new form testjyms
     */
    public Jkms() {
         
        initComponents();
        initialMDIWindowParas();
        
        CommonParas.SysPathURL = sSysPathURL;
        CommonParas.SysPath = System.getProperty("user.dir")+File.separator;

//        JFrameSplash2.showSplashWindow();

        modifyLocales();
System.out.println(getClass().getResource("/jyms/image/lock.png"));

        String ss = getClass().getResource("/").getPath().substring(1);
        System.out.println(ss);
    }
    
    /**
        * 函数:     initialMDIWindowParas
        * 函数描述: 初始化窗口参数
    */
    private void initialMDIWindowParas(){
        CommonParas.g_RootPane = rootPane;

        jMenuBarSys.setUI(new MenuBarUI());
        jToolBarJkms.setUI(new ToolBarUI());
        //jToolBarJkms.setUI(new BasicToolBarUI());

        Color BackGroudC = new java.awt.Color(64, 64, 64);
        Color ForeGroudC = new java.awt.Color(255, 255, 255);

        
        JPanel TitleBarPanel = new JPanel();
        TitleBarPanel.setLayout(new BorderLayout());
        TitleBarPanel.setBackground(BackGroudC);
        
        //设置窗口右上角的按钮：关闭、最小化、加锁、操作员信息
        JPanel ButtonPanel = new JPanel();
        ButtonPanel.setBackground(new java.awt.Color(64, 64, 64));

        CommonParas.setJButtonUnDecorated(HeadB);
        CommonParas.setJButtonUnDecorated(LockB);
        CommonParas.setJButtonUnDecorated(CloseB);
        CommonParas.setJButtonUnDecorated(MinB);
        

        ButtonPanel.add(HeadB);
        ButtonPanel.add(OperatorName);
        ButtonPanel.add(LockB);
        ButtonPanel.add(MinB);
        ButtonPanel.add(CloseB);

        TitleBarPanel.add(ButtonPanel, BorderLayout.EAST);
        
        jMenuBarSys.add(TitleBarPanel);
    }
    
    /**
        * 函数:     initialSysParas
        * 函数描述: 初始化系统参数
    */
    public void initialSysParas(){
        try {
            CommonParas.hCNetSDK.NET_DVR_Init();
            //设置连接时间
            CommonParas.hCNetSDK.NET_DVR_SetConnectTime(2000, 1);
            CommonParas.hCNetSDK.NET_DVR_SetReconnect(10000, true);
        }catch (UnsatisfiedLinkError u){
            JOptionPane.showMessageDialog(this, sInitializationFailed);//"SDK初始化失败!");
            //System.out.println("error"+CommonParas.hCNetSDK.NET_DVR_GetLastError());
            TxtLogger.append(sFileName, "jButtonLoginActionPerformed()","用户初始化SDK时捕捉到UnsatisfiedLinkError:   " + u.toString());
        }
        //获取设备参数表中的 设备序列号,IP地址,端口号,用户名,密码,别名,设备类型代码，设备类型（代码表中的代码名称）等参数；再加上userID
        CommonParas.initialDeviceDetailParaList("");//初始化glistDeviceDetailPara
        
        //报警布防
        JFrameSplash.setProcessDesc(getMenuItemText(jMenuItemSetupAlarmChan) + " ...");//设备报警布防
        CommonParas.setupAlarmChan();
        JFrameSplash.setProcessDesc("");//

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        HeadB = new javax.swing.JButton();
        CloseB = new javax.swing.JButton();
        LockB = new javax.swing.JButton();
        MinB = new javax.swing.JButton();
        OperatorName = new javax.swing.JLabel();
        jDialogOperator = new javax.swing.JDialog(this, false);
        jPanel2 = new javax.swing.JPanel();
        jButtonModifyPassword = new javax.swing.JButton();
        jButtonLogout = new javax.swing.JButton();
        jButtonSwitchUser = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanelTop = new javax.swing.JPanel();
        jToolBarJkms = new javax.swing.JToolBar();
        jPanelDown = new javax.swing.JPanel();
        desktopPane = new javax.swing.JDesktopPane();
        jMenuBarSys = new javax.swing.JMenuBar();
        jMenuSystem = new javax.swing.JMenu();
        jMenuItemLock = new javax.swing.JMenuItem();
        jMenuItemModifyPassword = new javax.swing.JMenuItem();
        jMenuItemSwitchUser = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemAbout = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuDev = new javax.swing.JMenu();
        jMenuItemDevManage = new javax.swing.JMenuItem();
        jMenuItemDVRGroup = new javax.swing.JMenuItem();
        jMenuItemDVRMaint = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItemCheckTime = new javax.swing.JMenuItem();
        jMenuItemCtrlWiper = new javax.swing.JMenuItem();
        jMenuView = new javax.swing.JMenu();
        jMenuItemNavigation = new javax.swing.JMenuItem();
        jMenuItemAlarmMSG = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemPreview = new javax.swing.JMenuItem();
        jMenuItemPlayBack = new javax.swing.JMenuItem();
        jMenuAlarm = new javax.swing.JMenu();
        jMenuItemSetupAlarmChan = new javax.swing.JMenuItem();
        jMenuItemSetupAlarmOut = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSetAlarmParas = new javax.swing.JMenuItem();
        jMenuTool = new javax.swing.JMenu();
        jMenuItemUserManage = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemRecordSchedule = new javax.swing.JMenuItem();
        jMenuItemRecordQuicklySetup = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSysParasConfig = new javax.swing.JMenuItem();
        jMenuItemLogMaint = new javax.swing.JMenuItem();
        jSeparatorLanguage = new javax.swing.JPopupMenu.Separator();
        jMenuLanguage = new javax.swing.JMenu();
        jCheckBoxMenuItemChinese = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemEnglish = new javax.swing.JCheckBoxMenuItem();
        jMenuLogo = new javax.swing.JMenu();

        HeadB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/head.png"))); // NOI18N
        HeadB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HeadBActionPerformed(evt);
            }
        });

        CloseB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/close.png"))); // NOI18N
        CloseB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseBActionPerformed(evt);
            }
        });

        LockB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/lock.png"))); // NOI18N
        LockB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LockBActionPerformed(evt);
            }
        });

        MinB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/min.png"))); // NOI18N
        MinB.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        MinB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MinB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MinBActionPerformed(evt);
            }
        });

        OperatorName.setBackground(new java.awt.Color(64, 64, 64));
        OperatorName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        OperatorName.setForeground(new java.awt.Color(255, 255, 255));
        OperatorName.setText("admin");
        OperatorName.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        OperatorName.setMaximumSize(new java.awt.Dimension(160, 40));
        OperatorName.setMinimumSize(new java.awt.Dimension(48, 40));
        OperatorName.setPreferredSize(new java.awt.Dimension(60, 40));

        jDialogOperator.setMinimumSize(new java.awt.Dimension(176, 155));
        jDialogOperator.setUndecorated(true);
        jDialogOperator.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                jDialogOperatorWindowLostFocus(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jButtonModifyPassword.setBackground(new java.awt.Color(0, 0, 0));
        jButtonModifyPassword.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonModifyPassword.setForeground(new java.awt.Color(255, 255, 255));
        jButtonModifyPassword.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/modifypassword24.png"))); // NOI18N
        jButtonModifyPassword.setText(" 密码修改");
        jButtonModifyPassword.setBorder(null);
        jButtonModifyPassword.setBorderPainted(false);
        jButtonModifyPassword.setContentAreaFilled(false);
        jButtonModifyPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonModifyPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModifyPasswordActionPerformed(evt);
            }
        });

        jButtonLogout.setBackground(new java.awt.Color(0, 0, 0));
        jButtonLogout.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonLogout.setForeground(new java.awt.Color(255, 255, 255));
        jButtonLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/logout24.png"))); // NOI18N
        jButtonLogout.setText(" 注销");
        jButtonLogout.setToolTipText("");
        jButtonLogout.setBorder(null);
        jButtonLogout.setBorderPainted(false);
        jButtonLogout.setContentAreaFilled(false);
        jButtonLogout.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLogoutActionPerformed(evt);
            }
        });

        jButtonSwitchUser.setBackground(new java.awt.Color(0, 0, 0));
        jButtonSwitchUser.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSwitchUser.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSwitchUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/switchuser24.png"))); // NOI18N
        jButtonSwitchUser.setText(" 切换用户");
        jButtonSwitchUser.setBorder(null);
        jButtonSwitchUser.setBorderPainted(false);
        jButtonSwitchUser.setContentAreaFilled(false);
        jButtonSwitchUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonSwitchUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSwitchUserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonModifyPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSwitchUser, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonLogout)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonModifyPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSwitchUser, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonLogout, jButtonModifyPassword, jButtonSwitchUser});

        jDialogOperator.getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanelTop.setBackground(new java.awt.Color(51, 51, 51));
        jPanelTop.setMaximumSize(new java.awt.Dimension(32767, 14));
        jPanelTop.setMinimumSize(new java.awt.Dimension(10, 14));
        jPanelTop.setName(""); // NOI18N
        jPanelTop.setPreferredSize(new java.awt.Dimension(10, 14));
        jPanel1.add(jPanelTop, java.awt.BorderLayout.PAGE_START);

        jToolBarJkms.setBackground(new java.awt.Color(151, 151, 151));
        jToolBarJkms.setBorder(null);
        jToolBarJkms.setFloatable(false);
        jToolBarJkms.setForeground(new java.awt.Color(255, 255, 255));
        jToolBarJkms.setRollover(true);
        jToolBarJkms.setEnabled(false);
        jToolBarJkms.setMaximumSize(new java.awt.Dimension(2, 42));
        jToolBarJkms.setMinimumSize(new java.awt.Dimension(2, 42));
        jToolBarJkms.setOpaque(false);
        jToolBarJkms.setPreferredSize(new java.awt.Dimension(2, 42));
        jPanel1.add(jToolBarJkms, java.awt.BorderLayout.CENTER);

        jPanelDown.setBackground(new java.awt.Color(51, 51, 51));
        jPanelDown.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel1.add(jPanelDown, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        desktopPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                desktopPaneComponentResized(evt);
            }
        });
        getContentPane().add(desktopPane, java.awt.BorderLayout.CENTER);

        jMenuBarSys.setBackground(new java.awt.Color(64, 64, 64));
        jMenuBarSys.setForeground(new java.awt.Color(255, 255, 255));
        jMenuBarSys.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuBarSys.setPreferredSize(new java.awt.Dimension(306, 48));

        jMenuSystem.setBackground(new java.awt.Color(100, 100, 100));
        jMenuSystem.setForeground(new java.awt.Color(255, 255, 255));
        jMenuSystem.setText("系统");
        jMenuSystem.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 16)); // NOI18N
        jMenuSystem.setMargin(new java.awt.Insets(0, 19, 0, 19));

        jMenuItemLock.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jMenuItemLock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/lock24.png"))); // NOI18N
        jMenuItemLock.setText("   加   锁 ");
        jMenuItemLock.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLockActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemLock);

        jMenuItemModifyPassword.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemModifyPassword.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/modifypassword24.png"))); // NOI18N
        jMenuItemModifyPassword.setText("   密码修改");
        jMenuItemModifyPassword.setActionCommand("  密码修改");
        jMenuItemModifyPassword.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemModifyPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemModifyPasswordActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemModifyPassword);

        jMenuItemSwitchUser.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jMenuItemSwitchUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/switchuser24.png"))); // NOI18N
        jMenuItemSwitchUser.setText("   切换用户");
        jMenuItemSwitchUser.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemSwitchUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSwitchUserActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemSwitchUser);
        jMenuSystem.add(jSeparator5);

        jMenuItemAbout.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jMenuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/about24.png"))); // NOI18N
        jMenuItemAbout.setText("   关于系统");
        jMenuItemAbout.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemAbout);
        jMenuSystem.add(jSeparator7);

        jMenuItemExit.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/exit24.png"))); // NOI18N
        jMenuItemExit.setText("   退   出");
        jMenuItemExit.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitjButtonExitActionPerformed(evt);
            }
        });
        jMenuSystem.add(jMenuItemExit);

        jMenuBarSys.add(jMenuSystem);

        jMenuDev.setBackground(new java.awt.Color(100, 100, 100));
        jMenuDev.setForeground(new java.awt.Color(255, 255, 255));
        jMenuDev.setText("设备");
        jMenuDev.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 16)); // NOI18N
        jMenuDev.setMargin(new java.awt.Insets(0, 19, 0, 19));

        jMenuItemDevManage.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemDevManage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/dvrmaint24.png"))); // NOI18N
        jMenuItemDevManage.setText("   设备管理");
        jMenuItemDevManage.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemDevManage.setName(""); // NOI18N
        jMenuItemDevManage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDevManageActionPerformed(evt);
            }
        });
        jMenuDev.add(jMenuItemDevManage);

        jMenuItemDVRGroup.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemDVRGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/devgroup24.png"))); // NOI18N
        jMenuItemDVRGroup.setText("   设备分组管理");
        jMenuItemDVRGroup.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemDVRGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDVRGroupActionPerformed(evt);
            }
        });
        jMenuDev.add(jMenuItemDVRGroup);

        jMenuItemDVRMaint.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemDVRMaint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/dvrmanage24.png"))); // NOI18N
        jMenuItemDVRMaint.setText("   设备维护");
        jMenuItemDVRMaint.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemDVRMaint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDVRMaintActionPerformed(evt);
            }
        });
        jMenuDev.add(jMenuItemDVRMaint);
        jMenuDev.add(jSeparator6);

        jMenuItemCheckTime.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemCheckTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/checktime24.png"))); // NOI18N
        jMenuItemCheckTime.setText("   设备校时");
        jMenuItemCheckTime.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemCheckTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCheckTimeActionPerformed(evt);
            }
        });
        jMenuDev.add(jMenuItemCheckTime);

        jMenuItemCtrlWiper.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemCtrlWiper.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ctrlwiper24.png"))); // NOI18N
        jMenuItemCtrlWiper.setText("   控制雨刷");
        jMenuItemCtrlWiper.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemCtrlWiper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCtrlWiperActionPerformed(evt);
            }
        });
        jMenuDev.add(jMenuItemCtrlWiper);

        jMenuBarSys.add(jMenuDev);

        jMenuView.setBackground(new java.awt.Color(100, 100, 100));
        jMenuView.setForeground(new java.awt.Color(255, 255, 255));
        jMenuView.setText("视图");
        jMenuView.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 16)); // NOI18N
        jMenuView.setMargin(new java.awt.Insets(0, 19, 0, 19));

        jMenuItemNavigation.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemNavigation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/navigation24.png"))); // NOI18N
        jMenuItemNavigation.setText("   导航窗口");
        jMenuItemNavigation.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemNavigation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNavigationActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuItemNavigation);

        jMenuItemAlarmMSG.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemAlarmMSG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/alarm24.png"))); // NOI18N
        jMenuItemAlarmMSG.setText("   报警窗口");
        jMenuItemAlarmMSG.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemAlarmMSG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAlarmMSGActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuItemAlarmMSG);
        jMenuView.add(jSeparator1);

        jMenuItemPreview.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/preview24.png"))); // NOI18N
        jMenuItemPreview.setText("   设备预览");
        jMenuItemPreview.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPreviewActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuItemPreview);

        jMenuItemPlayBack.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemPlayBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/playback24.png"))); // NOI18N
        jMenuItemPlayBack.setText("   录像回放");
        jMenuItemPlayBack.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemPlayBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPlayBackActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuItemPlayBack);

        jMenuBarSys.add(jMenuView);

        jMenuAlarm.setBackground(new java.awt.Color(100, 100, 100));
        jMenuAlarm.setForeground(new java.awt.Color(255, 255, 255));
        jMenuAlarm.setText("报警");
        jMenuAlarm.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 16)); // NOI18N
        jMenuAlarm.setMargin(new java.awt.Insets(0, 19, 0, 19));

        jMenuItemSetupAlarmChan.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemSetupAlarmChan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/alarmsetup24.png"))); // NOI18N
        jMenuItemSetupAlarmChan.setText("   设备报警布防");
        jMenuItemSetupAlarmChan.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemSetupAlarmChan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSetupAlarmChanActionPerformed(evt);
            }
        });
        jMenuAlarm.add(jMenuItemSetupAlarmChan);

        jMenuItemSetupAlarmOut.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemSetupAlarmOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/alarmoutctrl24.png"))); // NOI18N
        jMenuItemSetupAlarmOut.setText("   报警输出控制");
        jMenuItemSetupAlarmOut.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemSetupAlarmOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSetupAlarmOutActionPerformed(evt);
            }
        });
        jMenuAlarm.add(jMenuItemSetupAlarmOut);
        jMenuAlarm.add(jSeparator4);

        jMenuItemSetAlarmParas.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemSetAlarmParas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/alarmparassetup24.png"))); // NOI18N
        jMenuItemSetAlarmParas.setText("   报警参数设置");
        jMenuItemSetAlarmParas.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemSetAlarmParas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSetAlarmParasActionPerformed(evt);
            }
        });
        jMenuAlarm.add(jMenuItemSetAlarmParas);

        jMenuBarSys.add(jMenuAlarm);

        jMenuTool.setBackground(new java.awt.Color(100, 100, 100));
        jMenuTool.setForeground(new java.awt.Color(255, 255, 255));
        jMenuTool.setText("工具");
        jMenuTool.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 16)); // NOI18N
        jMenuTool.setMargin(new java.awt.Insets(0, 19, 0, 19));

        jMenuItemUserManage.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemUserManage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/usermang24.png"))); // NOI18N
        jMenuItemUserManage.setText("   用户管理");
        jMenuItemUserManage.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemUserManage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUserManageActionPerformed(evt);
            }
        });
        jMenuTool.add(jMenuItemUserManage);
        jMenuTool.add(jSeparator2);

        jMenuItemRecordSchedule.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemRecordSchedule.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/recordschedule24.png"))); // NOI18N
        jMenuItemRecordSchedule.setText("   录像存储计划");
        jMenuItemRecordSchedule.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemRecordSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRecordScheduleActionPerformed(evt);
            }
        });
        jMenuTool.add(jMenuItemRecordSchedule);

        jMenuItemRecordQuicklySetup.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemRecordQuicklySetup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/recordscheduleShortcuts24.png"))); // NOI18N
        jMenuItemRecordQuicklySetup.setText("   录像快速设置");
        jMenuItemRecordQuicklySetup.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemRecordQuicklySetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRecordQuicklySetupActionPerformed(evt);
            }
        });
        jMenuTool.add(jMenuItemRecordQuicklySetup);
        jMenuTool.add(jSeparator3);

        jMenuItemSysParasConfig.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemSysParasConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/sysconfig24.png"))); // NOI18N
        jMenuItemSysParasConfig.setText("   系统配置");
        jMenuItemSysParasConfig.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemSysParasConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSysParasConfigActionPerformed(evt);
            }
        });
        jMenuTool.add(jMenuItemSysParasConfig);

        jMenuItemLogMaint.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuItemLogMaint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/logmanage24.png"))); // NOI18N
        jMenuItemLogMaint.setText("   日志管理");
        jMenuItemLogMaint.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jMenuItemLogMaint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLogMaintActionPerformed(evt);
            }
        });
        jMenuTool.add(jMenuItemLogMaint);
        jMenuTool.add(jSeparatorLanguage);

        jMenuLanguage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/menu_language24.png"))); // NOI18N
        jMenuLanguage.setText("   语    言");
        jMenuLanguage.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jMenuLanguage.setMargin(new java.awt.Insets(5, 0, 5, 0));

        jCheckBoxMenuItemChinese.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jCheckBoxMenuItemChinese.setSelected(true);
        jCheckBoxMenuItemChinese.setText("中文");
        jCheckBoxMenuItemChinese.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jCheckBoxMenuItemChinese.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemChineseActionPerformed(evt);
            }
        });
        jMenuLanguage.add(jCheckBoxMenuItemChinese);

        jCheckBoxMenuItemEnglish.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jCheckBoxMenuItemEnglish.setSelected(true);
        jCheckBoxMenuItemEnglish.setText("English(Australia)");
        jCheckBoxMenuItemEnglish.setMargin(new java.awt.Insets(5, 0, 5, 0));
        jCheckBoxMenuItemEnglish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemEnglishActionPerformed(evt);
            }
        });
        jMenuLanguage.add(jCheckBoxMenuItemEnglish);

        jMenuTool.add(jMenuLanguage);

        jMenuBarSys.add(jMenuTool);

        jMenuLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/logo.png"))); // NOI18N
        jMenuLogo.setBorderPainted(true);
        jMenuLogo.setFocusable(false);
        jMenuLogo.setRequestFocusEnabled(false);
        jMenuLogo.setRolloverEnabled(false);
        jMenuLogo.setVerifyInputWhenFocusTarget(false);
        jMenuLogo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jMenuLogoMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuLogoMousePressed(evt);
            }
        });
        jMenuBarSys.add(jMenuLogo);

        setJMenuBar(jMenuBarSys);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void desktopPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_desktopPaneComponentResized
        // TODO add your handling code here:
        JInternalFrame[] jj = desktopPane.getAllFrames();
        for (int i=0;i<jj.length;i++){
            jj[i].setSize(desktopPane.getSize());
        }
    }//GEN-LAST:event_desktopPaneComponentResized

    private void jMenuItemExitjButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitjButtonExitActionPerformed
        // TODO add your handling code here:
        //cleanup SDK
        exitSysQuery();
    }//GEN-LAST:event_jMenuItemExitjButtonExitActionPerformed

    private void jMenuItemPlayBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPlayBackActionPerformed
        // TODO add your handling code here:
//        if (!CommonParas.softRegister(sFileName)){
//            exitSys();
//        }
        for (int i=0;i<listJFrame.size();i++){
            if (listJFrame.get(i) instanceof  JFramePlayBack){
                listJFrame.get(i).setVisible(true);
                listJFrame.get(i).setExtendedState(JFrame.MAXIMIZED_BOTH); 
                return;
            }
        }
        //写日志
        CommonParas.SystemWriteLog(CommonParas.LogType.LOG_OPER_CODE, getMenuItemText(jMenuItemPlayBack), sFileName);
        
        JFramePlayBack FramePlayBack = new JFramePlayBack();
        FramePlayBack.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        FramePlayBack.setVisible(true);
        listJFrame.add(FramePlayBack);
        
//        if (ifInFrameIsOpen("录像回放")) return;
//        JInFramePlayBack jInternalJFrame22= new JInFramePlayBack();
//        jInternalJFrame22.setVisible(true);
//        new ToolBarAction("录像回放",  new ImageIcon(getClass().getResource("/jyms/image/monitor.png")),jInternalJFrame22);
        //CommonParas.SystemWriteLog(new ClientLogBean(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()),CommonParas.UserState.UserName,CommonParas.LogType.LOG_OPER_CODE,"报警参数设置"), sFileName);
        
    }//GEN-LAST:event_jMenuItemPlayBackActionPerformed

    private void jMenuItemSetAlarmParasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSetAlarmParasActionPerformed
        // TODO add your handling code here:
        
        try{
            if (ifInFrameIsOpen(sInFrameTitle[0])) return;
            JInFrameAlarmConfig jInternalJFrame22= new JInFrameAlarmConfig();
            //打开报警设置窗口，同时填写日志pe
            openOneInFrame( jInternalJFrame22, sInFrameTitle[0], sSysPathURL + "image/alarm16.png", getMenuItemText(jMenuItemSetAlarmParas));
        }catch (Exception e){
            TxtLogger.append(sFileName, "jMenuItemSetAlarmParasActionPerformed()","系统在打开报警设置窗口事件中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        
    }//GEN-LAST:event_jMenuItemSetAlarmParasActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        RefreshWindowParasWorker refreshWorker = new RefreshWindowParasWorker();
        refreshWorker.execute();
//        try{
//            CommonParas.jkms.initialSysParas();
//            
//            OperatorName.setText(CommonParas.UserState.UserName);
////            OperatorName.setSize(new java.awt.Dimension(48, 40));
////            OperatorName.setMaximumSize(new java.awt.Dimension(48, 40));
////        OperatorName.setMinimumSize(new java.awt.Dimension(48, 40));
//            setWidthOfOperatorName();//根据用户名长度设置其对应的JLabel一个合适的长度
//CommonParas.setProcessDesc("打开报警窗口。。。");
//            //打开报警窗口
//            openOneInFrame( InFrameAlarmMSG, sTitleOfAlarmMSGToolBar, sSysPathURL + "image/alarm16.png", null );
//            //打开导航窗口
//            openOneInFrame( InFrameNavigation, sTitleOfNavigationToolBar, sSysPathURL + "image/navigation16.png", null );
//
//            int xx = jMenuBarSys.getWidth()/2 - jMenuSystem.getWidth()*5 - jMenuLogo.getWidth()/2;//38为图标的一半宽度
//            jMenuLogo.setMargin(new java.awt.Insets(0, xx, 0, 0));
//
//            
//            //设置系统菜单状态
//            refreshMenuStateByAuthority();
//            //启动备份数据库定时器
//            startBackupDatabaseTimer();
//    //        jMenuSystem.setBackground(Color.RED);//这里设置MenuItem的背景色为黄色
//    //        jMenuItemLock.setUI(new MyMenuItemUI(Color.BLUE,Color.RED));
//    //        CommonParas.SystemWriteLog(new ClientLogBean(new Date(),CommonParas.UserState.UserName,CommonParas.LogType.LOG_OPER,"设备管理"), sFileName);
//        }catch (Exception e){
//            System.out.println(e.toString());
//            TxtLogger.append(sFileName, "formWindowOpened()","系统在窗口打开事件中，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
    }//GEN-LAST:event_formWindowOpened

    private void jMenuItemSetupAlarmOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSetupAlarmOutActionPerformed
        // TODO add your handling code here:
        //日志：报警输出控制
        CommonParas.SystemWriteLog(CommonParas.LogType.LOG_OPER_CODE, getMenuItemText(jMenuItemSetupAlarmOut), sFileName);
        
        JDialogAlarmOutCtrl DialogAlarmOutCtrl = new JDialogAlarmOutCtrl(this,true);
        CommonParas.centerWindow(DialogAlarmOutCtrl);
        DialogAlarmOutCtrl.setVisible(true);
        
    }//GEN-LAST:event_jMenuItemSetupAlarmOutActionPerformed

    private void jMenuLogoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuLogoMousePressed
        // TODO add your handling code here:
        jMenuLogo.setSelected(false);
    }//GEN-LAST:event_jMenuLogoMousePressed

    private void jMenuLogoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuLogoMouseEntered
        // TODO add your handling code here:
        jMenuLogo.setSelected(false);
//        jMenuLogo.setForeground(new java.awt.Color(64, 64, 64));
//        //jMenuLogo.setBackground(new java.awt.Color(64, 64, 64));
//        jMenuLogo.setOpaque(false);
    }//GEN-LAST:event_jMenuLogoMouseEntered

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        CommonParas.g_RootPane = rootPane;
    }//GEN-LAST:event_formWindowActivated

    private void jMenuItemSysParasConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSysParasConfigActionPerformed
        // TODO add your handling code here:
        CommonParas.SystemWriteLog(CommonParas.LogType.LOG_OPER_CODE, getMenuItemText(jMenuItemSysParasConfig), sFileName);
        
        JDialogSysParasSet DialogSysParasSet = new JDialogSysParasSet(this,true);
        CommonParas.centerWindow(DialogSysParasSet);
        DialogSysParasSet.setVisible(true);
    }//GEN-LAST:event_jMenuItemSysParasConfigActionPerformed

    private void jMenuItemDevManageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDevManageActionPerformed
        // TODO add your handling code here:
        if (ifInFrameIsOpen(sInFrameTitle[1])) return;
        
        JInFrameDeviceManage deviceManagement = new JInFrameDeviceManage();
        //打开设备管理窗口，同时填写日志
        openOneInFrame(deviceManagement, sInFrameTitle[1], sSysPathURL + "image/dvrmanage16.png", getMenuItemText(jMenuItemDevManage));

    }//GEN-LAST:event_jMenuItemDevManageActionPerformed

    private void jMenuItemCheckTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCheckTimeActionPerformed
        // TODO add your handling code here:
        JDialogCheckTime DialogCheckTime = new JDialogCheckTime(this,true);
        CommonParas.centerWindow(DialogCheckTime);
        DialogCheckTime.setVisible(true);
    }//GEN-LAST:event_jMenuItemCheckTimeActionPerformed

    private void jMenuItemDVRMaintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDVRMaintActionPerformed
        // TODO add your handling code here:
        JDialogDVRMaint DialogDVRMaint = new JDialogDVRMaint(this,true);
        CommonParas.centerWindow(DialogDVRMaint);
        DialogDVRMaint.setVisible(true);
    }//GEN-LAST:event_jMenuItemDVRMaintActionPerformed

    private void jMenuItemCtrlWiperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCtrlWiperActionPerformed
        // TODO add your handling code here:
        JDialogCtrlWiper DialogCtrlWiper = new JDialogCtrlWiper(this,true);
        CommonParas.centerWindow(DialogCtrlWiper);
        DialogCtrlWiper.setVisible(true);
    }//GEN-LAST:event_jMenuItemCtrlWiperActionPerformed

    private void jMenuItemPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPreviewActionPerformed
        // TODO add your handling code here:
        try{

//            if (!CommonParas.softRegister(sFileName)){
//                exitSys();
//            }

            for (int i=0;i<listJFrame.size();i++){
                if (listJFrame.get(i) instanceof  JFrameTotalPreview){
                    listJFrame.get(i).setVisible(true);
                    listJFrame.get(i).setExtendedState(JFrame.MAXIMIZED_BOTH); 
                    return;
                }
            }

            CommonParas.SystemWriteLog(CommonParas.LogType.LOG_OPER_CODE, jMenuItemPreview.getText(), sFileName);

            JFrameTotalPreview frameTotalPreview = new JFrameTotalPreview();
            frameTotalPreview.setExtendedState(JFrame.MAXIMIZED_BOTH); 
            frameTotalPreview.setVisible(true);
            listJFrame.add(frameTotalPreview);
        }catch (Exception e){
            TxtLogger.append(sFileName, "jMenuItemPreviewActionPerformed()","系统在打开视频预览窗口过程中，出现错误"
                            + "\r\n                       Exception:" + e.toString());
        }
        
    }//GEN-LAST:event_jMenuItemPreviewActionPerformed

    private void jMenuItemSetupAlarmChanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSetupAlarmChanActionPerformed
        // TODO add your handling code here:
        JDialogSetupAlarmCtrl DialogSetupAlarmCtrl = new JDialogSetupAlarmCtrl(this,true);
        //日志：设备报警布防
        CommonParas.SystemWriteLog(CommonParas.LogType.LOG_OPER_CODE, getMenuItemText(jMenuItemSetupAlarmChan), sFileName);
        CommonParas.centerWindow(DialogSetupAlarmCtrl);
        DialogSetupAlarmCtrl.setVisible(true);
        
        
    }//GEN-LAST:event_jMenuItemSetupAlarmChanActionPerformed

    private void jMenuItemUserManageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUserManageActionPerformed
        // TODO add your handling code here:
        if (ifInFrameIsOpen(sInFrameTitle[2])) return;
        JInFrameUserManage jInternalJFrame2= new JInFrameUserManage(); 
        //打开用户管理窗口，同时填写日志
        openOneInFrame( jInternalJFrame2, sInFrameTitle[2], sSysPathURL + "image/usermang16.png", getMenuItemText(jMenuItemUserManage));
    }//GEN-LAST:event_jMenuItemUserManageActionPerformed

    private void jMenuItemRecordScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecordScheduleActionPerformed
        // TODO add your handling code here:
        
        if (ifInFrameIsOpen(sInFrameTitle[3])) return;
        JInFrameRecordSchedule jInternalJFrame2= new JInFrameRecordSchedule("录像");//不需要翻译，只是一个参数
        //打开录像计划窗口，同时填写日志
        openOneInFrame( jInternalJFrame2, sInFrameTitle[3], sSysPathURL + "image/recordschedule16.png", getMenuItemText(jMenuItemRecordSchedule) );
    }//GEN-LAST:event_jMenuItemRecordScheduleActionPerformed

    private void jMenuItemLogMaintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLogMaintActionPerformed
        // TODO add your handling code here:
        //if (!CommonParas.ifHaveUserAuthority("录像抓图计划配置", sFileName)) return;

        if (ifInFrameIsOpen(sInFrameTitle[4])) return;

        JInFrameLogMaint InFrameLogMaint= new JInFrameLogMaint();

        //打开日志管理窗口，同时填写日志
        openOneInFrame( InFrameLogMaint, sInFrameTitle[4], sSysPathURL + "image/logmanage16.png", getMenuItemText(jMenuItemLogMaint) );

        
    }//GEN-LAST:event_jMenuItemLogMaintActionPerformed

    private void jMenuItemLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLockActionPerformed
        // TODO add your handling code here:
        JDialogLockScreen lock = new JDialogLockScreen(null, "System lock screen", true);
        lock.setVisible(true);
    }//GEN-LAST:event_jMenuItemLockActionPerformed

    private void jMenuItemModifyPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemModifyPasswordActionPerformed
        // TODO add your handling code here:
        JDialogModifyPassword DialogModifyPassword = new JDialogModifyPassword(this,true);
        DialogModifyPassword.setLocation(new Point(0, 60));
        //CommonParas.setAppropriateLocation(DialogModifyPassword);
        DialogModifyPassword.setVisible(true);
        //CommonParas.SystemWriteLog(CommonParas.LogType.LOG_OPER_CODE, "修改密码", sFileName);
    }//GEN-LAST:event_jMenuItemModifyPasswordActionPerformed

    private void jMenuItemSwitchUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSwitchUserActionPerformed
        // TODO add your handling code here:
        UserLogin SwitchUser = new UserLogin(this,true,1);
        
        SwitchUser.setLocation(new Point(0, 60));
        //CommonParas.setAppropriateLocation(SwitchUser);
        SwitchUser.setVisible(true);
        if (SwitchUser.getReturnState() == 0) return;
        
        //切换用户之后重新处理系统的当前用户标识
        doAfterSwitchUser();
//        OperatorName.setText(CommonParas.UserState.UserName);
//        closeOtherInternalFrames();
//        refreshMenuStateByAuthority();
//        for (JFrame ArrayFrame1 : CommonParas.g_ListJFrame) {
//            if (ArrayFrame1 instanceof  JFrameTotalPreview){
//                ((JFrameTotalPreview) ArrayFrame1).modifyUserInfo();
//                return;
//            }
//        }
    }//GEN-LAST:event_jMenuItemSwitchUserActionPerformed

    private void jMenuItemNavigationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNavigationActionPerformed
        // TODO add your handling code here:
        if (ifInFrameIsOpen(sTitleOfNavigationToolBar)) return;
        //打开导航窗口
        openOneInFrame( InFrameNavigation, sTitleOfNavigationToolBar, sSysPathURL + "image/navigation16.png", null );

    }//GEN-LAST:event_jMenuItemNavigationActionPerformed

    private void jMenuItemAlarmMSGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAlarmMSGActionPerformed
        // TODO add your handling code here:JInFrameAlarmMSG
        if (ifInFrameIsOpen(sTitleOfAlarmMSGToolBar)) return;
        //JInFrameAlarmMSG InFrameAlarmMSG= new JInFrameAlarmMSG();
        //打开报警窗口
        openOneInFrame( InFrameAlarmMSG, sTitleOfAlarmMSGToolBar, sSysPathURL + "image/alarm16.png", null );

    }//GEN-LAST:event_jMenuItemAlarmMSGActionPerformed

    private void LockBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LockBActionPerformed
        // TODO add your handling code here:
        JDialogLockScreen lock = new JDialogLockScreen(null, "System lock screen", true);
        lock.setVisible(true);
    }//GEN-LAST:event_LockBActionPerformed

    private void CloseBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseBActionPerformed
        // TODO add your handling code here:
        exitSysQuery();
    }//GEN-LAST:event_CloseBActionPerformed

    private void MinBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MinBActionPerformed
        // TODO add your handling code here:
        setExtendedState(JFrame.ICONIFIED);
    }//GEN-LAST:event_MinBActionPerformed

    private void jDialogOperatorWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialogOperatorWindowLostFocus
        // TODO add your handling code here:
        jDialogOperator.dispose();
    }//GEN-LAST:event_jDialogOperatorWindowLostFocus

    private void HeadBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HeadBActionPerformed
        // TODO add your handling code here:
        CommonParas.setAppropriateLocation(jDialogOperator, HeadB);
        jDialogOperator.setVisible(true);
    }//GEN-LAST:event_HeadBActionPerformed

    private void jButtonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogoutActionPerformed
        // TODO add your handling code here:
        jMenuItemExit.doClick();
    }//GEN-LAST:event_jButtonLogoutActionPerformed

    private void jButtonModifyPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModifyPasswordActionPerformed
        // TODO add your handling code here:
        JDialogModifyPassword DialogModifyPassword = new JDialogModifyPassword(this,true);
        CommonParas.setAppropriateLocation(DialogModifyPassword, HeadB);
        DialogModifyPassword.setVisible(true);
    }//GEN-LAST:event_jButtonModifyPasswordActionPerformed

    private void jButtonSwitchUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSwitchUserActionPerformed
        // TODO add your handling code here:
        UserLogin SwitchUser = new UserLogin(this,true,1);
        
        CommonParas.setAppropriateLocation(SwitchUser, HeadB);
        SwitchUser.setVisible(true);
        if (SwitchUser.getReturnState() == 0) return;
        
        //切换用户之后重新处理系统的当前用户标识
        doAfterSwitchUser();
    }//GEN-LAST:event_jButtonSwitchUserActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        // TODO add your handling code here:
        JDialogAbout About = new JDialogAbout(this,true);
        CommonParas.centerWindow(About);
        About.setVisible(true);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemDVRGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDVRGroupActionPerformed
        // TODO add your handling code here:
        JDialogDVRResourceImport DialogDVRResourceImport = new JDialogDVRResourceImport(this, true);
        //DialogDVRResourceImport.setLocation(jButtonImport.getX()-DialogDVRResourceImport.getWidth(), jButtonImport.getY());
        CommonParas.centerWindow(DialogDVRResourceImport);
        DialogDVRResourceImport.setVisible(true);
        
        if (DialogDVRResourceImport.isIfModifySuccess()){
            //如果导入成功，则重新建树
            for (JFrame ArrayFrame1 : CommonParas.g_ListJFrame) {
                if (ArrayFrame1 instanceof  JFrameTotalPreview){
                    ((JFrameTotalPreview) ArrayFrame1).createMyGroupTree();
                    return;
                }
            }
        }
    }//GEN-LAST:event_jMenuItemDVRGroupActionPerformed

    private void jMenuItemRecordQuicklySetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecordQuicklySetupActionPerformed
        // TODO add your handling code here:
        JDialogRecordSchedule DialogRecordSchedule = new JDialogRecordSchedule(this,true);
        CommonParas.centerWindow(DialogRecordSchedule);
        DialogRecordSchedule.setVisible(true);

    }//GEN-LAST:event_jMenuItemRecordQuicklySetupActionPerformed

    private void jCheckBoxMenuItemEnglishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemEnglishActionPerformed
        // TODO add your handling code here:
        //设置语言菜单下的子菜单的选中状态
        intialMenuLanguageState();
        //如果当前语言已经是英文了，则不做任何操作。
        if (CommonParas.SysParas.SYSPARAS_COMMON_LANGUAGE.equals(MyLocales.LANGUAGE_ENGLISH)) return;
        if ((JOptionPane.showConfirmDialog(this, "语言切换会在系统重启后生效。是否要继续？",  
                    "提醒",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)) return;
        
        if (MyLocales.modifySysEnglishAndAustralia()){
            //操作时间、日志类型、描述信息、备注、调用的文件名
            CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, "语言切换", "英文(澳大利亚)",  sFileName);
            CommonParas.showMessage("语言切换为英文，系统重启后生效！", sFileName);
        }else{
            //4项：操作时间、描述信息、日志备注、调用的文件名
            CommonParas.SystemWriteErrorLog("", "语言切换失败", "英文(澳大利亚)", sFileName);
            CommonParas.showMessage("语言切换英文，失败！", sFileName);
        }
        //jCheckBoxMenuItemChinese.setSelected(false);
    }//GEN-LAST:event_jCheckBoxMenuItemEnglishActionPerformed

    private void jCheckBoxMenuItemChineseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemChineseActionPerformed
        // TODO add your handling code here:Language switch will take effect after system reboot. Do you want to continue?
        //设置语言菜单下的子菜单的选中状态
        intialMenuLanguageState();
        //如果当前语言已经是中文了，则不做任何操作。
        if (CommonParas.SysParas.SYSPARAS_COMMON_LANGUAGE.equals(MyLocales.LANGUAGE_CHINESE))  return;

        if ((JOptionPane.showConfirmDialog(this, "Language switch will take effect after system reboot. Do you want to continue?",  
                    "Remind",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)) return;
        
        if (MyLocales.modifySysChineseAndChina()){
            //操作时间、日志类型、描述信息、备注、调用的文件名
            CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, "Language switch", "Chinese(China)",  sFileName);
            CommonParas.showMessage("Switch language to Chinese, after system restart to take effect!", sFileName);
        }else{
            //4项：操作时间、描述信息、日志备注、调用的文件名
            CommonParas.SystemWriteErrorLog("", "Language switch failed", "Chinese(China)", sFileName);
            CommonParas.showMessage("Language switch in English, fail!", sFileName);
        }
        //jCheckBoxMenuItemEnglish.setSelected(false);
    }//GEN-LAST:event_jCheckBoxMenuItemChineseActionPerformed
    
    private String getMenuItemText(JMenuItem MenuItem){
        return MenuItem.getText().trim();
    }
    /**
        * 函数:      openOneInFrame
        * 函数描述:  打开JInternalFrame子窗口
        * @param InFrame   JInternalFrame子窗口变量
        * @param Title     子窗口在toolbar上的标题
        * @param SUrl      窗口图标的Url值
        * @param LogDescription 日志描述
    */
    private void openOneInFrame(JInternalFrame InFrame, String Title, String SUrl, String LogDescription ){
        try{
            
            Icon icon = null;
            if (SUrl != null && !SUrl.equals("")) icon = new ImageIcon(new URL(SUrl));//icon = new ImageIcon(ImageIO.read(new URL(SUrl)));
            else System.out.println("SUrl is null or Empty");

            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            
            if (LogDescription!=null && !LogDescription.equals("") )
                CommonParas.SystemWriteLog(CommonParas.LogType.LOG_OPER_CODE, LogDescription, sFileName);
            new ToolBarAction(Title,  icon, InFrame);
            InFrame.setVisible(true);

        }catch (Exception e){
            TxtLogger.append(sFileName, "openOneInFrame()","系统在打开JInternalFrame窗口过程中，出现错误"
                            + "\r\n                       Exception:" + e.toString()
                            + "\r\n                       参数：Title=" + Title);
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    /**
        * 函数:      ifInFrameIsOpen
        * 函数描述:  判断该JInternalFrame是否存在
        * @param Title    JInternalFrame的标题
        * @return boolean  存在返回true；不存在返回false
    */
    private boolean ifInFrameIsOpen(String Title){
        
        JInternalFrame[] ArrayInFrame = desktopPane.getAllFrames();

        for (JInternalFrame ArrayInFrame1 : ArrayInFrame) {
            if (ArrayInFrame1.getTitle().equals(Title)) {
                
                try {
                    ArrayInFrame1.setSelected(true);
                }catch (PropertyVetoException ex) {
                    
                }return true;
            }
        }
        return false;
    }
    /**
	 * 函数:      closeOtherInternalFrames
         * 函数描述:  关闭除导航窗口和报警窗口外所有的JInternalFrame窗口
    */
    private void closeOtherInternalFrames(){
        JInternalFrame[] ArrayInFrame = desktopPane.getAllFrames();
        for (JInternalFrame ArrayInFrame1 : ArrayInFrame) {
            if (ArrayInFrame1.getTitle().equals(sTitleOfAlarmMSGToolBar) || ArrayInFrame1.getTitle().equals(sTitleOfNavigationToolBar)) {
                continue;
            }
            ArrayInFrame1.doDefaultCloseAction();
        }

    }
    /**
	 * 函数:      closeAllInternalFrames
         * 函数描述:  关闭所有的JInternalFrame窗口
    */
    private void closeAllInternalFrames(){
        try{
            JInternalFrame[] ArrayInFrame = desktopPane.getAllFrames();
            for (JInternalFrame ArrayInFrame1 : ArrayInFrame) {
                ArrayInFrame1.doDefaultCloseAction();
            }
        }catch (Exception e){
            TxtLogger.append(sFileName, "closeAllInternalFrames()","系统在关闭所有的JInternalFrame窗口过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      closeAllJFrames
         * 函数描述:  关闭所有的JFrame窗口
    */
    private void closeAllJFrames(){
        for (int i=listJFrame.size()-1;i>=0;i--){
            listJFrame.get(i).dispatchEvent(new WindowEvent((Window)listJFrame.get(i),WindowEvent.WINDOW_CLOSING));
        }
    }
    /**
	 * 函数:      doAfterSwitchUser
         * 函数描述:  切换用户之后重新处理系统的当前用户标识
    */
    private void doAfterSwitchUser(){
        OperatorName.setText(CommonParas.UserState.UserName);
        HeadB.setIcon(CommonParas.UserState.HeadIcon);
        setWidthOfOperatorName();//根据用户名长度设置其对应的JLabel一个合适的长度
        closeOtherInternalFrames();
        refreshMenuStateByAuthority();
        for (JFrame ArrayFrame1 : CommonParas.g_ListJFrame) {
            if (ArrayFrame1 instanceof  JFrameTotalPreview){
                ((JFrameTotalPreview) ArrayFrame1).doAfterSwitchUser();
                return;
            }
        }
    }
    /**
	 * 函数:      doBeforeExit
         * 函数描述:  在退出系统之前所做的工作
    */
    private boolean doBeforeExit(){
        try{
            //报警撤防、设备预览停止、设备注销、释放SDK资源、关闭窗口
            CommonParas.closeAlarmChan();//撤防
            closeAllJFrames();
            closeAllInternalFrames();
            //如果已经注册,注销
            CommonParas.closeDeviceDetailParaList(sFileName);
            //cleanup SDK
            CommonParas.hCNetSDK.NET_DVR_Cleanup();
            CommonParas.SystemWriteLog(CommonParas.LogType.LOG_SYS_CODE, CommonParas.sUserLogout, sFileName);
            return true;
        }catch (Exception e){
            TxtLogger.append(sFileName, "doBeforeExit()","系统在在退出系统之前所做的操作过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /**
        * 函数:      refreshMenuStateByAuthority
        * 函数描述:  根据用户的权限设置菜单的状态
    */
    private void refreshMenuStateByAuthority(){
        
        /*超级管理员权限设置*/
        setItemInvisibleForNonAdmin();
                
        /*管理员权限设置*/
        setItemStateForManager();
        
        
        /*操作员权限设置*/
        //AUTHORITY_PREVIEW = "预览";jMenuItemPreview;
        InFrameNavigation.jButtonPreview.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_PREVIEW, jMenuItemPreview));

        //AUTHORITY_PLAYBACK = "回放远程录像";jMenuItemPlayBack;
        InFrameNavigation.jButtonPlayBack.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_PLAYBACK, jMenuItemPlayBack));
 
    }
    /**
        * 函数:      setItemStateForManager
        * 函数描述:  将管理员的的未授权权限部分变为不可视或者不可用。
    */
    private void setItemStateForManager(){
        
        //AUTHORITY_RECORDSCHEDULE_QUICKLYSETUP = "录像快速设置";jMenuItemRecordQuicklySetup;
        InFrameNavigation.jButtonRecordQuicklySetup.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_RECORDSCHEDULE_QUICKLYSETUP, jMenuItemRecordQuicklySetup));
        
        //AUTHORITY_RECORDSCHEDULE_CONFIG = "录像抓图计划配置";jMenuItemRecordSchedule;
        InFrameNavigation.jButtonRecordSchedule.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_RECORDSCHEDULE_CONFIG, jMenuItemRecordSchedule));
    
        //AUTHORITY_USER_MANAGE = "用户管理";jMenuItemUserManage;
        InFrameNavigation.jButtonUserManage.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_USER_MANAGE, jMenuItemUserManage));

        //AUTHORITY_ALARMPARAS_SETUP = "报警参数设置";jMenuItemSetAlarmParas;
        InFrameNavigation.jButtonAlarmParasSetup.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_ALARMPARAS_SETUP, jMenuItemSetAlarmParas));

        //AUTHORITY_ALARM_SETUP = "设备报警布防";jMenuItemSetupAlarmChan;
        InFrameNavigation.jButtonAlarmSetup.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_ALARM_SETUP, jMenuItemSetupAlarmChan));

        //AUTHORITY_ALARMOUT_CTRL = "报警输出控制";jMenuItemSetupAlarmOut;
        InFrameNavigation.jButtonAlarmOutCtrl.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_ALARMOUT_CTRL, jMenuItemSetupAlarmOut));

        //AUTHORITY_LOG_MANAGE = "日志管理";jMenuItemLogMaint;
        InFrameNavigation.jButtonLogMaint.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_LOG_MANAGE, jMenuItemLogMaint));

        //AUTHORITY_DVR_MAINT = "设备维护";jMenuItemDVRMaint;
        InFrameNavigation.jButtonDVRMaint.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_DVR_MAINT, jMenuItemDVRMaint));
        
        //AUTHORITY_CHECKTIME_BATCH = "批量校时";jMenuItemCheckTime;
        InFrameNavigation.jButtonDVRCheckTime.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_CHECKTIME_BATCH, jMenuItemCheckTime));
        
        //AUTHORITY_CLIENT_EXIT = "退出客户端";jMenuItem1Exit;
        if (!CommonParas.ifHaveUserAuthority(CommonParas.AuthorityItems.AUTHORITY_CLIENT_EXIT, sFileName)){
            jMenuItemExit.setEnabled(false);
            CloseB.setEnabled(false);
        }else{
            jMenuItemExit.setEnabled(true);
            CloseB.setEnabled(true);
        }
        CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_CLIENT_EXIT, jMenuItemExit);
        
        //设备分组管理AUTHORITY_DVRGROUP_MANAGE jButtonDVRGroup
        InFrameNavigation.jButtonDVRGroup.setEnabled(CommonParas.setMenuState(CommonParas.AuthorityItems.AUTHORITY_DVRGROUP_MANAGE, jMenuItemDVRGroup));
    }
    /**
        * 函数:      setItemInvisibleForNonAdmin
        * 函数描述:  将超级管理员的权限部分变为不可视
    */
    private void setItemInvisibleForNonAdmin(){
        boolean IfAdminVisible = CommonParas.ifAdmin();
        
        //编码设备管理
        jMenuItemDevManage.setVisible(IfAdminVisible);
        InFrameNavigation.jButtonDVRManage.setVisible(IfAdminVisible);
        //系统参数配置
        jMenuItemSysParasConfig.setVisible(IfAdminVisible);
        InFrameNavigation.jButtonSysParasConfig.setVisible(IfAdminVisible);
        //控制雨刷
        jMenuItemCtrlWiper.setVisible(IfAdminVisible);
        //语言转换
        jMenuLanguage.setVisible(IfAdminVisible);
        jSeparatorLanguage.setVisible(IfAdminVisible);

    }
    
    /**
        * 函数:      exitSysQuery
        * 函数描述:  询问确定后，用户注销退出
    */
    public void exitSysQuery(){
        if ((JOptionPane.showConfirmDialog(this, sMessageBox[0],  
                        sMessageBox[1], JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)) return;
        if (doBeforeExit()) System.exit(0);
    }
    
    /**
        * 函数:      exitSys
        * 函数描述:  用户注销退出
    */
    public void exitSys(){
        if (doBeforeExit()) System.exit(0);
    }
    
    /**
        * 函数:      setWidthOfOperatorName
        * 函数描述:  根据用户名长度设置其对应的JLabel一个合适的长度
    */
    private void setWidthOfOperatorName(){
        try{
            String LableText = OperatorName.getText().trim();
            if (LableText.equals("")) return;

            char[] chars = LableText.toCharArray();
            FontMetrics fontMetrics = OperatorName.getFontMetrics(OperatorName.getFont());

            int PreferredW = fontMetrics.charsWidth(chars, 0, chars.length);
            OperatorName.setPreferredSize(new java.awt.Dimension(PreferredW, 40));
        }catch(Exception e){
            TxtLogger.append(sFileName, "setWidthOfOperatorName()","系统在根据用户名长度设置其对应的JLabel一个合适的长度过程中，出现错误"
                     + "\r\n                       Exception:" + e.toString());
        }
    }
    
    
    /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
        * @param Language 设置的系统语言
    */
    private void modifyLocales(){
        
        //设置语言菜单下的子菜单的选中状态
        intialMenuLanguageState();
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作
        
        MyLocales Locales = CommonParas.SysParas.sysLocales;//MyLocales.getMyLocales(CommonParas.SysParas.SYSPARAS_COMMON_LANGUAGE, CommonParas.SysParas.SYSPARAS_COMMON_COUNTRY);
        
        //菜单显示
        jMenuSystem.setText(Locales.getString("ClassStrings", "Jkms.jMenuSystem"));
        jMenuItemLock.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemLock"));
        jMenuItemModifyPassword.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemModifyPassword"));
        jMenuItemSwitchUser.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemSwitchUser"));
        jMenuItemAbout.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemAbout"));
        jMenuItemExit.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemExit"));

        jMenuDev.setText(Locales.getString("ClassStrings", "Jkms.jMenuDev"));
        jMenuItemDevManage.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemDevManage"));
        jMenuItemDVRGroup.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemDVRGroup"));
        jMenuItemDVRMaint.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemDVRMaint"));
        jMenuItemCheckTime.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemCheckTime"));
        jMenuItemCtrlWiper.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemCtrlWiper"));

        jMenuView.setText(Locales.getString("ClassStrings", "Jkms.jMenuView"));
        jMenuItemNavigation.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemNavigation"));
        jMenuItemAlarmMSG.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemAlarmMSG"));
        jMenuItemPreview.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemPreview"));
        jMenuItemPlayBack.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemPlayBack"));

        jMenuAlarm.setText(Locales.getString("ClassStrings", "Jkms.jMenuAlarm"));
        jMenuItemSetupAlarmChan.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemSetupAlarmChan"));
        jMenuItemSetupAlarmOut.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemSetupAlarmOut"));
        jMenuItemSetAlarmParas.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemSetAlarmParas"));

        jMenuTool.setText(Locales.getString("ClassStrings", "Jkms.jMenuTool"));
        jMenuItemUserManage.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemUserManage"));
        jMenuItemRecordSchedule.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemRecordSchedule"));
        jMenuItemRecordQuicklySetup.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemRecordQuicklySetup"));
        jMenuItemSysParasConfig.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemSysParasConfig"));
        jMenuItemLogMaint.setText(Locales.getString("ClassStrings", "Jkms.jMenuItemLogMaint"));
        jMenuLanguage.setText(Locales.getString("ClassStrings", "Jkms.jMenuLanguage"));

        //按钮显示
        jButtonLogout.setText(Locales.getString("ClassStrings", "Jkms.jButtonLogout"));
        jButtonModifyPassword.setText(Locales.getString("ClassStrings", "Jkms.jButtonModifyPassword"));
        jButtonSwitchUser.setText(Locales.getString("ClassStrings", "Jkms.jButtonSwitchUser"));
        
        //其他信息参数
        sTitleOfAlarmMSGToolBar   = Locales.getString("MessageStrings", "Jkms.sTitleOfAlarmMSGToolBar");  //报警模块的名字Alarm Window报警窗口
        sTitleOfNavigationToolBar = Locales.getString("MessageStrings", "Jkms.sTitleOfNavigationToolBar");//导航模块的名字Navigation Window导航窗口
        sInitializationFailed   = Locales.getString("MessageStrings", "Jkms.sInitializationFailed");  //SDK初始化失败!
        
        sInFrameTitle[0] = Locales.getString("MessageStrings", "Jkms.sInFrameTitle0");//"报警设置"
        sInFrameTitle[1] = Locales.getString("MessageStrings", "Jkms.sInFrameTitle1");//"设备管理"
        sInFrameTitle[2] = Locales.getString("MessageStrings", "Jkms.sInFrameTitle2");//"用户管理"
        sInFrameTitle[3] = Locales.getString("MessageStrings", "Jkms.sInFrameTitle3");//"录像计划"
        sInFrameTitle[4] = Locales.getString("MessageStrings", "Jkms.sInFrameTitle4");//"日志管理"
        sMessageBox[0]   = Locales.getString("MessageStrings", "Jkms.sMessageBox0");//"确定要退出？"
        sMessageBox[1]   = Locales.getString("MessageStrings", "Jkms.sMessageBox1");//"退出"};
        

    }
    

    
    /**
        * 函数:      intialMenuLanguageState
        * 函数描述:  设置语言菜单下的子菜单的选中状态
    */
    private void intialMenuLanguageState(){
        if (CommonParas.SysParas.SYSPARAS_COMMON_LANGUAGE.equals(MyLocales.LANGUAGE_CHINESE)) {//如果是中文
            jCheckBoxMenuItemChinese.setSelected(true);
            jCheckBoxMenuItemEnglish.setSelected(false);
        }else{//英文
            jCheckBoxMenuItemChinese.setSelected(false);
            jCheckBoxMenuItemEnglish.setSelected(true);
        }
    } 

    private void startBackupDatabaseTimer(){
        try {
            // 一天的毫秒数
            long daySpan = 24 * 60 * 60 * 1000;
            
            // 规定的每天时间15:33:30运行
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd '00:00:00'");
            // 首次运行时间
            Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf.format(new Date()));
            
            // 如果今天的已经过了 首次运行时间就改为明天
            if(System.currentTimeMillis() > startTime.getTime())
                startTime = new Date(startTime.getTime() + daySpan);
            
            Timer t = new Timer();
            
            TimerTask task = new TimerTask(){
                @Override
                public void run() {
                    InitDB.getInitDB(sFileName).backUpDatabase();
                }
            };
            
            // 以每24小时执行一次
            t.scheduleAtFixedRate(task, startTime, daySpan);
        } catch (ParseException ex) {
            TxtLogger.append(sFileName, "startBackupDatabaseTimer()","系统在备份数据库过程中，出现错误"
                     + "\r\n                       ParseException:" + ex.toString());
        }catch (Exception ex) {
            TxtLogger.append(sFileName, "startBackupDatabaseTimer()","系统在备份数据库过程中，出现错误"
                     + "\r\n                       Exception:" + ex.toString());
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        if (CommonParas.isRunning()) return;
                    
        if (!CommonParas.SysParas.initialSysParas()) System.exit(0);//初始化系统参数。系统第一次连接数据库

        CommonParas.modifyLocales();//国际化

        if (!CommonParas.softRegister("Jkms.java")) System.exit(0);//检测序列号

        
        try {
            UIManager.setLookAndFeel(JymsLookAndFeel.class.getName());
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Jkms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
       

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                try{

                    CommonParas.jkms = new Jkms();

                    UserLogin userLogin = new UserLogin(CommonParas.jkms,true,0);
                    CommonParas.centerWindow(userLogin);
                    userLogin.setVisible(true);
                    if (userLogin.getReturnState() == 0) System.exit(0);
                    
                    JFrameSplash.showSplashWindow();
                    
                    CommonParas.jkms.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    CommonParas.jkms.setVisible(true);

                    
                }catch(Exception e){
                    TxtLogger.append("Jkms", "main()","系统在启动过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
                }
                //jkms.setVisible(true);
            }
        });
    }
    
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CloseB;
    private javax.swing.JButton HeadB;
    private javax.swing.JButton LockB;
    private javax.swing.JButton MinB;
    private javax.swing.JLabel OperatorName;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JButton jButtonLogout;
    private javax.swing.JButton jButtonModifyPassword;
    private javax.swing.JButton jButtonSwitchUser;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemChinese;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemEnglish;
    private javax.swing.JDialog jDialogOperator;
    private javax.swing.JMenu jMenuAlarm;
    private javax.swing.JMenuBar jMenuBarSys;
    private javax.swing.JMenu jMenuDev;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemAlarmMSG;
    protected javax.swing.JMenuItem jMenuItemCheckTime;
    private javax.swing.JMenuItem jMenuItemCtrlWiper;
    protected javax.swing.JMenuItem jMenuItemDVRGroup;
    protected javax.swing.JMenuItem jMenuItemDVRMaint;
    protected javax.swing.JMenuItem jMenuItemDevManage;
    protected javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemLock;
    protected javax.swing.JMenuItem jMenuItemLogMaint;
    private javax.swing.JMenuItem jMenuItemModifyPassword;
    private javax.swing.JMenuItem jMenuItemNavigation;
    protected javax.swing.JMenuItem jMenuItemPlayBack;
    protected javax.swing.JMenuItem jMenuItemPreview;
    protected javax.swing.JMenuItem jMenuItemRecordQuicklySetup;
    protected javax.swing.JMenuItem jMenuItemRecordSchedule;
    protected javax.swing.JMenuItem jMenuItemSetAlarmParas;
    protected javax.swing.JMenuItem jMenuItemSetupAlarmChan;
    protected javax.swing.JMenuItem jMenuItemSetupAlarmOut;
    private javax.swing.JMenuItem jMenuItemSwitchUser;
    protected javax.swing.JMenuItem jMenuItemSysParasConfig;
    protected javax.swing.JMenuItem jMenuItemUserManage;
    private javax.swing.JMenu jMenuLanguage;
    private javax.swing.JMenu jMenuLogo;
    protected javax.swing.JMenu jMenuSystem;
    private javax.swing.JMenu jMenuTool;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelDown;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparatorLanguage;
    private javax.swing.JToolBar jToolBarJkms;
    // End of variables declaration//GEN-END:variables
  
    /**
        * 内部类:    ToolBarAction
        * 类描述:   每打开一个子窗口，在TooleBar上添加按钮和分隔符
    */
    class ToolBarAction extends AbstractAction {
        private JInternalFrame InternalJFrame;
        private JButton JB;
        private int widthOfJB = 100;
        private int heightOfJB = 34;
        public ToolBarAction(String name, Icon icon,JInternalFrame jInternalJFrame) {
            super(name);
            try{
                
                this.InternalJFrame = jInternalJFrame;
                InternalJFrame.setTitle(name);
                InternalJFrame.setResizable(false);
                InternalJFrame.setClosable(true);
                InternalJFrame.setMaximizable(true);
                InternalJFrame.setSize(desktopPane.getSize());
                ((BasicInternalFrameUI) InternalJFrame.getUI()).setNorthPane(null); 
                desktopPane.add(InternalJFrame); 
                try {
                    InternalJFrame.setSelected(true);
                } catch (PropertyVetoException ex) {
                    TxtLogger.append(sFileName, "ToolBarAction()","ToolBarAction构造函数的过程中，出现错误"
                                 + "\r\n                       Exception:" + ex.toString());
                }

                JB = jToolBarJkms.add(this);
                jToolBarJkms.addSeparator();
                JB.setIcon(icon); // NOI18N
                JB.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // 设置ToolBar按钮文字"Microsoft YaHei UI"
                JB.setForeground(new java.awt.Color(255, 255, 255));
                JB.setBackground(new java.awt.Color(64, 64, 64));
                //JB.setSize(150, 38);//设置ToolBar按钮的尺寸
                widthOfJB = getWidthOfButtonText(JB) + 36;
                JB.setPreferredSize(new Dimension(widthOfJB, heightOfJB));
                JB.setMaximumSize(new Dimension(widthOfJB, heightOfJB));
                JB.setRolloverEnabled(false);
                JB.setOpaque(true);
                JB.setBorder(null);
                JB.setBorderPainted(false);
    //        JB.setContentAreaFilled(false);  
    //            JB.setUI(new ToolButtonUI());

                /*
                     * 如果没有设置文字的位置，系统默认值会将文字置于图形的右边中间位置。
                */
                JB.setHorizontalTextPosition(JButton.RIGHT);
                JB.setVerticalTextPosition(JButton.CENTER);
                jToolBarJkms.validate();
                jToolBarJkms.updateUI();
                listJButton.add(JB);
                //listInJFrame.add(InternalJFrame);

                InternalJFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
                    @Override
                    public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {

                        int Index = jToolBarJkms.getComponentIndex(JB);
                        if (Index >= 0 && Index < jToolBarJkms.getComponentCount()-1) jToolBarJkms.remove(Index+1);//删除分隔符
                        jToolBarJkms.remove(JB);
                        jToolBarJkms.validate();
                        jToolBarJkms.updateUI();

                        refreshJButtonState();
                    }
                    @Override
                    public void internalFrameOpened(InternalFrameEvent e) {

                    }
                    @Override
                    public void internalFrameClosing(InternalFrameEvent e) {}
                    @Override
                    public void internalFrameIconified(InternalFrameEvent e) {}
                    @Override
                    public void internalFrameDeiconified(InternalFrameEvent e) {}
                    @Override
                    public void internalFrameActivated(InternalFrameEvent e) {
                        JB.setBackground(new java.awt.Color(64, 64, 64));
                        JB.setPreferredSize(new Dimension(widthOfJB, heightOfJB));
                        JB.setMaximumSize(new Dimension( widthOfJB, heightOfJB));
                        JB.setRolloverEnabled(false);

                    }
                    @Override
                    public void internalFrameDeactivated(InternalFrameEvent e) {
                        JB.setBackground(new java.awt.Color(151, 151, 151));
                        JB.setPreferredSize(new Dimension(widthOfJB, heightOfJB));
                        JB.setMaximumSize(new Dimension( widthOfJB, heightOfJB));
                        JB.setRolloverEnabled(false);
                    }
                });
            }catch (Exception e){
                TxtLogger.append(sFileName+".ToolBarAction", "ToolBarAction()","系统在在退出系统之前所做的操作过程中，出现错误"
                                + "\r\n                       Exception:" + e.toString()
                                + "\r\n                       窗口名:" + name);//name
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {

                try {
                    InternalJFrame.setSelected(true);
                    JB.setPreferredSize(new Dimension(widthOfJB, heightOfJB));
                    JB.setMaximumSize(new Dimension(widthOfJB, heightOfJB));
                    JB.setBackground(new java.awt.Color(64, 64, 64));
                } catch (Exception ex) {
                    TxtLogger.append(sFileName, "ToolBarAction.actionPerformed()","ToolBarAction类按钮被按的过程中，出现错误"
                             + "\r\n                       Exception:" + ex.toString());
                }
        }
        
//        private void clickJButton(){
////            int Index = listJButton.indexOf(JB);
////            if (Index  == listJButton.size()-1)
////                listJButton.get(listJButton.size()-2).doClick();
////            else listJButton.get(listJButton.size()-1).doClick();
//            listJButton.get(listJButton.size()-1).doClick();
//        }

        private void refreshJButtonState(){
            
            listJButton.remove(JB);
            if (listJButton.size()<1) return;
            listJButton.get(listJButton.size()-1).doClick();
            for (int i=0;i<listJButton.size();i++){
                listJButton.get(i).setRolloverEnabled(false);
            }
        }
        
        /**
            * 函数:            getWidthOfButtonText
            * 函数描述:         获取JButton的文字长度
            * @param Button     JButton
            * @return int       JButton中文字的长度
        */
        private int getWidthOfButtonText(JButton Button){
            try{

                String ButtonText = Button.getText().trim();
                if (ButtonText.equals("")) return widthOfJB;

                char[] chars = ButtonText.toCharArray();
                FontMetrics fontMetrics = Button.getFontMetrics(Button.getFont());

                int PreferredW = fontMetrics.charsWidth(chars, 0, chars.length);

                return PreferredW;

            }catch(Exception e){
                TxtLogger.append(sFileName, "getWidthOfButtonText()","系统在获取JButton的文字长度过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
            }
            return widthOfJB;
        }
    }// end of inner class ToolBarAction
    
    
    /**
        *类:      RefreshWindowParasWorker
        *类描述:  刷新窗口参数状态
        *        原先，不管用哪种窗口JWindow、JFrame、JDialog，用什么方法，都只可以在窗口的构造函数改变SplashWindow的控件值
        *        却不能在窗口的Opened事件中改变SplashWindow的控件值。
        *        最后只能用并行进程的方法，才正确的解决。
    */
    private class RefreshWindowParasWorker extends SwingWorker {

        @Override
        protected Object doInBackground() throws Exception {
            try{
                CommonParas.jkms.initialSysParas();
                OperatorName.setText(CommonParas.UserState.UserName);
                HeadB.setIcon(CommonParas.UserState.HeadIcon);
                setWidthOfOperatorName();//根据用户名长度设置其对应的JLabel一个合适的长度
                
                //打开报警窗口
                //JFrameSplash2.setProcessDesc("打开报警窗口...");
                InFrameAlarmMSG = new JInFrameAlarmMSG();
                openOneInFrame( InFrameAlarmMSG, sTitleOfAlarmMSGToolBar, sSysPathURL + "image/alarm16.png", null );
                //打开导航窗口
                //JFrameSplash2.setProcessDesc("打开导航窗口...");
                InFrameNavigation = new JInFrameNavigation(CommonParas.jkms);
                openOneInFrame( InFrameNavigation, sTitleOfNavigationToolBar, sSysPathURL + "image/navigation16.png", null );

                //菜单项的长度包括Margin的长度
                int xx = jMenuBarSys.getWidth()/2 - (jMenuSystem.getWidth() + jMenuDev.getWidth() + jMenuView.getWidth() + jMenuAlarm.getWidth() + jMenuTool.getWidth()) - jMenuLogo.getWidth()/2;//38为图标的一半宽度
              
//                System.out.println(jMenuBarSys.getWidth());
//                System.out.println(jMenuSystem.getWidth());
//                System.out.println(jMenuLogo.getWidth());
                jMenuLogo.setMargin(new java.awt.Insets(0, xx, 0, 0));


                //设置系统菜单状态
                refreshMenuStateByAuthority();
                JFrameSplash.closeSplashWindow();

                //启动备份数据库定时器
                startBackupDatabaseTimer();

            }catch (Exception e){
                System.out.println(e.toString());
                TxtLogger.append(sFileName, "formWindowOpened()","系统在窗口打开事件中，出现错误"
                                 + "\r\n                       Exception:" + e.toString());
            }
            return null;
        }

    }
    
}
