package model;

import java.sql.Timestamp;

public class Incidente {
    private int id;
    private String data;
    private String horario;
    private String setor;
    private String tipo;
    private String descricao;
    private String nivel;
    private boolean policiamento;
    private boolean resolvido;
    private Timestamp createdAt;
    private Integer usuarioId;
    private Integer eventoId;

    public Incidente() {}

    public Incidente(
        int id,
        String data,
        String horario,
        String setor,
        String tipo,
        String descricao,
        String nivel,
        boolean policiamento,
        boolean resolvido,
        Timestamp createdAt,
        Integer usuarioId,
        Integer eventoId
    ) {
        this.id = id;
        this.data = data;
        this.horario = horario;
        this.setor = setor;
        this.tipo = tipo;
        this.descricao = descricao;
        this.nivel = nivel;
        this.policiamento = policiamento;
        this.resolvido = resolvido;
        this.createdAt = createdAt;
        this.usuarioId = usuarioId;
        this.eventoId = eventoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public boolean isPoliciamento() {
        return policiamento;
    }

    public void setPoliciamento(boolean policiamento) {
        this.policiamento = policiamento;
    }

    public boolean isResolvido() {
        return resolvido;
    }

    public void setResolvido(boolean resolvido) {
        this.resolvido = resolvido;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getEventoId() {
        return eventoId;
    }

    public void setEventoId(Integer eventoId) {
        this.eventoId = eventoId;
    }
}

