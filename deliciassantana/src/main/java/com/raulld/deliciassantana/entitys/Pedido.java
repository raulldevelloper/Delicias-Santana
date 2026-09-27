package com.raulld.deliciassantana.entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING) // sempre STRING, nunca ORDINAL (evita bug se reordenar o enum)
    @Column(nullable = false, length = 30)
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(length = 300)
    private String observacao; // ex: "sem cebola"

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @PrePersist
    protected void aoInserir() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // método utilitário para manter os dois lados da relação sincronizados
    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }

    public void recalcularValorTotal() {
        this.valorTotal = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // getters e setters
}
