package com.dab.book_network.config;

import com.dab.book_network.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Habilita el manejo de CORS usando el bean configurado abajo
                .cors(withDefaults())

                // ✅ Desactiva CSRF porque estamos usando JWT
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ Establece manejo de sesión sin estado (para APIs REST)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))

                // ✅ Configura reglas de autorización
                .authorizeHttpRequests(req -> req
                        .requestMatchers(
                                "/api/v1/auth/**",    // incluye el prefijo del context-path
                                "/auth/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ Configura el proveedor de autenticación y el filtro JWT
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * ✅ Configuración CORREGIDA de CORS
     * Soluciona el error 403 en preflight requests (OPTIONS)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔹 Usa allowedOriginPatterns para mayor flexibilidad
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://social-book-frontend.vercel.app",
                "http://localhost:4200"
        ));

        // 🔹 Métodos explícitamente permitidos (incluye OPTIONS)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 🔹 Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With"));

        // 🔹 Headers expuestos
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Get-Header"));

        // 🔹 Permitir credenciales
        configuration.setAllowCredentials(true);

        // 🔹 Tiempo de cache para preflight (1 hora)
        configuration.setMaxAge(3600L);

        // 🔹 IMPORTANTE: NO usar applyPermitDefaultValues() porque sobrescribe tu configuración

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}