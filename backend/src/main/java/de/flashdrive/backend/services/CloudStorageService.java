package de.flashdrive.backend.services;

import com.google.api.gax.paging.Page;
import com.google.auth.Credentials;
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
                        BlobInfo.newBuilder(bucketName, file.getOriginalFilename()).build(), //get original file name
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

    public List<String> getAll(String username) {

        List<String> list = new ArrayList<>();

        String bucketName = "flashdrive-" + username + "-bucket";

        Page<Blob> blobs = storage.list(bucketName ,Storage.BlobListOption.currentDirectory());

        for (Blob blob : blobs.iterateAll()) {
/*            System.out.println("\n\n\nUser metadata:");

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
            System.out.println("Last Metadata Update: " + new Date(blob.getUpdateTime()));*/
            list.add(blob.getName());
        }
        return list;
    }
}
