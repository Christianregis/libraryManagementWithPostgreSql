package com.example.libraryManager.controller;

import com.example.libraryManager.dto.LoginRequest;
import com.example.libraryManager.dto.UserDto;
import com.example.libraryManager.model.User;
import com.example.libraryManager.role.Role;
import com.example.libraryManager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentification", description = "API de gestion de l'inscription et de la connexion des utilisateurs")
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
     * Permet d'enregistrer un nouvel utilisateur dans le système.
     *
     * Cette méthode crée un nouveau compte utilisateur avec le rôle MEMBER.
     * Les informations fournies dans l'objet UserDto sont utilisées pour créer
     * l'utilisateur puis enregistrées en base de données après encodage du mot de passe.
     *
     * @param userDto objet contenant les informations du nouvel utilisateur
     *                (nom, prénom, email, mot de passe, confirmation du mot de passe)
     * @return ResponseEntity contenant les informations du nouvel utilisateur enregistré
     */
    @Operation(
            summary = "Inscription d'un utilisateur",
            description = "Permet d'enregistrer un nouvel utilisateur avec le rôle MEMBER dans le système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur enregistré avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide ou données incorrectes")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du nouvel utilisateur à enregistrer",
                    required = true
            )
            @RequestBody UserDto userDto){
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
     * Permet à un utilisateur de se connecter au système.
     *
     * Cette méthode authentifie l'utilisateur à l'aide de son email et de son mot de passe.
     * Si les informations sont correctes, l'utilisateur est authentifié et ses informations
     * sont retournées dans la réponse.
     *
     * @param loginRequest objet contenant les informations de connexion de l'utilisateur
     *                     (email et mot de passe)
     * @return ResponseEntity contenant les informations de l'utilisateur authentifié
     */
    @Operation(
            summary = "Connexion d'un utilisateur",
            description = "Authentifie un utilisateur à partir de son email et de son mot de passe"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations de connexion de l'utilisateur",
                    required = true
            )
            @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        return ResponseEntity.ok(authentication.getPrincipal());
    }
}
