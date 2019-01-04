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
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import jyms.data.TxtLogger;
import jyms.tools.DomXML;
import jyms.tools.ImageIconBufferPool;
import jyms.ui.ScrollBarUI_White;
import jyms.ui.ScrollPaneUI_White;
import jyms.ui.SliderUI_White;
import jyms.ui.TabbedPaneUI_Small;
import jyms.ui.TableHeaderUI_White;
import jyms.ui.TableUI_White;

/**
 *
 * @author John
 */
public class JDialogPTZControl2 extends javax.swing.JDialog {
    
    private final String sFileName = this.getClass().getName() + ".java";
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
    private JLabel jLabelPTZ_P = new JLabel();//从父窗口传递进来的JLabel，改变其P值
    private JLabel jLabelPTZ_T = new JLabel();//从父窗口传递进来的JLabel，改变其T值
    private JLabel jLabelPTZ_Z = new JLabel();//从父窗口传递进来的JLabel，改变其Z值
    private boolean bLabelPTZ = false;//是否需要进行PTZ的参数显示
    private ChannelRealPlayer channelRPBall;
    private DefaultTableModel presetTableModel;
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
    
    //为日志填写做准备
    int[] iCommands = new int[]{2,3,4,5,6,7,8,9,11,12,13,14,15,16,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,3313};
    String[] sCommandMeanings = new String[]{"灯光电源","雨刷开关","风扇开关","加热器开关","辅助设备开关","辅助设备开关","设置预置点",
                    "清除预置点","焦距变大(倍率变大)","焦距变小(倍率变小)","焦点前调","焦点后调","光圈扩大","光圈缩小","云台上仰","云台下俯","云台左转","云台右转",
                    "云台上仰和左转","云台上仰和右转","云台下俯和左转","云台下俯和右转","云台左右自动扫描","将预置点加入巡航序列","设置巡航点停顿时间","设置巡航速度",
                    "将预置点从巡航序列中删除","开始记录轨迹","停止记录轨迹","开始轨迹","开始巡航","停止巡航","转到预置点","云台下俯和焦距变大(倍率变大)",
                    "云台下俯和焦距变小(倍率变小)","云台左转和焦距变大(倍率变大)","云台左转和焦距变小(倍率变小)","云台右转和焦距变大(倍率变大)",
                    "云台右转和焦距变小(倍率变小)","云台上仰和左转和焦距变大(倍率变大)","云台上仰和左转和焦距变小(倍率变小)","云台上仰和右转和焦距变大(倍率变大)",
                    "云台上仰和右转和焦距变小(倍率变小)","云台下俯和左转和焦距变大(倍率变大)","云台下俯和左转和焦距变小(倍率变小)","云台下俯和右转和焦距变大(倍率变大)",
                    "云台下俯和右转和焦距变小(倍率变小)","云台上仰和焦距变大(倍率变大)","云台上仰和焦距变小(倍率变小)","快球云台花样扫描"};
    ArrayList<Integer> listCommands = new ArrayList<>();


    /**
        * Creates new form JDialogPTZControl
        * @param parent
        * @param channelRP
        * @param jLabelPTZP
        * @param jLabelPTZT
        * @param jLabelPTZZ
     */
    public JDialogPTZControl2(java.awt.Window parent, ChannelRealPlayer channelRP,JLabel jLabelPTZP,JLabel jLabelPTZT,JLabel jLabelPTZZ) {
        super(parent);
        initComponents();

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
    public JDialogPTZControl2(java.awt.Frame parent, boolean modal, ChannelRealPlayer channelRP) {
//        this(parent, modal,lRealHandle,null,null,null) ;
        super(parent, modal);
        
        initComponents();
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
        
        presetTableModel = CommonParas.initialNormalOneEditTableModel(new String[]{"ID", "预置点"});
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
        cruisePointTableModel = CommonParas.initialNormalNoEditTableModel(new String[]{"ID", "巡航点","时速"});
        jTableCruise.setModel(cruisePointTableModel);
        jTableCruise.setRowHeight(30);
        
        setTableColWidth();
        
        refreshPTZStrCfgAbility();
        if (jComboBoxCruise.getItemCount() > 0) 
            refreshCruiseTableData(1);
        
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

        //巡航路径
        CommonParas.setJButtonUnDecorated(jButtonRunSeq);
        CommonParas.setJButtonUnDecorated(jButtonAddPoint);
        CommonParas.setJButtonUnDecorated(jButtonDelPoint);
        
        //云台轨迹
        CommonParas.setJButtonUnDecorated(jButtonRunCruise);
        CommonParas.setJButtonUnDecorated(jButtonMemCruise);
        CommonParas.setJButtonUnDecorated(jButtonDeleteAllCruise);
        CommonParas.setJButtonUnDecorated(jButtonDeleteCruise);
 
        
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
        
        jSliderSpeed.setUI(new SliderUI_White(jSliderSpeed));
       

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
	 * 函数:      modifyPresetName
         * 函数描述:  修改预置点名称
    */
    private void modifyPresetName(int Row, int Col, String OldPresetName, String NewPresetName){
        try{
            NativeLong UserID = channelRPBall.getUserID();
            int Channel = 1;
            if (UserID.intValue() < 0) return;
            HCNetSDK.NET_DVR_PRESET_NAME StruPresetName = new HCNetSDK.NET_DVR_PRESET_NAME();
            StruPresetName.wPresetNum = (short) (Row + 1);
            StruPresetName.byName = Arrays.copyOf(NewPresetName.getBytes(),HCNetSDK.NAME_LEN);//NewPresetName.getBytes();
            StruPresetName.dwSize = StruPresetName.size();
            //String sOperateTime = (String)presetTableModel.getValueAt(Row, 1);//取得预置点名称

            StruPresetName.write();
            Pointer lpPresetName = StruPresetName.getPointer();
            boolean setPresetName = CommonParas.hCNetSDK.NET_DVR_SetDVRConfig(UserID, HCNetSDK.NET_DVR_SET_PRESET_NAME,
                    new NativeLong(Channel), lpPresetName, StruPresetName.size());
            StruPresetName.read();
            if (setPresetName){
                writePTZControlLog("修改预置点["+OldPresetName+"]名称为：["+NewPresetName+"]");
                CommonParas.showMessage("修改预置点["+OldPresetName+"]名称为：["+NewPresetName+"]", sFileName);
            }else{
                //CommonParas.showErrorMessage(this.getRootPane(), "设置预置点名称失败！", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                CommonParas.showErrorMessage( "设置预置点名称失败", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
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

            if (!bRet)
            {
                //CommonParas.showErrorMessage(this.getRootPane(), "获取预置点信息失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                CommonParas.showErrorMessage("获取预置点信息失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                //写错误日志
                CommonParas.SystemWriteErrorLog( "获取预置点信息失败",  channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                return;
            }
            //else JOptionPane.showMessageDialog(this, "获取云台巡航、轨迹、预置点信息成功！");
            for (int i=0;i<strPreSetName.strPreSetName320.length;i++){
                if (strPreSetName.strPreSetName320[i].dwSize > 0){
                    //添加预置点信息
                    //jComboBoxPreset.addItem("" + strPreSetName.strPreSetName320[i].wPresetNum + new String(strPreSetName.strPreSetName320[i].byName).trim());
                    Vector NewRow = new Vector();
                    NewRow.add(strPreSetName.strPreSetName320[i].wPresetNum);
                    NewRow.add(new String(strPreSetName.strPreSetName320[i].byName).trim());
                    presetTableModel.addRow(NewRow);
                }
                presetTableModel.fireTableDataChanged();
            }
            
            //获得巡航路径最大值和云台轨迹最大值
            String pInBuf = "<PTZAbility><channelNO>1</channelNO></PTZAbility>";
            byte[] pOutBuf = new byte[64*1024];//128k应该够用了
            bRet = CommonParas.hCNetSDK.NET_DVR_GetDeviceAbility( UserID, HCNetSDK.DEVICE_ABILITY_INFO, pInBuf, pInBuf.length(), pOutBuf, 64*1024);
            
            if (!bRet)
            {
                //CommonParas.showErrorMessage(this.getRootPane(), "获取设备能力集失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                CommonParas.showErrorMessage("获取设备能力集失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                //写错误日志
                CommonParas.SystemWriteErrorLog( "获取设备能力集失败",  channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
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
                    CruisePoints[i - 1] = "路径" + i;
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
                    
                    jComboBoxTrack.addItem("轨迹" + i);
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
        jDialogCruisePoint = new javax.swing.JDialog();
        jPanelFirst = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jButtonExit2 = new javax.swing.JButton();
        jPanelCenter = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jComboBoxCruisePoint = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxPreset = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxDwell = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jComboBoxCruiseSpeed = new javax.swing.JComboBox<>();
        jPanelLast = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
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
        jLabel2 = new javax.swing.JLabel();
        jButtonZoomOut = new javax.swing.JButton();
        jButtonFocusFar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButtonFocusNear = new javax.swing.JButton();
        jButtonIrisOpen = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
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
        jLabel16 = new javax.swing.JLabel();

        jLabelP.setText("1");

        jLabelT.setText("1");

        jLabelZ.setText("1");

        jDialogCruisePoint.setTitle("添加巡航点");
        jDialogCruisePoint.setMinimumSize(new java.awt.Dimension(420, 300));
        jDialogCruisePoint.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        jDialogCruisePoint.setUndecorated(true);

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
                .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
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

        jLabel9.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel9.setText("巡航点：");

        jComboBoxCruisePoint.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabel6.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel6.setText("预置点：");

        jComboBoxPreset.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabel7.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel7.setText("巡航时间：");

        jComboBoxDwell.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabel8.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel8.setText("巡航速度：");

        jComboBoxCruiseSpeed.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        javax.swing.GroupLayout jPanelCenterLayout = new javax.swing.GroupLayout(jPanelCenter);
        jPanelCenter.setLayout(jPanelCenterLayout);
        jPanelCenterLayout.setHorizontalGroup(
            jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCenterLayout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelCenterLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxCruiseSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelCenterLayout.createSequentialGroup()
                        .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxPreset, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxDwell, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCenterLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxCruisePoint, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addContainerGap(111, Short.MAX_VALUE))
        );
        jPanelCenterLayout.setVerticalGroup(
            jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCenterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jComboBoxCruisePoint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBoxPreset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBoxDwell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBoxCruiseSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jDialogCruisePoint.getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jButtonOK.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonOK.setText("确定");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonCancel.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
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
                .addContainerGap(181, Short.MAX_VALUE)
                .addComponent(jButtonOK)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancel)
                .addGap(101, 101, 101))
        );
        jPanelLastLayout.setVerticalGroup(
            jPanelLastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLastLayout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(jPanelLastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK)
                    .addComponent(jButtonCancel))
                .addContainerGap())
        );

        jDialogCruisePoint.getContentPane().add(jPanelLast, java.awt.BorderLayout.PAGE_END);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("云台控制");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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

        jLabel2.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("倍率");

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

        jLabel3.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("聚焦");

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

        jLabel4.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("光圈");

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
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonFocusNear, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonZoomOut, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonFocusNear, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonFocusFar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonIrisOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        jButtonAux2.setToolTipText("开启辅助设备开关2");
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
                    .addComponent(jButtonHeater, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(22, 22, 22)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPanePTZ.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jPanelPreset.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
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
        jButtonDeletePreset.setToolTipText("删除预览点设置");
        jButtonDeletePreset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeletePresetActionPerformed(evt);
            }
        });
        jPanel18.add(jButtonDeletePreset);

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

        jTablePreset.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
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

        jPanel17.setMinimumSize(new java.awt.Dimension(107, 25));
        jPanel17.setPreferredSize(new java.awt.Dimension(107, 75));
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

        jTableCruise.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
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
            .addGap(0, 221, Short.MAX_VALUE)
        );

        jPanelTrack.add(jPanel22, java.awt.BorderLayout.CENTER);

        jTabbedPanePTZ.addTab("轨迹", jPanelTrack);

        jLabel16.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabel16.setText("云台控制");

        javax.swing.GroupLayout jPanelPTZLayout = new javax.swing.GroupLayout(jPanelPTZ);
        jPanelPTZ.setLayout(jPanelPTZLayout);
        jPanelPTZLayout.setHorizontalGroup(
            jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPTZLayout.createSequentialGroup()
                .addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTabbedPanePTZ, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanelPTZLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelPTZLayout.setVerticalGroup(
            jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPTZLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTabbedPanePTZ, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPTZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelPTZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        jDialogCruisePoint.setVisible(false);
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

    private void jButtonHeaterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHeaterActionPerformed
        changeSwitchState(m_bHeaterOn,  HCNetSDK.HEATER_PWRON,  jButtonHeater,  "heateron.png",  "heateroff.png", "停止加热" , "加热");
    }//GEN-LAST:event_jButtonHeaterActionPerformed

    private void jButtonLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLightActionPerformed
        changeSwitchState(m_bLightOn,  HCNetSDK.LIGHT_PWRON,  jButtonLight,  "lighton.png",  "lightoff.png", "关闭灯光" , "开启灯光");
    }//GEN-LAST:event_jButtonLightActionPerformed

    private void jButtonWiperPwronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWiperPwronActionPerformed
        changeSwitchState(m_bWiperOn,  HCNetSDK.WIPER_PWRON,  jButtonWiperPwron,  "wiperon.png",  "wiperoff.png", "关闭雨刷" , "开启雨刷");

    }//GEN-LAST:event_jButtonWiperPwronActionPerformed

    private void jButtonAux1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAux1ActionPerformed
        changeSwitchState(m_bAuxOn1,  HCNetSDK.AUX_PWRON1,  jButtonAux1,  "auxiliarystop.png",  "auxiliary.png", "关闭辅助设备开关" , "开启辅助设备开关");

    }//GEN-LAST:event_jButtonAux1ActionPerformed

    private void jButtonAux2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAux2ActionPerformed
        changeSwitchState(m_bAuxOn2,  HCNetSDK.AUX_PWRON2,  jButtonAux2,  "auxiliarystop2.png",  "auxiliary2.png", "关闭辅助设备开关2" , "开启辅助设备开关2");

    }//GEN-LAST:event_jButtonAux2ActionPerformed

    private void jButtonFanPwronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFanPwronActionPerformed

        changeSwitchState(m_bFanOn,  HCNetSDK.FAN_PWRON,  jButtonFanPwron,  "fanon.png",  "fanoff.png", "关闭风扇" , "开启风扇");

    }//GEN-LAST:event_jButtonFanPwronActionPerformed

    private void jButtonGotoPresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGotoPresetActionPerformed
        // TODO add your handling code here:
        //int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
        int iPreset = jTablePreset.getSelectedRow()+ 1;
        String PresetName = (String)presetTableModel.getValueAt(iPreset - 1, 1);
        if (CommonParas.hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.GOTO_PRESET, iPreset)){
            writePTZControlLog("调用预置点："+PresetName);
        }else{
            writePTZControlErrorLog("调用预置点："+PresetName);
            //CommonParas.showErrorMessage( "调用预置点"+PresetName+"失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
        }
    }//GEN-LAST:event_jButtonGotoPresetActionPerformed

    private void jButtonSetPresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetPresetActionPerformed
        //int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
        int iPreset = jTablePreset.getSelectedRow()+ 1;
        String PresetName = (String)presetTableModel.getValueAt(iPreset - 1, 1);
        if (CommonParas.hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.SET_PRESET, iPreset))
        {
            writePTZControlLog("设置预置点："+PresetName);
            CommonParas.showMessage("成功设置预置点："+PresetName, sFileName);
            //CommonParas.showMessage(  "设置预置点"+PresetName+"成功", sFileName);
        }else {
            writePTZControlErrorLog("设置预置点："+PresetName);
            //CommonParas.showErrorMessage( "设置预置点"+PresetName+"失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
        }
    }//GEN-LAST:event_jButtonSetPresetActionPerformed

    private void jButtonDeletePresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeletePresetActionPerformed
        //int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
        int iPreset = jTablePreset.getSelectedRow()+ 1;
        String PresetName = (String)presetTableModel.getValueAt(iPreset - 1, 1);

        if (CommonParas.hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.CLE_PRESET, iPreset))
        {
            writePTZControlLog("删除预置点："+PresetName);
            CommonParas.showMessage("成功删除预置点："+PresetName, sFileName);
            //CommonParas.showMessage( "删除预置点"+PresetName+"成功", sFileName);

        }else {
            writePTZControlErrorLog("删除预置点："+PresetName);
            //CommonParas.showErrorMessage( "删除预置点"+PresetName+"失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
        }
    }//GEN-LAST:event_jButtonDeletePresetActionPerformed

    private void jTablePresetMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablePresetMousePressed
        // TODO add your handling code here:

        if (jTablePreset.getSelectedColumn() == colOfPresetName){
            int Row = jTablePreset.getSelectedRow();
            oldPresetName = (String)jTablePreset.getValueAt(Row, colOfPresetName);
            jTablePreset.editCellAt(Row, colOfPresetName);
        }
    }//GEN-LAST:event_jTablePresetMousePressed

    private void jComboBoxCruiseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCruiseItemStateChanged
        // TODO add your handling code here:

        if(evt.getStateChange() == ItemEvent.SELECTED){
            int CruiseRoute = jComboBoxCruise.getSelectedIndex() + 1;
            refreshCruiseTableData(CruiseRoute);
        }
    }//GEN-LAST:event_jComboBoxCruiseItemStateChanged

    private void jButtonRunSeqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunSeqActionPerformed
        // TODO add your handling code here:
        byte iSeq = (byte) (jComboBoxCruise.getSelectedIndex() + 1);
        if (!m_bIsOnCruise)
        {
            if (CommonParas.hCNetSDK.NET_DVR_PTZCruise(m_lRealHandle, HCNetSDK.RUN_SEQ, iSeq, (byte) 0, (short) 0)){

                m_bIsOnCruise = true;
                jButtonRunSeq.setToolTipText("停止调用");
                jButtonRunSeq.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ptzstop.png"));

                modifyCruiseJButtonState(jButtonRunSeq);
            }else{
                writePTZControlErrorLog("调用第["+iSeq+"]号巡航路径");
                //JOptionPane.showMessageDialog(this, "调用巡航失败");
            }
        }else{
            if (CommonParas.hCNetSDK.NET_DVR_PTZCruise(m_lRealHandle, HCNetSDK.STOP_SEQ, iSeq, (byte) 0, (short) 0)) {
                writePTZControlLog("停止调用第["+iSeq+"]号巡航路径");
                CommonParas.showMessage("停止调用第["+iSeq+"]号巡航路径", sFileName);
                m_bIsOnCruise = false;
                jButtonRunSeq.setToolTipText("调用巡航路径");
                jButtonRunSeq.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ptzcall.png"));

                modifyCruiseJButtonState(null);
            }else{
                writePTZControlErrorLog("停止调用第["+iSeq+"]号巡航路径");
                //JOptionPane.showMessageDialog(this, "停止巡航失败");
            }
        }
    }//GEN-LAST:event_jButtonRunSeqActionPerformed

    private void jButtonAddPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddPointActionPerformed
        // TODO add your handling code here:
        iAddOrUpdate = 1;//增加
        refreshCruisePointData();
        //jDialogCruisePoint.setTitle("添加巡航点");
        jLabelTitle.setText("添加巡航点");
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
            writePTZControlLog("删除第["+CruisePointNO+"]号巡航点：" + CruisePointName);
            CommonParas.showMessage("成功删除第["+CruisePointNO+"]号巡航点：" + CruisePointName, sFileName);
            refreshCruiseTableData(CruiseRoute);
        }else{
            writePTZControlErrorLog("删除第["+CruisePointNO+"]号巡航点：" + CruisePointName);
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
                jLabelTitle.setText("编辑巡航点");
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
            String Msg = "开始记录第["+PatternID+"]条轨迹";
            if (Control > 0){
                writePTZControlLog(Msg);
                CommonParas.showMessage(Msg, sFileName);
                //jButtonMemCruise.setText("停止记录");
                jButtonMemCruise.setToolTipText("停止记录");
                jButtonMemCruise.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ptzstop.png"));
                m_bMemCruise = true;
                modifyTrackJButtonState(jButtonMemCruise);
            }else{
                //CommonParas.showErrorMessage( Msg + "失败", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                writePTZControlErrorLog(Msg);
            }
        }else{
            int Control = PTZPatternControl( HCNetSDK.STO_MEM_CRUISE);
            String Msg = "停止记录第["+PatternID+"]条轨迹";
            if (Control > 0){
                writePTZControlLog(Msg);
                CommonParas.showMessage(Msg, sFileName);
                //jButtonMemCruise.setText("开始记录");
                jButtonMemCruise.setToolTipText("开始记录云台轨迹");
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
            String Msg = "开始运行第["+PatternID+"]条轨迹";
            if (Control > 0){
                writePTZControlLog(Msg);
                CommonParas.showMessage(Msg, sFileName);
                //jButtonRunCruise.setText("停止运行");
                jButtonRunCruise.setToolTipText("停止运行云台轨迹");
                jButtonRunCruise.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ptzstop.png"));
                m_bRunCruise = true;
                modifyTrackJButtonState(jButtonRunCruise);
            }else{
                //CommonParas.showErrorMessage( Msg + "失败", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
                writePTZControlErrorLog(Msg);
            }
        }else{
            int Control = PTZPatternControl( HCNetSDK.STOP_CRUISE);
            String Msg = "停止运行第["+PatternID+"]条轨迹";
            if (Control > 0){
                writePTZControlLog(Msg);
                CommonParas.showMessage(Msg, sFileName);
                //jButtonRunCruise.setText("开始运行");
                jButtonRunCruise.setToolTipText("开始运行云台轨迹");
                jButtonRunCruise.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ptzcall.png"));
                m_bRunCruise = false;
                modifyTrackJButtonState(null);
            }else{
                writePTZControlErrorLog(Msg);
            }
        }

        //        int Control = PTZPatternControl( HCNetSDK.RUN_CRUISE);
        //        if (Control == 0)
        //        {
            //            CommonParas.showErrorMessage( "运行轨迹失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
            //            return;
            //        }
    }//GEN-LAST:event_jButtonRunCruiseActionPerformed

    private void jButtonDeleteCruiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteCruiseActionPerformed
        // TODO add your handling code here:
        int PatternID = jComboBoxTrack.getSelectedIndex() + 1;
        if (PatternID < 1 ) return;

        int Control = PTZPatternControl( HCNetSDK.DELETE_CRUISE);
        String Msg = "删除第["+PatternID+"]条轨迹";
        if (Control > 0){
            writePTZControlLog(Msg);
            CommonParas.showMessage(Msg, sFileName);
        }else{
            writePTZControlErrorLog(Msg);
        }
    }//GEN-LAST:event_jButtonDeleteCruiseActionPerformed

    private void jButtonDeleteAllCruiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteAllCruiseActionPerformed
        // TODO add your handling code here:

        int Control = PTZPatternControl( HCNetSDK.DELETE_ALL_CRUISE);
        String Msg = "删除所有运行轨迹";
        if (Control > 0){
            writePTZControlLog(Msg);
            CommonParas.showMessage(Msg, sFileName);
        }else{
            writePTZControlErrorLog(Msg);
        }
    }//GEN-LAST:event_jButtonDeleteAllCruiseActionPerformed

    private void jButtonExit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExit2ActionPerformed
        // TODO add your handling code here:
        jButtonCancel.doClick();
    }//GEN-LAST:event_jButtonExit2ActionPerformed

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
                writePTZControlLog("添加第["+CruisePointNO+"]号巡航点：" + "第["+PresetNum+"]号预置点["+ PresetName+"]");
                CommonParas.showMessage("成功添加第["+CruisePointNO+"]号巡航点：" + "第["+PresetNum+"]号预置点["+ PresetName+"]", sFileName);
            }else{//修改预置点
                writePTZControlLog("修改第["+CruisePointNO+"]号巡航点为：" + "第["+PresetNum+"]号预置点["+PresetName+"]");
                CommonParas.showMessage("修改第["+CruisePointNO+"]号巡航点为：" + "第["+PresetNum+"]号预置点["+PresetName+"]", sFileName);
            }
        }
        jDialogCruisePoint.setVisible(false);
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        // TODO add your handling code here:
        jDialogCruisePoint.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed
    
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
                    CommonParas.showErrorMessage( "调用巡航路径 '"+CruiseRoute+"' 失败。", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
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

        }catch (Exception e){
            TxtLogger.append(this.sFileName, "refreshCruiseTableData()","系统在调用巡航路径的过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }
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
                        if(iStop == 1) writePTZControlLog(getCommonMeaning(iPTZCommand));
                    }

                } else{//速度为默认时
                
                    ret = CommonParas.hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
                    if (!ret){
                        //JOptionPane.showMessageDialog(this, "云台控制失败");
                        writePTZControlErrorLog(getCommonMeaning(iPTZCommand));
                    }else{
                        //只有在鼠标释放时，也就是操作结束时才进行写日志操作
                        if(iStop == 1) writePTZControlLog(getCommonMeaning(iPTZCommand));
                    }
                }
                if (bLabelPTZ) setPTZPos();
            }
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PTZControlAll()","系统在云台控制过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
//    /**
//	 * 函数:      PTZControlAll
//         * 函数描述:  云台控制函数，同时设置球机当前位置表示的三个标签的值
//         * @param lRealHandle: 预览句柄
//         * @param iPTZCommand: PTZ控制命令
//         * @param iStop: 开始或是停止操作
//    */
//    private void PTZControlAll(NativeLong lRealHandle, int iPTZCommand, int iStop)
//    {
//        try{
//
//            int iSpeed = jSliderSpeed.getValue();
//            if (lRealHandle.intValue() >= 0){
//
//                boolean ret;
//                if (iSpeed >= 1)//有速度的ptz
//                {
//                    ret = CommonParas.hCNetSDK.NET_DVR_PTZControlWithSpeed(lRealHandle, iPTZCommand, iStop, iSpeed);
//
//                    if (!ret){
//                        //JOptionPane.showMessageDialog(this, "云台控制失败");
//                        writePTZControlErrorLog(getCommonMeaning(iPTZCommand));
//                        return;
//                    }else{
//                        //只有在鼠标释放时，也就是操作结束时才进行写日志操作
//                        if(iStop == 1) writePTZControlLog(getCommonMeaning(iPTZCommand));
//                    }
//
//                    setPTZPos();
//                } else//速度为默认时
//                {
//                    ret = CommonParas.hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
//                    if (!ret){
//                        //JOptionPane.showMessageDialog(this, "云台控制失败");
//                        writePTZControlErrorLog(getCommonMeaning(iPTZCommand));
//                        return;
//                    }else{
//                        //只有在鼠标释放时，也就是操作结束时才进行写日志操作
//                        if(iStop == 1) writePTZControlLog(getCommonMeaning(iPTZCommand));
//                    }
//
//                    setPTZPos();
//                }
//            }
//        }catch (Exception e){
//            TxtLogger.append(this.sFileName, "PTZControlAll()","系统在云台控制过程中，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
//    }
    
    /**
	 * 函数:      PTZControlAllSwitch
         * 函数描述:  云台控制函数，控制云台的开关操作
         * @param lRealHandle: 预览句柄
         * @param iPTZCommand: PTZ控制命令
         * @param iStop: 开始或是停止操作
    */
    private boolean PTZControlAllSwitch(NativeLong lRealHandle, int iPTZCommand, int iStop){
        try{
            String OnOff = iStop==0?"开启":"关闭";
            boolean ret = CommonParas.hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
            if (!ret){
                writePTZControlErrorLog(OnOff + getCommonMeaning(iPTZCommand));
            }else{
                writePTZControlLog(OnOff + getCommonMeaning(iPTZCommand));
            }
            return ret;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "PTZControlAllSwitch()","系统在云台控制过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /**
	 * 函数:      changeSwitchState
         * 函数描述:  PTZControlAllSwitch函数云台操作之后，改变按钮及相关参数状态
         * @param Switch: MyBoolean对象，因为boolean参数是值传递，创建简单对象，达到引用传递。
         * @param Command: PTZ控制命令
         * @param SwitchButton: 按钮
         * @param ONIcon: 按钮“开关”打开，显示按钮图标
         * @param OFFIcon: 按钮“开关”关闭，显示按钮图标
    */
    private void changeSwitchState(MyBoolean Switch, int Command, JButton SwitchButton, String ONIcon, String OFFIcon, String ONText, String OFFText){
        if (!Switch.isIfON()){
            if (PTZControlAllSwitch(m_lRealHandle, Command, 0)){//0－开始
                //改用图标方式，不用文字方式了
                SwitchButton.setToolTipText(ONText);
                SwitchButton.setIcon(ImageIconBufferPool.getInstance().getImageIcon(ONIcon));
                Switch.setIfON(true); 
            }
        } else{
            if (PTZControlAllSwitch(m_lRealHandle, Command, 1)){//1－停止 
                SwitchButton.setToolTipText(OFFText);
                SwitchButton.setIcon(ImageIconBufferPool.getInstance().getImageIcon(OFFIcon));
                Switch.setIfON(false); 
            }
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
        int Index = -1;
        try{
            for (int i=0;i<iCommands.length;i++){
                if (iCommands[i] == Command) {Index = i;break;}
            }
            if (Index == -1 || Index>=sCommandMeanings.length) return "";
            return sCommandMeanings[Index];
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getCommonMeaning()","系统在获得PTZ控制命令含义的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return "";
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
    
    
    /**
	 * 函数:      writePTZControlLog
         * 函数描述:  PTZ控制窗口的写日志函数
    */
    private void writePTZControlLog( String Remarks){
        //操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、备注、调用的文件名
        CommonParas.SystemWriteLog("", CommonParas.LogType.LOG_OPER_CODE, "云台控制", channelRPBall.getDeviceparaBean().getSerialNO(), channelRPBall.getGroupName(), 
                                channelRPBall.getEncodingDVRChannelNode(), channelRPBall.getSerialNoJoin(), "", CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,
                                CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE, Remarks, sFileName);
    }
    
    /**
	 * 函数:      writePTZControlErrorLog
         * 函数描述:  PTZ控制窗口的写错误日志函数
    */
    public void writePTZControlErrorLog(String Remarks){
        //写错误日志
        //操作时间、设备别名、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
        CommonParas.SystemWriteErrorLog("", channelRPBall.getDeviceparaBean().getAnothername(), "云台控制失败", channelRPBall.getDeviceparaBean().getSerialNO(),  channelRPBall.getGroupName(),
                                channelRPBall.getEncodingDVRChannelNode(), channelRPBall.getSerialNoJoin(), "", CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE ,
                                CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE,sFileName);
        CommonParas.showErrorMessage(Remarks+"失败", channelRPBall.getDeviceparaBean().getAnothername(), sFileName);
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
            java.util.logging.Logger.getLogger(JDialogPTZControl2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialogPTZControl2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialogPTZControl2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialogPTZControl2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialogPTZControl2 dialog = new JDialogPTZControl2(new javax.swing.JFrame(), true,null);
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddPoint;
    private javax.swing.JButton jButtonAuto;
    private javax.swing.JButton jButtonAux1;
    private javax.swing.JButton jButtonAux2;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDelPoint;
    private javax.swing.JButton jButtonDeleteAllCruise;
    private javax.swing.JButton jButtonDeleteCruise;
    private javax.swing.JButton jButtonDeletePreset;
    private javax.swing.JButton jButtonExit2;
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
    private javax.swing.JButton jButtonMemCruise;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonRight;
    private javax.swing.JButton jButtonRightDown;
    private javax.swing.JButton jButtonRightUp;
    private javax.swing.JButton jButtonRunCruise;
    private javax.swing.JButton jButtonRunSeq;
    private javax.swing.JButton jButtonSetPreset;
    private javax.swing.JButton jButtonUp;
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
    private javax.swing.JDialog jDialogCruisePoint;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelP;
    private javax.swing.JLabel jLabelT;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelZ;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCruise;
    private javax.swing.JPanel jPanelFirst;
    private javax.swing.JPanel jPanelLast;
    private javax.swing.JPanel jPanelPTZ;
    private javax.swing.JPanel jPanelPreset;
    private javax.swing.JPanel jPanelTrack;
    private javax.swing.JScrollPane jScrollPaneCruise;
    private javax.swing.JScrollPane jScrollPanePreset;
    private javax.swing.JSlider jSliderSpeed;
    private javax.swing.JTabbedPane jTabbedPanePTZ;
    private javax.swing.JTable jTableCruise;
    private javax.swing.JTable jTablePreset;
    // End of variables declaration//GEN-END:variables

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
