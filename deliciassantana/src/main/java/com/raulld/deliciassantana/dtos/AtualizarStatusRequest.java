package com.raulld.deliciassantana.dtos;

import com.raulld.deliciassantana.entitys.StatusPedido;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AtualizarStatusRequest {

    @NotNull
    private StatusPedido novoStatus;

    // getters e setters
}
