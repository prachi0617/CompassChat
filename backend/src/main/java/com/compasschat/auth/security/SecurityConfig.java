package com.compasschat.auth.security;
    
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF protection is for cookie-based logins; we use wristbands, so off for the demo.
            .csrf(csrf -> csrf.disable())

            // "Stateless" = the club keeps NO memory of who's inside.
            // Your wristband is your only proof, every single time.
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // The front door and signup table are open to everyone:
                .requestMatchers("/api/auth/**").permitAll()
                // The WebSocket handshake door is open; we check the wristband inside it:
                .requestMatchers("/ws/**").permitAll()
                // H2's little database viewer, demo only:
                .requestMatchers("/h2-console/**").permitAll()
                // AI endpoints are open so unauthenticated / guest users can use the assistant:
                .requestMatchers("/api/ai/**").permitAll()
                // EVERY other door requires a valid wristband:
                .anyRequest().authenticated()
            )

            // H2 console needs frames allowed (demo only)
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))

            // Put our wristband-checker BEFORE Spring's default password checker
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
