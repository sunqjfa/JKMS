/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import jyms.data.TimeTemplateBean;
import jyms.data.TxtLogger;
import jyms.ui.SliderUI;


/**
 *
 * @author John
 */
public class JDialogTimeTemplate extends javax.swing.JDialog {
    
    private final String sFileName = this.getClass().getName() + ".java";
    private final int iPanelDeviceHeight;
    private final int iJButtonCanelXWidth;
    private final int iFrameHeight;
    private final int iFrameWidth;
    
    private boolean bAllDay;//是否全天模式
    private boolean bWorkDay;//是否工作日模式
    private String sTimeTemplateName;//时间模板名称
    int iInsertOrUpdate;//0添加；1修改；
    private int iState = -1;//0添加；1修改；-1取消
    ArrayList<String> ListInsertOrUpdateStr = new ArrayList<>();//保存添加或者修改的SQL语句列表
    
    private ArrayList<TimeTemplateBean> listTimeTemplateBean;//

    public HCNetSDK.NET_DVR_SCHEDTIMEWEEK[] strSchedTimeWeek; //每天一个时段，共7天
    //参数传递模式。“listTimeTemplateBean”表示是以listTimeTemplateBean为参数传递；“strSchedTimeWeek”表示是以strSchedTime为参数传递；
    private String sMode;
    
    /**
     * Creates new form JDialogTimeTemplate
     * @param parent
     * @param modal
     */
    public JDialogTimeTemplate(java.awt.Frame parent, boolean modal) {
        this(parent, modal,0,null);
    }
    /**
     * Creates new form JDialogTimeTemplate
     * @param parent
     * @param modal
     * @param InserOrUpdate
     * @param listTimeTemplate
     */
    public JDialogTimeTemplate(java.awt.Frame parent, boolean modal,int InserOrUpdate,ArrayList<TimeTemplateBean> listTimeTemplate) {
        super(parent, modal);
        initComponents();
        modifyLocales();
        iPanelDeviceHeight = jPanelWeek1.getHeight(); 
        if ((jLabelUserDefined.getX() + jLabelUserDefined.getWidth())>(jPanelWeek1.getX() + jPanelWeek1.getWidth())) 
            iJButtonCanelXWidth = jLabelUserDefined.getX() + jLabelUserDefined.getWidth()+30;
        else iJButtonCanelXWidth = jPanelWeek1.getX() + jPanelWeek1.getWidth()+30;
        
        iFrameHeight = this.getHeight();
        iFrameWidth = this.getWidth();
        this.iInsertOrUpdate = InserOrUpdate;
        if (listTimeTemplate == null) listTimeTemplateBean = new ArrayList<>();
        else listTimeTemplateBean = listTimeTemplate;
        sMode = "listTimeTemplateBean";
    }
    /**
     * Creates new form JDialogTimeTemplate
     * @param parent
     * @param modal
     * @param SchedTime
     * @param TemplateName
     */
    public JDialogTimeTemplate(java.awt.Frame parent, boolean modal, String TemplateName, HCNetSDK.NET_DVR_SCHEDTIMEWEEK[] SchedTime) {
        super(parent, modal);
        initComponents();
        modifyLocales();
        iPanelDeviceHeight = jPanelRecordSchedInfo1.getHeight()+10; 
        if ((jLabelUserDefined.getX() + jLabelUserDefined.getWidth())>(jPanelWeek1.getX() + jPanelWeek1.getWidth())) 
            iJButtonCanelXWidth = jLabelUserDefined.getX() + jLabelUserDefined.getWidth()+30;
        else iJButtonCanelXWidth = jPanelWeek1.getX() + jPanelWeek1.getWidth()+30;
        
        iFrameHeight = this.getHeight();
        iFrameWidth = this.getWidth();
        
        this.strSchedTimeWeek = SchedTime;//每天一个时段，共7天
        sMode = "strSchedTimeWeek";
        if (TemplateName == null || TemplateName.equals("") || TemplateName.equals(sTemplates[2])) {// "添加模板"
            //如果是新建模板，可修改名称
//            jCheckBoxModify.setSelected(true);
//            jCheckBoxModify.setText("新建模板");//存储该模板
            this.iInsertOrUpdate = 0;
        } else {
            //如果是修改模板，则不允许修改名称，可以修改布防时间，但是修改不了原先已经使用该模板的设备布防情况（在报警参数设置窗口实现）
            jTextFieldTemplateName.setText(TemplateName);
            jTextFieldTemplateName.setEditable(false);
            jComboBoxUserDefined.setSelectedItem(sModes[2]);//"自定义模式"
            this.iInsertOrUpdate = 1;
        }
        showStrSchedTimeWeek();
        slider11.setUI(new SliderUI(slider11));
        slider12.setUI(new SliderUI(slider12));
        slider21.setUI(new SliderUI(slider21));
        slider22.setUI(new SliderUI(slider22));
        slider31.setUI(new SliderUI(slider31));
        slider32.setUI(new SliderUI(slider32));
        slider41.setUI(new SliderUI(slider41));
        slider42.setUI(new SliderUI(slider42));
        slider51.setUI(new SliderUI(slider51));
        slider52.setUI(new SliderUI(slider52));
        slider61.setUI(new SliderUI(slider61));
        slider62.setUI(new SliderUI(slider62));
        slider71.setUI(new SliderUI(slider71));
        slider72.setUI(new SliderUI(slider72));


    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupRecordModel = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jPanelHeader = new javax.swing.JPanel();
        jLabelTemplateName = new javax.swing.JLabel();
        jTextFieldTemplateName = new javax.swing.JTextField();
        jButtonOK = new javax.swing.JButton();
        jButtonCanel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jRadioButtonAllDay = new javax.swing.JRadioButton();
        jRadioButtonWorkDay = new javax.swing.JRadioButton();
        jRadioButtonUserDefined = new javax.swing.JRadioButton();
        jComboBoxUserDefined = new javax.swing.JComboBox<>();
        jLabelUserDefined = new javax.swing.JLabel();
        jPanelTimeTemplateTitle = new javax.swing.JPanel();
        jLabelTimeTemplate = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jPanelTimeTemplate = new javax.swing.JPanel();
        jPanelRecordSchedInfo1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabelWeekTitle1 = new javax.swing.JLabel();
        jPanelWeek1 = new javax.swing.JPanel();
        jSpinnerMinute12 = new javax.swing.JSpinner();
        jLabelHour12 = new javax.swing.JLabel();
        slider12 = new javax.swing.JSlider();
        slider11 = new javax.swing.JSlider();
        jLabelMinute12 = new javax.swing.JLabel();
        jSpinnerHour12 = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jLabelMinute11 = new javax.swing.JLabel();
        jSpinnerMinute11 = new javax.swing.JSpinner();
        jLabelHour11 = new javax.swing.JLabel();
        jSpinnerHour11 = new javax.swing.JSpinner();
        jPanelRecordSchedInfo2 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabelWeekTitle2 = new javax.swing.JLabel();
        jPanelWeek2 = new javax.swing.JPanel();
        jSpinnerMinute22 = new javax.swing.JSpinner();
        jLabelHour22 = new javax.swing.JLabel();
        slider22 = new javax.swing.JSlider();
        slider21 = new javax.swing.JSlider();
        jLabelMinute22 = new javax.swing.JLabel();
        jSpinnerHour22 = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jLabelMinute21 = new javax.swing.JLabel();
        jSpinnerMinute21 = new javax.swing.JSpinner();
        jLabelHour21 = new javax.swing.JLabel();
        jSpinnerHour21 = new javax.swing.JSpinner();
        jPanelRecordSchedInfo3 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabelWeekTitle3 = new javax.swing.JLabel();
        jPanelWeek3 = new javax.swing.JPanel();
        jSpinnerMinute32 = new javax.swing.JSpinner();
        jLabelHour32 = new javax.swing.JLabel();
        slider32 = new javax.swing.JSlider();
        slider31 = new javax.swing.JSlider();
        jLabelMinute32 = new javax.swing.JLabel();
        jSpinnerHour32 = new javax.swing.JSpinner();
        jLabel46 = new javax.swing.JLabel();
        jLabelMinute31 = new javax.swing.JLabel();
        jSpinnerMinute31 = new javax.swing.JSpinner();
        jLabelHour31 = new javax.swing.JLabel();
        jSpinnerHour31 = new javax.swing.JSpinner();
        jPanelRecordSchedInfo4 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabelWeekTitle4 = new javax.swing.JLabel();
        jPanelWeek4 = new javax.swing.JPanel();
        jSpinnerMinute42 = new javax.swing.JSpinner();
        jLabelHour42 = new javax.swing.JLabel();
        slider42 = new javax.swing.JSlider();
        slider41 = new javax.swing.JSlider();
        jLabelMinute42 = new javax.swing.JLabel();
        jSpinnerHour42 = new javax.swing.JSpinner();
        jLabel52 = new javax.swing.JLabel();
        jLabelMinute41 = new javax.swing.JLabel();
        jSpinnerMinute41 = new javax.swing.JSpinner();
        jLabelHour41 = new javax.swing.JLabel();
        jSpinnerHour41 = new javax.swing.JSpinner();
        jPanelRecordSchedInfo5 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabelWeekTitle5 = new javax.swing.JLabel();
        jPanelWeek5 = new javax.swing.JPanel();
        jSpinnerMinute52 = new javax.swing.JSpinner();
        jLabelHour52 = new javax.swing.JLabel();
        slider52 = new javax.swing.JSlider();
        slider51 = new javax.swing.JSlider();
        jLabelMinute52 = new javax.swing.JLabel();
        jSpinnerHour52 = new javax.swing.JSpinner();
        jLabel58 = new javax.swing.JLabel();
        jLabelMinute51 = new javax.swing.JLabel();
        jSpinnerMinute51 = new javax.swing.JSpinner();
        jLabelHour51 = new javax.swing.JLabel();
        jSpinnerHour51 = new javax.swing.JSpinner();
        jPanelRecordSchedInfo6 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabelWeekTitle6 = new javax.swing.JLabel();
        jPanelWeek6 = new javax.swing.JPanel();
        jSpinnerMinute62 = new javax.swing.JSpinner();
        jLabelHour62 = new javax.swing.JLabel();
        slider62 = new javax.swing.JSlider();
        slider61 = new javax.swing.JSlider();
        jLabelMinute62 = new javax.swing.JLabel();
        jSpinnerHour62 = new javax.swing.JSpinner();
        jLabel64 = new javax.swing.JLabel();
        jLabelMinute61 = new javax.swing.JLabel();
        jSpinnerMinute61 = new javax.swing.JSpinner();
        jLabelHour61 = new javax.swing.JLabel();
        jSpinnerHour61 = new javax.swing.JSpinner();
        jPanelRecordSchedInfo7 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jLabelWeekTitle7 = new javax.swing.JLabel();
        jPanelWeek7 = new javax.swing.JPanel();
        jSpinnerMinute72 = new javax.swing.JSpinner();
        jLabelHour72 = new javax.swing.JLabel();
        slider72 = new javax.swing.JSlider();
        slider71 = new javax.swing.JSlider();
        jLabelMinute72 = new javax.swing.JLabel();
        jSpinnerHour72 = new javax.swing.JSpinner();
        jLabel70 = new javax.swing.JLabel();
        jLabelMinute71 = new javax.swing.JLabel();
        jSpinnerMinute71 = new javax.swing.JSpinner();
        jLabelHour71 = new javax.swing.JLabel();
        jSpinnerHour71 = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("时间模板");
        setBackground(java.awt.Color.darkGray);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(96, 96, 96));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanelHeader.setBackground(new java.awt.Color(151, 151, 151));
        jPanelHeader.setForeground(new java.awt.Color(255, 255, 255));

        jLabelTemplateName.setBackground(new java.awt.Color(151, 151, 151));
        jLabelTemplateName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelTemplateName.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTemplateName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTemplateName.setText("   模板名称");

        jTextFieldTemplateName.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N

        jButtonOK.setBackground(new java.awt.Color(64, 64, 64));
        jButtonOK.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonOK.setForeground(new java.awt.Color(255, 255, 255));
        jButtonOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/ok.png"))); // NOI18N
        jButtonOK.setText("保存");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonCanel.setBackground(new java.awt.Color(64, 64, 64));
        jButtonCanel.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jButtonCanel.setForeground(new java.awt.Color(255, 255, 255));
        jButtonCanel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/cancel.png"))); // NOI18N
        jButtonCanel.setText("取消");
        jButtonCanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCanelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelHeaderLayout = new javax.swing.GroupLayout(jPanelHeader);
        jPanelHeader.setLayout(jPanelHeaderLayout);
        jPanelHeaderLayout.setHorizontalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHeaderLayout.createSequentialGroup()
                .addComponent(jLabelTemplateName, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldTemplateName, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 522, Short.MAX_VALUE)
                .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCanel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(109, 109, 109))
        );

        jPanelHeaderLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonCanel, jButtonOK});

        jPanelHeaderLayout.setVerticalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHeaderLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonOK)
                        .addComponent(jButtonCanel))
                    .addGroup(jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelTemplateName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextFieldTemplateName)))
                .addContainerGap())
        );

        jPanel2.add(jPanelHeader, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBackground(new java.awt.Color(96, 96, 96));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jRadioButtonAllDay.setBackground(new java.awt.Color(96, 96, 96));
        buttonGroupRecordModel.add(jRadioButtonAllDay);
        jRadioButtonAllDay.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonAllDay.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButtonAllDay.setText("全天模式");
        jRadioButtonAllDay.setEnabled(false);
        jRadioButtonAllDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonAllDayActionPerformed(evt);
            }
        });

        jRadioButtonWorkDay.setBackground(new java.awt.Color(96, 96, 96));
        buttonGroupRecordModel.add(jRadioButtonWorkDay);
        jRadioButtonWorkDay.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonWorkDay.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButtonWorkDay.setText("工作日模式");
        jRadioButtonWorkDay.setEnabled(false);
        jRadioButtonWorkDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonWorkDayActionPerformed(evt);
            }
        });

        jRadioButtonUserDefined.setBackground(new java.awt.Color(96, 96, 96));
        buttonGroupRecordModel.add(jRadioButtonUserDefined);
        jRadioButtonUserDefined.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jRadioButtonUserDefined.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButtonUserDefined.setSelected(true);
        jRadioButtonUserDefined.setText("自定义模式");
        jRadioButtonUserDefined.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonUserDefinedActionPerformed(evt);
            }
        });

        jComboBoxUserDefined.setBackground(new java.awt.Color(96, 96, 96));
        jComboBoxUserDefined.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jComboBoxUserDefined.setForeground(new java.awt.Color(255, 255, 255));
        jComboBoxUserDefined.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "参照全天模式", "参照工作日模式", "自定义模式" }));
        jComboBoxUserDefined.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxUserDefinedItemStateChanged(evt);
            }
        });

        jLabelUserDefined.setBackground(new java.awt.Color(96, 96, 96));
        jLabelUserDefined.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelUserDefined.setForeground(new java.awt.Color(255, 255, 255));
        jLabelUserDefined.setText("全天模式：00：00—24：00");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButtonAllDay)
                .addGap(18, 18, 18)
                .addComponent(jRadioButtonWorkDay)
                .addGap(18, 18, 18)
                .addComponent(jRadioButtonUserDefined)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxUserDefined, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelUserDefined, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButtonAllDay)
                        .addComponent(jRadioButtonWorkDay)
                        .addComponent(jRadioButtonUserDefined)
                        .addComponent(jComboBoxUserDefined, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelUserDefined, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanelTimeTemplateTitle.setBackground(new java.awt.Color(151, 151, 151));
        jPanelTimeTemplateTitle.setForeground(new java.awt.Color(255, 255, 255));

        jLabelTimeTemplate.setBackground(new java.awt.Color(151, 151, 151));
        jLabelTimeTemplate.setFont(new java.awt.Font("微软雅黑", 1, 16)); // NOI18N
        jLabelTimeTemplate.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTimeTemplate.setText("   时间模板：");

        javax.swing.GroupLayout jPanelTimeTemplateTitleLayout = new javax.swing.GroupLayout(jPanelTimeTemplateTitle);
        jPanelTimeTemplateTitle.setLayout(jPanelTimeTemplateTitleLayout);
        jPanelTimeTemplateTitleLayout.setHorizontalGroup(
            jPanelTimeTemplateTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTimeTemplateTitleLayout.createSequentialGroup()
                .addComponent(jLabelTimeTemplate, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 983, Short.MAX_VALUE))
        );
        jPanelTimeTemplateTitleLayout.setVerticalGroup(
            jPanelTimeTemplateTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTimeTemplate, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
        );

        jPanel2.add(jPanelTimeTemplateTitle, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel4.setBackground(new java.awt.Color(96, 96, 96));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanelTimeTemplate.setBackground(new java.awt.Color(96, 96, 96));
        jPanelTimeTemplate.setForeground(new java.awt.Color(255, 255, 255));

        jPanelRecordSchedInfo1.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRecordSchedInfo1.setForeground(new java.awt.Color(255, 255, 255));

        jPanel8.setBackground(new java.awt.Color(151, 151, 151));
        jPanel8.setForeground(new java.awt.Color(255, 255, 255));

        jLabelWeekTitle1.setBackground(new java.awt.Color(151, 151, 151));
        jLabelWeekTitle1.setFont(new java.awt.Font("微软雅黑", 2, 16)); // NOI18N
        jLabelWeekTitle1.setForeground(new java.awt.Color(255, 255, 255));
        jLabelWeekTitle1.setText("  星期一");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabelWeekTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelWeekTitle1, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        jPanelWeek1.setBackground(new java.awt.Color(64, 64, 64));
        jPanelWeek1.setForeground(new java.awt.Color(255, 255, 255));

        jSpinnerMinute12.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute12.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour12.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour12.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour12.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour12.setText("时");

        slider12.setBackground(new java.awt.Color(64, 64, 64));
        slider12.setForeground(new java.awt.Color(255, 255, 255));
        slider12.setMajorTickSpacing(120);
        slider12.setMaximum(1440);
        slider12.setMinorTickSpacing(1);
        slider12.setPaintLabels(true);
        slider12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider12.setValueIsAdjusting(true);
        slider12.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider12StateChanged(evt);
            }
        });

        slider11.setBackground(new java.awt.Color(64, 64, 64));
        slider11.setMajorTickSpacing(120);
        slider11.setMaximum(1440);
        slider11.setMinorTickSpacing(1);
        slider11.setPaintLabels(true);
        slider11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider11.setValueIsAdjusting(true);
        slider11.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider11StateChanged(evt);
            }
        });

        jLabelMinute12.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute12.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute12.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute12.setText("分");

        jSpinnerHour12.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour12.setModel(new javax.swing.SpinnerNumberModel(8, 0, 24, 1));

        jLabel11.setBackground(new java.awt.Color(64, 64, 64));
        jLabel11.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("——");

        jLabelMinute11.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute11.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute11.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute11.setText("分");

        jSpinnerMinute11.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute11.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour11.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour11.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour11.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour11.setText("时");

        jSpinnerHour11.setModel(new javax.swing.SpinnerNumberModel(0, 0, 24, 1));

        javax.swing.GroupLayout jPanelWeek1Layout = new javax.swing.GroupLayout(jPanelWeek1);
        jPanelWeek1.setLayout(jPanelWeek1Layout);
        jPanelWeek1Layout.setHorizontalGroup(
            jPanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelWeek1Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour11, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSpinnerMinute11, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelWeek1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelMinute11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerHour12, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute12, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelWeek1Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(slider12, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        jPanelWeek1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jSpinnerHour11, jSpinnerHour12, jSpinnerMinute11, jSpinnerMinute12});

        jPanelWeek1Layout.setVerticalGroup(
            jPanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek1Layout.createSequentialGroup()
                .addGroup(jPanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelHour11)
                        .addComponent(jSpinnerHour11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSpinnerMinute11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelMinute11)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSpinnerHour12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSpinnerMinute12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelHour12)
                        .addComponent(jLabelMinute12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelWeek1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slider11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(slider12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanelWeek1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jSpinnerHour11, jSpinnerMinute11});

        javax.swing.GroupLayout jPanelRecordSchedInfo1Layout = new javax.swing.GroupLayout(jPanelRecordSchedInfo1);
        jPanelRecordSchedInfo1.setLayout(jPanelRecordSchedInfo1Layout);
        jPanelRecordSchedInfo1Layout.setHorizontalGroup(
            jPanelRecordSchedInfo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelWeek1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelRecordSchedInfo1Layout.setVerticalGroup(
            jPanelRecordSchedInfo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRecordSchedInfo1Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelWeek1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 17, Short.MAX_VALUE))
        );

        jPanelRecordSchedInfo2.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRecordSchedInfo2.setForeground(new java.awt.Color(255, 255, 255));

        jPanel14.setBackground(new java.awt.Color(151, 151, 151));
        jPanel14.setForeground(new java.awt.Color(255, 255, 255));

        jLabelWeekTitle2.setBackground(new java.awt.Color(151, 151, 151));
        jLabelWeekTitle2.setFont(new java.awt.Font("微软雅黑", 2, 16)); // NOI18N
        jLabelWeekTitle2.setForeground(new java.awt.Color(255, 255, 255));
        jLabelWeekTitle2.setText("  星期二");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabelWeekTitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelWeekTitle2, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        jPanelWeek2.setBackground(new java.awt.Color(64, 64, 64));
        jPanelWeek2.setForeground(new java.awt.Color(255, 255, 255));

        jSpinnerMinute22.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute22.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour22.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour22.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour22.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour22.setText("时");

        slider22.setMajorTickSpacing(120);
        slider22.setMaximum(1440);
        slider22.setMinorTickSpacing(1);
        slider22.setPaintLabels(true);
        slider22.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider22.setValueIsAdjusting(true);
        slider22.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider22StateChanged(evt);
            }
        });

        slider21.setBackground(new java.awt.Color(64, 64, 64));
        slider21.setForeground(new java.awt.Color(255, 255, 255));
        slider21.setMajorTickSpacing(120);
        slider21.setMaximum(1440);
        slider21.setMinorTickSpacing(1);
        slider21.setPaintLabels(true);
        slider21.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider21.setValueIsAdjusting(true);
        slider21.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider21StateChanged(evt);
            }
        });

        jLabelMinute22.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute22.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute22.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute22.setText("分");

        jSpinnerHour22.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour22.setModel(new javax.swing.SpinnerNumberModel(8, 0, 24, 1));

        jLabel14.setBackground(new java.awt.Color(64, 64, 64));
        jLabel14.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("——");

        jLabelMinute21.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute21.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute21.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute21.setText("分");

        jSpinnerMinute21.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute21.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour21.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour21.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour21.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour21.setText("时");

        jSpinnerHour21.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour21.setModel(new javax.swing.SpinnerNumberModel(8, 0, 24, 1));

        javax.swing.GroupLayout jPanelWeek2Layout = new javax.swing.GroupLayout(jPanelWeek2);
        jPanelWeek2.setLayout(jPanelWeek2Layout);
        jPanelWeek2Layout.setHorizontalGroup(
            jPanelWeek2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelWeek2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek2Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour21, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabelHour21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute21, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelWeek2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek2Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour22, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute22, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelWeek2Layout.setVerticalGroup(
            jPanelWeek2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek2Layout.createSequentialGroup()
                .addGroup(jPanelWeek2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinnerMinute22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelHour22)
                    .addComponent(jLabelMinute22)
                    .addComponent(jSpinnerHour22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanelWeek2Layout.createSequentialGroup()
                .addGroup(jPanelWeek2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinnerHour21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelWeek2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSpinnerMinute21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelHour21)
                        .addComponent(jLabelMinute21)
                        .addComponent(jLabel14)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanelRecordSchedInfo2Layout = new javax.swing.GroupLayout(jPanelRecordSchedInfo2);
        jPanelRecordSchedInfo2.setLayout(jPanelRecordSchedInfo2Layout);
        jPanelRecordSchedInfo2Layout.setHorizontalGroup(
            jPanelRecordSchedInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelWeek2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanelRecordSchedInfo2Layout.setVerticalGroup(
            jPanelRecordSchedInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRecordSchedInfo2Layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelWeek2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelRecordSchedInfo3.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRecordSchedInfo3.setForeground(new java.awt.Color(255, 255, 255));

        jPanel16.setBackground(new java.awt.Color(151, 151, 151));
        jPanel16.setForeground(new java.awt.Color(255, 255, 255));

        jLabelWeekTitle3.setBackground(new java.awt.Color(151, 151, 151));
        jLabelWeekTitle3.setFont(new java.awt.Font("微软雅黑", 2, 16)); // NOI18N
        jLabelWeekTitle3.setForeground(new java.awt.Color(255, 255, 255));
        jLabelWeekTitle3.setText("  星期三");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabelWeekTitle3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelWeekTitle3, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        jPanelWeek3.setBackground(new java.awt.Color(64, 64, 64));
        jPanelWeek3.setForeground(new java.awt.Color(255, 255, 255));

        jSpinnerMinute32.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute32.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour32.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour32.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour32.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour32.setText("时");

        slider32.setMajorTickSpacing(120);
        slider32.setMaximum(1440);
        slider32.setMinorTickSpacing(1);
        slider32.setPaintLabels(true);
        slider32.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider32.setValueIsAdjusting(true);
        slider32.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider32StateChanged(evt);
            }
        });

        slider31.setBackground(new java.awt.Color(64, 64, 64));
        slider31.setForeground(new java.awt.Color(255, 255, 255));
        slider31.setMajorTickSpacing(120);
        slider31.setMaximum(1440);
        slider31.setMinorTickSpacing(1);
        slider31.setPaintLabels(true);
        slider31.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider31.setValueIsAdjusting(true);
        slider31.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider31StateChanged(evt);
            }
        });

        jLabelMinute32.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute32.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute32.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute32.setText("分");

        jSpinnerHour32.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour32.setModel(new javax.swing.SpinnerNumberModel(8, 0, 24, 1));

        jLabel46.setBackground(new java.awt.Color(64, 64, 64));
        jLabel46.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(255, 255, 255));
        jLabel46.setText("——");

        jLabelMinute31.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute31.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute31.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute31.setText("分");

        jSpinnerMinute31.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute31.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour31.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour31.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour31.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour31.setText("时");

        jSpinnerHour31.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour31.setModel(new javax.swing.SpinnerNumberModel(8, 0, 24, 1));

        javax.swing.GroupLayout jPanelWeek3Layout = new javax.swing.GroupLayout(jPanelWeek3);
        jPanelWeek3.setLayout(jPanelWeek3Layout);
        jPanelWeek3Layout.setHorizontalGroup(
            jPanelWeek3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelWeek3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek3Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour31, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabelHour31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute31, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelWeek3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek3Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour32, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute32, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelWeek3Layout.setVerticalGroup(
            jPanelWeek3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek3Layout.createSequentialGroup()
                .addGroup(jPanelWeek3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinnerMinute32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelHour32)
                    .addComponent(jLabelMinute32)
                    .addComponent(jSpinnerHour32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanelWeek3Layout.createSequentialGroup()
                .addGroup(jPanelWeek3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinnerHour31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelWeek3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSpinnerMinute31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelHour31)
                        .addComponent(jLabelMinute31)
                        .addComponent(jLabel46)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanelRecordSchedInfo3Layout = new javax.swing.GroupLayout(jPanelRecordSchedInfo3);
        jPanelRecordSchedInfo3.setLayout(jPanelRecordSchedInfo3Layout);
        jPanelRecordSchedInfo3Layout.setHorizontalGroup(
            jPanelRecordSchedInfo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelWeek3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanelRecordSchedInfo3Layout.setVerticalGroup(
            jPanelRecordSchedInfo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRecordSchedInfo3Layout.createSequentialGroup()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelWeek3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 17, Short.MAX_VALUE))
        );

        jPanelRecordSchedInfo4.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRecordSchedInfo4.setForeground(new java.awt.Color(255, 255, 255));

        jPanel11.setBackground(new java.awt.Color(151, 151, 151));
        jPanel11.setForeground(new java.awt.Color(255, 255, 255));

        jLabelWeekTitle4.setBackground(new java.awt.Color(151, 151, 151));
        jLabelWeekTitle4.setFont(new java.awt.Font("微软雅黑", 2, 16)); // NOI18N
        jLabelWeekTitle4.setForeground(new java.awt.Color(255, 255, 255));
        jLabelWeekTitle4.setText("  星期四");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabelWeekTitle4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelWeekTitle4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        jPanelWeek4.setBackground(new java.awt.Color(64, 64, 64));
        jPanelWeek4.setForeground(new java.awt.Color(255, 255, 255));

        jSpinnerMinute42.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute42.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour42.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour42.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour42.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour42.setText("时");

        slider42.setBackground(new java.awt.Color(64, 64, 64));
        slider42.setForeground(new java.awt.Color(255, 255, 255));
        slider42.setMajorTickSpacing(120);
        slider42.setMaximum(1440);
        slider42.setMinorTickSpacing(1);
        slider42.setPaintLabels(true);
        slider42.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider42.setValueIsAdjusting(true);
        slider42.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider42StateChanged(evt);
            }
        });

        slider41.setBackground(new java.awt.Color(64, 64, 64));
        slider41.setMajorTickSpacing(120);
        slider41.setMaximum(1440);
        slider41.setMinorTickSpacing(1);
        slider41.setPaintLabels(true);
        slider41.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider41.setValueIsAdjusting(true);
        slider41.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider41StateChanged(evt);
            }
        });

        jLabelMinute42.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute42.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute42.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute42.setText("分");

        jSpinnerHour42.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour42.setModel(new javax.swing.SpinnerNumberModel(8, 0, 24, 1));

        jLabel52.setBackground(new java.awt.Color(64, 64, 64));
        jLabel52.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(255, 255, 255));
        jLabel52.setText("——");

        jLabelMinute41.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute41.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute41.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute41.setText("分");

        jSpinnerMinute41.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute41.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour41.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour41.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour41.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour41.setText("时");

        jSpinnerHour41.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour41.setModel(new javax.swing.SpinnerNumberModel(8, 0, 24, 1));

        javax.swing.GroupLayout jPanelWeek4Layout = new javax.swing.GroupLayout(jPanelWeek4);
        jPanelWeek4.setLayout(jPanelWeek4Layout);
        jPanelWeek4Layout.setHorizontalGroup(
            jPanelWeek4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelWeek4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek4Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour41, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute41, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelWeek4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek4Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour42, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute42, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelWeek4Layout.setVerticalGroup(
            jPanelWeek4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek4Layout.createSequentialGroup()
                .addGroup(jPanelWeek4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelWeek4Layout.createSequentialGroup()
                        .addGroup(jPanelWeek4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinnerHour42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelWeek4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jSpinnerMinute42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelHour42)
                                .addComponent(jLabelMinute42)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(slider42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelWeek4Layout.createSequentialGroup()
                        .addGroup(jPanelWeek4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelHour41)
                            .addGroup(jPanelWeek4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jSpinnerHour41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jSpinnerMinute41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelMinute41)
                                .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(slider41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelRecordSchedInfo4Layout = new javax.swing.GroupLayout(jPanelRecordSchedInfo4);
        jPanelRecordSchedInfo4.setLayout(jPanelRecordSchedInfo4Layout);
        jPanelRecordSchedInfo4Layout.setHorizontalGroup(
            jPanelRecordSchedInfo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelRecordSchedInfo4Layout.createSequentialGroup()
                .addComponent(jPanelWeek4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelRecordSchedInfo4Layout.setVerticalGroup(
            jPanelRecordSchedInfo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRecordSchedInfo4Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelWeek4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelRecordSchedInfo5.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRecordSchedInfo5.setForeground(new java.awt.Color(255, 255, 255));
        jPanelRecordSchedInfo5.setPreferredSize(new java.awt.Dimension(564, 138));

        jPanel18.setBackground(new java.awt.Color(151, 151, 151));
        jPanel18.setForeground(new java.awt.Color(255, 255, 255));

        jLabelWeekTitle5.setBackground(new java.awt.Color(151, 151, 151));
        jLabelWeekTitle5.setFont(new java.awt.Font("微软雅黑", 2, 16)); // NOI18N
        jLabelWeekTitle5.setForeground(new java.awt.Color(255, 255, 255));
        jLabelWeekTitle5.setText("  星期五");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabelWeekTitle5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelWeekTitle5, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        jPanelWeek5.setBackground(new java.awt.Color(64, 64, 64));
        jPanelWeek5.setForeground(new java.awt.Color(255, 255, 255));

        jSpinnerMinute52.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute52.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour52.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour52.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour52.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour52.setText("时");

        slider52.setMajorTickSpacing(120);
        slider52.setMaximum(1440);
        slider52.setMinorTickSpacing(1);
        slider52.setPaintLabels(true);
        slider52.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider52.setValueIsAdjusting(true);
        slider52.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider52StateChanged(evt);
            }
        });

        slider51.setBackground(new java.awt.Color(64, 64, 64));
        slider51.setForeground(new java.awt.Color(255, 255, 255));
        slider51.setMajorTickSpacing(120);
        slider51.setMaximum(1440);
        slider51.setMinorTickSpacing(1);
        slider51.setPaintLabels(true);
        slider51.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider51.setValueIsAdjusting(true);
        slider51.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider51StateChanged(evt);
            }
        });

        jLabelMinute52.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute52.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute52.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute52.setText("分");

        jSpinnerHour52.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour52.setModel(new javax.swing.SpinnerNumberModel(20, 0, 23, 1));

        jLabel58.setBackground(new java.awt.Color(64, 64, 64));
        jLabel58.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(255, 255, 255));
        jLabel58.setText("——");

        jLabelMinute51.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute51.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute51.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute51.setText("分");

        jSpinnerMinute51.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute51.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour51.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour51.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour51.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour51.setText("时");

        jSpinnerHour51.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour51.setModel(new javax.swing.SpinnerNumberModel(8, 0, 24, 1));

        javax.swing.GroupLayout jPanelWeek5Layout = new javax.swing.GroupLayout(jPanelWeek5);
        jPanelWeek5.setLayout(jPanelWeek5Layout);
        jPanelWeek5Layout.setHorizontalGroup(
            jPanelWeek5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelWeek5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek5Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour51, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabelHour51, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute51, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute51, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel58)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelWeek5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek5Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour52, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute52, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelWeek5Layout.setVerticalGroup(
            jPanelWeek5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek5Layout.createSequentialGroup()
                .addGroup(jPanelWeek5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinnerMinute52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelHour52)
                    .addComponent(jLabelMinute52)
                    .addComponent(jSpinnerHour52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanelWeek5Layout.createSequentialGroup()
                .addGroup(jPanelWeek5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinnerHour51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelWeek5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSpinnerMinute51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelHour51)
                        .addComponent(jLabelMinute51)
                        .addComponent(jLabel58)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanelRecordSchedInfo5Layout = new javax.swing.GroupLayout(jPanelRecordSchedInfo5);
        jPanelRecordSchedInfo5.setLayout(jPanelRecordSchedInfo5Layout);
        jPanelRecordSchedInfo5Layout.setHorizontalGroup(
            jPanelRecordSchedInfo5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelWeek5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanelRecordSchedInfo5Layout.setVerticalGroup(
            jPanelRecordSchedInfo5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRecordSchedInfo5Layout.createSequentialGroup()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelWeek5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 17, Short.MAX_VALUE))
        );

        jPanelRecordSchedInfo6.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRecordSchedInfo6.setForeground(new java.awt.Color(255, 255, 255));

        jPanel19.setBackground(new java.awt.Color(151, 151, 151));
        jPanel19.setForeground(new java.awt.Color(255, 255, 255));

        jLabelWeekTitle6.setBackground(new java.awt.Color(151, 151, 151));
        jLabelWeekTitle6.setFont(new java.awt.Font("微软雅黑", 2, 16)); // NOI18N
        jLabelWeekTitle6.setForeground(new java.awt.Color(255, 255, 255));
        jLabelWeekTitle6.setText("  星期六");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jLabelWeekTitle6, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 437, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelWeekTitle6, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        jPanelWeek6.setBackground(new java.awt.Color(64, 64, 64));
        jPanelWeek6.setForeground(new java.awt.Color(255, 255, 255));

        jSpinnerMinute62.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute62.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour62.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour62.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour62.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour62.setText("时");

        slider62.setBackground(new java.awt.Color(64, 64, 64));
        slider62.setForeground(new java.awt.Color(255, 255, 255));
        slider62.setMajorTickSpacing(120);
        slider62.setMaximum(1440);
        slider62.setMinorTickSpacing(1);
        slider62.setPaintLabels(true);
        slider62.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider62.setValueIsAdjusting(true);
        slider62.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider62StateChanged(evt);
            }
        });

        slider61.setBackground(new java.awt.Color(64, 64, 64));
        slider61.setMajorTickSpacing(120);
        slider61.setMaximum(1440);
        slider61.setMinorTickSpacing(1);
        slider61.setPaintLabels(true);
        slider61.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider61.setValueIsAdjusting(true);
        slider61.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider61StateChanged(evt);
            }
        });

        jLabelMinute62.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute62.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute62.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute62.setText("分");

        jSpinnerHour62.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour62.setModel(new javax.swing.SpinnerNumberModel(20, 0, 23, 1));

        jLabel64.setBackground(new java.awt.Color(64, 64, 64));
        jLabel64.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(255, 255, 255));
        jLabel64.setText("——");

        jLabelMinute61.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute61.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute61.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute61.setText("分");

        jSpinnerMinute61.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute61.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour61.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour61.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour61.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour61.setText("时");

        jSpinnerHour61.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour61.setModel(new javax.swing.SpinnerNumberModel(8, 0, 24, 1));

        javax.swing.GroupLayout jPanelWeek6Layout = new javax.swing.GroupLayout(jPanelWeek6);
        jPanelWeek6.setLayout(jPanelWeek6Layout);
        jPanelWeek6Layout.setHorizontalGroup(
            jPanelWeek6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelWeek6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek6Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour61, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour61, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute61, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider61, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute61, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel64)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelWeek6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek6Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour62, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute62, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelWeek6Layout.setVerticalGroup(
            jPanelWeek6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek6Layout.createSequentialGroup()
                .addGroup(jPanelWeek6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelWeek6Layout.createSequentialGroup()
                        .addGroup(jPanelWeek6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinnerHour62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelWeek6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jSpinnerMinute62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelHour62)
                                .addComponent(jLabelMinute62)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(slider62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelWeek6Layout.createSequentialGroup()
                        .addGroup(jPanelWeek6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelHour61)
                            .addGroup(jPanelWeek6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jSpinnerHour61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jSpinnerMinute61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelMinute61)
                                .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(slider61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 1, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelRecordSchedInfo6Layout = new javax.swing.GroupLayout(jPanelRecordSchedInfo6);
        jPanelRecordSchedInfo6.setLayout(jPanelRecordSchedInfo6Layout);
        jPanelRecordSchedInfo6Layout.setHorizontalGroup(
            jPanelRecordSchedInfo6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelWeek6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanelRecordSchedInfo6Layout.setVerticalGroup(
            jPanelRecordSchedInfo6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRecordSchedInfo6Layout.createSequentialGroup()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelWeek6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanelRecordSchedInfo7.setBackground(new java.awt.Color(64, 64, 64));
        jPanelRecordSchedInfo7.setForeground(new java.awt.Color(255, 255, 255));
        jPanelRecordSchedInfo7.setPreferredSize(new java.awt.Dimension(564, 138));

        jPanel21.setBackground(new java.awt.Color(151, 151, 151));
        jPanel21.setForeground(new java.awt.Color(255, 255, 255));

        jLabelWeekTitle7.setBackground(new java.awt.Color(151, 151, 151));
        jLabelWeekTitle7.setFont(new java.awt.Font("微软雅黑", 2, 16)); // NOI18N
        jLabelWeekTitle7.setForeground(new java.awt.Color(255, 255, 255));
        jLabelWeekTitle7.setText("  星期日");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jLabelWeekTitle7, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 437, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelWeekTitle7, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        jPanelWeek7.setBackground(new java.awt.Color(64, 64, 64));
        jPanelWeek7.setForeground(new java.awt.Color(255, 255, 255));

        jSpinnerMinute72.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute72.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour72.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour72.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour72.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour72.setText("时");

        slider72.setBackground(new java.awt.Color(64, 64, 64));
        slider72.setForeground(new java.awt.Color(255, 255, 255));
        slider72.setMajorTickSpacing(120);
        slider72.setMaximum(1440);
        slider72.setMinorTickSpacing(1);
        slider72.setPaintLabels(true);
        slider72.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider72.setValueIsAdjusting(true);
        slider72.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider72StateChanged(evt);
            }
        });

        slider71.setBackground(new java.awt.Color(64, 64, 64));
        slider71.setMajorTickSpacing(120);
        slider71.setMaximum(1440);
        slider71.setMinorTickSpacing(1);
        slider71.setPaintLabels(true);
        slider71.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        slider71.setValueIsAdjusting(true);
        slider71.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider71StateChanged(evt);
            }
        });

        jLabelMinute72.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute72.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute72.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute72.setText("分");

        jSpinnerHour72.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour72.setModel(new javax.swing.SpinnerNumberModel(20, 0, 23, 1));

        jLabel70.setBackground(new java.awt.Color(64, 64, 64));
        jLabel70.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel70.setForeground(new java.awt.Color(255, 255, 255));
        jLabel70.setText("——");

        jLabelMinute71.setBackground(new java.awt.Color(64, 64, 64));
        jLabelMinute71.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelMinute71.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMinute71.setText("分");

        jSpinnerMinute71.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerMinute71.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabelHour71.setBackground(new java.awt.Color(64, 64, 64));
        jLabelHour71.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
        jLabelHour71.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHour71.setText("时");

        jSpinnerHour71.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jSpinnerHour71.setModel(new javax.swing.SpinnerNumberModel(8, 0, 24, 1));

        javax.swing.GroupLayout jPanelWeek7Layout = new javax.swing.GroupLayout(jPanelWeek7);
        jPanelWeek7.setLayout(jPanelWeek7Layout);
        jPanelWeek7Layout.setHorizontalGroup(
            jPanelWeek7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelWeek7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek7Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour71, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour71, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute71, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute71, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel70)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelWeek7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelWeek7Layout.createSequentialGroup()
                        .addComponent(jSpinnerHour72, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHour72, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute72, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(slider72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinute72, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelWeek7Layout.setVerticalGroup(
            jPanelWeek7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWeek7Layout.createSequentialGroup()
                .addGroup(jPanelWeek7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelWeek7Layout.createSequentialGroup()
                        .addGroup(jPanelWeek7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinnerHour72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelWeek7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jSpinnerMinute72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelHour72)
                                .addComponent(jLabelMinute72)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(slider72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelWeek7Layout.createSequentialGroup()
                        .addGroup(jPanelWeek7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelHour71)
                            .addGroup(jPanelWeek7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jSpinnerHour71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jSpinnerMinute71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelMinute71)
                                .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(slider71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 1, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelRecordSchedInfo7Layout = new javax.swing.GroupLayout(jPanelRecordSchedInfo7);
        jPanelRecordSchedInfo7.setLayout(jPanelRecordSchedInfo7Layout);
        jPanelRecordSchedInfo7Layout.setHorizontalGroup(
            jPanelRecordSchedInfo7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelWeek7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanelRecordSchedInfo7Layout.setVerticalGroup(
            jPanelRecordSchedInfo7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRecordSchedInfo7Layout.createSequentialGroup()
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelWeek7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelTimeTemplateLayout = new javax.swing.GroupLayout(jPanelTimeTemplate);
        jPanelTimeTemplate.setLayout(jPanelTimeTemplateLayout);
        jPanelTimeTemplateLayout.setHorizontalGroup(
            jPanelTimeTemplateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTimeTemplateLayout.createSequentialGroup()
                .addGroup(jPanelTimeTemplateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanelRecordSchedInfo3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelRecordSchedInfo2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelRecordSchedInfo4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelRecordSchedInfo1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(36, 36, 36)
                .addGroup(jPanelTimeTemplateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelRecordSchedInfo5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelRecordSchedInfo6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelRecordSchedInfo7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanelTimeTemplateLayout.setVerticalGroup(
            jPanelTimeTemplateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTimeTemplateLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jPanelRecordSchedInfo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTimeTemplateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelRecordSchedInfo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelRecordSchedInfo5, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTimeTemplateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTimeTemplateLayout.createSequentialGroup()
                        .addComponent(jPanelRecordSchedInfo3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanelRecordSchedInfo6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTimeTemplateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelRecordSchedInfo4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelRecordSchedInfo7, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        jPanel4.add(jPanelTimeTemplate, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setViewportView(jPanel4);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        // TODO add your handling code here:
        
        
        if(jRadioButtonAllDay.isSelected()){
            sTimeTemplateName = jRadioButtonAllDay.getText();
        }else if (jRadioButtonWorkDay.isSelected()) {
            sTimeTemplateName = jRadioButtonWorkDay.getText();
         }else{//自定义模式
            sTimeTemplateName = jTextFieldTemplateName.getText().trim();//模板名称
        }
        //判断该模板的名称是否为空
        if (sTimeTemplateName == null || sTimeTemplateName.equals("")){
            JOptionPane.showMessageDialog(null, sTemplateNotEmpty);// "模板名称不能为空！"
            return;
        }
        if (sTimeTemplateName.length() > TimeTemplateBean.TEMPLATENAME_LENGTH){
            JOptionPane.showMessageDialog(null, MessageFormat.format(CommonParas.sLengthCannotExceed, jLabelTemplateName.getText(), TimeTemplateBean.TEMPLATENAME_LENGTH));// "模板名称长度不能超过20！"
            return;
        }
        //判断该模板的名称是否合适
        if (sTimeTemplateName.equals(sModes[2]) || sTimeTemplateName.equals(sModes[0]) || sTimeTemplateName.equals(sModes[1]) ){//"工作日模板""全天模板""添加模板"
            JOptionPane.showMessageDialog(null, sTemplateConflict);// "该模板名称和系统冲突，请重新换取模板名称！"
            return;
        }

        //判读是否已经存在该模板
        if (iInsertOrUpdate == 0 && TimeTemplateBean.getIfExistTheTemplate(sTimeTemplateName, sFileName) > 0){
            JOptionPane.showMessageDialog(null, sTemplateAlreadyExist);// "该模板名称已经存在，请重新换取模板名称！"
            return;
        }
        
        
        bAllDay = (jRadioButtonAllDay.isSelected() == true);
        
        //参数传递模式。“listTimeTemplateBean”表示是以listTimeTemplateBean为参数传递；“strSchedTime”表示是以strSchedTime为参数传递；
        //保存布防时间设置
        switch (sMode) {
            case "strSchedTimeWeek":
                setStrSchedTimeWeek();//设置布防时间段
                break;
            case "listTimeTemplateBean":
                break;
            default:
                break;
        }

        //存储该模板的修改，并没有修改设备的关联模板
        if (ListInsertOrUpdateStr.size() > 0){
            if (TimeTemplateBean.batchInsertUpdate(ListInsertOrUpdateStr, sFileName) <= 0)   
                TxtLogger.append(this.sFileName, "jButtonOKActionPerformed()","系统修改时间模板失败"); 
        }
        iState = iInsertOrUpdate;
        
        dispose();

    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jRadioButtonUserDefinedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonUserDefinedActionPerformed
        // TODO add your handling code here:
//        jTextFieldTemplateName.setText(sTimeTemplateName);
        setOCXState();
    }//GEN-LAST:event_jRadioButtonUserDefinedActionPerformed

    private void jRadioButtonWorkDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonWorkDayActionPerformed
        // TODO add your handling code here:
//        jTextFieldTemplateName.setText("工作日模式");
        setWorkDayState();
        setOCXState();
    }//GEN-LAST:event_jRadioButtonWorkDayActionPerformed

    private void jRadioButtonAllDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonAllDayActionPerformed
        // TODO add your handling code here:
        setAllDayState();
        setOCXState();
    }//GEN-LAST:event_jRadioButtonAllDayActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        
        setAllJSiliderModel();
        //setAllSpinnerModel();//容易出现JSpinner无法进行手工输入和加1减1操作
        setOCXState();
        
    }//GEN-LAST:event_formWindowOpened

    private void jButtonCanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCanelActionPerformed
        // TODO add your handling code here:
        iState = -1;
        dispose();
    }//GEN-LAST:event_jButtonCanelActionPerformed

    private void jComboBoxUserDefinedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxUserDefinedItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
            String sItem  = (String)jComboBoxUserDefined.getSelectedItem();
            int IndexOfModes = CommonParas.getIndexOfArray(sModes , sItem);
            //参照全天模式, 参照五天工作日模式, 参照七天工作日模式, 自定义模式
            switch (IndexOfModes){
                case 0://--------"参照全天模式"----------
                    setAllDayState();
                    jLabelUserDefined.setText(sModeAllDayDesc);// "全天模式：00：00—24：00"
                    break;
//                case "参照五天工作日模式":
//                    setWorkDayState();
//                    jLabelUserDefined.setText("五天工作日模式：80：00—20：00；周末除外");
//                    break;
                case 1://----------"参照工作日模式"----------
                    setWorkDayState();
                    jLabelUserDefined.setText(sModeWorkDayDesc);// "工作日模式：80：00—20：00"
                    break;
                case 2://----------"自定义模式"----------
                    jLabelUserDefined.setText(sModeUserDefineddDesc);// "自定义模式：所有时间段自定义"
                    break;
            }
            setOCXState();
        }
    }//GEN-LAST:event_jComboBoxUserDefinedItemStateChanged

    private void slider12StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider12StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider12,jSpinnerHour12,jSpinnerMinute12);
    }//GEN-LAST:event_slider12StateChanged

    private void slider11StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider11StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider11,jSpinnerHour11,jSpinnerMinute11);
    }//GEN-LAST:event_slider11StateChanged

    private void slider22StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider22StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider22,jSpinnerHour22,jSpinnerMinute22);
    }//GEN-LAST:event_slider22StateChanged

    private void slider21StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider21StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider21,jSpinnerHour21,jSpinnerMinute21);
    }//GEN-LAST:event_slider21StateChanged

    private void slider32StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider32StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider32,jSpinnerHour32,jSpinnerMinute32);
    }//GEN-LAST:event_slider32StateChanged

    private void slider31StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider31StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider31,jSpinnerHour31,jSpinnerMinute31);
    }//GEN-LAST:event_slider31StateChanged

    private void slider42StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider42StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider42,jSpinnerHour42,jSpinnerMinute42);
    }//GEN-LAST:event_slider42StateChanged

    private void slider41StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider41StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider41,jSpinnerHour41,jSpinnerMinute41);
    }//GEN-LAST:event_slider41StateChanged

    private void slider52StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider52StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider52,jSpinnerHour52,jSpinnerMinute52);
    }//GEN-LAST:event_slider52StateChanged

    private void slider51StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider51StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider51,jSpinnerHour51,jSpinnerMinute51);
    }//GEN-LAST:event_slider51StateChanged

    private void slider62StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider62StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider62,jSpinnerHour62,jSpinnerMinute61);
    }//GEN-LAST:event_slider62StateChanged

    private void slider61StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider61StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider61,jSpinnerHour61,jSpinnerMinute61);
    }//GEN-LAST:event_slider61StateChanged

    private void slider72StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider72StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider72,jSpinnerHour72,jSpinnerMinute71);
    }//GEN-LAST:event_slider72StateChanged

    private void slider71StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider71StateChanged
        // TODO add your handling code here:
        setJSpinnerValue(slider71,jSpinnerHour71,jSpinnerMinute71);
    }//GEN-LAST:event_slider71StateChanged

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
        CommonParas.setMaxSize(this);
    }//GEN-LAST:event_formComponentResized
    /**
	 * 函数:      setOCXState
         * 函数描述:  根据是否启用计划存储设置部件的可用/不可用状态
    */
    private void setOCXState(){
        
        //还有JPanel的标题
        if(jRadioButtonAllDay.isSelected()){//全天模式
            //只显示星期一，但不可编辑;标题变为星期一～日;起止时间为0-24
            //((TitledBorder)jPanelWeek1.getBorder()).setTitle("星期一～日");
            jLabelWeekTitle1.setText(sMondayToSunday);// "星期一～日"
            setJPanelAllOCXEnable(jPanelWeek1,false);//设置星期一不可编辑
            setOnePanelAllDayState(jSpinnerHour11,jSpinnerMinute11,jSpinnerHour12,jSpinnerMinute12);//星期一起止时间为0-24
            jPanelRecordSchedInfo2.setVisible(false);
            jPanelRecordSchedInfo3.setVisible(false);
            jPanelRecordSchedInfo4.setVisible(false);
            jPanelRecordSchedInfo5.setVisible(false);
            jPanelRecordSchedInfo6.setVisible(false);
            jPanelRecordSchedInfo7.setVisible(false);
            this.setSize(iJButtonCanelXWidth, iFrameHeight - iPanelDeviceHeight * 3 + 30);

            jComboBoxUserDefined.setEnabled(false);

        }else if (jRadioButtonWorkDay.isSelected()) {//工作日模式
            //只显示星期一，但不可编辑;标题变为星期一～日;起止时间为8-20
            //((TitledBorder)jPanelWeek1.getBorder()).setTitle("星期一～日");
            jLabelWeekTitle1.setText(sMondayToSunday);// "星期一～日"
            setJPanelAllOCXEnable(jPanelWeek1,false);//设置星期一不可编辑
            setOnePanelWorkDayState(jSpinnerHour11,jSpinnerMinute11,jSpinnerHour12,jSpinnerMinute12);//星期一起止时间为8-20
            jPanelRecordSchedInfo2.setVisible(false);
            jPanelRecordSchedInfo3.setVisible(false);
            jPanelRecordSchedInfo4.setVisible(false);
            jPanelRecordSchedInfo5.setVisible(false);
            jPanelRecordSchedInfo6.setVisible(false);
            jPanelRecordSchedInfo7.setVisible(false);
            this.setSize(iJButtonCanelXWidth, iFrameHeight - iPanelDeviceHeight * 3 + 30);

            jComboBoxUserDefined.setEnabled(false);

        }else{//自定义模式
            jComboBoxUserDefined.setEnabled(true);

            String sItem  = (String)jComboBoxUserDefined.getSelectedItem();
            int IndexOfModes = CommonParas.getIndexOfArray(sModes , sItem);
            //参照全天模式, 参照五天工作日模式, 参照七天工作日模式, 自定义模式
            switch (IndexOfModes){
                case 0://------------"参照全天模式"------------
                    //只显示星期一，但可编辑;标题变为星期一～日
                    //((TitledBorder)jPanelWeek1.getBorder()).setTitle("星期一～日");
                    jLabelWeekTitle1.setText(sMondayToSunday);// "星期一～日"
                    setJPanelAllOCXEnable(jPanelWeek1,true);//设置星期一可编辑
                    setOnePanelAllDayState(jSpinnerHour11,jSpinnerMinute11,jSpinnerHour12,jSpinnerMinute12);//星期一起止时间为0-24
                    jPanelRecordSchedInfo2.setVisible(false);
                    jPanelRecordSchedInfo3.setVisible(false);
                    jPanelRecordSchedInfo4.setVisible(false);
                    jPanelRecordSchedInfo5.setVisible(false);
                    jPanelRecordSchedInfo6.setVisible(false);
                    jPanelRecordSchedInfo7.setVisible(false);
                    this.setSize(iJButtonCanelXWidth, iFrameHeight - iPanelDeviceHeight * 3 + 30);
                    break;
//                case "参照五天工作日模式":
//                    //只显示星期一、星期六；标题变为星期一～五、星期六～日，但可编辑
//                    //((TitledBorder)jPanelWeek1.getBorder()).setTitle("星期一～五");
//                    jLabelWeekTitle1.setText("星期一～五");
//                    setJPanelAllOCXEnable(jPanelWeek1,true);//设置星期一可编辑
//                    setOnePanelWorkDayState(jSpinnerHour11,jSpinnerMinute11,jSpinnerHour12,jSpinnerMinute12);//星期一起止时间为8-20
//                    ((TitledBorder)jPanelWeek6.getBorder()).setTitle("星期六～日");
//                    setJPanelAllOCXEnable(jPanelWeek6,true);//设置星期一可编辑
//                    setOnePanelWorkDayState(jSpinnerHour61,jSpinnerMinute61,jSpinnerHour62,jSpinnerMinute62);//星期一起止时间为8-20
//                    jPanelRecordSchedInfo2.setVisible(false);
//                    jPanelRecordSchedInfo3.setVisible(false);
//                    jPanelRecordSchedInfo4.setVisible(false);
//                    jPanelRecordSchedInfo5.setVisible(false);
//                    jPanelRecordSchedInfo6.setVisible(true);
//                    jPanelRecordSchedInfo7.setVisible(false);
//                    this.setSize(iFrameWidth, iFrameHeight - iPanelDeviceHeight * 3 + 30);
//                    break;
                case 1://------------"参照工作日模式"------------
                    //只显示星期一，但可编辑;标题变为星期一～日
                    //((TitledBorder)jPanelWeek1.getBorder()).setTitle("星期一～日");
                    jLabelWeekTitle1.setText(sMondayToSunday);// "星期一～日"
                    setJPanelAllOCXEnable(jPanelWeek1,true);//设置星期一可编辑
                    setOnePanelWorkDayState(jSpinnerHour11,jSpinnerMinute11,jSpinnerHour12,jSpinnerMinute12);//星期一起止时间为8-20
                    jPanelRecordSchedInfo2.setVisible(false);
                    jPanelRecordSchedInfo3.setVisible(false);
                    jPanelRecordSchedInfo4.setVisible(false);
                    jPanelRecordSchedInfo5.setVisible(false);
                    jPanelRecordSchedInfo6.setVisible(false);
                    jPanelRecordSchedInfo7.setVisible(false);
                    this.setSize(iJButtonCanelXWidth, iFrameHeight - iPanelDeviceHeight * 3 + 30);
                    break;
                case 2://------------"自定义模式"------------
                    //显示所有，但可编辑；标题返回默认值
                    jLabelWeekTitle1.setText(sMonday);//"星期一"
                    //((TitledBorder)jPanelWeek1.getBorder()).setTitle("星期一");
                    setJPanelAllOCXEnable(jPanelWeek1,true);//设置星期一可编辑
                    //((TitledBorder)jPanelWeek6.getBorder()).setTitle("星期六");
                    setJPanelAllOCXEnable(jPanelWeek6,true);//设置星期一可编辑
                    jPanelRecordSchedInfo2.setVisible(true);
                    jPanelRecordSchedInfo3.setVisible(true);
                    jPanelRecordSchedInfo4.setVisible(true);
                    jPanelRecordSchedInfo5.setVisible(true);
                    jPanelRecordSchedInfo6.setVisible(true);
                    jPanelRecordSchedInfo7.setVisible(true);
                    this.setSize(iFrameWidth, iFrameHeight);
                    break;
            }
            
//            if (jCheckBoxAllDay.isSelected()){
//                //只显示星期一，但可编辑;标题变为星期一～日
//                ((TitledBorder)jPanelWeek1.getBorder()).setTitle("星期一～日");
//                setJPanelAllOCXEnable(jPanelWeek1,true);//设置星期一可编辑
//                setOnePanelAllDayState(jSpinnerHour11,jSpinnerMinute11,jSpinnerHour12,jSpinnerMinute12);//星期一起止时间为0-24
//                jPanelWeek2.setVisible(false);
//                jPanelWeek3.setVisible(false);
//                jPanelWeek4.setVisible(false);
//                jPanelWeek5.setVisible(false);
//                jPanelWeek6.setVisible(false);
//                jPanelWeek7.setVisible(false);
//                this.setSize(iJButtonCanelXWidth, iFrameHeight - iPanelDeviceHeight * 4);
//            }else if (jCheckBoxWorkDay5.isSelected()){
//                //只显示星期一、星期六；标题变为星期一～五、星期六～日，但可编辑
//                ((TitledBorder)jPanelWeek1.getBorder()).setTitle("星期一～五");
//                setJPanelAllOCXEnable(jPanelWeek1,true);//设置星期一可编辑
//                setOnePanelAllDayState(jSpinnerHour11,jSpinnerMinute11,jSpinnerHour12,jSpinnerMinute12);//星期一起止时间为0-24
//                ((TitledBorder)jPanelWeek6.getBorder()).setTitle("星期六～日");
//                setJPanelAllOCXEnable(jPanelWeek6,true);//设置星期一可编辑
//                jPanelWeek2.setVisible(false);
//                jPanelWeek3.setVisible(false);
//                jPanelWeek4.setVisible(false);
//                jPanelWeek5.setVisible(false);
//                jPanelWeek6.setVisible(true);
//                jPanelWeek7.setVisible(false);
//                this.setSize(iFrameWidth, iFrameHeight - iPanelDeviceHeight * 4);
//            }else{
//                //显示所有，但可编辑；标题返回默认值
//                ((TitledBorder)jPanelWeek1.getBorder()).setTitle("星期一");
//                setJPanelAllOCXEnable(jPanelWeek1,true);//设置星期一可编辑
//                ((TitledBorder)jPanelWeek6.getBorder()).setTitle("星期六");
//                setJPanelAllOCXEnable(jPanelWeek6,true);//设置星期一可编辑
//                jPanelWeek2.setVisible(true);
//                jPanelWeek3.setVisible(true);
//                jPanelWeek4.setVisible(true);
//                jPanelWeek5.setVisible(true);
//                jPanelWeek6.setVisible(true);
//                jPanelWeek7.setVisible(true);
//                this.setSize(iFrameWidth, iFrameHeight);
////                setAllJPanelEnable(true);
//            }
            
        }
    }
    /**
	 * 函数:      setAllJPanelEnable
         * 函数描述:  设置所有的JPanel中所有的控件是否可用
         * @para enabled   是否可用
    */
    private void setAllJPanelEnable(boolean Enable){
        setJPanelAllOCXEnable(jPanelWeek1,Enable);
        setJPanelAllOCXEnable(jPanelWeek2,Enable);
        setJPanelAllOCXEnable(jPanelWeek3,Enable);
        setJPanelAllOCXEnable(jPanelWeek4,Enable);
        setJPanelAllOCXEnable(jPanelWeek5,Enable);
        setJPanelAllOCXEnable(jPanelWeek6,Enable);
        setJPanelAllOCXEnable(jPanelWeek7,Enable);
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
	 * 函数:      setOnePanelAllDayState
         * 函数描述:  设置某一个JPanel中组件的状态为工作日模式状态
         * @para Slider   JSlider控件
         * @para jSpinnerHour   JSpinner控件
         * @para jSpinnerMinute   JSpinner控件
         * @para jSpinnerHour2   JSpinner控件
         * @para jSpinnerMinute2   JSpinner控件
    */
    private void setOnePanelWorkDayState(JSpinner jSpinnerHour, JSpinner jSpinnerMinute, JSpinner jSpinnerHour2, JSpinner jSpinnerMinute2){
        jSpinnerHour.setValue(8);
        jSpinnerMinute.setValue(0);
        jSpinnerHour2.setValue(20);
        jSpinnerMinute2.setValue(0);
    }
    /**
	 * 函数:      setWorkDayState
         * 函数描述:  设置所有JPanel中组件的状态为工作日模式状态
    */
    private void setWorkDayState(){
        //星期一
        jSpinnerHour11.setValue(8);
        jSpinnerMinute11.setValue(0);
        jSpinnerHour12.setValue(20);
        jSpinnerMinute12.setValue(0);
        //星期二
        jSpinnerHour21.setValue(8);
        jSpinnerMinute21.setValue(0);
        jSpinnerHour22.setValue(20);
        jSpinnerMinute22.setValue(0);
        //星期三
        jSpinnerHour31.setValue(8);
        jSpinnerMinute31.setValue(0);
        jSpinnerHour32.setValue(20);
        jSpinnerMinute32.setValue(0);
        //星期四
        jSpinnerHour41.setValue(8);
        jSpinnerMinute41.setValue(0);
        jSpinnerHour42.setValue(20);
        jSpinnerMinute42.setValue(0);
        //星期五
        jSpinnerHour51.setValue(8);
        jSpinnerMinute51.setValue(0);
        jSpinnerHour52.setValue(20);
        jSpinnerMinute52.setValue(0);
        //星期六
        jSpinnerHour61.setValue(8);
        jSpinnerMinute61.setValue(0);
        jSpinnerHour62.setValue(20);
        jSpinnerMinute62.setValue(0);
        //星期日
        jSpinnerHour71.setValue(8);
        jSpinnerMinute71.setValue(0);
        jSpinnerHour72.setValue(20);
        jSpinnerMinute72.setValue(0);

    }
    /**
	 * 函数:      setOnePanelAllDayState
         * 函数描述:  设置某一个JPanel中组件的状态为全天模式状态
         * @para Slider   JSlider控件
         * @para jSpinnerHour   JSpinner控件
         * @para jSpinnerMinute   JSpinner控件
         * @para jSpinnerHour2   JSpinner控件
         * @para jSpinnerMinute2   JSpinner控件
    */
    private void setOnePanelAllDayState(JSpinner jSpinnerHour, JSpinner jSpinnerMinute, JSpinner jSpinnerHour2, JSpinner jSpinnerMinute2){
        jSpinnerHour.setValue(0);
        jSpinnerMinute.setValue(0);
        jSpinnerHour2.setValue(24);
        jSpinnerMinute2.setValue(0);
    }
    /**
	 * 函数:      setAllDayState
         * 函数描述:  设置所有JPanel中组件的状态为全天模式状态
    */
    private void setAllDayState(){
        //星期一
        jSpinnerHour11.setValue(0);
        jSpinnerMinute11.setValue(0);
        jSpinnerHour12.setValue(24);
        jSpinnerMinute12.setValue(0);

        //星期二
        jSpinnerHour21.setValue(0);
        jSpinnerMinute21.setValue(0);
        jSpinnerHour22.setValue(24);
        jSpinnerMinute22.setValue(0);
        //星期三
        jSpinnerHour31.setValue(0);
        jSpinnerMinute31.setValue(0);
        jSpinnerHour32.setValue(24);
        jSpinnerMinute32.setValue(0);
        //星期四
        jSpinnerHour41.setValue(0);
        jSpinnerMinute41.setValue(0);
        jSpinnerHour42.setValue(24);
        jSpinnerMinute42.setValue(0);
        //星期五
        jSpinnerHour51.setValue(0);
        jSpinnerMinute51.setValue(0);
        jSpinnerHour52.setValue(24);
        jSpinnerMinute52.setValue(0);
        //星期六
        jSpinnerHour61.setValue(0);
        jSpinnerMinute61.setValue(0);
        jSpinnerHour62.setValue(24);
        jSpinnerMinute62.setValue(0);
        //星期日
        jSpinnerHour71.setValue(0);
        jSpinnerMinute71.setValue(0);
        jSpinnerHour72.setValue(24);
        jSpinnerMinute72.setValue(0);

    }
    /**
	 * 函数:      setJSpinnerValue
         * @para Slider   JSlider控件
         * @para Spinner1   JSpinner控件
         * @para Spinner2   JSpinner控件
         * 函数描述:  根据Slider的值动态设定Spinner1和Spinner2的值
    */
    private void setJSpinnerValue(JSlider Slider,JSpinner Spinner1,JSpinner Spinner2){
        int Hour = Slider.getValue()/60;
        int Minute = Slider.getValue() - Hour * 60;
        
        Spinner1.setValue(Hour);
        Spinner2.setValue(Minute);
    }
    /**
	 * 函数:      setAllJSiliderModel
         * 函数描述:  重新设定所有的JSilider控件的属性
    */
    private void setAllJSiliderModel(){
        setJSiliderModel(slider11);
        setJSiliderModel(slider12);
        setJSiliderModel(slider21);
        setJSiliderModel(slider22);
        setJSiliderModel(slider31);
        setJSiliderModel(slider32);
        setJSiliderModel(slider41);
        setJSiliderModel(slider42);
        setJSiliderModel(slider51);
        setJSiliderModel(slider52);
        setJSiliderModel(slider61);
        setJSiliderModel(slider62);
        setJSiliderModel(slider71);
        setJSiliderModel(slider72);

    }
    /**
	 * 函数:      setJSiliderModel
         * 函数描述:  重新设定JSlider控件的属性
         * @para Slider   JSlider控件
    */
    private void setJSiliderModel(JSlider Slider){
        Hashtable table = new Hashtable();

        table.put(0, new JLabel("0"));
        table.put(120, new JLabel("2"));
        table.put(240, new JLabel("4"));
        table.put(360,new JLabel("6"));
        table.put(480, new JLabel("8"));
        table.put(600, new JLabel("10"));
        table.put(720, new JLabel("12"));
        table.put(840, new JLabel("14"));

        table.put(960, new JLabel("16"));
        table.put(1080, new JLabel("18"));
        table.put(1200, new JLabel("20"));
        table.put(1320, new JLabel("22"));
        table.put(1440, new JLabel("24"));
        
        Slider.setLabelTable(table);
    }
    /**
	 * 函数:      setJSpinnerModel
         * 函数描述:  重新设定JSpinner控件的属性
         * @para Spinner   JSpinner控件
    */
    private void setJSpinnerModel(JSpinner Spinner){
//        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(Spinner, "0");
//        Spinner.setEditor(editor);
//        JFormattedTextField textField = ((JSpinner.NumberEditor) Spinner.getEditor()).getTextField();
//        textField.setEditable(true);
//        DefaultFormatterFactory factory = (DefaultFormatterFactory) textField.getFormatterFactory();
//        NumberFormatter formatter = (NumberFormatter) factory.getDefaultFormatter();
//        formatter.setAllowsInvalid(true);
    }
    /**
	 * 函数:      setAllSpinnerModel
         * 函数描述:  重新设定所有的JSpinner控件的属性
    */
    private void setAllSpinnerModel(){
        setJSpinnerModel(jSpinnerHour11);
        setJSpinnerModel(jSpinnerHour12);
        setJSpinnerModel(jSpinnerHour21);
        setJSpinnerModel(jSpinnerHour22);
        setJSpinnerModel(jSpinnerHour31);
        setJSpinnerModel(jSpinnerHour32);
        setJSpinnerModel(jSpinnerHour41);
        setJSpinnerModel(jSpinnerHour42);
        setJSpinnerModel(jSpinnerHour51);
        setJSpinnerModel(jSpinnerHour52);
        setJSpinnerModel(jSpinnerHour61);
        setJSpinnerModel(jSpinnerHour62);
        setJSpinnerModel(jSpinnerHour71);
        setJSpinnerModel(jSpinnerHour72);
        setJSpinnerModel(jSpinnerMinute11);
        setJSpinnerModel(jSpinnerMinute12);
        setJSpinnerModel(jSpinnerMinute21);
        setJSpinnerModel(jSpinnerMinute22);
        setJSpinnerModel(jSpinnerMinute31);
        setJSpinnerModel(jSpinnerMinute32);
        setJSpinnerModel(jSpinnerMinute41);
        setJSpinnerModel(jSpinnerMinute42);
        setJSpinnerModel(jSpinnerMinute51);
        setJSpinnerModel(jSpinnerMinute52);
        setJSpinnerModel(jSpinnerMinute61);
        setJSpinnerModel(jSpinnerMinute62);
        setJSpinnerModel(jSpinnerMinute71);
        setJSpinnerModel(jSpinnerMinute72);
    }
    /**
        *函数:      getByteValue
        *函数描述:  得到JSpinner的数值。因为直接转换byte会出现错误
        *@param  jSpinner
     */
    private byte getByteValue(JSpinner jSpinner){
        return Byte.parseByte(jSpinner.getValue().toString());
    }
    /**
	 * 函数:      getSchedTime
         * 函数描述:  获得布防时间段
         * @return strSchedTime   HCNetSDK.NET_DVR_SCHEDTIME[]
    */
    public HCNetSDK.NET_DVR_SCHEDTIMEWEEK[] getSchedTime(){
        return strSchedTimeWeek ;
    }
    /**
	 * 函数:      setOneStrSchedTimeWeek
         * 函数描述:  设置一天的布防时间段
    */
    private void setOneStrSchedTimeWeek(int iWeek,JSpinner jSpinnerHour1,JSpinner jSpinnerMinute1,JSpinner jSpinnerHour2,JSpinner jSpinnerMinute2){
        byte byStartHour,byStartMin,byStopHour,byStopMin;
        byStartHour = getByteValue( jSpinnerHour1);
        byStartMin = getByteValue( jSpinnerMinute1);
        byStopHour = getByteValue( jSpinnerHour2);
        byStopMin = getByteValue( jSpinnerMinute2);
        if (iInsertOrUpdate == 0){
            ListInsertOrUpdateStr.add(TimeTemplateBean.getStringInsertCommand(new TimeTemplateBean(sTimeTemplateName,(byte)iWeek,byStartHour,byStartMin,byStopHour,byStopMin)));
        }
        else if (iInsertOrUpdate == 1) {
            if (strSchedTimeWeek[iWeek-1].struAlarmTime[0].byStartHour != byStartHour || 
                strSchedTimeWeek[iWeek-1].struAlarmTime[0].byStartMin != byStartMin || 
                strSchedTimeWeek[iWeek-1].struAlarmTime[0].byStopHour != byStopHour || 
                strSchedTimeWeek[iWeek-1].struAlarmTime[0].byStopMin != byStopMin){
                ListInsertOrUpdateStr.add(TimeTemplateBean.getStringUpdateCommand(new TimeTemplateBean(sTimeTemplateName,(byte)iWeek,byStartHour,byStartMin,byStopHour,byStopMin)));
            }
        }

        strSchedTimeWeek[iWeek-1].struAlarmTime[0].byStartHour = byStartHour;
        strSchedTimeWeek[iWeek-1].struAlarmTime[0].byStartMin = byStartMin;
        strSchedTimeWeek[iWeek-1].struAlarmTime[0].byStopHour = byStopHour;
        strSchedTimeWeek[iWeek-1].struAlarmTime[0].byStopMin = byStopMin;
    }
    /**
	 * 函数:      setStrSchedTimeWeek
         * 函数描述:  设置布防时间段
    */
    private void setStrSchedTimeWeek(){
        setOneStrSchedTimeWeek(1,jSpinnerHour11,jSpinnerMinute11,jSpinnerHour12,jSpinnerMinute12);
        setOneStrSchedTimeWeek(2,jSpinnerHour21,jSpinnerMinute21,jSpinnerHour22,jSpinnerMinute22);
        setOneStrSchedTimeWeek(3,jSpinnerHour31,jSpinnerMinute31,jSpinnerHour32,jSpinnerMinute32);
        setOneStrSchedTimeWeek(4,jSpinnerHour41,jSpinnerMinute41,jSpinnerHour42,jSpinnerMinute42);
        setOneStrSchedTimeWeek(5,jSpinnerHour51,jSpinnerMinute51,jSpinnerHour52,jSpinnerMinute52);
        setOneStrSchedTimeWeek(6,jSpinnerHour61,jSpinnerMinute61,jSpinnerHour62,jSpinnerMinute62);
        setOneStrSchedTimeWeek(7,jSpinnerHour71,jSpinnerMinute71,jSpinnerHour72,jSpinnerMinute72);
//        byte byStartHour,byStartMin,byStopHour,byStopMin;
//
//        //星期一
//        byStartHour = getByteValue( jSpinnerHour11);
//        byStartMin = getByteValue( jSpinnerMinute11);
//        byStopHour = getByteValue( jSpinnerHour12);
//        byStopMin = getByteValue( jSpinnerMinute12);
//        if (iInsertOrUpdate == 0){
//            ListInsertOrUpdateStr.add(TimeTemplateBean.getStringInsertCommand(new TimeTemplateBean(sTimeTemplateName,(short)1,byStartHour,byStartMin,byStopHour,byStopMin)));
//        }
//        else if (iInsertOrUpdate == 1) {
//            if (strSchedTimeWeek[0].struAlarmTime[0].byStartHour != byStartHour || 
//                strSchedTimeWeek[0].struAlarmTime[0].byStartMin != byStartMin || 
//                strSchedTimeWeek[0].struAlarmTime[0].byStopHour != byStopHour || 
//                strSchedTimeWeek[0].struAlarmTime[0].byStopMin != byStopMin){
//                ListInsertOrUpdateStr.add(TimeTemplateBean.getStringUpdateCommand(new TimeTemplateBean(sTimeTemplateName,(short)1,byStartHour,byStartMin,byStopHour,byStopMin)));
//            }
//        }
//
//        strSchedTimeWeek[0].struAlarmTime[0].byStartHour = byStartHour;
//        strSchedTimeWeek[0].struAlarmTime[0].byStartMin = byStartMin;
//        strSchedTimeWeek[0].struAlarmTime[0].byStopHour = byStopHour;
//        strSchedTimeWeek[0].struAlarmTime[0].byStopMin = byStopMin;
//        
//        //星期二  
//        byStartHour = getByteValue( jSpinnerHour21);
//        byStartMin = getByteValue( jSpinnerMinute21);
//        byStopHour = getByteValue( jSpinnerHour22);
//        byStopMin = getByteValue( jSpinnerMinute22);
//        if (iInsertOrUpdate == 0){
//            ListInsertOrUpdateStr.add(TimeTemplateBean.getStringInsertCommand(new TimeTemplateBean(sTimeTemplateName,(short)2,byStartHour,byStartMin,byStopHour,byStopMin)));
//        }
//        else if (iInsertOrUpdate == 1) {
//            if (strSchedTimeWeek[1].struAlarmTime[0].byStartHour != byStartHour || 
//                strSchedTimeWeek[1].struAlarmTime[0].byStartMin != byStartMin || 
//                strSchedTimeWeek[1].struAlarmTime[0].byStopHour != byStopHour || 
//                strSchedTimeWeek[1].struAlarmTime[0].byStopMin != byStopMin){
//                ListInsertOrUpdateStr.add(TimeTemplateBean.getStringUpdateCommand(new TimeTemplateBean(sTimeTemplateName,(short)2,byStartHour,byStartMin,byStopHour,byStopMin)));
//            }
//        }
//
//        strSchedTimeWeek[1].struAlarmTime[0].byStartHour = byStartHour;
//        strSchedTimeWeek[1].struAlarmTime[0].byStartMin = byStartMin;
//        strSchedTimeWeek[1].struAlarmTime[0].byStopHour = byStopHour;
//        strSchedTimeWeek[1].struAlarmTime[0].byStopMin = byStopMin;
//
//        //星期三  
//        strSchedTimeWeek[2].struAlarmTime[0].byStartHour = getByteValue( jSpinnerHour31);
//        strSchedTimeWeek[2].struAlarmTime[0].byStartMin = getByteValue( jSpinnerMinute31);
//        strSchedTimeWeek[2].struAlarmTime[0].byStopHour = getByteValue( jSpinnerHour32);
//        strSchedTimeWeek[2].struAlarmTime[0].byStopMin = getByteValue( jSpinnerMinute32);
//        //星期四  
//        strSchedTimeWeek[3].struAlarmTime[0].byStartHour = getByteValue( jSpinnerHour41);
//        strSchedTimeWeek[3].struAlarmTime[0].byStartMin = getByteValue( jSpinnerMinute41);
//        strSchedTimeWeek[3].struAlarmTime[0].byStopHour = getByteValue( jSpinnerHour42);
//        strSchedTimeWeek[3].struAlarmTime[0].byStopMin = getByteValue( jSpinnerMinute42);
//        //星期五  
//        strSchedTimeWeek[4].struAlarmTime[0].byStartHour = getByteValue( jSpinnerHour51);
//        strSchedTimeWeek[4].struAlarmTime[0].byStartMin = getByteValue( jSpinnerMinute51);
//        strSchedTimeWeek[4].struAlarmTime[0].byStopHour = getByteValue( jSpinnerHour52);
//        strSchedTimeWeek[4].struAlarmTime[0].byStopMin = getByteValue( jSpinnerMinute52);
//        //星期六  
//        strSchedTimeWeek[5].struAlarmTime[0].byStartHour = getByteValue( jSpinnerHour61);
//        strSchedTimeWeek[5].struAlarmTime[0].byStartMin = getByteValue( jSpinnerMinute61);
//        strSchedTimeWeek[5].struAlarmTime[0].byStopHour = getByteValue( jSpinnerHour62);
//        strSchedTimeWeek[5].struAlarmTime[0].byStopMin = getByteValue( jSpinnerMinute62);
//        //星期日  
//        strSchedTimeWeek[6].struAlarmTime[0].byStartHour = getByteValue( jSpinnerHour71);
//        strSchedTimeWeek[6].struAlarmTime[0].byStartMin = getByteValue( jSpinnerMinute71);
//        strSchedTimeWeek[6].struAlarmTime[0].byStopHour = getByteValue( jSpinnerHour72);
//        strSchedTimeWeek[6].struAlarmTime[0].byStopMin = getByteValue( jSpinnerMinute72);
    }
    /**
	 * 函数:      showStrSchedTimeWeek
         * 函数描述:  显示布防时间段
    */
    private void showStrSchedTimeWeek(){
        if (strSchedTimeWeek == null || strSchedTimeWeek.length != HCNetSDK.MAX_DAYS)  return;
        try{
            //星期一
            jSpinnerHour11.setValue(strSchedTimeWeek[0].struAlarmTime[0].byStartHour);
            jSpinnerMinute11.setValue(strSchedTimeWeek[0].struAlarmTime[0].byStartMin);
            jSpinnerHour12.setValue(strSchedTimeWeek[0].struAlarmTime[0].byStopHour);
            jSpinnerMinute12.setValue(strSchedTimeWeek[0].struAlarmTime[0].byStopMin);

            //星期二
            jSpinnerHour21.setValue(strSchedTimeWeek[1].struAlarmTime[0].byStartHour);
            jSpinnerMinute21.setValue(strSchedTimeWeek[1].struAlarmTime[0].byStartMin);
            jSpinnerHour22.setValue(strSchedTimeWeek[1].struAlarmTime[0].byStopHour);
            jSpinnerMinute22.setValue(strSchedTimeWeek[1].struAlarmTime[0].byStopMin);
            //星期三
            jSpinnerHour31.setValue(strSchedTimeWeek[2].struAlarmTime[0].byStartHour);
            jSpinnerMinute31.setValue(strSchedTimeWeek[2].struAlarmTime[0].byStartMin);
            jSpinnerHour32.setValue(strSchedTimeWeek[2].struAlarmTime[0].byStopHour);
            jSpinnerMinute32.setValue(strSchedTimeWeek[2].struAlarmTime[0].byStopMin);
            //星期四
            jSpinnerHour41.setValue(strSchedTimeWeek[3].struAlarmTime[0].byStartHour);
            jSpinnerMinute41.setValue(strSchedTimeWeek[3].struAlarmTime[0].byStartMin);
            jSpinnerHour42.setValue(strSchedTimeWeek[3].struAlarmTime[0].byStopHour);
            jSpinnerMinute42.setValue(strSchedTimeWeek[3].struAlarmTime[0].byStopMin);
            //星期五
            jSpinnerHour51.setValue(strSchedTimeWeek[4].struAlarmTime[0].byStartHour);
            jSpinnerMinute51.setValue(strSchedTimeWeek[4].struAlarmTime[0].byStartMin);
            jSpinnerHour52.setValue(strSchedTimeWeek[4].struAlarmTime[0].byStopHour);
            jSpinnerMinute52.setValue(strSchedTimeWeek[4].struAlarmTime[0].byStopMin);
            //星期六
            jSpinnerHour61.setValue(strSchedTimeWeek[5].struAlarmTime[0].byStartHour);
            jSpinnerMinute61.setValue(strSchedTimeWeek[5].struAlarmTime[0].byStartMin);
            jSpinnerHour62.setValue(strSchedTimeWeek[5].struAlarmTime[0].byStopHour);
            jSpinnerMinute62.setValue(strSchedTimeWeek[5].struAlarmTime[0].byStopMin);
            //星期日
            jSpinnerHour71.setValue(strSchedTimeWeek[6].struAlarmTime[0].byStartHour);
            jSpinnerMinute71.setValue(strSchedTimeWeek[6].struAlarmTime[0].byStartMin);
            jSpinnerHour72.setValue(strSchedTimeWeek[6].struAlarmTime[0].byStopHour);
            jSpinnerMinute72.setValue(strSchedTimeWeek[6].struAlarmTime[0].byStopMin);
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "showStrSchedTimeWeek()","系统在显示布防时间段过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
	 * 函数:      isAllDay
         * 函数描述:  是否全天布防
         * @return boolean   true是；false否
    */
    public boolean isAllDay(){
        return bAllDay;
    }
    /**
     * @return the bWorkDay
     */
    public boolean isbWorkDay() {
        return bWorkDay;
    }
    public int getReturnStatus(){
       return iState;
    }
    public String getTimeTemplateName(){
        return sTimeTemplateName;
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
            java.util.logging.Logger.getLogger(JDialogTimeTemplate.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialogTimeTemplate.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialogTimeTemplate.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialogTimeTemplate.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialogTimeTemplate dialog = new JDialogTimeTemplate(new javax.swing.JFrame(), true);
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
        sModes = Locales.getString("ClassStrings", "JDialogTimeTemplate.sModes").split(",");  //参照全天模式,参照工作日模式,自定义模式 
        sTemplates = Locales.getString("ClassStrings", "JDialogTimeTemplate.sTemplates").split(",");  //全天模板,工作日模板,添加模板
        sTitle = Locales.getString("ClassStrings", "JDialogTimeTemplate.sTitle");  //时间模板
        sHour = Locales.getString("ClassStrings", "JDialogTimeTemplate.sHour");  //时
        sMinute = Locales.getString("ClassStrings", "JDialogTimeTemplate.sMinute");  //分
        sTemplateNotEmpty = Locales.getString("ClassStrings", "JDialogTimeTemplate.sTemplateNotEmpty");  //模板名称不能为空！
        sTemplateConflict = Locales.getString("ClassStrings", "JDialogTimeTemplate.sTemplateConflict");  //该模板名称和系统冲突，请重新换取模板名称！
        sTemplateAlreadyExist = Locales.getString("ClassStrings", "JDialogTimeTemplate.sTemplateAlreadyExist");  //该模板名称已经存在，请重新换取模板名称！
        sModeAllDayDesc = Locales.getString("ClassStrings", "JDialogTimeTemplate.sModeAllDayDesc");  //全天模式：00：00—24：00
        sModeWorkDayDesc = Locales.getString("ClassStrings", "JDialogTimeTemplate.sModeWorkDayDesc");  //工作日模式：80：00—20：00
        sModeUserDefineddDesc = Locales.getString("ClassStrings", "JDialogTimeTemplate.sModeUserDefineddDesc");  //自定义模式：所有时间段自定义
        sMondayToSunday = Locales.getString("ClassStrings", "JDialogTimeTemplate.sMondayToSunday");  //星期一～日
        sMonday = Locales.getString("ClassStrings", "JDialogTimeTemplate.sMonday");  //星期一
        
        //标签和按钮显示
        jLabelTemplateName.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jLabelTemplateName"));  //模板名称：
        jButtonOK.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jButtonOK"));  //保存
        jButtonCanel.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jButtonCanel"));  //取消
        jRadioButtonAllDay.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jRadioButtonAllDay"));  //全天模式
        jRadioButtonWorkDay.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jRadioButtonWorkDay"));  //工作日模式
        jRadioButtonUserDefined.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jRadioButtonUserDefined"));  //自定义模式
        jLabelTimeTemplate.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jLabelTimeTemplate"));  //时间模板：
        jLabelWeekTitle1.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jLabelWeekTitle1"));  //星期一
        jLabelWeekTitle2.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jLabelWeekTitle2"));  //星期二
        jLabelWeekTitle3.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jLabelWeekTitle3"));  //星期三
        jLabelWeekTitle4.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jLabelWeekTitle4"));  //星期四
        jLabelWeekTitle5.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jLabelWeekTitle5"));  //星期五
        jLabelWeekTitle6.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jLabelWeekTitle6"));  //星期六
        jLabelWeekTitle7.setText(Locales.getString("ClassStrings", "JDialogTimeTemplate.jLabelWeekTitle7"));  //星期日

        jComboBoxUserDefined.setModel(new javax.swing.DefaultComboBoxModel<>(sModes));

        this.setTitle(sTitle);
        jLabelUserDefined.setText(sModeUserDefineddDesc);
        jLabelHour11.setText(sHour);
        jLabelHour12.setText(sHour);
        jLabelHour21.setText(sHour);
        jLabelHour22.setText(sHour);
        jLabelHour31.setText(sHour);
        jLabelHour32.setText(sHour);
        jLabelHour41.setText(sHour);
        jLabelHour42.setText(sHour);
        jLabelHour51.setText(sHour);
        jLabelHour52.setText(sHour);
        jLabelHour61.setText(sHour);
        jLabelHour62.setText(sHour);
        jLabelHour71.setText(sHour);
        jLabelHour72.setText(sHour);
        jLabelMinute11.setText(sMinute);
        jLabelMinute12.setText(sMinute);
        jLabelMinute21.setText(sMinute);
        jLabelMinute22.setText(sMinute);
        jLabelMinute31.setText(sMinute);
        jLabelMinute32.setText(sMinute);
        jLabelMinute41.setText(sMinute);
        jLabelMinute42.setText(sMinute);
        jLabelMinute51.setText(sMinute);
        jLabelMinute52.setText(sMinute);
        jLabelMinute61.setText(sMinute);
        jLabelMinute62.setText(sMinute);
        jLabelMinute71.setText(sMinute);
        jLabelMinute72.setText(sMinute);

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupRecordModel;
    private javax.swing.JButton jButtonCanel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JComboBox<String> jComboBoxUserDefined;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabelHour11;
    private javax.swing.JLabel jLabelHour12;
    private javax.swing.JLabel jLabelHour21;
    private javax.swing.JLabel jLabelHour22;
    private javax.swing.JLabel jLabelHour31;
    private javax.swing.JLabel jLabelHour32;
    private javax.swing.JLabel jLabelHour41;
    private javax.swing.JLabel jLabelHour42;
    private javax.swing.JLabel jLabelHour51;
    private javax.swing.JLabel jLabelHour52;
    private javax.swing.JLabel jLabelHour61;
    private javax.swing.JLabel jLabelHour62;
    private javax.swing.JLabel jLabelHour71;
    private javax.swing.JLabel jLabelHour72;
    private javax.swing.JLabel jLabelMinute11;
    private javax.swing.JLabel jLabelMinute12;
    private javax.swing.JLabel jLabelMinute21;
    private javax.swing.JLabel jLabelMinute22;
    private javax.swing.JLabel jLabelMinute31;
    private javax.swing.JLabel jLabelMinute32;
    private javax.swing.JLabel jLabelMinute41;
    private javax.swing.JLabel jLabelMinute42;
    private javax.swing.JLabel jLabelMinute51;
    private javax.swing.JLabel jLabelMinute52;
    private javax.swing.JLabel jLabelMinute61;
    private javax.swing.JLabel jLabelMinute62;
    private javax.swing.JLabel jLabelMinute71;
    private javax.swing.JLabel jLabelMinute72;
    private javax.swing.JLabel jLabelTemplateName;
    private javax.swing.JLabel jLabelTimeTemplate;
    private javax.swing.JLabel jLabelUserDefined;
    private javax.swing.JLabel jLabelWeekTitle1;
    private javax.swing.JLabel jLabelWeekTitle2;
    private javax.swing.JLabel jLabelWeekTitle3;
    private javax.swing.JLabel jLabelWeekTitle4;
    private javax.swing.JLabel jLabelWeekTitle5;
    private javax.swing.JLabel jLabelWeekTitle6;
    private javax.swing.JLabel jLabelWeekTitle7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelRecordSchedInfo1;
    private javax.swing.JPanel jPanelRecordSchedInfo2;
    private javax.swing.JPanel jPanelRecordSchedInfo3;
    private javax.swing.JPanel jPanelRecordSchedInfo4;
    private javax.swing.JPanel jPanelRecordSchedInfo5;
    private javax.swing.JPanel jPanelRecordSchedInfo6;
    private javax.swing.JPanel jPanelRecordSchedInfo7;
    private javax.swing.JPanel jPanelTimeTemplate;
    private javax.swing.JPanel jPanelTimeTemplateTitle;
    private javax.swing.JPanel jPanelWeek1;
    private javax.swing.JPanel jPanelWeek2;
    private javax.swing.JPanel jPanelWeek3;
    private javax.swing.JPanel jPanelWeek4;
    private javax.swing.JPanel jPanelWeek5;
    private javax.swing.JPanel jPanelWeek6;
    private javax.swing.JPanel jPanelWeek7;
    private javax.swing.JRadioButton jRadioButtonAllDay;
    private javax.swing.JRadioButton jRadioButtonUserDefined;
    private javax.swing.JRadioButton jRadioButtonWorkDay;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerHour11;
    private javax.swing.JSpinner jSpinnerHour12;
    private javax.swing.JSpinner jSpinnerHour21;
    private javax.swing.JSpinner jSpinnerHour22;
    private javax.swing.JSpinner jSpinnerHour31;
    private javax.swing.JSpinner jSpinnerHour32;
    private javax.swing.JSpinner jSpinnerHour41;
    private javax.swing.JSpinner jSpinnerHour42;
    private javax.swing.JSpinner jSpinnerHour51;
    private javax.swing.JSpinner jSpinnerHour52;
    private javax.swing.JSpinner jSpinnerHour61;
    private javax.swing.JSpinner jSpinnerHour62;
    private javax.swing.JSpinner jSpinnerHour71;
    private javax.swing.JSpinner jSpinnerHour72;
    private javax.swing.JSpinner jSpinnerMinute11;
    private javax.swing.JSpinner jSpinnerMinute12;
    private javax.swing.JSpinner jSpinnerMinute21;
    private javax.swing.JSpinner jSpinnerMinute22;
    private javax.swing.JSpinner jSpinnerMinute31;
    private javax.swing.JSpinner jSpinnerMinute32;
    private javax.swing.JSpinner jSpinnerMinute41;
    private javax.swing.JSpinner jSpinnerMinute42;
    private javax.swing.JSpinner jSpinnerMinute51;
    private javax.swing.JSpinner jSpinnerMinute52;
    private javax.swing.JSpinner jSpinnerMinute61;
    private javax.swing.JSpinner jSpinnerMinute62;
    private javax.swing.JSpinner jSpinnerMinute71;
    private javax.swing.JSpinner jSpinnerMinute72;
    private javax.swing.JTextField jTextFieldTemplateName;
    private javax.swing.JSlider slider11;
    private javax.swing.JSlider slider12;
    private javax.swing.JSlider slider21;
    private javax.swing.JSlider slider22;
    private javax.swing.JSlider slider31;
    private javax.swing.JSlider slider32;
    private javax.swing.JSlider slider41;
    private javax.swing.JSlider slider42;
    private javax.swing.JSlider slider51;
    private javax.swing.JSlider slider52;
    private javax.swing.JSlider slider61;
    private javax.swing.JSlider slider62;
    private javax.swing.JSlider slider71;
    private javax.swing.JSlider slider72;
    // End of variables declaration//GEN-END:variables

    private String[] sModes = new String[]{"参照全天模式","参照工作日模式","自定义模式" };
    private String[] sTemplates = new String[]{"全天模板","工作日模板","添加模板"};
    private String sTitle = "时间模板";
    private String sHour = "时";
    private String sMinute = "分";
    private String sTemplateNotEmpty = "模板名称不能为空！";
    private String sTemplateConflict = "该模板名称和系统冲突，请重新换取模板名称！";
    private String sTemplateAlreadyExist = "该模板名称已经存在，请重新换取模板名称！";
    private String sModeAllDayDesc = "全天模式：00：00—24：00";
    private String sModeWorkDayDesc = "工作日模式：80：00—20：00";
    private String sModeUserDefineddDesc = "自定义模式：所有时间段自定义";
    private String sMondayToSunday = "星期一～日";
    private String sMonday = "星期一";
    
}
