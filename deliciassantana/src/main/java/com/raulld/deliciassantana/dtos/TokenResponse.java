package com.raulld.deliciassantana.dtos;

public class TokenResponse {
    private String token;
    private String role;
    private String email;
    private Long clienteId; // null se for ADMIN

    public TokenResponse(String token, String role, String email, Long clienteId) {
        this.token = token;
        this.role = role;
        this.email = email;
        this.clienteId = clienteId;
    }

    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public Long getClienteId() { return clienteId; }
}