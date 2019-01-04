
package jyms.tools;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JLabel;


public class GlassPaneToolTip  extends JComponent{

	private Point point;
        private String ToolTipMessage;
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
                        g.drawString(ToolTipMessage, point.x , point.y);

                        //Image img2 = Toolkit.getDefaultToolkit().getImage("image/cross1.PNG");
                        //g.drawImage(img, 50, 50, this);
		}
	}

	public void setPoint(Point point) {
		this.point = point;
	}
        
        public void setToolTipMessage(String ToolTipMessage) {
		this.ToolTipMessage = ToolTipMessage;
	}
  
}
