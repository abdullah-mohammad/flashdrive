package de.flashdrive.backend.configuration;

import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import de.flashdrive.backend.services.SpeechToTextService;
import de.flashdrive.backend.services.StorageService;
import de.flashdrive.backend.services.azure.AzureSpeechToTextService;
import de.flashdrive.backend.services.azure.AzureStorageService;
import de.flashdrive.backend.services.gcp.GCPSpeechToTextService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("Azure")
public class AzureConfiguration {


    @Value("${azure.storage.connection-string}")
    public String azureStorageConnectionString;

    @Value("${azure.storage.container-name}")
    public String azureStorageContainerName;

    @Bean
    public BlobServiceAsyncClient blobServiceAsyncClient() {
        return new BlobServiceClientBuilder().connectionString(azureStorageConnectionString).buildAsyncClient();
    }
    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder().connectionString(azureStorageConnectionString).buildClient();
    }

    @Bean
    public StorageService storageService() {
        return new AzureStorageService(blobServiceAsyncClient(), blobServiceClient());
    }

    @Bean
    public SpeechToTextService speechToTextService() {
        return new AzureSpeechToTextService();
    }
}