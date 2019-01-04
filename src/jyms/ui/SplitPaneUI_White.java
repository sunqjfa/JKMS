package jyms.ui;


import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import jyms.data.TxtLogger;



public class SplitPaneUI_White extends BasicSplitPaneUI{
    
    private final String FILE_NAME = this.getClass().getName() + ".java";
	private Color dividerDraggingColor;
        
                /**
     * Keys to use for forward focus traversal when the JComponent is
     * managing focus.
     */
    private Set<KeyStroke> managingFocusForwardTraversalKeys;

    /**
     * Keys to use for backward BasicSplitPaneUIfocus traversal when the JComponent is
     * managing focus.
     */
    private Set<KeyStroke> managingFocusBackwardTraversalKeys;
    
    /**
     * 只是将其背景色、前景色、border修改，其余没有什么改变
     */
    @Override
    protected  void installDefaults(){
        try{
            //super.installDefaults();
            LookAndFeel.installBorder(splitPane, "SplitPane.border_white");//原先为：SplitPane.border
            LookAndFeel.installColors(splitPane, "window.background_white",
                                      "window.foreground_white");//原先为："control", "SplitPane.foreground"

            if (divider == null) divider = createDefaultDivider();
            divider.setBasicSplitPaneUI(this);

            Border    b = divider.getBorder();

            if (b == null || !(b instanceof UIResource)) {
                divider.setBorder(UIManager.getBorder("SplitPaneDivider.border"));//原先为：SplitPaneDivider.border
            }
            dividerDraggingColor = UIManager.getColor("SplitPaneDivider.draggingColor");

            setOrientation(splitPane.getOrientation());

            // note: don't rename this temp variable to dividerSize
            // since it will conflict with "this.dividerSize" field
            Integer temp = (Integer)UIManager.get("SplitPane.dividerSize");
            LookAndFeel.installProperty(splitPane, "dividerSize", temp == null? 10: temp);

            divider.setDividerSize(splitPane.getDividerSize());
            dividerSize = divider.getDividerSize();
            splitPane.add(divider, JSplitPane.DIVIDER);

            setContinuousLayout(splitPane.isContinuousLayout());

            resetLayoutManager();

            /* Install the nonContinuousLayoutDivider here to avoid having to
            add/remove everything later. */
            if(nonContinuousLayoutDivider == null) {
                setNonContinuousLayoutDivider(
                                    createDefaultNonContinuousLayoutDivider(),
                                    true);
            } else {
                setNonContinuousLayoutDivider(nonContinuousLayoutDivider, true);
            }

            // focus forward traversal key
            if (managingFocusForwardTraversalKeys==null) {
                managingFocusForwardTraversalKeys = new HashSet<KeyStroke>();
                managingFocusForwardTraversalKeys.add(
                    KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
            }
            splitPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                                            managingFocusForwardTraversalKeys);
            // focus backward traversal key
            if (managingFocusBackwardTraversalKeys==null) {
                managingFocusBackwardTraversalKeys = new HashSet<KeyStroke>();
                managingFocusBackwardTraversalKeys.add(
                    KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK));
            }
            splitPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                                        managingFocusBackwardTraversalKeys);
        }catch (Exception e){
            TxtLogger.append(FILE_NAME, "installDefaults()","系统在Installs the UI defaults.过程中，出现错误"
                + "\r\n                       Exception:" + e.toString());
        }
    }
        

}
