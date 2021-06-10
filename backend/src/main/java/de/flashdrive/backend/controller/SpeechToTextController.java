package de.flashdrive.backend.controller;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import de.flashdrive.backend.services.GCPStorageService;
import de.flashdrive.backend.services.MimeTypes;
import de.flashdrive.backend.services.SpeechToTextService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class SpeechToTextController {

    Credentials credentials;
    Storage storage;

    @Autowired
    SpeechToTextService speechToTextService;

    @Autowired
    GCPStorageService cloudStorageService;

    public SpeechToTextController() throws IOException {
        credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/flashdrive-311519-bc4b841c158e.json"));
        storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("flashdrive-311519").build().getService();
    }

    @PostMapping("/speech")
    public ResponseEntity<?> convertSpeechToText(@RequestParam("username") String username,@RequestParam("file") MultipartFile file) throws Exception {

        String result="";

        if (file!=null){
            result = speechToTextService.audioFileToText(file.getInputStream());
        }

        if (!result.isEmpty()) {
            saveTextFile(username, result, Objects.requireNonNull(file.getOriginalFilename()));
        }

        if (result != null)
            return new ResponseEntity<>(result,HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/speech/{username}/{filename}")
    public ResponseEntity<?> convertSpeechToTextWithMic(@PathVariable("username") String username, @PathVariable("filename") String filename) throws Exception {

        String result = speechToTextService.convertSpeechToText(username,filename);

        if (!result.isEmpty()) {
            saveTextFile(username, result, filename);
        }

        if (result != null)
            return new ResponseEntity<>(result,HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    private void saveTextFile(@PathVariable("username") String username, String result, String fileName) throws IOException {
        String textFileName = fileName.split("\\.")[0];

        File textFile = Files.createTempFile(textFileName, ".txt").toFile();

        FileWriter fw = new FileWriter(textFile);
        fw.write(result);
        fw.close();

        MultipartFile multipartTextFile = new MockMultipartFile(textFile.getName(),
                textFile.getName(), MimeTypes.getMimeType("txt"),
                IOUtils.toByteArray(new FileInputStream(textFile)));

        cloudStorageService.upload(username, multipartTextFile);
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
