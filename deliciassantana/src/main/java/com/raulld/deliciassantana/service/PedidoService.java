package com.raulld.deliciassantana.service;

import com.raulld.deliciassantana.dtos.CriarPedidoRequest;
import com.raulld.deliciassantana.exception.EstoqueInsuficienteException;
import com.raulld.deliciassantana.exception.RecursoNaoEncontradoException;
import com.raulld.deliciassantana.exception.TransicaoStatusInvalidaException;
import com.raulld.deliciassantana.entitys.*;
import com.raulld.deliciassantana.repository.ClienteRepository;
import com.raulld.deliciassantana.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoService produtoService;
    private final String whatsappNumeroLanchonete;

    // transições de status permitidas — evita a dona (ou um bug) pular etapas
    private static final Map<StatusPedido, List<StatusPedido>> TRANSICOES_PERMITIDAS = Map.of(
            StatusPedido.AGUARDANDO_PAGAMENTO, List.of(StatusPedido.PAGAMENTO_CONFIRMADO, StatusPedido.CANCELADO),
            StatusPedido.PAGAMENTO_CONFIRMADO, List.of(StatusPedido.EM_PREPARO, StatusPedido.CANCELADO),
            StatusPedido.EM_PREPARO, List.of(StatusPedido.PRONTO_PARA_RETIRADA, StatusPedido.CANCELADO),
            StatusPedido.PRONTO_PARA_RETIRADA, List.of(StatusPedido.RETIRADO),
            StatusPedido.RETIRADO, List.of(),
            StatusPedido.CANCELADO, List.of()
    );

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoService produtoService,
                         @org.springframework.beans.factory.annotation.Value("${lanchonete.whatsapp.numero}")
                         String whatsappNumeroLanchonete) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoService = produtoService;
        this.whatsappNumeroLanchonete = whatsappNumeroLanchonete;
    }

    @Transactional
    public Pedido criarPedido(CriarPedidoRequest request, Long usuarioId) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado para este usuário"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setObservacao(request.getObservacao());

        for (CriarPedidoRequest.ItemPedidoRequest itemReq : request.getItens()) {
            Produto produto = produtoService.buscarPorId(itemReq.getProdutoId());

            if (!produto.isAtivo()) {
                throw new EstoqueInsuficienteException(
                        "Produto indisponível no momento: " + produto.getNome());
            }
            if (!produto.temEstoqueDisponivel(itemReq.getQuantidade())) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para: " + produto.getNome());
            }

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade());
            item.setPrecoUnitario(produto.getPreco()); // "congela" o preço
            pedido.adicionarItem(item);
        }

        pedido.recalcularValorTotal();
        Pedido salvo = pedidoRepository.save(pedido);

        // decrementa estoque só depois de confirmar que o pedido inteiro é válido
        decrementarEstoqueDosItens(salvo);

        return salvo;
    }

    private void decrementarEstoqueDosItens(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            if (produto.isControlaEstoque()) {
                int novoEstoque = produto.getEstoqueAtual() - item.getQuantidade();
                produtoService.ajustarEstoque(produto.getId(), Math.max(novoEstoque, 0));
            }
        }
    }

    @Transactional
    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = buscarPorId(pedidoId);

        List<StatusPedido> permitidas = TRANSICOES_PERMITIDAS.get(pedido.getStatus());
        if (!permitidas.contains(novoStatus)) {
            throw new TransicaoStatusInvalidaException(
                    "Não é possível mudar de " + pedido.getStatus() + " para " + novoStatus);
        }

        pedido.setStatus(novoStatus);

        // se cancelado, devolve o estoque
        if (novoStatus == StatusPedido.CANCELADO) {
            devolverEstoqueDosItens(pedido);
        }

        return pedidoRepository.save(pedido);
    }

    private void devolverEstoqueDosItens(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            if (produto.isControlaEstoque()) {
                int novoEstoque = produto.getEstoqueAtual() + item.getQuantidade();
                produtoService.ajustarEstoque(produto.getId(), novoEstoque);
            }
        }
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByCriadoEmDesc(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidosAtivosParaPainel() {
        // "ativos" = tudo que a dona ainda precisa acompanhar
        return pedidoRepository.findByStatusInOrderByCriadoEmAsc(List.of(
                StatusPedido.AGUARDANDO_PAGAMENTO,
                StatusPedido.PAGAMENTO_CONFIRMADO,
                StatusPedido.EM_PREPARO,
                StatusPedido.PRONTO_PARA_RETIRADA
        ));
    }

    public String gerarLinkWhatsApp(Pedido pedido) {
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Olá! Gostaria de confirmar o pedido #").append(pedido.getId()).append(":\n");

        for (ItemPedido item : pedido.getItens()) {
            mensagem.append("- ").append(item.getQuantidade())
                    .append("x ").append(item.getProduto().getNome())
                    .append(" (R$ ").append(item.getSubtotal()).append(")\n");
        }

        mensagem.append("Total: R$ ").append(pedido.getValorTotal());
        if (pedido.getObservacao() != null && !pedido.getObservacao().isBlank()) {
            mensagem.append("\nObs: ").append(pedido.getObservacao());
        }

        String textoCodificado = URLEncoder.encode(mensagem.toString(), StandardCharsets.UTF_8);
        return "https://wa.me/" + whatsappNumeroLanchonete + "?text=" + textoCodificado;
    }
}