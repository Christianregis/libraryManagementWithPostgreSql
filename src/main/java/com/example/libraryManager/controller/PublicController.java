package com.example.libraryManager.controller;

import com.example.libraryManager.dto.LoginRequest;
import com.example.libraryManager.dto.UserDto;
import com.example.libraryManager.model.User;
import com.example.libraryManager.role.Role;
import com.example.libraryManager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gestion des pages accecibles a tous
 */
@RestController
@RequestMapping("/api/auth")
public class PublicController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * Contructeur
     * @param userService UserService
     * @param authenticationManager AuthenticationManager
     * @param passwordEncoder PasswordEncoder
     */
    public PublicController(UserService userService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Enregister un future membre
     * @param userDto UserDto
     * @return ResponseEntity
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto){
        User user = new User();
        user.setName(userDto.getName());
        user.setPrenom(userDto.getPrenom());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setConfirm_password(userDto.getConfirm_password());
        user.setRole(Role.MEMBER);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(user).toDto());
    }

    /**
     * connecter un utilisateur
     * @param loginRequest LoginRequest(DTO)
     * @return ResponseEntity
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        return ResponseEntity.ok(authentication.getPrincipal());
    }
}
