package com.kuaidao.manageweb.controller.merchant.resourcetrajectory;

import com.kuaidao.aggregation.dto.clue.ClueBasicDTO;
import com.kuaidao.aggregation.dto.clue.ClueCustomerDTO;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueIntentionDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.ClueReceiveDTO;
import com.kuaidao.aggregation.dto.log.ImLogsDTO;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.controller.merchant.pubcustomer.PubcustomerController;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueCustomerFeignClient;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.log.ImLogMgtFeignClient;
import com.kuaidao.manageweb.feign.merchant.clue.ClueInfoDetailFeignClient;
import com.kuaidao.manageweb.feign.merchant.clue.ClueManagementFeignClient;
import com.kuaidao.merchant.dto.clue.ClueManagementDto;
import com.kuaidao.merchant.dto.clue.ClueManagementParamDto;
import com.kuaidao.merchant.dto.pubcusres.ClueReceiveRecordsDTO;
import com.kuaidao.merchant.dto.resourcetrajectory.ResourceTrajectoryDTO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: admin
 * @Date: 2019/9/27 10:36
 * @Description:
 */
@Controller
@RequestMapping("/merchant/resourceTrajectory")
public class ResourceTrajectoryController {
  private static Logger logger = LoggerFactory.getLogger(ResourceTrajectoryController.class);

  @Autowired
  ImLogMgtFeignClient imLogMgtFeignClient;

  @Autowired
  private MyCustomerFeignClient myCustomerFeignClient;

  @Autowired
  private ClueManagementFeignClient clueManagementFeignClient;


  /**
   * 页面跳转
   */
  @RequestMapping("/topage")
  public String listPage(HttpServletRequest request) {
    request.setAttribute("clueId",request.getParameter("clueId"));
    return "merchant/resourceTrajectory/resourceTrajectory";
  }
  @RequestMapping("/topage1")
  public String listPage1(HttpServletRequest request) {
    return "merchant/resourceTrajectory/resourceTrajectory";
  }
  /**
   * 页面数据接口
   */

  @ResponseBody
  @RequestMapping("/data")
  public JSONResult<ResourceTrajectoryDTO> data(@RequestBody IdEntityLong clueId) {

    ResourceTrajectoryDTO resourceTrajectory = new ResourceTrajectoryDTO();

    // 查询聊天记录：
    ImLogsDTO logReqDTO = new ImLogsDTO();
    logReqDTO.setClueId(clueId.getId());
    JSONResult<List<ImLogsDTO>> imLogs = imLogMgtFeignClient.queryIMLogRecord(logReqDTO);
    if(CommonUtil.resultCheck(imLogs)){
      resourceTrajectory.setChatList(imLogs.getData());
    }
    // 查询基础
//    customerFeignClient.findcustomersByClueIds();
    ClueQueryDTO queryDTO = new ClueQueryDTO();
    queryDTO.setClueId(clueId.getId());
    JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(queryDTO);
    // 查询当前资源是否来源于共有池
    ClueManagementParamDto reqDto = new ClueManagementParamDto();
    reqDto.setClueId(clueId.getId());
    JSONResult<List<ClueManagementDto>> listJSONResult = clueManagementFeignClient
        .listNoPage(reqDto);
    Integer cpoolReceiveFlag = 0;
    if(CommonUtil.resultCheck(listJSONResult)){
      List<ClueManagementDto> data = listJSONResult.getData();
      ClueManagementDto clueManagementDto = data.get(0);
      cpoolReceiveFlag = clueManagementDto.getCpoolReceiveFlag();
    }

    if(CommonUtil.resultCheck(clueInfo)){
      ClueDTO data = clueInfo.getData();
      ClueCustomerDTO clueCustomer = data.getClueCustomer();
      if(clueCustomer!=null){
        resourceTrajectory.setAge(clueCustomer.getAge());
        resourceTrajectory.setCusName(clueCustomer.getCusName());
        resourceTrajectory.setEmail(clueCustomer.getEmail());
        if(cpoolReceiveFlag!=null&&cpoolReceiveFlag==1){
          resourceTrajectory.setPhone("***");
        }else{
          resourceTrajectory.setPhone(clueCustomer.getPhone());
        }
        resourceTrajectory.setPhone(clueCustomer.getPhone());
        resourceTrajectory.setPhone2(clueCustomer.getPhone2());
        resourceTrajectory.setPhone3(clueCustomer.getPhone3());
        resourceTrajectory.setPhone4(clueCustomer.getPhone4());
        resourceTrajectory.setPhone5(clueCustomer.getPhone5());
        resourceTrajectory.setWechat(clueCustomer.getWechat());
        resourceTrajectory.setWechat2(clueCustomer.getWechat2());
        resourceTrajectory.setWechatCode(clueCustomer.getWechatCode());
        resourceTrajectory.setQq(clueCustomer.getQq());
        resourceTrajectory.setSex(clueCustomer.getSex()!=null&&clueCustomer.getSex()==1?"男":"女");
      }
      ClueBasicDTO clueBasic = data.getClueBasic();
      if(clueBasic!=null){
        // 客户详情信息
        resourceTrajectory.setClueId(clueBasic.getId());
        resourceTrajectory.setMessageTime(clueBasic.getMessageTime());
        resourceTrajectory.setMessagePoint(clueBasic.getMessagePoint());
        resourceTrajectory.setSearchWord(clueBasic.getSearchWord());
        resourceTrajectory.setProjectName(clueBasic.getProjectName());
        resourceTrajectory.setSource(clueBasic.getSourceName());
        resourceTrajectory.setCategory(clueBasic.getCategoryName());
        // 第5条中接口字段中广告位信息froms -- 解析后，为系统中广告位信息
        resourceTrajectory.setTerminal(clueBasic.getSourceTypeName());
      }
      ClueIntentionDTO clueIntention = data.getClueIntention();
      if(clueIntention!=null){
        resourceTrajectory.setArea(clueIntention.getAddress());
      }

      //设置跳转URL
      ClueReceiveDTO clueReceive = data.getClueReceive();
      if(clueReceive!=null){
        String urlAddress = clueReceive.getUrlAddress();
        resourceTrajectory.setUrl(urlAddress);
      }
      // 设置图片 -- 待定
//        resourceTrajectory.setImageUrl();
    }

    return new JSONResult().success(resourceTrajectory);
  }


}
