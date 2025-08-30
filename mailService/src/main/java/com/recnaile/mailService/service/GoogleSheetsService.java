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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "Mail Service";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    @Value("${google.sheets.spreadsheet.id}")
    private String spreadsheetId;

    @Value("${google.sheets.credentials.file}")
    private Resource credentialsFile;

    @Value("${google.sheets.sheet.name:Submissions}")
    private String sheetName;

    private HttpRequestInitializer getCredentials() throws IOException {
        try (InputStream in = credentialsFile.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(SCOPES);
            return new HttpCredentialsAdapter(credentials);
        }
    }

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void saveDronePlan(DronePlanForm form, String referenceId, String timestamp) {
        try {
            Sheets sheetsService = getSheetsService();
            ensureSheetExists(sheetsService);

            List<List<Object>> values = prepareDronePlanRowData(form, referenceId, timestamp);
            ValueRange body = new ValueRange()
                    .setValues(values)
                    .setMajorDimension("ROWS");

            sheetsService.spreadsheets().values()
                    .append(spreadsheetId, sheetName + "!A:A", body)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();

        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to save drone plan to Google Sheets", e);
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
        addSheetRequest.setProperties(new SheetProperties().setTitle(sheetName));

        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
        batchUpdateRequest.setRequests(Collections.singletonList(
                new Request().setAddSheet(addSheetRequest)
        ));

        sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

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
    }

    private List<List<Object>> prepareDronePlanRowData(DronePlanForm form, String referenceId, String timestamp) {
        return Collections.singletonList(Arrays.asList(
                referenceId,
                timestamp,
                form.getEmail(),
                form.getDroneType(),
                form.getRequirements(),
                String.join(", ", form.getFeatures()),
                form.getBudget(),
                form.getTimeline()
        ));
    }
}