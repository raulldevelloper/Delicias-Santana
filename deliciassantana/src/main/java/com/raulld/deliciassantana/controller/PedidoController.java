package com.raulld.deliciassantana.controller;

import com.raulld.deliciassantana.dtos.AtualizarStatusRequest;
import com.raulld.deliciassantana.dtos.CriarPedidoRequest;
import com.raulld.deliciassantana.dtos.PedidoResponse;
import com.raulld.deliciassantana.entitys.ItemPedido;
import com.raulld.deliciassantana.entitys.Pedido;
import com.raulld.deliciassantana.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // usado pelo cliente ao finalizar o carrinho
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@Valid @RequestBody CriarPedidoRequest request) {
        Pedido pedido = pedidoService.criarPedido(request);
        String linkWhatsApp = pedidoService.gerarLinkWhatsApp(pedido);
        return paraResponse(pedido, linkWhatsApp);
    }

    // usado pelo cliente pra acompanhar status do próprio pedido
    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable Long id) {
        return paraResponse(pedidoService.buscarPorId(id), null);
    }

    // usado pelo cliente pra ver histórico de pedidos dele
    @GetMapping("/cliente/{clienteId}")
    public List<PedidoResponse> listarPorCliente(@PathVariable Long clienteId) {
        return pedidoService.listarPorCliente(clienteId).stream()
                .map(p -> paraResponse(p, null))
                .collect(Collectors.toList());
    }

    // usado pelo PAINEL DA DONA — lista tudo que ainda precisa de atenção
    @GetMapping("/painel")
    public List<PedidoResponse> listarPedidosAtivos() {
        return pedidoService.listarPedidosAtivosParaPainel().stream()
                .map(p -> paraResponse(p, null))
                .collect(Collectors.toList());
    }

    // usado pelo PAINEL DA DONA — avança o status do pedido
    @PatchMapping("/{id}/status")
    public PedidoResponse atualizarStatus(@PathVariable Long id, @Valid @RequestBody AtualizarStatusRequest request) {
        Pedido pedido = pedidoService.atualizarStatus(id, request.getNovoStatus());
        return paraResponse(pedido, null);
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
