package com.loldomizer.repository;

import com.loldomizer.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Long> {
    List<Partida> findByUsuarioId(Long usuarioId);

    // Busca os pendentes
    List<Partida> findByUsuarioIdAndStatusOrderByIdDesc(Long usuarioId, String status);

    // NOVO: Busca o histórico (tudo que NÃO for o status passado, ex: tudo que não for PENDENTE)
    List<Partida> findByUsuarioIdAndStatusNotOrderByIdDesc(Long usuarioId, String status);
}