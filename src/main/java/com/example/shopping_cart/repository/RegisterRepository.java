package com.example.shopping_cart.repository;

import com.example.shopping_cart.model.Register;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegisterRepository extends JpaRepository<Register, Integer> {
    public Register findByEmail(String email);

   public  List<Register> findByRole(String role);
}
