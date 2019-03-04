package com.kuaidao.manageweb.controller.sign;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.pagehelper.PageHelper;
import com.kuaidao.aggregation.dto.busmycustomer.RejectSignOrderReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordRespDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.sign.SignRecordFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * 签约记录
 * @author  Chen
 * @date 2019年3月1日 下午6:29:43   
 * @version V1.0
 */
@RequestMapping("/sign/signRecord")
@Controller
public class SignRecordController {
    
    private static Logger logger = LoggerFactory.getLogger(SignRecordController.class);

    @Autowired
    SignRecordFeignClient signRecordFeignClient;
    
    @Autowired
    OrganizationFeignClient organizationFeignClient;
    
    @Autowired
    UserInfoFeignClient userInfoFeignClient;
    
    /**
     * 签约记录 页面
     * @return
     */
    @RequestMapping("/signRecordPage")
    public String signRecordPage(HttpServletRequest request) {

        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        //商务小组
        List<OrganizationDTO> businessGroupList = getBusinessGroupList(orgId, OrgTypeConstant.SWZ);
        //电销组 
        List<OrganizationRespDTO> teleGroupList =getTeleGroupList(OrgTypeConstant.DXZ);
        //商务经理        
        List<UserInfoDTO> busManagerList = getUserInfo(orgId,RoleCodeEnum.SWJL.name());
        //电销人员
        List<UserInfoDTO> teleList = getUserInfo(null, RoleCodeEnum.DXCYGW.name());
        
        request.setAttribute("busManagerList",busManagerList);
        request.setAttribute("businessGroupList",businessGroupList);
        request.setAttribute("teleGroupList",teleGroupList);
        request.setAttribute("teleGroupList",teleList);

        return "signrecord/signRecord";
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
        } 
        return userJr.getData();
    }
    
    /**
     * 获取商务组
     * @param orgId
     * @param orgType
     * @return
     */
    private List<OrganizationDTO> getBusinessGroupList(Long orgId,Integer orgType){
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationDTO>> orgJr = organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        if(orgJr==null || !JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("query org list res{{}}",orgJr);
            return null;
        }
        return orgJr.getData();
        
    }
    
    /**
     * 获取所有的 组
     * @param orgType
     * @return
     */
    private List<OrganizationRespDTO> getTeleGroupList(Integer orgType){
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationRespDTO>> orgJr = organizationFeignClient.queryOrgByParam(busGroupReqDTO);
        if(orgJr==null || !JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("query org list res{{}}",orgJr);
            return null;
        }
        return orgJr.getData();
    }
    

    /**
     * 签约记录 分页
     * @param reqDTO
     * @return
     */
    @PostMapping("/listSignRecord")
    @ResponseBody
    public JSONResult<PageBean<SignRecordRespDTO>> listSignRecord(@RequestBody SignRecordReqDTO reqDTO) {
        //TODO devin 角色处理
        
        PageHelper.startPage(reqDTO.getPageNum(),reqDTO.getPageSize());
        return signRecordFeignClient.listSignRecord(reqDTO);
    }
    
    /**
     * 驳回签约单
     * @return
     */
    @PostMapping("/rejectSignOrder")
    @LogRecord(description = "签约单驳回",operationType = LogRecord.OperationType.UPDATE,menuName = MenuEnum.SIGN_ORDER)
    @ResponseBody
    public JSONResult<Boolean> rejectSignOrder(@Valid @RequestBody RejectSignOrderReqDTO reqDTO,BindingResult result){
        if(result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        
        return signRecordFeignClient.rejectSignOrder(reqDTO);
    }
    
        
}
