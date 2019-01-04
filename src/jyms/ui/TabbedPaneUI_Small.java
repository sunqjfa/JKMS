package jyms.ui;


import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.*;



public class TabbedPaneUI_Small extends BasicTabbedPaneUI implements SwingConstants{ 
    
    private   Color selectedColor;
    private boolean tabsOverlapBorder;
    private boolean tabsOpaque = true;
    private boolean contentOpaque = true;
    
    @Override
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(tabPane, "TabbedPane.background_small",
                                    "TabbedPane.foreground_small", "TabbedPane.font_small");
        highlight = UIManager.getColor("TabbedPane.light");
        lightHighlight = UIManager.getColor("TabbedPane.highlight");
        shadow = UIManager.getColor("TabbedPane.shadow");
        darkShadow = UIManager.getColor("TabbedPane.darkShadow");
        focus = UIManager.getColor("TabbedPane.focus_small");
        selectedColor = UIManager.getColor("TabbedPane.selected_small");

        textIconGap = UIManager.getInt("TabbedPane.textIconGap");
        tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
        selectedTabPadInsets = UIManager.getInsets("TabbedPane.selectedTabPadInsets");
        tabAreaInsets = UIManager.getInsets("TabbedPane.tabAreaInsets");
        tabsOverlapBorder = UIManager.getBoolean("TabbedPane.tabsOverlapBorder");
        contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
        tabRunOverlay = UIManager.getInt("TabbedPane.tabRunOverlay");
        tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
        contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
        Object opaque = UIManager.get("TabbedPane.opaque");
        if (opaque == null) {
            opaque = Boolean.FALSE;
        }
        LookAndFeel.installProperty(tabPane, "opaque", opaque);

        // Fix for 6711145 BasicTabbedPanuUI should not throw a NPE if these
        // keys are missing. So we are setting them to there default values here
        // if the keys are missing.
        if (tabInsets == null) tabInsets = new Insets(0,4,1,4);
        if (selectedTabPadInsets == null) selectedTabPadInsets = new Insets(2,2,2,1);
        if (tabAreaInsets == null) tabAreaInsets = new Insets(3,2,0,2);
        if (contentBorderInsets == null) contentBorderInsets = new Insets(2,2,3,3);
    }

}
