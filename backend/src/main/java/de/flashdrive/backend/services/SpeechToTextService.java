package de.flashdrive.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

@Service
public interface SpeechToTextService {

    String audioFileToText(MultipartFile multipartFile) throws Exception;

    String convertSpeechToText(String username, String filename) throws Exception;

    String streamingMicRecognize();
}
