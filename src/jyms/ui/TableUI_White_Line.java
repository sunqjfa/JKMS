
package jyms.ui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;


public class TableUI_White_Line extends BasicTableUI
{
    /**	Creates and returns the UI delegate for the specified component */
    public static ComponentUI createUI(JComponent c) {
        return new TableUI_White_Line();
    }


	@Override
    protected void installDefaults() {
    	super.installDefaults();
        table.setRowHeight(30);
//        //不显示水平的网格线
//        table.setShowHorizontalLines(true);
//        //不显示垂直的网格线
//        table.setShowVerticalLines(true);
//        //将 rowMargin 和 columnMargin（单元格之间间距的高度和宽度）
        table.setIntercellSpacing(new Dimension(0,1));

        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setSelectionBackground(new java.awt.Color(230, 230, 230));
        table.setFont(new java.awt.Font("微软雅黑", 0, 16)); // NOI18N
    }

}
