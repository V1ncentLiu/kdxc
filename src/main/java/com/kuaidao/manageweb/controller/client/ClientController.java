package com.kuaidao.manageweb.controller.client;

import java.util.List;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.client.AddOrUpdateQimoClientDTO;
import com.kuaidao.aggregation.dto.client.AddOrUpdateTrClientDTO;
import com.kuaidao.aggregation.dto.client.QimoClientQueryDTO;
import com.kuaidao.aggregation.dto.client.QimoClientRespDTO;
import com.kuaidao.aggregation.dto.client.QimoDataRespDTO;
import com.kuaidao.aggregation.dto.client.QueryQimoDTO;
import com.kuaidao.aggregation.dto.client.QueryTrClientDTO;
import com.kuaidao.aggregation.dto.client.TrClientDataRespDTO;
import com.kuaidao.aggregation.dto.client.TrClientQueryDTO;
import com.kuaidao.aggregation.dto.client.TrClientRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * 坐席管理 controller 
 * @author: Chen
 * @date: 2019年1月22日 下午2:30:05   
 * @version V1.0
 */
@Controller
@RequestMapping("/client/client")
public class ClientController {
    private static Logger logger = LoggerFactory.getLogger(ClientController.class);
    
    @Autowired
    ClientFeignClient clientFeignClient;

    

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
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());
        
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
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        logger.info("userId{{}},delete tian run client,idList{{}}",curLoginUser.getId(),idListReq);
        
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
        
        return clientFeignClient.queryTrClient(idEntity);
    }


    /**
     *   根据参数查询数据 精确匹配
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryTrClientByParam")
    public JSONResult<TrClientRespDTO> queryTrClientByParam(@RequestBody TrClientQueryDTO queryDTO)
            throws Exception {
       
        return clientFeignClient.queryTrClientByParam(queryDTO);
    }


    /**
     * 分页查询天润坐席
     * @param queryClientDTO
     * @return
     */
    @PostMapping("/listTrClientPage")
    public JSONResult<PageBean<TrClientDataRespDTO>> listTrClientPage(
            @RequestBody QueryTrClientDTO queryClientDTO) {

        return clientFeignClient.listTrClientPage(queryClientDTO);
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
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());
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
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        logger.info("userId{{}},delete qimo client,idList{{}}",curLoginUser.getId(),idListReq);
        
        return clientFeignClient.deleteQimoClient(idListReq);
    }
    
    
    /**
     * 根据Id 查询七陌坐席
     * @param idEntity
     * @return
     */
    @PostMapping("/queryQimoClientById")
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
    public JSONResult<PageBean<QimoDataRespDTO>> listQimoClientPage(
            @RequestBody QueryQimoDTO queryClientDTO) {

        return clientFeignClient.listQimoClientPage(queryClientDTO);
    }


}
