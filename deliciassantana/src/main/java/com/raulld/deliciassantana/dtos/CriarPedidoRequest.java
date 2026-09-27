package com.raulld.deliciassantana.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriarPedidoRequest {

    @NotNull
    private Long clienteId;

    private String observacao;

    @NotEmpty
    @Valid
    private List<ItemPedidoRequest> itens;

    // getters e setters

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemPedidoRequest {
        @NotNull
        private Long produtoId;

        @NotNull
        private Integer quantidade;


        // getters e setters
    }
}
