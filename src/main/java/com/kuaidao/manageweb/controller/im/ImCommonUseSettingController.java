package com.kuaidao.manageweb.controller.im;



import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.custservice.dto.autoresponse.AutoResponseReqDto;
import com.kuaidao.custservice.dto.autoresponse.AutoResponseRespDto;
import com.kuaidao.custservice.dto.language.CommonLanguageReqDto;
import com.kuaidao.custservice.dto.language.CommonLanguageRespDto;
import com.kuaidao.manageweb.feign.autoresponse.AutoResponseFeignClient;
import com.kuaidao.manageweb.feign.language.CommonLanguageFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 接口层
 * Created  on 2020-9-1 10:42:23
 */
@Controller
@RequestMapping("/im")
public class ImCommonUseSettingController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AutoResponseFeignClient autoResponseFeignClient;
    @Autowired
    private CommonLanguageFeignClient commonLanguageFeignClient;

    /***
     * 初始化菜单管理页面
     *
     * @return
     */
    @RequestMapping("/imCommonUseSitting/index")
    public String imCommonUseSittingIndex(ModelMap modelMap) {
        //1、查询常用语
        CommonLanguageReqDto commonLanguageReqDto  = new CommonLanguageReqDto();
        JSONResult<List<CommonLanguageRespDto>> commonLanguageResult = commonLanguageFeignClient.queryListByType(commonLanguageReqDto);
        if (commonLanguageResult != null && JSONResult.SUCCESS.equals(commonLanguageResult.getCode()) && commonLanguageResult.getData() != null) {
            List<CommonLanguageRespDto> commonLanguageRespDtoList = commonLanguageResult.getData();
            Map<Integer, List<CommonLanguageRespDto>> comMap = ListUtils.emptyIfNull(commonLanguageRespDtoList).stream()
                    .filter(e -> e != null)
                    .collect(Collectors.groupingBy(CommonLanguageRespDto::getType));
            if(comMap.size()>0){
                List<CommonLanguageRespDto> userLangList = comMap.get(1);
                List<CommonLanguageRespDto> saleLangList = comMap.get(2);
                modelMap.put("userLangList",userLangList);
                modelMap.put("saleLangList",saleLangList);
            }
        } else {
            logger.error("query module tree,res{{}}", commonLanguageResult);
        }
        //2、查询自动提交
        AutoResponseReqDto dto = new AutoResponseReqDto();
        JSONResult<List<AutoResponseRespDto>> listJSONResult = autoResponseFeignClient.queryListByType(dto);
        if (listJSONResult != null && JSONResult.SUCCESS.equals(listJSONResult.getCode()) && listJSONResult.getData() != null) {
            List<AutoResponseRespDto> autoResponseRespDtoList = listJSONResult.getData();
            final Map<Integer, List<AutoResponseRespDto>> autoResponseMap;
            autoResponseMap = ListUtils.emptyIfNull(autoResponseRespDtoList).stream()
                    .filter(e -> e != null)
                    .collect(Collectors.groupingBy(AutoResponseRespDto::getType));
            if(autoResponseMap.size()>0){
                List<AutoResponseRespDto> kaichangList = autoResponseMap.get(1);
                List<AutoResponseRespDto> timeoutList = autoResponseMap.get(2);
                List<AutoResponseRespDto> noWorkList = autoResponseMap.get(3);
                List<AutoResponseRespDto> lixianList = autoResponseMap.get(4);
                modelMap.put("kaichangList",kaichangList);
                modelMap.put("timeOut", CollectionUtils.isNotEmpty(timeoutList)?timeoutList.get(0):null);
                modelMap.put("noWork",CollectionUtils.isNotEmpty(noWorkList)?noWorkList.get(0):null);
                modelMap.put("lixian",CollectionUtils.isNotEmpty(lixianList)?lixianList.get(0):null);
            }
        } else {
            logger.error("AutoResponseController imCommonUseSittingIndex tree,res{{}}", listJSONResult);
        }
        return "im/imCommonUseSitting";
    }




}
