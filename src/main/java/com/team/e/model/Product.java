package com.team.e.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column( nullable = false)
    private String productName;

    @ManyToOne( fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId", nullable = false)
    @JsonIgnoreProperties({"products"})
    private Category category;
}
