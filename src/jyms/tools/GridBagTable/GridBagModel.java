/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools.GridBagTable;

import java.awt.Point;  
  
public interface GridBagModel {  
    //格子处于正常状态  
    public static final int DEFAULT = 0;  
    //格子合并了其他的格子  
    public static final int MERGE = 1;  
    //格子被其他格子合并  
    public static final int COVERED = -1;  
      
    /** 
     * @param row 行 
     * @param column 列 
     * @return 该单元格在行、列的跨度 
     */  
    Point getGrid(int row, int column);  
      
    /** 
     * 在Y轴方向的跨度 
     * @param row 
     * @param column 
     * @return 
     */  
    int getRowGrid(int row, int column);  
      
    /** 
     * 在X轴方向的跨度 
     * @param row 
     * @param column 
     * @return 
     */  
    int getColumnGrid(int row, int column);  
  
    /** 
     * @param rows 行集合 
     * @param columns 列集合 
     * @return 单元格集合是否可以合并在一起 
     */  
    boolean canMergeCells(int[] rows, int[] columns);  
      
    /** 
     * 判断该单元格状态 
     * @param row 
     * @param column 
     * @return MERGE|DEFAULT|COVERED 
     */  
    int getCellState(int row, int column);  
      
    /** 
     * 将单元格集合合并 
     * @param startRow 开始行 
     * @param endRow 结束行 
     * @param startColumn 开始列 
     * @param endColumn 结束列 
     * @return 是否合并成功 
     */  
    boolean mergeCells(int startRow, int endRow, int startColumn, int endColumn);  
      
    /** 
     * 将单元格集合合并 
     * @param rows 行集合 
     * @param columns 列集合 
     * @return 是否合并成功 
     */  
    boolean mergeCells(int[] rows, int[] columns);  
      
    /** 
     * 拆分单元格 
     * @param row 行 
     * @param column 列 
     * @return 是否拆分成功 
     */  
    boolean spliteCellAt(int row, int column);  
      
    /** 
     * 清除 所有合并 
     */  
    void clearMergence();  
}  