package com.example.carbooking.Model;

public class Car {
    public int imageResId;
    public String name;
    public String price;
    public String discount;
    public int capacity;

    public Car (int imageResId, String name, String price, String discount, int capacity) {
        this.imageResId = imageResId;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.capacity = capacity;
    }
}
