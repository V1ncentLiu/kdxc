package com.kuaidao.manageweb.controller.visitrecord;

import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.kuaidao.aggregation.constant.AggregationConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordRespDTO;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.visitrecord.BusVisitRecordFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author yangbiao
 * @Date: 2019/1/2 15:14
 * @Description: 到访记录
 */

@Controller
@RequestMapping("/busVisitRecord")
public class BusinessVisitRecordController {

    private static Logger logger = LoggerFactory.getLogger(BusinessVisitRecordController.class);

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private BusVisitRecordFeignClient visitRecordFeignClient;

    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;


    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request, @RequestParam String clueId) {
        BusVisitRecordReqDTO recordReqDTO = new BusVisitRecordReqDTO();
        recordReqDTO.setClueId(Long.valueOf(clueId));
        JSONResult<List<BusVisitRecordRespDTO>> listJSONResult =
                visitRecordFeignClient.queryList(recordReqDTO);
        List<BusVisitRecordRespDTO> data = new ArrayList<>();
        String notSignReason = "";
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
            data = listJSONResult.getData();
            if (data != null && data.size() > 0) {
                notSignReason = data.get(data.size() - 1).getNotSignReason();
            }
        }
        // 查询到访状态： 首次到访 2次到访 多次到访 （需要添加接口）

        request.setAttribute("tableData", data);
        request.setAttribute("notSignReason", notSignReason);
        return "bus_mycustomer/showVisitRecord";
    }


    /**
     * 新增
     */
    @RequestMapping("/insert")
    @ResponseBody
    @LogRecord(description = "添加到访记录", operationType = OperationType.INSERT,
        menuName = MenuEnum.CUSTOMER_VISIT_RECORD)
    public JSONResult<Boolean> saveVisitRecord(
            @Valid @RequestBody BusVisitRecordInsertOrUpdateDTO dto, BindingResult result)
            throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setCreateUser(user.getId());
        dto.setId(null);
        return visitRecordFeignClient.saveVisitRecord(dto);
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    @ResponseBody
    public JSONResult<Boolean> updateVisitRecord(
            @Valid @RequestBody BusVisitRecordInsertOrUpdateDTO dto, BindingResult result)
            throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setUpdateUser(user.getId());
        return visitRecordFeignClient.updateVisitRecord(dto);
    }

    /**
     * 查询 不分页
     */
    @RequestMapping("/queryList")
    @ResponseBody
    public JSONResult<List<BusVisitRecordRespDTO>> queryList(@RequestBody BusVisitRecordReqDTO dto)
            throws Exception {
        dto.setIsVisit(AggregationConstant.YES);
        return visitRecordFeignClient.queryList(dto);
    }


    /**
     * 查询明细
     */
    @RequestMapping("/one")
    @ResponseBody
    public JSONResult<BusVisitRecordRespDTO> queryOne(@RequestBody IdEntityLong idEntityLong)
            throws Exception {
        return visitRecordFeignClient.queryOne(idEntityLong);
    }

    /**
     * 跳转到 到访记录明细页面
     */
    @RequestMapping("/visitRecordPage")
    public String visitRecordPage(HttpServletRequest request, @RequestParam String clueId,
            @RequestParam String visitStatus, @RequestParam String signAuditStatus)
            throws Exception {
        request.setAttribute("clueId", clueId);
        request.setAttribute("visitStatus", visitStatus);
        request.setAttribute("signAuditStatus", signAuditStatus);
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (JSONResult.SUCCESS.equals(proJson.getCode())) {
            request.setAttribute("proSelect", proJson.getData());
        }

        JSONResult<List<CompanyInfoDTO>> listJSONResult = companyInfoFeignClient.allCompany();
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
            request.setAttribute("companySelect", proJson.getData());
        }
        return "bus_mycustomer/showVisitRecord";
    }

    /**
     * 到访记录，新增时候回显信息
     */
    @RequestMapping("/echo")
    @ResponseBody
    public JSONResult<BusVisitRecordRespDTO> echo(@RequestBody IdEntityLong idEntityLong)
            throws Exception {
        BusVisitRecordRespDTO recordRespDTO = new BusVisitRecordRespDTO();
        // 查询需要进行回显的信息，并进行映射
        /**
         * 考察公司： 分公司 到访时间： 预约时间 客户姓名： 线索-客户姓名 考察项目： 品尝项目 签约省份： 投资意向信息- 省份 签约城市： 投资意向信息- 城市 签约区县：
         * 投资意向信息- 区县 来访城市： 派车单-城市 到访人数： 派车单-客户人数
         */

        JSONResult<BusVisitRecordRespDTO> maxNewOne =
                visitRecordFeignClient.findMaxNewOne(idEntityLong);
        if (JSONResult.SUCCESS.equals(maxNewOne.getCode())) {
            BusVisitRecordRespDTO data = maxNewOne.getData();
            if (data != null) {
                data.setRebutReason(null);
                data.setRebutTime(null);
                data.setNotSignReason(null);
                data.setIsSign(1);
                data.setVisitPeopleNum(null);
                return new JSONResult<BusVisitRecordRespDTO>().success(data);
            }
        }

        JSONResult<Map> mapJSONResult = visitRecordFeignClient.echoAppoinment(idEntityLong);
        if (JSONResult.SUCCESS.equals(mapJSONResult.getCode())) {
            Map data = mapJSONResult.getData();
            if (data != null) {
                Object arrivalTime = data.get("arrivalTime");
                Date arrDate = null;
                if (arrivalTime == null) {
                    arrDate = new Date();
                } else {
                    arrDate = new Date((Long) arrivalTime);
                }

                recordRespDTO.setVistitTime(arrDate);
                recordRespDTO.setCustomerName((String) data.get("cusName"));
                String tasteProjectId = (String) data.get("tasteProjectId");
                String[] split = tasteProjectId.split(",");
                if (split.length > 0 && !"".equals(split[0])) {
                    recordRespDTO.setProjectId(Long.valueOf(split[0]));
                }

                recordRespDTO.setSignProvince((String) data.get("signProvince"));
                recordRespDTO.setSignCity((String) data.get("signCity"));
                recordRespDTO.setSignDistrict((String) data.get("signDistrict"));
                recordRespDTO.setVisitCity((String) data.get("city"));
                recordRespDTO.setVisitPeopleNum((Integer) data.get("cusNum"));
                recordRespDTO.setVisitType(1);
                recordRespDTO.setIsSign(1);
            }
        }

        recordRespDTO.setRebutReason(null);
        recordRespDTO.setRebutTime(null);
        recordRespDTO.setNotSignReason(null);
        return new JSONResult<BusVisitRecordRespDTO>().success(recordRespDTO);
    }


}
