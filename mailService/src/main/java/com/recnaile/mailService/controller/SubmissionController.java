


// package com.recnaile.mailService.controller;

// import com.recnaile.mailService.model.DronePlanForm;
// import com.recnaile.mailService.service.EmailService;
// import com.recnaile.mailService.service.ExcelService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.UUID;

// @RestController
// @RequestMapping("/api/submissions")
// public class SubmissionController {

//     @Autowired
//     private EmailService emailService;

//     @Autowired
//     private ExcelService excelService;

//     @PostMapping
//     public ResponseEntity<String> submitDronePlan(@RequestBody DronePlanForm form) {
//         try {
//             // Generate reference ID
//             String referenceId = "DRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//             String timestamp = LocalDateTime.now()
//                     .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

//             // Save to Excel
//             excelService.saveToExcel(form, referenceId, timestamp);

//             // Send emails
//             emailService.sendDronePlanEmail(form, referenceId, timestamp, false); // To admin
//             emailService.sendDronePlanEmail(form, referenceId, timestamp, true); // To customer

//             return ResponseEntity.ok("{\"message\": \"Submission processed successfully\", \"referenceId\": \"" +
//                     referenceId + "\"}");
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError()
//                     .body("{\"error\": \"Error processing submission: " + e.getMessage() + "\"}");
//         }
//     }




package com.recnaile.mailService.controller;

import com.recnaile.mailService.model.DronePlanForm;
import com.recnaile.mailService.service.EmailService;
import com.recnaile.mailService.service.GoogleSheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private GoogleSheetsService googleSheetsService;

    @PostMapping
    public ResponseEntity<String> submitDronePlan(@RequestBody DronePlanForm form) {
        try {
            // Generate reference ID
            String referenceId = "DRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            // Save to Google Sheet
            googleSheetsService.saveToSheet(form, referenceId, timestamp);

            // Send emails
            emailService.sendDronePlanEmail(form, referenceId, timestamp, false); // To admin
            emailService.sendDronePlanEmail(form, referenceId, timestamp, true); // To customer

            return ResponseEntity.ok("{\"message\": \"Submission processed successfully\", \"referenceId\": \"" +
                    referenceId + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Error processing submission: " + e.getMessage() + "\"}");
        }
    }
}



// }
