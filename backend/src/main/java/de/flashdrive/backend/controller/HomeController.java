package de.flashdrive.backend.controller;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.ImmutableList;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class HomeController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/api")
    public List<Map<String, Object>> listUsers() throws IOException {
        return jdbcTemplate.queryForList("SELECT * FROM Users;");
    }

   /*@GetMapping("/api")
    public String index() {
       return "Hallo Leute !";
   }*/

}
