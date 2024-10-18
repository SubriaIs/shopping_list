package com.team.e.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ShoppingList {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shoppingListId;

    // One-to-One relationship with UserGroup
    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"groupUsers", "groupNotifications"})
    @JoinColumn(name = "groupId", nullable = false)
    private UserGroup userGroup;

    @Column(name = "shoppingListName", nullable = false, length = 100)
    private String shoppingListName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private String createdAt;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ShoppingListProduct> shoppingListProducts;

}
