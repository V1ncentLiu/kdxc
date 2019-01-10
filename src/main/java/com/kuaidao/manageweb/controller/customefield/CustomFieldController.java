package com.kuaidao.manageweb.controller.customefield;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.sys.constant.SysConstant;
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
        return "customfield/customField";
    }


    /***
     *  自定义字段 首页
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
     *  添加自定义字段 -菜单
     * @param orgDTO
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
    @PostMapping("/saveOrUpdateMenu")
    @ResponseBody
    public JSONResult saveMenu(@Valid @RequestBody CustomFieldMenuAddAndUpdateDTO menuDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = menuDTO.getId();
        if (id == null) {
            // TODO devin
            menuDTO.setCreateUser(1111L);
            return customFieldFeignClient.saveMenu(menuDTO);
        }

        return customFieldFeignClient.updateMenu(menuDTO);
    }

    /**
     *   更新自定义字段 -菜单
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateMenu")
    @ResponseBody
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
     *   删除自定义字段 -菜单
     * @param orgDTO
     * @return
     */
    @PostMapping("/deleteMenu")
    @ResponseBody
    public JSONResult deleteMenu(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return customFieldFeignClient.deleteMenu(idListReq);
    }

    /**
     *   查询自定义字段 -菜单
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
     *  添加自定义字段
     * @param orgDTO
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    public JSONResult save(@Valid @RequestBody CustomFieldAddAndUpdateDTO customDTO,
            BindingResult result) throws IllegalAccessException, InvocationTargetException {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = customDTO.getId();
        if (id == null) {
            // TODO devin
            customDTO.setCreateUser(1111L);
            return customFieldFeignClient.save(customDTO);
        }

        return customFieldFeignClient.update(customDTO);
    }

    /**
     *   更新自定义字段
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
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
     *   删除自定义字段
     * @param orgDTO
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
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
     *   查询菜单下 自定义字段
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
     * 下载导入文件模板
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(HttpServletRequest request)
            throws IOException {
        // 获取文件路径
        File filePath = ResourceUtils.getFile(
                ResourceUtils.CLASSPATH_URL_PREFIX + "excel-templates/custom-field-template.xlsx");

        FileSystemResource file = new FileSystemResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        String fileName  =new String("自定义字段导入模板.xlsx".getBytes(),"iso-8859-1");
        headers.add("Content-Disposition",
                String.format("attachment; filename=\"%s\"",fileName));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok().headers(headers).contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }

    /**
     * 上传自定义字段
     * @param result
     * @return
     */
    @PostMapping("/uploadCustomField")
    @ResponseBody
    public JSONResult uploadCustomField(@RequestParam("file") MultipartFile file,@RequestParam("id") long menuId) throws Exception {
        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        logger.info("upload size:{{}}" , excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return  new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 1000) {
            logger.error("上传自定义字段,大于1000条，条数{{}}", excelDataList.size());
            return  new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),"导入数据过多，已超过1000条！");
        }

        //存放合法的数据
        List<CustomFieldAddAndUpdateDTO> dataList = new ArrayList<CustomFieldAddAndUpdateDTO>();

        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            CustomFieldAddAndUpdateDTO rowDto = new CustomFieldAddAndUpdateDTO();
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String)object;
                if(j==0) {//字段编码
                    if(!validFiled(value)) {
                        break;
                    }
                    rowDto.setFieldCode(value);
                }else if(j==1) {//字段名称
                    if(!validFiled(value)) {
                        break;
                    }
                    rowDto.setFieldName(value);
                }else if(j==2) {//外显名称
                    if(!validFiled(value)) {
                        break;
                    }
                    rowDto.setDisplayName(value);
                }else if(j==3) {//序号
                    if(!StringUtils.isNumeric(value) || value.length()>5) {
                        break;
                    }
                    rowDto.setSortNum(Integer.parseInt(value));
                    
                }else if(j==4){//宽度
                    if(!StringUtils.isNumeric(value)) {
                        break;
                    }
                    int widthInt = Integer.parseInt(value);
                    if(widthInt<0 || widthInt>1000) {
                        break;
                    }
                    rowDto.setWidth(widthInt);
                }
            }//inner foreach end
            
            //TODO  devin
            rowDto.setCreateUser(111L);
            rowDto.setFieldType(SysConstant.FieldType.TEXT);
            rowDto.setMenuId(menuId);
            dataList.add(rowDto);
        }//outer foreach end
        logger.info("upload custom filed, valid success num{{}}",dataList.size());
        customFieldFeignClient.saveBatchCustomField(dataList);

        return new JSONResult<>().success(true);
    }
    
    /**
     * 验证上传的数据是否合法
     * @param field
     * @return
     */
    private boolean validFiled(String field) {
        if(StringUtils.isBlank(field) || field.length()>50) {
            return false;
        }
        return true;
    }


}
