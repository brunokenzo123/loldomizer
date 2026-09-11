package com.loldomizer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF temporariamente para facilitar a requisição via Fetch API do JavaScript
                .authorizeHttpRequests(auth -> auth
                        // Libera a página inicial, arquivos estáticos e a futura rota de registro de usuários
                        .requestMatchers( "/login.html", "/img/**", "/api/usuarios/registrar", "/api/usuarios/login").permitAll()
                        // Todas as outras rotas exigirão um usuário logado
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        // Regra de Redirecionamento: Se alguém sem acesso tentar entrar em uma rota bloqueada, mande para o login
                        .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login.html"))
                )
                .formLogin(form -> form
                        .disable() // Desativamos o formulário padrão do Spring para podermos usar o seu com Tailwind depois
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Retorna o algoritmo BCrypt, que gera hashes seguros para as senhas no banco
        return new BCryptPasswordEncoder();
    }
}