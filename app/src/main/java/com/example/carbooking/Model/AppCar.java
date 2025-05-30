package com.example.carbooking.Model;

public class AppCar {
    private String id;             // Unique ID for the car
    private String name;
    private String model;
    private double price;
    private double discount;
    private String description;
    private String image;

    private String status;       // "Pending", "Confirmed", "Rejected"
    private Boolean available;   // true = available, false = rented

    public AppCar() {
        // Required for Firebase
    }

    // Full constructor
    public AppCar(String id, String name, String model, double price,  double discount, String description, String status, Boolean available, String image) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.status = status;
        this.available = available;
        this.image = image;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscount(){
        return discount;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public Boolean getAvailable() {
        return available;
    }

    public String getStatus() {
        return status != null ? status : "Pending";
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscount(double discount){
        this.discount = discount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
