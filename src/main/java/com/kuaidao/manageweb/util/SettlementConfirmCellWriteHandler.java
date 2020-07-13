package com.kuaidao.manageweb.util;

import com.alibaba.excel.util.StyleUtil;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.kuaidao.aggregation.constant.AggregationConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

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
 * @description: 结算确认导出行监听
 * @author: fanjd
 * @create: 2020-07-11 19:47
 */
@Slf4j
public class SettlementConfirmCellWriteHandler implements RowWriteHandler {
    /**
     * 列号
     */
    private int count = 0;
    /**
     * 一定将样式设置成全局变量
     * 首行只需要创建一次样式就可以 不然每行都创建一次 数据量大的话会保错
     * 异常信息：The maximum number of Cell Styles was exceeded. You can define up to 64000 style in a .xlsx
     * Workbook
     */
    private CellStyle firstCellStyle;

    @Override
    public void beforeRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Integer integer, Integer integer1,
            Boolean aBoolean) {

    }

    @Override
    public void afterRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer integer, Boolean aBoolean) {
       /* if (log.isDebugEnabled()) {
            log.debug("第{}行创建完毕！", row.getRowNum());
        }
        Cell cell = row.createCell(0);
        if (firstCellStyle == null) {
            Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
            firstCellStyle = CellStyleUtil .firstCellStyle(workbook);
            log.info("设置首列样式成功");
        }
        cell.setCellStyle(firstCellStyle);
        // 设置列宽 0列 10个字符宽度
        writeSheetHolder.getSheet().setColumnWidth(0, 10 * 256);
        if (row.getRowNum() == 0) {
            cell.setCellValue("序号");
            return;
        }
        cell.setCellValue(++count);*/
    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex,
            Boolean isHead) {
        Workbook workbook = writeSheetHolder.getSheet().getWorkbook();

        if (!isHead) {
            if (row.getCell(27).getStringCellValue().equals(AggregationConstant.REFOUND_REBATE_TYPE.DATA_TYPE_REFOUND)) {
                bulidCellStyle(workbook, row, IndexedColors.YELLOW.getIndex());
            }
            if (row.getCell(27).getStringCellValue().equals(AggregationConstant.REFOUND_REBATE_TYPE.DATA_TYPE_REBATE)) {
                bulidCellStyle(workbook, row, IndexedColors.ORANGE.getIndex());
            }
        }
    }

    public void bulidCellStyle(Workbook workbook, Row row, short color) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillBackgroundColor(color);
        cellStyle.setFillForegroundColor(color);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
       /* // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        // 字体策略
        // 设置背景颜色白色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 12);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        contentWriteFont.setColor(color);
        // 设置 自动换行
        contentWriteCellStyle.setWrapped(true);
        // 设置 垂直居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle style = StyleUtil.buildHeadCellStyle(workbook, contentWriteCellStyle);*/
        for (int i = 0; i < 28; i++) {
            Cell cell = row.getCell(i);
           // cell.setCellStyle(style);
            cell.setCellStyle(cellStyle);
        }
    }
}
