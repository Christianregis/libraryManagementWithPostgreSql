package com.example.libraryManager.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour la connexion utlisateur
 */
@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;
}
