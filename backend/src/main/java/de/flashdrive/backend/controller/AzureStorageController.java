package de.flashdrive.backend.controller;


import de.flashdrive.backend.models.SuccessfulOperation;
import de.flashdrive.backend.security.jwt.MessageResponse;
import de.flashdrive.backend.services.BlobStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/storage")
public class AzureStorageController {

    @Autowired
    private BlobStorageService blobStorageService;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createContainer(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        if (blobStorageService.createContainer(username)) {
            return new ResponseEntity(new SuccessfulOperation(), HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Error: container create!"));
    }

    @GetMapping(value = "/all/{username}")
    public Object getListOfFiles(@PathVariable("username") String username) {
        return blobStorageService.listOfFiles(username);
    }

    @PostMapping(value = "/upload")
    public Object handleFileUpload(@RequestParam("username") String username,@RequestParam("file") MultipartFile multipartFile) throws IOException {
        blobStorageService.upload(username, multipartFile);
        return new ResponseEntity(new SuccessfulOperation(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{username}/{fileName}")
    public Object handleFileDelete(@PathVariable("username") String username,@PathVariable String fileName) throws IOException {
        blobStorageService.delete(username,fileName);
        return new ResponseEntity(new SuccessfulOperation(), HttpStatus.OK);
    }

    @GetMapping(value = "/download/{username}/{filename}")
    public @ResponseBody
    byte[] handleFileDownload(HttpServletResponse response,@PathVariable("username") String username, @PathVariable("filename") String fileName) throws IOException {
        response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
        return blobStorageService.download(username,fileName);
    }

    @PostMapping(value = "/filter")
    public ResponseEntity<?> filterItemList(@RequestBody Map<String, String> filter) {
        try {
            String username = filter.get("username");
            String name = filter.get("name");
            String type = filter.get("type");
            String date = filter.get("date");

            List<Map<String, String>> list = new ArrayList<>();
            if (!username.isEmpty()) {

                list = blobStorageService.filterBlobsBy(username, name, type, date);

            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: username should not be empty"));
            }

            if (list.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("No match"));
            }

            return new ResponseEntity<>(list, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: wrong username"));
        }
    }
}
