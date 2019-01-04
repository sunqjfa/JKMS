/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import java.awt.GridLayout;
import javax.swing.SwingWorker;
import jyms.tools.InfiniteProgressPanel;

/**
 *
 * @author John
 */
public class JDialogInfiniteProgress extends javax.swing.JDialog {

    private InfiniteProgressPanel ProgressPanel;
    private SwingWorker progressSwingWorker;
    /**
     * Creates new form JDialogInfiniteProgress
     * @param parent
     * @param modal
     * @param NewSwingWorker
     */
    public JDialogInfiniteProgress(java.awt.Frame parent, boolean modal, SwingWorker NewSwingWorker) {
        super(parent, modal);
        initComponents();
        
        progressSwingWorker = NewSwingWorker;
        //UI操作
        CommonParas.setJButtonUnDecorated(jButtonStop);
        jPanelFoot.setVisible(false);
//        initialDialogParas();
    }

//    private void initialDialogParas(){
////        ProgressPanel = new InfiniteProgressPanel();
////        ProgressPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
//////        setGlassPane(ProgressPanel);
////        ProgressPanel.start();
////        jPanel3.setLayout(new GridLayout(1 , 1));
////        jPanel3.add(ProgressPanel);
////        getContentPane().setLayout(new BorderLayout());
////        jPanel3.add(BorderLayout.CENTER, buildInfinitePanel());
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabelProgressInfo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButtonStop = new javax.swing.JButton();
        jPanelProgress = new javax.swing.JPanel();
        jPanelFoot = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDetailInfo = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setFocusable(false);
        setFocusableWindowState(false);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(344, 187));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabelProgressInfo.setFont(new java.awt.Font("微软雅黑", 1, 17)); // NOI18N
        jLabelProgressInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jLabelProgressInfo, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jButtonStop.setBackground(new java.awt.Color(255, 255, 255));
        jButtonStop.setFont(new java.awt.Font("微软雅黑", 0, 17)); // NOI18N
        jButtonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/stoprun.png"))); // NOI18N
        jButtonStop.setText(sStop);
        jButtonStop.setBorderPainted(false);
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonStop, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout jPanelProgressLayout = new javax.swing.GroupLayout(jPanelProgress);
        jPanelProgress.setLayout(jPanelProgressLayout);
        jPanelProgressLayout.setHorizontalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 360, Short.MAX_VALUE)
        );
        jPanelProgressLayout.setVerticalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 235, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelProgress, java.awt.BorderLayout.CENTER);

        jPanelFoot.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setBorder(null);

        jTextAreaDetailInfo.setEditable(false);
        jTextAreaDetailInfo.setBackground(new java.awt.Color(240, 240, 240));
        jTextAreaDetailInfo.setColumns(20);
        jTextAreaDetailInfo.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jTextAreaDetailInfo.setForeground(new java.awt.Color(255, 0, 0));
        jTextAreaDetailInfo.setLineWrap(true);
        jTextAreaDetailInfo.setRows(3);
        jScrollPane1.setViewportView(jTextAreaDetailInfo);

        javax.swing.GroupLayout jPanelFootLayout = new javax.swing.GroupLayout(jPanelFoot);
        jPanelFoot.setLayout(jPanelFootLayout);
        jPanelFootLayout.setHorizontalGroup(
            jPanelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
        );
        jPanelFootLayout.setVerticalGroup(
            jPanelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFootLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(jPanelFoot, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopActionPerformed
        // TODO add your handling code here:
        progressSwingWorker.cancel(true);
        stopPogress();
    }//GEN-LAST:event_jButtonStopActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        //信息为空，圆形的条的数量为12，颜色遮挡（或者叫“面纱”）的透明度级别为1，每秒帧数为12，淡入和淡出的持续时间为300毫秒
        ProgressPanel = new InfiniteProgressPanel("", 12 , 1, 12, 300);
        ProgressPanel.setPanelSize(jPanelProgress.getSize());
        //ProgressPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        ProgressPanel.start();
        jPanelProgress.setLayout(new GridLayout(1 , 1));
        jPanelProgress.add(ProgressPanel);
        
        this.validate();
        
//        ProgressPanel = new InfiniteProgressPanel();
//        ProgressPanel.setPanelSize(jPanelProgress.getSize());
//        setGlassPane(ProgressPanel);
//        ProgressPanel.start();
        
    }//GEN-LAST:event_formWindowOpened

    public void stopPogress(){
        
        ProgressPanel.stop();
        this.dispose();
    }
    
    public void setPorgressInfo(String PorgressInfo){
        jLabelProgressInfo.setText(PorgressInfo+"...");
    }
    public void setPorgressDetailInfo(String PorgressDetailInfo){
        //ProgressPanel.setText(PorgressDetailInfo);
        this.setSize(344, 264);
        jPanelFoot.setVisible(true);
        jTextAreaDetailInfo.setText(PorgressDetailInfo);
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
            java.util.logging.Logger.getLogger(JDialogInfiniteProgress.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialogInfiniteProgress.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialogInfiniteProgress.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialogInfiniteProgress.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialogInfiniteProgress dialog = new JDialogInfiniteProgress(new javax.swing.JFrame(), true,null);
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
    public static void modifyLocales(){
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作
        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        //信息显示
        sStop = Locales.getString("ClassStrings", "JDialogInfiniteProgress.sStop");  //日

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonStop;
    private javax.swing.JLabel jLabelProgressInfo;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelFoot;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaDetailInfo;
    // End of variables declaration//GEN-END:variables
    
    private static String sStop = "停止";
}