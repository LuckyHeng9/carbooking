package com.example.carbooking.model;

import java.util.function.BiConsumer;

public class AppCar {
    private String id;
    private String name;
    private String model;
    private String price;
    private String description;
    private Boolean carstatus;
    private String image;



    public  AppCar() {}

    // Full constructor
    public  AppCar(String id, String name, String model, String price, String description, boolean carstatus , String image) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.price = price;
        this.carstatus = carstatus;
        this.description = description;
        this.image = image;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getModel() { return model; }
    public String getPrice() { return price; }
    public Boolean getCarstatus(){return  carstatus;}
    public String getDescription() { return description; }
    public String getImage() { return image; }



    // Setters

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setModel(String model) { this.model = model; }
    public void setPrice(String price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public  void  setCarstatus(Boolean carstatus){this.carstatus = carstatus;}
    public void setImage(String image) { this.image = image; }
}
