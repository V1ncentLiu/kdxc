package com.kuaidao.manageweb.controller.ip;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.ip.IpAccessFeignClient;
import com.kuaidao.sys.dto.ip.IpAccessManagerQueryDTO;
import com.kuaidao.sys.dto.ip.IpPackageInfoDTO;
import com.kuaidao.sys.dto.ip.IpRepositoryInfoDTO;

@Controller
@RequestMapping("/ip/ipAccess")
public class IpAccessController {
	@Autowired
	private IpAccessFeignClient ipAccessFeignClient;

 
	@RequestMapping("/initIpAccess")
	public String initRoleInfo() {
		return "ip/ipAccessManagerList";
	}

	/***
	 * 展现Ip库页面页面
	 * 
	 * @return
	 */
	@RequestMapping("/queryRepositoryList")
	@ResponseBody
	public JSONResult<PageBean<IpRepositoryInfoDTO>> queryRepositoryList(@RequestBody IpAccessManagerQueryDTO queryDTO,
			HttpServletRequest request, HttpServletResponse response) {
		return ipAccessFeignClient.querytIpPageList(queryDTO);

	}
	
	@RequestMapping("/saveIpRepository")
	@ResponseBody
	public JSONResult<String> saveIpRepository(@RequestBody IpRepositoryInfoDTO dto,
			HttpServletRequest request, HttpServletResponse response) {
		return ipAccessFeignClient.saveIpRepository(dto);

	}
	@RequestMapping("/deleteIpRepository")
	@ResponseBody
	public JSONResult<String> deleteIpRepository(@RequestBody IpRepositoryInfoDTO dto,
			HttpServletRequest request, HttpServletResponse response) {
		return ipAccessFeignClient.deleteIpRepository(dto);

	}
	
	/***
	 * 展现Ip包页面
	 * 
	 * @return
	 */
	@RequestMapping("/queryPackageList")
	@ResponseBody
	public JSONResult<PageBean<IpPackageInfoDTO>> queryPackageList(@RequestBody IpAccessManagerQueryDTO queryDTO,
			HttpServletRequest request, HttpServletResponse response) {
		return ipAccessFeignClient.querytPackagePageList(queryDTO);

	}
	
	@RequestMapping("/saveIpPackage")
	@ResponseBody
	public JSONResult<String> saveIpPackage(@RequestBody IpPackageInfoDTO dto,
			HttpServletRequest request, HttpServletResponse response) {
		return ipAccessFeignClient.saveIpPackage(dto);

	}
	@RequestMapping("/updateIpPackage")
	@ResponseBody
	public JSONResult<String> updateIpPackage(@RequestBody IpPackageInfoDTO dto,
			HttpServletRequest request, HttpServletResponse response) {
		return ipAccessFeignClient.updateIpPackage(dto);

	}
	@RequestMapping("/deleteIppackage")
	@ResponseBody
	public JSONResult<String> deleteIppackage(@RequestBody IpPackageInfoDTO dto,
			HttpServletRequest request, HttpServletResponse response) {
		return ipAccessFeignClient.deleteIppackage(dto);

	}
	
	/**
	 * 查询角色列表
	 * 
	 * @param dto
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/queryAllPackageList")
	@ResponseBody
	public JSONResult<PageBean<IpPackageInfoDTO>> queryAllPackageList(HttpServletRequest request,
			HttpServletResponse response) {
		return ipAccessFeignClient.querytAllPackageList();
	}

	/**
	 * 
	 * @param dto
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/queryIpPackageByParam")
	@ResponseBody
	public JSONResult<List<IpPackageInfoDTO>> queryIpPackageByParam(@RequestBody IpAccessManagerQueryDTO dto) {
		return ipAccessFeignClient.queryIpPackageByParam(dto);
	} 

}
