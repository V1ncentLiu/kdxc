package com.kuaidao.manageweb.demo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kuaidao.manageweb.demo.entity.Address;

@Controller
@RequestMapping("/path")
public class PathController {
	
	@RequestMapping("/index")
	public  String index() {
		return "index";
	}
	
	/**
	 * 不分页table
	 * @return
	 */
	@RequestMapping("/tableNoPage")
	public String tableNoPage() {
		return "tableNoPage";
	}
	
	
	 
	@RequestMapping("/form")
	public String form() {
		return "demo/form";
	}
	
	@RequestMapping("/tablePage")
	public String tablePage() {
		return "tablePage";
	}
	
	
	@RequestMapping("/tableOpt")
	public String tableOpt() {
		return "demo/tableOpt";
	}
	
	@RequestMapping("/router")
	public String router() {
		return "router";
	}
	
	@RequestMapping("/brandList")
	public String brandList() {
		return "demo/brandList";
	}
	
	@RequestMapping("/thymeleafTable")
	public String thymeleafTable(HttpServletRequest request) {
		
		List<Address> list = new ArrayList<Address>();
		for (int i = 0; i < 10; i++) {
			Address address = new Address();
			address.setArea("区域-"+i);
			address.setName("姓名-"+i);
			address.setAddress("北京-"+i);
			list.add(address);
		}
		request.setAttribute("tableList",list);
		request.setAttribute("num","这是thymeleaf渲染的");
		return "thymeleafTable";
	}
	
	
	@RequestMapping("/dynamic_column_table")
	public String dynamic_column_table() {
		return "demo/dynamic_column_table";
	}
	
	@RequestMapping("/table_page")
	public String table_page(){
	    return "demo/table_page" ;
	}

}
