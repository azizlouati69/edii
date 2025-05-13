package com.example.AuthService.config;

import com.example.AuthService.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class PrimarySecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(PrimarySecurityConfig.class);

    private final JwtService jwtService;

    public PrimarySecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean(name = "primarySecurityFilterChain")
    @Primary
    @Order(1)
    public SecurityFilterChain primarySecurityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring Primary SecurityFilterChain for /auth/**");

        http
                .securityMatcher("/auth/**")
                .cors(cors -> cors.configurationSource(primaryCorsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/auth/register"),
                                new AntPathRequestMatcher("/auth/login"),
                                new AntPathRequestMatcher("/error")
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean(name = "primaryCorsConfigurationSource")
    @Primary
    public CorsConfigurationSource primaryCorsConfigurationSource() {
        logger.info("Configuring Primary CorsConfigurationSource");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean(name = "PrimarysecurityPasswordEncoder")
    public PasswordEncoder securityPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "PrimaryJwtAuthenticationFilter")
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService);
    }
}
