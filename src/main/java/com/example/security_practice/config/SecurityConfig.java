package com.example.security_practice.config;

import com.example.security_practice.filter.JwtAuthenticationFilter;
import com.example.security_practice.service.MemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter authFilter;
    private final PasswordEncoderConfig passwordEncode;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter authFilter, PasswordEncoderConfig passwordEncode) {
        this.authFilter = authFilter;
        this.passwordEncode = passwordEncode;
    }


    @Bean
    public MemberDetailsService userDetailsService() {
        return new MemberDetailsService();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/auth/welcome")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/auth/addNewUser")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/auth/generateToken")).permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/auth/user/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/auth/admin/**")).hasAuthority("ADMIN")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncode.passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
