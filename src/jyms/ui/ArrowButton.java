package jyms.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 *	A button with an arrow. This class is used for JScrollBars.
 * 
 * 	@author	Markus Fischer
 *
 *  	<p>This software is under the <a href="http://www.gnu.org/copyleft/lesser.html" target="_blank">GNU Lesser General Public License</a>
 */


class ArrowButton extends BasicArrowButton 
{
	/**	The direction into which the arrow points */
	protected int 			direction;

	/**	Shadow color */
	private Color 			shadow;
	
	/**	Dark shadow color */
	private Color 			darkShadow;
	
	/**	Highlighting color */
	private Color 			highlight;
	
	/**	If true, the left edge will be drawn */
	protected boolean 		drawLeftBorder=true;
	
	/**	If true, the right edge will be drawn */
	protected boolean drawRightBorder=true;
	
	/**	If true, the top edge will be drawn */
	protected boolean drawTopBorder=true;
	
	/**	If true, the bottom edge will be drawn */
	protected boolean drawBottomBorder=true;

        public ArrowButton(int direction, Color background, Color shadow, Color darkShadow, Color highlight) {
            super(direction, background, shadow, darkShadow, highlight);
        }
	/**
         * Creates a {@code BasicArrowButton} whose arrow
         * is drawn in the specified direction.
         *
         * @param direction the direction of the arrow; one of
         *        {@code SwingConstants.NORTH}, {@code SwingConstants.SOUTH},
         *        {@code SwingConstants.EAST} or {@code SwingConstants.WEST}
         */
        public ArrowButton(int direction) {
            super(direction);
        }
	






	/**	Indicates whether the bottom border is to be drawn or not */
	public void setDrawBottomBorder(boolean drawBottomBorder)
	{
		this.drawBottomBorder=drawBottomBorder;
	}
	
	
	/**	Returns the direction into which the arrow points. This is one
	 * 	of the SwingConstants.
	 * 
	 *  	@see SwingConstants
	 */	
	public int getDirection()
	{
		return direction;
	}
	
	
	/**	Sets the direction into which the arrow points. This is one
	 * 	of the SwingConstants.
	 * 
	 * 	@see SwingConstants
	 */
	public void setDirection(int dir)
	{
		direction= dir;
	}


	/**	Paints the button */
	public void paint(Graphics g)
	{
		Color origColor;
		boolean isPressed, isEnabled;
		int w, h, size;

		w= getSize().width;
		h= getSize().height;
		origColor= g.getColor();
		isPressed= getModel().isPressed();
		isEnabled= isEnabled();

		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);

		// Draw the proper Border
		g.setColor(UIManager.getColor("Button.borderColor"));
		if(drawLeftBorder)
			g.drawLine(0, 0, 0, h);
		if(drawTopBorder)
			g.drawLine(0, 0, w, 0);
		if(drawRightBorder)
			g.drawLine(w-1, 0, w-1, h);
		if(drawBottomBorder)
			g.drawLine(0, h-1, w-1, h-1);

		// If there's no room to draw arrow, bail
		if (h < 5 || w < 5)
		{
			g.setColor(origColor);
			return;
		}

		if (isPressed)
		{
			g.translate(1, 1);
		}

		// Draw the arrow
		size= Math.min((h - 4) / 3, (w - 4) / 3);
		size= Math.max(size, 2);
		paintTriangle(
			g,
			(w - size) / 2,
			(h - size) / 2,
			size,
			direction,
			isEnabled);

		// Reset the Graphics back to it's original settings
		if (isPressed)
		{
			g.translate(-1, -1);
		}
		g.setColor(origColor);

	}


}
