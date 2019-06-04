package com.meridian.ball;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;

@Configuration
public class GoogleCloudConfig {
    
    @Bean
    public Storage googleCloudStorage() throws GeneralSecurityException, IOException {
        return new Storage.Builder(httpTransport(), jsonFactory(), googleCredential()).setApplicationName("ballovertime").build();
    }
    
    @Bean
    public HttpTransport httpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }
    
    @Bean
    public JsonFactory jsonFactory() {
        return JacksonFactory.getDefaultInstance();
    }
    
    @Bean
    public GoogleCredential googleCredential() throws IOException {
        return GoogleCredential.getApplicationDefault()
                .createScoped(Collections.singletonList(StorageScopes.CLOUD_PLATFORM));
    }
}
