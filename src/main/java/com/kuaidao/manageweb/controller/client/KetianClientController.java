package com.kuaidao.manageweb.controller.client;

import com.kuaidao.aggregation.dto.client.ClientLoginReCordDTO;
import com.kuaidao.callcenter.dto.ketianclient.*;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;
import com.kuaidao.manageweb.feign.client.KetianFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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


    /**
     * 页面跳转
     * 
     * @param request
     * @return
     */
    @GetMapping("/ketianClientPage")
    public String ketianClientPage(HttpServletRequest request) {

        return "client/ketianClientPage";
    }

    /**
     * 分页查询坐席
     * 
     * @param reqDTO
     * @return
     */
    @PostMapping("/listClientPage")
    @ResponseBody
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
    public JSONResult updateClient(@Valid @RequestBody KetianClientInsertAndUpdateReqDTO reqDTO, BindingResult result) {
        if (result.hasErrors()) {
            logger.warn("update client ,illegal param {{}}", reqDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        return ketianFeignClient.insertAndUpdateClient(reqDTO);
    }

    /**
     * 根据坐席号查询坐席信息
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
        clientLoginRecord.setClientType(clientLoginRecord.getClientType());
        clientLoginRecord.setCno(ketianClientRespDTO.getClientExtNo());
        clientLoginRecord.setOrgId(curLoginUser.getOrgId());
        clientLoginRecord.setAccountNo(ketianClientRespDTO.getUserName());
        return clientFeignClient.clientLoginRecord(clientLoginRecord);
    }

    /**
     * 外呼
     * 
     * @param result
     * @return
     */
    @PostMapping("/outbound")
    @ResponseBody
    public JSONResult outbound(@Valid @RequestBody KetianClientOutboundDTO outboundDTO, BindingResult result) {
        if (result.hasErrors()) {
            logger.warn("ketian outbound illegal param {{}}",outboundDTO);
            return CommonUtil.validateParam(result);
        }
        return ketianFeignClient.outbound(outboundDTO);
    }

}
