
package jyms.ui;


import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;


public class ScrollPaneUI extends BasicScrollPaneUI
{
    

    public static ComponentUI createUI(JComponent x) 
    {
    	return new ScrollPaneUI();
    }
    
    @Override
    protected void installDefaults(JScrollPane scrollpane) 
    {
    	super.installDefaults(scrollpane);
    	
    	/*控制滚动面板及其Viewport的透明性 */
    	//scrollpane.setOpaque(UIManager.getBoolean("ScrollPane.opaque"));
    	scrollpane.getViewport().setOpaque(UIManager.getBoolean("ScrollPane.opaque"));
        //scrollpane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JLabel(ImageIconBufferPool.getInstance().getImageIcon("ptzdown.png")));
        scrollpane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JLabel());
    }

}
