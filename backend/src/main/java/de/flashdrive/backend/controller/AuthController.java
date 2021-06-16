package de.flashdrive.backend.controller;

import javax.validation.Valid;

import de.flashdrive.backend.models.User;
import de.flashdrive.backend.repository.UserRepository;
import de.flashdrive.backend.security.LoginRequest;
import de.flashdrive.backend.security.SignupRequest;
import de.flashdrive.backend.security.jwt.JwtResponse;
import de.flashdrive.backend.security.jwt.JwtUtils;
import de.flashdrive.backend.response.MessageResponse;
import de.flashdrive.backend.services.StorageService;
import de.flashdrive.backend.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    StorageService storageService;

    @GetMapping("ddd")
    public String getD() {
        return System.getenv("MYSQL_PASSWORD");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signedUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getEmail(),
                userDetails.getFirstname(),
                userDetails.getLastname(),
                userDetails.getGender(),
                userDetails.getAddress(),
                userDetails.getPicturePath()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getEmail(),
                signUpRequest.getFirstname(),
                signUpRequest.getLastname(),
                signUpRequest.getGender(),
                signUpRequest.getAddress(),
                "");
        try {
            userRepository.save(user);

            storageService.createBucket(user.getUsername().toLowerCase());

        } catch (Exception e) {
            System.out.println("\nHERE IS A STRANGE MSG: "+e.getMessage());
            /*return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));*/
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}