package com.raulld.deliciassantana.service;

import com.raulld.deliciassantana.dtos.CadastroClienteRequest;
import com.raulld.deliciassantana.entitys.Cliente;
import com.raulld.deliciassantana.entitys.Role;
import com.raulld.deliciassantana.entitys.Usuario;
import com.raulld.deliciassantana.exception.EmailJaCadastradoException;
import com.raulld.deliciassantana.exception.RecursoNaoEncontradoException;
import com.raulld.deliciassantana.repository.ClienteRepository;
import com.raulld.deliciassantana.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Cliente cadastrar(CadastroClienteRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailJaCadastradoException("Este email já está cadastrado: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha())); // agora vai com hash
        usuario.setRole(Role.CLIENTE);
        usuario = usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setTelefone(request.getTelefone());
        cliente.setUsuario(usuario);

        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente dadosAtualizados) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(dadosAtualizados.getNome());
        cliente.setTelefone(dadosAtualizados.getTelefone());
        return clienteRepository.save(cliente);
    }
}