/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicArrowButton;
import jyms.CommonParas;
import jyms.MyLocales;

/**
 * hi , this is my first ui component. my ui component "hello world"!!
 * 
 * @author bugu
 * 
 */
public class MulitCombobox extends JComponent {

	private Object[] values;
        private Object[] valueCodes;
	private Object[] defaultValues;
	private List<ActionListener> listeners = new ArrayList<ActionListener>();
	private MulitPopup popup;
	private JTextField editor;
	protected JButton   arrowButton;
	private String valueSperator = DEFAULT_VALUE_SPERATOR;
	private static final String DEFAULT_VALUE_SPERATOR = "; "; 
        private int widthOfCom = 500;
        private final int heightOfCom = 20;
        
        private boolean bPopupShow = false;

	public MulitCombobox(){
		//this(value,defaultValue,DEFAULT_VALUE_SPERATOR);
		super();
                //this.setSize(200, 20);
		this.valueSperator = DEFAULT_VALUE_SPERATOR;
                //this.setBounds(0, 0, 600, 30);
	}
	
	public MulitCombobox(Object[] value, Object[] valueCode, Object[] defaultValue , int WidthOfCom) {
                super();
		this.widthOfCom = WidthOfCom;
		initComponent(value, valueCode, defaultValue);
	}

	public void initComponent(Object[] value, Object[] valueCode, Object[] defaultValue) {
		//閺嗗倹妞傛担璺ㄦ暏鐠囥儱绔风仦锟介崥搴ｇ敾閼奉亜绻侀崘娆庨嚋鐢啫鐪�
                //this.setSize(200, 30);
                //this.setMaximumSize(new Dimension(300,30));
		values = value;
                valueCodes = valueCode;
		defaultValues = defaultValue;
		this.setLayout(new FlowLayout());
		popup =new  MulitPopup(values,valueCodes,defaultValues);
		popup.addActionListener(new PopupAction());
		editor = new JTextField();
                editor.setPreferredSize(new Dimension(widthOfCom,heightOfCom));
		editor.setBackground(Color.WHITE);
		editor.setEditable(false);
                editor.setFont(new java.awt.Font(sFontName, 0, 16)); // NOI18N "微软雅黑"
		editor.setBorder(null);
		editor.addMouseListener(new EditorHandler());
		arrowButton = createArrowButton();
                
		arrowButton.addMouseListener(new ButtonHandler());
		add(editor);
		add(arrowButton);
		setText() ;
	}

	public Object[] getSelectedValues() {
		return popup.getSelectedValues();
	}
	
	public void addActionListener(ActionListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}
	
	protected void fireActionPerformed(ActionEvent e) {
		for (ActionListener l : listeners) {
			l.actionPerformed(e);
		}
	}
	
	private class PopupAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(e.getActionCommand().equals(MulitPopup.CANCEL_EVENT)){
				popup.setVisible(false);
			}else if(e.getActionCommand().equals(MulitPopup.COMMIT_EVENT)){
				defaultValues = popup.getSelectedValues();
                                popup.setVisible(false);
				setText();
				//閹跺﹣绨ㄦ禒鍓佹埛缂侇厺绱堕柅鎺戝毉閸橈拷				fireActionPerformed(e);
			}
//			togglePopup();
		}

	}
	
	private void togglePopup(){
		if(bPopupShow){
                    //System.out.println("togglePopup1");
                    popup.setVisible(false);
                    setText();
                    bPopupShow = false;
		}else{
                    //System.out.println("togglePopup2");
                    popup.setDefaultValue(defaultValues);
                    popup.show(this, 0, getHeight());
                    bPopupShow = true;
                        //popup.setSize(500, 500);
		}
	}
        private void togglePopupForButton(){
		if(popup.isVisible()){
                    //System.out.println("togglePopup1");
                    popup.setVisible(false);
                    setText();
                    bPopupShow = false;
		}else{
                    //System.out.println("togglePopup2");
                    popup.setDefaultValue(defaultValues);
                    popup.show(this, 0, getHeight());
                    bPopupShow = true;
                    //popup.setSize(500, 500);
		}
	}
	
	private void setText() {
            if (values.length == defaultValues.length) editor.setText(sSelectAll);// "全部选择"
            else {
		StringBuilder builder = new StringBuilder();
		for(Object dv : defaultValues){
			builder.append(dv);
			builder.append(valueSperator);
		}
		editor.setText(builder.substring(0, builder.length() > 0 ? builder.length() -valueSperator.length()  : 0).toString());
            }
	}
        
        public String getCheckedText(){
            return editor.getText();
        }

        public String getCheckedCodes(){
            Object[] selectedCodes = popup.getSelectedCodes();
            if (valueCodes.length == selectedCodes.length) return sSelectAll;// "全部选择"
            else {
		StringBuilder builder = new StringBuilder();
		for(Object dv : selectedCodes){
                        builder.append("'");
			builder.append(dv);
                        builder.append("',");
		}
		return (builder.substring(0, builder.length() > 0 ? builder.length() -1  : 0));
            }
        }
        
        public String getCheckedValues(){
            Object[] selectedValues = popup.getSelectedValues();
            if (valueCodes.length == selectedValues.length) return sSelectAll;// "全部选择"
            else {
		StringBuilder builder = new StringBuilder();
		for(Object dv : selectedValues){
                        builder.append("“");
			builder.append(dv);
                        builder.append("”,");
		}
		return (builder.substring(0, builder.length() > 0 ? builder.length() -1  : 0));
            }
        }
        
        public boolean ifCheckAll(){
            if (values.length == defaultValues.length) return true;
            return false;
        }

	private class EditorHandler implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {togglePopup();}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
        
        private class ButtonHandler implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) { togglePopupForButton();}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
        @Override
        public void paintComponent(Graphics g){
              g.setColor(Color.white);
              g.fillRect(0,0,getWidth(),getHeight());
              arrowButton.setIcon(ImageIconBufferPool.getInstance().getImageIcon("arrowbutton.png"));
              //g.fillRect(0,0,300,20);
        }
	  
        /**
               * 閹芥鍤淛DK
           * Creates an button which will be used as the control to show or hide
           * the popup portion of the combo box.
           *
           * @return a button which represents the popup control
           */
        protected JButton createArrowButton() {
                JButton button = new BasicArrowButton(BasicArrowButton.SOUTH,
                                            UIManager.getColor("ComboBox.buttonBackground"),
                                            UIManager.getColor("ComboBox.buttonShadow"),
                                            UIManager.getColor("ComboBox.buttonDarkShadow"),
                                            UIManager.getColor("ComboBox.buttonHighlight"));
                button.setName("ComboBox.arrowButton");
                
                
                //button.setMinimumSize(new Dimension(heightOfCom, heightOfCom));
                return button;
        }
	    
	private class MulitComboboxLayout  implements LayoutManager{

			@Override
			public void addLayoutComponent(String name, Component comp) {
				// TODO Auto-generated method stub
			}

			@Override
			public void removeLayoutComponent(Component comp) {
				// TODO Auto-generated method stub
			}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				return parent.getPreferredSize();
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				return parent.getMinimumSize();
			}

			@Override
			public void layoutContainer(Container parent) {
				int w=parent.getWidth();
                                int h=parent.getHeight();
                                Insets insets=parent.getInsets();
                                h=h-insets.top-insets.bottom;
			}
	    }
    
        /**
        * 
        * @author bugu
        * 
        */
        public class MulitPopup extends JPopupMenu {

            private List<ActionListener> listeners = new ArrayList<ActionListener>();
            private Object[] values;
            private Object[] valueCodes;
            private Object[] defaultValues;
            private List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
            private JButton commitButton ;
            private JButton cancelButton;
            public static final String COMMIT_EVENT = "commit";
            public static final String CANCEL_EVENT = "cancel";

            public MulitPopup(Object[] value , Object[] valueCode, Object[] defaultValue) {
                    super();
                    values = value;
                    valueCodes = valueCode;
                    defaultValues = defaultValue;
                    initComponent();
                    int SizeOfMenu = value.length;
                    this.setPopupSize(widthOfCom + 40, (SizeOfMenu+1) * 40);
                    //this.setMargin(new java.awt.Insets(5, 0, 5, 0));
            }

            public void addActionListener(ActionListener listener) {
                    if (!listeners.contains(listener))
                            listeners.add(listener);
            }

            public void removeActionListener(ActionListener listener) {
                    if (listeners.contains(listener))
                            listeners.remove(listener);
            }

            private void initComponent() {

                    JPanel checkboxPane = new JPanel();
                    JPanel buttonPane = new JPanel();
                    this.setLayout(new BorderLayout());

                    for(Object v : values){
                            JCheckBox temp = new JCheckBox(v.toString() , selected(v));
                            temp.setFont(new java.awt.Font(sFontName, 0, 16)); // NOI18N "微软雅黑"
                            checkBoxList.add(temp);
                    }
                    checkboxPane.setBorder(null);
                    checkboxPane.setLayout(new GridLayout(checkBoxList.size() , 1 ,3, 3));
                    for(JCheckBox box : checkBoxList){
                            checkboxPane.add(box);
                    }

                    commitButton = new JButton(sCommit);// "确定"
                    commitButton.setIcon(ImageIconBufferPool.getInstance().getImageIcon("ok2.png"));
                    commitButton.setFont(new java.awt.Font(sFontName, 0, 16)); // NOI18N "微软雅黑"
                    commitButton.addActionListener(new ActionListener(){
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                    commit();
                            }

                    });

                    cancelButton = new JButton(sCancel);// "取消"
                    cancelButton.setIcon(ImageIconBufferPool.getInstance().getImageIcon("cancel2.png"));
                    cancelButton.setFont(new java.awt.Font(sFontName, 0, 16)); // NOI18N "微软雅黑"
                    cancelButton.addActionListener(new ActionListener(){
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                    cancel();
                            }

                    });

                    buttonPane.add(commitButton);
                    buttonPane.add(cancelButton);
                    this.add(checkboxPane , BorderLayout.CENTER);
                    this.add(buttonPane , BorderLayout.SOUTH);
            }

            private boolean selected(Object v) {
                    for(Object dv : defaultValues){
                            if( dv .equals(v) ){
                                    return true;
                            }
                    }
                    return false;
            }

            protected void fireActionPerformed(ActionEvent e) {
                    for (ActionListener l : listeners) {
                            l.actionPerformed(e);
                    }
            }

            public Object[] getSelectedValues(){
                    List<Object> selectedValues = new ArrayList<Object>();
                    for(int i = 0 ; i < checkBoxList.size() ; i++){
                            if(checkBoxList.get(i).isSelected())
                                    selectedValues.add(values[i]);
                    }
                    return selectedValues.toArray(new Object[selectedValues.size()]);
            }
            
            public Object[] getSelectedCodes(){
                    List<Object> selectedCodes = new ArrayList<Object>();
                    for(int i = 0 ; i < checkBoxList.size() ; i++){
                            if(checkBoxList.get(i).isSelected())
                                    selectedCodes.add(valueCodes[i]);
                    }
                    return selectedCodes.toArray(new Object[selectedCodes.size()]);
            }

            public void setDefaultValue(Object[] defaultValue) {
                    defaultValues = defaultValue;

            }

            public void commit(){
                    fireActionPerformed(new ActionEvent(this, 0, COMMIT_EVENT));
            }

            public void cancel(){
                    fireActionPerformed(new ActionEvent(this, 0, CANCEL_EVENT));
            }

    }
        
        /**
        * 函数:      modifyLocales
        * 函数描述:  根据系统语言设置窗口的控件信息和消息文本
    */
    public static void modifyLocales(){
        if (CommonParas.SysParas.ifChinese) return;//如果是中文，则不做任何操作
        
        MyLocales Locales = CommonParas.SysParas.sysLocales;
        
        //信息显示
        sCommit = Locales.getString("ClassStrings", "MulitCombobox.sCommit");  //"确定";
        sCancel = Locales.getString("ClassStrings", "MulitCombobox.sCancel");  //"取消";
        sSelectAll = Locales.getString("ClassStrings", "MulitCombobox.sSelectAll");  //"全部选择";
        //sFontName = Locales.getString("ClassStrings", "MulitCombobox.sFontName");  //"微软雅黑";
    }
    
    private static String sCommit = "确定";
    private static String sCancel = "取消";
    private static String sSelectAll = "全部选择";
    private static String sFontName = "Microsoft YaHei UI";

}
