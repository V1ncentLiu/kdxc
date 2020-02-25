package com.kuaidao.manageweb.controller.deptcallset;

import com.kuaidao.callcenter.dto.deptcallset.DeptCallSetAddAndUpdateDTO;
import com.kuaidao.callcenter.dto.deptcallset.DeptCallSetQueryDTO;
import com.kuaidao.callcenter.dto.deptcallset.DeptCallSetRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.deptcallset.DeptCallSetFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author  yangbiao
 * @Date: 2019/1/2 15:14
 * @Description:
 *      部门呼叫设置
 */

@Controller
@RequestMapping("/deptcallset")
public class DeptCallSetController {

    private static Logger logger = LoggerFactory.getLogger(DeptCallSetController.class);


    @Autowired
    DeptCallSetFeignClient deptCallSetFeignClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    /**
     * 获取所有组织机构
     * @return
     */
    @RequestMapping("/allOrgs")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> allOrgs(){
        OrganizationQueryDTO dto = new OrganizationQueryDTO();
        dto.setSystemCode(SystemCodeConstant.HUI_JU);
        return organizationFeignClient.queryOrgByParam(dto);
    }

    /**
     * 获取组织结构所有叶子节点
     * @return
     */
    @RequestMapping("/listLeafOrg")
    @ResponseBody
    public JSONResult<List<OrganizationDTO>> listLeafOrg(){
        OrganizationQueryDTO dto = new OrganizationQueryDTO();
        dto.setSystemCode(SystemCodeConstant.HUI_JU);
        return organizationFeignClient.listLeafOrg(dto);
    }

    /**
     * 组织机构名称与ID做映射
     * @return
     */
    private Map<String,Long> orgNameToId(){
        Map<String,Long> map = new HashMap();
        JSONResult<List<OrganizationRespDTO>> orgsRes = allOrgs();
        if(JSONResult.SUCCESS.equals(orgsRes.getCode())) {
            List<OrganizationRespDTO> dataList = orgsRes.getData();
            for (OrganizationRespDTO dto : dataList) {
                map.put(dto.getName(), dto.getId());
            }
        }
        return map;
    }

    /**
     * 组织机构ID 和 名称 做映射
     * @return
     */
    private Map<Long,String> orgIdToName(){
        Map<Long,String> map = new HashMap();
        JSONResult<List<OrganizationRespDTO>> orgsRes = allOrgs();
        if(JSONResult.SUCCESS.equals(orgsRes.getCode())){
            List<OrganizationRespDTO> dataList = orgsRes.getData();
            for(OrganizationRespDTO dto : dataList){
                map.put(dto.getId(),dto.getName());
            }
        }
        return map;
    }


    /**
     * 跳转部门呼叫设置首页
     * @return
     */
    @RequestMapping("/deptcallsetPage")
    public String pageIndex(){
        return "deptCellSet/deptCallSetList";
    }

    /**
     * 新增
     * @param dto
     * @param result
     * @return
     */
    @RequiresPermissions("DeptCallSet:add")
    @LogRecord(description = "部门呼叫设置-新增",operationType = LogRecord.OperationType.INSERT,menuName = MenuEnum.DEPTCALLSET_MANAGENT)
    @RequestMapping("/saveOne")
    @ResponseBody
    public JSONResult insertOne(@Valid @RequestBody DeptCallSetAddAndUpdateDTO dto , BindingResult result){
        if (result.hasErrors()){
            return  CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setCreateTime(new Date());
        dto.setCreateUser(user.getId());
        return deptCallSetFeignClient.saveDeptCallSet(dto);
    }

    /**
     * 更新
     * @param dto
     * @param result
     * @return
     */
    @RequiresPermissions("DeptCallSet:edit")
    @LogRecord(description = "部门呼叫设置-更新",operationType = LogRecord.OperationType.UPDATE,menuName = MenuEnum.DEPTCALLSET_MANAGENT)
    @RequestMapping("/updateDeptcallset")
    @ResponseBody
    public JSONResult updateDeptcallset(@Valid @RequestBody DeptCallSetAddAndUpdateDTO dto , BindingResult result){

        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(user.getId());
        if("".equals(dto.getDeptNo())){
            dto.setDeptNo(null);
        }
        if(dto.getOrgId()!=null&&dto.getOrgId()==0L){
            dto.setOrgId(null);
        }
        return deptCallSetFeignClient.updateDeptCallSets(dto);
    }

    /**
     * 更新
     * @param dto
     * @return
     */
    @RequiresPermissions("DeptCallSet:edit")
    @LogRecord(description = "部门呼叫设置-更新",operationType = LogRecord.OperationType.UPDATE,menuName = MenuEnum.DEPTCALLSET_MANAGENT)
    @RequestMapping("/updateDeptcallsetForNotNull")
    @ResponseBody
    public JSONResult updateDeptCallSetsForNotNull(@RequestBody DeptCallSetAddAndUpdateDTO dto){
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(user.getId());
        return deptCallSetFeignClient.updateDeptCallSetsForNotNull(dto);
    }

    /**
     * 分页查询
     * @param dto
     * @return
     */
    @RequiresPermissions("DeptCallSet:view")
    @PostMapping("/queryDeptcallset")
    @ResponseBody
    public JSONResult<PageBean<DeptCallSetRespDTO>> queryDeptcallset(@RequestBody DeptCallSetQueryDTO dto){
        JSONResult<PageBean<DeptCallSetRespDTO>> list = deptCallSetFeignClient.queryDeptCallSetList(dto);
        if(JSONResult.SUCCESS.equals(list.getCode())){
            List<DeptCallSetRespDTO> data = list.getData().getData();
            Map<Long, String> orgMap = orgIdToName();
            List<DeptCallSetRespDTO> resList = new ArrayList();
            for(int i = 0 ; i<data.size() ; i++ ){
                DeptCallSetRespDTO tempDto = data.get(i);
                tempDto.setOrgName(orgMap.get(tempDto.getOrgId()));
                resList.add(tempDto);
            }
            list.getData().setData(resList);
        }
        return  list;
    }


    /**
     * 查询明细
     * @param idEntity
     * @return
     */
    @PostMapping("/queryOne")
    @ResponseBody
    public JSONResult<DeptCallSetRespDTO> queryOne(@RequestBody  IdEntity idEntity){
        JSONResult<DeptCallSetRespDTO> one = deptCallSetFeignClient.queryOne(idEntity);
        DeptCallSetRespDTO data = one.getData();
        data.setOrgName(orgIdToName().get(data.getOrgId()));
        one.setData(data);
        return one;
    }


    /**
     * 导入方法
     * @param file
     * @param importFlag
     * @return
     * @throws Exception
     */
    @RequiresPermissions("DeptCallSet:import")
    @LogRecord(description = "部门呼叫设置-批量导入",operationType = LogRecord.OperationType.IMPORTS,menuName = MenuEnum.DEPTCALLSET_MANAGENT)
    @PostMapping("/import")
    @ResponseBody
    public JSONResult uploadDeptCallSet(@RequestParam("file") MultipartFile file, @RequestParam("importFlag") String importFlag) throws Exception {
        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        logger.info("DeptCallSet upload size:{{}}", excelDataList.size()-1);
        if (excelDataList == null || excelDataList.size() == 1) {
            return  new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 1001) {
            logger.error("上传自定义字段,大于1000条，条数{{}}", excelDataList.size());
            return  new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),"导入数据过多，已超过1000条！");
        }
//      如果则直接返回值
        if("show".equals(importFlag)){
            return showImportDatasList(excelDataList);
        }else{
            return importDatasList(excelDataList);
        }
    }

    /**
     * 插入合格数据，
     *     返回不合格的数据
     * @param excelDataList
     * @return
     */
    private JSONResult importDatasList(List<List<Object>> excelDataList){

//      进行检验。然后进行数据插入。
        //存放合法的数据
        Map<String, Long> orgMap = orgNameToId();
        List<DeptCallSetAddAndUpdateDTO> dataList = new ArrayList<DeptCallSetAddAndUpdateDTO>();
        List<DeptCallSetAddAndUpdateDTO> errList = new ArrayList<DeptCallSetAddAndUpdateDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            DeptCallSetAddAndUpdateDTO rowDto = new DeptCallSetAddAndUpdateDTO();
            List<Object> rowList = excelDataList.get(i);
            boolean dataFalg = true;
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String)object;
//              添加针对每行的数据检验
                if(j==0) {// 部门呼叫编码
                    if(!validFiled(value)) { dataFalg = false; }
                    rowDto.setDeptNo(value);
                }else if(j==1) {//呼叫中心编号
                    if(!validFiled(value)) {  dataFalg = false; }
                    rowDto.setCallCenterNo(value);
                }else if(j==2) {//token
                    if(!validFiled(value)) {  dataFalg = false; }
                    rowDto.setToken(value);
                }else if(j==3) {//企业用户名
                    if(!validFiled(value)) { dataFalg = false; }
                    rowDto.setCompanyUser(value);
                }else if(j==4){//企业用户名密码
                    if(!validFiled(value)) {  dataFalg = false; }
                    rowDto.setCompanyPass(value);
                }else if(j==5){//部门用户
                    if(!validFiled(value)) {  dataFalg = false; }
                    rowDto.setDeptUser(value);
                }else if(j==6){//部门用户密码
                    if(!validFiled(value)) {  dataFalg = false; }
                    rowDto.setDeptPass(value);
                }else if(j==7){//所在部门（组织）
                    if(!validFiled(value)) {  dataFalg = false; }
                    Long aLong = orgMap.get(value);
                    if(aLong==null){  dataFalg = false; }
                    rowDto.setOrgId(aLong);
                    rowDto.setOrgName(value);
                }
            }

            if(dataFalg){
                UserInfoDTO user = CommUtil.getCurLoginUser();
                rowDto.setId(IdUtil.getUUID());
                rowDto.setCreateUser(user.getId());
                Date date = new Date(System.currentTimeMillis()+i*-1000);
                rowDto.setCreateTime(date);
                dataList.add(rowDto);
            }else{
                errList.add(rowDto);
            }
        }
//      数据导入，使用insert ignore 进行批量插入，如果数据已经存在则，则忽略（判断标准为主键以及唯一索引）
        JSONResult result = deptCallSetFeignClient.importDeptCallSets(dataList);
//      查询那些东西没有被插入
        if(JSONResult.SUCCESS.equals(result.getCode())){
            if(result.getData()!=null){
                errList.addAll((List<DeptCallSetAddAndUpdateDTO>)result.getData());
            }
        }
//      导入失败的东西
        if(errList.size()>0){
            return new JSONResult().success(errList);
        }
        return new JSONResult().success(true);
    }

    /**
     * 转换数据格式，用于导入前的数据预览
     * @param excelDataList
     * @return
     */
    private JSONResult showImportDatasList(List<List<Object>> excelDataList){
        List<DeptCallSetRespDTO> showList = new ArrayList<DeptCallSetRespDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            DeptCallSetRespDTO showRowDto = new DeptCallSetRespDTO();
            List<Object> rowList = excelDataList.get(i);
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String)object;
                if(j==0) {// 部门呼叫编码
                    showRowDto.setDeptNo(value);
                }else if(j==1) {//呼叫中心编号
                    showRowDto.setCallCenterNo(value);
                }else if(j==2) {//token
                    showRowDto.setToken(value);
                }else if(j==3) {//企业用户名
                    showRowDto.setCompanyUser(value);
                }else if(j==4){//企业用户名密码
                    showRowDto.setCompanyPass(value);
                }else if(j==5){//部门用户
                    showRowDto.setDeptUser(value);
                }else if(j==6){//部门用户密码
                    showRowDto.setDeptPass(value);
                }else if(j==7){//所在部门（组织）
                    showRowDto.setOrgName(value);
                }
            }
            showList.add(showRowDto);
        }
        return new JSONResult().success(showList);
    }

    /**
     * 检验方法
     * @param field
     * @return
     */
    private boolean validFiled(String field) {
        if(StringUtils.isBlank(field) || field.length()>50) {
            return false;
        }
        return true;
    }

    /**
     * 模板下载
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(HttpServletRequest request) {
        // 获取文件路径
        logger.info("===进入方法==");
        HttpHeaders headers = new HttpHeaders();
        FileSystemResource file = null;
        InputStream inputStream = null;
        long length = 0L;
        logger.info("===进入模板下载==");
        try{
            File filePath = ResourceUtils.getFile(
                    ResourceUtils.CLASSPATH_URL_PREFIX + "excel-templates/dept-call-setting.xlsx");
            logger.info("==模板路径==");
            logger.info(filePath.getAbsolutePath());
            logger.info(filePath.getPath());
            file = new FileSystemResource(filePath);

            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            String fileName  =new String("部门呼叫设置批量导入模板.xlsx".getBytes(),"iso-8859-1");
            headers.add("Content-Disposition",
                    String.format("attachment; filename=\"%s\"",fileName));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            length = file.contentLength();
            inputStream = file.getInputStream();
        }catch (IOException e){
            logger.info("===进入异常捕获==");
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.info("===退出异常捕获==");
        }

        return ResponseEntity.ok().headers(headers).contentLength(length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(inputStream));
    }

}
