package jyms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 * 几乎所有时髦的应用都有一个欢迎屏幕。欢迎屏幕既是宣传产品的方法之一，
 * 而且在长时间的应用启动过程中，欢迎屏幕还用来表示应用正在准备过程中。
 */


/**
 * 本例子实现一个欢迎屏幕，常用作应用软件的启动画面。
 */
public class JWindowSplash extends JWindow {
        private JLabel labelProcessDesc = new JLabel("", javax.swing.SwingConstants.CENTER);
	/**
	 * 构造函数
	 * @param frame		欢迎屏幕所属的窗体
	 */
	public JWindowSplash(Window frame) {
		super(frame);
		setBackground(new java.awt.Color(64, 64, 64));
		// 建立一个标签，标签中显示图片。
		JLabel label = new JLabel(new ImageIcon(this.getClass().getResource("/jyms/image/Splash.jpg")));
		// 将标签放在欢迎屏幕中间
		getContentPane().add(label, BorderLayout.CENTER);
		pack();
		// 获取屏幕的分辨率大小
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// 获取标签大小
		Dimension labelSize = label.getPreferredSize();

                labelProcessDesc.setOpaque(true);
                
                labelProcessDesc.setBackground(new java.awt.Color(0, 0, 0));
                labelProcessDesc.setForeground(new java.awt.Color(255, 255, 255));
                labelProcessDesc.setPreferredSize(new java.awt.Dimension(56, 30));
                getContentPane().add(labelProcessDesc, BorderLayout.PAGE_END);
		// 将欢迎屏幕放在屏幕中间
		setLocation(screenSize.width / 2 - (labelSize.width / 2),
				screenSize.height / 2 - (labelSize.height / 2));

		setVisible(true);

	}
	
        public void setProcessDesc(String ProcessDesc){
            labelProcessDesc.setText(ProcessDesc);
        }

        
	public static void main(String[] args){
		JFrame frame = new JFrame("欢迎屏幕");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                
		//SplashWindow splash = new SplashWindow("Splash.jpg", frame, 2000);
                JWindowSplash splash = new JWindowSplash(frame);
                splash.setProcessDesc("欢迎屏幕");
		frame.pack();
		frame.setVisible(true);
                splash.setProcessDesc("欢迎屏幕2");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(JWindowSplash.class.getName()).log(Level.SEVERE, null, ex);
            }
                splash.setProcessDesc("欢迎屏幕3");
	}
}