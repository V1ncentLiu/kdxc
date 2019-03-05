package com.kuaidao.manageweb.controller.visitrecord;

import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordRespDTO;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.visitrecord.BusVisitRecordFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangbiao
 * @Date: 2019/1/2 15:14
 * @Description:
 *      到访记录
 */

@Controller
@RequestMapping("/busVisitRecord")
public class BusinessVisitRecordController {

    private static Logger logger = LoggerFactory.getLogger(BusinessVisitRecordController.class);

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private BusVisitRecordFeignClient visitRecordFeignClient;


    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request,@RequestParam String clueId){
        BusVisitRecordReqDTO recordReqDTO = new BusVisitRecordReqDTO();
        recordReqDTO.setClueId(Long.valueOf(clueId));
        JSONResult<List<BusVisitRecordRespDTO>> listJSONResult = visitRecordFeignClient.queryList(recordReqDTO);
        List<BusVisitRecordRespDTO> data = new ArrayList<>();
        String notSignReason="";
        if(JSONResult.SUCCESS.equals(listJSONResult.getCode())){
            data = listJSONResult.getData();
            if(data!=null&&data.size()>0){
                notSignReason =  data.get(data.size()-1).getNotSignReason();
            }
        }
//      查询到访状态： 首次到访  2次到访  多次到访  （需要添加接口）

        request.setAttribute("tableData",data);
        request.setAttribute("notSignReason",notSignReason);
        return "bus_mycustomer/showVisitRecord";
    }


    /**
     * 新增
     */
    @RequestMapping("/insert")
    @ResponseBody
    public JSONResult<Boolean> saveVisitRecord(@Valid @RequestBody BusVisitRecordInsertOrUpdateDTO dto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return visitRecordFeignClient.saveVisitRecord(dto);
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    @ResponseBody
    public JSONResult<Boolean> updateVisitRecord(@Valid @RequestBody BusVisitRecordInsertOrUpdateDTO dto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return visitRecordFeignClient.updateVisitRecord(dto);
    }

    /**
     * 查询 不分页
     */
    @RequestMapping("/queryList")
    @ResponseBody
    public JSONResult<List<BusVisitRecordRespDTO>> queryList(@RequestBody BusVisitRecordReqDTO dto) throws Exception {
        return visitRecordFeignClient.queryList(dto);
    }


    /**
     *  查询明细
     */
    @RequestMapping("/one")
    @ResponseBody
    public JSONResult<BusVisitRecordRespDTO> queryOne(@RequestBody IdEntityLong idEntityLong) throws Exception {
        return visitRecordFeignClient.queryOne(idEntityLong);
    }

    /**
     *  跳转到 到访记录明细页面
     */
    @RequestMapping("/visitRecordPage")
    public String visitRecordPage(HttpServletRequest request,@RequestParam String clueId , @RequestParam String visitStatus) throws Exception {
        request.setAttribute("clueId",clueId);
        request.setAttribute("visitStatus",visitStatus);
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("proSelect", proJson.getData());
        }
        return  "bus_mycustomer/showVisitRecord";
    }

    /**
     *  到访记录，新增时候回显信息
     */
    @RequestMapping("/echo")
    @ResponseBody
    public JSONResult<BusVisitRecordRespDTO> echo(@RequestBody IdEntityLong idEntityLong) throws Exception {
        BusVisitRecordRespDTO recordRespDTO = new BusVisitRecordRespDTO();
//      查询需要进行回显的信息，并进行映射

        return new JSONResult<BusVisitRecordRespDTO>().success(recordRespDTO);
    }


}
