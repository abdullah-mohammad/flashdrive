package de.flashdrive.backend.services.azure;


import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.models.ParallelTransferOptions;
import de.flashdrive.backend.services.MimeTypes;
import de.flashdrive.backend.services.StorageService;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class AzureStorageService implements StorageService {

    private final BlobServiceAsyncClient blobServiceAsyncClient;

    private final BlobServiceClient blobServiceClient;
    int blockSize = 10 * 1024;
    int numBuffers = 5;

    public AzureStorageService(BlobServiceAsyncClient blobServiceAsyncClient, BlobServiceClient blobServiceClient) {
        this.blobServiceAsyncClient = blobServiceAsyncClient;
        this.blobServiceClient = blobServiceClient;
    }


    public boolean createBucket(String username) {
        String containerName = "flashdrive-" + username + "-bucket";
        blobServiceClient.createBlobContainer(containerName);
        return true;
    }

    public List<Map<String, String>> listOfFiles(String username) {
        String containerName = "flashdrive-" + username + "-bucket";
        return blobServiceClient.getBlobContainerClient(containerName).listBlobs().stream()
                .map(blob -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", blob.getName());
                    map.put("size", blob.getProperties().toString());
                    map.put("type", blob.getProperties().getContentType());
                    map.put("created", blob.getProperties().getCreationTime().toString());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public boolean upload(String username, MultipartFile file) {
        try {
            String containerName = "flashdrive-" + username + "-bucket";
            BlobListDetails blobListDetails = new BlobListDetails().setRetrieveMetadata(true);//set "retrieve metadata" option to true
            ListBlobsOptions listBlobsOptions = new ListBlobsOptions().setDetails(blobListDetails);
            //BlobAsyncClient blobAsyncClient = blobServiceAsyncClient.getBlobContainerAsyncClient(containerName).getBlobAsyncClient(file.getOriginalFilename());
            BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(file.getOriginalFilename());

            Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(file.getBytes()));
            ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions(numBuffers, blockSize, null);
            //blobAsyncClient.upload(data, parallelTransferOptions, true).block();
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean delete(String username, String fileName) {
        String containerName = "flashdrive-" + username + "-bucket";
        BlobAsyncClient blobAsyncClient = blobServiceAsyncClient.getBlobContainerAsyncClient(containerName).getBlobAsyncClient(fileName);

        blobAsyncClient.delete().block();
        return true;
    }

    public ByteArrayOutputStream download(String username, String fileName) {
        String containerName = "flashdrive-" + username + "-bucket";
        BlobContainerClient container = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = container.getBlobClient(fileName);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        blobClient.download(os);
        return os;
    }

    public List<Map<String, String>> filter(String username, String name, String ext, String date) {

        String containerName = "flashdrive-" + username + "-bucket";

        OffsetDateTime createTime = null;
        if (!date.isEmpty()) {
            LocalDate lDT = LocalDate.parse(date);
            System.out.println(lDT);
            createTime = OffsetDateTime.of(lDT, LocalTime.MIN, ZoneOffset.UTC);
        }

        List<Map<String, String>> list = new ArrayList<>();

        BlobListDetails blobListDetails = new BlobListDetails().setRetrieveMetadata(true);//set "retrieve metadata" option to true
        ListBlobsOptions listBlobsOptions = new ListBlobsOptions().setDetails(blobListDetails);

        OffsetDateTime finalCreateTime = createTime;
        blobServiceAsyncClient.getBlobContainerAsyncClient(containerName).listBlobs(listBlobsOptions).toIterable().forEach(blob -> {

            Map<String, String> map = new HashMap<>();
            if ((!name.isEmpty() && blob.getName().contains(name)) ||
                    (!ext.isEmpty() && blob.getProperties().getContentType() != null && blob.getProperties().getContentType().equals(MimeTypes.getMimeType(ext))) ||
                    (!date.isEmpty() && finalCreateTime != null && finalCreateTime.compareTo(blob.getProperties().getCreationTime()) == -1) ||
                    (name.isEmpty() && ext.isEmpty() && date.isEmpty())) {

                map.put("name", blob.getName());
                map.put("size", blob.getProperties().toString());
                map.put("type", blob.getProperties().getContentType());
                map.put("created", blob.getProperties().getCreationTime().toString());


                list.add(map);
            }
        });

        return list;
    }

}
