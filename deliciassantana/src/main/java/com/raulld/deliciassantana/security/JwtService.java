package com.raulld.deliciassantana.security;

import com.raulld.deliciassantana.entitys.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey chaveAssinatura;
    private final long tempoExpiracaoMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long tempoExpiracaoMs) {
        this.chaveAssinatura = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tempoExpiracaoMs = tempoExpiracaoMs;
    }

    public String gerarToken(Usuario usuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + tempoExpiracaoMs);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("role", usuario.getRole().name())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chaveAssinatura, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public Long extrairId(String token) {
        Claims claims = extrairTodosClaims(token);
        return claims.get("id", Long.class);
    }

    public String extrairRole(String token) {
        Claims claims = extrairTodosClaims(token);
        return claims.get("role", String.class);
    }

    public boolean tokenValido(String token, String emailEsperado) {
        String email = extrairEmail(token);
        return email.equals(emailEsperado) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        Date expiracao = extrairClaim(token, Claims::getExpiration);
        return expiracao.before(new Date());
    }

    private <T> T extrairClaim(String token, Function<Claims, T> resolvedor) {
        Claims claims = extrairTodosClaims(token);
        return resolvedor.apply(claims);
    }

    private Claims extrairTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(chaveAssinatura)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
