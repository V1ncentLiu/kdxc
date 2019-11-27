package com.kuaidao.manageweb.controller.paydetail;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kuaidao.aggregation.dto.paydetail.PayChangeRecordDTO;
import com.kuaidao.aggregation.dto.paydetail.PayChangeRecordParamDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.paydetail.PayChangeRecordFeignClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 修改付款明细操作记录 Created on 2019-11-25 16:45:56
 */
@Slf4j
@Controller
@RequestMapping("/payChangRecord")
public class PayChangeRecordController {

    @Autowired
    private PayChangeRecordFeignClient payChangeRecordFeignClient;


    /**
     * 付款明细操作记录列表初始化
     *
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2019/11/26 15:37
     * @since: 1.0.0
     **/
    @RequestMapping("/pageInit")
    @RequiresPermissions("paydetail:payChangRecord:view")
    public String balanceAccountPage(HttpServletRequest request) {
        return "business/busChangePaymentRecord";
    }

    /**
     * 付款明细操作记录列表
     * 
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2019/11/26 15:37
     * @since: 1.0.0
     **/
    @PostMapping("/getPageList")
    public JSONResult<PageBean<PayChangeRecordDTO>> getPageList(@RequestBody PayChangeRecordParamDTO payChangRecordParamDTO) {
        JSONResult<PageBean<PayChangeRecordDTO>> jsonResult = payChangeRecordFeignClient.getPageList(payChangRecordParamDTO);
        return jsonResult;
    }

    /**
     * 付款明细操作记录导出
     *
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2019/11/26 15:37
     * @since: 1.0.0
     **/
    @RequestMapping("/exportPayChangRecord")
    @RequiresPermissions("paydetail:payChangRecord:export")
    public void exportPayChangRecord(@RequestBody PayChangeRecordParamDTO payChangRecordParamDTO, HttpServletResponse response) throws Exception {
        JSONResult<List<PayChangeRecordDTO>> listNoPage = payChangeRecordFeignClient.getPayChangRecordList(payChangRecordParamDTO);
        List<List<Object>> dataList = new ArrayList<>();
        if (JSONResult.SUCCESS.equals(listNoPage.getCode()) && CollectionUtils.isNotEmpty(listNoPage.getData())) {
            List<PayChangeRecordDTO> resultList = listNoPage.getData();
            int size = resultList.size();
            for (int i = 0; i < size; i++) {
                PayChangeRecordDTO dto = resultList.get(i);
                List<Object> curList = new ArrayList<>();
                // 序号
                curList.add(i + 1);
                // 变更时间
                curList.add(getTimeStr(dto.getCreateTime()));
                // 签约单编号
                curList.add(dto.getSignNo());
                // 付款类型
                curList.add(dto.getPayTypeName());
                // 结算单编号
                curList.add(dto.getStatementNo());
                // 商务经理
                curList.add(dto.getBusinessManagerName());
                // 商务组
                curList.add(dto.getBusinessGroupName());
                // 变更人
                curList.add(dto.getCreateUserName());
                // 操作类型
                curList.add(dto.getOperationTypeName());
                // 备注信息
                curList.add(dto.getRemark());
                dataList.add(curList);
            }
        } else {
            log.error("exportPayChangRecord rule_report res{{}}", listNoPage);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "更改付款记录" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }

    /**
     * 设置导出表头
     * 
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2019/11/27 10:16
     * @since: 1.0.0
     **/
    private List<Object> getPayChangRecordHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("变更时间");
        headTitleList.add("签约单编号");
        headTitleList.add("付款类型");
        headTitleList.add("结算单编号");
        headTitleList.add("商务经理");
        headTitleList.add("商务组");
        headTitleList.add("变更人");
        headTitleList.add("操作类型");
        headTitleList.add("备注信息");
        return headTitleList;
    }

    /**
     * 转换时间
     *
     * @author: Fanjd
     * @param date 日期
     * @return: java.lang.String
     * @Date: 2019/9/11 10:57
     * @since: 1.0.0
     **/
    private String getTimeStr(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.convert2String(date, DateUtil.ymdhms);
    }
}
