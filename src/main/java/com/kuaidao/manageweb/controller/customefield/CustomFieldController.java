package com.kuaidao.manageweb.controller.customefield;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.customfield.CustomFieldAddAndUpdateDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuAddAndUpdateDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuRespDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldRespDTO;
import com.kuaidao.sys.dto.customfield.UserFieldReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;


/**
 * 自定义字段
 * 
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
     * 菜单 首页
     * 
     * @return
     */
    @RequiresPermissions("customfield:view")
    @RequestMapping("/customFieldMenuPage")
    public String customFieldMenuPage() {
        return "customfield/customField";
    }


    /***
     * 自定义字段 首页
     * 
     * @return
     */
    @RequestMapping("/customFieldPage")
    public String customFieldPage(@RequestParam Long id, HttpServletRequest request) {
        IdEntity idEntity = new IdEntity(id + "");
        JSONResult<CustomFieldMenuRespDTO> fieldMenuJR =
                customFieldFeignClient.queryFieldMenuById(idEntity);
        if (fieldMenuJR != null && JSONResult.SUCCESS.equals(fieldMenuJR.getCode())) {
            request.setAttribute("fieldMenu", fieldMenuJR.getData());
        } else {
            logger.error("根据Id 查询自定义字段菜单信息,res{{}}", fieldMenuJR);
        }
        return "customfield/fieldSetting";
    }



    /**
     * 添加自定义字段 -菜单
     * 
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @RequiresPermissions("customfield:add")
    @PostMapping("/saveMenu")
    @ResponseBody
    @LogRecord(description = "添加自定义字段菜单", operationType = OperationType.INSERT,
            menuName = MenuEnum.CUSTOM_FIELD)
    public JSONResult saveMenu(@Valid @RequestBody CustomFieldMenuAddAndUpdateDTO menuDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = menuDTO.getId();
        if (id == null) {
            Subject subject = SecurityUtils.getSubject();
            UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
            menuDTO.setCreateUser(user.getId());
            return customFieldFeignClient.saveMenu(menuDTO);
        }

        return customFieldFeignClient.updateMenu(menuDTO);
    }

    /**
     * 更新自定义字段 -菜单
     * 
     * @param orgDTO
     * @return
     */
    @RequiresPermissions("customfield:edit")
    @PostMapping("/updateMenu")
    @ResponseBody
    @LogRecord(description = "修改自定义字段菜单", operationType = OperationType.UPDATE,
            menuName = MenuEnum.CUSTOM_FIELD)
    public JSONResult updateMenu(@Valid @RequestBody CustomFieldMenuAddAndUpdateDTO menuDTO,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = menuDTO.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return customFieldFeignClient.updateMenu(menuDTO);
    }

    /**
     * 删除自定义字段 -菜单
     * 
     * @param orgDTO
     * @return
     */
    @RequiresPermissions("customfield:delete")
    @PostMapping("/deleteMenu")
    @ResponseBody
    @LogRecord(description = "删除自定义字段菜单", operationType = OperationType.DELETE,
            menuName = MenuEnum.CUSTOM_FIELD)
    public JSONResult deleteMenu(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return customFieldFeignClient.deleteMenu(idListReq);
    }

    /**
     * 查询自定义字段 -菜单
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/listMenuPage")
    @ResponseBody
    public JSONResult<PageBean<CustomFieldMenuRespDTO>> listMenuPage(int pageNum, int pageSize,
            @RequestBody CustomFieldMenuQueryDTO queryDTO) {
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        return customFieldFeignClient.listMenuPage(queryDTO);
    }

    /**
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/isExistsFieldMenu")
    @ResponseBody
    public JSONResult<Boolean> isExistsFieldMenu(@RequestBody CustomFieldMenuQueryDTO queryDTO) {
        return customFieldFeignClient.isExistsFieldMenu(queryDTO);
    }


    /**
     * 根据ID 查询 自定义字段 菜单信息
     * 
     * @param queryDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/queryFieldMenuById")
    @ResponseBody
    public JSONResult<CustomFieldMenuRespDTO> queryFieldMenuById(@RequestBody IdEntity idEntity)
            throws IllegalAccessException, InvocationTargetException {
        return customFieldFeignClient.queryFieldMenuById(idEntity);
    }

    /**
     * 添加自定义字段
     * 
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @RequiresPermissions("customfield:addField")
    @PostMapping("/save")
    @ResponseBody
    @LogRecord(description = "添加自定义字段", operationType = OperationType.INSERT,
            menuName = MenuEnum.CUSTOM_FIELD)
    public JSONResult save(@Valid @RequestBody CustomFieldAddAndUpdateDTO customDTO,
            BindingResult result) throws IllegalAccessException, InvocationTargetException {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = customDTO.getId();
        if (id == null) {
            Subject subject = SecurityUtils.getSubject();
            UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
            customDTO.setCreateUser(user.getId());
            return customFieldFeignClient.save(customDTO);
        }

        return customFieldFeignClient.update(customDTO);
    }

    /**
     * 更新自定义字段
     * 
     * @param orgDTO
     * @return
     */
    @RequiresPermissions("customfield:editField")
    @PostMapping("/update")
    @ResponseBody
    @LogRecord(description = "修改自定义字段", operationType = OperationType.UPDATE,
            menuName = MenuEnum.CUSTOM_FIELD)
    public JSONResult update(@Valid @RequestBody CustomFieldAddAndUpdateDTO customDTO,
            BindingResult result) throws IllegalAccessException, InvocationTargetException {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = customDTO.getId();
        if (id == null) {
            logger.error("update custom field,id is null");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return customFieldFeignClient.update(customDTO);
    }

    /**
     * 删除自定义字段
     * 
     * @param orgDTO
     * @return
     */
    @RequiresPermissions("customfield:delField")
    @PostMapping("/delete")
    @ResponseBody
    @LogRecord(description = "删除自定义字段", operationType = OperationType.INSERT,
            menuName = MenuEnum.CUSTOM_FIELD)
    public JSONResult delete(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        logger.info("delete customfield,id{{}}", idList);
        return customFieldFeignClient.delete(idListReq);
    }

    /**
     * 查询菜单下 自定义字段
     * 
     * @param queryDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/query")
    @ResponseBody
    public JSONResult<CustomFieldRespDTO> query(@RequestBody CustomFieldQueryDTO queryDTO) {
        return customFieldFeignClient.query(queryDTO);
    }


    /**
     * 分页查询 自定义字段列表
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/listCustomFieldPage")
    @ResponseBody
    public JSONResult<PageBean<CustomFieldRespDTO>> listCustomFieldPage(int pageNum, int pageSize,
            @RequestBody CustomFieldQueryDTO queryDTO) {
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        return customFieldFeignClient.listCustomFieldPage(queryDTO);
    }

    /**
     * 上传自定义字段
     * 
     * @param result
     * @return
     */
    @RequiresPermissions("customfield:imField")
    @PostMapping("/uploadCustomField")
    @ResponseBody
    @LogRecord(description = "上传自定义字段", operationType = OperationType.IMPORTS,
            menuName = MenuEnum.CUSTOM_FIELD)
    public JSONResult uploadCustomField(@RequestParam("file") MultipartFile file,
            @RequestParam("id") long menuId) throws Exception {
        // 获取当前的用户信息
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        long userId = user.getId();

        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        logger.info("userid{{}} custom field upload size:{{}}", userId, excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),
                    SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 1000) {
            logger.error("上传自定义字段,大于1000条，条数{{}}", excelDataList.size());
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),
                    "导入数据过多，已超过1000条！");
        }

        // 存放合法的数据
        List<CustomFieldAddAndUpdateDTO> dataList = new ArrayList<CustomFieldAddAndUpdateDTO>();
        // 存放 字段编码 ，只存放最早的 合法的一条
        Set<String> fieldCodeSet = new HashSet<>();

        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            CustomFieldAddAndUpdateDTO rowDto = new CustomFieldAddAndUpdateDTO();
            boolean isValid = true;// 是否验证通过 ，默认 true 通过
            if (i == 1) {
                // 记录上传列数
                int rowSize = rowList.size();
                logger.info("upload custom field,userId{{}},upload rows num{{}}", userId, rowSize);
            }
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String) object;
                if (j == 0) {// 字段编码
                    if (!validFiled(value)) {
                        isValid = false;
                        break;
                    }
                    if (fieldCodeSet.contains(value)) {
                        isValid = false;
                        break;
                    }
                    fieldCodeSet.add(value);
                    rowDto.setFieldCode(value);
                } else if (j == 1) {// 字段名称
                    if (!validFiled(value)) {
                        isValid = false;
                        break;
                    }
                    rowDto.setFieldName(value);
                } else if (j == 2) {// 外显名称
                    if (!validFiled(value)) {
                        isValid = false;
                        break;
                    }
                    rowDto.setDisplayName(value);
                } else if (j == 3) {// 序号
                    if (StringUtils.isBlank(value)) {
                        continue;
                    }
                    if (!StringUtils.isNumeric(value) || value.length() > 5) {
                        isValid = false;
                        break;
                    }
                    rowDto.setSortNum(Integer.parseInt(value));

                } else if (j == 4) {// 宽度
                    if (StringUtils.isBlank(value)) {
                        continue;
                    }
                    if (!StringUtils.isNumeric(value)) {
                        isValid = false;
                        break;
                    }
                    int widthInt = Integer.parseInt(value);
                    if (widthInt <= 0 || widthInt > 1000) {
                        isValid = false;
                        break;
                    }
                    rowDto.setWidth(widthInt);
                }
            } // inner foreach end
            if (isValid) {
                rowDto.setCreateUser(userId);
                rowDto.setFieldType(SysConstant.FieldType.TEXT);
                rowDto.setMenuId(menuId);
                dataList.add(rowDto);
            }

        } // outer foreach end
        logger.info("upload custom filed, valid success num{{}},userId{{}}", dataList.size(),
                userId);
        if (dataList.size() == 0) {
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),
                    SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }

        return customFieldFeignClient.saveBatchCustomField(dataList);

    }

    /**
     * 保存用户与自定义字段关系
     * 
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/saveUserField")
    @ResponseBody
    public JSONResult saveUserField(@Valid @RequestBody UserFieldReq userFieldReq,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        userFieldReq.setUserId(user.getId());
        return customFieldFeignClient.saveUserField(userFieldReq);
    }

    /**
     * 验证上传的数据是否合法
     * 
     * @param field
     * @return
     */
    private boolean validFiled(String field) {
        if (StringUtils.isBlank(field) || field.length() > 50) {
            return false;
        }
        return true;
    }


}
