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
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private  Integer id;
    @ManyToOne
   private Register user;
    @ManyToOne
   private Product product;

   private Integer quantity;
   @Transient
   private Double totalPrice;
   @Transient
   private Double totalOderPrice;
}
