package com.example.shopping_cart.service.impl;

import com.example.shopping_cart.model.Product;
import com.example.shopping_cart.repository.ProductRepository;
import com.example.shopping_cart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product addProduct(Product product) { // thêm product
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Boolean deleteProduct(int id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(int id) {

        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image) {
        Product dbProduct = getProductById(product.getId());
        String imageName = image.isEmpty()? dbProduct.getImage():image.getOriginalFilename();
        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setStock(product.getStock());
        dbProduct.setImage(imageName);
        dbProduct.setDiscount(product.getDiscount());
        // 5=100*(5-100);100-5=95
       double total= product.getPrice()*(product.getDiscount()/100);
       double discountPrice = product.getPrice()-total;
       dbProduct.setDiscountPrice(discountPrice);
       dbProduct.setIsActive(product.getIsActive());
        Product updateProduct = productRepository.save(dbProduct);
        if(!ObjectUtils.isEmpty(updateProduct)) {
            if (!image.isEmpty()) {
                try {


                    File saveFile = new ClassPathResource("static/img").getFile();

                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                            + image.getOriginalFilename());

                    // System.out.println(path);
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                }catch (Exception e)
                {
                  e.printStackTrace();
                }
            }
            return product;
        }

        return null;
    }

    @Override
    public List<Product> getAllActiveProducts( String category) {
        List<Product> products=null;
        if(ObjectUtils.isEmpty(category)) {
          products   = productRepository.findByIsActiveTrue();
        }else{
            products= productRepository.findByCategory(category); // laay tat ca
        }

        return products;
    }

    @Override
    public List<Product> searchProducts(String ch) {

        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch);
    }

    @Override
    public Page<Product> getAllActiveProductPagination(Integer page, Integer size,String category) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> pageProduct = null;

        if (ObjectUtils.isEmpty(category)) {
            pageProduct = productRepository.findByIsActiveTrue(pageable);
        } else {
            pageProduct = productRepository.findByCategory(pageable, category);
        }
        return pageProduct;
    }

    @Override
    public Page<Product> searchProductPagination(Integer page, Integer size, String ch) {
        Pageable pageable=PageRequest.of(page,size);

        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);
    }

    @Override
    public Page<Product> getAllProductsPagination(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page,size);

        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> searchActiveProductPagination(Integer page, Integer size, String category, String ch) {

        Pageable pageable = PageRequest.of(page, size);

//      productRepository.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch, pageable);

        return  productRepository.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch, pageable);

    }


}
