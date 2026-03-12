package com.openclassroom.chatop.auth.controllers;

import com.openclassroom.chatop.auth.dtos.AuthResponseDto;
import com.openclassroom.chatop.auth.dtos.LoginRequestDto;
import com.openclassroom.chatop.auth.dtos.RegisterRequestDto;
import com.openclassroom.chatop.auth.services.AuthService;
import com.openclassroom.chatop.user.UserResponseDto;
import com.openclassroom.chatop.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Inscription d'un nouvel utilisateur")
    @ApiResponse(responseCode = "201", description = "Utilisateur créé")
    @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        AuthResponseDto result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Connexion et obtention d'un JWT")
    @ApiResponse(responseCode = "200", description = "Connexion réussie")
    @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Profil de l'utilisateur connecté")
    @ApiResponse(responseCode = "200", description = "Utilisateur trouvé")
    @ApiResponse(responseCode = "401", description = "Non authentifié")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(Authentication authentication) {
        String currentUserEmail = authentication.getName();
        UserResponseDto profile = userService.getByEmail(currentUserEmail);
        return ResponseEntity.ok(profile);
    }
}
