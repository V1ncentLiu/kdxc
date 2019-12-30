package com.kuaidao.manageweb.controller.merchant.consumerecord;

import com.kuaidao.account.dto.config.MerchantProportionConfigDTO;
import com.kuaidao.account.dto.consume.CompanyConsumeRecordDTO;
import com.kuaidao.account.dto.consume.CompanyConsumeRecordReq;
import com.kuaidao.account.dto.consume.MonthConsumeStatisticsDTO;
import com.kuaidao.account.dto.consume.MonthConsumeStatisticsReq;
import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.aggregation.dto.project.CompanyInfoPageParam;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.merchant.consumerecord.MerchantConsumeRecordFeignClient;
import com.kuaidao.manageweb.feign.merchant.consumerecord.MerchantProportionConfigFeignClient;
import com.kuaidao.manageweb.feign.merchant.consumerecord.MonthConsumeStatisticsFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商家月消费记录统计
 */
@Controller
@RequestMapping("/merchant/monthConsumeStatistics")
@Slf4j
public class MonthConsumeStatisticsController {

    //内部商家
    private static final Integer INNER_MERCHANT_TYPE = 1;
    //商家主账号
    private static final Integer USER_TYPE = 2;

    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;
    @Autowired
    private MonthConsumeStatisticsFeignClient monthConsumeStatisticsFeignClient;
    @Autowired
    private MerchantProportionConfigFeignClient merchantProportionConfigFeignClient;
    @Autowired
    private MerchantConsumeRecordFeignClient merchantConsumeRecordFeignClient;


    /**
     * 商家月消费统计 跳转页面
     */
    @RequestMapping("/toConsumeMonthly")
    public String toConsumeMonthly(HttpServletRequest request) {
        List<UserInfoDTO> groupList = getGroupList();
        request.setAttribute("groupList", groupList);
        return "merchant/consumeRecord/consumeMonthly";
    }


    /**
     * 商家月消费记录统计列表
     */
    @RequestMapping("/getConsumeMonthlyList")
    @ResponseBody
    public JSONResult<PageBean<MonthConsumeStatisticsDTO>> getConsumeMonthlyList(@RequestBody MonthConsumeStatisticsReq pageParam,
                                                                                 HttpServletRequest request) {
        JSONResult<PageBean<MonthConsumeStatisticsDTO>> list = monthConsumeStatisticsFeignClient.list(pageParam);
        return monthConsumeStatisticsFeignClient.list(pageParam);
    }

    /**
     * 公司占比设置 跳转页面
     */
    @RequestMapping("/toPercentageSetting")
    public String toPercentageSetting(HttpServletRequest request) {
        return "merchant/consumeRecord/percentageSetting";
    }

    /**
     * 公司占比设置 页面
     */
    @RequestMapping("/getPercentageSettingList")
    @ResponseBody
    public JSONResult<List<MerchantProportionConfigDTO>> getPercentageSettingList(){
        //集团配置集合
        List<MerchantProportionConfigDTO> list = new ArrayList<>();
        //集团集合
        List<UserInfoDTO> groupList = getGroupList();
        if(null != groupList){
            for(UserInfoDTO userInfoDTO : groupList){
                MerchantProportionConfigDTO merchantProportionConfigDTO = new MerchantProportionConfigDTO();
                merchantProportionConfigDTO.setMerchantUserId(userInfoDTO.getId());
                merchantProportionConfigDTO.setMerchantUserName(userInfoDTO.getName());
                CompanyInfoPageParam companyInfoPageParam = new CompanyInfoPageParam();
                companyInfoPageParam.setCompanyGroupId(userInfoDTO.getId());
                JSONResult<List<CompanyInfoDTO>> companyListRes = companyInfoFeignClient.getCompanyListByParam(companyInfoPageParam);
                if(companyListRes.getCode().equals(JSONResult.SUCCESS) && CollectionUtils.isNotEmpty(companyListRes.getData())){
                    List<String> ids = companyListRes.getData().stream().sorted(Comparator.comparing(CompanyInfoDTO::getId)).map(p -> String.valueOf(p.getId())).collect(Collectors.toList());
                    List<String> names = companyListRes.getData().stream().sorted(Comparator.comparing(CompanyInfoDTO::getId)).map(p -> p.getCompanyName()).collect(Collectors.toList());
                    merchantProportionConfigDTO.setCompanyIds(String.join(",",ids));
                    merchantProportionConfigDTO.setCompanyNames(String.join(",",names));
                }
                JSONResult<List<MerchantProportionConfigDTO>> listRes = merchantProportionConfigFeignClient.list();
                if(listRes.getCode().equals(JSONResult.SUCCESS) && CollectionUtils.isNotEmpty(listRes.getData())){
                    List<MerchantProportionConfigDTO> data = listRes.getData();
                    Map<Long, List<MerchantProportionConfigDTO>> proportionMap =
                            data.stream().collect(Collectors.groupingBy(MerchantProportionConfigDTO::getMerchantUserId, Collectors.toList()));
                    List<MerchantProportionConfigDTO> merchantProportionConfigDTOS = proportionMap.get(userInfoDTO.getId());
                    if(CollectionUtils.isNotEmpty(merchantProportionConfigDTOS)){
                        merchantProportionConfigDTO.setProportion(merchantProportionConfigDTOS.get(0).getProportion());
                        merchantProportionConfigDTO.setId(merchantProportionConfigDTOS.get(0).getId());
                    }
                }
                list.add(merchantProportionConfigDTO);
            }
        }
        return new JSONResult<List<MerchantProportionConfigDTO>>().success(list);
    }


    @PostMapping("/saveOrUpdate")
    @ResponseBody
    public JSONResult saveOrUpdate(@RequestBody MerchantProportionConfigDTO merchantProportionConfigDTO){
        Long id = merchantProportionConfigDTO.getId();
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        if(null != id){
            merchantProportionConfigDTO.setCreateUserId(curLoginUser.getId());
        }else{
            merchantProportionConfigDTO.setUpdateUserId(curLoginUser.getId());
        }
        return merchantProportionConfigFeignClient.saveOrUpdate(merchantProportionConfigDTO);
    }


    @RequestMapping("/exportExcel")
    public void exportExcel(HttpServletRequest request,
                            HttpServletResponse response, @RequestBody CompanyConsumeRecordReq companyConsumeRecordReq){
        try{
            JSONResult<List<CompanyConsumeRecordDTO>> listJSONResult =
                    merchantConsumeRecordFeignClient.exportCompanyConsumeRecord(companyConsumeRecordReq);
            List<List<Object>> dataList = new ArrayList<List<Object>>();
            dataList.add(getTitleList());
            List<CompanyConsumeRecordDTO> data = listJSONResult.getData();
            buildList(dataList, data);
            XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
            String name = MessageFormat.format("商家月消费记录统计表{0}.xlsx","_"+System.currentTimeMillis());
            response.addHeader("Content-Disposition",
                    "attachment;filename=\"" + name+"\"");
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            ServletOutputStream outputStream = response.getOutputStream();
            wbWorkbook.write(outputStream);
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 获取集团商家
     */
    private List<UserInfoDTO> getGroupList(){
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setMerchantType(INNER_MERCHANT_TYPE);
        userInfoDTO.setUserType(USER_TYPE);
        JSONResult<List<UserInfoDTO>> listJSONResult = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return listJSONResult.getData();
    }


    private void buildList(List<List<Object>> dataList, List<CompanyConsumeRecordDTO> orderList) {
        for(int i = 0; i<orderList.size(); i++){
            CompanyConsumeRecordDTO ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getCompanyName());
            curList.add(ra.getCreateTimeStr());
            curList.add(ra.getClueId());
            curList.add(ra.getAmount());
            dataList.add(curList);
        }
    }
    private List<Object> getTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("分公司名称");
        headTitleList.add("消费时间");
        headTitleList.add("消费资源ID");
        headTitleList.add("消费金额（元）");
        return headTitleList;
    }
}
