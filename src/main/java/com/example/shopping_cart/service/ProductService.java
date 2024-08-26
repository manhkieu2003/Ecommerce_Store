package com.example.shopping_cart.service;

import com.example.shopping_cart.model.Category;
import com.example.shopping_cart.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    public Product addProduct(Product product);

    public List<Product> getAllProducts();

    public Boolean deleteProduct(int id);

    public Product getProductById(int id);
    public Product updateProduct(Product product, MultipartFile file);
    public List<Product> getAllActiveProducts( String category);
    public List<Product> searchProducts(String ch);
    // phân trang
    public Page<Product> getAllActiveProductPagination(Integer page, Integer size,String category);
    public Page<Product> searchProductPagination(Integer page, Integer size, String ch);

    public Page<Product> getAllProductsPagination(Integer page, Integer size);

   public Page<Product> searchActiveProductPagination(Integer page, Integer size, String category, String ch);
}
