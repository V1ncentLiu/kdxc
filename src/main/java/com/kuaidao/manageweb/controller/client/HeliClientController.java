package com.kuaidao.manageweb.controller.client;

import com.kuaidao.aggregation.dto.client.ImportTrClientDTO;
import com.kuaidao.aggregation.dto.client.UploadTrClientDataDTO;
import com.kuaidao.callcenter.dto.HeliClientInsertReq;
import com.kuaidao.callcenter.dto.ImportHeliClientDTO;
import com.kuaidao.callcenter.dto.RonglianClientDTO;
import com.kuaidao.callcenter.dto.RonglianClientInsertReq;
import com.kuaidao.callcenter.dto.seatmanager.HeliClientReq;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.constant.Constants;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import com.kuaidao.aggregation.dto.client.ClientLoginReCordDTO;
import com.kuaidao.callcenter.dto.HeLiClientOutboundReqDTO;
import com.kuaidao.callcenter.dto.HeliClientReqDTO;
import com.kuaidao.callcenter.dto.HeliClientRespDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.JSONUtil;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * 合力 坐席
 * 
 * @author Devin.Chen
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

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    RedisTemplate redisTemplate;


    /**
     * 跳转 合力坐席管理页面
     * 
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
        if (RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            // 电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId =
                    getCurOrgGroupByOrgId(String.valueOf(curLoginUser.getOrgId()));
            if (curOrgGroupByOrgId != null) {
                orgList.add(curOrgGroupByOrgId);
            }
            request.setAttribute("orgList", orgList);
        } else {
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setSystemCode(SystemCodeConstant.HUI_JU);
            // queryDTO.setOrgType(OrgTypeConstant.DXZ);
            // 查询全部
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
     *
     * @param heLiClientOutboundReqDTO
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    @LogRecord(description = "合力坐席登录", operationType = OperationType.CLIENT_LOGIN,
            menuName = MenuEnum.HELI_CLIENT_MANAGEMENT)
    public JSONResult login(@RequestBody HeLiClientOutboundReqDTO heLiClientOutboundReqDTO) {
        String clientNo = heLiClientOutboundReqDTO.getClientNo();
        if (!CommonUtil.isNotBlank(clientNo)) {
            logger.error("heliClient login param{{}}", heLiClientOutboundReqDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        // 将坐席号放入seesion 便于后续使用
        SecurityUtils.getSubject().getSession().setAttribute("clientNo:axb", clientNo);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        heLiClientOutboundReqDTO.setOrgId(orgId);

        JSONResult loginRes = heliClientFeignClient.login(heLiClientOutboundReqDTO);
        if (!JSONResult.SUCCESS.equals(loginRes.getCode())) {
            return loginRes;
        }
        ClientLoginReCordDTO clientLoginRecord = new ClientLoginReCordDTO();
        clientLoginRecord.setAccountId(curLoginUser.getId());
        clientLoginRecord.setAccountType(heLiClientOutboundReqDTO.getAccountType());
        clientLoginRecord.setOrgId(orgId);
        clientLoginRecord.setCno(heLiClientOutboundReqDTO.getClientNo());
        clientLoginRecord.setClientType(heLiClientOutboundReqDTO.getClientType());
        JSONResult<Boolean> loginRecordJr = clientFeignClient.clientLoginRecord(clientLoginRecord);
        if (!JSONResult.SUCCESS.equals(loginRecordJr.getCode())) {
            logger.error("heliClient push redis ,param{{}},res{{}}", clientLoginRecord,
                    loginRecordJr);
        }

        return loginRecordJr;
    }

    /**
     * 坐席退出
     * 
     * @param heLiClientOutboundReqDTO
     * @return
     */
    @ResponseBody
    @PostMapping("/logout")
    @LogRecord(description = "合力坐席退出", operationType = OperationType.CLIENT_LOGOUT,
            menuName = MenuEnum.HELI_CLIENT_MANAGEMENT)
    public JSONResult logout(@RequestBody HeLiClientOutboundReqDTO heLiClientOutboundReqDTO) {
        String clientNo = heLiClientOutboundReqDTO.getClientNo();
        if (!CommonUtil.isNotBlank(clientNo)) {
            logger.error("heliClient logout param{{}}", heLiClientOutboundReqDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        // 退出删除坐席号
        Session session = SecurityUtils.getSubject().getSession();
        session.removeAttribute("clientNo:axb");
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        heLiClientOutboundReqDTO.setOrgId(orgId);
        return heliClientFeignClient.logout(heLiClientOutboundReqDTO);
    }


    /**
     * 坐席外呼
     * 
     * @param heLiClientOutboundReqDTO
     * @return
     */
    @PostMapping("/outbound")
    @ResponseBody
    @LogRecord(description = "合力坐席外呼", operationType = OperationType.OUTBOUNDCALL,
            menuName = MenuEnum.HELI_CLIENT_MANAGEMENT)
    public JSONResult outbound(@RequestBody HeLiClientOutboundReqDTO heLiClientOutboundReqDTO) {
        String customerPhone = heLiClientOutboundReqDTO.getCustomerPhone();
        if (StringUtils.isBlank(customerPhone)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        // 获取坐席号
        Session session = SecurityUtils.getSubject().getSession();
        String clientNo = (String) session.getAttribute("clientNo:axb");
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        heLiClientOutboundReqDTO.setOrgId(orgId);
        heLiClientOutboundReqDTO.setClientNo(clientNo);
        logger.info("合力坐席外呼,请求实体:{}", JSONUtil.toJSon(heLiClientOutboundReqDTO));
        return heliClientFeignClient.outbound(heLiClientOutboundReqDTO);
    }

    /**
     * 查询坐席列表
     * 
     * @param heliClientReqDTO
     * @return
     */
    @PostMapping("/listClientsPage")
    @ResponseBody
    public JSONResult<PageBean<HeliClientRespDTO>> listClientsPage(
            @RequestBody HeliClientReqDTO heliClientReqDTO) {
        return heliClientFeignClient.listClientsPage(heliClientReqDTO);
    }

    /**
     * 获取当前 orgId所在的组织
     * 
     * @param orgId
     * @param
     * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId + "");
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if (!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}", idEntity, orgJr);
            return null;
        }
        return orgJr.getData();
    }


    /**
     * 根据坐席号查询坐席信息
     * 
     * @param heliClientReqDTO
     * @return
     */
    @PostMapping("/queryClientInfoByCno")
    @ResponseBody
    public JSONResult<Boolean> queryClientInfoByCno(
            @RequestBody HeliClientReqDTO heliClientReqDTO) {
        String cno = heliClientReqDTO.getClientNo();
        if (StringUtils.isBlank(cno)) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        heliClientReqDTO.setClientNo(cno);
        heliClientReqDTO.setOrgId(curLoginUser.getOrgId());
        JSONResult<List<HeliClientRespDTO>> trClientJr =
                heliClientFeignClient.listClientByParams(heliClientReqDTO);
        if (trClientJr == null || !JSONResult.SUCCESS.equals(trClientJr.getCode())) {
            logger.error(
                    "queryClientInfoByCno  heliClientFeignClient.listClientByParams(),param{{}},rs{{}}",
                    trClientJr);
            return new JSONResult<Boolean>().fail(trClientJr.getCode(), trClientJr.getMsg());
        }
        // 默认坐席属于自己
        boolean isBelongToSelf = true;
        List<HeliClientRespDTO> data = trClientJr.getData();
        if (CollectionUtils.isEmpty(data)) {
            isBelongToSelf = false;
        }

        return new JSONResult<Boolean>().success(isBelongToSelf);
    }

    /**
     * 下载合力坐席通话录音
     * 
     * @param url 录音文件地址
     * @return
     */
    @RequestMapping("/downloadHeliClientAudio")
    @ResponseBody
    public ResponseEntity downloadHeliClientAudio(String url) throws Exception {
        HttpHeaders header = new HttpHeaders();
        String decodeUrl = URLDecoder.decode(url, "utf-8");
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(decodeUrl, HttpMethod.GET,
                new HttpEntity<>(header), byte[].class);
        return responseEntity;
    }

    /**
     * 添加坐席
    
     * @return
     */
    @RequiresPermissions("callCenter:heliClient:add")
    @PostMapping("/insertHeliClient")
    @ResponseBody
    public JSONResult insertHeliClient(@Valid @RequestBody HeliClientInsertReq reqDTO, BindingResult result) {
        if (result.hasErrors()) {
            logger.error("insert heli client ,param {{}}", reqDTO);
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());
        return heliClientFeignClient.saveHeliClient(reqDTO);
    }

    /**
     * 修改坐席
     *
     * @return
     */
    @RequiresPermissions("callCenter:heliClient:edit")
    @PostMapping("/updateHeliClient")
    @ResponseBody
    public JSONResult updateHeliClient(@RequestBody HeliClientReq reqDTO) {
        Long id = reqDTO.getId();
        if(null == id){
            return CommonUtil.getParamIllegalJSONResult();
        }
        return heliClientFeignClient.updateHeliClient(reqDTO);
    }

    /**
     * 删除坐席
     * @param idListReq
     * @return
     */
    @RequiresPermissions("callCenter:heliClient:delete")
    @PostMapping("/deleteClientByIdList")
    @ResponseBody
    public JSONResult deleteClientByIdList(@RequestBody IdListReq idListReq){
        List<String> idList = idListReq.getIdList();
        if(CollectionUtils.isEmpty(idList)){
            return CommonUtil.getParamIllegalJSONResult();
        }
        return  heliClientFeignClient.deleteHeliClient(idListReq);
    }

    /**
     * 根据id查询坐席
     *
     * @param idEntity
     * @return
     */
    @PostMapping("/queryById")
    @ResponseBody
    public JSONResult<HeliClientRespDTO> queryById(@RequestBody IdEntity idEntity) {
        return heliClientFeignClient.queryHeliClientById(idEntity);
    }

    /***
     * 上传文件
     * @return
     */
    @RequiresPermissions("callCenter:heliClient:import")
    @PostMapping("/uploadHeliClient")
    @ResponseBody
    public JSONResult uploadHeliClient(@RequestParam("file") MultipartFile file) throws Exception {
        // 获取当前的用户信息
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();

        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        logger.info("userid{{}} heli_client upload size:{{}}", userId, excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),
                SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 1000) {
            logger.error("上传天润坐席,大于1000条，条数{{}}", excelDataList.size());
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),
                "导入数据过多，已超过1000条！");
        }

        // 存放合法的数据
        List<ImportHeliClientDTO> dataList = new ArrayList<ImportHeliClientDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            ImportHeliClientDTO rowDto = new ImportHeliClientDTO();
            if (i == 1) {
                // 记录上传列数
                int rowSize = rowList.size();
                logger.info("upload heli_client,userId{{}},upload rows num{{}}", userId, rowSize);
            }
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String) object;

                if (j == 0) {
                    // 坐席号
                    rowDto.setClientNo(value);
                }else if (j == 1) {
                    //登录名
                    rowDto.setLoginName(value);
                }else if (j == 2) {
                    //秘钥
                    rowDto.setSecret(value);
                }  else if (j == 3) {
                    // 用户账户编号
                    rowDto.setAccount(value);
                } else if (j == 4) {
                    // 合力7x24平台的登录名
                    rowDto.setIntegratedId(value);
                } else if (j == 5) {
                    // appid
                    rowDto.setAppid(value);
                } else if (j == 6) {
                    // 坐席登录方式  sip:软电话 Local:直线  gateway:语音网关
                    rowDto.setExtenType(value);
                } else if (j == 7) {
                    // 电销组
                    rowDto.setOrgName(value);
                }
            }
            dataList.add(rowDto);
        }

        excelDataList = null;

        redisTemplate.opsForValue().set(Constants.HELI_CLIENT_KEY + userId, dataList, 10 * 60,
            TimeUnit.SECONDS);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("num", dataList.size());
        resMap.put("data", dataList);
        return new JSONResult<>().success(resMap);
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("callCenter:heliClient:import")
    @LogRecord(description = "上传合力坐席", operationType = OperationType.IMPORTS,
        menuName = MenuEnum.HELI_CLIENT_MANAGEMENT)
    @ResponseBody
    @PostMapping("/submitHeliClientData")
    public JSONResult<List<ImportHeliClientDTO>> submitHeliClientData() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();
        List<ImportHeliClientDTO> dataList = (List<ImportHeliClientDTO>) redisTemplate.opsForValue()
            .get(Constants.HELI_CLIENT_KEY + userId);
        UploadTrClientDataDTO<ImportHeliClientDTO> reqClientDataDTO =
            new UploadTrClientDataDTO<ImportHeliClientDTO>();
        reqClientDataDTO.setCreateUser(userId);
        reqClientDataDTO.setList(dataList);
        redisTemplate.delete(Constants.HELI_CLIENT_KEY + userId);
        return heliClientFeignClient.uploadHeliClientData(reqClientDataDTO);
    }
}
