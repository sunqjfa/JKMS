
package jyms.ui;


import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import jyms.data.TxtLogger;


public class ScrollPaneUI_White extends BasicScrollPaneUI
{
    private final String FILE_NAME = this.getClass().getName() + ".java";

    public static ComponentUI createUI(JComponent x) 
    {
    	return new ScrollPaneUI_White();
    }
    
    /**
     *
     * @param scrollpane
     */
    @Override
    protected void installDefaults(JScrollPane scrollpane) 
    {
        try{
            LookAndFeel.installBorder(scrollpane, "ScrollPane.border_white");
            LookAndFeel.installColorsAndFont(scrollpane,
                "ScrollPane.background_white",
                "ScrollPane.foreground_white",
                "ScrollPane.font");

            Border vpBorder = scrollpane.getViewportBorder();
            if ((vpBorder == null) ||( vpBorder instanceof UIResource)) {
                vpBorder = UIManager.getBorder("ScrollPane.viewportBorder_white");
                scrollpane.setViewportBorder(vpBorder);
            }
            LookAndFeel.installProperty(scrollpane, "opaque", Boolean.TRUE);

            /*控制滚动面板Viewport的透明性 */
            scrollpane.getViewport().setOpaque(UIManager.getBoolean("ScrollPane.opaque_white"));
            scrollpane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JLabel());
        }catch (Exception e){
            TxtLogger.append(FILE_NAME, "installDefaults()","系统在Installs the UI defaults.过程中，出现错误"
                + "\r\n                       Exception:" + e.toString());
        }
    }

}
