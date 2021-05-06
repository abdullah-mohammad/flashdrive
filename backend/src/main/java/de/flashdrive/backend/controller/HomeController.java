package de.flashdrive.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< backend/src/main/java/de/flashdrive/backend/controller/HomeController.java
=======
//import org.springframework.jdbc.core.JdbcTemplate;
>>>>>>> backend/src/main/java/de/flashdrive/backend/controller/HomeController.java
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class HomeController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public List<Map<String, Object>> listUsers() throws IOException {
        return jdbcTemplate.queryForList("SELECT * FROM users;");
    }

     /*@GetMapping("/")
    public String index() {
       return "Hallo Leute !";
   }*/

}
