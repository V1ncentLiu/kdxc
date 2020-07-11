package com.kuaidao.manageweb.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomCellWriteHandler implements RowWriteHandler {
    @Override
    public void beforeRowCreate(WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder, Integer rowIndex, Integer relativeRowIndex,
            Boolean isHead) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
            Row row, Integer relativeRowIndex, Boolean isHead) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();
        if (!isHead && row.getCell(3).getStringCellValue() != null) {

            cellStyle.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
            cellStyle.setFillForegroundColor((short) row.getRowNum());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            for (int i = 0; i < 30; i++) {
                Cell cell = row.getCell(i);
                cell.setCellStyle(cellStyle);
            }
        }
    }


}
