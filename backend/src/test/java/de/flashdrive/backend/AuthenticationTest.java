package de.flashdrive.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.flashdrive.backend.models.User;
import de.flashdrive.backend.repository.UserRepository;
import de.flashdrive.backend.security.SignupRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

   /* @Test
    void registrationWorksThroughAllLayers() throws Exception {
        SignupRequest user = new SignupRequest("UserNameTest","password","usernametest@test.de","Test","test","M","Hamburg");

        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
        User user1 = userRepository.findByUsername("UserNameTest").get();
        assertThat(user1.getEmail()).isEqualTo("usernametest@test.de");
        userRepository.delete(user1);

    }
    @Test
    void registrationWithExistedUsername() throws Exception {
        SignupRequest user = new SignupRequest("user2","password","usernametest@test.de","Test","test","M","Hamburg");

        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

    }
    @Test
    void registrationWithExistedEmail() throws Exception {
        SignupRequest user = new SignupRequest("user200","password","usernametest@test.de","Test","test","M","Hamburg");

        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void registrationWithInvalidEmail() throws Exception {
        SignupRequest user = new SignupRequest("user200","password","usernametestemail","Test","test","M","Hamburg");
        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

    }*/
}