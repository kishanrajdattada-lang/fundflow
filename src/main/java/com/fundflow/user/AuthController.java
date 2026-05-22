package com.fundflow.user;

import com.fundflow.common.ApiResponse;
import com.fundflow.config.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse.UserSummary> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepo.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists with email " + request.email());
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        User savedUser = userRepo.save(user);

        AuthResponse.UserSummary userSummary = AuthResponse.UserSummary.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();

        return ApiResponse.<AuthResponse.UserSummary>builder()
                .status(HttpStatus.CREATED.value())
                .message("User registered successfully")
                .data(userSummary)
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String token = jwtService.generateToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(token)
                .expiresInSeconds(jwtService.getExpirationTime() / 1000)
                .user(AuthResponse.UserSummary.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .build();

        return ApiResponse.<AuthResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(authResponse)
                .build();
    }
}
