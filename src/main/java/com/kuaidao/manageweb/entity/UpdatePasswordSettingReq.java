package com.kuaidao.manageweb.entity;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class UpdatePasswordSettingReq implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String passwordExpires;
    private List<String> reminderTime;
}
