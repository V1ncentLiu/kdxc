/**
 *
 */
package com.kuaidao.manageweb.controller.merchant.consumerecord;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.kuaidao.account.dto.consume.ConsumeRecordNumDTO;
import com.kuaidao.account.dto.consume.CountConsumeRecordDTO;
import com.kuaidao.account.dto.consume.MerchantConsumeRecordDTO;
import com.kuaidao.account.dto.consume.MerchantConsumeRecordPageParam;
import com.kuaidao.businessconfig.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.merchant.consumerecord.MerchantConsumeRecordFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.telemarketing.TelemarketingLayoutFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/merchant/consumeRecord")
public class ConsumeRecordController {
    private static Logger logger = LoggerFactory.getLogger(MerchantConsumeRecordController.class);
    @Autowired
    private MerchantConsumeRecordFeignClient merchantConsumeRecordFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private TelemarketingLayoutFeignClient telemarketingLayoutFeignClient;

    /***
     * 消费记录列表页(管理端)
     *
     * @return
     */
    @RequestMapping("/initRecordList")
    @RequiresPermissions("merchant:consumeRecord:view")
    public String initCompanyList(HttpServletRequest request) {
        // 商家账号是所有的商家主账户（商家账户管理里所有非禁用的商家主账号）
        List<Integer> statusList = new ArrayList<>();
        // 启用
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        // 锁定
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getMerchantUser(SysConstant.USER_TYPE_TWO, statusList);
        request.setAttribute("userList", userList);
        return "merchant/consumeRecord/consumeRecord";
    }

    /***
     * 消费记录列表(管理端)
     *
     * @return
     */
    @PostMapping("/countList")
    @ResponseBody
    @RequiresPermissions("merchant:consumeRecord:view")
    public JSONResult<PageBean<CountConsumeRecordDTO>> countList(@RequestBody MerchantConsumeRecordPageParam pageParam, HttpServletRequest request) {
        // 消费记录
        JSONResult<PageBean<CountConsumeRecordDTO>> countList = merchantConsumeRecordFeignClient.countList(pageParam);

        return countList;
    }

    /**
     * 消费记录列表(管理端) 导出
     *
     * @param
     * @return
     */
    @RequiresPermissions("merchant:consumeRecord:export")
    @PostMapping("/countListExport")
    @LogRecord(description = "导出", operationType = OperationType.EXPORT, menuName = MenuEnum.CONSUME_RECORD)
    public void export(@RequestBody List<CountConsumeRecordDTO> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("list param{}", list);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());

        if (list != null && list.size() != 0) {

            int size = list.size();

            for (int i = 0; i < size; i++) {
                CountConsumeRecordDTO dto = list.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(i + 1);
                //
                curList.add(dto.getUserName());
                curList.add(dto.getDateNum());
                curList.add(dto.getAmount());
                curList.add(dto.getCreateDate());
                curList.add(dto.getClueNum());
                dataList.add(curList);
            }

        } else {
            logger.error("export consume_record_export res{{}}", list);
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
        sheet.setColumnWidth(5, 5500);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007ExcelWorkbook(workBook, dataList);

        String name = "商家消费记录" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }

    private List<Object> getHeadTitleList() {

        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("消费商家名称");
        headTitleList.add("消费数（天）");
        headTitleList.add("消费金额（元）");
        headTitleList.add("消费时间");
        headTitleList.add("总获取资源数（条）");
        return headTitleList;
    }

    /***
     * 查询今日、昨日消费统计（管理端）
     *
     * @return
     */
    @PostMapping("/countNum")
    @ResponseBody
    @RequiresPermissions("merchant:consumeRecord:view")
    public JSONResult<ConsumeRecordNumDTO> countNum(@RequestBody MerchantConsumeRecordPageParam pageParam, HttpServletRequest request) {
        // 消费记录
        JSONResult<ConsumeRecordNumDTO> countNum = merchantConsumeRecordFeignClient.countNum(pageParam);

        return countNum;
    }

    /***
     * 单个商家消费记录页(管理端)
     *
     * @return
     */
    @RequestMapping("/initSingleMerchant")
    @RequiresPermissions("merchant:consumeRecord:view")
    public String initSingleMerchant(@RequestParam Long mainAccountId, HttpServletRequest request) {
        List<UserInfoDTO> userList = getUserListByainAccountId(mainAccountId);
        request.setAttribute("userList", userList);
        request.setAttribute("mainAccountId", mainAccountId + "");
        return "merchant/consumeRecord/singleConsumeRecord";
    }

    /***
     * 单个商家消费记录(管理段)
     *
     * @return
     */
    @PostMapping("/countListMerchant")
    @ResponseBody
    @RequiresPermissions("merchant:consumeRecord:view")
    public JSONResult<PageBean<CountConsumeRecordDTO>> countListMerchant(@RequestBody MerchantConsumeRecordPageParam pageParam,
                                                                         HttpServletRequest request) {
        // 消费记录
        JSONResult<PageBean<CountConsumeRecordDTO>> countListMerchant = merchantConsumeRecordFeignClient.countListMerchant(pageParam);

        return countListMerchant;
    }

    /**
     * 单个商家消费记录(管理端) 导出
     *
     * @param
     * @return
     */
    @RequiresPermissions("merchant:consumeRecord:export")
    @PostMapping("/countListMerchantExport")
    @LogRecord(description = "导出", operationType = OperationType.EXPORT, menuName = MenuEnum.CONSUME_RECORD)
    public void countListMerchantExport(@RequestBody List<CountConsumeRecordDTO> list, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        logger.debug("list param{}", list);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitle());

        if (list != null && list.size() != 0) {

            int size = list.size();

            for (int i = 0; i < size; i++) {
                CountConsumeRecordDTO dto = list.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(i + 1);
                //
                curList.add(dto.getUserName());
                curList.add(dto.getCreateDate());
                curList.add(dto.getAmount());
                curList.add(dto.getClueNum());
                dataList.add(curList);
            }

        } else {
            logger.error("export consume_record_export res{{}}", list);
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
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007ExcelWorkbook(workBook, dataList);

        String name = "商家消费记录" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }

    private List<Object> getHeadTitle() {

        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("商家名称");
        headTitleList.add("消费日期");
        headTitleList.add("消费金额（元）");
        headTitleList.add("获取资源条数");
        return headTitleList;
    }

    /***
     * 消费明细列表页(管理段)
     *
     * @return
     */
    @RequestMapping("/initInfoList")
    @RequiresPermissions("merchant:consumeRecord:view")
    public String initInfoList(HttpServletRequest request, @RequestParam Long mainAccountId) {
        List<UserInfoDTO> userList = getMerchantUserListByainAccountId(mainAccountId);
        request.setAttribute("userList", userList);
        return "merchant/consumeRecord/consumeRecordInfo";
    }


    /***
     * 消费明细列表(管理段)
     *
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("merchant:consumeRecord:view")
    public JSONResult<PageBean<MerchantConsumeRecordDTO>> list(@RequestBody MerchantConsumeRecordPageParam pageParam) {
        if (null == pageParam.getUserId()) {
            pageParam.setMerchantUserList(pageParam.getUserList());
        }
        // 消费记录
        JSONResult<PageBean<MerchantConsumeRecordDTO>> list = merchantConsumeRecordFeignClient.list(pageParam);

        return list;
    }

    /***
     * 消费明细列表(管理段)
     *
     * @return
     */
    @PostMapping("/exportList")
    @RequiresPermissions("merchant:consumeRecord:view")
    @LogRecord(description = "导出", operationType = OperationType.EXPORT, menuName = MenuEnum.CONSUME_RECORD)
    public void exportList(@RequestBody MerchantConsumeRecordPageParam pageParam, HttpServletResponse response) {
        if (null == pageParam.getUserId()) {
            pageParam.setMerchantUserList(pageParam.getUserList());
        }
        // 消费记录
        JSONResult<List<MerchantConsumeRecordDTO>> jsonResult =merchantConsumeRecordFeignClient.listDeatilExport(pageParam);
        List<MerchantConsumeDetailExportModel> merchantConsumeDetailExportModelList = new ArrayList();
        if (jsonResult.getCode().equals(JSONResult.SUCCESS)) {
            List<MerchantConsumeRecordDTO> data = jsonResult.getData();
            if (CollectionUtils.isNotEmpty(data)) {
                for (int i = 0; i < data.size(); i++) {
                    MerchantConsumeRecordDTO merchantConsumeRecordDTO = data.get(i);
                    MerchantConsumeDetailExportModel merchantConsumeDetailExportModel = new MerchantConsumeDetailExportModel();
                    BeanUtils.copyProperties(merchantConsumeRecordDTO, merchantConsumeDetailExportModel);
                    int flag =i+1;
                    merchantConsumeDetailExportModel.setId(flag);
                    merchantConsumeDetailExportModel.setCustomerTime(DateUtil.convert2String(merchantConsumeRecordDTO.getCreateTime(),DateUtil.ymdhms));
                    merchantConsumeDetailExportModel.setClueId(merchantConsumeRecordDTO.getClueId()+"");
                    merchantConsumeDetailExportModelList.add(merchantConsumeDetailExportModel);
                }
            }

        }

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            String name = "商家消费明细" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            ExcelWriter excelWriter = EasyExcel.write(outputStream, MerchantConsumeDetailExportModel.class).build();
            if (merchantConsumeDetailExportModelList != null && merchantConsumeDetailExportModelList.size() > 0) {
                List<List<MerchantConsumeDetailExportModel>> partition = Lists.partition(merchantConsumeDetailExportModelList, 50000);
                for (int i = 0; i < partition.size(); i++) {
                    // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样
                    WriteSheet writeSheet = EasyExcel.writerSheet(i, "Sheet" + i).build();
                    // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                    excelWriter.write(partition.get(i), writeSheet);
                }
            } else {
                // 实例化表单
                WriteSheet writeSheet = EasyExcel.writerSheet(0, "商家消费明细").build();
                excelWriter.write(merchantConsumeDetailExportModelList, writeSheet);
            }

            excelWriter.finish();
        } catch (IOException e) {

        }
    }

    /**
     * 获取当前登录账号
     *
     * @param
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 根据商家主账户查询所有子账号和本身
     *
     * @param
     * @return
     */
    private List<UserInfoDTO> getMerchantUserListByainAccountId(Long mainAccountId) {
        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();
        // 主账号空的时候查询所有(是点消费明细按钮进来的)
        // 展示所有的主账号以及子账号和所有的电销组
        if (null == mainAccountId) {
            List<Integer> statusList = new ArrayList<>();
            // 启用
            statusList.add(SysConstant.USER_STATUS_ENABLE);
            // 锁定
            statusList.add(SysConstant.USER_STATUS_LOCK);
            userList = getMerchantUser(SysConstant.USER_TYPE_TWO, statusList);
            return userList;
        }
        // 根据主账号查询
        // 内部商家展示主账号和绑定的电销组
        // 外部商家展示主账号和子账号
        JSONResult<UserInfoDTO> jsonResult = userInfoFeignClient.get(new IdEntityLong(mainAccountId));
        if (JSONResult.SUCCESS.equals(jsonResult.getCode()) && null != jsonResult.getCode()) {
            UserInfoDTO user = jsonResult.getData();
            userList.add(user);
            // 内部商家
            if (SysConstant.MerchantType.TYPE1 == user.getMerchantType()) {
                TelemarketingLayoutDTO reqDto = new TelemarketingLayoutDTO();
                reqDto.setCompanyGroupId(mainAccountId);
                JSONResult<List<OrganizationDTO>> result = telemarketingLayoutFeignClient.getdxListByCompanyGroupId(reqDto);
                List<OrganizationDTO> orgList = result.getData();
                if (result.getCode().equals(JSONResult.SUCCESS) && CollectionUtils.isNotEmpty(result.getData())) {
                    for (OrganizationDTO organizationDTO : orgList) {
                        UserInfoDTO userInfoDTO = new UserInfoDTO();
                        BeanUtils.copyProperties(organizationDTO, userInfoDTO);
                        //添加内部电销组
                        userList.add(userInfoDTO);
                    }
                }
            }
            // 外部商家
            if (SysConstant.MerchantType.TYPE2 == user.getMerchantType()) {
                // 查询子账号
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUserType(SysConstant.USER_TYPE_THREE);
                userInfoDTO.setParentId(mainAccountId);
                JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
                //添加所有子账号
                userList.addAll(merchantUserList.getData());
            }
        }
        return userList;
    }

    /**
     * 根据商家主账户查询所有子账号和本身
     *
     * @param
     * @return
     */
    private List<UserInfoDTO> getUserListByainAccountId(Long mainAccountId) {
        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();
        // 主账号空的时候查询所有(是点消费明细按钮进来的)
        // 展示所有的主账号以及子账号和所有的电销组
        if (null == mainAccountId) {
            userList = buildAllUserList();
            return userList;
        }
        // 根据主账号查询
        // 内部商家展示主账号和绑定的电销组
        // 外部商家展示主账号和子账号
        JSONResult<UserInfoDTO> jsonResult = userInfoFeignClient.get(new IdEntityLong(mainAccountId));
        if (JSONResult.SUCCESS.equals(jsonResult.getCode()) && null != jsonResult.getCode()) {
            UserInfoDTO user = jsonResult.getData();
            userList.add(user);
            // 内部商家
            if (SysConstant.MerchantType.TYPE1 == user.getMerchantType()) {
                TelemarketingLayoutDTO reqDto = new TelemarketingLayoutDTO();
                reqDto.setCompanyGroupId(mainAccountId);
                JSONResult<List<OrganizationDTO>> result = telemarketingLayoutFeignClient.getdxListByCompanyGroupId(reqDto);
                List<OrganizationDTO> orgList = result.getData();
                if (result.getCode().equals(JSONResult.SUCCESS) && CollectionUtils.isNotEmpty(result.getData())) {
                    for (OrganizationDTO organizationDTO : orgList) {
                        UserInfoDTO userInfoDTO = new UserInfoDTO();
                        BeanUtils.copyProperties(organizationDTO, userInfoDTO);
                        //添加内部电销组
                        userList.add(userInfoDTO);
                    }
                }
            }
            // 外部商家
            if (SysConstant.MerchantType.TYPE2 == user.getMerchantType()) {
                // 查询子账号
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUserType(SysConstant.USER_TYPE_THREE);
                userInfoDTO.setParentId(mainAccountId);
                JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
                //添加所有子账号
                userList.addAll(merchantUserList.getData());
            }
        }
        return userList;
    }

    /**
     * * 展示所有的主账号+子账号+电销组
     *
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2019/10/23 10:55
     * @since: 1.0.0
     **/
    private List<UserInfoDTO> buildAllUserList() {
        List<UserInfoDTO> userList = new ArrayList<>();
        // 所有的电销组
        userList.addAll(getSaleGroupList());
        // 状态集合
        List<Integer> status = new ArrayList<>();
        // 启用
        status.add(SysConstant.USER_STATUS_ENABLE);
        // 锁定
        status.add(SysConstant.USER_STATUS_LOCK);
        // 查询所有主账号
        List<UserInfoDTO> mainAccountList = getMerchantUser(SysConstant.USER_TYPE_TWO, status);
        userList.addAll(mainAccountList);
        // 查询所有子账号
        List<UserInfoDTO> subAccountList = getMerchantUser(SysConstant.USER_TYPE_THREE, status);
        userList.addAll(subAccountList);
        return userList;
    }

    /**
     * 获取所有组织组并将电销组id和名字转换成用户的id和名字
     *
     * @return
     */
    private List<UserInfoDTO> getSaleGroupList() {
        List<UserInfoDTO> userList = new ArrayList<>();
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.DXZ);
        // 查询所有组织
        JSONResult<List<OrganizationRespDTO>> jsonResult = organizationFeignClient.queryOrgByParam(queryDTO);
        if (jsonResult.getCode().equals(JSONResult.SUCCESS) && CollectionUtils.isNotEmpty(jsonResult.getData())) {
            List<OrganizationRespDTO> orgList = jsonResult.getData();
            for (OrganizationRespDTO org : orgList) {
                // 转换电销组id和名字对应用户的id和名字
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                BeanUtils.copyProperties(org, userInfoDTO);
                userList.add(userInfoDTO);
            }
        }
        return userList;
    }

    /**
     * 查询所有商家账号
     *
     * @param
     * @return
     */
    private List<UserInfoDTO> getMerchantUser(Integer userType, List<Integer> arrayList) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(userType);
        userInfoDTO.setStatusList(arrayList);
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return merchantUserList.getData();
    }
}
