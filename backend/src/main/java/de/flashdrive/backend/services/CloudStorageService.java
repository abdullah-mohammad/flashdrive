package de.flashdrive.backend.services;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.gax.paging.Page;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.services.storage.model.StorageObject;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CloudStorageService {

    Credentials credentials;
    Storage storage;
    @Autowired
    ResourceLoader resourceLoader;

    /*public CloudStorageService() {
        //System.out.println("=========== "+resourceLoader.getResource("flashdrive-311519-bc4b841c158e.json").getFilename());
        System.out.println("MAL SCHAUEM!");
        try {
            *//*credentials = GoogleCredentials
                    .fromStream(new FileInputStream("/Users/venancekonan/Documents/STUDIUM/HAW/Semester_5/Kurse/CloudComputing/Praktikum/flashdrive/backend/src/main/resources/flashdrive-311519-bc4b841c158e.json"));*//*
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public CloudStorageService() {
        //System.out.println("OK");
        //System.out.println("=========== "+resourceLoader.getResource("flashdrive-311519-bc4b841c158e.json").getFilename());
        try {
            credentials = GoogleCredentials.fromStream(new FileInputStream("../../flashdrive-311519-bc4b841c158e.json"));
            //.fromStream(new FileInputStream("/Users/venancekonan/Documents/STUDIUM/HAW/Semester_5/Kurse/CloudComputing/Praktikum/flashdrive/backend/src/main/resources/flashdrive-311519-bc4b841c158e.json"));

            storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("flashdrive-311519").build().getService();
        } catch (
                Exception e) {
            //e.printStackTrace();
            System.err.println(e.getMessage());
        }

    }

    public void createBucket(String username) {
        try {
            Bucket bucket = storage.create(BucketInfo.of("flashdrive-" + username + "-bucket"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public List<String> upload(String bucketName, List<MultipartFile> files) {
        try {
            List<String> mediaLinks = new ArrayList<>();
            for (MultipartFile file : files) {
                BlobInfo blobInfo = storage.create(
                        BlobInfo.newBuilder("flashdrive-" + bucketName + "-bucket", file.getOriginalFilename()).build(), //get original file name
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

    public Blob download(String bucketName, String fileName) {

        try{
            Blob blob = storage.get(BlobId.of("flashdrive-" + bucketName + "-bucket", fileName));
            return blob;
        }catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }


        /* https://medium.com/teamarimac/file-upload-and-download-with-spring-boot-firebase-af068bc62614
        String destFileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));     // to set random strinh for destination file name
        String destFilePath = "Z:\\New folder\\" + destFileName;                                    // to set destination file path

        ////////////////////////////////   Download  ////////////////////////////////////////////////////////////////////////
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("path of JSON with genarated private key"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        Blob blob = storage.get(BlobId.of("your bucket name", fileName));
        blob.downloadTo(Paths.get(destFilePath));
        return sendResponse("200", "Successfully Downloaded!");*/
    }

    public boolean delete(String bucketName, String fileName)
    {
        Blob blob = storage.get(BlobId.of("flashdrive-" + bucketName + "-bucket", fileName));
        return blob.delete();
    }

    public List<String>  getAll(String bucketName) {

        Page<Blob> blobPage = storage.list("flashdrive-" + bucketName + "-bucket");
        List<String> list = new ArrayList<>();

        for (Blob o:blobPage.iterateAll()) {
            list.add(o.getName());
        }
        return list;
    }
}
