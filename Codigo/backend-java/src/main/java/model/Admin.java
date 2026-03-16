package model;

import java.sql.Timestamp;

public class Admin {
    private int id;
    private String login;
    private String senha;
    private String nome;
    private String email;
    private Timestamp createdAt;

    // Construtor vazio
    public Admin() {}

    // Construtor completo
    public Admin(int id, String login, String senha, String nome, String email, Timestamp createdAt) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
