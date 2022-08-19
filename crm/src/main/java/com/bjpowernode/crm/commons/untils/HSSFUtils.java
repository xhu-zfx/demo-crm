package com.bjpowernode.crm.commons.untils;

import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * excel工具类
 */
public class HSSFUtils {
    public static String getCellValueForStr(HSSFCell cell){
        String result="";
        if (cell.getCellType()== HSSFCell.CELL_TYPE_STRING) {
            result = cell.getStringCellValue();
        } else if (cell.getCellType()== HSSFCell.CELL_TYPE_NUMERIC) {
            result = cell.getNumericCellValue() + "";
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
            result=cell.getBooleanCellValue()+"";
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
            result=cell.getCellFormula();
        } else {
            result="";
        }
        return result;
    }
}
