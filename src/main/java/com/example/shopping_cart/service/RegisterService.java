package com.example.shopping_cart.service;

import com.example.shopping_cart.model.Register;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RegisterService {
    public  Register saveRegister(Register register);
   public Register getByRegisterEmail(String email);
   public Register updateProfile(Register register, MultipartFile img) throws IOException;
    public  Register saveAdmin(Register register);
    public List<Register> getUsers(String role);
    public Boolean updateAccountStatus(Integer id, Boolean status);
}
