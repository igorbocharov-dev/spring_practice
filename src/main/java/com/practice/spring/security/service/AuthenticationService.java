package com.practice.spring.security.service;

import com.practice.spring.dto.security.LoginRequest;
import com.practice.spring.dto.security.TokenResponse;
import com.practice.spring.dto.security.TokenSubject;
import com.practice.spring.security.userDetails.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AuthenticationService {

    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(JwtTokenService jwtTokenService,
                                 AuthenticationManager authenticationManager) {
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
    }

    public TokenResponse login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) principal.getAuthorities();
        String username = principal.getUsername();
        TokenSubject tokenSubject = new TokenSubject(authorities, username);
        String accessToken = jwtTokenService.generateToken(tokenSubject);
        return new TokenResponse(accessToken);
    }
}
