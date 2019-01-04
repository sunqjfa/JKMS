/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author John
 */

public class UsersBean {

    private String sUserName;
    private String sUserType;
    private String sPassword;
    private String sPasswordLevel;
    //数据库长度限制，用户名长度不能>16
    public static final int USERNAME_LENGTH = 16;
    private static final String FILE_NAME = "--->>UserBean.java";

    public UsersBean(String UserName,String UserType,String Password,String PasswordLevel){
        this.sUserName = UserName;
        this.sUserType = UserType==null?"":UserType;
        this.sPassword = Password==null?"":Password;
        this.sPasswordLevel = PasswordLevel==null?"":PasswordLevel;
    }

    public UsersBean(String UserName) {
        this.sUserName = UserName;
    }

    public String getUsername() {
        return sUserName;
    }

    public void setUsername(String username) {
        this.sUserName = username;
    }

    public String getUserType() {
        return sUserType;
    }

    public void setUserType(String type) {
        this.sUserType = type;
    }

    public String getPassword() {
        return sPassword;
    }

    public void setPassword(String password) {
        this.sPassword = password;
    }
    
    /**
     * @return the PasswordLevel
     */
    public String getPasswordLevel() {
        return sPasswordLevel;
    }

    /**
     * @param PasswordLevel the PasswordLevel to set
     */
    public void setPasswordLevel(String PasswordLevel) {
        this.sPasswordLevel = PasswordLevel;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sUserName != null ? sUserName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UsersBean)) {
            return false;
        }
        UsersBean other = (UsersBean) object;
        if ((this.sUserName == null && other.sUserName != null) || (this.sUserName != null && !this.sUserName.equals(other.sUserName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jyms.data.Users[ username=" + sUserName + " ]";
    }
    
    /**
	 * 函数:      getUserNameList
         * 函数描述:  获取用户表中的“用户名”列表
         * @param FileName     调用该函数的文件名
         * @return ArrayList     如果出错，返回NULL
    */
    public static ArrayList<String> getUserNameList(String FileName) {
        String sqlQuery = "SELECT USERNAME  FROM USERS where Usertype > '" + jyms.CommonParas.USER_TYPECODE_ADMIN + "'";
        return InitDB.getInitDB(FileName + FILE_NAME).executeQueryOneColList(sqlQuery) ;
    }
    
    /**
	* 用户登录
        * @param UserName
        * @param FileName
        * @return  0，表示登录成功；1，表示数据库连接错误；2访问对象Statement错误；3表示数据查询错误；4用户不存在；5密码错误;6语法错误
     */
    public static UsersBean getUserBean(String UserName,String FileName) {  

        String sqlstr="select USERNAME,USERTYPE,PASSWORD,PasswordLevel from USERS where  USERNAME = '" + UserName + "'";
        ResultSet rs = InitDB.getInitDB(FileName + FILE_NAME).getRs(sqlstr);
        if (rs == null){
            return null;
        }else{
            try{
                if (!rs.next()) return null;
                UsersBean User = new UsersBean(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4));
                return User;
                
             }catch(SQLException e){
                TxtLogger.append(FileName, "UserLogin()", "数据库查询过程中捕捉到SQLException" + "\r\n                       SQL:" + 
                        sqlstr + "\r\n                       SQL Error Code:  " + 
                        String.valueOf(e.getErrorCode()) + "\r\n" + "                       SQL Exception:   " + e.toString());
                return null;
             }
        }
    }

    /**
	* 用户登录
        * @param UserName
        * @param Password
        * @param FileName
        * @return  0，表示登录成功；1，表示数据库连接错误；2访问对象Statement错误；3表示数据查询错误；4用户不存在；5密码错误;6语法错误
     */
    public static int UserLogin(String UserName, String Password, String FileName) {  
  
        InitDB DB = InitDB.getInitDB(FileName + FILE_NAME);
        
        String sqlstr="select USERNAME,PASSWORD from USERS where  USERNAME = '" + UserName + "'";
        ResultSet rs = DB.getRs(sqlstr);
        if (rs == null){
            return DB.getErrorCode();
        }else
        {
            try
            {
                if (!rs.next()) return 4;
                String sPassword2 = rs.getString("PASSWORD");
                if (Password.equals(sPassword2))
                {
                    return 0;
                }else
                {
                    return 5;
                }
             }   
            catch(SQLException e)
            {
                TxtLogger.append(FileName, "UserLogin()", "数据库查询过程中捕捉到SQLException" + "\r\n                       SQL:" + 
                        sqlstr + "\r\n                       SQL Error Code:  " + 
                        String.valueOf(e.getErrorCode()) + "\r\n" + "                       SQL Exception:   " + e.toString());
                return 6;
             }
        }
     }

    /**
	 * 函数:      getUsersList
         * 函数描述:  获取用户名,类型代码,类型名称（代码表中的代码名称），密码,密码级别等用户信息
         * @param FileName     调用该函数的文件名
         * @return ArrayList     
    */
    public static ArrayList getUsersList(String FileName) {
        
        if (FileName == null || FileName.equals("")) return null;
        String sqlQuery = "select USERNAME,USERTYPE,CODES.CODENAME AS TYPENAME,PASSWORD,PASSWORDLEVEL from USERS,CODES where  USERS.Usertype > '" + jyms.CommonParas.USER_TYPECODE_ADMIN + "' and USERS.USERTYPE=CODES.CODE order by code";
        return InitDB.getInitDB(FileName + FILE_NAME).executeQueryList(sqlQuery) ;
    }
    /**
        *函数:      deleteUser
        *函数描述:  删除用户所有的数据(用户表、用户权限表、附加权限表)
        * @param UserName	 用户名
        * @param FileName       调用该函数的文件名
        * @return int：成功返回true；失败，false.
     */
    public static boolean deleteUser(String UserName,String FileName){
        InitDB DB = InitDB.getInitDB(FileName + FILE_NAME);
        
        DB.setMyAutoCommit(false);
        String sql3 = "delete  from Users where UserName ='" + UserName + "'";
        
        int iCount3 = DB.executeUpdate(sql3,false);
        if (iCount3 < 1) {
            DB.myRollback();

            DB.setMyAutoCommit(true);
            return false;
        }
        
        String sql2 = "delete  from USERAUTHORITYS  where UserName ='" + UserName + "'";
        
        int iCount2 = DB.executeUpdate(sql2,false);
        if (iCount2 < 0) {
            DB.myRollback();

            DB.setMyAutoCommit(true);
            return false;
        }
        
        String sql = "delete from SUBUSERAUTHORITYS  where UserName ='" + UserName + "'";
        int iCount1 = DB.executeUpdate(sql,false);
        if (iCount1 > 0){
            DB.myCommit();
            DB.setMyAutoCommit(true);
            return true;
        }else if (iCount1 == 0 && iCount2 == 0){//对应只有用户但没有权限的情况
            DB.myCommit();
            DB.setMyAutoCommit(true);
            return true;
        }
        else{
            DB.myRollback();
            DB.setMyAutoCommit(true);
            return false;
        }
    }
    /**
        *函数:      insertUser
        *函数描述:  插入一条用户记录
        * @param Bean	 UsersBean
        * @param FileName       调用该函数的文件名
        * @return int：成功返回插入的行数1；失败，返回0；出错返回-1.
     */
    public static int insertUser(UsersBean Bean, String FileName){
        if (Bean == null) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        
        String SqlInsert = "INSERT INTO USERS (USERNAME, USERTYPE, PASSWORD, PASSWORDLEVEL) VALUES ('"
                    + Bean.getUsername()+ "', '"  + Bean.getUserType()+ "', '"  
                    + Bean.getPassword()+ "', '"  + Bean.getPasswordLevel()+ "')";
     
        return InitDB.getInitDB(FileName + FILE_NAME).executeUpdate(SqlInsert);
    }
    /**
        *函数:      modifyUserParas
        *函数描述:  修改用户资料
        * @param UserName	 用户名
        * @param Password	 用户密码
     * @param UserType
        * @param FileName       调用该函数的文件名
        * @return int：成功返回插入的行数1；失败，返回0；出错返回-1.
     */
    public static int modifyUserParas(String UserName,String Password,String UserType,String FileName){
        if (UserName == null) return -1;
        if (Password == null && UserType == null) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        String Sql = "";
        if (Password != null && UserType == null){
            Sql = "update Users set Password = '" + Password + "' where UserName ='" + UserName + "'";
        }
        else if (Password != null && UserType != null){
            Sql = "update Users set UserType = '" + UserType + "', Password = '" + Password + "' and  where UserName ='" + UserName + "'";
        }
        else  if (Password == null && UserType != null){
            Sql = "update Users set UserType = '" + UserType + "' where UserName ='" + UserName + "'";
        }
        else return -1;
        
        return InitDB.getInitDB(FileName + FILE_NAME).executeUpdate(Sql);
    }
    
    /**
        *函数:      modifyUserParas
        *函数描述:  修改用户资料
        * @param usersBean
        * @param FileName       调用该函数的文件名
        * @return int：成功返回插入的行数1；失败，返回0；出错返回-1.
     */
    public static int modifyUserParas(UsersBean usersBean,String FileName){
        if (usersBean.getUserType() == null) return -1;
        //if (Password == null && UserType == null) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        String Sql = "UPDATE USERS SET USERTYPE = '"+usersBean.getUserType()
                    +"', PASSWORD = '"+usersBean.getPassword()
                    +"', PASSWORDLEVEL = '"+usersBean.getPasswordLevel()
                    +"' WHERE USERNAME = '"+usersBean.getUsername()+"'";
        
        return InitDB.getInitDB(FileName + FILE_NAME).executeUpdate(Sql);
    }
    
    /**
        *函数:      checkPasswordIfOK
        *函数描述:  验证用户输入密码是否正确
        * @param UserName
        * @param Password
        * @param FileName       调用该函数的文件名
        * @return int：正确返回1；错误，返回0；其他错误返回-1.
     */
    public static int checkPasswordIfOK(String UserName,String Password,String FileName){
        if (UserName == null && UserName.equals("")) return -1;
        if (Password == null && Password.equals("")) return -1;
        String sql = "select count(*) from Users where username = '"+UserName+"' and password ='"+Password+"'";
        return InitDB.getInitDB(FileName + FILE_NAME).getNums(sql);
    }
    
    /**
        *函数:      modifyUserParas
        *函数描述:  修改用户资料
        * @param UserName	 用户名
        * @param PasswordOld    旧密码
        * @param Password	 用户密码
        * @param PasswordLevel
        * @param FileName       调用该函数的文件名
        * @return int：成功返回1；失败，返回0；出错返回-1；旧密码错误，返回-2；
     */
    public static int modifyPassword(String UserName, String PasswordOld, String Password, String PasswordLevel, String FileName){
        if (UserName == null && UserName.equals("")) return -1;
        if (Password == null && Password.equals("")) return -1;
        if (FileName == null || FileName.equals("")) return -1;
        if (PasswordOld == null && PasswordOld.equals("")) return -1;
        
        if (checkPasswordIfOK(UserName,PasswordOld,FileName) < 1) return -2;
        
        String Sql = "UPDATE USERS SET PASSWORD = '" + Password 
                   + "', PASSWORDLEVEL = '" + PasswordLevel + "' WHERE USERNAME = '" + UserName + "'";
       
        return InitDB.getInitDB(FileName + FILE_NAME).executeUpdate(Sql);
    }
    
    /**
        *函数:      modifyUserParas
        *函数描述:  获取修改用户类型的SQL字符串
        * @param UserName	 用户名
        * @param UserType
        * @return String：修改用户类型SQL字符串；
     */
    public static String getStringUpdateUserType(String UserName, String UserType){
        String UpdateCommand  = "UPDATE USERS SET USERTYPE = '" + UserType + "' WHERE USERNAME = '" + UserName+ "'";
        return UpdateCommand;
    }
    
    /**
        *函数:      getStringUpdatePassword
        *函数描述:  获取修改用户密码的SQL字符串
        * @param UserName	 用户名
        * @param Password
        * @param PasswordLevel
        * @return String：修改用户类型SQL字符串；
     */
    public static String getStringUpdatePassword(String UserName, String Password, String PasswordLevel){
        String UpdateCommand  = "UPDATE USERS SET PASSWORD = '" + Password 
                            + "', PASSWORDLEVEL = '" + PasswordLevel + "' WHERE USERNAME = '" + UserName + "'";
        return UpdateCommand;
    }
    
    /**
        *函数:      getStringUpdateUserParas
        *函数描述:  获取修改用户资料的SQL字符串
        * @param UserName	 用户名
        * @param UserType
        * @param Password
        * @param PasswordLevel
        * @return String：修改用户类型SQL字符串；
     */
    public static String getStringUpdateUserParas(String UserName, String UserType, String Password, String PasswordLevel){
        String UpdateCommand  = "UPDATE USERS SET USERTYPE = '" + UserType + "', PASSWORD = '" + Password 
                            + "', PASSWORDLEVEL = '" + PasswordLevel + "' WHERE USERNAME = '" + UserName + "'";
        return UpdateCommand;
    }
    
}
