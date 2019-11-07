package com.kuaidao.manageweb.controller.client;

import com.kuaidao.aggregation.dto.client.ClientLoginReCordDTO;
import com.kuaidao.callcenter.dto.ketianclient.*;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;
import com.kuaidao.manageweb.feign.client.KetianFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 科天controller
 * 
 * @author Devin.Chen
 * @Date: 2019/10/23 16:10
 */
@Controller
@RequestMapping("/client/ketianClient")
public class KetianClientController {
    private static Logger logger = LoggerFactory.getLogger(KetianClientController.class);

    @Autowired
    KetianFeignClient ketianFeignClient;

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
    @GetMapping("/ketianClientPage")
    @RequiresPermissions("callCenter:ketianClient:view")
    public String ketianClientPage(HttpServletRequest request) {
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
            //查询全部
            JSONResult<List<OrganizationRespDTO>> orgListJr = organizationFeignClient.queryOrgByParam(queryDTO);
            if (orgListJr == null || !JSONResult.SUCCESS.equals(orgListJr.getCode())) {
                logger.error("跳转科天坐席时，查询组织机构列表报错,res{{}}", orgListJr);
            } else {
                request.setAttribute("orgList", orgListJr.getData());
            }
        }

        return "client/ketianClientPage";
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
    @PostMapping("/listClientPage")
    @ResponseBody
    @RequiresPermissions("callCenter:ketianClient:view")
    public JSONResult<PageBean<KetianClientRespDTO>> listClientPage(@RequestBody KetianClientPageReqDTO reqDTO) {
        return ketianFeignClient.listClientPage(reqDTO);
    }

    /**
     * 查询坐席号是否属于自己
     * 
     * @param ketianClientReqDTO
     * @return
     */
    @PostMapping("/queryClientBelongToMy")
    @ResponseBody
    public JSONResult<Boolean> queryClientBelongToMy(@RequestBody KetianClientReqDTO ketianClientReqDTO) {
        String loginName = ketianClientReqDTO.getLoginName();
        if (StringUtils.isBlank(loginName)) {
            logger.warn("ketianClient queryClientBelongToMy ,illegal param {{}} ", ketianClientReqDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long accountId = curLoginUser.getId();
        ketianClientReqDTO.setAccountId(accountId);
        JSONResult<List<KetianClientRespDTO>> clientJr = ketianFeignClient.listClient(ketianClientReqDTO);
        if (clientJr == null || !JSONResult.SUCCESS.equals(clientJr.getCode())) {
            logger.error("queryClientBelongToMy  heliClientFeignClient.listClientByParams(),param{{}},rs{{}}", ketianClientReqDTO, clientJr);
            return new JSONResult<Boolean>().fail(clientJr.getCode(), clientJr.getMsg());
        }
        // 默认坐席属于自己
        boolean isBelongToSelf = true;
        List<KetianClientRespDTO> data = clientJr.getData();
        if (CollectionUtils.isEmpty(data)) {
            isBelongToSelf = false;
        }
        return new JSONResult<Boolean>().success(isBelongToSelf);

    }

    /**
     * 根据ID查询记录
     * 
     * @param idEntityLong
     * @return
     */
    @PostMapping("/queryById")
    @ResponseBody
    public JSONResult<KetianClientRespDTO> queryById(@RequestBody IdEntityLong idEntityLong) {
        Long id = idEntityLong.getId();
        if (id == null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        return ketianFeignClient.queryById(idEntityLong);
    }

    /**
     * 添加坐席
     * 
     * @return
     */
    @PostMapping("/insertClient")
    @ResponseBody
    @RequiresPermissions("callCenter:ketianClient:add")
    public JSONResult insertClient(@Valid @RequestBody KetianClientInsertAndUpdateReqDTO reqDTO, BindingResult result) {
        if (result.hasErrors()) {
            logger.warn("insert client ,illegal param {{}}", reqDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());
        return ketianFeignClient.insertAndUpdateClient(reqDTO);
    }

    /**
     * 修改坐席
     * 
     * @return
     */
    @PostMapping("/updateClient")
    @ResponseBody
    @RequiresPermissions("callCenter:ketianClient:edit")
    public JSONResult updateClient(@Valid @RequestBody KetianClientInsertAndUpdateReqDTO reqDTO, BindingResult result) {
        if (result.hasErrors()) {
            logger.warn("update client ,illegal param {{}}", reqDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        Long id = reqDTO.getId();
        if(id == null){
            return CommonUtil.getParamIllegalJSONResult();
        }
        return ketianFeignClient.insertAndUpdateClient(reqDTO);
    }

    /**
     * 根据登录名查询坐席信息
     * 
     * @param ketianClientReqDTO
     * @return
     */
    @PostMapping("/queryClientByLoginName")
    @ResponseBody
    public JSONResult<KetianClientRespDTO> queryClientByLoginName(@RequestBody KetianClientReqDTO ketianClientReqDTO) {
        String loginName = ketianClientReqDTO.getLoginName();
        if (StringUtils.isBlank(loginName)) {
            logger.warn("ketianClient queryClientInfoByCno ,illegal param {{}} ", ketianClientReqDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long accountId = curLoginUser.getId();
        ketianClientReqDTO.setAccountId(accountId);
        JSONResult<List<KetianClientRespDTO>> clientJr = ketianFeignClient.listClient(ketianClientReqDTO);
        if (clientJr == null || !JSONResult.SUCCESS.equals(clientJr.getCode())) {
            logger.error("queryClientByLoginName  heliClientFeignClient.listClientByParams(),param{{}},rs{{}}", ketianClientReqDTO, clientJr);
            return new JSONResult<KetianClientRespDTO>().fail(clientJr.getCode(), clientJr.getMsg());
        }
        List<KetianClientRespDTO> clientList = clientJr.getData();
        if (CollectionUtils.isNotEmpty(clientList)) {
            KetianClientRespDTO ketianClientRespDTO = clientList.get(0);
            return new JSONResult<KetianClientRespDTO>().success(ketianClientRespDTO);
        }
        return new JSONResult<KetianClientRespDTO>().success(null);
    }

    /**
     * 坐席登录记录
     * 
     * @return
     */
    @PostMapping("/clientLoginRecord")
    @ResponseBody
    @LogRecord(description = "科天坐席登录", operationType = LogRecord.OperationType.CLIENT_LOGIN, menuName = MenuEnum.KETIAN_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> clientLoginRecord(@Valid @RequestBody KetianClientLoginDTO ketianClientLoginDTO, BindingResult result) {
        if (result.hasErrors()) {
            logger.warn("ketian  clientLoginRecord illegal param{{}}", ketianClientLoginDTO);
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        KetianClientReqDTO ketianClientReqDTO = new KetianClientReqDTO();
        Long accountId = curLoginUser.getId();
        ketianClientReqDTO.setAccountId(accountId);
        ketianClientReqDTO.setLoginName(ketianClientLoginDTO.getLoginName());
        JSONResult<List<KetianClientRespDTO>> clientJr = ketianFeignClient.listClient(ketianClientReqDTO);
        if (clientJr == null || !JSONResult.SUCCESS.equals(clientJr.getCode())) {
            logger.error("clientLoginRecord  ketianFeignClient.listClient(),param{{}},rs{{}}", ketianClientReqDTO, clientJr);
            return new JSONResult<Boolean>().fail(clientJr.getCode(), clientJr.getMsg());
        }
        List<KetianClientRespDTO> clientList = clientJr.getData();
        if (CollectionUtils.isEmpty(clientList)) {
            return new JSONResult<Boolean>().fail("-1", "未查询到数据");
        }
        // 坐席信息存到redis
        KetianClientRespDTO ketianClientRespDTO = clientList.get(0);
        ClientLoginReCordDTO clientLoginRecord = new ClientLoginReCordDTO();
        clientLoginRecord.setAccountId(accountId);
        clientLoginRecord.setAccountType(ketianClientLoginDTO.getAccountType());
        clientLoginRecord.setClientType(Integer.parseInt(ketianClientLoginDTO.getClientType()));
        clientLoginRecord.setCno(ketianClientRespDTO.getClientExtNo());
        clientLoginRecord.setOrgId(curLoginUser.getOrgId());
        clientLoginRecord.setAccountNo(ketianClientRespDTO.getUserName());
        return clientFeignClient.clientLoginRecord(clientLoginRecord);
    }


    /**
     * 删除坐席
     * @param idListLongReq
     * @return
     */
    @PostMapping("/deleteClientByIdList")
    @ResponseBody
    @RequiresPermissions("callCenter:ketianClient:delete")
    public JSONResult deleteClientByIdList(@RequestBody IdListLongReq idListLongReq){
        List<Long> idList = idListLongReq.getIdList();
        if(CollectionUtils.isEmpty(idList)){
            return CommonUtil.getParamIllegalJSONResult();
        }
        return  ketianFeignClient.deleteClientByIdList(idListLongReq);
    }

    /**
     * 外呼
     * 
     * @param result
     * @return
     */
    @PostMapping("/outbound")
    @ResponseBody
    @LogRecord(description = "科天坐席外呼", operationType = LogRecord.OperationType.OUTBOUNDCALL, menuName = MenuEnum.KETIAN_CLIENT_MANAGEMENT)
    public JSONResult outbound(@Valid @RequestBody KetianClientOutboundDTO outboundDTO, BindingResult result) {
        if (result.hasErrors()) {
            logger.warn("ketian outbound illegal param {{}}",outboundDTO);
            return CommonUtil.validateParam(result);
        }
        return ketianFeignClient.outbound(outboundDTO);
    }

}
