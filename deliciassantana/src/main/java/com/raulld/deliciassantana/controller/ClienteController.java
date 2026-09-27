package com.raulld.deliciassantana.controller;

import com.raulld.deliciassantana.dtos.CadastroClienteRequest;
import com.raulld.deliciassantana.dtos.ClienteResponse;
import com.raulld.deliciassantana.entitys.Cliente;
import com.raulld.deliciassantana.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse cadastrar(@Valid @RequestBody CadastroClienteRequest request) {
        return paraResponse(clienteService.cadastrar(request));
    }

    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(@PathVariable Long id) {
        return paraResponse(clienteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        return paraResponse(clienteService.atualizar(id, cliente));
    }

    private ClienteResponse paraResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getUsuario().getEmail(),
                cliente.getUsuario().getRole().name()
        );
    }
}