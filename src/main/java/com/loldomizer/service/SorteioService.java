package com.loldomizer.service;

import com.loldomizer.model.JogadorRequest;
import com.loldomizer.model.JogadorResponse;
import com.loldomizer.model.SorteioRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SorteioService {

    private final Map<String, List<String>> campeoes = Map.of(
            "top", List.of("Aatrox", "Ambessa", "Camille", "Darius", "Fiora", "Garen", "Mordekaiser", "Ornn", "Sett", "Yasuo"),
            "mid", List.of("Ahri", "Akali", "Azir", "Ekko", "LeBlanc", "Lux", "Syndra", "Yasuo", "Yone", "Zed","Locke"),
            "adc", List.of("Ashe", "Caitlyn", "Draven", "Ezreal", "Jhin", "Jinx", "Kai'Sa", "Lucian", "Vayne", "Xayah"),
            "sup", List.of("Alistar", "Blitzcrank", "Braum", "Leona", "Lulu", "Morgana", "Nautilus", "Pyke", "Thresh", "Zyra"),
            "jg",  List.of("Amumu", "Briar", "Ekko", "Graves", "Hecarim", "Kayn", "Lee Sin", "Sejuani", "Vi", "Viego")
    );

    private final Map<String, String> builds = Map.of(
            "WhatsApp", "Sua build e runas deverão ser verdes",
            "YouTube",  "Sua build e runas deverão ser vermelhas",
            "Twitch",   "Sua build e runas deverão ser roxas",
            "Facebook", "Sua build e runas deverão ser azuis"
    );

    private final Random random = new Random();

    public List<JogadorResponse> realizarSorteio(SorteioRequest request) {
        List<String> lanesDisponiveis = new ArrayList<>(List.of("top", "jg", "mid", "adc", "sup"));
        Set<String> campeoesUsados = new HashSet<>();
        List<JogadorResponse> resultados = new ArrayList<>();

        for (JogadorRequest jogador : request.getJogadores()) {
            String lane;

            if (request.getOpcaoSorteio() == 1) {
                lane = jogador.getLane().toLowerCase();
                if (!lanesDisponiveis.contains(lane)) {
                    throw new IllegalArgumentException("Lane indisponível ou já escolhida: " + lane);
                }
                lanesDisponiveis.remove(lane);
            } else {
                if (lanesDisponiveis.isEmpty()) {
                    throw new IllegalArgumentException("Lanes esgotadas.");
                }
                int idx = random.nextInt(lanesDisponiveis.size());
                lane = lanesDisponiveis.remove(idx);
            }

            // Sorteio do Campeão
            List<String> pool = campeoes.getOrDefault(lane, Collections.emptyList());
            List<String> candidatos = pool.stream()
                    .filter(c -> !campeoesUsados.contains(c))
                    .toList();

            if (candidatos.isEmpty()) {
                throw new IllegalStateException("Sem campeões disponíveis para a lane " + lane);
            }

            String campeaoSorteado = candidatos.get(random.nextInt(candidatos.size()));
            campeoesUsados.add(campeaoSorteado);

            // Sorteio da Build
            String buildDescricao = "Sem build adicional.";
            if (jogador.isSortearBuild()) {
                List<String> chavesBuild = new ArrayList<>(builds.keySet());
                String buildRede = chavesBuild.get(random.nextInt(chavesBuild.size()));
                buildDescricao = "Build " + buildRede + ": " + builds.get(buildRede);
            }

            resultados.add(new JogadorResponse(jogador.getNome(), lane.toUpperCase(), campeaoSorteado, buildDescricao));
        }

        return resultados;
    }
}