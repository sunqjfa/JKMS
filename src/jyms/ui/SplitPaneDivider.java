package jyms.ui;


import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.*;


class SplitPaneDivider extends BasicSplitPaneDivider
{
	private int   inset=2;

	private BasicSplitPaneUI ui;
	
	private int oneTouchSize;
	
	private int standardSize;
	
	
	public SplitPaneDivider(BasicSplitPaneUI ui)
	{
		super(ui);
		
		this.ui=ui;
		standardSize=UIManager.getInt("SplitPane.dividerSize");
		oneTouchSize=UIManager.getInt("SplitPane.oneTouchDividerSize");
		
		setLayout(new MetalDividerLayout());
	}

	
	protected void oneTouchExpandableChanged()
	{
		super.oneTouchExpandableChanged();
		
		if(ui.getSplitPane()!=null && ui.getSplitPane().isOneTouchExpandable())
			setDividerSize(oneTouchSize);
		else
			setDividerSize(standardSize);
	}
	
	
	public void paint(Graphics g)
	{
		g.setColor(UIManager.getColor("SplitPane.dividerground"));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		paintComponents(g);
	}




	/*
	 * The following methods only exist in order to be able to access protected
	 * members in the superclass, because these are otherwise not available
	 * in any inner class.
	 */
	int getOneTouchSizeFromSuper()
	{
		return super.ONE_TOUCH_SIZE;
	}


	int getOneTouchOffsetFromSuper()
	{
		return super.ONE_TOUCH_OFFSET;
	}


	int getOrientationFromSuper()
	{
		return super.orientation;
	}


	JSplitPane getSplitPaneFromSuper()
	{
		return super.splitPane;
	}


	JButton getLeftButtonFromSuper()
	{
		return super.leftButton;
	}


	JButton getRightButtonFromSuper()
	{
		return super.rightButton;
	}

	/**
	 * Used to layout a MetalSplitPaneDivider. Layout for the divider
	 * involves appropriately moving the left/right buttons around.
	 * <p>
	 * This inner class is marked &quot;public&quot; due to a compiler bug.
	 * This class should be treated as a &quot;protected&quot; inner class.
	 * Instantiate it only within subclasses of MetalSplitPaneDivider.
	 */
	public class MetalDividerLayout implements LayoutManager
	{
		public void layoutContainer(Container c)
		{
			JButton    leftButton=getLeftButtonFromSuper();
			JButton    rightButton=getRightButtonFromSuper();
			JSplitPane splitPane=getSplitPaneFromSuper();
			int		  orientation=getOrientationFromSuper();
			int		  oneTouchSize=getOneTouchSizeFromSuper();
			int		  oneTouchOffset=getOneTouchOffsetFromSuper();
			Insets	  insets=getInsets();


			// This layout differs from the one used in BasicSplitPaneDivider.
			// It does not center justify the oneTouchExpadable buttons.
			// This was necessary in order to meet the spec of the Metal
			// splitpane divider.
			if(leftButton!=null && rightButton!=null && c==SplitPaneDivider.this)
			{
				if(splitPane.isOneTouchExpandable())
				{
					if(orientation==JSplitPane.VERTICAL_SPLIT)
					{
						int extraY=(insets!=null) ? insets.top : 0;
						int blockSize=getDividerSize();

						if(insets!=null)
						{
							blockSize-=(insets.top+insets.bottom);
						}
						blockSize=Math.min(blockSize, oneTouchSize);
						leftButton.setBounds(oneTouchOffset, extraY, blockSize*2,
													blockSize);
						rightButton.setBounds(oneTouchOffset+oneTouchSize*2, extraY,
													 blockSize*2, blockSize);
					}
					else
					{
						int blockSize=getDividerSize();
						int extraX=(insets!=null) ? insets.left : 0;

						if(insets!=null)
						{
							blockSize-=(insets.left+insets.right);
						}
						blockSize=Math.min(blockSize, oneTouchSize);
						leftButton.setBounds(extraX, oneTouchOffset, blockSize,
													blockSize*2);
						rightButton.setBounds(extraX, oneTouchOffset+oneTouchSize*2,
													 blockSize, blockSize*2);
					}
				}
				else
				{
					leftButton.setBounds(-5, -5, 1, 1);
					rightButton.setBounds(-5, -5, 1, 1);
				}
			}
		}


		public Dimension minimumLayoutSize(Container c)
		{
			return new Dimension(0, 0);
		}


		public Dimension preferredLayoutSize(Container c)
		{
			return new Dimension(0, 0);
		}


		public void removeLayoutComponent(Component c) {}


		public void addLayoutComponent(String string, Component c) {}
	}
}
