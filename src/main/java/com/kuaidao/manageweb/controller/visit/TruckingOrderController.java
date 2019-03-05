package com.kuaidao.manageweb.controller.visit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.visit.TrackingOrderReqDTO;
import com.kuaidao.aggregation.dto.visit.TrackingOrderRespDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.feign.visit.TrackingOrderFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * 邀約來訪派車單
 * @author  Chen
 * @date 2019年3月5日 上午9:09:07
 * @version V1.0
 */
@Controller
@RequestMapping("/truckingOrder")
public class TruckingOrderController {
    
    private static Logger logger = LoggerFactory.getLogger(TruckingOrderController.class);

    @Autowired
    TrackingOrderFeignClient trackingOrderFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    /**
     * 邀約來訪派車單
     * @return
     */
    @RequestMapping("/truckingOrderPage")
    public String visitRecordPage(HttpServletRequest request) {
        //电销人员
        List<UserInfoDTO> teleSaleList = getUserInfo(null, RoleCodeEnum.DXCYGW.name());
        request.setAttribute("teleSaleList",teleSaleList);
        return "visit/truckingOrder";
    }

    private List<UserInfoDTO> getUserInfo(Long orgId,String roleName){
        UserOrgRoleReq req = new UserOrgRoleReq();
        if(orgId!=null) {
            req.setOrgId(orgId);
        }
        req.setRoleCode(roleName);
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        if(userJr==null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error("查询电销通话记录-获取组内顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",req,userJr);
            return null;
        }
        return userJr.getData();
    }

    /**
     * 查询邀约来访派车单
     * @param reqDTO
     * @return
     */
    @PostMapping("/listTrackingOrder")
    @ResponseBody
     public JSONResult<PageBean<TrackingOrderRespDTO>> listTrackingOrder(@RequestBody TrackingOrderReqDTO reqDTO){
        //TODO devin  调试 暂时放开
   /*     UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if(roleList!=null && roleList.size()!=0) {
            RoleInfoDTO roleInfoDTO = roleList.get(0);
            String roleName = roleInfoDTO.getRoleName();
            if(RoleCodeEnum.SWDQZJ.value().equals(roleName)) {
                UserOrgRoleReq req = new UserOrgRoleReq();
                req.setOrgId(orgId);
                req.setRoleCode(RoleCodeEnum.SWZJ.name());
                JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
                if(userJr==null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
                    logger.error("查询电销通话记录-获取组内顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",req,userJr);
                }
                List<UserInfoDTO> userInfoDTOList = userJr.getData();
                if(userInfoDTOList!=null && userInfoDTOList.size()!=0) {
                    List<Long> idList = userInfoDTOList.stream().map(UserInfoDTO::getId).collect(Collectors.toList());
                    reqDTO.setBusDirectorIdList(idList);
                }

            }else if(RoleCodeEnum.SWZJ.value().equals(roleName)){//商务总监
                List<Long> idList = new ArrayList<>();
                idList.add(curLoginUser.getId());
                reqDTO.setBusDirectorIdList(idList);
            }
        }*/

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
