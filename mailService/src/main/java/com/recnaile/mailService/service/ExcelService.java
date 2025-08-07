package com.recnaile.mailService.service;

import com.recnaile.mailService.model.BulkOrderRequest;
import com.recnaile.mailService.model.DronePlanForm;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

@Service
public class ExcelService {

    @Value("${app.excel.file-path}")
    private String excelFilePath;

    public void saveToExcel(DronePlanForm form, String referenceId, String timestamp) throws IOException {
        // Ensure directory exists
        Path path = Paths.get(excelFilePath);
        Files.createDirectories(path.getParent());

        Workbook workbook;
        File file = new File(excelFilePath);

        // Create new workbook if file doesn't exist
        if (!file.exists()) {
            workbook = new XSSFWorkbook();
            createHeaderRow(workbook.createSheet("Submissions"));
        } else {
            // Read existing workbook
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = WorkbookFactory.create(fis);
            }
        }

        Sheet sheet = workbook.getSheet("Submissions");
        if (sheet == null) {
            sheet = workbook.createSheet("Submissions");
            createHeaderRow(sheet);
        }

        // Create new row with form data
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        createDataRow(row, form, referenceId, timestamp);

        // Auto-size columns for better readability
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        } finally {
            workbook.close();
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Reference ID", "Timestamp", "Email", "Drone Type",
                "Requirements", "Features", "Budget", "Timeline"
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

    private void createDataRow(Row row, DronePlanForm form, String referenceId, String timestamp) {
        row.createCell(0).setCellValue(referenceId);
        row.createCell(1).setCellValue(timestamp);
        row.createCell(2).setCellValue(form.getEmail());
        row.createCell(3).setCellValue(form.getDroneType());
        row.createCell(4).setCellValue(form.getRequirements());
        row.createCell(5).setCellValue(String.join(", ", form.getFeatures()));
        row.createCell(6).setCellValue(form.getBudget());
        row.createCell(7).setCellValue(form.getTimeline());
    }


}