package com.kuaidao.manageweb.controller.invalidcustomer;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.kuaidao.aggregation.dto.financing.RefundRespDTO;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.DateUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.invalidcustomer.AbandonParamDTO;
import com.kuaidao.aggregation.dto.invalidcustomer.AbandonRespDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.manageweb.feign.InvalidCustomer.AbandonFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @author yangbiao
 * @Date: 2019/2/11 15:13
 * @Description: 废弃池
 */
@Controller
@RequestMapping("/abandonsource")
public class AbandonController {


    private static Logger logger = LoggerFactory.getLogger(AbandonController.class);

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    AbandonFeignClient abandonFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;


    @RequiresPermissions("aggregation:abandonPool:view")
    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<AbandonRespDTO>> queryListPage(@RequestBody AbandonParamDTO dto) {

        Date date1 = dto.getCreateTime1();
        Date date2 = dto.getCreateTime2();
        if (date1 != null && date2 != null) {
            if (date1.getTime() > date2.getTime()) {
                return new JSONResult().fail("-1", "创建时间，结束时间不能早于开始时间!");
            }
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 推广所属公司 为当前账号所在机构的推广所属公司
        dto.setPromotionCompany(user.getPromotionCompany());
        return abandonFeignClient.queryListPage(dto);
    }

    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request) {
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (JSONResult.SUCCESS.equals(proJson.getCode())) {
            request.setAttribute("proSelect", proJson.getData());
        }
        request.setAttribute("zyzyList", queryUserByRole());

        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("aggregation:abandonPool");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());

        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("aggregation:abandonPool");
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());

        return "invalidcustomer/abandonList";
    }

    /**
     * 查询所有资源专员
     * 
     * @return
     */

    private List<UserInfoDTO> queryUserByRole() {

        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();

        UserOrgRoleReq userRole = new UserOrgRoleReq();
        userRole.setRoleCode(RoleCodeEnum.TGZJ.name());
        JSONResult<List<UserInfoDTO>> userZxzjList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userZxzjList.getCode()) && null != userZxzjList.getData()
                && userZxzjList.getData().size() > 0) {
            userList.addAll(userZxzjList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.YHZG.name());
        JSONResult<List<UserInfoDTO>> userYhZgList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userYhZgList.getCode()) && null != userYhZgList.getData()
                && userYhZgList.getData().size() > 0) {
            userList.addAll(userYhZgList.getData());
        }
        userRole.setRoleCode(RoleCodeEnum.TGYHWY.name());
        JSONResult<List<UserInfoDTO>> userYhWyList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userYhWyList.getCode()) && null != userYhWyList.getData()
                && userYhWyList.getData().size() > 0) {
            userList.addAll(userYhWyList.getData());
        }
        userRole.setRoleCode(RoleCodeEnum.KFZY.name());
        JSONResult<List<UserInfoDTO>> userKfList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userKfList.getCode()) && null != userKfList.getData()
                && userKfList.getData().size() > 0) {
            userList.addAll(userKfList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.KFZG.name());
        JSONResult<List<UserInfoDTO>> userKfZgList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userKfZgList.getCode()) && null != userKfZgList.getData()
                && userKfZgList.getData().size() > 0) {
            userList.addAll(userKfZgList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.TGNQWY.name());
        JSONResult<List<UserInfoDTO>> userKNqWyList =
                userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userKNqWyList.getCode()) && null != userKNqWyList.getData()
                && userKNqWyList.getData().size() > 0) {
            userList.addAll(userKNqWyList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.NQZG.name());
        JSONResult<List<UserInfoDTO>> userNqZgList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userNqZgList.getCode()) && null != userNqZgList.getData()
                && userNqZgList.getData().size() > 0) {
            userList.addAll(userNqZgList.getData());
        }

        return userList;

    }
    @RequiresPermissions("aggregation:abandonPool:export")
    @PostMapping("/findAbandonCluesCount")
    @ResponseBody
    public JSONResult<Long> findAbandonCluesCount(@RequestBody AbandonParamDTO dto)
            throws Exception {
        return abandonFeignClient.findAbandonCluesCount(dto);
    }

    @PostMapping("/queryListExport")
    public void queryListExport(HttpServletRequest request, HttpServletResponse response, @RequestBody AbandonParamDTO dto)  throws Exception{
        Long strarDate = System.currentTimeMillis();
        Date date1 = dto.getCreateTime1();
        Date date2 = dto.getCreateTime2();
        if (date1 != null && date2 != null) {
            if (date1.getTime() > date2.getTime()) {
                return ;
            }
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 推广所属公司 为当前账号所在机构的推广所属公司
        dto.setPromotionCompany(user.getPromotionCompany());
        JSONResult<List<AbandonRespDTO>>  abandonRespResult = abandonFeignClient.queryListExport(dto);
        List<AbandonExportModel> abandonExportModels = new ArrayList<>();
        if (JSONResult.SUCCESS.equals(abandonRespResult.getCode()) && abandonRespResult.getData() != null
                && abandonRespResult.getData().size() > 0) {
            List<AbandonRespDTO> abandonRespList = abandonRespResult.getData();
            for(AbandonRespDTO abandonRespDTO:abandonRespList){
                AbandonExportModel abandonExportModel = new AbandonExportModel();
                BeanUtils.copyProperties(abandonRespDTO, abandonExportModel);
                abandonExportModels.add(abandonExportModel);
            }

        }
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            String name =
                    "废弃池导出" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            ExcelWriter excelWriter =
                    EasyExcel.write(outputStream, AbandonExportModel.class).build();
            List<List<AbandonExportModel>> partition = Lists.partition(abandonExportModels, 50000);
            if(abandonExportModels !=null && abandonExportModels.size()>0){
                for (int i = 0; i < partition.size(); i++) {
                    // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样
                    WriteSheet writeSheet = EasyExcel.writerSheet(i, "Sheet" + i).build();
                    // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                    excelWriter.write(partition.get(i), writeSheet);
                }
            }else{
                //实例化表单
                WriteSheet writeSheet = EasyExcel.writerSheet(0, "废弃池导出" ).build();
                excelWriter.write(abandonExportModels, writeSheet);
            }

            excelWriter.finish();
        }
        logger.info("废弃池导出总数量{}" , abandonExportModels.size());
        logger.info("废弃池导出总时长{}" , (System.currentTimeMillis() - strarDate));
    }
}
