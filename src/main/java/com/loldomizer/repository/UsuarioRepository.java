package com.loldomizer.repository;

import com.loldomizer.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // O Spring cria o comando SQL automaticamente baseado na nomenclatura deste método
    Optional<Usuario> findByLogin(String login);
}