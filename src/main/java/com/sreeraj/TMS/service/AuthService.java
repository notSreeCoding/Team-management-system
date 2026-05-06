package com.sreeraj.TMS.service;

import com.sreeraj.TMS.dto.AuthRespDTO;
import com.sreeraj.TMS.dto.LoginDTO;
import com.sreeraj.TMS.dto.RegDTO;
import com.sreeraj.TMS.entity.Role;
import com.sreeraj.TMS.entity.User;
import com.sreeraj.TMS.repo.UserRepository;
import com.sreeraj.TMS.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthRespDTO register(RegDTO regDTO) {
        User user = new User();

        if(userRepository.existsByEmail(regDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setFullName(regDTO.getName());
        user.setPassword(passwordEncoder.encode(regDTO.getPassword()));
        user.setEmail(regDTO.getEmail());
        user.setRole(Role.USER);
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthRespDTO(accessToken, refreshToken);
    }

    public AuthRespDTO login(LoginDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String AccessToken = jwtService.generateAccessToken(request.getEmail());
        String RefreshToken = jwtService.generateRefreshToken(request.getEmail());

        return new AuthRespDTO(AccessToken, RefreshToken);
    }
}

