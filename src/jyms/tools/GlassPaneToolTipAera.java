
package jyms.tools;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JLabel;


public class GlassPaneToolTipAera  extends JComponent{

	private Point point;
        private String[] ToolTipMessages;
        private static JLabel statusLabel = new JLabel();

	@Override
	protected void paintComponent(Graphics g) {
		if (point != null) {
			//g.setColor(Color.RED);
			//g.fillOval(point.x - 10, point.y - 10, 20, 20);
                        //Image img = Toolkit.getDefaultToolkit().getImage("image/cross1.gif");
                        //g.drawImage(img, point.x - 10, point.y - 10, this);
                        

                        g.setColor(Color.RED);
                        g.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 14));
                        FontMetrics fontMetrics = g.getFontMetrics(g.getFont());
                        g.clearRect(point.x , point.y - fontMetrics.getHeight(), 200, fontMetrics.getHeight()*ToolTipMessages.length + fontMetrics.getAscent());
                        
                        for (int i=0;i<ToolTipMessages.length;i++){
                            g.drawString(ToolTipMessages[i], point.x , point.y+fontMetrics.getHeight()*i);
                        }
                        
                        //Image img2 = Toolkit.getDefaultToolkit().getImage("image/cross1.PNG");
                        //g.drawImage(img, 50, 50, this);
		}
	}

	public void setPoint(Point point) {
		this.point = point;
	}
        
        public void setToolTipMessage(String ToolTipMessage) {
		this.ToolTipMessages = ToolTipMessage.split("<br>");
	}
   
}
