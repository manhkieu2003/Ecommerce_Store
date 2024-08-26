package com.example.shopping_cart.service;

import com.example.shopping_cart.model.OrderRequest;
import com.example.shopping_cart.model.ProductOrder;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    public void saveOrder(Integer userId, OrderRequest orderRequest);
    public List<ProductOrder> getOrdersByUser(Integer userId);
    public Boolean updateOrderStatus(Integer id, String status);
    public List<ProductOrder>    getAllOrders();
    public ProductOrder getOrdersByOrderId(String orderId);
    public Page<ProductOrder> getAllOrdersPagination(int page ,int size);
}
