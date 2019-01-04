/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;

import java.awt.BorderLayout;  
import java.awt.Color;  
import java.awt.Cursor;
import java.awt.Dimension;  
import java.awt.FlowLayout;  
import java.awt.Font;  
import java.awt.Frame;  
import java.awt.GridLayout;  
import java.awt.Point;  
import java.awt.Toolkit;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.text.ParseException;  
import java.text.ParsePosition;
import java.text.SimpleDateFormat;  
import java.util.Calendar;  
import java.util.Date;  
import java.util.GregorianCalendar;
import javax.swing.BorderFactory;
  
import javax.swing.JButton;  
import javax.swing.JDialog;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  
import javax.swing.JSpinner;  
import javax.swing.SpinnerNumberModel;  
import javax.swing.SwingConstants;  
import javax.swing.SwingUtilities;  
import javax.swing.border.LineBorder;  
import javax.swing.event.ChangeEvent;  
import javax.swing.event.ChangeListener;  
  
/** 
    * YouAreStupid 收集网上靠谱的例子，修改后的Swing日期 
    * 时间选择器,因为修改时间匆忙，希望有时间的朋友继续改进。 
    * 例子原作者:zjw 
    * 修改/完善：YouAreStupid 
    * 修改完善：sqj，2017.3.13日
*/
public class DateChooserJButton extends JButton {  
  
    private DateChooser dateChooser = null;  
    private String preLabel = "";  
    private String originalText = null;  
    private SimpleDateFormat sdf = null;  
    
    public static boolean tFlag = true;
    public static int currAccYear = 0;

    public static int currAccMont = 0;

    public static int currAccDay = 0;
  
    public DateChooserJButton() {  
        this(getNowDate());  
    }  
  
    public DateChooserJButton(String dateString) {  
        this();  
        setText(getDefaultDateFormat(), dateString);  
        //保存原始是日期时间  
        initOriginalText(dateString);  
    }  
  
    public DateChooserJButton(SimpleDateFormat df, String dateString) {  
        this();  
        setText(df, dateString);  
  
        //记忆当前的日期格式化器  
        this.sdf = df;  
  
        //记忆原始日期时间  
        Date originalDate = null;  
        try {  
            originalDate = df.parse(dateString);  
        } catch (ParseException ex) {  
            originalDate = getNowDate();  
        }  
        initOriginalText(originalDate);  
    }  
  
    public DateChooserJButton(Date date) {  
        this("", date);  
        //记忆原始日期时间  
        initOriginalText(date);  
    }  
  
    public DateChooserJButton(String preLabel, Date date) {  
        if (preLabel != null) {  
            this.preLabel = preLabel;  
        }  
        setDate(date);  
        //记忆原始是日期时间  
        initOriginalText(date); 
        
        //现在加上JButton背景色和前景色
        setBackground(new Color(64,64,64));  
        setForeground(Color.white);  
        setFont(new Font("微软雅黑", Font.PLAIN, 16));  //new java.awt.Font("微软雅黑", 0, 16)
  
//        setBorder(null);  
        setCursor(new Cursor(Cursor.HAND_CURSOR));  
        super.addActionListener(new ActionListener() {  
  
            @Override  
            public void actionPerformed(ActionEvent e) {  
                if (dateChooser == null) {  
                    dateChooser = new DateChooser();  
//                    dateChooser.updateUI();
                }  
                Point p = getLocationOnScreen();  
                p.y = p.y + 30;  
                dateChooser.showDateChooser(p);  
            }  
        });  
    }  
    
    public  DateChooser getDateChooser(){
        return new DateChooser(true);
    }
    private static Date getNowDate() {  
        return Calendar.getInstance().getTime();  
    }  
  
    private static SimpleDateFormat getDefaultDateFormat() {  
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    }  
  
    /** 
     * 得到当前使用的日期格式化器 
     * @return 日期格式化器 
     */  
    public SimpleDateFormat getCurrentSimpleDateFormat(){  
        if(this.sdf != null){  
            return sdf;  
        }else{  
            return getDefaultDateFormat();  
        }  
    }  
  
  
    //保存原始是日期时间  
    private void initOriginalText(String dateString) {  
        this.originalText = dateString;  
    }  
  
    //保存原始是日期时间  
    private void initOriginalText(Date date) {  
        this.originalText = preLabel + getDefaultDateFormat().format(date);  
    }  
  
    /**  
     * 得到当前记忆的原始日期时间 
     * @return 当前记忆的原始日期时间（未修改前的日期时间） 
     */  
    public String getOriginalText() {  
        return originalText;  
    }  
  
    // 覆盖父类的方法  
    @Override  
    public void setText(String s) {  
        Date date;  
        try {  
            date = getDefaultDateFormat().parse(s);  
        } catch (ParseException e) {  
            date = getNowDate();  
        }  
        setDate(date);  
    }  
  
    public void setText(SimpleDateFormat df, String s) {  
        Date date;  
        try {  
            date = df.parse(s);  
        } catch (ParseException e) {  
            date = getNowDate();  
        }  
        setDate(date);  
    }  
  
    public void setDate(Date date) {  

        super.setText(preLabel + getDefaultDateFormat().format(date));  
    }  
  
    public Date getDate() {  
        String dateString = getText().substring(preLabel.length());  
        try {  
            SimpleDateFormat currentSdf = getCurrentSimpleDateFormat();  
            return currentSdf.parse(dateString);  
        } catch (ParseException e) {  
            return getNowDate();  
        }  
    }  
    
   
    public int getDay(){
        return Integer.parseInt(getText().substring(8, 10));
    }
    
    /**   
     * 覆盖父类的方法使之无效 
     * @param listener 响应监听器 
     */  
    @Override  
    public void addActionListener(ActionListener listener) {  
    }  
  
    /** 
     * 内部类，主要是定义一个JPanel，然后把日历相关的所有内容填入本JPanel， 
     * 然后再创建一个JDialog，把本内部类定义的JPanel放入JDialog的内容区 
     */  
    private class DateChooser extends JPanel implements ActionListener, ChangeListener {  
  
        Font UnifiedFont = new Font("微软雅黑", Font.PLAIN, 16);
        int startYear = 1980; // 默认【最小】显示年份  
        int lastYear = 2050; // 默认【最大】显示年份  
        int width = 480; // 界面宽度
        int height = 320; // 界面高度  
        Color Cblack = new Color(32,32,32);
        Color backGroundColor = Color.gray; // 底色  -
//        // 月历表格配色----------------//  
//        Color palletTableColor = Cblack; // 日历表底色  -
//        Color todayBackColor = Color.orange; // 今天背景色 
//        
//        Color dateFontColor = Color.white; // 日期文字色  -
//        Color weekFontColor = Color.white; // 星期文字色  
//        Color weekendFontColor = Color.red; // 日期、星期周末文字色  
//        // 控制条配色------------------//  
//        Color controlLineColor = Cblack; // 控制条底色  -
//        Color controlTextColor = Color.white; // 控制条标签文字色  
//        Color rbFontColor = Color.white; // RoundBox文字色  
//        Color rbBorderColor = Cblack; // RoundBox边框色 - 
//        Color rbButtonColor = Color.pink; // RoundBox按钮色  
//        Color rbBtFontColor = Color.red; // RoundBox按钮文字色  
        // 月历表格配色----------------//  
        Color palletTableColor = Color.white; // 日历表底色  
        Color todayBackColor = Color.orange; // 今天背景色  
        Color weekFontColor = Color.blue; // 星期文字色  
        Color dateFontColor = Color.black; // 日期文字色  
        Color weekendFontColor = Color.red; // 周末文字色  
        // 控制条配色------------------//  
        Color controlLineColor = Color.white; // 控制条底色  
        Color controlTextColor = Color.BLACK; // 控制条标签文字色  
        Color rbFontColor = Color.white; // RoundBox文字色  
        Color rbBorderColor = Color.red; // RoundBox边框色  
        Color rbButtonColor = Color.pink; // RoundBox按钮色  
        Color rbBtFontColor = Color.red; // RoundBox按钮文字色  
        /** 点击DateChooserButton时弹出的对话框，日历内容在这个对话框内 */  
        JDialog dialog;  
        JSpinner yearSpin;  
        JSpinner monthSpin;  
        JSpinner daySpin;  
        JSpinner hourSpin;  
        JSpinner minuteSpin;  
        JSpinner secondSpin;  
        JButton[][] daysButton = new JButton[6][7];  
  
        DateChooser() {  
  
            setLayout(new BorderLayout());  
            setBorder(new LineBorder(backGroundColor, 2));  
            setBackground(backGroundColor);  
  
            JPanel topYearAndMonth = createYearAndMonthPanel();  
            add(topYearAndMonth, BorderLayout.NORTH);  
            JPanel centerWeekAndDay = createWeekAndDayPanal();  
            add(centerWeekAndDay, BorderLayout.CENTER);  
            JPanel buttonBarPanel = createButtonBarPanel();  
            this.add(buttonBarPanel, java.awt.BorderLayout.SOUTH);  
            flushWeekAndDay(); 
        }  
        
        DateChooser(boolean IfOutput) {  
  
            setLayout(new BorderLayout());  
            setBorder(new LineBorder(backGroundColor, 2));  
            setBackground(backGroundColor);  
            
  
            JPanel topYearAndMonth = createYearAndMonthPanel();  
            add(topYearAndMonth, BorderLayout.NORTH);  
            JPanel centerWeekAndDay = createWeekAndDayPanal();  
            add(centerWeekAndDay, BorderLayout.CENTER);  
            flushWeekAndDay();  
            daySpin.setValue(getDay());
        }  
        
        private JPanel createYearAndMonthPanel() {  
            Calendar c = getCalendar();  
            int currentYear = c.get(Calendar.YEAR);  
            int currentMonth = c.get(Calendar.MONTH) + 1;  
            int currentDay = c.get(Calendar.DATE );
            int currentHour = c.get(Calendar.HOUR_OF_DAY);  
            int currentMinute = c.get(Calendar.MINUTE);  
            int currentSecond = c.get(Calendar.SECOND);  
            currAccYear = currentYear;

            currAccMont = currentMonth;

            currAccDay = currentDay;
  
            JPanel result = new JPanel();  
            result.setLayout(new FlowLayout());  
            result.setBackground(controlLineColor);  
  
            yearSpin = new JSpinner(new SpinnerNumberModel(currentYear, startYear, lastYear, 1));  
//            yearSpin.setPreferredSize(new Dimension(48, 20));  .setBorder(BorderFactory.
            yearSpin.setPreferredSize(new Dimension(70, 30));  
            yearSpin.setName("Year");
            yearSpin.setFont(UnifiedFont);
            yearSpin.setEditor(new JSpinner.NumberEditor(yearSpin, "####"));  
            yearSpin.setBorder(BorderFactory.createLineBorder(controlLineColor));
            yearSpin.setBorder(null);
            
            yearSpin.addChangeListener(this);  
            result.add(yearSpin);  
  
            JLabel yearLabel = new JLabel("年");  
            yearLabel.setFont(UnifiedFont);
            yearLabel.setForeground(controlTextColor);  
            result.add(yearLabel);  
  
            monthSpin = new JSpinner(new SpinnerNumberModel(currentMonth, 1, 12, 1));  
            monthSpin.setPreferredSize(new Dimension(48, 30));  
            monthSpin.setName("Month");  
            monthSpin.setFont(UnifiedFont);
            monthSpin.setEditor(new JSpinner.NumberEditor(monthSpin, "##"));  
            monthSpin.addChangeListener(this);  
            result.add(monthSpin);  
  
            JLabel monthLabel = new JLabel("月");  
            monthLabel.setFont(UnifiedFont);
            monthLabel.setForeground(controlTextColor);  
            result.add(monthLabel);  
  

            int LastDay = getMaxDayByYearMonth(currentYear, currentMonth);
            //如果这里要能够选择,会要判断很多东西,比如每个月分别由多少日,以及闰年问题.所以,就干脆把Enable设为false  
            
            daySpin = new JSpinner();//new SpinnerNumberModel(currentMonth, 1, 31, 1));  new SpinnerNumberModel(currentDay, 1, LastDay, 1)
            daySpin.setPreferredSize(new Dimension(48, 30));  
            daySpin.setName("Day");  
            daySpin.setFont(UnifiedFont);
            //daySpin.setEditor(new JSpinner.NumberEditor(daySpin, "##"));  //没想到这一句没有注释，从3月31日，修改月份为2时，日期没有改变为28日。
            daySpin.addChangeListener(this);  
            modifyDaySpinModel(currentDay, LastDay);
            //daySpin.setEnabled(false);  
            //daySpin.setToolTipText("请下下面的日历面板中进行选择哪一天！");  //没想到这一句没有注释，从3月31日，修改月份为2时，日期没有改变为28日。
            
            result.add(daySpin);  
  
            JLabel dayLabel = new JLabel("日"); 
            dayLabel.setFont(UnifiedFont);
            dayLabel.setForeground(controlTextColor);  
            result.add(dayLabel);  
  
            hourSpin = new JSpinner(new SpinnerNumberModel(currentHour, 0, 23, 1));  
            hourSpin.setPreferredSize(new Dimension(48, 30));  
            hourSpin.setName("Hour");  
            hourSpin.setFont(UnifiedFont);
            hourSpin.setEditor(new JSpinner.NumberEditor(hourSpin, "##"));  
            hourSpin.addChangeListener(this);  
            result.add(hourSpin);  
  
            JLabel hourLabel = new JLabel("时");  
            hourLabel.setFont(UnifiedFont);
            hourLabel.setForeground(controlTextColor);  
            result.add(hourLabel);  
  
            minuteSpin = new JSpinner(new SpinnerNumberModel(currentMinute, 0, 59, 1));  
            minuteSpin.setPreferredSize(new Dimension(48, 30));  
            minuteSpin.setName("Minute");  
            minuteSpin.setFont(UnifiedFont);
            minuteSpin.setEditor(new JSpinner.NumberEditor(minuteSpin, "##"));  
            minuteSpin.addChangeListener(this);  
            result.add(minuteSpin);  
  
            JLabel minuteLabel = new JLabel("分");  
            minuteLabel.setFont(UnifiedFont);
            minuteLabel.setForeground(controlTextColor);  
            result.add(minuteLabel);  
  
            secondSpin = new JSpinner(new SpinnerNumberModel(currentSecond, 0, 59, 1));  
            secondSpin.setPreferredSize(new Dimension(48, 30));  
            secondSpin.setName("Second");  
            secondSpin.setFont(UnifiedFont);
            secondSpin.setEditor(new JSpinner.NumberEditor(secondSpin, "##"));  
            secondSpin.addChangeListener(this);  
            result.add(secondSpin);  
  
            JLabel secondLabel = new JLabel("秒");  
            secondLabel.setFont(UnifiedFont);
            secondLabel.setForeground(controlTextColor);  
            result.add(secondLabel);  
  
            return result;  
        }  
  
        private JPanel createWeekAndDayPanal() {  
            String colname[] = {"日", "一", "二", "三", "四", "五", "六"};  
            JPanel result = new JPanel();  
            // 设置固定字体，以免调用环境改变影响界面美观  
            result.setFont(new Font("微软雅黑", Font.PLAIN, 16));  
            result.setLayout(new GridLayout(7, 7));  
            result.setBackground(controlLineColor);  
            JLabel cell;  
  
            for (int i = 0; i < 7; i++) {  
                cell = new JLabel(colname[i]);  
                cell.setFont(new Font("微软雅黑", Font.PLAIN, 16));  
                cell.setHorizontalAlignment(JLabel.CENTER); /////////////////////////////////// 
                if (i == 0 || i == 6) {  
                    cell.setForeground(weekendFontColor);  
                } else {  
                    cell.setForeground(weekFontColor);  
                }  
                result.add(cell);  
            }  
  
            int actionCommandId = 0;  
            for (int i = 0; i < 6; i++) {  
                for (int j = 0; j < 7; j++) {  
                    JButton numberButton = new JButton();  
                    numberButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));  
                    numberButton.setBorder(null);
                    numberButton.setBorderPainted(false);
                    numberButton.setContentAreaFilled(false);  
                    numberButton.setHorizontalAlignment(SwingConstants.CENTER);  ///////////////////////
                    numberButton.setActionCommand(String.valueOf(actionCommandId));  
                    numberButton.addActionListener(this);  
                    numberButton.setBackground(palletTableColor);  
                    numberButton.setForeground(dateFontColor);  
                    if (j == 0 || j == 6) {  
                        numberButton.setForeground(weekendFontColor);  
                    } else {  
                        numberButton.setForeground(dateFontColor);  
                    }  
                    daysButton[i][j] = numberButton;  
                    result.add(numberButton);  
                    actionCommandId++;  
                }  
            }  
  
            return result;  
        }  
  
        /** 得到DateChooserButton的当前text，本方法是为按钮事件匿名类准备的。 */  
        public String getTextOfDateChooserButton() {  
            return getText();  
        }  
        
        /** 恢复DateChooserButton的原始日期时间text，本方法是为按钮事件匿名类准备的。 */  
        public void restoreTheOriginalDate() { 
                    	tFlag = true;
            String originalText = getOriginalText();  
            setText(originalText);  
        }  
  
        private JPanel createButtonBarPanel() {  
            JPanel panel = new JPanel();  
            panel.setLayout(new java.awt.GridLayout(1, 2));  
  
            JButton ok = new JButton("确定");  
            ok.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ok2.png"));
            ok.setFont(new Font("微软雅黑", Font.PLAIN, 16));  
            ok.addActionListener(new ActionListener() {  
  
                @Override  
                public void actionPerformed(ActionEvent e) {  
                    //记忆原始日期时间  
                    initOriginalText(getTextOfDateChooserButton());  
                    //隐藏日历对话框  
                    dialog.setVisible(false);  
                }  
            });  
            panel.add(ok);  
  
            JButton cancel = new JButton("取消");  
            cancel.setIcon(ImageIconBufferPool.getInstance().getImageIcon("cancel2.png"));
            cancel.setFont(new Font("微软雅黑", Font.PLAIN, 16));  
            cancel.addActionListener(new ActionListener() {  
  
                @Override  
                public void actionPerformed(ActionEvent e) {  
                    //恢复原始的日期时间  
                    restoreTheOriginalDate();  
                    //隐藏日历对话框  
                    dialog.setVisible(false);  
                }  
            });  
  
            panel.add(cancel);  
            return panel;  
        }  
  
        private JDialog createDialog(Frame owner) {  
            JDialog result = new JDialog(owner, "日期时间选择", false);  
            //添加窗口失去焦点事件
            result.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
                public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                }
                public void windowLostFocus(java.awt.event.WindowEvent evt) {
                    //恢复原始的日期时间  
                            restoreTheOriginalDate();  
                            //隐藏日历对话框  
                            dialog.setVisible(false);  
                }
            });
            result.setUndecorated(true);
            result.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);  
            JPanel jPanelFirst = new JPanel();
            jPanelFirst.setBackground(controlLineColor); 
            jPanelFirst.setBorder(new LineBorder(backGroundColor, 2));  

            JLabel jLabel2 = new JLabel();
            jLabel2.setFont(new java.awt.Font("微软雅黑", 1, 18)); // NOI18N
            jLabel2.setForeground(controlTextColor);  
            jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel2.setText("日期时间选择");
            JButton jButtonExit= new JButton();
            jButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jyms/image/close.png"))); // NOI18N
            jButtonExit.setBorder(null);
            jButtonExit.setBorderPainted(false);
            jButtonExit.setContentAreaFilled(false);
            jButtonExit.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    //恢复原始的日期时间  
                        restoreTheOriginalDate();  
                        //隐藏日历对话框  
                        dialog.setVisible(false);  
                }
            });

            javax.swing.GroupLayout jPanelFirstLayout = new javax.swing.GroupLayout(jPanelFirst);
            jPanelFirst.setLayout(jPanelFirstLayout);
            jPanelFirstLayout.setHorizontalGroup(
                jPanelFirstLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFirstLayout.createSequentialGroup()
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
            );
            jPanelFirstLayout.setVerticalGroup(
                jPanelFirstLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelFirstLayout.createSequentialGroup()
                    .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(20, Short.MAX_VALUE))
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );

            result.getContentPane().add(jPanelFirst, BorderLayout.PAGE_START);
        
            result.getContentPane().add(this, BorderLayout.CENTER);  
            result.pack();  
            result.setSize(width, height); 
            
            return result;  
        }  
  
        void showDateChooser(Point position) {  
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(DateChooserJButton.this);  
            if (dialog == null || dialog.getOwner() != owner) {  
                dialog = createDialog(owner);  
            }  
            dialog.setLocation(getAppropriateLocation(owner, position));  
//            flushWeekAndDay(); 
            daySpin.setValue(getDay());//和最近下载的不同

            dialog.setVisible(true);  
        }  
  
        Point getAppropriateLocation(Frame owner, Point position) {  
            Point result = new Point(position);  
            Point p = owner.getLocation();  
            int offsetX = (position.x + width) - (p.x + owner.getWidth());  
            int offsetY = (position.y + height) - (p.y + owner.getHeight());  
  
            if (offsetX > 0) {  
                result.x -= offsetX;  
            }  
  
            if (offsetY > 0) {  
                result.y -= offsetY;  
            }  
  
            return result;  
        }  
  
        private Calendar getCalendar() {  
            Calendar result = Calendar.getInstance();  
            result.setTime(getDate());  
            return result;  
        }  
  
        private int getSelectedYear() {  
            return ((Integer) yearSpin.getValue()).intValue();  
        }  
  
        private int getSelectedMonth() {  
            return ((Integer) monthSpin.getValue()).intValue();  
        }  
        private int getSelectedDay() {

            return ((Integer) daySpin.getValue()).intValue();

        }
  
        private int getSelectedHour() {  
            return ((Integer) hourSpin.getValue()).intValue();  
        }  
  
        private int getSelectedMinite() {  
            return ((Integer) minuteSpin.getValue()).intValue();  
        }  
  
        private int getSelectedSecond() {  
            return ((Integer) secondSpin.getValue()).intValue();  
        }  
        
        
  
        private void dayColorUpdate(boolean isOldDay) {  
            Calendar c = getCalendar();  
            int day = c.get(Calendar.DAY_OF_MONTH);  
            c.set(Calendar.DAY_OF_MONTH, 1);  
            int actionCommandId = day - 2 + c.get(Calendar.DAY_OF_WEEK);  
            int i = actionCommandId / 7;  
            int j = actionCommandId % 7;  
            Color OldColor = dateFontColor;
            if (j==0 || j == 6) OldColor = weekendFontColor;
            if (isOldDay) {  
                daysButton[i][j].setForeground(OldColor);  //weekendFontColor
            } else {  
                daysButton[i][j].setForeground(todayBackColor);  
            }  
        }  
  
        private void flushWeekAndDay() {  
            Calendar c = getCalendar();  
            c.set(Calendar.DAY_OF_MONTH, 1);  
            int maxDayNo = c.getActualMaximum(Calendar.DAY_OF_MONTH);  
            int dayNo = 2 - c.get(Calendar.DAY_OF_WEEK);  
            for (int i = 0; i < 6; i++) {  
                for (int j = 0; j < 7; j++) {  
                    String s = "";  
                    if (dayNo >= 1 && dayNo <= maxDayNo) {  
                        s = String.valueOf(dayNo);  
                    }  
                    daysButton[i][j].setText(s);  
                    dayNo++;  
                }  
            }  
            dayColorUpdate(false);  
        }  
  
        /** 
         * 选择日期时的响应事件 
         */  
        @Override  
        public void stateChanged(ChangeEvent e) {  
//            JSpinner source = (JSpinner) e.getSource();  
            Calendar c = getCalendar();  
            if (e.getSource()==hourSpin) {  //.equals("Hour")
                c.set(Calendar.HOUR_OF_DAY, getSelectedHour());  
                setDate(c.getTime());  
                return;  
            }  
            //if (source.getName().equals("Minute")) {  
            if (e.getSource()==minuteSpin) {  
                c.set(Calendar.MINUTE, getSelectedMinite());  
                setDate(c.getTime());  
                return;  
            } 
            
            //if (source.getName().equals("Second")) {  
            if (e.getSource()==secondSpin) {  
                c.set(Calendar.SECOND, getSelectedSecond());  
                setDate(c.getTime());  
                return;  
            }  
  

            if(e.getSource()==monthSpin){

                int LastDay = getLastDay();

                int currentDay = c.get(Calendar.DATE );

                modifyDaySpinModel( currentDay, LastDay);
                c.set(Calendar.MONTH, getSelectedMonth() - 1);
                c.set(Calendar.DATE,getSelectedDay() );

            }

            

            if(e.getSource()==yearSpin){
            	int LastDay = getLastDay();

                int currentDay = c.get(Calendar.DATE );

                modifyDaySpinModel( currentDay, LastDay);
                c.set(Calendar.YEAR, getSelectedYear());

            }

            if(e.getSource()==daySpin){
                c.set(Calendar.DATE,getSelectedDay() );
            }

            dayColorUpdate(true);
            setDate(c.getTime());

            flushWeekAndDay();
        }  
  
        /** 
         * 选择日期时的响应事件 
        */  
        @Override  
        public void actionPerformed(ActionEvent e) {  
            JButton source = (JButton) e.getSource();  
            if (source.getText().length() == 0) {  
                return;  
            }  
            dayColorUpdate(true);  
            source.setForeground(todayBackColor);  
            int newDay = Integer.parseInt(source.getText());  
            Calendar c = getCalendar();  
            c.set(Calendar.DAY_OF_MONTH, newDay);  
            setDate(c.getTime());  
            //把daySpin中的值也变了  
            daySpin.setValue(Integer.valueOf(newDay));  
        }  
        
        private int getLastDay(){
            currAccYear = getSelectedYear();
            currAccMont = getSelectedMonth();
            
            return getMaxDayByYearMonth(currAccYear,currAccMont);

        }
        
        private void modifyDaySpinModel(int CurrentDay, int LastDay){
            switch (LastDay) {
                case 28:
                case 29:
                case 30:
                    if(CurrentDay>LastDay){
                        daySpin.setModel(new SpinnerNumberModel(LastDay, 1,LastDay, 1));
                    }else{
                        daySpin.setModel(new SpinnerNumberModel(CurrentDay, 1,LastDay, 1));
                    }
                    break;
                default:
                    daySpin.setModel(new SpinnerNumberModel(CurrentDay, 1,LastDay, 1));
                    break;
            }
        }
        
        /**
            * 获得某个月最大天数
            *
            * @param year 年份
            * @param month 月份 (1-12)
            * @return 某个月最大天数
        */
        public int getMaxDayByYearMonth(int year, int month) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            return calendar.getActualMaximum(Calendar.DATE);
        }
    }
    
    
    
//    /**
//
//	 * 获取一个月的最后一天
//
//	 * 
//
//	 * @param dat
//
//	 * @return
//
//	 */
//
//  public static String getEndDateOfMonth(String dat) {// yyyy-MM-dd
//
//		String str = dat.substring(0, 8);
//
//		String month = dat.substring(5, 7);
//
//		int mon = Integer.parseInt(month);
//
//		if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8
//
//				|| mon == 10 || mon == 12) {
//
//			str += "31";
//
//		} else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
//
//			str += "30";
//
//		} else {
//
//			if (isLeapYear(dat)) {
//
//				str += "29";
//
//			} else {
//
//				str += "28";
//
//			}
//
//		}
//
//		return str;
//  }

	
                
        /**
            * 判断是否润年
            * @param ddate
            * @return
        */

	public static boolean isLeapYear(String ddate) {

		/**
		 * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
		 * 3.能被4整除同时能被100整除则不是闰年
		 */

		Date d = strToDate(ddate);

		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();

		gc.setTime(d);

		int year = gc.get(Calendar.YEAR);

		if ((year % 400) == 0) return true;
		else if ((year % 4) == 0) {
			if ((year % 100) == 0) return false;
                        else return true;
		} else return false;

	}
        
        /**

	 * 将短时间格式字符串转换为时间 yyyy-MM-dd

	 * 

	 * @param strDate

	 * @return

	 */

	public static Date strToDate(String strDate) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		ParsePosition pos = new ParsePosition(0);

		Date strtodate = formatter.parse(strDate, pos);

		return strtodate;

	}  
  
    /** 
     * 测试方法 
     */  
    public static void main(String[] args) {  
        JFrame mainFrame = new JFrame("测试");  
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        mainFrame.setSize(300, 300);  
        mainFrame.setLayout(new java.awt.BorderLayout());  
        mainFrame.add(new DateChooserJButton(), java.awt.BorderLayout.CENTER);  
  
        Toolkit kit = Toolkit.getDefaultToolkit();  
        Dimension screenSize = kit.getScreenSize();  
        int width = (int) screenSize.getWidth();  
        int height = (int) screenSize.getHeight();  
        int w = mainFrame.getWidth();  
        int h = mainFrame.getHeight();  
        mainFrame.setLocation((width - w) / 2, (height - h) / 2);  
  
        mainFrame.setVisible(true);  
    }  
}  
