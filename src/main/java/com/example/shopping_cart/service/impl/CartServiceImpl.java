package com.example.shopping_cart.service.impl;

import com.example.shopping_cart.model.Cart;
import com.example.shopping_cart.model.Product;
import com.example.shopping_cart.model.Register;
import com.example.shopping_cart.repository.CartRepository;
import com.example.shopping_cart.repository.ProductRepository;
import com.example.shopping_cart.repository.RegisterRepository;
import com.example.shopping_cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    RegisterRepository registerRepository;
    @Autowired
    ProductRepository productRepository;
    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        Register user = registerRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();
     Cart cartSatus = cartRepository.findByProductIdAndUserId(productId,userId);
     Cart cart= null;
     if(ObjectUtils.isEmpty(cartSatus)) // nếu cart trống thêm cart
     {
          cart = new Cart();
          cart.setUser(user);
          cart.setProduct(product);
          cart.setQuantity(1);
          cart.setTotalPrice(1*product.getDiscountPrice());


     } else {    // đã cos cart rồi tăng số lượng
         cart=cartSatus;
         cart.setQuantity(cart == null ? 0 : cart.getQuantity()+1 );
         cart.setTotalPrice(cart.getProduct().getDiscountPrice()*cart.getQuantity());
     }
     Cart saveCart = cartRepository.save(cart);
     return saveCart;
    }

    @Override
    public List<Cart> getCartsByUser(Integer userId) {
       List<Cart> carts =cartRepository.findByUserId(userId);   // danh sách các cart theo id
        Double totalOderPrice =0.0;
        List<Cart> cartList =new ArrayList<>();
        for(Cart cart:carts)
        {
            double totalPrice=cart.getProduct().getDiscountPrice()*cart.getQuantity();
            cart.setTotalPrice(totalPrice);
            totalOderPrice+=totalPrice;
            cart.setTotalOderPrice(totalOderPrice);
            cartList.add(cart);




        }

        return cartList;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer cartByUserId = cartRepository.countByUserId(userId);
        return cartByUserId;
    }

    @Override
    public void updateCart(String sy, Integer cid) {
        Cart cart = cartRepository.findById(cid).get();
        int updateQuantity;
        if(sy.equalsIgnoreCase("de"))
        {
           updateQuantity = cart.getQuantity()-1;
           if(updateQuantity<0)
           {
             cartRepository.delete(cart);
           }else{
               cart.setQuantity(updateQuantity);
               cartRepository.save(cart);
           }

        }else{
            updateQuantity=cart.getQuantity()+1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }

    }
}
