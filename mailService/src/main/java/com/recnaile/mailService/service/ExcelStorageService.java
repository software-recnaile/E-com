package com.recnaile.mailService.service;

import com.recnaile.mailService.model.BulkOrderRequest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

@Service
public class ExcelStorageService {

    private final Path storagePath;
    private final Object lock = new Object();

    public ExcelStorageService(@Value("${excel.storage.path}") String storagePath) {
        this.storagePath = Paths.get(storagePath);
        try {
            Files.createDirectories(this.storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory", e);
        }
    }

    public void appendToMasterExcel(BulkOrderRequest orderRequest) {
        synchronized (lock) {
            Path masterFile = storagePath.resolve("master_orders.xlsx");

            try (Workbook workbook = getOrCreateWorkbook(masterFile);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                // Get or create the "Orders" sheet
                Sheet sheet = workbook.getSheet("Orders");
                if (sheet == null) {
                    sheet = workbook.createSheet("Orders");
                    createHeaderRow(sheet);
                }

                // Append the new order data
                appendOrderData(sheet, orderRequest);

                // Save the workbook
                workbook.write(outputStream);
                Files.write(masterFile, outputStream.toByteArray(), StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);

            } catch (IOException e) {
                throw new RuntimeException("Failed to update master Excel file", e);
            }
        }
    }

    private Workbook getOrCreateWorkbook(Path file) throws IOException {
        if (Files.exists(file)) {
            try (InputStream inputStream = Files.newInputStream(file)) {
                return new XSSFWorkbook(inputStream);
            }
        }
        return new XSSFWorkbook();
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Timestamp", "Order Reference", "Company Name", "Company Type", "Tax ID",
                "Contact Person", "Email", "Phone", "Product Category", "Product Subcategory",
                "Quantity", "Special Requirements", "Shipping Address", "Billing Address",
                "Same as Shipping", "Payment Terms", "Additional Requirements", "Delivery Date"
        };

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    private void appendOrderData(Sheet sheet, BulkOrderRequest orderRequest) {
        for (BulkOrderRequest.Product product : orderRequest.getProducts()) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);

            int col = 0;
            row.createCell(col++).setCellValue(new Date().toString());
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
    }

    public byte[] getMasterExcel() throws IOException {
        Path masterFile = storagePath.resolve("master_orders.xlsx");
        if (Files.exists(masterFile)) {
            return Files.readAllBytes(masterFile);
        }
        return new byte[0];
    }
}
