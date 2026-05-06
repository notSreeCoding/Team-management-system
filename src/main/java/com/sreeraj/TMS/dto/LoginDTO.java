package com.sreeraj.TMS.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    @Email
    public String email;
    @NotBlank
    public String password;


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
