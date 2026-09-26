package com.druv.team_task_board.controller;

import com.druv.team_task_board.dto.*;
import com.druv.team_task_board.model.Role;
import com.druv.team_task_board.model.User;
import com.druv.team_task_board.repository.User_Repository;
import com.druv.team_task_board.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private User_Repository userRepository;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Autowired AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil JwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Aut_responseDTO> register(@Valid @RequestBody Register_requestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered: " + dto.getEmail());
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole() != null ? dto.getRole() : Role.MEMBER);
        userRepository.save(user);

        String token = JwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new Aut_responseDTO(token));
    }

    @PostMapping("/login")
    public ResponseEntity<Aut_responseDTO> login(@Valid @RequestBody Login_requestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        String token = JwtUtil.generateToken(dto.getEmail());
        return ResponseEntity.ok(new Aut_responseDTO(token));
    }
}