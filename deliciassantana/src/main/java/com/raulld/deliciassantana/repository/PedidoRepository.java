package com.raulld.deliciassantana.repository;

import com.raulld.deliciassantana.entitys.Pedido;
import com.raulld.deliciassantana.entitys.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByCriadoEmDesc(Long clienteId);
    List<Pedido> findByStatusOrderByCriadoEmAsc(StatusPedido status);
    List<Pedido> findByStatusInOrderByCriadoEmAsc(List<StatusPedido> status);
}
