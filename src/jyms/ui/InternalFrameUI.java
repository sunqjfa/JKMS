package jyms.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import java.beans.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;




public class InternalFrameUI extends BasicInternalFrameUI
{
	/**	The title pane for the JInternalFrame */
        protected BasicInternalFrameTitlePane titlePane; // access needs this

	/**	Listens for property changes of the JInternalFrame */


	/**	Empty border */
	private static final Border handyEmptyBorder= new EmptyBorder(0, 0, 0, 0);

	/**	Property constant */
	protected static String IS_PALETTE= "JInternalFrame.isPalette";

	/**	Property constant */
	private static String FRAME_TYPE= "JInternalFrame.frameType";
	
	/**	Property constant */
	private static String NORMAL_FRAME= "normal";
	
	/**	Property constant */
	private static String PALETTE_FRAME= "palette";
	
	/**	Property constant */
	private static String OPTION_DIALOG= "optionDialog";


	/**	Creates an instance for the specified JInternalFrame */
	public InternalFrameUI(JInternalFrame b)
	{
		super(b);
	}


	/**	Creates and returns a UI delegate for the specified component */
	public static ComponentUI createUI(JComponent c)
	{
		return new InternalFrameUI((JInternalFrame) c);
	}


	/**	Installs the UI delegate for the specified component */
	public void installUI(JComponent c)
	{
		super.installUI(c);

		Object paletteProp= c.getClientProperty(IS_PALETTE);
		if (paletteProp != null)
		{
			setPalette(((Boolean) paletteProp).booleanValue());
		}

		Container content= frame.getContentPane();
		stripContentBorder(content);
		//c.setOpaque(false);
	}


	/**	Uninstalls the UI delegate for the specified component */
	public void uninstallUI(JComponent c)
	{
		frame= (JInternalFrame) c;

		Container cont= ((JInternalFrame) (c)).getContentPane();
		if (cont instanceof JComponent)
		{
			JComponent content= (JComponent) cont;
			if (content.getBorder() == handyEmptyBorder)
			{
				content.setBorder(null);
			}
		}
		super.uninstallUI(c);
	}



	/**	Uninstalls any components installed for the associated JInternalFrame */
	protected void uninstallComponents()
	{
		titlePane= null;
		super.uninstallComponents();
	}

	
	/**	Removes the previous content border from the specified component, and
	 * 	sets an empty border if there has been no border or an UIResource instance
	 * 	before.
	 */
	private void stripContentBorder(Object c)
	{
		if (c instanceof JComponent)
		{
			JComponent contentComp= (JComponent) c;
			Border contentBorder= contentComp.getBorder();
			if (contentBorder == null || contentBorder instanceof UIResource)
			{
				contentComp.setBorder(handyEmptyBorder);
			}
		}
	}


	/**	Creates the title pane for the specified JInternalFrame */
	protected JComponent createNorthPane(JInternalFrame w)
	{
		titlePane= new BasicInternalFrameTitlePane(w);
		return titlePane;
	}


	/**	Sets the frame type according to the specified type constant. This must
	 * 	be one of the SwingConstants.
	 */
	private void setFrameType(String frameType)
	{
		if (frameType.equals(OPTION_DIALOG)){
			LookAndFeel.installBorder(frame, "InternalFrame.optionDialogBorder");
		}else if (frameType.equals(PALETTE_FRAME)){
			LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
		}else{
			LookAndFeel.installBorder(frame, "InternalFrame.border");

		}
	}


	/**	Sets whether this JInternalFrame is to use a palette border or not */
	public void setPalette(boolean isPalette)
	{
		if (isPalette)
		{
			LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
		}
		else
		{
			LookAndFeel.installBorder(frame, "InternalFrame.border");
		}

	}


	private static class MetalPropertyChangeHandler implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String name= e.getPropertyName();
			JInternalFrame jif= (JInternalFrame) e.getSource();

			if (!(jif.getUI() instanceof InternalFrameUI))
			{
				return;
			}

			InternalFrameUI ui= (InternalFrameUI) jif.getUI();

			if (name.equals(FRAME_TYPE))
			{
				if (e.getNewValue() instanceof String)
				{
					ui.setFrameType((String) e.getNewValue());
				}
			}
			else if (name.equals(IS_PALETTE))
			{
				if (e.getNewValue() != null)
				{
					ui.setPalette(((Boolean) e.getNewValue()).booleanValue());
				}
				else
				{
					ui.setPalette(false);
				}
			}
			else if (name.equals(JInternalFrame.CONTENT_PANE_PROPERTY))
			{
				ui.stripContentBorder(e.getNewValue());
			}
		}
	} // end class MetalPropertyChangeHandler
}
