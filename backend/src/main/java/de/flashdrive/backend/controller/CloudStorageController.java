package de.flashdrive.backend.controller;

import com.google.cloud.storage.Blob;
import de.flashdrive.backend.security.jwt.MessageResponse;
import de.flashdrive.backend.services.CloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class CloudStorageController {

    @Autowired
    private CloudStorageService cloudStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("username") String bucketName, @RequestParam("file") List<MultipartFile> file) {
        try {
            List<String> mediaLinks = cloudStorageService.upload(bucketName, file);
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
                                     @RequestParam(value = "path",required = false) String path
    ) {

        try {
            if (path == null)
                path = "";

            String home = System.getProperty("user.home");
            String destFilePath = home + "/Downloads/" + filename; // to set destination file path
            Blob blob = cloudStorageService.download(username, path + filename);

            if(blob == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            blob.downloadTo(Paths.get(destFilePath));

            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all/{username}")
    public ResponseEntity<?> getFileList(@PathVariable("username") String username) {
        try {
            List<String> list = cloudStorageService.getAll(username, "");
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            //return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{username}/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable("username") String username, @PathVariable("filename") String filename) {
        if (cloudStorageService.delete(username, filename)) {
            return ResponseEntity.ok(new MessageResponse("File deleted successfully!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Error: download error!"));
    }
}
