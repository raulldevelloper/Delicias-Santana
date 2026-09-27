package com.raulld.deliciassantana.dtos;

import com.raulld.deliciassantana.entitys.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResponse {

    private Long id;
    private StatusPedido status;
    private LocalDateTime criadoEm;
    private BigDecimal valorTotal;
    private String observacao;
    private List<ItemPedidoResponse> itens;
    private String linkWhatsApp; // só preenchido na criação do pedido

    // getters

    public static class ItemPedidoResponse {
        private String nomeProduto;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal subtotal;

        public ItemPedidoResponse(String nomeProduto, Integer quantidade,
                                  BigDecimal precoUnitario, BigDecimal subtotal) {
            this.nomeProduto = nomeProduto;
            this.quantidade = quantidade;
            this.precoUnitario = precoUnitario;
            this.subtotal = subtotal;
        }

        // getters
    }
}
