package jyms.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import jyms.tools.ImageIconBufferPool;


public class JymsLookAndFeel extends NimbusLookAndFeel
{
	private static final boolean				isSlim=true;
	
        /**	窗口背景颜色[51,51,51] */
	private static final ColorUIResource 	COLOR_WINDOW_BACKGROUND = new ColorUIResource(51,51,51);
        /**	主体颜色，包括菜单栏、表格内容、导航界面[64, 64, 64] */
	private static final ColorUIResource 	COLOR_MAIN              = new ColorUIResource(64, 64, 64);
        /**	ToolBar，黑背景的JPabel背景色 */
	private static final ColorUIResource 	COLOR_TOOLBAR           = new ColorUIResource(151,151,151);
        /**	分割条背景色,TabbedPane底色 */
	private static final ColorUIResource 	COLOR_DIVIDER           = new ColorUIResource(120,120,120);
        /**	 按钮背景色 */
	private static final ColorUIResource 	COLOR_BUTTON            = new ColorUIResource(120,120,120);
        /** 	窗口背景颜色[235,235,235] */
	private static final ColorUIResource	COLOR_WINDOW_BACKGROUND2 = new ColorUIResource(235,235,235);
        /** 	窗口前景颜色[235,235,235] */
	private static final ColorUIResource	COLOR_WINDOW_FOREGROUND2 = new ColorUIResource(0,0,0);
        /**	 前景色、字体颜色 */
	private static final ColorUIResource 	COLOR_FOREGROUND        = new ColorUIResource(255,255,255);
        
        /**	控件 border */
	private static final ColorUIResource	COLOR_BORDER            = new ColorUIResource(100, 90, 90);

        /*******白色背景的颜色变量*******/
        /**	 JTable、JTree背景色、字体颜色 */
	private static final ColorUIResource 	COLOR_TABLE_BACKGROUND2 = new ColorUIResource(255,255,255);
        /**	 JTable、JTree前景色、字体颜色 */
	private static final ColorUIResource 	COLOR_TABLE_FOREGROUND2 = new ColorUIResource(0,0,0);
        /*      TAB选中的颜色*/
        private static final ColorUIResource 	COLOR_TAB_SELECTED = new ColorUIResource(105,23,29);
        
        /**	标准字体 */
	private static FontUIResource		FONT_STANDARD       = new FontUIResource("微软雅黑", Font.PLAIN, 16);
	/**	标准加粗字体 */
	private static final FontUIResource	FONT_STANDARD_BOLD  = new FontUIResource("微软雅黑", Font.BOLD, 16);
        /**	标题字体 */
	private static FontUIResource		FONT_TITLE          = new FontUIResource("微软雅黑", Font.PLAIN, 18);
        /**	小标题字体，主要用于设备预览中的云台控制中的Tab栏字体 */
	private static FontUIResource		FONT_TITLE_SMALL    = new FontUIResource("微软雅黑", Font.BOLD, 16);
        /**	标题字体加粗 */
	private static FontUIResource		FONT_TITLE_BOLD     = new FontUIResource("微软雅黑", Font.BOLD, 18);
        
//	/**	The text foreground color */
//	private static final ColorUIResource 	textFg=new ColorUIResource(Color.BLACK);
//	
//	/** 	The window background color */
//	private static final ColorUIResource	windowBg=new ColorUIResource(220, 220, 220);
//	
//	/** 	The background for inactive windows */
//	private static final ColorUIResource 	inactiveBg=new ColorUIResource(180, 180, 180);
//	
	/**	The background for active windows */
	private static final ColorUIResource	activeBg=new ColorUIResource(220, 220, 220);
	
	/**	The highlight for active resources */
	private static final ColorUIResource 	activeHighlight=new ColorUIResource(Color.WHITE);
	
	/** 	The shadow for active resources */
	private static final ColorUIResource	activeShadow=new ColorUIResource(150, 150, 150);
//	
//	/**	The light shadow for active resources */
//	private static final ColorUIResource 	activeLightShadow=new ColorUIResource(200, 200, 200);
//	
	/**	The foreground color for selections */
	private static final ColorUIResource 	COLOR_SEL_FOREGROUND=new ColorUIResource(Color.WHITE);
	
	/**	The background color for selections */
	private static final ColorUIResource	COLOR_SEL_BACKGROUND=new ColorUIResource(0, 0, 153);
//	
//	/**	The background color for active ToolButtons */
//	private static final ColorUIResource	toolButtonActiveBg=new ColorUIResource(173,173,209);
//	
//	/**	The border color for active ToolButtons */
//	private static final ColorUIResource	toolButtonBorder=COLOR_SEL_BACKGROUND;
//	
//	/**	The color of the focus */
//	private static final ColorUIResource	focusColor=COLOR_SEL_BACKGROUND; //focusColor=new ColorUIResource(100, 90, 130);
//	
//	/**	The color of inactive borders */
//	private static final ColorUIResource	inactiveBorderColor=new ColorUIResource(100, 90, 90);
//	
//
//	
//	/**	The background for fields */
//	private static final ColorUIResource	fieldBg = new ColorUIResource(Color.WHITE);
//	
//	/**	The color for tree lines */
//	private static final ColorUIResource	treeLineColor=new ColorUIResource(220, 220, 220);
//	
//	/**	Inactive window gradient color 1 */
//	private static final ColorUIResource	inactiveGradColor1=new ColorUIResource(200, 200, 200);
//	
//	/**	Inactive window gradient color 2 */
//	private static final ColorUIResource	inactiveGradColor2=new ColorUIResource(Color.WHITE);
//
//	/**	Active window gradient color 1 */	
//	private static final ColorUIResource 	activeGradColor1=COLOR_SEL_BACKGROUND;
//	
//	/**	Active window gradient color 2 */
//	private static final ColorUIResource	activeGradColor2=new ColorUIResource(Color.WHITE);
//	
//	/**	Background for tool tips */	
//	private static final ColorUIResource	toolTipBg=new ColorUIResource(255, 255, 240);
//	
//	/**	Standard font */
//	private static FontUIResource				stdFont=new FontUIResource("Tahoma", Font.PLAIN, 11);
//	
//	/**	Standard bold font */
//	private static final FontUIResource		stdBoldFont=new FontUIResource("Tahoma", Font.BOLD, 11);
//	
//	/**	Helper border to be used in compound border */
//	private static final Border 				hb5=BorderFactory.createLineBorder(fieldBg, 1);
//	
//	/** 	Helper border to be used in compound border */
//	private static final Border 				hb6=BorderFactory.createLineBorder(COLOR_BORDER);
//	
//	/**	Helper border to be used in compound border */
//	private static final Border				hb7=BorderFactory.createLineBorder(COLOR_BORDER);
//	
//	/**	Helper border to be used in compound border */
//	private static final Border				hb8=BorderFactory.createCompoundBorder(hb5, hb6);
//	
//	/**	Helper border to be used in compound border */
//	private static final BorderUIResource.CompoundBorderUIResource internalFrameBorder=new BorderUIResource.CompoundBorderUIResource(hb7, hb8);
//	
//	/**	Helper border to be used in compound border */		
//	private static final BorderUIResource	internalFrameBorder2=new BorderUIResource(new InternalFrameBorder(COLOR_BORDER, inactiveBg, Color.WHITE));
//	
//	private static final BorderUIResource.LineBorderUIResource	plainDialogBorder=new BorderUIResource.LineBorderUIResource(Color.BLACK);	
//
//	/**	Helper border to be used in compound border */
//	private static final Border				hb1=BorderFactory.createMatteBorder(0, 0, 1, 0, activeHighlight);
//	
//	/**	Helper border to be used in compound border */
//	private static final Border 				hb2=BorderFactory.createMatteBorder(0, 0, 1, 0, activeShadow);
//	
//	/**	Border for separating the menu (and toolbars) from the main panel */
//	private static final BorderUIResource.CompoundBorderUIResource menuBarBorder=new BorderUIResource.CompoundBorderUIResource(hb1, hb2);
//	
//	/**	Helper border to be used in compound border */
//	private static final Border				hb9=BorderFactory.createMatteBorder(0, 0, 0, 1, activeHighlight);
//	
//	/**	Helper border to be used in compound border */
//	private static final Border 				hb10=BorderFactory.createMatteBorder(0, 0, 0, 1, activeShadow);
//	
//	/**	Border for separating the menu (and toolbars) from the main panel */
//	private static final BorderUIResource.CompoundBorderUIResource toolBarVerticalBorder=new BorderUIResource.CompoundBorderUIResource(hb9, hb10);	
//
//	private static final BorderUIResource.EmptyBorderUIResource	menuBorder=new BorderUIResource.EmptyBorderUIResource(2, 5, 2, 5);
//	
//	/**	Helper border to be used in compound border */
//	private static final Border				hb3=BorderFactory.createEmptyBorder(2, 2, 2, 2);
//	
//	/**	Helper border to be used in compound border */
//	private static final Border				hb4=BorderFactory.createLineBorder(COLOR_BORDER);
//	
//	/**	Border for progress bars */
//	private static final BorderUIResource.CompoundBorderUIResource progressBarBorder=new BorderUIResource.CompoundBorderUIResource(hb4, hb3);
//
//	/**	Simple, single pixel border */
//	private static final BorderUIResource.LineBorderUIResource popupMenuBorder=new BorderUIResource.LineBorderUIResource(focusColor);
//	
//	/**	Simple, single pixel border */
//	private static final BorderUIResource.LineBorderUIResource simpleBorder=new BorderUIResource.LineBorderUIResource(COLOR_BORDER);
//	
//	/**	Border for combo boxes */
//	private static final BorderUIResource.LineBorderUIResource comboBoxBorder=new BorderUIResource.LineBorderUIResource(COLOR_BORDER);
        /**	背景不透明 */
        private static final boolean				OPAQUE_BACKGROUND = true;//opaque是不透明的意思
        /**	背景透明 */
        private static final boolean				NO_OPAQUE_BACKGROUND = false;
	
	

        @Override
	public UIDefaults getDefaults()
	{
		UIDefaults UIHashTables=super.getDefaults();	
			
		// Bind the UI classes to the respective components
		String packageName="jyms.ui.";		
//		UIHashTables.put("ButtonUI", 					packageName+"ButtonUI");
//		UIHashTables.put("FileChooserUI", 			packageName+"FileChooserUI");
//		UIHashTables.put("LabelUI",					packageName+"LabelUI");
//		UIHashTables.put("MenuBarUI", 				packageName+"MenuBarUI");
//		UIHashTables.put("MenuItemUI", 				packageName+"MenuItemUI");
//		UIHashTables.put("MenuUI", 					packageName+"MenuUI");
//		UIHashTables.put("SeparatorUI", 				packageName+"SeparatorUI");
//		UIHashTables.put("PopupMenuSeparatorUI", 	packageName+"PopupMenuSeparatorUI");
//		UIHashTables.put("CheckBoxMenuItemUI", 	packageName+"CheckBoxMenuItemUI");
//		UIHashTables.put("DesktopIconUI", 			packageName+"DesktopIconUI");
//		UIHashTables.put("RadioButtonMenuItemUI", packageName+"RadioButtonMenuItemUI");
//		UIHashTables.put("ComboBoxUI", 				packageName+"ComboBoxUI");
		
//		UIHashTables.put("ToggleButtonUI", 			packageName+"ToggleButtonUI");
//		UIHashTables.put("RadioButtonUI", 			packageName+"RadioButtonUI");
//		UIHashTables.put("CheckBoxUI", 				packageName+"CheckBoxUI");
		UIHashTables.put("InternalFrameUI",                     packageName+"InternalFrameUI");
                UIHashTables.put("ListUI",                              packageName+"ListUI");
//		UIHashTables.put("OptionPaneUI", 			packageName+"OptionPaneUI");
//		UIHashTables.put("RootPaneUI",				packageName+"RootPaneUI");	
                UIHashTables.put("ScrollBarUI", 			packageName+"ScrollBarUI");
                UIHashTables.put("ScrollPaneUI", 			packageName+"ScrollPaneUI");
                UIHashTables.put("SplitPaneUI", 			packageName+"SplitPaneUI");
		UIHashTables.put("TabbedPaneUI", 			packageName+"TabbedPaneUI");
		UIHashTables.put("TableUI", 				packageName+"TableUI");//一用TableUI就会出现表格横、竖线
		UIHashTables.put("TableHeaderUI", 			packageName+"TableHeaderUI");
		//UIHashTables.put("ToolBarUI", 				packageName+"ToolBarUI");
//		UIHashTables.put("ToolButtonUI", 			packageName+"ToolButtonUI");
//		UIHashTables.put("ProgressBarUI", 			packageName+"ProgressBarUI");
		UIHashTables.put("SliderUI", 				packageName+"SliderUI");

		UIHashTables.put("SpinnerUI", 				packageName+"SpinnerUI");
                UIHashTables.put("TreeUI", 				packageName+"TreeUI");
		
//		// Set parameters for visual appearance
//		UIHashTables.put("Button.font", stdFont);
//		UIHashTables.put("Button.borderColor", COLOR_BORDER);
//		UIHashTables.put("Button.border", null);
//		UIHashTables.put("Button.focusBorderColor", focusColor);
//		UIHashTables.put("Button.disabledBorderColor", inactiveBorderColor);
//		UIHashTables.put("Button.background", windowBg); //new ColorUIResource(Color.WHITE));
//		UIHashTables.put("Button.highlight", activeHighlight);
//		UIHashTables.put("Button.textShiftOffset", new Integer(5));
//		UIHashTables.put("Button.margin", new InsetsUIResource(0, 7, 0, 7));
//
//		UIHashTables.put("CheckBox.background", windowBg);
//		UIHashTables.put("CheckBox.font", stdFont);
//
//		UIHashTables.put("CheckBoxMenuItem.background", windowBg);
//		UIHashTables.put("CheckBoxMenuItem.font", stdFont);
//		UIHashTables.put("CheckBoxMenuItem.selectionBackground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("CheckBoxMenuItem.selectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("CheckBoxMenuItem.acceleratorFont", stdFont);
//		UIHashTables.put("CheckBoxMenuItem.acceleratorForeground", textFg);
//		UIHashTables.put("CheckBoxMenuItem.acceleratorSelectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("CheckBoxMenuItem.border", menuBorder);
//		
//		UIHashTables.put("ComboBox.font", stdFont);
//		UIHashTables.put("ComboBox.background", fieldBg);
//		UIHashTables.put("ComboBox.selectionBackground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("ComboBox.selectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("ComboBox.border", comboBoxBorder);
//		
		UIHashTables.put("control", COLOR_WINDOW_BACKGROUND2);//[51,51,51]
		UIHashTables.put("controlHighlight", activeHighlight);
		UIHashTables.put("controlShadow", activeShadow);
                
                UIHashTables.put("window.background2", COLOR_WINDOW_BACKGROUND2);//[51,51,51]
		UIHashTables.put("window.foreground2", COLOR_WINDOW_FOREGROUND2);
//
//		UIHashTables.put("Desktop.background", Color.WHITE);
//
//		UIHashTables.put("FileChooser.newFolderIcon", makeIcon(getClass(),"icons/filechooser_newfolder.gif"));
//		UIHashTables.put("FileChooser.upFolderIcon", makeIcon(getClass(),"icons/filechooser_back.gif"));
//		UIHashTables.put("FileChooser.homeFolderIcon", makeIcon(getClass(),"icons/home.gif"));
//		UIHashTables.put("FileChooser.detailsViewIcon", makeIcon(getClass(),"icons/filechooser_details.gif"));
//		UIHashTables.put("FileChooser.listViewIcon", makeIcon(getClass(),"icons/filechooser_list.gif"));
//		UIHashTables.put("FileChooser.folderIcon", makeIcon(getClass(),"icons/tree_closed.gif"));
//		
//		UIHashTables.put("FormattedTextField.font", stdFont);
//		UIHashTables.put("FormattedTextField.border", new BorderUIResource.MatteBorderUIResource(1, 1, 1, 0, COLOR_BORDER));
//		UIHashTables.put("FormattedTextField.selectionBackground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("FormattedTextField.selectionForeground", COLOR_SEL_FOREGROUND);
//
//		UIHashTables.put("InternalFrame.optionDialogBorder", internalFrameBorder); //new BorderUIResource.LineBorderUIResource(COLOR_BORDER, 2));
//		UIHashTables.put("InternalFrame.paletteBorder", internalFrameBorder); //new BorderUIResource.LineBorderUIResource(COLOR_BORDER, 2));
//		UIHashTables.put("InternalFrame.border", internalFrameBorder); //new BorderUIResource.LineBorderUIResource(COLOR_BORDER, 2));
//	
//		UIHashTables.put("InternalFrame.closeIcon", LookAndFeel.makeIcon(getClass(),"icons/frame_close.gif"));
//		UIHashTables.put("InternalFrame.icon", LookAndFeel.makeIcon(getClass(),"icons/frame_icon.gif"));
//		UIHashTables.put("InternalFrame.maximizeIcon", LookAndFeel.makeIcon(getClass(),"icons/frame_max.gif"));
//		UIHashTables.put("InternalFrame.minimizeIcon", LookAndFeel.makeIcon(getClass(),"icons/frame_max.gif"));
//		UIHashTables.put("InternalFrame.iconifyIcon", LookAndFeel.makeIcon(getClass(),"icons/frame_min.gif"));
//		UIHashTables.put("InternalFrame.inactiveTitleBackground", inactiveGradColor1);
//		UIHashTables.put("InternalFrame.inactiveTitleGradientColor", inactiveGradColor2);
//		UIHashTables.put("InternalFrame.activeTitleBackground", activeGradColor1);
//		UIHashTables.put("InternalFrame.activeTitleGradientColor", activeGradColor2);
//		UIHashTables.put("InternalFrame.activeTitleForeground", COLOR_SEL_FOREGROUND); 
//
//		UIHashTables.put("Label.font", stdFont);
		UIHashTables.put("Label.border", null);
//
//		UIHashTables.put("List.selectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("List.selectionBackground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("List.font", stdFont);
//		UIHashTables.put("List.focusCellHighlightBorder", new BorderUIResource.LineBorderUIResource(COLOR_SEL_BACKGROUND));
//
//		UIHashTables.put("Menu.font", stdFont);
//		UIHashTables.put("Menu.background", windowBg);
//		UIHashTables.put("Menu.selectionBackground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("Menu.selectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("Menu.border", menuBorder);
//				
//		UIHashTables.put("MenuBar.background", windowBg);
//		UIHashTables.put("MenuBar.border", menuBarBorder);
//	
//		UIHashTables.put("MenuItem.font", stdFont);
//		UIHashTables.put("MenuItem.background", windowBg);
//		UIHashTables.put("MenuItem.selectionBackground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("MenuItem.selectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("MenuItem.acceleratorFont", stdFont);
//		UIHashTables.put("MenuItem.acceleratorForeground", textFg);
//		UIHashTables.put("MenuItem.acceleratorSelectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("MenuItem.border", menuBorder);
//		
//		UIHashTables.put("OptionPane.background", windowBg);
//		UIHashTables.put("OptionPane.border", new BorderUIResource.LineBorderUIResource(windowBg, 10));
//		UIHashTables.put("OptionPane.buttonAreaBorder", null);
//		UIHashTables.put("OptionPane.errorDialog.border.background", windowBg);
//		UIHashTables.put("OptionPane.errorDialog.titlePane.background", windowBg);
//		UIHashTables.put("OptionPane.questionDialog.border.background", windowBg);
//		UIHashTables.put("OptionPane.questionDialog.titlePane.background", windowBg);
//		UIHashTables.put("OptionPane.warningDialog.border.background", windowBg);
//		UIHashTables.put("OptionPane.warningDialog.titlePane.background", windowBg);	
//
//		UIHashTables.put("Panel.background", windowBg);
                //只有在特殊的情况下采用，比如黑白分明的JDialog窗口用的，PanelUI只是对特殊JPanel采用的，用法，setUI(new PanelUI())
                UIHashTables.put("Panel.border", null);//MatteBorderUIResource(5, 5, 5, 5, COLOR_WINDOW_BACKGROUND2));
                UIHashTables.put("Panel.lineborder", new BorderUIResource.LineBorderUIResource(COLOR_BORDER));//MatteBorderUIResource(5, 5, 5, 5, COLOR_WINDOW_BACKGROUND2));
                UIHashTables.put("Panel.matteborder", new BorderUIResource.MatteBorderUIResource(15, 15, 15, 15, COLOR_WINDOW_BACKGROUND2));//MatteBorderUIResource(5, 5, 5, 5, COLOR_WINDOW_BACKGROUND2));
                UIHashTables.put("Panel.background_black", COLOR_TOOLBAR);
                UIHashTables.put("Panel.foreground_black", COLOR_FOREGROUND);
//		
//		UIHashTables.put("PopupMenuSeparator.foreground", COLOR_BORDER);
//		UIHashTables.put("PopupMenuSeparator.background", windowBg);
//		UIHashTables.put("PopupMenu.border", popupMenuBorder);
//		
//		UIHashTables.put("ProgressBar.foreground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("ProgressBar.cellLength", new Integer(10));
//		UIHashTables.put("ProgressBar.cellSpacing", new Integer(2));
//		UIHashTables.put("ProgressBar.border", progressBarBorder);
//		
//		UIHashTables.put("RadioButton.background", windowBg);
//		UIHashTables.put("RadioButton.font", stdFont);
//		UIHashTables.put("RadioButton.focusColor", focusColor);
//
//		UIHashTables.put("RadioButtonMenuItem.background", windowBg);
//		UIHashTables.put("RadioButtonMenuItem.font", stdFont);
//		UIHashTables.put("RadioButtonMenuItem.selectionBackground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("RadioButtonMenuItem.selectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("RadioButtonMenuItem.acceleratorFont", stdFont);
//		UIHashTables.put("RadioButtonMenuItem.acceleratorForeground", textFg);
//		UIHashTables.put("RadioButtonMenuItem.acceleratorSelectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("RadioButtonMenuItem.border", menuBorder);
//
//		UIHashTables.put("RootPane.frameBorder", internalFrameBorder2);
//		UIHashTables.put("RootPane.plainDialogBorder", plainDialogBorder);
//		UIHashTables.put("RootPane.informationDialogBorder", plainDialogBorder);
//		UIHashTables.put("RootPane.errorDialogBorder", plainDialogBorder);
//		UIHashTables.put("RootPane.colorChooserDialogBorder", plainDialogBorder);
//		UIHashTables.put("RootPane.fileChooserDialogBorder", plainDialogBorder);
//		UIHashTables.put("RootPane.questionDialogBorder", plainDialogBorder);
//		UIHashTables.put("RootPane.warningDialogBorder", plainDialogBorder);


/*-----------------------以下是ScrollBar参数-----------------------*/
//		if(isSlim)
//			UIHashTables.put("ScrollBar.width", new Integer(14)); // activeShadow); //windowBg);
//			

                //黑色背景
		UIHashTables.put("ScrollBar.background", COLOR_MAIN); //滚动条背景颜色
                UIHashTables.put("ScrollBar.foreground", COLOR_MAIN); //滚动条背景颜色
		UIHashTables.put("ScrollBar.thumb", COLOR_TOOLBAR);//滚动条颜色
		UIHashTables.put("ScrollBar.thumbHighlight", COLOR_TOOLBAR);//滚动条颜色
		UIHashTables.put("ScrollBar.thumbShadow", COLOR_MAIN);//滚动条阴影颜色
                UIHashTables.put("ScrollBar.thumbDarkShadow", COLOR_MAIN);//滚动条阴影颜色
                UIHashTables.put("ScrollBar.track", new ColorUIResource(100,100,100));//
                UIHashTables.put("ScrollBar.trackHighlight", COLOR_MAIN);//
		//UIHashTables.put("ScrollBar.thumbStripes", COLOR_MAIN);//滚动条条纹颜色
                
                //白色背景
//                UIHashTables.put("ScrollBar.background_white", COLOR_FOREGROUND); //滚动条背景颜色
//		UIHashTables.put("ScrollBar.thumb_white", COLOR_WINDOW_BACKGROUND2);//滚动条颜色
//		UIHashTables.put("ScrollBar.thumbHighlight_white", COLOR_TOOLBAR);//滚动条高亮获得焦点颜色
//		UIHashTables.put("ScrollBar.thumbShadow_white", COLOR_FOREGROUND);//滚动条阴影颜色
//                UIHashTables.put("ScrollBar.thumbDarkShadow_white", COLOR_FOREGROUND);//滚动条阴影颜色ScrollBar.thumbDarkShadow_white
//		UIHashTables.put("ScrollBar.thumbStripes_white", COLOR_FOREGROUND);//滚动条条纹颜色   ScrollBar.border
//                UIHashTables.put("ScrollBar.track_white", new ColorUIResource(220,220,220));//
//                UIHashTables.put("ScrollBar.border_white", null);//new BorderUIResource.MatteBorderUIResource(2, 2, 2, 2, COLOR_FOREGROUND));

                ColorUIResource 	Color_Thumb = new ColorUIResource(220,220,220);
                UIHashTables.put("ScrollBar.background_white", Color_Thumb); //滚动条背景颜色
                UIHashTables.put("ScrollBar.foreground_white", Color_Thumb); //滚动条背景颜色
		UIHashTables.put("ScrollBar.thumb_white", Color_Thumb);//√滚动条颜色
		UIHashTables.put("ScrollBar.thumbHighlight_white", Color_Thumb);//滚动条高亮获得焦点颜色
		UIHashTables.put("ScrollBar.thumbShadow_white", Color_Thumb);//滚动条阴影颜色
                UIHashTables.put("ScrollBar.thumbDarkShadow_white", new ColorUIResource(140,140,140));//√箭头颜色和滚动条右侧边缘颜色。阴影颜色ScrollBar.thumbDarkShadow_white
//		UIHashTables.put("ScrollBar.thumbStripes_white", COLOR_FOREGROUND);//滚动条条纹颜色,没感觉有什么用   ScrollBar.border
                UIHashTables.put("ScrollBar.track_white", new ColorUIResource(240,240,240));//√滚动条滑轨颜色
                UIHashTables.put("ScrollBar.border_white", null);//new BorderUIResource.MatteBorderUIResource(2, 2, 2, 2, COLOR_FOREGROUND));
/*-----------------------以上是ScrollBar参数-----------------------*/
                
/*-----------------------以下是ScrollPane参数-----------------------*/
                //黑色背景
		UIHashTables.put("ScrollPane.viewportBorder", null);
		UIHashTables.put("ScrollPane.border",new BorderUIResource.MatteBorderUIResource(1, 1, 1, 1, COLOR_DIVIDER));//注释掉因为和SplitPane.border加起来显得太粗了。
		UIHashTables.put("ScrollPane.opaque", NO_OPAQUE_BACKGROUND);//背景透明
                UIHashTables.put("ScrollPane.background", COLOR_MAIN); //背景颜色
                UIHashTables.put("ScrollPane.foreground", COLOR_FOREGROUND); //前景颜色
                //白色背景
		UIHashTables.put("ScrollPane.viewportBorder_white", null);
		UIHashTables.put("ScrollPane.border_white",new BorderUIResource.MatteBorderUIResource(1, 1, 1, 1, COLOR_DIVIDER));//注释掉因为和SplitPane.border加起来显得太粗了。
		UIHashTables.put("ScrollPane.opaque_white", NO_OPAQUE_BACKGROUND);//背景透明
                UIHashTables.put("ScrollPane.background_white", COLOR_TABLE_BACKGROUND2); //背景颜色
                UIHashTables.put("ScrollPane.foreground_white", COLOR_TABLE_FOREGROUND2); //前景颜色
/*-----------------------以上是ScrollPane参数-----------------------*/
//
//		UIHashTables.put("Separator.foreground", COLOR_BORDER);
//		UIHashTables.put("Separator.background", windowBg);
//
		UIHashTables.put("Slider.background", COLOR_MAIN);
		UIHashTables.put("Slider.foreground", COLOR_FOREGROUND);//foreground
		UIHashTables.put("Slider.trackColor", activeBg);//Slider.trackColor滑道的颜色
                UIHashTables.put("Slider.tickColor", COLOR_FOREGROUND);//Slider.tickColor刻度的颜色
                UIHashTables.put("Slider.highlight", COLOR_FOREGROUND);//
                UIHashTables.put("Slider.shadow", COLOR_FOREGROUND);//
                UIHashTables.put("Slider.focus", COLOR_MAIN);//控件获得焦点时，四周的点虚线颜色                
                
                UIHashTables.put("Slider.background_white", COLOR_WINDOW_BACKGROUND2);
		UIHashTables.put("Slider.foreground_white", COLOR_WINDOW_FOREGROUND2);//foreground
                
                UIHashTables.put("Slider.highlight_white", COLOR_WINDOW_FOREGROUND2);//
                UIHashTables.put("Slider.shadow_white", COLOR_WINDOW_FOREGROUND2);//
                UIHashTables.put("Slider.focus_white", COLOR_WINDOW_FOREGROUND2);//控件获得焦点时，四周的点虚线颜色  和背景颜色一致时，就不用看到讨厌的控件四周的点虚线

//
		UIHashTables.put("SplitPane.dividerSize", 8);
//		UIHashTables.put("SplitPane.oneTouchDividerSize", new Integer(8));
		UIHashTables.put("SplitPane.background", COLOR_MAIN);
                UIHashTables.put("SplitPane.dividerground", COLOR_DIVIDER);
		UIHashTables.put("SplitPane.border", new BorderUIResource.MatteBorderUIResource(5, 5, 5, 5, COLOR_DIVIDER));
                
                UIHashTables.put("SplitPane.background_white", COLOR_TABLE_BACKGROUND2);
                UIHashTables.put("SplitPane.dividerground_white", COLOR_TABLE_FOREGROUND2);
		UIHashTables.put("SplitPane.border_white", new BorderUIResource.MatteBorderUIResource(5, 5, 5, 5, COLOR_WINDOW_BACKGROUND2));

                //result.put("SplitPane.Insets", new InsetsUIResource(2, 16,16, 16));//设置上下空白：上，左，下，右
                
                
//
		//UIHashTables.put("Spinner.border", null);
                UIHashTables.put("Spinner.background", COLOR_MAIN);
                UIHashTables.put("Spinner.foreground", COLOR_FOREGROUND);
                UIHashTables.put("Spinner.font", FONT_STANDARD);
                
/*-----------------------以下是TabbedPane参数-----------------------*/
		UIHashTables.put("TabbedPane.selectedTabPadInsets", new InsetsUIResource(2, 0, 0, 0));
		
		if(isSlim)
			UIHashTables.put("TabbedPane.tabInsets", new InsetsUIResource(4, 8, 4, 8));//设置Tab项目上下空白：上，左，下，右
		else
			UIHashTables.put("TabbedPane.tabInsets", new InsetsUIResource(13, 6, 12, 6));
			
		UIHashTables.put("TabbedPane.font", FONT_TITLE);
		UIHashTables.put("TabbedPane.thickBorders", false);
                UIHashTables.put("TabbedPane.tabsOverlapBorder", true);
		UIHashTables.put("TabbedPane.selectedFont", FONT_TITLE_BOLD);
		UIHashTables.put("TabbedPane.selected", COLOR_DIVIDER);//
                UIHashTables.put("TabbedPane.focus", COLOR_DIVIDER);//控件获得焦点时，四周的点虚线颜色。和TabbedPane.selected设置成一个颜色的
		UIHashTables.put("TabbedPane.background", COLOR_MAIN);//
                UIHashTables.put("TabbedPane.foreground", COLOR_FOREGROUND);//
                UIHashTables.put("TabbedPane.opaque", OPAQUE_BACKGROUND);//背景透明
                UIHashTables.put("TabbedPane.tabsOpaque", NO_OPAQUE_BACKGROUND);//背景透明
                
                /*-----------------------以下是TabbedPane_small参数-----------------------*/
                UIHashTables.put("TabbedPane.background_small", COLOR_WINDOW_BACKGROUND2);//
                UIHashTables.put("TabbedPane.foreground_small", COLOR_TABLE_FOREGROUND2);//
                UIHashTables.put("TabbedPane.font_small", FONT_TITLE_SMALL);
                UIHashTables.put("TabbedPane.selected_small", new ColorUIResource(150,150,150));
                UIHashTables.put("TabbedPane.focus_small", new ColorUIResource(150,150,150));//控件获得焦点时，四周的点虚线颜色。和TabbedPane.selected设置成一个颜色的
/*-----------------------TabbedPane参数结束-----------------------*/

		UIHashTables.put("Table.scrollPaneBorder", new BorderUIResource.LineBorderUIResource(COLOR_BORDER));
		UIHashTables.put("Table.selectionBackground", COLOR_SEL_BACKGROUND);
		UIHashTables.put("Table.selectionForeground", COLOR_SEL_FOREGROUND);
		UIHashTables.put("Table.focusCellBackground", COLOR_SEL_BACKGROUND);
		UIHashTables.put("Table.focusCellForeground", COLOR_SEL_FOREGROUND);
		UIHashTables.put("Table.focusCellHighlightBorder", null);
		
                UIHashTables.put("Table.rowHeight", 40);
                
                UIHashTables.put("TableHeader.cellBorder", new BorderUIResource.MatteBorderUIResource(0, 0, 1, 1, COLOR_BORDER));
                UIHashTables.put("TableHeader.background", COLOR_MAIN);
//                UIHashTables.put("Table.background", COLOR_MAIN);
//                UIHashTables.put("Table.foreground", COLOR_FOREGROUND);
//
//		UIHashTables.put("TextArea.selectionBackground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("TextArea.selectionForeground", COLOR_SEL_FOREGROUND);
//
//		UIHashTables.put("TextField.selectionBackground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("TextField.selectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("TextField.border", simpleBorder);
//						
//		UIHashTables.put("ToggleButton.border", null);
//		UIHashTables.put("ToggleButton.background", activeBg);
//
//		UIHashTables.put("ToolBar.background", windowBg);
//		UIHashTables.put("ToolBar.border", menuBarBorder);
//		UIHashTables.put("ToolBar.verticalBorder", toolBarVerticalBorder);
//		
//		UIHashTables.put("ToolButton.activeBackground", toolButtonActiveBg);
//		UIHashTables.put("ToolButton.activeForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("ToolButton.activeBorderColor", toolButtonBorder);
//		
//		UIHashTables.put("ToolTip.background", toolTipBg);
//		UIHashTables.put("ToolTip.border", new BorderUIResource.LineBorderUIResource(COLOR_BORDER));
//		
//		UIHashTables.put("Tree.expandedIcon", LookAndFeel.makeIcon(getClass(),"../image/TreeexpandedIcon.png"));
//		UIHashTables.put("Tree.leafIcon", LookAndFeel.makeIcon(getClass(),"icons/tree_leaf.gif"));
//		UIHashTables.put("Tree.collapsedIcon", LookAndFeel.makeIcon(getClass(),"../image/TreecollapsedIcon.png"));
//		UIHashTables.put("Tree.closedIcon", LookAndFeel.makeIcon(getClass(),"icons/tree_closed.gif"));
//		UIHashTables.put("Tree.openIcon", LookAndFeel.makeIcon(getClass(),"icons/tree_open.gif"));
//		UIHashTables.put("Tree.selectionBackground", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("Tree.selectionBorderColor", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("Tree.selectionForeground", COLOR_SEL_FOREGROUND);
//		UIHashTables.put("Tree.textBackground", fieldBg);
//		UIHashTables.put("Tree.textForeground", textFg);
//		UIHashTables.put("Tree.font", stdFont);
//		UIHashTables.put("Tree.foreground", textFg);
//		UIHashTables.put("Tree.line", COLOR_SEL_BACKGROUND);
//		UIHashTables.put("Tree.hash", treeLineColor);
//		UIHashTables.put("Tree.rowHeight", new Integer(17));
//		
//		// Set constants for NetBeans (workaround for buggy NB)
//		UIHashTables.put("controlFont", new FontUIResource(new Font("Dialog", Font.PLAIN, 11)));
//		
//		// Associate the icons with their components
//		setIcon(UIHashTables, "Menu.arrowIcon", "icons/arrow.gif");
//		setIcon(UIHashTables, "Menu.invArrowIcon", "icons/invarrow.gif");
//		setIcon(UIHashTables, "CheckBoxMenuItem.uncheckIcon", "icons/menucheckbox0.gif");
//		setIcon(UIHashTables, "CheckBoxMenuItem.checkIcon", "icons/menucheckbox1.gif");
//		setIcon(UIHashTables, "RadioButtonMenuItem.uncheckIcon", "icons/menuradiobutton0.gif");
//		setIcon(UIHashTables, "RadioButtonMenuItem.checkIcon", "icons/menuradiobutton1.gif");
//		setIcon(UIHashTables, "ComboBox.icon", "icons/downarrow.gif");
//		setIcon(UIHashTables, "Arrow.down", "icons/downarrow.gif");
//		setIcon(UIHashTables, "Arrow.up", "icons/uparrow.gif");
//		setIcon(UIHashTables, "Arrow.left", "icons/leftarrow.gif");
//		setIcon(UIHashTables, "Arrow.right", "icons/rightarrow.gif");
//		setIcon(UIHashTables, "RadioButton.unselectedEnabledIcon", "icons/radiobutton0.gif");
//		setIcon(UIHashTables, "RadioButton.selectedEnabledIcon", "icons/radiobutton1.gif");
//		setIcon(UIHashTables, "RadioButton.unselectedDisabledIcon", "icons/radiobutton0.gif");
//		setIcon(UIHashTables, "RadioButton.selectedDisabledIcon", "icons/radiobutton1.gif");
//		setIcon(UIHashTables, "CheckBox.unselectedEnabledIcon", "icons/checkbox0.gif");
//		setIcon(UIHashTables, "CheckBox.selectedEnabledIcon", "icons/checkbox1.gif");
//		setIcon(UIHashTables, "CheckBox.unselectedDisabledIcon", "icons/checkbox0.gif");
//		setIcon(UIHashTables, "CheckBox.selectedDisabledIcon", "icons/checkbox1.gif");
//		setIcon(UIHashTables, "Slider.horizontalThumbIcon", "icons/sliderdown.gif");
//		setIcon(UIHashTables, "Slider.verticalThumbIcon", "icons/sliderright.gif");

//                setIcon(UIHashTables, "Tree.expandedIcon", "../image/TreeexpandedIcon.png");
//                setIcon(UIHashTables, "Tree.collapsedIcon", "../image/TreecollapsedIcon.png");
//                UIManager.put("Tree.collapsedIcon", ImageIconBufferPool.getInstance().getImageIcon("TreecollapsedIcon.png"));
//                UIManager.put("Tree.expandedIcon", ImageIconBufferPool.getInstance().getImageIcon("TreeexpandedIcon.png"));
		
		return UIHashTables;
	}
	
	
	/**	Adds to the specified UIDefaults an association of the specified key with
	 * 	an icon made from the specified icon file name. The icon is created
	 * 	by calling makeIcon().
	 * 
	 * 	@param	uiDefaults			The UI defaults table for which the specified
	 * 										icon will be added
	 * 	@param	key					The key under which the icon will be added
	 * 	@param	iconFileName		The file name of the icon to be associated
	 * 										with the key
	 * 	@see		#makeIcon()
	 */
	private void setIcon(UIDefaults uiDefaults, String key, String iconFileName)
	{
		Object icon=LookAndFeel.makeIcon(getClass(), iconFileName);
		uiDefaults.put(key, icon);
	}
	

	/**	Returns a one line description of this look and feel
     * @return  */
        @Override
	public String getDescription()
	{
		return "Jyms look and feel";
	}
	

	/**	Returns a String that identifies this look and feel
     * @return  */
        @Override
	public String getID()
	{
		return "JymsLF";
	}
	

	/**	Returns the name of this look and feel
     * @return  */
        @Override
	public String getName()
	{
		return "Jyms Look and Feel";
	}
	

	/**	Returns true if the LookAndFeel returned RootPaneUI instances support 
	 * 	providing Window decorations in a JRootPane.
     * @return 
	 */
        @Override
	public boolean getSupportsWindowDecorations()
	{
		return true;
	}
	

	/**	This is called before the first (and usually only) call to getDefaults.
	 * 
	 * 	@see	#getDefaults()
	 */
        @Override
	public void initialize()
	{
		super.initialize();
	}
	

	/**	Returns true if this is the native look and feel of the current
	 * 	operating system.
     * @return 
	 */ 
        @Override
	public boolean isNativeLookAndFeel()
	{
		return false;
	}
	

	/**	Returns true if the underlying platform supports or allows
	 * 	this look and feel
     * @return 
	 */
        @Override
	public boolean isSupportedLookAndFeel()
	{
		return true;
	}

}
