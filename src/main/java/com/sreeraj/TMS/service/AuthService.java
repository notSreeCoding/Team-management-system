package com.sreeraj.TMS.service;

import com.sreeraj.TMS.dto.*;
import com.sreeraj.TMS.entity.RefreshToken;
import com.sreeraj.TMS.exception.EmailAlreadyExistsException;
import com.sreeraj.TMS.exception.InvalidCredentialsException;
import com.sreeraj.TMS.exception.RefreshTokenException;
import com.sreeraj.TMS.repo.RefreshTokenRepository;
import com.sreeraj.TMS.security.JwtService;
import com.sreeraj.TMS.entity.Role;
import com.sreeraj.TMS.entity.User;
import com.sreeraj.TMS.repo.UserRepository;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthRespDTO register(RegDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setFullName(request.getName());

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return new  AuthRespDTO(accessToken, refreshToken);
    }

    public AuthRespDTO login(LoginDTO request) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }catch(Exception e){
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return new AuthRespDTO(accessToken, refreshToken);
    }

    public AuthRespDTO refreshToken(RefreshDTO request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenException("Invalid refresh token"));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = storedToken.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        saveRefreshToken(user, newRefreshToken);

        return new AuthRespDTO(newAccessToken,newRefreshToken);
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
    }
}