package com.kuaidao.manageweb.controller.client;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.kuaidao.aggregation.dto.client.ClientLoginReCordDTO;
import com.kuaidao.callcenter.dto.HeLiClientOutboundReqDTO;
import com.kuaidao.callcenter.dto.HeliClientReqDTO;
import com.kuaidao.callcenter.dto.HeliClientRespDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;
import com.kuaidao.manageweb.feign.client.HeliClientFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
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
    
    @Autowired
    OrganizationFeignClient organizationFeignClient;
    
    
    /**
     * 跳转 合力坐席管理页面
    * @return
     */
    @RequiresPermissions("callCenter:heliClient:view")
    @GetMapping("/heliClientPage")
    public String heliClientPage(HttpServletRequest request) {
        List<OrganizationDTO> orgList = new ArrayList<>();
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(String.valueOf(curLoginUser.getOrgId()));
            if(curOrgGroupByOrgId!=null) {
                orgList.add(curOrgGroupByOrgId);
            }
            request.setAttribute("orgList", orgList);
        } else {
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setSystemCode(SystemCodeConstant.HUI_JU);
//      queryDTO.setOrgType(OrgTypeConstant.DXZ);
            //查询全部
            JSONResult<List<OrganizationRespDTO>> orgListJr =
                organizationFeignClient.queryOrgByParam(queryDTO);
            if (orgListJr == null || !JSONResult.SUCCESS.equals(orgListJr.getCode())) {
                logger.error("跳转合力坐席时，查询组织机构列表报错,res{{}}", orgListJr);
            } else {
                request.setAttribute("orgList", orgListJr.getData());
            }
        }
        return "client/heliClientPage";
    }
    
    
    /**
     * 坐席登录
    * @param heLiClientOutboundReqDTO
    * @return
     */
    @PostMapping("/login")
    @ResponseBody
   /* @LogRecord(description = "合力坐席登录", operationType = OperationType.LOGIN,
    menuName = MenuEnum.HELI_CLIENT_MANAGEMENT)*/
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
/*    @LogRecord(description = "合力坐席退出", operationType = OperationType.LOGINOUT,
    menuName = MenuEnum.HELI_CLIENT_MANAGEMENT)*/
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
    
    
    /**
     * 坐席外呼
    * @param heLiClientOutboundReqDTO
    * @return
     */
    @PostMapping("/outbound")
    @ResponseBody
   /* @LogRecord(description = "合力坐席外呼", operationType = OperationType.OUTBOUNDCALL,
    menuName = MenuEnum.HELI_CLIENT_MANAGEMENT)*/
    public JSONResult outbound (@RequestBody HeLiClientOutboundReqDTO  heLiClientOutboundReqDTO) {
          String customerPhone = heLiClientOutboundReqDTO.getCustomerPhone();
          if (StringUtils.isBlank(customerPhone)) {
              return CommonUtil.getParamIllegalJSONResult();
          }

          UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
          Long orgId = curLoginUser.getOrgId();
          heLiClientOutboundReqDTO.setOrgId(orgId);
          return heliClientFeignClient.outbound(heLiClientOutboundReqDTO);
    }
    
    /**
     * 查询坐席列表
    * @param heliClientReqDTO
    * @return
     */
    @PostMapping("/listClientsPage")
    @ResponseBody
    public JSONResult<PageBean<HeliClientRespDTO>> listClientsPage(@RequestBody HeliClientReqDTO heliClientReqDTO){
        return heliClientFeignClient.listClientsPage(heliClientReqDTO);
    }
    
    /**
     * 获取当前 orgId所在的组织
     * @param orgId
     * @param
     * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId+"");
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}",idEntity,orgJr);
            return null;
        }
        return orgJr.getData();
    }
    

}
