package de.flashdrive.backend.controller;

import de.flashdrive.backend.security.jwt.MessageResponse;
import de.flashdrive.backend.services.GCPStorageService;
import de.flashdrive.backend.services.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class GCPStorageController {

    @Autowired
    private GCPStorageService cloudStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("username") String username, @RequestParam("file") MultipartFile file) {
        try {
            String mediaLinks = cloudStorageService.upload(username, file);
            return new ResponseEntity<>(mediaLinks, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: upload error!"));
        }
    }

    @GetMapping(
            value = "/download/{username}/{filename}",
            produces = {MediaType.ALL_VALUE}
    )
    public ResponseEntity<?> getFile(@PathVariable("username") String username,
                                     @PathVariable("filename") String filename,
                                     @RequestParam(value = "path", required = false) String path) {

        if (path == null)
            path = "";
        byte[] res = cloudStorageService.download(username, path + filename);
        if (res != null) {
            ByteArrayResource resource = new ByteArrayResource(res);

            String contentTyp = MimeTypes.getMimeType(filename.split("\\.")[1]);

            return ResponseEntity.ok()
                    .contentLength(res.length)
                    .header("Content-type", contentTyp)
                    .header("Content-disposition", "attachment; filename=\"" + path + "\"").body(resource);
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Error: download error!"));

    }

    @GetMapping("/all/{username}")
    public ResponseEntity<?> getFileList(@PathVariable("username") String username) {
        try {
            List<Map<String, String>> list = cloudStorageService.getAll(username);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{username}/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable("username") String username, @PathVariable("filename") String filename) {
        if (cloudStorageService.delete(username, filename)) {
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

                list = cloudStorageService.filterBlobsBy(username, name, type, date);

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
