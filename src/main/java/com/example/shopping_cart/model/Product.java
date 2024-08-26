package com.example.shopping_cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 500)
    private String title;
    @Column(length = 5000)
    private String description;
    private String category;
    private Double price;
    private int stock;
    private String image;
//    @Column(name = "discount", nullable = false, columnDefinition = "int default 0")
    @Column(name = "discount", nullable = false)
    private int discount;
    private Double discountPrice;
    private Boolean isActive;


}
