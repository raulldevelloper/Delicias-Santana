package com.raulld.deliciassantana.dtos;

public class ClienteResponse {

    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String role;

    public ClienteResponse(Long id, String nome, String telefone, String email, String role) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
