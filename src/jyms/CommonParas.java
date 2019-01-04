/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import jyms.data.ClientLogBean;
import jyms.data.DeviceGroupBean;
import jyms.data.DeviceParaBean;
import jyms.data.DeviceResourceBean;
import jyms.data.InitDB;
import jyms.data.SysErrorCodesBean;
import jyms.data.SysParaCodesBean;
import jyms.data.TxtLogger;
import jyms.data.UserAuthoritysBean;
import jyms.tools.CheckBoxNodeTree.CheckBoxTreeNode;
import jyms.tools.ImageIconBufferPool;
import jyms.tools.MessageGlassPane;
import jyms.tools.MulitCombobox;
import jyms.tools.WaveProcess.WaveFilePlay;
import jyms.tools.endecrypt.HardwareSerial;
import jyms.tools.endecrypt.LicenseGenerator;
import jyms.tools.endecrypt.MD5Encrypt;

/**
 *
 * @author John
 */
public class CommonParas {
    
    private static final String FILE_NAME = "--->>CommonParas.java";
    public static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
    
    
    public static String SysPathURL = "";//实际以jar文件方式运行时：“jar:file:/E:/dist/jyms.jar!/jyms/”;在NetBeans程序运行：“file:/E:/mydoc/GitHub/QDJKMS/jyMS/build/classes/jyms/”
    public static String SysPath = "";//系统路径。比如在NetBeans程序运行："E:\mydoc\GitHub\QDJKMS\jyMS\"
    
    public static class UserState{
        public static String UserName = "";//用户名字
        public static String UserTypeCode = USER_TYPECODE_OPERATOR;//用户类型代码。操作员代码的数字表示
        public static String UserType = "";//用户类型。默认是操作员
        public static String LogonTime = "";//登录时间
        public static ImageIcon HeadIcon = ImageIconBufferPool.getInstance().getImageIcon("head.png");
        public static void setHeadIcon(){
            switch(UserTypeCode){
                case USER_TYPECODE_OPERATOR:
                    HeadIcon = ImageIconBufferPool.getInstance().getImageIcon("headoperator.png");
                    break;
                case USER_TYPECODE_MANAGER:
                    HeadIcon = ImageIconBufferPool.getInstance().getImageIcon("headmanager.png");
                    break;
                case USER_TYPECODE_ADMIN:
                    HeadIcon = ImageIconBufferPool.getInstance().getImageIcon("headadmin.png");
                    break;
            }
        }
    }
    /**
        *内部类:   AlarmRecCtrl
        *类描述:   报警信息接收控制类
    */  
    public static class AlarmRecCtrl{
        //处理方式
        public static boolean ifPopUpWindow = false;//是否弹出报警窗口
        public static boolean ifVoice = true;//是否发出报警声音
        public static boolean isPopUpAlarmMSGOpened = false;//JDialogPopUpAlarmMSG窗口是否已经打开
    }
    //JInFrameAlarmMSG InFrameAlarmMSG= new JInFrameAlarmMSG();
    public  static JDialogPopUpAlarmMSG     DialogPopUpAlarmMSG;//JDialogPopUpAlarmMSG窗口的实例
    private static JDialogInfiniteProgress  DialogInfiniteProgress;//JDialogInfiniteProgress窗口的实例
    
    public static final String USER_TYPECODE_ADMIN = "020100";//超级管理员代码的数字表示
    public static final String USER_TYPECODE_MANAGER = "020200";//管理员代码的数字表示
    public static final String USER_TYPECODE_OPERATOR = "020300";//操作员代码的数字表示
    
    public static String USER_TYPE_ADMIN = "超级管理员";
    public static String USER_TYPE_MANAGER = "管理员";
    public static String USER_TYPE_OPERATOR = "操作员";
    
    
    public static ArrayList<Sadp.SADP_DEV_NET_PARAM_JAVA> g_list_SADP_DEV_NET_PARAM_JAVA = new ArrayList<>() ;
    
    //listDeviceDetailPara获取设备参数表中的 DeviceParaBean-0,设备类型-1（代码表中的代码名称）等参数；
    //现在加上登录ID-2，再加上NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V40-3;报警布防句柄AlarmHandle-4;
    //IP设备资源及IP通道资源配置结构体NET_DVR_IPPARACFG-5
    public static ArrayList g_listDeviceDetailPara;
    
    public static ArrayList<UserAuthoritysBean> g_ListUserAuthoritys = new ArrayList<>();//操作用户的权限。用户权限表中的用户名、权限项目、有无权限、备注等信息
    public static ArrayList g_ListSubAuthoritys = new ArrayList();//操作用户的附加权限(设备)。获取用户附加权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息
    public static NativeLong g_lVoiceHandle = new NativeLong(-1);//全局的语音对讲句柄。现在没有用到。
    public static boolean g_bShareSound = false;//是否是共享声卡模式
    
    public static DefaultTableModel alarmMSGTableModel;//报警模块TableModel
    public static DefaultTableModel errorMSGTableModel;//错误模块TableModel

    public static AlarmFMSGCallBack alarmFMSGCallBack = null;//报警回调函数实现
    public static JRootPane g_RootPane;//全局的JRootPane变量。该轻量级容器由 JFrame、JDialog、JWindow、JApplet 和 JInternalFrame 在后台使用
    public static JRootPane g_RootPane2;//全局的JRootPane变量。该轻量级容器由 JFrame、JDialog、JWindow、JApplet 和 JInternalFrame 在后台使用
    
    public static  ArrayList<JFrame> g_ListJFrame = new ArrayList<>();//JFrame集合。因为JInternalFrame集合可以读出来
    public static  Jkms jkms = null;
    
    //java国际化
    public static MyLocales myLocales;//本地语言环境
    
    public static  String MY_SEPARATOR = "-*-#%----#34573223#33**-";//公共的字符串分隔符，只要不和字符串中的字符串相同即可
    //SDK中字符编码格式：0- 无字符编码信息(老设备，暂时都按GB2312，即中国大陆)，1- GB2312(简体中文)，2- GBK，3- BIG5(繁体中文)，4- Shift_JIS(日文)，5- EUC-KR(韩文)，6- UTF-8，7- ISO8859-1，8- ISO8859-2，9- ISO8859-3，…，依次类推，21- ISO8859-15(西欧) 
    public static final String[] CHAR_ENCODE_TYPE = new String[] { "GB2312","GB2312","GBK","BIG5","Shift_JIS","EUC-KR","UTF-8","ISO8859-1","ISO8859-2","ISO8859-3","ISO8859-4","ISO8859-5","ISO8859-6",
                                                            "ISO8859-7","ISO8859-8","ISO8859-9","ISO8859-10","ISO8859-11","ISO8859-12","ISO8859-13","ISO8859-14","ISO8859-15"};
    
    
    
    /**
        * 函数:      isRunning
        * 函数描述:  判断程序是否已经运行(适用于windows系统)
        * @return boolean   已经运行，返回true；没有运行，返回false
    */
    public static boolean isRunning() {
        boolean rv = false;
        try {
            String os_name = System.getProperty("os.name");
            // 指定文件锁路径
            String path = null;
            if (os_name.contains("Windows")) {
                // 假如是Windows操作系统
                path = System.getProperty("user.home")
                        + System.getProperty("file.separator");
            } else {
                path = "/usr/temp/";
            }
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 程序名称
            String applicationName = "msserver.bat";
            RandomAccessFile fis = new RandomAccessFile(path + applicationName
                    + ".lock", "rw");
            FileChannel lockfc = fis.getChannel();
            FileLock flock = lockfc.tryLock();
            if (flock == null) {
                System.out.println("Main procedure is already runing...");
                rv = true;
            }
        }catch (Exception e){
            TxtLogger.append(FILE_NAME, "isRunning()","系统在判断程序是否已经运行的过程中，出现错误"
                             + "\r\n                       Exception:" );
        }
        return rv;
    }
    /**
        * 函数:      showMessage
        * 函数描述:  在窗口的GlassPane上显示信息
        * @param RootPane javax.swing.JRootPane
        * @param Message    消息
        * @param FileName   调用文件名
    */
    public static void showMessage(JRootPane RootPane, String Message, String FileName){
        MessageGlassPane.showMessage(RootPane, Message,  FileName);
    }
    /**
        * 函数:      showMessage
        * 函数描述:  在窗口的GlassPane上显示信息
        * @param Message    消息
        * @param FileName   调用文件名
    */
    public static void showMessage(String Message, String FileName){
//        g_RootPane.requestFocus();
        showMessage(g_RootPane, Message,  FileName);
    }
    /**
        * 函数:      showMessage
        * 函数描述:  在窗口的GlassPane上显示错误信息
        * @param RootPane javax.swing.JRootPane
        * @param Description    描述
        * @param Anothername    设备别名
        * @param FileName   调用文件名
    */
    public static void showErrorMessage(JRootPane RootPane, String Description, String Anothername, String FileName){
        String sErrorMsg = createErrorMessage(Anothername, Description);
        if (DialogInfiniteProgress != null && DialogInfiniteProgress.isVisible()) 
            DialogInfiniteProgress.setPorgressDetailInfo(sErrorMsg);
        else
            MessageGlassPane.showMessage(RootPane, sErrorMsg,  FileName);
    }
    /**
        * 函数:      showMessage
        * 函数描述:  在窗口的GlassPane上显示错误信息
        * @param Description    描述
        * @param Anothername    设备别名
        * @param FileName   调用文件名
    */
    public static void showErrorMessage(String Description, String Anothername, String FileName){
        
        showErrorMessage(g_RootPane, Description, Anothername, FileName);
    }
    /**
        * 函数:      showNoAuthorityMessage
        * 函数描述:  在窗口的GlassPane上显示错误信息
        * @param RootPane javax.swing.JRootPane
        * @param AuthorityItem    权限
        * @param AnotherName    设备别名
        * @param FileName   调用文件名
        * @return boolean
    */
    public static boolean showNoAuthorityMessage(JRootPane RootPane, String AuthorityItem, String AnotherName, String FileName){
        if (!CommonParas.ifHaveSubAuthorityAnotherName(AuthorityItem, AnotherName, FILE_NAME)){
            String sMsg = CommonParas.getNoAuthorityMsg(AuthorityItem, AnotherName);
            CommonParas.showMessage(RootPane, sMsg, FILE_NAME);
            return false;
        }else  return true;
    }
    /**
        * 函数:      showNoAuthorityMessage
        * 函数描述:  在窗口的GlassPane上显示错误信息
        * @param AuthorityItem    权限
        * @param AnotherName    设备别名
        * @param FileName   调用文件名
        * @return boolean
    */
    public static boolean showNoAuthorityMessage( String AuthorityItem, String AnotherName, String FileName){
        return showNoAuthorityMessage( g_RootPane,  AuthorityItem,  AnotherName,  FileName);
    }
    
    /**
        * 函数:      showErrorMessageDialog
        * 函数描述:  弹出错误信息的对话框
        * @param parentComponent 确定在其中显示对话框的 Frame；如果为 null 或者 parentComponent 不具有 Frame，则使用默认的 Frame
        * @param Message 要显示的文本信息
    */
    public static void showErrorMessageDialog( Component parentComponent, String Message){
        JOptionPane.showMessageDialog(parentComponent, Message, errorMsgDialogTitle, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
        * 函数:      showErrorMessageDialog
        * 函数描述:  弹出提示信息的对话框
        * @param parentComponent 确定在其中显示对话框的 Frame；如果为 null 或者 parentComponent 不具有 Frame，则使用默认的 Frame
        * @param Message 要显示的文本信息
    */
    public static void showRemindMessageDialog( Component parentComponent, String Message){
        JOptionPane.showMessageDialog(parentComponent, Message, remindMsgDialogTitle, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
        * 函数:      getUserTypeCode
        * 函数描述:  根据用户类型得到用户类型代码
        * @param UserType   用户类型
        * @return String 成功返回其代码；失败返回""。
    */
    public static String getUserTypeCode(String UserType){
        if (UserType == null || UserType.equals("")) return "";
        if (UserType.equals(CommonParas.USER_TYPE_ADMIN)){
            return USER_TYPECODE_ADMIN;
        }else if (UserType.equals(CommonParas.USER_TYPE_MANAGER)){
            return USER_TYPECODE_MANAGER;
        }else if(UserType.equals(CommonParas.USER_TYPE_OPERATOR)){
            return USER_TYPECODE_OPERATOR;
        }
//        switch (UserType){
//            case USER_TYPE_ADMIN:
//                return USER_TYPECODE_ADMIN;
//            case USER_TYPE_MANAGER:
//                return USER_TYPECODE_MANAGER;
//            case USER_TYPE_OPERATOR:
//                return USER_TYPECODE_OPERATOR;
//        }
        return "";
    }
    
    /**
        * 函数:      getUserType
        * 函数描述:  根据用户类型代码得到用户类型
        * @param UserTypeCode 用户类型代码
        * @return String   用户类型
    */
    public static String getUserType(String UserTypeCode){
        if (UserTypeCode == null || UserTypeCode.equals("")) return "";
        switch (UserTypeCode){
            case USER_TYPECODE_ADMIN:
                return USER_TYPE_ADMIN;
            case USER_TYPECODE_MANAGER:
                return USER_TYPE_MANAGER;
            case USER_TYPECODE_OPERATOR:
                return USER_TYPE_OPERATOR;
        }
        return "";
    }
    
    /**
        * 函数:      setMenuState
        * 函数描述:  根据用户的权限项目设置菜单的状态
        * @param AuthorityItem
        * @param Item
        * @return 
    */
    public static boolean setMenuState(String AuthorityItem, Component Item){
        if (!CommonParas.ifHaveUserAuthority(AuthorityItem, FILE_NAME)) {
            Item.setEnabled(false);
            return false;
        }else  {
            Item.setEnabled(true);
            return true;
        }
    }
    
    /**
        * 函数:      setMenuVisible
        * 函数描述:  根据用户的权限项目设置菜单的可见性
        * @param AuthorityItem
        * @param Item
        * @return 
    */
    public static boolean setMenuVisible(String AuthorityItem, Component Item){
        if (!CommonParas.ifHaveUserAuthority(AuthorityItem, FILE_NAME)) {
            Item.setVisible(false);
            return false;
        }else  {
            Item.setVisible(true);
            return true;
        }
    }
    
    /**
	 * 函数:      ifHaveUserAuthority
         * 函数描述:  是否存在该用户权限
         * @param Index
         * @param FileName	调用的文件名
         * @return boolean：成功true；不具有或者失败返回false。.
     */
    public static boolean ifHaveUserAuthority(int Index, String FileName){
        try{
            if (UserState.UserTypeCode.equals(USER_TYPECODE_ADMIN)) return true;
            if (Index < 0) return false;

            UserAuthoritysBean AuthoritysBean = g_ListUserAuthoritys.get(Index);
            
            return  AuthoritysBean.getIfhave().equals("1");

            
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "ifHaveUserAuthority()","系统查询用户是否拥有权限的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        //JOptionPane.showMessageDialog(null, "您没有 " + AuthorityItem +" 的权限");
        return false;
    }
    /**
	 * 函数:      ifHaveUserAuthority
         * 函数描述:  是否存在该用户权限
	 * @param AuthorityItem	  权限项目
         * @param FileName	调用的文件名
         * @return boolean：成功true；不具有或者失败返回false。.
     */
    public static boolean ifHaveUserAuthority(String AuthorityItem, String FileName){
        try{
            if (UserState.UserTypeCode.equals(USER_TYPECODE_ADMIN)) return true;
            if (AuthorityItem == null || AuthorityItem.equals("")) return false;
            for (int i=0;i<g_ListUserAuthoritys.size();i++){
                UserAuthoritysBean AuthoritysBean = g_ListUserAuthoritys.get(i);
                if (AuthoritysBean.getAuthorityitem().equals(AuthorityItem)) {
                    if (AuthoritysBean.getIfhave().equals("1")) return true;
                }
            }
            
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "ifHaveUserAuthority()","系统查询用户是否拥有权限的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        //JOptionPane.showMessageDialog(null, "您没有 " + AuthorityItem +" 的权限");
        return false;
    }
    /**
	 * 函数:      ifAdmin
         * 函数描述:  是否当前用户为超级管理员
         * @return boolean：是true；否false。.
     */
    public static boolean ifAdmin(){
        return UserState.UserTypeCode.equals( USER_TYPECODE_ADMIN );
    }
    /**
	 * 函数:      ifManager
         * 函数描述:  是否当前用户为管理员
         * @return boolean：是true；否false。.
     */
    public static boolean ifManager(){
        return UserState.UserTypeCode.equals( USER_TYPECODE_MANAGER );
    }
    /**
	 * 函数:      ifOperator
         * 函数描述:  是否当前用户为操作员
         * @return boolean：是true；否false。.
     */
    public static boolean ifOperator(){
        return UserState.UserTypeCode.equals( USER_TYPECODE_OPERATOR );
    }
    /**
        * 函数:      checkAdmin
        * 函数描述:  判断是否当前用户为超级管理员
        * @param User
        * @param Password   加密过的密码
        * @return boolean：是true；否false。.
    */
    public static boolean checkAdmin(String User, String Password){
        return MD5Encrypt.getMD5Str(User).equals("220F5160DACFF17908840E79B4A66C43") && Password.equals("BB6268E922878D2C501268904849217C");
    }
    /**
        * 函数:      checkAdminPassword
        * 函数描述:  判断当前用户为超级管理员，且密码正确与否
        * @param Password   加密过的密码
        * @return boolean：是true；否false。.
    */
    public static boolean checkAdminPassword(String Password){
        return ifAdmin() && Password.equals("BB6268E922878D2C501268904849217C");
    }
    
    /**
	 * 函数:      ifHaveUserAuthority
         * 函数描述:  是否存在该用户附加权限。g_ListSubAuthoritys用户附加权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息
         * @param Index
         * @param FileName	调用的文件名
         * @return boolean：成功true；不具有或者失败返回false。.
     */
    public static boolean ifHaveSubUserAuthority(int Index, String FileName){
        try{
            if (UserState.UserTypeCode.equals(USER_TYPECODE_ADMIN)) return true;
            if (Index < 0) return false;

            ArrayList<String> ListSubAuthority = (ArrayList<String>)g_ListSubAuthoritys.get(Index);
            return  ListSubAuthority.get(4).equals("1") ;
            
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "ifHaveSubAuthorityAnotherName()","系统查询用户是否拥有权限的过程中，出现错误"
                             + "\r\n                       参数Index:" + Index
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /**
	 * 函数:      ifHaveUserAuthority
         * 函数描述:  是否存在该用户附加权限。g_ListSubAuthoritys用户附加权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息
	 * @param AuthorityItem	  权限项目
         * @param AnotherName	  设备别名
         * @param DeviceSerialno	  设备序列号
         * @param FileName	调用的文件名
         * @return boolean：成功true；不具有或者失败返回false。.
     */
    public static boolean ifHaveSubUserAuthority(String AuthorityItem,String AnotherName,String DeviceSerialno,String FileName){
        if (UserState.UserTypeCode.equals(USER_TYPECODE_ADMIN)) return true;
        if (AnotherName != null) return ifHaveSubAuthorityAnotherName(AuthorityItem,AnotherName,FileName);
        if (DeviceSerialno != null) return ifHaveSubAuthoritySerialno(AuthorityItem,DeviceSerialno,FileName);
        return false;
    }
    /**
	 * 函数:      ifHaveSubAuthorityAnotherName
         * 函数描述:  是否存在该用户附加权限。g_ListSubAuthoritys用户附加权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息
	 * @param AuthorityItem	  权限项目
         * @param AnotherName	  设备别名
         * @param FileName	调用的文件名
         * @return boolean：成功true；不具有或者失败返回false。.
     */
    public static boolean ifHaveSubAuthorityAnotherName(String AuthorityItem,String AnotherName,String FileName){
        try{
            if (UserState.UserTypeCode.equals(USER_TYPECODE_ADMIN)) return true;
            if (AuthorityItem == null || AuthorityItem.equals("")) return false;
            if (AnotherName == null || AnotherName.equals("") ) return false;
            for (int i=0;i<g_ListSubAuthoritys.size();i++){
                //获取用户附加权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息
                ArrayList<String> ListSubAuthority = (ArrayList<String>)g_ListSubAuthoritys.get(i);
                if (ListSubAuthority.get(1).equals(AuthorityItem) && ListSubAuthority.get(3).equals(AnotherName)) { 
                    if (ListSubAuthority.get(4).equals("1")) return true;
                }
            }
            
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "ifHaveSubAuthorityAnotherName()","系统查询用户是否拥有权限的过程中，出现错误"
                    + "\r\n                       参数AuthorityItem:" + AuthorityItem
                    + "\r\n                       参数AnotherName:"   + AnotherName
                    + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /**
	 * 函数:      ifHaveSubAuthoritySerialno
         * 函数描述:  是否存在该用户附加权限。g_ListSubAuthoritys用户附加权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息
	 * @param AuthorityItem	  权限项目
         * @param DeviceSerialno	  设备序列号
         * @param FileName	调用的文件名
         * @return boolean：成功true；不具有或者失败返回false。.
     */
    public static boolean ifHaveSubAuthoritySerialno(String AuthorityItem,String DeviceSerialno,String FileName){
        try{
            if (UserState.UserTypeCode.equals(USER_TYPECODE_ADMIN)) return true;
            if (AuthorityItem == null || AuthorityItem.equals("")) return false;
            if (DeviceSerialno == null || DeviceSerialno.equals("")) return false;
            for (int i=0;i<g_ListSubAuthoritys.size();i++){
                //获取用户附加权限表中的用户名、权限项目、设备序列号、设备别名、有否、备注等信息
                ArrayList<String> ListSubAuthority = (ArrayList<String>)g_ListSubAuthoritys.get(i);
                if (ListSubAuthority.get(1).equals(AuthorityItem) && ListSubAuthority.get(2).equals(DeviceSerialno)) { 
                    if (ListSubAuthority.get(4).equals("1")) return true;
                }
            }
            
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "ifHaveSubSerialnoAuthority()","系统查询用户是否拥有权限的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /**
        * 函数:      getNoAuthorityMsg
        * 函数描述:  返回用户没有该设备、该权限的消息
        * @param AuthorityItem	  权限项目
        * @param AnotherName	  设备别名
        * @return String   无权限的消息
    */
    public static String getNoAuthorityMsg(String AuthorityItem,String AnotherName){
        //return "您不拥有对设备\"" + AnotherName + "\"的\"" + AuthorityItem + "\"的权限";
        return MessageFormat.format(noAuthorityMsg, AnotherName, AuthorityItem);
    }
    
    /**
	 * 函数:      reverseString
         * 函数描述:  将字符串倒写
	 * @param str	
         * @return String：倒写后的字符串。.
     */
    public static String reverseString(String str) {  
        if (str == null || str.length() <= 1) {  
            return str;  
        }  
        StringBuffer sb = new StringBuffer(str);  
        sb = sb.reverse();  
        return sb.toString();  
    } 

    /**
	 * 函数:      getPasswordGrade
         * 函数描述:  返回设备密码等级
	 * @param UserName     设备用户名
	 * @param Password	设备密码
         * @return String：返回设备密码等级。.
         *                  将密码输入分为数字(0~9)、小写字母(a~z)、大写字母(A~Z)、特殊符号（:\"除外）4类，等级分为4个等级，如下所示：
         *                  等级0（风险密码）：密码长度小于8位，或者只包含4类字符中的任意一类，或者密码与用户名一样，或者密码是用户名的倒写。例如：12345、abcdef。
         *                  等级1（弱密码）：包含两类字符，且组合为（数字+小写字母）或（数字+大写字母），且长度大于等于8位。例如：abc12345、123ABCDEF
         *                  等级2（中密码）：包含两类字符，且组合不能为（数字+小写字母）和（数字+大写字母），且长度大于等于8位。例如：12345***++、ABCDabcd。
         *                  等级3（强密码）：包含三类字符及以上，且长度大于等于8位。例如：Abc12345、abc12345++。 
     */
    public static String getPasswordGrade(String UserName,String Password){
        if (UserName == null || Password == null) return null;
        if (Password.length()<8) return passwordGradeRisk ;
        if (Password.equals(UserName)) return passwordGradeRisk ;   
        if (Password.equals(reverseString(UserName))) return passwordGradeRisk ; 
        int iLowercase = 0;//小写字母(a~z)
        int iUppercase = 0;//大写字母(A~Z)
        int iDigital = 0;//数字(0~9)
        int iSpecial = 0;//特殊符号（:\"除外）
 
        for (int i=0;i<Password.length();i++){
            char char2 =Password.charAt(i);
            if (char2 >= '0' && char2 <= '9') {
                iDigital = 1;
            }
            else if (char2 >= 'a' && char2 <= 'z') {
                iLowercase = 1;
            }
            else if (char2 >= 'A' && char2 <= 'Z') {
                iUppercase = 1;
            }
            else{
                iSpecial = 1;
            }
        }
        if ((iDigital + iLowercase + iUppercase +iSpecial) == 1) return passwordGradeRisk ; 
        //等级1（弱密码）：包含两类字符，且组合为（数字+小写字母）或（数字+大写字母），且长度大于等于8位。
        if (iDigital == 1 && iSpecial == 0 && (iDigital + iLowercase + iUppercase) == 2)  return passwordGradeWeak ; 
        //等级2（中密码）：包含两类字符，且组合不能为（数字+小写字母）和（数字+大写字母），且长度大于等于8位。
        if ((iDigital + iLowercase + iUppercase +iSpecial) == 2) return passwordGradeMedium ; 
        //等级3（强密码）：包含三类字符及以上，且长度大于等于8位。
        if ((iDigital + iLowercase + iUppercase +iSpecial) >= 3) return passwordGradeStrong ; 
        
        return null;
    }
    
    /**
        * 函数:      createErrorMessage
        * 函数描述:  为了简化详细描述，统一规格，生成详细描述
        * @param AnotherName    设备别名
        * @param Description    简单描述
        * @return 详细描述
    */
    public static String createErrorMessage(String AnotherName,String Description){
        int ErrorCode = hCNetSDK.NET_DVR_GetLastError();
        String ErrorMsg;
        if (SysParas.ifChinese) {
            ErrorMsg = SysErrorCodesBean.getErrorMsg(ErrorCode, FILE_NAME);
        }else{
            ErrorMsg = hCNetSDK.NET_DVR_GetErrorMsg(new NativeLongByReference(new NativeLong(ErrorCode)));
        }

        return MessageFormat.format(deviceErrorMessage, AnotherName, Description, ErrorCode, ErrorMsg);
        //return "设备：" + AnotherName + "  " + Description + "。错误代码：" + ErrorCode + "；\r\n错误描述："+ SysErrorCodesBean.getErrorMsg(ErrorCode, FILE_NAME);
    }
    
    /**
        * 函数:      createErrorMessageDataBase
        * 函数描述:  为了简化详细描述，统一规格，生成详细描述
        * @param AnotherName    设备别名
        * @param Description    简单描述
        * @return 详细描述
    */
    public static String createErrorMessageDataBase(String AnotherName,String Description){
        int ErrorCode = hCNetSDK.NET_DVR_GetLastError();
        String ErrorMsg;
        if (SysParas.ifChinese) {
            ErrorMsg = SysErrorCodesBean.getErrorMsg(ErrorCode, FILE_NAME);
        }else{
            ErrorMsg = hCNetSDK.NET_DVR_GetErrorMsg(new NativeLongByReference(new NativeLong(ErrorCode)));
        }
        
        
        String Remarks = MessageFormat.format(deviceErrorMessageDataBase, AnotherName, Description, ErrorCode, ErrorMsg);
        //如果Remarks>200（数据库表数据项长度）
        if (Remarks.length() > ClientLogBean.REMARKS_LENGTH) Remarks = Remarks.substring(0,ClientLogBean.REMARKS_LENGTH);

        return Remarks;
        
    }
    
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志
        * @param LogBean    ClientLogBean
        * @param FileName   调用的文件名
    */
    public static void SystemWriteLog(ClientLogBean LogBean,String FileName){
    /*客户端日志CLIENTLOG   其中分组名现在版本暂时不写，没有什么用。为以后版本准备的
          ID、操作时间TIMESTAMP、用户名VARCHAR(16)、日志类型CHAR(6)、描述信息VARCHAR(60)、设备序列号 VARCHAR(48)、分组名VARCHAR(20)、节点名 VARCHAR(30)、注释CHAR(200)
        CommonParas.SystemWriteLog(new ClientLogBean(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()),CommonParas.UserState.UserName,CommonParas.LogType.LOG_OPER,"设备管理"), FILE_NAME);*/
        if(ifAdmin()) return;//如果是超级管理员则不必进行写日志操作。
        if (ClientLogBean.insertClientLog(LogBean, FileName) < 1 ){
            TxtLogger.append(FileName, "SystemWriteLog()","用户写日志错误");
        }
    }
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志
        * @param LogBean    ClientLogBean
        * @param Remarks
        * @param FileName   调用的文件名
    */
    public static void SystemWriteLog(ClientLogBean LogBean, String Remarks, String FileName){
    /*客户端日志CLIENTLOG   其中分组名现在版本暂时不写，没有什么用。为以后版本准备的
          ID、操作时间TIMESTAMP、用户名VARCHAR(16)、日志类型CHAR(6)、描述信息VARCHAR(60)、设备序列号 VARCHAR(48)、分组名VARCHAR(20)、节点名 VARCHAR(30)、注释CHAR(200)
        CommonParas.SystemWriteLog(new ClientLogBean(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()),CommonParas.UserState.UserName,CommonParas.LogType.LOG_OPER,"设备管理"), FILE_NAME);*/
        LogBean.setRemarks(Remarks);
        SystemWriteLog( LogBean, FileName);
    }

    /**
        * 函数:      SystemWriteErrorLog
        * 函数描述:  系统写错误日志
        * @param LogBean    ClientLogBean
        * @param Remarks    日志备注，详细描述
        * @param FileName   调用的文件名
    */
    public static void SystemWriteErrorLog(ClientLogBean LogBean,String Remarks,String FileName){
    /*客户端日志CLIENTLOG   其中分组名现在版本暂时不写，没有什么用。为以后版本准备的
          ID、操作时间TIMESTAMP、用户名VARCHAR(16)、日志类型CHAR(6)、描述信息VARCHAR(60)、设备序列号 VARCHAR(48)、分组名VARCHAR(20)、节点名 VARCHAR(30)、注释CHAR(200)
        CommonParas.SystemWriteLog(new ClientLogBean(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()),CommonParas.UserState.UserName,CommonParas.LogType.LOG_OPER,"设备管理"), FILE_NAME);*/
        SystemWriteLog( LogBean,  Remarks,  FileName);
        writeErrorInfoToTable(LogBean.getOperationtime(), Remarks);
    }
    
    /**
        * 函数:      SystemWriteErrorLog
        * 函数描述:  系统写错误日志（4项：操作时间、用户名、日志类型、描述信息、日志备注，其中用户名系统自动加入，不需要在输入参数中）
        * @param Description    描述
        * @param Anothername    设备别名
        * @param FileName   调用的文件名
    */
    public static void SystemWriteErrorLog(String Description, String Anothername, String FileName){
        String OperationTime = getOperationTime();
        String sErrorMsg = createErrorMessageDataBase(Anothername, Description);

        SystemWriteErrorLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, LogType.LOG_ERROR_CODE,Description),
                sErrorMsg, FileName);
    }
    
    /**
        * 函数:      SystemWriteErrorLog
        * 函数描述:  系统写错误日志（4项：操作时间、描述信息、日志备注、调用的文件名，其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime
        * @param Description    描述
        * @param Remarks    日志备注，详细描述
        * @param FileName   调用的文件名
    */
    public static void SystemWriteErrorLog(String OperationTime, String Description, String Remarks, String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        SystemWriteErrorLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, LogType.LOG_ERROR_CODE, Description),
                            Remarks, FileName);
    }
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（5项：操作时间、描述信息、设备序列号、设备类型、调用的文件名，其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime
        * @param Anothername    设备别名
        * @param Description    错误总体描述
        * @param SerialNO   设备序列号
        * @param DVRType    设备类型
        * @param FileName   调用的文件名
    */
    public static void SystemWriteErrorLog(String OperationTime, String Anothername, String Description, String SerialNO, String DVRType, String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        String sErrorMsg = createErrorMessageDataBase(Anothername, Description);
        
        SystemWriteErrorLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, LogType.LOG_ERROR_CODE, Description, SerialNO, "", "", "", "", DVRType, ""),
                            sErrorMsg,  FileName);
    }
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（6项：操作时间、描述信息、设备序列号、设备类型、备注、调用的文件名，其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime
        * @param Anothername    设备别名
        * @param Description    错误总体描述
        * @param SerialNO   设备序列号
        * @param DVRType    设备类型
        * @param Remarks    错误细节描述
        * @param FileName   调用的文件名
    */
    public static void SystemWriteErrorLog(String OperationTime, String Anothername, String Description, String SerialNO, String DVRType, String Remarks, String FileName){
        
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        String sErrorMsg = createErrorMessageDataBase(Anothername, Remarks);
        
        SystemWriteErrorLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, LogType.LOG_ERROR_CODE, Description, SerialNO, "", "", "", "", DVRType, ""),
                            sErrorMsg,  FileName);
    }
    /**
        * 函数:      SystemWriteErrorLog
        * 函数描述:  系统写日志（12项ClientLogBean：操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、备注、调用的文件名
        *                                           其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime  操作时间
        * @param Anothername    设备别名
        * @param Description    错误总体描述
        * @param SerialNO   设备序列号
        * @param GroupName
        * @param NodeName   设备节点名
        * @param SerialNOJoin
        * @param ChannelJoin
        * @param DVRType
        * @param ObjectType
        * @param FileName   调用的文件名
    */
    public static void SystemWriteErrorLog(String OperationTime,String Anothername,String Description,String SerialNO,String GroupName,String NodeName,String SerialNOJoin,String ChannelJoin,String DVRType,String ObjectType,String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        String sErrorMsg = createErrorMessageDataBase(Anothername, Description);

        SystemWriteErrorLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, LogType.LOG_ERROR_CODE, Description, SerialNO, GroupName, NodeName, 
                                            SerialNOJoin, ChannelJoin, DVRType, ObjectType), 
                            sErrorMsg,  FileName);
    }
    
    /**
        * 函数:      SystemWriteErrorLog(针对上面函数的补充)
        * 函数描述:  系统写日志（12项ClientLogBean：操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、备注、调用的文件名
        *                                           其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime  操作时间
        * @param Anothername    设备别名
        * @param Description    描述
        * @param SerialNO   设备序列号
        * @param GroupName
        * @param NodeName   设备节点名
        * @param SerialNOJoin
        * @param ChannelJoin
        * @param DVRType
        * @param ObjectType
        * @param Remarks    错误细节描述
        * @param FileName   调用的文件名
    */
    public static void SystemWriteErrorLog(String OperationTime,String Anothername,String Description,String SerialNO,String GroupName,String NodeName,String SerialNOJoin,String ChannelJoin,String DVRType,String ObjectType,String Remarks, String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        String sErrorMsg = createErrorMessageDataBase(Anothername, Remarks);

        SystemWriteErrorLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, LogType.LOG_ERROR_CODE, Description, SerialNO, GroupName, NodeName, 
                                            SerialNOJoin, ChannelJoin, DVRType, ObjectType), 
                            sErrorMsg,  FileName);
    }
    
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（3项：日志类型、描述信息、调用的文件名，其中操作时间、用户名系统自动加入，不需要在输入参数中）
        *               一般用在点击菜单，打开窗口用。不涉及到具体设备
        * @param Logtype    日志类型
        * @param Description    描述
        * @param FileName   调用的文件名
    */
    public static void SystemWriteLog(String Logtype, String Description, String FileName){
        String OperationTime = getOperationTime();
        SystemWriteLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, Logtype, Description), FileName);
    }
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（4项：操作时间、日志类型、描述信息、调用的文件名，其中用户名系统自动加入，不需要在输入参数中）
        *               和上面3个参数的一样，只不过操作时间，需要写入，而上面的函数自动写入。
        * @param OperationTime  操作时间
        * @param Logtype    日志类型
        * @param Description    描述
        * @param FileName   调用的文件名
    */
    public static void SystemWriteLog(String OperationTime, String Logtype, String Description, String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        SystemWriteLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, Logtype, Description), FileName);
    }
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（5项：操作时间、日志类型、描述信息、备注、调用的文件名，其中用户名系统自动加入，不需要在输入参数中）
        *               和上面3个参数的一样，只不过操作时间，需要写入，而上面的函数自动写入。
        * @param OperationTime  操作时间
        * @param Logtype    日志类型
        * @param Description    描述
        * @param Remarks
        * @param FileName   调用的文件名
    */
    public static void SystemWriteLog(String OperationTime, String Logtype, String Description, String Remarks, String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        SystemWriteLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, Logtype, Description),  Remarks,  FileName);
    }
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（6项：操作时间、日志类型、描述信息、设备序列号、设备类型、调用的文件名，其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime
        * @param LogType    日志类型
        * @param Description    描述
        * @param SerialNO   设备序列号
        * @param DVRType    设备类型
        * @param FileName   调用的文件名
    */
    public static void SystemWriteLog(String OperationTime, String LogType, String Description, String SerialNO, String DVRType, String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        SystemWriteLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName,LogType, Description, SerialNO, "", "", "", "", DVRType, ""),  FileName);
    }
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（7项：操作时间、日志类型、描述信息、设备序列号、设备类型、备注、调用的文件名，其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime
        * @param LogType    日志类型
        * @param Description    描述
        * @param SerialNO   设备序列号
        * @param DVRType    设备类型
        * @param FileName   调用的文件名
    */
    public static void SystemWriteLog(String OperationTime, String LogType, String Description, String SerialNO, String DVRType, String remarks, String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        SystemWriteLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName,LogType, Description, SerialNO, "", "", "", "", DVRType, "", remarks),  FileName);
    }
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（9项：操作时间、日志类型、描述信息、设备序列号、分组名、节点名、设备类型、被操作对象类型、调用的文件名，其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime
        * @param Logtype    日志类型
        * @param Description    描述
        * @param Serialno   设备序列号
        * @param GroupName
        * @param Nodename   设备节点名
        * @param DVRType
        * @param ObjectType
        * @param FileName   调用的文件名
    */
    public static void SystemWriteLog(String OperationTime, String Logtype, String Description, String Serialno, String GroupName, String Nodename, String DVRType, String ObjectType, String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        //11项：操作时间、用户名、日志类型、描述信息、设备序列号、分组名、节点名 、接入设备序列号、接入通道、设备类型、被操作对象类型
        SystemWriteLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, Logtype,
                Description, Serialno, GroupName, Nodename, "","",DVRType, ObjectType),  FileName);
    }
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（10项：操作时间、日志类型、描述信息、设备序列号、分组名、节点名、设备类型、被操作对象类型、备注、调用的文件名，其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime
        * @param Logtype    日志类型
        * @param Description    描述
        * @param Serialno   设备序列号
        * @param GroupName
        * @param Nodename   设备节点名
        * @param DVRType
        * @param ObjectType
        * @param Remarks
        * @param FileName   调用的文件名
    */
    public static void SystemWriteLog(String OperationTime, String Logtype, String Description, String Serialno, String GroupName, String Nodename, String DVRType, String ObjectType, String Remarks, String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        //12项：操作时间、用户名、日志类型、描述信息、设备序列号、分组名、节点名 、接入设备序列号、接入通道、设备类型、被操作对象类型、备注
        SystemWriteLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName, Logtype,
                Description, Serialno, GroupName, Nodename, "","",DVRType, ObjectType, Remarks),  FileName);
    }

    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（11项ClientLogBean：操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、调用的文件名
        *                                           其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime  操作时间
        * @param LogType    日志类型
        * @param Description    描述
        * @param SerialNO   设备序列号
        * @param GroupName
        * @param NodeName   设备节点名
        * @param SerialNOJoin
        * @param ChannelJoin
        * @param DVRType
        * @param ObjectType
        * @param FileName   调用的文件名
        
    */
    public static void SystemWriteLog(String OperationTime,String LogType,String Description,String SerialNO,String GroupName,String NodeName,String SerialNOJoin,String ChannelJoin,String DVRType,String ObjectType,String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        SystemWriteLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName,LogType, Description, SerialNO, GroupName, NodeName, 
                                            SerialNOJoin, ChannelJoin, DVRType, ObjectType),  FileName);
        
    }
    /**
        * 函数:      SystemWriteLog
        * 函数描述:  系统写日志（12项ClientLogBean：操作时间、日志类型、描述信息、设备序列号、分组名、节点名、接入设备序列号、接入通道、设备类型、被操作对象类型、备注、调用的文件名
        *                                           其中用户名系统自动加入，不需要在输入参数中）
        * @param OperationTime  操作时间
        * @param LogType    日志类型
        * @param Description    描述
        * @param SerialNO   设备序列号
        * @param GroupName
        * @param NodeName   设备节点名
        * @param SerialNOJoin
        * @param ChannelJoin
        * @param DVRType
        * @param ObjectType
        * @param remarks
        * @param FileName   调用的文件名
    */
    public static void SystemWriteLog(String OperationTime,String LogType,String Description,String SerialNO,String GroupName,String NodeName,String SerialNOJoin,String ChannelJoin,String DVRType,String ObjectType, String remarks,String FileName){
        if (OperationTime == null || OperationTime.equals("")) OperationTime = getOperationTime();
        SystemWriteLog(new ClientLogBean(OperationTime, CommonParas.UserState.UserName,LogType, Description, SerialNO, GroupName, NodeName, 
                                            SerialNOJoin, ChannelJoin, DVRType, ObjectType, remarks),  FileName);
        
    }
    /**
        * 函数:      getOperationTime
        * 函数描述:  返回当前日期的字符串形式表示（“yyyy-MM-dd HH:mm:ss.SSS”)
        *            主要用于数据库填写和中文日期表示方式
        * @return String   返回当前日期的字符串形式表示（“yyyy-MM-dd HH:mm:ss.SSS”）
    */
    public static String getOperationTime(){
        return databaseSDF.format(new Date());
    }
    
    public static String getLessThsnFixedLengthString(String Item, int Length){
        //如果Remarks>200（数据库表数据项长度）
        if (Item.length() > Length) return Item.substring(0, Length);
        else return Item;
    }
    
    /**
        * 函数:      getOperationTime
        * 函数描述:  返回给定日期的字符串形式表示（“yyyy-MM-dd HH:mm:ss.SSS”)
        *            主要用于数据库填写和中文日期表示方式
        * @param CurrentTime 给定日期
        * @return String   返回当前日期的字符串形式表示（“yyyy-MM-dd HH:mm:ss.SSS”)
    */
    public static String getOperationTime(Date CurrentTime){
        return databaseSDF.format(CurrentTime);//向数据库中输入datetime类型数据，必须用"yyyy-MM-dd HH:mm:ss.SSS"格式
    }
    /**
        * 函数:      previewOneChannel
        * 函数描述:  预览某一个通道的摄像
        * @param channelRealPlayer//正在预览的通道信息类对象channelRealPlayer
        * @param FileName
        * @return int   返回2,注册该设备后预览成功；1，利用传递的UserID预览成功；0，预览失败；-1出现错误
    */
    public static int previewOneChannel(ChannelRealPlayer channelRealPlayer,String FileName){
        int ReturnCode = -1;
        boolean IfLogin = false;
        try{
            
            if (channelRealPlayer == null) return -1;
            
            Panel CurrenPanel = channelRealPlayer.getPanelRealplay();//相对应的预览的Panel对象
            NativeLong Channel = channelRealPlayer.getChannel();//通道号

            if (Channel.intValue() < 1) return -1;
            if (CurrenPanel == null) return -1;
            
            NativeLong UserID = channelRealPlayer.getUserID();
            
            if (UserID == null || UserID.intValue() == -1){
                //原先该设备没有登录，则进行登录
                DeviceParaBean deviceparaBean = channelRealPlayer.getDeviceparaBean();
                if (deviceparaBean == null) return -1;

                //注册该设备
                int Index = getIndexOfDeviceList(deviceparaBean.getAnothername(),FileName);
                if (Index == -1) return -1;
                UserID = getUserID(deviceparaBean.getAnothername(),FileName);

                if (UserID.longValue() == -1){
                    return -1;
                }
                //登录后，将该OnePreviewIndexInfo中的UserID重新赋值

                channelRealPlayer.setBasicParas(deviceparaBean, Index, UserID);
                IfLogin = true;
                
            }
            
            //开始在该CurrenPanel预览该设备Channel通道录像
            ReturnCode = channelRealPlayer.previewThisChannel();
            
            if (ReturnCode == 1 && IfLogin) ReturnCode = 2;
            
        }catch (NumberFormatException | HeadlessException e){
            TxtLogger.append(FileName + FILE_NAME, "previewOneChannel()","系统预览某一个通道的视频摄像过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            ReturnCode = -1;
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "previewOneChannel()","系统预览某一个通道的视频摄像过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            ReturnCode = -1;
        }
        return ReturnCode;
    }
    
    /**
	 * 函数:      stopPreviewOneChannel
         * 函数描述:  结束某一个通道的摄像预览
         * @param FileName
         * @para channelRealPlayer  //正在预览的通道信息类对象channelRealPlayer
         * @return int   返回1，成功；0，失败；-1出现错误
    */
    public static int stopPreviewOneChannel(ChannelRealPlayer channelRealPlayer,String FileName){
        try{
            if (channelRealPlayer == null) return -1;
            if (FileName == null || FileName.equals("")) return -1;
            NativeLong lPreviewHandle = channelRealPlayer.getPreviewHandle();
            if (lPreviewHandle == null) return -1;
            return channelRealPlayer.stopPreviewThisChannel();
//            channelRealPlayer.doBeforeStopPreview();
//
//            if (hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandle)) {//设置停止预览
//                channelRealPlayer.doAfterStopPreview();
//                return 1;
//            }
//            else return 0;
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "stopPreviewOneChannel()","系统结束某一个通道的摄像预览过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }
    /**
        * 函数:      setBasicParasOfChannelRealPlayer
        * 函数描述:  设置某一个预览对象的基本设备参数
        * @param channelRealPlayer 正在预览的通道信息类对象channelRealPlayer
        * @param AnotherName    设备别名
        * @param FileName 调用该函数的文件名
        * @return int   返回1，成功；0，失败；-1出现错误
    */
    public static int setBasicParasOfChannelRealPlayer(ChannelRealPlayer channelRealPlayer,String DVRIP,String AnotherName,String FileName){
        int IndexListDevicePara = getIndexOfDeviceList(DVRIP,AnotherName,FileName);
        return setBasicParasOfChannelRealPlayer(channelRealPlayer,IndexListDevicePara,FileName);
    }
    /**
        * 函数:      setBasicParasOfChannelRealPlayer
        * 函数描述:  设置某一个预览对象的基本设备参数
        * @param channelRealPlayer 正在预览的通道信息类对象channelRealPlayer
        * @param IndexListDevicePara    设备列表索引号
        * @param FileName 调用该函数的文件名
        * @return int   返回1，成功；0，失败；-1出现错误
    */
    public static int setBasicParasOfChannelRealPlayer(ChannelRealPlayer channelRealPlayer,int IndexListDevicePara,String FileName){
        if (channelRealPlayer == null) return -1;
        if (IndexListDevicePara < 0) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        try{
            //listDeviceDetailPara获取设备参数表中的 设备序列号0,IP地址1,端口号2,用户名3,密码4,别名5,设备类型代码6，设备类型7（代码表中的代码名称）等参数；
            //现在加上登录ID8
//            ArrayList OneDeviceDetailPara =  (ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara);
            DeviceParaBean ParaBean = (DeviceParaBean)(((ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara)).get(0));
            
            channelRealPlayer.setBasicParas(ParaBean, IndexListDevicePara, getUserID(IndexListDevicePara,FileName));
            return 1;
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "setBasicParasOfChannelRealPlayer()","系统设置某一个预览对象的基本设备参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }
    
    /**
        * 函数:      CreateDeviceTree
        * 函数描述:  建立设备树，显示所有的设备
        * @param jTreeDevice  JTree控件名
        * @param m_DeviceRoot   JTree控件根节点
        * @param NodeType   节点类型：“CheckBoxTreeNode”JCheckBox节点类型；默认为DefaultMutableTreeNode类型
        * @param FileName   调用该函数的文件
    */
    public static void CreateDeviceTree(JTree jTreeDevice, DefaultMutableTreeNode m_DeviceRoot, String NodeType, String FileName){
        CreateDeviceTree( jTreeDevice, m_DeviceRoot, NodeType, "", FileName );
    }
    /**
        * 函数:      CreateDeviceTree
        * 函数描述:  建立设备树，显示所有的设备
        * @param jTreeDevice    JTree控件名
        * @param m_DeviceRoot   JTree控件根节点
        * @param NodeType       节点类型：“CheckBoxTreeNode”JCheckBox节点类型；默认为DefaultMutableTreeNode类型
        * @param DVRTypeCode    设备类型代码
        * @param FileName       调用该函数的文件
    */
    public static void CreateDeviceTree(JTree jTreeDevice, DefaultMutableTreeNode m_DeviceRoot, String NodeType, String DVRTypeCode, String FileName){
        try{
            if(jTreeDevice == null) return;
            if(m_DeviceRoot == null) return;
            if(FileName == null || FileName.equals("")) return;
            if(NodeType == null) return;
            if(DVRTypeCode == null) DVRTypeCode = "";

            DefaultTreeModel TreeModel = ((DefaultTreeModel) jTreeDevice.getModel());//获取树模型
            //一次只能选一个节点
            jTreeDevice.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
            
            m_DeviceRoot.removeAllChildren();//根节点删除所有的子节点
            TreeModel.reload();//将添加的节点显示到界面

            ArrayList listAllDevice = DeviceParaBean.getDeviceParaList( DVRTypeCode, FileName );
            
            DefaultMutableTreeNode DeviceNode;//设备节点

            for (int i=0;i<listAllDevice.size();i++){
                DeviceParaBean deviceParaBean = (DeviceParaBean)listAllDevice.get(i);
                switch (NodeType){
                        case "CheckBoxTreeNode":
                            DeviceNode = new CheckBoxTreeNode(deviceParaBean.getAnothername());
                            break;
                        default:
                            DeviceNode = new DefaultMutableTreeNode(deviceParaBean.getAnothername());
                            break;
                    }
                TreeModel.insertNodeInto(DeviceNode, m_DeviceRoot, i);//在根节点添加设备节点
            }
            TreeModel.reload();//将添加的节点显示到界面
            //jTreeDevice.setRowHeight(30);
            jTreeDevice.expandRow(0);

            //选中第一个组的第一个设备类型
//            jTreeResource.setSelectionInterval(1, 1);//选中第二个节点（第一个节点的第一个子节点）
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "CreateDeviceTree()","系统在建立设备资源树过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
//    /**
//        * 函数:      CreateDeviceResourceTypeTree
//        * 函数描述:  建立设备资源树:设备别名和设备资源分类名称
//        * @param jTreeResourceType  JTree控件名
//        * @param m_RootResourceType   JTree控件根节点
//        * @param NodeType   节点类型：“CheckBoxTreeNode”JCheckBox节点类型；默认为DefaultMutableTreeNode类型
//        * @param FileName   调用该函数的文件
//    */
//    public static void CreateDeviceResourceTypeTree(JTree jTreeResourceType, DefaultMutableTreeNode m_RootResourceType, String NodeType, String FileName){
//        try{
//            if(jTreeResourceType == null) return;
//            if(m_RootResourceType == null) return;
//            if(FileName == null || FileName.equals("")) return;
//            if(NodeType == null) return;
//
//            
//            //一次只能选一个节点
//            jTreeResourceType.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
//            
//            m_RootResourceType.removeAllChildren();//根节点删除所有的子节点
//            DefaultTreeModel TreeModel = ((DefaultTreeModel) jTreeResourceType.getModel());//获取树模型
//            TreeModel.reload();//将添加的节点显示到界面
//            //获取设备资源表中的“设备序列号-0”、“设备资源分类-1”、对应的“设备别名-2”（设备参数表中的设备别名）、设备资源分类名称-3（代码表中代码名称）
//            ArrayList listAllDeviceResourceType = DeviceResourceBean.getAllDeviceResourceTypeList(FileName);
//            
//            DefaultMutableTreeNode DeviceNode =  new DefaultMutableTreeNode(""), ResourceTypeNode =  new DefaultMutableTreeNode("");//设备节点
//            String DeviceName = "";
//            int NumOfDevice = 0;
//            int NumOfResouceType = 0;
//            
//            for (int i=0;i<listAllDeviceResourceType.size();i++){
//                ArrayList NewList = (ArrayList)listAllDeviceResourceType.get(i);
//                String NewDeviceName = (String)NewList.get(2);
//                if (!(NewDeviceName.equals(DeviceName))){
//                    NumOfResouceType = 0;//设备资源类型从0开始计数
//                    DeviceName = NewDeviceName;//重新将新的设备名赋值给DeviceName
//                    switch (NodeType){
//                        case "CheckBoxTreeNode":
//                            DeviceNode = new CheckBoxTreeNode(NewDeviceName);
//                            break;
//                        default:
//                            DeviceNode = new DefaultMutableTreeNode(NewDeviceName);
//                            break;
//                    }
//                    TreeModel.insertNodeInto(DeviceNode, m_RootResourceType, NumOfDevice++);//在根节点添加设备节点
//                }
// 
//                switch (NodeType){
//                    case "CheckBoxTreeNode":
//                        ResourceTypeNode = new CheckBoxTreeNode((String)NewList.get(3));
//                        break;
//                    default:
//                        ResourceTypeNode = new DefaultMutableTreeNode((String)NewList.get(3));
//                        break;
//                }
//                TreeModel.insertNodeInto(ResourceTypeNode, DeviceNode, NumOfResouceType++);//在根节点添加设备节点
// 
//        }
//            TreeModel.reload();//将添加的节点显示到界面
//            //jTreeResourceType.setRowHeight(30);
//            jTreeResourceType.expandRow(0);
//
//            //选中第一个组的第一个设备类型
////            jTreeResource.setSelectionInterval(1, 1);//选中第二个节点（第一个节点的第一个子节点）
//        }catch (Exception e){
//            TxtLogger.append(FileName + FILE_NAME, "CreateDeviceResourceTypeTree()","系统在建立设备资源树:显示所有的设备、资源类型过程中，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
//    }
    
    
    /**
        * 函数:      CreateDeviceResourceTree
        * 函数描述:  建立设备资源树
        *            1、显示所有的设备、资源类型、资源
        *            2、显示所有的设备、某一类资源
        * @param jTreeResource  JTree控件名
        * @param m_DeviceRootResource   JTree控件根节点
        * @param NodeType   节点类型：“CheckBoxTreeNode”JCheckBox节点类型；默认为DefaultMutableTreeNode类型
        * @param ResourceType 设备资源类型
        * @param FileName   调用该函数的文件
    */
    public static void CreateDeviceResourceTree(JTree jTreeResource, DefaultMutableTreeNode m_DeviceRootResource, String NodeType, String ResourceType, String FileName)
    {
        try{
            if(jTreeResource == null) return;
            if(m_DeviceRootResource == null) return;
            if(FileName == null || FileName.equals("")) return;
            if(NodeType == null) return;
            
            //如果ResourceType = null，ResourceType = ""或者all，则显示全部的资源，显示第1种：显示所有的设备、资源类型、资源
            //如果ResourceType = 某一类资源分类，则显示第2种：显示所有的设备、某一类资源
            
            boolean ifShowResouceType = false;//是否显示资源类型
            if(ResourceType == null || ResourceType.equals("") || ResourceType.equals("all"))  ifShowResouceType = true;//是否仅显示设备，不显示其资源
            
            
            //一次只能选一个节点
            jTreeResource.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
            
            m_DeviceRootResource.removeAllChildren();//根节点删除所有的子节点
            DefaultTreeModel TreeModel = ((DefaultTreeModel) jTreeResource.getModel());//获取树模型
            TreeModel.reload();//将添加的节点显示到界面
            ArrayList listAllDeviceResource;// = DeviceGroupBean.getAllDeviceGroupParaList(ResourceType,FileName + FILE_NAME); ;//设备分组表中的所有“节点名”,“别名”,“组名”,“设备资源分类”和对应的设备基本参数表中的“设备序列号”,“IP地址”等参数
            
            //获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
            //对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）
            listAllDeviceResource = DeviceResourceBean.getAllDeviceResourceParaList(ResourceType, FileName + FILE_NAME); 
            
            String DeviceName = "";
            String ResouceTypeName = "";
            int NumOfDevice = 0;
            int NumOfResouceType = 0;
            int NumOfResouce = 0;
            
            DefaultMutableTreeNode DeviceNode ,ResourceTypeNode, ResouceNode;//设备节点、资源类型节点、资源节点
            DefaultMutableTreeNode ResourceUpperNode;//资源节点的上级节点（可以是设备节点，也可以是资源类型节点）
            
            switch (NodeType){
                case "CheckBoxTreeNode":
                    DeviceNode = new CheckBoxTreeNode(DeviceName);
                    ResourceTypeNode =  new CheckBoxTreeNode("");
                    break;
                default:
                    DeviceNode = new DefaultMutableTreeNode(DeviceName);
                    ResourceTypeNode =  new DefaultMutableTreeNode("");
                    break;
            }
            
            for (int i=0;i<listAllDeviceResource.size();i++){

                //添加组名为二级节点newNode
                ArrayList NewList = (ArrayList)listAllDeviceResource.get(i);//
                String NewDeviceName = (String)NewList.get(1);
                String NewResouceTypeName = (String)NewList.get(4);
                
                DeviceResourceBean deviceResourceBean = (DeviceResourceBean)NewList.get(0);
                String SerialnoJoin = deviceResourceBean.getsSerialnoJoin();
                
                if (!(NewDeviceName.equals(DeviceName))){
                    NumOfResouceType = 0;//设备资源类型从0开始计数
                    NumOfResouce = 0;//设备资源序号从新开始计数
                    
                    switch (NodeType){
                        case "CheckBoxTreeNode":
                            DeviceNode = new CheckBoxTreeNode(NewDeviceName);
                            break;
                        default:
                            DeviceNode = new DefaultMutableTreeNode(NewDeviceName);
                            break;
                    }
                    TreeModel.insertNodeInto(DeviceNode, m_DeviceRootResource, NumOfDevice++);//在根节点添加设备节点
                }
                if (ifShowResouceType){
                    if ((!(NewResouceTypeName.equals(ResouceTypeName))) || (!(NewDeviceName.equals(DeviceName)))){
                        NumOfResouce = 0;//设备资源序号从新开始计数
                        
                        switch (NodeType){
                            case "CheckBoxTreeNode":
                                ResourceTypeNode = new CheckBoxTreeNode(NewResouceTypeName);
                                break;
                            default:
                                ResourceTypeNode = new DefaultMutableTreeNode(NewResouceTypeName);
                                break;
                        }
                        TreeModel.insertNodeInto(ResourceTypeNode, DeviceNode, NumOfResouceType++);//在根节点添加设备节点
                    }
                }
                
                //给新的资源节点赋值
                switch (NodeType){
                    case "CheckBoxTreeNode":
                        if (SerialnoJoin.equals("")){
                            ResouceNode = new CheckBoxTreeNode((String)NewList.get(1) + "_" + deviceResourceBean.getNodename());
                        }
                        else {
                            ResouceNode = new CheckBoxTreeNode((String)NewList.get(1) + "_" + (String)NewList.get(3) + "_" + deviceResourceBean.getNodename());
                        }
                        break;
                    default:
                        if (SerialnoJoin.equals("")){
                            ResouceNode = new DefaultMutableTreeNode((String)NewList.get(1) + "_" + deviceResourceBean.getNodename());
                        }
                        else {
                            ResouceNode = new DefaultMutableTreeNode((String)NewList.get(1) + "_" + (String)NewList.get(3) + "_" + deviceResourceBean.getNodename());
                        }
                        break;
                }
                
                if (ifShowResouceType) TreeModel.insertNodeInto(ResouceNode, ResourceTypeNode, NumOfResouce++);//在该设备节点添加资源节点
                else TreeModel.insertNodeInto(ResouceNode, DeviceNode, NumOfResouce++);//在该设备节点添加资源节点
                
                DeviceName = NewDeviceName;//重新将新的设备名赋值给DeviceName
                ResouceTypeName = NewResouceTypeName;//重新将新的设备名赋值给DeviceName

            }
            //jTreeResource.setRowHeight(50);
            TreeModel.reload();//将添加的节点显示到界面
            
            jTreeResource.expandRow(0);

            //选中第一个组的第一个设备类型
//            jTreeResource.setSelectionInterval(1, 1);//选中第二个节点（第一个节点的第一个子节点）
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "CreateDeviceResourceTree()","系统在建立设备资源树过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    
    
    /**
        * 函数:      CreateDeviceResourceTree
        * 函数描述:  建立设备资源树
        *            1、显示所有的设备、资源类型、资源
        *            2、显示所有的设备、某一类资源
        * @param jTreeResource  JTree控件名
        * @param m_DeviceRootResource   JTree控件根节点
        * @param NodeType   节点类型：“CheckBoxTreeNode”JCheckBox节点类型；默认为DefaultMutableTreeNode类型
        * @param DVRType    二级设备类型
        * @param ResourceType 设备资源类型
        * @param FileName   调用该函数的文件
    */
    public static void CreateOneTypeDeviceResourceTree(JTree jTreeResource, DefaultMutableTreeNode m_DeviceRootResource, String NodeType, 
            String DVRType, String ResourceType, String FileName)
    {
        try{
            if(jTreeResource == null) return;
            if(m_DeviceRootResource == null) return;
            if(FileName == null || FileName.equals("")) return;
            if(NodeType == null) return;
            
            //如果ResourceType = null，ResourceType = ""或者all，则显示全部的资源，显示第1种：显示所有的设备、资源类型、资源
            //如果ResourceType = 某一类资源分类，则显示第2种：显示所有的设备、某一类资源
            
            boolean ifShowResouceType = false;//是否显示资源类型
            if(ResourceType == null || ResourceType.equals("") || ResourceType.equals("all"))  ifShowResouceType = true;//是否仅显示设备，不显示其资源
            
            
            //一次只能选一个节点
            jTreeResource.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
            
            m_DeviceRootResource.removeAllChildren();//根节点删除所有的子节点
            DefaultTreeModel TreeModel = ((DefaultTreeModel) jTreeResource.getModel());//获取树模型
            TreeModel.reload();//将添加的节点显示到界面
            ArrayList listAllDeviceResource;// = DeviceGroupBean.getAllDeviceGroupParaList(ResourceType,FileName + FILE_NAME); ;//设备分组表中的所有“节点名”,“别名”,“组名”,“设备资源分类”和对应的设备基本参数表中的“设备序列号”,“IP地址”等参数
            
            //获取DeviceResourceBean（设备资源表中的“设备序列号”、“节点名”、“设备资源分类”、“接入设备的序列号”）-0
            //对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）
            listAllDeviceResource = DeviceResourceBean.getDeviceResourceParaList(DVRType, ResourceType, FileName + FILE_NAME); ;
            
            String DeviceName = "";
            String ResouceTypeName = "";
            int NumOfDevice = 0;
            int NumOfResouceType = 0;
            int NumOfResouce = 0;
            
            DefaultMutableTreeNode DeviceNode ,ResourceTypeNode, ResouceNode;//设备节点、资源类型节点、资源节点
            DefaultMutableTreeNode ResourceUpperNode;//资源节点的上级节点（可以是设备节点，也可以是资源类型节点）
            
            switch (NodeType){
                case "CheckBoxTreeNode":
                    DeviceNode = new CheckBoxTreeNode(DeviceName);
                    ResourceTypeNode =  new CheckBoxTreeNode("");
                    break;
                default:
                    DeviceNode = new DefaultMutableTreeNode(DeviceName);
                    ResourceTypeNode =  new DefaultMutableTreeNode("");
                    break;
            }
            
            for (int i=0;i<listAllDeviceResource.size();i++){

                //添加组名为二级节点newNode
                ArrayList NewList = (ArrayList)listAllDeviceResource.get(i);//
                String NewDeviceName = (String)NewList.get(1);
                String NewResouceTypeName = (String)NewList.get(4);
                
                DeviceResourceBean deviceResourceBean = (DeviceResourceBean)NewList.get(0);
                String SerialnoJoin = deviceResourceBean.getsSerialnoJoin();
                
                if (!(NewDeviceName.equals(DeviceName))){
                    NumOfResouceType = 0;//设备资源类型从0开始计数
                    NumOfResouce = 0;//设备资源序号从新开始计数
                    
                    switch (NodeType){
                        case "CheckBoxTreeNode":
                            DeviceNode = new CheckBoxTreeNode(NewDeviceName);
                            break;
                        default:
                            DeviceNode = new DefaultMutableTreeNode(NewDeviceName);
                            break;
                    }
                    TreeModel.insertNodeInto(DeviceNode, m_DeviceRootResource, NumOfDevice++);//在根节点添加设备节点
                }
                if (ifShowResouceType){
                    if ((!(NewResouceTypeName.equals(ResouceTypeName))) || (!(NewDeviceName.equals(DeviceName)))){
                        NumOfResouce = 0;//设备资源序号从新开始计数
                        
                        switch (NodeType){
                            case "CheckBoxTreeNode":
                                ResourceTypeNode = new CheckBoxTreeNode(NewResouceTypeName);
                                break;
                            default:
                                ResourceTypeNode = new DefaultMutableTreeNode(NewResouceTypeName);
                                break;
                        }
                        TreeModel.insertNodeInto(ResourceTypeNode, DeviceNode, NumOfResouceType++);//在根节点添加设备节点
                    }
                }
                
                //给新的资源节点赋值
                switch (NodeType){
                    case "CheckBoxTreeNode":
                        if (SerialnoJoin.equals("")){
                            ResouceNode = new CheckBoxTreeNode((String)NewList.get(1) + "_" + deviceResourceBean.getNodename());
                        }
                        else {
                            ResouceNode = new CheckBoxTreeNode((String)NewList.get(1) + "_" + (String)NewList.get(3) + "_" + deviceResourceBean.getNodename());
                        }
                        break;
                    default:
                        if (SerialnoJoin.equals("")){
                            ResouceNode = new DefaultMutableTreeNode((String)NewList.get(1) + "_" + deviceResourceBean.getNodename());
                        }
                        else {
                            ResouceNode = new DefaultMutableTreeNode((String)NewList.get(1) + "_" + (String)NewList.get(3) + "_" + deviceResourceBean.getNodename());
                        }
                        break;
                }
                
                if (ifShowResouceType) TreeModel.insertNodeInto(ResouceNode, ResourceTypeNode, NumOfResouce++);//在该设备节点添加资源节点
                else TreeModel.insertNodeInto(ResouceNode, DeviceNode, NumOfResouce++);//在该设备节点添加资源节点
                
                DeviceName = NewDeviceName;//重新将新的设备名赋值给DeviceName
                ResouceTypeName = NewResouceTypeName;//重新将新的设备名赋值给DeviceName

            }
            //jTreeResource.setRowHeight(50);
            TreeModel.reload();//将添加的节点显示到界面
            
            jTreeResource.expandRow(0);

            //选中第一个组的第一个设备类型
//            jTreeResource.setSelectionInterval(1, 1);//选中第二个节点（第一个节点的第一个子节点）
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "CreateDeviceResourceTree()","系统在建立设备资源树过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
//    /**
//        * 函数:      DelDeviceResourceTree
//        * 函数描述:  建立设备资源树
//        *            1、显示所有的设备、资源类型、资源
//        *            2、显示所有的设备、某一类资源
//        * @param jTreeResource  JTree控件名
//        * @param m_DeviceRootResource   JTree控件根节点
//        * @param NodeType   节点类型：“CheckBoxTreeNode”JCheckBox节点类型；默认为DefaultMutableTreeNode类型
//        * @param ResourceType 设备资源类型
//        * @param FileName   调用该函数的文件
//    */
//    public void DelTreeNodesKeepNVR(JTree jTreeResource, DefaultMutableTreeNode m_DeviceRootResource, String NodeType, String ResourceType, String FileName){
//        
//    }
    /**
        * 函数:      CreateGroupResourceTree
        * 函数描述:  建立设备资源树
        *            1、显示所有的分组、资源类型、资源
        *            2、显示所有的分组、某一类资源
        * @param jTreeResource  JTree控件名
        * @param m_DeviceRootResource   JTree控件根节点
        * @param NodeType   节点类型：“CheckBoxTreeNode”JCheckBox节点类型；默认为DefaultMutableTreeNode类型
        * @param ResourceType 设备资源类型
        * @param FileName   调用该函数的文件
    */
    public static void CreateGroupResourceTree(JTree jTreeResource, DefaultMutableTreeNode m_DeviceRootResource, String NodeType, String ResourceType, String FileName){
        
        try{
            
            if(jTreeResource == null) return;
            if(m_DeviceRootResource == null) return;
            if(FileName == null || FileName.equals("")) return;
            if(NodeType == null) return;
            //如果ResourceType = null，ResourceType = ""或者all，则显示全部的资源，显示第1种：显示所有的设备、资源类型、资源
            //如果ResourceType = 某一类资源分类，则显示第2种：显示所有的设备、某一类资源
            boolean ifShowResouceType = false;//是否显示资源类型
            if(ResourceType == null || ResourceType.equals("") || ResourceType.equals("all"))  ifShowResouceType = true;//是否仅显示设备，不显示其资源类型
            
            
            //一次只能选一个节点
            jTreeResource.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
//            ArrayList listDeviceGroupNames = DeviceGroupBean.getDeviceGroupNameList(FileName + FILE_NAME);//获取设备分组表中的““组名”列表
//            if (listDeviceGroupNames == null) return;
            
            m_DeviceRootResource.removeAllChildren();//根节点删除所有的子节点
            
            //TreeModel.reload();//将添加的节点显示到界面。reload出错，java.lang.NullPointerException
            //jTreeResource.updateUI();//用updateUI方法会给JTree的UI界面设置造成障碍，会让UI设置显示不出来。所以修改为validate（）方法
            //jTreeResource.validate();//使用 validate 方法会使容器再次布置其子组件。已经显示容器后，在修改此容器的子组件的时候（在容器中添加或移除组件，或者更改与布局相关的信息），应该调用上述方法。
            DefaultTreeModel TreeModel = ((DefaultTreeModel) jTreeResource.getModel());//获取树模型
            TreeModel.reload();//将添加的节点显示到界面。
            
            ArrayList listDeviceGroupParas = new ArrayList();// = DeviceGroupBean.getAllDeviceGroupParaList(ResourceType,FileName + FILE_NAME); ;//设备分组表中的所有“节点名”,“别名”,“组名”,“设备资源分类”和对应的设备基本参数表中的“设备序列号”,“IP地址”等参数
            
            //获取DeviceGroupBean（设备分组表中的“设备序列号”、“节点名”、“组名”、“设备资源分类”、“接入设备的序列号”）-0
            //         对应的“设备别名-1”、“IP地址-2”等参数，及对应的的接入设备的“设备别名-3”，设备资源分类名称-4（代码表中代码名称）
            listDeviceGroupParas = DeviceGroupBean.getAllDeviceGroupParaList(ResourceType,FileName + FILE_NAME); ;

            String GroupName = "";
            String ResouceTypeName = "";
            int NumOfGroup = 0;
            int NumOfResouceType = 0;
            int NumOfResouce = 0;
            
            DefaultMutableTreeNode GroupNode ,ResourceTypeNode, ResouceNode;//设备节点、资源类型节点、资源节点
            DefaultMutableTreeNode ResourceUpperNode;//资源节点的上级节点（可以是设备节点，也可以是资源类型节点）
            
            switch (NodeType){
                case "CheckBoxTreeNode":
                    GroupNode = new CheckBoxTreeNode(GroupName);
                    ResourceTypeNode =  new CheckBoxTreeNode("");
                    break;
                default:
                    GroupNode = new DefaultMutableTreeNode(GroupName);
                    ResourceTypeNode =  new DefaultMutableTreeNode("");
                    break;
            }
            
            for (int i=0;i<listDeviceGroupParas.size();i++){
                //添加组名为二级节点newNode
                ArrayList NewList = (ArrayList)listDeviceGroupParas.get(i);//
                DeviceGroupBean deviceGroupBean = (DeviceGroupBean)NewList.get(0);
                String NewGroupName = deviceGroupBean.getGroupName();
                String NewResouceTypeName = (String)NewList.get(4);
                
                
                String SerialnoJoin = deviceGroupBean.getsSerialNOJoin();
                //String DeviceName = (String)NewList.get(1);
                
                
                if (!(NewGroupName.equals(GroupName))){
                    NumOfResouceType = 0;//设备资源类型从0开始计数
                    NumOfResouce = 0;//设备资源序号从新开始计数
                    
                    switch (NodeType){
                        case "CheckBoxTreeNode":
                            GroupNode = new CheckBoxTreeNode(NewGroupName);
                            break;
                        default:
                            GroupNode = new DefaultMutableTreeNode(NewGroupName);
                            break;
                    }
                    TreeModel.insertNodeInto(GroupNode, m_DeviceRootResource, NumOfGroup++);//在根节点添加设备节点
                }
                /*1、如果组名不相等的情况下，肯定要插入资源类型节点
                2、如果组名相等的情况下，如果资源类型不相等，也要进行插入*/
                if (ifShowResouceType){
                    if ((!(NewResouceTypeName.equals(ResouceTypeName))) || (!(NewGroupName.equals(GroupName)))){
                        NumOfResouce = 0;//设备资源序号从新开始计数
                        
                        switch (NodeType){
                            case "CheckBoxTreeNode":
                                ResourceTypeNode = new CheckBoxTreeNode(NewResouceTypeName);
                                break;
                            default:
                                ResourceTypeNode = new DefaultMutableTreeNode(NewResouceTypeName);
                                break;
                        }
                        TreeModel.insertNodeInto(ResourceTypeNode, GroupNode, NumOfResouceType++);//在根节点添加设备节点
                    }
                }
                
                //给新的资源节点赋值
                switch (NodeType){
                    case "CheckBoxTreeNode":
                        if (SerialnoJoin.equals("")){
                            ResouceNode = new CheckBoxTreeNode((String)NewList.get(1) + "_" + deviceGroupBean.getNodename());
                        }
                        else {
                            ResouceNode = new CheckBoxTreeNode((String)NewList.get(1) + "_" + (String)NewList.get(3) + "_" + deviceGroupBean.getNodename());
                        }
                        break;
                    default:
                        if (SerialnoJoin.equals("")){
                            ResouceNode = new DefaultMutableTreeNode((String)NewList.get(1) + "_" + deviceGroupBean.getNodename());
                        }
                        else {
                            ResouceNode = new DefaultMutableTreeNode((String)NewList.get(1) + "_" + (String)NewList.get(3) + "_" + deviceGroupBean.getNodename());
                        }
                        break;
                }
                
                if (ifShowResouceType) TreeModel.insertNodeInto(ResouceNode, ResourceTypeNode, NumOfResouce++);//在该设备节点添加资源节点
                else TreeModel.insertNodeInto(ResouceNode, GroupNode, NumOfResouce++);//在该设备节点添加资源节点
                
                GroupName = NewGroupName;//重新将新的设备名赋值给DeviceName
                ResouceTypeName = NewResouceTypeName;//重新将新的设备名赋值给DeviceName

            }
            TreeModel.reload();//将添加的节点显示到界面
            //jTreeResource.expandRow(0);

            //选中第一个组的第一个设备类型
//            jTreeResource.setSelectionInterval(1, 1);//选中第二个节点（第一个节点的第一个子节点）
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "CreateGroupResourceTree()","系统在建立设备资源树过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      CreateResourceTree
        * 函数描述:  建立设备资源树
        * @param jTreeResource  JTree控件名
        * @param m_DeviceRootResource   JTree控件根节点
        * @param ResourceType 设备资源类型
        * @param FileName   调用该函数的文件
    */
    public static void CreateGroupResourceTree(JTree jTreeResource,DefaultMutableTreeNode m_DeviceRootResource,String ResourceType,String FileName){
        CreateGroupResourceTree(jTreeResource,m_DeviceRootResource,"",ResourceType,FileName);
    }
    
    
    /**
        * 函数:      getAnotherName
        * 函数描述:  获得该设备别名
        * @param DVRIP
        * @param FileName
        * @return String   失败返回""。
    */
    public static String getAnotherName(String DVRIP,String FileName){
        if (DVRIP == null || DVRIP.equals("")) return "";
        if (FileName == null || FileName.equals("")) return "";
        try{
            int IndexListDevicePara = getIndexOfDeviceList(DVRIP,null,FileName);
            if (IndexListDevicePara == -1) return "";
            //listDeviceDetailPara获取设备参数表中的 设备序列号0,IP地址1,端口号2,用户名3,密码4,别名5,设备类型代码6，设备类型7（代码表中的代码名称）等参数；
            //现在加上登录ID8
//            ArrayList OneDeviceDetailPara =  (ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara);
//            return (String)OneDeviceDetailPara.get(5);
            DeviceParaBean deviceParaBean = (DeviceParaBean)(((ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara)).get(0));
            return deviceParaBean.getAnothername();
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getAnotherName()","系统在获得该设备别名过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return "";
    }
    
    /**
        * 函数:      getSerialNO
        * 函数描述:  获得该设备序列号
        * @param AnotherName
        * @param FileName
        * @return String   失败返回null。
    */
    public static String getSerialNO(String AnotherName,String FileName){
        return getSerialNO(null,AnotherName,FileName);
    }
    /**
        * 函数:      getSerialNO
        * 函数描述:  获得该设备序列号
        * @param DVRIP  IP地址
        * @param AnotherName    设备别名
        * @param FileName   调用的文件名
        * @return String   失败返回null。
    */
    public static String getSerialNO(String DVRIP,String AnotherName,String FileName){
        
        if (FileName == null || FileName.equals("")) return "";
        int IndexListDevicePara = getIndexOfDeviceList(DVRIP,AnotherName,null,FileName);
        return getSerialNO(IndexListDevicePara, FileName);
    }
    /**
        * 函数:      getSerialNO
        * 函数描述:  获得该设备序列号
        * @param IndexListDevicePara   listDeviceDetailPara设备参数表中的索引
        * @param FileName   调用的文件名
        * @return String   失败返回""。
    */
    public static String getSerialNO(int IndexListDevicePara,String FileName){
        
        if (IndexListDevicePara == -1) return "";
        if (FileName == null || FileName.equals(""))  return "";
        try{
            //listDeviceDetailPara获取设备参数表中的 设备序列号0,IP地址1,端口号2,用户名3,密码4,别名5,设备类型代码6，设备类型7（代码表中的代码名称）等参数；
            //现在加上登录ID8
            DeviceParaBean deviceParaBean = (DeviceParaBean)(((ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara)).get(0));
            return deviceParaBean.getSerialNO();
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getSerialNO()","系统在获得该设备序列号过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return "";
    }
    /**
        * 函数:      getDeviceParaBean
        * 函数描述:  获得该设备参数Bean
        * @param DVRIP
        * @param AnotherName
        * @param SerialNO
        * @param FileName
        * @return DeviceParaBean   失败返回null。
    */
    public static DeviceParaBean getDeviceParaBean(String DVRIP,String AnotherName,String SerialNO,String FileName){
        int IndexListDevicePara = getIndexOfDeviceList(DVRIP,AnotherName,SerialNO,FileName);
        return getDeviceParaBean( IndexListDevicePara, FileName);

    }
    /**
        * 函数:      getDeviceParaBeanJoin
        * 函数描述:  获得接入设备的DeviceParaBean参数
        * @param IndexListDevicePara   listDeviceDetailPara设备参数表中的索引
        * @param Channel //IP通道，>32
        * @param FileName
        * @return DeviceParaBean   失败返回null。
    */
    public static DeviceParaBean getDeviceParaBeanJoin(int IndexListDevicePara, int Channel, String FileName){
        try{
            if (IndexListDevicePara == -1) return null;
            HCNetSDK.NET_DVR_IPPARACFG StrIpparaCfg = CommonParas.getStrIpparacfg(IndexListDevicePara , FILE_NAME);
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDeviceInfo = CommonParas.getStrDeviceInfo(IndexListDevicePara ,FILE_NAME);
            if (StrIpparaCfg == null) return null;
            if (StrDeviceInfo == null) return null;
            int iIPChanel = Channel - (HCNetSDK.MAX_ANALOG_CHANNUM + StrDeviceInfo.byStartChan);
            if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return null;//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）

            byte iDevID = StrIpparaCfg.struIPChanInfo[iIPChanel].byIPID;
            String IP4 = new String (StrIpparaCfg.struIPDevInfo[iDevID - 1].struIP.sIpV4).trim();
            int IndexJoin = CommonParas.getIndexOfDeviceList(IP4, "", FILE_NAME);
            if (IndexJoin > -1) return CommonParas.getDeviceParaBean(IndexJoin,FILE_NAME);

        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getDeviceParaBeanJoin()","系统在获得接入设备的DeviceParaBean参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return null;
    }
    /**
        * 函数:      setDeviceParaBean
        * 函数描述:  设置列表中的DeviceParaBean
        * @param deviceParaBean
        * @param FileName
        * @return int 成功返回1；失败返回-1
    */
    public static int setDeviceParaBean(DeviceParaBean deviceParaBean,String FileName){
        try{
            int IndexListDevicePara = getIndexOfDeviceList(null,null,deviceParaBean.getSerialNO(),FileName);
            if (IndexListDevicePara == -1) return -1;
            ((ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara)).set(0, deviceParaBean);
            return 1;
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getSerialNO()","系统在获得该设备序列号过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }
    /**
        * 函数:      delOneFromListDeviceDetailPara
        * 函数描述:  从g_listDeviceDetailPara列表中删除设备信息
        * @param SerialNO   设备序列号
        * @param FileName
        * @return int 成功返回1；失败返回-1
    */
    public static int delOneFromListDeviceDetailPara(String SerialNO,String FileName){
        try{
            int IndexListDevicePara = getIndexOfDeviceList(null,null,SerialNO,FileName);
            if (IndexListDevicePara == -1) return -1;
            ArrayList NewList = (ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara);
            
            //报警布防句柄AlarmHandle-4;
            //撤销设备报警布防
            NativeLong AlarmHandle = (NativeLong)NewList.get(4);
            if (AlarmHandle.intValue() > -1) hCNetSDK.NET_DVR_CloseAlarmChan_V30(AlarmHandle);
            //注销设备
            NativeLong UserID = (NativeLong)NewList.get(2);
            if (UserID.intValue() > -1) hCNetSDK.NET_DVR_Logout_V30(UserID);
            //从g_listDeviceDetailPara列表中删除设备信息
            g_listDeviceDetailPara.remove(IndexListDevicePara);
            
            return 1;
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "delOneFromListDeviceDetailPara()","系统在从g_listDeviceDetailPara列表中删除设备信息过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }
    /**
        * 函数:      getDeviceParaBean
        * 函数描述:  获得该设备参数Bean
        * @param AnotherName
        * @param FileName
        * @return DeviceParaBean   失败返回null。
    */
    public static DeviceParaBean getDeviceParaBean(String AnotherName,String FileName){
        int IndexListDevicePara = getIndexOfDeviceList(AnotherName,FileName);
        return getDeviceParaBean( IndexListDevicePara, FileName);
    }
    /**
        * 函数:      getDeviceParaBean
        * 函数描述:  获得该设备参数Bean
        * @param IndexListDevicePara   listDeviceDetailPara设备参数表中的索引
        * @param FileName
        * @return DeviceParaBean   失败返回null。
    */
    public static DeviceParaBean getDeviceParaBean(int IndexListDevicePara,String FileName){
        try{
            if (IndexListDevicePara == -1) return null;
               //listDeviceDetailPara获取设备参数表中的 设备序列号0,IP地址1,端口号2,用户名3,密码4,别名5,设备类型代码6，设备类型7（代码表中的代码名称）等参数；
               //现在加上登录ID8
            return (DeviceParaBean)(((ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara)).get(0));
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getDeviceParaBean()","系统在获得该设备参数Bean过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return null;
     }
    /**
	 * 函数:      getStrDeviceInfo
         * 函数描述:  获得该设备别名对应的NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30
         * @param AnotherName  设备别名
         * @param FileName
         * @return NET_DVR_DEVICEINFO_V30   该设备别名对应的NET_DVR_DEVICEINFO_V30。如果登录失败返回为null
    */
    public static HCNetSDK.NET_DVR_DEVICEINFO_V30 getStrDeviceInfo(String AnotherName,String FileName){
        int IndexListDevicePara = getIndexOfDeviceList(AnotherName,FileName);
        return getStrDeviceInfo(IndexListDevicePara,FileName);
    }
    /**
	 * 函数:      getStrDeviceInfo
         * 函数描述:  获得该设备列表索引对应的NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30
         * @param IndexListDevicePara   listDeviceDetailPara设备参数表中的索引
         * @param FileName
         * @return NET_DVR_DEVICEINFO_V30   该设备别名对应的NET_DVR_DEVICEINFO_V30。如果登录失败返回为null
    */
    public static HCNetSDK.NET_DVR_DEVICEINFO_V30 getStrDeviceInfo(int IndexListDevicePara,String FileName){
        if (IndexListDevicePara == -1) return null;
        if (FileName == null || FileName.equals(""))  return null;
        HCNetSDK.NET_DVR_DEVICEINFO_V40 StruDeviceInfo40 = getStrDeviceInfo40(IndexListDevicePara, FileName);
        return StruDeviceInfo40==null?null:StruDeviceInfo40.struDeviceV30;
//        try{
//            //listDeviceDetailPara获取设备参数表中的 DeviceParaBean0,设备类型1（代码表中的代码名称）等参数；
//            //现在加上登录ID-2，再加上NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30-3;报警布防句柄AlarmHandle-4;
//            //IP设备资源及IP通道资源配置结构体NET_DVR_IPPARACFG-5
//            ArrayList OneDeviceDetailPara =  (ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara);
//            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDeviceInfo = (HCNetSDK.NET_DVR_DEVICEINFO_V30)OneDeviceDetailPara.get(3);
//            if (StrDeviceInfo != null) return StrDeviceInfo;
//            
//            DeviceParaBean deviceParaBean = (DeviceParaBean)(OneDeviceDetailPara.get(0));
//
//            StrDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
//            NativeLong UserID = hCNetSDK.NET_DVR_Login_V30(deviceParaBean.getDVRIP(), Short.parseShort(deviceParaBean.getServerport()), 
//                                                            deviceParaBean.getUsername(), deviceParaBean.getPassword(), StrDeviceInfo);
//
//            if (UserID.intValue() > -1){
//                OneDeviceDetailPara.set(2, UserID);//设置该UserID
//                OneDeviceDetailPara.set(3, StrDeviceInfo);//NET_DVR_Login_V30()参数结构设备参数结构体
//                return StrDeviceInfo;
//            }else{
//                CommonParas.showErrorMessage(errorMsgOfLoginFail, deviceParaBean.getAnothername() , FILE_NAME);
//                CommonParas.SystemWriteErrorLog("", deviceParaBean.getAnothername(),  errorLogOfLoginFail,  deviceParaBean.getSerialNO(),  CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE,  FILE_NAME);
//            }
//        }catch (Exception e){
//            TxtLogger.append(FileName + FILE_NAME, "getStrDeviceInfo()","系统在获得该设备列表索引对应的NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30过程中，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
//        
//        return null;
    }
    
    /**
	 * 函数:      getStrDeviceInfo
         * 函数描述:  获得该设备别名对应的NET_DVR_Login_V40()设备参数结构体NET_DVR_DEVICEINFO_V40
         * @param AnotherName  设备别名
         * @param FileName
         * @return NET_DVR_DEVICEINFO_V30   该设备别名对应的NET_DVR_DEVICEINFO_V30。如果登录失败返回为null
    */
    public static HCNetSDK.NET_DVR_DEVICEINFO_V40 getStrDeviceInfo40(String AnotherName,String FileName){
        int IndexListDevicePara = getIndexOfDeviceList(AnotherName,FileName);
        return getStrDeviceInfo40(IndexListDevicePara,FileName);
    }
    /**
	 * 函数:      getStrDeviceInfo
         * 函数描述:  获得该设备列表索引对应的NET_DVR_Login_V40()设备参数结构体NET_DVR_DEVICEINFO_V40
         * @param IndexListDevicePara   listDeviceDetailPara设备参数表中的索引
         * @param FileName
         * @return NET_DVR_DEVICEINFO_V30   该设备别名对应的NET_DVR_DEVICEINFO_V30。如果登录失败返回为null
    */
    public static HCNetSDK.NET_DVR_DEVICEINFO_V40 getStrDeviceInfo40(int IndexListDevicePara,String FileName){
        if (IndexListDevicePara == -1) return null;
        if (FileName == null || FileName.equals(""))  return null;
        try{
            //listDeviceDetailPara获取设备参数表中的 DeviceParaBean0,设备类型1（代码表中的代码名称）等参数；
            //现在加上登录ID-2，再加上NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V40-3;报警布防句柄AlarmHandle-4;
            //IP设备资源及IP通道资源配置结构体NET_DVR_IPPARACFG-5
            ArrayList OneDeviceDetailPara =  (ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara);
            HCNetSDK.NET_DVR_DEVICEINFO_V40 StrDeviceInfo = (HCNetSDK.NET_DVR_DEVICEINFO_V40)OneDeviceDetailPara.get(3);
            if (StrDeviceInfo != null) return StrDeviceInfo;
            
            DeviceParaBean deviceParaBean = (DeviceParaBean)(OneDeviceDetailPara.get(0));

            StrDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();
            NativeLong UserID = HCNetSDKExpand.NET_DVR_Login_V40(deviceParaBean.getDVRIP(), Short.parseShort(deviceParaBean.getServerport()), 
                                                            deviceParaBean.getUsername(), deviceParaBean.getPassword(), StrDeviceInfo,FileName);

            if (UserID.intValue() > -1){
                OneDeviceDetailPara.set(2, UserID);//设置该UserID
                OneDeviceDetailPara.set(3, StrDeviceInfo);//NET_DVR_Login_V30()参数结构设备参数结构体
                return StrDeviceInfo;
            }else{
                CommonParas.showErrorMessage(errorMsgOfLoginFail, deviceParaBean.getAnothername() , FILE_NAME);
                CommonParas.SystemWriteErrorLog("", deviceParaBean.getAnothername(),  errorLogOfLoginFail,  deviceParaBean.getSerialNO(),  CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE,  FILE_NAME);
            }
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getStrDeviceInfo()","系统在获得该设备列表索引对应的NET_DVR_Login_V40()设备参数结构体过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        
        return null;
    }
    
    public static String getCharEncodeType(int IndexListDevicePara,String FileName){
        if (IndexListDevicePara == -1) return "";
        if (FileName == null || FileName.equals(""))  return "";
        try{
            HCNetSDK.NET_DVR_DEVICEINFO_V40 StruDeviceInfo40 = getStrDeviceInfo40(IndexListDevicePara, FileName);
            byte byCharEncodeType= StruDeviceInfo40==null?-1:StruDeviceInfo40.byCharEncodeType;
            return byCharEncodeType==-1?"":CHAR_ENCODE_TYPE[byCharEncodeType];
        } catch (Exception e) {
            TxtLogger.append(FILE_NAME, "getCharEncodeType()","系统在获取设备字符编码类型过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return "";
    }
    public static String convertUTF8ToOther(String Original,String OtherEncodeType){
        if (OtherEncodeType == null)  return "";
        if (OtherEncodeType == null || OtherEncodeType.equals(""))  return Original;
        try {
            return new String(Original.getBytes("UTF-8"),OtherEncodeType);
        } catch (UnsupportedEncodingException e) {
            TxtLogger.append(FILE_NAME, "convertUTF8ToOther()","系统在字符串编码转换过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return Original;
    }
    
    public static String convertOtherToUTF8(String Original,String OriginalEncodeType){
        
        if (Original == null)  return "";
        
        try {
            if (OriginalEncodeType == null || OriginalEncodeType.equals(""))  return  new String(Original.getBytes(),"GBK");
            return new String(Original.getBytes(OriginalEncodeType),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            TxtLogger.append(FILE_NAME, "convertOtherToUTF8()","系统在字符串编码转换过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return Original;
    }
    
    public static String getEncoding(String str) {       
        //SDK中字符编码格式：0- 无字符编码信息(老设备，暂时都按GBK，即中国大陆)，1- GB2312(简体中文)，2- GBK，3- BIG5(繁体中文)，4- Shift_JIS(日文)，
        //5- EUC-KR(韩文)，6- UTF-8，7- ISO8859-1，8- ISO8859-2，9- ISO8859-3，…，依次类推，21- ISO8859-15(西欧) 
        try{
            for (int i=1;i<CHAR_ENCODE_TYPE.length;i++){
                if (str.equals(new String(str.getBytes(CHAR_ENCODE_TYPE[i]), CHAR_ENCODE_TYPE[i]))) {        
                   return CHAR_ENCODE_TYPE[i];        
                }        
            }
        } catch (UnsupportedEncodingException e) {
            TxtLogger.append(FILE_NAME, "convertOtherToUTF8()","系统在字符串编码转换过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return CHAR_ENCODE_TYPE[0]; //默认值 
    }
    /**
        * 函数:      getStrDeviceInfoJoin
        * 函数描述:  获得接入设备的NET_DVR_DEVICEINFO_V30参数
        * @param IndexListDevicePara   listDeviceDetailPara设备参数表中的索引
        * @param Channel //IP通道，>32
        * @param FileName
        * @return DeviceParaBean   失败返回null。
    */
    public static HCNetSDK.NET_DVR_DEVICEINFO_V30 getStrDeviceInfoJoin(int IndexListDevicePara, int Channel, String FileName){
        try{
            if (IndexListDevicePara == -1) return null;
            HCNetSDK.NET_DVR_IPPARACFG StrIpparaCfg = CommonParas.getStrIpparacfg(IndexListDevicePara , FILE_NAME);
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDeviceInfo = CommonParas.getStrDeviceInfo(IndexListDevicePara ,FILE_NAME);
            if (StrIpparaCfg == null) return null;
            if (StrDeviceInfo == null) return null;
            int iIPChanel = Channel - (HCNetSDK.MAX_ANALOG_CHANNUM + StrDeviceInfo.byStartChan);
            if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return null;//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）

            byte iDevID = StrIpparaCfg.struIPChanInfo[iIPChanel].byIPID;
            String IP4 = new String (StrIpparaCfg.struIPDevInfo[iDevID - 1].struIP.sIpV4).trim();
            int IndexJoin = CommonParas.getIndexOfDeviceList(IP4, "", FILE_NAME);
            if (IndexJoin > -1) return CommonParas.getStrDeviceInfo(IndexJoin, FILE_NAME);

        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getDeviceParaBeanJoin()","系统在获得接入设备的DeviceParaBean参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return null;
    }
    /**
        * 函数:      getStrDeviceInfoJoin
        * 函数描述:  获得接入设备的NET_DVR_DEVICEINFO_V30参数
        * @param IndexListDevicePara   listDeviceDetailPara设备参数表中的索引
        * @param Channel //IP通道，>32
        * @param FileName
        * @return DeviceParaBean   失败返回null。
    */
    public static HCNetSDK.NET_DVR_DEVICEINFO_V40 getStrDeviceInfoJoin40(int IndexListDevicePara, int Channel, String FileName){
        try{
            if (IndexListDevicePara == -1) return null;
            HCNetSDK.NET_DVR_IPPARACFG StrIpparaCfg = CommonParas.getStrIpparacfg(IndexListDevicePara , FILE_NAME);
            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDeviceInfo = CommonParas.getStrDeviceInfo(IndexListDevicePara ,FILE_NAME);
            if (StrIpparaCfg == null) return null;
            if (StrDeviceInfo == null) return null;
            int iIPChanel = Channel - (HCNetSDK.MAX_ANALOG_CHANNUM + StrDeviceInfo.byStartChan);
            if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return null;//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）

            byte iDevID = StrIpparaCfg.struIPChanInfo[iIPChanel].byIPID;
            String IP4 = new String (StrIpparaCfg.struIPDevInfo[iDevID - 1].struIP.sIpV4).trim();
            int IndexJoin = CommonParas.getIndexOfDeviceList(IP4, "", FILE_NAME);
            if (IndexJoin > -1) return CommonParas.getStrDeviceInfo40(IndexJoin, FILE_NAME);

        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getDeviceParaBeanJoin()","系统在获得接入设备的DeviceParaBean参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return null;
    }
    /**
        * 函数:      getStrIpparacfg
        * 函数描述:  NET_DVR_IPPARACFGIP设备资源及IP通道资源配置结构体。
        * @param DVRIP
        * @param FileName
        * @return HCNetSDK.NET_DVR_IPPARACFG   失败返回null。
    */
    public static HCNetSDK.NET_DVR_IPPARACFG getStrIpparacfg(String DVRIP,String FileName){
    
        if (DVRIP == null || DVRIP.equals("")) return null;
        if (FileName == null || FileName.equals("")) return null;
        int IndexListDevicePara = getIndexOfDeviceList(DVRIP,null,FileName);
        return getStrIpparacfg(IndexListDevicePara,FileName);

    }
    
//    public 
    
    /**
        * 函数:      getStrIpparacfg
        * 函数描述:  NET_DVR_IPPARACFGIP设备资源及IP通道资源配置结构体。
        * @param IndexListDevicePara    listDeviceDetailPara设备参数表中的索引
        * @param FileName
        * @return HCNetSDK.NET_DVR_IPPARACFG   失败返回null。
    */
    public static HCNetSDK.NET_DVR_IPPARACFG getStrIpparacfg(int IndexListDevicePara,String FileName){
        if (IndexListDevicePara == -1) return null;
        if (FileName == null || FileName.equals("")) return null;
        try{
            
            ArrayList OneDeviceDetailPara =  (ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara);
            //listDeviceDetailPara获取设备参数表中的 DeviceParaBean0,设备类型1（代码表中的代码名称）等参数；
        //现在加上登录ID-2，再加上NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30-3;报警布防句柄AlarmHandle-4;
        //IP设备资源及IP通道资源配置结构体NET_DVR_IPPARACFG-5
            HCNetSDK.NET_DVR_IPPARACFG StrIpparacfg = (HCNetSDK.NET_DVR_IPPARACFG)OneDeviceDetailPara.get(5);
            if (StrIpparacfg != null) return StrIpparacfg;
            
            NativeLong lUserID = getUserID(IndexListDevicePara,FileName);
            if(lUserID.intValue() == -1) return null;
            
            IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
            HCNetSDK.NET_DVR_IPPARACFG strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
            strIpparaCfg.write();
            Pointer lpIpParaConfig = strIpparaCfg.getPointer();
            boolean bGetIpCfg = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0), lpIpParaConfig, strIpparaCfg.size(), ibrBytesReturned);
            strIpparaCfg.read();
            
            if (bGetIpCfg){
                OneDeviceDetailPara.set(5, strIpparaCfg);//设置该NET_DVR_IPPARACFG
                return strIpparaCfg;
            }

        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getStrIpparacfg()","系统在获得设备资源及IP通道资源配置结构体过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return null;
        
    }
    /**
        * 函数:      getAnotherNameJoinFromNode
        * 函数描述:  获得设备资源节点获得接入设备名
        * @param TreeNode 设备资源树节点名。如：录像机_枪机_IP监控点_01
        * @return String   接入设备名。失败返回""
    */
    public static String getAnotherNameJoinFromNode(String TreeNode){
        if (TreeNode == null || TreeNode.equals("")) return "";
        if (TreeNode.lastIndexOf("IP") > -1) {
            String[] ss =  TreeNode.split("_");
            if (ss.length >= 4) return ss[1];
        }
        return "";
    }
    /**
        * 函数:      getDVRNodeFromTreeNode
        * 函数描述:  根据设备资源树节点获得设备资源节点名
        * @param TreeNode 设备资源节点名。如：录像机_枪机_IP监控点_01
        * @return String   设备节点。失败返回""
    */
    public static String getDVRNodeFromTreeNode(String TreeNode){
//        if (TreeNode == null || TreeNode.equals("")) return "";
//        String[] ss =  TreeNode.split("_");
//        if (TreeNode.lastIndexOf("IP") > -1) {
//            if (ss.length >= 4) return ss[ss.length-2] + "_" + ss[ss.length-1];
//        }else{
//            if (ss.length >= 3) return ss[ss.length-2] + "_" + ss[ss.length-1];
//        }
//        return "";
        
        if (TreeNode == null || TreeNode.equals("")) return "";
        int Index  = TreeNode.lastIndexOf("_", TreeNode.length()-4);//反向搜索得到倒数第二个"_"的索引
        if (Index > -1) return TreeNode.substring(Index+1);
        return "";
    }
    /**
        * 函数:      getArraysFromTreeNode
        * 函数描述:  根据设备资源树节点获得数组：设备名、接入设备名、设备资源节点名
        * @param TreeNode 设备资源节点名。如：录像机_枪机_IP监控点_01
        * @return String   设备节点。失败返回""
    */
    public static String[] getArraysFromTreeNode(String TreeNode){
        String[] ReturnA = new String[]{"","",""};
        if (TreeNode == null || TreeNode.equals("")) return ReturnA;
        String[] ss =  TreeNode.split("_");
        if (TreeNode.lastIndexOf("IP") > -1) {
            if (ss.length == 4) {
                ReturnA[0] = ss[0];
                ReturnA[1] = ss[1];
                ReturnA[2] = ss[2] + "_" + ss[3];
            }
        }else{
            if (ss.length == 3) {
                ReturnA[0] = ss[0];
                ReturnA[1] = "";
                ReturnA[2] = ss[1] + "_" + ss[2];
            }
        }
        return ReturnA;
    }
    
    /**
        * 函数:      getArraysSerialnoFromTreeNode
        * 函数描述:  根据设备资源树节点获得数组：设备序列号、接入设备序列号、设备资源节点名
        * @param TreeNode 设备资源节点名。如：录像机_枪机_IP监控点_01
        * @return String   设备节点。失败返回""
    */
    public static String[] getArraysSerialnoFromTreeNode(String TreeNode){
        String[] ReturnA = new String[]{"","",""};
        if (TreeNode == null || TreeNode.equals("")) return ReturnA;
        String[] ss =  TreeNode.split("_");
        if (TreeNode.lastIndexOf("IP") > -1) {
            if (ss.length == 4) {
                ReturnA[0] = getSerialNO(ss[0], FILE_NAME);
                ReturnA[1] = getSerialNO(ss[1], FILE_NAME);
                ReturnA[2] = ss[2] + "_" + ss[3];
            }
        }else{
            if (ss.length == 3) {
                ReturnA[0] = getSerialNO(ss[0], FILE_NAME);
                ReturnA[1] = "";
                ReturnA[2] = ss[1] + "_" + ss[2];
            }
        }
        return ReturnA;
    }
    
    /**
        * 函数:      getNodeName
        * 函数描述:  根据该设备当前的通道号获得节点字符串
        * @param NodeNo    节点序号:设备当前的通道号、报警输入、输出的序号
        * @param IfIP   是否是IP设备资源
        * @param NodeType   节点类型：1通道号；2报警输入序号；3报警输出序号
        * @return String   当前通道的节点名，比如：监控点_03、IP监控点_01、报警输入_10
    */
    public static String getNodeName(int NodeNo, boolean IfIP, String NodeType){
        String sIP = "";
        if (IfIP) sIP = "IP";
        String NodeName = "";
        switch (NodeType){
            case DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE :
                NodeName = sIP + DVRResourceType.NODETYPE_CHANNEL + "_" + getNumberToTwoBitsStr(NodeNo);
                break;
            case DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_CODE :
                NodeName = sIP + DVRResourceType.NODETYPE_ALARMIN + "_" + getNumberToTwoBitsStr(NodeNo);
                break;
            case DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_CODE:
                NodeName = sIP + DVRResourceType.NODETYPE_ALARMOUT + "_" + getNumberToTwoBitsStr(NodeNo);
                break;
            case DVRResourceType.RESTYPE_ENCODINGDVR_DISK_CODE:
                NodeName = sIP + DVRResourceType.NODETYPE_DISK + "_" + getNumberToTwoBitsStr(NodeNo);
                break;
            default:
                break;
        }
        return NodeName.trim();
    }

    /**
	 * 函数:      getNodeNumber
         * 函数描述:  将数字以两位的字符串表示
	 * @param Channum	
         * @return String：字符串。.
     */
    public static String getNumberToTwoBitsStr(int Channum){
        if (Channum < 10 && Channum >0) return "0" + Channum;
        else if (Channum >=10 && Channum < 100) return Integer.toString(Channum);
        else {return "00";}
    }
    /**
        * 函数:      getIndexOfArray
        * 函数描述:   获取元素在数组中的索引
        * @param arr    数组
        * @param value2 元素
        * @return int： 元素在数组中的索引；如果不存在，返回-1。
     */
    public static int getIndexOfArray(String[] arr,String value2){  
        if (arr==null || arr.length==0 || value2.isEmpty()) return -1;
        for(int i=0;i<arr.length;i++){  
            if(arr[i].equals(value2)){  
                return i;  
            }
        }  
        return -1;
    }
    /**
	 * 函数:      getChannelNumber
         * 函数描述:  根据节点字符串获得该设备当前的通道号
         * @param ChannelNode   当前通道的节点名，比如：监控点_03、IP监控点_01、报警输入_10
         * @return int   设备当前的通道号、报警输入、输出的序号
    */
    public static int getChannel(String ChannelNode){
        int Channel = Integer.parseInt(ChannelNode.substring(ChannelNode.lastIndexOf("_") + 1 , ChannelNode.length()));
        if (ChannelNode.lastIndexOf("IP") > -1) Channel = Channel + HCNetSDK.MAX_ANALOG_CHANNUM;//32;//IP通道号要加32
        return Channel;
    }
  
    /**
        * 函数:      getIndexJoinOfDeviceList
        * 函数描述:  获得IP设备某接入设备的设备列表listDeviceDetailPara索引
        * @param StrDevInfo 设备登陆后得到的设备参数结构体
        * @param strIpparaCfg IP设备资源及IP通道资源配置结构体
        * @param Channel    预览以及参数配置要使用的音视频通道号
        * @param FileName   调用的文件名
        * @return int   该设备别名对应的设备所对应的设备列表getIndexOfDeviceList索引。如果没有则为-1
    */
    public static int getIndexJoinOfDeviceList(HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo, HCNetSDK.NET_DVR_IPPARACFG strIpparaCfg, int Channel, String FileName){
        try{
            if (StrDevInfo == null) return -1;
            //下面判断是否是接入设备，根据HCNetSDK.NET_DVR_IPPARACFG，得出接入设备参数
            if (strIpparaCfg == null) return -1;
            
            int iIPChanel = Channel - (HCNetSDK.MAX_ANALOG_CHANNUM + StrDevInfo.byStartChan);
            if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return -1;//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）

            byte iDevID = strIpparaCfg.struIPChanInfo[iIPChanel].byIPID;
            String IP4 = new String (strIpparaCfg.struIPDevInfo[iDevID - 1].struIP.sIpV4).trim();
            return getIndexOfDeviceList(IP4,"",FileName);

        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getIndexJoinOfDeviceList()","系统在获取IP设备某接入设备的设备列表listDeviceDetailPara索引过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

        return -1;
    }
    /**
        * 函数:      getDVRIPJoin
        * 函数描述:  获得IP设备某通道接入设备的IP地址
        * @param StrDevInfo 设备登陆后得到的设备参数结构体
        * @param strIpparaCfg IP设备资源及IP通道资源配置结构体
        * @param Channel    预览以及参数配置要使用的音视频通道号
        * @param FileName   调用的文件名
        * @return StringIP设备某通道接入设备的IP地址，失败返回""。
    */
    public static String getDVRIPJoin(HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo, HCNetSDK.NET_DVR_IPPARACFG strIpparaCfg, int Channel, String FileName){
        try{
            if (StrDevInfo == null) return "";

            //下面判断是否是接入设备，根据HCNetSDK.NET_DVR_IPPARACFG，得出接入设备参数
            if (strIpparaCfg == null) return "";
            int iIPChanel = Channel - (HCNetSDK.MAX_ANALOG_CHANNUM + StrDevInfo.byStartChan);
            if (iIPChanel < 0 || iIPChanel > HCNetSDK.MAX_IP_CHANNEL -1) return "";//IP通道资源struIPChanInfo的下标索引值（0到MAX_IP_CHANNEL -1）

            byte iDevID = strIpparaCfg.struIPChanInfo[iIPChanel].byIPID;
            String IP4 = new String (strIpparaCfg.struIPDevInfo[iDevID - 1].struIP.sIpV4).trim();
            return IP4;

        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getDVRIPJoin()","系统在获得IP设备某通道接入设备的IP地址过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

        return "";
    }
    /**
        * 函数:      getDVRChannelJoin
        * 函数描述:  获得IP设备对应接入设备IP地址的音视频通道号
        * @param StrDevInfo 设备登陆后得到的设备参数结构体
        * @param strIpparaCfg IP设备资源及IP通道资源配置结构体
        * @param DVRIP    IP设备某通道接入设备的IP地址
        * @param FileName   调用的文件名
        * @return int 预览以及参数配置要使用的音视频通道号，失败返回-1。
    */
    public static int getDVRChannelJoin(HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo, HCNetSDK.NET_DVR_IPPARACFG strIpparaCfg, String DVRIP, String FileName){
        if (StrDevInfo == null) return -1;

        //下面判断是否是接入设备，根据HCNetSDK.NET_DVR_IPPARACFG，得出接入设备参数
        if (strIpparaCfg == null) return -1;
        if (DVRIP == null || DVRIP.equals("")) return -1;
        int DevID = -1;
        for (int i=0;i<HCNetSDK.MAX_IP_DEVICE;i++){
            String IP4 = new String (strIpparaCfg.struIPDevInfo[i].struIP.sIpV4).trim();
            if (DVRIP.equals(IP4)) {
                DevID = i + 1;
                break;
            }
        }
        if (DevID == -1) return -1;
        for (int i=0;i<HCNetSDK.MAX_IP_CHANNEL;i++){
            byte DevID2 = strIpparaCfg.struIPChanInfo[i].byIPID;
            if (DevID2 == DevID) return i + HCNetSDK.MAX_ANALOG_CHANNUM + StrDevInfo.byStartChan;
        }
        return -1;
    }
    /**
	 * 函数:      getUserID
         * 函数描述:  获得该设备别名对应的登录UserID
         * @para AnotherName  设备别名
         * @return NativeLong   该设备别名对应的登录UserID。如果登录失败返回为-1
    */
    public static NativeLong getUserID(String AnotherName,String FileName){
        int IndexListDevicePara = getIndexOfDeviceList(AnotherName,FileName);
        return getUserID(IndexListDevicePara,FileName);
    }
    /**
        * 函数:      getUserID
        * 函数描述:  获得该设备列表索引对应的登录UserID
        * @param IndexListDevicePara  listDeviceDetailPara设备参数表中的索引
        * @param FileName
        * @return NativeLong   该设备别名对应的登录UserID。如果登录失败返回为-1
    */
    public static NativeLong getUserID(int IndexListDevicePara,String FileName){

        try{
            if (IndexListDevicePara == -1) return new NativeLong(-1);
            if (FileName == null || FileName.equals("")) return new NativeLong(-1);
            //listDeviceDetailPara获取设备参数表中的 设备序列号0,IP地址1,端口号2,用户名3,密码4,别名5,设备类型代码6，设备类型7（代码表中的代码名称）等参数；
            //现在加上登录ID8;再加上NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30-9;报警布防句柄AlarmHandle-10

            //listDeviceDetailPara获取设备参数表中的 DeviceParaBean0,设备类型1（代码表中的代码名称）等参数；
            //现在加上登录ID-2，再加上NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30-3;报警布防句柄AlarmHandle-4;
            //IP设备资源及IP通道资源配置结构体NET_DVR_IPPARACFG-5
            ArrayList OneDeviceDetailPara =  (ArrayList)g_listDeviceDetailPara.get(IndexListDevicePara);
            NativeLong UserID = (NativeLong)OneDeviceDetailPara.get(2);
            if (UserID.intValue() > -1) return UserID;
            //如果没有注册，需要注册
            DeviceParaBean deviceParaBean = (DeviceParaBean)(OneDeviceDetailPara.get(0));
//            HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();//NET_DVR_Login_V30()参数结构
//            //注册该设备
//            UserID = hCNetSDK.NET_DVR_Login_V30(deviceParaBean.getDVRIP(), Short.parseShort(deviceParaBean.getServerport()), 
//                                                            deviceParaBean.getUsername(), deviceParaBean.getPassword(), StrDeviceInfo);

            HCNetSDK.NET_DVR_DEVICEINFO_V40 StrDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();
            UserID = HCNetSDKExpand.NET_DVR_Login_V40(deviceParaBean.getDVRIP(), Short.parseShort(deviceParaBean.getServerport()), 
                                                            deviceParaBean.getUsername(), deviceParaBean.getPassword(), StrDeviceInfo,FileName);
            
            if (UserID.intValue() > -1){
                OneDeviceDetailPara.set(2, UserID);//设置该UserID
                OneDeviceDetailPara.set(3, StrDeviceInfo);//NET_DVR_Login_V30()参数结构设备参数结构体
            }else{
                CommonParas.showErrorMessage(errorMsgOfLoginFail, deviceParaBean.getAnothername() , FILE_NAME);
                CommonParas.SystemWriteErrorLog("", deviceParaBean.getAnothername(),  errorLogOfLoginFail,  deviceParaBean.getSerialNO(),  CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE,  FILE_NAME);
            }
            return UserID;
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getUserID()","系统在获得该设备列表索引对应的登录UserID过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        } 
        return new NativeLong(-1);
        
    }
    /**
        * 函数:      getIndexOfDeviceList
        * 函数描述:  获得该设备别名对应的设备所对应的设备列表listDeviceDetailPara索引
        * @param AnotherName
        * @param FileName
        * @return int   该设备别名对应的设备所对应的设备列表getIndexOfDeviceList索引。如果没有则为-1
    */
    public static int getIndexOfDeviceList(String AnotherName,String FileName){
        try{
            if (g_listDeviceDetailPara == null || g_listDeviceDetailPara.size() == 0) return -1;
            int Index = -1;
            if (AnotherName == null || AnotherName.equals("")) return Index;
            //listDeviceDetailPara获取设备参数表中的 设备序列号0,IP地址1,端口号2,用户名3,密码4,别名5,设备类型代码6，设备类型7（代码表中的代码名称）等参数；
            //现在加上登录ID8
            for (int i=0;i<g_listDeviceDetailPara.size();i++){

                DeviceParaBean deviceParaBean = (DeviceParaBean)(((ArrayList)g_listDeviceDetailPara.get(i)).get(0));
                if (deviceParaBean.getAnothername().equals(AnotherName)) return i;

            }
            return Index;
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getIndexOfDeviceList()","系统在获得该设备别名对应的设备所对应的设备列表listDeviceparaBean索引过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            return -1;
        }
    }
    
    /**
        * 函数:      getIndexOfDeviceList
        * 函数描述:  获得该设备IP地址和别名对应的设备所对应的设备列表listDeviceDetailPara索引
        * @param DVRIP  ip地址
        * @param AnotherName    设备别名
        * @param SerialNO   设备序列号
        * @param FileName   
        * @return int   该设备别名对应的设备所对应的设备列表getIndexOfDeviceList索引。如果没有则为-1
    */
    public static int getIndexOfDeviceList(String DVRIP,String AnotherName,String SerialNO,String FileName){
        try{
            
            if (SerialNO == null || SerialNO.equals("")) return getIndexOfDeviceList(DVRIP,AnotherName,FileName);
            if (g_listDeviceDetailPara == null || g_listDeviceDetailPara.isEmpty()) return -1;

            //listDeviceDetailPara获取设备参数表中的 设备序列号0,IP地址1,端口号2,用户名3,密码4,别名5,设备类型代码6，设备类型7（代码表中的代码名称）等参数；
            //现在加上登录ID8
            for (int i=0;i<g_listDeviceDetailPara.size();i++){
                
                DeviceParaBean deviceParaBean = (DeviceParaBean)(((ArrayList)g_listDeviceDetailPara.get(i)).get(0));
                if (deviceParaBean.getSerialNO().equals(SerialNO)) return i;
            }

        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getIndexOfDeviceList()","系统在获得该设备IP地址和别名对应的设备所对应的设备列表listDeviceparaBean索引过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());        }
        return -1;
    }
    /**
        * 函数:      getIndexOfDeviceList
        * 函数描述:  获得该设备IP地址和别名对应的设备所对应的设备列表listDeviceDetailPara索引
        * @param DVRIP  ip地址
        * @param AnotherName    设备别名
        * @param FileName   
        * @return int   该设备别名对应的设备所对应的设备列表getIndexOfDeviceList索引。如果没有则为-1
    */
    public static int getIndexOfDeviceList(String DVRIP,String AnotherName,String FileName){
        try{
            if (DVRIP == null || DVRIP.equals("")) return getIndexOfDeviceList(AnotherName,FileName);
            if (g_listDeviceDetailPara == null || g_listDeviceDetailPara.size() == 0) return -1;
            int Index = -1;

            //listDeviceDetailPara获取设备参数表中的 设备序列号0,IP地址1,端口号2,用户名3,密码4,别名5,设备类型代码6，设备类型7（代码表中的代码名称）等参数；
            //现在加上登录ID8
            for (int i=0;i<g_listDeviceDetailPara.size();i++){
                
                DeviceParaBean deviceParaBean = (DeviceParaBean)(((ArrayList)g_listDeviceDetailPara.get(i)).get(0));
                if (deviceParaBean.getDVRIP().equals(DVRIP)) return i;
            }
            return Index;
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "getIndexOfDeviceList()","系统在获得该设备IP地址和别名对应的设备所对应的设备列表listDeviceparaBean索引过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            return -1;
        }
    }
    /**
        *函数:      initialDeviceDetailParaList
        *函数描述:  初始化g_listDeviceDetailPara
        * @param FileName
    */
    public static void initialDeviceDetailParaList(String FileName){
        try{
            if (g_listDeviceDetailPara != null ) g_listDeviceDetailPara.clear();//已管理设备的序列号数组
            g_listDeviceDetailPara = DeviceParaBean.getDeviceDetailParaList(FileName + FILE_NAME);
            if (g_listDeviceDetailPara == null || g_listDeviceDetailPara.isEmpty()) return;

            for (int i=0;i<g_listDeviceDetailPara.size();i++){
                ArrayList OneDeviceDetailPara =  (ArrayList)g_listDeviceDetailPara.get(i);
                OneDeviceDetailPara.add(new NativeLong(-1));//现在加上登录ID-2
                OneDeviceDetailPara.add(null);//设备参数结构体NET_DVR_DEVICEINFO_V40-3
                OneDeviceDetailPara.add(new NativeLong(-1));//报警布防句柄AlarmHandle-4
                OneDeviceDetailPara.add(null);//IP设备资源及IP通道资源配置结构体NET_DVR_IPPARACFG-5
            }
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "initialDeviceDetailParaList()","系统在初始化g_listDeviceDetailPara过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        *函数:      closeDeviceDetailParaList
        *函数描述:  结束g_listDeviceDetailPara，设备注销
        * @param FileName
    */
    public static void closeDeviceDetailParaList(String FileName){
        try{

            if (g_listDeviceDetailPara == null || g_listDeviceDetailPara.size() == 0) return;

            for (int i=0;i<g_listDeviceDetailPara.size();i++){
                ArrayList NewList = (ArrayList)g_listDeviceDetailPara.get(i);
                NativeLong UserID = (NativeLong)NewList.get(2);
                boolean IfLogout = false;
                if (UserID.intValue() > -1) 
                    IfLogout = hCNetSDK.NET_DVR_Logout_V30(UserID);
                if (IfLogout) NewList.set(2, new NativeLong(-1));//登录ID-2
                NewList.set(3, false);//设备参数结构体NET_DVR_DEVICEINFO_V30-3
                //NewList.set(4, new NativeLong(-1));//报警布防句柄AlarmHandle-4
                NewList.set(5, false);//IP设备资源及IP通道资源配置结构体NET_DVR_IPPARACFG-5
            }
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "closeDeviceDetailParaList()","系统在结束g_listDeviceDetailPara，设备注销过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:     insertToDeviceDetailParaList
        * 函数描述:  在g_listDeviceDetailPara中添加新管理的设备
        * @param SerialNO
        * @param UserID
        * @param StrIpparacfg
        * @param StrDeviceInfo
        * @param FileName
    */
    public static void insertToDeviceDetailParaList(String  SerialNO, NativeLong UserID, HCNetSDK.NET_DVR_DEVICEINFO_V40 StrDeviceInfo,
            HCNetSDK.NET_DVR_IPPARACFG  StrIpparacfg, String FileName){
        try{
            ArrayList NewList = DeviceParaBean.getOneDeviceDetailPara(SerialNO, FileName);
            NewList.add(UserID);//登录ID-2
            NewList.add(StrDeviceInfo);//设备参数结构体NET_DVR_DEVICEINFO_V30-3
            NewList.add(new NativeLong(-1));//报警布防句柄AlarmHandle-4
            NewList.add(StrIpparacfg);//IP设备资源及IP通道资源配置结构体NET_DVR_IPPARACFG-5
            g_listDeviceDetailPara.add(NewList);
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "insertToDeviceDetailParaList()","系统在在g_listDeviceDetailPara中添加新管理的设备过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }

    /**
        *函数:      getIfIPConflict
        *函数描述:  判断该IP地址是否存在冲突
        *@param sDVRIP   需要判断的IP地址
        *@return    boolean  true，存在冲突；false不冲突。
    */
    public static boolean getIfIPConflict(String sDVRIP, String FileName){
       
        if (g_listDeviceDetailPara != null) {
            for (int i=0;i<g_listDeviceDetailPara.size();i++) {
                 DeviceParaBean deviceparaBean = getDeviceParaBean(i, FileName);//listDeviceparaBean.get(i);
                if (deviceparaBean.getDVRIP().equals(sDVRIP)) {
//                    JOptionPane.showMessageDialog(rootPane, "此设备存在地址冲突！");
                    return true;
                }
            }
            return false;
        }else 
        {return false;}
    }
    /**
        *函数:      getIfManaged
        *函数描述:  判断该该设备是否已经被管理
        *@param SerialNO   该设备的序列号
        * @param FileName
        *@return    boolean  是，true；否，false。
    */
    public static boolean getIfManaged(String SerialNO, String FileName){
        
        if (g_listDeviceDetailPara != null) {
            for (int i=0;i<g_listDeviceDetailPara.size();i++) {
                 DeviceParaBean deviceparaBean = getDeviceParaBean(i, FILE_NAME);//listDeviceparaBean.get(i);
                if (deviceparaBean.getSerialNO().equals(SerialNO)) {
//                    JOptionPane.showMessageDialog(rootPane, "此设备已经添加管理！");
                    return true;
                }
            }
            return false;
        }else 
        {return false;}
        
    }
    /**
        *函数:      getSerialNOFromSADP
        *函数描述:  根据设备IP地址，从SADP模块中得到该设备的序列号
        * @param DVRIP  设备IP地址
        *@return    String  该设备的序列号；没有，返回""。
    */
    public static String getSerialNOFromSADP(String DVRIP){
        if (DVRIP == null) return "";
        for (int i=0;i<g_list_SADP_DEV_NET_PARAM_JAVA.size();i++){
            Sadp.SADP_DEV_NET_PARAM_JAVA SADPParas = g_list_SADP_DEV_NET_PARAM_JAVA.get(i);
            if (SADPParas.getIPv4Address().equals(DVRIP)) return SADPParas.getSerialNO();
        }
        return "";
    }
    /**
	 * 函数:      initialNormalNoEditTableModel
         * 函数描述:  初始化普通的不可编辑的DefaultTableModel
         * @param tabeTile
         * @return DefaultTableModel
    */
    public static DefaultTableModel initialNormalNoEditTableModel(String [] tabeTile)
    {
//        String tabeTile[];
//        tabeTile = new String [] {"IP", "设备类型", "主控版本", "安全状态", "服务端口", "开始时间", "是否已管理", "设备序列号"};
        class MyTableModel extends DefaultTableModel{ 
            
            public MyTableModel(Object[] columnNames, int rowCount){
                super(columnNames,rowCount);
            }
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            } 
        }
        MyTableModel myTableModel = new MyTableModel(tabeTile, 0);
        return myTableModel;
    }
    
    /**
	 * 函数:      initialNormalOneEditTableModel
         * 函数描述:  初始化普通的最后一列可编辑的DefaultTableModel
         * @return DefaultTableModel
    */
    public static DefaultTableModel initialNormalOneEditTableModel(final String [] tabeTile)
    {
//        String tabeTile[];
//        tabeTile = new String [] {"IP", "设备类型", "主控版本", "安全状态", "服务端口", "开始时间", "是否已管理", "设备序列号"};
        class MyTableModel extends DefaultTableModel{ 
            
            public MyTableModel(Object[] columnNames, int rowCount){
                super(columnNames,rowCount);
            }
            @Override
            public boolean isCellEditable(int row, int column) { 
                if (row >= 0 &&  column == tabeTile.length-1) return true;
                return false; 
            } 
        }
        MyTableModel myTableModel = new MyTableModel(tabeTile, 0);
        return myTableModel;
    }
    
    public static Object makeObj(final String item)  {
        return new Object() {
            @Override
            public String toString() { return item; } 
        };
    }
    /**
        * 函数:      centerWindow
        * 函数描述:  窗口在屏幕中央显示
        * @param window
    */
    public static void centerWindow(Container window) {
        try{
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int w = window.getSize().width;
            int h = window.getSize().height;
            int x = (dim.width - w) / 2;
            int y = (dim.height - h) / 2;
            window.setLocation(x, y);
        }catch (Exception e){
            TxtLogger.append(FILE_NAME, "centerWindow()","系统将窗口在屏幕中央显示过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      setJButtonUnDecorated
        * 函数描述:  设置JButton无边框
        * @param jButton
    */
    public static void setJButtonUnDecorated(AbstractButton jButton){
        jButton.setBorder(null);
        jButton.setBorderPainted(false);
        jButton.setContentAreaFilled(false);

    }
    /**
	 * 函数:      setJPanelAllOCXEnable
         * 函数描述:  设置JPanel中所有的控件是否可用
         * @param jPanel  JPanel
         * @param enabled   是否可用
    */
    public static void setJPanelAllOCXEnable(JPanel jPanel,boolean enabled){
        jPanel.setEnabled(enabled);
        Component[] com =  jPanel.getComponents();
        for (Component com1 : com) {
            com1.setEnabled(enabled);
        }
    }
    

    
//    public static boolean softRegister(String FileName){
//        try{
//            
//        LicenseGeneratorWorker licenseGeneratorWorker = new LicenseGeneratorWorker();
//        licenseGeneratorWorker.execute();
//            if (licenseGeneratorWorker.isSuccess()){
//                JDialogLicense DialogLicense = new JDialogLicense(null,true, licenseGeneratorWorker.getGeneratedCode());
//                CommonParas.centerWindow(DialogLicense);
//                DialogLicense.setVisible(true);
//                return DialogLicense.isRegisterSucc();
//            }
//            return true;
//        }catch (Exception e){
//            TxtLogger.append(FileName + FILE_NAME, "setMaxSize()","系统在设置窗口的最大尺寸过程中，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
//
//        return false;
//    }
    
    public static boolean softRegister(String FileName){
        try{

            String MotherboardSN = HardwareSerial.getMotherboardSN();
            String CPUSerial = HardwareSerial.getCPUSerial();
            String MachineCode = HardwareSerial.generateMachineCode(MotherboardSN + ";" + CPUSerial);
            String License = LicenseGenerator.generateLicense(MachineCode);
            String License2 = SysParaCodesBean.getParaValue(SysParas.SYSPARAS_COMMON_REGCODE_CODE, FileName + FILE_NAME);

            if (!License.equals(License2)){
                JDialogLicense DialogLicense = new JDialogLicense(null,true, MotherboardSN + ";" + CPUSerial + ";" + MachineCode);
                CommonParas.centerWindow(DialogLicense);
                DialogLicense.setVisible(true);
                return DialogLicense.isRegisterSucc();
            }
            return true;
        }catch (Exception e){
            TxtLogger.append(FileName + FILE_NAME, "softRegister()","系统在进行软件注册检测过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }

        return false;
    }

    public static void showProgressWindow(SwingWorker NewSwingWorker){
        DialogInfiniteProgress = new JDialogInfiniteProgress(null, false, NewSwingWorker);
        centerWindow(DialogInfiniteProgress);
        DialogInfiniteProgress.setVisible(true);
    }
    public static void closeProgressWindow(){
        if (DialogInfiniteProgress.isVisible()) 
            DialogInfiniteProgress.stopPogress();
    }
    public static void setPorgressInfo(String PorgressInfo){
        if (DialogInfiniteProgress.isVisible()) DialogInfiniteProgress.setPorgressInfo(PorgressInfo);
    }

    /**
        * 函数:      setAppropriateLocation3
        * 函数描述:  设置弹出窗口的位置
        * @param Window2   弹出窗口，一般为JDialog 
        * @param jButton   打开Window2的按钮
    */
    public static void setAppropriateLocation3(Window Window2, JButton jButton) {  
        //初始位置，即鼠标按键的位置，也就是按钮的位置
        Point ButtonPosition = jButton.getLocationOnScreen();  
//        Dimension   screenSize   =   Toolkit.getDefaultToolkit().getScreenSize();
//        if (MousePosition.y < screenSize.height/2) 
//            MousePosition.y = MousePosition.y + jButton.getHeight(); 
        ButtonPosition.y = ButtonPosition.y + jButton.getHeight();  
        //父窗口
        Window owner = SwingUtilities.getWindowAncestor(jButton);  
        //父窗口位置
        Point OwnerLocation = owner.getLocationOnScreen();  
        
        //目标窗口Window2初始位置
        Point result = new Point(ButtonPosition);  

        int offsetX = (ButtonPosition.x + Window2.getWidth()) - (OwnerLocation.x + owner.getWidth());  
        int offsetY = (ButtonPosition.y + Window2.getHeight()) - (OwnerLocation.y + owner.getHeight());  

        if (offsetX > 0) {  
            result.x -= offsetX;  
        }  

        if (offsetY > 0) {  
            result.y -= (offsetY+jButton.getHeight()*2);  
        }  
        Window2.setLocation(result);

    }  
    /**
        * 函数:      setAppropriateLocation
        * 函数描述:  设置弹出窗口的位置
        * @param Window2   弹出窗口，一般为JDialog 
        * @param jButton   打开Window2的按钮
    */
    public static void setAppropriateLocation(Window Window2, JButton jButton) {  
        //初始位置，即鼠标按键的位置，也就是按钮的位置
        Point ButtonPosition = jButton.getLocationOnScreen();  

        
        int WidthWindow = Window2.getWidth();
        int HeightWindow = Window2.getHeight();
        //目标窗口Window2初始位置
        Point result = new Point(ButtonPosition);  
        
        Dimension   screenSize   =   Toolkit.getDefaultToolkit().getScreenSize();
        if (ButtonPosition.y < screenSize.height/2) 
            result.y = ButtonPosition.y + jButton.getHeight(); 
        else 
            result.y = ButtonPosition.y - HeightWindow; 
        
        if ((result.x + WidthWindow) > screenSize.width)    result.x = screenSize.width - WidthWindow;
        if ((result.y + HeightWindow) > screenSize.height)  result.y = screenSize.height - HeightWindow;
        if (result.y < 0) result.y = 0;

        Window2.setLocation(result);

    }  
    
    /**
        * 函数:      setAppropriateLocation
        * 函数描述:  设置弹出窗口的位置
        * @param Window2   弹出窗口，一般为JDialog 
        * @param MousePosition Point位置，包括鼠标按键、菜单项的位置
    */
    public static void setAppropriateLocation(Window Window2, Point MousePosition) {  

        int WidthWindow = Window2.getWidth();
        int HeightWindow = Window2.getHeight();
        //目标窗口Window2初始位置
        Point result = new Point(MousePosition);  
        
        Dimension   screenSize   =   Toolkit.getDefaultToolkit().getScreenSize();
        if (MousePosition.y > screenSize.height/2) 
            result.y = MousePosition.y - HeightWindow; 
        
        if ((result.x + WidthWindow) > screenSize.width) result.x = screenSize.width - WidthWindow;
        if ((result.y + HeightWindow) > screenSize.height) result.y = screenSize.height - HeightWindow;
        if (result.y < 0) result.y = 0;

        Window2.setLocation(result);
        
    }  
    /**
        * 函数:      setAppropriateLocation
        * 函数描述:  设置弹出窗口的位置
        * @param Window2   弹出窗口，一般为JDialog 
    */
    public static void setAppropriateLocation(Window Window2) {  
        
        //初始位置，即鼠标按键的位置，也就是按钮的位置
        Point MousePosition = MouseInfo.getPointerInfo().getLocation();

        int WidthWindow = Window2.getWidth();
        int HeightWindow = Window2.getHeight();
        //目标窗口Window2初始位置
        Point result = new Point(MousePosition);  
        
        Dimension   screenSize   =   Toolkit.getDefaultToolkit().getScreenSize();
        if (MousePosition.y > screenSize.height/2) 
            result.y = MousePosition.y - HeightWindow; 
        
        if ((result.x + WidthWindow) > screenSize.width) result.x = screenSize.width - WidthWindow;
        if ((result.y + HeightWindow) > screenSize.height) result.y = screenSize.height - HeightWindow;
        if (result.y < 0) result.y = 0;

        Window2.setLocation(result);
        
    }  
    
    /**
        * 函数:      setMaxSize
        * 函数描述:  设置窗口的最大尺寸。最大不能超过屏幕尺寸
        * @param Window2   设置窗口
    */
    public static void setMaxSize(Window Window2){
        try{
            Dimension   screenSize   =   Toolkit.getDefaultToolkit().getScreenSize();
            int WHeight = Window2.getHeight();
            int WWidth  = Window2.getWidth();
            boolean IfBeyondScreen = false;
            if (Window2.getHeight() > screenSize.height) {
                WHeight = screenSize.height;
                IfBeyondScreen = true;
            }

            if (Window2.getWidth()> screenSize.width) {
                WWidth  = screenSize.width;
                IfBeyondScreen = true;
            }

            if (IfBeyondScreen) Window2.setSize(WWidth, WHeight);
        }catch (Exception e){
            TxtLogger.append(FILE_NAME, "setMaxSize()","系统在设置窗口的最大尺寸过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    /**
        * 函数:      setupOneDevAlarmChan
        * 函数描述:  单一设备设置报警布防
        * @param AnotherName 设备别名
        * @param IfSetupAlarm 是否设置报警布防。true设置；false取消
        * @return int 设备设置布防/撤防成功，返回>1；失败返回0；出现异常，返回-1
    */
    public static int setupOneDevAlarmChan(String AnotherName,boolean IfSetupAlarm){
        try{
            if (IfSetupAlarm) {
                int Index = CommonParas.getIndexOfDeviceList(AnotherName, FILE_NAME);
                NativeLong UserID = getUserID(Index, FILE_NAME);
                NativeLong AlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V30(UserID);
                if (AlarmHandle.intValue() == -1){ 
                    CommonParas.showErrorMessage(setupAlarmChanFail , AnotherName, FILE_NAME);
                }else {
                    ((ArrayList)g_listDeviceDetailPara.get(Index)).set(4, AlarmHandle);
                    ((DeviceParaBean)((ArrayList)g_listDeviceDetailPara.get(Index)).get(0)).setIfsetupAlarm("1");
                    CommonParas.showMessage( AnotherName + " "+ setupAlarmChanSucc , FILE_NAME);
                }
                return AlarmHandle.intValue() == -1?0:1;
            }else{
                int Index = CommonParas.getIndexOfDeviceList(AnotherName, FILE_NAME);
                NativeLong AlarmHandle = (NativeLong)((ArrayList)g_listDeviceDetailPara.get(Index)).get(4);
                boolean IfCloseAlarm = hCNetSDK.NET_DVR_CloseAlarmChan_V30(AlarmHandle);
                if (IfCloseAlarm){
                    ((ArrayList)g_listDeviceDetailPara.get(Index)).set(4, new NativeLong(-1));
                    ((DeviceParaBean)((ArrayList)g_listDeviceDetailPara.get(Index)).get(0)).setIfsetupAlarm("0");//.set(4, new NativeLong(-1));
                    CommonParas.showMessage( AnotherName + " "+ closeAlarmChanSucc , FILE_NAME);
                }else {
                    CommonParas.showErrorMessage(closeAlarmChanFail , AnotherName, FILE_NAME);
                    
                }
                return IfCloseAlarm?1:0;
            }
        }catch (Exception e){
            TxtLogger.append(FILE_NAME, "setupOneDevAlarmChan()","系统在进行设备设置布防/撤防过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return -1;
    }
    /**
        * 函数:      setupAlarmChan
        * 函数描述:  设备报警布防，同时不修改数据库中的数据
    */
    public static void setupAlarmChan(){
        
        try{
            g_RootPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            if (alarmFMSGCallBack == null)
            {
                alarmFMSGCallBack = new AlarmFMSGCallBack();
                Pointer pUser = null;
                if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(alarmFMSGCallBack, pUser))
                {
                    System.out.println("设置回调函数失败!，错误代码:"+ hCNetSDK.NET_DVR_GetLastError());
                    return;
                }
            }
            //listDeviceDetailPara获取设备参数表中的 DeviceParaBean-0,设备类型-1（代码表中的代码名称）等参数；
            //现在加上登录ID-2，再加上NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30-3;报警布防句柄AlarmHandle-4;
            //IP设备资源及IP通道资源配置结构体NET_DVR_IPPARACFG-5

            for (int i=0;i<g_listDeviceDetailPara.size();i++){
                    ArrayList NewList = (ArrayList)g_listDeviceDetailPara.get(i);
                    DeviceParaBean deviceParaBean = (DeviceParaBean)NewList.get(0);
                    if (deviceParaBean.getIfsetupAlarm().equals("1")){
                            NativeLong UserID = getUserID(i,FILE_NAME);
                            NativeLong AlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V30(UserID);

                            if (AlarmHandle.intValue() == -1){
                                
                                CommonParas.showErrorMessage(setupAlarmChanFail , deviceParaBean.getAnothername() , FILE_NAME);//布防失败
                                CommonParas.SystemWriteErrorLog("", deviceParaBean.getAnothername(),  setupAlarmChanError ,  deviceParaBean.getSerialNO(),  CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE,  FILE_NAME);//设备报警布防失败
                            }
                            else{
                                NewList.set(4, AlarmHandle);
                            }
                    }

            }
        }catch (IllegalArgumentException e){
            TxtLogger.append(FILE_NAME, "setupAlarmChan()","系统在进行设备布防过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }catch (Exception e){
            TxtLogger.append(FILE_NAME, "setupAlarmChan()","系统在进行设备布防过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        g_RootPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    /**
        * 函数:      closeAlarmChan
        * 函数描述:  设备报警撤防，同时不修改数据库中的数据
    */
    public static void closeAlarmChan(){
        
        try{
            
            //listDeviceDetailPara获取设备参数表中的 DeviceParaBean-0,设备类型-1（代码表中的代码名称）等参数；
            //现在加上登录ID-2，再加上NET_DVR_Login_V30()设备参数结构体NET_DVR_DEVICEINFO_V30-3;报警布防句柄AlarmHandle-4;
            //IP设备资源及IP通道资源配置结构体NET_DVR_IPPARACFG-5

            for (int i=0;i<g_listDeviceDetailPara.size();i++){
                    ArrayList NewList = (ArrayList)g_listDeviceDetailPara.get(i);
                    DeviceParaBean deviceParaBean = (DeviceParaBean)NewList.get(0);
                    if (deviceParaBean.getIfsetupAlarm().equals("1")){
                        NativeLong AlarmHandle = (NativeLong)((ArrayList)g_listDeviceDetailPara.get(i)).get(4);
                        if (hCNetSDK.NET_DVR_CloseAlarmChan_V30(AlarmHandle))   NewList.set(4, new NativeLong(-1));
                        else{
                            CommonParas.showErrorMessage(closeAlarmChanFail , deviceParaBean.getAnothername() , FILE_NAME);
                            CommonParas.SystemWriteErrorLog("", deviceParaBean.getAnothername(),  closeAlarmChanError ,  deviceParaBean.getSerialNO(),  CommonParas.DVRType.DVRTYPE_ENCODINGDVR_CODE,  FILE_NAME);
                        }
                    }
            }
        }catch (IllegalArgumentException e){
            TxtLogger.append(FILE_NAME, "closeAlarmChan()","系统在进行设备撤防过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }catch (Exception e){
            TxtLogger.append(FILE_NAME, "closeAlarmChan()","系统在进行设备撤防过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        //this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        //this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } 
    /**
        * 函数:      writeErrorInfoToTable
        * 函数描述:  系统错误信息填写至报警信息窗口的错误TAB表中
        * @param ErrorTime 错误信息产生时间
        * @param ErrorInfo 错误信息
    */
    private static void writeErrorInfoToTable(String ErrorTime, String ErrorInfo){
        if (ErrorTime == null || ErrorInfo == null || ErrorTime.equals("") || ErrorInfo.equals("")) return;
        String[] newRow = new String[2];//报警窗口添加的数据行
        newRow[0] = ErrorTime;
        newRow[1] = ErrorInfo;
        errorMSGTableModel.addRow(newRow);
        errorMSGTableModel.fireTableDataChanged();
    }
    
    /**
        *类:      LicenseGeneratorWorker
        *类描述:  产生注册码的并行线程类
    */
    public static class LicenseGeneratorWorker extends SwingWorker {

        private JDialogLicense DialogLicense;
        private String MotherboardSN = "";
        private String CPUSerial = "";
        private String MachineCode = "";
        private String License = "";
        private boolean bSuccess = false;
        private String sGeneratedCode = "";
        
        @Override
        protected String doInBackground() {
            try{
                MotherboardSN = HardwareSerial.getMotherboardSN();
                CPUSerial = HardwareSerial.getCPUSerial();
                MachineCode = HardwareSerial.generateMachineCode(MotherboardSN + ";" + CPUSerial);
                License = LicenseGenerator.generateLicense(MachineCode);
                sGeneratedCode = MotherboardSN + ";" + CPUSerial + ";" + MachineCode;
                return License;
            } catch (Exception e) {
                TxtLogger.append(FILE_NAME + "-->LicenseGeneratorWorker", "doInBackground()","系统在产生注册码的过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());   
            }
            return "";
        }
        @Override
        protected void done(){

            String License2 = SysParaCodesBean.getParaValue(SysParas.SYSPARAS_COMMON_REGCODE_CODE, FILE_NAME);

            if (License.equals(License2)){
                bSuccess = true;
                MotherboardSN = "";
                CPUSerial = "";
                MachineCode = "";
                License = "";
            }

        }

        /**
         * @return the bSuccess
         */
        public boolean isSuccess() {
            return bSuccess;
        }

        /**
         * @return the sGeneratedCode
         */
        public String getGeneratedCode() {
            return sGeneratedCode;
        }
    }
    
    /**
        * 函数:      clearMessageAlarmList
        * 函数描述:  清除错误和报警显示窗口的信息
        * @param IndexOfTab 信息种类，0代表报警信息，1代表错误信息
        * @return 成功返回true
    */
    public static boolean clearMessageAlarmList(int IndexOfTab){
        try{
            if(IndexOfTab == 0){
                alarmMSGTableModel.getDataVector().clear();
                alarmMSGTableModel.fireTableDataChanged();
                return true;
            }else if (IndexOfTab == 1){
                errorMSGTableModel.getDataVector().clear();
                errorMSGTableModel.fireTableDataChanged();
                return true;
            }
        } catch (Exception e) {
            TxtLogger.append(FILE_NAME, "clearMessageAlarmList()","系统在设置报警信息回调函数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
        return false;
    }
    /******************************************************************************
     *内部类:   AlarmFMSGCallBack
     *报警信息回调函数类
     ******************************************************************************/
    public static class AlarmFMSGCallBack implements HCNetSDK.FMSGCallBack{
        
        private WaveFilePlay playAlarmWaveFile; //播放报警声音的WaveFilePlay对象
        private String AlarmWaveFilePath;       //报警声音文件路径
        //private int indexOfAlarmMSG = 0;//报警序号
        private String sAlarmType = "";//报警类型
        private int iChannelDiskAlarmIn = 0;//报警通道号/报警输入号/硬盘号
        private int iChannelJoin = 0;//报警通道
        private int iDVROrResource = 0;//0代表资源报警；1代表设备本身报警
        private String sDVRType = DVRType.DVRTYPE_ENCODINGDVR_CODE;//设备类型。默认为编码设备/门口机
        private String sResourceType = "";//设备资源类型
        private String[] newRow = new String[9];//报警窗口添加的数据行
        private Date today = new Date();//报警时间
        private DateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//报警时间的表示形式，写入数据库的时间形式
//        /*报警时间的表示形式,因为澳大利亚时间形式22/02/2017 4:13:17 PM不能存入数据库
//        java.sql.SQLDataException: 日期时间值的字符串表示的语法不正确。
//        所以特别设置该变量写入数据库
//        */
        private String[] sIP = new String[2];//存储IP地址（包括IP通道发生报警的设备地址）
        private String[] sIPJoin = new String[]{"",""};//接入设备的存储IP地址（包括IP通道对应的模拟通道设备的设备地址）
        private String[] sAlarmTypes = {"信号量报警","硬盘满","信号丢失","移动侦测","硬盘未格式化","读写硬盘出错","遮挡报警",
            "制式不匹配","非法访问","视频信号异常","录像/抓图异常","越界侦测报警","区域入侵侦测报警" };
        private ArrayList listShowAlarmTypes = new ArrayList();
        
        //报警信息回调函数构造函数
        public AlarmFMSGCallBack(){
            modifyLocales();
            for (int i=0;i<sAlarmTypes.length;i++){
                ArrayList NewList = new ArrayList();
                NewList.add(true);
                NewList.add(sAlarmTypes[i]);
                listShowAlarmTypes.add(NewList);
            }
        }
        /**
         *
         * @param lCommand
         * @param pAlarmer
         * @param pAlarmInfo
         * @param dwBufLen
         * @param pUser
         */
        @Override
        public void invoke(NativeLong lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, HCNetSDK.RECV_ALARM pAlarmInfo, int dwBufLen, Pointer pUser){
                try{
                    today = new Date(); 
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    //lCommand是传的报警类型
                    switch (lCommand.intValue()){
                        
                        case HCNetSDK.COMM_ALARM:   //移动侦测、视频丢失、遮挡、IO信号量等报警信息(V3.0以上版本支持的设备)
                            receiveCommAlarm(pAlarmInfo,pAlarmer); //接收HCNetSDK.COMM_ALARM_V30类报警
                            break;
                        case HCNetSDK.COMM_ALARM_V30:   //移动侦测、视频丢失、遮挡、IO信号量等报警信息(V3.0以上版本支持的设备)
                            receiveCommAlarmV30(pAlarmInfo,pAlarmer); //接收HCNetSDK.COMM_ALARM_V30类报警
                            break;
                        
                        case HCNetSDK.COMM_ALARM_RULE:  //行为分析信息
                            receiveCommAlarmRule(pAlarmInfo); //接收HCNetSDK.COMM_ALARM_RULE类报警
                            break;
                        default:
                            System.out.println("Unknown alarm type! Command code："+lCommand.intValue());
                            TxtLogger.append(FILE_NAME, "AlarmFMSGCallBack.invoke()","Unknown alarm type!消息类型代码："+lCommand.intValue());
                            break;
                    }
                    
                    processAlarmMSG(pAlarmer); //处理报警信息

                }catch (Exception e){
                    TxtLogger.append(FILE_NAME, "AlarmFMSGCallBack.invoke()","系统在设置报警信息回调函数过程中，出现错误"
                                 + "\r\n                       Exception:" + e.toString());
                }
        }
        
        /**
            * 函数:      processAlarmMSG
            * 函数描述:  处理报警信息
            * @param pAlarmer  报警设备信息
        */
        private void processAlarmMSG(HCNetSDK.NET_DVR_ALARMER pAlarmer){
                try{
                        //{"ID", "报警时间", "报警源", "报警细节", "报警内容", "预览", "发送邮件", "注释"};
                        //设备名称（别名）、设备通道、通道节点名、报警类型、录像文件名（可以回放）
                        //"ID", "报警时间",  "报警类型", "IP地址","socket IP地址", "通道号"
                        ////"ID0", "报警时间1",  "报警类型2", "报警设备3", "通道/报警输入/硬盘号4","接入设备5","通道号6","报警时间2-7","备注8"
                        String NodeName = "";
                        String[] SerialNO =  new String[2];
                        String SerialNOJoin = "";
                        SerialNO = new String(pAlarmer.sSerialNumber).split("\0", 2);
                        
                        int indexOfAlarmMSG = alarmMSGTableModel.getRowCount() + 1;//报警序号
                        //1、报警数据窗口中添加数据
                        //newRow[0] = Integer.toString(++indexOfAlarmMSG);//ID
                        newRow[0] = Integer.toString(indexOfAlarmMSG);//ID
                        newRow[1] = dateFormat.format(today);//报警时间
                        newRow[2] = sAlarmType;//报警类型
                        newRow[3] = CommonParas.getAnotherName(sIP[0], FILE_NAME);//报警设备
                        sDVRType = getDeviceParaBean(sIP[0], null, null, FILE_NAME).getDVRType();
                        
                        if (iDVROrResource == 0){//0代表资源报警；1代表设备本身报警
                            switch (sResourceType){
                                case DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE:
                                    if (iChannelDiskAlarmIn > HCNetSDK.MAX_ANALOG_CHANNUM){//如果设备>32，说明是IP通道，输出接入设备的IP地址和通道号
                                        newRow[4] = Integer.toString(iChannelDiskAlarmIn - HCNetSDK.MAX_ANALOG_CHANNUM);//报警设备通道号
                                        //接入设备、设备通道号
                                        newRow[5] = CommonParas.getAnotherName(sIPJoin[0], FILE_NAME);//接入设备sIPJoin[0];
                                        newRow[6] = Integer.toString(iChannelJoin);
                                        SerialNOJoin = getDeviceParaBean(sIPJoin[0],"","",FILE_NAME).getSerialNO();
                                        NodeName = getNodeName(iChannelDiskAlarmIn - HCNetSDK.MAX_ANALOG_CHANNUM, true, DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);//"IP监控点_"+ getNumberToTwoBitsStr(iChannelDiskAlarmIn - HCNetSDK.MAX_ANALOG_CHANNUM);
                                    }else{
                                        newRow[4] = Integer.toString(iChannelDiskAlarmIn);//报警设备通道号
                                        //接入设备、设备通道号
                                        newRow[5] = "";
                                        newRow[6] = "";
                                        NodeName = getNodeName(iChannelDiskAlarmIn, false, DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE);//"监控点_"+ getNumberToTwoBitsStr(iChannelDiskAlarmIn);
                                    }
                                    break;
                                case DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_CODE:
                                    newRow[4] = Integer.toString(iChannelDiskAlarmIn);//报警输入号
                                    NodeName = getNodeName(iChannelDiskAlarmIn, false, DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_CODE);
                                    break;
                                case DVRResourceType.RESTYPE_ENCODINGDVR_DISK_CODE:
                                    newRow[4] = Integer.toString(iChannelDiskAlarmIn);//报警硬盘号
                                    NodeName = getNodeName(iChannelDiskAlarmIn, false, DVRResourceType.RESTYPE_ENCODINGDVR_DISK_CODE);
                                    break;
                            }
                        }
                        
                        
                        
                        newRow[7] = getOperationTime(today);//报警时间2
                        newRow[8] = "";//注释
                        alarmMSGTableModel.insertRow(0, newRow);
                        //alarmMSGTableModel.addRow(newRow);
                        alarmMSGTableModel.fireTableDataChanged();
                        //2、弹出报警窗口
                        if (AlarmRecCtrl.ifPopUpWindow) {
                            getJDialogPopUpAlarmMSG(indexOfAlarmMSG - 1);
                        }

                        //3、发出报警声音
                        makeAlarmVoice(AlarmWaveFilePath);

                        //4、写报警日志
                        //11项：String OperationTime,String LogType,String Description,String SerialNO,String GroupName,String NodeName,String SerialNOJoin,String ChannelJoin,String DVRType,String ObjectType
                        SystemWriteLog(getOperationTime(today),CommonParas.LogType.LOG_ALARM_CODE,sAlarmType,SerialNO[0],"",NodeName,SerialNOJoin,newRow[6],sDVRType, sResourceType, FILE_NAME);
                }catch (Exception e){
                    TxtLogger.append(FILE_NAME, "processAlarmMSG()","系统在处理报警信息过程中，出现错误"
                                 + "\r\n                       Exception:" + e.toString());
                }
        }
        /**
            * 函数:      receiveCommAlarmRule
            * 函数描述:  接收HCNetSDK.COMM_ALARM_RULE类报警
            * @param pAlarmInfo  用于接收报警信息的缓存区
        */
        private void receiveCommAlarmRule(HCNetSDK.RECV_ALARM pAlarmInfo){
            try{
                    HCNetSDK.NET_VCA_RULE_ALARM strRuleAlarm = new HCNetSDK.NET_VCA_RULE_ALARM();
                    strRuleAlarm.write();
                    Pointer pInfo = strRuleAlarm.getPointer();
                    pInfo.write(0, pAlarmInfo.RecvBuffer, 0, strRuleAlarm.size());
                    strRuleAlarm.read();

                    switch (strRuleAlarm.struRuleInfo.wEventTypeEx)
                    {
                            case HCNetSDK.VCA_RULE_EVENT_TYPE_EX.ENUM_VCA_EVENT_TRAVERSE_PLANE:
                                sAlarmType = sAlarmTypes[11];//"越界侦测报警";
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_TREVERSEPLANE;
                                break;
                            case HCNetSDK.VCA_RULE_EVENT_TYPE_EX.ENUM_VCA_EVENT_INTRUSION:
                                sAlarmType = sAlarmTypes[12];//"区域入侵侦测报警";
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_FIELDDETECION;
                                break;
                            default:
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_OTHER ;
                                break;
                    }
                    iDVROrResource = 0;//是设备通道资源报警
                    sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE;//设备通道资源类型
                    iChannelDiskAlarmIn = strRuleAlarm.struDevInfo.byIvmsChannel;
                    sIPJoin = new String(strRuleAlarm.struDevInfo.struDevIP.sIpV4).split("\0", 2);
                    iChannelJoin = strRuleAlarm.struDevInfo.byChannel;
                    

            }catch (Exception e){
                TxtLogger.append(FILE_NAME, "receiveCommAlarmRule()","系统在接收HCNetSDK.COMM_ALARM_RULE类报警过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            }
        }
        /**
            * 函数:      receiveCommAlarm
            * 函数描述:  接收HCNetSDK.COMM_ALARM类报警
            * @param pAlarmInfo  用于接收报警信息的缓存区
        */
        private void receiveCommAlarm(HCNetSDK.RECV_ALARM pAlarmInfo, HCNetSDK.NET_DVR_ALARMER pAlarmer){
            try{
                    HCNetSDK.NET_DVR_ALARMINFO strAlarmInfo = new HCNetSDK.NET_DVR_ALARMINFO();
                    strAlarmInfo.write();
                    Pointer pInfo = strAlarmInfo.getPointer();
                    pInfo.write(0, pAlarmInfo.RecvBuffer, 0, strAlarmInfo.size());
                    strAlarmInfo.read();

                    switch (strAlarmInfo.dwAlarmType)
                    {
                            case 0://报警输入
                                sAlarmType = sAlarmTypes[0];//"信号量报警";
                                iDVROrResource = 0;//是设备报警输入资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_CODE;//设备报警输入资源类型
                                iChannelDiskAlarmIn = strAlarmInfo.dwAlarmInputNumber;//报警输入号
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_ALARMIN ;
                                break;
                            case 1://异常报警
                                sAlarmType = sAlarmTypes[1];//"硬盘满";
                                iDVROrResource = 0;//是设备硬盘资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_DISK_CODE;//设备硬盘资源类型
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfo.dwDiskNumber);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX ;
                                break;
                            case 2:
                                sAlarmType = sAlarmTypes[2];//"信号丢失";
                                iDVROrResource = 0;//是设备通道资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE;//设备通道资源类型
                                //为了和NET_VCA_RULE_ALARM保持一致，所以通道号+1后，从1开始（strAlarmInfoV30.byChannel数组下标是从0开始）
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfo.dwChannel);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_OTHER ;
                                break;
                            case 3:
                                sAlarmType = sAlarmTypes[3];//"移动侦测";
                                iDVROrResource = 0;//是设备通道资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE;//设备通道资源类型
                                //为了和NET_VCA_RULE_ALARM保持一致，所以通道号+1后，从1开始（strAlarmInfoV30.byChannel数组下标是从0开始）
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfo.dwChannel);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_MOTION ;
                                break;
                            case 4://异常报警
                                sAlarmType = sAlarmTypes[4];//"硬盘未格式化";
                                iDVROrResource = 0;//是设备硬盘资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_DISK_CODE;//设备硬盘资源类型
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfo.dwDiskNumber);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX ;
                                break;
                            case 5://异常报警
                                sAlarmType = sAlarmTypes[5];//"读写硬盘出错";
                                iDVROrResource = 0;//是设备硬盘资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_DISK_CODE;//设备硬盘资源类型
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfo.dwDiskNumber);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX ;
                                break;
                            case 6:
                                sAlarmType = sAlarmTypes[6];//"遮挡报警";
                                iDVROrResource = 0;//是设备通道资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE;//设备通道资源类型
                                //为了和NET_VCA_RULE_ALARM保持一致，所以通道号+1后，从1开始（strAlarmInfoV30.byChannel数组下标是从0开始）
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfo.dwChannel);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_OTHER ;
                                break;
                            case 7://异常报警
                                sAlarmType = sAlarmTypes[7];//"制式不匹配";
                                iDVROrResource = 1;//是设备报警
                                sResourceType = "";//设备通道资源类型
                                iChannelDiskAlarmIn = 0;
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX ;
                                break;
                            case 8://异常报警
                                sAlarmType = sAlarmTypes[8];//"非法访问";
                                iDVROrResource = 1;//是设备报警
                                sResourceType = "";//设备通道资源类型
                                iChannelDiskAlarmIn = 0;
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX;
                                break;
                                  
                    }
                    
                    if (iChannelDiskAlarmIn > HCNetSDK.MAX_ANALOG_CHANNUM) {
                        HCNetSDK.NET_DVR_IPPARACFG StrIpparacfg = getStrIpparacfg(sIP[0],FILE_NAME);
                        if (StrIpparacfg != null) {
                            iChannelJoin = StrIpparacfg.struIPChanInfo[iChannelDiskAlarmIn - HCNetSDK.MAX_ANALOG_CHANNUM -1].byChannel;
                            int iDevID = StrIpparacfg.struIPChanInfo[iChannelDiskAlarmIn - HCNetSDK.MAX_ANALOG_CHANNUM -1].byIPID;
                            sIPJoin = new String(StrIpparacfg.struIPDevInfo[iDevID -1].struIP.sIpV4).split("\0", 2);
                        }
                    }

            }catch (Exception e){
                TxtLogger.append(FILE_NAME, "receiveCommAlarm()","系统在接收HCNetSDK.COMM_ALARM类报警过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            }
        }
        /**
            * 函数:      receiveCommAlarmV30
            * 函数描述:  接收HCNetSDK.COMM_ALARM_V30类报警
            * @param pAlarmInfo  用于接收报警信息的缓存区
        */
        private void receiveCommAlarmV30(HCNetSDK.RECV_ALARM pAlarmInfo,HCNetSDK.NET_DVR_ALARMER pAlarmer){
            try{
                    HCNetSDK.NET_DVR_ALARMINFO_V30 strAlarmInfoV30 = new HCNetSDK.NET_DVR_ALARMINFO_V30();
                    strAlarmInfoV30.write();
                    Pointer pInfoV30 = strAlarmInfoV30.getPointer();
                    pInfoV30.write(0, pAlarmInfo.RecvBuffer, 0, strAlarmInfoV30.size());
                    strAlarmInfoV30.read();

                    switch (strAlarmInfoV30.dwAlarmType)
                    {
                            case 0://报警输入
                                sAlarmType = sAlarmTypes[0];//"信号量报警";
                                iDVROrResource = 0;//是设备报警输入资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_CODE;//设备报警输入资源类型
                                iChannelDiskAlarmIn = strAlarmInfoV30.dwAlarmInputNumber;//报警输入号
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_ALARMIN ;
                                break;
                            case 1://异常报警
                                sAlarmType = sAlarmTypes[1];//"硬盘满";
                                iDVROrResource = 0;//是设备硬盘资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_DISK_CODE;//设备硬盘资源类型
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfoV30.byDiskNumber);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX;
                                break;
                            case 2:
                                sAlarmType = sAlarmTypes[2];//"信号丢失";
                                iDVROrResource = 0;//是设备通道资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE;//设备通道资源类型
                                //为了和NET_VCA_RULE_ALARM保持一致，所以通道号+1后，从1开始（strAlarmInfoV30.byChannel数组下标是从0开始）
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfoV30.byChannel);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_OTHER ;
                                break;
                            case 3:
                                sAlarmType = sAlarmTypes[3];//"移动侦测";
                                iDVROrResource = 0;//是设备通道资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE;//设备通道资源类型
                                //为了和NET_VCA_RULE_ALARM保持一致，所以通道号+1后，从1开始（strAlarmInfoV30.byChannel数组下标是从0开始）
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfoV30.byChannel);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_MOTION ;
                                break;
                            case 4://异常报警
                                sAlarmType = sAlarmTypes[4];//"硬盘未格式化";
                                iDVROrResource = 0;//是设备硬盘资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_DISK_CODE;//设备硬盘资源类型
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfoV30.byDiskNumber);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX;
                                break;
                            case 5://异常报警
                                sAlarmType = sAlarmTypes[5];//"读写硬盘出错";
                                iDVROrResource = 0;//是设备硬盘资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_DISK_CODE;//设备硬盘资源类型
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfoV30.byDiskNumber);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX;
                                break;
                            case 6:
                                sAlarmType = sAlarmTypes[6];//"遮挡报警";
                                iDVROrResource = 0;//是设备通道资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE;//设备通道资源类型
                                //为了和NET_VCA_RULE_ALARM保持一致，所以通道号+1后，从1开始（strAlarmInfoV30.byChannel数组下标是从0开始）
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfoV30.byChannel);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_OTHER ;
                                break;
                            case 7://异常报警
                                sAlarmType = sAlarmTypes[7];//"制式不匹配";
                                iDVROrResource = 1;//是设备报警
                                sResourceType = "";//设备通道资源类型
                                iChannelDiskAlarmIn = 0;
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX;
                                break;
                            case 8://异常报警
                                sAlarmType = sAlarmTypes[8];//"非法访问";
                                iDVROrResource = 1;//是设备报警
                                sResourceType = "";//设备通道资源类型
                                iChannelDiskAlarmIn = 0;
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX;
                                break;
                            case 9://异常报警
                                sAlarmType = sAlarmTypes[9];//"视频信号异常";
                                iDVROrResource = 0;//是设备通道资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE;//设备通道资源类型
                                //为了和NET_VCA_RULE_ALARM保持一致，所以通道号+1后，从1开始（strAlarmInfoV30.byChannel数组下标是从0开始）
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfoV30.byChannel);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX;
                                break;
                            case 10://异常报警
                                sAlarmType = sAlarmTypes[10];//"录像/抓图异常";
                                iDVROrResource = 0;//是设备通道资源报警
                                sResourceType = DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_CODE;//设备通道资源类型
                                //为了和NET_VCA_RULE_ALARM保持一致，所以通道号+1后，从1开始（strAlarmInfoV30.byChannel数组下标是从0开始）
                                iChannelDiskAlarmIn = getAlarmResourceNO(strAlarmInfoV30.byChannel);
                                AlarmWaveFilePath = SysParas.SYSPARAS_ALARMAUDIO_DVREX;
                                break;
                    }
                    
                    if (iChannelDiskAlarmIn > HCNetSDK.MAX_ANALOG_CHANNUM) {
                        HCNetSDK.NET_DVR_IPPARACFG StrIpparacfg = getStrIpparacfg(sIP[0],FILE_NAME);
                        if (StrIpparacfg != null) {
                            iChannelJoin = StrIpparacfg.struIPChanInfo[iChannelDiskAlarmIn - HCNetSDK.MAX_ANALOG_CHANNUM -1].byChannel;
                            int iDevID = StrIpparacfg.struIPChanInfo[iChannelDiskAlarmIn - HCNetSDK.MAX_ANALOG_CHANNUM -1].byIPID;
                            sIPJoin = new String(StrIpparacfg.struIPDevInfo[iDevID -1].struIP.sIpV4).split("\0", 2);
                        }
                    }

            }catch (Exception e){
                TxtLogger.append(FILE_NAME, "receiveCommAlarmV30()","系统在接收HCNetSDK.COMM_ALARM_V30类报警过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            }
        }
        /**
            * 函数:      getAlarmChannel
            * 函数描述:  获得报警通道或者硬盘号
            * @param byChannelDisk  存放各通道是否报警的通道数组（strAlarmInfoV30.byChannel）、硬盘数组strAlarmInfoV30.byDiskNumber
            * @return int 返回报警通道号/硬盘号，从1开始
        */
        private int getAlarmResourceNO(byte[] byChannelDisk){
            //从0到MAX_CHANNUM_V30 - 1；MAX_CHANNUM_V30 = (MAX_ANALOG_CHANNUM + MAX_IP_CHANNEL)
            for (int i=0;i<byChannelDisk.length;i++){
                if (byChannelDisk[i] == 1){
                    return i+1;
                }
            }
            return 0;
        }
        /**
            * 函数:      getAlarmChannel
            * 函数描述:  获得报警通道或者硬盘号
            * @param dwChannelDisk  存放各通道是否报警的通道数组（strAlarmInfoV30.byChannel）、硬盘数组strAlarmInfoV30.byDiskNumber
            * @return int 返回报警通道号/硬盘号，从1开始
        */
        private int getAlarmResourceNO(int[] dwChannelDisk){
            //从0到MAX_CHANNUM_V30 - 1；MAX_CHANNUM_V30 = (MAX_ANALOG_CHANNUM + MAX_IP_CHANNEL)
            for (int i=0;i<dwChannelDisk.length;i++){
                if (dwChannelDisk[i] == 1){
                    return i+1;
                }
            }
            return 0;
        }
        public JDialogPopUpAlarmMSG getJDialogPopUpAlarmMSG(int Row){
            if(AlarmRecCtrl.isPopUpAlarmMSGOpened)  {
                DialogPopUpAlarmMSG.getAlarmMSG(Row);
            }else{
                DialogPopUpAlarmMSG = new JDialogPopUpAlarmMSG(null,false,Row);
                centerWindow(DialogPopUpAlarmMSG);
                DialogPopUpAlarmMSG.setVisible(true);
                DialogPopUpAlarmMSG.setAlwaysOnTop(true);
            }
            return DialogPopUpAlarmMSG ;
        }
        
        public void makeAlarmVoice(String PathOfWaveFile){
            try{
                if(AlarmRecCtrl.ifVoice)  {
                    playAlarmWaveFile = WaveFilePlay.getWaveFilePlay(PathOfWaveFile);//new PlayWaveFile();
                    playAlarmWaveFile.startPlay();
                }
            }catch (Exception e){
                TxtLogger.append(FILE_NAME, "makeAlarmVoice()","系统在发出报警声音的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            }
        }
        
        public void stopAlarmVoice(){
            if (playAlarmWaveFile != null) playAlarmWaveFile.stopPlay();
        }
        
        public ArrayList getListShowAlarmTypes(){
            return listShowAlarmTypes;
        }
        
        /**
            * 函数:      modifyLocales
            * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
        */
        public void modifyLocales(){
            if (SysParas.ifChinese) return;//如果是中文，则不做任何操作
            MyLocales Locales = SysParas.sysLocales;
            
            sAlarmTypes[0]  = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes0");//信号量报警
            sAlarmTypes[1]  = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes1");//硬盘满
            sAlarmTypes[2]  = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes2");//信号丢失
            sAlarmTypes[3]  = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes3");//移动侦测
            sAlarmTypes[4]  = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes4");//硬盘未格式化
            sAlarmTypes[5]  = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes5");//读写硬盘出错
            sAlarmTypes[6]  = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes6");//遮挡报警
            sAlarmTypes[7]  = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes7");//制式不匹配
            sAlarmTypes[8]  = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes8");//非法访问
            sAlarmTypes[9]  = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes9");//视频信号异常
            sAlarmTypes[10] = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes10");//录像/抓图异常
            sAlarmTypes[11] = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes11");//越界侦测报警
            sAlarmTypes[12] = Locales.getString("ClassStrings", "AlarmFMSGCallBack.sAlarmTypes12");//区域入侵侦测报警
            
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locales.getLocale());

        }

    }
    
    
    /**
        *内部类:   DVRType
        *类描述:   设备类型代码类
    */ 
    public static class DVRType{
        public static final String DVRTYPE_CODE = "030000";//设备类型
        public static final String DVRTYPE_ENCODINGDVR_CODE = "030100";//编码设备/门口机
        
        public static final String DVRTYPE_ENCODINGDVR_CAMERA_CODE = "030101";//编码设备/门口机-摄像设备
        public static final String DVRTYPE_ENCODINGDVR_NVR_CODE = "030102";//编码设备/门口机-NVR录像机
        
        /**
            * 函数:      getDVRSecondaryType
            * 函数描述:  根据设备参数结构体NET_DVR_DEVICEINFO_V30获取设备的二级类型代码
            *            因为现在只有2种设备可以选择，所以只是先将函数写出来，以后扩展时修改
            * @param StrDevInfo HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo
            * @return String 设备的二级类型代码，失败返回""
        */
        public static String getDVRSecondaryType(HCNetSDK.NET_DVR_DEVICEINFO_V30 StrDevInfo){
            if (HCNetSDKExpand.isNVR(StrDevInfo)) return DVRTYPE_ENCODINGDVR_NVR_CODE;
            else return DVRTYPE_ENCODINGDVR_CAMERA_CODE;
        }
    }
    /**
        *内部类:   DVRResourceType
        *类描述:   设备资源代码类
    */ 
    public static class DVRResourceType{
        
        public static final String RESTYPE_CODE = "040000";//设备资源类型,简称RESTYPE
        
        public static final String RESTYPE_ENCODINGDVR_CODE = "040100";//编码设备资源类型
        
        public static final String RESTYPE_ENCODINGDVR_CHANNEL_CODE = "040101";//编码通道
        public static final String RESTYPE_ENCODINGDVR_ALARMIN_CODE = "040102";//报警输入
        public static final String RESTYPE_ENCODINGDVR_DEFENCEAREA_CODE = "040103";//防区
        public static final String RESTYPE_ENCODINGDVR_ALARMOUT_CODE = "040104";//报警输出
        public static final String RESTYPE_ENCODINGDVR_DISK_CODE = "040105";//硬盘
        
        //和下面的设备资源分类名称对应
        public static final String NODETYPE_CHANNEL = "Channel";
        public static final String NODETYPE_ALARMIN = "AlarmIn";
        public static final String NODETYPE_ALARMOUT = "AlarmOut";
        public static final String NODETYPE_DISK = "Disk";
        //public static final String NODETYPE_DEFENCEAREA = "DefenceArea";
        
        public static String RESTYPE_ENCODINGDVR_CHANNEL_NAME = "编码通道";//编码通道
        public static String RESTYPE_ENCODINGDVR_ALARMIN_NAME = "报警输入";//报警输入
        public static String RESTYPE_ENCODINGDVR_DEFENCEAREA_NAME = "防区";//防区
        public static String RESTYPE_ENCODINGDVR_ALARMOUT_NAME = "报警输出";//报警输出
        public static String RESTYPE_ENCODINGDVR_DISK_NAME = "硬盘";//硬盘
        
        /**
            * 函数:      ifFitRules
            * 函数描述:  验证设备别名的命名是否符合规则（不能包含特殊字符，包括IP）
            * @param AnotherName
            * @return String 符合标准，返回""；否则返回不符合标准的字符串。
        */
        public static String ifFitRules(String AnotherName){
            if (AnotherName.lastIndexOf(RESTYPE_ENCODINGDVR_CHANNEL_NAME)       > -1) return RESTYPE_ENCODINGDVR_CHANNEL_NAME;
            if (AnotherName.lastIndexOf(RESTYPE_ENCODINGDVR_ALARMIN_NAME)       > -1) return RESTYPE_ENCODINGDVR_ALARMIN_NAME;
            if (AnotherName.lastIndexOf(RESTYPE_ENCODINGDVR_DEFENCEAREA_NAME)   > -1) return RESTYPE_ENCODINGDVR_DEFENCEAREA_NAME;
            if (AnotherName.lastIndexOf(RESTYPE_ENCODINGDVR_ALARMOUT_NAME)      > -1) return RESTYPE_ENCODINGDVR_ALARMOUT_NAME;
            if (AnotherName.lastIndexOf(RESTYPE_ENCODINGDVR_DISK_NAME)          > -1) return RESTYPE_ENCODINGDVR_DISK_NAME;
            
            if (AnotherName.lastIndexOf(NODETYPE_CHANNEL)   > -1) return NODETYPE_CHANNEL;
            if (AnotherName.lastIndexOf(NODETYPE_ALARMIN)   > -1) return NODETYPE_ALARMIN;
            if (AnotherName.lastIndexOf(NODETYPE_ALARMOUT)  > -1) return NODETYPE_ALARMOUT;
            if (AnotherName.lastIndexOf(NODETYPE_DISK)      > -1) return NODETYPE_DISK;
            if (AnotherName.lastIndexOf("IP") > -1) return "IP";
            if (AnotherName.lastIndexOf("_") > -1) return "_";
            return "";
        }
        /**
            * 函数:      getResourceTypeFromNode
            * 函数描述:  根据设备资源节点名称得到设备资源类型代码
            * @param NodeName 设备资源节点名称
            * @return String 设备资源节点代码；否则返回""。
        */
        public static String getResourceTypeCodeFromNode(String NodeName){
            return getResourceTypeCode(getResourceTypeFromNode(NodeName));
        }
        /**
            * 函数:      getResourceTypeFromNode
            * 函数描述:  根据设备资源节点名称得到设备资源类型名称
            * @param NodeName 设备资源节点名称
            * @return String 设备资源节点名称；否则返回""。
        */
        public static String getResourceTypeFromNode(String NodeName){
            if (NodeName.lastIndexOf(RESTYPE_ENCODINGDVR_CHANNEL_NAME) > -1) return RESTYPE_ENCODINGDVR_CHANNEL_NAME;
            if (NodeName.lastIndexOf(RESTYPE_ENCODINGDVR_ALARMIN_NAME) > -1) return RESTYPE_ENCODINGDVR_ALARMIN_NAME;
            if (NodeName.lastIndexOf(RESTYPE_ENCODINGDVR_DEFENCEAREA_NAME) > -1) return RESTYPE_ENCODINGDVR_DEFENCEAREA_NAME;
            if (NodeName.lastIndexOf(RESTYPE_ENCODINGDVR_ALARMOUT_NAME) > -1) return RESTYPE_ENCODINGDVR_ALARMOUT_NAME;
            if (NodeName.lastIndexOf(RESTYPE_ENCODINGDVR_DISK_NAME) > -1) return RESTYPE_ENCODINGDVR_DISK_NAME;
            return "";
        }
        /**
            * 函数:      getResourceTypeCode
            * 函数描述:  根据设备类型名称得到类型代码
            * @param ResourceTypeName 
            * @return String 返回类型代码；否则返回""。
        */
        public static String getResourceTypeCode(String ResourceTypeName){
            if (ResourceTypeName.equals(RESTYPE_ENCODINGDVR_CHANNEL_NAME)) return RESTYPE_ENCODINGDVR_CHANNEL_CODE;
            if (ResourceTypeName.equals(RESTYPE_ENCODINGDVR_ALARMIN_NAME)) return RESTYPE_ENCODINGDVR_ALARMIN_CODE;
            if (ResourceTypeName.equals(RESTYPE_ENCODINGDVR_DEFENCEAREA_NAME)) return RESTYPE_ENCODINGDVR_DEFENCEAREA_CODE;
            if (ResourceTypeName.equals(RESTYPE_ENCODINGDVR_ALARMOUT_NAME)) return RESTYPE_ENCODINGDVR_ALARMOUT_CODE;
            if (ResourceTypeName.equals(RESTYPE_ENCODINGDVR_DISK_NAME)) return RESTYPE_ENCODINGDVR_DISK_CODE;
            return "";
        }
        
        /**
            * 函数:      modifyLocales
            * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
        */
        public static void modifyLocales(){
            if (SysParas.ifChinese) return;//如果是中文，则不做任何操作
            MyLocales Locales = SysParas.sysLocales;
            
            RESTYPE_ENCODINGDVR_CHANNEL_NAME        = Locales.getString("ClassStrings", "DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_NAME");//编码通道
            RESTYPE_ENCODINGDVR_ALARMIN_NAME        = Locales.getString("ClassStrings", "DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_NAME");//报警输入
            RESTYPE_ENCODINGDVR_DEFENCEAREA_NAME    = Locales.getString("ClassStrings", "DVRResourceType.RESTYPE_ENCODINGDVR_DEFENCEAREA_NAME");//防区
            RESTYPE_ENCODINGDVR_ALARMOUT_NAME       = Locales.getString("ClassStrings", "DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_NAME");//报警输出
            RESTYPE_ENCODINGDVR_DISK_NAME           = Locales.getString("ClassStrings", "DVRResourceType.RESTYPE_ENCODINGDVR_DISK_NAME");//硬盘
        }
    }
    /**
        *内部类:   LogType
        *类描述:   日志类型代码类
    */  
    public static class LogType{
        public static final String LOG_TYPE_CODE = "050000";//日志类型
        //日志类型代码
        public static final String LOG_SYS_CODE = "050100";//系统日志代码
        public static final String LOG_OPER_CODE = "050200";//操作日志代码
        public static final String LOG_ALARM_CODE = "050300";//报警日志代码
        public static final String LOG_ERROR_CODE = "050400";//错误日志代码
        //日志类型名称
        public static String LOG_SYS_NAME = "系统日志";//系统日志
        public static String LOG_OPER_NAME = "操作日志";//操作日志
        public static String LOG_ALARM_NAME = "报警日志";//报警日志
        public static String LOG_ERROR_NAME = "错误日志";//错误日志
        
        /**
            * 函数:      modifyLocales
            * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
        */
        public static void modifyLocales(){
            if (SysParas.ifChinese) return;//如果是中文，则不做任何操作
            MyLocales Locales = SysParas.sysLocales;
            
            LOG_SYS_NAME    = Locales.getString("ClassStrings", "LogType.LOG_SYS_NAME");//系统日志
            LOG_OPER_NAME   = Locales.getString("ClassStrings", "LogType.LOG_OPER_NAME");//操作日志
            LOG_ALARM_NAME  = Locales.getString("ClassStrings", "LogType.LOG_ALARM_NAME");//报警日志
            LOG_ERROR_NAME  = Locales.getString("ClassStrings", "LogType.LOG_ERROR_NAME");//错误日志

        }
    }
    /**
        *内部类:   TimeTemplateClass
        *类描述:   时间模板用途分类
    */  
    public static class TimeTemplateClass{
        //时间模板用途分类
        public static final String MONITOR_SETUPALARMCHAN = "070100";// 监控点报警布防时间模板
        public static final String EXCEPTION_SETUPALARMCHAN  = "070200";//异常报警布防时间模板
        public static final String ALARMIN_SETUPALARMCHAN  = "070300";//报警输入布防时间模板
        public static final String ALARMOUT_SETUPALARMCHAN  = "070400";//报警输出布防时间模板
        public static final String RECORD_SCHEDULE  = "070500";//录像存储计划时间模板
        public static final String CAPTURE_SCHEEDULE  = "070600";//抓图计划时间模板
        
        public static String createTimeTemplateClas(int Index){
            return "070" + Index + "00";
        }
    }
    /**
        *内部类:   SysParas
        *类描述:   系统参数分类
    */  
    public static class SysParas{
        
        public static MyLocales sysLocales;
        public static boolean ifChinese = true;//是否为中文。if (Language.equals(MyLocales.LANGUAGE_CHINESE)) return;//如果是中文，则不做任何操作
        
        public static final int SYSPARACLASS_NUM = 4;//系统参数种类个数，现在暂定为4，以后添加再说
        public static String SYSPARAS_COMMON_NAME = "系统常用参数";//0
        public static String SYSPARAS_ALARMAUDIO_NAME = "报警声音参数";//1
        public static String SYSPARAS_IMAGE_NAME = "图像参数";//2
        public static String SYSPARAS_FILESAVE_NAME = "文件存储参数";//3
        
        public static int SYSPARAS_COMMON_ALARMTIME = 5;//报警时间持续时长，秒
        public static String SYSPARAS_COMMON_LANGUAGE = "Chinese";//系统语言
        public static String SYSPARAS_COMMON_COUNTRY = "China";//系统国家
        
        public static String SYSPARAS_ALARMAUDIO_MOTION = "";//移动侦测报警声音
        public static String SYSPARAS_ALARMAUDIO_ALARMIN = "";//报警输入报警声音
        public static String SYSPARAS_ALARMAUDIO_DVREX = "";//设备异常报警声音
        public static String SYSPARAS_ALARMAUDIO_SIGNALEX = "";//视频信号异常报警声音
        public static String SYSPARAS_ALARMAUDIO_TREVERSEPLANE = "";//越界侦测报警声音
        public static String SYSPARAS_ALARMAUDIO_FIELDDETECION = "";//区域入侵报警声音
        public static String SYSPARAS_ALARMAUDIO_OTHER = "";//其他报警的报警声音

        public static String SYSPARAS_FILESAVEDIR_VEDIO = "";//视频文件保存路径
        public static String SYSPARAS_FILESAVEDIR_CAPTURE = "";//抓图文件保存路径
        public static String SYSPARAS_FILESAVEDIR_SETUP = "";//设备配置文件保存路径
        
        private static String selectFilePath    = "请选择文件保存路径...";
        private static String approveButtonText = "确定";
        
        /**
            * 函数:      getSysParas
            * 函数描述:  从数据库中读取系统公开的参数
            * @return boolean 成功返回true；失败返回false。
        */
        public static boolean initialSysParas(){
            try{
                Locale.setDefault(Locale.ENGLISH);//暂时先按通用的英文作为国际化标准。因为下面的两次弹出对话框需要英文表示
                //系统第一次连接数据库
                ArrayList listSysParas = SysParaCodesBean.getSysParaList("", FILE_NAME);
                if (listSysParas == null || listSysParas.isEmpty()) {
                    if (InitDB.getErrorCode() > 0)
                        JOptionPane.showMessageDialog(null, "Database connection error! Please check whether the database engine starts ...", "Error", JOptionPane.ERROR_MESSAGE);//"数据库连接错误！请检查数据库引擎是否启动..."
                        //showErrorMessageDialog( null, "Database connection error! Please check whether the database engine starts ...");
                    return false;
                }

                for (int i=0;i<listSysParas.size();i++){
                    ArrayList newList = (ArrayList)listSysParas.get(i);
                    SysParaCodesBean ParaCodesBean = (SysParaCodesBean)newList.get(0);
                    switch (ParaCodesBean.getSysparacode()){
                        case SYSPARAS_ALARMAUDIO_MOTION_CODE://移动侦测报警声音
                            SYSPARAS_ALARMAUDIO_MOTION = modifyWaveFilePath(ParaCodesBean.getSysparavalue(), "intel_alarm.wav");
                            break;
                        case SYSPARAS_ALARMAUDIO_ALARMIN_CODE://报警输入报警声音
                            SYSPARAS_ALARMAUDIO_ALARMIN = modifyWaveFilePath(ParaCodesBean.getSysparavalue(), "alarm_input.wav");
                            break;
                        case SYSPARAS_ALARMAUDIO_DVREX_CODE://设备异常报警声音
                            SYSPARAS_ALARMAUDIO_DVREX = modifyWaveFilePath(ParaCodesBean.getSysparavalue(), "device_abnormal.wav");
                            break;
                        case SYSPARAS_ALARMAUDIO_SIGNALEX_CODE://视频信号异常报警声音
                            SYSPARAS_ALARMAUDIO_SIGNALEX = modifyWaveFilePath(ParaCodesBean.getSysparavalue(), "video_abnormal.wav");
                            break;
                        case SYSPARAS_ALARMAUDIO_TREVERSE_CODE://越界侦测报警声音
                            SYSPARAS_ALARMAUDIO_TREVERSEPLANE = modifyWaveFilePath(ParaCodesBean.getSysparavalue(), "Alarm.wav");
                            break;
                        case SYSPARAS_ALARMAUDIO_FIELD_CODE://区域入侵报警声音
                            SYSPARAS_ALARMAUDIO_FIELDDETECION = modifyWaveFilePath(ParaCodesBean.getSysparavalue(), "Alarm.wav");
                            break;
                        case SYSPARAS_ALARMAUDIO_OTHER_CODE://其他报警的报警声音
                            SYSPARAS_ALARMAUDIO_OTHER = modifyWaveFilePath(ParaCodesBean.getSysparavalue(), "other_alarm.wav");
                            break;
                        case SYSPARAS_FILESAVEDIR_VEDIO_CODE://视频文件保存路径
                            SYSPARAS_FILESAVEDIR_VEDIO = ParaCodesBean.getSysparavalue();
                            createDirectory(SYSPARAS_FILESAVEDIR_VEDIO);
                            break;
                        case SYSPARAS_FILESAVEDIR_CAPTURE_CODE://抓图文件保存路径
                            SYSPARAS_FILESAVEDIR_CAPTURE = ParaCodesBean.getSysparavalue();
                            createDirectory(SYSPARAS_FILESAVEDIR_CAPTURE);
                            break;
                        case SYSPARAS_FILESAVEDIR_SETUP_CODE://设备配置文件保存路径
                            SYSPARAS_FILESAVEDIR_SETUP = ParaCodesBean.getSysparavalue();
                            createDirectory(SYSPARAS_FILESAVEDIR_SETUP);
                            break;
                        case SYSPARAS_COMMON_ALARMTIME_CODE://报警时间持续时长
                            SYSPARAS_COMMON_ALARMTIME = Integer.parseInt(ParaCodesBean.getSysparavalue());//报警时间持续时长，秒
                            break;
                        case SYSPARAS_COMMON_LANGUAGE_CODE://系统语言
                            SYSPARAS_COMMON_LANGUAGE = ParaCodesBean.getSysparavalue();
                            break;
                        case SYSPARAS_COMMON_COUNTRY_CODE://安装系统的国家
                            SYSPARAS_COMMON_COUNTRY = ParaCodesBean.getSysparavalue();
                            break;
                        default:
                            break;
                    }
                }
                listSysParas.clear();
                sysLocales = MyLocales.getMyLocales(SYSPARAS_COMMON_LANGUAGE, SYSPARAS_COMMON_COUNTRY);
                Locale.setDefault(sysLocales.getLocale());//选择国际化标准
                ifChinese = SYSPARAS_COMMON_LANGUAGE.equals(MyLocales.LANGUAGE_CHINESE);//是否是中文
                return true;
            }catch (Exception e){
                TxtLogger.append(FILE_NAME, "initialSysParas()","系统在从数据库中读取系统公开参数过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
            }
            JOptionPane.showMessageDialog(null, "Reading system parameter error!", "Remind", JOptionPane.ERROR_MESSAGE);//
            return false;
        }
        //比如200201。一共分3层：（1-2位）20代表系统参数；（3-4位）02代表报警声音参数代码；（5-6位）01代表移动侦测报警声音
        public static final String SYSPARAS_CODE = "200000";//系统参数代码
        public static final String SYSPARAS_COMMON_CODE = "200100";//系统常用参数代码
        public static final String SYSPARAS_ALARMAUDIO_CODE = "200200";//报警声音参数代码
        public static final String SYSPARAS_IMAGE_CODE = "200300";//图像参数代码
        public static final String SYSPARAS_FILESAVE_CODE = "200400";//文件存储参数代码
        
        public static final String SYSPARAS_COMMON_REGCODE_CODE = "200101";//系统注册码（保存在系统中）
        public static final String SYSPARAS_COMMON_ALARMTIME_CODE = "200102";//报警时间持续时长
        public static final String SYSPARAS_COMMON_LANGUAGE_CODE = "200103";//系统语言
        public static final String SYSPARAS_COMMON_COUNTRY_CODE = "200104";//系统国家
         
        public static final String SYSPARAS_ALARMAUDIO_MOTION_CODE = "200201";//移动侦测报警声音
        public static final String SYSPARAS_ALARMAUDIO_ALARMIN_CODE = "200202";//报警输入报警声音
        public static final String SYSPARAS_ALARMAUDIO_DVREX_CODE = "200203";//设备异常报警声音
        public static final String SYSPARAS_ALARMAUDIO_SIGNALEX_CODE = "200204";//视频信号异常报警声音
        public static final String SYSPARAS_ALARMAUDIO_TREVERSE_CODE = "200205";//越界侦测报警声音
        public static final String SYSPARAS_ALARMAUDIO_FIELD_CODE = "200206";//区域入侵报警声音
        public static final String SYSPARAS_ALARMAUDIO_OTHER_CODE = "200207";//其他报警的报警声音
        
        public static final String SYSPARAS_FILESAVEDIR_VEDIO_CODE = "200401";//视频文件保存路径 参数代码
        public static final String SYSPARAS_FILESAVEDIR_CAPTURE_CODE = "200402";//抓图文件保存路径 参数代码
        public static final String SYSPARAS_FILESAVEDIR_SETUP_CODE = "200403";//设备配置文件保存路径 参数代码
        
        public static final String SYSPARAS_FILESAVE_PREVIEW = "Preview";//预览存储录像和抓图命名前缀
        public static final String SYSPARAS_FILESAVE_PLAYBACK = "PlackBack";//录像回放存储录像和抓图命名前缀
        
        /**
         * @return the SYSPARAS_FILESAVEDIR_VEDIO
         */
        public static String getSysParasFileSaveDirVedio() {
            String PathName = SYSPARAS_FILESAVEDIR_VEDIO;
            if (PathName.equals("")) {
                PathName = getPathFromJFileChooser();//获取被选择文件的路径
            }
            return PathName;
        }

        /**
         * @return the SYSPARAS_FILESAVEDIR_CAPTURE
         */
        public static String getSysParasFileSaveDirCapture() {
            String PathName = SYSPARAS_FILESAVEDIR_CAPTURE;
            if (PathName.equals("")) {
                PathName = getPathFromJFileChooser();//获取被选择文件的路径
            }
            return PathName;

        }

        /**
         * @return the SYSPARAS_FILESAVEDIR_SETUP
         */
        public static String getSysParasFileSaveDirSetup() {
            return SYSPARAS_FILESAVEDIR_SETUP;
        }
        /**
            * 函数:      setFileChooserDirectory
            * 函数描述:  为设备配置文件导入/导出的文件选择对话框设置默认目录 
            * @param JFileChooser1
        */
        public static void setFileChooserDirectory(JFileChooser JFileChooser1){
            if (!SYSPARAS_FILESAVEDIR_SETUP.equals(""))
                JFileChooser1.setCurrentDirectory(new File(SYSPARAS_FILESAVEDIR_SETUP));
        }
        
        private static String getPathFromJFileChooser(){
            JFileChooser myJFileChooser = new JFileChooser();
                myJFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY );

                myJFileChooser.setDialogTitle(selectFilePath);
                myJFileChooser.setApproveButtonText(approveButtonText);
                if (myJFileChooser.showSaveDialog(null) == JFileChooser.CANCEL_OPTION) return "";//如果取消，则返回。确定，则继续
                return myJFileChooser.getSelectedFile().getAbsolutePath();//获取被选择文件的路径
        }
        
        private static void createDirectory(String PathD){
            File file =new File(PathD);    
            //如果文件夹不存在则创建    
            if  (!PathD.equals("") && !file.exists()) {       
                file.mkdirs();    
            }
        }
        
        private static String modifyWaveFilePath(String OldFileName, String NewFileName){
            File file =new File(OldFileName);    
            //如果文件不存在，则用系统默认的文件   
            if  (OldFileName.equals("") || !file.exists()) {       
                return SysPath + "wav" + File.separator + NewFileName;    
            }
            return OldFileName;
        }

        /**
            * 函数:      modifyLocales
            * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
        */
        public static void modifyLocales(){
            if (ifChinese) return;//如果是中文，则不做任何操作
            MyLocales Locales = SysParas.sysLocales;
            
            SYSPARAS_COMMON_NAME        = Locales.getString("ClassStrings", "SysParas.SYSPARAS_COMMON_NAME");//"系统常用参数";//0
            SYSPARAS_ALARMAUDIO_NAME    = Locales.getString("ClassStrings", "SysParas.SYSPARAS_ALARMAUDIO_NAME");//"报警声音参数";//1
            SYSPARAS_IMAGE_NAME         = Locales.getString("ClassStrings", "SysParas.SYSPARAS_IMAGE_NAME");//"图像参数";//2
            SYSPARAS_FILESAVE_NAME      = Locales.getString("ClassStrings", "SysParas.SYSPARAS_FILESAVE_NAME");//"文件存储参数";//3
            selectFilePath              = Locales.getString("ClassStrings", "SysParas.selectFilePath");//"请选择文件保存路径...";
            approveButtonText           = Locales.getString("ClassStrings", "SysParas.approveButtonText");//"确定";
            
        }
        
    }
    
    
    /**
        *内部类:   AuthorityItems
        *类描述:   用户权限
    */  
    public static class AuthorityItems{
//        /*超级管理员权限信息*/
//        public static final String AUTHORITY_DVR_MANAGE = "设备管理";                   //Device Management
//        public static final String AUTHORITY_SYSPARAS_CONFIG = "系统参数配置";          //System Parameter Configuration
//        public static final String AUTHORITY_WIPER_CTRL = "控制雨刷";                   //Wiper Control
//        public static final String AUTHORITY_FISHBALL_TRACKSETUP = "鱼球联动设置";      //FishEye IPD Linkage Settings
//        /*管理员权限信息*/
//        public static final String AUTHORITY_RECORDSCHEDULE_CONFIG = "录像计划配置";     //Record Schedule
//        public static final String AUTHORITY_RECORDSCHEDULE_QUICKLYSETUP = "录像快速设置"; //Quick Record Schedule
//        public static final String AUTHORITY_USER_MANAGE = "用户管理";                   //Account Management
//        public static final String AUTHORITY_ALARMPARAS_SETUP = "报警参数设置";           //Alarm Parameter Setting
//        public static final String AUTHORITY_ALARM_SETUP = "设备报警布防";                //Device Alarm Arming
//        public static final String AUTHORITY_ALARMOUT_CTRL = "报警输出控制";              //Alarm Output Control
//        public static final String AUTHORITY_LOG_MANAGE = "日志管理";                     //Log Management
//        public static final String AUTHORITY_DVR_MAINT = "设备维护";                      //Device Maintenance
//        public static final String AUTHORITY_CHECKTIME_BATCH = "批量校时";                //Batch Time Sync
//        public static final String AUTHORITY_FISHBALL_TRACK = "鱼球联动";                 //FishEye IPD Linkage
//        public static final String AUTHORITY_CLIENT_EXIT = "退出客户端";                  //Logout
//        public static final String AUTHORITY_DVRGROUP_MANAGE = "设备分组管理";            //Device Group Management
//        /*操作员权限信息*/
//        public static final String AUTHORITY_PREVIEW = "设备预览";                        //Device Preview
//        public static final String AUTHORITY_PTZCTRL = "云台控制";                        //PTZ Control
//        public static final String AUTHORITY_PLAYBACK = "回放远程录像";                   //Remote Playback
//        public static final String AUTHORITY_PLAYBACK_DOWNLOAD = "下载远程录像";          //Download Remote Video
//        public static final String AUTHORITY_SPEAK = "语音对讲";                          //Voice Intercom
//        public static final String AUTHORITY_CHECKTIME = "设备校时";                      //Device Time Sync
        
        /*超级管理员权限信息*/
        public static final String AUTHORITY_DVR_MANAGE = "DVR_MANAGE";                     //设备管理
        public static final String AUTHORITY_SYSPARAS_CONFIG = "SYSPARAS_CONFIG";           //系统参数配置
        public static final String AUTHORITY_WIPER_CTRL = "WIPER_CTRL";                     //控制雨刷
        public static final String AUTHORITY_FISHBALL_TRACKSETUP = "FISHBALL_TRACKSETUP";   //鱼球联动设置
        public static final String AUTHORITY_LANGUAGE_CONVERT = "LANGUAGE_CONVERT";         //语言转换
        /*管理员权限信息*/
        public static final String AUTHORITY_RECORDSCHEDULE_CONFIG = "RECORDSCHEDULE_CONFIG"; //录像计划配置
        public static final String AUTHORITY_RECORDSCHEDULE_QUICKLYSETUP = "RECORDSCHEDULE_QUICKLYSETUP"; //录像快速设置
        public static final String AUTHORITY_USER_MANAGE = "USER_MANAGE";                   //用户管理
        public static final String AUTHORITY_ALARMPARAS_SETUP = "ALARMPARAS_SETUP";         //报警参数设置
        public static final String AUTHORITY_ALARM_SETUP = "ALARM_SETUP";                   //设备报警布防
        public static final String AUTHORITY_ALARMOUT_CTRL = "ALARMOUT_CTRL";               //报警输出控制
        public static final String AUTHORITY_LOG_MANAGE = "LOG_MANAGE";                     //日志管理
        public static final String AUTHORITY_DVR_MAINT = "DVR_MAINT";                       //设备维护
        public static final String AUTHORITY_CHECKTIME_BATCH = "CHECKTIME_BATCH";           //批量校时
        public static final String AUTHORITY_FISHBALL_TRACK = "FISHBALL_TRACK";             //鱼球联动
        public static final String AUTHORITY_CLIENT_EXIT = "CLIENT_EXIT";                   //退出客户端
        public static final String AUTHORITY_DVRGROUP_MANAGE = "DVRGROUP_MANAGE";           //设备分组管理
        /*操作员权限信息*/
        public static final String AUTHORITY_PREVIEW = "PREVIEW";                       //设备预览
        public static final String AUTHORITY_PTZCTRL = "PTZCTRL";                       //云台控制
        public static final String AUTHORITY_PLAYBACK = "PLAYBACK";                     //回放远程录像
        public static final String AUTHORITY_PLAYBACK_DOWNLOAD = "PLAYBACK_DOWNLOAD";   //下载远程录像
        public static final String AUTHORITY_SPEAK = "SPEAK";                           //语音对讲
        public static final String AUTHORITY_CHECKTIME = "CHECKTIME";                   //设备校时
        
//        /**
//            * 函数:      modifyLocales
//            * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
//        */
//        public static void modifyLocales(){
//            if (SysParas.ifChinese) return;//如果是中文，则不做任何操作
//            MyLocales Locales = SysParas.sysLocales;
//            
//            /*超级管理员权限信息*/
//            String AUTHORITY_DVR_MANAGE2                    = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_DVR_MANAGE");//设备管理
//            String AUTHORITY_SYSPARAS_CONFIG2               = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_SYSPARAS_CONFIG");//系统参数配置
//            String AUTHORITY_WIPER_CTRL2                    = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_WIPER_CTRL");//控制雨刷
//            String AUTHORITY_FISHBALL_TRACKSETUP2           = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_FISHBALL_TRACKSETUP");//鱼球联动设置
//            String AUTHORITY_LANGUAGE_CONVERT2              = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_LANGUAGE_CONVERT");//语言转换
//            /*管理员权限信息*/
//            String AUTHORITY_RECORDSCHEDULE_CONFIG2         = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_RECORDSCHEDULE_CONFIG");//录像计划配置
//            String AUTHORITY_RECORDSCHEDULE_QUICKLYSETUP2   = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_RECORDSCHEDULE_QUICKLYSETUP");//录像快速设置
//            String AUTHORITY_USER_MANAGE2                   = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_USER_MANAGE");//用户管理
//            String AUTHORITY_ALARMPARAS_SETUP2              = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_ALARMPARAS_SETUP");//报警参数设置
//            String AUTHORITY_ALARM_SETUP2                   = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_ALARM_SETUP");//设备报警布防
//            String AUTHORITY_ALARMOUT_CTRL2                 = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_ALARMOUT_CTRL");//报警输出控制
//            String AUTHORITY_LOG_MANAGE2                    = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_LOG_MANAGE");//日志管理
//            String AUTHORITY_DVR_MAINT2                     = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_DVR_MAINT");//设备维护
//            String AUTHORITY_CHECKTIME_BATCH2               = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_CHECKTIME_BATCH");//批量校时
//            String AUTHORITY_FISHBALL_TRACK2                = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_FISHBALL_TRACK");//鱼球联动
//            String AUTHORITY_CLIENT_EXIT2                   = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_CLIENT_EXIT");//退出客户端
//            String AUTHORITY_DVRGROUP_MANAGE2               = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_DVRGROUP_MANAGE");//设备分组管理
//            /*操作员权限信息*/
//            String AUTHORITY_PREVIEW2                       = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_PREVIEW");//设备预览
//            String AUTHORITY_PTZCTRL2                       = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_PTZCTRL");//云台控制
//            String AUTHORITY_PLAYBACK2                      = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_PLAYBACK");//回放远程录像
//            String AUTHORITY_PLAYBACK_DOWNLOAD2             = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_PLAYBACK_DOWNLOAD");//下载远程录像
//            String AUTHORITY_SPEAK2                         = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_SPEAK");//语音对讲
//            String AUTHORITY_CHECKTIME2                     = Locales.getString("ClassStrings", "AuthorityItems.AUTHORITY_CHECKTIME");//设备校时
//            
//            ArrayList<String> ListUpdate = new ArrayList();
//            /*超级管理员权限信息*/
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_DVR_MANAGE2,         AUTHORITY_DVR_MANAGE));//设备管理
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_SYSPARAS_CONFIG2,    AUTHORITY_SYSPARAS_CONFIG));//系统参数配置
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_WIPER_CTRL2,         AUTHORITY_WIPER_CTRL));//控制雨刷
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_FISHBALL_TRACKSETUP2,AUTHORITY_FISHBALL_TRACKSETUP));//鱼球联动设置
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_LANGUAGE_CONVERT2,   AUTHORITY_LANGUAGE_CONVERT));//语言转换
//            /*管理员权限信息*/
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_RECORDSCHEDULE_CONFIG2,      AUTHORITY_RECORDSCHEDULE_CONFIG));//录像计划配置
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_RECORDSCHEDULE_QUICKLYSETUP2,AUTHORITY_RECORDSCHEDULE_QUICKLYSETUP));//录像快速设置
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_USER_MANAGE2,                AUTHORITY_USER_MANAGE));//用户管理
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_ALARMPARAS_SETUP2,           AUTHORITY_ALARMPARAS_SETUP));//报警参数设置
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_ALARM_SETUP2,                AUTHORITY_ALARM_SETUP));//设备报警布防
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_ALARMOUT_CTRL2,              AUTHORITY_ALARMOUT_CTRL));//报警输出控制
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_LOG_MANAGE2,                 AUTHORITY_LOG_MANAGE));//日志管理
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_DVR_MAINT2,                  AUTHORITY_DVR_MAINT));//设备维护
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_CHECKTIME_BATCH2,    AUTHORITY_CHECKTIME_BATCH));//批量校时
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_FISHBALL_TRACK2,     AUTHORITY_FISHBALL_TRACK));//鱼球联动
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_CLIENT_EXIT2,        AUTHORITY_CLIENT_EXIT));//退出客户端
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_DVRGROUP_MANAGE2,    AUTHORITY_DVRGROUP_MANAGE));//设备分组管理
//            /*操作员权限信息*/
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_PREVIEW2,            AUTHORITY_PREVIEW));//设备预览
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_PTZCTRL2,            AUTHORITY_PTZCTRL));//云台控制
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_PLAYBACK2,           AUTHORITY_PLAYBACK));//回放远程录像
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_PLAYBACK_DOWNLOAD2,  AUTHORITY_PLAYBACK_DOWNLOAD));//下载远程录像
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_SPEAK2,              AUTHORITY_SPEAK));//语音对讲
//            ListUpdate.add(StandardAuthoritysBean.getStringUpdateCommand(AUTHORITY_CHECKTIME2,          AUTHORITY_CHECKTIME));//设备校时
//            
//            if (ListUpdate.size() > 0){
//                if (StandardAuthoritysBean.batchUpdateUpdate(ListUpdate, FILE_NAME) <= 0)   
//                    TxtLogger.append(FILE_NAME, "AuthorityItems.modifyLocales()","系统修改用户权限失败"); 
//            }
//
//        }


    }
    
    /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    public static void modifyLocales(){
        if (SysParas.ifChinese) return;//如果是中文，则不做任何操作
        
        SysParas.modifyLocales();
        //AuthorityItems.modifyLocales();//暂时不进行处理了，因为统一在修改语言的时候进行处理了。都写在数据库中了。
        
        LogType.modifyLocales();
        DVRResourceType.modifyLocales();
        
        jyms.tools.DateChooserJButtonE.modifyLocales();//时间选择窗口
        JDialogInfiniteProgress.modifyLocales();//进程等待窗口
        MulitCombobox.modifyLocales();//多选下拉列表框
        JDialogLockScreen.modifyLocales();//屏幕锁定
        
        ChannelPlayBack.modifyLocales();//录像回放类
        ChannelRealPlayer.modifyLocales();//设备预览类
        HCNetSDKExpand.modifyLocales();
        
        MyLocales Locales = SysParas.sysLocales;
        
        setupAlarmChanError = Locales.getString("MessageStrings", "CommonParas.setupAlarmChanError");//设备报警布防失败
        setupAlarmChanFail = Locales.getString("MessageStrings", "CommonParas.setupAlarmChanFail");//布防失败！
        setupAlarmChanSucc = Locales.getString("MessageStrings", "CommonParas.setupAlarmChanSucc");//布防成功！

        closeAlarmChanError = Locales.getString("MessageStrings", "CommonParas.closeAlarmChanError");//设备报警撤防失败
        closeAlarmChanFail = Locales.getString("MessageStrings", "CommonParas.closeAlarmChanFail");//撤防失败！
        closeAlarmChanSucc = Locales.getString("MessageStrings", "CommonParas.closeAlarmChanSucc");//撤防成功！

        errorLogOfLoginFail = Locales.getString("MessageStrings", "CommonParas.errorLogOfLoginFail");//设备登录失败
        errorMsgOfLoginFail = Locales.getString("MessageStrings", "CommonParas.errorMsgOfLoginFail");//设备登录失败！

        deviceErrorMessage = Locales.getString("MessageStrings", "CommonParas.deviceErrorMessage");//设备：{0}  {1}。错误代码：{2}；\r\n错误描述：{3}
        deviceErrorMessageDataBase = Locales.getString("MessageStrings", "CommonParas.deviceErrorMessageDataBase");//"错误！设备：{0}；操作：{1}！ 错误代码：{2}； 错误描述：{3}";
        noAuthorityMsg = Locales.getString("MessageStrings", "CommonParas.noAuthorityMsg");//您不拥有对“{0}”的“{1}”权限

        passwordGradeRisk = Locales.getString("MessageStrings", "CommonParas.passwordGradeRisk");//风险密码
        passwordGradeWeak = Locales.getString("MessageStrings", "CommonParas.passwordGradeWeak");//弱密码
        passwordGradeMedium = Locales.getString("MessageStrings", "CommonParas.passwordGradeMedium");//中密码
        passwordGradeStrong = Locales.getString("MessageStrings", "CommonParas.passwordGradeStrong");//强密码

//        nodeTypeChannel = Locales.getString("MessageStrings", "CommonParas.nodeTypeChannel");//监控点
//        nodeTypeAlarmIn = Locales.getString("MessageStrings", "CommonParas.nodeTypeAlarmIn");//报警输入
//        nodeTypeAlarmOut = Locales.getString("MessageStrings", "CommonParas.nodeTypeAlarmOut");//报警输出
//        nodeTypeDisk = Locales.getString("MessageStrings", "CommonParas.nodeTypeDisk");//硬盘
        
        errorMsgDialogTitle  = Locales.getString("MessageStrings", "CommonParas.errorMsgDialogTitle");//"错误";
        remindMsgDialogTitle = Locales.getString("MessageStrings", "CommonParas.remindMsgDialogTitle");//"提醒";

        USER_TYPE_ADMIN = Locales.getString("MessageStrings", "CommonParas.USER_TYPE_ADMIN");//超级管理员
        USER_TYPE_MANAGER = Locales.getString("MessageStrings", "CommonParas.USER_TYPE_MANAGER");//管理员
        USER_TYPE_OPERATOR = Locales.getString("MessageStrings", "CommonParas.USER_TYPE_OPERATOR");//操作员
        
        //常用词
        sWeek1 = Locales.getString("CommonParas", "sWeek1");  //星期一
        sWeek2 = Locales.getString("CommonParas", "sWeek2");  //星期二
        sWeek3 = Locales.getString("CommonParas", "sWeek3");  //星期三
        sWeek4 = Locales.getString("CommonParas", "sWeek4");  //星期四
        sWeek5 = Locales.getString("CommonParas", "sWeek5");  //星期五
        sWeek6 = Locales.getString("CommonParas", "sWeek6");  //星期六
        sWeek7 = Locales.getString("CommonParas", "sWeek7");  //星期日
        
        sUserLogout = Locales.getString("CommonParas", "sUserLogout");  //用户注销
        sLengthCannotExceed = Locales.getString("CommonParas", "sLengthCannotExceed");  //"{0}长度不能超过{1}！"
    }
    //为中英文对照准备的变量
    private static String setupAlarmChanError = "设备报警布防失败";
    private static String setupAlarmChanFail = "布防失败";
    private static String setupAlarmChanSucc = "布防成功";

    private static String closeAlarmChanError = "设备报警撤防失败";
    private static String closeAlarmChanFail = "撤防失败";
    private static String closeAlarmChanSucc = "撤防成功";

    private static String errorLogOfLoginFail = "设备登录失败";
    private static String errorMsgOfLoginFail = "设备登录失败";

    private static String deviceErrorMessage = "错误！\r\n设备：{0}\r\n操作：{1}！\r\n错误代码：{2}；\r\n错误描述：{3}";
    private static String deviceErrorMessageDataBase = "错误！设备：{0}；操作：{1}！ 错误代码：{2}； 错误描述：{3}";//专门为数据库输入而设，格式不同
    private static String noAuthorityMsg = "您不拥有对“{0}”的“{1}”权限";

    private static String passwordGradeRisk = "风险密码";
    private static String passwordGradeWeak = "弱密码";
    private static String passwordGradeMedium = "中密码";
    private static String passwordGradeStrong = "强密码";

//    public static final String NODETYPE_CHANNEL = "Channel";
//    public static final String NODETYPE_ALARMIN = "AlarmIn";
//    public static final String NODETYPE_ALARMOUT = "AlarmOut";
//    public static final String NODETYPE_DISK = "Disk";
    
    private static String errorMsgDialogTitle = "错误";
    private static String remindMsgDialogTitle = "提醒";
    
    
    
    //常用词
    public static String sWeek1 = "星期一";
    public static String sWeek2 = "星期二";
    public static String sWeek3 = "星期三";
    public static String sWeek4 = "星期四";
    public static String sWeek5 = "星期五";
    public static String sWeek6 = "星期六";
    public static String sWeek7 = "星期日";

    public static String sUserLogout = "用户注销";
    public static String sLengthCannotExceed = "{0}长度不能超过{1}！";
    //仅仅为数据库操作而做准备的。
    private static final SimpleDateFormat databaseSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//向数据库中输入datetime类型数据，必须用"yyyy-MM-dd HH:mm:ss.SSS"格式

}
