package com.recnaile.bulkorderservice.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "Bulk Order Service";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    private final String spreadsheetId;
    private final String sheetName;
    private final Environment env;
    private final BulkOrderRepository bulkOrderRepository;
    private final MongoTemplate accountMongoTemplate;

    @Autowired
    public GoogleSheetsService(
            @Value("${google.sheets.spreadsheet.id}") String spreadsheetId,
            @Value("${google.sheets.sheet.name:BulkOrders}") String sheetName,
            Environment env,
            @Qualifier("accountMongoTemplate") MongoTemplate accountMongoTemplate,
            BulkOrderRepository bulkOrderRepository) {
        this.spreadsheetId = spreadsheetId;
        this.sheetName = sheetName;
        this.env = env;
        this.accountMongoTemplate = accountMongoTemplate;
        this.bulkOrderRepository = bulkOrderRepository;
        log.info("GoogleSheetsService initialized with spreadsheet: {}, sheet: {}", spreadsheetId, sheetName);
    }

    @Transactional
    public void saveBulkOrder(BulkOrderRequest request, String referenceId, String timestamp) {
        log.info("Attempting to save bulk order with reference ID: {}", referenceId);

        try {
            saveToGoogleSheets(request, referenceId, timestamp);
            saveToAccountDB(request, referenceId, timestamp);
            log.info("Successfully saved order with reference ID: {} to both Google Sheets and account-db", referenceId);
        } catch (IOException | GeneralSecurityException e) {
            log.error("Failed to save to Google Sheets for order: {}", referenceId, e);
            throw new RuntimeException("Failed to save order to Google Sheets", e);
        } catch (DataAccessException e) {
            log.error("Failed to save to account-db for order: {}", referenceId, e);
            throw new RuntimeException("Failed to save order to account-db", e);
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

        // Use the full range A:P to ensure all columns are properly handled
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, sheetName + "!A:P", body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .execute();

        log.debug("Successfully saved order {} to Google Sheets", referenceId);
    }

    private void saveToAccountDB(BulkOrderRequest request, String referenceId, String timestamp) {
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

        // Use accountMongoTemplate to save to account-db
        accountMongoTemplate.save(document, "bulk_orders");
        log.debug("Successfully saved order {} to account-db", referenceId);
    }

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Sheets sheetsService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            log.debug("Google Sheets service initialized successfully");
            return sheetsService;
        } catch (GeneralSecurityException e) {
            log.error("Security exception while initializing Google Sheets service", e);
            throw e;
        }
    }

    private HttpRequestInitializer getCredentials() throws IOException {
        // Try multiple environment variable names for maximum compatibility
        String credentialsJson = null;

        // Check all possible environment variable names
        String[] possibleEnvVars = {
                "GOOGLE_CREDENTIALS_JSON",
                "GOOGLE_CREDENTIALS",
                "GCP_CREDENTIALS",
                "GOOGLE_SERVICE_ACCOUNT_JSON"
        };

        for (String envVar : possibleEnvVars) {
            credentialsJson = env.getProperty(envVar);
            if (credentialsJson != null && !credentialsJson.trim().isEmpty()) {
                log.info("Found credentials in environment variable: {}", envVar);
                break;
            }

            // Also check system properties as fallback
            credentialsJson = System.getProperty(envVar);
            if (credentialsJson != null && !credentialsJson.trim().isEmpty()) {
                log.info("Found credentials in system property: {}", envVar);
                break;
            }
        }

        if (credentialsJson == null || credentialsJson.trim().isEmpty()) {
            String errorMsg = "Google credentials not found. Checked environment variables: " +
                    String.join(", ", possibleEnvVars) +
                    ". Please set GOOGLE_CREDENTIALS_JSON environment variable.";
            log.error(errorMsg);
            throw new IOException(errorMsg);
        }

        // Clean up the JSON string (remove any extra quotes or spaces)
        credentialsJson = credentialsJson.trim();
        if (credentialsJson.startsWith("\"") && credentialsJson.endsWith("\"")) {
            credentialsJson = credentialsJson.substring(1, credentialsJson.length() - 1);
        }

        // Replace escaped newlines if present
        credentialsJson = credentialsJson.replace("\\n", "\n");

        log.info("Successfully loaded Google credentials (length: {})", credentialsJson.length());

        try (InputStream in = new ByteArrayInputStream(credentialsJson.getBytes())) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(SCOPES);
            log.info("Google credentials successfully authenticated");
            return new HttpCredentialsAdapter(credentials);
        } catch (IOException e) {
            log.error("Failed to parse Google credentials JSON. First 100 chars: {}",
                    credentialsJson.substring(0, Math.min(100, credentialsJson.length())), e);
            throw new IOException("Failed to parse Google credentials JSON. Please check the format.", e);
        }
    }

    private void ensureSheetExists(Sheets sheetsService) throws IOException {
        try {
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            boolean sheetExists = spreadsheet.getSheets().stream()
                    .anyMatch(sheet -> sheet.getProperties().getTitle().equals(sheetName));

            if (!sheetExists) {
                log.info("Sheet '{}' does not exist, creating with headers", sheetName);
                createNewSheetWithHeaders(sheetsService);
            } else {
                log.debug("Sheet '{}' already exists", sheetName);
            }
        } catch (IOException e) {
            log.error("Failed to check if sheet exists: {}", sheetName, e);
            throw e;
        }
    }

    private void createNewSheetWithHeaders(Sheets sheetsService) throws IOException {
        try {
            AddSheetRequest addSheetRequest = new AddSheetRequest();
            SheetProperties sheetProperties = new SheetProperties();
            sheetProperties.setTitle(sheetName);
            addSheetRequest.setProperties(sheetProperties);

            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
            batchUpdateRequest.setRequests(Collections.singletonList(
                    new Request().setAddSheet(addSheetRequest)));

            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
            log.info("Created new sheet: {}", sheetName);

            // Add headers
            List<List<Object>> headers = Collections.singletonList(Arrays.asList(
                    "Reference ID", "Timestamp", "Company Name", "Contact Person", "Email",
                    "Phone", "Company Type", "Tax ID", "Selected Products", "Quantity",
                    "Delivery Date", "Shipping Address", "Billing Address", "Payment Terms",
                    "Additional Notes", "Same as Shipping"
            ));

            ValueRange headerBody = new ValueRange()
                    .setValues(headers)
                    .setMajorDimension("ROWS");

            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, sheetName + "!A1", headerBody)
                    .setValueInputOption("USER_ENTERED")
                    .execute();

            log.info("Added headers to sheet: {}", sheetName);

        } catch (IOException e) {
            log.error("Failed to create new sheet: {}", sheetName, e);
            throw e;
        }
    }

    private List<List<Object>> prepareRowData(BulkOrderRequest request, String referenceId, String timestamp) {
        String productsString = formatSelectedProducts(request.getSelectedProducts());

        List<Object> rowData = Arrays.asList(
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
                request.getAdditionalNotes(),
                request.isSameAsShipping() ? "Yes" : "No"
        );

        log.debug("Prepared row data for reference ID: {}", referenceId);
        return Collections.singletonList(rowData);
    }

    private String formatSelectedProducts(Map<String, List<String>> selectedProducts) {
        if (selectedProducts == null || selectedProducts.isEmpty()) {
            return "No products selected";
        }

        StringBuilder productsBuilder = new StringBuilder();
        selectedProducts.forEach((category, subcategories) -> {
            if (subcategories != null) {
                subcategories.forEach(subcategory -> {
                    if (productsBuilder.length() > 0) {
                        productsBuilder.append("; ");
                    }
                    productsBuilder.append(category).append(" - ").append(subcategory);
                });
            }
        });
        return productsBuilder.toString();
    }

    public List<BulkOrderDocument> getAllOrders() {
        log.debug("Fetching all bulk orders from account-db");
        return accountMongoTemplate.findAll(BulkOrderDocument.class, "bulk_orders");
    }

    public Optional<BulkOrderDocument> getOrderByReferenceId(String referenceId) {
        log.debug("Fetching order by reference ID: {}", referenceId);
        return Optional.ofNullable(
                accountMongoTemplate.findOne(
                        Query.query(Criteria.where("referenceId").is(referenceId)),
                        BulkOrderDocument.class,
                        "bulk_orders"
                )
        );
    }

    public List<BulkOrderDocument> getOrdersByProcessStatus(BulkOrderDocument.OrderProcessStatus status) {
        log.debug("Fetching orders by process status: {}", status);
        return accountMongoTemplate.find(
                Query.query(Criteria.where("processStatus").is(status)),
                BulkOrderDocument.class,
                "bulk_orders"
        );
    }

    public List<BulkOrderDocument> getOrdersByPaymentStatus(BulkOrderDocument.PaymentStatus status) {
        log.debug("Fetching orders by payment status: {}", status);
        return accountMongoTemplate.find(
                Query.query(Criteria.where("paymentStatus").is(status)),
                BulkOrderDocument.class,
                "bulk_orders"
        );
    }

    /**
     * Test method to verify Google Sheets connection
     */
    public boolean testConnection() {
        try {
            Sheets sheetsService = getSheetsService();
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            log.info("Successfully connected to Google Sheets: {}", spreadsheet.getProperties().getTitle());
            return true;
        } catch (IOException | GeneralSecurityException e) {
            log.error("Google Sheets connection test failed", e);
            return false;
        }
    }

    /**
     * Get the last row with data in the sheet
     */
    public int getLastRow() {
        try {
            Sheets sheetsService = getSheetsService();
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, sheetName + "!A:A")
                    .execute();

            List<List<Object>> values = response.getValues();
            return values != null ? values.size() : 0;

        } catch (IOException | GeneralSecurityException e) {
            log.error("Failed to get last row from sheet: {}", sheetName, e);
            return -1;
        }
    }
}