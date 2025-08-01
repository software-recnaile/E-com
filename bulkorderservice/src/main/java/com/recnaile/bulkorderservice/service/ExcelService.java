package com.recnaile.bulkorderservice.service;

import com.recnaile.bulkorderservice.model.BulkOrderRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {

    @Value("${app.excel.bulk-orders-file-path}")
    private String excelFilePath;

    public void saveBulkOrderToExcel(BulkOrderRequest request, String referenceId, String timestamp) throws IOException {
        // Ensure directory exists
        Path path = Paths.get(excelFilePath);
        Files.createDirectories(path.getParent());

        Workbook workbook;
        File file = new File(excelFilePath);

        // Create new workbook if file doesn't exist
        if (!file.exists()) {
            workbook = new XSSFWorkbook();
            createBulkOrderHeaderRow(workbook.createSheet("BulkOrders"));
        } else {
            // Read existing workbook
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = WorkbookFactory.create(fis);
            }
        }

        Sheet sheet = workbook.getSheet("BulkOrders");
        if (sheet == null) {
            sheet = workbook.createSheet("BulkOrders");
            createBulkOrderHeaderRow(sheet);
        }

        // Create new row with form data
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        createBulkOrderDataRow(row, request, referenceId, timestamp);

        // Auto-size columns for better readability
        for (int i = 0; i < 15; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        } finally {
            workbook.close();
        }
    }

    private void createBulkOrderHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Reference ID", "Timestamp", "Company Name", "Contact Person", "Email",
                "Phone", "Company Type", "Tax ID", "Selected Products", "Quantity",
                "Delivery Date", "Shipping Address", "Billing Address", "Payment Terms",
                "Additional Notes"
        };

        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void createBulkOrderDataRow(Row row, BulkOrderRequest request, String referenceId, String timestamp) {
        int cellNum = 0;

        // Basic information
        row.createCell(cellNum++).setCellValue(referenceId);
        row.createCell(cellNum++).setCellValue(timestamp);
        row.createCell(cellNum++).setCellValue(request.getCompanyName());
        row.createCell(cellNum++).setCellValue(request.getContactPerson());
        row.createCell(cellNum++).setCellValue(request.getEmail());
        row.createCell(cellNum++).setCellValue(request.getPhone());
        row.createCell(cellNum++).setCellValue(request.getCompanyType());
        row.createCell(cellNum++).setCellValue(request.getTaxId());

        // Selected products as comma-separated string
        StringBuilder productsBuilder = new StringBuilder();
        request.getSelectedProducts().forEach((category, subcategories) -> {
            subcategories.forEach(subcategory -> {
                if (productsBuilder.length() > 0) productsBuilder.append("; ");
                productsBuilder.append(category).append(" - ").append(subcategory);
            });
        });
        row.createCell(cellNum++).setCellValue(productsBuilder.toString());

        // Order details
        row.createCell(cellNum++).setCellValue(request.getQuantity());
        row.createCell(cellNum++).setCellValue(request.getDeliveryDate());
        row.createCell(cellNum++).setCellValue(request.getShippingAddress());
        row.createCell(cellNum++).setCellValue(request.getBillingAddress());
        row.createCell(cellNum++).setCellValue(request.getPaymentTerms());
        row.createCell(cellNum).setCellValue(request.getAdditionalNotes());
    }
}