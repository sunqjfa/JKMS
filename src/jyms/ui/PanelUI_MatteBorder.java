
package jyms.ui;

import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 *
 * @author John
 * LineBorder形式的JPanel，并不是一个通用的PanelUI
 */
public class PanelUI_MatteBorder extends BasicPanelUI {
     protected void installDefaults(JPanel p) {
        LookAndFeel.installColorsAndFont(p,
                                         "Panel.background",
                                         "Panel.foreground",
                                         "Panel.font");
        LookAndFeel.installBorder(p,"Panel.matteborder");
        LookAndFeel.installProperty(p, "opaque", Boolean.TRUE);
    }
}
