package com.raulld.deliciassantana.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco; // BigDecimal sempre para dinheiro, nunca double

    private String fotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false)
    private boolean controlaEstoque = false;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer estoqueAtual = 0;

    @Column(nullable = false)
    private Integer tempoPreparoMinutos = 10;

    // getters e setters

    // regra de negócio simples que pode morar na entidade
    public boolean temEstoqueDisponivel(int quantidade) {
        if (!controlaEstoque) return true;
        return estoqueAtual >= quantidade;
    }
}
