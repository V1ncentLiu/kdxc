package com.kuaidao.manageweb.demo.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.expression.Lists;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.demo.DTO.AddressReqDTO;
import com.kuaidao.manageweb.demo.DTO.PageTable;
import com.kuaidao.manageweb.demo.entity.Activity;
import com.kuaidao.manageweb.demo.entity.Address;
import com.kuaidao.manageweb.feign.SysFeign;


@RestController
@RequestMapping("/demo")
public class DemoController {
	private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
	
	//@PostConstruct
	public void initData() {
		String sql = "INSERT INTO address_test (area, name, address) VALUES (?, ?,?);";
		for (int i = 0; i < 100; i++) {
			Address address = new Address();
			address.setId("row_"+i);
			address.setArea("区域-"+i);
			address.setName("姓名-"+i);
			address.setAddress("北京-"+i);
		}
		
	}

	@RequestMapping("/get")
	public String  get() {
		logger.info("dddddddddddddd");
		return "success";
	}
	
	/**
	 * 不分页table
	 * @return
	 */
	@PostMapping("/listTableData")
	public List<Address>  listTableData(@RequestBody AddressReqDTO reqDTO) {
		logger.info(reqDTO.toString());
		List<Address> list = new ArrayList<Address>();
		for (int i = 0; i < 10; i++) {
			Address address = new Address();
			address.setId("row_"+i);
			address.setArea("区域-"+i);
			address.setName("姓名-"+i);
			address.setAddress("北京-"+i);
			list.add(address);
		}

		return list;
	}
	
	@GetMapping("/getTableNum")
	public int getTableNum(int id) {
		logger.info("id:"+id);
		return 22;
	}
	
	
	
	/**
	 * form 表單提交
	 * @param reqDTO
	 * @return
	 */
	List<Activity> cacheList = new ArrayList<>();
	AtomicInteger  atomicInteger = new AtomicInteger(1);
	@PostMapping("/submitForm")
	public int submitForm(@RequestBody Activity reqDTO) {
		logger.info(reqDTO.toString());
		reqDTO.setId(atomicInteger.incrementAndGet()+"");
		cacheList.add(reqDTO);
		return 1;
	}
	
	@GetMapping("/getFormData")
	public Activity getFormData(int id) {
	    for (Activity activity : cacheList) {
	    	if(Integer.parseInt(activity.getId())==id) {
				return activity;
			}
		}
	    if(cacheList.size()==0) {
	    	return null;
	    }
		return cacheList.get(cacheList.size()-1);
	}
	
	@RequestMapping("/listTableByPageNum")
	public PageTable<Address> listTableByPageNum(int pageSize,int pageNum){
		List<Address> list = new ArrayList<Address>();
		for (int i = 0; i < 100; i++) {
			Address address = new Address();
			address.setArea("区域-"+i);
			address.setName("姓名-"+i);
			address.setAddress("北京-"+i);
			list.add(address);
		}
		//模拟库中取出的数据
		List<Address> pageAddresses = list.subList(((pageNum-1)*pageSize), (pageNum-1)*pageSize+pageSize);
		
		PageTable<Address> pageTable = new PageTable<>(pageAddresses);
		pageTable.setCurrentPage(pageNum);
		pageTable.setTotal(list.size());
		pageTable.setPageSize(pageSize);
		
		int pageSizes = list.size()/pageSize == 0 ? list.size()/pageSize : list.size()/pageSize+1;
		
		pageTable.setPageSizes(pageSizes);
		logger.info(pageTable.toString());
		return pageTable;
	}
	
	
	@PostMapping("/listTableByPageNum2")
	public PageTable<Address> listTableByPageNum2(int pageSize,int pageNum,@RequestBody AddressReqDTO reqDTO){
		logger.info(reqDTO.toString());
		logger.info("pageSize{{}},pageNum{{}}",pageSize,pageNum);

	    List<Address> list = new ArrayList<Address>();
        for (int i = 0; i < 100; i++) {
            Address address = new Address();
            address.setArea("区域-"+i);
            address.setName("姓名-"+i);
            address.setAddress("北京-"+i);
            list.add(address);
        }
		
        int endIndex =  (pageNum-1)*pageSize+pageSize;
        endIndex = Math.min(endIndex, list.size());
		//模拟库中取出的数据
		List<Address> pageAddresses = list.subList(((pageNum-1)*pageSize),endIndex);
	
		int countNum = list.size();
		PageTable<Address> pageTable = new PageTable<>(pageAddresses);
		pageTable.setCurrentPage(pageNum);
		pageTable.setTotal(countNum);
		pageTable.setPageSize(pageSize);
		
		int pageSizes = countNum/pageSize == 0 ? countNum/pageSize : countNum/pageSize+1;
		
		pageTable.setPageSizes(pageSizes);
		logger.info(pageTable.toString());
		return pageTable;
	}
	
	

}

