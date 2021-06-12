package de.flashdrive.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface StorageService {

    boolean upload(String username, MultipartFile file);

    byte[] download(String username, String fileName);

    boolean delete(String username, String fileName);

    List<Map<String, String>> listOfFiles(String username);

    List<Map<String, String>> filter(String username, String name, String type, String date);

    boolean createBucket(String username);
}
