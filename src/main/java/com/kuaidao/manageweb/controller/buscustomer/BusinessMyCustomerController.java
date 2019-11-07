package com.kuaidao.manageweb.controller.buscustomer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerRespDTO;
import com.kuaidao.aggregation.dto.busmycustomer.MyCustomerParamDTO;
import com.kuaidao.aggregation.dto.clue.ClueBasicDTO;
import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.busmycustomer.BusMyCustomerFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author yangbiao 接口层 Created on 2019-2-12 15:06:38 商务模块--我的客户
 */

@Controller
@RequestMapping("/aggregation/businessMyCustomer")
public class BusinessMyCustomerController {

    private static Logger logger = LoggerFactory.getLogger(BusinessMyCustomerController.class);

    @Autowired
    CompanyInfoFeignClient companyInfoFeignClient;

    @Autowired
    BusMyCustomerFeignClient busMyCustomerFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request) {
        logger.info("------------ 商务：我的客户列表 ---------------");
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 电销组
        MyCustomerParamDTO dto = new MyCustomerParamDTO();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
        } else {
            dto.setBusSaleId(user.getId());
        }
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (JSONResult.SUCCESS.equals(proJson.getCode())) {
            request.setAttribute("proAllSelect", proJson.getData());
            if (!CollectionUtils.isEmpty(proJson.getData())) {
                List<ProjectInfoDTO> data = proJson.getData();
                List<ProjectInfoDTO> alist = new ArrayList<>();
                for (ProjectInfoDTO infoDTO : data) {
                    if (AggregationConstant.NO.equals(infoDTO.getIsNotSign())) {
                        alist.add(infoDTO);
                    }
                }
                request.setAttribute("proSelect", alist);
            }
        }
        JSONResult<List<CompanyInfoDTO>> listJSONResult = companyInfoFeignClient.allCompany();
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
            request.setAttribute("companySelect", proJson.getData());
        }
        // 查询赠送类型集合
        request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
        // request.setAttribute("teleGroupList", teleGroupList);
        // request.setAttribute("teleSaleList", teleSaleList);
        request.setAttribute("loginUserId", user.getId());
        return "bus_mycustomer/mycustomerList";
    }


    @PostMapping("/teleSaleAndGroupName")
    @ResponseBody
    public JSONResult<Map> teleSaleAndGroupName(@RequestBody MyCustomerParamDTO param) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        Map<String, List<OrganizationDTO>> map = new HashMap<>();
        Map<Long, OrganizationDTO> groupMap = new HashMap();
        List teleGroupList = new ArrayList();
        List teleSaleList = new ArrayList();
        Map<Long, OrganizationDTO> saleMap = new HashMap();

        MyCustomerParamDTO dto = new MyCustomerParamDTO();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
        } else {
            dto.setBusSaleId(user.getId());
        }

        JSONResult<List<BusMyCustomerRespDTO>> resList = busMyCustomerFeignClient.queryList(dto);
        if (JSONResult.SUCCESS.equals(resList.getCode())) {
            List<BusMyCustomerRespDTO> datas = resList.getData();
            for (BusMyCustomerRespDTO myCustomerRespDTO : datas) {

                if (myCustomerRespDTO.getTeleGorupId() != null) {
                    OrganizationDTO organizationDTO = new OrganizationDTO();
                    organizationDTO.setId(myCustomerRespDTO.getTeleGorupId());
                    organizationDTO.setName(myCustomerRespDTO.getTeleGorupName());
                    organizationDTO.setCreateTime(myCustomerRespDTO.getTeleGorupCreateTime());
                    groupMap.put(organizationDTO.getId(), organizationDTO);
                }
                // 创业顾问
                if (myCustomerRespDTO.getTeleSaleId() != null) {
                    OrganizationDTO organizationDTO = new OrganizationDTO();
                    organizationDTO.setId(myCustomerRespDTO.getTeleSaleId());
                    organizationDTO.setName(myCustomerRespDTO.getTeleSaleName());
                    saleMap.put(myCustomerRespDTO.getTeleSaleId(), organizationDTO);
                }
            }

            for (Map.Entry<Long, OrganizationDTO> entry : groupMap.entrySet()) {
                if (entry.getValue().getCreateTime() != null) {
                    teleGroupList.add(entry.getValue());
                }
            }
            for (Map.Entry<Long, OrganizationDTO> entry : saleMap.entrySet()) {
                teleSaleList.add(entry.getValue());
            }
        }
        logger.info("电销组,{{}}" + teleGroupList);
        Collections.sort(teleGroupList, Comparator.comparing(OrganizationDTO::getCreateTime));
        map.put("teleGroupList", teleGroupList);
        map.put("teleSaleList", teleSaleList);
        return new JSONResult<Map>().success(map);
    }



    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<BusMyCustomerRespDTO>> queryListPage(
            @RequestBody MyCustomerParamDTO param) {
        logger.info("============分页数据查询==================");

        Date date1 = param.getReserveTime1();
        Date date2 = param.getReserveTime2();
        if (date1 != null && date2 != null) {
            if (date1.getTime() > date2.getTime()) {
                return new JSONResult().fail("-1", "邀约来访时间，结束时间不能早于开始时间!");
            }
        }

        Date date3 = param.getAllocateTime1();
        Date date4 = param.getAllocateTime2();
        if (date3 != null && date4 != null) {
            if (date3.getTime() > date4.getTime()) {
                return new JSONResult().fail("-1", "接收客户时间，结束时间不能早于开始时间!");
            }
        }

        Date date5 = param.getAppiontmentCreateTime1();
        Date date6 = param.getAppiontmentCreateTime2();
        if (date5 != null && date6 != null) {
            if (date5.getTime() > date6.getTime()) {
                return new JSONResult().fail("-1", "提交邀约时间，结束时间不能早于开始时间!");
            }
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        /**
         * 下回代码注释掉，请记得给我改回来
         */
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
        } else {
            param.setBusSaleId(user.getId());
            param.setBusGroupId(user.getOrgId());
        }
        return busMyCustomerFeignClient.queryPageList(param);
    }

    /**
     * 标记未到访
     */
    @PostMapping("/notVisit")
    @ResponseBody
    @LogRecord(description = "标记未到访", operationType = OperationType.UPDATE,
            menuName = MenuEnum.BUS_MY_CUSTOMER)
    public JSONResult<Boolean> notVisit(@RequestBody BusMyCustomerReqDTO param) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        param.setUserId(curLoginUser.getId());
        if (curLoginUser.getBusinessLine() != null) {
            param.setBusinessLine(curLoginUser.getBusinessLine());
        }
        return busMyCustomerFeignClient.notVisit(param);
    }

    /**
     * 未到访原因查看
     */
    @PostMapping("/notVisitReason")
    @ResponseBody
    public JSONResult<ClueBasicDTO> notVisitReason(@RequestBody IdEntityLong idEntityLong) {
        return busMyCustomerFeignClient.notVisitReason(idEntityLong);
    }

    @PostMapping("/listNoPage")
    @ResponseBody
    public JSONResult<List<CompanyInfoDTO>> listNoPage() {
        JSONResult<List<CompanyInfoDTO>> list = companyInfoFeignClient.allCompany();
        return list;
    }

    /**
     * 查询字典表
     * 
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode =
                dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null
                && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }
}
