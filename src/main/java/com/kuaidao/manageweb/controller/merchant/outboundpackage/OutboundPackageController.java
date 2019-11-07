package com.kuaidao.manageweb.controller.merchant.outboundpackage;

import com.kuaidao.account.dto.outboundpackage.OutboundPackageReqDTO;
import com.kuaidao.account.dto.outboundpackage.OutboundPackageRespDTO;
import com.kuaidao.account.dto.outboundpackage.OutboundPackageUpdateDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.outboundpackage.OutboundPackageFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 外呼套餐
 * @author Devin.Chen
 * @Date: 2019/10/7 11:10
 */
@RequestMapping("/merchant/outboundPackage")
@Controller
public class OutboundPackageController {

    private static Logger logger = LoggerFactory.getLogger(OutboundPackageController.class);

    @Autowired
    OutboundPackageFeignClient outboundPackageFeignClient;

    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;


    /**
     * 页面跳转
     * @return
     */
    @GetMapping("/index")
    @RequiresPermissions("merchant:outboundPackage:view")
    public String index(HttpServletRequest request){
        JSONResult<List<DictionaryItemRespDTO>>  dicJr= dictionaryItemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.SUPPLY_COMPANY.getCode());
        request.setAttribute("supplyCompanyList",dicJr.getData());
        return "merchant/outboundPackage/outboundPackagePage";
    }

    /**
     * 分页 查询服务套餐
     * @param reqDTO
     * @return
     */
    @PostMapping("/listOutboundPackagePage")
    @ResponseBody
    @RequiresPermissions("merchant:outboundPackage:view")
    public JSONResult<PageBean<OutboundPackageRespDTO>> listOutboundPackagePage(@RequestBody OutboundPackageReqDTO reqDTO){
        return outboundPackageFeignClient.listOutboundPackagePage(reqDTO);
    }


    /**
     * 添加服务套餐
     * @param updateDTO
     * @param result
     * @return
     */
    @PostMapping("/addOutboundPackage")
    @ResponseBody
    @LogRecord(operationType = LogRecord.OperationType.INSERT, description = "添加服务费用套餐",
            menuName = MenuEnum.OUTBOUND_PACKAGE)
    @RequiresPermissions("merchant:outboundPackage:add")
    public JSONResult addOutboundPackage(@Valid  @RequestBody OutboundPackageUpdateDTO updateDTO, HttpSession session, BindingResult result){
        if(result.hasErrors()){
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        updateDTO.setCreateUser(curLoginUser.getId());
        return outboundPackageFeignClient.addOrUpdate(updateDTO);
    }

    /**
     * 添加服务套餐
     * @param updateDTO
     * @param result
     * @return
     */
    @PostMapping("/updateOutboundPackage")
    @ResponseBody
    @RequiresPermissions("merchant:outboundPackage:edit")
    @LogRecord(operationType = LogRecord.OperationType.UPDATE, description = "更新服务费用套餐",
            menuName = MenuEnum.OUTBOUND_PACKAGE)
    public JSONResult updateOutboundPackage(@Valid  @RequestBody OutboundPackageUpdateDTO updateDTO, HttpSession session, BindingResult result){
        if(result.hasErrors()){
            return CommonUtil.validateParam(result);
        }
        Long id = updateDTO.getId();
        if(id==null){
            logger.warn("update outbound package is is null");
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        updateDTO.setUpdateUser(curLoginUser.getId());
        return outboundPackageFeignClient.addOrUpdate(updateDTO);
    }


    /**
     * 删除服务套餐
     * @return
     */
    @PostMapping("/deleteOutboundPackage")
    @ResponseBody
    @LogRecord(operationType = LogRecord.OperationType.DELETE, description = "删除服务费用套餐",
            menuName = MenuEnum.OUTBOUND_PACKAGE)
    @RequiresPermissions("merchant:outboundPackage:delete")
    public JSONResult deleteOutboundPackage(@RequestBody  IdListLongReq idListLongReq){
        List<Long> idList = idListLongReq.getIdList();
        if(CollectionUtils.isEmpty(idList)){
            logger.warn("delete outbound package ,id list is null");
            return CommonUtil.getParamIllegalJSONResult();
        }
        return outboundPackageFeignClient.delete(idListLongReq);

    }

    /**
     * 根据ID查询 服务套餐信息
     * @param idEntity
     * @return
     */
    @PostMapping("/queryOutboundPackageById")
    @ResponseBody
    public JSONResult<OutboundPackageRespDTO> queryOutboundPackageById(@RequestBody  IdEntityLong idEntity){
        Long id = idEntity.getId();
        if(id == null){
            return CommonUtil.getParamIllegalJSONResult();
        }
        return outboundPackageFeignClient.queryOutboundPackageById(idEntity);
    }
}
