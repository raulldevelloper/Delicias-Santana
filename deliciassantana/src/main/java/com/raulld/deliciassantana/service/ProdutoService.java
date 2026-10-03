package com.raulld.deliciassantana.service;

import com.raulld.deliciassantana.exception.RecursoNaoEncontradoException;
import com.raulld.deliciassantana.entitys.Categoria;
import com.raulld.deliciassantana.entitys.Produto;
import com.raulld.deliciassantana.repository.CategoriaRepository;
import com.raulld.deliciassantana.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Produto> listarCardapio() {
        return produtoRepository.findByAtivoTrueOrderByCategoriaAscNomeAsc();
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    @Transactional
    public Produto criar(Produto produto) {
        Categoria categoria = buscarCategoriaOuFalhar(produto.getCategoria().getId());
        produto.setCategoria(categoria);
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, Produto dadosAtualizados) {
        Produto produto = buscarPorId(id);
        produto.setNome(dadosAtualizados.getNome());
        produto.setDescricao(dadosAtualizados.getDescricao());
        produto.setPreco(dadosAtualizados.getPreco());
        produto.setFotoUrl(dadosAtualizados.getFotoUrl());
        produto.setCategoria(dadosAtualizados.getCategoria());
        produto.setControlaEstoque(dadosAtualizados.isControlaEstoque());
        produto.setTempoPreparoMinutos(dadosAtualizados.getTempoPreparoMinutos());
        produto.setEstoqueAtual(dadosAtualizados.getEstoqueAtual());
        produto.setAtivo(dadosAtualizados.isAtivo());
        return produtoRepository.save(produto);
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarPorId(id);
        produtoRepository.delete(produto);
    }

    @Transactional
    public void ajustarEstoque(Long produtoId, int novoEstoque) {
        Produto produto = buscarPorId(produtoId);
        produto.setEstoqueAtual(novoEstoque);
        if (produto.isControlaEstoque() && novoEstoque == 0) {
            produto.setAtivo(false);
        }
        produtoRepository.save(produto);
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodosParaAdmin() {
        return produtoRepository.findAllByOrderByCategoriaIdAscNomeAsc();
    }

    private Categoria buscarCategoriaOuFalhar(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada: " + categoriaId));
    }
}