package com.raulld.deliciassantana.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categoria")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String nome; // ex: "Lanches", "Bebidas", "Sobremesas"

    @Column(nullable = false)
    private Integer ordemExibicao = 0; // controla ordem no cardápio

    // getters e setters
}
