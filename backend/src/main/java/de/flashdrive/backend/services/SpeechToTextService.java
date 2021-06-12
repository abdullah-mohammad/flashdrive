package de.flashdrive.backend.services;

import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public interface SpeechToTextService {

    String audioFileToText(InputStream inputStream) throws Exception;

    String convertSpeechToText(String username, String filename) throws Exception;

    String streamingMicRecognize();
}
