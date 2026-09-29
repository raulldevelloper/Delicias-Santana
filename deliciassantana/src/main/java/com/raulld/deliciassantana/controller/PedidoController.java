package com.raulld.deliciassantana.controller;

import com.raulld.deliciassantana.dtos.AtualizarStatusRequest;
import com.raulld.deliciassantana.dtos.CriarPedidoRequest;
import com.raulld.deliciassantana.dtos.PedidoResponse;
import com.raulld.deliciassantana.entitys.ItemPedido;
import com.raulld.deliciassantana.entitys.Pedido;
import com.raulld.deliciassantana.entitys.Role;
import com.raulld.deliciassantana.repository.ClienteRepository;
import com.raulld.deliciassantana.security.UsuarioDetails;
import com.raulld.deliciassantana.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteRepository clienteRepository;

    public PedidoController(PedidoService pedidoService, ClienteRepository clienteRepository) {
        this.pedidoService = pedidoService;
        this.clienteRepository = clienteRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@Valid @RequestBody CriarPedidoRequest request,
                                @AuthenticationPrincipal UsuarioDetails usuarioLogado) {
        Pedido pedido = pedidoService.criarPedido(request, usuarioLogado.getUsuario().getId());
        String linkWhatsApp = pedidoService.gerarLinkWhatsApp(pedido);
        return paraResponse(pedido, linkWhatsApp);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable Long id,
                                      @AuthenticationPrincipal UsuarioDetails usuarioLogado) {
        Pedido pedido = pedidoService.buscarPorId(id);
        validarDonoDoPedidoOuAdmin(pedido, usuarioLogado);
        return paraResponse(pedido, null);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<PedidoResponse> listarPorCliente(@PathVariable Long clienteId,
                                                 @AuthenticationPrincipal UsuarioDetails usuarioLogado) {
        boolean ehAdmin = usuarioLogado.getUsuario().getRole() == Role.ADMIN;
        boolean ehDonoDaConta = clienteRepository.findByUsuarioId(usuarioLogado.getUsuario().getId())
                .map(c -> c.getId().equals(clienteId))
                .orElse(false);

        if (!ehAdmin && !ehDonoDaConta) {
            throw new AccessDeniedException("Acesso negado");
        }

        return pedidoService.listarPorCliente(clienteId).stream()
                .map(p -> paraResponse(p, null))
                .collect(Collectors.toList());
    }

    @GetMapping("/painel")
    public List<PedidoResponse> listarPedidosAtivos() {
        return pedidoService.listarPedidosAtivosParaPainel().stream()
                .map(p -> paraResponse(p, null))
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}/status")
    public PedidoResponse atualizarStatus(@PathVariable Long id, @Valid @RequestBody AtualizarStatusRequest request) {
        Pedido pedido = pedidoService.atualizarStatus(id, request.getNovoStatus());
        return paraResponse(pedido, null);
    }

    private void validarDonoDoPedidoOuAdmin(Pedido pedido, UsuarioDetails usuarioLogado) {
        boolean ehAdmin = usuarioLogado.getUsuario().getRole() == Role.ADMIN;
        boolean ehDono = pedido.getCliente().getUsuario().getId().equals(usuarioLogado.getUsuario().getId());
        if (!ehAdmin && !ehDono) {
            throw new AccessDeniedException("Acesso negado");
        }
    }

    private PedidoResponse paraResponse(Pedido pedido, String linkWhatsApp) {
        List<PedidoResponse.ItemPedidoResponse> itens = pedido.getItens().stream()
                .map(this::paraItemResponse)
                .collect(Collectors.toList());

        return new PedidoResponse(
                pedido.getId(),
                pedido.getStatus(),
                pedido.getCriadoEm(),
                pedido.getValorTotal(),
                pedido.getObservacao(),
                itens,
                linkWhatsApp
        );
    }

    private PedidoResponse.ItemPedidoResponse paraItemResponse(ItemPedido item) {
        return new PedidoResponse.ItemPedidoResponse(
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal()
        );
    }
}