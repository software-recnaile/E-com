package com.recnaile.mailService.model;



import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class DronePlanSubmission {
    private String serialNumber;
    private String email;
    private String requirements;
    private String droneType;
    private String budget;
    private String timeline;
    private String features;
    private String submittedAt;


}
