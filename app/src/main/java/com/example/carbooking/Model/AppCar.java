package com.example.carbooking.Model;

public class AppCar {
    private String id;             // Unique ID for the car
    private String name;
    private String model;
    private double price;
    private String description;
    private String image;

    private Boolean carstatus;   // null = pending, true = confirmed, false = rejected
    private Boolean available;   // true = available, false = rented

    public AppCar() {}

    // Full constructor
    public AppCar(String id, String name, String model, double price, String description, Boolean carstatus, Boolean available, String image) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.price = price;
        this.description = description;
        this.carstatus = carstatus;
        this.available = available;
        this.image = image;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getModel() { return model; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public Boolean getCarstatus() { return carstatus; }
    public Boolean getAvailable() { return available; }
    public String getImage() { return image; }

    public String getStatus() {
        if (carstatus == null) return "Pending";
        return carstatus ? "Confirmed" : "Rejected";
    }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setModel(String model) { this.model = model; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setCarstatus(Boolean carstatus) { this.carstatus = carstatus; }
    public void setAvailable(Boolean available) { this.available = available; }
    public void setImage(String image) { this.image = image; }
}
