package opsflow_backend.config;

import opsflow_backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ==========================================
                // DISABLE CSRF
                // ==========================================
                .csrf(csrf -> csrf.disable())

                // ==========================================
                // DISABLE FORM LOGIN
                // ==========================================
                .formLogin(form -> form.disable())

                // ==========================================
                // DISABLE HTTP BASIC
                // ==========================================
                .httpBasic(basic -> basic.disable())

                // ==========================================
                // JWT = STATELESS
                // ==========================================
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // ==========================================
                // AUTHORIZATION RULES
                // ==========================================
                .authorizeHttpRequests(auth -> auth

                        // ----------------------------------
                        // PUBLIC AUTHENTICATION
                        // ----------------------------------
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // ----------------------------------
                        // PUBLIC ACTUATOR
                        // ----------------------------------
                        .requestMatchers("/actuator/health")
                        .permitAll()

                        .requestMatchers("/actuator/info")
                        .permitAll()

                        // ----------------------------------
                        // PUBLIC SWAGGER / OPENAPI
                        // ----------------------------------
                        .requestMatchers("/swagger-ui.html")
                        .permitAll()

                        .requestMatchers("/swagger-ui/**")
                        .permitAll()

                        .requestMatchers("/v3/api-docs/**")
                        .permitAll()

                        // ==================================
                        // TASK ENDPOINTS
                        // ADMIN + MANAGER
                        // ==================================

                        // /api/tasks/{id}
                        .requestMatchers("/api/tasks/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // /api/projects/{projectId}/tasks
                        // /api/projects/{projectId}/tasks/{id}
                        .requestMatchers("/api/projects/*/tasks/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // ==================================
                        // PROJECT + PROJECT MEMBER ENDPOINTS
                        // ADMIN + MANAGER
                        // ==================================
                        .requestMatchers("/api/projects/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // ==================================
                        // ADMIN ONLY
                        // ==================================
                        .requestMatchers("/api/users/admin-test")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")

                        // ==================================
                        // EVERYTHING ELSE
                        // ==================================
                        .anyRequest()
                        .authenticated()
                )

                // ==========================================
                // JWT FILTER
                // ==========================================
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}