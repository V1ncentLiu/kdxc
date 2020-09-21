package com.kuaidao.manageweb.controller.im;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.kuaidao.common.constant.*;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.custservice.dto.custservice.CustomerInfoDTO;
import com.kuaidao.custservice.dto.onlineleave.SaleMonitorDTO;
import com.kuaidao.custservice.dto.onlineleave.SaleOnlineLeaveLogReq;
import com.kuaidao.custservice.dto.onlineleave.TSaleMonitorReq;
import com.kuaidao.im.dto.MessageRecordData;
import com.kuaidao.im.dto.MessageRecordExportSearchReq;
import com.kuaidao.im.dto.MessageRecordPageReq;
import com.kuaidao.im.util.JSONPageResult;
import com.kuaidao.manageweb.feign.im.CustomerInfoFeignClient;
import com.kuaidao.manageweb.feign.im.ImFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/message/im/")
public class ImMessageController {

    @Resource
    private ImFeignClient imFeignClient;

    @Resource
    private CustomerInfoFeignClient customerInfoFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    /**
     * 聊天记录跳转
     */
    @RequiresPermissions("message:record:view")
    @GetMapping("/chatRecordIndex")
    public String chatRecordIndex( HttpServletRequest request ){
        List dxzList = getCommonDxzList();
        request.setAttribute("dzList", dxzList);
        return "im/imChattingRecords";
    }

    @RequiresPermissions("message:monitor:view")
    @GetMapping("/saleMonitorIndex")
    public String saleMonitorIndex( HttpServletRequest request ){
        List dxzList = getCommonDxzList();
        request.setAttribute("dzList", dxzList);
        return "im/imManagement";
    }

    private List getCommonDxzList() {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        List dxzList = new ArrayList(0);
        // 管理员
        if(roleList != null && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())){
            if (CollectionUtils.isNotEmpty(roleList) && roleList.get(0) != null) {
                OrganizationQueryDTO dto = new OrganizationQueryDTO();
                dto.setSystemCode(SystemCodeConstant.HUI_JU);
                dto.setOrgType(OrgTypeConstant.DXZ);
                dto.setBusinessLine(user.getBusinessLine());
                JSONResult<List<OrganizationRespDTO>> dzList = organizationFeignClient.queryOrgByParam(dto);
                dxzList = dzList.getData();
            }
            return dxzList;
        }else{
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(user.getOrgId());
            organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId = organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            dxzList = listDescenDantByParentId.getData();
            // 自己组织加入
            OrganizationDTO myOrganizationDTO = new OrganizationDTO();
            myOrganizationDTO.setId(user.getOrgId());
            myOrganizationDTO.setName(user.getName());
            if(CollectionUtils.isEmpty(dxzList)){
                dxzList = new ArrayList(1);
            }
            dxzList.add(myOrganizationDTO);
            return dxzList;
        }
    }

    /**
     * 聊天记录历史分页
     * @param messageRecordPageReq
     */
    @SuppressWarnings("all")
    @PostMapping("/getChatRecordPage")
    public @ResponseBody JSONPageResult<List<MessageRecordData>> getChatRecordPage(@RequestBody MessageRecordPageReq messageRecordPageReq ){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if(StringUtils.isBlank(messageRecordPageReq.getTeleSaleId())){
            if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
                //电销总监
                List<UserInfoDTO> userList = getTeleSaleByOrgId(orgId);
                if(CollectionUtils.isEmpty(userList)) {
                    JSONPageResult jsonPageResult = new JSONPageResult();
                    jsonPageResult.setPageSize(messageRecordPageReq.getPageSize());
                    jsonPageResult.setPageNum(messageRecordPageReq.getPageNum());
                    return new JSONPageResult().success(new ArrayList<>());
                }
                List<Long> idList = userList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user->user.getId()).collect(Collectors.toList());
                idList.add(curLoginUser.getId());// 当前登陆人Id
                // 封装多个电销顾问ID
                messageRecordPageReq.setTeleSaleId(Joiner.on(",").join(idList));

            }else if (CollectionUtils.isNotEmpty(roleList) && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
                // 管理员

            }else {
                List<UserInfoDTO>  userInfoList  = getTeleSaleByOrgId(curLoginUser.getOrgId());
                if (CollectionUtils.isEmpty(userInfoList)) {
                    JSONPageResult jsonPageResult = new JSONPageResult();
                    jsonPageResult.setPageSize(messageRecordPageReq.getPageSize());
                    jsonPageResult.setPageNum(messageRecordPageReq.getPageNum());
                    return new JSONPageResult().success(new ArrayList<>());
                }
                List<Long> idList = userInfoList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user->user.getId()).collect(Collectors.toList());
                messageRecordPageReq.setTeleSaleId(Joiner.on(",").join(idList));
            }
        }
        JSONPageResult<List<MessageRecordData>> chatRecordPage = imFeignClient.getChatRecordPage(messageRecordPageReq);
        if(null != chatRecordPage){
            transChatRecord(chatRecordPage.getData(),false);
        }
        return chatRecordPage;
    }

    /**
     * 客户聊天记录
     * @param messageRecordPageReq
     * @return
     */
    @PostMapping("/listChatRecord")
    public @ResponseBody JSONPageResult<List<MessageRecordData>> listChatRecord(@Valid @RequestBody MessageRecordPageReq messageRecordPageReq , BindingResult result ){
        if (result.hasErrors()) {
            return validateParam(result);
        }
        // 封装客户Id
        Map<String,Object> map = new HashMap<>();
        map.put("clueId",messageRecordPageReq.getCusId());
        JSONResult<List<CustomerInfoDTO>> listJSONResult = customerInfoFeignClient.getCustomerInfoListByClueId(map);
        if(null == listJSONResult || !"0".equals(listJSONResult.getCode())){
            return new JSONPageResult().fail(SysErrorCodeEnum.ERR_AUTH_LIMIT.getCode(),SysErrorCodeEnum.ERR_AUTH_LIMIT.getMessage());
        }
        List<CustomerInfoDTO> data ;
        if(CollectionUtils.isEmpty(data = listJSONResult.getData())){
            // 直接返回
            JSONPageResult jsonPageResult = new JSONPageResult();
            jsonPageResult.setPageSize(messageRecordPageReq.getPageSize());
            jsonPageResult.setPageNum(messageRecordPageReq.getPageNum());
            return new JSONPageResult().success(new ArrayList<>());
        }
        CustomerInfoDTO customerInfoDTO = data.get(0);
        // 客户Im无值?
        if(StringUtils.isBlank(customerInfoDTO.getImId())){
            return new JSONPageResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        // 设置accId
        messageRecordPageReq.setAccId(customerInfoDTO.getImId());
        JSONPageResult<List<MessageRecordData>> listJSONPageResult = imFeignClient.listChatRecord(messageRecordPageReq);
        if(null != listJSONPageResult){
            transChatRecord(listJSONPageResult.getData(),false);
        }
        return listJSONPageResult;
    }

    /**
     * 导出聊天记录
     * @param messageRecordExportSearchReq
     * @param response
     * @throws Exception
     */
    @PostMapping("/export")
    public void export(@RequestBody MessageRecordExportSearchReq messageRecordExportSearchReq , HttpServletResponse response){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if(StringUtils.isBlank(messageRecordExportSearchReq.getTeleSaleId())){
            if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
                //电销总监
                List<UserInfoDTO> userList = getTeleSaleByOrgId(orgId);
                if(CollectionUtils.isEmpty(userList)) {
                    messageRecordExportSearchReq.setTeleSaleId("-1");
                }else{
                    List<Long> idList = userList.parallelStream()
                            .filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user->user.getId()).collect(Collectors.toList());
                    idList.add(curLoginUser.getId());
                    // 封装多个电销顾问ID
                    messageRecordExportSearchReq.setTeleSaleId(Joiner.on(",").join(idList));
                }
            }else if (CollectionUtils.isNotEmpty(roleList) && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {

                // 管理员
            }else {
                List<UserInfoDTO>  userInfoList  = getTeleSaleByOrgId(curLoginUser.getOrgId());
                if (CollectionUtils.isEmpty(userInfoList)) {
                    messageRecordExportSearchReq.setTeleSaleId("-1");
                }else{
                    List<Long> idList = userInfoList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user->user.getId()).collect(Collectors.toList());
                    messageRecordExportSearchReq.setTeleSaleId(Joiner.on(",").join(idList));
                }
            }
        }
        // 获得历史聊天记录
        try {
            JSONPageResult<List<MessageRecordData>> chatRecordList = imFeignClient.getChatRecordList(messageRecordExportSearchReq);
            List<List<Object>> dataList = new ArrayList<>();
            dataList.add(getHeadTitleList());
            if(null != chatRecordList && JSONResult.SUCCESS.equals(chatRecordList.getCode()) && chatRecordList.getData() != null && chatRecordList.getData().size() != 0) {
                List<MessageRecordData> resultList = chatRecordList.getData();
                transChatRecord(resultList , true );
                int size = resultList.size();
                for (int i = 0; i < size; i++) {
                    MessageRecordData dto = resultList.get(i);
                    List<Object> t = new ArrayList<>();
                    // 序号，电销组，会话顾问，会话客户，聊天时间（年月日时分秒），聊天内容
                    t.add(i + 1);
                    // 电销组
                    t.add(dto.getTeleGorupName());
                    // 会话顾问
                    t.add(dto.getTeleSaleName());
                    // 会话客户
                    t.add(dto.getCusName());
                    // 聊天时间
                    if(StringUtils.isNotBlank(dto.getMsgTimestamp())){
                        t.add(DateUtil.timeStampMills2Str(dto.getMsgTimestamp(),null));
                    }
                    // 聊天内容
                    t.add(dto.getBody());
                    dataList.add(t);
                }
             }
            // 创建一个工作薄
            XSSFWorkbook workBook = new XSSFWorkbook();
            // 创建一个工作薄对象sheet
            XSSFSheet sheet = workBook.createSheet();
            // 设置宽度
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 8000);
            sheet.setColumnWidth(6, 8000);

            XSSFWorkbook wbWorkbook = ExcelUtil.creat2007ExcelWorkbook(workBook, dataList);
            String name = "IM聊天记录" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            ServletOutputStream outputStream = response.getOutputStream();
            wbWorkbook.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            log.error("导出聊天记录io-e",e);
        }catch (Exception e){
            log.error("导出聊天记录e",e);
        }
    }

    // 消息体Attach自定义消息组装封装到body
    public void transChatRecord(List<MessageRecordData> messageRecordDataList , boolean isCal) {
        if(CollectionUtils.isEmpty(messageRecordDataList)){
            return ;
        }
        Map<String,String> productCardMap = new LinkedHashMap<>();
        productCardMap.put("investmentStr","我的总价预算");
        productCardMap.put("expectedArea","开店区域");
        productCardMap.put("hasStore","店面");
        productCardMap.put("job","职业");
        productCardMap.put("ageStr","年龄");
        productCardMap.put("intentionBrand","意向品牌");
        productCardMap.put("interestBrandCategoryList","意向品类");
        productCardMap.put("remark","备注");
        productCardMap.put("repastExperience","是否有餐饮相关经验");

        Map<String,String> brandMap = new LinkedHashMap<>();
        brandMap.put("titleName","");
        brandMap.put("subTitle","投资区间");
        brandMap.put("mainPoint","");
        brandMap.put("city","");

        Integer cardType = 18,brandType = 8;
        String hiType = "17" , audioType = "AUDIO" , pictureType = "PICTURE" , videoType = "VIDEO" , fileType="FILE";

        for (MessageRecordData messageRecordData : messageRecordDataList){
            if(StringUtils.isNotBlank(messageRecordData.getBody())){
                continue ;
            }
            String attach = messageRecordData.getAttach();
            if(StringUtils.isBlank(attach)){
                continue;
            }
            // 项目卡
            StringBuilder content = new StringBuilder();

            JSONObject jsonObject = JSONObject.parseObject(attach);

            JSONObject childRenData = (JSONObject) jsonObject.get("data");
            // 项目卡片
            if(cardType.equals(jsonObject.get("type")) && null != childRenData){
                for (Map.Entry<String, String> map:productCardMap.entrySet()){
                    String key = map.getKey();
                    String value = map.getValue();
                    if(null != childRenData.get(key)){
                        content.append(value).append(":").append(childRenData.get(key)).append("\t");
                    }
                }
            }
            // 品牌卡片
            if(brandType.equals(jsonObject.get("type")) && null != childRenData){
                for (Map.Entry<String, String> map : brandMap.entrySet()){
                    String key = map.getKey();
                    String value = map.getValue();
                    if(StringUtils.isBlank(value)){
                        content.append(childRenData.get(key)).append("\t");
                    }else{
                        if(null != childRenData.get(key)){
                            content.append(value).append(":").append(childRenData.get(key)).append("\t");
                        }
                    }
                }
            }
            // 打招呼
            if(hiType.equals(jsonObject.get("type")) && null != childRenData && null != childRenData.get("data")){
                JSONArray data = (JSONArray) childRenData.get("data");
                for (Object o : data){
                    JSONObject json = (JSONObject) o;
                    content.append(json.get("comText")).append("\t");
                }
            }
            if(isCal){
                // 图片
                if(pictureType.equals(messageRecordData.getMsgType()) &&  null != jsonObject.get("url")){
                    content.append("图片").append(":").append(jsonObject.get("url"));
                }
                // 语音
                if(audioType.equals(messageRecordData.getMsgType())  && null != jsonObject.get("url")){
                    // 语音
                    content.append("语音").append(":").append(jsonObject.get("url"));
                }
                // 视频
                if(videoType.equals(messageRecordData.getMsgType())  && null != jsonObject.get("url")){
                    // 视频
                    content.append("视频").append(":").append(jsonObject.get("url"));
                }
                if(fileType.equals(messageRecordData.getMsgType())  && null != jsonObject.get("url")){
                    // 视频
                    content.append("文件").append(":").append(jsonObject.get("url"));
                }
            }
            // 设置最终值
            messageRecordData.setBody(content.toString());
        }
    }

    /**
     * 导出标题
     * 序号，电销组，会话顾问，会话客户，聊天时间（年月日时分秒），聊天内容
     * @return
     */
    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组");
        headTitleList.add("会话顾问");
        headTitleList.add("会话客户");
        headTitleList.add("聊天时间（年月日时分秒）");
        headTitleList.add("聊天内容");
        return headTitleList;
    }


    /**
     * 顾问监控分页
     * @param tSaleMonitorReq
     * @return
     */
    @PostMapping("/saleMonitor")
    public @ResponseBody JSONResult<PageBean<SaleMonitorDTO>> getSaleMonitor(@RequestBody TSaleMonitorReq tSaleMonitorReq){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if( null == tSaleMonitorReq.getTeleSaleId()){
            if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
                //电销总监
                List<UserInfoDTO> userList = getTeleSaleByOrgId(orgId);
                if(CollectionUtils.isEmpty(userList)) {
                    tSaleMonitorReq.setTeleSaleIds(Arrays.asList(-1L));
                }else{
                    List<Long> idList = userList.parallelStream()
                            .filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user->user.getId()).collect(Collectors.toList());
                    idList.add(curLoginUser.getId());
                    // 封装多个电销顾问ID
                    tSaleMonitorReq.setTeleSaleIds(idList);
                }
            }else if (CollectionUtils.isNotEmpty(roleList) && CollectionUtils.isNotEmpty(roleList) && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {

                // 管理员
            }else {
                List<UserInfoDTO>  userInfoList  = getTeleSaleByOrgId(curLoginUser.getOrgId());
                if (CollectionUtils.isEmpty(userInfoList)) {
                    tSaleMonitorReq.setTeleSaleIds(Arrays.asList(-1L));
                }else{
                    List<Long> idList = userInfoList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user->user.getId()).collect(Collectors.toList());
                    tSaleMonitorReq.setTeleSaleIds(idList);
                }
            }
        }else{

            tSaleMonitorReq.setTeleSaleIds(Arrays.asList(tSaleMonitorReq.getTeleSaleId()));
        }
        JSONResult<PageBean<SaleMonitorDTO>> result = customerInfoFeignClient.getSaleMonitor(tSaleMonitorReq);

        return result;
    }

    /**
     * 顾问在线离线忙碌状态数量
     * @return
     */
    @PostMapping("/getSaleImStateNum")
    public @ResponseBody JSONResult<List<Map<String, Object>>> getSaleImStateNum(){
        UserInfoDTO userCurrent = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = userCurrent.getRoleList();
        Map<String,Object> paramMap = new HashMap<>();
        if(roleList != null && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())){

            // 管理员
        }else{
            // 当前组织下面人员
            List<UserInfoDTO> userList = getTeleSaleByOrgId(userCurrent.getOrgId());
            if(CollectionUtils.isNotEmpty(userList)){
                List<Long> idList = userList.parallelStream()
                        .filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user->user.getId()).collect(Collectors.toList());
                paramMap.put("saleIdList",idList);
            }else{
                ArrayList<Long> longs = new ArrayList<>(1);
                longs.add(-1L);
                paramMap.put("saleIdList",longs);
            }
        }

        JSONResult<List<Map<String, Object>>> result = customerInfoFeignClient.getSaleImStateNum(paramMap);

        return result;
    }

    /**
     * 在线忙碌离线操作
     * @param saleOnlineLeaveLogReq
     * @return
     */
    @PostMapping("/onlineleave")
    public @ResponseBody JSONResult<Boolean> onlineleave(@Valid @RequestBody  SaleOnlineLeaveLogReq saleOnlineLeaveLogReq , BindingResult result , HttpServletRequest request){
        log.info("web-onlineleave入参={},result={}" , JSON.toJSONString(saleOnlineLeaveLogReq) , JSON.toJSONString(result));
        String agent=request.getHeader("User-Agent");
        log.info("web-agent浏览器={}" , agent);
        if (result.hasErrors()) {
            // 参数校验
            return CommonUtil.validateParam(result);
        }
        // 顾问Id不存在直接从session中获得
        if(null == saleOnlineLeaveLogReq.getTeleSaleId()){
            UserInfoDTO user = CommUtil.getCurLoginUser();
            if(null == user ){
                log.warn("user is null!");
                return new JSONResult().fail(SysErrorCodeEnum.ERR_AUTH_LIMIT.getCode(),SysErrorCodeEnum.ERR_AUTH_LIMIT.getMessage());
            }
            List<RoleInfoDTO> roleList = user.getRoleList();
            if(CollectionUtils.isEmpty(roleList)){
                log.warn("roleList is null");
                return new JSONResult().fail(SysErrorCodeEnum.ERR_AUTH_LIMIT.getCode(),SysErrorCodeEnum.ERR_AUTH_LIMIT.getMessage());
            }
            Map<String, String> roleMap = roleList.stream().map(RoleInfoDTO::getRoleCode).collect(Collectors.toMap(k -> k, v -> v, (x, y) -> x));
            // 电销顾问 & 业务线是的商机盒子的
            if(roleMap.containsKey(RoleCodeEnum.DXCYGW.name()) && ((Integer) BusinessLineConstant.SHANGJI).equals(user.getBusinessLine())){
                // 设置顾问Id
                saleOnlineLeaveLogReq.setTeleSaleId(user.getId());
                JSONResult<Boolean> onlineleave = customerInfoFeignClient.onlineleave(saleOnlineLeaveLogReq);
                log.info("session返回结果={}" , onlineleave);
                return onlineleave ;
            }
        }else{
            // 顾问Id存在直接设置在线离线
            JSONResult<Boolean> onlineleave = customerInfoFeignClient.onlineleave(saleOnlineLeaveLogReq);
            log.info("无session返回结果={}" , onlineleave);
            return onlineleave ;
        }
        return new JSONResult().fail(SysErrorCodeEnum.ERR_AUTH_LIMIT.getCode(),SysErrorCodeEnum.ERR_AUTH_LIMIT.getMessage());
    }

    /**
     * 分页参数校验
     * @param result
     * @return
     */
    private static JSONPageResult validateParam(BindingResult result) {
        List<ObjectError> list = result.getAllErrors();
        for (ObjectError error : list) {
            log.error("参数校验失败：{},错误信息：{}", error.getArguments(), error.getDefaultMessage());
        }
        return new JSONPageResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
    }

    /**
     * 根据orgId 获取创业顾问
     *
     * @param orgId
     * @return
     */
    @SuppressWarnings("all")
    private List<UserInfoDTO> getTeleSaleByOrgId(Long orgId) {
        log.info("callrecord orgId {{}}",orgId);
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setOrgId(orgId);
        req.setRoleCode(RoleCodeEnum.DXCYGW.name());
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        log.info("callrecord userJr {{}}",userJr);
        if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            log.error(
                    "查询电销通话记录-获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                    orgId, userJr);
            return null;
        }
        return userJr.getData();
    }
}
