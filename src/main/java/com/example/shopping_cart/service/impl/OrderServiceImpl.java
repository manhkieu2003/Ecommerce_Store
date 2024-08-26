package com.example.shopping_cart.service.impl;

import com.example.shopping_cart.model.Cart;
import com.example.shopping_cart.model.OrderAddress;
import com.example.shopping_cart.model.OrderRequest;
import com.example.shopping_cart.model.ProductOrder;
import com.example.shopping_cart.repository.CartRepository;
import com.example.shopping_cart.repository.ProductOrderRepository;
import com.example.shopping_cart.service.OrderService;
import com.example.shopping_cart.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private CartRepository cartRepository;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        for(Cart cart :carts)
        {
             ProductOrder productOrder = new ProductOrder();
             productOrder.setOrderId(UUID.randomUUID().toString());
             productOrder.setOrderDate(new Date());
             productOrder.setProduct(cart.getProduct());
             productOrder.setPrice(cart.getProduct().getPrice());
             productOrder.setQuantity(cart.getQuantity());

             productOrder.setUser(cart.getUser());
             productOrder.setStatus(OrderStatus.IN_PROGRESS.getName());
             productOrder.setPaymentType(orderRequest.getPaymentType());
            OrderAddress orderAddress = new OrderAddress();
            orderAddress.setFirst_name(orderRequest.getFirst_name());
            orderAddress.setLast_name(orderRequest.getLast_name());
            orderAddress.setEmail(orderRequest.getEmail());
            orderAddress.setPhone(orderRequest.getPhone());
            orderAddress.setAddress(orderRequest.getAddress());
            orderAddress.setCity(orderRequest.getCity());
            orderAddress.setState(orderRequest.getState());
            orderAddress.setPincode(orderRequest.getPincode());
            productOrder.setOrderAddress(orderAddress);
            productOrderRepository.save(productOrder);




        }

    }

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        List<ProductOrder> orders =productOrderRepository.findByUserId(userId);
        return orders;
    }

    @Override
    public Boolean updateOrderStatus(Integer id, String status) {
        Optional<ProductOrder> findByID = productOrderRepository.findById(id);
        if(findByID.isPresent()) {
            ProductOrder productOrder = findByID.get();
            productOrder.setStatus(status);
            productOrderRepository.save(productOrder);
            return true;
        }
        return false;
    }

    @Override
    public List<ProductOrder> getAllOrders() {

        return productOrderRepository.findAll();
    }

    @Override
    public ProductOrder getOrdersByOrderId(String orderId) {
       return productOrderRepository.findByOrderId(orderId);

    }

    @Override
    public Page<ProductOrder> getAllOrdersPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return productOrderRepository.findAll(pageable);
    }
}
