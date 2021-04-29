package de.flashdrive.backend.controller;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class HomeController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public List<Map<String, Object>> listUsers() throws IOException {
        return jdbcTemplate.queryForList("SELECT * FROM Users;");
    }
}
