package com.sreeraj.TMS.controller;

import com.sreeraj.TMS.dto.AuthRespDTO;
import com.sreeraj.TMS.dto.LoginDTO;
import com.sreeraj.TMS.dto.RegDTO;
import com.sreeraj.TMS.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthRespDTO register(@Valid @RequestBody RegDTO regDTO) {
        return authService.register(regDTO);
    }

    @PostMapping("/login")
    public AuthRespDTO login(
           @Valid @RequestBody LoginDTO request
    ) {

        return authService.login(request);
    }
}
