package com.helpdesk.configs;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     return http
    //         .csrf(AbstractHttpConfigurer::disable)
    //         .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    //         .authorizeHttpRequests(auth -> auth
    //             .requestMatchers("/api/v1/auth/**").permitAll()
    //             .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
    //             .requestMatchers("/api/v1/customer/**").hasRole("CUSTOMER")
    //             .requestMatchers("/api/v1/user/**").hasAnyRole("ADMIN", "CUSTOMER", "USER")
    //             .anyRequest().authenticated()
    //         )
    //         .sessionManagement(session -> 
    //             session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    //         )
    //         .authenticationProvider(authenticationProvider())
    //         .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
    //         .exceptionHandling(exception -> 
    //             exception
    //                 .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
    //                 .accessDeniedHandler(new AccessDeniedHandlerImpl())
    //         )
    //         .build();
    // }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/v1/customer/**").hasRole("CUSTOMER")
            .requestMatchers("/api/v1/user/**").hasAnyRole("ADMIN", "CUSTOMER", "USER")
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exception -> 
            exception
                .authenticationEntryPoint((request, response, authException) -> {
                    System.out.println("Authentication failed: " + authException.getMessage());
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), 
                        authException.getMessage());
                })
        )
        .build();
}

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
