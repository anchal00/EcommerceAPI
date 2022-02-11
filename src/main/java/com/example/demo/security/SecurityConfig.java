package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String[] AUTH_WHITELIST = {
        // -- swagger ui
        "/swagger-resources/**", 
        "/swagger-ui.html", 
        "/v2/api-docs", 
        "/webjars/**" ,
        "/", 
        "/h2/**"
    };

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(authenticationManagerBean()).userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll();
        http.cors().and().csrf().disable()
                .authorizeRequests().antMatchers(HttpMethod.POST, Constants.SIGN_UP_PATH)
                .permitAll().anyRequest().authenticated()
                .and()
                .addFilter(new AuthFilter(authenticationManager()))
                .addFilter(new AuthVerificationFilter(authenticationManager()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                
        http.headers().frameOptions().disable();

        
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
