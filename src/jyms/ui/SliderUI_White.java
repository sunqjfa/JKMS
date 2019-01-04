package jyms.ui;


import java.awt.*;
import java.util.ArrayList;


import javax.swing.*;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicSliderUI;

public class SliderUI_White extends BasicSliderUI{
    //存储需要正常显示颜色的区域起始/终止值（在这之前需要先显示默认颜色）
    private Point normalTrackSection = new Point();
    //借用Point这个对象用来存储Slider滑道上特殊颜色显示区域的起始/终止值
    //listSpecialTrackSection存储所有特殊区域的值的集合。在添加之前必须经过value和长度的换算
    private ArrayList<Point> listSpecialTrackSection = new ArrayList<>();
    
    private Color shadowColor;
    private Color highlightColor;
    private Color focusColor;
    
    /**
     * Whther or not sameLabelBaselines is up to date.
     */
    private boolean checkedLabelBaselines;
    private transient boolean isDragging;
    private int lastValue;

    public SliderUI_White(JSlider b) {
        super(b);
    }
    
    @Override
    protected void installDefaults( JSlider slider ) {
        LookAndFeel.installBorder(slider, "Slider.border");
        LookAndFeel.installColorsAndFont(slider, "Slider.background_white",
                                         "Slider.foreground_white", "Slider.font");
        highlightColor = UIManager.getColor("Slider.highlight_white");

        shadowColor = UIManager.getColor("Slider.shadow_white");
        focusColor = UIManager.getColor("Slider.focus_white");

        focusInsets = (Insets)UIManager.get( "Slider.focusInsets" );
        // use default if missing so that BasicSliderUI can be used in other
        // LAFs like Nimbus
        if (focusInsets == null) focusInsets = new InsetsUIResource(2,2,2,2);
    }

    /**
     * Called for every label in the label table.  Used to draw the labels for horizontal sliders.
     * The graphics have been translated to labelRect.y already.
     * @param g
     * @param value
     * @param label
     * @see JSlider#setLabelTable
     */
    @Override
    protected void paintHorizontalLabel( Graphics g, int value, Component label ) {
        int labelCenter = xPositionForValue( value );
        int labelLeft = labelCenter - (label.getPreferredSize().width / 2);
        g.translate( labelLeft, 0 );
        label.setForeground(Color.white);
        label.paint( g );
        g.translate( -labelLeft, 0 );
    }
    

    
}
