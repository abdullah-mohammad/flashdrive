package de.flashdrive.backend.services.gcp;

import com.google.api.client.util.Preconditions;
import com.google.api.gax.paging.Page;
import com.google.auth.Credentials;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.*;
import de.flashdrive.backend.services.MimeTypes;
import de.flashdrive.backend.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GCPStorageService  implements StorageService {

    @Autowired
    Credentials credentials;

    @Autowired
    Storage storage;

    public boolean createBucket(String username) {
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
        return true;
    }

    public boolean upload(String username, MultipartFile file) {
        try {
            String bucketName = "flashdrive-" + username + "-bucket";
                BlobInfo blobInfo = storage.create(
                        BlobInfo.newBuilder(bucketName, file.getOriginalFilename()).setContentType(file.getContentType()).build(), // get original file name
                        file.getBytes(), // the file
                        Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ) // Set file permission
                );

            return true;
        } catch (IllegalStateException | IOException e) {
            return false;
        }
    }

    public ByteArrayOutputStream download(String username, String fileName) {
        try {
            String bucketName = "flashdrive-" + username + "-bucket";
            Bucket bucket = storage.get(bucketName);
            Preconditions.checkNotNull(bucket, "Bucket [%s] not found", bucketName);

            Blob blob = bucket.get(fileName);
            Preconditions.checkNotNull(blob, "blob [%s] not found", fileName);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            blob.downloadTo(Paths.get("./"));
            return os;

            //return blob.getContent();
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

    public List<Map<String, String>> listOfFiles(String username) {

        List<Map<String, String>> list = new ArrayList<>();

        String bucketName = "flashdrive-" + username + "-bucket";

        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.fields(Storage.BlobField.values()));

        for (Blob blob : blobs.iterateAll()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", blob.getName());
            map.put("size", blob.getSize().toString());
            map.put("type", blob.getContentType());
            map.put("bucket", blob.getBucket());
            map.put("last update time", new Date(blob.getCreateTime()).toString());

            list.add(map);
        }
        return list;
    }

    public List<Map<String, String>> filter(String username, String name, String ext, String date) {

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
                map.put("lastUpdate", new SimpleDateFormat("yyyy-MM-dd").format(blob.getCreateTime()));

                list.add(map);
            }
        }

        return list;
    }

}
