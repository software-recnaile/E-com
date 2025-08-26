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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.io.IOException;
import java.io.InputStream;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

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

    @Value("${google.sheets.spreadsheet.id}")
    private String spreadsheetId;

    // @Value("${google.sheets.credentials.file}")
    // private Resource credentialsFile;

    @Value("${google.sheets.sheet.name:BulkOrders}")
    private String sheetName;

    private final BulkOrderRepository bulkOrderRepository;
    private final MongoTemplate accountMongoTemplate;

    @Autowired
    public GoogleSheetsService(
            @Qualifier("accountMongoTemplate") MongoTemplate accountMongoTemplate,
            BulkOrderRepository bulkOrderRepository) {
        this.accountMongoTemplate = accountMongoTemplate;
        this.bulkOrderRepository = bulkOrderRepository;
    }

    @Transactional
    public void saveBulkOrder(BulkOrderRequest request, String referenceId, String timestamp) {
        try {
            saveToGoogleSheets(request, referenceId, timestamp);
            saveToAccountDB(request, referenceId, timestamp);
            log.info("Successfully saved order with reference ID: {} to account-db", referenceId);
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

        sheetsService.spreadsheets().values()
                .append(spreadsheetId, sheetName + "!A:A", body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
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
    }

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getCredentials()))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

  private GoogleCredentials getCredentials() throws IOException {
    // google.sheets.credentials.json comes from application.properties -> Render env var
    String credentialsJson = System.getenv("GOOGLE_CREDENTIALS");

    if (credentialsJson == null || credentialsJson.isEmpty()) {
        throw new IllegalStateException("GOOGLE_CREDENTIALS env variable is missing!");
    }

    try (InputStream in = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))) {
        return GoogleCredentials.fromStream(in).createScoped(SCOPES);
    }
}


    private void ensureSheetExists(Sheets sheetsService) throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        boolean sheetExists = spreadsheet.getSheets().stream()
                .anyMatch(sheet -> sheet.getProperties().getTitle().equals(sheetName));

        if (!sheetExists) {
            createNewSheetWithHeaders(sheetsService);
        }
    }

    private void createNewSheetWithHeaders(Sheets sheetsService) throws IOException {
        AddSheetRequest addSheetRequest = new AddSheetRequest();
        SheetProperties sheetProperties = new SheetProperties();
        sheetProperties.setTitle(sheetName);
        addSheetRequest.setProperties(sheetProperties);

        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
        batchUpdateRequest.setRequests(Collections.singletonList(
                new Request().setAddSheet(addSheetRequest)));

        sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

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

    public List<BulkOrderDocument> getAllOrders() {
        // Use accountMongoTemplate to query from account-db
        return accountMongoTemplate.findAll(BulkOrderDocument.class, "bulk_orders");
    }

    public Optional<BulkOrderDocument> getOrderByReferenceId(String referenceId) {
        // Use accountMongoTemplate to query from account-db
        return Optional.ofNullable(
                accountMongoTemplate.findOne(
                        Query.query(Criteria.where("referenceId").is(referenceId)),
                        BulkOrderDocument.class,
                        "bulk_orders"
                )
        );
    }

    public List<BulkOrderDocument> getOrdersByProcessStatus(BulkOrderDocument.OrderProcessStatus status) {
        // Use accountMongoTemplate to query from account-db
        return accountMongoTemplate.find(
                Query.query(Criteria.where("processStatus").is(status)),
                BulkOrderDocument.class,
                "bulk_orders"
        );
    }

    public List<BulkOrderDocument> getOrdersByPaymentStatus(BulkOrderDocument.PaymentStatus status) {
        // Use accountMongoTemplate to query from account-db
        return accountMongoTemplate.find(
                Query.query(Criteria.where("paymentStatus").is(status)),
                BulkOrderDocument.class,
                "bulk_orders"
        );
    }

}

