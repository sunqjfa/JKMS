package jyms.ui;


import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;



public class ScrollBarUI_White extends BasicScrollBarUI
{
    /** True indicates a middle click will absolutely position the
     * scrollbar. */
    private boolean supportsAbsolutePositioning;

    /**
     * The scrollbar value is cached to save real value if the view is adjusted.
     */
    private int scrollBarValue;
    
    protected void configureScrollBarColors(){
        LookAndFeel.installColors(scrollbar, "ScrollBar.background_white",
                                  "ScrollBar.foreground_white");
        thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight_white");
        thumbLightShadowColor = UIManager.getColor("ScrollBar.thumbShadow_white");
        thumbDarkShadowColor = UIManager.getColor("ScrollBar.thumbDarkShadow_white");
        thumbColor = UIManager.getColor("ScrollBar.thumb_white");
        trackColor = UIManager.getColor("ScrollBar.track_white");
        trackHighlightColor = UIManager.getColor("ScrollBar.trackHighlight_white");
    }


    protected void installDefaults()
    {
        scrollBarWidth = UIManager.getInt("ScrollBar.width");
        if (scrollBarWidth <= 0) {
            scrollBarWidth = 16;
        }
        minimumThumbSize = (Dimension)UIManager.get("ScrollBar.minimumThumbSize");
        maximumThumbSize = (Dimension)UIManager.get("ScrollBar.maximumThumbSize");

        Boolean absB = (Boolean)UIManager.get("ScrollBar.allowsAbsolutePositioning");
        supportsAbsolutePositioning = (absB != null) ? absB.booleanValue() :
                                      false;

        trackHighlight = NO_HIGHLIGHT;
        if (scrollbar.getLayout() == null ||
                     (scrollbar.getLayout() instanceof UIResource)) {
            scrollbar.setLayout(this);
        }
        configureScrollBarColors();
        LookAndFeel.installBorder(scrollbar, "ScrollBar.border_white");
        LookAndFeel.installProperty(scrollbar, "opaque", Boolean.TRUE);

        scrollBarValue = scrollbar.getValue();

        incrGap = UIManager.getInt("ScrollBar.incrementButtonGap");
        decrGap = UIManager.getInt("ScrollBar.decrementButtonGap");

        // TODO this can be removed when incrGap/decrGap become protected
        // handle scaling for sizeVarients for special case components. The
        // key "JComponent.sizeVariant" scales for large/small/mini
        // components are based on Apples LAF
        String scaleKey = (String)scrollbar.getClientProperty(
                "JComponent.sizeVariant");
        if (scaleKey != null){
            if ("large".equals(scaleKey)){
                scrollBarWidth *= 1.15;
                incrGap *= 1.15;
                decrGap *= 1.15;
            } else if ("small".equals(scaleKey)){
                scrollBarWidth *= 0.857;
                incrGap *= 0.857;
                decrGap *= 0.714;
            } else if ("mini".equals(scaleKey)){
                scrollBarWidth *= 0.714;
                incrGap *= 0.714;
                decrGap *= 0.714;
            }
        }
    }
    protected JButton createDecreaseButton(int orientation)  {
        return new BasicArrowButton(orientation,
                                    UIManager.getColor("ScrollBar.thumb_white"),
                                    UIManager.getColor("ScrollBar.thumbShadow_white"),
                                    UIManager.getColor("ScrollBar.thumbDarkShadow_white"),
                                    UIManager.getColor("ScrollBar.thumbHighlight_white"));
    }

    protected JButton createIncreaseButton(int orientation)  {
        return new BasicArrowButton(orientation,
                                    UIManager.getColor("ScrollBar.thumb_white"),
                                    UIManager.getColor("ScrollBar.thumbShadow_white"),
                                    UIManager.getColor("ScrollBar.thumbDarkShadow_white"),
                                    UIManager.getColor("ScrollBar.thumbHighlight_white"));
    }
}
