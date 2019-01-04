/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools.GridBagTable;

import java.awt.Component;   
import java.awt.Point;   
import java.awt.Rectangle;   
import java.util.Enumeration;   
import java.util.EventObject;   
  
import javax.swing.DefaultCellEditor;   
import javax.swing.JTable;   
import javax.swing.SwingUtilities;   
import javax.swing.event.TableModelEvent;   
import javax.swing.table.AbstractTableModel;   
import javax.swing.table.TableColumn;   
import javax.swing.table.TableColumnModel;   
  
/**  
 * @author 15860102@qq.com  
 */  
public class GridBagTable extends JTable{   
  
    GridBagModel gridBagModel;   

    
       
    public GridBagModel getGridBagModel() {   
        return gridBagModel;   
    }   
       
    public void setGridBagModel(GridBagModel gridBagModel){   
        if( gridBagModel != null && gridBagModel != this.gridBagModel )   
            this.gridBagModel = gridBagModel;   
    }   
    
    public GridBagTable(AbstractTableModel dm){   
        super(dm);         
        getTableHeader().setReorderingAllowed(false);   
        gridBagModel = new DefaultGridBagTableModel(dm);           
        getColumnModel().setColumnSelectionAllowed(true);   
    }   
       
     private void updateSubComponentUI(Object componentShell) {   
        if (componentShell == null) {   
            return;   
        }   
        Component component = null;   
        if (componentShell instanceof Component) {   
            component = (Component)componentShell;   
        }   
        if (componentShell instanceof DefaultCellEditor) {   
            component = ((DefaultCellEditor)componentShell).getComponent();   
        }   
  
        if (component != null) {   
            SwingUtilities.updateComponentTreeUI(component);   
        }   
    }   
       
    public void updateUI() {       
        // Update the UIs of the cell renderers, cell editors and header renderers.   
        TableColumnModel cm = getColumnModel();   
        for(int column = 0; column < cm.getColumnCount(); column++) {   
            TableColumn aColumn = cm.getColumn(column);   
        updateSubComponentUI(aColumn.getCellRenderer());   
            updateSubComponentUI(aColumn.getCellEditor());   
        updateSubComponentUI(aColumn.getHeaderRenderer());   
        }   
  
        // Update the UIs of all the default renderers.   
        Enumeration defaultRenderers = defaultRenderersByColumnClass.elements();   
        while (defaultRenderers.hasMoreElements()) {   
            updateSubComponentUI(defaultRenderers.nextElement());   
        }   
  
        // Update the UIs of all the default editors.   
        Enumeration defaultEditors = defaultEditorsByColumnClass.elements();   
        while (defaultEditors.hasMoreElements()) {   
            updateSubComponentUI(defaultEditors.nextElement());   
        }   
  
        // Update the UI of the table header   
        if (tableHeader != null && tableHeader.getParent() == null) {   
            tableHeader.updateUI();   
        }   
        setUI(new GridBagTableUI());   
    }   
       
    public Rectangle getGridCellRect(int row, int column, boolean includeSpacing){   
        return super.getCellRect(row, column, includeSpacing);   
    }   
       
    public Rectangle getCellRect(int row, int column, boolean includeSpacing) {        
        Rectangle cellRect = super.getCellRect(row, column, includeSpacing);   
        int cols = gridBagModel.getColumnGrid(row, column);   
        TableColumnModel cm = getColumnModel();   
        for( int n=1; n<cols; n++)   
            cellRect.width += cm.getColumn(column+n).getWidth();   
        int rows = gridBagModel.getRowGrid(row, column);   
        for( int n=1; n<rows; n++)   
            cellRect.height += getRowHeight(row+n);   
        return cellRect;            
    }   
       
    public void tableChanged(TableModelEvent e){   
        super.tableChanged(e);   
        //TODO   
    }   
       
    public boolean mergeCells(int startRow, int endRow, int startColumn, int endColumn){   
        if( gridBagModel.mergeCells(startRow, endRow, startColumn, endColumn)){   
            repaint();   
            return true;   
        }      
        return false;   
    }  
    
    public boolean canMergeCells(int[] rows, int[] columns) {
        if( gridBagModel.canMergeCells(rows, columns)){   
            repaint();   
            return true;   
        }      
        return false;  
    }
    
    public boolean mergeCells(int[] rows, int[] columns){   
        if( gridBagModel.mergeCells(rows, columns)){   
            repaint();   
            return true;   
        }      
        return false;   
    }   
       
    public boolean spliteCellAt(int row, int column){   
        if( gridBagModel.spliteCellAt(row, column)){   
            repaint();   
            return true;   
        }   
        return false;   
    }   
    
    public int getColumnGrid(int row, int column){
        return gridBagModel.getColumnGrid(row, column);
    }
      
    public int getRowGrid(int row, int column){
        return gridBagModel.getRowGrid(row, column);
    }
    
    public int getCellState(int row, int column){
        return gridBagModel.getCellState(row, column);
    }
    
    public void clearMergence(){  
        gridBagModel.clearMergence();
        repaint();   
    }
       
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {   
        if( gridBagModel.getCellState( rowIndex , columnIndex ) != GridBagModel.COVERED  )   
            super.changeSelection(rowIndex, columnIndex, toggle, extend);   
        Point p;   
        for( int row = rowIndex; row >= 0; row-- ){   
            for( int col = columnIndex; col >= 0; col-- ){   
                p = gridBagModel.getGrid(row, col);   
                //p = ((Point)((Vector)rowVector.get(row)).get(col));   
                if( col + p.x > columnIndex && row + p.y > rowIndex){   
                    rowIndex = row;   
                    columnIndex = col;   
                    break;   
                }   
            }   
        }      
        super.changeSelection(rowIndex, columnIndex, toggle, extend);   
        repaint();   
    }   
       
    public boolean editCellAt(int rowIndex, int columnIndex, EventObject e){   
        if( gridBagModel.getCellState( rowIndex , columnIndex ) != GridBagModel.COVERED  )   
            return super.editCellAt(rowIndex, columnIndex, e);      
        Point p;   
        for( int row = rowIndex; row >= 0; row-- ){   
            for( int col = columnIndex; col >= 0; col-- ){   
                p = gridBagModel.getGrid(row, col);   
                if( col + p.x > columnIndex && row + p.y > rowIndex){   
                    rowIndex = row;   
                    columnIndex = col;   
                    break;   
                }   
            }   
        }          
        return super.editCellAt(rowIndex, columnIndex, e);           
    }   
    
//    public int getHeight() { return super.getHeight(); }
}  