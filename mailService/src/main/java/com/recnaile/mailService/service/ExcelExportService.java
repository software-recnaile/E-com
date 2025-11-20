package com.recnaile.mailService.service;


import com.recnaile.mailService.model.BulkOrderRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportService {

    public byte[] generateBulkOrderExcel(BulkOrderRequest orderRequest) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Create a single sheet with all data
            Sheet sheet = workbook.createSheet("Order Details");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Order Reference", "Company Name", "Company Type", "Tax ID",
                    "Contact Person", "Email", "Phone", "Product Category",
                    "Product Subcategory", "Quantity", "Special Requirements",
                    "Shipping Address", "Billing Address", "Same as Shipping",
                    "Payment Terms", "Additional Requirements", "Delivery Date"
            };

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Add data rows (one row per product)
            int rowNum = 1;
            for (BulkOrderRequest.Product product : orderRequest.getProducts()) {
                Row row = sheet.createRow(rowNum++);

                int col = 0;
                row.createCell(col++).setCellValue(orderRequest.getOrderReference());
                row.createCell(col++).setCellValue(orderRequest.getCompanyInfo().getName());
                row.createCell(col++).setCellValue(orderRequest.getCompanyInfo().getType());
                row.createCell(col++).setCellValue(orderRequest.getCompanyInfo().getTaxId());
                row.createCell(col++).setCellValue(orderRequest.getContactInfo().getPerson());
                row.createCell(col++).setCellValue(orderRequest.getContactInfo().getEmail());
                row.createCell(col++).setCellValue(orderRequest.getContactInfo().getPhone());
                row.createCell(col++).setCellValue(product.getCategory());
                row.createCell(col++).setCellValue(product.getSubcategory());
                row.createCell(col++).setCellValue(product.getQuantity());
                row.createCell(col++).setCellValue(product.getSpecialRequirements());
                row.createCell(col++).setCellValue(orderRequest.getShippingInfo().getShippingAddress());
                row.createCell(col++).setCellValue(orderRequest.getShippingInfo().getBillingAddress());
                row.createCell(col++).setCellValue(orderRequest.getShippingInfo().isSameAsShipping() ? "Yes" : "No");
                row.createCell(col++).setCellValue(orderRequest.getPaymentTerms());
                row.createCell(col++).setCellValue(orderRequest.getAdditionalRequirements());
                row.createCell(col++).setCellValue(orderRequest.getDeliveryDate());
            }

            // Auto-size all columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}