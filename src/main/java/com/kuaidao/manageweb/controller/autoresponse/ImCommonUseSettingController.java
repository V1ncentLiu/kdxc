package com.kuaidao.manageweb.controller.autoresponse;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 接口层
 * Created  on 2020-9-1 10:42:23
 */
@Controller
@RequestMapping("/imCommonUseSitting")
public class ImCommonUseSettingController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private AutoResponseFeignClient autoResponseFeignClient;
//    @Autowired
//    private CommonLanguageFeignClient commonLanguageFeignClient;
//
//    /***
//     * 初始化菜单管理页面
//     *
//     * @return
//     */
//    @RequestMapping("/index")
//    public String imCommonUseSittingIndex(ModelMap modelMap) {
//        //1、查询常用语
//        CommonLanguageReqDto commonLanguageReqDto  = new CommonLanguageReqDto();
//        commonLanguageReqDto.setType(0);
//        JSONResult<List<CommonLanguageRespDto>> commonLanguageResult = commonLanguageFeignClient.queryListByType(commonLanguageReqDto);
//        if (commonLanguageResult != null && JSONResult.SUCCESS.equals(commonLanguageResult.getCode()) && commonLanguageResult.getData() != null) {
//            List<CommonLanguageRespDto> commonLanguageRespDtoList = commonLanguageResult.getData();
//            modelMap.put("commonLanguageRespDtoList",commonLanguageRespDtoList);
//        } else {
//            logger.error("query module tree,res{{}}", commonLanguageResult);
//        }
//        //2、查询自动提交
//        AutoResponseReqDto dto = new AutoResponseReqDto();
//        JSONResult<List<AutoResponseRespDto>> listJSONResult = autoResponseFeignClient.queryListByType(dto);
//        if (listJSONResult != null && JSONResult.SUCCESS.equals(listJSONResult.getCode()) && listJSONResult.getData() != null) {
//            List<AutoResponseRespDto> autoResponseRespDtoList = listJSONResult.getData();
//            final Map<Integer, List<AutoResponseRespDto>> autoResponseMap = ListUtils.emptyIfNull(autoResponseRespDtoList).stream()
//                    .filter(e -> e != null)
//                    .collect(Collectors.groupingBy(AutoResponseRespDto::getType));
//            modelMap.put("autoResponseMap",autoResponseMap);
//        } else {
//            logger.error("AutoResponseController imCommonUseSittingIndex tree,res{{}}", listJSONResult);
//        }
//        return "/im/imCommonUseSitting";
//    }

    /***
     * 初始化菜单管理页面
     *
     * @return
     */
    @RequestMapping("/imCommonUseSitting")
    public String imCommonUseSittingTestIndex(ModelMap modelMap) {

        return "im/imCommonUseSitting";
    }


}
