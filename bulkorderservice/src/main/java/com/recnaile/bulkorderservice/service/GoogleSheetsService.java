//package com.recnaile.bulkorderservice.service;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.SheetsScopes;
//import com.google.api.services.sheets.v4.model.*;
//import com.google.auth.http.HttpCredentialsAdapter;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.recnaile.bulkorderservice.model.BulkOrderDocument;
//import com.recnaile.bulkorderservice.model.BulkOrderRequest;
//import com.recnaile.bulkorderservice.repository.BulkOrderRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.GeneralSecurityException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@Service
//public class GoogleSheetsService {
//
//    private static final String APPLICATION_NAME = "Bulk Order Service";
//    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
//
//    @Value("${google.sheets.spreadsheet.id}")
//    private String spreadsheetId;
//
//    @Value("${google.sheets.credentials.file}")
//    private Resource credentialsFile;
//
//    @Value("${google.sheets.sheet.name:BulkOrders}")
//    private String sheetName;
//
//    @Autowired
//    private BulkOrderRepository bulkOrderRepository;
//
//    /**
//     * Creates an authorized Credential object.
//     */
//    private GoogleCredentials getCredentials() throws IOException {
//        try (InputStream in = credentialsFile.getInputStream()) {
//            return GoogleCredentials.fromStream(in)
//                    .createScoped(SCOPES);
//        }
//    }
//
//    /**
//     * Build and return an authorized Sheets API client service.
//     */
//    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getCredentials()))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//    public void saveBulkOrderToSheet(BulkOrderRequest request, String referenceId, String timestamp)
//            throws IOException, GeneralSecurityException {
//
//
//        Sheets sheetsService = getSheetsService();
//
//        // Check if sheet exists, create if not
//        ensureSheetExists(sheetsService);
//
//        // Prepare the data to append
//        List<List<Object>> values = prepareRowData(request, referenceId, timestamp);
//
//        // Create the value range and append request
//        ValueRange body = new ValueRange()
//                .setValues(values)
//                .setMajorDimension("ROWS");
//
//        // Append the data
//        sheetsService.spreadsheets().values()
//                .append(spreadsheetId, sheetName + "!A:A", body)
//                .setValueInputOption("USER_ENTERED")
//                .setInsertDataOption("INSERT_ROWS")
//                .execute();
//    }
//
//    private void ensureSheetExists(Sheets sheetsService) throws IOException {
//        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
//        boolean sheetExists = false;
//
//        for (Sheet sheet : spreadsheet.getSheets()) {
//            if (sheet.getProperties().getTitle().equals(sheetName)) {
//                sheetExists = true;
//                break;
//            }
//        }
//
//        if (!sheetExists) {
//            // Create the sheet with headers
//            AddSheetRequest addSheetRequest = new AddSheetRequest();
//            SheetProperties sheetProperties = new SheetProperties();
//            sheetProperties.setTitle(sheetName);
//            addSheetRequest.setProperties(sheetProperties);
//
//            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
//            batchUpdateRequest.setRequests(Collections.singletonList(
//                    new Request().setAddSheet(addSheetRequest)));
//
//            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
//
//            // Add headers
//            List<List<Object>> headers = Collections.singletonList(Arrays.asList(
//                    "Reference ID", "Timestamp", "Company Name", "Contact Person", "Email",
//                    "Phone", "Company Type", "Tax ID", "Selected Products", "Quantity",
//                    "Delivery Date", "Shipping Address", "Billing Address", "Payment Terms",
//                    "Additional Notes"
//            ));
//
//            ValueRange headerBody = new ValueRange()
//                    .setValues(headers)
//                    .setMajorDimension("ROWS");
//
//            sheetsService.spreadsheets().values()
//                    .update(spreadsheetId, sheetName + "!A1", headerBody)
//                    .setValueInputOption("USER_ENTERED")
//                    .execute();
//        }
//    }
//
//    private List<List<Object>> prepareRowData(BulkOrderRequest request, String referenceId, String timestamp) {
//        // Prepare selected products string
//        StringBuilder productsBuilder = new StringBuilder();
//        request.getSelectedProducts().forEach((category, subcategories) -> {
//            subcategories.forEach(subcategory -> {
//                if (productsBuilder.length() > 0) productsBuilder.append("; ");
//                productsBuilder.append(category).append(" - ").append(subcategory);
//            });
//        });
//
//        return Collections.singletonList(Arrays.asList(
//                referenceId,
//                timestamp,
//                request.getCompanyName(),
//                request.getContactPerson(),
//                request.getEmail(),
//                request.getPhone(),
//                request.getCompanyType(),
//                request.getTaxId(),
//                productsBuilder.toString(),
//                request.getQuantity(),
//                request.getDeliveryDate(),
//                request.getShippingAddress(),
//                request.getBillingAddress(),
//                request.getPaymentTerms(),
//                request.getAdditionalNotes()
//        ));
//    }
//    private void saveToMongoDB(BulkOrderRequest request, String referenceId, String timestamp) {
//        BulkOrderDocument document = new BulkOrderDocument();
//        document.setReferenceId(referenceId);
//        document.setTimestamp(LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME));
//        document.setCompanyName(request.getCompanyName());
//        document.setContactPerson(request.getContactPerson());
//        document.setEmail(request.getEmail());
//        document.setPhone(request.getPhone());
//        document.setQuantity(request.getQuantity());
//        document.setDeliveryDate(request.getDeliveryDate());
//        document.setAdditionalNotes(request.getAdditionalNotes());
//        document.setCompanyType(request.getCompanyType());
//        document.setTaxId(request.getTaxId());
//        document.setShippingAddress(request.getShippingAddress());
//        document.setBillingAddress(request.getBillingAddress());
//        document.setPaymentTerms(request.getPaymentTerms());
//        document.setSameAsShipping(request.isSameAsShipping());
//        document.setSelectedProducts(request.getSelectedProducts());
//
//        bulkOrderRepository.save(document);
//    }
//
//}


package com.recnaile.bulkorderservice.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.recnaile.bulkorderservice.model.BulkOrderDocument;
import com.recnaile.bulkorderservice.model.BulkOrderRequest;
import com.recnaile.bulkorderservice.repository.BulkOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GoogleSheetsService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSheetsService.class);
    private static final String APPLICATION_NAME = "Bulk Order Service";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    @Value("${google.sheets.spreadsheet.id}")
    private String spreadsheetId;

    @Value("${google.sheets.credentials.file}")
    private Resource credentialsFile;

    @Value("${google.sheets.sheet.name:BulkOrders}")
    private String sheetName;

    @Autowired
    private BulkOrderRepository bulkOrderRepository;

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

    /**
     * Saves bulk order data to both Google Sheets and MongoDB
     */
    @Transactional
    public void saveBulkOrder(BulkOrderRequest request, String referenceId, String timestamp) {
        try {
            saveToGoogleSheets(request, referenceId, timestamp);
            saveToMongoDB(request, referenceId, timestamp);
            logger.info("Successfully saved order with reference ID: {}", referenceId);
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Failed to save to Google Sheets for order: {}", referenceId, e);
            throw new RuntimeException("Failed to save order to Google Sheets", e);
        } catch (DataAccessException e) {
            logger.error("Failed to save to MongoDB for order: {}", referenceId, e);
            throw new RuntimeException("Failed to save order to MongoDB", e);
        }
    }

    private void saveToGoogleSheets(BulkOrderRequest request, String referenceId, String timestamp)
            throws IOException, GeneralSecurityException {
        Sheets sheetsService = getSheetsService();
        ensureSheetExists(sheetsService);

        List<List<Object>> values = prepareRowData(request, referenceId, timestamp);
        ValueRange body = new ValueRange()
                .setValues(values)
                .setMajorDimension("ROWS");

        sheetsService.spreadsheets().values()
                .append(spreadsheetId, sheetName + "!A:A", body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }

    private void saveToMongoDB(BulkOrderRequest request, String referenceId, String timestamp) {
        BulkOrderDocument document = new BulkOrderDocument();
        document.setReferenceId(referenceId);
        document.setTimestamp(LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME));
        document.setCompanyName(request.getCompanyName());
        document.setContactPerson(request.getContactPerson());
        document.setEmail(request.getEmail());
        document.setPhone(request.getPhone());
        document.setQuantity(request.getQuantity());
        document.setDeliveryDate(request.getDeliveryDate());
        document.setAdditionalNotes(request.getAdditionalNotes());
        document.setCompanyType(request.getCompanyType());
        document.setTaxId(request.getTaxId());
        document.setShippingAddress(request.getShippingAddress());
        document.setBillingAddress(request.getBillingAddress());
        document.setPaymentTerms(request.getPaymentTerms());
        document.setSameAsShipping(request.isSameAsShipping());
        document.setSelectedProducts(request.getSelectedProducts());

        bulkOrderRepository.save(document);
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
            createNewSheetWithHeaders(sheetsService);
        }
    }

    private void createNewSheetWithHeaders(Sheets sheetsService) throws IOException {
        // Create the sheet
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

    private List<List<Object>> prepareRowData(BulkOrderRequest request, String referenceId, String timestamp) {
        // Prepare selected products string
        String productsString = formatSelectedProducts(request.getSelectedProducts());

        return Collections.singletonList(Arrays.asList(
                referenceId,
                timestamp,
                request.getCompanyName(),
                request.getContactPerson(),
                request.getEmail(),
                request.getPhone(),
                request.getCompanyType(),
                request.getTaxId(),
                productsString,
                request.getQuantity(),
                request.getDeliveryDate(),
                request.getShippingAddress(),
                request.getBillingAddress(),
                request.getPaymentTerms(),
                request.getAdditionalNotes()
        ));
    }

    private String formatSelectedProducts(Map<String, List<String>> selectedProducts) {
        StringBuilder productsBuilder = new StringBuilder();
        selectedProducts.forEach((category, subcategories) -> {
            subcategories.forEach(subcategory -> {
                if (productsBuilder.length() > 0) {
                    productsBuilder.append("; ");
                }
                productsBuilder.append(category).append(" - ").append(subcategory);
            });
        });
        return productsBuilder.toString();
    }

    /**
     * Retrieves all orders from MongoDB
     */
    public List<BulkOrderDocument> getAllOrders() {
        return bulkOrderRepository.findAll();
    }

    /**
     * Finds an order by reference ID
     */
    public Optional<BulkOrderDocument> getOrderByReferenceId(String referenceId) {
        return bulkOrderRepository.findByReferenceId(referenceId);
    }

    // Add these methods to your existing GoogleSheetsService


    public List<BulkOrderDocument> getOrdersByProcessStatus(BulkOrderDocument.OrderProcessStatus status) {
        return bulkOrderRepository.findByProcessStatus(status);
    }

    public List<BulkOrderDocument> getOrdersByPaymentStatus(BulkOrderDocument.PaymentStatus status) {
        return bulkOrderRepository.findByPaymentStatus(status);
    }
}