package com.raulld.deliciassantana.repository;

import com.raulld.deliciassantana.entitys.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findAllByOrderByOrdemExibicaoAsc();
}
