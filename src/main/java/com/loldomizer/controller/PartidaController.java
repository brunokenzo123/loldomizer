package com.loldomizer.controller;

import com.loldomizer.model.Partida;
import com.loldomizer.model.Usuario;
import com.loldomizer.repository.PartidaRepository;
import com.loldomizer.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/salvar")
    public ResponseEntity<String> salvarPartidaPendente(@RequestBody Partida partida) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuario = usuarioRepository.findByLogin(login);

        if (usuario.isPresent()) {
            partida.setUsuario(usuario.get());
            partida.setStatus("PENDENTE");
            partidaRepository.save(partida);
            return ResponseEntity.ok("Sorteio registrado!");
        }
        return ResponseEntity.status(403).body("Não autorizado.");
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Partida>> listarPendentes() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuario = usuarioRepository.findByLogin(login);

        if (usuario.isPresent()) {
            return ResponseEntity.ok(partidaRepository.findByUsuarioIdAndStatusOrderByIdDesc(usuario.get().getId(), "PENDENTE"));
        }
        return ResponseEntity.status(403).build();
    }

    // Busca o histórico de partidas
    @GetMapping("/historico")
    public ResponseEntity<List<Partida>> listarHistorico() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuario = usuarioRepository.findByLogin(login);

        if (usuario.isPresent()) {
            return ResponseEntity.ok(partidaRepository.findByUsuarioIdAndStatusNotOrderByIdDesc(usuario.get().getId(), "PENDENTE"));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> atualizarStatus(@PathVariable Long id, @RequestBody Partida dadosAtualizados) {
        Optional<Partida> partidaExistente = partidaRepository.findById(id);
        if (partidaExistente.isPresent()) {
            Partida partida = partidaExistente.get();
            partida.setStatus(dadosAtualizados.getStatus());
            partidaRepository.save(partida);
            return ResponseEntity.ok("Registrado com sucesso!");
        }
        return ResponseEntity.notFound().build();
    }

    // Calcula o Top 5 e o Melhor de cada categoria
    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Object>> buscarResumo() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuario = usuarioRepository.findByLogin(login);

        if (usuario.isPresent()) {
            List<Partida> todas = partidaRepository.findByUsuarioId(usuario.get().getId());

            // Calcula o ranking geral
            List<Map<String, Object>> statsCampeoes = calcularEstatisticas(todas, true);
            List<Map<String, Object>> statsLanes = calcularEstatisticas(todas, false);

            Map<String, Object> melhorCampeaoDestaque = null;

            // Se houver uma "Melhor Rota", filtra as partidas para achar o melhor campeão nela
            if (!statsLanes.isEmpty()) {
                String melhorLaneNome = (String) statsLanes.get(0).get("nome");

                List<Partida> partidasDaMelhorLane = new ArrayList<>();
                for (Partida p : todas) {
                    if (melhorLaneNome.equals(p.getLane())) {
                        partidasDaMelhorLane.add(p);
                    }
                }

                // Calcula as estatísticas dos campeões apenas dentro dessa rota
                List<Map<String, Object>> campeoesDestaque = calcularEstatisticas(partidasDaMelhorLane, true);
                if (!campeoesDestaque.isEmpty()) {
                    melhorCampeaoDestaque = campeoesDestaque.get(0);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("campeoes", statsCampeoes);
            response.put("lanes", statsLanes);
            response.put("melhorCampeaoDaLane", melhorCampeaoDestaque); // Retorna o campeão específico da rota

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(403).build();
    }

    // Função auxiliar para agrupar e calcular a porcentagem de vitória
    private List<Map<String, Object>> calcularEstatisticas(List<Partida> partidas, boolean porCampeao) {
        Map<String, long[]> contagem = new HashMap<>(); // index 0 = total validas, index 1 = vitorias

        for (Partida p : partidas) {
            // Ignora pendentes e remakes no cálculo de winrate
            if (p.getStatus().equals("PENDENTE") || p.getStatus().equals("REMAKE")) continue;

            String chave = porCampeao ? p.getCampeao() : p.getLane();
            contagem.putIfAbsent(chave, new long[]{0, 0});
            contagem.get(chave)[0]++;

            if (p.getStatus().equals("VITORIA")) {
                contagem.get(chave)[1]++;
            }
        }

        List<Map<String, Object>> lista = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : contagem.entrySet()) {
            long total = entry.getValue()[0];
            long vitorias = entry.getValue()[1];
            long wr = total > 0 ? (vitorias * 100) / total : 0;

            Map<String, Object> map = new HashMap<>();
            map.put("nome", entry.getKey());
            map.put("winrate", wr);
            map.put("totalJogos", total);
            lista.add(map);
        }

        // Ordena por maior Win Rate e depois por quantidade de jogos como desempate
        lista.sort((m1, m2) -> {
            Long wr1 = (Long) m1.get("winrate");
            Long wr2 = (Long) m2.get("winrate");
            int compWr = wr2.compareTo(wr1);
            if (compWr != 0) return compWr;
            return ((Long) m2.get("totalJogos")).compareTo((Long) m1.get("totalJogos"));
        });

        return lista;
    }
}