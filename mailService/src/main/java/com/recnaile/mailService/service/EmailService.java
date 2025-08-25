package com.recnaile.mailService.service;

import com.recnaile.mailService.model.BulkOrderRequest;
import com.recnaile.mailService.model.DronePlanForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendDronePlanEmail(DronePlanForm form, String referenceId, String timestamp, boolean isConfirmation)  {
        Context context = new Context();
        context.setVariable("isConfirmation", isConfirmation);
        context.setVariable("referenceId", referenceId);
        context.setVariable("email", form.getEmail());
        context.setVariable("requirements", form.getRequirements());
        context.setVariable("droneType", form.getDroneType());
        context.setVariable("budget", form.getBudget());
        context.setVariable("timeline", form.getTimeline());
        context.setVariable("features", form.getFeatures()); // This should be a List<String>
        context.setVariable("submittedAt", timestamp);

        String emailContent = templateEngine.process("email-template", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);


        try {
            helper.setFrom(fromEmail);
            helper.setTo(isConfirmation ? form.getEmail() : adminEmail);
            helper.setSubject(isConfirmation ?
                    "Thank you for your custom drone plan request #" + referenceId :
                    "New Drone Plan Request #" + referenceId);
            helper.setText(emailContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }




}