package com.recnaile.bulkorderservice.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.recnaile.bulkorderservice.model.BulkOrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "Bulk Order Service";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    @Value("${google.sheets.spreadsheet.id}")
    private String spreadsheetId;

    @Value("${google.sheets.credentials.file}")
    private Resource credentialsFile;

    @Value("${google.sheets.sheet.name:BulkOrders}")
    private String sheetName;

    /**
     * Creates an authorized Credential object.
     */
    private GoogleCredentials getCredentials() throws IOException {
        try (InputStream in = credentialsFile.getInputStream()) {
            return GoogleCredentials.fromStream(in)
                    .createScoped(SCOPES);
        }
    }

    /**
     * Build and return an authorized Sheets API client service.
     */
    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getCredentials()))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    public void saveBulkOrderToSheet(BulkOrderRequest request, String referenceId, String timestamp)
            throws IOException, GeneralSecurityException {


        Sheets sheetsService = getSheetsService();

        // Check if sheet exists, create if not
        ensureSheetExists(sheetsService);

        // Prepare the data to append
        List<List<Object>> values = prepareRowData(request, referenceId, timestamp);

        // Create the value range and append request
        ValueRange body = new ValueRange()
                .setValues(values)
                .setMajorDimension("ROWS");

        // Append the data
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, sheetName + "!A:A", body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }

    private void ensureSheetExists(Sheets sheetsService) throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        boolean sheetExists = false;

        for (Sheet sheet : spreadsheet.getSheets()) {
            if (sheet.getProperties().getTitle().equals(sheetName)) {
                sheetExists = true;
                break;
            }
        }

        if (!sheetExists) {
            // Create the sheet with headers
            AddSheetRequest addSheetRequest = new AddSheetRequest();
            SheetProperties sheetProperties = new SheetProperties();
            sheetProperties.setTitle(sheetName);
            addSheetRequest.setProperties(sheetProperties);

            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
            batchUpdateRequest.setRequests(Collections.singletonList(
                    new Request().setAddSheet(addSheetRequest)));

            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

            // Add headers
            List<List<Object>> headers = Collections.singletonList(Arrays.asList(
                    "Reference ID", "Timestamp", "Company Name", "Contact Person", "Email",
                    "Phone", "Company Type", "Tax ID", "Selected Products", "Quantity",
                    "Delivery Date", "Shipping Address", "Billing Address", "Payment Terms",
                    "Additional Notes"
            ));

            ValueRange headerBody = new ValueRange()
                    .setValues(headers)
                    .setMajorDimension("ROWS");

            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, sheetName + "!A1", headerBody)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
        }
    }

    private List<List<Object>> prepareRowData(BulkOrderRequest request, String referenceId, String timestamp) {
        // Prepare selected products string
        StringBuilder productsBuilder = new StringBuilder();
        request.getSelectedProducts().forEach((category, subcategories) -> {
            subcategories.forEach(subcategory -> {
                if (productsBuilder.length() > 0) productsBuilder.append("; ");
                productsBuilder.append(category).append(" - ").append(subcategory);
            });
        });

        return Collections.singletonList(Arrays.asList(
                referenceId,
                timestamp,
                request.getCompanyName(),
                request.getContactPerson(),
                request.getEmail(),
                request.getPhone(),
                request.getCompanyType(),
                request.getTaxId(),
                productsBuilder.toString(),
                request.getQuantity(),
                request.getDeliveryDate(),
                request.getShippingAddress(),
                request.getBillingAddress(),
                request.getPaymentTerms(),
                request.getAdditionalNotes()
        ));
    }
}