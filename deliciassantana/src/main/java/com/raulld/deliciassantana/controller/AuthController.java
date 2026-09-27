package com.raulld.deliciassantana.controller;

import com.raulld.deliciassantana.dtos.CadastroAdminRequest;
import com.raulld.deliciassantana.dtos.LoginRequest;
import com.raulld.deliciassantana.dtos.TokenResponse;
import com.raulld.deliciassantana.entitys.Cliente;
import com.raulld.deliciassantana.entitys.Role;
import com.raulld.deliciassantana.entitys.Usuario;
import com.raulld.deliciassantana.exception.EmailJaCadastradoException;
import com.raulld.deliciassantana.repository.ClienteRepository;
import com.raulld.deliciassantana.repository.UsuarioRepository;
import com.raulld.deliciassantana.security.JwtService;
import com.raulld.deliciassantana.security.UsuarioDetails;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          ClienteRepository clienteRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteRepository = clienteRepository;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));

        UsuarioDetails usuarioDetails = (UsuarioDetails) auth.getPrincipal();
        Usuario usuario = usuarioDetails.getUsuario();

        String token = jwtService.gerarToken(usuario);

        Long clienteId = clienteRepository.findByUsuarioId(usuario.getId())
                .map(Cliente::getId)
                .orElse(null); // ADMIN não tem Cliente vinculado

        return new TokenResponse(token, usuario.getRole().name(), usuario.getEmail(), clienteId);
    }

    // TODO: endpoint temporário só para criar o usuário ADMIN nos testes.
    // Remover ou proteger com senha de setup antes de qualquer coisa real.
    @PostMapping("/registrar-admin")
    public TokenResponse registrarAdmin(@Valid @RequestBody CadastroAdminRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailJaCadastradoException("Este email já está cadastrado: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(Role.ADMIN);
        usuario = usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario);
        return new TokenResponse(token, usuario.getRole().name(), usuario.getEmail(), null); // admin não tem clienteId
    }
}