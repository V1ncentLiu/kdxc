package com.kuaidao.manageweb.controller.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.client.ClientLoginReCordDTO;
import com.kuaidao.callcenter.dto.HeLiClientOutboundReqDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;
import com.kuaidao.manageweb.feign.client.HeliClientFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * 合力 坐席 
 * @author  Devin.Chen
 * @date 2019-08-08 13:41:28
 * @version V1.0
 */
@Controller
@RequestMapping("/client/heliClient")
public class HeliClientController {
    
    private static Logger logger = LoggerFactory.getLogger(HeliClientController.class);
    
    @Autowired
    HeliClientFeignClient heliClientFeignClient;
    
    @Autowired
    ClientFeignClient clientFeignClient;
    
    
    @PostMapping("/login")
    @ResponseBody
    public JSONResult login (@RequestBody HeLiClientOutboundReqDTO  heLiClientOutboundReqDTO) {
          String clientNo = heLiClientOutboundReqDTO.getClientNo();
          if(!CommonUtil.isNotBlank(clientNo)) {
              logger.error("heliClient login param{{}}",heLiClientOutboundReqDTO);
              return CommonUtil.getParamIllegalJSONResult();
          }
          
          UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
          Long orgId = curLoginUser.getOrgId();
          heLiClientOutboundReqDTO.setOrgId(orgId);
          
          JSONResult loginRes = heliClientFeignClient.login(heLiClientOutboundReqDTO);
          if(!JSONResult.SUCCESS.equals(loginRes.getCode())) {
              return loginRes;
          }
          ClientLoginReCordDTO clientLoginRecord = new ClientLoginReCordDTO();
          clientLoginRecord.setAccountId(curLoginUser.getId());
          clientLoginRecord.setAccountType(heLiClientOutboundReqDTO.getAccountType());
          clientLoginRecord.setOrgId(orgId);
          clientLoginRecord.setCno(heLiClientOutboundReqDTO.getClientNo());
          clientLoginRecord.setClientType(heLiClientOutboundReqDTO.getClientType());
          JSONResult<Boolean> loginRecordJr = clientFeignClient.clientLoginRecord(clientLoginRecord);
          if(!JSONResult.SUCCESS.equals(loginRecordJr.getCode())) {
              logger.error("heliClient push redis ,param{{}},res{{}}",clientLoginRecord,loginRecordJr);
          }
        
          return loginRecordJr;
    }
    
    /**
     * 坐席退出
    * @param heLiClientOutboundReqDTO
    * @return
     */
    @ResponseBody
    @PostMapping("/logout")
    public JSONResult logout (@RequestBody HeLiClientOutboundReqDTO  heLiClientOutboundReqDTO) {
          String clientNo = heLiClientOutboundReqDTO.getClientNo();
          if(!CommonUtil.isNotBlank(clientNo)) {
              logger.error("heliClient logout param{{}}",heLiClientOutboundReqDTO);
              return CommonUtil.getParamIllegalJSONResult();
          }
          
          UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
          Long orgId = curLoginUser.getOrgId();
          heLiClientOutboundReqDTO.setOrgId(orgId);
          return heliClientFeignClient.logout(heLiClientOutboundReqDTO);
    }
    
    

}
