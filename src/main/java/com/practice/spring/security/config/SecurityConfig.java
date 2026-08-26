package com.practice.spring.security.config;

import com.practice.spring.security.handler.AuthenticationEntryPointImpl;
import com.practice.spring.security.filter.JwtFilter;
import com.practice.spring.security.user.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter, AuthenticationEntryPointImpl authenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers(HttpMethod.GET, "/notes", "/notes/**", "/notes/export").hasAuthority(Authority.READ.getAuthority())
                        .requestMatchers(HttpMethod.POST, "/notes/create", "/notes/import").hasAuthority(Authority.WRITE.getAuthority())
                        .requestMatchers(HttpMethod.PUT, "/notes/update/**").hasAuthority(Authority.WRITE.getAuthority())
                        .requestMatchers(HttpMethod.DELETE, "/notes/delete/**").hasAuthority(Authority.WRITE.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/actuator/**").hasAuthority(Authority.ADMIN.getAuthority())
                        .anyRequest().permitAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
