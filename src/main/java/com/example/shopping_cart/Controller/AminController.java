package com.example.shopping_cart.Controller;

import com.example.shopping_cart.model.Category;
import com.example.shopping_cart.model.Product;
import com.example.shopping_cart.model.ProductOrder;
import com.example.shopping_cart.model.Register;
import com.example.shopping_cart.repository.ProductRepository;
import com.example.shopping_cart.service.*;
import com.example.shopping_cart.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.core.io.ClassPathResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class AminController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @ModelAttribute
    public void getUserDetails(Principal principal, Model model)
    {
        if(principal != null)
        {
            String email = principal.getName();
            Register registeruser = registerService.getByRegisterEmail(email);
            model.addAttribute("user", registeruser);
            Integer countCart = cartService.getCountCart(registeruser.getId());
            model.addAttribute("countCart",countCart);
        }
    }

    @GetMapping("")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model model) {
        List<Category> categoryList = categoryService.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String category(Model model,@RequestParam(name = "page",defaultValue ="0") int page,@RequestParam(name = "size",defaultValue = "4") int size) {
   // 1: lấy category khi chưa phân trang
//        model.addAttribute("categories", categoryService.getAllCategories());
        //2: lấy category khi phân trang
        Page<Category> pages= categoryService.getAllCategoryPagination(page,size);
        List<Category> categories = pages.getContent();
        model.addAttribute("categories", categories);

        model.addAttribute("page", pages.getNumber());
        model.addAttribute("size", size);
        model.addAttribute("totalElements", pages.getTotalElements());
        model.addAttribute("totalPages", pages.getTotalPages());
        model.addAttribute("isFirst", pages.isFirst());
        model.addAttribute("isLast", pages.isLast());


        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) {
        String fileName = file != null ? file.getOriginalFilename() : "default.jpg"; // true lay tên gốc file
        category.setImageName(fileName);
        Boolean exitCategory = categoryService.exitCategory(category.getName());  // kiem tra rong
        if (exitCategory) {     // true
            session.setAttribute("errorMsg", "Category already exit");   // loi
        } else {
            Category saveCategory = categoryService.saveCategory(category);       // thuc hien
            if (ObjectUtils.isEmpty(saveCategory)) {
                session.setAttribute("errorMsg", "Category not exit");

            } else {
                session.setAttribute("success", "Category successfully");
            }
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable Integer id, HttpSession session) {
        Boolean deleteCategory = categoryService.deleteCategory(id);
        if (deleteCategory) {
            session.setAttribute("success", "Xóa thành công");
        } else {
            session.setAttribute("errorMsg", "Xóa lỗi");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable Integer id, Model model) {
        model.addAttribute("category", categoryService.getCategoryId(id));
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        Category categoryUpdate = categoryService.getCategoryId(category.getId());
        String fileName = file.isEmpty() ? categoryUpdate.getImageName() : file.getOriginalFilename();
        if (!ObjectUtils.isEmpty(category)) {
            categoryUpdate.setName(category.getName());
            categoryUpdate.setIsActive(category.getIsActive());
            categoryUpdate.setImageName(fileName);


        }
        Category updateCategory = categoryService.saveCategory(categoryUpdate);
        if (!ObjectUtils.isEmpty(updateCategory)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
                        + file.getOriginalFilename());

                // System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("success", "Sửa thành công");
        } else {
            session.setAttribute("errorMsg", "Sửa lỗi");
        }

        return "redirect:/admin/category" ;
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session) throws IOException {
        String imageName = image.isEmpty() ? "default.jp" : image.getOriginalFilename();
        product.setImage(imageName); //setImageproduct
    product.setDiscount(0);
      product.setDiscountPrice(product.getPrice());
        Product saveProduct = productService.addProduct(product);
        if (!ObjectUtils.isEmpty(saveProduct)) {
            File saveFile = new ClassPathResource("static/img").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                    + image.getOriginalFilename());

            // System.out.println(path);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//            session.setAttribute("success", "Thêm thành công");
            return "redirect:/admin/products";

        } else {
            session.setAttribute("errorMsg", "Lỗi thêm");
            return "redirect:/admin/loadAddProduct";
        }

    }

    @GetMapping("/products")
    public String loadViewProducts( @RequestParam(defaultValue = "") String ch,Model model,
     @RequestParam(name = "page",defaultValue = "0") int page,@RequestParam(name = "size",defaultValue = "4") int size
    ) {
//        model.addAttribute("products", productService.getAllProducts());
//        return "admin/products";
 //1: lấy danh sách product và search khi chưa phân trang

//            List<Product> products = null;
//            if (ch != null && ch.length() > 0) {
//                products = productService.searchProducts(ch);
//            } else {
//                products = productService.getAllProducts();
//            }
//            model.addAttribute("products", products);
        //2 : lấy danh sách product và search phân trang
        Page<Product> pages = null;
        if (ch != null && ch.length() > 0) {
            pages = productService.searchProductPagination(page, size, ch);
        } else {
            pages = productService.getAllProductsPagination(page, size);
        }
        model.addAttribute("products", pages.getContent());

        model.addAttribute("page", pages.getNumber());
        model.addAttribute("size", size);
        model.addAttribute("totalElements", pages.getTotalElements());
        model.addAttribute("totalPages", pages.getTotalPages());
        model.addAttribute("isFirst", pages.isFirst());
        model.addAttribute("isLast", pages.isLast());
            return "admin/products";

    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable Integer id, HttpSession session) {
        Boolean deleteProduct = productService.deleteProduct(id);
        if (deleteProduct) {
            session.setAttribute("success", "xóa thành công");

        } else {
            session.setAttribute("errorMsg", "xóa lỗi");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable Integer id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("category", categoryService.getCategoryId(id));
        return "/admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session,Model model) throws IOException {
       Product updateProduct =productService.updateProduct(product,image);
       if(product.getDiscount()<0||product.getDiscount()>100){
           session.setAttribute("errorMsg","Invalid discount");
       }
       if (!ObjectUtils.isEmpty(updateProduct)) {
//           session.setAttribute("success","Sửa thành công");
           return "redirect:/admin/products";

       }else{
           session.setAttribute("errorMsg","Sửa lỗi");
           return "redirect:/admin/editProduct/"+product.getId();
       }


//        return "redirect:/admin/editProduct/"+product.getId();
//        return "redirect:/admin/products";
    }
    @GetMapping("/orders")
    public String getAllOrders(Model model,@RequestParam(name = "page",defaultValue = "0") int page,
                           @RequestParam(name = "size",defaultValue = "4") int size
                               )
    {
        //1:list order chưa phân trang
//        List<ProductOrder> allorders =orderService.getAllOrders();
//        model.addAttribute("orders",allorders);
        //2:list order đã phân trang
        Page<ProductOrder> pages = orderService.getAllOrdersPagination(page, size);
        model.addAttribute("orders", pages.getContent());
//        model.addAttribute("srch", false);

        model.addAttribute("page", pages.getNumber());
        model.addAttribute("size", size);
        model.addAttribute("totalElements", pages.getTotalElements());
        model.addAttribute("totalPages", pages.getTotalPages());
        model.addAttribute("isFirst", pages.isFirst());
        model.addAttribute("isLast", pages.isLast());

        return "/admin/orders";
    }
    @PostMapping("/update-order-status")
    public String updateStatus(@RequestParam("id") int id,@RequestParam("st") int st,HttpSession session)
    {
        OrderStatus[] values = OrderStatus.values();
        String status = null;
        for(OrderStatus orderstatus:values){
            if(Objects.equals(orderstatus.getId(), st))
            {
                status = orderstatus.getName();
            }
        }
        Boolean updateOrder = orderService.updateOrderStatus(id,status);
        if(updateOrder){
            session.setAttribute("success","Update status thanhf công");
        }else{
            session.setAttribute("errorMsg","Lỗi sửa");
        }
        return "redirect:/admin/orders";
        }
    @GetMapping("/search-order")
    public String search(@RequestParam String orderId,Model model,HttpSession session) {
//        ProductOrder order = orderService.getOrdersByOrderId(orderId);
//        if(ObjectUtils.isEmpty(order))
//        {
//             session.setAttribute("errorMsg","loi");
//        }else{
//           model.addAttribute("order",order);
//        }
//
//        return "/admin/orders";


        if (orderId != null && orderId.length() > 0) {

            ProductOrder order = orderService.getOrdersByOrderId(orderId);

            if (ObjectUtils.isEmpty(order)) {
                session.setAttribute("errorMsg", "Incorrect orderId");
                model.addAttribute("orderDtls", null);
            } else {
                model.addAttribute("orderDtls", order);
            }

            model.addAttribute("srch", true);
        } else {
            List<ProductOrder> allOrders = orderService.getAllOrders();
            model.addAttribute("orders", allOrders);
            model.addAttribute("srch", false);
        }
        return "/admin/orders";

    }
    @GetMapping("/add-admin")
    public String LoadaddAdmin()
    {
        return "/admin/add_admin";

    }
    @PostMapping("/saveAdmin")
    public String saveAdmin(@ModelAttribute Register register, @RequestParam("img") MultipartFile file, Model model, HttpSession session) throws IOException {
        String imageName = file.isEmpty()?"default.jpg":file.getOriginalFilename();
        register.setProfileImage(imageName);
        Register saveRegister = registerService.saveAdmin(register);
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

        return "redirect:/admin/add-admin";
    }
    @GetMapping("/profile")
    public String profile() {
        return "/admin/profile";
    }
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Register user, @RequestParam MultipartFile img, HttpSession session) throws IOException {
        Register updateAdminProfile = registerService.updateProfile(user,img);
        if (ObjectUtils.isEmpty(updateAdminProfile)) {
            session.setAttribute("errorMsg", "loi");
        } else {
            session.setAttribute("success", "thanh cong");
        }
        return "redirect:/admin/profile";
    }
    @GetMapping("/users")
    public String getAllUsers(Model m, @RequestParam Integer type) {
        List<Register> users = null;
        if (type == 1) {
            users = registerService.getUsers("ROLE_USER");
        } else {
            users = registerService.getUsers("ROLE_ADMIN");
        }
        m.addAttribute("userType",type);
        m.addAttribute("users", users);
        return "/admin/users";
    }
    @GetMapping("/updateSts")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,@RequestParam Integer type, HttpSession session) {
        Boolean f = registerService.updateAccountStatus(id, status);
        if (f) {
            session.setAttribute("success", "Account Status Updated");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }
        return "redirect:/admin/users?type="+type;
    }
}
