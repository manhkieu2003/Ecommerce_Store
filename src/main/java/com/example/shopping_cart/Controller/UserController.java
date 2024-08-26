package com.example.shopping_cart.Controller;

import com.example.shopping_cart.model.Cart;
import com.example.shopping_cart.model.OrderRequest;
import com.example.shopping_cart.model.ProductOrder;
import com.example.shopping_cart.model.Register;
import com.example.shopping_cart.service.CartService;
import com.example.shopping_cart.service.OrderService;
import com.example.shopping_cart.service.RegisterService;
import com.example.shopping_cart.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@CommonsLog
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private RegisterService registerService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @GetMapping("/")
    public String home()
    {
        return "user/home";
    }

    @ModelAttribute
    public void getUserDetails(Principal principal, Model model)
    {
        if(principal != null)
        {
            String email = principal.getName();
            Register registeruser = registerService.getByRegisterEmail(email);
            model.addAttribute("user", registeruser);
        }
    }
    @GetMapping("/addCart")
    public String addToCart(@RequestParam("pid") int pid, @RequestParam("uid") int uid, Model model, HttpSession session)
    {
        Cart saveCart = cartService.saveCart(pid,uid);
        if(!ObjectUtils.isEmpty(saveCart))
        {
            session.setAttribute("success","Thêm cart thành công");
        }else{
            session.setAttribute("errorMsg","Thêm cart lỗi");
        }

        return "redirect:/product/"+pid;
    }
    @GetMapping("/cart")
    public String loadCart(Principal p,Model model)

    {

        Register user = getLoggedInUserDetail(p);
       List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts",carts);
       // laays ra toong tiền
        if(carts.size()>0) {
            double totalOderPrice = carts.get(carts.size() - 1).getTotalOderPrice();

            System.out.println(totalOderPrice);


            model.addAttribute("totalOderPrice", totalOderPrice);

        }
        return "/user/cart";
    }
    @GetMapping("/cartQuantityUpdate")
    public String updateCart(@RequestParam("sy") String sy,@RequestParam("cid") Integer cid)
    {
          cartService.updateCart(sy,cid);
        return "redirect:/user/cart";
    }

    private Register getLoggedInUserDetail(Principal p) {
        String email = p.getName();
        Register registerUser = registerService.getByRegisterEmail(email);
        return  registerUser;
    }
    @GetMapping("/orders")
    public String orders(Principal p, Model model) {
        Register user = getLoggedInUserDetail(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts", carts);
        // laays ra toong tiền
        if (carts.size() > 0) {
            double totalOderPrice = carts.get(carts.size() - 1).getTotalOderPrice();
            double AlltotalOrderPrice = carts.get(carts.size() - 1).getTotalOderPrice() + 250 + 100;
            System.out.println(totalOderPrice);
            System.out.println(AlltotalOrderPrice);

            model.addAttribute("totalOderPrice", totalOderPrice);
            model.addAttribute("AlltotalOrderPrice", AlltotalOrderPrice);

        }
        return "/user/order";
    }
    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest,Principal p)
    {
//        System.out.println(orderRequest);
       Register user = getLoggedInUserDetail(p);
       orderService.saveOrder(user.getId(),orderRequest);

          return "/user/orderSucess";
    }
    @GetMapping("/orderSucess")
    public String orderSucess()
    {

        return "/user/orderSucess";
    }
    @GetMapping("/user-orders")
    public String Myorder(Model model,Principal p)
    {
        Register user = getLoggedInUserDetail(p);
     List<ProductOrder> orders=orderService.getOrdersByUser(user.getId());
        System.out.println(orders);
     model.addAttribute("orders",orders);

        return "/user/my_orders";
    }
    @GetMapping("/update-status")
    public String updateStatus(@RequestParam Integer id,@RequestParam Integer st,HttpSession session)
    {
        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderSt : values) {
             if(Objects.equals(orderSt.getId(), st))
             {
                 status=orderSt.getName();
             }
        }

        Boolean updateOrder = orderService.updateOrderStatus(id, status);

        if (updateOrder) {
            session.setAttribute("success", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "status not updated");
        }

        return "redirect:/user/user-orders";
    }
    @GetMapping("/profile")
    public String profile()
    {
        return "/user/profile";
    }
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Register user, @RequestParam MultipartFile img,HttpSession session) throws IOException {
      Register userUpdate=  registerService.updateProfile(user,img);
        if(ObjectUtils.isEmpty(userUpdate))
        {
            session.setAttribute("errorMsg","Looi");
        }else{
            session.setAttribute("success","thanh cong");
        }
        return "redirect:/user/profile";
    }
}
