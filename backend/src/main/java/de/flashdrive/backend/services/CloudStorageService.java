package de.flashdrive.backend.services;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

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

    public void createBucket() {
        System.out.println("OK");
        //System.out.println("=========== "+resourceLoader.getResource("flashdrive-311519-bc4b841c158e.json").getFilename());
        try {
            credentials = GoogleCredentials
                    .fromStream(new FileInputStream("/Users/venancekonan/Documents/STUDIUM/HAW/Semester_5/Kurse/CloudComputing/Praktikum/flashdrive/backend/src/main/resources/flashdrive-311519-bc4b841c158e.json"));

            storage = StorageOptions.newBuilder().setCredentials(credentials)
                    .setProjectId("flashdrive-311519").build().getService();
            Bucket bucket = storage.create(BucketInfo.of("baeldung-bucket"));
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
