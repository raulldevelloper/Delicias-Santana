package com.raulld.deliciassantana.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CadastroClienteRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String telefone;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}