package de.flashdrive.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.flashdrive.backend.models.User;
import de.flashdrive.backend.repository.UserRepository;
import de.flashdrive.backend.security.LoginRequest;
import de.flashdrive.backend.security.SignupRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SignInTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void signInWithValidUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.getUsername();
        String loginData = "{\"username\":\""+"user3"+"\",\"password\":\""+"password"+"\"}";

        mockMvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content(loginData))
                .andExpect(status().isOk());

    }
    @Test
    void signInWithInvalidUserName() throws Exception {

        String loginData = "{\"username\":\""+"user333"+"\",\"password\":\""+"password"+"\"}";

        mockMvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content(loginData))
                .andExpect(status().isUnauthorized());

    }
    @Test
    void signInWithValidPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.getUsername();
        String loginData = "{\"username\":\""+"user3"+"\",\"password\":\""+"passw"+"\"}";

        mockMvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content(loginData))
                .andExpect(status().isUnauthorized());

    }
}