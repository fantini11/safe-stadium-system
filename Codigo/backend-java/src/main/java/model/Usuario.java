package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Usuario {
    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private Date dataNascimento;
    private String clubeCoracao;
    private String foto;
    private Timestamp cadastradoEm;

    // Construtor vazio
    public Usuario() {}

    // Construtor completo
    public Usuario(int id, String nome, String cpf, String email, String telefone, 
                   Date dataNascimento, String clubeCoracao, String foto, Timestamp cadastradoEm) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.clubeCoracao = clubeCoracao;
        this.foto = foto;
        this.cadastradoEm = cadastradoEm;
    }

    // Construtor sem ID (para insert)
    public Usuario(String nome, String cpf, String email, String telefone, 
                   Date dataNascimento, String clubeCoracao) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.clubeCoracao = clubeCoracao;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getClubeCoracao() {
        return clubeCoracao;
    }

    public void setClubeCoracao(String clubeCoracao) {
        this.clubeCoracao = clubeCoracao;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Timestamp getCadastradoEm() {
        return cadastradoEm;
    }

    public void setCadastradoEm(Timestamp cadastradoEm) {
        this.cadastradoEm = cadastradoEm;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", clubeCoracao='" + clubeCoracao + '\'' +
                ", cadastradoEm=" + cadastradoEm +
                '}';
    }
}
