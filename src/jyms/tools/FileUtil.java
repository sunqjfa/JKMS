/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;

import java.awt.Component;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author John
 */
public class FileUtil {
    public static void saveFile(Component myWindow,String extensions){
        JFileChooser JFileChooser1 = new JFileChooser();//启动一个文件选择器

            FileNameExtensionFilter filter = new FileNameExtensionFilter("配置文件（*.bin）", "bin");
            JFileChooser1.setFileFilter(filter);

            if (JFileChooser.APPROVE_OPTION == JFileChooser1.showSaveDialog(myWindow))//如果文件选择完毕
            {

                String filepath = JFileChooser1.getSelectedFile().getPath();//获取被选择文件的路径
                File file = JFileChooser1.getSelectedFile();
                if (file.exists()){
                    int copy = JOptionPane.showConfirmDialog(null,"是否要覆盖当前文件？", "保存", 
                            JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);     
                    if (copy == JOptionPane.NO_OPTION) return;
                }else filepath = filepath + ".bin";
            }
    }
    
    /**
     * 获得类的基路径，打成jar包也可以正确获得路径
     * @return 
     */
    public static String getBasePath(){
        /*
        /D:/zhao/Documents/NetBeansProjects/docCompare/build/classes/
        /D:/zhao/Documents/NetBeansProjects/docCompare/dist/bundles/docCompare/app/docCompare.jar
        */
        String filePath = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        
        
        if (filePath.endsWith(".jar")){
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
            try {
                filePath = URLDecoder.decode(filePath, "UTF-8"); //解决路径中有空格%20的问题
            } catch (UnsupportedEncodingException ex) {

            }

        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }
    
    
    public static void main(String[] args) throws Exception {
        System.out.println(getBasePath());
    }

}
