package de.flashdrive.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class SpeechToTextTest {



    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void speechTest() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/speech/user8/Rap-aus-Gaza.flac")
                .param("username", "user8")
                .param("filename", "E:\\HAW\\SEMESTER-5\\SE in Cloud\\flashdrive\\backend\\src\\main\\resources\\Test.txt"))
                .andExpect(status().is(200));
    }

   /* @Test
    public void speechWithMicTest() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/speech/mic")).andExpect(status().is(200));
    }*/

}

