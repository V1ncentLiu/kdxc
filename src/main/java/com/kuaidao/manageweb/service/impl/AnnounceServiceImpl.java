package com.kuaidao.manageweb.service.impl;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.log.LogMgtFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.service.IAnnounceService;
import com.kuaidao.manageweb.service.LogService;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveAddAndUpdateDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserInfoParamListReqDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AnnounceServiceImpl implements IAnnounceService {

	@Autowired
	UserInfoFeignClient userInfoFeignClient;

	@Autowired
	AnnReceiveFeignClient annReceiveFeignClient;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Async
	@Override
	public void sendMessage( AnnouncementAddAndUpdateDTO dto) {
		long startTime = System.currentTimeMillis();
		Long orgId = dto.getOrgId();
		List<UserInfoDTO> list = new ArrayList();
		List<Long> idsList = new ArrayList<>();

		UserInfoPageParam param = new UserInfoPageParam();
		param.setPageNum(1);
		param.setPageSize(100000);
		if(orgId==0){ //全部用户
			param.setStatus(1); //
		}else{//指定组织结构下的数据。
//                获取多个组织下的用户，通过组织ID进行获取。
			List<Long> orgids = dto.getOrgids();
			param.setStatus(1);
			param.setOrgIdList(orgids);
		}
		UserInfoParamListReqDTO userInfoParamListReqDTO = new UserInfoParamListReqDTO();
		JSONResult<List<UserInfoDTO>> list1 = userInfoFeignClient.listUserInfoByParam(userInfoParamListReqDTO);

		if(list1.getCode().equals("0")){
			list = list1.getData();
			InsertBatch(list1,dto.getType(),idsList,dto.getId());
		}
		Integer type = dto.getType();
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
	}

	public void sendMessage(Integer type,List<UserInfoDTO> list, List<Long> idsList,Long annId){
		if(type==1||type==0){ //站内公告通知
			for(int i = 0 ; i < list.size();i++){
				Long aLong = idsList.get(i);
				UserInfoDTO userInfo =  list.get(i);
				amqpTemplate.convertAndSend("amq.topic",userInfo.getOrgId()+"."+userInfo.getId(),"announce,"+annId+","+aLong);
			}
		}
	}

	public void InsertBatch(JSONResult<List<UserInfoDTO>> list1 ,Integer type,List<Long> idsList,Long annId){
		List<UserInfoDTO> list = list1.getData();
		List<AnnReceiveAddAndUpdateDTO> annrList = new ArrayList<AnnReceiveAddAndUpdateDTO>();
		for(UserInfoDTO userinfo :list){
			AnnReceiveAddAndUpdateDTO annDto = new AnnReceiveAddAndUpdateDTO();
			long annRecId = IdUtil.getUUID();
			idsList.add(annRecId);
			annDto.setId(annRecId);
			annDto.setReceiveUser(userinfo.getId());
			annDto.setAnnouncementId(annId);
			annrList.add(annDto);
		}
		annReceiveFeignClient.batchInsert(annrList);
		sendMessage(type,list,idsList,annId);
	}


}
