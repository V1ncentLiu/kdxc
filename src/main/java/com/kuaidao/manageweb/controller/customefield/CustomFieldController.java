package com.kuaidao.manageweb.controller.customefield;

import java.lang.reflect.InvocationTargetException;
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
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.sys.dto.customfield.CustomFieldAddAndUpdateDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuAddAndUpdateDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuRespDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldRespDTO;


/**
 *  自定义字段 
 * @author: Chen Chengxue
 * @date: 2018年12月28日 下午1:45:12   
 * @version V1.0
 */
@Controller
@RequestMapping("/customfield/customField")
public class CustomFieldController {
    private static Logger logger = LoggerFactory.getLogger(CustomFieldController.class);
    @Autowired
    CustomFieldFeignClient customFieldFeignClient;
    
    
    /***
     *   菜单  首页
     * @return
     */
    @RequestMapping("/customFieldMenuPage")
    public String customFieldMenuPage() {
        return "customfield/customFieldMenuPage";
    }
    
    
    /***
     *  自定义字段 首页
     * @return
     */
    @RequestMapping("/customFieldPage")
    public String customFieldPage() {
        return "customfield/customFieldPage";
    }
    
    
    
    /**
     *  添加自定义字段 -菜单
     * @param orgDTO
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
    @PostMapping("/saveMenu")
    @ResponseBody
    public JSONResult saveMenu(@Valid @RequestBody CustomFieldMenuAddAndUpdateDTO menuDTO,BindingResult result)
            throws IllegalAccessException, InvocationTargetException {
        if(result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
       
        return customFieldFeignClient.saveMenu(menuDTO);
    }

    /**
     *   更新自定义字段 -菜单
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateMenu")
    @ResponseBody
    public JSONResult updateMenu(@Valid @RequestBody CustomFieldMenuAddAndUpdateDTO menuDTO,BindingResult result) {
        
        if(result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        
        Long id =  menuDTO.getId();
        if(id==null) {
           return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        
        return customFieldFeignClient.updateMenu(menuDTO);
    }

    /**
     *   删除自定义字段 -菜单
     * @param orgDTO
     * @return
     */
    @PostMapping("/deleteMenu")
    @ResponseBody
    public JSONResult deleteMenu(@RequestBody IdEntity idEntity) {
        String idStr = idEntity.getId();
        if(StringUtils.isNotBlank(idStr)) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return customFieldFeignClient.deleteMenu(idEntity);
    }

    /**
     *   查询自定义字段 -菜单
     * @param queryDTO
     * @return
     */
    @PostMapping("/listMenuPage")
    @ResponseBody
    public JSONResult<PageBean<CustomFieldMenuRespDTO>> listMenuPage(@RequestBody CustomFieldMenuQueryDTO queryDTO) {
        return customFieldFeignClient.listMenuPage(queryDTO);
    }

    /**
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryMenuByParam")
    @ResponseBody
    public JSONResult<CustomFieldMenuRespDTO> queryMenuByParam(@RequestBody CustomFieldMenuQueryDTO queryDTO) {
      
        return null;
    }


    /**
     * 根据ID 查询 自定义字段 菜单信息
     * @param queryDTO
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
    @PostMapping("/queryFieldMenuById")
    @ResponseBody
    public JSONResult<CustomFieldMenuRespDTO> queryFieldMenuById(@RequestBody IdEntity idEntity) throws IllegalAccessException, InvocationTargetException {
       return customFieldFeignClient.queryFieldMenuById(idEntity);
    }

    /**
     *  添加自定义字段
     * @param orgDTO
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
    @PostMapping("/save")
    @ResponseBody
    public JSONResult save(@Valid @RequestBody CustomFieldAddAndUpdateDTO customDTO,BindingResult result) throws IllegalAccessException, InvocationTargetException {
        if(result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
       return customFieldFeignClient.save(customDTO);
    }

    /**
     *   更新自定义字段
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public JSONResult update(@Valid @RequestBody CustomFieldAddAndUpdateDTO customDTO,BindingResult result)throws IllegalAccessException, InvocationTargetException {
        if(result.hasErrors()) {
            return  CommonUtil.validateParam(result);
        }
        Long id = customDTO.getId();
        if(id==null) {
            logger.error("update custom field,id is null");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

       return customFieldFeignClient.update(customDTO);
    }

    /**
     *   删除自定义字段
     * @param orgDTO
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public JSONResult delete(@RequestBody IdEntity idEntity) {
        String idStr = idEntity.getId();
        if(StringUtils.isNotBlank(idStr)) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage()); 
        }
        long id = Long.parseLong(idStr);
        logger.info("delete customfield,id{{}}",id);
        return customFieldFeignClient.delete(idEntity);
    }

    /**
     *   查询菜单下 自定义字段
     * @param queryDTO
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
    @PostMapping("/query")
    @ResponseBody
    public JSONResult<CustomFieldRespDTO> query(@RequestBody CustomFieldQueryDTO queryDTO){
      return customFieldFeignClient.query(queryDTO);
    }
    
    
    /**
     * 分页查询 自定义字段列表
     * @param queryDTO
     * @return
     */
    @PostMapping("/listCustomFieldPage")
    @ResponseBody
    public JSONResult<PageBean<CustomFieldRespDTO>> listCustomFieldPage(@RequestBody CustomFieldQueryDTO queryDTO) {
        return customFieldFeignClient.listCustomFieldPage(queryDTO);
    }

}
