package jyms.ui;


import java.awt.*;

import java.util.Enumeration;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;


public class TableHeaderUI extends BasicTableHeaderUI
{

        private TableCellRenderer originalHeaderRenderer;

	/**	Creates and returns the UI delegate for the specified component */
	public static ComponentUI createUI(JComponent component)
	{
		return new TableHeaderUI();
	}


	/**	Installs the UI settings for the specified component */
	public void installUI(JComponent c)
	{
            super.installUI(c);
            originalHeaderRenderer = header.getDefaultRenderer();
			if (originalHeaderRenderer instanceof UIResource) 
			{
				header.setDefaultRenderer(new MyDefaultRenderer());
			}

	}




	/**
	 * Register all keyboard actions on the JTableHeader.
	 */
//	protected void installKeyboardActions() {}


	/**	Installs the UI settings for the specified component */
	public void uninstallUI(JComponent c)
	{
            if (header.getDefaultRenderer() instanceof MyDefaultRenderer) 
		{
			header.setDefaultRenderer(originalHeaderRenderer);
		}
		super.uninstallUI(c);
//		uninstallDefaults();
//		uninstallListeners();
//		uninstallKeyboardActions();
//
//		header.remove(rendererPane);
//		rendererPane=null;
//		header=null;
	}



	/**	Paints the specified component */
	public void paint(Graphics g, JComponent c)
	{
		if(header.getColumnModel().getColumnCount()<=0)
		{
			return;
		}
		boolean			  ltr=header.getComponentOrientation().isLeftToRight();

		Rectangle		  clip=g.getClipBounds();
		Point				  left=clip.getLocation();
		Point				  right=new Point(clip.x+clip.width-1, clip.y);
		TableColumnModel cm=header.getColumnModel();
		int				  cMin=header.columnAtPoint(ltr ? left : right);
		int				  cMax=header.columnAtPoint(ltr ? right : left);

		// This should never happen. 
		if(cMin==-1)
		{
			cMin=0;
		}

		// If the table does not have enough columns to fill the view we'll get -1.
		// Replace this with the index of the last column.
		if(cMax==-1)
		{
			cMax=cm.getColumnCount()-1;
		}

		TableColumn draggedColumn=header.getDraggedColumn();
		int		   columnWidth;
		int		   columnMargin=cm.getColumnMargin();
		Rectangle   cellRect=header.getHeaderRect(cMin);
		TableColumn aColumn;
		if(ltr)
		{
			for(int column=cMin; column<=cMax; column++)
			{
				aColumn=cm.getColumn(column);
				columnWidth=aColumn.getWidth();
				cellRect.width=columnWidth-columnMargin;
				if(aColumn!=draggedColumn)
				{
					paintCell(g, cellRect, column, column+1==header.getColumnModel().getColumnCount());
				}
				cellRect.x+=columnWidth;
//                                g.setColor(new Color(100,100,100));//[151,151,151]
//                                g.drawLine(cellRect.x, cellRect.y, cellRect.x, cellRect.y+cellRect.height);
			}
		}else{
			aColumn=cm.getColumn(cMin);
			if(aColumn!=draggedColumn)
			{
				columnWidth=aColumn.getWidth();
				cellRect.width=columnWidth-columnMargin;
				cellRect.x+=columnMargin;
				paintCell(g, cellRect, cMin, false);
			}
			for(int column=cMin+1; column<=cMax; column++)
			{
				aColumn=cm.getColumn(column);
				columnWidth=aColumn.getWidth();
				cellRect.width=columnWidth-columnMargin;
				cellRect.x-=columnWidth;
				if(aColumn!=draggedColumn)
				{
					paintCell(g, cellRect, column, false);
				}
//                                g.setColor(new Color(100,100,100));//[151,151,151]
//                                g.drawLine(cellRect.x, cellRect.y, cellRect.x, cellRect.y+cellRect.height);
			}
		}

		// Paint the dragged column if we are dragging. 
		if(draggedColumn!=null)
		{
			int		 draggedColumnIndex=viewIndexForColumn(draggedColumn);
			Rectangle draggedCellRect=header.getHeaderRect(draggedColumnIndex);

			// Draw a gray well in place of the moving column. 
			g.setColor(header.getParent().getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
						  draggedCellRect.width, draggedCellRect.height);

			draggedCellRect.x+=header.getDraggedDistance();

			// Fill the background. 
			g.setColor(header.getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
						  draggedCellRect.width, draggedCellRect.height);

			paintCell(g, draggedCellRect, draggedColumnIndex, false);

		}

		// Remove all components in the rendererPane. 
		rendererPane.removeAll();
                
                Dimension size = c.getSize();

                int w = size.width-1,h = size.height-1;

                g.setColor(new Color(100,100,100));//[151,151,151]
		g.drawLine(0, h, w, h);

                //给标题画上竖线
                //paintCellLine( g);
	}
        
        /**
            * 函数:      paintCellLine
            * 函数描述:  给标题画上竖线（但是总不成功）
        */
        private void paintCellLine(Graphics g){
            TableColumnModel cm=header.getColumnModel();
            for(int column=0; column<cm.getColumnCount(); column++){
                Component component=getHeaderRenderer(column);
                Dimension size = component.getSize();

                int w = size.width-2,h = size.height-1;
                g.setColor(new Color(80,80,80));//[151,151,151]
                g.drawLine(component.getX()+w, component.getY()+10, component.getX()+w, component.getY() + h-10);
            }
        }

	private Component getHeaderRenderer(int columnIndex)
	{
		TableColumn		   aColumn=header.getColumnModel().getColumn(columnIndex);
		TableCellRenderer renderer=aColumn.getHeaderRenderer();
		if(renderer==null)
		{
			renderer=header.getDefaultRenderer();
		}

		return renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(),false, false, -1, columnIndex);
	}


	private void paintCell(Graphics g, Rectangle cellRect, int columnIndex, boolean isLastCell)
	{
		Component component=getHeaderRenderer(columnIndex);
                //以下四行给标题画上竖线
//                Dimension size = component.getSize();
//                int w = size.width-3,h = size.height-1;
//                g.setColor(new Color(0,0,0));//[151,151,151]好像不起作用
//                g.drawLine(component.getX()+w, component.getY()+h*02, component.getX()+w, component.getY() + h-h*02);
                
		rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
											 cellRect.width+(isLastCell ? 1 : 0), cellRect.height, true);
	}


	private int viewIndexForColumn(TableColumn aColumn)
	{
		TableColumnModel cm=header.getColumnModel();
		for(int column=0; column<cm.getColumnCount(); column++)
		{
			if(cm.getColumn(column)==aColumn)
			{
				return column;
			}
		}

		return -1;
	}


	//
	// Size Methods
	//
	private int getHeaderHeight()
	{
		return 35;//设定高度为35
	}


	private Dimension createHeaderSize(long width)
	{
		TableColumnModel columnModel=header.getColumnModel();

		// None of the callers include the intercell spacing, do it here.
		if(width>Integer.MAX_VALUE)
		{
			width=Integer.MAX_VALUE;
		}
                //设定Header的高度为35
		return new Dimension((int)width, getHeaderHeight());
	}


	/**	Return the minimum size of the header. The minimum width is the sum
	 * 	of the minimum widths of each column (plus inter-cell spacing).
	 */
	public Dimension getMinimumSize(JComponent c)
	{
		long		   width=0;
		Enumeration enumeration=header.getColumnModel().getColumns();
		while(enumeration.hasMoreElements())
		{
			TableColumn aColumn=(TableColumn)enumeration.nextElement();
			width=width+aColumn.getMinWidth();
		}

		return createHeaderSize(width);
	}


	/**
	 * Return the preferred size of the header. The preferred height is the
	 * maximum of the preferred heights of all of the components provided
	 * by the header renderers. The preferred width is the sum of the
	 * preferred widths of each column (plus inter-cell spacing).
	 */
	public Dimension getPreferredSize(JComponent c)
	{
		long		   width=0;
		Enumeration enumeration=header.getColumnModel().getColumns();
		while(enumeration.hasMoreElements())
		{
			TableColumn aColumn=(TableColumn)enumeration.nextElement();
			width=width+aColumn.getPreferredWidth();
		}

		return createHeaderSize(width);
	}


	/**
	 * Return the maximum size of the header. The maximum width is the sum
	 * of the maximum widths of each column (plus inter-cell spacing).
	 */
	public Dimension getMaximumSize(JComponent c)
	{
		long		   width=0;
		Enumeration enumeration=header.getColumnModel().getColumns();
		while(enumeration.hasMoreElements())
		{
			TableColumn aColumn=(TableColumn)enumeration.nextElement();
			width=width+aColumn.getMaxWidth();
		}

		return createHeaderSize(width);
	}

        
        private class MyDefaultRenderer extends DefaultTableCellRenderer 
	{

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                // TODO Auto-generated method stub
                setHorizontalAlignment(JLabel.LEADING); 
                Component comp = super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
                comp.setBackground(new java.awt.Color(64, 64, 64));
                comp.setForeground(Color.WHITE);
                comp.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
                return comp;
            }
        }
}
	// End of Class BasicTableHeaderUI
