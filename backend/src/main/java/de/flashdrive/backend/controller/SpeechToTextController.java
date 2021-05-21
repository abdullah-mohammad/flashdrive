package de.flashdrive.backend.controller;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.cloud.storage.Storage;
import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.storage.StorageOptions;
import de.flashdrive.backend.services.CloudStorageService;
import de.flashdrive.backend.services.SpeechToTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SpeechToTextController {

    Credentials credentials;
    Storage storage;

    @Autowired
    SpeechToTextService speechToTextService;

    @Autowired
    CloudStorageService cloudStorageService;

    @PostMapping("/speech")
    public ResponseEntity<?> convertSpeechToText(@RequestParam("file") MultipartFile file) throws Exception {
        credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/flashdrive-311519-bc4b841c158e.json"));
        storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("flashdrive-311519").build().getService();
        String text="";
        List<MultipartFile> files =new ArrayList<>();
        files.add(file);
        String link = cloudStorageService.upload("user8",files).get(0);
        if (file!=null){
            text = speechToTextService.audioFileToText(link);
        }


        if (text != null)
            return new ResponseEntity<>(text,HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/speech/{username}/{filename}")
    public ResponseEntity<?> convertSpeechToTextWithMic(@PathVariable("username") String username, @PathVariable("filename") String filename) throws Exception {
        credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/flashdrive-311519-bc4b841c158e.json"));
        storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("flashdrive-311519").build().getService();


        String text = speechToTextService.convertSpeechToText(username,filename);

        if (text != null)
            return new ResponseEntity<>(text,HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }
    @GetMapping("/speech/mic")
    public ResponseEntity<?> convertSpeechToTextWithMic() throws Exception {

        String text = speechToTextService.streamingMicRecognize();

        if (text != null)
            return new ResponseEntity<>(text,HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }
}
