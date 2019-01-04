/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import java.awt.event.ItemEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import jyms.tools.TableUtil.JTableCustomizeModel;
import jyms.data.DeviceParaBean;
import jyms.tools.endecrypt.MD5Encrypt;
import jyms.data.StandardAuthoritysBean;
import jyms.data.SubUserAuthoritysBean;
import jyms.data.TxtLogger;
import jyms.data.UserAuthoritysBean;
import jyms.data.UsersBean;
import jyms.ui.ScrollBarUI_White;
import jyms.ui.ScrollPaneUI_White;
import jyms.ui.TableHeaderUI_White;
import jyms.ui.TableUI_White;

/**
 * @author John
 * 因添加用户和修改用户涉及的功能太复杂，所以将其分为两个窗口处理，一个JDialogInsertUser，另一个JDialogModifyUser
 * 
 * 超级用户可以有所有的功能。不能修改自己的名字和用户类型、和权限，只能修改自己的密码。
 * 管理员用户只能修改自己的密码，修改不了自己的权限；
 *          不能添加管理员用户。只能添加、修改操作员用户；用户类型下拉列表是灰的；     
 *          可以修改操作员用户的密码和权限，但只限于自己的所拥有的权限范围之内。
 * 操作员用户只能修改自己的密码。除了密码输入外，其他都是灰的。
 * ----------------------------------------------
 * 修改用户资料：
 * 操作用户为Operator；被修改User。在这个窗口中，有一点：Operator级别永远>=User
 * 1、if Operator == User，只能修改密码，其他都是灰的。但是用户类型、用户名等都是该用户的值。
 * 因为在上级窗口里，只能出现操作用户以下级别的用户和自己。那么以下其余所有的情况就是Operator > User的情况了
 * 2、else if Operator == 1级，则可以做所有的工作。但是用户类型则没有“超级用户”项。所有的项都是用户的资料。
 * 3、else if Operator == 2级，用户类型是灰的其它的都可以修改，显示的是操作员。但是只能在自己的权限范围之内。所有的项都是用户的资料。
 * 4、 注意：Operator == 3级已经包含在第1项中
 * 添加用户资料：
 * 1、if Operator == 1级，则用户类型没有“超级用户”项，显示在“管理员”上，权限设置在“管理员”上，其他可以修改。
 * 2、if Operator == 2级，则用户类型只有“操作员”，权限 限制在“操作员”上，但必须限制在Operator以下，其他可以修改。
 */
public class JDialogInsertUser extends javax.swing.JDialog {

    
    private final String sFileName = this.getClass().getName() + ".java";
    private ArrayList<StandardAuthoritysBean> listStandardAuthoritys = new ArrayList<>();//超级管理员添加用户的标准权限。
    private ArrayList<UserAuthoritysBean> listUserAuthoritys = new ArrayList<>();//管理员添加的用户的的初始权限，最多和管理员所拥有的操作员权限相同
    private ArrayList listSubAuthoritys = new ArrayList();//管理员添加的用户的的初始附加权限，最多和管理员所拥有的操作员附加权限相同
    private ArrayList<DeviceParaBean> listDevicePara = new ArrayList<>();//获取设备参数表中的 设备序列号,IP地址,端口号,用户名,密码,别名,设备类型代码等参数
    
    private JTableCustomizeModel userAuthoritysTableModel ;
    private JTableCustomizeModel subAuthoritysTableModel ;
    private boolean ifCauseModifyOther = false;//改变其中一个列表，是否引起对另一个列表的修改。即如果是程序运行引起的值更改，不能引起另一个的表修改;用户手动修改可以引起另外一个表的修改
    private String sUserTypeCode = CommonParas.USER_TYPECODE_MANAGER;//待添加用户的权限代码，默认是管理员权限
//    private boolean bFirstSelect = true;//是否第一次选择JComboBox，因为编码选择也是触发了ItemStateChanged事件
    private int iRowStanard = 0;//标准权限表中的当前行
    private ArrayList<Boolean> listAuthoritySelectedOriginal = new ArrayList<>();//记录权限表当前行的初始值（也就是创建者的权限值）
    private ArrayList<Boolean> listSubSelectedOriginal = new ArrayList<>();//记录附加权限表当前行的初始值（也就是创建者的权限值）
    private ArrayList<String> listUserNames = new ArrayList<>();//需要复制权限的用户名称列表
    private ArrayList<String> listUserTypeCodes = new ArrayList<>();//需要复制权限的用户名称列表
    
    private int iState = 0;
    private String sUserName = "";//添加的用户名
    
//    private boolean bTriggerTableDataChanging = false;//为了tableChanged事件，由于因为程序触发和人工触发。保证程序触发不引起循环触发Procedure triggers
//    private boolean bTriggerSubTableDataChanging = false;//为了tableChanged事件，由于因为程序触发和人工触发。保证程序触发不引起循环触发Procedure triggers
    
//    private ArrayList<String> ListUsers;
    /**
     * Creates new form JDialogModifyUser
     * @param parent
     * @param modal
     * @param ListUserNames
     * @param listUserTypeCodes
     */
    public JDialogInsertUser(java.awt.Frame parent, boolean modal, ArrayList<String> ListUserNames, ArrayList<String> listUserTypeCodes) {
        super(parent, modal);
        this.listUserNames = ListUserNames;
        this.listUserTypeCodes = listUserTypeCodes;
        initComponents();
        modifyLocales();
        intialParas();
        
    }

    
    private void intialParas(){
        /** 
            * 1、if Operator == 1级，则用户类型没有“超级用户”项，显示在“管理员”上，权限设置在“管理员”上，其他可以修改。
            * 2、if Operator == 2级，则用户类型只有“操作员”，权限 限制在“操作员”上，但必须限制在Operator以下，其他可以修改。
        * */
        switch(CommonParas.UserState.UserTypeCode){
            case CommonParas.USER_TYPECODE_ADMIN:
                jComboBoxUserType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { CommonParas.USER_TYPE_MANAGER, CommonParas.USER_TYPE_OPERATOR }));
                sUserTypeCode = CommonParas.USER_TYPECODE_MANAGER;
//                //设置只有超级管理员才可以复制权限。
//                jRadioButtonFuZhi.setEnabled(true);
                break;
            case CommonParas.USER_TYPECODE_MANAGER:
                jComboBoxUserType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { CommonParas.USER_TYPE_OPERATOR }));
                sUserTypeCode = CommonParas.USER_TYPECODE_OPERATOR;
                break;
            default:
                jRadioButtonFuZhi.setEnabled(false);
                this.dispose();
                break;
        }
        userAuthoritysTableModel = new JTableCheckBoxModelAuthority();//this.initialUserAuthoritysTableModel();
        jTableUserAuthoritys.setModel(userAuthoritysTableModel);
    
        subAuthoritysTableModel = new JTableCheckBoxModelSubAuthority();//this.initialSubAuthoritysTableModel();
        jTableSubAuthoritys.setModel(subAuthoritysTableModel);
        //listDevicePara获取设备参数表中的 设备序列号,IP地址,端口号,用户名,密码,别名,设备类型代码等参数
        listDevicePara = DeviceParaBean.getDeviceParaList(this.sFileName);
        
        //向权限列表中添加数据
        
        //向列表框中添加需要复制权限的用户名
        if(listUserNames != null && !listUserNames.isEmpty()){
            Object[] Items = listUserNames.toArray();
            jComboBoxUserNames.setModel(new DefaultComboBoxModel(Items) );
        }else{
            jRadioButtonFuZhi.setEnabled(false);
        }
        
        
        jTableUserAuthoritys.getTableHeader().setUI(new TableHeaderUI_White());
        jTableUserAuthoritys.setUI(new TableUI_White());
        
        jTableSubAuthoritys.getTableHeader().setUI(new TableHeaderUI_White());
        jTableSubAuthoritys.setUI(new TableUI_White());
        
        jScrollPane1.setUI(new ScrollPaneUI_White());
        jScrollPane1.getVerticalScrollBar().setUI(new ScrollBarUI_White());
        jScrollPane1.getHorizontalScrollBar().setUI(new ScrollBarUI_White());
        
        jScrollPane2.setUI(new ScrollPaneUI_White());
        jScrollPane2.getVerticalScrollBar().setUI(new ScrollBarUI_White());
        jScrollPane2.getHorizontalScrollBar().setUI(new ScrollBarUI_White());
        
        CommonParas.setJButtonUnDecorated(jButtonExit);

        
//        subAuthoritysTableModel.addTableModelListener(new TableModelListener(){
//
//                @Override
//                public void tableChanged(TableModelEvent e) {
//                    //如果是程序触发该事件，不予处理
////                    if (bTriggerSubTableDataChanging) {
////                        bTriggerSubTableDataChanging = false;
////                        return;
////                    }
//                    System.out.println("tableChanged triggerevent!");
//                    System.out.println(e.getColumn());
//                    System.out.println(e.getFirstRow());
//                    
////                    /**
////                    * 当全部是未选择的时候，主权限为未选择
////                    * 当有一个选择，主权限为选择
////                    */
////                   try{
////                       //首先判断是否点击第一列，否则退出
////                       if (e.getColumn() != 0) return;
////
////                       int Row = e.getFirstRow();//jTableSubAuthoritys.getSelectedRow();
////                       int Row_Model= jTableSubAuthoritys.convertRowIndexToModel(Row);//将视图中的行索引转化为数据模型中的行索引
////                       if (Row < 0 ) return;
////                       int CountSub = jTableSubAuthoritys.getRowCount();
////                       if (Row > CountSub ) return;
////
////                       //最后检测有无权限，如果没有，则取消设置权限的操作
////                       if (!CommonParas.ifHaveSubUserAuthority(Row_Model, sFileName)) {
////                           procedureModifySubSelected(Row_Model, listSubSelected.get(Row_Model));
////                           return;
////                       }
////
////                       boolean Selected = (boolean)jTableUserAuthoritys.getValueAt(iRowStanard,0);
////
////                        int UnSelectedRows = 0;//记录为false的值的个数
////                        int SelectedRows = 0;//记录为true的值的个数
////                        for (int i=0;i<CountSub;i++){
////                            boolean tem = (boolean)jTableSubAuthoritys.getValueAt(i,0);
////                            if (tem == false) UnSelectedRows ++;
////                            if (tem == true) SelectedRows ++;
////                        }
////
////                        if (SelectedRows > 0){
////                            //当有一个选择true，且刚才主权限为false时，进行设置。如果刚才已经为true，则不必进行选择。
////                            if (Selected == false) {
////                                        //jTableUserAuthoritys.setValueAt(!NewSelect, iRowStanard, 0);
////                                        //不知道为什么上面设置后竟然没有立即出现结果，直到该行失去焦点时才出现结果。
////                                        //而给jTableSubAuthoritys赋值时则可以及时出现。没办法，只好采用下面的方法。
////                                userAuthoritysTableModel.setValueAt(true, iRowStanard, 0);
////                                userAuthoritysTableModel.fireTableCellUpdated(iRowStanard, 0);
////                            }
////                        }else if (SelectedRows == 0){
////
////                            if (UnSelectedRows == CountSub){
////                                userAuthoritysTableModel.setValueAt(false, iRowStanard, 0);
////                                userAuthoritysTableModel.fireTableCellUpdated(iRowStanard, 0);
////                            }else{
////                                TxtLogger.append(sFileName, "jTableSubAuthoritysMouseReleased()","系统出现未知错误，请检查");   
////                            }
////                        }
////
////
////                   } catch(Exception ex){
////                       TxtLogger.append(sFileName, "jTableSubAuthoritysMouseReleased()","系统在选择附加权限的过程中，出现错误" + 
////                                       "\r\n                       Exception:" + ex.toString());   
////                   }
////                    
//                }
//        
//        });
    }
    
    public void procedureModifySelected(int Row, boolean NewSelected){
//        bTriggerTableDataChanging = true;
        userAuthoritysTableModel.setValueAt(NewSelected, Row, 0);
        userAuthoritysTableModel.fireTableCellUpdated(Row, 0);
    }
    
    public void procedureModifySubSelected(int Row, boolean NewSelected){
//        bTriggerSubTableDataChanging = true;
        subAuthoritysTableModel.setValueAt(NewSelected, Row, 0);
        subAuthoritysTableModel.fireTableCellUpdated(Row, 0);
//        System.out.println(Row);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupQuanXian = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanelHeader = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jButtonExit = new javax.swing.JButton();
        jPanelUserInfo = new javax.swing.JPanel();
        jLabelUserName = new javax.swing.JLabel();
        jTextFieldUserName = new javax.swing.JTextField();
        jComboBoxUserType = new javax.swing.JComboBox<>();
        jLabelPassword = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabelPassword2 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jComboBoxUserNames = new javax.swing.JComboBox<>();
        jButtonCopyQuanXian = new javax.swing.JButton();
        jRadioButtonUserType = new javax.swing.JRadioButton();
        jRadioButtonFuZhi = new javax.swing.JRadioButton();
        jPanelUserRights = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableUserAuthoritys = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableSubAuthoritys = new javax.swing.JTable();
        jLabelSubTitle = new javax.swing.JLabel();
        jPanelLast = new javax.swing.JPanel();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("添加用户");
        setUndecorated(true);
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabelTitle.setFont(new java.awt.Font("微软雅黑", 1, 18)); // NOI18N
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("添加用户");

        jButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/close.png"))); // NOI18N
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelHeaderLayout = new javax.swing.GroupLayout(jPanelHeader);
        jPanelHeader.setLayout(jPanelHeaderLayout);
        jPanelHeaderLayout.setHorizontalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHeaderLayout.createSequentialGroup()
                .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelHeaderLayout.setVerticalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHeaderLayout.createSequentialGroup()
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.add(jPanelHeader, java.awt.BorderLayout.PAGE_START);

        jPanelUserInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "用户信息", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 16))); // NOI18N

        jLabelUserName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelUserName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelUserName.setText("用户名");

        jTextFieldUserName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jComboBoxUserType.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxUserType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxUserTypeItemStateChanged(evt);
            }
        });

        jLabelPassword.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelPassword.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPassword.setText("密  码");

        jPasswordField1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelPassword2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelPassword2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPassword2.setText("确认密码");

        jPasswordField2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jComboBoxUserNames.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxUserNames.setEnabled(false);

        jButtonCopyQuanXian.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonCopyQuanXian.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/userqxfz.png"))); // NOI18N
        jButtonCopyQuanXian.setText("复制权限");
        jButtonCopyQuanXian.setEnabled(false);
        jButtonCopyQuanXian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCopyQuanXianActionPerformed(evt);
            }
        });

        buttonGroupQuanXian.add(jRadioButtonUserType);
        jRadioButtonUserType.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonUserType.setSelected(true);
        jRadioButtonUserType.setText("用户类型");
        jRadioButtonUserType.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButtonUserType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonUserTypeActionPerformed(evt);
            }
        });

        buttonGroupQuanXian.add(jRadioButtonFuZhi);
        jRadioButtonFuZhi.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 16)); // NOI18N
        jRadioButtonFuZhi.setText("权限复制");
        jRadioButtonFuZhi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButtonFuZhi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonFuZhiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelUserInfoLayout = new javax.swing.GroupLayout(jPanelUserInfo);
        jPanelUserInfo.setLayout(jPanelUserInfoLayout);
        jPanelUserInfoLayout.setHorizontalGroup(
            jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUserInfoLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelUserInfoLayout.createSequentialGroup()
                        .addComponent(jRadioButtonUserType, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 3, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBoxUserType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldUserName)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonFuZhi, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPassword2, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPasswordField2)
                    .addComponent(jComboBoxUserNames, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonCopyQuanXian, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelUserInfoLayout.setVerticalGroup(
            jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelUserInfoLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButtonFuZhi)
                        .addComponent(jComboBoxUserNames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxUserType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jRadioButtonUserType)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelUserInfoLayout.createSequentialGroup()
                        .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelUserName)
                            .addGroup(jPanelUserInfoLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jTextFieldUserName)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelPassword)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelPassword2)
                            .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButtonCopyQuanXian))
                .addContainerGap())
        );

        jPanelUserInfoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonCopyQuanXian, jLabelPassword, jLabelPassword2, jLabelUserName, jPasswordField1, jPasswordField2});

        jPanel1.add(jPanelUserInfo, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanelUserRights.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "用户权限", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 16))); // NOI18N

        jTableUserAuthoritys.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jTableUserAuthoritys.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "标题 1"
            }
        ));
        jTableUserAuthoritys.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableUserAuthoritysMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableUserAuthoritysMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTableUserAuthoritys);

        jTableSubAuthoritys.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jTableSubAuthoritys.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1"
            }
        ));
        jTableSubAuthoritys.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableSubAuthoritysMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableSubAuthoritysMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTableSubAuthoritys);

        jLabelSubTitle.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        javax.swing.GroupLayout jPanelUserRightsLayout = new javax.swing.GroupLayout(jPanelUserRights);
        jPanelUserRights.setLayout(jPanelUserRightsLayout);
        jPanelUserRightsLayout.setHorizontalGroup(
            jPanelUserRightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUserRightsLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelUserRightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                    .addComponent(jLabelSubTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanelUserRightsLayout.setVerticalGroup(
            jPanelUserRightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUserRightsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelUserRightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelUserRightsLayout.createSequentialGroup()
                        .addComponent(jLabelSubTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(jPanelUserRights, java.awt.BorderLayout.CENTER);

        jButtonSave.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok2.png"))); // NOI18N
        jButtonSave.setText("保 存");
        jButtonSave.setToolTipText("");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonCancel.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cancel2.png"))); // NOI18N
        jButtonCancel.setText("取 消");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLastLayout = new javax.swing.GroupLayout(jPanelLast);
        jPanelLast.setLayout(jPanelLastLayout);
        jPanelLastLayout.setHorizontalGroup(
            jPanelLastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLastLayout.createSequentialGroup()
                .addContainerGap(429, Short.MAX_VALUE)
                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonCancel)
                .addGap(54, 54, 54))
        );
        jPanelLastLayout.setVerticalGroup(
            jPanelLastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLastLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelLastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSave)
                    .addComponent(jButtonCancel))
                .addContainerGap())
        );

        getContentPane().add(jPanelLast, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:

        setTableColWidth();
        //下面两句完全用下拉列表框的选择触发。
        fillIntoTableAuthoritys();
        
        //经过测试，好像发生了ListSelectionModel的valueChanged事件之后，clicked事件就不再发生了。
        jTableUserAuthoritys.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                try{
                    int Row = jTableUserAuthoritys.getSelectedRow();//e.getLastIndex();getLastIndex都会在第一行、最后一行出问题。
                    
                    if (Row < 0 ) return;
                    iRowStanard = Row;//用户权限表中的当前行
                    //重新过滤附加权限列表中的数据
                    tableRowSelected(Row);
                } catch(Exception ex){
                    TxtLogger.append(sFileName, "ListSelectionModel.valueChanged()","系统在选择用户权限的过程中，出现错误" + 
                                    "\r\n                       Exception:" + ex.toString());   
                }
            }
        });
        
        //主权限选择第1行（行号为0），重新过滤附加权限列表中的数据
        tableRowSelected(0);
    }//GEN-LAST:event_formWindowOpened

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        // TODO add your handling code here:
        iState = -1;
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jComboBoxUserTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxUserTypeItemStateChanged
        // TODO add your handling code here:
        if(jRadioButtonUserType.isSelected() && evt.getStateChange() == ItemEvent.SELECTED)
            {
                String UserType = (String)jComboBoxUserType.getSelectedItem();
                if (UserType.equals(CommonParas.USER_TYPE_MANAGER)){
                    sUserTypeCode = CommonParas.USER_TYPECODE_MANAGER;
                }else if(UserType.equals(CommonParas.USER_TYPE_OPERATOR)){
                    sUserTypeCode = CommonParas.USER_TYPECODE_OPERATOR;
                }
                
//                switch (UserType){
//                    case CommonParas.USER_TYPE_MANAGER:
//                        sUserTypeCode = CommonParas.USER_TYPECODE_MANAGER;
//                        break;
//                    case CommonParas.USER_TYPE_OPERATOR:
//                        sUserTypeCode = CommonParas.USER_TYPECODE_OPERATOR;
//                        break;
//                }

                fillIntoTableAuthoritys();
                tableRowSelected(0);
            }
    }//GEN-LAST:event_jComboBoxUserTypeItemStateChanged

   //保存按钮
    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        // TODO add your handling code here:
        try {
            //添加用户资料
            sUserName = jTextFieldUserName.getText().trim();//用户名
            String Password1 = new String(jPasswordField1.getPassword());
            String Password2 = new String(jPasswordField2.getPassword());
            if (getUserName() == null || getUserName().equals("")){
                JOptionPane.showMessageDialog(null, sUserNotNull);// "用户名不能为空！"
                return;
            }
            if (sUserName.length() > UsersBean.USERNAME_LENGTH){
                JOptionPane.showMessageDialog(null, MessageFormat.format(CommonParas.sLengthCannotExceed, jLabelUserName.getText(), UsersBean.USERNAME_LENGTH));// "用户名长度不能超过16！"
                return;
            }
            if (listUserNames.contains(sUserName)){
                JOptionPane.showMessageDialog(null, sUserExist);// "用户名已存在！"
                return;
            }
            if (Password1 == null || Password1.equals("") || Password2 == null || Password2.equals("")){
                JOptionPane.showMessageDialog(null, sPassNoEmpty);// "密码不能为空！"
                return;
            }
            if (!Password1.equals(Password2)){
                JOptionPane.showMessageDialog(null, sPassNotSame);// "两次输入的密码不一致！"
                return;
            }
            if (UsersBean.insertUser(new UsersBean(getUserName(),sUserTypeCode,MD5Encrypt.getMD5Str(Password1),CommonParas.getPasswordGrade(getUserName(), Password1)), sFileName) < 1){
                JOptionPane.showMessageDialog(null, sAddUserFail);// "添加用户失败！"
                return ;
            }
            
            boolean IfAdmin = CommonParas.UserState.UserTypeCode.equals(CommonParas.USER_TYPECODE_ADMIN);//是否超级管理员
            //添加用户权限
            ArrayList<String> ListInsertStr = new ArrayList<String>();
            for (int i=0;i<userAuthoritysTableModel.getRowCount();i++){
                //用户名 CHAR(16)、权限项目 VARCHAR(30)、有否	char(1)

                String IfHave;
                if ((Boolean)userAuthoritysTableModel.getValueAt(i, 0)) IfHave = "1";
                else IfHave = "0";
                
                //因为非超级管理员用户创建用户的时候，不能进行权限复制。listAuthoritySelectedOriginal所存储的值为当前用户值，所以可以用listAuthoritySelectedOriginal进行权限检测
                if (!IfAdmin && !listAuthoritySelectedOriginal.get(i))//为了保险起见，再次检测创建者的权限。即使“MousePressed->MouseReleased（鼠标按下，过一段时间，释放鼠标）”发生被选中的情况，也会被“淘汰”出局
                    IfHave = "0";
                String InsertStr = UserAuthoritysBean.getStringInsertCommand(getUserName(),(String)userAuthoritysTableModel.getValueAt(i, 1),IfHave,(String)userAuthoritysTableModel.getValueAt(i, 3));

                ListInsertStr.add(InsertStr);
            }
            //添加用户附加权限
            for (int i=0;i<subAuthoritysTableModel.getRowCount();i++){

                String IfHave2;
                
                if ((Boolean)subAuthoritysTableModel.getValueAt(i, 0)) IfHave2 = "1";
                else IfHave2 = "0";
                //因为非超级管理员用户创建用户的时候，不能进行权限复制。listAuthoritySelectedOriginal所存储的值为当前用户值，所以可以用listAuthoritySelectedOriginal进行权限检测
                if (!IfAdmin && !listSubSelectedOriginal.get(i))//为了保险起见，再次检测创建者的权限。即使“MousePressed->MouseReleased（鼠标按下，过一段时间，释放鼠标）”发生被选中的情况，也会被“淘汰”出局
                    IfHave2 = "0";
                String InsertStr2 = SubUserAuthoritysBean.getStringInsertCommand(getUserName(), (String)subAuthoritysTableModel.getValueAt(i, 2), (String)subAuthoritysTableModel.getValueAt(i, 3), IfHave2, (String)subAuthoritysTableModel.getValueAt(i, 4));
                ListInsertStr.add(InsertStr2);
            }
            
            if (UserAuthoritysBean.batchInsertUpdate(ListInsertStr, sFileName) > 0) {
                //JOptionPane.showMessageDialog(null, "成功添加用户：" + getUserName());
                iState = 1;
            }else{
                TxtLogger.append(this.sFileName, "jButtonSaveActionPerformed()","系统在保存用户资料的过程中，出现错误"); 
                iState = 0;
            }
            
        } catch(Exception e){
            TxtLogger.append(this.sFileName, "jButtonSaveActionPerformed()","系统在保存用户资料的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
            iState = 0;
        }
        this.dispose();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonCopyQuanXianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCopyQuanXianActionPerformed
        // TODO add your handling code here:
        if (jComboBoxUserNames.getItemCount()<1) return;
        String UserName = jComboBoxUserNames.getSelectedItem().toString();
        if (UserName.equals("")) return;
        refeshUserAuthoritysList(UserName);//刷新
        fillIntoTableManagerAuthoritys();
    }//GEN-LAST:event_jButtonCopyQuanXianActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        // TODO add your handling code here:
        jButtonCancel.doClick();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jRadioButtonUserTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonUserTypeActionPerformed
        // TODO add your handling code here:
        jComboBoxUserType.setEnabled(true);
        jComboBoxUserNames.setEnabled(false);
        jButtonCopyQuanXian.setEnabled(false);
    }//GEN-LAST:event_jRadioButtonUserTypeActionPerformed

    private void jRadioButtonFuZhiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonFuZhiActionPerformed
        // TODO add your handling code here:
        jComboBoxUserType.setEnabled(false);
        jComboBoxUserNames.setEnabled(true);
        jButtonCopyQuanXian.setEnabled(true);
    }//GEN-LAST:event_jRadioButtonFuZhiActionPerformed

    private void jTableUserAuthoritysMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableUserAuthoritysMouseClicked
        // TODO add your handling code here:
        //经过测试，MouseReleased、MousePressed、MouseClicked三个事件，只有MouseReleased事件完成得最好，从不出错
        //MouseClicked三个事件次之，偶尔也会出错。MousePressed正好相反，作出的都是反的。
        //首先判断是否点击第一列，否则退出

//        try{
//            
//            int Row = jTableUserAuthoritys.getSelectedRow();
//            if (Row < 0 ) return;
//            iRowStanard = Row;
//            tableRowSelected(Row);//对附件权限表进行过滤
//            
//            //首先判断是否点击第一列，否则退出
//            if (jTableUserAuthoritys.getSelectedColumn() != 0) return;
//
//            //下面这种处理是错误的，因为如果原始状态为true，那么快速点击，则会处于选中状态true，则不会恢复到上一次的状态。
////            //其次判断点击次数，快速点击的，除第一次外，不予处理
////            int ClickCount = evt.getClickCount();
////            if (ClickCount > 1) {
////                procedureModifySelected(Row, listAuthoritySelected.get(Row));
////                return;
////            }
//
//            //最后检测有无权限，如果没有，则取消设置权限的操作
//            String AuthorityItem = (String)jTableUserAuthoritys.getValueAt(Row,1);
//            if (!CommonParas.ifHaveUserAuthority(AuthorityItem, sFileName)) {
//                procedureModifySelected(Row, listAuthoritySelectedOriginal.get(Row));
//                return;
//            }
//
//            boolean NewSelect = (boolean)jTableUserAuthoritys.getValueAt(Row,0);//clicked事件中，其返回值则是鼠标点击后的值
//            
//            modifySubTableSlectState(NewSelect);//改变附加权限表中的选中状态
//
//            //修改列表中JCheckBox的值，进过测试，tablemodel中的值也发生变化。
//        } catch(Exception e){
//            TxtLogger.append(this.sFileName, "jTableUserAuthoritysMouseClicked()","系统在选择用户权限的过程中，出现错误" + 
//                            "\r\n                       Exception:" + e.toString());   
//        }
    }//GEN-LAST:event_jTableUserAuthoritysMouseClicked

    private void jTableSubAuthoritysMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSubAuthoritysMouseClicked
        // TODO add your handling code here:
        /**
         * 当全部是未选择的时候，主权限为未选择
         * 当有一个选择，主权限为选择
         * 当MousePressed->MouseReleased（鼠标按下，过一段时间，释放鼠标），会“逃出”Clicked事件的检测。但是也逃不出Save按钮的检测。
         */
//        try{
//            //首先判断是否点击第一列，否则退出
//            if (jTableSubAuthoritys.getSelectedColumn() != 0) return;
//
//            int Row = jTableSubAuthoritys.getSelectedRow();
//            int Row_Model= jTableSubAuthoritys.convertRowIndexToModel(Row);//将视图中的行索引转化为数据模型中的行索引
//            if (Row < 0 ) return;
//            int CountSub = jTableSubAuthoritys.getRowCount();
//            if (Row > CountSub ) return;
//            
//            //下面这种处理是错误的，因为如果原始状态为true，那么快速点击，则会处于选中状态true，则不会恢复到上一次的状态。
////            //其次判断点击次数，快速点击的，除第一次外，不予处理
////            int ClickCount = evt.getClickCount();
////            if (ClickCount > 1) {
////                procedureModifySubSelected(Row_Model, listSubSelected.get(Row_Model));
////                return;
////            }
//            
//            //最后检测有无权限，如果没有，则取消设置权限的操作
//            String AuthorityItem = (String)jTableSubAuthoritys.getValueAt(Row,2);
//            String AnotherName = (String)jTableSubAuthoritys.getValueAt(Row,1);
//            if (!CommonParas.ifHaveSubAuthorityAnotherName(AuthorityItem,AnotherName, sFileName)) {
//                procedureModifySubSelected(Row_Model, listSubSelectedOriginal.get(Row_Model));
//                return;
//            }
//            
//            //主选择
//            boolean Selected = (boolean)jTableUserAuthoritys.getValueAt(iRowStanard,0);
//
//            int UnSelectedRows = 0;//记录为false的值的个数
//            int SelectedRows = 0;//记录为true的值的个数
//            for (int i=0;i<CountSub;i++){
//                boolean tem = (boolean)jTableSubAuthoritys.getValueAt(i,0);
//                if (tem == false) UnSelectedRows ++;
//                if (tem == true) SelectedRows ++;
//
//            }
//
//            if (SelectedRows > 0){
//                //当有一个选择true，且刚才主权限为false时，进行设置。如果刚才已经为true，则不必进行选择。
//                if (Selected == false) {
//                            //jTableUserAuthoritys.setValueAt(!NewSelect, iRowStanard, 0);
//                            //不知道为什么上面设置后竟然没有立即出现结果，直到该行失去焦点时才出现结果。
//                            //而给jTableSubAuthoritys赋值时则可以及时出现。没办法，只好采用下面的方法。
//                    userAuthoritysTableModel.setValueAt(true, iRowStanard, 0);
//                    userAuthoritysTableModel.fireTableCellUpdated(iRowStanard, 0);
//                }
//            }else if (SelectedRows == 0){
//
//                if (UnSelectedRows == CountSub){
//                    userAuthoritysTableModel.setValueAt(false, iRowStanard, 0);
//                    userAuthoritysTableModel.fireTableCellUpdated(iRowStanard, 0);
//                }else{
//                    TxtLogger.append(this.sFileName, "jTableSubAuthoritysMouseReleased()","系统出现未知错误，请检查");   
//                }
//            }
//
//
//        } catch(Exception e){
//            TxtLogger.append(this.sFileName, "jTableSubAuthoritysMouseReleased()","系统在选择附加权限的过程中，出现错误" + 
//                            "\r\n                       Exception:" + e.toString());   
//        }
    }//GEN-LAST:event_jTableSubAuthoritysMouseClicked

    private void jTableUserAuthoritysMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableUserAuthoritysMousePressed
        // TODO add your handling code here:
        ifCauseModifyOther = true;
    }//GEN-LAST:event_jTableUserAuthoritysMousePressed

    private void jTableSubAuthoritysMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSubAuthoritysMousePressed
        // TODO add your handling code here:
        ifCauseModifyOther = true;
    }//GEN-LAST:event_jTableSubAuthoritysMousePressed
    /**
        *函数:      tableRowSelected
        *@param Row    标准权限表中的当前行
        *函数描述:  重新过滤附加权限列表中的数据
    */
    private void tableRowSelected(int Row){
        try{
        if (userAuthoritysTableModel.getRowCount() == 0) return;
            if (Row < 0 ) return;
            if (Row > userAuthoritysTableModel.getRowCount() ) return;
            //{"选中","用户权限","是否细分","权限类型","用户权限"};//第一个用户权限为代码形式，最后一个用户权限实际是备注
            String Authorityitem = (String)userAuthoritysTableModel.getValueAt(Row, 1);
            jLabelSubTitle.setText((String)userAuthoritysTableModel.getValueAt(Row, 4));// + "   相关"

            if (subAuthoritysTableModel.getRowCount() < 1) return;
            //若要测试完全匹配，可分别使用字符 '^' 和 '$' 来匹配该字符串的开头和结尾。例如，“^foo$”只包含其字符串完全为“foo”的行，而不是“food”之类。有关受支持的正则表达式结构的完整描述，请参阅 Pattern。 
            String sFilter = "^" + Authorityitem.trim() +"$";
            TableRowSorter sorter = new TableRowSorter(subAuthoritysTableModel);
            sorter.setRowFilter(RowFilter.regexFilter(sFilter));
            jTableSubAuthoritys.setRowSorter(sorter);
        } catch(Exception e){
            TxtLogger.append(this.sFileName, "tableRowSelected()","系统在重新过滤附加权限列表中数据的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }

//    /**
//        *函数:      modifySubTableSlectState
//        *@param NewSelect    改变后的值
//        *函数描述:  改变附加权限表中的选中状态
//    */
//    private void modifySubTableSlectState(boolean NewSelect){
//        for (int i=0;i<jTableSubAuthoritys.getRowCount();i++){
//            
//            String AuthorityItem = (String)jTableSubAuthoritys.getValueAt(i,2);
//            String AnotherName = (String)jTableSubAuthoritys.getValueAt(i,1);
//            //检测有无权限，如果没有，则取消设置权限的操作
//            if (!CommonParas.ifHaveSubAuthorityAnotherName(AuthorityItem,AnotherName, sFileName))  continue;
//            
//            jTableSubAuthoritys.setValueAt(NewSelect, i, 0);
//        }
//    }
    /**
        * 函数:      fillIntoTableAuthoritys
        * @param     UserNameCoyp   复制权限的用户名
        * @param     UserType       用户类型String UserNameCoyp, String UserTypeCode
 函数描述:  将用户的权限列表添加到JTable中
    */
    private void fillIntoTableAuthoritys(){
        switch(CommonParas.UserState.UserTypeCode){
            case CommonParas.USER_TYPECODE_ADMIN:
                refeshAuthoritysList(sUserTypeCode);//刷新
                fillIntoTableStandardAuthoritys();
                break;
            case CommonParas.USER_TYPECODE_MANAGER:
                refeshAuthoritysList(sUserTypeCode);//刷新
                fillIntoTableManagerAuthoritys();
                break;
            default:
                break;
        }
    }
    /**
        *函数:      fillIntoTableStandardAuthoritys
        *函数描述:  从数据库中提取所有符合条件的标准权限到JTable中
    */
    private void fillIntoTableStandardAuthoritys(){
        try {
            
            if (listStandardAuthoritys.size() == 0) return;
            
            for (int i=0;i<listStandardAuthoritys.size();i++){
                //以UserAuthoritysBean形式输出的StandardAuthoritysBean
                StandardAuthoritysBean StandardBean = listStandardAuthoritys.get(i);
                //获取标准权限表中的用户类型代码、权限项目、是否细分、备注等信息
                
                //{"选中","用户权限","是否细分","权限类型","用户权限"};//最后一个用户权限实际是备注
                Vector newRow = new Vector();
                
                newRow.add(new Boolean(true));
                listAuthoritySelectedOriginal.add(true);
                newRow.add(StandardBean.getAuthorityitem());
                newRow.add(StandardBean.getIfsubdivision());
                String AuthorityType = StandardBean.getUsertype();
                newRow.add(AuthorityType);
                newRow.add(StandardBean.getRemarks());
                
                userAuthoritysTableModel.addRow(newRow);//和下面的语句效果实际证明是同样的。
//                userAuthoritysTableModel.getDataVector().add(newRow);
                //如果是普通管理员登录,则需设置权限在该管理员以下,所以需要另外设置。

                //如果是超级管理员登录，则继续以下操作。
                //往附加权限表中添加与该项权限项目相关的设备权限
                if (StandardBean.getIfsubdivision().equals("1")) {
                    
                    //listDevicePara获取设备参数表中的 设备序列号,IP地址,端口号,用户名,密码,别名,设备类型代码等参数
                    for (int j=0;j<listDevicePara.size();j++){
                        DeviceParaBean deviceParaBean = listDevicePara.get(j);
                        Vector newRow2 = new Vector();
                        newRow2.add(new Boolean(true));
                        listSubSelectedOriginal.add(new Boolean(true));
                        newRow2.add(deviceParaBean.getAnothername());
                        newRow2.add(StandardBean.getAuthorityitem());
                        newRow2.add(deviceParaBean.getSerialNO());
                        newRow2.add(AuthorityType);
                        subAuthoritysTableModel.addRow(newRow2);
                    }
                    
                    subAuthoritysTableModel.fireTableDataChanged();
                    jTableSubAuthoritys.repaint();
                }
            }
            userAuthoritysTableModel.fireTableDataChanged();
            jTableUserAuthoritys.repaint();

        }catch(Exception e)
            {
                TxtLogger.append(this.sFileName, "fillIntoTableDeviceManaged()","系统在刷新管理设备表过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }

    }
    
    
    /**
        *函数:      fillIntoTableManagerAuthoritys
        *函数描述:  从数据库中提取所有符合条件的标准权限到JTable中
    */
    private void fillIntoTableManagerAuthoritys(){
        try {
            
            if (listUserAuthoritys.isEmpty() || listSubAuthoritys.isEmpty()) return;
            
            //listUserAuthoritys获取用户权限表中的用户名、权限项目、有无权限、备注等信息
            for (int i=0;i<listUserAuthoritys.size();i++){
                UserAuthoritysBean AuthoritysBean = listUserAuthoritys.get(i);
                //获取标准权限表中的用户类型代码、权限项目、是否细分等信息
                
                //{"选中","用户权限","是否细分","权限类型","用户权限"};//最后一个用户权限实际是备注
                Vector newRow = new Vector();
                if (AuthoritysBean.getIfhave().equals("1")) {
                    newRow.add(new Boolean(true));
                    listAuthoritySelectedOriginal.add(true);
                }else{
                    newRow.add(new Boolean(false));
                    listAuthoritySelectedOriginal.add(false);
                }
                newRow.add(AuthoritysBean.getAuthorityitem());//权限项目
                newRow.add("");//有无细分在这里为空
                newRow.add(AuthoritysBean.getRemarks());//有无权限
                newRow.add(AuthoritysBean.getAuthorityitemRemarks());//是权限项目的翻译形式
                userAuthoritysTableModel.addRow(newRow);//和下面的语句效果实际证明是同样的。
//                userAuthoritysTableModel.getDataVector().add(newRow);
            }
            userAuthoritysTableModel.fireTableDataChanged();
            jTableUserAuthoritys.repaint();
            
            
            //往附加权限表中添加与该项权限项目相关的设备权限  
            //获取用户权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息
            for (int j=0;j<listSubAuthoritys.size();j++){
                ArrayList SubList = (ArrayList)listSubAuthoritys.get(j);
                Vector newRow2 = new Vector();
                //"选中","设备","用户权限","设备序列号","权限类型"
                if (((String)SubList.get(4)).equals("1")) {
                    newRow2.add(new Boolean(true));
                    listSubSelectedOriginal.add(true);
                }else{
                    newRow2.add(new Boolean(false));
                    listSubSelectedOriginal.add(false);
                }
                newRow2.add((String)SubList.get(3));//设备别名
                newRow2.add((String)SubList.get(1));//权限项目
                newRow2.add((String)SubList.get(2));//设备序列号
                newRow2.add((String)SubList.get(5));//备注。即权限类型，对应标准权限表的用户类型
                subAuthoritysTableModel.addRow(newRow2);
                
            }
            subAuthoritysTableModel.fireTableDataChanged();
            jTableSubAuthoritys.repaint();

        }catch(Exception e)
            {
                TxtLogger.append(this.sFileName, "fillIntoTableDeviceManaged()","系统在刷新管理设备表过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }

    }
    /**
        *函数:      refeshAuthoritysList
        *@param UserTypeCode    用户类型代码
        *函数描述:  重新刷新标准用户权限列表listStandardAuthoritys
    */
    private void refeshAuthoritysList(String UserTypeCode){
        try {
            
            switch(CommonParas.UserState.UserTypeCode){
                case CommonParas.USER_TYPECODE_ADMIN:
                    if (listStandardAuthoritys != null ) listStandardAuthoritys.clear();//已管理设备的序列号数组
                    listStandardAuthoritys = StandardAuthoritysBean.getStandardAuthoritysList(UserTypeCode,sFileName);
                    break;
                case CommonParas.USER_TYPECODE_MANAGER:
                    listUserAuthoritys = UserAuthoritysBean.getUserAuthoritysList(CommonParas.UserState.UserName, UserTypeCode, sFileName);//管理员添加的用户的的初始权限，最多和管理员所拥有的操作员权限相同
                    listSubAuthoritys = SubUserAuthoritysBean.getSubUserAuthoritysList(CommonParas.UserState.UserName, UserTypeCode, sFileName);//管理员添加的用户的的初始附加权限，最多和管理员所拥有的操作员附加权限相同
                    break;
                default:
                    break;
            }
            
            listAuthoritySelectedOriginal.clear();
            listSubSelectedOriginal.clear();
            Vector v = userAuthoritysTableModel.getDataVector();
            if (v != null) v.clear();
            Vector v2= subAuthoritysTableModel.getDataVector();
            if (v2 != null) v2.clear();
        }catch(Exception e)
        {
            TxtLogger.append(this.sFileName, "refeshAuthoritysList()","系统在重新刷新标准用户权限列表过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
    
    
    /**
        *函数:      refeshUserAuthoritysList
        *@param UserTypeCode    用户类型代码
        *函数描述:  重新刷新标准用户权限列表listStandardAuthoritys
    */
    private void refeshUserAuthoritysList(String UserName){
        try {

            listUserAuthoritys = UserAuthoritysBean.getUserAuthoritysList(UserName,  sFileName);//管理员添加的用户的的初始权限，最多和管理员所拥有的操作员权限相同
            listSubAuthoritys = SubUserAuthoritysBean.getSubUserAuthoritysList(UserName,  sFileName);//管理员添加的用户的的初始附加权限，最多和管理员所拥有的操作员附加权限相同
            sUserTypeCode = listUserTypeCodes.get(listUserNames.indexOf(UserName));
            jComboBoxUserType.setSelectedItem(CommonParas.getUserType(sUserTypeCode));
            //sUserTypeCode
            listAuthoritySelectedOriginal.clear();
            listSubSelectedOriginal.clear();
            Vector v = userAuthoritysTableModel.getDataVector();
            if (v != null) v.clear();
            Vector v2= subAuthoritysTableModel.getDataVector();
            if (v2 != null) v2.clear();
        }catch(Exception e)
        {
            TxtLogger.append(this.sFileName, "refeshUserAuthoritysList()","系统在重新刷新标准用户权限列表过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
    
    
    /**
        * 函数:      initialUserAuthoritysTableModel
        * 函数描述:  初始化用户权限列表jTableUserAuthoritys
        * @return JTableCheckBoxModel
    */
    private JTableCheckBoxModel initialUserAuthoritysTableModel()
    {
        //String[] sTableUserAuthoritysTitle;
    
        //sTableUserAuthoritysTitle = new String[] {"选中","用户权限","是否细分","权限类型","用户权限"};//最后一个用户权限实际是备注
        JTableCheckBoxModel  UserAuthoritysTableModel =new JTableCheckBoxModel(sTableUserAuthoritysTitle);
        return UserAuthoritysTableModel;
    }

    /**
        * 函数:      initialSubAuthoritysTableModel
        * 函数描述:  初始化附加用户权限列表jTableSubUserAuthoritys
        * @return JTableCheckBoxModel
    */
    private JTableCheckBoxModel initialSubAuthoritysTableModel()
    {
        //eSubAuthoritysTitle = new String[] {"选中","设备","用户权限","设备序列号","权限类型"};
        JTableCheckBoxModel  SubUserAuthoritysTableModel =new JTableCheckBoxModel(sTableSubAuthoritysTitleAdd);
        return SubUserAuthoritysTableModel;
    }
    /**
	 * 函数:      setTableColWidth
         * 函数描述:  设置表格特殊列的宽度
    */
    private void setTableColWidth(){
        
        try {
            //{"选中0","用户权限1","是否细分2","权限类型3","用户权限4"};//最后一个用户权限实际是备注
            TableColumnModel tcmUserAuthoritys = jTableUserAuthoritys.getColumnModel();
            tcmUserAuthoritys.getColumn(0).setMinWidth(30);
            tcmUserAuthoritys.getColumn(0).setMaxWidth(120);
            tcmUserAuthoritys.getColumn(0).setPreferredWidth(90);
            tcmUserAuthoritys.getColumn(1).setMinWidth(0);
            tcmUserAuthoritys.getColumn(1).setMaxWidth(0);
            tcmUserAuthoritys.getColumn(1).setWidth(0);
            tcmUserAuthoritys.getColumn(2).setMinWidth(0);
            tcmUserAuthoritys.getColumn(2).setMaxWidth(0);
            tcmUserAuthoritys.getColumn(2).setWidth(0);
            tcmUserAuthoritys.getColumn(3).setMinWidth(0);
            tcmUserAuthoritys.getColumn(3).setMaxWidth(0);
            tcmUserAuthoritys.getColumn(3).setWidth(0);

            TableColumnModel tcmSubAuthoritys = jTableSubAuthoritys.getColumnModel();
            tcmSubAuthoritys.getColumn(0).setMinWidth(30);
            tcmSubAuthoritys.getColumn(0).setMaxWidth(120);
            tcmSubAuthoritys.getColumn(0).setPreferredWidth(90);

            tcmSubAuthoritys.getColumn(2).setMinWidth(0);
            tcmSubAuthoritys.getColumn(2).setMaxWidth(0);
            tcmSubAuthoritys.getColumn(2).setWidth(0);
            tcmSubAuthoritys.getColumn(3).setMinWidth(0);
            tcmSubAuthoritys.getColumn(3).setMaxWidth(0);
            tcmSubAuthoritys.getColumn(3).setWidth(0);
            tcmSubAuthoritys.getColumn(4).setMinWidth(0);
            tcmSubAuthoritys.getColumn(4).setMaxWidth(0);
            tcmSubAuthoritys.getColumn(4).setWidth(0);
        }catch(Exception e)
        {
            TxtLogger.append(this.sFileName, "setTableColWidth()","系统在设置表格特殊列的宽度过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }

    }
    /**
	 * 函数:      getState
         * 函数描述:  向上一级窗口返回操作的状态
         * @return int 操作的状态。1成功；0失败。
    */
    public int getState(){
        return iState;
    }
    /**
     * @return the sUserName
     */
    public String getUserName() {
        return sUserName;
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
            java.util.logging.Logger.getLogger(JDialogInsertUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialogInsertUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialogInsertUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialogInsertUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialogInsertUser dialog = new JDialogInsertUser(new javax.swing.JFrame(), true, null,null);
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
        sTableUserAuthoritysTitle = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sTableUserAuthoritysTitle").split(",");  //选中,用户权限,是否细分,权限类型
        sTableSubAuthoritysTitleAdd = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sTableSubAuthoritysTitleAdd").split(",");  //选中,设备,用户权限,设备序列号,权限类型
        sUserNotNull = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sUserNotNull");  //用户名不能为空！
        sUserExist = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sUserExist");  //用户名已存在！
        sPassNoEmpty = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sPassNoEmpty");  //密码不能为空！
        sPassNotSame = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sPassNotSame");  //两次输入的密码不一致！
        sAddUserFail = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sAddUserFail");  //添加用户失败！
        
        sAddUser = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sAddUser");  //添加用户
        this.setTitle(sAddUser);
        jLabelTitle.setText(sAddUser);//	添加用户


        //标签和按钮显示
        jLabelUserName.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jLabelUserName"));  //用户名
        jLabelPassword.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jLabelPassword"));  //密  码
        jLabelPassword2.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jLabelPassword2"));  //确认密码
        jRadioButtonUserType.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jRadioButtonUserType"));  //用户类型
        jButtonCopyQuanXian.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.sCopyRight"));  //复制权限
        jRadioButtonFuZhi.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.sCopyRight"));  //权限复制
        jButtonSave.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jButtonSave"));  //保 存
        jButtonCancel.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jButtonCancel"));  //取 消

        ((TitledBorder)jPanelUserInfo.getBorder()).setTitle(   Locales.getString("JInFrameUserManage", "JInFrameUserManage.jPanelUserInfo"));  //用户信息
        ((TitledBorder)jPanelUserRights.getBorder()).setTitle( Locales.getString("JInFrameUserManage", "JInFrameUserManage.jPanelUserRights"));  //用户权限

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupQuanXian;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonCopyQuanXian;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JComboBox<String> jComboBoxUserNames;
    private javax.swing.JComboBox<String> jComboBoxUserType;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JLabel jLabelPassword2;
    private javax.swing.JLabel jLabelSubTitle;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelUserName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelLast;
    private javax.swing.JPanel jPanelUserInfo;
    private javax.swing.JPanel jPanelUserRights;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JRadioButton jRadioButtonFuZhi;
    private javax.swing.JRadioButton jRadioButtonUserType;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableSubAuthoritys;
    private javax.swing.JTable jTableUserAuthoritys;
    private javax.swing.JTextField jTextFieldUserName;
    // End of variables declaration//GEN-END:variables
    
    private String[] sTableUserAuthoritysTitle = new String[] {"选中","用户权限","是否细分","权限类型","用户权限"};//最后一个用户权限实际是备注
    private String[] sTableSubAuthoritysTitleAdd = new String[] {"选中","设备","用户权限","设备序列号","权限类型"};
    private String sAddUser = "添加用户";
    private String sUserNotNull = "用户名不能为空！";
    private String sUserExist = "用户名已存在！";
    private String sPassNoEmpty = "密码不能为空！";
    private String sPassNotSame = "两次输入的密码不一致！";
    private String sAddUserFail = "添加用户失败！";

    private class JTableCheckBoxModel extends JTableCustomizeModel
    {

        public JTableCheckBoxModel(String[] columnNames) {
            super(columnNames);
        }
        
        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 0) return true;
            return false;
        }
    }
    
    
    
    private class JTableCheckBoxModelAuthority extends JTableCustomizeModel
    {

        public JTableCheckBoxModelAuthority() {
            super(sTableUserAuthoritysTitle);// new String[] {"选中","用户权限","是否细分","权限类型"}
        }
        
        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 0) {
                String AuthorityItem = (String)super.getValueAt( row, 1);
                if (CommonParas.ifHaveUserAuthority(AuthorityItem, sFileName)) return true;
                else return false;
            }
            return false;
        }
        
        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (column != 0) return;
            
            String AuthorityItem = (String)super.getValueAt( row, 1);
            if (!CommonParas.ifHaveUserAuthority(AuthorityItem, sFileName)) return;
            
            super.setValueAt(aValue, row, column);

            /*判断是否是手动改变。
            如果是，则应该改变附加权限表中的选中状态；
            如果否，则是因为附加权限表中值修改引起的主权限表的值修改，那么就不应该再引起附加权限表的修改，从而导致相互修改的死循环
            */
            if (ifCauseModifyOther)
                modifySubTableSlectState((boolean)aValue);//改变附加权限表中的选中状态
        }
    }
    
    private class JTableCheckBoxModelSubAuthority extends JTableCustomizeModel
    {

        public JTableCheckBoxModelSubAuthority() {
            super(sTableSubAuthoritysTitleAdd);// new String[] {"选中","设备","用户权限","设备序列号","权限类型"}
        }
        
        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 0) {
                String AuthorityItem = (String)super.getValueAt( row, 2);
                if (CommonParas.ifHaveUserAuthority(AuthorityItem, sFileName)) return true;
                else return false;
            }
            return false;
        }
        
        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (column != 0) return;
            
            String AuthorityItem = (String)super.getValueAt( row, 2);
            String AnotherName = (String)super.getValueAt( row, 1);
            if (!CommonParas.ifHaveSubAuthorityAnotherName(AuthorityItem,AnotherName, sFileName)) return;
            
            super.setValueAt(aValue, row, column);
            /*判断是否是手动改变。
            如果是，则应该改变附加权限表中的选中状态；
            如果否，则是因为附加权限表中值修改引起的主权限表的值修改，那么就不应该再引起附加权限表的修改，从而导致相互修改的死循环
            */
            if (ifCauseModifyOther)
                modifyMainTableSlectState();
        }
    }
    
    /**
        * 函数:     modifySubTableSlectState
        * 函数描述: 改变附加权限表中的选中状态
        * @param    NewSelect    改变后的值
    */
    private void modifySubTableSlectState(boolean NewSelect){
        /*  判断是否是手动改变。
            如果是，则应该改变附加权限表中的选中状态；
            如果否，则是因为附加权限表中值修改引起的主权限表的值修改，那么就不应该再引起附加权限表的修改，从而导致相互修改的死循环
        */
        ifCauseModifyOther = false;
        try{
            for (int i=0;i<jTableSubAuthoritys.getRowCount();i++){
                String AuthorityItem = (String)jTableSubAuthoritys.getValueAt(i,2);
                String AnotherName = (String)jTableSubAuthoritys.getValueAt(i,1);
                //检测有无权限，如果没有，则取消设置权限的操作
                if (!CommonParas.ifHaveSubAuthorityAnotherName(AuthorityItem,AnotherName, sFileName))  continue;
                //System.out.println(jTableSubAuthoritys.convertRowIndexToModel(i));
                subAuthoritysTableModel.setValueAt(NewSelect, jTableSubAuthoritys.convertRowIndexToModel(i), 0);
                //jTableSubAuthoritys.setValueAt(NewSelect, i, 0);
                subAuthoritysTableModel.fireTableDataChanged();
                jTableSubAuthoritys.repaint();
            }
        } catch(Exception e){
            TxtLogger.append(sFileName, "modifySubTableSlectState()","系统在改变附加权限表中的选中状态的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
    
    /**
        *函数:  modifyMainTableSlectState
        *函数描述:  改变主权限表中的选中状态
    */
    private void modifyMainTableSlectState(){
        /*  判断是否是手动改变。
            如果是，则应该改变附加权限表中的选中状态；
            如果否，则是因为附加权限表中值修改引起的主权限表的值修改，那么就不应该再引起附加权限表的修改，从而导致相互修改的死循环
        */
        ifCauseModifyOther = false;
        /**
         * 当全部是未选择的时候，主权限为未选择
         * 当有一个选择，主权限为选择
         * 当MousePressed->MouseReleased（鼠标按下，过一段时间，释放鼠标），会“逃出”Clicked事件的检测。但是也逃不出Save按钮的检测。
        */
        try{

            int CountSub = jTableSubAuthoritys.getRowCount();

            //主权限值
            boolean NewMainSelect = (Boolean)jTableUserAuthoritys.getValueAt(iRowStanard,0);

            int UnSelectedRows = 0;//记录为false的值的个数
            int SelectedRows = 0;//记录为true的值的个数
            for (int i=0;i<CountSub;i++){
                boolean tem = (boolean)jTableSubAuthoritys.getValueAt(i,0);
                if (tem == false) UnSelectedRows ++;
                if (tem == true) SelectedRows ++;
            }


            if (SelectedRows > 0){
                //当有一个选择true，主权限为选择true,且刚才主权限为false时，进行设置。如果刚才已经为true，则不必进行选择。
                if (NewMainSelect == false) {
                    //jTableUserAuthoritys.setValueAt(!NewSelect, iRowStanard, 0);
                    //不知道为什么上面设置后竟然没有立即出现结果，直到该行失去焦点时才出现结果。
                    //而给jTableSubAuthoritys赋值时则可以及时出现。没办法，只好采用下面的方法。
                    userAuthoritysTableModel.setValueAt(true, iRowStanard, 0);
                    userAuthoritysTableModel.fireTableCellUpdated(iRowStanard, 0);
                }
            }else if (SelectedRows == 0){

                if (UnSelectedRows == CountSub){
                    userAuthoritysTableModel.setValueAt(false, iRowStanard, 0);
                    userAuthoritysTableModel.fireTableCellUpdated(iRowStanard, 0);
                }else{
                    TxtLogger.append(sFileName, "modifyMainTableSlectState()","系统出现未知错误，请检查");
                }
            }

        } catch(Exception e){
            TxtLogger.append(sFileName, "modifyMainTableSlectState()","系统在改变主权限表中的选中状态的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
}
