package com.kuaidao.manageweb.controller.deptCallSet;

import com.kuaidao.aggregation.dto.deptCallSet.DeptCallSetAddAndUpdateDTO;
import com.kuaidao.aggregation.dto.deptCallSet.DeptCallSetQueryDTO;
import com.kuaidao.aggregation.dto.deptCallSet.DeptCallSetRespDTO;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.deptcallset.DeptCallSetFeignClient;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      部门呼叫设置
 */

@Controller
@RequestMapping("/deptcallset")
public class DeptCallSetController {

    private static Logger logger = LoggerFactory.getLogger(DeptCallSetController.class);


    @Autowired
    DeptCallSetFeignClient deptCallSetFeignClient;

    @RequestMapping("/deptcallsetPage")
    public String pageIndex(){
        return "deptCellSet/deptCallSetList";
    }


    @RequestMapping("/saveOne")
    @ResponseBody
    public JSONResult insertOne(@Valid @RequestBody DeptCallSetAddAndUpdateDTO dto , BindingResult result){
        if (result.hasErrors()) return  CommonUtil.validateParam(result);

        dto.setCreateTime(new Date());
        dto.setCreateUser(1084621842175623168L);

        return deptCallSetFeignClient.saveDeptCallSet(dto);
}

    @RequestMapping("/updateAbnoramlUser")
    @ResponseBody
    public JSONResult updateAbnoramlUser(@RequestBody DeptCallSetAddAndUpdateDTO dto){
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(1084621842175623168L);
        return deptCallSetFeignClient.updateDeptCallSets(dto);
    }

    @PostMapping("/queryAbnoramlUsers")
    @ResponseBody
    public JSONResult<PageBean<DeptCallSetRespDTO>> queryDictionary(@RequestBody DeptCallSetQueryDTO dto){
        return deptCallSetFeignClient.queryDeptCallSetList(dto);
    }

    @PostMapping("/import")
    @ResponseBody
    public JSONResult<PageBean<DeptCallSetRespDTO>> queryDictionary(@RequestBody List<DeptCallSetAddAndUpdateDTO> list){
        /**
         * 进行数据简单
         */
        List<DeptCallSetAddAndUpdateDTO> insertList = new ArrayList();
        for(DeptCallSetAddAndUpdateDTO dto:list){

//          进行数据长度检验
//          进行组织机构检验


            dto.setId(IdUtil.getUUID());
            dto.setCreateUser(1084621842175623168L);
            dto.setCreateTime(new Date());
            insertList.add(dto);
        }
        return deptCallSetFeignClient.importDeptCallSets(insertList);
    }

}
