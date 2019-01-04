
package jyms.ui;

import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.plaf.basic.BasicPanelUI;
import jyms.data.TxtLogger;

/**
 *
 * @author John
 * LineBorder形式的JPanel，并不是一个通用的PanelUI
 */
public class PanelUI_Black extends BasicPanelUI {
    private final String FILE_NAME = this.getClass().getName() + ".java";
     @Override
     protected void installDefaults(JPanel p) {
        try{
            LookAndFeel.installColorsAndFont(p,
                                             "Panel.background_black",
                                             "Panel.foreground_black",
                                             "Panel.font");
            //LookAndFeel.installBorder(p,null);
            LookAndFeel.installProperty(p, "opaque", Boolean.TRUE);
        }catch (Exception e){
            TxtLogger.append(FILE_NAME, "installDefaults()","系统在Installs the UI defaults.过程中，出现错误"
                + "\r\n                       Exception:" + e.toString());
        }
    }
}
