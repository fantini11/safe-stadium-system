package model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Evento {
    private int id;
    private String nome;
    private Date data;
    private Time horario;
    private String local;
    private String descricao;
    private Timestamp createdAt;
    private Integer criadoPorAdmin;

    // Construtor vazio
    public Evento() {}

    // Construtor completo
    public Evento(int id, String nome, Date data, Time horario, String local, String descricao, Timestamp createdAt, Integer criadoPorAdmin) {
        this.id = id;
        this.nome = nome;
        this.data = data;
        this.horario = horario;
        this.local = local;
        this.descricao = descricao;
        this.createdAt = createdAt;
        this.criadoPorAdmin = criadoPorAdmin;
    }

    // Construtor sem ID (para insert)
    public Evento(String nome, Date data, Time horario, String local, String descricao) {
        this.nome = nome;
        this.data = data;
        this.horario = horario;
        this.local = local;
        this.descricao = descricao;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Time getHorario() {
        return horario;
    }

    public void setHorario(Time horario) {
        this.horario = horario;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCriadoPorAdmin() {
        return criadoPorAdmin;
    }

    public void setCriadoPorAdmin(Integer criadoPorAdmin) {
        this.criadoPorAdmin = criadoPorAdmin;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", data=" + data +
                ", horario=" + horario +
                ", local='" + local + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
