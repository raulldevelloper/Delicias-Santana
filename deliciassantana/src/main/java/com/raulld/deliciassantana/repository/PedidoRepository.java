package com.raulld.deliciassantana.repository;

import com.raulld.deliciassantana.entitys.Pedido;
import com.raulld.deliciassantana.entitys.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByCriadoEmDesc(Long clienteId);
    @org.springframework.data.jpa.repository.Query(
            "SELECT DISTINCT p FROM Pedido p " +
                    "JOIN FETCH p.itens i " +
                    "JOIN FETCH i.produto " +
                    "WHERE p.status IN :status " +
                    "ORDER BY p.criadoEm ASC"
    )
    List<Pedido> findByStatusInOrderByCriadoEmAsc(@org.springframework.data.repository.query.Param("status") List<StatusPedido> status);
}
