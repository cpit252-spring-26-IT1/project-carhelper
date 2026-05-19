package com.carhelper.dto;

public class ResaleValueRequest {
    private Long userId;
    private String brand;
    private String model;
    private int year;
    private int mileage;
    private String condition;
    private String mechanicalProblems;
    private String language;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getMechanicalProblems() {
        return mechanicalProblems;
    }

    public void setMechanicalProblems(String mechanicalProblems) {
        this.mechanicalProblems = mechanicalProblems;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
