/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Color;
import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;

import java.awt.Font;
import javax.swing.border.EtchedBorder;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import jyms.tools.endecrypt.MD5Encrypt;
import jyms.data.UsersBean;


public class JDialogLockScreen extends JDialog {
    
    private final String sFileName = this.getClass().getName() + ".java";
    BorderLayout borderLayout1 = new BorderLayout();
    JButton jButton2 = new JButton();
    boolean isOK = true;
    JLabel jlableInfo = null;
    private boolean canRunCount = true;
    private JPasswordField passwordField;
    private JLabel label_3;
    public static void main(String[] args){
        JDialogLockScreen lock = new JDialogLockScreen(null, sTitle, true);// "系统锁屏"
        lock.setVisible(true);
    }
    public static JDialogLockScreen getLockScreen(java.awt.Window jframe){
        JDialogLockScreen lock = new JDialogLockScreen(jframe, sTitle, true);// "系统锁屏"
        lock.setVisible(true);
        return lock;
    }

    public JDialogLockScreen(java.awt.Window jframe, String str, boolean bool) {
        super(jframe, str, APPLICATION_MODAL );
        setSize(300, 135);
      //这个方法尤为重要，只要屏蔽了右上角的关闭按钮
        setUndecorated(true);
      //设置总是将dialog显示在屏幕的最前面
        setAlwaysOnTop(true);

//        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
//                    (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
        setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - getWidth(), 0);

        //setPreferredSize(new Dimension(200, 130));
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        JPanel rootPanel = new JPanel();
        rootPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(64, 64, 64), null));//[64,64,64](0,255, 255)
        getContentPane().add(rootPanel, BorderLayout.CENTER);
        rootPanel.setLayout(new BorderLayout(0, 0));
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(10, 60));
        rootPanel.add(panel, BorderLayout.NORTH);
        //panel.setLayout(new MigLayout("", "[][][][]", "[][]"));

        JLabel label = new JLabel("<html> <p align='center'>"+sAlreadyLock+"</p></html>");// "系统运行正常，但已锁定。"\u7CFB\u7EDF\u8FD0\u884C\u6B63\u5E38\uFF0C\u4F46\u5DF2\u9501\u5B9A\u3002
        label.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(290,20));
        panel.add(label, "cell 1 0");

        JLabel label_1 = new JLabel("<html> <p align='center'>"+ sPleaseEnterPass +"</p></html>",SwingConstants.CENTER);//请您输入登录的密码解锁！
        label_1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        label_1.setPreferredSize(new Dimension(290,20));
        panel.add(label_1, "cell 1 1");

        JPanel panel_1 = new JPanel();
        panel_1.setPreferredSize(new Dimension(300, 5));
        rootPanel.add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JSeparator separator = new JSeparator();
        //separator.setForeground(new Color(0, 255, 255));
        separator.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(64, 64, 64), null));//[64,64,64](0,255, 255)
        separator.setPreferredSize(new Dimension(280, 1));
        panel_1.add(separator);

        JPanel panel_2 = new JPanel();
        panel_2.setPreferredSize(new Dimension(10, 60));
        rootPanel.add(panel_2, BorderLayout.SOUTH);
        panel_2.setLayout(null);

        JLabel label_2 = new JLabel(sPassColon);// "密码："\u5BC6\u7801\uFF1A
        label_2.setBounds(26, 10, 100, 20);
        panel_2.add(label_2);

        passwordField = new JPasswordField();
        passwordField.setBounds(116, 7, 160, 26);
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                System.out.println(event.getKeyCode());
                if (event.getKeyCode() == 10) {
                    String pw = new String(passwordField.getPassword());//passwordField.getText();//new String(jPasswordFieldLogin.getPassword());
                    pw = MD5Encrypt.getMD5Str(pw);
                    if (CommonParas.checkAdminPassword(pw)){
                        dispose();
                    }else if (UsersBean.checkPasswordIfOK(CommonParas.UserState.UserName, pw, sFileName) > 0) {
                        dispose();
                    } else {
                        label_3.setText(sSorry);// "对不起，请您输入正确的密码！"
                        passwordField.setText("");
                        return;
                    }
                }
            }
        });
        passwordField.setPreferredSize(new Dimension(210, 21));
        panel_2.add(passwordField);

        label_3 = new JLabel("");
        label_3.setBackground(Color.ORANGE);
        label_3.setBounds(66, 35, 210, 15);
        panel_2.add(label_3);
    }
    
    /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    public static void modifyLocales(){
        
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作

        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        //信息显示
        sTitle          = Locales.getString("ClassStrings", "JDialogLockScreen.sTitle");  //系统锁屏
        sAlreadyLock    = Locales.getString("ClassStrings", "JDialogLockScreen.sAlreadyLock");  //系统运行正常，但已锁定。
        sPleaseEnterPass= Locales.getString("ClassStrings", "JDialogLockScreen.sPleaseEnterPass");  //请您输入登录的密码解锁！
        sPassColon      = Locales.getString("ClassStrings", "JDialogLockScreen.sPassColon");  //密码：
        sSorry          = Locales.getString("ClassStrings", "JDialogLockScreen.sSorry");  //对不起，请您输入正确的密码！
        
    }
    
    private static String sTitle = "系统锁屏";
    private static String sAlreadyLock = "系统运行正常，但已锁定。";
    private static String sPleaseEnterPass = "请您输入登录的密码解锁！";
    private static String sPassColon = "密码：";
    private static String sSorry = "对不起，请您输入正确的密码！";
}


