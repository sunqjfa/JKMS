/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.User32.POINT;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.ptr.IntByReference;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import jyms.data.DeviceParaBean;
import jyms.data.TxtLogger;
import jyms.tools.DomXML;
import jyms.tools.ImageIconBufferPool;
import jyms.ui.ScrollBarUI_White;
import jyms.ui.ScrollPaneUI_White;
import jyms.ui.SplitPaneUI_White;

/**
 *
 * @author John
 */
public class JFrameFishEyeTrackSetup extends javax.swing.JFrame {
    
    private final String sFileName = this.getClass().getName() + ".java";
    private byte bPointNum = 3;
    private ChannelRealPlayer channelRPBall = new ChannelRealPlayer();//球机的通道预览信息类
//    ChannelRealPlayer channelRPFishEyeSmall;//鱼眼分画面的通道预览信息类
    private ChannelRealPlayer channelRPFishEye;//鱼眼的通道预览信息类
    private boolean ifTrackSetupSuccess = false;//是否成功进行鱼球联动
    //设备登录所产生的用户ID列表，该设备登录所产生的用户ID
    private ArrayList<NativeLong> listDeviceLoginUserID = new ArrayList<>();
    static GDI32 gDi = GDI32.INSTANCE;
//    static USER32 uSer = USER32.INSTANCE;
    private FDrawFunSetPoint fDrawFunSetPoint;//画图回调函数 
    private FDrawFunSetBallCenter fDrawFunSetBallCenter;//球机画中心点函数

    private int IndexOfPoint = 1;//当前正在设置是哪一个标定点1/2/3 ？
    
    private HCNetSDK.NET_DVR_TRACK_DEV_PARAM strTrackDevPara;//跟踪设备参数结构。
    private HCNetSDK.NET_DVR_TRACK_CALIBRATION_PARAM_V41 strTrackCalibrationPara;//跟踪设备的标定参数结构体。
    private HCNetSDK.NET_DVR_PTZ_TRACK_PARAM strPTZTrackPara;//PTZ跟踪参数结构
    
    private String[] sPTZTrackParamRemark=new String[]{"跟踪目标消失后，跟踪下一目标或者回到初始位置。", "跟踪时间（T1）内目标消失，则跟踪下一个目标或者回到初始位置。","当前目标跟踪时间超过（T2）秒后，则跟踪出现的下一个目标或者回到初始位置。"};
 
    /**
     * Creates new form JFrameFishEyeTrackSetup
     * @param CRPFishEye
     */
    public JFrameFishEyeTrackSetup(ChannelRealPlayer CRPFishEye) {
        initComponents();
        modifyLocales();
//        sPTZTrackParamRemark = new String[3];
//        sPTZTrackParamRemark[0] = "跟踪目标消失后，跟踪下一目标或者回到初始位置。";
//        sPTZTrackParamRemark[1] = "跟踪时间（T1）内目标消失，则跟踪下一个目标或者回到初始位置。";
//        sPTZTrackParamRemark[2] = "当前目标跟踪时间超过（T2）秒后，则跟踪出现的下一个目标或者回到初始位置。";

        this.channelRPFishEye = CRPFishEye;
        if (CRPFishEye == null) channelRPFishEye = new ChannelRealPlayer();
        
        getFishEyeCalibPointNum();
        
        fDrawFunSetPoint = new FDrawFunSetPoint(bPointNum);//画图回调函数 
        POINT BallCenter = new POINT(panelBall.getWidth()/2,panelBall.getHeight()/2);
        fDrawFunSetBallCenter = new FDrawFunSetBallCenter(BallCenter);//画图回调函数 
        strTrackDevPara = new HCNetSDK.NET_DVR_TRACK_DEV_PARAM();
        strTrackCalibrationPara = new HCNetSDK.NET_DVR_TRACK_CALIBRATION_PARAM_V41();
        
        getTrackDevPara();
        if (this.channelRPBall.getIndexOflistDevice() < 0) {
            channelRPBall = new ChannelRealPlayer();
//            if (CRPBall == null) channelRPBall = new ChannelRealPlayer();
//            else this.channelRPBall = CRPBall;
        }
        strTrackCalibrationPara.byPointNum = bPointNum;//有效标定点个数 
        
        
        jSplitPane1.setUI(new SplitPaneUI_White());
  
        jScrollPane1.setUI(new ScrollPaneUI_White());
        jScrollPane1.getVerticalScrollBar().setUI(new ScrollBarUI_White());
        jScrollPane1.getHorizontalScrollBar().setUI(new ScrollBarUI_White());

    }

    private void getFishEyeCalibPointNum(){
        if (channelRPFishEye.getDeviceparaBean() != null){
            byte[] pOutBufAlarm = new byte[32*1024];//32k应该够用了
            if (1 == HCNetSDKExpand.GetDeviceAbility(channelRPFishEye.getDeviceparaBean().getAnothername(), 1, HCNetSDK.FISHEYE_ABILITY, "<FishEyeIPCAbility version=\"2.0\"><channelNO>", "</channelNO></FishEyeIPCAbility>", pOutBufAlarm)){
                String XMLOut = new String(pOutBufAlarm).trim();
                DomXML domXML = new DomXML(XMLOut,sFileName);

                //查看共有几个标定点.节点：TrackDevice - CalibParam - calibPointNum max="6"
                String ReturnV = domXML.readThirdLevelAttributeValue("TrackDevice", "CalibParam", "calibPointNum", "max");

                if (!(ReturnV.equals(""))) {
                    byte calibPointNum = Byte.parseByte(ReturnV);
                    bPointNum = calibPointNum;
                    if (calibPointNum <= 2){
                        jRadioButtonPoint3.setEnabled(false);
                        jTextFieldPointX3.setEnabled(false);
                        jTextFieldPointY3.setEnabled(false);
                        jTextFieldDomeP3.setEnabled(false);
                        jTextFieldDomeT3.setEnabled(false);
                        jTextFieldDomeZ3.setEnabled(false);
                        jButtonSetPoint3.setEnabled(false);
                    }
                }
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupPoint = new javax.swing.ButtonGroup();
        buttonGroupTrackMode = new javax.swing.ButtonGroup();
        jDialogPTZTrack = new javax.swing.JDialog();
        jRadioButtonPTZTrackAlways = new javax.swing.JRadioButton();
        jRadioButtonPTZTrackByTime = new javax.swing.JRadioButton();
        jRadioButtonPTZTrackNext = new javax.swing.JRadioButton();
        jSpinnerTrackTime = new javax.swing.JSpinner();
        jLabelPTZTrackByTimeUnit = new javax.swing.JLabel();
        jSpinnerTrackTimeMin = new javax.swing.JSpinner();
        jLabelPTZTrackNextUnit = new javax.swing.JLabel();
        jButtonSavePTZTrack = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabelRemark = new javax.swing.JLabel();
        jLabelExplanation = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        panelFishEye = new java.awt.Panel();
        jPanel2 = new javax.swing.JPanel();
        panelBall = new java.awt.Panel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabelFishEyePoint = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabelFishEyeX = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabelFishEyeY = new javax.swing.JLabel();
        jLabelFishEyeIP = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jButtonSelFishEye = new javax.swing.JButton();
        jButtonPTZTrackSet = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabelDomeCamera = new javax.swing.JLabel();
        jLabelBallIP = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelBallP = new javax.swing.JLabel();
        jLabelSelectPointX1 = new javax.swing.JLabel();
        jLabelBallT = new javax.swing.JLabel();
        jLabelSelectPointX2 = new javax.swing.JLabel();
        jLabelBallZ = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jButtonSelDome = new javax.swing.JButton();
        jButtonPTZControl = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButtonSetBallHorizontal = new javax.swing.JButton();
        jTextFieldPointX3 = new javax.swing.JTextField();
        jTextFieldBallHorizontalZ = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextFieldDomeT3 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldDomeP2 = new javax.swing.JTextField();
        jTextFieldDomeT2 = new javax.swing.JTextField();
        jTextFieldDomeZ2 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldBallHorizontalP = new javax.swing.JTextField();
        jLabelFishEye1 = new javax.swing.JLabel();
        jLabelDome2 = new javax.swing.JLabel();
        jTextFieldPointX2 = new javax.swing.JTextField();
        jRadioButtonPoint1 = new javax.swing.JRadioButton();
        jTextFieldPointY1 = new javax.swing.JTextField();
        jTextFieldDomeZ3 = new javax.swing.JTextField();
        jTextFieldDomeZ1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jRadioButtonPoint2 = new javax.swing.JRadioButton();
        jTextFieldPointX1 = new javax.swing.JTextField();
        jTextFieldBallHorizontalT = new javax.swing.JTextField();
        jButtonSetPoint3 = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldPointY3 = new javax.swing.JTextField();
        jRadioButtonPoint3 = new javax.swing.JRadioButton();
        jLabel31 = new javax.swing.JLabel();
        jButtonSetPoint1 = new javax.swing.JButton();
        jLabelDome1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldDomeP3 = new javax.swing.JTextField();
        jTextFieldPointY2 = new javax.swing.JTextField();
        jTextFieldDomeT1 = new javax.swing.JTextField();
        jLabelDome3 = new javax.swing.JLabel();
        jTextFieldDomeP1 = new javax.swing.JTextField();
        jLabelFishEye2 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabelDomeHorizon = new javax.swing.JLabel();
        jLabelFishEye3 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jButtonSave = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jButtonSetPoint2 = new javax.swing.JButton();

        jDialogPTZTrack.setTitle("PTZ跟踪参数");
        jDialogPTZTrack.setMinimumSize(new java.awt.Dimension(590, 324));
        jDialogPTZTrack.setModal(true);
        jDialogPTZTrack.setResizable(false);
        jDialogPTZTrack.setType(java.awt.Window.Type.POPUP);

        buttonGroupTrackMode.add(jRadioButtonPTZTrackAlways);
        jRadioButtonPTZTrackAlways.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonPTZTrackAlways.setText("一直跟踪当前目标");
        jRadioButtonPTZTrackAlways.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonPTZTrackAlwaysItemStateChanged(evt);
            }
        });

        buttonGroupTrackMode.add(jRadioButtonPTZTrackByTime);
        jRadioButtonPTZTrackByTime.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonPTZTrackByTime.setText("指定时间内跟踪当前目标。指定时间（T1）：");
        jRadioButtonPTZTrackByTime.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonPTZTrackByTimeItemStateChanged(evt);
            }
        });

        buttonGroupTrackMode.add(jRadioButtonPTZTrackNext);
        jRadioButtonPTZTrackNext.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonPTZTrackNext.setText("跟踪下一目标。当前目标最低跟踪时间（T2）：");
        jRadioButtonPTZTrackNext.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonPTZTrackNextItemStateChanged(evt);
            }
        });

        jSpinnerTrackTime.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jSpinnerTrackTime.setModel(new javax.swing.SpinnerNumberModel(Byte.valueOf((byte)10), Byte.valueOf((byte)1), Byte.valueOf((byte)60), Byte.valueOf((byte)1)));

        jLabelPTZTrackByTimeUnit.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelPTZTrackByTimeUnit.setText("秒（1-60）");

        jSpinnerTrackTimeMin.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jSpinnerTrackTimeMin.setModel(new javax.swing.SpinnerNumberModel(Byte.valueOf((byte)2), Byte.valueOf((byte)1), Byte.valueOf((byte)10), Byte.valueOf((byte)1)));

        jLabelPTZTrackNextUnit.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelPTZTrackNextUnit.setText("秒（1-10）");

        jButtonSavePTZTrack.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSavePTZTrack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok2.png"))); // NOI18N
        jButtonSavePTZTrack.setText("保  存");
        jButtonSavePTZTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSavePTZTrackActionPerformed(evt);
            }
        });

        jButtonCancel.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cancel.png"))); // NOI18N
        jButtonCancel.setText("取  消");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jLabelRemark.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelExplanation.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelExplanation.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelExplanation.setText("说明：");

        javax.swing.GroupLayout jDialogPTZTrackLayout = new javax.swing.GroupLayout(jDialogPTZTrack.getContentPane());
        jDialogPTZTrack.getContentPane().setLayout(jDialogPTZTrackLayout);
        jDialogPTZTrackLayout.setHorizontalGroup(
            jDialogPTZTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogPTZTrackLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jDialogPTZTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogPTZTrackLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonSavePTZTrack)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancel)
                        .addGap(69, 69, 69))
                    .addGroup(jDialogPTZTrackLayout.createSequentialGroup()
                        .addGroup(jDialogPTZTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jRadioButtonPTZTrackAlways, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jRadioButtonPTZTrackByTime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButtonPTZTrackNext, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jDialogPTZTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSpinnerTrackTimeMin, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                            .addComponent(jSpinnerTrackTime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jDialogPTZTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelPTZTrackByTimeUnit, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(jLabelPTZTrackNextUnit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogPTZTrackLayout.createSequentialGroup()
                .addComponent(jLabelExplanation, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 42, Short.MAX_VALUE))
        );
        jDialogPTZTrackLayout.setVerticalGroup(
            jDialogPTZTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogPTZTrackLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jRadioButtonPTZTrackAlways)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialogPTZTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonPTZTrackByTime, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinnerTrackTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPTZTrackByTimeUnit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialogPTZTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonPTZTrackNext)
                    .addComponent(jSpinnerTrackTimeMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPTZTrackNextUnit))
                .addGap(32, 32, 32)
                .addGroup(jDialogPTZTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSavePTZTrack)
                    .addComponent(jButtonCancel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialogPTZTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelExplanation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelRemark, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(55, Short.MAX_VALUE))
        );

        jDialogPTZTrackLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabelPTZTrackByTimeUnit, jRadioButtonPTZTrackByTime, jSpinnerTrackTime});

        jDialogPTZTrackLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabelPTZTrackNextUnit, jRadioButtonPTZTrackNext, jSpinnerTrackTimeMin});

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("鱼球联动跟踪配置");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jSplitPane1.setEnabled(false);

        jPanel1.setLayout(new java.awt.BorderLayout());

        panelFishEye.setBackground(new java.awt.Color(204, 255, 255));
        panelFishEye.setPreferredSize(new java.awt.Dimension(560, 488));
        panelFishEye.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelFishEyeMousePressed(evt);
            }
        });

        javax.swing.GroupLayout panelFishEyeLayout = new javax.swing.GroupLayout(panelFishEye);
        panelFishEye.setLayout(panelFishEyeLayout);
        panelFishEyeLayout.setHorizontalGroup(
            panelFishEyeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
        );
        panelFishEyeLayout.setVerticalGroup(
            panelFishEyeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 488, Short.MAX_VALUE)
        );

        jPanel1.add(panelFishEye, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        panelBall.setBackground(new java.awt.Color(204, 255, 255));
        panelBall.setPreferredSize(new java.awt.Dimension(560, 488));

        javax.swing.GroupLayout panelBallLayout = new javax.swing.GroupLayout(panelBall);
        panelBall.setLayout(panelBallLayout);
        panelBallLayout.setHorizontalGroup(
            panelBallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 564, Short.MAX_VALUE)
        );
        panelBallLayout.setVerticalGroup(
            panelBallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 488, Short.MAX_VALUE)
        );

        jPanel2.add(panelBall, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel2);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.PAGE_START);

        jPanel4.setLayout(new java.awt.GridLayout(1, 0));

        jPanel6.setLayout(new java.awt.BorderLayout());

        jLabelFishEyePoint.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelFishEyePoint.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelFishEyePoint.setText("鱼眼选点：");

        jLabel15.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("X:");

        jLabelFishEyeX.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelFishEyeX.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelFishEyeX.setText("1");

        jLabel18.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Y:");

        jLabelFishEyeY.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelFishEyeY.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelFishEyeY.setText("1");

        jLabelFishEyeIP.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(89, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFishEyePoint)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelFishEyeIP, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFishEyeX, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFishEyeY, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelFishEyePoint, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFishEyeIP, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabelFishEyeX, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabelFishEyeY, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 27, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel10, java.awt.BorderLayout.CENTER);

        jButtonSelFishEye.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSelFishEye.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/fishlogout.png"))); // NOI18N
        jButtonSelFishEye.setText("选择鱼眼");
        jButtonSelFishEye.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelFishEyeActionPerformed(evt);
            }
        });

        jButtonPTZTrackSet.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonPTZTrackSet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/setup2.png"))); // NOI18N
        jButtonPTZTrackSet.setText("PTZ跟踪参数");
        jButtonPTZTrackSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPTZTrackSetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonPTZTrackSet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonSelFishEye, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonSelFishEye)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPTZTrackSet)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel11, java.awt.BorderLayout.EAST);

        jPanel4.add(jPanel6);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabelDomeCamera.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelDomeCamera.setText("当前球机：");

        jLabelBallIP.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabel3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel3.setText("PAN");

        jLabelBallP.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelBallP.setText("1");

        jLabelSelectPointX1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelSelectPointX1.setText("TILT");

        jLabelBallT.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelBallT.setText("1");

        jLabelSelectPointX2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelSelectPointX2.setText("ZOOM");

        jLabelBallZ.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelBallZ.setText("1");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(77, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDomeCamera)
                    .addComponent(jLabelSelectPointX1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSelectPointX2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelBallIP, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelBallP, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelBallT, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelBallZ, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelBallIP, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDomeCamera, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabelBallP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelSelectPointX1)
                    .addComponent(jLabelBallT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelSelectPointX2)
                    .addComponent(jLabelBallZ))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabelSelectPointX1, jLabelSelectPointX2});

        jPanel5.add(jPanel9, java.awt.BorderLayout.CENTER);

        jButtonSelDome.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSelDome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/balllogout.png"))); // NOI18N
        jButtonSelDome.setText("选择球机");
        jButtonSelDome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelDomeActionPerformed(evt);
            }
        });

        jButtonPTZControl.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonPTZControl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ptz.png"))); // NOI18N
        jButtonPTZControl.setText("云台控制");
        jButtonPTZControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPTZControlActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonPTZControl, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                    .addComponent(jButtonSelDome, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonSelDome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonPTZControl)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel8, java.awt.BorderLayout.EAST);

        jPanel4.add(jPanel5);

        jButtonSetBallHorizontal.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSetBallHorizontal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/point0.png"))); // NOI18N
        jButtonSetBallHorizontal.setText("设置地平线   ");
        jButtonSetBallHorizontal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonSetBallHorizontal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetBallHorizontalActionPerformed(evt);
            }
        });

        jTextFieldPointX3.setEditable(false);
        jTextFieldPointX3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jTextFieldBallHorizontalZ.setEditable(false);
        jTextFieldBallHorizontalZ.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabel27.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel27.setText("Z");

        jLabel5.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel5.setText("P");

        jLabel13.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel13.setText("T");

        jLabel19.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel19.setText("Z");

        jLabel25.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel25.setText("X");

        jLabel16.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel16.setText("T");

        jTextFieldDomeT3.setEditable(false);
        jTextFieldDomeT3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabel14.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel14.setText("Z");

        jTextFieldDomeP2.setEditable(false);
        jTextFieldDomeP2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jTextFieldDomeT2.setEditable(false);
        jTextFieldDomeT2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jTextFieldDomeZ2.setEditable(false);
        jTextFieldDomeZ2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabel9.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel9.setText("X");

        jTextFieldBallHorizontalP.setEditable(false);
        jTextFieldBallHorizontalP.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelFishEye1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelFishEye1.setText("鱼眼");

        jLabelDome2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelDome2.setText("球机");

        jTextFieldPointX2.setEditable(false);
        jTextFieldPointX2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        buttonGroupPoint.add(jRadioButtonPoint1);
        jRadioButtonPoint1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonPoint1.setSelected(true);
        jRadioButtonPoint1.setText("标定点1");
        jRadioButtonPoint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPoint1ActionPerformed(evt);
            }
        });

        jTextFieldPointY1.setEditable(false);
        jTextFieldPointY1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jTextFieldDomeZ3.setEditable(false);
        jTextFieldDomeZ3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jTextFieldDomeZ1.setEditable(false);
        jTextFieldDomeZ1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabel12.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel12.setText("P");

        jLabel23.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel23.setText("P");

        jLabel24.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel24.setText("T");

        jLabel21.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel21.setText("Y");

        buttonGroupPoint.add(jRadioButtonPoint2);
        jRadioButtonPoint2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonPoint2.setText("标定点2");
        jRadioButtonPoint2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPoint2ActionPerformed(evt);
            }
        });

        jTextFieldPointX1.setEditable(false);
        jTextFieldPointX1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jTextFieldBallHorizontalT.setEditable(false);
        jTextFieldBallHorizontalT.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jButtonSetPoint3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSetPoint3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/point3.png"))); // NOI18N
        jButtonSetPoint3.setText("设置标定点 3");
        jButtonSetPoint3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonSetPoint3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetPoint3ActionPerformed(evt);
            }
        });

        jButtonExit.setFont(new java.awt.Font("微软雅黑", 0, 17)); // NOI18N
        jButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cancel.png"))); // NOI18N
        jButtonExit.setText("退出");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel6.setText("Z");

        jTextFieldPointY3.setEditable(false);
        jTextFieldPointY3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        buttonGroupPoint.add(jRadioButtonPoint3);
        jRadioButtonPoint3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonPoint3.setText("标定点3");
        jRadioButtonPoint3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPoint3ActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel31.setText("P");

        jButtonSetPoint1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSetPoint1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/point1.png"))); // NOI18N
        jButtonSetPoint1.setText("设置标定点 1");
        jButtonSetPoint1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonSetPoint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetPoint1ActionPerformed(evt);
            }
        });

        jLabelDome1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelDome1.setText("球机");

        jLabel7.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel7.setText("T");

        jTextFieldDomeP3.setEditable(false);
        jTextFieldDomeP3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jTextFieldPointY2.setEditable(false);
        jTextFieldPointY2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jTextFieldDomeT1.setEditable(false);
        jTextFieldDomeT1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelDome3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelDome3.setText("球机");

        jTextFieldDomeP1.setEditable(false);
        jTextFieldDomeP1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelFishEye2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelFishEye2.setText("鱼眼");

        jLabel10.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel10.setText("Y");

        jLabelDomeHorizon.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelDomeHorizon.setText("球机地平线位置");

        jLabelFishEye3.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelFishEye3.setText("鱼眼");

        jLabel29.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel29.setText("Y");

        jButtonSave.setFont(new java.awt.Font("微软雅黑", 1, 17)); // NOI18N
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok.png"))); // NOI18N
        jButtonSave.setText("保存");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel17.setText("X");

        jButtonSetPoint2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSetPoint2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/point2.png"))); // NOI18N
        jButtonSetPoint2.setText("设置标定点 2");
        jButtonSetPoint2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonSetPoint2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetPoint2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jRadioButtonPoint3, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                .addComponent(jRadioButtonPoint2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jRadioButtonPoint1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelFishEye1, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                            .addComponent(jLabelFishEye2, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                            .addComponent(jLabelFishEye3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabelDomeHorizon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldPointX2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldPointX3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextFieldBallHorizontalP, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldPointX1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTextFieldPointY2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelDome2, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldDomeP2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDomeT2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldDomeZ2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jButtonSetPoint2))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTextFieldPointY3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelDome3, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldDomeP3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDomeT3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldDomeZ3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jButtonSetPoint3)))
                        .addGap(51, 51, 51)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTextFieldPointY1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelDome1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldDomeP1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDomeT1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldDomeZ1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTextFieldBallHorizontalT, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldBallHorizontalZ, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonSetPoint1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonSetBallHorizontal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(209, 209, 209))))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel9});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel7});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextFieldDomeP1, jTextFieldDomeP2, jTextFieldDomeP3});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabelFishEye1, jLabelFishEye2, jLabelFishEye3});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabelDome1, jLabelDome2, jLabelDome3});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel25});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelDomeHorizon, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldBallHorizontalP, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldBallHorizontalT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldBallHorizontalZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelFishEye1)
                            .addComponent(jTextFieldPointX1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldPointY1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabelDome1)
                            .addComponent(jLabel12)
                            .addComponent(jTextFieldDomeP1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(jTextFieldDomeT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(jTextFieldDomeZ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonSetPoint1)
                            .addComponent(jRadioButtonPoint1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelFishEye2)
                            .addComponent(jTextFieldPointX2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldPointY2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel21)
                            .addComponent(jLabelDome2)
                            .addComponent(jLabel23)
                            .addComponent(jTextFieldDomeP2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)
                            .addComponent(jTextFieldDomeT2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(jTextFieldDomeZ2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonSetPoint2)
                            .addComponent(jRadioButtonPoint2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelFishEye3)
                                .addComponent(jTextFieldPointX3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldPointY3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel25)
                                .addComponent(jLabel29)
                                .addComponent(jLabelDome3)
                                .addComponent(jLabel31)
                                .addComponent(jTextFieldDomeP3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel24)
                                .addComponent(jTextFieldDomeT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel27)
                                .addComponent(jTextFieldDomeZ3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButtonSetPoint3))
                            .addComponent(jRadioButtonPoint3)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jButtonSetBallHorizontal)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonExit, jButtonSave});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel5, jLabel6, jLabel7, jLabelDomeHorizon, jTextFieldBallHorizontalP, jTextFieldBallHorizontalT, jTextFieldBallHorizontalZ});

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1129, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGap(0, 2, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 1124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 3, Short.MAX_VALUE)))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 312, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jScrollPane1.setViewportView(jPanel7);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSetPoint1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetPoint1ActionPerformed
        // TODO add your handling code here:
        setCBPoint(1,jTextFieldPointX1,jTextFieldPointY1,jTextFieldDomeP1,jTextFieldDomeT1,jTextFieldDomeZ1);
    }//GEN-LAST:event_jButtonSetPoint1ActionPerformed

    private void jButtonSetPoint2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetPoint2ActionPerformed
        // TODO add your handling code here:
        setCBPoint(2,jTextFieldPointX2,jTextFieldPointY2,jTextFieldDomeP2,jTextFieldDomeT2,jTextFieldDomeZ2);
    }//GEN-LAST:event_jButtonSetPoint2ActionPerformed

    private void jButtonSetPoint3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetPoint3ActionPerformed
        // TODO add your handling code here:
        setCBPoint(3,jTextFieldPointX3,jTextFieldPointY3,jTextFieldDomeP3,jTextFieldDomeT3,jTextFieldDomeZ3);
    }//GEN-LAST:event_jButtonSetPoint3ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        previewRPFishEye();
        previewRPBall();
        jSplitPane1.setDividerLocation(0.5);
        CommonParas.setMaxSize(this);
       
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        doBeforeClose();

        this.dispose();

    }//GEN-LAST:event_formWindowClosing

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        // TODO add your handling code here:
        try{
            //设置“跟踪设备参数结构“中的”球机设备通道参数结构体“
            DeviceParaBean deviceparaBean = channelRPBall.getDeviceparaBean();
            if (deviceparaBean == null) return;
            //设置联动跟踪设备参数 
            strTrackDevPara.struTrackDevChan = new HCNetSDK.NET_DVR_CHANNEL();
            //Arrays.copyOf方法很重要，必须把byte[]用0补齐。
            strTrackDevPara.struTrackDevChan.byAddress = Arrays.copyOf(deviceparaBean.getDVRIP().getBytes(),HCNetSDK.MAX_DOMAIN_NAME);//
            strTrackDevPara.struTrackDevChan.wDVRPort = Short.parseShort(deviceparaBean.getServerport());
            strTrackDevPara.struTrackDevChan.sUserName = Arrays.copyOf(deviceparaBean.getUsername().getBytes(),HCNetSDK.NAME_LEN);//
            strTrackDevPara.struTrackDevChan.sPassword = Arrays.copyOf(deviceparaBean.getPassword().getBytes(),HCNetSDK.PASSWD_LEN);//
            strTrackDevPara.struTrackDevChan.dwChannel = 1;
            
            //联动跟踪设备参数，即球机的设备通道参数结构体已经在前面设置过
            for (int i=bPointNum;i<HCNetSDK.MAX_CALIB_PT;i++){
                strTrackCalibrationPara.struCBPoint[i] = new HCNetSDK.NET_DVR_CB_POINT();
            }
            strTrackDevPara.struCalParam = strTrackCalibrationPara;//跟踪设备标定参数
            strTrackDevPara.byEnable = 1;//是否启用此跟踪设备：0-不启用，1-启用 
            strTrackDevPara.byTransMode = 0;//通讯方式：0-网络SDK，1-485串口 
            strTrackDevPara.dwSize = strTrackDevPara.size();//没想到这一步竟然很关键，不设置的话，会报参数错误。
            strTrackDevPara.write();
            Pointer lpTrackDevPara = strTrackDevPara.getPointer();
            boolean SetTrackSuccess = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(channelRPFishEye.getUserID(), HCNetSDK.NET_DVR_SET_TRACK_DEV_PARAM,
                                                                                channelRPFishEye.getChannel(), lpTrackDevPara, strTrackDevPara.size());
            strTrackDevPara.read();
            if (SetTrackSuccess) {
                channelRPFishEye.writePreviewLog(sSetFisheyeLinkDome);// "设置鱼球联动"
                CommonParas.showMessage(sSetFisheyeLinkDomeSucc, sFileName);// "已成功设置鱼球联动"
                //JOptionPane.showMessageDialog(null, "已成功设置鱼球联动");
            }else{
                channelRPFishEye.writePreviewErrorLog(sSetFisheyeLinkDomeFail);// "设置鱼球联动失败"
                CommonParas.showErrorMessage(sSetFisheyeLinkDomeFail, channelRPFishEye.getDeviceparaBean().getAnothername(), sFileName);// "设置鱼球联动失败"
//                TxtLogger.append(this.sFileName, "jButtonSaveActionPerformed()","保存设置鱼球联动参数时失败："
//                             + "\r\n                       错误码:" + CommonParas.hCNetSDK.NET_DVR_GetLastError());
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jButtonSaveActionPerformed()","保存设置鱼球联动参数时出现错误："
                             + "\r\n                       Exception:" + e.toString()
                                + "\r\n                       NET_DVR_TRACK_DEV_PARAM:" + strTrackDevPara.toString());
        }
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void panelFishEyeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelFishEyeMousePressed
        // TODO add your handling code here:
        POINT point = new POINT(evt.getX(), evt.getY());
        jLabelFishEyeX.setText("" + evt.getX());
        jLabelFishEyeY.setText("" + evt.getY());
        fDrawFunSetPoint.setPosititon(point);

    }//GEN-LAST:event_panelFishEyeMousePressed

    private void jButtonPTZControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPTZControlActionPerformed
        // TODO add your handling code here:
        if (channelRPBall == null) return;
        if (channelRPBall.getPreviewHandle().intValue() < 0) return;
        JDialogPTZControl dialog = new JDialogPTZControl(this, channelRPBall,jLabelBallP,jLabelBallT,jLabelBallZ);
        CommonParas.setAppropriateLocation(dialog, jButtonPTZControl);
        dialog.setVisible(true);
    }//GEN-LAST:event_jButtonPTZControlActionPerformed

    private void jButtonSetBallHorizontalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetBallHorizontalActionPerformed
        // TODO add your handling code here:
        if (channelRPBall == null) return;
        try{

            HCNetSDK.NET_DVR_PTZPOS strPTZPos = getPTZPos();
            jTextFieldBallHorizontalP.setText(Short.toString(strPTZPos.wPanPos));
            jTextFieldBallHorizontalT.setText(Short.toString(strPTZPos.wTiltPos));
            jTextFieldBallHorizontalZ.setText(Short.toString(strPTZPos.wZoomPos));
            strTrackCalibrationPara.struHorizonPtzPos = strPTZPos;//这种写法行不行？
//            strTrackCalibrationPara.struHorizonPtzPos.wPanPos = strPTZPos.wPanPos;
//            strTrackCalibrationPara.struHorizonPtzPos.wTiltPos = strPTZPos.wTiltPos;
//            strTrackCalibrationPara.struHorizonPtzPos.wZoomPos = strPTZPos.wZoomPos;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jButtonSetBallHorizontalActionPerformed()","设置球机水平线PTZ坐标时出现错误："
                             + "\r\n                       Exception:" + e.toString());
        }
    }//GEN-LAST:event_jButtonSetBallHorizontalActionPerformed

    private void jRadioButtonPoint1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPoint1ActionPerformed
        // TODO add your handling code here:
        refreshPointParas();
    }//GEN-LAST:event_jRadioButtonPoint1ActionPerformed

    private void jRadioButtonPoint2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPoint2ActionPerformed
        // TODO add your handling code here:
        refreshPointParas();
    }//GEN-LAST:event_jRadioButtonPoint2ActionPerformed

    private void jRadioButtonPoint3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPoint3ActionPerformed
        // TODO add your handling code here:
        refreshPointParas();
    }//GEN-LAST:event_jRadioButtonPoint3ActionPerformed

    private void jButtonSelDomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelDomeActionPerformed
        // TODO add your handling code here:
        String buttonText = jButtonSelDome.getText();
        if (buttonText.equals(sSelDome)) {// "选择球机"
            
            JDialogSelectDev dialogSelectDev = new JDialogSelectDev(this,true);
            //设置窗口显示位置
            CommonParas.setAppropriateLocation(dialogSelectDev, jButtonSelDome);
            dialogSelectDev.setVisible(true);
            //开始处理返回信息进行球机预览
            int IndexListDevicePara = dialogSelectDev.getIndexListDevicePara();
            if (IndexListDevicePara == -1) return;
            //判断是否是球机
            if(!HCNetSDKExpand.isIPD(CommonParas.getStrDeviceInfo(IndexListDevicePara, sFileName).wDevType)) return;
            
            if (CommonParas.setBasicParasOfChannelRealPlayer(channelRPBall, IndexListDevicePara,sFileName) > 0){
                if (previewRPBall()) {
                    jButtonSelDome.setText(sLogOutDome);// "注销球机"
                    jButtonSelDome.setIcon(ImageIconBufferPool.getInstance().getImageIcon("balllogout.png"));
                }
            }
        }else {
            if (CommonParas.stopPreviewOneChannel(channelRPBall, sFileName) > 0){
                jButtonSelDome.setText(sSelDome);// "选择球机"
                jButtonSelDome.setIcon(ImageIconBufferPool.getInstance().getImageIcon("balllogon.png"));
            }
        }
    }//GEN-LAST:event_jButtonSelDomeActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        // TODO add your handling code here:
        doBeforeClose();
        dispose();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonPTZTrackSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPTZTrackSetActionPerformed
        // TODO add your handling code here:
        try{
            
            if (strPTZTrackPara == null) strPTZTrackPara = new HCNetSDK.NET_DVR_PTZ_TRACK_PARAM();
            IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
            boolean bRet = false;

            strPTZTrackPara.write();
            Pointer lpPTZTrackPara = strPTZTrackPara.getPointer();
            bRet = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(channelRPFishEye.getUserID(), HCNetSDK.NET_DVR_GET_PTZ_TRACK_PARAM,
                                                  channelRPFishEye.getChannel(), lpPTZTrackPara, strPTZTrackPara.size(), ibrBytesReturned);
            strPTZTrackPara.read();

            if (!bRet) return;
            switch (strPTZTrackPara.byTrackMode){
                case HCNetSDK.PTZ_TRACK_MODE.PTZ_TRACK_MODE_ALWAYS:
                    jRadioButtonPTZTrackAlways.setSelected(true);
    //                jSpinnerTrackTime.setEnabled(false);
    //                jSpinnerTrackTimeMin.setEnabled(false);
                    break;
                case HCNetSDK.PTZ_TRACK_MODE.PTZ_TRACK_MODE_BYTIME:
                    jRadioButtonPTZTrackByTime.setSelected(true);
    //                jSpinnerTrackTime.setEnabled(true);
                    jSpinnerTrackTime.setValue(strPTZTrackPara.dwTrackTime);
    //                jSpinnerTrackTimeMin.setEnabled(false);
                    break;
                case HCNetSDK.PTZ_TRACK_MODE.PTZ_TRACK_MODE_NEXT:
                    jRadioButtonPTZTrackNext.setSelected(true);
    //                jSpinnerTrackTime.setEnabled(false);
    //                jSpinnerTrackTimeMin.setEnabled(true);
                    jSpinnerTrackTimeMin.setValue(strPTZTrackPara.dwTrackTime);
                    break;
            }
            CommonParas.setAppropriateLocation(jDialogPTZTrack, jButtonPTZTrackSet);
            jDialogPTZTrack.setVisible(true);

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jButtonPTZTrackSetActionPerformed()","获取鱼眼的PTZ跟踪参数时出现错误："
                                + "\r\n                       Exception:" + e.toString());
        }
 
    }//GEN-LAST:event_jButtonPTZTrackSetActionPerformed

    private void jRadioButtonPTZTrackAlwaysItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonPTZTrackAlwaysItemStateChanged
        // TODO add your handling code here:
        jSpinnerTrackTime.setEnabled(false);
        jSpinnerTrackTimeMin.setEnabled(false);
        jLabelRemark.setText("<html> <p>"+sPTZTrackParamRemark[0]+"</p></html>");
    }//GEN-LAST:event_jRadioButtonPTZTrackAlwaysItemStateChanged

    private void jRadioButtonPTZTrackByTimeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonPTZTrackByTimeItemStateChanged
        // TODO add your handling code here:
        jSpinnerTrackTime.setEnabled(true);
        jSpinnerTrackTimeMin.setEnabled(false);
        jLabelRemark.setText("<html> <p>"+sPTZTrackParamRemark[1]+"</p></html>");
    }//GEN-LAST:event_jRadioButtonPTZTrackByTimeItemStateChanged

    private void jRadioButtonPTZTrackNextItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonPTZTrackNextItemStateChanged
        // TODO add your handling code here:
        jSpinnerTrackTime.setEnabled(false);
        jSpinnerTrackTimeMin.setEnabled(true);
        jLabelRemark.setText("<html> <p>"+sPTZTrackParamRemark[2]+"</p></html>");
    }//GEN-LAST:event_jRadioButtonPTZTrackNextItemStateChanged

    private void jButtonSavePTZTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSavePTZTrackActionPerformed
        // TODO add your handling code here:
        try{
            strPTZTrackPara.byAutoTrackEnable = 0;
            if (jRadioButtonPTZTrackAlways.isSelected()) {
                strPTZTrackPara.byTrackMode = HCNetSDK.PTZ_TRACK_MODE.PTZ_TRACK_MODE_ALWAYS;
            }
            else if (jRadioButtonPTZTrackByTime.isSelected()) {
                strPTZTrackPara.byTrackMode = HCNetSDK.PTZ_TRACK_MODE.PTZ_TRACK_MODE_BYTIME;
                strPTZTrackPara.dwTrackTime = (byte)jSpinnerTrackTime.getValue();
            }
            else if (jRadioButtonPTZTrackNext.isSelected()) {
                strPTZTrackPara.byTrackMode = HCNetSDK.PTZ_TRACK_MODE.PTZ_TRACK_MODE_NEXT;
                strPTZTrackPara.dwTrackTime = (byte)jSpinnerTrackTimeMin.getValue();
            }
            else strPTZTrackPara.byTrackMode = HCNetSDK.PTZ_TRACK_MODE.PTZ_TRACK_MODE_ALWAYS;
            strPTZTrackPara.byLinkageTarget = 0;//联动目标：0- 默认，1- 球机1，2- 球机2，…，依次类推 

            strPTZTrackPara.dwSize = strPTZTrackPara.size();//没想到这一步竟然很关键，不设置的话，会报参数错误。
            strPTZTrackPara.write();
            Pointer lpPTZTrackPara = strPTZTrackPara.getPointer();
            boolean SetTrackSuccess = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(channelRPFishEye.getUserID(), HCNetSDK.NET_DVR_SET_PTZ_TRACK_PARAM,
                                                                                channelRPFishEye.getChannel(), lpPTZTrackPara, strPTZTrackPara.size());
            strPTZTrackPara.read();
            if (SetTrackSuccess) {
                JOptionPane.showMessageDialog(null, ssSetFisheyePTZTrackParaSucc);//"已成功设置鱼眼PTZ跟踪参数"
                channelRPFishEye.writePreviewLog( ssSetFisheyePTZTrackPara);// "设置鱼眼PTZ跟踪参数"
            }else{
                channelRPFishEye.writePreviewErrorLog(ssSetFisheyePTZTrackParaFail);// "设置鱼眼PTZ跟踪参数失败"
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "jButtonPTZTrackSetActionPerformed()","获取鱼眼的PTZ跟踪参数时出现错误："
                                + "\r\n                       Exception:" + e.toString());
        }
    }//GEN-LAST:event_jButtonSavePTZTrackActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        // TODO add your handling code here:
        jDialogPTZTrack.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonSelFishEyeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelFishEyeActionPerformed
        // TODO add your handling code here:
        String buttonText = jButtonSelFishEye.getText();
        if (buttonText.equals(sSelFishEye)) {// "选择鱼眼"
            
            JDialogSelectDev dialogSelectDev = new JDialogSelectDev(this,true);
            //设置窗口显示位置
            CommonParas.setAppropriateLocation(dialogSelectDev, jButtonSelFishEye);
            dialogSelectDev.setVisible(true);
            //开始处理返回信息进行鱼眼预览
            int IndexListDevicePara = dialogSelectDev.getIndexListDevicePara();
            if (IndexListDevicePara == -1) return;
            //判断是否是球机
            if(!HCNetSDKExpand.isFishEye(CommonParas.getStrDeviceInfo(IndexListDevicePara, sFileName).wDevType)) return;
            
            if (CommonParas.setBasicParasOfChannelRealPlayer(channelRPFishEye, IndexListDevicePara,sFileName) > 0){
                if (previewRPFishEye()) {
                    jButtonSelFishEye.setText(sLogOutFishEye );//"注销鱼眼"
                    jButtonSelFishEye.setIcon(ImageIconBufferPool.getInstance().getImageIcon("fishlogout.png"));
                }
            }
        }else {
            if (CommonParas.stopPreviewOneChannel(channelRPFishEye, sFileName) > 0){
                jButtonSelFishEye.setText(sSelFishEye);//"选择鱼眼"
                jButtonSelFishEye.setIcon(ImageIconBufferPool.getInstance().getImageIcon("fishlogon.png"));
            }
        }
    }//GEN-LAST:event_jButtonSelFishEyeActionPerformed
    
    /**
        * 函数:      getTrackDevPara
        * 函数描述:  获取鱼眼的跟踪设备参数结构值
    */
    private void getTrackDevPara(){
        if (channelRPFishEye == null) return ;
        try {
            IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
            boolean bRet = false;

            strTrackDevPara.write();
            Pointer lpTrackDevPara = strTrackDevPara.getPointer();
            boolean GetTrackSuccess = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(channelRPFishEye.getUserID(), HCNetSDK.NET_DVR_GET_TRACK_DEV_PARAM,
                                                  channelRPFishEye.getChannel(), lpTrackDevPara, strTrackDevPara.size(), ibrBytesReturned);
            strTrackDevPara.read();

            if (!GetTrackSuccess) return;
            
            //设置球机水平、3个标定点和相对应得球机PTZ值、FDrawFunSetPoint中的3个转换坐标值（鱼眼）、球机预览对象的参数设置（地址、端口号等）
            strTrackCalibrationPara = strTrackDevPara.struCalParam;
            //设置球机水平PTZ
            jTextFieldBallHorizontalP.setText(Short.toString(strTrackCalibrationPara.struHorizonPtzPos.wPanPos));
            jTextFieldBallHorizontalT.setText(Short.toString(strTrackCalibrationPara.struHorizonPtzPos.wTiltPos));
            jTextFieldBallHorizontalZ.setText(Short.toString(strTrackCalibrationPara.struHorizonPtzPos.wZoomPos));
            //3个标定点和相对应得球机PTZ值
            getCBPoint(1,jTextFieldPointX1,jTextFieldPointY1,jTextFieldDomeP1,jTextFieldDomeT1,jTextFieldDomeZ1);
            getCBPoint(2,jTextFieldPointX2,jTextFieldPointY2,jTextFieldDomeP2,jTextFieldDomeT2,jTextFieldDomeZ2);
            if (bPointNum > 2) getCBPoint(3,jTextFieldPointX3,jTextFieldPointY3,jTextFieldDomeP3,jTextFieldDomeT3,jTextFieldDomeZ3);
            //FDrawFunSetPoint中的3个转换坐标值（鱼眼）
            fDrawFunSetPoint.setPosititon(new POINT(Integer.parseInt(jTextFieldPointX1.getText()), 
                    Integer.parseInt(jTextFieldPointY1.getText())),1);
            fDrawFunSetPoint.setPosititon(new POINT(Integer.parseInt(jTextFieldPointX2.getText()), 
                    Integer.parseInt(jTextFieldPointY2.getText())),2);
            if (bPointNum > 2) fDrawFunSetPoint.setPosititon(new POINT(Integer.parseInt(jTextFieldPointX3.getText()), 
                    Integer.parseInt(jTextFieldPointY3.getText())),3);
            //球机预览对象的设备参数设置
            String IP = new String(strTrackDevPara.struTrackDevChan.byAddress).trim();
            CommonParas.setBasicParasOfChannelRealPlayer(channelRPBall,IP,null,sFileName);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getTrackDevPara()","获取鱼眼的跟踪设备参数结构值时出现错误："
                                + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      previewRPFishEye
        * 函数描述:  预览鱼眼
    */
    private boolean previewRPFishEye(){
        try{
            if (channelRPFishEye != null) {
                channelRPFishEye.setPanelRealplay(panelFishEye);

                channelRPFishEye.setChannel(new NativeLong(1));
                int RetrunCode = CommonParas.previewOneChannel(this.channelRPFishEye, this.sFileName);
                //回调函数，在鱼眼预览画面标注标定点
                if (RetrunCode > 0){
                    CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRPFishEye.getPreviewHandle(), fDrawFunSetPoint, 0);
                    jButtonSelFishEye.setText(sLogOutFishEye);//"注销鱼眼"
                    jLabelFishEyeIP.setText(channelRPFishEye.getDeviceparaBean().getDVRIP());
                    return true;
                }
                
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "previewRPFishEye()","预览鱼眼时出现错误："
                                + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /**
        * 函数:      previewRPBall
        * 函数描述:  预览球机
    */
    private boolean previewRPBall(){
        try{
            if (channelRPBall != null) {
                if (channelRPBall.getPreviewHandle().intValue() != -1) 
                    CommonParas.stopPreviewOneChannel(channelRPBall, sFileName);
                channelRPBall.setPanelRealplay(panelBall);
                channelRPBall.setChannel(new NativeLong(1));
    //            channelRPBall.setPreviewHandle(null);
                int RetrunCode2 = CommonParas.previewOneChannel(this.channelRPBall, this.sFileName);
                //回调函数，在球机预览画面标注中心点
                if (RetrunCode2 > 0){
                    CommonParas.hCNetSDK.NET_DVR_RigisterDrawFun(channelRPBall.getPreviewHandle(), fDrawFunSetBallCenter, 0);
                    jButtonSelDome.setText(sLogOutDome);// "注销球机"
                    jLabelBallIP.setText(channelRPBall.getDeviceparaBean().getDVRIP());
                    return true;
                }
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "previewRPBall()","预览球机时出现错误："
                                + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /**
        * 函数:      getCBPoint
        * 函数描述:  获取鱼球联动标定点
        * @param Index  标定点索引
        * @param JTextField 更新多个JTextField的值
        * @return boolean 操作成功返回true，否则false
    */
    private void getCBPoint(int Index,JTextField jTextFieldPointX,JTextField jTextFieldPointY,
            JTextField jTextFieldBallP,JTextField jTextFieldBallT,JTextField jTextFieldBallZ){
        
        try {
            if (Index < 1) return;
            float FishWidth = (float)panelFishEye.getWidth();
            float FishHight = (float)panelFishEye.getHeight();
            float fX = strTrackCalibrationPara.struCBPoint[Index -1].struPoint.fX;
            float fY = strTrackCalibrationPara.struCBPoint[Index -1].struPoint.fY;
        
            jTextFieldPointX.setText(Integer.toString((int)(FishWidth * fX)));
            jTextFieldPointY.setText(Integer.toString((int)(FishHight * fY)));
            
            jTextFieldBallP.setText(Short.toString(strTrackCalibrationPara.struCBPoint[Index -1].struPtzPos.wPanPos));
            jTextFieldBallT.setText(Short.toString(strTrackCalibrationPara.struCBPoint[Index -1].struPtzPos.wTiltPos));
            jTextFieldBallZ.setText(Short.toString(strTrackCalibrationPara.struCBPoint[Index -1].struPtzPos.wZoomPos));
            
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getCBPoint()","获取鱼球联动标定点"+Index+"时出现错误："
                                + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      setCBPoint
        * 函数描述:  设置鱼球联动标定点
        * @param Index  标定点索引
        * @param JTextField 更新多个JTextField的值
        * @return boolean 操作成功返回true，否则false
    */
    private void setCBPoint(int Index,JTextField jTextFieldPointX,JTextField jTextFieldPointY,
            JTextField jTextFieldBallP,JTextField jTextFieldBallT,JTextField jTextFieldBallZ){
        
        try {
            if (Index < 1) return;
            jTextFieldPointX.setText(jLabelFishEyeX.getText());
            jTextFieldPointY.setText(jLabelFishEyeY.getText());
            HCNetSDK.NET_DVR_PTZPOS strPTZPos = getPTZPos();//返回球机当前位置配置结构体
            jTextFieldBallP.setText(Short.toString(strPTZPos.wPanPos));
            jTextFieldBallT.setText(Short.toString(strPTZPos.wTiltPos));
            jTextFieldBallZ.setText(Short.toString(strPTZPos.wZoomPos));
            //球机输入的PTZ坐标 
            strTrackCalibrationPara.struCBPoint[Index -1] = new HCNetSDK.NET_DVR_CB_POINT();
            strTrackCalibrationPara.struCBPoint[Index -1].struPtzPos = strPTZPos;//这种写法行不行？
            
            //标定点，鱼眼，其值归一化到0-1 
            float FishWidth = (float)panelFishEye.getWidth();
            float FishHight = (float)panelFishEye.getHeight();
            float fX = Float.parseFloat(jLabelFishEyeX.getText()) / FishWidth;
            float fY = Float.parseFloat(jLabelFishEyeY.getText()) / FishHight;
            strTrackCalibrationPara.struCBPoint[Index -1].struPoint.fX = fX;
            strTrackCalibrationPara.struCBPoint[Index -1].struPoint.fY = fY;
//            JOptionPane.showMessageDialog(rootPane, "fX = " + fX + ";fY = " + fY);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "setCBPoint()","设置鱼球联动标定点"+Index+"时出现错误："
                                + "\r\n                       Exception:" + e.toString());
        }
    }
    
    /**
	 * 函数:      getPTZPos
         * 函数描述:  返回球机当前位置配置结构体
         * @return NET_DVR_PTZPOS
    */
    public HCNetSDK.NET_DVR_PTZPOS getPTZPos(){
        try{
            NativeLong UserID = channelRPBall.getUserID();
            if (UserID == null || UserID.intValue() < 0) return null;
            IntByReference ibrBytesReturned = new IntByReference(0);//获取设备的配置信息。

            boolean bRet = false;
            HCNetSDK.NET_DVR_PTZPOS strPTZPos = new HCNetSDK.NET_DVR_PTZPOS();
            strPTZPos.write();
            Pointer lpStrPTZPos = strPTZPos.getPointer();
            bRet = CommonParas.hCNetSDK.NET_DVR_GetDVRConfig(UserID,HCNetSDK.NET_DVR_GET_PTZPOS,channelRPBall.getChannel(),lpStrPTZPos,strPTZPos.size(),ibrBytesReturned);
            strPTZPos.read();
            jLabelBallP.setText(""+strPTZPos.wPanPos);
            jLabelBallT.setText(""+strPTZPos.wTiltPos);
            jLabelBallZ.setText(""+strPTZPos.wZoomPos);
            return strPTZPos;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getPTZPos()","系统在对所有登录后没有注销的设备进行注销过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            return null;
        }
   
    }
    /**
	 * 函数:      refreshPointParas
         * 函数描述:  设置标定点界面按钮和参数状态
    */
    private void refreshPointParas(){
        if (jRadioButtonPoint1.isSelected()){
            jButtonSetPoint1.setEnabled(true);
            jButtonSetPoint2.setEnabled(false);
            jButtonSetPoint3.setEnabled(false);
            IndexOfPoint = 1;
        }
        else if (jRadioButtonPoint2.isSelected()){
            jButtonSetPoint1.setEnabled(false);
            jButtonSetPoint2.setEnabled(true);
            jButtonSetPoint3.setEnabled(false);
            IndexOfPoint = 2;
        }
        else {
            jButtonSetPoint1.setEnabled(false);
            jButtonSetPoint2.setEnabled(false);
            jButtonSetPoint3.setEnabled(true);
            IndexOfPoint = 3;
        }
        fDrawFunSetPoint.setIndexOfPoint(IndexOfPoint);
    }
    
    private void doBeforeClose(){
//        if (!ifTrackSetupSuccess){
//            if ((JOptionPane.showConfirmDialog(this, "还未设置鱼球联动，是否要继续退出？",  
//                "提醒",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)) return;
//        }
        CommonParas.stopPreviewOneChannel(channelRPFishEye,sFileName);
        CommonParas.stopPreviewOneChannel(channelRPBall,sFileName);
    }
    
    /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    private void modifyLocales(){
        
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作
        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        //信息显示
        sPTZTrackParamRemark[0] = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sPTZTrackParamRemark0");  //跟踪目标消失后，跟踪下一目标或者回到初始位置。
        sPTZTrackParamRemark[1] = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sPTZTrackParamRemark1");  //跟踪时间（T1）内目标消失，则跟踪下一个目标或者回到初始位置。
        sPTZTrackParamRemark[2] = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sPTZTrackParamRemark2");  //当前目标跟踪时间超过（T2）秒后，则跟踪出现的下一个目标或者回到初始位置。
        sTitle = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sTitle");  //鱼球联动跟踪配置
        sTitlePTZTrack = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sTitlePTZTrack");  //PTZ跟踪参数
        sSelFishEye = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sSelFishEye");  //选择鱼眼
        sLogOutFishEye = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sLogOutFishEye");  //注销鱼眼
        sSelDome = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sSelDome");  //选择球机
        sLogOutDome = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sLogOutDome");  //注销球机
        sFishEye = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sFishEye");  //鱼眼
        sDome = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sDome");  //球机
        sSave = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sSave");  //保存
        sSetFisheyeLinkDome = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sSetFisheyeLinkDome");  //设置鱼球联动
        sSetFisheyeLinkDomeSucc = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sSetFisheyeLinkDomeSucc");  //已成功设置鱼球联动
        sSetFisheyeLinkDomeFail = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.sSetFisheyeLinkDomeFail");  //设置鱼球联动失败
        ssSetFisheyePTZTrackPara = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.ssSetFisheyePTZTrackPara");  //设置鱼眼PTZ跟踪参数
        ssSetFisheyePTZTrackParaSucc = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.ssSetFisheyePTZTrackParaSucc");  //已成功设置鱼眼PTZ跟踪参数
        ssSetFisheyePTZTrackParaFail = Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.ssSetFisheyePTZTrackParaFail");  //设置鱼眼PTZ跟踪参数失败
        //标签和按钮显示"<html> <p>"+sPTZTrackParamRemark[1]+"</p></html>"
        jRadioButtonPTZTrackAlways.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jRadioButtonPTZTrackAlways"));  //一直跟踪当前目标
        jRadioButtonPTZTrackByTime.setText( "<html> <p>"+Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jRadioButtonPTZTrackByTime")+"</p></html>");  //指定时间内跟踪当前目标。指定时间（T1）：
        jRadioButtonPTZTrackNext.setText(  "<html> <p>"+Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jRadioButtonPTZTrackNext")+"</p></html>");  //跟踪下一目标。当前目标最低跟踪时间（T2）：
        jLabelPTZTrackByTimeUnit.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jLabelPTZTrackByTimeUnit"));  //秒（1-60）
        jLabelPTZTrackNextUnit.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jLabelPTZTrackNextUnit"));  //秒（1-10）
        jButtonCancel.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jButtonCancel"));  //取消
        jLabelExplanation.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jLabelExplanation"));  //说明：
        jLabelFishEyePoint.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jLabelFishEyePoint"));  //鱼眼选点：
        jButtonPTZTrackSet.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jButtonPTZTrackSet"));  //PTZ跟踪参数
        jLabelDomeCamera.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jLabelDomeCamera"));  //当前球机：
        jButtonPTZControl.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jButtonPTZControl"));  //云台控制
        jLabelDomeHorizon.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jLabelDomeHorizon"));  //球机地平线位置
        jButtonSetBallHorizontal.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jButtonSetBallHorizontal"));  //设置地平线   
        jRadioButtonPoint1.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jRadioButtonPoint1"));  //标定点1
        jRadioButtonPoint2.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jRadioButtonPoint2"));  //标定点2
        jRadioButtonPoint3.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jRadioButtonPoint3"));  //标定点3
        jButtonSetPoint1.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jButtonSetPoint1"));  //设置标定点 1
        jButtonSetPoint2.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jButtonSetPoint2"));  //设置标定点 2
        jButtonSetPoint3.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jButtonSetPoint3"));  //设置标定点 3
        jButtonExit.setText( Locales.getString("ClassStrings", "JFrameFishEyeTrackSetup.jButtonExit"));  //退出

        //国际化后设置
        this.setTitle(sTitle);  //鱼球联动跟踪配置
        jDialogPTZTrack.setTitle(sTitlePTZTrack);  //PTZ跟踪参数
        jLabelFishEye1.setText(sFishEye);  //鱼眼
        jLabelFishEye2.setText(sFishEye);  //鱼眼
        jLabelFishEye3.setText(sFishEye);  //鱼眼
        jLabelDome1.setText(sDome);  //球机
        jLabelDome2.setText(sDome);  //球机
        jLabelDome3.setText(sDome);  //球机
        jButtonSelFishEye.setText(sSelFishEye);  //选择鱼眼
        jButtonSelDome.setText(sSelDome);  //选择球机
        jButtonSave.setText(sSave);  //保存
        jButtonSavePTZTrack.setText(sSave);  //保存

    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupPoint;
    private javax.swing.ButtonGroup buttonGroupTrackMode;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonPTZControl;
    private javax.swing.JButton jButtonPTZTrackSet;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSavePTZTrack;
    private javax.swing.JButton jButtonSelDome;
    private javax.swing.JButton jButtonSelFishEye;
    private javax.swing.JButton jButtonSetBallHorizontal;
    private javax.swing.JButton jButtonSetPoint1;
    private javax.swing.JButton jButtonSetPoint2;
    private javax.swing.JButton jButtonSetPoint3;
    private javax.swing.JDialog jDialogPTZTrack;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelBallIP;
    private javax.swing.JLabel jLabelBallP;
    private javax.swing.JLabel jLabelBallT;
    private javax.swing.JLabel jLabelBallZ;
    private javax.swing.JLabel jLabelDome1;
    private javax.swing.JLabel jLabelDome2;
    private javax.swing.JLabel jLabelDome3;
    private javax.swing.JLabel jLabelDomeCamera;
    private javax.swing.JLabel jLabelDomeHorizon;
    private javax.swing.JLabel jLabelExplanation;
    private javax.swing.JLabel jLabelFishEye1;
    private javax.swing.JLabel jLabelFishEye2;
    private javax.swing.JLabel jLabelFishEye3;
    private javax.swing.JLabel jLabelFishEyeIP;
    private javax.swing.JLabel jLabelFishEyePoint;
    private javax.swing.JLabel jLabelFishEyeX;
    private javax.swing.JLabel jLabelFishEyeY;
    private javax.swing.JLabel jLabelPTZTrackByTimeUnit;
    private javax.swing.JLabel jLabelPTZTrackNextUnit;
    private javax.swing.JLabel jLabelRemark;
    private javax.swing.JLabel jLabelSelectPointX1;
    private javax.swing.JLabel jLabelSelectPointX2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButtonPTZTrackAlways;
    private javax.swing.JRadioButton jRadioButtonPTZTrackByTime;
    private javax.swing.JRadioButton jRadioButtonPTZTrackNext;
    private javax.swing.JRadioButton jRadioButtonPoint1;
    private javax.swing.JRadioButton jRadioButtonPoint2;
    private javax.swing.JRadioButton jRadioButtonPoint3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerTrackTime;
    private javax.swing.JSpinner jSpinnerTrackTimeMin;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField jTextFieldBallHorizontalP;
    private javax.swing.JTextField jTextFieldBallHorizontalT;
    private javax.swing.JTextField jTextFieldBallHorizontalZ;
    private javax.swing.JTextField jTextFieldDomeP1;
    private javax.swing.JTextField jTextFieldDomeP2;
    private javax.swing.JTextField jTextFieldDomeP3;
    private javax.swing.JTextField jTextFieldDomeT1;
    private javax.swing.JTextField jTextFieldDomeT2;
    private javax.swing.JTextField jTextFieldDomeT3;
    private javax.swing.JTextField jTextFieldDomeZ1;
    private javax.swing.JTextField jTextFieldDomeZ2;
    private javax.swing.JTextField jTextFieldDomeZ3;
    private javax.swing.JTextField jTextFieldPointX1;
    private javax.swing.JTextField jTextFieldPointX2;
    private javax.swing.JTextField jTextFieldPointX3;
    private javax.swing.JTextField jTextFieldPointY1;
    private javax.swing.JTextField jTextFieldPointY2;
    private javax.swing.JTextField jTextFieldPointY3;
    private java.awt.Panel panelBall;
    private java.awt.Panel panelFishEye;
    // End of variables declaration//GEN-END:variables

    private String sTitle = "鱼球联动跟踪配置";
    private String sTitlePTZTrack = "PTZ跟踪参数";
    private String sSelFishEye = "选择鱼眼";
    private String sLogOutFishEye = "注销鱼眼";
    private String sSelDome = "选择球机";
    private String sLogOutDome = "注销球机";
    private String sFishEye = "鱼眼";
    private String sDome = "球机";
    private String sSave = "保存";
    private String sSetFisheyeLinkDome = "设置鱼球联动";
    private String sSetFisheyeLinkDomeSucc = "已成功设置鱼球联动";
    private String sSetFisheyeLinkDomeFail = "设置鱼球联动失败";
    private String ssSetFisheyePTZTrackPara = "设置鱼眼PTZ跟踪参数";
    private String ssSetFisheyePTZTrackParaSucc = "已成功设置鱼眼PTZ跟踪参数";
    private String ssSetFisheyePTZTrackParaFail = "设置鱼眼PTZ跟踪参数失败";

    /**
	 * 函数:      FDrawFunSet
         * 函数描述:  设置表定点位置
    */
    class FDrawFunSetPoint implements HCNetSDK.FDrawFun{

        private ArrayList<POINT> listPositions = new ArrayList<POINT>();//存储标定点的列表
        private final int iPointNum;//标定点个数
        private int IndexOfPoint = 1;//当前要设置的标定点索引
        
        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser) {
//            if ()
            gDi.SetTextColor(hDc, 0x0000ff);
            for (int i=0; i < listPositions.size(); i++)
            {
                  String Text = Integer.toString(i+1);
                  POINT Position = listPositions.get(i);
                // 设备描述表句柄// 字符串的开始位置 x坐标// 字符串的开始位置 y坐标// 字符串//字串中要描绘的字符数量
                boolean DrawText = gDi.TextOut(hDc,Position.x,Position.y, Text,Text.length());
//                if (DrawText) System.out.println("Device output characters successfully");
            }
            gDi.SetBkMode(hDc,GDI32.TRANSPARENT);  
//            System.out.println("设备输出字符成功" +IndexOfPoint);
//            IndexOfPoint++;
            
        }
    
        public FDrawFunSetPoint(int PointNum){
            if (PointNum<1) this.iPointNum = 1;
            else this.iPointNum = PointNum;
            for (int i=0;i<iPointNum;i++) {
                POINT tmpPoint = new POINT(i+1,i+1);
                listPositions.add(tmpPoint);
            }
        }
        /**
         * @return the Posititon
         */
        public POINT getPosititon(int Index) {
            if (Index < 1) return null;
            return listPositions.get(Index);
        }

        /**
         * @param Posititon the Posititon to set
         */
        public void setPosititon(POINT Posititon) {
            if (Posititon == null) return;
            POINT Position2 = listPositions.get(IndexOfPoint - 1);
            Position2.x = Posititon.x;
            Position2.y = Posititon.y;

        }
        public void setPosititon(POINT Posititon,int IndexOfList) {
            if (Posititon == null) return;
            POINT Position2 = listPositions.get(IndexOfList - 1);
            Position2.x = Posititon.x;
            Position2.y = Posititon.y;
        }
        /**
         * @return the PointNum
         */
        public int getPointNum() {
            return iPointNum;
        }

        /**
         * @return the IndexOfPoint
         */
        public int getIndexOfPoint() {
            return IndexOfPoint;
        }

        /**
         * @param IndexOfPoint the IndexOfPoint to set
         */
        public void setIndexOfPoint(int IndexOfPoint) {
            this.IndexOfPoint = IndexOfPoint;
        }

        /**
         * @param PointNum the PointNum to set
         */
//        public void setPointNum(int PointNum) {
//            this.PointNum = PointNum;
//        }

    }
    /**
        * 函数:      FDrawFunSetBallCenter
        * 函数描述:  设置球机中心点位置
    */
    class FDrawFunSetBallCenter implements HCNetSDK.FDrawFun{
        POINT CenterPosition;
        
        @Override
        public void invoke(NativeLong lRealHandle, W32API.HDC hDc, int dwUser) {
            gDi.SetTextColor(hDc, 0x0000ff);
            boolean DrawText = gDi.TextOut(hDc,CenterPosition.x,CenterPosition.y, "+",1);
            gDi.SetBkMode(hDc,GDI32.TRANSPARENT);  
        }
        
        //构造器函数
        public FDrawFunSetBallCenter(POINT CenterPosition){
            this.CenterPosition = CenterPosition;
        }
    }
    
    
    class DragPicListener implements MouseInputListener{ 
      
        Point point=new Point(0,0); //坐标点
        Component Pic;
        public DragPicListener(Component Pic){
            this.Pic = Pic;
        }
        @Override
        public void mousePressed(MouseEvent e)
        {
            point=SwingUtilities.convertPoint(Pic,e.getPoint(),Pic.getParent()); //得到当前坐标点
         }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            Point newPoint=SwingUtilities.convertPoint(Pic,e.getPoint(),Pic.getParent()); //转换坐标系统
            Pic.setLocation(Pic.getX()+(newPoint.x-point.x),Pic.getY()+(newPoint.y-point.y)); //设置标签图片的新位置
            point=newPoint; //更改坐标点
         }

        @Override
        public void mouseReleased(MouseEvent e){}

        @Override
        public void mouseEntered(MouseEvent e){}

        @Override
        public void mouseExited(MouseEvent e){}

        @Override
        public void mouseClicked(MouseEvent e){}

        @Override
        public void mouseMoved(MouseEvent e){}
    }
    
        


}
