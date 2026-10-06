package com.veterinaria.service;

import com.veterinaria.dto.AuthResponse;
import com.veterinaria.dto.LoginRequest;
import com.veterinaria.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
