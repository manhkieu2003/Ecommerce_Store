package com.example.shopping_cart.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class OrderRequest {
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String paymentType;
}
