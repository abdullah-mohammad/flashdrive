package de.flashdrive.backend.services.gcp;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
//import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import de.flashdrive.backend.services.SpeechToTextService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class GCPSpeechToTextService implements SpeechToTextService {

    // [START speech_transcribe_streaming_mic]
    /** Performs microphone streaming speech recognition with a duration of 1 minute. */
    public String streamingMicRecognize() {
        StringBuilder resultText = new StringBuilder();
        ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
        try (SpeechClient client = SpeechClient.create()) {

            responseObserver = new ResponseObserver<StreamingRecognizeResponse>() {
                        ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                        public void onStart(StreamController controller) {}

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

    public String convertSpeechToText(String username,String filename) throws Exception {
        try (SpeechClient client = SpeechClient.create()) {
            RecognitionConfig.Builder builder = RecognitionConfig.newBuilder().setSampleRateHertz(16000).setEncoding(RecognitionConfig.AudioEncoding.MP3)
                    .setLanguageCode("de-DE").setEnableAutomaticPunctuation(true).setEnableWordTimeOffsets(true).setAudioChannelCount(2);
            builder.setModel("default");

            RecognitionConfig config = builder.build();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setUri("gs://flashdrive-"+username+"-bucket/"+filename).build();

            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response = client.longRunningRecognizeAsync(config, audio);

            while (!response.isDone()) {
                Thread.sleep(10000);
            }

            List<SpeechRecognitionResult> speechResults = response.get().getResultsList();

            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult result : speechResults) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcription.append(alternative.getTranscript());
            }
            return transcription.toString();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

    public String audioFileToText(MultipartFile multipartFile) throws Exception {

        try (SpeechClient client = SpeechClient.create()) {
            RecognitionConfig.Builder builder = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.MP3)
                    .setLanguageCode("de-DE")
                    .setSampleRateHertz(16000)
                    .setEnableAutomaticPunctuation(true)
                    .setEnableWordTimeOffsets(true)
                    .setAudioChannelCount(2);

            builder.setModel("default");

            RecognitionConfig config = builder.build();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(ByteString.readFrom(multipartFile.getInputStream())).build();

            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response = client.longRunningRecognizeAsync(config, audio);

            while (!response.isDone()) {
                Thread.sleep(10000);
            }

            List<SpeechRecognitionResult> speechResults = response.get().getResultsList();

            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult result : speechResults) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcription.append(alternative.getTranscript());
            }
            return transcription.toString();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }
}
