package de.flashdrive.backend;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Configuration
public class StorageConfiguration {

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
    public BlobContainerAsyncClient blobAsyncClient() {
        return blobServiceAsyncClient().getBlobContainerAsyncClient(azureStorageContainerName);
    }
    @Bean
    public CloudBlobClient cloudBlobClient() throws URISyntaxException, StorageException, InvalidKeyException {
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(azureStorageConnectionString);
        return storageAccount.createCloudBlobClient();
    }
    @Bean
    public CloudBlobContainer testBlobContainer() throws URISyntaxException, StorageException, InvalidKeyException {
        return cloudBlobClient().getContainerReference(azureStorageContainerName);
    }

}