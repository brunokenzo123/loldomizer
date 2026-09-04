package com.loldomizer.model;

public class JogadorResponse {
    private String nome;
    private String lane;
    private String campeao;
    private String build;

    public JogadorResponse(String nome, String lane, String campeao, String build) {
        this.nome = nome;
        this.lane = lane;
        this.campeao = campeao;
        this.build = build;
    }

    // Getters
    public String getNome() { return nome; }
    public String getLane() { return lane; }
    public String getCampeao() { return campeao; }
    public String getBuild() { return build; }
}