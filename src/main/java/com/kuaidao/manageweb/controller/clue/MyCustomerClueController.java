package com.kuaidao.manageweb.controller.clue;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.clue.ClueBasicDTO;
import com.kuaidao.aggregation.dto.clue.ClueCustomerDTO;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.ClueRelateDTO;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.aggregation.dto.clue.CustomerClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.ReleaseClueDTO;
import com.kuaidao.aggregation.dto.clue.RepeatClueDTO;
import com.kuaidao.aggregation.dto.clue.RepeatClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.RepeatClueSaveDTO;
import com.kuaidao.aggregation.dto.clueappiont.ClueAppiontmentDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

@Controller
@RequestMapping("/tele/clueMyCustomerInfo")
public class MyCustomerClueController {

	@Autowired
	private ProjectInfoFeignClient projectInfoFeignClient;

	@Autowired
	private MyCustomerFeignClient myCustomerFeignClient;

	@Autowired
	private UserInfoFeignClient userInfoFeignClient;

	@Autowired
	private CallRecordFeign callRecordFeign;

	/**
	 * 初始化我的客户
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/initmyCustomer")
	public String initmyCustomer(HttpServletRequest request, Model model) {

		return "clue/myCustom";
	}

	/**
	 * 我的客户分页查询
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */

	@RequestMapping("/findTeleClueInfo")
	@ResponseBody
	public JSONResult<PageBean<CustomerClueDTO>> findTeleClueInfo(HttpServletRequest request,
			@RequestBody CustomerClueQueryDTO dto) {
		if (null != dto && null != dto.getDayTel() && dto.getDayTel().intValue() == 1) {
			// 当日拨打电话
			dto.setTelTime(new Date());
		}
		if (null != dto && null != dto.getDayTel() && dto.getTrackingDay().intValue() == 1) {
			// 当日跟进
			dto.setTrackingTime(new Date());
		}
		JSONResult<PageBean<CustomerClueDTO>> jsonResult = myCustomerFeignClient.findTeleClueInfo(dto);
		return jsonResult;
	}

	/**
	 * 我的客户创建资源
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/createClue")
	public String createClue(HttpServletRequest request, Model model) {

		ProjectInfoPageParam param = new ProjectInfoPageParam();
		// 项目
		JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
		if (proJson.getCode().equals(JSONResult.SUCCESS)) {
			model.addAttribute("proSelect", proJson.getData());
		}

		return "clue/addCustomerResources";
	}

	/**
	 * 我的客户释放资源
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping("/releaseClue")
	@ResponseBody
	public JSONResult<String> releaseClue(HttpServletRequest request, @RequestBody ReleaseClueDTO dto) {

		Subject subject = SecurityUtils.getSubject();
		UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
		if (null != user) {
			dto.setReleaseUserId(user.getId());
		}
		return myCustomerFeignClient.releaseClue(dto);
	}

	/**
	 * 维护客户资源数据
	 * 
	 * @param request
	 * @param clueId
	 * @return
	 */
	@RequestMapping("/customerEditInfo")
	public String customerEditInfo(HttpServletRequest request, @RequestParam String clueId) {

		CallRecordReqDTO call = new CallRecordReqDTO();
		call.setClueId(clueId);
		call.setPageSize(10000);
		call.setPageNum(1);
		JSONResult<PageBean<CallRecordRespDTO>> callRecord = callRecordFeign.listTmCallReacordByParams(call);
		// 资源通话记录
		if (callRecord != null && callRecord.SUCCESS.equals(callRecord.getCode()) && callRecord.getData() != null) {

			request.setAttribute("callRecord", callRecord.getData());
		}
		ClueQueryDTO queryDTO = new ClueQueryDTO();

		queryDTO.setClueId(new Long(clueId));

		request.setAttribute("clueId", clueId);

		JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(queryDTO);

		// 维护的资源数据
		if (clueInfo != null && clueInfo.SUCCESS.equals(clueInfo.getCode()) && clueInfo.getData() != null) {

			if (null != clueInfo.getData().getClueCustomer()) {
				request.setAttribute("customer", clueInfo.getData().getClueCustomer());
			}
			if (null != clueInfo.getData().getClueBasic()) {
				request.setAttribute("base", clueInfo.getData().getClueBasic());
			}
			if (null != clueInfo.getData().getClueIntention()) {
				request.setAttribute("intention", clueInfo.getData().getClueIntention());
			}
		}
		return "clue/addCustomerMaintenance";
	}

	/**
	 * 预约来访页面跳转
	 * 
	 * @param request
	 * @param clueId
	 * @return
	 */
	@RequestMapping("/inviteCustomer")
	public String inviteCustomer(HttpServletRequest request, @RequestParam String clueId, Model model) {
		request.setAttribute("clueId", clueId);

		ProjectInfoPageParam param = new ProjectInfoPageParam();
		// 项目
		JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
		if (proJson.getCode().equals(JSONResult.SUCCESS)) {
			model.addAttribute("proSelect", proJson.getData());
		}
		return "clue/inviteCustomerForm";
	}

	/**
	 * 预约来访数据保存
	 * 
	 * @param request
	 * @param clueId
	 * @return
	 */
	@RequestMapping("/inviteCustomerSave")
	@ResponseBody
	public JSONResult<String> inviteCustomerSave(HttpServletRequest request, @RequestBody ClueAppiontmentDTO dto) {
		Subject subject = SecurityUtils.getSubject();
		UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
		if (null != user) {
		 	dto.setCreateUser(user.getId());
		}
		return myCustomerFeignClient.saveAppiontment(dto);

	}

	/**
	 * 重单申请查询重单数据
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping("/queryRepeatClue")
	@ResponseBody
	public JSONResult<List<RepeatClueDTO>> queryRepeatClue(HttpServletRequest request,
			@RequestBody RepeatClueQueryDTO dto) {
		Subject subject = SecurityUtils.getSubject();
		UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
		if (null != user) {
			dto.setTelemarketingId(user.getId());
		}
		return myCustomerFeignClient.queryRepeatClue(dto);
	}

	/**
	 * 重单申请查询被重单数据
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping("/queryRepeatCurClue")
	@ResponseBody
	public JSONResult<List<RepeatClueDTO>> queryRepeatCurClue(HttpServletRequest request,
			@RequestBody RepeatClueQueryDTO dto) {
		return myCustomerFeignClient.queryRepeatClue(dto);
	}

	/**
	 * 查询所有电销创业顾问
	 * 
	 * @param request
	 * @return
	 */

	@RequestMapping("/listByOrgAndRole")
	@ResponseBody
	public JSONResult<List<UserInfoDTO>> listByOrgAndRole(HttpServletRequest request) {
		UserOrgRoleReq userRole = new UserOrgRoleReq();
		userRole.setRoleCode(RoleCodeEnum.DXCYGW.name());
		return userInfoFeignClient.listByOrgAndRole(userRole);
	}

	/**
	 * 重单申请保存
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */

	@RequestMapping("/saveRepeatClue")
	@ResponseBody
	public JSONResult<String> saveRepeatClue(HttpServletRequest request, @RequestBody RepeatClueSaveDTO dto) {

		Subject subject = SecurityUtils.getSubject();
		UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
		if (null != user) {
			dto.setOrgId(user.getOrgId());
			dto.setUserId(user.getId());
		}
		if (null != dto.getRepeatUserId()) {
			IdEntityLong id = new IdEntityLong();
			id.setId(dto.getRepeatUserId());
			JSONResult<UserInfoDTO> userInfo = userInfoFeignClient.get(id);
			if (userInfo != null && userInfo.SUCCESS.equals(userInfo.getCode()) && userInfo.getData() != null) {
				dto.setRepeatOrgId(userInfo.getData().getId());
			}
		}
		return myCustomerFeignClient.saveRepeatClue(dto);
	}

	/**
	 * 新建资源保存
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping("/saveCreateClue")
	@ResponseBody
	public JSONResult<String> saveCreateClue(HttpServletRequest request, @RequestBody ClueDTO dto) {
		Subject subject = SecurityUtils.getSubject();
		UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
		if (null != user) {
			// 添加创建人
			if (null != dto) {
				ClueCustomerDTO cus = dto.getClueCustomer();
				if (null != cus) {
					cus.setCreateUser(user.getId());
					cus.setCreateTime(new Date());
				}
				ClueBasicDTO basic = dto.getClueBasic();
				if (null != basic) {
					// 添加创建人
					basic.setCreateUser(user.getId());
					basic.setCreateTime(new Date());
				}
			}
			// 电销关联数据
			ClueRelateDTO relation = new ClueRelateDTO();
			// 电销顾问
			relation.setTeleSaleId(user.getId());
			// 电销组
			relation.setTeleGorupId(user.getOrgId());

			dto.setClueRelate(relation);

		}

		return myCustomerFeignClient.createCustomerClue(dto);

	}

}
