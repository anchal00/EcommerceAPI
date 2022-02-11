package com.example.demo.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class AuthVerificationFilter extends BasicAuthenticationFilter {

    public AuthVerificationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(Constants.HEADER_STRING);

        if (!isValidHeader(header)) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authenticationToken = getUsernamePassAuthToken(request, header);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getUsernamePassAuthToken(HttpServletRequest request, String header) {
        if (isValidHeader(header)) {

            final String RECIEVED_JWT = header.replace(Constants.TOKEN_PREFIX, "");

            String user = JWT
                .require(Algorithm.HMAC512(Constants.SECRET_KEY.getBytes()) ).build()
                .verify(RECIEVED_JWT)
                .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
            }
        }
        
        return null;
    }

    private boolean isValidHeader(String header) {
        if (header == null || header.isBlank() || !header.startsWith(Constants.TOKEN_PREFIX)) {
            return false;
        }
        return true;

    }

}
