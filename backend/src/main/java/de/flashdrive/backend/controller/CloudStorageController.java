package de.flashdrive.backend.controller;

import de.flashdrive.backend.services.CloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api")
@RestController
public class CloudStorageController {

    @Autowired
    private CloudStorageService cloudStorageService;

    @PostMapping("/single/upload")
    public ResponseEntity<HttpStatus> uploadSingleFile(@RequestParam("username") String bucketName, @RequestParam("file") MultipartFile file) {
        try {
            //String filename = cloudStorageService.storeFile(file);
            cloudStorageService.createBucket();

            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
