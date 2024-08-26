package com.example.shopping_cart.service;

import com.example.shopping_cart.model.Cart;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

public interface CartService {
    public Cart saveCart(Integer productId, Integer userId); // addcart
    public List<Cart> getCartsByUser(Integer userId);
    public Integer getCountCart(Integer userId);
    public void updateCart(String sy,Integer cid);

}
