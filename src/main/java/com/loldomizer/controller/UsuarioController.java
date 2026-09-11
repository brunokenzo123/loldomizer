package com.loldomizer.controller;

import com.loldomizer.model.Usuario;
import com.loldomizer.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Rota para criar uma nova conta
    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody Usuario novoUsuario) {
        // Verifica se o login já existe no banco
        if (repository.findByLogin(novoUsuario.getLogin()).isPresent()) {
            return ResponseEntity.badRequest().body("Este usuário já existe.");
        }

        // Criptografa a senha antes de salvar
        novoUsuario.setSenha(passwordEncoder.encode(novoUsuario.getSenha()));
        repository.save(novoUsuario);

        return ResponseEntity.ok("Conta criada com sucesso! Você já pode fazer login.");
    }

    // Rota para validar o acesso
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuario usuarioLogin, HttpSession session) {
        Optional<Usuario> userDb = repository.findByLogin(usuarioLogin.getLogin());

        // Verifica se o usuário existe e se a senha digitada bate com o hash do banco
        if (userDb.isPresent() && passwordEncoder.matches(usuarioLogin.getSenha(), userDb.get().getSenha())) {

            // Avisa ao "Firewall" (Spring Security) que este usuário tem permissão para navegar
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDb.get().getLogin(), null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Salva a sessão no servidor
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            session.setAttribute("usuarioId", userDb.get().getId());

            return ResponseEntity.ok("Acesso liberado");
        }

        return ResponseEntity.status(401).body("Usuário ou senha incorretos.");
    }
}