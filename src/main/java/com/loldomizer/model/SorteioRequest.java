package com.loldomizer.model;

import java.util.List;

public class SorteioRequest {
    private int opcaoSorteio; // 1: escolhe lane | 2: sorteia lane
    private List<JogadorRequest> jogadores;

    // Getters e Setters
    public int getOpcaoSorteio() { return opcaoSorteio; }
    public void setOpcaoSorteio(int opcaoSorteio) { this.opcaoSorteio = opcaoSorteio; }
    public List<JogadorRequest> getJogadores() { return jogadores; }
    public void setJogadores(List<JogadorRequest> jogadores) { this.jogadores = jogadores; }
}