package jyms.data;

import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class InitDB {

    private String DBDriver = null;//JDBC访问驱动程序类名
    private String url = null;//数据库连接URL
    private String user = null;//数据库用户名
    private String password = null;//数据库密码
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private static InitDB initDB_obj = null ;
    private String sFileName = "";
    private int iErrorCode = 0;//数据库操作错误码：1数据库连接conn错误；2stmt错误；3rs错误

    /**
        * 构造函数，需要传参数的公用访问//备用
        * @param FileName	调用该类的文件名
     */
    private InitDB(String FileName) { // Java DB网络模式的访问
//        this("org.apache.derby.jdbc.ClientDriver","jdbc:derby://localhost:1527/jyms;create=true","app","app123456",FileName);
        
//        this("org.apache.derby.jdbc.ClientDriver","jdbc:derby:D:\\dist\\jyms;","app","app123456",FileName);
        this("org.apache.derby.jdbc.ClientDriver","","app","app123456",FileName);
    }
    
    /**
        * 构造函数，需要传参数的公用访问//备用
        * @param DBDriver     JDBC访问驱动程序类名 (嵌入式数据库模式org.apache.derby.jdbc.EmbeddedDriver)
        * @param url		连接URL (嵌入式数据库模式jdbc:derby:jyms;create=true)
        * @param user         数据库用户名
        * @param password	数据库密码
        * @param FileName	调用该类的文件名
     */
    private InitDB(String DBDriver,String url, String user,String password,String FileName) {
        this.DBDriver = DBDriver ;
        this.url = url ;
        this.user = user ;
        this.password = password;
        this.sFileName = FileName + "--->>InitDB.java";
        try {
            Class.forName(DBDriver); // 加载驱动
            if (url == null || url.equals("")) url=getDatabaseUrl();
            conn = DriverManager.getConnection( url, user, password);//建立连接
            stmt = conn.createStatement(); // 获取访问对象
        }catch (ClassNotFoundException e) {
            TxtLogger.append(this.sFileName ,"InitDB()","连接数据库过程中捕捉到ClassNotFoundException" + "\r\n" + "                       ClassNotFoundException:   " + e.toString());
        }catch (ExceptionInInitializerError e) {
            TxtLogger.append(this.sFileName, "InitDB()","连接数据库过程中捕捉到ExceptionInInitializerError" + "\r\n                       ExceptionInInitializerError:   " + e.toString());
        }catch (LinkageError e) {
            TxtLogger.append(this.sFileName, "InitDB()","连接数据库过程中捕捉到LinkageError" + "\r\n                       LinkageError:   " + e.toString());
        }catch (SQLException e) {
            TxtLogger.append(this.sFileName, "InitDB()","连接数据库过程中捕捉到SQLException" + "\r\n                       SQL Error Code:  " + 
                        String.valueOf(e.getErrorCode()) + "\r\n" + "                       SQL Exception:   " + e.toString());
        }catch (NullPointerException e) {
             TxtLogger.append(this.sFileName, "InitDB()","连接数据库过程中捕捉到NullPointerException" + "\r\n" + "                       NullPointerException:   " + e.toString());
        }catch (Exception e) {
             TxtLogger.append(this.sFileName, "InitDB()","连接数据库过程中捕捉到Exception" + "\r\n" + "                       Exception:   " + e.toString());
        }
    }
    
    public static InitDB getInitDB(String FileName) {
        if(initDB_obj==null)  initDB_obj = new InitDB(FileName);
        initDB_obj.sFileName = FileName + "--->>InitDB.java";
        return initDB_obj ;
    }

    private String getDatabaseUrl(){
        String DatabaseUrl = System.getProperty("user.dir");
        return "jdbc:derby:"+DatabaseUrl+"\\jyms;"; //标准格式："jdbc:derby:D:\\dist\\jyms;"、"jdbc:derby://localhost:1527/jyms;create=true"、"jdbc:derby:D:\\JKMS\\dist\\jyms";
//        return "jdbc:derby:D:\\JKMS\\dist\\jyms";
    }
    
    public Connection getConn() {
        return conn;
    }

    public Statement getStmt() {
        return stmt;
    }

    public ResultSet getRs(String sql) { // 获取查询结果集
        
        if (conn == null || stmt == null) return null;
        if (sql.toLowerCase().contains("select")) {
            try {
                rs = stmt.executeQuery(sql);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
//                e.printStackTrace();
                TxtLogger.append(this.sFileName, "getRs()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL:"+ sql 
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
            }
        }
        return rs;
    }
    
    /**
        *函数:      executeQueryOneCol
        *函数描述:  执行对数据库的查询操作，返回查询结果String
        * @param sqlQuery	 sql操作语句
        * @return String： 返回查询结果；失败，返回null.
     */
    public String executeQueryOneCol(String sqlQuery){
    
        if (conn == null || stmt == null) return null;
        if (!sqlQuery.toLowerCase().contains("select")) return null;
        String sReturn = null;
        try {
            
            ResultSet rs2 = getRs(sqlQuery) ;
            if (rs2 == null) return null;
           
            if (rs2.next()) {
                String ss = rs2.getString(1);
                sReturn = ss==null?"":ss;
            }
        } catch (SQLException e) {
            TxtLogger.append(this.sFileName, "executeQueryOneCol()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(this.sFileName, "executeQueryOneCol()","数据查询过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return sReturn;
    }
    /**
        *函数:      executeQueryOneColList
        *函数描述:  执行对数据库的查询操作，并将结果放入ArrayList<>中
        * @param sqlQuery	 sql操作语句
        * @return ArrayList： 单层ArrayList返回查询结果；失败，返回null.
     */
    public ArrayList<String> executeQueryOneColList(String sqlQuery){
    
        if (conn == null || stmt == null) return null;
        if (!sqlQuery.toLowerCase().contains("select")) return null;
        
        ArrayList<String> listOneCol = new ArrayList<String>();
        try {
            
            ResultSet rs2 = getRs(sqlQuery) ;
            if (rs2 == null) return null;
           
            while(rs2.next()) {
                String tempStr = rs2.getString(1);
                if (tempStr==null) tempStr="";
                listOneCol.add(tempStr);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(this.sFileName, "executeQueryOneColList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(this.sFileName, "executeQueryOneColList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(this.sFileName, "executeQueryOneColList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(this.sFileName, "executeQueryOneColList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(this.sFileName, "executeQueryOneColList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(this.sFileName, "executeQueryOneColList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(this.sFileName, "executeQueryOneColList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listOneCol;
    }

    /**
        *函数:      executeQueryList
        *函数描述:  执行对数据库的查询操作，并将结果放入ArrayList<>中
        * @param sqlQuery	 sql操作语句
        * @return ArrayList： 返回查询结果双层ArrayList<>；失败，返回null.
     */
    public ArrayList executeQueryList(String sqlQuery)
    {
        if (conn == null || stmt == null) return null;
        if (!sqlQuery.toLowerCase().contains("select")) return null;
        ArrayList listRS = new ArrayList();

        try {
            
            ResultSet rs2 = getRs(sqlQuery) ;
            if (rs2 == null) return null;
            ResultSetMetaData rsmd=rs2.getMetaData();
            int numCols =rsmd.getColumnCount();
            
            while(rs2.next()) {
                ArrayList columns = new ArrayList();
                for (int i=1;i<=numCols;i++){
                        String tempStr=rs2.getString(i);
                        if (tempStr==null) tempStr="";
                        tempStr=tempStr.trim();
                        columns.add(tempStr);
                }
                listRS.add(columns);
            }
        } catch (UnsupportedOperationException e){
            TxtLogger.append(this.sFileName, "executeQueryList()","在添加对象到ArrayList过程中，此 collection 不支持 add 操作，出现错误" + 
                        "\r\n                       UnsupportedOperationException:" + e.toString());  
        }
        catch (ClassCastException e){
            TxtLogger.append(this.sFileName, "executeQueryList()","在添加对象到ArrayList过程中，指定元素的类不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       ClassCastException:" + e.toString());  
        }catch (NullPointerException e){
            TxtLogger.append(this.sFileName, "executeQueryList()","在添加对象到ArrayList过程中，指定的元素为 null，并且此 collection 不允许 null 元素，出现错误" + 
                         "\r\n                       NullPointerException:" + e.toString());  
        }catch (IllegalArgumentException e){
            TxtLogger.append(this.sFileName, "executeQueryList()","在添加对象到ArrayList过程中，元素的某属性不允许它添加到此 collection 中，出现错误" + 
                         "\r\n                       IllegalArgumentException:" + e.toString());  
        }catch (IllegalStateException e){
            TxtLogger.append(this.sFileName, "executeQueryList()","在添加对象到ArrayList过程中，由于插入限制，元素不能在此时间添加，出现错误" + 
                         "\r\n                       IllegalStateException:" + e.toString());  
        }catch (SQLException e) {
            TxtLogger.append(this.sFileName, "executeQueryList()","用户在数据查询过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
        } catch (Exception e){
            TxtLogger.append(this.sFileName, "executeQueryList()","数据查询并添加对象到ArrayList过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return listRS;
    }

    
    /**
        *函数:      getNums
        *函数描述:  数据库查询是否存在该分组的数据
        * @param sqlQuery	 查询语句。仅仅为了查询select count(*)等语句，查询数据表中的行数
        * @return int：存在返回存在的个数；不存在返回0；失败或参数错误或其他错误，返回-1
    */
    public int getNums(String sqlQuery){
        try {
            if (sqlQuery == null || sqlQuery.equals("")) return -1;
            if (!sqlQuery.toLowerCase().contains("select")) return -1;
            
            ResultSet rs2 = getRs(sqlQuery);
            if (rs == null) return -1;
            if (!rs.next()) return 0;
            else return rs.getInt(1);
        } catch (SQLException e) {
            TxtLogger.append(this.sFileName, "getNums()","用户在进行数据查询是否存在需要的数据的过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlQuery
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
            return -1;
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "getNums()","用户在进行数据查询是否存在需要的数据的的过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
            return -1;
        }
    }
    //执行对数据库的更改操作
    /**
        *函数:      executeUpdate
        *函数描述:  执行对数据库的更改操作
        * @param sqlUpdate	 sql操作语句
        * @param IfAutoCommit       是否自动提交
        * @return int： 返回操作影响的行数；未成功，返回-1.
     */
    public int executeUpdate(String sqlUpdate,boolean IfAutoCommit)
    {
        int count = -1;    
        try{
//            sqlUpdate = new String(sqlUpdate.getBytes("ISO-8859-1"),"GBK");									
            count = stmt.executeUpdate(sqlUpdate);

            if (IfAutoCommit) {
                if (count > -1)
                {
                    if (getIfAutoCommit() == 0) conn.commit();
                }   
            }

        }catch (SQLException e){
            TxtLogger.append(this.sFileName, "executeUpdate()","用户在数据更新过程中捕捉到SQLException" 
                        + "\r\n                       SQL:"+ sqlUpdate 
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
            count = -1;    
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "executeUpdate()","用户在数据更新过程中捕捉到Exception" 
                        + "\r\n                       SQL:"+ sqlUpdate 
                        + "\r\n                       Exception:   " + e.toString() + "\r\n");
            count = -1;    
        }

        return count;
    }
    
    //执行对数据库的更改操作
    public int executeUpdate(String sqlUpdate)
    {
        return executeUpdate(sqlUpdate, true);
    }
    
    public int batchExecuteUpdate(ArrayList<String> ListUpdateStr){
        
        try {
            if (ListUpdateStr == null) return 0;
            if (ListUpdateStr.isEmpty()) return 0;
            setMyAutoCommit(false);
            
            for (int i=0;i<ListUpdateStr.size();i++){

                stmt.addBatch(ListUpdateStr.get(i));
            }
            int[] Count = stmt.executeBatch();  //(sqlUpdate,false);大于等于 0 的数 - 指示成功处理了命令，是给出执行命令所影响数据库中行数的更新计数 
            
            for (int i=0;i<Count.length;i++){
                /*  1、大于等于 0 的数 - 指示成功处理了命令，是给出执行命令所影响数据库中行数的更新计数 
                    2、SUCCESS_NO_INFO 的值 - 指示成功执行了命令，但受影响的行数是未知的 
                    3、EXECUTE_FAILED 的值 - 指示未能成功执行命令，仅当命令失败后驱动程序继续处理命令时出现*/
                if (Count[i] < 0 && (Count[i]!=Statement.SUCCESS_NO_INFO)) {
                    myRollback();
                    setMyAutoCommit(true);
                    return 0;
                }
            }
            myCommit();
            setMyAutoCommit(true);
            return 1;
            
        } catch (BatchUpdateException e){
            TxtLogger.append(this.sFileName, "batchExecuteUpdate()","用户在数据更新过程中捕捉到SQLException"
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
   
        }catch (SQLException e){
            TxtLogger.append(this.sFileName, "batchExecuteUpdate()","用户在数据更新过程中捕捉到SQLException"
                        + "\r\n                       SQL Error Code:  " + String.valueOf(e.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + e.toString() + "\r\n");
     
        }catch (Exception e){
            TxtLogger.append(this.sFileName, "batchExecuteUpdate()","用户在数据更新过程中捕捉到Exception" 
                        + "\r\n                       Exception:   " + e.toString() + "\r\n");
    
        }
        myRollback();
        setMyAutoCommit(true);
        return -1;
    }
    
    public void closeDB() { // 关闭连接等，释放资源
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            TxtLogger.append(this.sFileName, "closeDB()","用户在断开数据库连接过程中捕捉到SQLException:   " + e.toString());
        }
    }
    
    /**
	 * 返回数据库连接是否自动提交Commit
         * @return int整数。0，非自动提交；1，自动提交；2，出现异常
     */
    public int getIfAutoCommit(){
        try {
            if (conn.getAutoCommit()) return 1;
            else return 0;
        } catch (SQLException e) {
            TxtLogger.append(this.sFileName, "getIfAutoCommit()","获取 Connection 对象的当前自动提交模式过程中捕捉到SQLException" + "\r\n                       SQL Error Code:  " + 
                        String.valueOf(e.getErrorCode()) + "\r\n" + "                       SQL Exception:   " + e.toString());
            return 2;
        }catch (Exception e) {
            TxtLogger.append(this.sFileName, "getIfAutoCommit()","获取 Connection 对象的当前自动提交模式过程中捕捉到Exception" + "\r\n" + "                       Exception:   " + e.toString());
            return 2;
        }

    }
    
    /**
	 * 设置数据库连接是否自动提交Commit,将此连接的自动提交模式设置为给定状态。
         * @param autoCommit - 为 true 表示启用自动提交模式；为 false 表示禁用自动提交模式
    */
    public void setMyAutoCommit(boolean autoCommit){
        try {
            conn.setAutoCommit(autoCommit);
          } catch (SQLException e) {
            TxtLogger.append(this.sFileName, "setMyAutoCommit()","设置 Connection 对象的当前自动提交模式过程中捕捉到SQLException" + "\r\n                       SQL Error Code:  " + 
                        String.valueOf(e.getErrorCode()) + "\r\n" + "                       SQL Exception:   " + e.toString());

        }catch (Exception e) {
            TxtLogger.append(this.sFileName, "setMyAutoCommit()","设置 Connection 对象的当前自动提交模式过程中捕捉到Exception" + "\r\n" + "                       Exception:   " + e.toString());
        }
    }
    
    /**
	 * 使所有上一次提交/回滚后进行的更改成为持久更改，并释放此 Connection 对象当前持有的所有数据库锁。此方法只应该在已禁用自动提交模式时使用。 
     */
    public void myCommit(){
        try {
            conn.commit();
          } catch (SQLException e) {
            TxtLogger.append(this.sFileName, "myCommit()","取消在当前事务中进行的所有更改的过程中捕捉到SQLException" + "\r\n                       SQL Error Code:  " + 
                        String.valueOf(e.getErrorCode()) + "\r\n" + "                       SQL Exception:   " + e.toString());

        }catch (Exception e) {
            TxtLogger.append(this.sFileName, "myCommit()","取消在当前事务中进行的所有更改的过程中捕捉到Exception" + "\r\n" + "                       Exception:   " + e.toString());
        }
    }
    /**
	 * 取消在当前事务中进行的所有更改，并释放此 Connection 对象当前持有的所有数据库锁。此方法只应该在已禁用自动提交模式时使用。 
     */
    public void myRollback(){
        try {
            conn.rollback();
          } catch (SQLException e) {
            TxtLogger.append(this.sFileName, "myRollback()","取消在当前事务中进行的所有更改的过程中捕捉到SQLException" + "\r\n                       SQL Error Code:  " + 
                        String.valueOf(e.getErrorCode()) + "\r\n" + "                       SQL Exception:   " + e.toString());

        }catch (Exception e) {
            TxtLogger.append(this.sFileName, "myRollback()","取消在当前事务中进行的所有更改的过程中捕捉到Exception" + "\r\n" + "                       Exception:   " + e.toString());
        }
    }

    /**
        * 函数:      backUpDatabase
        * 函数描述:  备份数据库
        * @return   成功返回true；失败返回false
    */
    public boolean backUpDatabase(){
        String sqlstmt = "CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)";
        try {
            CallableStatement cstmt = conn.prepareCall(sqlstmt);
            cstmt.setString(1,"D:/dbbackups/");
            boolean BackupSucc = cstmt.execute();
            cstmt.close();
            return BackupSucc;
        } catch (SQLException ex) {
            TxtLogger.append(this.sFileName, "executeQueryList()","用户在数据库备份过程中捕捉到SQLException" + "\r\n                       SQL：" + sqlstmt
                        + "\r\n                       SQL Error Code:  " + String.valueOf(ex.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + ex.toString() + "\r\n");
        }
        return false;
    }
    
    
    private boolean restoreDatabase(){
        String dbURL = "jdbc:derby:"+System.getProperty("user.dir")+"\\jyms;restoreFrom=D:/dbbackups/jyms";
        try {
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement(); // 获取访问对象
            return true;
        } catch (SQLException ex) {
            TxtLogger.append(this.sFileName, "executeQueryList()","用户在数据库恢复过程中捕捉到SQLException" + "\r\n                       SQL：" + dbURL
                        + "\r\n                       SQL Error Code:  " + String.valueOf(ex.getErrorCode()) 
                        + "\r\n                       SQL Exception:   " + ex.toString() + "\r\n");
        }
        return false;
    
    }
    /**
        * 返回该类的错误代码
        * @return int整数。0，无错误；1，数据库连接问题；2，Statement异常；3，ResultSet异常
    */
    public static int getErrorCode(){
        if (initDB_obj.conn == null) return 1;
        if (initDB_obj.stmt == null) return 2;
        if (initDB_obj.rs == null) return 3;
        return 0;
    }
}
