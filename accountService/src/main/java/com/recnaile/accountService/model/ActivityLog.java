//package com.recnaile.accountService.model;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.time.LocalDateTime;
//
//@Data
//@Document(collection = "order_activity_logs")
//public class ActivityLog {
//    @Id
//    private String id;
//    private String email;
//
//    private String activityType; // LOGIN, ORDER_CREATED, ORDER_UPDATED, etc.
//    private String description;
//    private LocalDateTime timestamp;
//}


package com.recnaile.accountService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order_activity_logs")
public class ActivityLog {
    @Id
    private String id;
    private String email;

    private String activityType; // e.g., "LOGIN", "ORDER_CREATED", "ORDER_UPDATED"
    private String description;
    private LocalDateTime timestamp;
}