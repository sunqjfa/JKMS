/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools.GridBagTable;


import java.awt.BorderLayout;   
import java.awt.event.ActionEvent;   
import java.awt.event.ActionListener;   
  
import javax.swing.JButton;   
import javax.swing.JFrame;   
import javax.swing.JPanel;
import javax.swing.JScrollPane;   
import javax.swing.table.DefaultTableModel;   
import jyms.ui.TableUI_White_Line;
  

  
  
public class testGridBagTable implements ActionListener{   
       
    GridBagTable table;   
    public testGridBagTable()   
    {   
        JFrame d = new JFrame();   
        DefaultTableModel model = new DefaultTableModel(5,5);   
           
        table = new GridBagTable(model);   
        table.setRowHeight(20);   
//        table.setUI(null);
           
        JScrollPane pane = new JScrollPane(table);   
        d.getContentPane().add(pane, BorderLayout.CENTER);   
        
        JPanel jPanel1 = new JPanel();
        d.getContentPane().add(jPanel1, BorderLayout.NORTH);  
        
        JButton btn = new JButton("合并"); 
        jPanel1.add(btn);
        //d.getContentPane().add(btn, BorderLayout.NORTH);   
        btn.addActionListener(this);   
        
        JButton btn2 = new JButton("拆分"); 
        jPanel1.add(btn2);
        //d.getContentPane().add(btn, BorderLayout.NORTH);   
        btn2.addActionListener(this);  
        
        d.setBounds(0, 0, 400, 400);   
        d.setVisible(true);   
    }   
       
    public static void main(String[] fsd){   
        new testGridBagTable();   
    }   
       
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton)e.getSource();
        if (btn.getText().equals("合并")){
            table.mergeCells(table.getSelectedRows(), table.getSelectedColumns()); 
            //btn.setText("拆分");
        }else {
            table.spliteCellAt(table.getSelectedRow(), table.getSelectedColumn()); 
            //btn.setText("合并");
        }
        
    }   
}  