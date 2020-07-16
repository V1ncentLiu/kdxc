package com.kuaidao.manageweb.controller.financing;

import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.util.StyleUtil;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

/**
 * ┏┓ ┏┓+ +
 * ┏┛┻━━━┛┻┓ + +
 * ┃ ┃
 * ┃ ━ ┃ ++ + + +
 * ████━████ ┃+
 * ┃ ┃ +
 * ┃ ┻ ┃
 * ┃ ┃ + +
 * ┗━┓ ┏━┛
 * ┃ ┃
 * ┃ ┃ + + + +
 * ┃ ┃
 * ┃ ┃ + 神兽保佑
 * ┃ ┃ 代码无bug
 * ┃ ┃ +
 * ┃ ┗━━━┓ + +
 * ┃ ┣┓
 * ┃ ┏┛
 * ┗┓┓┏━┳┓┏┛ + + + +
 * ┃┫┫ ┃┫┫
 * ┗┻┛ ┗┻┛+ + + +
 * @description: 表头处理样式
 * @author: fanjd
 * @create: 2020-07-13 14:11
 */
public class TitleColorSheetWriteHandler implements CellWriteHandler {

    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer integer,
            Integer integer1, Boolean aBoolean) {

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer integer,
            Boolean aBoolean) {

    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<CellData> list, Cell cell, Head head,
            Integer integer, Boolean aBoolean) {
        // 只处理第一行
        if (0 == cell.getRowIndex()) {
            // 设置列宽
            Sheet sheet = writeSheetHolder.getSheet();
            sheet.setColumnWidth(cell.getColumnIndex(), 16 * 256);
            // 设置行高
            writeSheetHolder.getSheet().getRow(0).setHeight((short) (1.8 * 256));
            // 获取workbook
            Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
            // 获取样式实例
            WriteCellStyle headWriteCellStyle = new WriteCellStyle();
            // 获取字体实例
            WriteFont headWriteFont = new WriteFont();
            // 设置字体样式
            headWriteFont.setFontName("宋体");
            // 设置字体大小
            headWriteFont.setFontHeightInPoints((short) 14);
            // 边框
            headWriteFont.setBold(true);
            headWriteCellStyle.setWriteFont(headWriteFont);
            // 设置背景颜色为淡蓝色
            headWriteCellStyle.setFillBackgroundColor(IndexedColors.PALE_BLUE.getIndex());
            headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            // 设置指定单元格字体自定义颜色
            headWriteFont.setColor(IndexedColors.BLACK.getIndex());
            // 获取样式实例
            CellStyle cellStyle = StyleUtil.buildHeadCellStyle(workbook, headWriteCellStyle);
            // 单元格设置样式
            cell.setCellStyle(cellStyle);
        }
    }

}
