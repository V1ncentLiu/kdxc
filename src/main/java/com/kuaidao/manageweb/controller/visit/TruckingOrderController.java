package com.kuaidao.manageweb.controller.visit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.visit.TrackingOrderReqDTO;
import com.kuaidao.aggregation.dto.visit.TrackingOrderRespDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.visit.TrackingOrderFeignClient;

@Controller
@RequestMapping("/truckingOrder")
public class TruckingOrderController {
    
    @Autowired
    TrackingOrderFeignClient trackingOrderFeignClient;
    /**
     * 查询邀约来访派车单
     * @param reqDTO
     * @return
     */
    @PostMapping("/listTrackingOrder")
    @ResponseBody
     public JSONResult<PageBean<TrackingOrderRespDTO>> listTrackingOrder(@RequestBody TrackingOrderReqDTO reqDTO){
         return trackingOrderFeignClient.listTrackingOrder(reqDTO) ;
     }
    
    

    
    
    /**
     * 导出 
     * @param reqDTO
     * @return
     */
    @PostMapping("/exportTrackingOrder")
     public void exportTrackingOrder(@RequestBody TrackingOrderReqDTO reqDTO,HttpServletResponse response) throws Exception{
        JSONResult<List<TrackingOrderRespDTO>> trackingOrderListJr = trackingOrderFeignClient.exportTrackingOrder(reqDTO);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());
        
       if(JSONResult.SUCCESS.equals(trackingOrderListJr.getCode()) && trackingOrderListJr.getData()!=null&& trackingOrderListJr.getData().size()!=0) {
            List<TrackingOrderRespDTO> orderList = trackingOrderListJr.getData();
            int size = orderList.size();
            for (int i = 0; i < size; i++) {
                TrackingOrderRespDTO trackingOrderRespDTO = orderList.get(0);
                List<Object> curList = new ArrayList<>();
                curList.add(i+1);
                curList.add(getTimeStr(trackingOrderRespDTO.getReserveTime()));
                curList.add(trackingOrderRespDTO.getBusDirectorName());
                curList.add(trackingOrderRespDTO.getBusCompanyName());
                curList.add(trackingOrderRespDTO.getTasteProject());
                curList.add(getTimeStr(trackingOrderRespDTO.getCreateTime()));
                curList.add(trackingOrderRespDTO.getSubmitGroup());
                curList.add(trackingOrderRespDTO.getAccountPhone());
                curList.add(trackingOrderRespDTO.getCusName());
                curList.add(trackingOrderRespDTO.getCusNum());
                curList.add(trackingOrderRespDTO.getCusPhone());
                curList.add(trackingOrderRespDTO.getCusName());
                curList.add(getTimeStr(trackingOrderRespDTO.getArrivalTime()));
                curList.add(getTimeStr(trackingOrderRespDTO.getDepartTime()));
                curList.add(trackingOrderRespDTO.getCity());
                curList.add(getTimeStr(trackingOrderRespDTO.getPickUpTime()));
                curList.add(trackingOrderRespDTO.getDelayMark());
                curList.add(trackingOrderRespDTO.getFlight());
                curList.add(trackingOrderRespDTO.getPickUpPlace());
                curList.add(trackingOrderRespDTO.getTerminal());
                curList.add(trackingOrderRespDTO.getReceivedPlace());
                dataList.add(curList);
            }
            
        }
        
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        
        
        String name =  "派车单"+ DateUtil.convert2String(new Date(),DateUtil.ymdhms2)+".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
        
     }
    
    private String getTimeStr(Date date) {
        if(date==null) {
           return ""; 
        }
        return DateUtil.convert2String(date,DateUtil.ymdhms);
    }
    
    
    private List<Object> getHeadTitleList(){
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("邀约来访时间");
        headTitleList.add("商务总监");
        headTitleList.add("餐饮公司");
        headTitleList.add("品尝项目");
        headTitleList.add("申请提交时间");
        headTitleList.add("申请部门");
        headTitleList.add("顾问电话");
        headTitleList.add("客户姓名");
        headTitleList.add("客户人数");
        headTitleList.add("客户手机号");
        headTitleList.add("客户联系人姓名");
        headTitleList.add("到站时间");
        headTitleList.add("出站时间");
        headTitleList.add("出车城市");
        headTitleList.add("接站时间");
        headTitleList.add("延误说明");
        headTitleList.add("车次／航班");
        headTitleList.add("接站地");
        headTitleList.add("航站楼");
        headTitleList.add("接到地（酒店／公司）");
        return headTitleList;
    }
    

}
