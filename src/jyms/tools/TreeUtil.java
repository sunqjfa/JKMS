/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import jyms.CommonParas;

/**
 *
 * @author John
 */
public class TreeUtil {
    
    /**
        * 函数:      modifyTreeCellRendere
        * 函数描述:  设置JTree控件黑白分明的外观（主要是JTree是黑色背景的情况下）
        * @param MyTree
    */
    public static void modifyTreeCellRenderer(JTree MyTree){
        //实例化一个DefaultTreeCellRenderer对象
        DefaultTreeCellRenderer cellRender = (DefaultTreeCellRenderer) MyTree.getCellRenderer();

        ////设置处于折叠状态下非叶子节点的图标
//        cellRender.setClosedIcon(ImageIconBufferPool.getInstance().getImageIcon("treeclosedicon.png"));
        ////设置叶子节点的图标
//        cellRender.setLeafIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon.png"));
        ////设置处于展开状态下非叶子节点的图标
//        cellRender.setOpenIcon(ImageIconBufferPool.getInstance().getImageIcon("treeopenicon.png"));

        //设置非选定节点的背景色
        cellRender.setBackgroundNonSelectionColor(new java.awt.Color(64, 64, 64));
        //设置节点在选中状态下的背景色
//        cellRender.setBackgroundSelectionColor(Color.lightGray);
        
        //设置节点边框的颜色
//        cellRender.setBorderSelectionColor(Color.lightGray);

        //设置绘制选中状态下节点文本的颜色
        cellRender.setTextSelectionColor(Color.WHITE);
        //设置绘制非选中状态下节点文本的颜色
        cellRender.setTextNonSelectionColor(Color.WHITE);
        cellRender.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        MyTree.setCellRenderer(cellRender);
    }
     
    /**
        *内部类:   BlackNodeTreeCellRenderer
        *类描述:   自己定制的JTree控件黑白分明的外观（主要是JTree是黑色背景的情况下）
        *用法：    jTreeGroupResource.setCellRenderer(new BlackNodeTreeCellRenderer());
    */  
    public static class BlackNodeTreeCellRenderer extends DefaultTreeCellRenderer{
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,  
                    boolean selected, boolean expanded, boolean leaf, int row,  
                    boolean hasFocus)  
        {  
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            ////设置处于折叠状态下非叶子节点的图标
            //this.setClosedIcon(ImageIconBufferPool.getInstance().getImageIcon("treeclosedicon.png"));
            ////设置叶子节点的图标
            //this.setLeafIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon.png"));
            ////设置处于展开状态下非叶子节点的图标
            //this.setOpenIcon(ImageIconBufferPool.getInstance().getImageIcon("treeopenicon.png"));

            //设置非选定节点的背景色
            this.setBackgroundNonSelectionColor(new java.awt.Color(64, 64, 64));
            //设置节点在选中状态下的背景色
//            this.setBackgroundSelectionColor(Color.lightGray);

            //设置节点边框的颜色
//            this.setBorderSelectionColor(Color.lightGray);

            //设置绘制选中状态下节点文本的颜色
            this.setTextSelectionColor(Color.WHITE);
            //设置绘制非选中状态下节点文本的颜色
            this.setTextNonSelectionColor(Color.WHITE);
            this.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
            
            //设定叶子节点的图标
            DefaultMutableTreeNode   node=(DefaultMutableTreeNode)value;
            
            String value1=value.toString();
            if (!node.isLeaf()) return this;
            if(value1.contains(CommonParas.DVRResourceType.NODETYPE_CHANNEL) || value1.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_NAME)){//"监控点""通道"
                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_monitorpoint.png"));
            }else if(value1.contains(CommonParas.DVRResourceType.NODETYPE_ALARMIN) || value1.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_NAME)){//"报警输入"
                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_alarmin.png"));
            }else if(value1.contains(CommonParas.DVRResourceType.NODETYPE_ALARMOUT) || value1.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_NAME)){//"报警输出"
                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_alarmout.png"));
            }else if(value1.contains(CommonParas.DVRResourceType.NODETYPE_DISK) || value1.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_DISK_NAME)){//"硬盘"
                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_disk.png"));
            }else{//设备节点
                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_device.png"));
            }
            return this;
        }
    }
    /**
        *内部类:   CustomNodeTreeCellRendere
        *类描述:   自己定制的JTree控件的外观（主要设置JTree控件的图标）
        *用法：    jTreeGroupResource.setCellRenderer(new CustomNodeTreeCellRendere());
    */  
    public static class CustomNodeTreeCellRenderer extends DefaultTreeCellRenderer{
 
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,  
                    boolean selected, boolean expanded, boolean leaf, int row,  
                    boolean hasFocus)  
        {  
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode   node=(DefaultMutableTreeNode)value;
            
            String value1=value.toString();
            
            if (!node.isLeaf()) return this;
            
            if(value1.contains(CommonParas.DVRResourceType.NODETYPE_CHANNEL) || value1.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_CHANNEL_NAME)){//"监控点""通道"
                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_monitorpoint.png"));
            }else if(value1.contains(CommonParas.DVRResourceType.NODETYPE_ALARMIN) || value1.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMIN_NAME)){//"报警输入"
                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_alarmin.png"));
            }else if(value1.contains(CommonParas.DVRResourceType.NODETYPE_ALARMOUT) || value1.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_ALARMOUT_NAME)){//"报警输出"
                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_alarmout.png"));
            }else if(value1.contains(CommonParas.DVRResourceType.NODETYPE_DISK) || value1.contains(CommonParas.DVRResourceType.RESTYPE_ENCODINGDVR_DISK_NAME)){//"硬盘"
                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_disk.png"));
            }else{//设备节点
                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_device.png"));
            }
//            if(value1.contains("监控点")){
//                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_monitorpoint.png"));
//            }else if(value1.contains("报警输入")){
//                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_alarmin.png"));
//            }else if(value1.contains("报警输出")){
//                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_alarmout.png"));
//            }else if(value1.contains("硬盘")){
//                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_disk.png"));
//            }else if(value1.contains("通道")){
//                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_monitorpoint.png"));
//            }else{//设备节点
//                setIcon(ImageIconBufferPool.getInstance().getImageIcon("treeleaficon_device.png"));
//            }
            return this; 
        }
    }
    
    
}
