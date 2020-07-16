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
 * @description: 单元格样式设置
 * @author: fanjd
 * @create: 2020-07-13 14:20
 */
public class CellColorSheetWriteHandler implements CellWriteHandler {

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
        // 不处理第一行
        if (0 != cell.getRowIndex()) {
            // 根据单元格获取workbook
            Workbook workbook = cell.getSheet().getWorkbook();
            WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
            // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
            // 字体策略
            // 设置背景颜色白色
            contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            WriteFont contentWriteFont = new WriteFont();
            // 字体大小
            contentWriteFont.setFontHeightInPoints((short) 12);
            contentWriteCellStyle.setWriteFont(contentWriteFont);
            // 设置 自动换行
            contentWriteCellStyle.setWrapped(true);
            // 设置 垂直居中
            contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            CellStyle cellStyle = StyleUtil.buildHeadCellStyle(workbook, contentWriteCellStyle);
            cell.setCellStyle(cellStyle);
        }

    }
}
