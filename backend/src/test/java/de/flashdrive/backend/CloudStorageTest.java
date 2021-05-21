package de.flashdrive.backend;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class CloudStorageTest {


        @Autowired
        private WebApplicationContext webApplicationContext;

    @Test
    //Todo
    public void uploadtest() throws Exception {
        File file = new File("E:\\HAW\\SEMESTER-5\\SE in Cloud\\flashdrive\\backend\\src\\main\\resources\\Test.txt");
        MockMultipartFile firstFile = new MockMultipartFile("file", file.getName(), "text/plain", "some xml".getBytes());
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
                .file(firstFile)
                .param("username", "user8"))
                .andExpect(status().is(200));
    }
    @Test
    public void downloadTest() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/download/user8/BAI5.pdf")
                .param("username", "user8")
                .param("filename",  "BAI5.pdf"))
                .andExpect(status().is(200));
    }
    @Test
    public void deleteTest() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/delete/user8/HttpClient.java")
                .param("username", "user8")
                .param("filename",  "HttpClient.java"))
                .andExpect(status().is(200));
    }

}

