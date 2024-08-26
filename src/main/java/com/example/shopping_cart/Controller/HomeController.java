package com.example.shopping_cart.Controller;

import com.example.shopping_cart.model.Category;
import com.example.shopping_cart.model.Product;
import com.example.shopping_cart.model.Register;
import com.example.shopping_cart.service.CartService;
import com.example.shopping_cart.service.CategoryService;
import com.example.shopping_cart.service.ProductService;
import com.example.shopping_cart.service.RegisterService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private CartService cartService;
    @ModelAttribute
    public void getUserDetails(Principal principal,Model model)
    {
        if(principal != null)
        {
            String email = principal.getName();
            Register registeruser = registerService.getByRegisterEmail(email);
            model.addAttribute("user", registeruser);
            Integer countCart = cartService.getCountCart(registeruser.getId());
            model.addAttribute("countCart",countCart);
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("categorys", allActiveCategory);
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
                .sorted((c1,c2)->c2.getId().compareTo(c1.getId()))
                .limit(6).toList();
        List<Product> allActiveProducts = productService.getAllActiveProducts("").stream()
                .sorted((p1,p2)->p2.getId().compareTo(p1.getId()))
                .limit(8).toList();
        model.addAttribute("categorys", allActiveCategory);
        model.addAttribute("products", allActiveProducts);


        return "index";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products")
    public String products(Model model ,@RequestParam( value = "category",defaultValue = "") String category,@RequestParam(name = "page", defaultValue = "0") Integer page,
                           @RequestParam(name = "size", defaultValue = "4") Integer size,@RequestParam(name = "ch",defaultValue = "") String ch) {
        System.out.println(category);
        List<Category> categories=categoryService.getAllActiveCategory();
//        List<Product> products = productService.getAllActiveProducts(category);
        model.addAttribute("categories", categories);

        model.addAttribute("paramValue", category);
        // truyền active thông qua category

//        model.addAttribute("products", products);
        Page<Product> pages = null;
        if (ch != null && ch.length() > 0) {
            pages=productService.searchActiveProductPagination(page,size,category,ch);
        }else{
            pages = productService.getAllActiveProductPagination(page, size, category);
        }
//        if (StringUtils.isEmpty(ch)) {
//            pages = productService.getAllActiveProductPagination(page, size, category);
//        }else {
//            pages=productService.searchActiveProductPagination(page,size,category,ch);
//        }

//        Page<Product> pages = productService.getAllActiveProductPagination(page, size, category);
        List<Product> products = pages.getContent();
        model.addAttribute("products", products);
        model.addAttribute("productsSize", products.size());

        model.addAttribute("page", pages.getNumber());
        model.addAttribute("size",size);
        model.addAttribute("totalElements", pages.getTotalElements());
        model.addAttribute("totalPages", pages.getTotalPages());
        model.addAttribute("isFirst", pages.isFirst());
        model.addAttribute("isLast", pages.isLast());

        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable("id") int id,
      Model model
    ) {
        Product productId = productService.getProductById(id);
        model.addAttribute("productId", productId);
        return "view_product";
    }
    @PostMapping("/saveRegister")
    public String saveRegister(@ModelAttribute Register register, @RequestParam("img") MultipartFile file, Model model, HttpSession session) throws IOException {
        String imageName = file.isEmpty()?"default.jpg":file.getOriginalFilename();
        register.setProfileImage(imageName);
       Register saveRegister = registerService.saveRegister(register);
       if(!ObjectUtils.isEmpty(saveRegister))
       {
           if(!file.isEmpty())
           {
               File saveFile = new ClassPathResource("static/img").getFile();

               Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                       + file.getOriginalFilename());

                System.out.println(path);
               Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
           }
           session.setAttribute("success","thêm  thanh cong");
       }else{
           session.setAttribute("errorMsg","thêm  lỗi");
       }

        return "redirect:/register";
    }
    @GetMapping("/search")
    public String search(@RequestParam  String ch,Model model)
    {
        List<Product> searchProduct = productService.searchProducts(ch);
        model.addAttribute("products", searchProduct);
        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories",categories);
        return "product";

    }


}
