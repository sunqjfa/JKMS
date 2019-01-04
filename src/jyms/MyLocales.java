/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import jyms.data.InitDB;
import jyms.data.SysParaCodesBean;
import jyms.data.TxtLogger;

/**
 *
 * @author John
 * 该类只是对相关的窗口和信息进行语言的转换，不负责保存系统当前的语言、国家等参数
 */
public class MyLocales {

    private static final String FILE_NAME = "MyLocales.java";
    private String language_Current = "English";
    private String country_Current  = "Australia";
    private final HashMap<String,String> hmLanguages = new HashMap(); 
    private final HashMap<String,String> hmCountries = new HashMap(); 
    
    private static MyLocales instance = null;
    private Locale locale;
    private ResourceBundle rBundle;//资源包
    private String rBundleName = "";//资源包的名字
    
    private DateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//报警时间的表示形式

    private MyLocales(){
        //语言，常用的ISO-639-1语言代码
        hmLanguages.put("Chinese",      "zh");//
        hmLanguages.put("Danish",       "da");//丹麦语
        hmLanguages.put("Dutch",      	"nl");//荷兰语
        hmLanguages.put("English",      "en");//英语
        hmLanguages.put("French", 	"fr");//法语
        hmLanguages.put("Finnish", 	"fi");//芬兰语
        hmLanguages.put("German", 	"de");//德语
        hmLanguages.put("Greek", 	"el");//希腊语
        hmLanguages.put("Italian", 	"it");//意大利语
        hmLanguages.put("Japanese", 	"ja");//日语
        hmLanguages.put("Korean", 	"ko");//韩文；朝鲜语；韩国语
        hmLanguages.put("Norwegian", 	"no");//挪威语
        hmLanguages.put("Portuguese", 	"Pt");//葡萄牙语
        hmLanguages.put("Spanish", 	"sp");//西班牙语
        hmLanguages.put("Swedish", 	"sv");//瑞典语
        hmLanguages.put("Turkish", 	"tr");//土耳其语
        //国家，表5-2常用的ISO-3166-1国家代码
        hmCountries.put("Austria", 	"AT");//奥地利
        hmCountries.put("Australia", 	"AU");//澳大利亚
        hmCountries.put("Belgium", 	"BE");//比利时
        hmCountries.put("Canada", 	"CA");//加拿大
        hmCountries.put("China", 	"CN");//中国
        hmCountries.put("Denmark", 	"DK");//丹麦
        hmCountries.put("Finland", 	"FI");//芬兰
        hmCountries.put("Germany", 	"DE");//德国
        hmCountries.put("Great Britain", "GB");//英国（大不列颠（包括英格兰、苏格兰和威尔士））
        hmCountries.put("Greece", 	"GR");//希腊
        hmCountries.put("Ireland", 	"IE");//爱尔兰
        hmCountries.put("Italy", 	"IT");//意大利
        hmCountries.put("Japan", 	"JP");//日本
        hmCountries.put("Korea", 	"KR");//韩国
        hmCountries.put("The Netherlands", "NL");//荷兰
        hmCountries.put("Norway", 	"NO");//挪威
        hmCountries.put("Portugal", 	"PT");//葡萄牙
        hmCountries.put("Spain", 	"ES");//西班牙
        hmCountries.put("Sweden", 	"SE");//瑞典
        hmCountries.put("Switzerland", 	"CH");//瑞士
        hmCountries.put("Taiwan", 	"TW");//台湾地区
        hmCountries.put("Turkey", 	"TR");//土耳其
        hmCountries.put("United States", "US");//美国

//        language_Current = CommonParas.SysParas.SYSPARAS_COMMON_LANGUAGE;//SysParaCodesBean.getParaValue(CommonParas.SysParas.SYSPARAS_COMMON_LANGUAGE_CODE, sFileName);
//        country_Current = CommonParas.SysParas.SYSPARAS_COMMON_COUNTRY;//SysParaCodesBean.getParaValue(CommonParas.SysParas.SYSPARAS_COMMON_COUNTRY_CODE, sFileName);
//        locale = new Locale(language_Current);
    }
    
    /*---------------------------------getMyLocales的三种方法最好只选择用一种方式，否则可能会出现混乱-------------------------------*/
    /**
     * @return the myLocales
     */
    public static MyLocales getMyLocales() {
        if(instance==null){
            instance = new MyLocales();
            instance.intialLocale();
        }
        return instance;
    }
    
    /**
     * @param Language
     * @return the myLocales
     */
    public static MyLocales getMyLocales(String Language) {
        if(instance==null){
            instance = new MyLocales();
            instance.setLanguage_Current(Language);
            instance.intialLocale(Language);
        }else{
            if (!instance.language_Current.equals(Language)){
                instance.setLanguage_Current(Language);
                instance.intialLocale(Language);
            }
        }
        return instance;
    }
    
    /**
     * @param Language
     * @param Country
     * @return the myLocales
     */
    public static MyLocales getMyLocales(String Language, String Country) {
        if(instance==null){
            instance = new MyLocales();
            instance.setLanguage_Current(Language);
            instance.setCountry_Current(Country);
            instance.intialLocale(Language, Country);
        }else{
            if (!instance.language_Current.equals(Language) || !instance.country_Current.equals(Country)){
                instance.setLanguage_Current(Language);
                instance.setCountry_Current(Country);
                instance.intialLocale(Language, Country);
            }
        }
        return instance;
    }
    /**
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
        * 函数:      getString
        * 函数描述:  从资源包或它的某个父包中获取给定键的字符串。
        * @param BaseName  资源包的基本名称，是一个完全限定类名 
        * @param Key       所需字符串的键 
        * @return String   此资源包或它的某个父包中获取给定键的字符串。失败或其他错误返回""
    */
    public String getString(String BaseName, String Key){
        if (BaseName == null || BaseName.equals("")) return "";
        if (Key == null      || Key.equals(""))      return "";
        
        setrBundleName( BaseName );
        
        return rBundle.getString( Key );
    }
    /**
        * 函数:      getOperationTime
        * 函数描述:  返回给定日期的字符串形式表示（本地化日期格式)
        * @param CurrentTime 给定日期
        * @return String   返回给定日期的字符串形式表示（本地化日期格式)
    */
    public String getOperationTime(Date CurrentTime){
        return dateFormat.format(CurrentTime);
    }
    /**
        * 函数:      getOperationTime
        * 函数描述:  返回当前日期的字符串形式表示（本地化日期格式)
        * @return String   返回当前日期的字符串形式表示（本地化日期格式)
    */
    public String getOperationTime(){
        return dateFormat.format(new Date());
    }
    /**
     * @return the dateFormat
     */
    public DateFormat getDateTimeFormat() {
        return dateFormat;
    }
    /**
     * @return the dateFormat
     */
    public DateFormat getDateFormat() {
        return DateFormat.getDateInstance(DateFormat.MEDIUM, this.locale);
    }
    /**
     * @return the dateFormat Time Pattern
     */
    public String getDateFormatPattern() {
        return ((SimpleDateFormat)DateFormat.getDateInstance(DateFormat.MEDIUM, this.locale)).toPattern();
    }
    /**
     * @return the dateFormat
     */
    public DateFormat getTimeFormat() {
        return DateFormat.getTimeInstance(DateFormat.MEDIUM, this.locale);
    }
    /**
     * @return the dateFormat Time Pattern
     */
    public String getTimeFormatPattern() {
        return ((SimpleDateFormat)DateFormat.getTimeInstance(DateFormat.MEDIUM, this.locale)).toPattern();
    }
    
    private void intialLocale() {
        this.locale = new Locale(hmLanguages.get(language_Current), hmCountries.get(country_Current));
        if (!LANGUAGE_CHINESE.equals(language_Current))
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, this.locale);
    }
    private void intialLocale(String Language) {
        this.locale = new Locale(hmLanguages.get(Language));
        if (!LANGUAGE_CHINESE.equals(Language))
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, this.locale);
    }
    private void intialLocale(String Language, String Country) {
        this.locale = new Locale(hmLanguages.get(Language), hmCountries.get(Country));
        if (!LANGUAGE_CHINESE.equals(Language))
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, this.locale);
        //System.out.println(((SimpleDateFormat)dateFormat).toPattern());
    }
    /**
     * @param BaseName the rBundleName to set
     */
    private void setrBundleName(String BaseName) {
        try{
            if (!this.rBundleName.equals(BaseName)){
                this.rBundleName = BaseName;
                //如果是读取的文件带有“properties”后缀名，它会从工程根目录下找
                //System.getProperty("user.dir") +"\\config\\resourceBundle.properties"
                rBundle = ResourceBundle.getBundle("jyms.myLocales." + BaseName, locale);
            }
        }catch (Exception e){
            TxtLogger.append(FILE_NAME, "setrBundleName()","系统在获得资源文件过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());
        }
    }
    
    /**
     * @param Language the language_Current to set
     * 设置定制的语言代码
     */
    private void setLanguage_Current(String Language) {
        this.language_Current = Language;
    }
    /**
     * @param Country the country_Current to set
     */
    private void setCountry_Current(String Country) {
        this.country_Current = Country;
    }
    
//    public ResourceBundle getResourceBundle(String baseName){
//        if (rBundle == null ||  !rBundleName.equals(baseName)){
//            setrBundleName(baseName);
//            rBundle = ResourceBundle.getBundle("myLocales." + baseName, locale);
//        }
//        return rBundle;
//    }
    
//    /**
//        * 函数:      getString
//        * 函数描述:  从资源包或它的某个父包中获取给定键的字符串。
//        * @param Language  语言， hmLanguages中的key值
//        * @param BaseName  资源包的基本名称，是一个完全限定类名 
//        * @param Key       所需字符串的键 
//        * @return String   此资源包或它的某个父包中获取给定键的字符串。失败或其他错误返回""
//    */
//    public String getString(String Language, String BaseName, String Key){
//        if (Language == null || Language.equals("")) return "";
//        if (BaseName == null || BaseName.equals("")) return "";
//        if (Key == null      || Key.equals(""))      return "";
//        
//        setLanguage_Current( Language );
//        setrBundleName( BaseName );
//        
//        return rBundle.getString( Key );
//    }

//    /**
//     * @param Language the language_Current to set
//     * 设置定制的语言代码
//     */
//    public void setLanguage_Current(String Language) {
//        try{
//            if (!this.language_Current.equals(Language)){
//                this.language_Current = Language;
//                this.locale = new Locale(hmLanguages.get(Language));
//            }
//        }catch (Exception e){
//            TxtLogger.append(FILE_NAME, "setLanguage_Current()","系统在设置定制的语言代码过程中，出现错误"
//                             + "\r\n                       Exception:" + e.toString());
//        }
//    }
    
    
    
    
    

    
    
    /*-----------------------static方法，修改系统的默认语言和国家参数-------------------------*/
    /**
        * 函数:      modifySysLanguage
        * 函数描述:  修改系统默认的语言（存储在数据库中）
        * @param Language  语言， hmLanguages中的key值
        * @return boolean  成功返回true；失败返回false。
    */
    public static boolean modifySysLanguage(String Language){
        return SysParaCodesBean.updateSysParaValue(CommonParas.SysParas.SYSPARAS_COMMON_LANGUAGE_CODE, Language, FILE_NAME)>0;
    }
    /**
        * 函数:      modifySysLanguageEnglish
        * 函数描述:  修改系统默认的语言为英文（存储在数据库中）
        * @return boolean  成功返回true；失败返回false。
    */
    public static boolean modifySysLanguageEnglish(){
        return modifySysLanguage(LANGUAGE_ENGLISH);
    }
    /**
        * 函数:      modifySysLanguageChinese
        * 函数描述:  修改系统默认的语言为中文（存储在数据库中）
        * @return boolean  成功返回true；失败返回false。
    */
    public static boolean modifySysLanguageChinese(){
        return modifySysLanguage(LANGUAGE_CHINESE);
    }
    
    /**
        * 函数:      modifySysCountry
        * 函数描述:  修改系统默认的国家（存储在数据库中）
        * @param Country   国家名称， hmCountries中的key值
        * @return boolean  成功返回true；失败返回false。
    */
    public static boolean modifySysCountry(String Country){
        return SysParaCodesBean.updateSysParaValue(CommonParas.SysParas.SYSPARAS_COMMON_COUNTRY_CODE, Country, FILE_NAME)>0;
    }
    
    /**
        * 函数:      modifySysLanguageAndCountry
        * 函数描述:  修改系统默认的语言和国家（存储在数据库中）
        * @param Language  语言名称， hmLanguages中的key值
        * @param Country   国家名称， hmCountries中的key值
        * @return boolean  成功返回true；失败返回false。
    */
    public static boolean modifySysLanguageAndCountry(String Language, String Country){
        if (Language == null || Language.equals("")) return false;
        if (Country == null  || Country.equals(""))  return false;
        
        ArrayList<String> ListModifyString = new ArrayList();
        ListModifyString.add(SysParaCodesBean.getStringUpdateCommand(CommonParas.SysParas.SYSPARAS_COMMON_LANGUAGE_CODE, Language));
        ListModifyString.add(SysParaCodesBean.getStringUpdateCommand(CommonParas.SysParas.SYSPARAS_COMMON_COUNTRY_CODE, Country));
        
        //修改数据库中与语言环境有关的数据
        modifyDataForLanguage( Language, ListModifyString);
        
        //return SysParaCodesBean.batchInsertUpdate(ListModifyString, FILE_NAME) > 0;
        return InitDB.getInitDB(FILE_NAME).batchExecuteUpdate(ListModifyString) > 0;
    }
    
    /**
        * 函数:      modifySysEnglishAndAustralia
        * 函数描述:  修改系统默认的语言和国家为英语和澳大利亚（存储在数据库中）
        * @return boolean  成功返回true；失败返回false。
    */
    public static boolean modifySysEnglishAndAustralia(){
        return modifySysLanguageAndCountry(LANGUAGE_ENGLISH,  LANGUAGE_AUSTRALIA);
    }
    
    /**
        * 函数:      modifySysEnglishAndAustralia
        * 函数描述:  修改系统默认的语言和国家为汉语和中国大陆地区（存储在数据库中）
        * @return boolean  成功返回true；失败返回false。
    */
    public static boolean modifySysChineseAndChina(){
        return modifySysLanguageAndCountry(LANGUAGE_CHINESE,  COUNTRY_CHINA);
    }
    
    /**
        * 函数:      modifyDataForLanguage
        * 函数描述:  修改数据库中与语言环境有关的数据
        * @param Language   语言
        * @param ListModifyString    存储SQL语句的ArrayList变量
    */
    private static void modifyDataForLanguage(String Language, ArrayList<String> ListModifyString){
        if (Language.equals(LANGUAGE_CHINESE)) modifyDataForChinese( ListModifyString );
        else if (Language.equals(LANGUAGE_ENGLISH)) modifyDataForEnglish( ListModifyString );
    }
    
    /**
        * 函数:      modifyDataForChinese
        * 函数描述:  修改数据库中与语言环境有关的数据（中文）
        * @param    ListModifyString    存储SQL语句的ArrayList变量
    */
    private static void modifyDataForChinese(ArrayList<String> ListModifyString){
        /*--------------删除“标准权限表”的所有记录，并重新添加中文记录--------------*/
        ListModifyString.add("delete from STANDARDAUTHORITYS");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020100', 'DVR_MANAGE', '0', '设备管理')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020100', 'SYSPARAS_CONFIG', '0', '系统参数配置')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020100', 'WIPER_CTRL', '1', '控制雨刷')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020100', 'FISHBALL_TRACKSETUP', '0', '鱼球联动设置')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020100', 'LANGUAGE_CONVERT', '0', '语言转换')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'RECORDSCHEDULE_CONFIG', '0', '录像计划配置')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'RECORDSCHEDULE_QUICKLYSETUP', '0', '录像快速设置')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'USER_MANAGE', '0', '用户管理')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'ALARMPARAS_SETUP', '0', '报警参数设置')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'ALARM_SETUP', '0', '设备报警布防')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'ALARMOUT_CTRL', '0', '报警输出控制')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'LOG_MANAGE', '0', '日志管理')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'DVR_MAINT', '0', '设备维护')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'CHECKTIME_BATCH', '0', '批量校时')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'FISHBALL_TRACK', '0', '鱼球联动')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'CLIENT_EXIT', '0', '退出客户端')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'DVRGROUP_MANAGE', '0', '设备分组管理')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'PREVIEW', '1', '设备预览')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'PTZCTRL', '1', '云台控制')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'PLAYBACK', '1', '回放远程录像')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'PLAYBACK_DOWNLOAD', '1', '下载远程录像')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'SPEAK', '1', '语音对讲')");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'CHECKTIME', '1', '设备校时')");
        /*--------------删除“代码表”的所有记录，并重新添加中文记录--------------*/
        ListModifyString.add("delete from CODES");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010000', '厂商', NULL, NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010100', '凯康威视', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010200', '大华', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010300', '安迅视', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010400', '松下', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010500', '索尼', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010600', '博世', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010700', 'Vivotec', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010800', '三星', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010900', 'Acti', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('011000', 'Arecont', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('011100', '三洋', '010000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('020000', '用户类型', NULL, NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('020100', '超级管理员', '020000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('020200', '管理员', '020000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('020300', '操作员', '020000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030000', '设备类型', NULL, NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030100', '编码设备/门口机', '030000', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030200', '存储服务器', '030000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030300', '流媒体服务器', '030000', '0')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030400', '解码设备', '030000', '0')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030500', '级联服务器', '030000', '0')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030600', '转码器', '030000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030700', '萤石云设备', '030000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030800', '大屏控制器', '030000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030900', '报警主机', '030000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('031000', '室内机/管理机', '030000', NULL)");
        /*编码设备/门口机分类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030101', '摄像设备', '030100', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030102', '录像机NVR', '030100', '1')");
        /*设备资源类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040000', '设备资源类型', NULL, NULL)");
        /*编码设备资源类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040100', '编码设备资源类型', '040000', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040101', '编码通道', '040100', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040102', '报警输入', '040100', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040103', '防区', '040100', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040104', '报警输出', '040100', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040105', '硬盘', '040100', '1')");
        /*日志类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('050000', '日志类型', NULL, NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('050100', '系统日志', '050000', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('050200', '操作日志', '050000', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('050300', '报警日志', '050000', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('050400', '错误日志', '050000', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070000', '时间模板用途分类', NULL, NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070100', '监控点报警布防', '070000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070200', '异常报警布防', '070000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070300', '报警输入布防', '070000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070400', '报警输出布防', '070000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070500', '录像存储计划', '070000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070600', '抓图计划', '070000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080000', '系统错误代码分类', NULL, NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080100', '网络通讯库错误码', '080000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080200', 'RTSP通讯库错误码', '080000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080300', '软解码库错误码', '080000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080400', '转封装库错误码', '080000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080500', '语音对讲库错误码', '080000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080600', 'Qos流控库错误码', '080000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('200000', '系统参数', NULL, NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('200100', '系统常用参数', '200000', '')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('200200', '报警声音参数', '200000', '1')");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('200300', '图像参数', '200000', NULL)");
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('200400', '文件存储参数', '200000', '1')");
        /*--------------删除“系统参数表”的部分记录，并重新添加英文记录--------------*/
        ListModifyString.add("delete from SYSPARACODES where SYSPARACLASSCODE <> '200100'");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200201', '移动侦测', '', '')");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200202', '报警输入', '', '')");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200203', '设备异常', '', '')");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200204', '视频信号异常', '', '')");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200205', '越界侦测', '', '')");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200206', '区域入侵', '', '')");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200207', '其他报警', '', '')");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200400', '200401', '视频文件保存路径', 'D:\\save\\vedio', '')");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200400', '200402', '抓图文件保存路径', 'D:\\save\\capture', '')");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200400', '200403', '设备配置文件保存路径', 'D:\\save\\setup', '')");
        /*--------------删除“用户权限记录表”、“附加用户权限记录表”、“操作用户表”、“每周时间段模板”、
        “设备时间模板表”、“设备基本参数表”、“设备资源表”、“设备分组表等表的所有记录”--------------*/
        ListModifyString.add("delete from USERAUTHORITYS");
        ListModifyString.add("delete from SUBUSERAUTHORITYS");
        ListModifyString.add("delete from USERS");
        ListModifyString.add("delete from TIMETEMPLATE");
        ListModifyString.add("delete from DEVTIMETEMPLATE");
        ListModifyString.add("delete from DEVICEPARA");
        ListModifyString.add("delete from DEVICERESOURCE");
        ListModifyString.add("delete from DEVICEGROUP");
        /*--------------删除“预览视图表”除VIEW_6、VIEW_8之外的所有记录--------------*/
        ListModifyString.add("delete from VIEWS where VIEWNAME not in ('VIEW_6','VIEW_8')");

    }
    
    
    /**
        * 函数:      modifyDataForEnglish
        * 函数描述:  修改数据库中与语言环境有关的数据（英文）
        * @param    ListModifyString    存储SQL语句的ArrayList变量
    */
    private static void modifyDataForEnglish(ArrayList<String> ListModifyString){

        /*--------------删除“标准权限表”的所有记录，并重新添加中文记录--------------*/
        ListModifyString.add("delete from STANDARDAUTHORITYS");
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020100', 'DVR_MANAGE', '0', 'Device Management')");/*设备管理*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020100', 'SYSPARAS_CONFIG', '0', 'System Parameter Configuration')");/*系统参数配置*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020100', 'WIPER_CTRL', '1', 'WiperControl')");/*控制雨刷*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020100', 'FISHBALL_TRACKSETUP', '0', 'FishEye and Dome Linkage Settings')");/*鱼球联动设置*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020100', 'LANGUAGE_CONVERT', '0', 'Language Conversion')");/*语言转换*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'RECORDSCHEDULE_CONFIG', '0', 'Record Schedule')");/*录像计划配置*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'RECORDSCHEDULE_QUICKLYSETUP', '0', 'Quick Record Schedule')");/*录像快速设置*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'USER_MANAGE', '0', 'Account Management')");/*用户管理*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'ALARMPARAS_SETUP', '0', 'Alarm Parameter Setting')");/*报警参数设置*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'ALARM_SETUP', '0', 'Device Alarm Arming')");/*设备报警布防*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'ALARMOUT_CTRL', '0', 'Alarm Output Control')");/*报警输出控制*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'LOG_MANAGE', '0', 'Log Management')");/*日志管理*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'DVR_MAINT', '0', 'Device Maintenance')");/*设备维护*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'CHECKTIME_BATCH', '0', 'Batch Time Sync')");/*批量校时*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'FISHBALL_TRACK', '0', 'FishEye and Dome Linkage')");/*鱼球联动*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'CLIENT_EXIT', '0', 'Logout')");/*退出客户端*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020200', 'DVRGROUP_MANAGE', '0', 'Device Group Management')");/*设备分组管理*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'PREVIEW', '1', 'Device Preview')");/*设备预览*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'PTZCTRL', '1', 'PTZ Control')");/*云台控制*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'PLAYBACK', '1', 'Remote Playback')");/*回放远程录像*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'PLAYBACK_DOWNLOAD', '1', 'Download Remote Video')");/*下载远程录像*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'SPEAK', '1', 'Voice Intercom')");/*语音对讲*/
        ListModifyString.add("INSERT INTO STANDARDAUTHORITYS (USERTYPE, AUTHORITYITEM, IFSUBDIVISION, REMARKS) VALUES ('020300', 'CHECKTIME', '1', 'Device Time Sync')");/*设备校时*/
        /*--------------删除“代码表”的所有记录，并重新添加中文记录--------------*/
        ListModifyString.add("delete from CODES"); 
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010000', 'Manufacturer', NULL, NULL)"); /*厂商*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010100', 'Hikvision', '010000', NULL)");/*海康威视*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010200', 'Dahua', '010000', NULL)"); /*大华*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010300', 'Axis ', '010000', NULL)"); /*安迅视*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010400', 'Panasonic', '010000', NULL)"); /*松下*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010500', 'Sony', '010000', NULL)"); /*索尼*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010600', 'Bosch', '010000', NULL)"); /*博世*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010700', 'Vivotec', '010000', NULL)"); /*'Vivotec'*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010800', 'Samsung', '010000', NULL)"); /*三星*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('010900', 'Acti', '010000', NULL)"); /*Acti*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('011000', 'Arecont', '010000', NULL)"); /*Arecont*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('011100', 'SANYO', '010000', NULL)"); /*三洋*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('020000', 'User Type', NULL, NULL)"); /*用户类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('020100', 'Super Administrator', '020000', NULL)"); /*超级管理员*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('020200', 'Administrator', '020000', NULL)"); /*管理员*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('020300', 'Operator', '020000', NULL)"); /*操作员*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030000', 'Device Type', NULL, NULL)"); /*设备类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030100', 'Encoding/Outdoor Device', '030000', '1')"); /*编码设备/门口机*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030200', 'Storage Server', '030000', NULL)"); /*存储服务器*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030300', 'Stream Media Server', '030000', '0')"); /*流媒体服务器*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030400', 'Decoding Device', '030000', '0')"); /*解码设备*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030500', 'Cascading Server', '030000', '0')"); /*级联服务器*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030600', 'Transcoder', '030000', NULL)"); /*转码器*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030700', 'EZVIZ Cloud P2P Device', '030000', NULL)"); /*萤石云设备*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030800', 'Video Wall Controller', '030000', NULL)"); /*大屏控制器*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030900', 'Security Control Panel', '030000', NULL)"); /*报警主机*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('031000', 'Indoor Station/Master Station', '030000', NULL)"); /*室内机/管理机*/
        /*编码设备/门口机分类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030101', 'Camera', '030100', '1')"); /*摄像设备 */
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('030102', 'NVR', '030100', '1')"); /*录像机*/
        /*设备资源类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040000', 'Device Resource Type', NULL, NULL)"); /*设备资源类型*/
        /*编码设备资源类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040100', 'Encoding Device Resource Type', '040000', '1')"); /*编码设备资源类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040101', 'Channel', '040100', '1')"); /*EnCoding 编码通道*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040102', 'Alarm Input', '040100', '1')"); /*报警输入*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040103', 'Defence Area', '040100', NULL)"); /*防区*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040104', 'Alarm Output', '040100', '1')"); /*报警输出*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('040105', 'Disk', '040100', '1')"); /*硬盘*/
        /*日志类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('050000', 'Log Type', NULL, NULL)"); /*日志类型*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('050100', 'System Log', '050000', '1')"); /*系统日志*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('050200', 'Operation Log', '050000', '1')"); /*操作日志*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('050300', 'Alarm Log', '050000', '1')"); /*报警日志*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('050400', 'Error Log', '050000', '1')"); /*错误日志*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070000', 'Time Template Use Classification', NULL, NULL)"); /*时间模板用途分类*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070100', 'Monitoring Point Alarm Arming', '070000', NULL)"); /*监控点报警布防*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070200', 'Exception Alarm Arming', '070000', NULL)"); /*异常报警布防*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070300', 'Alarm In Arming', '070000', NULL)"); /*报警输入布防*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070400', 'Alarm Out Arming', '070000', NULL)"); /*报警输出布防*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070500', 'Storage Schedule', '070000', NULL)"); /*录像存储计划*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('070600', 'Capture Schedule', '070000', NULL)"); /*抓图计划*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080000', 'System Error Code Classification', NULL, NULL)"); /*系统错误代码分类*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080100', 'Network communication Library Error Code', '080000', NULL)"); /*网络通讯库错误码*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080200', 'RTSP communication Library Error Code', '080000', NULL)"); /* RTSP通讯库错误码*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080300', 'Soft decoding Library Error Code', '080000', NULL)"); /*软解码库错误码*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080400', 'Transcoded Library Error Code', '080000', NULL)"); /*转封装库错误码*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080500', 'Voice Intercom Library Error Code', '080000', NULL)"); /*语音对讲库错误码*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('080600', 'Qos Flow Control Library Error Code', '080000', NULL)"); /* Qos流控库错误码*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('200000', 'System Parameters', NULL, NULL)"); /*系统参数*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('200100', 'System Common Parameters', '200000', '')"); /*系统常用参数*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('200200', 'Alarm Sound Parameters', '200000', '1')"); /*报警声音参数*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('200300', 'Image Parameters', '200000', NULL)"); /*图像参数*/
        ListModifyString.add("INSERT INTO CODES (CODE, CODENAME, UPPERCODE, REMARKS) VALUES ('200400', 'File Storage Parameters', '200000', '1')"); /*文件存储参数*/
        /*--------------删除“系统参数表”的部分记录，并重新添加英文记录--------------*/
        ListModifyString.add("delete from SYSPARACODES where SYSPARACLASSCODE <> '200100'");
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200201', 'Motion Detection', '', '')"); /*移动侦测*/
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200202', 'Alarm In', '', '')"); /*报警输入*/
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200203', 'Device Exception', '', '')"); /*设备异常*/
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200204', 'Video Exception', '', '')"); /*视频信号异常*/
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200205', 'Cross-Border Detection', '', '')"); /* 越界侦测*/
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200206', 'Intrusion Detection', '', '')"); /*区域入侵 */
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200200', '200207', 'Other Alarm', '', '')"); /*其他报警*/
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200400', '200401', 'The path where the video file is saved', 'D:\\save\\vedio', '')"); /*视频文件保存路径*/
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200400', '200402', 'The path where the capture file is saved', 'D:\\save\\capture', '')"); /*抓图文件保存路径*/
        ListModifyString.add("INSERT INTO SYSPARACODES (SYSPARACLASSCODE, SYSPARACODE, SYSPARANAME, SYSPARAVALUE, REMARKS) VALUES ('200400', '200403', 'The path where the configuration file is saved', 'D:\\save\\setup', '')"); /*设备配置文件保存路径*/

        /*--------------删除“用户权限记录表”、“附加用户权限记录表”、“操作用户表”、“每周时间段模板”、
        “设备时间模板表”、“设备基本参数表”、“设备资源表”、“设备分组表等表的所有记录”--------------*/
        ListModifyString.add("delete from USERAUTHORITYS");
        ListModifyString.add("delete from SUBUSERAUTHORITYS");
        ListModifyString.add("delete from USERS");
        ListModifyString.add("delete from TIMETEMPLATE");
        ListModifyString.add("delete from DEVTIMETEMPLATE");
        ListModifyString.add("delete from DEVICEPARA");
        ListModifyString.add("delete from DEVICERESOURCE");
        ListModifyString.add("delete from DEVICEGROUP");
        /*--------------删除“预览视图表”除VIEW_6、VIEW_8之外的所有记录--------------*/
        ListModifyString.add("delete from VIEWS where VIEWNAME not in ('VIEW_6','VIEW_8')");

    }

    public static final String LANGUAGE_CHINESE = "Chinese";
    public static final String LANGUAGE_ENGLISH = "English";
    
    public static final String COUNTRY_CHINA       = "China";
    public static final String LANGUAGE_AUSTRALIA  = "Australia";//澳大利亚
/*
    表5-1常用的ISO-639-1语言代码
    语 言	代 码
    Chinese	zh
    Danish	da
    Dutch	nl
    English	en
    French	fr
    Finnish	fi
    German	de
    Greek	el
    Italian	it
    Japanese	ja
    Korean	ko
    Norwegian	no
    Portuguese	Pt
    Spanish	sp
    Swedish	sv
    Turkish	tr


    表5-2常用的ISO-3166-1国家代码
    国 家               代 码
    Austria             AT
    Australia           AU 	澳大利亚
    Belgium             BE
    Canada              CA
    China               CN
    Denmark             DK
    Finland             FI
    Germany             DE
    Great Britain	GB
    Greece              GR
    Ireland             IE
    Italy	        IT
    Japan	        JP
    Korea	        KR
    The Netherlands	NL
    Norway	        NO
    Portugal	        PT
    Spain	        ES
    Sweden	        SE
    Switzerland	        CH
    Taiwan	        TW
    Turkey	        TR
    United States	US
*/


}
