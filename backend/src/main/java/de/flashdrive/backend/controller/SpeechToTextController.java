package de.flashdrive.backend.controller;

import com.google.auth.Credentials;
import com.google.cloud.storage.Storage;
import de.flashdrive.backend.services.MimeTypes;
import de.flashdrive.backend.services.SpeechToTextService;
import de.flashdrive.backend.services.StorageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class SpeechToTextController {

    @Autowired
    SpeechToTextService speechToTextService;

    @Autowired
    StorageService storageService;

    Credentials credentials;

    Storage storage;


    @PostMapping("/speech")
    public ResponseEntity<?> convertSpeechToText(@RequestParam("username") String username, @RequestParam("file") MultipartFile file) throws Exception {

        String result="";

        if (file!=null){
            result = speechToTextService.audioFileToText(file.getInputStream());
        }

        if (!result.isEmpty()) {
            saveTextFile(username, result, Objects.requireNonNull(file.getOriginalFilename()));
        }

        if (result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
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

    private void saveTextFile(String username, String result, String fileName) throws IOException {
        String textFileName = fileName.split("\\.")[0];

        File textFile = Files.createTempFile(textFileName, ".txt").toFile();

        FileWriter fw = new FileWriter(textFile);
        fw.write(result);
        fw.close();

        MultipartFile multipartTextFile = new MockMultipartFile(textFile.getName(),
                textFile.getName(), MimeTypes.getMimeType("txt"),
                IOUtils.toByteArray(new FileInputStream(textFile)));

        storageService.upload(username, multipartTextFile);
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
