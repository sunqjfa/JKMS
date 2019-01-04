/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;


import javax.swing.JFrame;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import jyms.data.TxtLogger;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
/**
 * @用法说明：
 *          1、直接调用showMessage(JRootPane RootPane, final String Message, final String FileName)
 * @author John
 */
public class MessageGlassPane extends JPanel {
    private static  String sFileName = "--->> MessageGlassPane.java";
    private static JLabel statusLabel = new JLabel();
    private static final int WIDTH_OF_STATUSLABEL = 200;//statusLabel的宽度
    private JRootPane myRootPane;
    private static MessageGlassPane myMessageGlassPane = null;
    private Point myPoint = null;
    
    private MessageGlassPane() {
        
        this.setLayout(new AbsoluteLayout());
        add(statusLabel, new AbsoluteConstraints(200, 222, 200, 200));//x,y,width,height
        // Transparent透明
        setOpaque(false);
        statusLabel.setOpaque(true);
    }
    
    
    private static void setStatusText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try{
                    statusLabel.setOpaque(true);
                    StringBuilder builder = new StringBuilder("<html>");
                    FontMetrics fontMetrics = statusLabel.getFontMetrics(statusLabel.getFont());
                    String[] splitText = text.split("\r\n");
                    for (String splitText1 : splitText) {
                        char[] chars = splitText1.toCharArray();
                    
                        for (int beginIndex = 0, limit = 1;; limit++) {
                            //System.out.println(beginIndex + " " + limit + " " + (beginIndex + limit));
                            if (fontMetrics.charsWidth(chars, beginIndex, limit) < WIDTH_OF_STATUSLABEL) {
                                if (beginIndex + limit < chars.length) {
                                    continue;
                                }
                                builder.append(chars, beginIndex, limit);
                                break;
                            }
                            builder.append(chars, beginIndex, limit - 1).append("<br>");
                            beginIndex += limit - 1;
                            limit = 0;//原先为1，因为循环到for时，先加1，直接从2开始了，所以要改为0
                        }
                        builder.append("<br>");
                    }
                    
                    builder.append("</html>");
                    statusLabel.setText(builder.toString());
                    //statusLabel.setText(text);
                } catch (Exception ex) {
                    TxtLogger.append(sFileName, "setStatusText()","系统设置消息文本的过程中，出现错误"
                             + "\r\n                       Exception:" + ex.toString());
                }
            }
        });
    }
    
    
    public static MessageGlassPane getMessageGlassPane(){
    
        if(myMessageGlassPane==null)  myMessageGlassPane = new MessageGlassPane();
        //initDB_obj.sFileName = FileName + "--->>InitDB.java";JRootPane
        return myMessageGlassPane ;
    }
    
    public static void showMessage(JRootPane RootPane, final String Message, final String FileName) {
        RootPane.setGlassPane(getMessageGlassPane());
        myMessageGlassPane.myRootPane = RootPane;
//        this.remove(statusLabel);
//        statusLabel  = new JLabel();
//
//        statusLabel.setForeground(Color.BLUE);//设置文本颜色
//        statusLabel.setFont(new java.awt.Font("宋体", 1, 15)); // NOI18N
//        //statusLabel.setOpaque(false);// Transparent透明
//        myMessageGlassPane.setStatusText("<html> <p>" + Message + "</p></html>");
////        statusLabelX = RootPane.getWidth() - widthOfstatusLabel - 15;
//        statusLabelX = 15;
//        statusLabelY = myRootPane.getHeight() - 340;
//        add(statusLabel, new AbsoluteConstraints(statusLabelX, statusLabelY));//x,y,width,height

        showMessage(Message, FileName);
    }
    
    public static void showMessage(JWindow frame, final String Message, final String FileName) {
        showMessage(frame.getRootPane(),Message,FileName);
    }
    public static void showMessage(JInternalFrame frame, final String Message, final String FileName) {
        showMessage(frame.getRootPane(),Message,FileName);
    }
    public static void showMessage(JDialog frame, final String Message, final String FileName) {
        showMessage(frame.getRootPane(),Message,FileName);
    }
    public static void showMessage(JFrame frame, final String Message, final String FileName) {
        showMessage(frame.getRootPane(),Message,FileName);
    }
    
    private static void showMessage(final String Message, final String FileName) {

        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    myMessageGlassPane.remove(statusLabel);
                    statusLabel  = new JLabel();

                    statusLabel.setForeground(Color.RED);//设置文本颜色
                    statusLabel.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 15)); // NOI18N
                    //statusLabel.setOpaque(false);// Transparent透明
                    //myMessageGlassPane.setStatusText("<html> <p>" + Message + "</p></html>");
                    myMessageGlassPane.setStatusText(Message);
            //        int statusLabelX = RootPane.getWidth() - widthOfstatusLabel - 15;
                    int statusLabelX = 15;//JLabel的位置X
                    int statusLabelY = myMessageGlassPane.myRootPane.getHeight() - 200;//JLabel的位置Y
//                    if (myMessageGlassPane.myPoint != null){
//                        statusLabelX = myMessageGlassPane.myPoint.x;//JLabel的位置X
//                        statusLabelY = myMessageGlassPane.myPoint.y;//JLabel的位置Y
//                    }
                    
                    myMessageGlassPane.add(statusLabel, new AbsoluteConstraints(statusLabelX, statusLabelY));//x,y,width,height
                    
                    myMessageGlassPane.setVisible (true);
                    
                    //myMessageGlassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    // TODO Long time operation here
                    Thread.sleep(5000);//显示5秒时间
                    //myMessageGlassPane.setCursor(new Cursor (Cursor.DEFAULT_CURSOR));
                    myMessageGlassPane.setVisible(false);
                } catch (Exception ex) {
                    TxtLogger.append(FileName + sFileName, "ifHaveUserAuthority()","系统查询用户是否拥有权限的过程中，出现错误"
                             + "\r\n                       Exception:" + ex.toString());
                }
            } 
        };
        th.start();
        
    }
    public void setPoint(Point point) {
		this.myPoint = point;
	}
    
    /**
    * Install this to the jframe. as glass pane.
     * @param RootPane
    */
//    public void installAsGlassPane(JRootPane RootPane) {
//        RootPane.setGlassPane(myMessageGlassPane);
//        myMessageGlassPane.myRootPane = RootPane;
//    }
    /**
    * A small demo code of how to use this glasspane.
    * @param args
    */
    public static void main(String[] args) {
        final JFrame frame = new JFrame("Test GlassPane");
        frame.setBackground(Color.BLUE);
        frame.setForeground(Color.red);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        final MessageGlassPane glassPane = MessageGlassPane.getMessageGlassPane();

        frame.setGlassPane(glassPane);
        JButton button = new JButton("Test Query");
        button.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
            // Call in new thread to allow the UI to update
            glassPane.showMessage(frame.getRootPane(), "The content this code. The content of this method is always * regenerated by the Form Editor.This method is called from within "
                        + "the constructor to initialize the form.  WARNING: Do NOT modif", "");
            }
        });
        frame.getContentPane().setLayout (new FlowLayout());
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (button);
        frame.setSize(200, 200);
        frame.setVisible(true);
    } 
 }