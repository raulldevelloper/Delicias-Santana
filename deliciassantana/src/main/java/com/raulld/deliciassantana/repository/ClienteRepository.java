package com.raulld.deliciassantana.repository;

import com.raulld.deliciassantana.entitys.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByTelefone(String telefone);
    Optional<Cliente> findByUsuarioId(Long usuarioId);
}