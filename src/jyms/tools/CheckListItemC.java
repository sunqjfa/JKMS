/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import jyms.data.TxtLogger;

public class CheckListItemC {
    private static String sFileName = "--->>CheckListItemC.java";
/**
 *
 * @author John
 */
/******************************************************************************
     *类:   CheckListItem
     *
     ******************************************************************************/
    public static class CheckListItem
    {
        boolean check;
        String text;
        private boolean enable;
        
        public CheckListItem(boolean check, String text)
        {
            this.check = check;
            this.text = text;
        }

        public boolean getCheck()
        {
            return check;
        }

        public void setCheck(boolean _check)
        {
            check = _check;
        }
        public void setSelfCheck(boolean _check)
        {
            if (enable) 
            check = _check;
        }

        public String getText()
        {
            return text;
        }

        public void setText(String _text)
        {
            text = _text;
        }

        /**
         * @return the enable
         */
        public boolean isEnable() {
            return enable;
        }

        /**
         * @param enable the enable to set
         */
        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }
    /******************************************************************************
     *类:   CheckListMouseListener
     *
     ******************************************************************************/
    public static class CheckListMouseListener extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent e)
        {
            try{
                JList list = (JList) e.getSource();
                int index = list.locationToIndex(e.getPoint());
                CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
                item.setSelfCheck(!item.getCheck());
                Rectangle rect = list.getCellBounds(index, index);
                list.repaint(rect);
            }catch (Exception ex){
                TxtLogger.append(sFileName, "CheckListMouseListener.mousePressed()","系统在点击列表过程中，出现错误"
                                                 + "\r\n                       Exception:" + ex.toString());
            }
        }
    }

/******************************************************************************
     *类:   CheckListItemRenderer
     *JCheckBox   ListCellRenderer
     ******************************************************************************/
    public static class CheckListItemRenderer extends JCheckBox implements ListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus)
        {
            CheckListItem item = (CheckListItem) value;
            this.setSelected(item.getCheck());
            this.setText(item.getText());
            this.setFont(list.getFont());
            this.setEnabled(list.isEnabled());
            item.setEnable(list.isEnabled());
            return this;
        }
    }
}
