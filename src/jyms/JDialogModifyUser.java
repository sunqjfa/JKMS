/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import java.awt.event.ItemEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;
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
public class JDialogModifyUser extends javax.swing.JDialog {

    
    private final String sFileName = this.getClass().getName() + ".java";
    
    private ArrayList<UserAuthoritysBean> listUserAuthoritys = new ArrayList<>();//获取标准权限表中的用户名、权限项目、有无权限、备注等信息
    private ArrayList listSubAuthoritys = new ArrayList();//获取标准权限表中的用户名、权限项目、设备序列号、设备别名、有否等信息
    
    private ArrayList<StandardAuthoritysBean> listStandardAuthoritys = new ArrayList<>();//超级管理员添加用户的标准权限。
    private ArrayList<DeviceParaBean> listDevicePara = new ArrayList<>();//获取设备参数表中的 设备序列号,IP地址,端口号,用户名,密码,别名,设备类型代码等参数
    
    private JTableCustomizeModel userAuthoritysTableModel ;
    private JTableCustomizeModel subAuthoritysTableModel ;
    private boolean ifCauseModifyOther = false;//改变其中一个列表，是否引起对另一个列表的修改。即如果是程序运行引起的值更改，不能引起另一个的表修改;用户手动修改可以引起另外一个表的修改
    private String sUserTypeCode;//待修改的用户级别代码；默认是管理员权限
    private String sUserType;//待修改的用户级别；默认是管理员权限
    private final String sUserTypeCodeOriginal;//保留起初的用户级别代码
    private final String sUserName;//待修改的用户的用户名
    private boolean bFirstSelect = true;//是否窗口初始化过程中选择JComboBox，因为编码选择也是触发了ItemStateChanged事件
    private int iState = 0;//1表示修改成功；0表示修改失败；-1取消操作
    private ArrayList<Boolean> listAuthoritySelectedOriginal = new ArrayList<>();//存放原始的选择
//    private ArrayList<Boolean> listAuthoritySelected = new ArrayList<>();////存放实时的选择
    private ArrayList<Boolean> listSubSelectedOriginal = new ArrayList<>();//存放附加权限表原始的选择
//    private ArrayList<Boolean> listSubSelected = new ArrayList<>();//记录附加权限表当前行的实际值
    private int iRowStanard = 0;//用户权限表中的当前行
    private boolean ifStandardAuthotiry = false;//是否使用标准权限重新进行权限设定
//    private ArrayList<String> ListUsers;
    /**
     * Creates new form JDialogModifyUser
     * @param parent
     * @param modal
     * @param ListUser
     * @para    InserOrModify    1表示添加；0表示修改
     * @para    ListUser包括用户名,类型代码,类型名称（代码表中的代码名称），密码,密码级别等用户信息
     */
    public JDialogModifyUser(java.awt.Frame parent, boolean modal, ArrayList<String> ListUser) {
        super(parent, modal);
        initComponents();
        modifyLocales();
        //ListUser包括用户名,类型代码,类型名称（代码表中的代码名称），密码,密码级别等用户信息
        //所有的项都是用户的资料。
        jLabelMessage.setText(ListUser.get(4));
        sUserTypeCode = ListUser.get(1);
        sUserType  = ListUser.get(2);
        sUserTypeCodeOriginal = sUserTypeCode;
        sUserName = ListUser.get(0);
        
        initialDialogParas();
        
    }
    
    /**
	 * 函数:      initialDialogParas
         * 函数描述:  初始化窗口参数
    */
    private void initialDialogParas(){
        userAuthoritysTableModel = new JTableCheckBoxModelAuthority();//this.initialUserAuthoritysTableModel();
        jTableUserAuthoritys.setModel(userAuthoritysTableModel);
    
        subAuthoritysTableModel = new JTableCheckBoxModelSubAuthority();//this.initialSubAuthoritysTableModel();
        jTableSubAuthoritys.setModel(subAuthoritysTableModel);
        //ListUsers获取用户名,类型代码,类型名称（代码表中的代码名称），密码,密码级别等用户信息
//        listDevicePara = DeviceParaBean.getDeviceParaList(this.sFileName);
        
        switch(CommonParas.UserState.UserTypeCode){
            case CommonParas.USER_TYPECODE_ADMIN:
                jComboBoxUserType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { CommonParas.USER_TYPE_ADMIN,CommonParas.USER_TYPE_MANAGER, CommonParas.USER_TYPE_OPERATOR }));
                break;
            case CommonParas.USER_TYPECODE_MANAGER:
                jComboBoxUserType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { CommonParas.USER_TYPE_MANAGER, CommonParas.USER_TYPE_OPERATOR }));
                break;
            case CommonParas.USER_TYPECODE_OPERATOR:
                jComboBoxUserType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { CommonParas.USER_TYPE_OPERATOR }));
                break;
            default:
                
                break;
        }
         /**
            *1、if Operator == User，只能修改密码，其他都是灰的。但是用户类型、用户名等都是该用户的值。
            * 因为在上级窗口里，只能出现操作用户以下级别的用户和自己。那么以下其余所有的情况就是Operator > User的情况了
            * 2、else if Operator == 1级，则可以做所有的工作。但是用户类型则没有“超级用户”项。所有的项都是用户的资料。
            * 3、else if Operator == 2级，用户类型是灰的其它的都可以修改，显示的是操作员。但是只能在自己的权限范围之内。所有的项都是用户的资料。
            * 4、 注意：Operator == 3级已经包含在第1项中
        */
        //1、if Operator == User，只能修改密码，其他都是灰的。但是用户类型、用户名等都是该用户的值。
        if (CommonParas.UserState.UserName.equals(sUserName)) {
            jTextFieldUserName.setEnabled(false);
            jComboBoxUserType.setEnabled(false);
            jTableUserAuthoritys.setEnabled(false);
            jTableSubAuthoritys.setEnabled(false);
        }
        // 2、else if Operator == 1级（创建比自己低级的用户），则可以做所有的工作。但是用户类型则没有“超级用户”项(前面已经加上，所以现在要删除)。所有的项都是用户的资料。
        if (CommonParas.UserState.UserTypeCode.equals(CommonParas.USER_TYPECODE_ADMIN)){
            jTextFieldUserName.setEnabled(false);
            if (!(CommonParas.UserState.UserName.equals(sUserName))) jComboBoxUserType.removeItem(CommonParas.USER_TYPE_ADMIN);
         }
        if (CommonParas.UserState.UserTypeCode.equals(CommonParas.USER_TYPECODE_MANAGER)){
            if (!(CommonParas.UserState.UserName.equals(sUserName))) jComboBoxUserType.removeItem(CommonParas.USER_TYPECODE_MANAGER);
            jComboBoxUserType.removeItem(CommonParas.USER_TYPE_ADMIN);
            jTextFieldUserName.setEnabled(false);
            jComboBoxUserType.setEnabled(false);
         }

        jTextFieldUserName.setText(sUserName);
        jComboBoxUserType.setSelectedItem(sUserType);
        
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
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanelUserInfo = new javax.swing.JPanel();
        jLabelUserName = new javax.swing.JLabel();
        jTextFieldUserName = new javax.swing.JTextField();
        jRadioButtonUserType = new javax.swing.JLabel();
        jComboBoxUserType = new javax.swing.JComboBox<>();
        jLabelPassword = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabelPassword2 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jLabelMessage = new javax.swing.JLabel();
        jPanelHeader = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jButtonExit = new javax.swing.JButton();
        jPanelUserRights = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableUserAuthoritys = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableSubAuthoritys = new javax.swing.JTable();
        jLabelSubTitle = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("修改用户资料");
        setUndecorated(true);
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanelUserInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "用户信息", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 16))); // NOI18N

        jLabelUserName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelUserName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelUserName.setText("用户名");

        jTextFieldUserName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jRadioButtonUserType.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonUserType.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jRadioButtonUserType.setText("用户类型");

        jComboBoxUserType.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxUserType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxUserTypeItemStateChanged(evt);
            }
        });

        jLabelPassword.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelPassword.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPassword.setText("密码");

        jPasswordField1.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelPassword2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelPassword2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPassword2.setText("确认密码");

        jPasswordField2.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jLabelMessage.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelMessage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanelUserInfoLayout = new javax.swing.GroupLayout(jPanelUserInfo);
        jPanelUserInfo.setLayout(jPanelUserInfoLayout);
        jPanelUserInfoLayout.setHorizontalGroup(
            jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUserInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelUserName, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jLabelPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldUserName, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                    .addComponent(jPasswordField1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelUserInfoLayout.createSequentialGroup()
                        .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jRadioButtonUserType, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                            .addComponent(jLabelPassword2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPasswordField2)
                            .addComponent(jComboBoxUserType, 0, 152, Short.MAX_VALUE)))
                    .addComponent(jLabelMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(135, Short.MAX_VALUE))
        );
        jPanelUserInfoLayout.setVerticalGroup(
            jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUserInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelUserName)
                    .addComponent(jTextFieldUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButtonUserType)
                    .addComponent(jComboBoxUserType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPassword)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPassword2)
                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanelUserInfo, java.awt.BorderLayout.CENTER);

        jLabelTitle.setFont(new java.awt.Font("微软雅黑", 1, 18)); // NOI18N
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("修改用户资料");

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
                .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelHeaderLayout.setVerticalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHeaderLayout.createSequentialGroup()
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
            .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel4.add(jPanelHeader, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelUserRightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelUserRightsLayout.createSequentialGroup()
                        .addComponent(jLabelSubTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        jPanelUserRightsLayout.setVerticalGroup(
            jPanelUserRightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelUserRightsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelUserRightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addGroup(jPanelUserRightsLayout.createSequentialGroup()
                        .addComponent(jLabelSubTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(jPanelUserRights, java.awt.BorderLayout.CENTER);

        jButtonSave.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok2.png"))); // NOI18N
        jButtonSave.setText("保存");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(407, Short.MAX_VALUE)
                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonCancel, jButtonSave});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonSave))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        
        
        setTableColWidth();
        bFirstSelect = false;

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

    /**
        *函数:      tableRowSelected
        *@param Row    标准权限表中的当前行
        *函数描述:  重新过滤附加权限列表中的数据
    */
    private void tableRowSelected(int Row){
        if (userAuthoritysTableModel.getRowCount() == 0) return;
        if (Row < 0 ) return;
        if (Row > userAuthoritysTableModel.getRowCount() ) return;
        String Authorityitem = (String)userAuthoritysTableModel.getValueAt(Row, 1);
        //System.out.println("Row:"+Row + " Value:" + Authorityitem);
        //{"选中","用户权限","是否细分","权限类型","用户权限"};//第一个用户权限为代码形式，最后一个用户权限实际是备注
        jLabelSubTitle.setText((String)userAuthoritysTableModel.getValueAt(Row, 4));// + "   相关"
        
        //若要测试完全匹配，可分别使用字符 '^' 和 '$' 来匹配该字符串的开头和结尾。例如，“^foo$”只包含其字符串完全为“foo”的行，而不是“food”之类。有关受支持的正则表达式结构的完整描述，请参阅 Pattern。 
        String sFilter = "^" + Authorityitem.trim() +"$";
        TableRowSorter sorter = new TableRowSorter(subAuthoritysTableModel);
        sorter.setRowFilter(RowFilter.regexFilter(sFilter));
        jTableSubAuthoritys.setRowSorter(sorter);
    }
    
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        // TODO add your handling code here:
        iState = -1;
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jComboBoxUserTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxUserTypeItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED)
        {
            if (bFirstSelect)  return;

            sUserTypeCode = CommonParas.getUserTypeCode((String)jComboBoxUserType.getSelectedItem());

            fillIntoTableStandardAuthoritys();            
            tableRowSelected(0);
            ifStandardAuthotiry = true;//是否使用标准权限重新进行权限设定
        }
    }//GEN-LAST:event_jComboBoxUserTypeItemStateChanged
    
   
    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        // TODO add your handling code here:
        try {
            int ModifyUserData = 0;//是否执行了修改用户密码或者用户级别的操作,是为1，否为0
            int ModifyAuthority = 0;//是否执行了修改用户权限的操作,是为1，否为0
            String Password1 = new String(jPasswordField1.getPassword());
            String Password2 = new String(jPasswordField2.getPassword());
            boolean IfModifyPassword        = false;//是否修改密码
            boolean IfModifyUserType = !sUserTypeCode.equals(sUserTypeCodeOriginal);
            boolean IfModifyAuthority   = false;//是否修改用户权限
            
            
            if (!Password1.equals("")){
                IfModifyPassword = true;//是否修改密码
            }
            String Password = "";////String Password = null,UserTypeCode = null;

            if (!Password1.equals(Password2)){
                JOptionPane.showMessageDialog(null, sPassNotSame);// "两次输入的密码不一致！"
                return;
            }else{
                Password = MD5Encrypt.getMD5Str(Password1);
             }
            
            //如果是修改自己的密码，对原密码进行验证，是否是本人修改
            if (IfModifyPassword && sUserTypeCode.equals(CommonParas.UserState.UserTypeCode)){
                String PasswordOld = JOptionPane.showInputDialog(sEnterPassVerify);// "请输入原密码进行验证："
                if (PasswordOld == null) return;
                if (UsersBean.checkPasswordIfOK(sUserName,MD5Encrypt.getMD5Str(PasswordOld),sFileName) < 1) {
                    JOptionPane.showMessageDialog(null, sPassError);// "密码错误，密码验证失败！"
                    return;
                }
            }
            
            
            //修改用户权限
            ArrayList<String> ListUpdateStr = new ArrayList<>();
            //ifStandardAuthotiry表示修改了用户级别
            if (IfModifyUserType && IfModifyPassword){//修改用户密码和用户级别
                String InsertStr = UsersBean.getStringUpdateUserParas(sUserName, sUserTypeCode, Password, CommonParas.getPasswordGrade(sUserName, Password1));
                ListUpdateStr.add(InsertStr);
//                ModifyUserData = 1;
//                if (UsersBean.modifyUserParas(new UsersBean(sUserName,sUserTypeCode, Password,CommonParas.getPasswordGrade(sUserName, Password1)),sFileName) > 0 ) 
//                    ModifyUserData = 1;
//                else  {
//                    TxtLogger.append(this.sFileName, "jButtonSaveActionPerformed()","系统修改用户密码失败"); 
//                    JOptionPane.showMessageDialog(null, "修改用户资料失败！");
//                    return;
//                }
                
            }else if(IfModifyUserType && !IfModifyPassword){//修改用户级别
                String InsertStr = UsersBean.getStringUpdateUserType(sUserName, sUserTypeCode);
                ListUpdateStr.add(InsertStr);
//                ModifyUserData = 1;
            }else if (!IfModifyUserType && IfModifyPassword){//修改用户密码
                String InsertStr = UsersBean.getStringUpdatePassword(sUserName, Password, CommonParas.getPasswordGrade(sUserName, Password1));
                ListUpdateStr.add(InsertStr);
//                ModifyUserData = 1;
            }
                    
            boolean IfAdmin = CommonParas.UserState.UserTypeCode.equals(CommonParas.USER_TYPECODE_ADMIN);//是否超级管理员

            
            //如果使用标准权限输入，则需将原来的权限删除，重新加入新的权限
            if (ifStandardAuthotiry && listStandardAuthoritys.size() > 0) {
                //先将原先用户的权限删除
                ListUpdateStr.add(UserAuthoritysBean.getStringDeleteCommand(sUserName));
                ListUpdateStr.add(SubUserAuthoritysBean.getStringDeleteCommand(sUserName));
                
                //添加用户权限
                for (int i=0;i<userAuthoritysTableModel.getRowCount();i++){
                    //用户名 CHAR(16)、权限项目 VARCHAR(30)、有否	char(1)
                    //"选中","用户权限","是否细分","权限类型"
                    String IfHave = "0";
                    //因为只有超级管理员才可以修改用户的级别，才会产生标准权限，所以不需要进行权限检测。
                    if ((Boolean)userAuthoritysTableModel.getValueAt(i, 0)) IfHave = "1";

                    String InsertStr = UserAuthoritysBean.getStringInsertCommand(sUserName,(String)userAuthoritysTableModel.getValueAt(i, 1),IfHave,(String)userAuthoritysTableModel.getValueAt(i, 3));
                    ListUpdateStr.add(InsertStr);
                }
                
                //添加用户附加权限
                for (int i=0;i<subAuthoritysTableModel.getRowCount();i++){

                    //    "选中","设备","用户权限","设备序列号","权限类型"                
                    String IfHave2 = "0";
                    //因为只有超级管理员才可以修改用户的级别，才会产生标准权限，所以不需要进行权限检测。
                    if ((Boolean)subAuthoritysTableModel.getValueAt(i, 0)) IfHave2 = "1";

                    String InsertStr2 = SubUserAuthoritysBean.getStringInsertCommand(sUserName, (String)subAuthoritysTableModel.getValueAt(i, 2), (String)subAuthoritysTableModel.getValueAt(i, 3), IfHave2, (String)subAuthoritysTableModel.getValueAt(i, 4));
                    ListUpdateStr.add(InsertStr2);
                }
                
                IfModifyAuthority   = true;//是否修改用户权限
                ModifyAuthority = 1;

            }else {//如果使用原来的权限基础上修改
                if (userAuthoritysTableModel.getRowCount() != listAuthoritySelectedOriginal.size()) return;
                if (subAuthoritysTableModel.getRowCount() != listSubSelectedOriginal.size()) return;
                //listUserAuthoritys获取标准权限表中的用户名、权限项目、有无权限、备注等信息

                
                for (int i=0;i<listAuthoritySelectedOriginal.size();i++){
                    String IfHave;
    //                UserAuthoritysBean AuthoritysBean = listUserAuthoritys.get(i);
                    boolean Bvalue1 = (Boolean)userAuthoritysTableModel.getValueAt(i, 0);
                    boolean Bvalue2 = listAuthoritySelectedOriginal.get(i);
                    
                    if (Bvalue1 ==  Bvalue2) continue;//如果和初始值相等，则不必处理

                    String AuthorityItem = (String)userAuthoritysTableModel.getValueAt(i,1);
                    
                    //为了保险起见，再次检测创建者的权限。即使“MousePressed->MouseReleased（鼠标按下，过一段时间，释放鼠标）”发生被选中的情况，也会被“淘汰”出局    
                    if (!CommonParas.ifHaveUserAuthority(AuthorityItem, sFileName)) continue;
                    
                    IfModifyAuthority   = true;//是否修改用户权限
                    
                    if (Bvalue1) IfHave = "1";//取反值
                    else IfHave = "0";

                    String UpdateStr = UserAuthoritysBean.getStringUpdateCommand(sUserName,(String)userAuthoritysTableModel.getValueAt(i, 1),IfHave);
                    ListUpdateStr.add(UpdateStr);

                }
                //listSubAuthoritys获取标准权限表中的用户名、权限项目、设备序列号、设备别名、有否等信息
                for (int i=0;i<listSubSelectedOriginal.size();i++){
                    String IfHave;
                    boolean Bvalue1 = (Boolean)subAuthoritysTableModel.getValueAt(i, 0);
                    boolean Bvalue2 = listSubSelectedOriginal.get(i);
                    String NewDev = (String)subAuthoritysTableModel.getValueAt(i, 5);
                    
                    if (NewDev.equals("0") && Bvalue1 ==  Bvalue2) continue;//如果和初始值相等，且不为新添设备，则不必处理
                    
                    String AuthorityItem = (String)subAuthoritysTableModel.getValueAt(i,2);
                    String AnotherName = (String)subAuthoritysTableModel.getValueAt(i,1);
                    //为了保险起见，再次检测创建者的权限。即使“MousePressed->MouseReleased（鼠标按下，过一段时间，释放鼠标）”发生被选中的情况，也会被“淘汰”出局
                    if (!CommonParas.ifHaveSubAuthorityAnotherName(AuthorityItem,AnotherName, sFileName)) continue;
                    IfModifyAuthority   = true;//是否修改用户权限

                    if (Bvalue1) IfHave = "1";//取反值
                    else IfHave = "0";
                    
                    String UpdateStr;
                    if (NewDev.equals("0"))
                        UpdateStr = SubUserAuthoritysBean.getStringUpdateCommand(sUserName,(String)subAuthoritysTableModel.getValueAt(i, 2),(String)subAuthoritysTableModel.getValueAt(i, 3),IfHave);
                    else
                        UpdateStr = SubUserAuthoritysBean.getStringInsertCommand(sUserName, AuthorityItem, (String)subAuthoritysTableModel.getValueAt(i, 3), IfHave, (String)subAuthoritysTableModel.getValueAt(i, 4));
                    ListUpdateStr.add(UpdateStr);
                        //String UserName, String Authorityitem,String DeviceSerialno,String IfHave
                }

            }
            
            
            if (ListUpdateStr.isEmpty()) return;//不做任何处理，包括关闭窗口
            
            boolean ModifySucc = false;
            ModifySucc = UserAuthoritysBean.batchInsertUpdate(ListUpdateStr, sFileName) > 0;
            if (ModifySucc){
                if ((IfModifyPassword || IfModifyUserType) && IfModifyAuthority) {
                    iState = 1; 
                    JOptionPane.showMessageDialog(null, MessageFormat.format(sModifyUserDataRight_Succ, sUserName));//"成功修改用户 " + sUserName +" 的资料和权限"
                }else if ((IfModifyPassword || IfModifyUserType) && !IfModifyAuthority){
                    iState = 1; 
                    JOptionPane.showMessageDialog(null, MessageFormat.format(sModifyUserData_Succ, sUserName));//"成功修改用户 " + sUserName +" 的资料"
                }else if (!(IfModifyPassword || IfModifyUserType) && IfModifyAuthority){
                    iState = 1; 
                    JOptionPane.showMessageDialog(null, MessageFormat.format(sModifyUserRight_Succ, sUserName));//"成功修改用户 " + sUserName +" 的权限"
                }
            }else{
                JOptionPane.showMessageDialog(null, sModifyUserFail);// "修改用户资料失败！"
            }
            
//            if (ModifyAuthority ==1 && ModifyUserData == 1) {
//                iState = 1; 
//                JOptionPane.showMessageDialog(null, "成功修改用户 " + sUserName +" 的资料和权限");
//            }else if (ModifyAuthority ==0 && ModifyUserData == 1){
//                iState = 1; 
//                JOptionPane.showMessageDialog(null, "成功修改用户 " + sUserName +" 的资料");
//            }else if (ModifyAuthority ==1 && ModifyUserData == 0){
//                iState = 1; 
//                JOptionPane.showMessageDialog(null, "成功修改用户 " + sUserName +" 的权限");
//            }else{
//                iState = 0;
////                JOptionPane.showMessageDialog(null, "成功修改用户 " + sUserName +" 的密码和权限");
//            }
            
        }catch(Exception e){
            TxtLogger.append(this.sFileName, "jButtonSaveActionPerformed()","系统在保存用户资料的过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
        this.dispose();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        // TODO add your handling code here:
        jButtonCancel.doClick();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jTableUserAuthoritysMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableUserAuthoritysMousePressed
        // TODO add your handling code here:
        ifCauseModifyOther = true;
//        int Row = jTableUserAuthoritys.getSelectedRow();
//                    
//        if (Row < 0 ) return;
//        iRowStanard = Row;//用户权限表中的当前行
//        tableRowSelected(Row);
    }//GEN-LAST:event_jTableUserAuthoritysMousePressed

    private void jTableSubAuthoritysMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSubAuthoritysMousePressed
        // TODO add your handling code here:
        ifCauseModifyOther = true;
    }//GEN-LAST:event_jTableSubAuthoritysMousePressed

    public void procedureModifySelected(int Row, boolean NewSelected){
        userAuthoritysTableModel.setValueAt(NewSelected, Row, 0);
        userAuthoritysTableModel.fireTableCellUpdated(Row, 0);
    }
    public void procedureModifySubSelected(int Row, boolean NewSelected){
        subAuthoritysTableModel.setValueAt(NewSelected, Row, 0);
        subAuthoritysTableModel.fireTableCellUpdated(Row, 0);
    }

    
    
    private void fillIntoTableAuthoritys(){
        
        //switch(CommonParas.UserState.UserTypeCode){
        switch(sUserTypeCode){
            case CommonParas.USER_TYPECODE_ADMIN:
                fillIntoTableStandardAuthoritys();
                break;
            default:
                fillIntoTableManagerAuthoritys();
                break;
        }
    }
    /**
        *函数:      fillIntoTableStandardAuthoritys
        *函数描述:  从数据库中提取所有符合条件的标准权限到JTable中
    */
    private void fillIntoTableStandardAuthoritys(){
        try {
            refeshStandardAuthoritysList(sUserTypeCode);//刷新
 
            for (int i=0;i<listStandardAuthoritys.size();i++){
                //以UserAuthoritysBean形式输出的StandardAuthoritysBean
                StandardAuthoritysBean StandardBean = listStandardAuthoritys.get(i);
                //获取标准权限表中的用户类型代码、权限项目、是否细分、备注等信息
                
                //{"选中","用户权限","是否细分","权限类型","用户权限"};//最后一个用户权限实际是备注
                Vector newRow = new Vector();
                
                newRow.add(true);
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
                        newRow2.add(true);
                        listSubSelectedOriginal.add(true);
                        newRow2.add(deviceParaBean.getAnothername());
                        newRow2.add(StandardBean.getAuthorityitem());
                        newRow2.add(deviceParaBean.getSerialNO());
                        newRow2.add(AuthorityType);
                        newRow2.add("1");//新添设备
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
                TxtLogger.append(this.sFileName, "fillIntoTableStandardAuthoritys()","系统在刷新管理设备表过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }

    }
    /**
        *函数:      fillIntoTableManagerAuthoritys
        *函数描述:  从数据库中提取所有符合条件的标准权限到JTable中
    */
    private void fillIntoTableManagerAuthoritys(){
        try {
            refeshAuthoritysData();//刷新
            //listUserAuthoritys获取用户权限表中的用户名、权限项目、有无权限、备注等信息
            for (int i=0;i<listUserAuthoritys.size();i++){
                UserAuthoritysBean AuthoritysBean = listUserAuthoritys.get(i);
                //"选中","用户权限","是否细分","权限类型"
                Vector newRow = new Vector();
                if (AuthoritysBean.getIfhave().equals("1")) {
                    newRow.add(true);
//                    listAuthoritySelected.add(true);
                    listAuthoritySelectedOriginal.add(true);
                }else{
                    newRow.add(false);
//                    listAuthoritySelected.add(false);
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
            //"选中","设备","用户权限","设备序列号","权限类型"
            for (int j=0;j<listSubAuthoritys.size();j++){
                ArrayList SubList = (ArrayList)listSubAuthoritys.get(j);
                Vector newRow2 = new Vector();
                //"选中","设备","用户权限","设备序列号","权限类型"
                if (((String)SubList.get(4)).equals("1")) {
                    newRow2.add(true);
                    listSubSelectedOriginal.add(true);
//                    listSubSelected.add(true);
                }else{
                    newRow2.add(false);
                    listSubSelectedOriginal.add(false);
//                    listSubSelected.add(false);
                }
                newRow2.add((String)SubList.get(3));//设备别名
                newRow2.add((String)SubList.get(1));//权限项目
                newRow2.add((String)SubList.get(2));//设备序列号
                newRow2.add((String)SubList.get(5));//备注。即权限类型，对应标准权限表的用户类型
                newRow2.add((String)SubList.get(6));//是否新添设备
                subAuthoritysTableModel.addRow(newRow2);
                
            }
            subAuthoritysTableModel.fireTableDataChanged();
            jTableSubAuthoritys.repaint();

        }catch(Exception e)
            {
                TxtLogger.append(this.sFileName, "fillIntoTableManagerAuthoritys()","系统在刷新管理设备表过程中，出现错误" + 
                                "\r\n                       Exception:" + e.toString());   
            }

    }
    /**
        *函数:      refeshAuthoritysData
        *函数描述:  重新刷新用户权限列表listUserAuthoritys和附加权限列表listSubAuthoritys
    */
    private void refeshAuthoritysData(){
        try {
            
            if (listUserAuthoritys != null ) listUserAuthoritys.clear();//已管理设备的序列号数组
            listUserAuthoritys = UserAuthoritysBean.getUserAuthoritysList(sUserName,sFileName);
            if (listSubAuthoritys != null ) listSubAuthoritys.clear();//已管理设备的序列号数组
            listSubAuthoritys = SubUserAuthoritysBean.getSubUserAuthoritysList(sUserName,sFileName);
            
            int DevNumsOfSubAuthoritys = SubUserAuthoritysBean.getAuthorityDeviceNums(sUserName, sFileName);
            if (DevNumsOfSubAuthoritys < CommonParas.g_listDeviceDetailPara.size()){
                ArrayList NewSubAuthoritysList = SubUserAuthoritysBean.getNewSubUserAuthoritysList(sUserName, sFileName);
                listSubAuthoritys.addAll(NewSubAuthoritysList);
//                //往附加权限表中添加与该项权限项目相关的设备权限  
//                //获取用户权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息
//                //"选中","设备","用户权限","设备序列号","权限类型"
//                for (int j=0;j<listSubAuthoritys.size();j++){
//                    ArrayList SubList = (ArrayList)listSubAuthoritys.get(j);
//                    Vector newRow2 = new Vector();
//                    //"选中","设备","用户权限","设备序列号","权限类型"
//                    if (((String)SubList.get(4)).equals("1")) {
//                        newRow2.add(true);
//                        listSubSelectedOriginal.add(true);
//    //                    listSubSelected.add(true);
//                    }else{
//                        newRow2.add(false);
//                        listSubSelectedOriginal.add(false);
//    //                    listSubSelected.add(false);
//                    }
//                    newRow2.add((String)SubList.get(3));//设备别名
//                    newRow2.add((String)SubList.get(1));//权限项目
//                    newRow2.add((String)SubList.get(2));//设备序列号
//                    newRow2.add((String)SubList.get(5));//备注。即权限类型，对应标准权限表的用户类
//                    subAuthoritysTableModel.addRow(newRow2);
//
//                }
            }
                
            listAuthoritySelectedOriginal.clear();
            listSubSelectedOriginal.clear();
            Vector v = userAuthoritysTableModel.getDataVector();
            if (v != null) v.clear();
            Vector v2= subAuthoritysTableModel.getDataVector();
            if (v2 != null) v2.clear();
        }catch(Exception e){
            TxtLogger.append(sFileName, "refeshAuthoritysData()","系统在重新刷新用户权限列表过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
    /**
        *函数:      refeshStandardAuthoritysList
        *@param UserTypeCode    用户类型代码
        *函数描述:  重新刷新标准用户权限列表listStandardAuthoritys
    */
    private void refeshStandardAuthoritysList(String UserTypeCode){
        try {
            
            if (listStandardAuthoritys != null ) listStandardAuthoritys.clear();//已管理设备的序列号数组
            listStandardAuthoritys = StandardAuthoritysBean.getStandardAuthoritysList(UserTypeCode,sFileName);
            if (listDevicePara != null ) listDevicePara.clear();//已管理设备的序列号数组
            listDevicePara = DeviceParaBean.getDeviceParaList(this.sFileName);
        
            listAuthoritySelectedOriginal.clear();
            listSubSelectedOriginal.clear();
            Vector v = userAuthoritysTableModel.getDataVector();
            if (v != null) v.clear();
            Vector v2= subAuthoritysTableModel.getDataVector();
            if (v2 != null) v2.clear();
        }catch(Exception e)
        {
            TxtLogger.append(this.sFileName, "refeshStandardAuthoritysList()","系统在重新刷新标准用户权限列表过程中，出现错误" + 
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
    
        //sTableUserAuthoritysTitle = new String[] {"选中","用户权限","是否细分","权限类型"};
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
        //String[] sTableSubAuthoritysTitleModify;
        //sTableSubAuthoritysTitleModify = new String[] {"选中","设备","用户权限","设备序列号","权限类型","新添设备否"};
        JTableCheckBoxModel  SubUserAuthoritysTableModel =new JTableCheckBoxModel(sTableSubAuthoritysTitleModify);
        return SubUserAuthoritysTableModel;
    }
    /**
	 * 函数:      setTableColWidth
         * 函数描述:  设置表格特殊列的宽度
    */
    private void setTableColWidth(){
        TableColumnModel tcmUserAuthoritys = jTableUserAuthoritys.getColumnModel();
        tcmUserAuthoritys.getColumn(0).setMinWidth(30);
        tcmUserAuthoritys.getColumn(0).setMaxWidth(120);
        tcmUserAuthoritys.getColumn(0).setPreferredWidth(90);
        tcmUserAuthoritys.getColumn(1).setMinWidth(0);
        tcmUserAuthoritys.getColumn(1).setMaxWidth(0);
        tcmUserAuthoritys.getColumn(1).setWidth(0);
        tcmUserAuthoritys.getColumn(2).setMinWidth(0);
        tcmUserAuthoritys.getColumn(2).setMaxWidth(0);
        tcmUserAuthoritys.getColumn(2).setPreferredWidth(0);
        tcmUserAuthoritys.getColumn(3).setMinWidth(0);
        tcmUserAuthoritys.getColumn(3).setMaxWidth(0);
        tcmUserAuthoritys.getColumn(3).setPreferredWidth(0);
        
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
        tcmSubAuthoritys.getColumn(5).setMinWidth(0);
        tcmSubAuthoritys.getColumn(5).setMaxWidth(0);
        tcmSubAuthoritys.getColumn(5).setWidth(0);

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
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    private void modifyLocales(){
        
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作

        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        //信息显示
        sTableUserAuthoritysTitle = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sTableUserAuthoritysTitle").split(",");  //选中,用户权限,是否细分,权限类型
        sTableSubAuthoritysTitleModify = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sTableSubAuthoritysTitleModify").split(",");  //选中,设备,用户权限,设备序列号,权限类型,新添设备否
        sEnterPassVerify = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sEnterPassVerify");  //请输入原密码进行验证：
        sPassError = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sPassError");  //密码错误，密码验证失败！
        sModifyUserDataRight_Succ = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sModifyUserDataRight_Succ");  //成功修改用户“{0}”的资料和权限
        sModifyUserData_Succ = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sModifyUserData_Succ");  //成功修改用户“{0}”的资料
        sModifyUserRight_Succ = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sModifyUserRight_Succ");  //成功修改用户“{0}”的权限
        sModifyUserFail = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sModifyUserFail");  //修改用户资料失败！
        sModifyUser = Locales.getString("JInFrameUserManage", "JInFrameUserManage.sModifyUser");  //修改用户资料

        this.setTitle(sModifyUser);
        jLabelTitle.setText(sModifyUser);//	添加用户


        //标签和按钮显示
        jLabelUserName.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jLabelUserName"));  //用户名
        jLabelPassword.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jLabelPassword"));  //密  码
        jLabelPassword2.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jLabelPassword2"));  //确认密码
        jRadioButtonUserType.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jRadioButtonUserType"));  //用户类型
        jButtonSave.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jButtonSave"));  //保 存
        jButtonCancel.setText(Locales.getString("JInFrameUserManage", "JInFrameUserManage.jButtonCancel"));  //取 消

        ((TitledBorder)jPanelUserInfo.getBorder()).setTitle(   Locales.getString("JInFrameUserManage", "JInFrameUserManage.jPanelUserInfo"));  //用户信息
        ((TitledBorder)jPanelUserRights.getBorder()).setTitle( Locales.getString("JInFrameUserManage", "JInFrameUserManage.jPanelUserRights"));  //用户权限

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JComboBox<String> jComboBoxUserType;
    private javax.swing.JLabel jLabelMessage;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JLabel jLabelPassword2;
    private javax.swing.JLabel jLabelSubTitle;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelUserName;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelUserInfo;
    private javax.swing.JPanel jPanelUserRights;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JLabel jRadioButtonUserType;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableSubAuthoritys;
    private javax.swing.JTable jTableUserAuthoritys;
    private javax.swing.JTextField jTextFieldUserName;
    // End of variables declaration//GEN-END:variables

    
    private String[] sTableUserAuthoritysTitle = new String[] {"选中","用户权限","是否细分","权限类型","用户权限"};//最后一个用户权限实际是备注
    private String[] sTableSubAuthoritysTitleModify = new String[] {"选中","设备","用户权限","设备序列号","权限类型","新添设备否"};
    private String sModifyUser = "修改用户资料";
    private String sPassNotSame = "两次输入的密码不一致！";
    private String sEnterPassVerify = "请输入原密码进行验证：";
    private String sPassError = "密码错误，密码验证失败！";
    private String sModifyUserDataRight_Succ = "成功修改用户“{0}”的资料和权限";
    private String sModifyUserData_Succ = "成功修改用户“{0}”的资料";
    private String sModifyUserRight_Succ = "成功修改用户“{0}”的权限";
    private String sModifyUserFail = "修改用户资料失败！";

    
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
            super(sTableSubAuthoritysTitleModify);// new String[] {"选中","设备","用户权限","设备序列号","权限类型","新添设备否"}
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
