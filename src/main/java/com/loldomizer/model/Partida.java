package com.loldomizer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String lane;
    private String campeao;
    private String build;

    @Column(nullable = false)
    private String status = "PENDENTE";

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getLane() { return lane; }
    public void setLane(String lane) { this.lane = lane; }
    public String getCampeao() { return campeao; }
    public void setCampeao(String campeao) { this.campeao = campeao; }
    public String getBuild() { return build; }
    public void setBuild(String build) { this.build = build; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}