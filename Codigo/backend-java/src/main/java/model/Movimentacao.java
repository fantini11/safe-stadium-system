package model;

import java.sql.Timestamp;

public class Movimentacao {
    private int id;
    private String tipo; // "entrada" ou "saida"
    private String portao;
    private String cpf;
    private String nome;
    private String data;
    private String hora;
    private Timestamp timestamp;
    private Timestamp createdAt;
    private Integer usuarioId;
    private Integer eventoId;

    // Construtor vazio
    public Movimentacao() {}

    // Construtor completo
    public Movimentacao(int id, String tipo, String portao, String cpf, String nome,
                        String data, String hora, Timestamp timestamp, Timestamp createdAt,
                        Integer usuarioId, Integer eventoId) {
        this.id = id;
        this.tipo = tipo;
        this.portao = portao;
        this.cpf = cpf;
        this.nome = nome;
        this.data = data;
        this.hora = hora;
        this.timestamp = timestamp;
        this.createdAt = createdAt;
        this.usuarioId = usuarioId;
        this.eventoId = eventoId;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getPortao() { return portao; }
    public void setPortao(String portao) { this.portao = portao; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public Integer getEventoId() { return eventoId; }
    public void setEventoId(Integer eventoId) { this.eventoId = eventoId; }
}
