/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.data;

/**
 *
 * @author John
 */
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class TxtLogger {
    
   public static void main(String[] args) {
        String content = " Date:" + new Date() + "  |";
        System.out.println(LOG_PATH);
//        for(int i =0;i<10;i++)
//        	content =content+content;
//        append(content);
//        append(content);
    }
	private static final String LOG_PATH = System.getProperty("user.dir")+File.separator+"logs"+File.separator;
	//private static final String LOG_FILE = LOG_PATH + "log.txt";
	//private static final String LINE_SEPARATOR = System.getProperty("line.separator"); 
        
        
    static String strRight(String mids) {
        return mids.substring(mids.length() - 2, mids.length());
    }

    public static void append(String FileName, String FunctionName, String content) {     
        
	SimpleDateFormat sm1 = new SimpleDateFormat("yyyy-MM-dd");
        
        File file = new File(LOG_PATH + sm1.format(new Date())+".log");
        FileWriter fw=null;
        try {
            if (!file.exists())
            {
                File dir = new File(file.getParent());
                if (!dir.exists())
                {
                	dir.mkdirs();
                }
                file.createNewFile();
            }

        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
		String data=sm.format(new Date())+"---"+"Program: " + FileName + ";\r\n                       Function: " + FunctionName 
                                                        + "\r\n                       error: " + content;
			fw=new FileWriter(file,true);//设置为:True,表示写入的时候追加数据
			fw.write(data+"\r\n");
		} catch (IOException e) {
			 e.printStackTrace();
		}
		finally{
			if(fw!=null)
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		if(file.length()>64*1024){
			java.util.Date dd = new java.util.Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");
	        String sdate = sdf.format(dd);
	        File newFile = new File(LOG_PATH + sdate + ".log");
	        customBufferStreamCopy(file,newFile);
	        file.delete();
		}
    }
    
    /*
     * 复制文件
     */
    private static void customBufferStreamCopy(File source, File target) {
        InputStream fis = null;
        OutputStream fos = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(target);
            byte[] buf = new byte[4096];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
        }
    }

}
