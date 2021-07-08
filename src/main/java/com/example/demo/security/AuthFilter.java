package com.example.demo.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    private final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    private AuthenticationManager authenticationManager;
    
    private User userData;

    public AuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {

            ServletInputStream inputStream = request.getInputStream();

            this.userData = new ObjectMapper().readValue(inputStream, User.class);
            
            log.info("attempting authentication for Username - "+ userData.getUsername());

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(this.userData.getUsername(),
                    this.userData.getPassword(), Collections.emptyList());

            return authenticationManager.authenticate(token);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        final String token = JWT.create().withSubject(this.userData.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + Constants.EXPIRATION_DELAY))
                .sign(Algorithm.HMAC512(Constants.SECRET_KEY.getBytes()));

        response.addHeader(Constants.HEADER_STRING, Constants.TOKEN_PREFIX + token);

        log.info("Authentication successful for Username -"+ this.userData.getUsername());
    }

}
