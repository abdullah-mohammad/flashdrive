package de.flashdrive.backend.services.azure;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.Future;

import de.flashdrive.backend.services.SpeechToTextService;
import de.flashdrive.backend.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class AzureSpeechToTextService implements SpeechToTextService {


    @Autowired
    StorageService storageService;


    // [START speech_transcribe_streaming_mic]

    /**
     * Performs microphone streaming speech recognition with a duration of 1 minute.
     */
    public String streamingMicRecognize() {
        StringBuilder resultText = new StringBuilder();
        ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
        try (SpeechClient client = SpeechClient.create()) {

            responseObserver = new ResponseObserver<StreamingRecognizeResponse>() {
                ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                public void onStart(StreamController controller) {
                }

                public void onResponse(StreamingRecognizeResponse response) {
                    responses.add(response);
                }

                public void onComplete() {
                    for (StreamingRecognizeResponse response : responses) {
                        StreamingRecognitionResult result = response.getResultsList().get(0);
                        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                        resultText.append(alternative.getTranscript());
                    }
                }

                public void onError(Throwable t) {
                    System.out.println(t);
                }
            };

            ClientStream<StreamingRecognizeRequest> clientStream =
                    client.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig recognitionConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setLanguageCode("de-DE")
                            .setSampleRateHertz(16000)
                            .build();
            StreamingRecognitionConfig streamingRecognitionConfig =
                    StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).build();

            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(streamingRecognitionConfig)
                            .build(); // The first request in a streaming call has to be a config

            clientStream.send(request);

            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info targetInfo =
                    new DataLine.Info(
                            TargetDataLine.class,
                            audioFormat); // Set the system information to read from the microphone audio stream

            if (!AudioSystem.isLineSupported(targetInfo)) {
                System.out.println("Microphone not supported");
                System.exit(0);
            }
            // Target data line captures the audio stream the microphone produces.
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            System.out.println("Start speaking");
            long startTime = System.currentTimeMillis();
            // Audio Input Stream
            AudioInputStream audio = new AudioInputStream(targetDataLine);
            while (true) {
                long estimatedTime = System.currentTimeMillis() - startTime;
                byte[] data = new byte[6400];
                audio.read(data);
                if (estimatedTime > 10000) { // 60 seconds
                    System.out.println("Stop speaking.");
                    targetDataLine.stop();
                    targetDataLine.close();
                    break;
                }
                request =
                        StreamingRecognizeRequest.newBuilder()
                                .setAudioContent(ByteString.copyFrom(data))
                                .build();
                clientStream.send(request);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        responseObserver.onComplete();
        return resultText.toString();
    }
    // [END speech_transcribe_streaming_mic]

    public String convertSpeechToText(String username, String filename) throws Exception {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription("730aecb689d24fdfb593c45ec857a0f8", "northeurope");

        String suffix = filename.substring(filename.lastIndexOf("."));
        //String result = null;

        File file = Files.createTempFile(filename, suffix).toFile();
        byte[] fileByteArray = storageService.download(username, filename).toByteArray();

        File sourceFile = Files.createTempFile(filename, suffix).toFile();
        try (FileOutputStream fos = new FileOutputStream(sourceFile)) {
            fos.write(fileByteArray);
        }

        AudioConfig audioConfig = AudioConfig.fromWavFileInput(sourceFile.getAbsolutePath());

        SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);
        Future<com.microsoft.cognitiveservices.speech.SpeechRecognitionResult> task = recognizer.recognizeOnceAsync();
        SpeechRecognitionResult result = task.get();
        file.deleteOnExit();
        return result.getText();
    }


    public String audioFileToText(MultipartFile multipartFile) throws Exception {

        String fileName = multipartFile.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));

        File file = null;
        try {
            file = File.createTempFile(fileName, prefix);
            multipartFile.transferTo(file);
        } catch (Exception e) {
            e.printStackTrace();

        }

        String result = audioToText(file);

        return result;
    }


    private String audioToText(File file) throws InterruptedException, java.util.concurrent.ExecutionException {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription("730aecb689d24fdfb593c45ec857a0f8", "northeurope");
        AudioConfig audioConfig = AudioConfig.fromWavFileInput(file.getPath());
        SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);
        Future<com.microsoft.cognitiveservices.speech.SpeechRecognitionResult> task = recognizer.recognizeOnceAsync();
        SpeechRecognitionResult result = task.get();
        System.out.println("RECOGNIZED: Text=" + result.getText());
        return result.getText();
    }

}
