package com.recnaile.mailService.service;

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
import com.recnaile.mailService.model.DronePlanForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSheetsService.class);
    private static final String APPLICATION_NAME = "Mail Service";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    private final String spreadsheetId;
    private final String sheetName;
    private final Environment env;

    public GoogleSheetsService(
            @Value("${google.sheets.spreadsheet.id}") String spreadsheetId,
            @Value("${google.sheets.sheet.name:Submissions}") String sheetName,
            Environment env) {
        this.spreadsheetId = spreadsheetId;
        this.sheetName = sheetName;
        this.env = env;
        logger.info("GoogleSheetsService initialized with spreadsheet: {}, sheet: {}", spreadsheetId, sheetName);
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
                logger.info("Found credentials in environment variable: {}", envVar);
                break;
            }

            // Also check system properties as fallback
            credentialsJson = System.getProperty(envVar);
            if (credentialsJson != null && !credentialsJson.trim().isEmpty()) {
                logger.info("Found credentials in system property: {}", envVar);
                break;
            }
        }

        if (credentialsJson == null || credentialsJson.trim().isEmpty()) {
            String errorMsg = "Google credentials not found. Checked environment variables: " +
                    String.join(", ", possibleEnvVars) +
                    ". Please set GOOGLE_CREDENTIALS_JSON environment variable.";
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }

        // Clean up the JSON string (remove any extra quotes or spaces)
        credentialsJson = credentialsJson.trim();
        if (credentialsJson.startsWith("\"") && credentialsJson.endsWith("\"")) {
            credentialsJson = credentialsJson.substring(1, credentialsJson.length() - 1);
        }

        // Replace escaped newlines if present
        credentialsJson = credentialsJson.replace("\\n", "\n");

        logger.info("Successfully loaded Google credentials (length: {})", credentialsJson.length());

        try (InputStream in = new ByteArrayInputStream(credentialsJson.getBytes())) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(SCOPES);
            logger.info("Google credentials successfully authenticated");
            return new HttpCredentialsAdapter(credentials);
        } catch (IOException e) {
            logger.error("Failed to parse Google credentials JSON. First 100 chars: {}",
                    credentialsJson.substring(0, Math.min(100, credentialsJson.length())), e);
            throw new IOException("Failed to parse Google credentials JSON. Please check the format.", e);
        }
    }

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Sheets sheetsService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            logger.debug("Google Sheets service initialized successfully");
            return sheetsService;
        } catch (GeneralSecurityException e) {
            logger.error("Security exception while initializing Google Sheets service", e);
            throw e;
        }
    }

    public void saveDronePlan(DronePlanForm form, String referenceId, String timestamp) {
        logger.info("Attempting to save drone plan with reference ID: {}", referenceId);

        try {
            Sheets sheetsService = getSheetsService();
            ensureSheetExists(sheetsService);

            List<List<Object>> values = prepareDronePlanRowData(form, referenceId, timestamp);
            ValueRange body = new ValueRange()
                    .setValues(values)
                    .setMajorDimension("ROWS");

            // Use the full range A:H to ensure all columns are properly handled
            sheetsService.spreadsheets().values()
                    .append(spreadsheetId, sheetName + "!A:H", body)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();

            logger.info("Successfully saved drone plan to Google Sheets with reference ID: {}", referenceId);

        } catch (IOException | GeneralSecurityException e) {
            logger.error("Failed to save drone plan to Google Sheets with reference ID: {}", referenceId, e);
            throw new RuntimeException("Failed to save drone plan to Google Sheets", e);
        }
    }

    private void ensureSheetExists(Sheets sheetsService) throws IOException {
        try {
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            boolean sheetExists = spreadsheet.getSheets().stream()
                    .anyMatch(sheet -> sheet.getProperties().getTitle().equals(sheetName));

            if (!sheetExists) {
                logger.info("Sheet '{}' does not exist, creating with headers", sheetName);
                createNewSheetWithHeaders(sheetsService);
            } else {
                logger.debug("Sheet '{}' already exists", sheetName);
            }
        } catch (IOException e) {
            logger.error("Failed to check if sheet exists: {}", sheetName, e);
            throw e;
        }
    }

    private void createNewSheetWithHeaders(Sheets sheetsService) throws IOException {
        try {
            AddSheetRequest addSheetRequest = new AddSheetRequest();
            addSheetRequest.setProperties(new SheetProperties().setTitle(sheetName));

            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
            batchUpdateRequest.setRequests(Collections.singletonList(
                    new Request().setAddSheet(addSheetRequest)
            ));

            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
            logger.info("Created new sheet: {}", sheetName);

            // Add headers
            ValueRange headerBody = new ValueRange()
                    .setValues(Collections.singletonList(
                            Arrays.asList(
                                    "Reference ID", "Timestamp", "Email", "Drone Type",
                                    "Requirements", "Features", "Budget", "Timeline"
                            )
                    ));

            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, sheetName + "!A1", headerBody)
                    .setValueInputOption("USER_ENTERED")
                    .execute();

            logger.info("Added headers to sheet: {}", sheetName);

        } catch (IOException e) {
            logger.error("Failed to create new sheet: {}", sheetName, e);
            throw e;
        }
    }

    private List<List<Object>> prepareDronePlanRowData(DronePlanForm form, String referenceId, String timestamp) {
        List<Object> rowData = Arrays.asList(
                referenceId,
                timestamp,
                form.getEmail(),
                form.getDroneType(),
                form.getRequirements(),
                String.join(", ", form.getFeatures()),
                form.getBudget(),
                form.getTimeline()
        );

        logger.debug("Prepared row data for reference ID: {}", referenceId);
        return Collections.singletonList(rowData);
    }

    /**
     * Test method to verify Google Sheets connection
     */
    public boolean testConnection() {
        try {
            Sheets sheetsService = getSheetsService();
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            logger.info("Successfully connected to Google Sheets: {}", spreadsheet.getProperties().getTitle());
            return true;
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Google Sheets connection test failed", e);
            return false;
        }
    }
}