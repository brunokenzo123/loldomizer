package com.loldomizer.service;

import com.loldomizer.model.JogadorRequest;
import com.loldomizer.model.JogadorResponse;
import com.loldomizer.model.SorteioRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SorteioService {

    private final Map<String, List<String>> campeoes = Map.of(
            "top", List.of(
                    "Aatrox", "Akali", "Ambessa", "Camille", "Cassiopeia", "Cho'Gath", "Darius", "Dr. Mundo", "Fiora",
                    "Gangplank", "Garen", "Gnar", "Gragas", "Gwen", "Heimerdinger", "Illaoi", "Irelia", "Jax", "Jayce",
                    "K'Sante", "Karma", "Kayle", "Kennen", "Kled", "Lillia", "Malphite", "Maokai", "Mordekaiser", "Nasus",
                    "Olaf", "Ornn", "Pantheon", "Poppy", "Quinn", "Renekton", "Riven", "Rumble", "Ryze", "Sejuani", "Sett",
                    "Shen", "Singed", "Sion", "Smolder", "Tahm Kench", "Teemo", "Trundle", "Tryndamere", "Urgot", "Vayne",
                    "Volibear", "Warwick", "Wukong", "Yasuo", "Yone", "Yorick", "Zac"
            ),
            "jg", List.of(
                    "Amumu", "Bel'Veth", "Briar", "Diana", "Ekko", "Elise", "Evelynn", "Fiddlesticks", "Gragas", "Graves",
                    "Hecarim", "Ivern", "Jarvan IV", "Jax", "Karthus", "Kayn", "Kha'Zix", "Kindred", "Lee Sin", "Lillia",
                    "Master Yi", "Nidalee", "Nocturne", "Nunu e Willump", "Olaf", "Pantheon", "Poppy", "Rammus", "Rek'Sai",
                    "Rengar", "Sejuani", "Shaco", "Shyvana", "Skarner", "Sylas", "Taliyah", "Talon", "Trundle", "Udyr",
                    "Vi", "Viego", "Volibear", "Warwick", "Wukong", "Xin Zhao", "Zac", "Zed"
            ),
            "mid", List.of(
                    "Ahri", "Akali", "Akshan", "Anivia", "Annie", "Aurelion Sol", "Azir", "Cassiopeia", "Corki", "Diana",
                    "Ekko", "Fizz", "Galio", "Gangplank", "Gragas", "Heimerdinger", "Hwei", "Irelia", "Jayce", "Kassadin",
                    "Katarina", "Kayle", "LeBlanc", "Lissandra", "Locke", "Lucian", "Lux", "Malzahar", "Naafiri", "Neeko",
                    "Orianna", "Pantheon", "Qiyana", "Ryze", "Smolder", "Sylas", "Syndra", "Taliyah", "Talon", "Tristana",
                    "Twisted Fate", "Veigar", "Vel'Koz", "Vex", "Viktor", "Vladimir", "Xerath", "Yasuo", "Yone", "Zed",
                    "Ziggs", "Zoe"
            ),
            "adc", List.of(
                    "Aphelios", "Ashe", "Caitlyn", "Cassiopeia", "Draven", "Ezreal", "Jhin", "Jinx", "Kai'Sa", "Kalista",
                    "Karthus", "Kog'Maw", "Lucian", "Miss Fortune", "Nilah", "Samira", "Seraphine", "Sivir", "Smolder",
                    "Swain", "Syndra", "Tristana", "Twitch", "Varus", "Vayne", "Xayah", "Yasuo", "Zeri", "Ziggs"
            ),
            "sup", List.of(
                    "Alistar", "Amumu", "Ashe", "Bardo", "Blitzcrank", "Brand", "Braum", "Camille", "Galio", "Heimerdinger",
                    "Janna", "Karma", "Leona", "Lulu", "Lux", "Maokai", "Milio", "Morgana", "Nami", "Nautilus", "Neeko",
                    "Pantheon", "Pyke", "Rakan", "Rell", "Renata Glasc", "Senna", "Seraphine", "Shaco", "Sona", "Soraka",
                    "Swain", "Tahm Kench", "Taric", "Thresh", "Vel'Koz", "Xerath", "Yuumi", "Zilean", "Zyra"
            )
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