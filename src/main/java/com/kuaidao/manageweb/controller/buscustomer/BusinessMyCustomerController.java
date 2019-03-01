package com.kuaidao.manageweb.controller.buscustomer;

import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerRespDTO;
import com.kuaidao.aggregation.dto.busmycustomer.MyCustomerParamDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.busmycustomer.BusMyCustomerFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * @author yangbiao
 * 接口层
 * Created  on 2019-2-12 15:06:38
 *  商务模块--我的客户
 */

@Controller
@RequestMapping("/aggregation/businessMyCustomer")
public class BusinessMyCustomerController {

    private static Logger logger = LoggerFactory.getLogger(BusinessMyCustomerController.class);

    @Autowired
    BusMyCustomerFeignClient busMyCustomerFeignClient;

    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request){
        logger.info("------------ 商务：我的客户列表 ---------------");
        //电销组
        List teleGroupList = new ArrayList();
        List teleSaleList = new ArrayList();
        List tasteProList = new ArrayList();

        MyCustomerParamDTO dto = new MyCustomerParamDTO();
        JSONResult<List<BusMyCustomerRespDTO>> resList = busMyCustomerFeignClient.queryList(dto);
        if(JSONResult.SUCCESS.equals(resList.getCode())){
            List<BusMyCustomerRespDTO> datas = resList.getData();
            for(BusMyCustomerRespDTO myCustomerRespDTO :datas){
                Map groupMap = new HashMap();
//                电销组
                teleGroupList.add(groupMap);
                groupMap.put("id",myCustomerRespDTO.getTeleGorupId());
                groupMap.put("name",myCustomerRespDTO.getTeleGorupName());
//                创业顾问
                Map saleMap = new HashMap();
                saleMap.put("id",myCustomerRespDTO.getTeleSaleId());
                saleMap.put("name",myCustomerRespDTO.getTeleSaleName());
                teleSaleList.add(saleMap);
//                品尝项目
                Map tasteProMap = new HashMap();
                tasteProMap.put("id",myCustomerRespDTO.getTasteProjectId());
                tasteProMap.put("name",myCustomerRespDTO.getTasteProjectName());
                tasteProList.add(tasteProMap);
            }
        }
        return "bus_mycustomer/mycustomerList";
    }

    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<BusMyCustomerRespDTO>> queryListPage(MyCustomerParamDTO param){
        return busMyCustomerFeignClient.queryPageList(param);
    }

}
