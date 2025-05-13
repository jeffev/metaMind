package com.metamind.usuario_service.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

public class CognitoJwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final CognitoIdentityProviderClient cognitoClient;

    public CognitoJwtAuthenticationFilter(AuthenticationManager authenticationManager, CognitoIdentityProviderClient cognitoClient) {
        super(authenticationManager);
        this.cognitoClient = cognitoClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // Valide o token com o Cognito
            Authentication auth = getAuthentication(token);
            if (auth != null) {
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        try {
            chain.doFilter(request, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    private Authentication getAuthentication(String token) {
        // Use o Cognito para validar o token e obter o usuário
        GetUserRequest request = GetUserRequest.builder().accessToken(token).build();
        try {
            cognitoClient.getUser(request);
            return new UsernamePasswordAuthenticationToken("user", null, new ArrayList<>());
        } catch (Exception e) {
            return null;
        }
    }
}