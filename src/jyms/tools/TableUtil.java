/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import jyms.data.TxtLogger;

/**
 *
 * @author John
 */
public class TableUtil {
    
    
    private static final String sFileName = "--->>TableModelProcess.java";
    /**
    *
    * @author John
    */

    public static class TableEditor extends DefaultCellEditor {
            private int columnEditor = -1;
            private String componentType = "JButton";
            public TableEditor(JCheckBox checkBox,int column,String ComponentType) {
                super(checkBox); 
                this.columnEditor = column;
                this.componentType = ComponentType;
            }

            public TableEditor(JComboBox comboBox,int column,String ComponentType) {
                    super(comboBox);
                    this.columnEditor = column;
                    this.componentType = ComponentType;
            }

            public TableEditor(JTextField textField,int column,String ComponentType) {
                    super(textField);
                    this.columnEditor = column;
                    this.componentType = ComponentType;
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                            boolean isSelected, int row, int column) {
//                    Component label;
                    if (row>=0 && column==this.columnEditor) {
                        if (this.componentType.equals("JCheckBox")){
                                JCheckBox checkBox = new JCheckBox();
                                checkBox.addItemListener(new java.awt.event.ItemListener() {
                                    @Override
                                    public void itemStateChanged(java.awt.event.ItemEvent evt) {
                                        jCheckBoxItemStateChanged(evt);
                                    }
                                });
                                return checkBox;
                        } else if (this.componentType.equals("JTextField")){
                            JTextField textField = new JTextField();
                            textField.setText((String)value);
                            return textField;
                        }
                        else {
                                JButton button = new JButton("刷新");
                                button.addActionListener(new ActionListener(){
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            // TODO Auto-generated method stub
                                            jButtonactionPerformed(e);
                                        }
                                });
                                button.setPreferredSize(new Dimension(table.getColumnModel().getColumn(column).getPreferredWidth(),4));
                                return button;
                        }
 
                    } else return super.getTableCellEditorComponent(table, value, isSelected,  row, column);
            }
            private void jButtonactionPerformed(ActionEvent e){}
            private void jCheckBoxItemStateChanged(ItemEvent evt){
//            JOptionPane.showMessageDialog(null, "test2");
            }
    }
    
    /**
     *
     * @author John
     */
    public static class ButtonTableRender extends DefaultTableCellRenderer {
        
            private int columnRender = -1;
            private String componentType = "JButton";
            public ButtonTableRender(int column,String ComponentType){
                this.columnRender = column;
                this.componentType = ComponentType;
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,

                            boolean isSelected, boolean hasFocus, int row, int column) {

                    if (row>=0 && column==this.columnRender) {
                            if (this.componentType.equals("JCheckBox")){
                                    JCheckBox checkBox = new JCheckBox();
                                    checkBox.setPreferredSize(new Dimension(table.getColumnModel().getColumn(column).getPreferredWidth(),4));
                                    return checkBox;
                            }
                            else{
                                    JButton label = new JButton("Refresh");
                                    label.setPreferredSize(new Dimension(table.getColumnModel().getColumn(column).getPreferredWidth(),4));
                                    return label;
                            }
                    } else return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);		
            }
    }
    /**
        *内部类:   CheckHeaderCellRenderer
        *类描述:   自己定制的带JCheckBox表格标题栏
        *用法：    table.getTableHeader().setDefaultRenderer(new CheckHeaderCellRenderer(table));
    */  
    public static class CheckHeaderCellRenderer implements TableCellRenderer {
        JTableCheckBoxModel tableModel;
        JTableHeader tableHeader;
        final JCheckBox selectBox;

        public CheckHeaderCellRenderer(JTable table) {
            this.tableModel = (JTableCheckBoxModel)table.getModel();
            this.tableHeader = table.getTableHeader();
            selectBox = new JCheckBox(tableModel.getColumnName(0));
            selectBox.setSelected(false);
            tableHeader.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 0) {
                        //获得选中列
                        int selectColumn = tableHeader.columnAtPoint(e.getPoint());
                        if (selectColumn == 0) {
                            boolean value = !selectBox.isSelected();
                            selectBox.setSelected(value);
                            tableModel.selectAllOrNull(value);
                            tableHeader.repaint();
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // TODO Auto-generated method stub
            String valueStr = (String) value;
            JLabel label = new JLabel(valueStr);
            label.setHorizontalAlignment(SwingConstants.CENTER); // 表头标签剧中
            selectBox.setHorizontalAlignment(SwingConstants.CENTER);// 表头标签剧中
            selectBox.setBorderPainted(true);
            JComponent component = (column == 0) ? selectBox : label;

            component.setForeground(tableHeader.getForeground());
            component.setBackground(tableHeader.getBackground());
            component.setFont(tableHeader.getFont());
            component.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

            return component;
        }

    }
    /**
        *内部类:   JTableCheckBoxModel
        *类描述:   自己定制的JCheckBox表格式数据模型，JTableCustomizeModel;只有第一列可以修改
    */  
    public static class JTableCheckBoxModel extends JTableCustomizeModel
    {

            public JTableCheckBoxModel(String[] columnNames) {
                super(columnNames);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) return true;
                return false;
            }
            
            public void selectAllOrNull(boolean value) {
            for (int i = 0; i < getRowCount(); i++) {
                this.setValueAt(value, i, 0);
            }
        }
    }
    /**
        *内部类:   JTableCustomizeModel
        *类描述:   自己定制的表格式数据模型，继承AbstractTableModel，其他地方可以再次继承
    */  
    public static class JTableCustomizeModel extends AbstractTableModel
    {

            private Vector Myrows = new Vector();
            private String[] Mycolumns;

            public JTableCustomizeModel(String[] columnNames) {
                Mycolumns = columnNames;
            }

            public Vector getDataVector(){
                return Myrows;
            }
            public boolean addRow(Vector rowData) {
                return Myrows.add(rowData);
            } 
            public void removeRow(int row) {
                Myrows.removeElementAt(row);
            }
            public void removeAll(){
                Myrows.clear();
            }
            @Override
            public String getColumnName(int column) {
                return Mycolumns[column];
            }

            @Override
            public int getRowCount() {
                return Myrows.size();
            }

            @Override
            public int getColumnCount() {
                return Mycolumns.length;
            }

            @Override
            public Object getValueAt(int row, int column) {
                return ((Vector)Myrows.elementAt(row)).elementAt(column);
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                ((Vector)Myrows.elementAt(row)).setElementAt(aValue, column);//.elementAt(column) = aValue;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
    //            if (column == 0) return true;
                return false;
            }

            @Override
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
    } 
    
    /**
        *函数:      resizeTable
        *函数描述:  调整列表的列宽度
        * @param jsp
        * @param jg_table
        * @param bool true界面显示后，如果父容器大小改变，使用实际大小而不是首选大小；false父容器大小为首选大小
    */
    private void resizeTable(JScrollPane jsp,JTable jg_table,boolean bool) {
        Dimension containerwidth = null;
        try{
            if (!bool) {
                //初始化时，父容器大小为首选大小，实际大小为0
                containerwidth = jsp.getPreferredSize();
            } else {
                //界面显示后，如果父容器大小改变，使用实际大小而不是首选大小
                containerwidth = jsp.getSize();
            }
            //计算表格总体宽度 getTable().
            int allwidth = jg_table.getIntercellSpacing().width;
            for (int j = 0; j < jg_table.getColumnCount(); j++) {
                //计算该列中最长的宽度
                int max = 0;
                for (int i = 0; i < jg_table.getRowCount(); i++) {
                    int width = jg_table.getCellRenderer(i, j).getTableCellRendererComponent(jg_table, jg_table.getValueAt(i, j), false,false, i, j).getPreferredSize().width;
                    if (width > max) {
                        max = width;
                    }
                }
                //计算表头的宽度
                int headerwidth = jg_table.getTableHeader().getDefaultRenderer()
                        .getTableCellRendererComponent(jg_table, jg_table.getColumnModel(). getColumn(j).getIdentifier(), false, false,-1, j).getPreferredSize().width;
                //列宽至少应为列头宽度
                max += headerwidth;
                //设置列宽
                jg_table.getColumnModel().getColumn(j).setPreferredWidth(max);
                //给表格的整体宽度赋值，记得要加上单元格之间的线条宽度1个像素
                allwidth += max + jg_table.getIntercellSpacing().width;
            }
            allwidth += jg_table.getIntercellSpacing().width;
            //如果表格实际宽度大小父容器的宽度，则需要我们手动适应；否则让表格自适应
            if (allwidth > containerwidth.width) {
                jg_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            } else {
                jg_table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
            }
        }catch(Exception e)
        {
            TxtLogger.append(this.sFileName, "resizeTable()","系统在调整列表的列宽度过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }
    
    public static void modifyTableHeaderCellRendere(JTable MyTable){
        //实例化一个DefaultTreeCellRenderer对象
        JTableHeader THeader = MyTable.getTableHeader();
        DefaultTableCellRenderer CellRender = (DefaultTableCellRenderer) MyTable.getTableHeader().getDefaultRenderer();
        CellRender.setBackground(new java.awt.Color(64, 64, 64));
        CellRender.setForeground(Color.WHITE);
        
    }
    
    /**
     *不知为什么总是出现和预期相反的效果
     * @author John
     */
    public static class ColorTableRender extends DefaultTableCellRenderer {
        
            private int rowRender = 0;
            private Color rowColor = Color.white;
            private static ColorTableRender  renderInstance = null ;
            
            private ColorTableRender(int Row,Color RowColor){
                this.rowRender = Row;
                this.rowColor = RowColor;
            }
            
            public static ColorTableRender getRenderInstance(int Row, Color RowColor){
                if (renderInstance == null) renderInstance = new ColorTableRender(Row,RowColor);
                else {
                    renderInstance.setRowRender(Row);
                    renderInstance.setRowColor(RowColor);
                } 
                return renderInstance;
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                    Component com =  super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column);
                    Color cc = com.getBackground();
                    if (row == rowRender)//你要变色的行
                        com.setBackground(rowColor);
                    else 
                        com.setBackground(cc);
                    return com;
            }

            private void setColor(int Row, Color RowColor){
                this.rowRender = Row;
                this.rowColor = RowColor;
            }
            /**
             * @param rowRender the rowRender to set
             */
            private void setRowRender(int rowRender) {
                this.rowRender = rowRender;
            }

            /**
             * @param rowColor the rowColor to set
             */
            private void setRowColor(Color rowColor) {
                this.rowColor = rowColor;
            }
            

    }
}
