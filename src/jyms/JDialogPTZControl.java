/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jyms.data.TxtLogger;
import jyms.ui.SliderUI_White;

/**
 *
 * @author John
 */
public class JDialogPTZControl extends javax.swing.JDialog {
    
    private final String sFileName = this.getClass().getName() + ".java";
    private NativeLong m_lRealHandle = new NativeLong(-1);//预览句柄
    private boolean m_bAutoOn = false;//自动左右云台
//    private MyBoolean m_bLightOn = new MyBoolean(false);//开启灯光
//    private MyBoolean m_bWiperOn = new MyBoolean(false);//开启雨刷
//    private MyBoolean m_bFanOn = new MyBoolean(false);//开启风扇
//    private MyBoolean m_bHeaterOn = new MyBoolean(false);//开启加热
//    private MyBoolean m_bAuxOn1 = new MyBoolean(false);//开启辅助1
//    private MyBoolean m_bAuxOn2 = new MyBoolean(false);//开启辅助2
//    private boolean m_bIsOnCruise = false;//是否在巡航：RUN_SEQ
//    private boolean m_bMemCruise = false;//是否在记录轨迹：STA_MEM_CRUISE
//    private boolean m_bRunCruise = false;//是否在运行轨迹：RUN_CRUISE
    private JLabel jLabelPTZ_P = new JLabel();//从父窗口传递进来的JLabel，改变其P值
    private JLabel jLabelPTZ_T = new JLabel();//从父窗口传递进来的JLabel，改变其T值
    private JLabel jLabelPTZ_Z = new JLabel();//从父窗口传递进来的JLabel，改变其Z值
    private boolean bLabelPTZ = false;//是否需要进行PTZ的参数显示
    private ChannelRealPlayer channelRPBall;
//    private DefaultTableModel presetTableModel;
//    private String[] specialNoOfPreset;//此列表里的预置点只能用于调用，不能有其他操作，比如修改、删除
//    private int maxLengthOfPresetName = 20;//预置点名称的最长长度
//    private String oldPresetName = "";//存储修改之前的预置点名称
//    private final int colOfPresetName = 1;//存储预置点名称的列序号
//    
//    private DefaultTableModel cruisePointTableModel;//jTableCruise巡航点信息 
//    private int iCruisePointNumMax = 32;//单条巡航扫描的最大预置点个数
//    private int iDwellTimeMax = 255;//巡航点最大停顿时间
//    private int iDwellTimeDefault = 10;//巡航点默认停顿时间
//    private int iCruiseSpeedMax = 255;//最大巡航速度
//    private int iAddOrUpdate = 1;//1增加巡航点；2为修改巡航点
    
    //为日志填写做准备
    //int[] iCommands = new int[]{2,3,4,5,6,7,8,9,11,12,13,14,15,16,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,3313};
    String[] sCommandMeanings = new String[]{"灯光电源","雨刷开关","风扇开关","加热器开关","辅助设备开关","辅助设备开关","设置预置点",
                    "清除预置点","焦距变大(倍率变大)","焦距变小(倍率变小)","焦点前调","焦点后调","光圈扩大","光圈缩小","云台上仰","云台下俯","云台左转","云台右转",
                    "云台上仰和左转","云台上仰和右转","云台下俯和左转","云台下俯和右转","云台左右自动扫描","将预置点加入巡航序列","设置巡航点停顿时间","设置巡航速度",
                    "将预置点从巡航序列中删除","开始记录轨迹","停止记录轨迹","开始轨迹","开始巡航","停止巡航","转到预置点","云台下俯和焦距变大(倍率变大)",
                    "云台下俯和焦距变小(倍率变小)","云台左转和焦距变大(倍率变大)","云台左转和焦距变小(倍率变小)","云台右转和焦距变大(倍率变大)",
                    "云台右转和焦距变小(倍率变小)","云台上仰和左转和焦距变大(倍率变大)","云台上仰和左转和焦距变小(倍率变小)","云台上仰和右转和焦距变大(倍率变大)",
                    "云台上仰和右转和焦距变小(倍率变小)","云台下俯和左转和焦距变大(倍率变大)","云台下俯和左转和焦距变小(倍率变小)","云台下俯和右转和焦距变大(倍率变大)",
                    "云台下俯和右转和焦距变小(倍率变小)","云台上仰和焦距变大(倍率变大)","云台上仰和焦距变小(倍率变小)","快球云台花样扫描"};
    HashMap<Integer, String> hmCommandMeanings = new HashMap();


    /**
        * Creates new form JDialogPTZControl
        * @param parent
        * @param channelRP
        * @param jLabelPTZP
        * @param jLabelPTZT
        * @param jLabelPTZZ
     */
    public JDialogPTZControl(java.awt.Window parent, ChannelRealPlayer channelRP,JLabel jLabelPTZP,JLabel jLabelPTZT,JLabel jLabelPTZZ) {
        super(parent);
        initComponents();
        modifyLocales();
        intialCommonMeaning();
        this.channelRPBall = channelRP;
        
        m_lRealHandle = channelRP.getPreviewHandle();
        this.jLabelPTZ_P=jLabelPTZP;
        this.jLabelPTZ_T=jLabelPTZT;
        this.jLabelPTZ_Z=jLabelPTZZ;
        bLabelPTZ = true;//是否需要进行PTZ的参数显示
        //初始化对话框,预览,list初始化等操作
        initialDialog();
    }

    /**
        * Creates new form JDialogPTZControl
        * @param parent
        * @param modal
        * @param channelRP
     */
    public JDialogPTZControl(java.awt.Frame parent, boolean modal, ChannelRealPlayer channelRP) {
//        this(parent, modal,lRealHandle,null,null,null) ;
        super(parent, modal);
        
        initComponents();
        modifyLocales();
        intialCommonMeaning();
        this.channelRPBall = channelRP;
        m_lRealHandle = channelRP.getPreviewHandle();
        //初始化对话框,预览,list初始化等操作
        initialDialog();
        
    }
    /**
	 * 函数:      initialDialog
         * 函数描述:  初始化系统参数
    */
    private void initialDialog(){
        
        //UI操作
       
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
 
        jSliderSpeed.setUI(new SliderUI_White(jSliderSpeed));
       

    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelP = new javax.swing.JLabel();
        jLabelT = new javax.swing.JLabel();
        jLabelZ = new javax.swing.JLabel();
        jPanelPTZ = new javax.swing.JPanel();
        jLabelPTZCtrl = new javax.swing.JLabel();
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

        jLabelP.setText("1");

        jLabelT.setText("1");

        jLabelZ.setText("1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("云台控制");
        setPreferredSize(new java.awt.Dimension(200, 320));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabelPTZCtrl.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelPTZCtrl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jPanel3.setForeground(new java.awt.Color(240, 240, 240));
        jPanel3.setMinimumSize(new java.awt.Dimension(117, 120));
        jPanel3.setPreferredSize(new java.awt.Dimension(117, 120));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel10.setForeground(new java.awt.Color(240, 240, 240));
        jPanel10.setOpaque(false);
        jPanel10.setPreferredSize(new java.awt.Dimension(110, 98));
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
                .addGap(0, 15, Short.MAX_VALUE)
                .addComponent(jButtonLeftUp))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
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
                .addGap(0, 15, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
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
                .addGap(0, 15, Short.MAX_VALUE)
                .addComponent(jButtonLeftDown))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jButtonLeftDown)
                .addGap(0, 11, Short.MAX_VALUE))
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
                .addGap(0, 15, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jButtonRightDown)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jPanel10.add(jPanel16);

        jPanel3.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 110, 100));

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
                .addGap(26, 26, 26)
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
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonZoomOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonFocusFar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                            .addComponent(jButtonIrisClose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonFocusFar, jButtonZoomOut});

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelPTZLayout = new javax.swing.GroupLayout(jPanelPTZ);
        jPanelPTZ.setLayout(jPanelPTZLayout);
        jPanelPTZLayout.setHorizontalGroup(
            jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPTZLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanelPTZLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPTZCtrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelPTZLayout.setVerticalGroup(
            jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPTZLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPTZCtrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPTZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPTZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        //jDialogCruisePoint.setVisible(false);
    }//GEN-LAST:event_formWindowClosing

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

    private void jButtonFocusFarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFocusFarMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.FOCUS_FAR, 0);
    }//GEN-LAST:event_jButtonFocusFarMousePressed

    private void jButtonFocusFarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFocusFarMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.FOCUS_FAR, 1);
    }//GEN-LAST:event_jButtonFocusFarMouseReleased

    private void jButtonFocusNearMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFocusNearMousePressed
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.FOCUS_NEAR, 0);
    }//GEN-LAST:event_jButtonFocusNearMousePressed

    private void jButtonFocusNearMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFocusNearMouseReleased
        PTZWindowControlAll(m_lRealHandle, HCNetSDK.FOCUS_NEAR, 1);
    }//GEN-LAST:event_jButtonFocusNearMouseReleased

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
                    }else{
                        //只有在鼠标释放时，也就是操作结束时才进行写日志操作
                        //因为该窗口只有在报警设置、鱼球联动等设置里才用到该窗口，所以可以不写日志
                        //if(iStop == 1) writePTZControlLog(getCommonMeaning(iPTZCommand));
                    }

                } else{//速度为默认时
                
                    ret = CommonParas.hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
                    if (!ret){
                        //JOptionPane.showMessageDialog(this, "云台控制失败");
                        writePTZControlErrorLog(getCommonMeaning(iPTZCommand));
                    }else{
                        //只有在鼠标释放时，也就是操作结束时才进行写日志操作
                        //因为该窗口只有在报警设置、鱼球联动等设置里才用到该窗口，所以可以不写日志
                        //if(iStop == 1) writePTZControlLog(getCommonMeaning(iPTZCommand));
                    }
                }
                if (bLabelPTZ) setPTZPos();
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PTZWindowControlAll()","系统在云台控制过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }

    /**
	 * 函数:      getPTZPos
         * 函数描述:  设置球机当前位置表示的三个标签的值
    */
    public void setPTZPos(){
        try{
            if (channelRPBall == null || jLabelPTZ_P == null || jLabelPTZ_T == null || jLabelPTZ_Z == null) return;
            NativeLong UserID = channelRPBall.getUserID();
            if (UserID == null || UserID.intValue() < 0) return ;
            IntByReference ibrBytesReturned = new IntByReference(0);//获取设备的配置信息。

            boolean bRet = false;
            HCNetSDK.NET_DVR_PTZPOS strPTZPos = new HCNetSDK.NET_DVR_PTZPOS();
            strPTZPos.write();
            Pointer lpStrPTZPos = strPTZPos.getPointer();
            bRet = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(UserID,HCNetSDK.NET_DVR_GET_PTZPOS,channelRPBall.getChannel(),lpStrPTZPos,strPTZPos.size(),ibrBytesReturned);
            strPTZPos.read();
            jLabelPTZ_P.setText(""+strPTZPos.wPanPos);
            jLabelPTZ_T.setText(""+strPTZPos.wTiltPos);
            jLabelPTZ_Z.setText(""+strPTZPos.wZoomPos);

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setPTZPos()","系统在设置球机当前位置表示的三个标签的值过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
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
        //操作时间、设备别名、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
        CommonParas.SystemWriteErrorLog("", channelRPBall.getDeviceparaBean().getAnothername(), sPTZCtrlFail, channelRPBall.getDeviceparaBean().getSerialNO(),  channelRPBall.getGroupName(),
                                channelRPBall.getEncodingDVRChannelNode(), channelRPBall.getSerialNoJoin(), "", CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,
                                CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE,sFileName);
        CommonParas.showErrorMessage(Remarks, channelRPBall.getDeviceparaBean().getAnothername(), sFileName);//"云台控制失败"
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
            java.util.logging.Logger.getLogger(JDialogPTZControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialogPTZControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialogPTZControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialogPTZControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialogPTZControl dialog = new JDialogPTZControl(new javax.swing.JFrame(), true,null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
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
       
        sPTZCtrl =                  Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPTZCtrl");  //云台控制
        sPTZCtrlFail =              Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.sPTZCtrlFail");  //云台控制失败

        //标签和按钮显示
        this.setTitle(sPTZCtrl);
        //jLabelPTZCtrl.setText(sPTZCtrl);
        jLabelMagnification.setText(        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelMagnification"));  //倍率
        jLabelFocus.setText(                Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelFocus"));  //聚焦
        jLabelAperture.setText(             Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jLabelAperture"));  //光圈
        jSliderSpeed.setToolTipText(        Locales.getString("JFrameTotalPreview", "JFrameTotalPreview.jSliderSpeed"));//云台控制的速度


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
        
 
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAuto;
    private javax.swing.JButton jButtonFocusFar;
    private javax.swing.JButton jButtonFocusNear;
    private javax.swing.JButton jButtonIrisClose;
    private javax.swing.JButton jButtonIrisOpen;
    private javax.swing.JButton jButtonLeft;
    private javax.swing.JButton jButtonLeftDown;
    private javax.swing.JButton jButtonLeftUp;
    private javax.swing.JButton jButtonRight;
    private javax.swing.JButton jButtonRightDown;
    private javax.swing.JButton jButtonRightUp;
    private javax.swing.JButton jButtonUp;
    private javax.swing.JButton jButtonZoomIn;
    private javax.swing.JButton jButtonZoomOut;
    private javax.swing.JButton jButtondown;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabelAperture;
    private javax.swing.JLabel jLabelFocus;
    private javax.swing.JLabel jLabelMagnification;
    private javax.swing.JLabel jLabelP;
    private javax.swing.JLabel jLabelPTZCtrl;
    private javax.swing.JLabel jLabelT;
    private javax.swing.JLabel jLabelZ;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelPTZ;
    private javax.swing.JSlider jSliderSpeed;
    // End of variables declaration//GEN-END:variables

    private String sPTZCtrl = "云台控制";
    private String sPTZCtrlFail = "云台控制失败";
    
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
}
