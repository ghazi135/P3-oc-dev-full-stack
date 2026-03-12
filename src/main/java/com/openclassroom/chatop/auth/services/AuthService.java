package com.openclassroom.chatop.auth.services;

import com.openclassroom.chatop.auth.dtos.AuthResponseDto;
import com.openclassroom.chatop.auth.dtos.LoginRequestDto;
import com.openclassroom.chatop.auth.dtos.RegisterRequestDto;
import com.openclassroom.chatop.auth.jwt.JwtService;
import com.openclassroom.chatop.user.UserEntity;
import com.openclassroom.chatop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequestDto payload) {
        if (userRepository.existsByEmail(payload.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        UserEntity newUser = buildUserFromRegistration(payload);
        userRepository.save(newUser);

        String token = jwtService.generateToken(newUser.getEmail());
        return new AuthResponseDto(token, "User registered");
    }

    public AuthResponseDto login(LoginRequestDto payload) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        payload.getEmail(),
                        payload.getPassword()
                )
        );
        String token = jwtService.generateToken(payload.getEmail());
        return new AuthResponseDto(token, "Login successful");
    }

    private UserEntity buildUserFromRegistration(RegisterRequestDto payload) {
        UserEntity entity = new UserEntity();
        entity.setEmail(payload.getEmail());
        entity.setName(payload.getName());
        entity.setPassword(passwordEncoder.encode(payload.getPassword()));
        return entity;
    }
}
