package de.flashdrive.backend.services;


import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import de.flashdrive.backend.models.FileItem;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlobStorageService {

    private final CloudBlobClient cloudBlobClient;
    private final BlobContainerAsyncClient blobContainerAsyncClient;
    private final BlobServiceAsyncClient blobServiceAsyncClient;
    private final BlobServiceClient blobServiceClient;
    int blockSize = 10 * 1024;
    int numBuffers = 5;

    public BlobStorageService(CloudBlobClient cloudBlobClient, BlobContainerAsyncClient blobContainerAsyncClient, BlobServiceAsyncClient blobServiceAsyncClient, BlobServiceClient blobServiceClient) {
        this.cloudBlobClient = cloudBlobClient;
        this.blobContainerAsyncClient = blobContainerAsyncClient;
        this.blobServiceAsyncClient = blobServiceAsyncClient;
        this.blobServiceClient = blobServiceClient;
    }

    public boolean createContainer(String username) {
        String containerName = "flashdrive-" + username + "-bucket";
        return getContainer(containerName) != null;
    }

    public List<FileItem> listOfFiles(String username) {
        String containerName = "flashdrive-" + username + "-bucket";
        return blobServiceAsyncClient.getBlobContainerAsyncClient(containerName).listBlobs().toStream()
                .map(blobItem -> {
                    return new FileItem(blobItem.getName());
                })
                .collect(Collectors.toList());
    }

    public void upload(String username, MultipartFile file) throws IOException {
        String containerName = "flashdrive-" + username + "-bucket";
        BlobListDetails blobListDetails = new BlobListDetails().setRetrieveMetadata(true);//set "retrieve metadata" option to true
        ListBlobsOptions listBlobsOptions = new ListBlobsOptions().setDetails(blobListDetails);
        BlobAsyncClient blobAsyncClient = blobServiceAsyncClient.getBlobContainerAsyncClient(containerName).getBlobAsyncClient(file.getOriginalFilename());

        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(file.getResource().getInputStream().readAllBytes()));
        ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions(numBuffers, blockSize, null);
        blobAsyncClient.upload(data,parallelTransferOptions, true).block();
    }

    public void delete(String username,String fileName) {
        String containerName = "flashdrive-" + username + "-bucket";
        BlobAsyncClient blobAsyncClient = blobServiceAsyncClient.getBlobContainerAsyncClient(containerName).getBlobAsyncClient(fileName);

        blobAsyncClient.delete().block();
    }

    public byte[] download(String username,String fileName) {
        String containerName = "flashdrive-" + username + "-bucket";
        BlobAsyncClient blobAsyncClient = blobServiceAsyncClient.getBlobContainerAsyncClient(containerName).getBlobAsyncClient(fileName);
        return blobAsyncClient.download().blockLast().array();
    }

    public List<Map<String, String>> filterBlobsBy(String username, String name, String ext, String date) {

        String containerName = "flashdrive-" + username + "-bucket";

        OffsetDateTime createTime = null;
        if (!date.isEmpty()) {
            LocalDate lDT =LocalDate.parse(date);
            System.out.println(lDT);
            createTime = OffsetDateTime.of(lDT,LocalTime.MIN,ZoneOffset.UTC);
        }
        System.out.println(createTime);

        List<Map<String, String>> list = new ArrayList<>();

        BlobListDetails blobListDetails = new BlobListDetails().setRetrieveMetadata(true);//set "retrieve metadata" option to true
        ListBlobsOptions listBlobsOptions = new ListBlobsOptions().setDetails(blobListDetails);

        OffsetDateTime finalCreateTime = createTime;
        blobServiceClient.getBlobContainerClient(containerName).listBlobs(listBlobsOptions, Duration.ofHours(1000)).forEach(blob -> {

            Map<String, String> map = new HashMap<>();
            if ((!name.isEmpty() && blob.getName().contains(name)) ||
                    (!ext.isEmpty() && blob.getProperties().getContentType() != null && blob.getProperties().getContentType().equals(MimeTypes.getMimeType(ext))) ||
                    (!date.isEmpty() && finalCreateTime != null && finalCreateTime.compareTo(blob.getProperties().getCreationTime()) == -1) ||
                    (name.isEmpty() && ext.isEmpty() && date.isEmpty())) {
                System.out.println(blob.getProperties().getCreationTime().toString());
                map.put("name", blob.getName());
                map.put("size", blob.getProperties().toString());
                map.put("type", blob.getProperties().getContentType());
                map.put("created", blob.getProperties().getCreationTime().toString());


                list.add(map);
            }
        });

        return list;
    }

    public CloudBlobContainer getContainer(String containerName) {
        try {
            // Get a reference to a container , The container name must be lower case
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName.toLowerCase(Locale.ENGLISH));
            // Create the container if it does not exist.
            container.createIfNotExists();
            return container;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
