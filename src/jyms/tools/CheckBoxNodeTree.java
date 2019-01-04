/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;
import javax.swing.tree.DefaultMutableTreeNode;  
import java.awt.Component;  
  
import javax.swing.JCheckBox;  
import javax.swing.JPanel;  
import javax.swing.tree.TreeCellRenderer; 
import java.awt.Color;  
import java.awt.Dimension;  
import java.awt.Graphics;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;  
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;  
import javax.swing.JScrollPane;
import javax.swing.UIManager;  
import javax.swing.plaf.ColorUIResource; 
import javax.swing.tree.TreePath;
import javax.swing.JTree;  
import javax.swing.tree.DefaultTreeModel;  
import jyms.CommonParas;

/**
    *类:      CheckBoxNodeTree
    *类描述:  CheckBoxTree与JTree在两个层面上存在差异：
    *           1.在模型层上，CheckBoxTree的每个结点需要一个成员来保存其是否被选中，但是JTree的结点则不需要。
    *           2.在视图层上，CheckBoxTree的每个结点比JTree的结点多显示一个复选框。
    *             既然存在两个差异，那么只要我们把这两个差异部分通过自己的实现填补上，那么带复选框的树也就实现了。
 */
public class CheckBoxNodeTree {
    
    
    /**
        *类:      CheckBoxTreeNode
        *类描述:  定义一个新的结点类CheckBoxTreeNode，该类继承DefaultMutableTreeNode，并增加新的成员isSelected来表示该结点是否被选中。
        *           对于一颗CheckBoxTree，如果某一个结点被选中的话，其复选框会勾选上，并且使用CheckBoxTree的动机在于可以一次性地选中一颗子树。
        *           那么，在选中或取消一个结点时，其祖先结点和子孙结点应该做出某种变化。
    */
    public static class CheckBoxTreeNode extends DefaultMutableTreeNode  
    {  
            protected boolean isSelected;  

            public CheckBoxTreeNode()  
            {  
                this(null);  
            }  

            public CheckBoxTreeNode(Object userObject)  
            {  
                this(userObject, true, false);  
            }  

            public CheckBoxTreeNode(Object userObject, boolean allowsChildren, boolean isSelected)  
            {  
                super(userObject, allowsChildren);  
                this.isSelected = isSelected;  
            }  

            public boolean isSelected()  
            {  
                return isSelected;  
            }  

            public void setSelected(boolean _isSelected)  
            {  
                this.isSelected = _isSelected;  

                if(_isSelected)  
                {  
                    // 如果选中，则将其所有的子结点都选中  
                    if(children != null)  
                    {  
                        for(Object obj : children)  
                        {  
                            CheckBoxTreeNode node = (CheckBoxTreeNode)obj;  
                            if(_isSelected != node.isSelected())  
                                node.setSelected(_isSelected);  
                        }  
                    }  
                    // 向上检查，如果父结点的所有子结点都被选中，那么将父结点也选中  
                    CheckBoxTreeNode pNode = (CheckBoxTreeNode)parent;  
                    // 开始检查pNode的所有子节点是否都被选中  
                    if(pNode != null)  
                    {  
                        int index = 0;  
                        for(; index < pNode.children.size(); ++ index)  
                        {  
                            CheckBoxTreeNode pChildNode = (CheckBoxTreeNode)pNode.children.get(index);  
                            if(!pChildNode.isSelected())  
                                break;  
                        }  
                        /*  
                         * 表明pNode所有子结点都已经选中，则选中父结点， 
                         * 该方法是一个递归方法，因此在此不需要进行迭代，因为 
                         * 当选中父结点后，父结点本身会向上检查的。 
                         */  
                        if(index == pNode.children.size())  
                        {  
                            if(pNode.isSelected() != _isSelected)  
                                pNode.setSelected(_isSelected);  
                        }  
                    }  
                }  
                else   
                {  
                    /* 
                     * 如果是取消父结点导致子结点取消，那么此时所有的子结点都应该是选择上的； 
                     * 否则就是子结点取消导致父结点取消，然后父结点取消导致需要取消子结点，但 
                     * 是这时候是不需要取消子结点的。 
                     */  
                    if(children != null)  
                    {  
                        int index = 0;  
                        for(; index < children.size(); ++ index)  
                        {  
                            CheckBoxTreeNode childNode = (CheckBoxTreeNode)children.get(index);  
                            if(!childNode.isSelected())  
                                break;  
                        }  
                        // 从上向下取消的时候  
                        if(index == children.size())  
                        {  
                            for(int i = 0; i < children.size(); ++ i)  
                            {  
                                CheckBoxTreeNode node = (CheckBoxTreeNode)children.get(i);  
                                if(node.isSelected() != _isSelected)  
                                    node.setSelected(_isSelected);  
                            }  
                        }  
                    }  

                    // 向上取消，只要存在一个子节点不是选上的，那么父节点就不应该被选上。  
                    CheckBoxTreeNode pNode = (CheckBoxTreeNode)parent;  
                    if(pNode != null && pNode.isSelected() != _isSelected)  
                        pNode.setSelected(_isSelected);  
                }  
            }  
    }  
    /**
        *类:      CheckBoxTreeCellRenderer
        *类描述:  第二个差异是外观上的差异，JTree的每个结点是通过TreeCellRenderer进行显示的。
        *           为了解决第二个差异，我们定义一个新的类CheckBoxTreeCellRenderer，该类实现了TreeCellRenderer接口。
    */
    public static class CheckBoxTreeCellRenderer extends JPanel implements TreeCellRenderer  
    {  
            protected JCheckBox check;  
            protected CheckBoxTreeLabel label;  

            public CheckBoxTreeCellRenderer()  
            {  
                setBackground(UIManager.getColor("Tree.textBackground"));//Color.WHITE);
//                Color fff= UIManager.getColor("Tree.background");
//                System.out.println(fff);
                setLayout(null);  
                setBorder(null);
                add(check = new JCheckBox());  
                add(label = new CheckBoxTreeLabel());  
                check.setBackground(UIManager.getColor("Tree.textBackground"));  //("Tree.textBackground"));  
                label.setForeground(UIManager.getColor("Tree.textForeground"));  
            }  

            /** 
                * 返回的是一个<code>JPanel</code>对象，该对象中包含一个<code>JCheckBox</code>对象 
                * 和一个<code>JLabel</code>对象。并且根据每个结点是否被选中来决定<code>JCheckBox</code> 
                * 是否被选中。 
                * @param tree
                * @param value
                * @param selected
                * @param expanded
                * @param leaf
                * @param row
                * @param hasFocus
             */  
            @Override  
            public Component getTreeCellRendererComponent(JTree tree, Object value,  
                    boolean selected, boolean expanded, boolean leaf, int row,  
                    boolean hasFocus)  
            {  
                String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);  
                setEnabled(tree.isEnabled());  
                check.setSelected(((CheckBoxTreeNode)value).isSelected()); 
                check.setBounds(0, 0, check.getWidth(), tree.getRowHeight());  
                label.setBounds(check.getWidth(), 0, label.getWidth(), tree.getRowHeight());  
                label.setFont(tree.getFont());  
                label.setText(stringValue);  
                label.setSelected(selected);  
                label.setFocus(hasFocus);  
                if(leaf)  

//                    if(stringValue.contains("监控点")){
//                        label.setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_monitorpoint.png"));
//                    }else if(stringValue.contains("报警输入")){
//                        label.setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_alarmin.png"));
//                    }else if(stringValue.contains("报警输出")){
//                        label.setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_alarmout.png"));
//                    }else if(stringValue.contains("硬盘")){
//                        label.setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_disk.png"));
//                    }else{//设备节点
//                        label.setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_device.png"));
//                    }
                    if(stringValue.contains(CommonParas.DVRResourceType.NODETYPE_CHANNEL) || stringValue.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_NAME)){//"监控点""通道"
                        label.setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_monitorpoint.png"));
                    }else if(stringValue.contains(CommonParas.DVRResourceType.NODETYPE_ALARMIN) || stringValue.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_NAME)){//"报警输入"
                        label.setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_alarmin.png"));
                    }else if(stringValue.contains(CommonParas.DVRResourceType.NODETYPE_ALARMOUT) || stringValue.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_NAME)){//"报警输出"
                        label.setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_alarmout.png"));
                    }else if(stringValue.contains(CommonParas.DVRResourceType.NODETYPE_DISK) || stringValue.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_DISK_NAME)){//"硬盘"
                        label.setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_disk.png"));
                    }else{//设备节点
                        label.setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_device.png"));
                    }
                
//                    label.setIcon(UIManager.getIcon("Tree.leafIcon"));  
                else if(expanded)  
                    label.setIcon(UIManager.getIcon("Tree.openIcon"));  
                else  
                    label.setIcon(UIManager.getIcon("Tree.closedIcon"));  

                return this;  
            }  

            @Override  
            public Dimension getPreferredSize()  
            {  
                Dimension dCheck = check.getPreferredSize();  
                Dimension dLabel = label.getPreferredSize();  
                return new Dimension(dCheck.width + dLabel.width, dCheck.height < dLabel.height ? dLabel.height: dCheck.height);  
            }  

            @Override  
            public void doLayout()  
            {  
                Dimension dCheck = check.getPreferredSize();  
                Dimension dLabel = label.getPreferredSize();  
                int yCheck = 0;  
                int yLabel = 0;  
                if(dCheck.height < dLabel.height)  
                    yCheck = (dLabel.height - dCheck.height) / 2;  
                else  
                    yLabel = (dCheck.height - dLabel.height) / 2;  
                check.setLocation(0, yCheck);  
                check.setBounds(0, yCheck, dCheck.width, dCheck.height);  
                label.setLocation(dCheck.width, yLabel);  
                label.setBounds(dCheck.width, yLabel, dLabel.width, dLabel.height);  
            }  

            @Override  
            public void setBackground(Color color)  
            {  
                if(color instanceof ColorUIResource)  
                    color = null;  
                super.setBackground(color);  
            }  
    }    
    
    /**
        *类:      CheckBoxTreeLabel
        *类描述:  在CheckBoxTreeCellRenderer的实现中，getTreeCellRendererComponent方法返回的是JPanel，而不是像DefaultTreeCellRenderer那样返回JLabel，
        *           因此JPanel中的JLabel无法对选中做出反应，因此我们重新实现了一个JLabel的子类CheckBoxTreeLabel，它可以对选中做出反应
    */
    public static class CheckBoxTreeLabel extends JLabel  
    {  
            private boolean isSelected;  
            private boolean hasFocus;  

            public CheckBoxTreeLabel()  
            {  
            }  

            @Override  
            public void setBackground(Color color)  
            {  
                if(color instanceof ColorUIResource)  
                    color = null;  
                super.setBackground(color);  
            }  

            @Override  
            public void paint(Graphics g)  
            {  
                String str;  
                if((str = getText()) != null)  
                {  
                    if(0 < str.length())  
                    {  
                        if(isSelected)  
                            g.setColor(UIManager.getColor("Tree.selectionBackground"));  
                        else  
                            g.setColor(UIManager.getColor("Tree.textBackground"));  
                        Dimension d = getPreferredSize();  
                        int imageOffset = 0;  
                        Icon currentIcon = getIcon();  
                        if(currentIcon != null)  
                            imageOffset = currentIcon.getIconWidth() + Math.max(0, getIconTextGap() - 1);  
                        g.fillRect(imageOffset, 0, d.width - 1 - imageOffset, d.height);  
                        if(hasFocus)  
                        {  
                            g.setColor(UIManager.getColor("Tree.selectionBorderColor"));  
                            g.drawRect(imageOffset, 0, d.width - 1 - imageOffset, d.height - 1);  
                        }  
                    }  
                }  
                super.paint(g);  
            }  

            @Override  
            public Dimension getPreferredSize()  
            {  
                Dimension retDimension = super.getPreferredSize();  
                if(retDimension != null)  
                    retDimension = new Dimension(retDimension.width + 3, retDimension.height);  
                return retDimension;  
            }  

            public void setSelected(boolean isSelected)  
            {  
                this.isSelected = isSelected;  
            }  

            public void setFocus(boolean hasFocus)  
            {  
                this.hasFocus = hasFocus;  
            }  
    }  

    /**
        *类:      CheckBoxTreeNodeSelectionListener
        *类描述:  CheckBoxTree可以响应用户事件决定是否选中某个结点。为此，我们为CheckBoxTree添加一个响应用户鼠标事件的监听器CheckBoxTreeNodeSelectionListener
    */    
    public static class CheckBoxTreeNodeSelectionListener extends MouseAdapter  
    {  
        @Override  
         public void mouseClicked(MouseEvent event)  
         {  
            JTree tree = (JTree)event.getSource();  
             int x = event.getX();  
             int y = event.getY();  
             int row = tree.getRowForLocation(x, y);  
             TreePath path = tree.getPathForRow(row);  
            if(path != null)  
            {  
                 CheckBoxTreeNode node = (CheckBoxTreeNode)path.getLastPathComponent();  
                 if(node != null)  
                 {  
                     boolean isSelected = !node.isSelected();  
                     node.setSelected(isSelected);  
                     ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(node);  
                 }  
             }  
         }  
    }  
    
    public static void main(String[] args)  
    {  
        JFrame frame = new JFrame("CheckBoxTreeDemo");  
        frame.setBounds(200, 200, 400, 400);  
        final JTree tree = new JTree();  
        final CheckBoxTreeNode rootNode = new CheckBoxTreeNode("root");  
        final CheckBoxTreeNode node1 = new CheckBoxTreeNode("node_1");  
        CheckBoxTreeNode node1_1 = new CheckBoxTreeNode("node_1_1");  
        CheckBoxTreeNode node1_2 = new CheckBoxTreeNode("node_1_2");  
        CheckBoxTreeNode node1_3 = new CheckBoxTreeNode("node_1_3");  
        node1.add(node1_1);  
        node1.add(node1_2);  
        node1.add(node1_3); 
        CheckBoxTreeNode node2 = new CheckBoxTreeNode("node_2");  
        CheckBoxTreeNode node2_1 = new CheckBoxTreeNode("node_2_1");  
        CheckBoxTreeNode node2_2 = new CheckBoxTreeNode("node_2_2");  
        
        node2.add(node2_1);  
        node2.add(node2_2);  
        rootNode.add(node1);  
        rootNode.add(node2);   
        
        DefaultTreeModel model = new DefaultTreeModel(rootNode);  
        tree.addMouseListener(new CheckBoxTreeNodeSelectionListener());  
        tree.setModel(model);  
        tree.setCellRenderer(new CheckBoxTreeCellRenderer());  
        JScrollPane scroll = new JScrollPane(tree);  
        scroll.setBounds(0, 0, 300, 320);  
        frame.getContentPane().add(scroll); 
        JButton aa = new JButton("test");
        aa.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int ii = rootNode.getChildCount();
                for (int i=0;i<rootNode.getChildCount();i++)
                {
                    String[] str=new String[rootNode.getChildAt(i).getChildCount()];

                    System.out.println(rootNode.getChildAt(i).toString());
                    for (int a=0;a<rootNode.getChildAt(i).getChildCount();a++)
                    {
                        str[a]=rootNode.getChildAt(i).getChildAt(a).toString();
                        System.out.println(str[a]);
                    }

                    //list.add(str);
                }
                int ii1 = node1.getChildCount();
                System.out.println(""+ ii);
                System.out.println(""+ ii1);
            }
        });
          frame.getContentPane().add(aa); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setVisible(true);  
    } 


}
