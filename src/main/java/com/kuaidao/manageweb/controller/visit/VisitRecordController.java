package com.kuaidao.manageweb.controller.visit;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.pagehelper.PageHelper;
import com.kuaidao.aggregation.dto.visitrecord.RejectVisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitRecordRespDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;

/**
 *  訪問記錄
 * @author  Chen
 * @date 2019年3月4日 下午1:40:44   
 * @version V1.0
 */
@Controller
@RequestMapping("/visit/visitRecord")
public class VisitRecordController {

    @RequestMapping("/visitRecordPage")
    public String visitRecordPage() {
        return "visit/visitRecord";
    }
    
    /**
     * 查询 客户到访记录
     * @param visitRecordReqDTO
     * @return
     */
    @PostMapping("/listVisitRecord")
    @ResponseBody
    public JSONResult<PageBean<VisitRecordRespDTO>> listVisitRecord(@RequestBody VisitRecordReqDTO visitRecordReqDTO){
       return null;
    }
    
    /**
     * 驳回签约单
     * @return
     */
    @PostMapping("/rejectVisitRecord")
    @ResponseBody
    public JSONResult<Boolean> rejectVisitRecord(@Valid @RequestBody RejectVisitRecordReqDTO reqDTO,BindingResult result){
        if(result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
      
      return null;
    }
}
