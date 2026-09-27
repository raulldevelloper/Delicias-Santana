package com.raulld.deliciassantana.service;

import com.raulld.deliciassantana.exception.RecursoNaoEncontradoException;
import com.raulld.deliciassantana.entitys.Categoria;
import com.raulld.deliciassantana.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAllByOrderByOrdemExibicaoAsc();
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada: " + id));
    }

    @Transactional
    public Categoria criar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, Categoria dadosAtualizados) {
        Categoria categoria = buscarPorId(id);
        categoria.setNome(dadosAtualizados.getNome());
        categoria.setOrdemExibicao(dadosAtualizados.getOrdemExibicao());
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarPorId(id);
        categoriaRepository.delete(categoria);
    }
}
