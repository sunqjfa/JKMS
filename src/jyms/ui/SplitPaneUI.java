package jyms.ui;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


public class SplitPaneUI extends BasicSplitPaneUI
{
	/**	Creates and returns a UI delegate for the specified
	 * 	component.
            * @param component
            * @return 
	 */
	public static ComponentUI createUI(JComponent component) 
	{
		return new SplitPaneUI();
	}
	

	/**	Creates the default divider
            * @return  
        */
        @Override
	public BasicSplitPaneDivider createDefaultDivider() 
	{
		return new SplitPaneDivider(this);
	}
    

        @Override
        public void installUI(JComponent c)
	{
		super.installUI(c);
		
		c.addContainerListener(new SplitPaneContListener());
		
		if(c instanceof JSplitPane)
		{
			JSplitPane sp=(JSplitPane)c;
			Component child=sp.getLeftComponent();
			setContentBorder(child);
			
			child=sp.getRightComponent();
			setContentBorder(child);
		}
	}

   
   

	/**	Sets the border for the specified contained component */   
   protected void setContentBorder(Component c)
   {
		if(c instanceof JComponent)
		{
			JComponent jc=(JComponent)c;
			if(jc.getBorder()==null)
				jc.setBorder(new SplitPaneContentBorder());
		}
   }
	

    /**	Listener for content changes to the JSplitPane */	
    protected class SplitPaneContListener implements ContainerListener
    {
            public void componentAdded(ContainerEvent e)
            {
                    Component c= e.getChild();
                    setContentBorder(c);
            }

            public void componentRemoved(ContainerEvent e)
            {
                    Component c= e.getChild();

                    if (c instanceof JComponent)
                    {
                            JComponent jc=(JComponent)c;
                            if(jc.getBorder() instanceof SplitPaneContentBorder)
                                    jc.setBorder(null);
                    }
            }
    }    
        
        

}
