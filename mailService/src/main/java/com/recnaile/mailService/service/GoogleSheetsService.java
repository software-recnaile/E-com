package com.recnaile.mailService.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.recnaile.mailService.model.DronePlanForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "Drone Plan Submission";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    @Value("${google.sheet.id}")
    private String spreadsheetId;

    @Value("${google.sheet.range}")
    private String range;

    @Value("classpath:credentials.json")
    private Resource credentialsResource;

    private Sheets getSheetsService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        
        InputStream in = credentialsResource.getInputStream();
        Credential credential = GoogleCredential.fromStream(in)
                .createScoped(SCOPES);

        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void saveToSheet(DronePlanForm form, String referenceId, String timestamp) 
            throws GeneralSecurityException, IOException {
        Sheets service = getSheetsService();
        
        // Prepare the data to append
        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList(
                                referenceId,
                                timestamp,
                                form.getEmail(),
                                form.getDroneType(),
                                form.getRequirements(),
                                String.join(", ", form.getFeatures()),
                                form.getBudget(),
                                form.getTimeline()
                        )
                ));

        // Append the data
        service.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }
}
