package com.example.shopping_cart.util;

public enum OrderStatus {
    IN_PROGRESS(1,"IN PRORGESS"),
    ORDER_RECIVED(2,"Order recived"),
    PRODUCT_PACKED(3,"Product packet"),
    OUT_FOR_DELIVERY(4,"Out for delivery"),
    DELIVETY(5,"Delivery"),
    CANCEL(6,"Cancel");
    private int id;
    private String name;

    OrderStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
