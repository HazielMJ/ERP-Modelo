package com.erp.erp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ✅ Leer orígenes permitidos desde application.properties
    @Value("${cors.allowed.origins:http://localhost:8082,http://localhost:8080}")
    private String allowedOrigins;

    /**
     * Bean para encriptar contraseñas usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de seguridad HTTP
     * - Desactiva CSRF (común en APIs REST)
     * - Habilita CORS con la configuración personalizada
     * - Permite acceso a todas las rutas sin autenticación
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }

    /**
     * Configuración de CORS (Cross-Origin Resource Sharing)
     * Permite que el frontend se comunique con el backend
     * 
     * IMPORTANTE: 
     * - allowCredentials(true) permite el uso de cookies/sesiones HTTP
     * - Cuando allowCredentials es true, NO se puede usar "*" en allowedOrigins
     * - Se deben especificar los orígenes exactos
     * - Los orígenes se leen desde application.properties
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ✅ Orígenes permitidos desde properties (separados por coma)
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins.stream()
            .map(String::trim)
            .toList());
        
        // ✅ Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", 
            "POST", 
            "PUT", 
            "DELETE", 
            "OPTIONS", 
            "PATCH"
        ));
        
        // ✅ Headers permitidos
        configuration.setAllowedHeaders(List.of("*"));
        
        // ✅ Permitir credenciales (cookies, sesiones HTTP)
        configuration.setAllowCredentials(true);
        
        // ✅ Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type"
        ));
        
        // ✅ Tiempo de cacheo de la configuración CORS (en segundos)
        configuration.setMaxAge(3600L);
        
        // Aplicar configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
