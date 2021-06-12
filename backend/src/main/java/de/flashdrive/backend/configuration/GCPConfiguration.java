package de.flashdrive.backend.configuration;

import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import de.flashdrive.backend.services.SpeechToTextService;
import de.flashdrive.backend.services.StorageService;
import de.flashdrive.backend.services.gcp.GCPSpeechToTextService;
import de.flashdrive.backend.services.gcp.GCPStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Profile("GCP")
public class GCPConfiguration {

    @Value("${gcp.app.projectId}")
    public String projectId;

    @Value("${gcp.app.credentials}")
    public String credentialsFile;

    @Bean
    public Storage storage(){
        return StorageOptions.newBuilder().setCredentials(credentials()).setProjectId(projectId).build().getService();
    }

    @Bean
    public Credentials credentials(){
        try {
            return GoogleCredentials.fromStream(new FileInputStream(String.valueOf(credentialsFile)));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

    @Bean
    public SpeechToTextService speechToTextService() {
        return new GCPSpeechToTextService();
    }

    @Bean
    public StorageService storageService() {
        return new GCPStorageService();
    }
}