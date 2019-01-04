
package jyms.ui;


import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

/**
 *
 * @author John
 */
public class MenuBarUI extends BasicMenuBarUI
{

    public static ComponentUI createUI(JComponent x)
    {
    	return new MenuBarUI();
    }

}
