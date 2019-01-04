package jyms.ui;


import java.awt.*;
import java.util.ArrayList;


import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicSliderUI;


public class SliderUI_Customize extends BasicSliderUI{
    //存储需要正常显示颜色的区域起始/终止值（在这之前需要先显示默认颜色）
    private Point normalTrackSection = new Point();
    //借用Point这个对象用来存储Slider滑道上特殊颜色显示区域的起始/终止值
    //listSpecialTrackSection存储所有特殊区域的值的集合。在添加之前必须经过value和长度的换算
    private ArrayList<Point> listSpecialTrackSection = new ArrayList<>();
    
    private Color shadowColor;
    private Color highlightColor;
    private Color focusColor;

    public static ComponentUI createUI(JComponent b) {
        return new BasicSliderUI((JSlider)b);
    }


    public SliderUI_Customize(JSlider b) {
        super(b);
    }
 
    public void installUI(JComponent c)   {
        super.installUI(c);
        
    }

    /**
        * 绘制指示物
     */
    @Override
    public void paintThumb(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.red);
        g2d.drawLine(thumbRect.x+thumbRect.width/2, thumbRect.y, thumbRect.x+thumbRect.width/2, thumbRect.y + thumbRect.height*2);
        g2d.drawLine(thumbRect.x+thumbRect.width/2+1, thumbRect.y, thumbRect.x+thumbRect.width/2+1, thumbRect.y + thumbRect.height*2);
        //填充椭圆框为当前thumb位置
//        g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width,
//                        thumbRect.height);
        //也可以帖图(利用鼠标事件转换image即可体现不同状态)
        //g2d.drawImage(image, thumbRect.x, thumbRect.y, thumbRect.width,thumbRect.height,null);
    }

    /**
     * 绘制刻度轨迹(滑道)
     * @param g
     */
    @Override
    public void paintTrack(Graphics g) {
            int cy, cw;
            Rectangle trackBounds = trackRect;
            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                Graphics2D g2 = (Graphics2D) g;
                cy = (trackBounds.height / 2) - 2;
                cw = trackBounds.width;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(trackBounds.x, trackBounds.y + cy);

                //背景颜色
                g2.setPaint(Color.GRAY);
                g2.fillRect(0, -cy + 5, cw, cy);

                //设置正常显示的颜色（用途：比如录像正常的时间段）
                g2.setPaint(Color.GREEN);
                g2.fillRect(normalTrackSection.x, -cy + 5, normalTrackSection.y - normalTrackSection.x, cy);

                //设置不正常正常显示的颜色（用途：比如录像缺失的时间段）
                g2.setPaint(Color.GRAY);
                for (int i=0;i<listSpecialTrackSection.size();i++){
                    Point Point2 = listSpecialTrackSection.get(i);
                    g2.fillRect(Point2.x, -cy + 5, Point2.y - Point2.x, cy);
                }

                //背景颜色
//                g2.setPaint(slider.getBackground());
//                g2.fillRect(10, 10, cw, 5);

//                g2.setPaint(Color.WHITE);
//                g2.drawLine(0, cy, cw - 1, cy);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.translate(-trackBounds.x, -(trackBounds.y + cy));
            } else {
                super.paintTrack(g);
            }
    }
    /**
     * 绘制刻度
     * @param g
     */
    @Override
    public void paintTicks(Graphics g)  {
        Rectangle tickBounds = tickRect;

        g.setColor( UIManager.getColor("Slider.tickColor"));//g.setColor( Color.white);//g.setColor( UIManager.getColor("Slider.tickColor"));g.setColor(DefaultLookup.getColor(slider, this, "Slider.tickColor", Color.black));

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            g.translate(0, tickBounds.y);

            if (slider.getMinorTickSpacing() > 0) {
                int value = slider.getMinimum();

                while ( value <= slider.getMaximum() ) {
                    int xPos = xPositionForValue(value);
                    paintMinorTickForHorizSlider( g, tickBounds, xPos );

                    // Overflow checking
                    if (Integer.MAX_VALUE - slider.getMinorTickSpacing() < value) {
                        break;
                    }

                    value += slider.getMinorTickSpacing();
                }
            }

            if (slider.getMajorTickSpacing() > 0) {
                int value = slider.getMinimum();

                while ( value <= slider.getMaximum() ) {
                    int xPos = xPositionForValue(value);
                    paintMajorTickForHorizSlider( g, tickBounds, xPos );

                    // Overflow checking
                    if (Integer.MAX_VALUE - slider.getMajorTickSpacing() < value) {
                        break;
                    }

                    value += slider.getMajorTickSpacing();
                }
            }

            g.translate( 0, -tickBounds.y);
        } else {
            g.translate(tickBounds.x, 0);

            if (slider.getMinorTickSpacing() > 0) {
                int offset = 0;
                if(!UIUtils.isLeftToRight(slider)) {
                    offset = tickBounds.width - tickBounds.width / 2;
                    g.translate(offset, 0);
                }

                int value = slider.getMinimum();

                while (value <= slider.getMaximum()) {
                    int yPos = yPositionForValue(value);
                    paintMinorTickForVertSlider( g, tickBounds, yPos );

                    // Overflow checking
                    if (Integer.MAX_VALUE - slider.getMinorTickSpacing() < value) {
                        break;
                    }

                    value += slider.getMinorTickSpacing();
                }

                if(!UIUtils.isLeftToRight(slider)) {
                    g.translate(-offset, 0);
                }
            }

            if (slider.getMajorTickSpacing() > 0) {
                if(!UIUtils.isLeftToRight(slider)) {
                    g.translate(2, 0);
                }

                int value = slider.getMinimum();

                while (value <= slider.getMaximum()) {
                    int yPos = yPositionForValue(value);
                    paintMajorTickForVertSlider( g, tickBounds, yPos );

                    // Overflow checking
                    if (Integer.MAX_VALUE - slider.getMajorTickSpacing() < value) {
                        break;
                    }

                    value += slider.getMajorTickSpacing();
                }

                if(!UIUtils.isLeftToRight(slider)) {
                    g.translate(-2, 0);
                }
            }
            g.translate(-tickBounds.x, 0);
        }
    }
//
//    //暂时不用
//    private void paintScale(Graphics g)  {
//        //g.setColor(DefaultLookup.getColor(slider, this, "Slider.tickColor", Color.black));
//        int YPos = trackRect.y + trackRect.height;
//        g.setColor(Color.white);
//        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
//            //g.translate(0, tickBounds.y);
//
//            if (slider.getMinorTickSpacing() > 0) {
//                int value = slider.getMinimum();
//
//                while ( value <= slider.getMaximum() ) {
//                    int xPos = xPositionForValue(value);
//                    g.drawLine( xPos, YPos, xPos, YPos+8 );
//
//                    // Overflow checking
//                    if (Integer.MAX_VALUE - slider.getMinorTickSpacing() < value) {
//                        break;
//                    }
//
//                    value += slider.getMinorTickSpacing();
//                }
//            }
//
//            if (slider.getMajorTickSpacing() > 0) {
//                int value = slider.getMinimum();
//
//                while ( value <= slider.getMaximum() ) {
//                    int xPos = xPositionForValue(value);
//                    g.drawLine( xPos, YPos, xPos, YPos+15 );
//
//                    // Overflow checking
//                    if (Integer.MAX_VALUE - slider.getMajorTickSpacing() < value) {
//                        break;
//                    }
//
//                    value += slider.getMajorTickSpacing();
//                }
//            }
//
//            //g.translate( 0, -tickBounds.y);
//        } 
//    }

    protected void paintMinorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
        g.drawLine( x, 0, x, 8 );
    }

    protected void paintMajorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
        g.drawLine( x, 0, x, 15 );
    }
    
    /**
     * Called for every label in the label table.  Used to draw the labels for horizontal sliders.
     * The graphics have been translated to labelRect.y already.
     * @see JSlider#setLabelTable
     */
    protected void paintHorizontalLabel( Graphics g, int value, Component label ) {
        int labelCenter = xPositionForValue( value );
        int labelLeft = labelCenter - (label.getPreferredSize().width / 2);
        g.translate( labelLeft, 0 );
        label.setForeground(Color.white);
        label.paint( g );
//        int XPos = valueConvertToSliderPosition(value);
//        g.drawLine( XPos, label.getY(), XPos, label.getY() - 8 );
        g.translate( -labelLeft, 0 );
    }

    protected int xPositionForValue( int value )    {
        int min = slider.getMinimum();
        int max = slider.getMaximum();
        int trackLength = trackRect.width;
        double valueRange = (double)max - (double)min;
        double pixelsPerValue = (double)trackLength / valueRange;
        int trackLeft = trackRect.x;
        int trackRight = trackRect.x + (trackRect.width - 1);
        int xPosition;

        if ( !drawInverted() ) {
            xPosition = trackLeft;
            xPosition += Math.round( pixelsPerValue * ((double)value - min) );
        }
        else {
            xPosition = trackRight;
            xPosition -= Math.round( pixelsPerValue * ((double)value - min) );
        }

        xPosition = Math.max( trackLeft, xPosition );
        xPosition = Math.min( trackRight, xPosition );

        return xPosition;
    }
    /**
     * Called for every label in the label table.  Used to draw the labels for vertical sliders.
     * The graphics have been translated to labelRect.x already.
     * @see JSlider#setLabelTable
     */
//    protected void paintVerticalLabel( Graphics g, int value, Component label ) {
//        int labelCenter = yPositionForValue( value );
//        int labelTop = labelCenter - (label.getPreferredSize().height / 2);
//        g.translate( 0, labelTop );
//        label.paint( g );
//        
//        g.translate( 0, -labelTop );
//    }

 

    /**
     * This function is called when a mousePressed was detected in the track, not
     * in the thumb.  The default behavior is to scroll by block.  You can
     *  override this method to stop it from scrolling or to add additional behavior.
     */
    protected void scrollDueToClickInTrack( int dir) {
        //这种处理很重要，就是不让滑块在鼠标点击滑道的时候来回晃动
        //scrollByBlock( dir );
    }
    
    //添加滑道上Slider滑道上特殊颜色显示区域的起始/终止值
    public void addSpecialTrackSection(Point point){
        
        listSpecialTrackSection.add(new Point(valueConvertToPosition(point.x),valueConvertToPosition(point.y)));
    }


    /**
     * @return the normalTrackSection将滑道Track的长度位置值转换为JSlider的Value值
     */
    public Point getNormalTrackSection() {
        return new Point(positionConvertToValue(normalTrackSection.x),positionConvertToValue(normalTrackSection.y));
    }

    /**
     * @param normalTrackSection 存储JSlider的Value值，起始和结束值，并将其转化为其滑道Track的长度位置值
     */
    public void setNormalTrackSection(Point normalTrackSection) {
        this.normalTrackSection.x = valueConvertToPosition(normalTrackSection.x);
        this.normalTrackSection.y = valueConvertToPosition(normalTrackSection.y);
    }
    /**
        * 函数:      positionConvertToValue
        * 函数描述:  将JSlider的滑道Track的长度位置值，转化为其Value值
    */
    private int positionConvertToValue(int Position){
        double Position2 = (double)Position;
        double TrackWidth2 = (double)trackRect.width;
        double MaxValue = (double)slider.getMaximum();
        
        return (int) Math.round(Position2 * MaxValue/TrackWidth2);
    }
    /**
        * 函数:      positionConvertToValue
        * 函数描述:  将JSlider的Value值，转化为其滑道Track的长度位置值
    */
    private int valueConvertToPosition(int Value){
        double Value2 = (double)Value;
        double TrackWidth2 = (double)trackRect.width;
        double MaxValue = (double)slider.getMaximum();
        
        return (int) Math.round(Value2 * TrackWidth2/MaxValue);
    }
    /**
        * 函数:      positionConvertToValue
        * 函数描述:  将JSlider的滑道Track的长度位置值，转化为其Value值
    */
    private int valueConvertToSliderPosition(int Value){
        double SliderWidth = (double)slider.getWidth();
        double Value2 = (double)Value;
        double TrackWidth = (double)trackRect.width;
        double MaxValue = (double)slider.getMaximum();

        return (int) Math.round(trackRect.x+Value2 * TrackWidth/MaxValue);
        //return (int) Math.round((SliderWidth-TrackWidth)/2+Value2 * TrackWidth/MaxValue);
    }
    
    /**
        * 函数:      initialTrackParas
        * 函数描述:  将JSlider的滑道的显示参数初始化
    */
    public void initialTrackParas(){
        this.normalTrackSection.x = 0;
        this.normalTrackSection.y = 0;
        listSpecialTrackSection.clear();
    }
}
