package jyms.tools;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jyms.data.ClientLogBean;
import jyms.data.TxtLogger;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


/**
 * Excel util, create excel sheet, cell and style.
 * @param <T> generic class.
 */
public class ExcelUtil<T> {
   
    public static HSSFCellStyle getCellStyle(HSSFWorkbook workbook,boolean isHeader){
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setLocked(true);
        if (isHeader) {
            style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            HSSFFont font = workbook.createFont();
            font.setColor(HSSFColor.BLACK.index);
            font.setFontHeightInPoints((short) 12);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
        }        
        return style;
    }
    
   
    public static  void generateHeader(HSSFWorkbook workbook,HSSFSheet sheet,String[] headerColumns){
        HSSFCellStyle style = getCellStyle(workbook,true);
        Row row = sheet.createRow(0);
        row.setHeightInPoints(30);
        for(int i=0;i<headerColumns.length;i++){
            Cell cell = row.createCell(i);
            String[] column = headerColumns[i].split("_#_");
            sheet.setColumnWidth(i, Integer.valueOf(column[1]));
            cell.setCellValue(column[0]);
            cell.setCellStyle(style);
        }
    }    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public HSSFSheet creatAuditSheet(HSSFWorkbook workbook,String sheetName,
            List<T> dataset,String[] headerColumns,String[] fieldColumns) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        HSSFSheet sheet = workbook.createSheet(sheetName);
        sheet.protectSheet("");
        
        generateHeader(workbook,sheet,headerColumns); 
        HSSFCellStyle style = getCellStyle(workbook,false);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        int rowNum = 0;
        for(T t:dataset){
            rowNum++ ;
            Row row = sheet.createRow(rowNum); 
            row.setHeightInPoints(25);
            for(int i = 0; i < fieldColumns.length; i++){               
                String fieldName = fieldColumns[i] ;              
               
                String getMethodName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);                   
                try {                    
                    Class clazz = t.getClass();
                    Method getMethod;
                    getMethod = clazz.getMethod(getMethodName, new Class[]{} );
                    Object value = getMethod.invoke(t, new Object[]{});
                    String cellValue = "";
                    if (value instanceof Date){
                        Date date = (Date)value;
                        cellValue = sd.format(date);
                    }else{ 
                        cellValue = null != value ? value.toString() : "";
                    }                    
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(style);
                    cell.setCellValue(cellValue);
                   
                } catch (Exception e) {
                    
                } 
            }            
        }
        return sheet;        
    }
    
    public static  HSSFSheet creatAuditSheet(HSSFWorkbook workbook,String sheetName,ArrayList listSearchLogs, String[] headerColumns){
        try{
            HSSFSheet sheet = workbook.createSheet(sheetName);
            sheet.protectSheet("");

            generateHeader(workbook,sheet,headerColumns); 
            HSSFCellStyle style = getCellStyle(workbook,false);
            
            int rowNum = 0;
//            获取ClientLogBean（客户端日志表中的操作时间、用户名、日志类型代码、描述信息、设备序列号、节点名、接入设备序列号、注释）-0
//                   对应的“设备别名-1”、“对应的的接入设备的“设备别名-2”、对应的日志类型-3
            //{"操作时间0", "用户名1", "日志类型2", "描述信息3", "设备别名4", "节点名5", "注释6"};
            for (int i=0;i<listSearchLogs.size();i++){
                rowNum++ ;
                Row row = sheet.createRow(rowNum); 
                row.setHeightInPoints(25);

                ArrayList NewList = (ArrayList)listSearchLogs.get(i);
                ClientLogBean clientLogBean = (ClientLogBean)NewList.get(0);
                String AnotherName = (String)NewList.get(1);
                String AnotherNameJoin = (String)NewList.get(2);
                String NodeName = "";//详细的NodeName，不同于clientLogBean.getNodename()
                String cellValue = "";

                Cell cell = row.createCell(0);
                cell.setCellStyle(style);
                //cell.setCellValue(cellValue);
                cell.setCellValue(clientLogBean.getOperationtime());

                Cell cell1 = row.createCell(1);
                cell1.setCellStyle(style);
                //cell.setCellValue(cellValue);
                cell1.setCellValue(clientLogBean.getUsername());

                Cell cell2 = row.createCell(2);
                cell2.setCellStyle(style);
                //cell.setCellValue(cellValue);
                cell2.setCellValue((String)NewList.get(3));//日志类型

            }
            return sheet;        
        }catch(Exception e)
        {
            TxtLogger.append("ExcelUtil.java", "creatAuditSheet2()","系统在导出Excel过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
        return null;
    }
    
    public static void creatLogsToSheet(ArrayList listSearchLogs,String[] headerColumns,String ExcelFileName){
        FileOutputStream fileOut = null;
        try {
            
            HSSFWorkbook workbook = new HSSFWorkbook();
            //ExcelUtil<User> userSheet = new ExcelUtil<User>();
            //creatAuditSheet(workbook, "user sheet xls", listSearchLogs, headerColumns);
            
            HSSFSheet sheet = workbook.createSheet("Client Logs");
            sheet.protectSheet("");

            generateHeader(workbook,sheet,headerColumns); 
            HSSFCellStyle style = getCellStyle(workbook,false);
            
            int rowNum = 0;
            //            获取ClientLogBean（客户端日志表中的操作时间、用户名、日志类型代码、描述信息、设备序列号、节点名、接入设备序列号、注释）-0
//                   对应的“设备别名-1”、“对应的的接入设备的“设备别名-2”、对应的日志类型-3
            //{"操作时间0", "用户名1", "日志类型2", "描述信息3", "设备别名4", "节点名5", "注释6"};
            for (int i=0;i<listSearchLogs.size();i++){
                rowNum++ ;
                Row row = sheet.createRow(rowNum); 
                row.setHeightInPoints(25);

                ArrayList NewList = (ArrayList)listSearchLogs.get(i);
                ClientLogBean clientLogBean = (ClientLogBean)NewList.get(0);
                String AnotherName = (String)NewList.get(1);
                String AnotherNameJoin = (String)NewList.get(2);
                String NodeName = "";//详细的NodeName，不同于clientLogBean.getNodename()
                String cellValue = "";

                Cell cell = row.createCell(0);
                cell.setCellStyle(style);
                cell.setCellValue(clientLogBean.getOperationtime());

                Cell cell1 = row.createCell(1);
                cell1.setCellStyle(style);
                cell1.setCellValue(clientLogBean.getUsername());

                Cell cell2 = row.createCell(2);
                cell2.setCellStyle(style);
                cell2.setCellValue((String)NewList.get(3));//日志类型
                
                //描述信息
                Cell cell3 = row.createCell(3);
                cell3.setCellStyle(style);
                cell3.setCellValue(clientLogBean.getDescription());
                
                //设备别名
                Cell cell4 = row.createCell(4);
                cell4.setCellStyle(style);
                cell4.setCellValue(AnotherName);
                
                //针对设备和节点、接入设备都不为空的情况下
                if ((!AnotherName.equals("")) && (!clientLogBean.getNodename().equals("")) && (!AnotherNameJoin.equals(""))){
                    NodeName = AnotherName + "_" + AnotherNameJoin + "_" + clientLogBean.getNodename();
                }
                //2、针对设备和节点名不为空，而接入设备为空的情况下
                else if ((!AnotherName.equals("")) && (!clientLogBean.getNodename().equals("")) && AnotherNameJoin.equals("")){
                    NodeName = AnotherName + "_" + clientLogBean.getNodename();
                }
                //节点信息
                Cell cell5 = row.createCell(5);
                cell5.setCellStyle(style);
                cell5.setCellValue(NodeName);
                
                //注释
                Cell cell6 = row.createCell(6);
                cell6.setCellStyle(style);
                cell6.setCellValue(clientLogBean.getRemarks());

            }
            
            fileOut = new FileOutputStream(ExcelFileName);
            workbook.write(fileOut); 
            fileOut.close();
        }catch(Exception e)
        {
            TxtLogger.append("ExcelUtil.java", "myExportToExcel()","系统在导出Excel过程中，出现错误" + 
                            "\r\n                       Exception:" + e.toString());   
        }
    }

}
