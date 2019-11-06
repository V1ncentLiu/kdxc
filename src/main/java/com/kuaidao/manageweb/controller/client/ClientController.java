package com.kuaidao.manageweb.controller.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.*;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.kuaidao.aggregation.dto.client.AddOrUpdateQimoClientDTO;
import com.kuaidao.aggregation.dto.client.AddOrUpdateTrClientDTO;
import com.kuaidao.aggregation.dto.client.ClientLoginReCordDTO;
import com.kuaidao.aggregation.dto.client.ImportQimoClientDTO;
import com.kuaidao.aggregation.dto.client.ImportTrClientDTO;
import com.kuaidao.aggregation.dto.client.QimoClientQueryDTO;
import com.kuaidao.aggregation.dto.client.QimoClientRespDTO;
import com.kuaidao.aggregation.dto.client.QimoDataRespDTO;
import com.kuaidao.aggregation.dto.client.QimoLoginReqDTO;
import com.kuaidao.aggregation.dto.client.QueryQimoDTO;
import com.kuaidao.aggregation.dto.client.QueryTrClientDTO;
import com.kuaidao.aggregation.dto.client.TrClientDataRespDTO;
import com.kuaidao.aggregation.dto.client.TrClientQueryDTO;
import com.kuaidao.aggregation.dto.client.TrClientRespDTO;
import com.kuaidao.aggregation.dto.client.UploadTrClientDataDTO;
import com.kuaidao.aggregation.dto.client.UserCnoReqDTO;
import com.kuaidao.aggregation.dto.client.UserCnoRespDTO;
import com.kuaidao.callcenter.dto.QimoOutboundCallDTO;
import com.kuaidao.callcenter.dto.QimoOutboundCallRespDTO;
import com.kuaidao.callcenter.dto.TrAxbOutCallReqDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RedisConstant;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.netflix.hystrix.contrib.javanica.utils.CommonUtils;

/**
 * 坐席管理 controller 
 * @author  Chen
 * @date: 2019年1月22日 下午2:30:05   
 * @version V1.0
 */
@Controller
@RequestMapping("/client/client")
public class ClientController {
    private static Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    ClientFeignClient clientFeignClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    /**
     *  跳转天润坐席页面
     * @return
     */
    @RequiresPermissions("aggregation:trClient:view")
    @RequestMapping("/trClientIndex")
    public String trClientIndex(HttpServletRequest request) {
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
                logger.error("跳转天润坐席时，查询组织机构列表报错,res{{}}", orgListJr);
            } else {
                request.setAttribute("orgList", orgListJr.getData());
            }
        }
        return "client/trClientPage";
    }


    /**
     *  跳转七陌坐席页面
     * @return
     */
    @RequiresPermissions("aggregation:qimoClient:view")
    @RequestMapping("/qimoClientIndex")
    public String qimoClientIndex(HttpServletRequest request) {
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
                logger.error("跳转七陌坐席时，查询组织机构列表报错,res{{}}", orgListJr);
            } else {
                request.setAttribute("orgList", orgListJr.getData());
            }
        }
        return "client/qimoClientPage";
    }

    /**
     *  添加天润坐席
     * @param reqDTO
     * @param result
     * @return
     */
    @RequiresPermissions("aggregation:trClient:add")
    @PostMapping("/saveTrClient")
    @ResponseBody
    @LogRecord(operationType = OperationType.INSERT, description = "添加天润坐席",
            menuName = MenuEnum.TR_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> saveTrClient(@Valid @RequestBody AddOrUpdateTrClientDTO reqDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());
        return clientFeignClient.saveTrClient(reqDTO);
    }

    /**
     * 更新天润坐席
     * @param reqDTO
     * @param result
     * @return
     */
    @RequiresPermissions("aggregation:trClient:edit")
    @PostMapping("/updateTrClient")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "修改天润坐席",
            menuName = MenuEnum.TR_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> updateTrClient(@Valid @RequestBody AddOrUpdateTrClientDTO reqDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = reqDTO.getId();
        if (id == null) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return clientFeignClient.updateTrClient(reqDTO);
    }


    /**
     * 删除天润坐席 ，根据ID list 
     * @param idListReq
     * @return
     */
    @RequiresPermissions("aggregation:trClient:delete")
    @PostMapping("/deleteTrClient")
    @ResponseBody
    @LogRecord(operationType = OperationType.DELETE, description = "删除天润坐席",
            menuName = MenuEnum.TR_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> deleteTrClient(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        logger.info("userId{{}},delete tian run client,idList{{}}", curLoginUser.getId(),
                idListReq);

        return clientFeignClient.deleteTrClient(idListReq);
    }


    /**
     * 根据Id 查询天润坐席
     * @param idEntity
     * @return
     */
    @PostMapping("/queryTrClientById")
    @ResponseBody
    public JSONResult<TrClientRespDTO> queryTrClient(@RequestBody IdEntity idEntity)
            throws Exception {
        String id = idEntity.getId();
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            return new JSONResult<TrClientRespDTO>().fail(
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return clientFeignClient.queryTrClientById(idEntity);
    }


    /**
     *   根据参数查询数据 精确匹配
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryTrClientByParam")
    @ResponseBody
    public JSONResult<TrClientRespDTO> queryTrClientByParam(@RequestBody TrClientQueryDTO queryDTO)
            throws Exception {

        return clientFeignClient.queryTrClientByParam(queryDTO);
    }


    /**
    * 查看坐席号是否存在
    * @param queryDTO
    * @return
    */
    @PostMapping("/isExistsClient")
    @ResponseBody
    public JSONResult<Boolean> isExistsClient(@RequestBody TrClientQueryDTO queryDTO) {
        JSONResult<TrClientRespDTO> trClientJr = clientFeignClient.queryTrClientByParam(queryDTO);
        // false 不存在
        boolean isExists = true;
        if (trClientJr != null && JSONResult.SUCCESS.equals(trClientJr.getCode())) {
            TrClientRespDTO data = trClientJr.getData();
            if (data == null) {
                isExists = false;
            }

        }
        return new JSONResult<Boolean>().success(isExists);
    }


    /**
     * 分页查询天润坐席
     * @param queryClientDTO
     * @return
     */
    @RequiresPermissions("aggregation:trClient:view")
    @PostMapping("/listTrClientPage")
    @ResponseBody
    public JSONResult<PageBean<TrClientDataRespDTO>> listTrClientPage(
            @RequestBody QueryTrClientDTO queryClientDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        Long orgId = curLoginUser.getOrgId();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            queryClientDTO.setOrgId(orgId);
        }
        return clientFeignClient.listTrClientPage(queryClientDTO);
    }

    /**
     * 更新天润回呼设置
     * @param reqDTO
     * @param result
     * @return
     */
    @PostMapping("/updateCallbackPhone")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "修改天润坐席",
            menuName = MenuEnum.TR_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> updateCallbackPhone(@RequestBody AddOrUpdateTrClientDTO reqDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = reqDTO.getId();
        if (id == null) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return clientFeignClient.updateCallbackPhone(reqDTO);
    }


    /***
     * 上传文件
     * @param result
     * @return
     */
    @RequiresPermissions("aggregation:trClient:import")
    @PostMapping("/uploadTrClient")
    @ResponseBody
    public JSONResult uploadTrClient(@RequestParam("file") MultipartFile file) throws Exception {
        // 获取当前的用户信息
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();

        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        logger.info("userid{{}} tr_client upload size:{{}}", userId, excelDataList.size());

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
        List<ImportTrClientDTO> dataList = new ArrayList<ImportTrClientDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            ImportTrClientDTO rowDto = new ImportTrClientDTO();
            if (i == 1) {
                // 记录上传列数
                int rowSize = rowList.size();
                logger.info("upload tr_client,userId{{}},upload rows num{{}}", userId, rowSize);
            }
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String) object;
                
                if (j == 0) {
                 // 坐席号
                    rowDto.setClientNo(value);
                }else if (j == 1) {
                    //号码所属公司
                    rowDto.setNumberAttributionCompany(value);
                }else if (j == 2) {
                    //坐席归属地
                    rowDto.setAttribution(value);
                }  else if (j == 3) {
                    // 电销组
                    rowDto.setOrgName(value);
                } else if (j == 4) {
                    // 绑定电话
                    rowDto.setBindPhone(value);
                } else if (j == 5) {
                    // 回显号
                    rowDto.setDisplayPhone(value);
                } else if (j == 6) {
                    // 回呼号码
                    rowDto.setCallbackPhone(value);
                }
            }
            dataList.add(rowDto);
        }

        excelDataList = null;

        redisTemplate.opsForValue().set(Constants.TR_CLIENT_KEY + userId, dataList, 10 * 60,
                TimeUnit.SECONDS);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("num", dataList.size());
        resMap.put("data", dataList);
        return new JSONResult<>().success(resMap);
    }

    /**
     * 
     * @param result
     * @return
     */
    @RequiresPermissions("aggregation:trClient:import")
    @LogRecord(description = "上传天润坐席", operationType = OperationType.IMPORTS,
            menuName = MenuEnum.TR_CLIENT_MANAGEMENT)
    @ResponseBody
    @PostMapping("/submitTrClientData")
    public JSONResult<List<ImportTrClientDTO>> submitTrClientData() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();
        List<ImportTrClientDTO> dataList = (List<ImportTrClientDTO>) redisTemplate.opsForValue()
                .get(Constants.TR_CLIENT_KEY + userId);
        UploadTrClientDataDTO<ImportTrClientDTO> reqClientDataDTO =
                new UploadTrClientDataDTO<ImportTrClientDTO>();
        reqClientDataDTO.setCreateUser(userId);
        reqClientDataDTO.setList(dataList);
        redisTemplate.delete(Constants.TR_CLIENT_KEY + userId);
        return clientFeignClient.uploadTrClientData(reqClientDataDTO);
    }



    // ****以下是七陌的坐席方法 ****

    /**
     *  添加天润坐席
     * @param reqDTO
     * @param result
     * @return
     */
    @RequiresPermissions("aggregation:qimoClient:add")
    @PostMapping("/saveQimoClient")
    @ResponseBody
    @LogRecord(operationType = OperationType.INSERT, description = "保存七陌坐席",
            menuName = MenuEnum.QIMO_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> saveQimoClient(@Valid @RequestBody AddOrUpdateQimoClientDTO reqDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());
        Long userId = reqDTO.getUserId();
        if(userId!=null){
            IdEntityLong idEntityLong = new IdEntityLong();
            idEntityLong.setId(userId);
            JSONResult<UserInfoDTO> userInfoJr = userInfoFeignClient.get(idEntityLong);
            if(!JSONResult.SUCCESS.equals(userInfoJr.getCode())){
                return new JSONResult<Boolean>().fail(userInfoJr.getCode(),userInfoJr.getMsg());
            }
            UserInfoDTO userInfo = userInfoJr.getData();
            reqDTO.setOrgId(userInfo.getOrgId());

        }

        return clientFeignClient.saveQimoClient(reqDTO);
    }

    /**
     * 更新七陌坐席
     * @param reqDTO
     * @param result
     * @return
     */
    @RequiresPermissions("aggregation:qimoClient:edit")
    @PostMapping("/updateQimoClient")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "修改七陌坐席",
            menuName = MenuEnum.QIMO_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> updateQimoClient(@Valid @RequestBody AddOrUpdateQimoClientDTO reqDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = reqDTO.getId();
        if (id == null) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        Long userId = reqDTO.getUserId();
        if(userId!=null){
            IdEntityLong idEntityLong = new IdEntityLong();
            idEntityLong.setId(userId);
            JSONResult<UserInfoDTO> userInfoJr = userInfoFeignClient.get(idEntityLong);
            if(!JSONResult.SUCCESS.equals(userInfoJr.getCode())){
                return new JSONResult<Boolean>().fail(userInfoJr.getCode(),userInfoJr.getMsg());
            }
            UserInfoDTO userInfo = userInfoJr.getData();
            reqDTO.setOrgId(userInfo.getOrgId());

        }


        return clientFeignClient.updateQimoClient(reqDTO);
    }


    /**
     * 删除七陌坐席 ，根据ID list 
     * @param idListReq
     * @return
     */
    @RequiresPermissions("aggregation:qimoClient:delete")
    @PostMapping("/deleteQimoClient")
    @ResponseBody
    @LogRecord(operationType = OperationType.DELETE, description = "删除七陌坐席",
            menuName = MenuEnum.QIMO_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> deleteQimoClient(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        logger.info("userId{{}},delete qimo client,idList{{}}", curLoginUser.getId(), idListReq);

        return clientFeignClient.deleteQimoClient(idListReq);
    }


    /**
     * 根据Id 查询七陌坐席
     * @param idEntity
     * @return
     */
    @PostMapping("/queryQimoClientById")
    @ResponseBody
    public JSONResult<QimoClientRespDTO> queryQimoClientById(@RequestBody IdEntity idEntity)
            throws Exception {
        String id = idEntity.getId();
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            return new JSONResult<QimoClientRespDTO>().fail(
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        JSONResult<QimoClientRespDTO> qimoClientRespDTOJSONResult = clientFeignClient.queryQimoClientById(idEntity);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
                if(JSONResult.SUCCESS.equals(qimoClientRespDTOJSONResult.getCode()) && qimoClientRespDTOJSONResult.getData()!=null){
                    QimoClientRespDTO qimoClient = qimoClientRespDTOJSONResult.getData();
                    qimoClient.setIsDxzj(true);
                }
        }
        return qimoClientRespDTOJSONResult;
    }


    /**
     *   根据参数查询数据 精确匹配
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryQimoClientByParam")
    @ResponseBody
    public JSONResult<QimoClientRespDTO> queryQimoClientByParam(
            @RequestBody QimoClientQueryDTO queryDTO) throws Exception {

        return clientFeignClient.queryQimoClientByParam(queryDTO);
    }


    /**
     * 分页查询七陌坐席
     * @param queryClientDTO
     * @return
     */
    @RequiresPermissions("aggregation:qimoClient:view")
    @PostMapping("/listQimoClientPage")
    @ResponseBody
    public JSONResult<PageBean<QimoDataRespDTO>> listQimoClientPage(
            @RequestBody QueryQimoDTO queryClientDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        Long orgId = curLoginUser.getOrgId();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            queryClientDTO.setOrgId(orgId);
        }
        return clientFeignClient.listQimoClientPage(queryClientDTO);
    }


    /***
     * 上传文件
     * @param result
     * @return
     */
    @RequiresPermissions("aggregation:qimoClient:import")
    @PostMapping("/uploadQimoClient")
    @ResponseBody
    public JSONResult uploadQimoClient(@RequestParam("file") MultipartFile file) throws Exception {
        // 获取当前的用户信息
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();

        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        logger.info("userid{{}} qimo_client upload size:{{}}", userId, excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),
                    SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 5000) {
            logger.error("上传七陌坐席,大于5000条，条数{{}}", excelDataList.size());
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),
                    "导入数据过多，已超过5000条！");
        }

        // 存放合法的数据
        List<ImportQimoClientDTO> dataList = new ArrayList<ImportQimoClientDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            ImportQimoClientDTO rowDto = new ImportQimoClientDTO();
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String) object;
                if (j == 0) {
                    rowDto.setName(value);
                } else if (j == 1) {
                    // 登录坐席
                    rowDto.setLoginClient(value);
                } else if (j == 2) {
                    // 坐席编号
                    rowDto.setClientNo(value);
                }else if (j == 3) {
                    // 号码所属公司
                    rowDto.setNumberAttributionCompany(value);
                } else if (j == 4) {
                    // 坐席归属地
                    rowDto.setAttribution(value);
                } else if (j == 5) {
                    // 账号编号
                    rowDto.setAccountNo(value);
                } else if (j == 6) {
                    // 秘钥
                    rowDto.setSecretKey(value);
                } else if (j == 7) {
                    rowDto.setPhone1(value);
                } else if (j == 8) {
                    rowDto.setPhone2(value);
                } else if (j == 9) {
                    rowDto.setProxyurl(value);
                }
            }
            dataList.add(rowDto);
        }

        excelDataList = null;

        redisTemplate.opsForValue().set(Constants.QIMO_CLIENT_KEY + userId, dataList, 10 * 60,
                TimeUnit.SECONDS);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("num", dataList.size());
        resMap.put("data", dataList);
        return new JSONResult<>().success(resMap);
    }


    /**
     * 
     * @param result
     * @return
     */
    @RequiresPermissions("aggregation:qimoClient:import")
    @LogRecord(description = "上传七陌坐席", operationType = OperationType.IMPORTS,
            menuName = MenuEnum.QIMO_CLIENT_MANAGEMENT)
    @ResponseBody
    @PostMapping("/submitQimoClientData")
    public JSONResult<List<ImportQimoClientDTO>> submitQimoClientData() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();
        List<ImportQimoClientDTO> dataList = (List<ImportQimoClientDTO>) redisTemplate.opsForValue()
                .get(Constants.QIMO_CLIENT_KEY + userId);
        UploadTrClientDataDTO<ImportQimoClientDTO> reqClientDataDTO =
                new UploadTrClientDataDTO<ImportQimoClientDTO>();
        reqClientDataDTO.setCreateUser(userId);
        reqClientDataDTO.setList(dataList);
        // remove redis 临时数据
        redisTemplate.delete(Constants.QIMO_CLIENT_KEY + userId);
        JSONResult<List<ImportQimoClientDTO>> uploadTrClientData =
                clientFeignClient.uploadQimoClientData(reqClientDataDTO);
        return uploadTrClientData;
    }

    /**
     *  天润坐席登录
     * @param result
     * @return
     */
    @GetMapping("/login/{cno}")
    @ResponseBody
    public JSONResult login(@PathVariable String cno) {
        SecurityUtils.getSubject().getSession().setAttribute("axb", cno);
        return new JSONResult<>().success(true);
    }

    
    /**
     * 登录天润坐席前 操作
     * @param cno
     * @return
     */
    @RequestMapping(value = "/destroy/{cno}", method = { RequestMethod.POST })
    @ResponseBody
    public JSONResult<Boolean> destroy(@PathVariable("cno") String cno) {
        SecurityUtils.getSubject().getSession().removeAttribute("axb");
        SecurityUtils.getSubject().getSession().removeAttribute("bindType");
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        if (null != curLoginUser) {
            UserCnoReqDTO userCno = new UserCnoReqDTO();
            userCno.setCno(cno);
            userCno.setOrgId(curLoginUser.getOrgId());
            userCno.setUserId(curLoginUser.getId());
             JSONResult<UserCnoRespDTO> userCnoJr = clientFeignClient.queryUserCnoByCnoAndOrgId(userCno);
             if(JSONResult.SUCCESS.equals(userCnoJr.getCode())) {
                 if(userCnoJr.getData()!=null) {
                     JSONResult<Boolean> updateUserCnoByCnoAndOrgId = clientFeignClient.updateUserCnoByCnoAndOrgId(userCno);
                 }else {
                     JSONResult<Boolean> saveUserCno = clientFeignClient.saveUserCno(userCno);
                 }
             }else {
                 logger.error("destroy cno,queryUserCnoByCnoAndOrgId,res{{}}",userCnoJr);
            }
        }
        return new JSONResult<Boolean>().success(true);
    }
    
    
    /**
     *  七陌坐席登录
     * @param result
     * @return
     */
    @PostMapping("/qimoLogin")
    @ResponseBody
    @LogRecord(description = "七陌坐席", operationType = OperationType.CLIENT_LOGIN,
            menuName = MenuEnum.QIMO_CLIENT_MANAGEMENT)
    public JSONResult qimoLogin(@RequestBody QimoLoginReqDTO reqDTO ) {
        String loginName = reqDTO.getLoginName();
        String bindType = reqDTO.getBindType();
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long accountId = curLoginUser.getId();
       //JSONResult<QimoClientRespDTO> qimoClientJr = clientFeignClient.queryQimoClientByLoginClient(loginName);
        TrAxbOutCallReqDTO trAxbOutCallReqDTO = new TrAxbOutCallReqDTO();
        trAxbOutCallReqDTO.setLoginClient(loginName);
        trAxbOutCallReqDTO.setAccountId(accountId);
        JSONResult<QimoClientRespDTO> qimoClientJr = clientFeignClient.queryQimoClient(trAxbOutCallReqDTO);
       
        if(JSONResult.SUCCESS.equals(qimoClientJr.getCode())) {
            QimoClientRespDTO qimoClient = qimoClientJr.getData();
            if(qimoClient==null) {
                return new JSONResult<>().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"该坐席号不属于您");
            }else {
                Session session = SecurityUtils.getSubject().getSession();
                session.setAttribute("loginName", loginName);
                if("2".equals(bindType)) {
                    session.setAttribute("bindType", bindType);
                }
                ClientLoginReCordDTO clientLoginRecord = new ClientLoginReCordDTO();
                clientLoginRecord.setAccountId(curLoginUser.getId());
                clientLoginRecord.setAccountType(reqDTO.getAccountType());
                clientLoginRecord.setOrgId(curLoginUser.getOrgId());
                clientLoginRecord.setCno(qimoClient.getClientNo());
                clientLoginRecord.setClientType(reqDTO.getClientType());
                clientLoginRecord.setAccountNo(qimoClient.getAccountNo());
                JSONResult<Boolean> loginRecordJr = clientFeignClient.clientLoginRecord(clientLoginRecord);
                if(!JSONResult.SUCCESS.equals(loginRecordJr.getCode())) {
                    logger.error("qimo_login_put_redis,param{{}},res{{}}",clientLoginRecord,loginRecordJr);
                    return  new JSONResult<>().fail(loginRecordJr.getCode(),loginRecordJr.getMsg());
                }
                return new JSONResult<>().success(true);
            }
        }else {
            logger.error("七陌坐席登录，通过登录查询,param{{}},res{{}}",trAxbOutCallReqDTO,qimoClientJr);
        }
        return new JSONResult<>().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
    }
    
    /**
     * 坐席登录记录
     * @return
     */
    @PostMapping("/clientLoginRecord")
    @ResponseBody
    public JSONResult<Boolean> clientLoginRecord(@Valid @RequestBody ClientLoginReCordDTO clientLoginRecord,BindingResult result){
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        clientLoginRecord.setAccountId(curLoginUser.getId());
        clientLoginRecord.setAccountType(clientLoginRecord.getAccountType());
        clientLoginRecord.setOrgId(curLoginUser.getOrgId());
        return clientFeignClient.clientLoginRecord(clientLoginRecord);
        
    }
    
    /**
     * 七陌坐席退出
     * @param request
     * @return
     */
    @PostMapping("/qimoLogout")
    @ResponseBody
    @LogRecord(description = "七陌坐席退出登录", operationType = OperationType.CLIENT_LOGOUT,
        menuName = MenuEnum.QIMO_CLIENT_MANAGEMENT)
    public JSONResult loginout(HttpServletRequest request) {
        Session session = SecurityUtils.getSubject().getSession();
        if(StringUtils.isBlank((String)session.getAttribute("loginName"))) {
            return  new JSONResult<>().fail(SysErrorCodeEnum.ERR_UNLOGINCLIENT_FAIL.getCode(),SysErrorCodeEnum.ERR_UNLOGINCLIENT_FAIL.getMessage());
        }
        session.removeAttribute("loginName");
        session.removeAttribute("axb");
        session.removeAttribute("bindType");
        
        return new JSONResult<>().success(true);
    }
    
    /**
     * 七陌 外呼 
     * @param callDTO
     * @return
     */
    @PostMapping("/qimoOutboundCall")
    @ResponseBody
    @LogRecord(description = "七陌外呼", operationType = OperationType.OUTBOUNDCALL,
        menuName = MenuEnum.QIMO_CLIENT_MANAGEMENT)
    public JSONResult<QimoOutboundCallRespDTO> qimoOutboundCall(@RequestBody QimoOutboundCallDTO  callDTO){
        String customerPhoneNumber = callDTO.getCustomerPhoneNumber();
        if(StringUtils.isBlank(customerPhoneNumber)) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),"客户手机号为null");
        }
        
        Session session = SecurityUtils.getSubject().getSession();
       callDTO.setBindType((String)session.getAttribute("bindType"));
       callDTO.setLoginClient((String)session.getAttribute("loginName"));
        
        return clientFeignClient.qimoOutboundCall(callDTO);
    }
    
    /**
     * 天润axb 外呼
     * @return
     */
    @PostMapping("/axbOutCall")
    @ResponseBody
    @LogRecord(description = "天润axb外呼", operationType = OperationType.OUTBOUNDCALL,
        menuName = MenuEnum.TR_CLIENT_MANAGEMENT)
    public JSONResult<String> axbOutCall(@RequestBody TrAxbOutCallReqDTO trAxbOutCallReqDTO){
        Session session = SecurityUtils.getSubject().getSession();
        String  cno = (String)session.getAttribute("axb");
        trAxbOutCallReqDTO.setCno(cno);
        UserInfoDTO user = (UserInfoDTO) session.getAttribute("user");
        trAxbOutCallReqDTO.setAccountId(user.getId());
        trAxbOutCallReqDTO.setOrgId(user.getOrgId());
        
        return clientFeignClient.axbOutCall(trAxbOutCallReqDTO);
    }
    
    
    /**
     * 根据坐席号查询坐席的信息，判断坐席号是否属于当前用户
     * @param trClientQueryDTO  坐席号
     * @return
     */
    @PostMapping("/queryClientInfoByCno")
    @ResponseBody
   public JSONResult<Boolean> queryClientInfoByCno(@RequestBody TrClientQueryDTO trClientQueryDTO,HttpServletRequest request) {
        String cno =  trClientQueryDTO.getClientNo();
        if(StringUtils.isBlank(cno)) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        TrClientQueryDTO queryDTO = new TrClientQueryDTO();
        queryDTO.setClientNo(cno);
        queryDTO.setOrgId(curLoginUser.getOrgId());
        JSONResult<TrClientRespDTO>  trClientJr = clientFeignClient.queryTrClientByParam(queryDTO);
        if(trClientJr==null || !JSONResult.SUCCESS.equals(trClientJr.getCode())) {
            return new JSONResult<Boolean>().fail(trClientJr.getCode(),trClientJr.getMsg());
        }
        //默认坐席属于自己
        boolean isBelongToSelf = true;
        TrClientRespDTO data = trClientJr.getData();
        if (data==null) {
            isBelongToSelf = false;
        }
        
        return new JSONResult<Boolean>().success(isBelongToSelf);
    }
    
    /**
     * 天润坐席退出
    * @param trAxbOutCallReqDTO
    * @return
     */
    @PostMapping("/trClientLogout")
    @ResponseBody
    @LogRecord(description = "天润坐席退出", operationType = OperationType.CLIENT_LOGOUT,
    menuName = MenuEnum.TR_CLIENT_MANAGEMENT)
    public JSONResult trClientLogout(@RequestBody TrAxbOutCallReqDTO trAxbOutCallReqDTO) {
        String cno = trAxbOutCallReqDTO.getCno();
        if(StringUtils.isBlank(cno)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
            
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        trAxbOutCallReqDTO.setOrgId(curLoginUser.getOrgId());
        return clientFeignClient.trClientLogout(trAxbOutCallReqDTO);
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
