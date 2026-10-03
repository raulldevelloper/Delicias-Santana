package com.raulld.deliciassantana.repository;

import com.raulld.deliciassantana.entitys.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Produto p JOIN FETCH p.categoria WHERE p.ativo = true ORDER BY p.categoria.id ASC, p.nome ASC")
    List<Produto> findByAtivoTrueOrderByCategoriaAscNomeAsc();
    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Produto p JOIN FETCH p.categoria ORDER BY p.categoria.id ASC, p.nome ASC")
    List<Produto> findAllByOrderByCategoriaIdAscNomeAsc();
}
