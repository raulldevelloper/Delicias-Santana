package com.raulld.deliciassantana.controller;

import com.raulld.deliciassantana.dtos.ProdutoResponse;
import com.raulld.deliciassantana.entitys.Produto;
import com.raulld.deliciassantana.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<ProdutoResponse> listarCardapio() {
        return produtoService.listarCardapio().stream()
                .map(this::paraResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscarPorId(@PathVariable Long id) {
        return paraResponse(produtoService.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse criar(@RequestBody Produto produto) {
        return paraResponse(produtoService.criar(produto));
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        return paraResponse(produtoService.atualizar(id, produto));
    }

    @PatchMapping("/{id}/estoque")
    public ProdutoResponse ajustarEstoque(@PathVariable Long id, @RequestParam Integer quantidade) {
        produtoService.ajustarEstoque(id, quantidade);
        return paraResponse(produtoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        produtoService.excluir(id);
    }

    private ProdutoResponse paraResponse(Produto produto) {
        boolean disponivel = produto.isAtivo()
                && (!produto.isControlaEstoque() || produto.getEstoqueAtual() > 0);

        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getFotoUrl(),
                produto.getCategoria().getNome(),
                disponivel,
                produto.getTempoPreparoMinutos()
        );
    }
}
