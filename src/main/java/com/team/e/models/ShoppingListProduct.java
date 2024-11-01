package com.team.e.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ShoppingListProduct {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shoppingListProductId;

    @Column(name = "productName", nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"userGroup", "shoppingListProducts"})
    @JoinColumn(name = "shoppingListId", nullable = false, foreignKey = @ForeignKey(name = "fk_shoppingList_shoppingListProducts"))
    private ShoppingList shoppingList;


    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0; // Setting the default value in the entity

    @Column(name = "unit", nullable = false)
    private String unit = "kg";

    @Column(name = "purchase", nullable = false)
    private Boolean purchase = false;

}
