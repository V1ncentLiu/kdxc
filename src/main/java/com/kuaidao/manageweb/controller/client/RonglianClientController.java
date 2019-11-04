package com.kuaidao.manageweb.controller.client;

import com.kuaidao.aggregation.dto.client.ClientLoginReCordDTO;
import com.kuaidao.callcenter.dto.RonglianClientDTO;
import com.kuaidao.callcenter.dto.RonglianClientInsertReq;
import com.kuaidao.callcenter.dto.RonglianClientResqDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.JSONUtil;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;
import com.kuaidao.manageweb.feign.client.RonglianFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created on: 2019-10-29-20:24
 */
@Controller
@RequestMapping("/client/ronglianClient")
public class RonglianClientController {

    private static Logger logger = LoggerFactory.getLogger(RonglianClientController.class);

    @Autowired
    RonglianFeignClient ronglianFeignClient;

    @Autowired
    ClientFeignClient clientFeignClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;


    /**
     * 页面跳转
     *
     * @param request
     * @return
     */
    @RequiresPermissions("callCenter:ronglianClient:view")
    @GetMapping("/ronglianClientPage")
    public String ronglianClientPage(HttpServletRequest request) {
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
            JSONResult<List<OrganizationRespDTO>> orgListJr =
                organizationFeignClient.queryOrgByParam(queryDTO);
            if (orgListJr == null || !JSONResult.SUCCESS.equals(orgListJr.getCode())) {
                logger.error("跳转容联坐席时，查询组织机构列表报错,res{{}}", orgListJr);
            } else {
                request.setAttribute("orgList", orgListJr.getData());
            }
        }
        return "client/ronglianClientPage";
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

    /**
     * 分页查询坐席
     *
     * @param reqDTO
     * @return
     */
    @RequiresPermissions("callCenter:ronglianClient:view")
    @PostMapping("/listRonglianClientPage")
    @ResponseBody
    public JSONResult<PageBean<RonglianClientResqDTO>> listRonglianClientPage(@RequestBody RonglianClientDTO reqDTO) {
        return ronglianFeignClient.listRonglianClientPage(reqDTO);
    }

    /**
     * 根据id查询坐席
     *
     * @param idEntity
     * @return
     */
    @PostMapping("/queryById")
    @ResponseBody
    public JSONResult<RonglianClientResqDTO> queryById(@RequestBody IdEntity idEntity) {
        return ronglianFeignClient.queryRonglianClientById(idEntity);
    }

    /**
     * 添加坐席
     *
     * @return
     */
    @RequiresPermissions("callCenter:ronglianClient:add")
    @PostMapping("/insertRonglianClient")
    @ResponseBody
    public JSONResult insertRonglianClient(@Valid @RequestBody RonglianClientInsertReq reqDTO, BindingResult result) {
        if (result.hasErrors()) {
            logger.error("insert ronglian client ,param {{}}", reqDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());
        return ronglianFeignClient.saveRonglianClient(reqDTO);
    }

    /**
     * 修改坐席
     *
     * @return
     */
    @RequiresPermissions("callCenter:ronglianClient:edit")
    @PostMapping("/updateRonglianClient")
    @ResponseBody
    public JSONResult updateRonglianClient(@RequestBody RonglianClientDTO reqDTO) {
        Long id = reqDTO.getId();
        if(null == id){
            return CommonUtil.getParamIllegalJSONResult();
        }
        return ronglianFeignClient.updateRonglianClient(reqDTO);
    }

    /**
     * 删除坐席
     * @param idListLongReq
     * @return
     */
    @RequiresPermissions("callCenter:ronglianClient:delete")
    @PostMapping("/deleteClientByIdList")
    @ResponseBody
    public JSONResult deleteClientByIdList(@RequestBody IdListLongReq idListLongReq){
        List<Long> idList = idListLongReq.getIdList();
        if(CollectionUtils.isEmpty(idList)){
            return CommonUtil.getParamIllegalJSONResult();
        }
        return  ronglianFeignClient.deleteRonglianClient(idListLongReq);
    }

    /**
     * 坐席登录
     *
     * @param ronglianClientDTO
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public JSONResult login(@RequestBody RonglianClientDTO ronglianClientDTO) {
        String loginName = ronglianClientDTO.getLoginName();
        String accountType = ronglianClientDTO.getAccountType();
        Integer clientType = ronglianClientDTO.getClientType();
        if (CommonUtil.isBlank(loginName) || CommonUtil.isBlank(accountType) || null == clientType) {
            logger.error("ronglian login param{{}}", ronglianClientDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        //根据登录坐席查询坐席
        RonglianClientDTO reqDTO = new RonglianClientDTO();
        reqDTO.setLoginName(loginName);
        JSONResult<RonglianClientResqDTO> ronglianJr = ronglianFeignClient.queryRonglianClientByLoginName(reqDTO);
        if (!JSONResult.SUCCESS.equals(ronglianJr.getCode()) || null == ronglianJr.getData()) {
            return new JSONResult()
                .fail(SysErrorCodeEnum.ERR_NO_EXISTS_FAIL.getCode(), "登录坐席不存在");
        }
        //根据登录坐席和登录用户查询
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        RonglianClientDTO reqDTOByLoginNameAndUser = new RonglianClientDTO();
        reqDTOByLoginNameAndUser.setUserId(curLoginUser.getId());
        reqDTOByLoginNameAndUser.setLoginName(loginName);
        JSONResult<RonglianClientResqDTO> ronglianJrByLoginNameAndUser = ronglianFeignClient.queryRonglianClientByLoginName(reqDTOByLoginNameAndUser);
        if (!JSONResult.SUCCESS.equals(ronglianJrByLoginNameAndUser.getCode()) || null == ronglianJrByLoginNameAndUser.getData()) {
            return new JSONResult()
                .fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "该坐席不属于你");
        }
        // 将坐席号放入seesion 便于后续使用
        RonglianClientResqDTO ronglianClientResqDTO = ronglianJrByLoginNameAndUser.getData();
        SecurityUtils.getSubject().getSession().setAttribute("agentId:ronglian", ronglianClientResqDTO.getAgentId());
        Long orgId = curLoginUser.getOrgId();
        //调用登录接口
        //容联登录state参数值:11
        ronglianClientDTO.setState("11");
        ronglianClientDTO.setUserId(curLoginUser.getId());

        JSONResult loginJson = ronglianFeignClient.setRonglianClientState(ronglianClientDTO);
        if (!JSONResult.SUCCESS.equals(loginJson.getCode())) {
            return loginJson;
        }
        ClientLoginReCordDTO clientLoginRecord = new ClientLoginReCordDTO();
        clientLoginRecord.setAccountId(curLoginUser.getId());
        clientLoginRecord.setAccountType(accountType);
        clientLoginRecord.setOrgId(orgId);
        clientLoginRecord.setCno(ronglianClientResqDTO.getAgentId());
        clientLoginRecord.setClientType(clientType);
        clientLoginRecord.setAccountNo(ronglianClientResqDTO.getAppId());
        JSONResult<Boolean> loginRecordJr = clientFeignClient.clientLoginRecord(clientLoginRecord);
        if (!JSONResult.SUCCESS.equals(loginRecordJr.getCode())) {
            logger.error("ronglianClient push redis ,param{{}},res{{}}", clientLoginRecord, loginRecordJr);
        }

        return loginJson;
    }

    /**
     * 坐席退出
     *
     * @param ronglianClientDTO
     * @return
     */
    @ResponseBody
    @PostMapping("/logout")
    public JSONResult logout(@RequestBody RonglianClientDTO ronglianClientDTO) {
        String loginName = ronglianClientDTO.getLoginName();
        if (CommonUtil.isBlank(loginName)) {
            logger.error("ronglian loginout param{{}}", ronglianClientDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        // 退出删除坐席号
        Session session = SecurityUtils.getSubject().getSession();
        session.removeAttribute("agentId:ronglian");

        //容联下班state参数值:00
        ronglianClientDTO.setState("00");
        ronglianClientDTO.setUserId(curLoginUser.getId());
        return ronglianFeignClient.setRonglianClientState(ronglianClientDTO);
    }

    /**
     * 坐席外呼
     * @param ronglianClientDTO
     * @return
     */
    @PostMapping("/outbound")
    @ResponseBody
    public JSONResult outbound(@RequestBody RonglianClientDTO ronglianClientDTO) {
        String customerPhone = ronglianClientDTO.getCustomerPhone();
        String accountType = ronglianClientDTO.getAccountType();
        if (StringUtils.isBlank(customerPhone) || StringUtils.isBlank(accountType)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        //获取坐席号
        Session session = SecurityUtils.getSubject().getSession();
        String agentId = (String) session.getAttribute("agentId:ronglian");
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        ronglianClientDTO.setAgentId(agentId);
        ronglianClientDTO.setUserId(curLoginUser.getId());
        logger.info("容联坐席外呼,请求实体:{}", JSONUtil.toJSon(ronglianClientDTO));
        return ronglianFeignClient.ronglianOutBoundCall(ronglianClientDTO);
    }
}
