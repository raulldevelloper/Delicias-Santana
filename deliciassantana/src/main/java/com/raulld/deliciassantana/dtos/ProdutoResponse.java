package com.raulld.deliciassantana.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoResponse {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String fotoUrl;
    private String categoriaNome;
    private boolean disponivel; // já calcula ativo + estoque, o front não precisa saber a regra
    private Integer tempoPreparoMinutos;
}