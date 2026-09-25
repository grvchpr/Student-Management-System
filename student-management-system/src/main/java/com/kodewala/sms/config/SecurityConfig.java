package com.kodewala.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kodewala.sms.security.JwtAccessDeniedHandler;
import com.kodewala.sms.security.JwtAuthenticationEntryPoint;
import com.kodewala.sms.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	/*
	 * @Bean public AuthenticationProvider authenticationProvider(
	 * org.springframework.security.core.userdetails.UserDetailsService
	 * userDetailsService, PasswordEncoder passwordEncoder) {
	 * 
	 * DaoAuthenticationProvider provider = new
	 * DaoAuthenticationProvider(userDetailsService);
	 * 
	 * provider.setPasswordEncoder(passwordEncoder);
	 * 
	 * return provider; }
	 */

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

            // JWT-based API -> CSRF disabled
            .csrf(csrf -> csrf.disable())

            // Use the CorsConfigurationSource from CorsConfig
            .cors(cors -> {})

            // Stateless JWT authentication
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // Security headers
            .headers(headers -> headers

                // Prevent MIME-type sniffing
                .contentTypeOptions(
                    contentTypeOptions -> {}
                )

                // Prevent clickjacking
                .frameOptions(
                    frameOptions ->
                        frameOptions.deny()
                )

                // Control Referer information
                .referrerPolicy(
                    referrerPolicy ->
                        referrerPolicy
                            .policy(
                                org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy
                                    .STRICT_ORIGIN_WHEN_CROSS_ORIGIN
                            )
                )

                // Restrict browser features
                .permissionsPolicyHeader(
                	    permissionsPolicy ->
                	        permissionsPolicy.policy(
                	            "camera=(), " +
                	            "microphone=(), " +
                	            "geolocation=(), " +
                	            "payment=()"
                	        )
                	)

                // HSTS for HTTPS deployments
                .httpStrictTransportSecurity(
                    hsts -> hsts
                        .includeSubDomains(true)
                        .preload(false)
                        .maxAgeInSeconds(31536000)
                )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/actuator/health",
                    "/api/v1/auth/login",
                    "/api/v1/auth/register"
                ).permitAll()

                .anyRequest().authenticated()
            )

            .exceptionHandling(exception -> exception

                .authenticationEntryPoint(
                    jwtAuthenticationEntryPoint
                )

                .accessDeniedHandler(
                    jwtAccessDeniedHandler
                )
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}