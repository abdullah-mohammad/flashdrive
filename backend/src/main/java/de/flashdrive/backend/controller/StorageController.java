package de.flashdrive.backend.controller;

import de.flashdrive.backend.response.MessageResponse;
import de.flashdrive.backend.services.StorageService;
import de.flashdrive.backend.services.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("username") String username, @RequestParam("file") MultipartFile file) {

        if (storageService.upload(username, file))
            return ResponseEntity.ok().body(new MessageResponse("File uploaded successfully!"));

        return ResponseEntity.badRequest().body(new MessageResponse("Error: upload error!"));
    }

    @GetMapping(
            value = "/download/{username}/{filename}",
            produces = {MediaType.ALL_VALUE}
    )
    public ResponseEntity<ByteArrayResource> handleFileDownload(HttpServletResponse response,
                                                                @PathVariable("username") String username,
                                                                @PathVariable("filename") String fileName,
                                                                @RequestParam(value = "path", required = false) String path) {

        byte[] data = storageService.download(username,fileName).toByteArray();

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", MimeTypes.getMimeType(fileName.split("\\.")[0]))
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);

    }

    @GetMapping("/all/{username}")
    public ResponseEntity<?> getListOfFiles(@PathVariable("username") String username) {
        try {
            List<Map<String, String>> list = storageService.listOfFiles(username);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{username}/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable("username") String username, @PathVariable("filename") String filename) {
        if (storageService.delete(username, filename)) {
            return ResponseEntity.ok(new MessageResponse("File deleted successfully!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Error: delete error!"));
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

                list = storageService.filter(username, name, type, date);

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
