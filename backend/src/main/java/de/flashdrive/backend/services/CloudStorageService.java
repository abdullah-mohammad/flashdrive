package de.flashdrive.backend.services;

import com.google.api.client.util.Preconditions;
import com.google.api.gax.paging.Page;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CloudStorageService {

    Storage storage;
    String projectId = "flashdrive-311519";


    public CloudStorageService() {
        try {
            storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        } catch (
                Exception e) {
            //e.printStackTrace();
            System.err.println(e.getMessage());
        }

    }

    public void createBucket(String username) {
        try {
            String bucketName = "flashdrive-" + username + "-bucket";
            Bucket bucket = storage.create(BucketInfo.of(bucketName));
            Policy originalPolicy = storage.getIamPolicy(bucketName);
            storage.setIamPolicy(
                    bucketName,
                    originalPolicy
                            .toBuilder()
                            .addIdentity(StorageRoles.objectAdmin(), Identity.allAuthenticatedUsers()) // All auth-users can read & write on objects
                            .build());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public List<String> upload(String username, List<MultipartFile> files) {
        try {
            List<String> mediaLinks = new ArrayList<>();
            String bucketName = "flashdrive-" + username + "-bucket";
            for (MultipartFile file : files) {
                BlobInfo blobInfo = storage.create(
                        BlobInfo.newBuilder(bucketName, file.getOriginalFilename()).setContentType(file.getContentType()).build(), // get original file name
                        file.getBytes(), // the file
                        Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ) // Set file permission
                );
                mediaLinks.add(blobInfo.getMediaLink()); // Return file url
            }

            return mediaLinks;
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] download(String username, String fileName) {
        try {
            String bucketName = "flashdrive-" + username + "-bucket";
            Bucket bucket = storage.get(bucketName);
            Preconditions.checkNotNull(bucket, "Bucket [%s] not found", bucketName);

            Blob blob = bucket.get(fileName);
            Preconditions.checkNotNull(blob, "blob [%s] not found", fileName);

            return blob.getContent();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public boolean delete(String username, String fileName) {
        String bucketName = "flashdrive-" + username + "-bucket";
        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        return blob.delete();
    }

    public List<Map<String, String>> getAll(String username) {

        List<Map<String, String>> list = new ArrayList<>();

        String bucketName = "flashdrive-" + username + "-bucket";

        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.fields(Storage.BlobField.values()));

        for (Blob blob : blobs.iterateAll()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", blob.getName());
            map.put("size", blob.getSize().toString());
            map.put("type", blob.getContentType());
            map.put("bucket", blob.getBucket());
            map.put("last update time", new Date(blob.getUpdateTime()).toString());

            list.add(map);
        }
        return list;
    }

    public List<Map<String, String>> filterBlobsBy(String username, String name, String ext, String date) {

        List<Map<String, String>> list = new ArrayList<>();

        String bucketName = "flashdrive-" + username + "-bucket";

        Page<Blob> blobs = storage.list(bucketName, /*Storage.BlobListOption.prefix(name),*/
                Storage.BlobListOption.fields(Storage.BlobField.values()));
        Date updateTime = null;
        if (!date.isEmpty()) {
            try {
                updateTime = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
        }

        for (Blob blob : blobs.getValues()) {
            Map<String, String> map = new HashMap<>();

            if ((!name.isEmpty() && blob.getName().contains(name)) ||
                    (!ext.isEmpty() && blob.getContentType() != null && blob.getContentType().equals(MimeTypes.getMimeType(ext))) ||
                    (!date.isEmpty() && updateTime != null && updateTime.before(new Date(blob.getUpdateTime()))) ||
                    (name.isEmpty() && ext.isEmpty() && date.isEmpty())
            ) {

                map.put("name", blob.getName());
                map.put("size", blob.getSize().toString());
                map.put("type", blob.getContentType());
                map.put("bucket", blob.getBucket());
                map.put("lastUpdate", new SimpleDateFormat("yyyy-MM-dd").format(blob.getUpdateTime()));

                list.add(map);
            }
        }

        return list;
    }

}
