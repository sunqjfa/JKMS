
package jyms.ui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;


public class TableUI extends BasicTableUI
{
    /**	Creates and returns the UI delegate for the specified component
     * @param c
     * @return  */
    public static ComponentUI createUI(JComponent c) {
        return new TableUI();
    }


	@Override
    protected void installDefaults() {
    	super.installDefaults();
        table.setRowHeight(40);
        //不显示水平的网格线
        table.setShowHorizontalLines(false);
        //不显示垂直的网格线
        table.setShowVerticalLines(false);
        //将 rowMargin 和 columnMargin（单元格之间间距的高度和宽度）
        table.setIntercellSpacing(new Dimension(0,1));
            
        table.setBackground(new java.awt.Color(64, 64, 64));
        table.setForeground(new java.awt.Color(255, 255, 255));
        table.setSelectionBackground(Color.lightGray);
        table.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
    }

}
