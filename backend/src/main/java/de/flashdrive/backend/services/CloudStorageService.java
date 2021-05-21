package de.flashdrive.backend.services;

import com.google.api.gax.paging.Page;
import com.google.auth.Credentials;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class CloudStorageService {

    Storage storage;
    String projectId = "flashdrive-311519";


    public CloudStorageService() {
        storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
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

    public Blob download(String username, String fileName) {

        try {
            String bucketName = "flashdrive-" + username + "-bucket";
            Blob blob = storage.get(BlobId.of(bucketName, fileName));
            return blob;
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

        Page<Blob> blobs = storage.list(bucketName ,Storage.BlobListOption.currentDirectory());

        for (Blob blob : blobs.iterateAll()) {
            System.out.println("\n\n\nUser metadata:");

            System.out.println("Bucket: " + blob.getBucket());
            System.out.println("CacheControl: " + blob.getCacheControl());
            System.out.println("ComponentCount: " + blob.getComponentCount());
            System.out.println("ContentDisposition: " + blob.getContentDisposition());
            System.out.println("ContentEncoding: " + blob.getContentEncoding());
            System.out.println("ContentLanguage: " + blob.getContentLanguage());
            System.out.println("ContentType: " + blob.getContentType());
            System.out.println("Crc32c: " + blob.getCrc32c());
            System.out.println("ETag: " + blob.getEtag());
            System.out.println("Generation: " + blob.getGeneration());
            System.out.println("Id: " + blob.getBlobId());
            System.out.println("Md5Hash: " + blob.getMd5());
            System.out.println("MediaLink: " + blob.getMediaLink());
            System.out.println("Metageneration: " + blob.getMetageneration());
            System.out.println("Name: " + blob.getName());
            System.out.println("Size: " + blob.getSize());
            System.out.println("StorageClass: " + blob.getStorageClass());
            System.out.println("TimeCreated: " + new Date(blob.getCreateTime()));
            System.out.println("Last Metadata Update: " + new Date(blob.getUpdateTime()));

            Map<String, String> map = new HashMap<>();
            map.put("name", blob.getName());
            map.put("size", blob.getSize().toString());
            map.put("type", blob.getContentType());
            map.put("create_date", blob.getCreateTime().toString());
            map.put("id", blob.getBlobId().toString());
            map.put("component_count", blob.getComponentCount().toString());
            map.put("name", blob.getName());

            list.add(map);
        }
        return list;
    }
}
