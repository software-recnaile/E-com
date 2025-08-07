package com.recnaile.mailService.model;



import lombok.Data;

import java.util.List;

@Data
public class DronePlanForm {
    private String email;
    private String requirements;
    private String droneType;
    private String budget;
    private String timeline;
    private List<String> features;


}
