package com.recnaile.bulkorderservice.service;

import com.recnaile.bulkorderservice.model.BulkOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

    public void sendBulkOrderEmail(BulkOrderRequest request, String referenceId,
                                   String timestamp, boolean isConfirmation) {
        try {
            Context context = new Context();
            context.setVariable("isConfirmation", isConfirmation);
            context.setVariable("referenceId", referenceId);
            context.setVariable("companyName", request.getCompanyName());
            context.setVariable("contactPerson", request.getContactPerson());
            context.setVariable("email", request.getEmail());
            context.setVariable("phone", request.getPhone());
            context.setVariable("selectedProducts", request.getSelectedProducts());
            context.setVariable("quantity", request.getQuantity());
            context.setVariable("deliveryDate", request.getDeliveryDate());
            context.setVariable("shippingAddress", request.getShippingAddress());
            context.setVariable("billingAddress", request.getBillingAddress());
            context.setVariable("paymentTerms", request.getPaymentTerms());
            context.setVariable("additionalNotes", request.getAdditionalNotes());
            context.setVariable("submittedAt", timestamp);

            String emailContent = templateEngine.process("bulk-order-email-template", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setFrom(fromEmail);
            helper.setTo(isConfirmation ? request.getEmail() : adminEmail);
            helper.setSubject(isConfirmation ?
                    "Thank you for your bulk order request #" + referenceId :
                    "New Bulk Order Request #" + referenceId);
            helper.setText(emailContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send bulk order email", e);
        }
    }
}