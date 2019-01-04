/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools.GridBagTable;

import java.awt.Point;  
import java.util.Arrays;  
import java.util.List;  
import java.util.Vector;  
  
import javax.swing.event.TableModelEvent;  
import javax.swing.event.TableModelListener;  
import javax.swing.table.AbstractTableModel;  
  
public class DefaultGridBagTableModel implements GridBagModel, TableModelListener{  
    protected AbstractTableModel model;  
    protected List<List<Point>> gridInfo;  
      
    DefaultGridBagTableModel(AbstractTableModel model){  
        gridInfo = new Vector<List<Point>>();  
        setTableModel(model);  
    }  
      
    public void setTableModel(AbstractTableModel model){  
        if( model != null && model != this.model ){  
            if( this.model != null )  
                this.model.removeTableModelListener(this);  
            //防止多次添加监听器  
            model.removeTableModelListener(this);  
            model.addTableModelListener(this);  
            this.model = model;  
            clearMergence();  
        }  
    }  
      
    public void clearMergence(){  
        if( gridInfo == null  )  
            gridInfo = new Vector<List<Point>>();  
        else  
            gridInfo.clear();  
          
        if( model == null )  
            return;  
          
        //初始化，每个格子占的格子数为(1,1);  
        for(int row=model.getRowCount(); --row>=0;){  
            List<Point> infos = new Vector<Point>();  
            gridInfo.add(infos);  
            for(int col=model.getColumnCount(); --col>=0;){  
                infos.add(getDefaultPoint());  
            }  
        }  
    }  
      
    public Point getDefaultPoint(){  
        return new Point(1,1);  
    }  
      
    @Override  
    public boolean canMergeCells(int[] rows, int[] columns) {  
        if( rows == null || columns == null ) return false;  
        Arrays.sort(rows);  
        for(int index=0; index<rows.length-1; index++){  
            if( rows[index+1] - rows[index] > 1 )  
                return false;  
        }  
        Arrays.sort(columns);  
        for(int index=0; index<columns.length-1; index++){  
            if( columns[index+1] - columns[index] > 1 )  
                return false;  
        }  
        if (!isRectangleOfAllCells(rows, columns)) return false;
        return true;  
    }  
    /**
        *函数:      isRectangleOfAllCells
        *函数描述:  所有选中的单元格是否为矩形
        * @param  ViewName 自定义视图的名称
     */
    private boolean isRectangleOfAllCells(int[] rows, int[] columns){
        int RowCount = rows.length;
        int ColCount = columns.length;
        for (int i=0;i<RowCount;i++){
            for (int j=0;j<ColCount;j++){
                if ((getRowGrid(rows[i], columns[j]) > RowCount) || (getColumnGrid(rows[i], columns[j]) > ColCount)) 
                    return false;
            }
        }
        return true;
    }
      
    @Override  
    public int getCellState(int row, int column) {  
        Point grid = getGrid(row, column);  
        if( grid == null ) return DEFAULT;  
        if( grid.x>1 || grid.y>1 )  
            return MERGE;  
        if( grid.x<=0 || grid.y<=0 )  
            return COVERED;  
        return DEFAULT;  
    }  
  
    @Override  
    public int getColumnGrid(int row, int column) {  
        if( gridInfo != null && row >=0 && row < gridInfo.size() ){  
            List<Point> gridRow = gridInfo.get(row);  
            if( gridRow != null && column >=0 && column < gridRow.size() ){  
                Point point = gridRow.get(column);  
                if( point != null )  
                    return point.x;  
            }     
        }  
        return 1;  
    }  
  
    @Override  
    public Point getGrid(int row, int column) {  
        if( gridInfo != null && row >=0 && row < gridInfo.size() ){  
            List<Point> gridRow = gridInfo.get(row);  
            if( gridRow != null && column >=0 && column < gridRow.size() ){  
                return gridRow.get(column);  
            }     
        }  
        return getDefaultPoint();  
    }  
  
    @Override  
    public int getRowGrid(int row, int column) {  
        if( gridInfo != null && row >=0 && row < gridInfo.size() ){  
            List<Point> gridRow = gridInfo.get(row);  
            if( gridRow != null && column >=0 && column < gridRow.size() ){  
                Point point = gridRow.get(column);  
                if( point != null )  
                    return point.y;  
            }     
        }  
        return 1;  
    }  
  
    protected boolean setGrid(int row, int column, Point grid) {  
        if( gridInfo != null && row >=0 && row < gridInfo.size() ){  
            List<Point> gridRow = gridInfo.get(row);  
            if( gridRow != null && column >=0 && column < gridRow.size() ){  
                Point point = gridRow.get(column);  
                if( point != null ){  
                    point.setLocation(grid);  
                }  
                else{  
                    gridRow.set(column, grid.getLocation());  
                }  
                return true;  
            }     
        }  
        return false;  
    }  
  
    @Override  
    public boolean spliteCellAt(int row, int column) {  
        if( gridInfo != null && row >=0 && row < gridInfo.size() ){  
            List<Point> gridRow = gridInfo.get(row);  
            if( gridRow != null && column >=0 && column < gridRow.size() ){  
                Point point = gridRow.get(column);  
                if( point != null ){  
                    point = point.getLocation();  
                    for(int a=0; a<point.y; a++){  
                        for(int b=0; b<point.x; b++){  
                            setGrid(row+a, column+b, getDefaultPoint());  
                        }  
                    }  
                }  
                else{  
                    gridRow.set(column, getDefaultPoint());  
                }  
                return true;  
            }     
        }  
        return false;  
    }  
      
    @Override  
    /** 
     * table中发生行的添加和删除的时候需要修改该模型 
     */  
    public void tableChanged(TableModelEvent e) {  
        //TODO  
    }  
      
    @Override  
    public boolean mergeCells(int[] rows, int[] columns) {  
        if( !canMergeCells(rows, columns) )  
            return false;  
        Arrays.sort(rows);  
        Arrays.sort(columns);  
        return mergeCells(rows[0],rows[rows.length-1],columns[0],columns[columns.length-1]);  
    }  
  
    @Override  
    public boolean mergeCells(int startRow, int endRow, int startColumn, int endColumn) {  
        setGrid(startRow, startColumn, new Point(endColumn-startColumn+1, endRow-startRow+1));   
        for(int row=startRow; row<=endRow; row++){  
            for(int col=startColumn; col<=endColumn; col++){  
                if(row==startRow&&col==startColumn)  
                    continue;  
                else  
                    setGrid(row, col, new Point(COVERED,COVERED));   
            }  
        }  
        return true;  
    }  
      
    public String toString(){  
        if( gridInfo == null )  
            return "";  
        StringBuffer sb = new StringBuffer();  
        for(List<Point> rowInfo : gridInfo ){  
            for(Point grid : rowInfo){  
                sb.append("["+grid.x+","+grid.y+"], ");  
            }  
            sb.append("\n");  
        }  
        return sb.toString();  
    }  
}  