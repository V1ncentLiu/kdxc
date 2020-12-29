package com.kuaidao.manageweb.feign.project;

import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.entity.JSONResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectWapper {

    @Autowired
    ProjectInfoFeignClient projectInfoFeignClient;

    public List<ProjectInfoDTO> allProject(){
        JSONResult<List<ProjectInfoDTO>> result = projectInfoFeignClient.allProject();
        List<ProjectInfoDTO> data = result.data();
        return data;
    }

    public  List<ProjectInfoDTO> signProject(List<ProjectInfoDTO> data){
        if(CollectionUtils.isEmpty(data)){
            return null;
        }
        List<ProjectInfoDTO> collect = data.stream().filter(a -> {
            return  a.getIsNotSign()==null||a.getIsNotSign() == 0;
        }).collect(Collectors.toList());
        return collect;
    }

    public  List<String > projectCategory(List<ProjectInfoDTO> data){
        if(CollectionUtils.isEmpty(data)){
            return null;
        }
        List<String> collect = data.stream().map(a->a.getCategory()).collect(Collectors.toList());
        return collect;
    }


}
