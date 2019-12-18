/**
 *
 */
package com.kuaidao.manageweb.controller.merchant.consumerecord;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kuaidao.aggregation.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.manageweb.feign.telemarketing.TelemarketingLayoutFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.account.dto.consume.ConsumeRecordNumDTO;
import com.kuaidao.account.dto.consume.CountConsumeRecordDTO;
import com.kuaidao.account.dto.consume.MerchantConsumeRecordDTO;
import com.kuaidao.account.dto.consume.MerchantConsumeRecordPageParam;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.merchant.consumerecord.MerchantConsumeRecordFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;

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
        List<UserInfoDTO> userList = getMerchantUser(statusList);
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
                curList.add(dto.getMainAccountName());
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
    public String initInfoList(HttpServletRequest request,@RequestParam Long mainAccountId) {
        List<UserInfoDTO> userList = getUserListByainAccountId(mainAccountId);
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
    public JSONResult<PageBean<MerchantConsumeRecordDTO>> list(@RequestBody MerchantConsumeRecordPageParam pageParam, HttpServletRequest request) {
        // 消费记录
        JSONResult<PageBean<MerchantConsumeRecordDTO>> list = merchantConsumeRecordFeignClient.list(pageParam);

        return list;
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
    private List<UserInfoDTO> getUserListByainAccountId(Long mainAccountId) {
        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();
        // 主账号空的时候查询所有(是点消费明细按钮进来的)
        // 内部商家查询所有电销组
        // 外部商家查询所有主账号和子账号
        if (null == mainAccountId) {
            userList = buildUserList();
            return userList;
        }
        JSONResult<UserInfoDTO> jsonResult = userInfoFeignClient.get(new IdEntityLong(mainAccountId));

        if (JSONResult.SUCCESS.equals(jsonResult.getCode())) {
            userList.add(jsonResult.getData());
        }
        // 查询子账号
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_THREE);
        userInfoDTO.setParentId(mainAccountId);
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        userList.addAll(merchantUserList.getData());
        return userList;
    }
    /**
     * * 外部商家-商家账号：当前登录商家主账号加子账号 * 内部商家-商家账户：电销布局里绑定的电销组
     *
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2019/10/23 10:55
     * @since: 1.0.0
     **/
    private List<UserInfoDTO> buildUserList() {
        UserInfoDTO user = getUser();
        List<UserInfoDTO> userList = new ArrayList<>();
        // 商家所属
        Integer merchantType = user.getMerchantType();
        // 内部商家
        if (SysConstant.MerchantType.TYPE1 == merchantType) {
            TelemarketingLayoutDTO reqDto = new TelemarketingLayoutDTO();
            reqDto.setCompanyGroupId(user.getId());
            JSONResult<List<OrganizationDTO>> result = telemarketingLayoutFeignClient.getdxListByCompanyGroupId(reqDto);
            List<OrganizationDTO> orgList = result.getData();
            if (result.getCode().equals(JSONResult.SUCCESS) && CollectionUtils.isNotEmpty(result.getData())) {
                for (OrganizationDTO organizationDTO : orgList) {
                    UserInfoDTO userInfoDTO = new UserInfoDTO();
                    BeanUtils.copyProperties(organizationDTO,userInfoDTO);
                }

            }
        }
        // 外部商家
        if (SysConstant.MerchantType.TYPE2 == merchantType) {
            userList.add(user);
            // 状态集合
            List<Integer> status = new ArrayList<>();
            // 启用
            status.add(SysConstant.USER_STATUS_ENABLE);
            // 锁定
            status.add(SysConstant.USER_STATUS_LOCK);
            userList = getMerchantUser(user.getId(), status);
        }
        return userList;
    }
    /**
     * 查询商家账号
     *
     * @param
     * @return
     */
    private List<UserInfoDTO> getMerchantUser(Long parentId, List<Integer> arrayList) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_THREE);
        userInfoDTO.setParentId(parentId);
        userInfoDTO.setStatusList(arrayList);
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return merchantUserList.getData();
    }

    /**
     * 查询所有商家账号
     *
     * @param
     * @return
     */
    private List<UserInfoDTO> getMerchantUser(List<Integer> arrayList) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
        userInfoDTO.setStatusList(arrayList);
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return merchantUserList.getData();
    }
}
