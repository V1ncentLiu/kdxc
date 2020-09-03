package com.kuaidao.manageweb.controller.im;


import com.kuaidao.common.constant.*;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.custservice.dto.custservice.CustomerInfoDTO;
import com.kuaidao.custservice.dto.onlineleave.SaleMonitorCalReq;
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
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.text.SimpleDateFormat;
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
    /**
     * 聊天记录跳转
     */
    @GetMapping("/chatRecordIndex")
    public String chatRecordIndex( HttpServletRequest request ){
        List dxzList = getCommonDxzList();
        request.setAttribute("dzList", dxzList);
        return "im/imChattingRecords";
    }

    @GetMapping("/saleMonitorIndex")
    public String saleMonitorIndex( HttpServletRequest request ){
        List dxzList = getCommonDxzList();
        request.setAttribute("dzList", dxzList);
        return "im/imManagement";
    }

    private List getCommonDxzList() {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        List dxzList = new ArrayList();
        if (roleList != null && roleList.get(0) != null) {
            OrganizationQueryDTO dto = new OrganizationQueryDTO();
            dto.setSystemCode(SystemCodeConstant.HUI_JU);
            dto.setOrgType(OrgTypeConstant.DXZ);
            dto.setBusinessLine(user.getBusinessLine());
            JSONResult<List<OrganizationRespDTO>> dzList = organizationFeignClient.queryOrgByParam(dto);
            dxzList = dzList.getData();
        }
        return dxzList;
    }

    /**
     * 聊天记录历史分页
     * @param messageRecordPageReq
     */
    @PostMapping("/getChatRecordPage")
    public @ResponseBody JSONPageResult<List<MessageRecordData>> getChatRecordPage(@RequestBody MessageRecordPageReq messageRecordPageReq ){

        return imFeignClient.getChatRecordPage(messageRecordPageReq);
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
        // 根据客户Id查询客户accId
        IdListReq idListReq = new IdListReq();
        List<String> idList = new ArrayList<>();
        // 封装客户Id
        idList.add(messageRecordPageReq.getCusId());
        idListReq.setIdList(idList);
        JSONResult<List<CustomerInfoDTO>> listJSONResult = customerInfoFeignClient.brandAndIssubmit(idListReq);

        List<CustomerInfoDTO> data ;

        if(null == listJSONResult || !"0".equals(listJSONResult.getCode()) || CollectionUtils.isEmpty(data = listJSONResult.getData())){

            return new JSONPageResult().fail(SysErrorCodeEnum.ERR_AUTH_LIMIT.getCode(),SysErrorCodeEnum.ERR_AUTH_LIMIT.getMessage());
        }
        CustomerInfoDTO customerInfoDTO = data.get(0);
        // 客户Im无值?
        if(StringUtils.isBlank(customerInfoDTO.getImId())){

            return new JSONPageResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        // 设置accId
        messageRecordPageReq.setAccId(customerInfoDTO.getImId());
        return imFeignClient.listChatRecord(messageRecordPageReq);
    }

    /**
     * 导出聊天记录
     * @param messageRecordExportSearchReq
     * @param response
     * @throws Exception
     */
    @PostMapping("/export")
    public void export(@RequestBody MessageRecordExportSearchReq messageRecordExportSearchReq , HttpServletResponse response){
        // 获得历史聊天记录
        try {
            JSONPageResult<List<MessageRecordData>> chatRecordList = imFeignClient.getChatRecordList(messageRecordExportSearchReq);
            List<List<Object>> dataList = new ArrayList<>();
            dataList.add(getHeadTitleList());
            if(null != chatRecordList && JSONResult.SUCCESS.equals(chatRecordList.getCode()) && chatRecordList.getData() != null && chatRecordList.getData().size() != 0) {
                List<MessageRecordData> resultList = chatRecordList.getData();
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        // 设置导出日期
                        t.add(sdf.format(Long.valueOf(dto.getMsgTimestamp())));
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
     * 提交会话客户量/客户提交量
     * @param saleMonitorCalReq
     * @return
     */
    @PostMapping("/calCusNum")
    public @ResponseBody JSONResult<Boolean> calCusNum(@RequestBody SaleMonitorCalReq saleMonitorCalReq){
        if(null == saleMonitorCalReq.getSessionCusNum() && null == saleMonitorCalReq.getCommitCusNum()){
            // 参数校验
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        saleMonitorCalReq.setSessionCusNum( null == saleMonitorCalReq.getSessionCusNum() ? null : 1L);
        saleMonitorCalReq.setCommitCusNum( null == saleMonitorCalReq.getCommitCusNum() ? null : 1L);
        // session获得用户
        UserInfoDTO user = CommUtil.getCurLoginUser();
        if(null == user){
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_AUTH_LIMIT.getCode(),SysErrorCodeEnum.ERR_AUTH_LIMIT.getMessage());
        }
        saleMonitorCalReq.setTeleSaleId(user.getId());
        return customerInfoFeignClient.calCusNum(saleMonitorCalReq);
    }

    /**
     * 顾问监控分页
     * @param tSaleMonitorReq
     * @return
     */
    @PostMapping("/saleMonitor")
    public @ResponseBody JSONResult<PageBean<SaleMonitorDTO>> getSaleMonitor(@RequestBody TSaleMonitorReq tSaleMonitorReq){

        JSONResult<PageBean<SaleMonitorDTO>> result = customerInfoFeignClient.getSaleMonitor(tSaleMonitorReq);

        return result;
    }

    /**
     * 顾问在线离线忙碌状态数量
     * @return
     */
    @PostMapping("/getSaleImStateNum")
    public @ResponseBody JSONResult<List<Map<String, Object>>> getSaleImStateNum(){

        JSONResult<List<Map<String, Object>>> result = customerInfoFeignClient.getSaleImStateNum();

        return result;
    }

    public static void main(String[] args) {
        JSONResult<List<Map<String, Object>>> result = new  JSONResult<List<Map<String, Object>>>();
        Map<String, Object> o = new HashMap<>();
        o.put("imStatus","imStatus");
        List<Map<String, Object>>  tttt = new ArrayList<>();
        tttt.add(o);
        result.setData(tttt);
        System.out.println(result);

    }

    /**
     * 在线忙碌离线操作
     * @param saleOnlineLeaveLogReq
     * @return
     */
    @PostMapping("/onlineleave")
    public @ResponseBody JSONResult<Boolean> onlineleave(@Valid @RequestBody  SaleOnlineLeaveLogReq saleOnlineLeaveLogReq , BindingResult result){
        if (result.hasErrors()) {
            // 参数校验
            return CommonUtil.validateParam(result);
        }
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
            return customerInfoFeignClient.onlineleave(saleOnlineLeaveLogReq);
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
}
