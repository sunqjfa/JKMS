package jyms.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class InfiniteProgressPanel extends JPanel implements MouseListener {

        private Dimension panelSize = new Dimension(340,340);//初始化大小，根据JDialogInfiniteProgress的大小而定
	/**
	 * 
	 */
	private static final long serialVersionUID = -1994937371682507261L;
	/** Contains the bars composing the circular shape. 构成圆形形状的条数组*/
	protected Area[] ticker = null;
	/** The animation thread is responsible for fade in/out and rotation.动画线程负责淡入/出和旋转 */
	protected Thread animation = null;
	/** Notifies whether the animation is running or not.通知是否运行动画 */
	protected boolean started = false;
	/** Alpha level of the veil, used for fade in/out.遮挡“面纱”的透明度级别，用于面纱的淡入、 淡出的 */
	protected int alphaLevel = 0;
	/** Duration of the veil's fade in/out.面纱的淡入、 淡出的持续时间 */
	protected int rampDelay = 300;
	/** Alpha level of the veil.遮挡“面纱”的透明度级别 */
	protected float shield = 1;//0.70f;
	/** Message displayed below the circular shape.圆型形状下方显示的消息 */
	protected String text = "";
	/** Amount of bars composing the circular shape.构成圆形形状的条的数量 */
	protected int barsCount = 12;
	/** Amount of frames per second. Lowers this to save CPU.每秒帧数。降低此保存 CPU */
	protected float fps = 15.0f;
	/** Rendering hints to set anti aliasing. 呈现提示设置抗锯齿效果*/
	protected RenderingHints hints = null;

	/**
	 * Creates a new progress panel with default values:<br />
	 * <ul>
	 * <li>No message</li>
	 * <li>12 bars</li>
	 * <li>Veil's alpha level is 70%</li>
	 * <li>15 frames per second</li>
	 * <li>Fade in/out last 300 ms</li>
	 * </ul>
	 */
	public InfiniteProgressPanel() {
		this("");
	}

	/**
	 * Creates a new progress panel with default values:<br />
	 * <ul>
	 * <li>12 bars</li>
	 * <li>Veil's alpha level is 70%</li>
	 * <li>15 frames per second</li>
	 * <li>Fade in/out last 300 ms</li>
	 * </ul>
	 * 
	 * @param text
	 *            The message to be displayed. Can be null or empty.
	 */
	public InfiniteProgressPanel(String text) {
		this(text, 12);
	}

	/**
	 * Creates a new progress panel with default values:<br />
	 * <ul>
	 * <li>Veil's alpha level is 70%</li>
	 * <li>15 frames per second</li>
	 * <li>Fade in/out last 300 ms</li>
	 * </ul>
	 * 
	 * @param text
	 *            The message to be displayed. Can be null or empty.
	 * @param barsCount
	 *            The amount of bars composing the circular shape
	 */
	public InfiniteProgressPanel(String text, int barsCount) {
		this(text, barsCount, 0.70f);
	}

	/**
	 * Creates a new progress panel with default values:<br />
	 * <ul>
	 * <li>15 frames per second</li>
	 * <li>Fade in/out last 300 ms</li>
	 * </ul>
	 * 
	 * @param text
	 *            The message to be displayed. Can be null or empty.
	 * @param barsCount
	 *            The amount of bars composing the circular shape.
	 * @param shield
	 *            The alpha level between 0.0 and 1.0 of the colored shield (or
	 *            veil).
	 */
	public InfiniteProgressPanel(String text, int barsCount, float shield) {
		this(text, barsCount, shield, 15.0f);
	}

	/**
	 * Creates a new progress panel with default values:<br />
	 * <ul>
	 * <li>Fade in/out last 300 ms</li>
	 * </ul>
	 * 
	 * @param text
	 *            The message to be displayed. Can be null or empty.
	 * @param barsCount
	 *            The amount of bars composing the circular shape.
	 * @param shield
	 *            The alpha level between 0.0 and 1.0 of the colored shield (or
	 *            veil).
	 * @param fps
	 *            The number of frames per second. Lower this value to decrease
	 *            CPU usage.
	 */
	public InfiniteProgressPanel(String text, int barsCount, float shield,
			float fps) {
		this(text, barsCount, shield, fps, 300);
	}

	/**
	 * Creates a new progress panel.
	 * 
	 * @param text
	 *            The message to be displayed. Can be null or empty.要显示的消息。 可以为null或为空。
	 * @param barsCount
	 *            The amount of bars composing the circular shape.组成圆形的条的数量。
	 * @param shield
	 *            The alpha level between 0.0 and 1.0 of the colored shield (or veil).颜色遮挡（或者叫“面纱”）的透明度级别，介于0.0到1.0之间
	 * @param fps
	 *            The number of frames per second. Lower this value to decrease CPU usage.每秒帧数。 降低此值可减少CPU使用率。
	 * @param rampDelay
	 *            The duration, in milli seconds, of the fade in and the fade out of the veil.淡入和淡出的持续时间（以毫秒为单位）。
	 */
	public InfiniteProgressPanel(String text, int barsCount, float shield,float fps, int rampDelay) {

		this.text = text;
		this.rampDelay = rampDelay >= 0 ? rampDelay : 0;
		this.shield = shield >= 0.0f ? shield : 0.0f;
		this.fps = fps > 0.0f ? fps : 15.0f;
		this.barsCount = barsCount > 0 ? barsCount : 14;

		this.hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
		this.hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
		this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}

    

	/**
	 * Changes the displayed message at runtime.
	 * 
	 * @param text
	 *            The message to be displayed. Can be null or empty.
	 */
	public void setText(String text) {
		this.text = text;
		repaint();
	}

	/**
	 * Returns the current displayed message.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Starts the waiting animation by fading the veil in, then rotating the
	 * shapes. 开始等待动画的褪色的面纱，然后旋转所有的条形图形
         * This method handles the visibility of the glass pane.此方法处理玻璃窗格的可见性。
	 */
	public void start() {
		addMouseListener(this);
		setVisible(true);
		ticker = buildTicker();
		animation = new Thread(new Animator(true));
		animation.start();
	}

	/**
	 * Stops the waiting animation by stopping the rotation of the circular
	 * shape and then by fading out the veil. This methods sets the panel
	 * invisible at the end.
	 */
	public void stop() {
		if (animation != null) {
			animation.interrupt();
			animation = null;
			animation = new Thread(new Animator(false));
			animation.start();
		}
	}

	/**
	 * Interrupts the animation, whatever its state is. You can use it when you
	 * need to stop the animation without running the fade out phase. This
	 * methods sets the panel invisible at the end.
	 */
	public void interrupt() {
		if (animation != null) {
			animation.interrupt();
			animation = null;

			removeMouseListener(this);
			setVisible(false);
		}
	}

	public void paintComponent(Graphics g) {
		if (started) {
			int width = getWidth();
			double maxY = 0.0;

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHints(hints);

			g2.setColor(new Color(240,240,240, (int) (alphaLevel * shield)));
			g2.fillRect(0, 0, getWidth(), getHeight());

			for (int i = 0; i < ticker.length; i++) {
				int channel = 224 - 128 / (i + 1);
				g2.setColor(new Color(channel, channel, channel, alphaLevel));
				g2.fill(ticker[i]);

				Rectangle2D bounds = ticker[i].getBounds2D();
				if (bounds.getMaxY() > maxY)
					maxY = bounds.getMaxY();
			}

                        if (text != null && text.length() > 0) {
                            
                            g2.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                            
                            StringBuilder builder = new StringBuilder("");
                            
                            char[] chars = text.toCharArray();
                            FontMetrics fontMetrics = g2.getFontMetrics();
                            
                            for (int beginIndex = 0, limit = 1;; limit++) {
                                //System.out.println(beginIndex + " " + limit + " " + (beginIndex + limit));
                                if (fontMetrics.charsWidth(chars, beginIndex, limit) < panelSize.width) {
                                    if (beginIndex + limit < chars.length) {
                                        continue;
                                    }
                                    builder.append(chars, beginIndex, limit);
                                    break;
                                }
                                builder.append(chars, beginIndex, limit - 1).append("<br>");
                                beginIndex += limit - 1;
                                limit = 0;//原先为1，因为循环到for时，先加1，直接从2开始了，所以要改为0
                            }
                            String Message = builder.toString();
                            String[] ToolTipMessages = Message.split("<br>");
                            
                            g2.setColor(Color.red);
                            
                            for (int i=0;i<ToolTipMessages.length;i++){
                                g2.drawString(ToolTipMessages[i], 0 , (int)maxY+fontMetrics.getHeight()*(i+1));
                            }
                        }
//			if (text != null && text.length() > 0) {
//				FontRenderContext context = g2.getFontRenderContext();
//				TextLayout layout = new TextLayout(text, getFont(), context);
//				Rectangle2D bounds = layout.getBounds();
//
//                                g2.setColor(Color.red);//Color.red
//				layout.draw(g2, (float) (width - bounds.getWidth()) / 2,
//						(float) (maxY + layout.getLeading() + 2 * layout
//								.getAscent()));
//			}
		}
	}

	/**
	 * Builds the circular shape and returns the result as an array of
	 * <code>Area</code>. Each <code>Area</code> is one of the bars composing
	 * the shape.
	 */
	private Area[] buildTicker() {

		Area[] ticker = new Area[barsCount];
		Point2D.Double center = new Point2D.Double((double) panelSize.width / 2,
				(double) panelSize.height / 2);
		double fixedAngle = 2.0 * Math.PI / ((double) barsCount);

		for (double i = 0.0; i < (double) barsCount; i++) {
			Area primitive = buildPrimitive();

			AffineTransform toCenter = AffineTransform.getTranslateInstance(
					center.getX(), center.getY());
			AffineTransform toBorder = AffineTransform.getTranslateInstance(
					24.0, -6.0);
			AffineTransform toCircle = AffineTransform.getRotateInstance(-i
					* fixedAngle, center.getX(), center.getY());

			AffineTransform toWheel = new AffineTransform();
			toWheel.concatenate(toCenter);
			toWheel.concatenate(toBorder);

			primitive.transform(toWheel);
			primitive.transform(toCircle);

			ticker[(int) i] = primitive;
		}

		return ticker;
	}

	/**
	 * Builds a bar.
	 */
	private Area buildPrimitive() {

		Rectangle2D.Double body = new Rectangle2D.Double(6, 0, 24, 12);
		Ellipse2D.Double head = new Ellipse2D.Double(0, 0, 12, 12);
		Ellipse2D.Double tail = new Ellipse2D.Double(24, 0, 12, 12);

		Area tick = new Area(body);
		tick.add(new Area(head));
		tick.add(new Area(tail));

		return tick;
	}

    /**
     * @param panelSize the panelSize to set
     */
    public void setPanelSize(Dimension panelSize) {
        this.panelSize = panelSize;
    }

	/**
	 * Animation thread.动画的线程。
	 */
	private class Animator implements Runnable {
		private boolean rampUp = true;

		protected Animator(boolean rampUp) {
			this.rampUp = rampUp;
		}

		public void run() {
			Point2D.Double center = new Point2D.Double((double) panelSize.width / 2,
					(double) panelSize.height / 2);
			double fixedIncrement = 2.0 * Math.PI / ((double) barsCount);
			AffineTransform toCircle = AffineTransform.getRotateInstance(
					fixedIncrement, center.getX(), center.getY());

			long start = System.currentTimeMillis();
			if (rampDelay == 0)
				alphaLevel = rampUp ? 255 : 0;

			started = true;
			boolean inRamp = rampUp;

			while (!Thread.interrupted()) {
				if (!inRamp) {
					for (int i = 0; i < ticker.length; i++)
						ticker[i].transform(toCircle);
				}

				repaint();

				if (rampUp) {
					if (alphaLevel < 255) {
						alphaLevel = (int) (255 * (System.currentTimeMillis() - start) / rampDelay);
						if (alphaLevel >= 255) {
							alphaLevel = 255;
							inRamp = false;
						}
					}
				} else if (alphaLevel > 0) {
					alphaLevel = (int) (255 - (255 * (System
							.currentTimeMillis() - start) / rampDelay));
					if (alphaLevel <= 0) {
						alphaLevel = 0;
						break;
					}
				}

				try {
					Thread.sleep(inRamp ? 10 : (int) (1000 / fps));
				} catch (InterruptedException ie) {
					break;
				}
				Thread.yield();
			}

			if (!rampUp) {
				started = false;
				repaint();

				setVisible(false);
				removeMouseListener(InfiniteProgressPanel.this);
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}