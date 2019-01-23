package com.kuaidao.manageweb.controller.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.kuaidao.aggregation.dto.client.AddOrUpdateQimoClientDTO;
import com.kuaidao.aggregation.dto.client.AddOrUpdateTrClientDTO;
import com.kuaidao.aggregation.dto.client.ImportTrClientDTO;
import com.kuaidao.aggregation.dto.client.QimoClientQueryDTO;
import com.kuaidao.aggregation.dto.client.QimoClientRespDTO;
import com.kuaidao.aggregation.dto.client.QimoDataRespDTO;
import com.kuaidao.aggregation.dto.client.QueryQimoDTO;
import com.kuaidao.aggregation.dto.client.QueryTrClientDTO;
import com.kuaidao.aggregation.dto.client.TrClientDataRespDTO;
import com.kuaidao.aggregation.dto.client.TrClientQueryDTO;
import com.kuaidao.aggregation.dto.client.TrClientRespDTO;
import com.kuaidao.aggregation.dto.client.UploadTrClientDataDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;

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

    /**
     *  跳转天润坐席页面
     * @return
     */
    @RequestMapping("/trClientIndex")
    public String trClientIndex(HttpServletRequest request) {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        JSONResult<List<OrganizationRespDTO>>  orgListJr = organizationFeignClient.queryOrgByParam(queryDTO);
        if(orgListJr==null || !JSONResult.SUCCESS.equals(orgListJr.getCode())) {
            logger.error("跳转天润坐席时，查询组织机构列表报错,res{{}}",orgListJr);
        }else {
            request.setAttribute("orgList",orgListJr.getData());
        }
        return "client/trClientPage";
    }

    
    /**
     *  跳转七陌坐席页面
     * @return
     */
    @RequestMapping("/qimoClientIndex")
    public String qimoClientIndex() {
        return "client/qimoClientPage";
    }
    /**
     *  添加天润坐席
     * @param reqDTO
     * @param result
     * @return
     */
    @PostMapping("/saveTrClient")
    @ResponseBody
    @LogRecord(operationType=OperationType.INSERT,description="添加天润坐席",menuName=MenuEnum.TR_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> saveTrClient(@Valid @RequestBody AddOrUpdateTrClientDTO reqDTO,
            BindingResult result)  throws Exception{
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        //TODO devin
     /*   UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());
        */
        reqDTO.setCreateUser(111L);
        return clientFeignClient.saveTrClient(reqDTO);
    }

    /**
     * 更新天润坐席
     * @param reqDTO
     * @param result
     * @return
     */
    @PostMapping("/updateTrClient")
    @ResponseBody
    @LogRecord(operationType=OperationType.UPDATE,description="修改天润坐席",menuName=MenuEnum.TR_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> updateTrClient(@Valid @RequestBody AddOrUpdateTrClientDTO reqDTO,
            BindingResult result) throws Exception{
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = reqDTO.getId();
        if (id == null) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
      /*  UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());*/
        
        return clientFeignClient.updateTrClient(reqDTO);
    }


    /**
     * 删除天润坐席 ，根据ID list 
     * @param idListReq
     * @return
     */
    @PostMapping("/deleteTrClient")
    @ResponseBody
    @LogRecord(operationType=OperationType.DELETE,description="删除天润坐席",menuName=MenuEnum.TR_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> deleteTrClient(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        } 
        //TODO devin 
      /*  UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        logger.info("userId{{}},delete tian run client,idList{{}}",curLoginUser.getId(),idListReq);*/
        
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
        //false 不存在
        boolean  isExists = true;
        if(trClientJr!=null &&  JSONResult.SUCCESS.equals(trClientJr.getCode())) {
            TrClientRespDTO data = trClientJr.getData();
            if(data==null) {
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
    @PostMapping("/listTrClientPage")
    @ResponseBody
    public JSONResult<PageBean<TrClientDataRespDTO>> listTrClientPage(
            @RequestBody QueryTrClientDTO queryClientDTO) {

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
    @LogRecord(operationType=OperationType.UPDATE,description="修改天润坐席",menuName=MenuEnum.TR_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> updateCallbackPhone( @RequestBody AddOrUpdateTrClientDTO reqDTO,
            BindingResult result) throws Exception{
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
    @PostMapping("/uploadTrClient")
    @ResponseBody
    public JSONResult uploadTrClient(@RequestParam("file") MultipartFile file) throws Exception {
        //获取当前的用户信息
       //TODO devin
        long userId = 111L;
        
        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        logger.info("userid{{}} tr_client upload size:{{}}" ,userId, excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return  new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 1000) {
            logger.error("上传天润坐席,大于1000条，条数{{}}", excelDataList.size());
            return  new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),"导入数据过多，已超过1000条！");
        }
        
        //存放合法的数据
        List<ImportTrClientDTO> dataList = new ArrayList<ImportTrClientDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            ImportTrClientDTO rowDto = new ImportTrClientDTO();
            if(i==1) {
                //记录上传列数
                int rowSize = rowList.size();
                logger.info("upload custom field,userId{{}},upload rows num{{}}",userId,rowSize);  
            }
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String)object;
                if(j==0) {//坐席号
                    rowDto.setClientNo(value);
                }else if(j==1) {//点小组
                   rowDto.setOrgName(value);
                }else if(j==2) {//绑定电话
                   rowDto.setBindPhone(value);
                }else if(j==3) {//回显号
                    rowDto.setDisplayPhone(value);
                }else if(j==4){//回呼号码
                    rowDto.setCallbackPhone(value);
                }
            }
            dataList.add(rowDto);
          }
        
        excelDataList = null;
        
        //TODO devin 临时 key
        redisTemplate.opsForValue().set("HUIJU:client:temp_tr_client"+userId, dataList,10*60,TimeUnit.SECONDS);
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
    @LogRecord(description="上传坐席",operationType=OperationType.IMPORTS,menuName=MenuEnum.TR_CLIENT_MANAGEMENT)
    @ResponseBody
    @PostMapping("/submitTrClientData")
    public JSONResult<List<ImportTrClientDTO>> submitTrClientData() {
        //TODO deivn userID
        long userId = 111L;
        List<ImportTrClientDTO> dataList = (List<ImportTrClientDTO>)redisTemplate.opsForValue().get("HUIJU:client:temp_tr_client"+userId);
        UploadTrClientDataDTO reqClientDataDTO = new UploadTrClientDataDTO();
        reqClientDataDTO.setCreateUser(userId);
        reqClientDataDTO.setList(dataList);
        return clientFeignClient.uploadTrClientData(reqClientDataDTO);
    }
    
    
    
    // ****以下是七陌的坐席方法 ****

    /**
     *  添加天润坐席
     * @param reqDTO
     * @param result
     * @return
     */
    @PostMapping("/saveQimoClient")
    @ResponseBody
    @LogRecord(operationType=OperationType.INSERT,description="保存七陌坐席",menuName=MenuEnum.QIMO_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> saveQimoClient(@Valid @RequestBody AddOrUpdateQimoClientDTO reqDTO,
            BindingResult result)  throws Exception{
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        //TODO devin
   /*     UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());*/
        reqDTO.setCreateUser(111L);
        return clientFeignClient.saveQimoClient(reqDTO);
    }

    /**
     * 更新七陌坐席
     * @param reqDTO
     * @param result
     * @return
     */
    @PostMapping("/updateQimoClient")
    @ResponseBody
    @LogRecord(operationType=OperationType.UPDATE,description="修改七陌坐席",menuName=MenuEnum.QIMO_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> updateQimoClient(@Valid @RequestBody AddOrUpdateQimoClientDTO reqDTO,
            BindingResult result) throws Exception{
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = reqDTO.getId();
        if (id == null) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        
     
        return clientFeignClient.updateQimoClient(reqDTO);
    }

    
    /**
     * 删除七陌坐席 ，根据ID list 
     * @param idListReq
     * @return
     */
    @PostMapping("/deleteQimoClient")
    @ResponseBody
    @LogRecord(operationType=OperationType.DELETE,description="删除七陌坐席",menuName=MenuEnum.QIMO_CLIENT_MANAGEMENT)
    public JSONResult<Boolean> deleteQimoClient(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        //TODO devin
        /*UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        logger.info("userId{{}},delete qimo client,idList{{}}",curLoginUser.getId(),idListReq);*/
        
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
        return clientFeignClient.queryQimoClientById(idEntity);
    }


    /**
     *   根据参数查询数据 精确匹配
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryQimoClientByParam")
    @ResponseBody
    public JSONResult<QimoClientRespDTO> queryQimoClientByParam(@RequestBody QimoClientQueryDTO queryDTO)
            throws Exception {
       
        return clientFeignClient.queryQimoClientByParam(queryDTO);
    }


    /**
     * 分页查询七陌坐席
     * @param queryClientDTO
     * @return
     */
    @PostMapping("/listQimoClientPage")
    @ResponseBody
    public JSONResult<PageBean<QimoDataRespDTO>> listQimoClientPage(
            @RequestBody QueryQimoDTO queryClientDTO) {

        return clientFeignClient.listQimoClientPage(queryClientDTO);
    }


}
