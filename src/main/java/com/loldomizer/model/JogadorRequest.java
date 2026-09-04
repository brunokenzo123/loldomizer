package com.loldomizer.model;

public class JogadorRequest {
    private String nome;
    private String lane; // Pode ser null se a opção for 2
    private boolean sortearBuild;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getLane() { return lane; }
    public void setLane(String lane) { this.lane = lane; }
    public boolean isSortearBuild() { return sortearBuild; }
    public void setSortearBuild(boolean sortearBuild) { this.sortearBuild = sortearBuild; }
}