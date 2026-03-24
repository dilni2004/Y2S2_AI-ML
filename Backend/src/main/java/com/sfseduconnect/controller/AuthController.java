package com.sfseduconnect.controller;

import com.sfseduconnect.dto.AuthRequest;
import com.sfseduconnect.dto.AuthResponse;
import com.sfseduconnect.entity.User;
import com.sfseduconnect.repository.UserRepository;
import com.sfseduconnect.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        User user = UserRepository.findByUsername(authRequest.getUsername()).orElseThrow();
        // Return token + user info (role, departmentId, studentId etc)
        AuthResponse response = new AuthResponse();
        response.setToken(jwt);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setFullName(user.getFullName());
        if (user.getDepartment() != null) {
            response.setDepartmentId(user.getDepartment().getId());
        }
        // For student, also set studentIdNumber if needed
        return ResponseEntity.ok(response);
    }
}