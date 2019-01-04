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
import java.awt.Font;  
import java.awt.Frame;  
import java.awt.GridLayout;  
import java.awt.Point;  
import java.awt.Toolkit;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;  
import java.text.SimpleDateFormat;  
import java.util.Calendar;  
import java.util.Date;  
import javax.swing.BorderFactory;
  
import javax.swing.JButton;  
import javax.swing.JDialog;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  
import javax.swing.JSpinner;  
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;  
import javax.swing.SwingUtilities;  
import javax.swing.border.LineBorder;  
import javax.swing.event.ChangeEvent;  
import javax.swing.event.ChangeListener;  
import jyms.CommonParas;
import jyms.MyLocales;
  
/** 
    * YouAreStupid 收集网上靠谱的例子，修改后的Swing日期 
    * 时间选择器,因为修改时间匆忙，希望有时间的朋友继续改进。 
    * 例子原作者:zjw 
    * 修改/完善：YouAreStupid 
    * 修改完善：sqj，2017.3.13日
*/
public class DateChooserJButtonE extends JButton {  
  
    private DateChooser dateChooser = null;  
    private String preLabel = "";  
    private String originalText = null;  
    private DateFormat sdf = null;  
    
    public static boolean tFlag = true;

  
    public DateChooserJButtonE() {  
        this(getNowDate());  
    }  
  
    public DateChooserJButtonE(String dateString) {  
        this();  
        setText(getCurrentSimpleDateFormat(), dateString);  
        //保存原始是日期时间  
        initOriginalText(dateString);  
    }  
  
    public DateChooserJButtonE(DateFormat df, String dateString) {  
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
  
    public DateChooserJButtonE(Date date) {  
        this("", date);  
        //记忆原始日期时间  
        initOriginalText(date);  
    }  
  
    public DateChooserJButtonE(String preLabel, Date date) {  
        if (preLabel != null) {  
            this.preLabel = preLabel;  
        }  
        sdf = CommonParas.SysParas.sysLocales.getDateTimeFormat();//后来加进去的
        setDate(date);  
        //记忆原始是日期时间  
        initOriginalText(date); 
        
        //现在加上JButton背景色和前景色
        setBackground(new Color(64,64,64));  
        setForeground(Color.white);  
        setFont(UnifiedFont );  //new java.awt.Font("微软雅黑", Font.PLAIN, 16)
  
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
  
    private static DateFormat getDefaultDateFormat() {  
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // CommonParas.SysParas.sysLocales.getDateTimeFormat();自启动模式
    }  
  
    /** 
     * 得到当前使用的日期格式化器 
     * @return 日期格式化器 
     */  
    public DateFormat getCurrentSimpleDateFormat(){  
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
        this.originalText = preLabel + getCurrentSimpleDateFormat().format(date);  
    }  
  
    /**  
     * 得到当前记忆的原始日期时间 
     * @return 当前记忆的原始日期时间（未修改前的日期时间） 
     */  
    public String getOriginalText() {  
        return originalText;  
    }  
    
    /**  getDefaultDateText
     * 得到默认的、适合于数据库标准的日期形式
     * @return 默认的、适合于数据库标准的日期形式
     */  
    public String getDefaultDateText() {  
        return getDefaultDateFormat().format(getDate());  
    }  
  
    // 覆盖父类的方法  
    @Override  
    public void setText(String s) {  
        Date date;  
        try {  
            date = getCurrentSimpleDateFormat().parse(s);  
        } catch (ParseException e) {  
            date = getNowDate();  
        }  
        setDate(date);  
    }  
  
    public void setText(DateFormat df, String s) {  
        Date date;  
        try {  
            date = df.parse(s);  
        } catch (ParseException e) {  
            date = getNowDate();  
        }  
        setDate(date);  
    }  
  
    public void setDate(Date date) {  

        super.setText(preLabel + getCurrentSimpleDateFormat().format(date));  
    }  
  
    public Date getDate() {  
        String dateString = getText().substring(preLabel.length());  
        try {  
            DateFormat currentSdf = getCurrentSimpleDateFormat();  
            return currentSdf.parse(dateString);  
        } catch (ParseException e) {  
            return getNowDate();  
        }  
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
//        JSpinner yearSpin;  
//        JSpinner monthSpin;  
//        JSpinner daySpin;  
//        JSpinner hourSpin;  
//        JSpinner minuteSpin;  
//        JSpinner secondSpin;  
        JButton[][] daysButton = new JButton[6][7];  
        
        JLabel yearleft, yearright, monthleft, monthright, center, centercontainer;
        JSpinner jSpinnerTime;
        private DateFormat  dateFormat;//date的表示方式比如，yyyy-MM-dd

  
        DateChooser() {  
  
            setLayout(new BorderLayout());  
            setBorder(new LineBorder(backGroundColor, 2));  
            setBackground(backGroundColor);  
            
            dateFormat = CommonParas.SysParas.sysLocales.getDateFormat();
  
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
            
            dateFormat = CommonParas.SysParas.sysLocales.getDateFormat();
  
            JPanel topYearAndMonth = createYearAndMonthPanel();  
            add(topYearAndMonth, BorderLayout.NORTH);  
            JPanel centerWeekAndDay = createWeekAndDayPanal();  
            add(centerWeekAndDay, BorderLayout.CENTER);  
            flushWeekAndDay();  

        }  
        
        private JPanel createYearAndMonthPanel() {  

            final int PHeight = 40;
            JPanel Result = new JPanel(); 
            JPanel PanelYearAndMonth = new JPanel();
            JPanel PanelTime = new JPanel();

            Result.setBackground(controlLineColor);  
            PanelYearAndMonth.setBackground(controlLineColor);  
            PanelTime.setBackground(controlLineColor);  
            
            
                yearleft = new JLabel("<<", JLabel.RIGHT);
                yearleft.setFont(UnifiedFont);
                yearleft.setPreferredSize(new Dimension(40, PHeight));  
                yearleft.setToolTipText(sYearLeft);//"上一年"
                
                monthleft = new JLabel("<", JLabel.LEFT);
                monthleft.setFont(UnifiedFont);
                monthleft.setPreferredSize(new Dimension(40, PHeight));  
                monthleft.setToolTipText(sMonthLeft);//"上一月"
                
                monthright = new JLabel(">", JLabel.RIGHT);
                monthright.setFont(UnifiedFont);
                monthright.setPreferredSize(new Dimension(40, PHeight));  
                monthright.setToolTipText(sMonthRight);//"下一月"
                
                yearright = new JLabel(">>", JLabel.LEFT);
                yearright.setFont(UnifiedFont);
                yearright.setPreferredSize(new Dimension(40, PHeight));  
                yearright.setToolTipText(sYearRight);//"下一年"
                
//                yearleft.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));// top,  left,  bottom,  right
//                yearright.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));// top,  left,  bottom,  right
                monthleft.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));// top,  left,  bottom,  right
                monthright.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));// top,  left,  bottom,  right



                centercontainer = new JLabel("", JLabel.CENTER);

                centercontainer.setLayout(new BorderLayout());

                center = new JLabel("", JLabel.CENTER);
                center.setHorizontalAlignment(SwingConstants.CENTER);
                center.setPreferredSize(new Dimension(80, PHeight));
                center.setFont(UnifiedFont);

 
            
            centercontainer.add(monthleft, BorderLayout.WEST);
            centercontainer.add(center, BorderLayout.CENTER);
            centercontainer.add(monthright, BorderLayout.EAST);


            
            PanelYearAndMonth.setPreferredSize(new Dimension(260, PHeight));  
            //PanelYearAndMonth.setBackground(new Color(160, 185, 215));
            
            PanelYearAndMonth.setLayout(new BorderLayout());  
            PanelYearAndMonth.add(yearleft, BorderLayout.WEST);
            PanelYearAndMonth.add(centercontainer, BorderLayout.CENTER);
            PanelYearAndMonth.add(yearright, BorderLayout.EAST);
                        
                    JLabel jLabelTime = new JLabel(sLabelTime, JLabel.RIGHT);//"时间："
                    jLabelTime.setFont(UnifiedFont);
                    jLabelTime.setPreferredSize(new Dimension(60, PHeight));  
                    jSpinnerTime = new JSpinner();
                    jSpinnerTime.setFont(UnifiedFont);//设置字体必须放在前面，如果放在最后面，发现不起作用
                    jSpinnerTime.setModel(new javax.swing.SpinnerDateModel(getDate(), null, null, java.util.Calendar.MINUTE));
                    jSpinnerTime.setBorder(null);
                    JSpinner.DateEditor editor2 = new JSpinner.DateEditor(jSpinnerTime, CommonParas.SysParas.sysLocales.getTimeFormatPattern());//"hh:mm:ss a"
                    jSpinnerTime.setEditor(editor2);
                    jSpinnerTime.addChangeListener(this); 
                    
            PanelTime.setLayout(new BorderLayout());  
            PanelTime.add(jLabelTime, BorderLayout.WEST);
            PanelTime.add(jSpinnerTime, BorderLayout.CENTER);    
            
            Result.setLayout(new BorderLayout());  
            Result.add(PanelYearAndMonth, BorderLayout.WEST);
            Result.add(PanelTime, BorderLayout.CENTER);  
                        
                        
                        Calendar c = getCalendar();  
                        center.setText(dateFormat.format(c.getTime()));//MessageFormat.format(sYearAndMonth , c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1)//c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1) + "月"
                        
                        yearleft.addMouseListener(new MouseAdapter() {
                            public void mouseEntered(MouseEvent me) {   buttoMmouseEntered(yearleft);}
                            public void mouseExited(MouseEvent me) {    buttoMmouseExited(yearleft);}
                            public void mousePressed(MouseEvent me) {   buttoMmousePressed( yearleft,  Calendar.YEAR,  -1);}
                            public void mouseReleased(MouseEvent me) {  buttoMmouseReleased(yearleft);}
			});

			yearright.addMouseListener(new MouseAdapter() {
                            public void mouseEntered(MouseEvent me) {   buttoMmouseEntered(yearright);}
                            public void mouseExited(MouseEvent me) {    buttoMmouseExited(yearright);}
                            public void mousePressed(MouseEvent me) {   buttoMmousePressed( yearright,  Calendar.YEAR,  1);}
                            public void mouseReleased(MouseEvent me) {  buttoMmouseReleased(yearright);}
			});

			monthleft.addMouseListener(new MouseAdapter() {
                            public void mouseEntered(MouseEvent me) {   buttoMmouseEntered(monthleft);}
                            public void mouseExited(MouseEvent me) {    buttoMmouseExited(monthleft);}
                            public void mousePressed(MouseEvent me) {   buttoMmousePressed( monthleft,  Calendar.MONTH,  -1);}
                            public void mouseReleased(MouseEvent me) {  buttoMmouseReleased(monthleft);}
			});

			monthright.addMouseListener(new MouseAdapter() {
                            public void mouseEntered(MouseEvent me) {   buttoMmouseEntered(monthright);}
                            public void mouseExited(MouseEvent me) {    buttoMmouseExited(monthright);}
                            public void mousePressed(MouseEvent me) {   buttoMmousePressed( monthright,  Calendar.MONTH,  1);}
                            public void mouseReleased(MouseEvent me) {  buttoMmouseReleased(monthright);}
			});
            return Result;  
        }  
        
        private void buttoMmouseEntered(JLabel DateLabel){
            DateLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            DateLabel.setForeground(Color.RED);
        }
        private void buttoMmouseExited(JLabel DateLabel){
            DateLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            DateLabel.setForeground(Color.BLACK);
        }
        private void buttoMmousePressed(JLabel DateLabel, int CalendarField, int AddValue){
            /*  Calendar.add(Calendar.MONTH, 1);
                如果是2017-3-31，则为2017-4-30
                如果是2017-1-31，则为2017-2-28
                如果是2017-1-30，则为2017-2-28
                如果是2017-1-29，则为2017-2-28
            */
            Calendar c = getCalendar();  
            c.add(CalendarField, AddValue);
            DateLabel.setForeground(Color.WHITE);
            
            center.setText(dateFormat.format(c.getTime()));//MessageFormat.format(sYearAndMonth , c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1)；c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1) + "月"
            dayColorUpdate(true);//将原来的日期设置为正常颜色，必须在设置新的日期之前进行
            setDate(c.getTime());

            flushWeekAndDay();
            //updateDate(c);
        }
        private void buttoMmouseReleased(JLabel DateLabel){
            DateLabel.setForeground(Color.BLACK);
        }
        
        
        
//        private void updateDate(Calendar c) {
//            //Calendar c = getCalendar();  
//            center.setText(MessageFormat.format(sYearAndMonth , c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1));//c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1) + "月"
//            dayColorUpdate(true);//将原来的日期设置为正常颜色，必须在设置新的日期之前进行
//            setDate(c.getTime());
//
//            flushWeekAndDay();
//            //把jSpinnerTime中的值也变了  
//            //((SpinnerDateModel)jSpinnerTime.getModel()).setValue(c.getTime()); 
//        }
  
        private JPanel createWeekAndDayPanal() {  
            //String sWeekName[] = {"日", "一", "二", "三", "四", "五", "六"};  
            JPanel result = new JPanel();  
            // 设置固定字体，以免调用环境改变影响界面美观  
            result.setFont(UnifiedFont); //  new Font("微软雅黑", Font.PLAIN, 16)
            result.setLayout(new GridLayout(7, 7));  
            result.setBackground(controlLineColor);  
            JLabel cell;  
  
            for (int i = 0; i < 7; i++) {  
                cell = new JLabel(sWeekName[i]);  
                cell.setFont(UnifiedFont);   
                cell.setHorizontalAlignment(JLabel.CENTER); /////////////////////////////////// 
                if (i == 0 || i == 6) {  
                    cell.setForeground(weekendFontColor);  
                } else {  
                    cell.setForeground(weekFontColor);  
                }  
                cell.setOpaque(true);
                cell.setBackground(new Color(230,230,230));  
                result.add(cell);  
            }  
  
            int actionCommandId = 0;  
            for (int i = 0; i < 6; i++) {  
                for (int j = 0; j < 7; j++) {  
                    JButton numberButton = new JButton();  
                    numberButton.setFont(UnifiedFont);  
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
        private String getTextOfDateChooserButton() {  
            return getText();  
        }  
        
        
        
        /** 恢复DateChooserButton的原始日期时间text，本方法是为按钮事件匿名类准备的。 */  
        private void restoreTheOriginalDate() { 
                    	tFlag = true;
            String originalText = getOriginalText();  
            setText(originalText);  
        }  
  
        private JPanel createButtonBarPanel() {  
            JPanel panel = new JPanel();  
            panel.setLayout(new java.awt.GridLayout(1, 2));  
  
            JButton ok = new JButton(sOk); // "确定"
            ok.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ok2.png"));
            ok.setFont(UnifiedFont);  
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
  
            JButton cancel = new JButton(sCancel); // "取消"
            cancel.setIcon(ImageIconBufferPool.getInstance().getImageIcon("cancel2.png"));
            cancel.setFont(UnifiedFont);  
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
            JDialog result = new JDialog(owner, sSelectDateTime, false);//  "日期时间选择"
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
            jLabel2.setFont(UnifiedFontBold); // NOI18N  new java.awt.Font("微软雅黑", 1, 18)
            jLabel2.setForeground(controlTextColor);  
            jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel2.setText(sSelectDateTime);//"日期时间选择"
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
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(DateChooserJButtonE.this);  
            if (dialog == null || dialog.getOwner() != owner) {  
                dialog = createDialog(owner);  
            }  
            dialog.setLocation(getAppropriateLocation(owner, position));
            
//            dayColorUpdate(true);
//            flushWeekAndDay(); 

            dialog.setVisible(true);  
        }  
  
        private Point getAppropriateLocation(Frame owner, Point position) {  
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
                daysButton[i][j].setForeground(OldColor); //日期文字 
            } else {  
                daysButton[i][j].setForeground(todayBackColor);  //当前日期文字色
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
            if (e.getSource()==jSpinnerTime) {  //.equals("Hour")
                SpinnerDateModel Model2 = (SpinnerDateModel)jSpinnerTime.getModel();
                Calendar cc = Calendar.getInstance();
                cc.setTime(Model2.getDate());

                c.set(Calendar.HOUR_OF_DAY, cc.get(Calendar.HOUR_OF_DAY));  
                c.set(Calendar.MINUTE, cc.get(Calendar.MINUTE));
                c.set(Calendar.SECOND, cc.get(Calendar.SECOND));
                setDate(c.getTime());  
            }  

            //dayColorUpdate(true);
//            setDate(c.getTime());
//
//            flushWeekAndDay();
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
            //把center的值也变了 
            center.setText(dateFormat.format(c.getTime()));
            //((SpinnerDateModel)jSpinnerTime.getModel()).setValue(c.getTime());  
        }  

    }
    
    private static Font UnifiedFont = new Font("微软雅黑", Font.PLAIN, 16);
    private static Font UnifiedFontBold = new Font("微软雅黑", Font.BOLD, 16);
    private static String sFontName = "微软雅黑";
    private static String[] sWeekName = {"日", "一", "二", "三", "四", "五", "六"};  
    private static String sSelectDateTime = "日期时间选择";
    private static String sYearAndMonth = "{0}年{1}月";
    private static String sYearLeft = "上一年";
    private static String sMonthLeft = "上一月";
    private static String sMonthRight = "下一月";
    private static String sYearRight = "下一年";
    private static String sLabelTime = "时间：";
    private static String sOk = "确定";
    private static String sCancel = "取消";
    
    /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    public static void modifyLocales(){
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作
        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        //信息显示
        sWeekName[0] = Locales.getString("ClassStrings", "DateChooserJButtonE.sWeekName0");  //日
        sWeekName[1] = Locales.getString("ClassStrings", "DateChooserJButtonE.sWeekName1");  //一
        sWeekName[2] = Locales.getString("ClassStrings", "DateChooserJButtonE.sWeekName2");  //二
        sWeekName[3] = Locales.getString("ClassStrings", "DateChooserJButtonE.sWeekName3");  //三
        sWeekName[4] = Locales.getString("ClassStrings", "DateChooserJButtonE.sWeekName4");  //四
        sWeekName[5] = Locales.getString("ClassStrings", "DateChooserJButtonE.sWeekName5");  //五
        sWeekName[6] = Locales.getString("ClassStrings", "DateChooserJButtonE.sWeekName6");  //六

        sSelectDateTime =   Locales.getString("ClassStrings", "DateChooserJButtonE.sSelectDateTime");  //日期时间选择
        sFontName =         Locales.getString("ClassStrings", "DateChooserJButtonE.sFontName");  //微软雅黑
        sYearAndMonth =     Locales.getString("ClassStrings", "DateChooserJButtonE.sYearAndMonth");  //{0}年{1}月
        sYearLeft =         Locales.getString("ClassStrings", "DateChooserJButtonE.sYearLeft");  //上一年
        sMonthLeft =        Locales.getString("ClassStrings", "DateChooserJButtonE.sMonthLeft");  //上一月
        sMonthRight =       Locales.getString("ClassStrings", "DateChooserJButtonE.sMonthRight");  //下一月
        sYearRight =        Locales.getString("ClassStrings", "DateChooserJButtonE.sYearRight");  //下一年
        sLabelTime =        Locales.getString("ClassStrings", "DateChooserJButtonE.sLabelTime");  //时间：
        sOk =               Locales.getString("ClassStrings", "DateChooserJButtonE.sOk");  //确定
        sCancel =           Locales.getString("ClassStrings", "DateChooserJButtonE.sCancel");  //取消

        UnifiedFont = new Font(sFontName, Font.PLAIN, 16);
        UnifiedFontBold = new Font(sFontName, Font.BOLD, 16);

    }

    /** 
     * 测试方法 
     */  
    public static void main(String[] args) {  
        JFrame mainFrame = new JFrame("测试");  
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        mainFrame.setSize(300, 300);  
        mainFrame.setLayout(new java.awt.BorderLayout());  
        mainFrame.add(new DateChooserJButtonE(new Date()), java.awt.BorderLayout.CENTER);  
  
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
