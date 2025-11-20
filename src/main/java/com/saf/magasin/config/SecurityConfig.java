package com.saf.magasin.config;

import com.saf.magasin.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Désactiver CSRF pour API REST
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Routes publiques
                .requestMatchers("/api/auth/register", "/api/auth/login", 
                                "/api/auth/verify", "/api/auth/resend-verification").permitAll()
                .requestMatchers("/", "/static/**", "/*.html", "/favicon.ico", "/uploads/**").permitAll()
                .requestMatchers("/login.html", "/register.html").permitAll()
                
                // Lecture publique des disques (GET uniquement)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/disques", "/api/disques/*").permitAll()
                
                // Routes protégées - nécessitent authentification
                .requestMatchers("/api/disques/**").authenticated()
                .requestMatchers("/api/commandes/**").authenticated()
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/images/**").authenticated()
                
                // Tout le reste nécessite authentification
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.permitAll());
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
