package com.kuaidao.manageweb.controller.sign;

import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailReqDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailRespDTO;
import com.kuaidao.aggregation.dto.sign.BusSignInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.sign.BusSignRespDTO;
import com.kuaidao.aggregation.dto.sign.PayDetailDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordRespDTO;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueCustomerFeignClient;
import com.kuaidao.manageweb.feign.paydetail.PayDetailFeignClient;
import com.kuaidao.manageweb.feign.visitrecord.BusVisitRecordFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kuaidao.aggregation.dto.clue.ClueRepetitionDTO;
import com.kuaidao.aggregation.dto.invitearea.InviteAreaDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.aggregation.dto.sign.BusinessSignDTO;
import com.kuaidao.aggregation.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.invitearea.InviteareaFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.sign.BusinessSignFeignClient;
import com.kuaidao.manageweb.util.DownFile;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      签约记录
 */

@Controller
@RequestMapping("/businesssign")
public class BusinessSignController {

    private static Logger logger = LoggerFactory.getLogger(BusinessSignController.class);
    @Autowired
    InviteareaFeignClient inviteareaFeignClient;
    @Autowired
    BusinessSignFeignClient businessSignFeignClient;
    @Autowired
    SysRegionFeignClient sysRegionFeignClient;
    @Autowired
	private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private ClueBasicFeignClient clueBasicFeignClient;
    @Autowired
    private ClueCustomerFeignClient clueCustomerFeignClient;


    @Autowired
    private BusVisitRecordFeignClient visitRecordFeignClient;

    @Autowired
    PayDetailFeignClient payDetailFeignClient;



    /**
     *  有效性签约单确认列表页面
     * 
     * @return
     */
    @RequestMapping("/businessSignValidPage")
    public String businessSignValidPage(HttpServletRequest request) {
    	OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
		orgDto.setOrgType(OrgTypeConstant.SWZ);
		orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
		//商务小组
		JSONResult<List<OrganizationRespDTO>> swList = organizationFeignClient.queryOrgByParam(orgDto);
		orgDto.setOrgType(OrgTypeConstant.DXZ);
		//电销小组
		JSONResult<List<OrganizationRespDTO>> dxList = organizationFeignClient.queryOrgByParam(orgDto);
		
		// 查询项目列表
        JSONResult<List<ProjectInfoDTO>> listNoPage =
                projectInfoFeignClient.listNoPage(new ProjectInfoPageParam());
      //获取省份
    	List<SysRegionDTO> proviceslist = sysRegionFeignClient.getproviceList().getData();
    	
    	request.setAttribute("swList", swList.getData());
		request.setAttribute("dxList", dxList.getData());
		request.setAttribute("projectList", listNoPage.getData());
		request.setAttribute("provinceList", proviceslist);
		return "business/businessSignValidPage";
    } 
    
    /**
     * 有效性签约单确认列表
     * 
     * @return
     */
    @RequestMapping("/businessSignValidList")
    @ResponseBody
    public JSONResult<PageBean<BusinessSignDTO>> businessSignValidList(HttpServletRequest request,@RequestBody BusinessSignDTO businessSignDTO) {
    	JSONResult<PageBean<BusinessSignDTO>> list = businessSignFeignClient.businessSignValidList(businessSignDTO);
    	return list;
    }
    /**
     * 签约有效性判断
     * 
     * @return
     */
    @RequestMapping("/updateBusinessSignDTOValidByIds")
    @LogRecord(description = "签约有效性修改",operationType = LogRecord.OperationType.UPDATE,menuName = MenuEnum.BUSINESSSIGNVALID)
    @ResponseBody
    public JSONResult addTelemarketingLayout(@RequestBody BusinessSignDTO businessSignDTO) {
    	return businessSignFeignClient.updateBusinessSignDTOValidByIds(businessSignDTO);
    }


    /**
     * 签约有效性判断
     *
     * @return
     */
    @RequestMapping("/getPaymentDetailsById")
    @ResponseBody
    public JSONResult<PayDetailDTO> getPaymentDetailsById(@RequestBody PayDetailDTO payDetailDTO) {
        return businessSignFeignClient.getPaymentDetailsById(payDetailDTO);
    }

    /**
     * 新增
     */
    @RequestMapping("/insert")
    @ResponseBody
    public JSONResult<Boolean> saveSign(@Valid @RequestBody BusSignInsertOrUpdateDTO dto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setCreateUser(user.getId());
        return businessSignFeignClient.saveSign(dto);
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    @ResponseBody
    public JSONResult<Boolean> updateSign(@Valid @RequestBody BusSignInsertOrUpdateDTO dto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setUpdateUser(user.getId());
        return businessSignFeignClient.updateSign(dto);
    }

    /**
     *  查询明细
     */
    @RequestMapping("/one")
    @ResponseBody
    public JSONResult<BusSignRespDTO> queryOne(@RequestBody IdEntityLong idEntityLong) throws Exception {
        JSONResult<BusSignRespDTO> res = businessSignFeignClient.queryOne(idEntityLong);
        if(JSONResult.SUCCESS.equals(res.getCode())){
            BusSignRespDTO data = res.getData();
            String linkPhone = linkPhone(idEntityLong);
            data.setPhone(linkPhone);
            res.setData(data);
        }
        return res;
    }

    /**
     *  签约单，新增时候回显信息
     *  1:回显当时提交邀约来访中填写的信息
     *  2: 若当前用户
     *
     *  客户姓名，签约餐饮公司=考察公司，签约项目，签约省份，签约城市，签约区／县。
     */
    @RequestMapping("/echo")
    @ResponseBody
    public JSONResult<BusSignRespDTO> echo(@RequestBody IdEntityLong idEntityLong) throws Exception {
        BusSignRespDTO signDTO = new BusSignRespDTO();
//      查询需要进行回显的信息，并进行映射
//      最新一次的邀约
        JSONResult<Map> mapJSONResult = visitRecordFeignClient.echoAppoinment(idEntityLong);
//      获取客户信息
        String linkPhone = linkPhone(idEntityLong);

//      查询最新一次到访
        JSONResult<BusVisitRecordRespDTO> maxNewOne = visitRecordFeignClient.findMaxNewOne(idEntityLong);
        Boolean flag = true;
        if(JSONResult.SUCCESS.equals(maxNewOne.getCode())){
            BusVisitRecordRespDTO data = maxNewOne.getData();
            if(data!=null){
                signDTO.setSignCompanyId(data.getCompanyid());
                signDTO.setSignProjectId(data.getProjectId());
                signDTO.setSignProvince(data.getSignProvince());
                signDTO.setSignCity(data.getSignCity());
                signDTO.setSignDictrict(data.getSignDistrict());
                signDTO.setSignShopType(data.getVistitStoreType());
                signDTO.setCustomerName(data.getCustomerName());
                signDTO.setPhone(linkPhone);
                signDTO.setSignType(1);
                signDTO.setPayType("1");
                flag = false;
            }
        }
        if(flag){
            if(JSONResult.SUCCESS.equals(mapJSONResult.getCode())){
                Map data = mapJSONResult.getData();
                if(data!=null){
//                    signDTO.setSignCompanyId((Long) data.get("busCompany"));
                    String tasteProjectId = (String)data.get("tasteProjectId");
                    String[] split = tasteProjectId.split(",");
                    if(split.length>0&&!"".equals(split[0])){
                        signDTO.setSignProjectId(Long.valueOf(split[0]));
                    }
                    signDTO.setSignProvince((String)data.get("signProvince"));
                    signDTO.setSignCity((String)data.get("signCity"));
                    signDTO.setSignDictrict((String)data.get("signDistrict"));
                    signDTO.setCustomerName((String)data.get("cusName"));
                    signDTO.setPhone(linkPhone);
                    signDTO.setSignType(1);
                    signDTO.setSignShopType("");
                    signDTO.setPayType("1");
                }
            }
        }
        signDTO.setRebutReason(null);
        signDTO.setRebutTime(null);
        return new JSONResult<BusSignRespDTO>().success(signDTO);
    }

    /**
     * 获取商务客户详情电话
     */
    private String linkPhone(IdEntityLong idEntityLong){
        List<Long> list = new ArrayList<>();
        list.add(idEntityLong.getId());
        IdListLongReq idListLongReq = new IdListLongReq();
        idListLongReq.setIdList(list);
        JSONResult<List<CustomerClueDTO>> listJSONResult = clueCustomerFeignClient.findcustomersByClueIds(idListLongReq);
        String linkPhone = "";
        if(JSONResult.SUCCESS.equals(listJSONResult.getCode())){
            List<CustomerClueDTO> data = listJSONResult.getData();
            CustomerClueDTO customerClueDTO = data.get(0);
            linkPhone = customerClueDTO.getLinkPhone();
        }
        return linkPhone;
    }


    /**
     *  跳转到 到访记录明细页面
     */
    @RequestMapping("/visitRecordPage")
    public String visitRecordPage(HttpServletRequest request,@RequestParam String clueId , @RequestParam String signId ,@RequestParam String readyOnly ) throws Exception {

        IdEntityLong idEntityLong = new IdEntityLong(); 
        idEntityLong.setId(Long.valueOf(clueId));
        JSONResult<BusSignRespDTO> busSign = queryOne(idEntityLong);
        BusSignRespDTO sign = busSign.getData();

        List<BusSignRespDTO> signData =  new ArrayList();
        signData.add(sign);
        List<BusSignRespDTO> PayAllData =  new ArrayList();
        PayAllData.add(sign);
//      签约基本信息
        request.setAttribute("signData", signData);
        request.setAttribute("payType",sign.getPayType());  // 最新一次付款类型： 用来判断显示行数

        if("4".equals(sign.getPayType())){
            readyOnly = "1";
        }

        if("1".equals(sign.getPayType())){
            /**
             * 全款时候：不存在定金 尾款 以及 追加定金的情况
             */
            request.setAttribute("PayAllData",PayAllData);
        }else {
            PayDetailReqDTO detailReqDTO = new PayDetailReqDTO();
            detailReqDTO.setSignId(Long.valueOf(signId));
            JSONResult<List<PayDetailRespDTO>> resListJson = payDetailFeignClient.queryList(detailReqDTO);
            if(JSONResult.SUCCESS.equals(resListJson.getCode())){
                List<PayDetailRespDTO> list = resListJson.getData();
//              定金
                List<PayDetailRespDTO> one = new ArrayList();
//              追加定金
                List<PayDetailRespDTO> two = new ArrayList();
//              尾款
                List<PayDetailRespDTO> three = new ArrayList();
                for(int i = 0 ; i < list.size() ; i ++){
                    PayDetailRespDTO dto  = list.get(i);
                    if("2".equals(dto.getPayType())){
                        one.add(dto);
                    }else   if("3".equals(dto.getPayType())){
                        two.add(dto);
                    }else   if("4".equals(dto.getPayType())){
                        three.add(dto);
                    }
                }
                request.setAttribute("oneData",one);
                request.setAttribute("twoData",two);
                request.setAttribute("threeData",three);
            }
        }
        request.setAttribute("clueId",clueId);
        request.setAttribute("signId",signId);
        request.setAttribute("readyOnly",readyOnly); //  readyOnly == 1 页面只读（没有添加按钮）
        return  "bus_mycustomer/showSignAndPayDetail";
    }

}
