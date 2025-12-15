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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final String credentialsFile;
    private final Environment env;
    private final ResourceLoader resourceLoader;

    public GoogleSheetsService(
            @Value("${google.sheets.spreadsheet.id}") String spreadsheetId,
            @Value("${google.sheets.sheet.name:Submissions}") String sheetName,
            @Value("${google.sheets.credentials.file}") String credentialsFile,
            Environment env,
            ResourceLoader resourceLoader) {
        this.spreadsheetId = spreadsheetId;
        this.sheetName = sheetName;
        this.credentialsFile = credentialsFile;
        this.env = env;
        this.resourceLoader = resourceLoader;
        logger.info("GoogleSheetsService initialized with spreadsheet: {}, sheet: {}, credentials file: {}",
                spreadsheetId, sheetName, credentialsFile);
    }

    private HttpRequestInitializer getCredentials() throws IOException {
        String credentialsJson = getCredentialsFromFile();

        if (credentialsJson == null || credentialsJson.trim().isEmpty()) {
            String errorMsg = "Google credentials not found. Please ensure credentials.json is available.";
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }

        return createCredentialsFromJson(credentialsJson);
    }

    private String getCredentialsFromFile() {
        try {
            logger.info("Attempting to load Google credentials from: {}", credentialsFile);

            // Handle classpath resources
            if (credentialsFile != null && credentialsFile.startsWith("classpath:")) {
                String classpathLocation = credentialsFile.substring("classpath:".length());
                return loadFromClasspath(classpathLocation);
            }

            // Handle file paths
            if (credentialsFile != null && credentialsFile.startsWith("file:")) {
                String filePath = credentialsFile.substring("file:".length()).trim();
                return loadFromFilePath(filePath);
            }

            // Try as regular file path
            if (credentialsFile != null && !credentialsFile.trim().isEmpty()) {
                return loadFromFilePath(credentialsFile);
            }

            // Fallback to environment variable
            return getCredentialsFromEnvironment();

        } catch (Exception e) {
            logger.error("❌ Failed to read credentials from file", e);
            return null;
        }
    }

    private String loadFromClasspath(String classpathLocation) throws IOException {
        logger.info("Loading credentials from classpath: {}", classpathLocation);
        Resource resource = resourceLoader.getResource("classpath:" + classpathLocation);

        if (!resource.exists()) {
            logger.warn("Classpath resource not found: {}", classpathLocation);
            return null;
        }

        try (InputStream is = resource.getInputStream()) {
            String content = new String(is.readAllBytes());
            if (content != null && !content.trim().isEmpty()) {
                logger.info("✅ Successfully loaded credentials from classpath: {} ({} bytes)",
                        classpathLocation, content.length());
                return content;
            }
        }
        return null;
    }

    private String loadFromFilePath(String filePath) throws IOException {
        logger.info("Loading credentials from file path: {}", filePath);
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            logger.warn("Credentials file not found: {}", path.toAbsolutePath());
            return null;
        }

        String content = Files.readString(path);
        if (content != null && !content.trim().isEmpty()) {
            logger.info("✅ Successfully loaded credentials from file: {} ({} bytes)",
                    path.toAbsolutePath(), content.length());
            return content;
        }
        return null;
    }

    private String getCredentialsFromEnvironment() {
        String[] possibleEnvVars = {
                "GOOGLE_CREDENTIALS_JSON",
                "GOOGLE_CREDENTIALS",
                "GCP_CREDENTIALS",
                "GOOGLE_SERVICE_ACCOUNT_JSON"
        };

        for (String envVar : possibleEnvVars) {
            String credentials = env.getProperty(envVar);
            if (credentials != null && !credentials.trim().isEmpty()) {
                logger.info("Found credentials in environment variable: {}", envVar);
                return credentials.trim();
            }

            credentials = System.getenv(envVar);
            if (credentials != null && !credentials.trim().isEmpty()) {
                logger.info("Found credentials in system environment: {}", envVar);
                return credentials.trim();
            }
        }
        return null;
    }

    private HttpRequestInitializer createCredentialsFromJson(String credentialsJson) throws IOException {
        // Clean up the JSON string
        credentialsJson = credentialsJson.trim();
        if (credentialsJson.startsWith("\"") && credentialsJson.endsWith("\"")) {
            credentialsJson = credentialsJson.substring(1, credentialsJson.length() - 1);
        }
        credentialsJson = credentialsJson.replace("\\n", "\n");

        logger.info("Processing Google credentials JSON ({} characters)", credentialsJson.length());

        try (InputStream in = new ByteArrayInputStream(credentialsJson.getBytes())) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(in).createScoped(SCOPES);
            logger.info("✅ Google credentials successfully authenticated and scoped");
            return new HttpCredentialsAdapter(credentials);
        } catch (IOException e) {
            logger.error("❌ Failed to parse Google credentials JSON", e);
            // Log first 200 chars for debugging (without sensitive info)
            String preview = credentialsJson.length() > 200 ?
                    credentialsJson.substring(0, 200) + "..." : credentialsJson;
            logger.error("Credentials JSON preview: {}", preview);
            throw new IOException("Failed to parse Google credentials JSON. Please check the file format.", e);
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
            logger.info("✅ Successfully connected to Google Sheets: {}", spreadsheet.getProperties().getTitle());
            return true;
        } catch (IOException | GeneralSecurityException e) {
            logger.error("❌ Google Sheets connection test failed", e);
            return false;
        }
    }

    /**
     * Get credential source information for debugging
     */
    public String getCredentialSourceInfo() {
        try {
            if (credentialsFile != null && credentialsFile.startsWith("classpath:")) {
                String classpathLocation = credentialsFile.substring("classpath:".length());
                return "Credentials loaded from classpath: " + classpathLocation;
            } else if (credentialsFile != null && credentialsFile.startsWith("file:")) {
                String filePath = credentialsFile.substring("file:".length());
                return "Credentials loaded from file: " + filePath;
            } else if (credentialsFile != null) {
                return "Credentials loaded from: " + credentialsFile;
            } else {
                return "No credentials file specified";
            }
        } catch (Exception e) {
            return "Error checking credential source: " + e.getMessage();
        }
    }

    /**
     * Health check method for credentials
     */
    public String checkCredentialsHealth() {
        try {
            String credentialsJson = getCredentialsFromFile();
            boolean credentialsFound = credentialsJson != null && !credentialsJson.trim().isEmpty();

            if (credentialsFound) {
                return "✅ Credentials found: " + getCredentialSourceInfo();
            } else {
                return "❌ No credentials found in any location";
            }
        } catch (Exception e) {
            return "❌ Error checking credentials: " + e.getMessage();
        }
    }
}