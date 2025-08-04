package com.devopshub.config;

import com.devopshub.security.jwt.JwtAuthenticationFilter;
import com.devopshub.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity // ✅ Enables @PreAuthorize
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // ✅ Public endpoints
                        .requestMatchers(
                                "/api/auth/**",        // login, register, forgot-password, etc.
                                "/error",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()

                        // ✅ Chat (any authenticated user)
                        .requestMatchers("/api/chat/**").hasAnyRole("USER", "ADMIN", "MODERATOR")

                        // ✅ Posts
                        .requestMatchers("/api/posts", "/api/posts/**").hasAnyRole("USER", "ADMIN", "MODERATOR") // View
                        .requestMatchers("/api/posts/create", "/api/posts/update/**").hasAnyRole("ADMIN", "MODERATOR")
                        .requestMatchers("/api/posts/delete/**").hasRole("ADMIN")

                        // ✅ Tutorials
                        .requestMatchers("/api/tutorials/upload", "/api/tutorials/my-tutorials").hasAnyRole("USER", "ADMIN", "MODERATOR")
                        .requestMatchers("/api/tutorials/**").permitAll()

                        // ✅ Projects
                        .requestMatchers("/api/projects/upload", "/api/projects/edit/**", "/api/projects/delete/**").hasAnyRole("USER", "ADMIN", "MODERATOR")
                        .requestMatchers("/api/projects/**").permitAll()

                        // ✅ Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ✅ User-only endpoints (profile, dashboard, bookmarks etc.)
                        .requestMatchers("/api/user/**").hasRole("USER")

                        // ✅ Everything else must be authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
