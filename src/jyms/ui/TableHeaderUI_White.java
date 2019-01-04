package jyms.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;





public class TableHeaderUI_White extends TableHeaderUI
{
        private TableCellRenderer originalHeaderRenderer;
        /**	Installs the UI settings for the specified component */
	public void installUI(JComponent c)
	{
            super.installUI(c);
            originalHeaderRenderer = header.getDefaultRenderer();
//			if (originalHeaderRenderer instanceof UIResource) 
//			{
            header.setDefaultRenderer(new MyDefaultRenderer2());
//			}

	}


	/**	Installs the UI settings for the specified component */
	public void uninstallUI(JComponent c)
	{
            if (header.getDefaultRenderer() instanceof MyDefaultRenderer2) 
		{
			header.setDefaultRenderer(originalHeaderRenderer);
		}
		super.uninstallUI(c);
	}

        private class MyDefaultRenderer2 extends DefaultTableCellRenderer 
	{

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                // TODO Auto-generated method stub
                setHorizontalAlignment(JLabel.LEADING); 
                Component comp = super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
                comp.setBackground(Color.WHITE);
                comp.setForeground(Color.BLACK);
                comp.setFont(new java.awt.Font("Microsoft YaHei UI", Font.PLAIN, 16));
                return comp;
            }
        }
}

